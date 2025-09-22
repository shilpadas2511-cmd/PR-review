package com.ibm.commerce.partner.core.outbound.actions;

import com.ibm.commerce.partner.core.actions.PartnerAbstractSimpleDecisionAction;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.quote.services.impl.DefaultPartnerSapCpqQuoteService;
import de.hybris.platform.commerceservices.model.process.QuoteProcessModel;
import de.hybris.platform.core.enums.ExportStatus;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.task.RetryLaterException;
import java.text.MessageFormat;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Action responsible for adding an IBM seller collaborator to a quote and sending collaborator
 * information to CPQ (Configure, Price, Quote) system.
 */
public class PartnerAddIbmSellerCollaboratorAction extends
    PartnerAbstractSimpleDecisionAction<QuoteProcessModel> {

    private static final Logger LOG = LogManager.getLogger(
        PartnerAddIbmSellerCollaboratorAction.class);

    private DefaultPartnerSapCpqQuoteService partnerSapCpqQuoteService;
    private QuoteService quoteService;

    /**
     * Constructs an instance of PartnerAddIbmSellerCollaboratorAction.
     *
     * @param maxRetryAllowed the maximum number of retry attempts allowed
     * @param retryDelay      the delay between retry attempts
     */
    protected PartnerAddIbmSellerCollaboratorAction(final Integer maxRetryAllowed,
        final Integer retryDelay) {
        super(maxRetryAllowed, retryDelay);
    }

    /**
     * Executes the action to add an IBM seller collaborator and update the quote status.
     *
     * @param quoteProcessModel the quote process model containing the quote details
     * @return Transition.OK if successful, Transition.NOK otherwise
     * @throws RetryLaterException if the process should be retried later
     */
    @Override
    public Transition executeAction(QuoteProcessModel quoteProcessModel) {
        Transition result = Transition.NOK;
        if (StringUtils.isNotBlank(quoteProcessModel.getQuoteCode())) {
            final QuoteModel quote = getQuoteService().getCurrentQuoteForCode(
                quoteProcessModel.getQuoteCode());
            if (!Objects.isNull(quote)) {
                try {
                    getPartnerSapCpqQuoteService().postCollaboratorInfo(
                        (IbmPartnerQuoteModel) quote);
                    setQuoteStatus(quote, ExportStatus.EXPORTED, quote.getExternalQuoteId());
                    LOG.info(
                        PartnercoreConstants.COLLAB_ACTION_LOG_SUCCESS,
                        quote.getCode(), quote.getExternalQuoteId());
                } catch (Exception e) {
                    LOG.error(PartnercoreConstants.COLLAB_ACTION_LOG_FAILURE, quote.getCode(), e);
                    setQuoteStatus(quote, ExportStatus.EXPORTED_WITH_SELLER_COLLAB_ERROR,
                        quote.getExternalQuoteId());
                    resetEndMessage(quoteProcessModel, Transition.NOK.toString());
                    return retryOrFailAction(quoteProcessModel,
                        MessageFormat.format(PartnercoreConstants.COLLAB_ACTION_RETRY_LOG_MSG,
                            this.getClass().getSimpleName(), quoteProcessModel.getCode()));
                }
                if (quote.getExportStatus().equals(ExportStatus.EXPORTED)) {
                    result = Transition.OK;
                    resetEndMessage(quoteProcessModel, Transition.OK.toString());
                }
            }
        }
        return result;
    }

    /**
     * Updates the export status of the given quote.
     *
     * @param quote           the quote to update
     * @param exportStatus    the new export status
     * @param externalQuoteId the external quote ID associated with CPQ
     */
    protected void setQuoteStatus(final QuoteModel quote, final ExportStatus exportStatus,
        final String externalQuoteId) {
        if (externalQuoteId != null) {
            quote.setCpqExternalQuoteId(StringUtils.trim(externalQuoteId));
        }
        quote.setExportStatus(exportStatus);
        getModelService().save(quote);
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
