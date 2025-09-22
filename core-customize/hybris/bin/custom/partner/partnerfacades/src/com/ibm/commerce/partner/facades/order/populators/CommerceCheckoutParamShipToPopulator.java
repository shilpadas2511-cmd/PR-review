package com.ibm.commerce.partner.facades.order.populators;

import com.ibm.commerce.data.order.IbmAddToCartParamsData;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.facades.company.PartnerB2BUnitFacade;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.springframework.lang.NonNull;

/**
 * Populates Ship To Information in CommerceCheckoutParam
 */
public class CommerceCheckoutParamShipToPopulator implements
    Populator<IbmAddToCartParamsData, CommerceCheckoutParameter> {

    private final PartnerB2BUnitFacade b2BUnitFacade;

    public CommerceCheckoutParamShipToPopulator(final PartnerB2BUnitFacade b2BUnitFacade) {
        this.b2BUnitFacade = b2BUnitFacade;
    }

    @Override
    public void populate(@NonNull final IbmAddToCartParamsData source,
        @NonNull final CommerceCheckoutParameter target) throws ConversionException {
        if (source.getShipToUnit() != null) {
            target.setShipToUnit(getB2BUnitFacade().getOrCreate(source.getShipToUnit()));
        }else if(target.getCart()!=null && target.getCart() instanceof IbmPartnerCartModel cartModel) {
            target.setShipToUnit(cartModel.getUnit());
        }
    }

    public PartnerB2BUnitFacade getB2BUnitFacade() {
        return b2BUnitFacade;
    }
}
