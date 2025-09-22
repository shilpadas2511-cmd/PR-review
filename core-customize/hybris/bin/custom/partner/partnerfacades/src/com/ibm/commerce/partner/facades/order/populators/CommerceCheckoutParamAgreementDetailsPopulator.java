package com.ibm.commerce.partner.facades.order.populators;

import com.ibm.commerce.data.order.IbmAddToCartParamsData;
import com.ibm.commerce.partner.company.endcustomer.data.IbmPartnerAgreementDetailData;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.IbmPartnerEndCustomerB2BUnitModel;
import com.ibm.commerce.partner.facades.company.PartnerB2BUnitFacade;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.springframework.lang.NonNull;

/**
 * Populates Agreement Details Information in CommerceCheckoutParam
 */
public class CommerceCheckoutParamAgreementDetailsPopulator implements
    Populator<IbmAddToCartParamsData, CommerceCheckoutParameter> {

    private PartnerB2BUnitFacade b2BUnitFacade;

    public CommerceCheckoutParamAgreementDetailsPopulator(
        final PartnerB2BUnitFacade b2BUnitFacade) {
        this.b2BUnitFacade = b2BUnitFacade;
    }

    @Override
    public void populate(@NonNull final IbmAddToCartParamsData source,
        @NonNull final CommerceCheckoutParameter target) throws ConversionException {
        //Partner Agreement Details
        if (source.getAgreementDetail() != null
            && target.getShipToUnit() instanceof IbmPartnerEndCustomerB2BUnitModel endCustomerB2BUnit) {
            IbmPartnerAgreementDetailData partnerAgreementDetailData = source.getAgreementDetail();
            target.setAgreementDetail(
                getB2BUnitFacade().getOrCreatePartnerAgreementDetail(partnerAgreementDetailData,
                    endCustomerB2BUnit));
        }else if(target.getCart() !=null && target.getCart() instanceof IbmPartnerCartModel cartModel) {
            target.setAgreementDetail(cartModel.getAgreementDetail());
        }
    }

    public PartnerB2BUnitFacade getB2BUnitFacade() {
        return b2BUnitFacade;
    }
}
