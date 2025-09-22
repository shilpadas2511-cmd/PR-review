/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.core.order.strategies.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.order.strategies.IbmPartnerCartDetailsStrategy;
import com.ibm.commerce.partner.core.utils.PartnerUtils;
import de.hybris.platform.commerceservices.enums.SalesApplication;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.Date;


/**
 * Default implementation of {@link IbmPartnerCartDetailsStrategy}
 */
public class DefaultIbmPartnerCartDetailsStrategy implements IbmPartnerCartDetailsStrategy {

    private final ModelService modelService;
    private final DefaultIbmPartnerQuoteChannelStrategy ibmPartnerQuoteChannelStrategy;
    public static final String DATE_PATTERN = "yyyy-MM-dd";

    public DefaultIbmPartnerCartDetailsStrategy(final ModelService modelService,
        DefaultIbmPartnerQuoteChannelStrategy ibmPartnerQuoteChannelStrategy) {
        this.modelService = modelService;
        this.ibmPartnerQuoteChannelStrategy = ibmPartnerQuoteChannelStrategy;
    }

    @Override
    public void updateCart(final CommerceCheckoutParameter parameter) {
        final IbmPartnerCartModel ibmCartModel = (IbmPartnerCartModel) parameter.getCart();

        validateParameterNotNull(ibmCartModel, "Cart model cannot be null");
        ibmCartModel.setOpportunity(parameter.getOpportunity());
        ibmCartModel.setBillToUnit(parameter.getBillToUnit());
        ibmCartModel.setUnit(parameter.getShipToUnit());
        ibmCartModel.setSoldThroughUnit(parameter.getSoldThroughUnit());
        ibmCartModel.setAgreementDetail(parameter.getAgreementDetail());
        if (parameter.getQuoteExpirationDate()!=null) {
            setQuoteExpirationDate(parameter, ibmCartModel);
        }
        if (ibmCartModel.getQuoteReference() == null) {
            ibmCartModel.setSalesApplication(
                parameter.getSalesApplication() == null ? SalesApplication.PARTNER_COMMERCE
                    : parameter.getSalesApplication());
        }
        ibmPartnerQuoteChannelStrategy.populateDistributionChannel(ibmCartModel,
            parameter.getSoldThroughUnit());
        getModelService().save(ibmCartModel);
        getModelService().refresh(ibmCartModel);
    }

    protected void setQuoteExpirationDate(final CommerceCheckoutParameter parameter,final IbmPartnerCartModel ibmCartModel){
        Date quoteExpirationDate =PartnerUtils.convertStringToDate(parameter.getQuoteExpirationDate(), DATE_PATTERN);
        ibmCartModel.setQuoteExpirationDate(quoteExpirationDate);
        if(ibmCartModel.getQuoteReference() != null
            && ibmCartModel.getQuoteReference()  instanceof IbmPartnerQuoteModel quoteModel){
            quoteModel.setQuoteExpirationDate(quoteExpirationDate);
            ibmCartModel.setQuoteReference(quoteModel);
            getModelService().save(quoteModel);
            getModelService().refresh(quoteModel);
        }
    }


    public ModelService getModelService() {
        return modelService;
    }

}
