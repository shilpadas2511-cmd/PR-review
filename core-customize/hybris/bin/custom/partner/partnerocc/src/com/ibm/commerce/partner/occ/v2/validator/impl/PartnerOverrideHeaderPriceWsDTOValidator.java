package com.ibm.commerce.partner.occ.v2.validator.impl;

import com.ibm.commerce.partnerwebservicescommons.dto.order.entry.pricing.PartnerOverrideHeaderPriceWsDTO;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class PartnerOverrideHeaderPriceWsDTOValidator implements Validator {


    @Override
    public void validate(Object target, Errors errors) {
        if (target instanceof PartnerOverrideHeaderPriceWsDTO partnerOverrideHeaderPriceWsDTO) {
            if (ObjectUtils.isNotEmpty(
                partnerOverrideHeaderPriceWsDTO.getOverrideTotalDiscount())) {
                if (Double.compare(partnerOverrideHeaderPriceWsDTO.getOverrideTotalDiscount(), 0)
                    < 0) {
                    errors.rejectValue("overrideTotalDiscount", null, "Total discount cannot be less than 0%");
                }
                if (Double.compare(partnerOverrideHeaderPriceWsDTO.getOverrideTotalDiscount(), 100)
                    > 0) {
                    errors.rejectValue("overrideTotalDiscount", null, "Total discount cannot be greater than 100%");
                }
            }
            if (ObjectUtils.isNotEmpty(
                partnerOverrideHeaderPriceWsDTO.getOverrideTotalPrice())) {
                if (Double.compare(partnerOverrideHeaderPriceWsDTO.getOverrideTotalPrice(), 0)
                    < 0) {
                    errors.rejectValue("overrideTotalPrice", null,
                        "Total Bid price cannot be less than $0");
                }
            }

        }
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return false;
    }

}