package com.ibm.commerce.partner.occ.controllers;


import com.ibm.commerce.partner.data.solrindexer.SolrIndexJobResponseData;
import com.ibm.commerce.partner.data.solrindexer.SolrIndexJobRequestData;
import com.ibm.commerce.partner.occ.v2.helper.PartnerSolrIndexerHelper;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commercewebservicescommons.annotation.SecurePortalUnauthenticatedAccess;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller that handles solr indexing
 */
@RestController
@ApiVersion("v2")
@RequestMapping(value = "/{baseSiteId}/solrIndexerJob")
@Tag(name = "solr indexer")
public class PartnerSolrIndexerController extends PartnerBaseController {

    @Resource(name = "partnerSolrIndexerHelper")
    protected PartnerSolrIndexerHelper partnerSolrIndexerHelper;

    /**
     * Triggers a Solr indexing cron job for the specified index and operation.
     *
     * @param solrIndexJobRequestData request data containing the index name and indexing
     *                                operation.
     * @return {@link SolrIndexJobResponseData} containing the triggered job status and metadata.
     * @throws CMSItemNotFoundException if the Solr indexer job or configuration cannot be found.
     * @throws IllegalArgumentException if the request is invalid or incomplete.
     */
    @Secured({"ROLE_CLIENT_ACCESSHUB"})
    @PostMapping(value = "/{indexName}/{indexerOperation}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @Operation(summary = "Triggers Solr Indexing Cronjob", description = "Triggers Solr Indexing Cronjob",
        operationId = "triggerSolrIndexing")
    @ApiResponse(responseCode = "400", description = "When the cronjob does not exist (CMSItemNotFoundException).")
    @ApiResponse(responseCode = "200", description = "The indexing job status")
    @Parameter(name = "indexName", description = "index name", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
    @Parameter(name = "indexerOperation", description = "indexing operation", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
    @SecurePortalUnauthenticatedAccess
    public SolrIndexJobResponseData triggerIndexingJob(
        @Parameter(description = "Contains the solr index job request data", required = true)
        @ModelAttribute final SolrIndexJobRequestData solrIndexJobRequestData)
        throws CMSItemNotFoundException, IllegalArgumentException {
        SolrIndexJobResponseData solrIndexJobResponseData = getPartnerSolrIndexerHelper().processIndexJob(
            solrIndexJobRequestData, true);
        return getDataMapper().map(solrIndexJobResponseData, SolrIndexJobResponseData.class);
    }

    /**
     * Retrieves the status of a Solr indexing cron job for the specified index and operation.
     *
     * @param solrIndexJobRequestData request data containing the index name and indexing
     *                                operation.
     * @return {@link SolrIndexJobResponseData} containing the current status and metadata of the
     * job.
     * @throws CMSItemNotFoundException if the Solr indexer job or configuration cannot be found.
     * @throws IllegalArgumentException if the request is invalid or incomplete.
     */
    @Secured({"ROLE_CLIENT_ACCESSHUB"})
    @GetMapping(value = "/{indexName}/{indexerOperation}")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    @Operation(summary = "Gets solr index job status by job code.", description = "Retrieves the status of the solr index job.",
        operationId = "getSolrIndexJobStatusByJobCode")
    @ApiResponse(responseCode = "200", description = "The indexing job status")
    @Parameter(name = "indexName", description = "index name", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
    @Parameter(name = "indexerOperation", description = "indexing operation", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
    @SecurePortalUnauthenticatedAccess
    public SolrIndexJobResponseData getIndexingJobStatus(
        @Parameter(description = "Contains the solr index job request data", hidden = true)
        @ModelAttribute final SolrIndexJobRequestData solrIndexJobRequestData)
        throws CMSItemNotFoundException, IllegalArgumentException {
        SolrIndexJobResponseData solrIndexJobResponseData = getPartnerSolrIndexerHelper().processIndexJob(
            solrIndexJobRequestData, false);
        return getDataMapper().map(solrIndexJobResponseData, SolrIndexJobResponseData.class);
    }

    public PartnerSolrIndexerHelper getPartnerSolrIndexerHelper() {
        return partnerSolrIndexerHelper;
    }

    public void setPartnerSolrIndexerHelper(
        PartnerSolrIndexerHelper partnerSolrIndexerHelper) {
        this.partnerSolrIndexerHelper = partnerSolrIndexerHelper;
    }
}
