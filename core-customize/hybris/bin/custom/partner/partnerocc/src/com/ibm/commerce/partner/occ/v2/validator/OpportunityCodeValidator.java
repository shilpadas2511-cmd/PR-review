package com.ibm.commerce.partner.occ.v2.validator;

import com.ibm.commerce.partner.core.opportunity.data.response.OpportunityDetailsSearchResponseData;
import com.ibm.commerce.partner.core.opportunity.service.PartnerOpportunityService;
import com.ibm.dto.order.IbmAddToCartParamsWsDTO;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Implementation for {@link Validator} "validates that the opportunityId is valid and that the
 * reseller is active for the logged-in user.
 */
public class OpportunityCodeValidator implements Validator {

    private final PartnerOpportunityService opportunityService;


    OpportunityCodeValidator(
        final PartnerOpportunityService opportunityService) {
        this.opportunityService = opportunityService;
    }

    @Override
    public boolean supports(final Class<?> arg0) {
        return String.class.isAssignableFrom(arg0);
    }

    /**
     * Overrides the validate method to ensure that the opportunityCode and resellerId are valid. It
     * also checks that the opportunityId is valid and that the reseller is active for the logged-in
     * user.
     */
    @Override
    public void validate(final Object target, final Errors errors) {

        if (target instanceof IbmAddToCartParamsWsDTO ibmAddToCartParamsWsDTO) {
            if (ObjectUtils.isNotEmpty(
                ibmAddToCartParamsWsDTO.getOpportunity()) && StringUtils.isNotBlank(
                ibmAddToCartParamsWsDTO.getOpportunity().getCode()) && ObjectUtils.isNotEmpty(
                ibmAddToCartParamsWsDTO.getSoldThroughUnit()) && ObjectUtils.isNotEmpty(
                ibmAddToCartParamsWsDTO.getShipToUnit())) {
                if (!isValidOpportuity(ibmAddToCartParamsWsDTO)) {
                    errors.rejectValue("Opportunity", null,
                        "Opportunity Not valid");
                }
            }

        }

    }

    /**
     * Validates the opportunityCode using the list obtained from the opportunity service.
     *
     * @param ibmAddToCartParamsWsDTO return boolean.
     */
    protected boolean isValidOpportuity(final IbmAddToCartParamsWsDTO ibmAddToCartParamsWsDTO) {
        boolean valid = false;
        if (ibmAddToCartParamsWsDTO.getOpportunity().getCode() != null
            && ibmAddToCartParamsWsDTO.getSoldThroughUnit().getUid() != null
            && ibmAddToCartParamsWsDTO.getShipToUnit().getIbmCustomerNumber() != null
            && ibmAddToCartParamsWsDTO.getBillToUnit() != null) {

            List<OpportunityDetailsSearchResponseData> opportunities = getOpportunityService().getOpportunities(
                ibmAddToCartParamsWsDTO.getSoldThroughUnit().getUid(),
                ibmAddToCartParamsWsDTO.getBillToUnit().getUid(),
                ibmAddToCartParamsWsDTO.getShipToUnit().getIbmCustomerNumber());
            return CollectionUtils.isNotEmpty(opportunities) && opportunities.stream()
                .anyMatch(opportunity -> opportunity.getOpportunityNumber().equals(
                    ibmAddToCartParamsWsDTO.getOpportunity().getCode()));
        }
        return valid;
    }

    public PartnerOpportunityService getOpportunityService() {
        return opportunityService;
    }

}


