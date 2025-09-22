package com.ibm.commerce.partner.occ.controllers;

import com.ibm.commerce.partner.facades.opportunity.PartnerOpportunityFacade;
import com.ibm.commerce.partner.facades.opportunity.data.OpportunityCustomerNumberSearchRequestData;
import com.ibm.commerce.partner.facades.opportunity.data.OpportunityDetailsData;
import com.ibm.commerce.partner.facades.opportunity.data.OpportunityDetailsListData;
import com.ibm.commerce.partner.facades.opportunity.data.OpportunityOwnerMailSearchRequestData;
import com.ibm.commerce.partner.facades.opportunity.data.OpportunitySearchDetailsData;
import com.ibm.commerce.partner.facades.opportunity.data.OpportunitySearchDetailsListData;
import com.ibm.commerce.partner.facades.opportunity.data.OpportunitySearchRequestData;
import com.ibm.commerce.partner.facades.opportunity.data.OpportunitySearchbyNumberRequestData;
import com.ibm.commerce.partnerwebservicescommons.dto.opportunity.OpportunityCustomerNumberSearchRequestWsDTO;
import com.ibm.commerce.partnerwebservicescommons.dto.opportunity.OpportunityNumberSearchRequestWsDTO;
import com.ibm.commerce.partnerwebservicescommons.dto.opportunity.OpportunityOwnerMailSearchRequestWsDTO;
import com.ibm.commerce.partnerwebservicescommons.dto.opportunity.OpportunitySearchRequestWsDTO;
import com.ibm.commerce.partnerwebservicescommons.dto.opportunity.PartnerOpportunityListWsDTO;
import com.ibm.commerce.partnerwebservicescommons.dto.opportunity.PartnerOpportunitySearchListWsDTO;
import de.hybris.platform.b2bocc.security.SecuredAccessConstants;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Resource;
import javax.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * PartnerOpportunitySearchController.To expose Opportunity Search based on Reseller CEID & Customer
 * ICN B2BCartsController API's.
 */
@RestController
@ApiVersion("v2")
@Tag(name = "B2B Opportunity Search Carts")
@RequestMapping(value = "/{baseSiteId}/users/{userId}/opportunity/")
public class PartnerOpportunityController extends PartnerBaseController {


    @Resource(name = "opportunityFacade")
    private PartnerOpportunityFacade opportunityFacade;

    @Resource(name = "dataMapper")
    private DataMapper dataMapper;

    @Resource(name = "partnerOpportunityValidator")
    private Validator partnerOpportunityValidator;

    @Resource(name = "partnerOpportunityCustomerNumberValidator")
    private Validator partnerOpportunityCustomerNumberValidator;

    @Resource(name = "partnerOpportunityNumberSearchValidator")
    private Validator partnerOpportunityNumberSearchValidator;

    @Resource(name = "partnerOpportunitySearchOwnerMailValidator")
    private Validator partnerOpportunitySearchOwnerMailValidator;

