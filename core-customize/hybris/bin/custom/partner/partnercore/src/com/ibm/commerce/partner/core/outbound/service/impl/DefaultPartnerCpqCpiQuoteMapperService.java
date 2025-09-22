package com.ibm.commerce.partner.core.outbound.service.impl;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.enums.PartnerQuoteChannelEnum;
import com.ibm.commerce.partner.core.model.IbmPartnerEndCustomerB2BUnitModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.outbound.service.PartnerCpqCpiQuoteMapperService;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.enumeration.EnumerationService;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * Default Partner PartnerCpqCpiQuote MapperService class is used to map the populate or map the
 * quote model field values to SAPCPQOutboundQuote data object
 */
public class DefaultPartnerCpqCpiQuoteMapperService implements
    PartnerCpqCpiQuoteMapperService<QuoteModel, SAPCPQOutboundQuoteModel> {

    private final String defaultDestinationChannel;
    private final String defaultSalesOrganization;
    private final String defaultFulfillmentSource;
    private final String defaultOpportunityId;
    private final String defaultJustificationCode;
    private final String defaultTransactionPriceLevel;
    private final EnumerationService enumerationService;
    private final String defaultSalesApplication;

    public DefaultPartnerCpqCpiQuoteMapperService(String defaultDestinationChannel,
        String defaultSalesOrganization, String defaultFulfillmentSource,
        String defaultOpportunityId, String defaultJustificationCode,
        String defaultTransactionPriceLevel, EnumerationService enumerationService,
        String defaultSalesApplication) {
        this.defaultDestinationChannel = defaultDestinationChannel;
        this.defaultSalesOrganization = defaultSalesOrganization;
        this.defaultFulfillmentSource = defaultFulfillmentSource;
        this.defaultOpportunityId = defaultOpportunityId;
        this.defaultJustificationCode = defaultJustificationCode;
        this.defaultTransactionPriceLevel = defaultTransactionPriceLevel;
        this.enumerationService = enumerationService;
        this.defaultSalesApplication = defaultSalesApplication;
    }


    @Override
    public void map(QuoteModel quoteModel,
        SAPCPQOutboundQuoteModel cpqOutboundQuote) {
        mapQuoteToSapCpqCpiOutboundQuote(quoteModel, cpqOutboundQuote);
    }

    protected void mapQuoteToSapCpqCpiOutboundQuote(QuoteModel quote,
        SAPCPQOutboundQuoteModel cpqOutboundQuote) {
        IbmPartnerQuoteModel quoteModel = (IbmPartnerQuoteModel) quote;
        cpqOutboundQuote.setQuoteId(quote.getCode());
        cpqOutboundQuote.setName(quote.getName());
        if (quote.getCurrency() != null) {
            cpqOutboundQuote.setCurrency(quote.getCurrency().getIsocode());
        }
        cpqOutboundQuote.setDistributionChannel(
            StringUtils.defaultIfBlank(quote.getCpqDistributionChannel(),
                PartnerQuoteChannelEnum.J.getCode()));
        if (Objects.nonNull(quoteModel.getUnit())
            && quoteModel.getUnit() instanceof IbmPartnerEndCustomerB2BUnitModel endCustomerB2BUnitModel) {
            cpqOutboundQuote.setBretIndicator(endCustomerB2BUnitModel.getGoe() && quoteModel.isSpecialBidQuote());
        }
        cpqOutboundQuote.setOrigin(
            quoteModel.getSalesApplication() != null && StringUtils.isNotBlank(
                quoteModel.getSalesApplication().getCode())
                ? getEnumerationService().getEnumerationName(quoteModel.getSalesApplication())
                : getDefaultSalesApplication());

        cpqOutboundQuote.setMarketCode(getDefaultSalesOrganization());
        cpqOutboundQuote.setFulfillmentSource(getDefaultFulfillmentSource());

        final String expirationDate = DateFormatUtils.format(quoteModel.getQuoteExpirationDate(),
            PartnercoreConstants.DEFAULT_QUOTE_DATE_PATTERN);
        cpqOutboundQuote.setExpirationDate(expirationDate);

        cpqOutboundQuote.setOpportunityId(
            quoteModel.getOpportunity() != null && StringUtils.isNotBlank(
                quoteModel.getOpportunity().getCode()) ? quoteModel.getOpportunity().getCode()
                : getDefaultOpportunityId());

        cpqOutboundQuote.setProgramType(
            quoteModel.getAgreementDetail() != null && StringUtils.isNotBlank(
                quoteModel.getAgreementDetail().getProgramType()) ? quoteModel.getAgreementDetail()
                .getProgramType() : StringUtils.EMPTY);

        cpqOutboundQuote.setJustification(
            StringUtils.defaultIfBlank(
                quoteModel.getSpecialBidBusinessJustification(), getDefaultJustificationCode()));

        cpqOutboundQuote.setTransactionPriceLevelCode(StringUtils.defaultIfBlank(
            quoteModel.getTransactionPriceLevel(), getDefaultTransactionPriceLevel()));

    }

    public String getDefaultDestinationChannel() {
        return defaultDestinationChannel;
    }

    public String getDefaultFulfillmentSource() {
        return defaultFulfillmentSource;
    }

    public String getDefaultJustificationCode() {
        return defaultJustificationCode;
    }

    public String getDefaultOpportunityId() {
        return defaultOpportunityId;
    }

    public String getDefaultSalesOrganization() {
        return defaultSalesOrganization;
    }

    public String getDefaultTransactionPriceLevel() {
        return defaultTransactionPriceLevel;
    }

    public String getDefaultSalesApplication() {
        return defaultSalesApplication;
    }

    public EnumerationService getEnumerationService() {
        return enumerationService;
    }

}
