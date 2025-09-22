package com.ibm.commerce.partner.facades.order.populators;

import com.ibm.commerce.data.order.IbmAddToCartParamsData;
import com.ibm.commerce.partner.facades.constants.PartnerfacadesConstants;
import de.hybris.platform.commerceservices.enums.SalesApplication;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;

/**
 * Populates Sales Application Information in CommerceCheckoutParam
 */
public class CommerceCheckoutParamSalesApplicationPopulator implements
    Populator<IbmAddToCartParamsData, CommerceCheckoutParameter> {

    @Override
    public void populate(@NonNull final IbmAddToCartParamsData source,
        @NonNull final CommerceCheckoutParameter target) throws ConversionException {

        target.setSalesApplication(
            StringUtils.isNotBlank(source.getSalesApplication()) ?
                SalesApplication.valueOf(source.getSalesApplication())
                : SalesApplication.PARTNER_COMMERCE);

    }
}