    /**
     * PartnerOpportunitySearchController.fetchOpportunities To expose  list of Opportunities Search
     * based on Reseller CEID & Customer ICN B2BCartsController API's.
     */
    @PostMapping(value = "/getopportunities", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(operationId = "getOpportunities", summary = "Get list of a valid opportunities.", description = "Obtain a list of valid opportunities from the opportunity service based on the customer ICN and resellerCEID")
    @Secured({SecuredAccessConstants.ROLE_CUSTOMERGROUP,
        SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP})
    @ApiBaseSiteIdAndUserIdParam
    public PartnerOpportunityListWsDTO fetchOpportunities(
        @Parameter(description = "opportunity search DTO", required = true) @RequestBody @Nonnull @Valid final OpportunitySearchRequestWsDTO opportunitySearchRequestWsDTO,
        @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        PartnerOpportunityListWsDTO response;
        try {
            validate(opportunitySearchRequestWsDTO, "opportunitySearchRequestWsDTO",
                partnerOpportunityValidator);
        } catch (WebserviceValidationException e) {
            final List<OpportunitySearchDetailsData> opportunityDetailsData = new ArrayList<>();
            return getDataMapper().map(getOpportunitiesDetailsSearchDataList(opportunityDetailsData),
                PartnerOpportunityListWsDTO.class, fields);
        }
        OpportunitySearchRequestData searchRequestData = getDataMapper().map(
            opportunitySearchRequestWsDTO, OpportunitySearchRequestData.class);
        final List<OpportunitySearchDetailsData> opportunityDetailsData = getOpportunityFacade().fetchOpportunityDetails(
            searchRequestData);
        response = getDataMapper().map(getOpportunitiesDetailsSearchDataList(opportunityDetailsData),
            PartnerOpportunityListWsDTO.class, fields);
        return response;
    }


    /**
     * PartnerOpportunitySearchController.fetchOpportunitiesByNumber To expose  list of
     * Opportunities Search based on opportunity number  & resellerID B2BCartsController API's.
     */
    @PostMapping(value = "/getopportunitiesbynumber", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(operationId = "getOpportunitiesByNumber", summary = "Get list of a valid opportunities by Number Search.", description = "Obtain a list of valid opportunities from the opportunity service based on the OpportunityNumber and resellerCEID")
    @Secured({SecuredAccessConstants.ROLE_CUSTOMERGROUP,
        SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP})
    @ApiBaseSiteIdAndUserIdParam
    public PartnerOpportunitySearchListWsDTO fetchOpportunitiesByNumber(
        @Parameter(description = "opportunity search DTO", required = true) @RequestBody @Nonnull @Valid final OpportunityNumberSearchRequestWsDTO opportunityNumberSearchRequestWsDTO,
        @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        try {
            validate(opportunityNumberSearchRequestWsDTO, "opportunityNumberSearchRequestWsDTO",
                partnerOpportunityNumberSearchValidator);
        } catch (WebserviceValidationException e) {
            return new PartnerOpportunitySearchListWsDTO();
        }
        OpportunitySearchbyNumberRequestData searchRequestData = getDataMapper().map(
            opportunityNumberSearchRequestWsDTO, OpportunitySearchbyNumberRequestData.class);
        final List<OpportunitySearchDetailsData> opportunityDetailsData = getOpportunityFacade().fetchOpportunitiesByNumber(
            searchRequestData);
        return getDataMapper().map(getOpportunitiesDetailsSearchDataList(opportunityDetailsData),
            PartnerOpportunitySearchListWsDTO.class, fields);
    }

    /**
     * PartnerOpportunitySearchController.fetchOpportunitiesByOwnerMail To expose  list of
     * Opportunities Search based on opportunity owner email  & resellerID B2BCartsController
     * API's.
     */
    @PostMapping(value = "/getopportunitiesbymailid", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(operationId = "getOpportunitiesByMail", summary = "Get list of a valid opportunities Search by Owner Mail", description = "Obtain a list of valid opportunities from the opportunity service based on the Opportunity ownerMail and resellerCEID")
    @Secured({SecuredAccessConstants.ROLE_CUSTOMERGROUP,
        SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP})
    @ApiBaseSiteIdAndUserIdParam
    public PartnerOpportunitySearchListWsDTO fetchOpportunitiesByOwnerMail(
        @Parameter(description = "opportunity search DTO", required = true) @RequestBody @Nonnull @Valid final OpportunityOwnerMailSearchRequestWsDTO opportunityOwnerMailSearchRequestWsDTO,
        @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        try {
            validate(opportunityOwnerMailSearchRequestWsDTO,
                "opportunityOwnerMailSearchRequestWsDTO",
                partnerOpportunitySearchOwnerMailValidator);
        } catch (WebserviceValidationException e) {
            return new PartnerOpportunitySearchListWsDTO();
        }
        OpportunityOwnerMailSearchRequestData searchRequestData = getDataMapper().map(
            opportunityOwnerMailSearchRequestWsDTO, OpportunityOwnerMailSearchRequestData.class);
        final List<OpportunitySearchDetailsData> opportunityDetailsData = getOpportunityFacade().fetchOpportunitiesByOwnerEmail(
            searchRequestData);
        return getDataMapper().map(getOpportunitiesDetailsSearchDataList(opportunityDetailsData),
            PartnerOpportunitySearchListWsDTO.class, fields);
    }

    /**
     * PartnerOpportunitySearchController.fetchOpportunitiesByCustomerNumber To expose  list of
     * Opportunities Search based on customer number , country code  & resellerID B2BCartsController
     * API's.
     */
    @PostMapping(value = "/getopportunitiesbycustomernumber", consumes = {
        MediaType.APPLICATION_JSON_VALUE})
    @Operation(operationId = "getOpportunitiesByCustomerNumber", summary = "Get list of a valid opportunities by customer number search.", description = "Obtain a list of valid opportunities from the opportunity service based on the customerNumber, countryCode")
    @Secured({SecuredAccessConstants.ROLE_CUSTOMERGROUP,
        SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP})
    @ApiBaseSiteIdAndUserIdParam
    public PartnerOpportunitySearchListWsDTO fetchOpportunitiesByCustomerNumber(
        @Parameter(description = "opportunity search DTO", required = true) @RequestBody @Nonnull @Valid final OpportunityCustomerNumberSearchRequestWsDTO opportunityCustomerNumberSearchRequestWsDTO,
        @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        try {
            validate(opportunityCustomerNumberSearchRequestWsDTO,
                "opportunityCustomerNumberSearchRequestWsDTO",
                partnerOpportunityCustomerNumberValidator);
        } catch (WebserviceValidationException e) {
            return new PartnerOpportunitySearchListWsDTO();
        }
        OpportunityCustomerNumberSearchRequestData searchRequestData = getDataMapper().map(
            opportunityCustomerNumberSearchRequestWsDTO,
            OpportunityCustomerNumberSearchRequestData.class);
        final List<OpportunitySearchDetailsData> opportunityDetailsData = getOpportunityFacade().fetchOpportunitiesByCustomerNumber(
            searchRequestData);
        return getDataMapper().map(getOpportunitiesDetailsSearchDataList(opportunityDetailsData),
            PartnerOpportunitySearchListWsDTO.class, fields);
    }

    /**
     * @param result converts List<OpportunityDetailsData> to OpportunityDetailsListData.
     */
    protected OpportunitySearchDetailsListData getOpportunitiesDetailsSearchDataList(
        final List<OpportunitySearchDetailsData> result) {
        final OpportunitySearchDetailsListData opportunityDetailsListData = new OpportunitySearchDetailsListData();
        opportunityDetailsListData.setOpportunities(result);
        return opportunityDetailsListData;
    }

    protected DataMapper getDataMapper() {
        return dataMapper;
    }

    protected void setDataMapper(final DataMapper dataMapper) {
        this.dataMapper = dataMapper;
    }

    public PartnerOpportunityFacade getOpportunityFacade() {
        return opportunityFacade;
    }

}
