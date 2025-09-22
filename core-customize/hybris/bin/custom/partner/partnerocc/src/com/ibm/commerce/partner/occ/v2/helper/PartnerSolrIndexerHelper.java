/**
 * Helper class for handling Solr indexer cron jobs within the OCC partner context.
 * <p>
 * This class provides methods to validate index job requests, trigger Solr indexer cron jobs, and
 * fetch necessary configuration and cron job models from the database.
 * </p>
 */
package com.ibm.commerce.partner.occ.v2.helper;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.data.solrindexer.SolrIndexJobRequestData;
import com.ibm.commerce.partner.data.solrindexer.SolrIndexJobResponseData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import de.hybris.platform.servicelayer.user.UserService;
import java.util.Objects;

import de.hybris.platform.solrfacetsearch.model.indexer.cron.SolrIndexerCronJobModel;

import de.hybris.platform.solrfacetsearch.model.config.SolrFacetSearchConfigModel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PartnerSolrIndexerHelper {

    private static final Logger LOG = LoggerFactory.getLogger(PartnerSolrIndexerHelper.class);

    private final UserService userService;
    private final ModelService modelService;
    private final CronJobService cronJobService;
    private final FlexibleSearchService flexibleSearchService;
    private final Converter<SolrIndexerCronJobModel, SolrIndexJobResponseData> partnerSolrJobStatusConverter;
    private final long solrJobTriggerWait;

    public PartnerSolrIndexerHelper(UserService userService, ModelService modelService,
        CronJobService cronJobService, FlexibleSearchService flexibleSearchService,
        Converter<SolrIndexerCronJobModel, SolrIndexJobResponseData> partnerSolrJobStatusConverter,
        long solrJobTriggerWait) {
        this.userService = userService;
        this.modelService = modelService;
        this.cronJobService = cronJobService;
        this.flexibleSearchService = flexibleSearchService;
        this.partnerSolrJobStatusConverter = partnerSolrJobStatusConverter;
        this.solrJobTriggerWait = solrJobTriggerWait;
    }

    /**
     * Processes a Solr indexing job request.
     * <p>
     * Validates the incoming request, retrieves the relevant cron job configuration, optionally
     * triggers the job if it's not running, and returns the job status.
     * </p>
     *
     * @param solrIndexJobRequestData the job request data containing indexer operation and index
     *                                name.
     * @param isTrigger               if true, triggers the job if it's not already running.
     * @return {@link SolrIndexJobResponseData} containing the job status and metadata.
     * @throws CMSItemNotFoundException if the required Solr configuration or indexer job is
     *                                  missing.
     * @throws IllegalArgumentException if the request data is invalid or incomplete.
     */
    public SolrIndexJobResponseData processIndexJob(
        final SolrIndexJobRequestData solrIndexJobRequestData, final boolean isTrigger)
        throws CMSItemNotFoundException, IllegalArgumentException {
        if (!validateSolrIndexRequest(solrIndexJobRequestData)) {
            LOG.error(PartnercoreConstants.MISSING_PARAMETERS);
            throw new IllegalArgumentException(PartnercoreConstants.MISSING_PARAMETERS);
        }

        final SolrIndexerCronJobModel indexerCronJob = fetchSolrIndexerCronJob(
            solrIndexJobRequestData.getIndexerOperation(), solrIndexJobRequestData.getIndexName());
        if (Objects.isNull(indexerCronJob)) {
            LOG.error(PartnercoreConstants.MISSING_SOLRINDEXERJOB);
            throw new CMSItemNotFoundException(PartnercoreConstants.MISSING_SOLRINDEXERJOB);
        }

        if (isTrigger && !CronJobStatus.RUNNING.equals(indexerCronJob.getStatus())) {
            if (Objects.isNull(indexerCronJob.getFacetSearchConfig())) {
                final SolrFacetSearchConfigModel facetSearchConfig = fetchSolrFacetSearchConfig(
                    solrIndexJobRequestData.getIndexName());
                if (Objects.isNull(facetSearchConfig)) {
                    LOG.error(PartnercoreConstants.MISSING_SOLRCONFIG);
                    throw new CMSItemNotFoundException(PartnercoreConstants.MISSING_SOLRCONFIG);
                }
                indexerCronJob.setFacetSearchConfig(facetSearchConfig);
            }
            triggerIndexCronJob(indexerCronJob);
        }

        SolrIndexJobResponseData solrIndexJobResponseData = getPartnerSolrJobStatusConverter().convert(
            indexerCronJob);
        return solrIndexJobResponseData;
    }


    /**
     * Triggers the execution of the given Solr indexer cron job using the specified facet search
     * configuration.
     * <p>
     * This method sets the provided {@link SolrFacetSearchConfigModel} on the cron job, assigns the
     * admin user as the session user, saves the updated cron job model, logs the trigger action,
     * and finally performs the cron job synchronously.
     * </p>
     *
     * @param indexerCronJob the {@link SolrIndexerCronJobModel} representing the cron job to be
     *                       triggered
     */
    public void triggerIndexCronJob(final SolrIndexerCronJobModel indexerCronJob) {
        indexerCronJob.setSessionUser(getUserService().getAdminUser());
        getModelService().save(indexerCronJob);
        getCronJobService().performCronJob(indexerCronJob, false);
        LOG.info(PartnercoreConstants.TRIGGERED_CRONJOB, indexerCronJob.getCode());
        try {
            Thread.sleep(getSolrJobTriggerWait());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.error(PartnercoreConstants.CRONJOB_TRIGGER_IDLE_WAIT);
        }
        getModelService().refresh(indexerCronJob);
    }


    /**
     * Validates the Solr index job request.
     *
     * @param solrIndexJobRequestData the job request data to validate.
     * @return true if the request data is valid (non-null and contains non-blank fields); false
     * otherwise.
     */
    public boolean validateSolrIndexRequest(final SolrIndexJobRequestData solrIndexJobRequestData) {
        if (Objects.isNull(solrIndexJobRequestData) || StringUtils.isBlank(
            solrIndexJobRequestData.getIndexName()) || StringUtils.isBlank(
            solrIndexJobRequestData.getIndexerOperation())) {
            return false;
        }
        return true;
    }

    /**
     * Fetches the {@link SolrFacetSearchConfigModel} for a given search configuration name.
     *
     * @param solrFacetSearchConfigName the name of the Solr facet search configuration.
     * @return the corresponding {@link SolrFacetSearchConfigModel}, or null if not found.
     */
    public SolrFacetSearchConfigModel fetchSolrFacetSearchConfig(
        final String solrFacetSearchConfigName) {
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(PartnercoreConstants.FACET_SEARCH_CONFIG_QUERY);
        searchQuery.addQueryParameter(PartnercoreConstants.FACET_SEARCH_CONFIG_NAME_PARAM, solrFacetSearchConfigName);

        final SolrFacetSearchConfigModel facetSearchConfig = getFlexibleSearchService().searchUnique(
            searchQuery);
        return facetSearchConfig;
    }

    /**
     * Fetches the {@link SolrIndexerCronJobModel} based on the provided indexer operation and index
     * name.
     *
     * @param indexerOperation the indexing operation (e.g., "full", "update").
     * @param indexName        the name of the index.
     * @return the matching {@link SolrIndexerCronJobModel}, or null if not found.
     */
    public SolrIndexerCronJobModel fetchSolrIndexerCronJob(final String indexerOperation,
        final String indexName) {
        final String indexerCronJobName = indexerOperation + PartnercoreConstants.HYPHEN
            + indexName + PartnercoreConstants.HYPHEN + PartnercoreConstants.CRON_JOB;
        LOG.info(PartnercoreConstants.FETCHING_CRONJOB, indexerCronJobName);
        final String query =
            PartnercoreConstants.SOLR_INDEXER_JOB_QUERY + PartnercoreConstants.EQUAL_SIGN + PartnercoreConstants.SINGLE_QUOTE_SIGN + indexerCronJobName
                + PartnercoreConstants.SINGLE_QUOTE_SIGN;
        LOG.info(PartnercoreConstants.SOLR_INDEXING_JOB_QUERY, query);
        final FlexibleSearchQuery fsQuery = new FlexibleSearchQuery(query);

        SolrIndexerCronJobModel indexerCronJob = getFlexibleSearchService().searchUnique(fsQuery);
        return indexerCronJob;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public CronJobService getCronJobService() {
        return cronJobService;
    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    public Converter<SolrIndexerCronJobModel, SolrIndexJobResponseData> getPartnerSolrJobStatusConverter() {
        return partnerSolrJobStatusConverter;
    }

    public UserService getUserService() {
        return userService;
    }

    public long getSolrJobTriggerWait() {
        return solrJobTriggerWait;
    }

}
