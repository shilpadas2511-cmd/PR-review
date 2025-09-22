package com.ibm.commerce.partner.core.order.hook;

import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import de.hybris.platform.commerceservices.order.CommerceSaveCartException;
import de.hybris.platform.commerceservices.order.hook.CommerceCloneSavedCartMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceSaveCartParameter;
import de.hybris.platform.commerceservices.service.data.CommerceSaveCartResult;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.model.ModelService;


/**
 * Partner Commerce Hook to Clone cart
 */
public class DefaultPartnerCommerceCloneSavedCartBasicMethodHook implements
    CommerceCloneSavedCartMethodHook {

    private final ModelService modelService;

    public DefaultPartnerCommerceCloneSavedCartBasicMethodHook(ModelService modelService) {
        this.modelService = modelService;
    }

    @Override
    public void beforeCloneSavedCart(CommerceSaveCartParameter parameters)
        throws CommerceSaveCartException {
        //Deliberately Left empty
    }

    @Override
    public void afterCloneSavedCart(CommerceSaveCartParameter parameters,
        CommerceSaveCartResult saveCartResult) throws CommerceSaveCartException {
        final CartModel savedCart = saveCartResult.getSavedCart();
        if (savedCart.getQuoteReference() == null
            && savedCart instanceof IbmPartnerCartModel partnerSaveCart) {
            partnerSaveCart.setPriceUid(null);
            getModelService().save(partnerSaveCart);
        }
    }

    public ModelService getModelService() {
        return modelService;
    }
}


