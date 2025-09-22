package com.ibm.commerce.partner.facades.order.populators;

import com.ibm.commerce.data.order.IbmAddToCartParamsData;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.facades.company.PartnerB2BUnitFacade;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.springframework.lang.NonNull;

/**
 * Populates Bill To Information in CommerceCheckoutParam
 */
public class CommerceCheckoutParamBillToPopulator implements
    Populator<IbmAddToCartParamsData, CommerceCheckoutParameter> {

    private final PartnerB2BUnitFacade b2BUnitFacade;

    public CommerceCheckoutParamBillToPopulator(final PartnerB2BUnitFacade b2BUnitFacade) {
        this.b2BUnitFacade = b2BUnitFacade;
    }

    @Override
    public void populate(@NonNull final IbmAddToCartParamsData source,
        @NonNull final CommerceCheckoutParameter target) throws ConversionException {
        if (source.getBillToUnit() != null) {
            target.setBillToUnit(getB2BUnitFacade().getOrCreate(source.getBillToUnit()));
        }else if(target.getCart()!=null && target.getCart() instanceof IbmPartnerCartModel cartModel) {
            target.setBillToUnit(cartModel.getBillToUnit());
        }
    }

    public PartnerB2BUnitFacade getB2BUnitFacade() {
        return b2BUnitFacade;
    }
}
