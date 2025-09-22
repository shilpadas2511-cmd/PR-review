/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.core.outbound.actions;

import de.hybris.platform.commerceservices.model.process.QuoteProcessModel;
import de.hybris.platform.core.enums.ExportStatus;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.order.QuoteService;

import java.io.StringReader;
import java.text.MessageFormat;
import java.util.Objects;

import javax.xml.bind.JAXB;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;

import com.ibm.commerce.partner.core.actions.PartnerAbstractSimpleDecisionAction;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.outbound.service.PartnerSapCpiOutboundQuoteConversionService;
import com.ibm.commerce.partner.core.outbound.service.PartnerSapCpiOutboundQuoteService;
import com.ibm.commerce.partner.core.outboundservices.quote.data.request.PartnerCpqQuotesRequestData;
import com.ibm.commerce.partner.core.outboundservices.quote.data.response.PartnerCpqQuotesResponseData;


/**
 * Partner SapCpi Send Quote Action class is used to execute the action to process the Quote
 * submission to CPI.
 */
public class PartnerSapCpiSendQuoteAction extends
    PartnerAbstractSimpleDecisionAction<QuoteProcessModel> {

    protected PartnerSapCpiSendQuoteAction(final Integer maxRetryAllowed,
        final Integer retryDelay) {
        super(maxRetryAllowed, retryDelay);
    }

    private static final Logger LOG = LogManager.getLogger(PartnerSapCpiSendQuoteAction.class);

    private QuoteService quoteService;
    private PartnerSapCpiOutboundQuoteConversionService partnerQuoteConversionService;
    private PartnerSapCpiOutboundQuoteService partnerSapCpiOutboundQuoteService;

    @Override
    public Transition executeAction(final QuoteProcessModel process) {
        Transition result = Transition.NOK;
        if (StringUtils.isNotBlank(process.getQuoteCode())) {
            final QuoteModel quote = getQuoteService().getCurrentQuoteForCode(
                process.getQuoteCode());
            PartnerCpqQuotesRequestData cpqOutboundQuotes;
            try {
                cpqOutboundQuotes = getPartnerQuoteConversionService().convertQuoteToSapCpiQuote(
                    quote);
            } catch (final IllegalArgumentException e) {
                LOG.error("SCPI Quote Conversion failed due to improper data for quoteId: {} - {}",
                    quote.getCode(), e.getCause());
                return Transition.NOK;
            }
            final String msg = MessageFormat.format("In {0} for process code : {1}",
                this.getClass().getSimpleName(), process.getCode());
            LOG.debug(msg);
            try {
                final ResponseEntity<String> responseEntityString = getPartnerSapCpiOutboundQuoteService().sendQuote(
                    cpqOutboundQuotes);
                if (Objects.nonNull(responseEntityString)) {
                    logXmlResponse(responseEntityString.getBody());
                    if (responseEntityString.getStatusCode().is2xxSuccessful()) {
							  processQuoteResponse(process, quote, responseEntityString);
                    } else {
                        setQuoteStatus(quote, ExportStatus.NOTEXPORTED, null);
                        LOG.info("The quote {} has not been sent to the backend! {} {}",
                            quote.getCode(), "- CPQ/CPI Error Message:::", responseEntityString);
                        resetEndMessage(process, Transition.NOK.toString());
                        return retryOrFailAction(process, msg);
                    }
                }
            } catch (final Exception error) {
                setQuoteStatus(quote, ExportStatus.NOTEXPORTED, null);
                LOG.error("The quote {} has not been sent to the backend through SCPI! {}",
                    quote.getCode(), error.getMessage());
                resetEndMessage(process, error.getMessage());
                return retryOrFailAction(process, msg);
            }
            if (quote.getExportStatus().equals(ExportStatus.EXPORTED)) {
                result = Transition.OK;
            }
        }
        return result;
    }

	 private void processQuoteResponse(final QuoteProcessModel process, final QuoteModel quote,
			final ResponseEntity<String> responseEntityString)
	{
		final PartnerCpqQuotesResponseData quotesResponseData = this.getStringPropertyValue(
		    responseEntityString.getBody());
		if (Objects.nonNull(quotesResponseData) && CollectionUtils.isNotEmpty(
		    quotesResponseData.getQuotes()) && StringUtils.isNotBlank(
		    quotesResponseData.getQuotes().get(0).getQuoteNumber())) {
		    quote.setState(QuoteState.BUYER_SUBMITTED);
		    setQuoteStatus(quote, ExportStatus.EXPORTED,
		        quotesResponseData.getQuotes().get(0).getQuoteNumber());
		    LOG.info(String.format(
		        "The quote [%s] has been successfully sent to the backend through SCPI! %n%s",
		        quote.getCode(),
		        quotesResponseData.getQuotes().get(0).getQuoteNumber()));
		    resetEndMessage(process,
		        quotesResponseData.getQuotes().get(0).getMessage());
		}
	}

    protected PartnerCpqQuotesResponseData getStringPropertyValue(final String response) {
        try {
            return JAXB.unmarshal(new StringReader(response), PartnerCpqQuotesResponseData.class);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }


    protected void setQuoteStatus(final QuoteModel quote, final ExportStatus exportStatus,
        final String externalQuoteId) {
        if (externalQuoteId != null) {
            quote.setCpqExternalQuoteId(StringUtils.trim(externalQuoteId));
        }
        quote.setExportStatus(exportStatus);
        getModelService().save(quote);
    }

    protected void resetEndMessage(final QuoteProcessModel process, final String responseMessage) {
        process.setEndMessage(responseMessage);
        getModelService().save(process);
    }

    protected void logXmlResponse(final String xmlResponse) {
        // transaction ID internal to hybris, used to match each request and corresponding response in log, when enabled
        final String transactionId = RandomStringUtils.random(16, true, true);
        LOG.info(PartnercoreConstants.TRANSACTION_ID_LOG + transactionId
            + PartnercoreConstants.DOUBLE_NEWLINE + xmlResponse);
    }

    /**
     * @return the partnerQuoteConversionService
     */
    public PartnerSapCpiOutboundQuoteConversionService getPartnerQuoteConversionService() {
        return partnerQuoteConversionService;
    }

    /**
     * @param partnerQuoteConversionService the partnerQuoteConversionService to set
     */
    public void setPartnerQuoteConversionService(
        final PartnerSapCpiOutboundQuoteConversionService partnerQuoteConversionService) {
        this.partnerQuoteConversionService = partnerQuoteConversionService;
    }

    /**
     * @return the partnerSapCpiOutboundQuoteService
     */
    public PartnerSapCpiOutboundQuoteService getPartnerSapCpiOutboundQuoteService() {
        return partnerSapCpiOutboundQuoteService;
    }

    /**
     * @param partnerSapCpiOutboundQuoteService the partnerSapCpiOutboundQuoteService to set
     */
    public void setPartnerSapCpiOutboundQuoteService(
        final PartnerSapCpiOutboundQuoteService partnerSapCpiOutboundQuoteService) {
        this.partnerSapCpiOutboundQuoteService = partnerSapCpiOutboundQuoteService;
    }

    /**
     * @return the quoteService
     */
    public QuoteService getQuoteService() {
        return quoteService;
    }

    /**
     * @param quoteService the quoteService to set
     */
    public void setQuoteService(final QuoteService quoteService) {
        this.quoteService = quoteService;
    }

}
