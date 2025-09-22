package com.ibm.commerce.partner.core.event;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import de.hybris.platform.commerceservices.model.process.QuoteProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Listener for handling {@link QuoteSubmittedEvent}. This listener is responsible for triggering
 * the quote process when a seller collaborator synchronization event occurs.
 */
public class QuoteSubmittedEventListener extends
    AbstractEventListener<QuoteSubmittedEvent> {

    private static final Logger LOG = LogManager.getLogger(
        QuoteSubmittedEventListener.class);

    private final ModelService modelService;
    private final BusinessProcessService businessProcessService;
    private final KeyGenerator processCodeGenerator;

    /**
     * Constructor for SyncSellerCollaboratorInfoEventListener.
     *
     * @param modelService           the model service for persisting data
     * @param businessProcessService the business process service for starting processes
     * @param processCodeGenerator   the key generator for generating process codes
     */
    public QuoteSubmittedEventListener(ModelService modelService,
        BusinessProcessService businessProcessService,
        KeyGenerator processCodeGenerator) {
        this.modelService = modelService;
        this.businessProcessService = businessProcessService;
        this.processCodeGenerator = processCodeGenerator;
    }

    /**
     * Handles the {@link QuoteSubmittedEvent}. It retrieves the submitted quote ID and triggers the
     * corresponding business process.
     *
     * @param event the event containing the submitted quote ID
     */
    @Override
    protected void onEvent(QuoteSubmittedEvent event) {
        String submittedQuoteId = event.getSubmittedQuoteId();
        final QuoteProcessModel quoteProcessModel = createProcess(event);
        quoteProcessModel.setQuoteCode(submittedQuoteId);
        getModelService().save(quoteProcessModel);
        LOG.info(PartnercoreConstants.COLLAB_LISTENER_LOG,
            submittedQuoteId, quoteProcessModel.getCode());
        getBusinessProcessService().startProcess(quoteProcessModel);
    }

    /**
     * Creates a new quote process model based on the event details.
     *
     * @param event the event containing the submitted quote ID
     * @return the created quote process model
     */
    protected QuoteProcessModel createProcess(final QuoteSubmittedEvent event) {
        return getBusinessProcessService().createProcess(getProcessCodeGenerator().generateFor(
                PartnercoreConstants.PARTNER_QUOTE_SUBMIT_PROCESS_CODE
                    + PartnercoreConstants.HYPHEN
                    + event.getSubmittedQuoteId()).toString(),
            PartnercoreConstants.PARTNER_QUOTE_SUBMIT_PROCESS_CODE);
    }

    /**
     * Gets the model service.
     *
     * @return the model service
     */
    public ModelService getModelService() {
        return modelService;
    }

    /**
     * Gets the business process service.
     *
     * @return the business process service
     */
    public BusinessProcessService getBusinessProcessService() {
        return businessProcessService;
    }

    /**
     * Gets the process code generator.
     *
     * @return the process code generator
     */
    public KeyGenerator getProcessCodeGenerator() {
        return processCodeGenerator;
    }

}
