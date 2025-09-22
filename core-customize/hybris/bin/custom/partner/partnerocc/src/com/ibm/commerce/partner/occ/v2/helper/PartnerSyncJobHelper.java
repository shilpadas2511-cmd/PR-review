package com.ibm.commerce.partner.occ.v2.helper;

import de.hybris.platform.cmsfacades.data.SyncJobData;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;

public class PartnerSyncJobHelper {

    private static final Logger LOG = LoggerFactory.getLogger(PartnerSyncJobHelper.class);

    private final FlexibleSearchService flexibleSearchService;
    private final Converter<Optional<CronJobModel>, SyncJobData> syncJobConverter;

    public PartnerSyncJobHelper(FlexibleSearchService flexibleSearchService,
        Converter<Optional<CronJobModel>, SyncJobData> syncJobConverter) {
        this.flexibleSearchService = flexibleSearchService;
        this.syncJobConverter = syncJobConverter;
    }

    public SyncJobData getSyncJobStatusByCode(String cronJobCode)
        throws ModelNotFoundException, AmbiguousIdentifierException {
        return getSyncJobConverter().convert(fetchSyncJobByCode(cronJobCode));
    }

    public Optional<CronJobModel> fetchSyncJobByCode(String cronJobCode)
        throws ModelNotFoundException, AmbiguousIdentifierException {
        final String query =
            PartnercoreConstants.FETCH_CRONJOB_BY_CODE_QUERY + PartnercoreConstants.EQUAL_SIGN + PartnercoreConstants.SINGLE_QUOTE_SIGN + cronJobCode
                + PartnercoreConstants.SINGLE_QUOTE_SIGN;
        LOG.info(PartnercoreConstants.SYNC_JOB_FETCH_QUERY, query);
        final FlexibleSearchQuery fsQuery = new FlexibleSearchQuery(query);
        CronJobModel cronjob = getFlexibleSearchService().searchUnique(fsQuery);
        return Optional.ofNullable(cronjob);
    }

    public Converter<Optional<CronJobModel>, SyncJobData> getSyncJobConverter() {
        return syncJobConverter;
    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }
}
