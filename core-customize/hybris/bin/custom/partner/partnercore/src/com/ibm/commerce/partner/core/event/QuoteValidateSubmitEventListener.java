package com.ibm.commerce.partner.core.event;


import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import de.hybris.platform.commerceservices.model.process.QuoteProcessModel;
import de.hybris.platform.core.model.order.QuoteModel;

import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;

import de.hybris.platform.servicelayer.model.ModelService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.util.HashMap;
import java.util.Map;

/**
 * Listener for handling {@link QuoteValidateSubmitEvent}. This listener is responsible for
 * triggering the validation and submission of quote process for buyer.
 */
public class QuoteValidateSubmitEventListener extends
    AbstractEventListener<QuoteValidateSubmitEvent> {

    private ModelService modelService;
    private BusinessProcessService businessProcessService;
    private static final Logger LOG = LoggerFactory.getLogger(
        QuoteValidateSubmitEventListener.class);

    /**
     * Handles the {@link QuoteValidateSubmitEvent}. It retrieves the submitted quote ID and
     * triggers the corresponding business process.
     *
     * @param event the event containing the submitted quote ID
     */
    @Override
    protected void onEvent(final QuoteValidateSubmitEvent event) {

        LOG.info(PartnercoreConstants.QUOTE_VALIDATION_EVENT_SUBMIT_LISTENER_LOG,
            event.getQuote().getCode());

        final Map<String, Object> contextParams = new HashMap<String, Object>();
        contextParams.put(PartnercoreConstants.QUOTE_USER_TYPE, event.getQuoteUserType());

        final QuoteProcessModel quoteValidateSubmitProcessModel = (QuoteProcessModel) getBusinessProcessService()
            .createProcess(
                PartnercoreConstants.QUOTE_VALIDATE_SUBMIT_PROCESS + PartnercoreConstants.HYPHEN
                    + event.getQuote()
                    .getCode() + PartnercoreConstants.HYPHEN
                    + event.getQuote().getStore().getUid()
                    + PartnercoreConstants.HYPHEN + System.currentTimeMillis(),
                PartnercoreConstants.SAP_CPQ_QUOTE_COMMON_VALIDATE_SUBMIT_PROCESS, contextParams);

        LOG.info(PartnercoreConstants.QUOTE_VALIDATE_SUBMIT_EVENT_CODE,
            quoteValidateSubmitProcessModel.getCode());

        final QuoteModel quoteModel = event.getQuote();
        quoteValidateSubmitProcessModel.setQuoteCode(quoteModel.getCode());
        getModelService().save(quoteValidateSubmitProcessModel);
        // start the business process
        getBusinessProcessService().startProcess(quoteValidateSubmitProcessModel);
    }

    /**
     * Gets the business process service.
     *
     * @return the business process service
     */
    protected BusinessProcessService getBusinessProcessService() {
        return businessProcessService;
    }

    /**
     * Sets the business process service.
     */
    @Required
    public void setBusinessProcessService(final BusinessProcessService businessProcessService) {
        this.businessProcessService = businessProcessService;
    }

    /**
     * Gets the model service.
     *
     * @return the model service
     */
    protected ModelService getModelService() {
        return modelService;
    }

    /**
     * Sets the model service
     */
    @Required
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

}
