/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.core.outbound.actions;


import com.ibm.commerce.partner.core.actions.PartnerAbstractSimpleDecisionAction;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.outboundservices.quote.data.response.PartnerCPQValidateQuoteResponseData;
import com.ibm.commerce.partner.core.quote.services.impl.DefaultPartnerSapCpqQuoteService;
import de.hybris.platform.core.enums.QuoteState;
import org.apache.commons.lang3.StringUtils;

import de.hybris.platform.commerceservices.model.process.QuoteProcessModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.order.QuoteService;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Action responsible for validating buyer quote details information with CPQ (Configure, Price,
 * Quote) system before submitting.
 */
public class PartnerQuoteValidationAction extends
    PartnerAbstractSimpleDecisionAction<QuoteProcessModel> {

    private static final Logger LOG = LoggerFactory.getLogger(
        PartnerQuoteValidationAction.class);

    private DefaultPartnerSapCpqQuoteService partnerSapCpqQuoteService;
    private QuoteService quoteService;

    protected PartnerQuoteValidationAction(final Integer maxRetryAllowed,
        final Integer retryDelay) {
        super(maxRetryAllowed, retryDelay);
    }

    /**
     * Executes CPQ validation for the quote in the process.
     *
     * @param quoteProcessModel process containing the quote reference
     * @return {@link Transition#OK} if valid, otherwise {@link Transition#NOK}
     */
    @Override
    public Transition executeAction(QuoteProcessModel quoteProcessModel) {
        Transition result = Transition.NOK;
        if (StringUtils.isNotBlank(quoteProcessModel.getQuoteCode())) {
            final QuoteModel quote = getQuoteService().getCurrentQuoteForCode(
                quoteProcessModel.getQuoteCode());
            if (!Objects.isNull(quote)) {
                try {
                    PartnerCPQValidateQuoteResponseData response = getPartnerSapCpqQuoteService().cpqQuoteValidation(
                        (IbmPartnerQuoteModel) quote);
                    if (response.isValid()) {
                        quote.setState(QuoteState.VALIDATE_COMPLETED);
                        getModelService().save(quote);
                        result = Transition.OK;
                    } else {
                        quote.setState(QuoteState.VALIDATE_FAILED);
                        ((IbmPartnerQuoteModel) quote).setErrorMessage(
                            Objects.nonNull(response.getFailedValidations())
                                ? response.getFailedValidations().toString() : StringUtils.EMPTY);
                        getModelService().save(quote);
                    }
                } catch (Exception e) {
                    LOG.error(PartnercoreConstants.QUOTE_VALIDATION_ACTION_LOG_ERROR,
                        quote.getCode(), quote.getExternalQuoteId());
                    quote.setState(QuoteState.VALIDATION_ERROR);
                    getModelService().save(quote);
                    resetEndMessage(quoteProcessModel, Transition.NOK.toString());
                    return retryOrFailAction(quoteProcessModel, e.getMessage());
                }
            }
        }
        return result;
    }

    /**
     * Resets the end message for the given quote process model.
     *
     * @param process         the quote process model
     * @param responseMessage the response message to set
     */
    protected void resetEndMessage(final QuoteProcessModel process, final String responseMessage) {
        process.setEndMessage(responseMessage);
        getModelService().save(process);
    }

    /**
     * Retrieves the Partner SAP CPQ Quote Service.
     *
     * @return the partner SAP CPQ quote service
     */
    public DefaultPartnerSapCpqQuoteService getPartnerSapCpqQuoteService() {
        return partnerSapCpqQuoteService;
    }

    /**
     * Sets the Partner SAP CPQ Quote Service.
     *
     * @param partnerSapCpqQuoteService the partner SAP CPQ quote service to set
     */
    public void setPartnerSapCpqQuoteService(
        DefaultPartnerSapCpqQuoteService partnerSapCpqQuoteService) {
        this.partnerSapCpqQuoteService = partnerSapCpqQuoteService;
    }

    /**
     * Retrieves the Quote Service.
     *
     * @return the quote service
     */
    public QuoteService getQuoteService() {
        return quoteService;
    }

    /**
     * Sets the Quote Service.
     *
     * @param quoteService the quote service to set
     */
    public void setQuoteService(final QuoteService quoteService) {
        this.quoteService = quoteService;
    }

}
