/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.occ.controllers;

import com.ibm.commerce.partner.occ.v2.helper.PartnerSyncJobHelper;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.data.SyncRequestData;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.cmsfacades.synchronization.SynchronizationFacade;
import de.hybris.platform.cmswebservices.data.SyncJobData;
import de.hybris.platform.cmswebservices.data.SyncJobRequestData;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commercewebservicescommons.annotation.SecurePortalUnauthenticatedAccess;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller that handles synchronization of catalogs
 */
@RestController
@ApiVersion("v2")
@RequestMapping(value = "/{baseSiteId}/catalogs/{catalogId}")
@Tag(name = "catalog version synchronization")
public class PartnerCatalogVersionSynchronizationController extends PartnerBaseController {

    @Resource
    private SynchronizationFacade synchronizationFacade;
    @Resource
    private UserService userService;
    @Resource(name = "partnerSyncJobHelper")
    protected PartnerSyncJobHelper partnerSyncJobHelper;

    /**
     * Retrieves the synchronization status for a catalog based on the provided source and target
     * versions.
     *
     * @param syncJobRequest the synchronization request parameters including source and target
     *                       versions
     * @return {@link SyncJobData} containing the synchronization status
     * @throws WebserviceValidationException if the request fails validation
     */
    @Secured({"ROLE_CLIENT_ACCESSHUB"})
    @GetMapping(value = "/synchronizations/versions/{sourceVersionId}/{targetVersionId}")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    @Operation(summary = "Gets synchronization status.", description =
        "Retrieves the status of the last synchronization for a catalog. Information is\n" +
            "retrieved based on a given catalog, source version and target version ids.",
        operationId = "getSynchronizationByCatalog")
    @ApiResponse(responseCode = "200", description = "The synchronization status")
    @Parameter(name = "catalogId", description = "The catalog id", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
    @Parameter(name = "sourceVersionId", description = "Catalog version used as a starting point in this synchronization", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
    @Parameter(name = "targetVersionId", description = "Catalog version destination to be synchronized", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
    @SecurePortalUnauthenticatedAccess
    public SyncJobData getSynchronizationByCatalogSourceTarget(
        @Parameter(description = "Contains the synchronization request data", required = true)
        @ModelAttribute final SyncJobRequestData syncJobRequest) {
        try {
            final SyncRequestData convertedSyncJobRequest = getDataMapper().map(syncJobRequest,
                SyncRequestData.class);

            final de.hybris.platform.cmsfacades.data.SyncJobData syncJobResult = getSynchronizationFacade()
                .getSynchronizationByCatalogSourceTarget(convertedSyncJobRequest);

            return getDataMapper().map(syncJobResult, SyncJobData.class);
        } catch (final ValidationException e) {
            throw new WebserviceValidationException(e.getValidationObject());
        }
    }

    /**
     * Creates a new synchronization job between the specified catalog source and target versions.
     *
     * @param syncJobRequest the synchronization request data
     * @return {@link SyncJobData} representing the status of the new synchronization job
     * @throws CMSItemNotFoundException      if one of the catalog versions does not exist
     * @throws WebserviceValidationException if validation fails for the synchronization request
     */
    @Secured({"ROLE_CLIENT_ACCESSHUB"})
    @PostMapping(value = "/synchronizations/versions/{sourceVersionId}/{targetVersionId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @Operation(summary = "Creates a catalog synchronization.", description = "Generates a brand new synchronization status. The status is generated based on a given catalog, source version and target version ids.",
        operationId = "createNewSynchronizationByCatalog")
    @ApiResponse(responseCode = "400", description = "When one of the catalogs does not exist (CMSItemNotFoundException).")
    @ApiResponse(responseCode = "200", description = "The synchronization status")
    @Parameter(name = "catalogId", description = "The catalog id", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
    @Parameter(name = "sourceVersionId", description = "Catalog version used as a starting point in this synchronization", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
    @Parameter(name = "targetVersionId", description = "Catalog version destination to be synchronized", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
    @SecurePortalUnauthenticatedAccess
    public SyncJobData triggerSyncJob(
        @Parameter(description = "Contains the synchronization request data", required = true)
        @ModelAttribute final SyncJobRequestData syncJobRequest) throws CMSItemNotFoundException {
        try {
            UserModel currentUser = getUserService().getCurrentUser();
            final SyncRequestData convertedSyncJobRequest = getDataMapper().map(syncJobRequest,
                SyncRequestData.class);
            getUserService().setCurrentUser(getUserService().getAdminUser());
            final de.hybris.platform.cmsfacades.data.SyncJobData syncJobResult = getSynchronizationFacade()
                .createCatalogSynchronization(convertedSyncJobRequest);
            getUserService().setCurrentUser(currentUser);
            return getDataMapper().map(syncJobResult, SyncJobData.class);
        } catch (final ValidationException e) {
            throw new WebserviceValidationException(e.getValidationObject());
        }
    }


    /**
     * Retrieves the synchronization status for a catalog based on the provided cron job code.
     *
     * <p>This endpoint is used to get the details of the last synchronization job
     * for a given catalog, typically identified by the catalog ID. The synchronization job is
     * identified by its {@code cronJobCode}.</p>
     *
     * @param cronJobCode the unique code of the synchronization cron job
     * @return a {@link SyncJobData} object representing the status of the synchronization job
     * @throws ModelNotFoundException       if no cron job is found for the given code
     * @throws AmbiguousIdentifierException if multiple cron jobs are found with the given code
     * @see de.hybris.platform.cmsfacades.data.SyncJobData
     * @see SyncJobData
     */
    @Secured({"ROLE_CLIENT_ACCESSHUB"})
    @GetMapping(value = "/synchronizations/status/{cronJobCode}")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    @Operation(summary = "Gets synchronization status.", description =
        "Retrieves the status of the last synchronization for a catalog. Information is\n" +
            "retrieved based on a given catalog, source version and target version ids.",
        operationId = "getSynchronizationByCatalog")
    @ApiResponse(responseCode = "200", description = "The synchronization status")
    @Parameter(name = "catalogId", description = "The catalog id", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
    @Parameter(name = "cronJobCode", description = "The cronJob code", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
    @SecurePortalUnauthenticatedAccess
    public SyncJobData getSyncJobStatusByCode(
        @PathVariable final String cronJobCode)
        throws ModelNotFoundException, AmbiguousIdentifierException {
        final de.hybris.platform.cmsfacades.data.SyncJobData syncJobResult = getPartnerSyncJobHelper().getSyncJobStatusByCode(
            cronJobCode);
        return getDataMapper().map(syncJobResult, SyncJobData.class);
    }

    public PartnerSyncJobHelper getPartnerSyncJobHelper() {
        return partnerSyncJobHelper;
    }

    public SynchronizationFacade getSynchronizationFacade() {
        return synchronizationFacade;
    }

    protected UserService getUserService() {
        return userService;
    }

}
