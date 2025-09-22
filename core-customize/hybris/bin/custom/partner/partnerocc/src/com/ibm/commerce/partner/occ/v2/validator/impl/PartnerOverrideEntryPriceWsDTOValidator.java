package com.ibm.commerce.partner.occ.v2.validator.impl;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.ibm.commerce.partnerwebservicescommons.dto.order.entry.pricing.PartnerOverrideEntryPriceWsDTO;

public class PartnerOverrideEntryPriceWsDTOValidator implements Validator {

	private static final String OVERRIDE_YEAR_TO_YEAR_GROWTH = "OverrideYearToYearGrowth";

	private static final String OVERRIDE_OBSOLETE_PRICE = "overrideObsoletePrice";

	private static final String OVERRIDE_OBSOLETE_PRICE_DEFAULT_MESSAGE ="Obsolete unit price cannot be less than $0";


    @Override
    public boolean supports(final Class<?> clazz) {
        return false;
    }

    @Override
    public void validate(final Object target, final Errors errors) {
        if (target instanceof final PartnerOverrideEntryPriceWsDTO partnerOverrideEntryPriceWsDTO) {
            if(partnerOverrideEntryPriceWsDTO.getEntryNumber() == null){
                errors.rejectValue("entryNumber", null, "entryNumber cannot null");
            }
            validateOverrideDiscount(errors, partnerOverrideEntryPriceWsDTO);
			validateOverrideObsoletePrice(errors, partnerOverrideEntryPriceWsDTO);
            if (ObjectUtils.isNotEmpty(
                partnerOverrideEntryPriceWsDTO.getOverridePrice())) {
                if (Double.compare(partnerOverrideEntryPriceWsDTO.getOverridePrice(), 0)
                    < 0) {
                    errors.rejectValue("overridePrice", null,
                        "Bid unit price cannot be less than $0");
                }
            }
			validateOverrideYTYDiscount(errors, partnerOverrideEntryPriceWsDTO);

        }
    }

	/**
	 * @param errors
	 * @param partnerOverrideEntryPriceWsDTO
	 */
	private void validateOverrideDiscount(final Errors errors, PartnerOverrideEntryPriceWsDTO partnerOverrideEntryPriceWsDTO)
	{
		if (ObjectUtils.isNotEmpty(
		    partnerOverrideEntryPriceWsDTO.getOverrideDiscount())) {
		    if (Double.compare(partnerOverrideEntryPriceWsDTO.getOverrideDiscount(), 0)
		        < 0) {
		        errors.rejectValue("overrideDiscount", null, "Discount cannot be less than 0%");
		    }
		    if (Double.compare(partnerOverrideEntryPriceWsDTO.getOverrideDiscount(), 100)
		        > 0) {
		        errors.rejectValue("overrideDiscount", null,
		            "Discount cannot be greater than 100%");
		    }
		}
	}

	private void validateOverrideYTYDiscount(final Errors errors, PartnerOverrideEntryPriceWsDTO partnerOverrideEntryPriceWsDTO)
	{
		if (ObjectUtils.isNotEmpty(
			partnerOverrideEntryPriceWsDTO.getOverrideYearToYearGrowth())) {
			if (Double.compare(partnerOverrideEntryPriceWsDTO.getOverrideYearToYearGrowth(), 0)
				< 0) {
				errors.rejectValue(OVERRIDE_YEAR_TO_YEAR_GROWTH, null, "Discount cannot be less than 0%");
			}
			if (Double.compare(partnerOverrideEntryPriceWsDTO.getOverrideYearToYearGrowth(), 100)
				> 0) {
				errors.rejectValue(OVERRIDE_YEAR_TO_YEAR_GROWTH, null,
					"Discount cannot be greater than 100%");
			}
		}
	}

	private void validateOverrideObsoletePrice(final Errors errors, PartnerOverrideEntryPriceWsDTO partnerOverrideEntryPriceWsDTO)
	{
		if (ObjectUtils.isNotEmpty(
			partnerOverrideEntryPriceWsDTO.getOverrideObsoletePrice())) {
			if (Double.compare(partnerOverrideEntryPriceWsDTO.getOverrideObsoletePrice(), NumberUtils.DOUBLE_ZERO)
				< 0) {
				errors.rejectValue(OVERRIDE_OBSOLETE_PRICE, null,
					OVERRIDE_OBSOLETE_PRICE_DEFAULT_MESSAGE);
			}
		}
	}
}