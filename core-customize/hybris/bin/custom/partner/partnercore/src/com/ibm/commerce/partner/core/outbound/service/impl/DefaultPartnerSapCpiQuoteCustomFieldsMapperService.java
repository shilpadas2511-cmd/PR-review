package com.ibm.commerce.partner.core.outbound.service.impl;

import com.ibm.commerce.common.core.product.service.IbmProductService;
import com.ibm.commerce.partner.core.enums.PartnerQuoteChannelEnum;
import de.hybris.platform.enumeration.EnumerationService;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.outbound.service.PartnerSapCpiQuoteMapperService;
import com.ibm.commerce.partner.core.outboundservices.quote.data.request.*;
import de.hybris.platform.core.model.order.QuoteModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Default Partner SapCpiQuoteCustomFields MapperService class is used to map the populate or map the quote
 * model field values to Quote SAPCPQOutboundQuotes data object
 */
public class DefaultPartnerSapCpiQuoteCustomFieldsMapperService implements
        PartnerSapCpiQuoteMapperService<QuoteModel,PartnerCpqQuoteRequestData> {

    protected static final Logger LOG = Logger.getLogger(
            DefaultPartnerSapCpiQuoteCustomFieldsMapperService.class);

    private final String defaultDestinationChannel;
    private final String defaultSalesOrganization;
    private final String defaultFulfillmentSource;
    private final String defaultOpportunityId;
    private final String defaultJustificationCode;
    private final IbmProductService productService;
    private final String  defaultTransactionPriceLevel;
    private final EnumerationService enumerationService;
    private final String defaultSalesApplication;

    public DefaultPartnerSapCpiQuoteCustomFieldsMapperService(final String defaultDestinationChannel,
                                                              final String defaultSalesOrganization, final String defaultFulfillmentSource,
                                                              final String defaultOpportunityId,
                                                              final String defaultJustificationCode,
                                                              final IbmProductService productService, final String defaultTransactionPriceLevel,
                                                              final EnumerationService enumerationService, final String defaultSalesApplication ) {
        this.defaultDestinationChannel = defaultDestinationChannel;
        this.defaultSalesOrganization = defaultSalesOrganization;
        this.defaultFulfillmentSource = defaultFulfillmentSource;
        this.defaultOpportunityId = defaultOpportunityId;
        this.defaultJustificationCode = defaultJustificationCode;
        this.productService = productService;
        this.defaultTransactionPriceLevel = defaultTransactionPriceLevel;
        this.enumerationService = enumerationService;
        this.defaultSalesApplication = defaultSalesApplication;
    }

    @Override
    public PartnerCpqQuoteRequestData map(final QuoteModel quoteModel, final PartnerCpqQuoteRequestData target ) {
        return mapQuoteToCPQOutboundQuote((IbmPartnerQuoteModel)quoteModel,target);
    }

    protected PartnerCpqQuoteRequestData mapQuoteToCPQOutboundQuote(
            final IbmPartnerQuoteModel source, final PartnerCpqQuoteRequestData target) {
        target.setCustomFields(mapCPQOutboundQuoteCustomFields(source));
        return target;
    }

    protected List<PartnerCpqQuoteCustomFieldRequestData> mapCPQOutboundQuoteCustomFields(
            final IbmPartnerQuoteModel source) {
        final List<PartnerCpqQuoteCustomFieldRequestData> customFieldList = new ArrayList<>();
        final String expirationDate = DateFormatUtils.format(source.getQuoteExpirationDate(),
                PartnercoreConstants.DEFAULT_QUOTE_DATE_PATTERN);
        customFieldList.add(
                createOutboundQuoteCustomField(PartnercoreConstants.EXPIRATIONDATE, expirationDate));
        customFieldList.add(
                createOutboundQuoteCustomField(PartnercoreConstants.FULFILLMENTSOURCEKEY,
                        getDefaultFulfillmentSource()));
        String salesApplication = source.getSalesApplication() != null && StringUtils.isNotBlank(
            source.getSalesApplication().getCode()) ?   enumerationService.getEnumerationName(source.getSalesApplication())
            : getDefaultSalesApplication();
        customFieldList.add(createOutboundQuoteCustomField(PartnercoreConstants.SOURCE,
            salesApplication));
        String opportunityId = source.getOpportunity() != null && StringUtils.isNotBlank(
                source.getOpportunity().getCode()) ? source.getOpportunity().getCode()
                : getDefaultOpportunityId();
        customFieldList.add(
                createOutboundQuoteCustomField(PartnercoreConstants.OPPORTUNITYID, opportunityId));
        customFieldList.add(
                createOutboundQuoteCustomField(PartnercoreConstants.DISTRIBUTIONCHANNELKEY,
                    StringUtils.defaultIfBlank(source.getCpqDistributionChannel(),
                    PartnerQuoteChannelEnum.J.getCode())));
        customFieldList.add(
                createOutboundQuoteCustomField(PartnercoreConstants.QUOTETITLE, source.getName()));
        String programType = source.getAgreementDetail() != null && StringUtils.isNotBlank(
                source.getAgreementDetail().getProgramType()) ? source.getAgreementDetail()
                .getProgramType() : StringUtils.EMPTY;
        customFieldList.add(
                createOutboundQuoteCustomField(PartnercoreConstants.PROGRAMTYPE, programType));
        customFieldList.add(createOutboundQuoteCustomField(PartnercoreConstants.SALESORGKEY,
                getDefaultSalesOrganization()));
        customFieldList.add(createOutboundQuoteCustomField(PartnercoreConstants.QUOTE_CREATOR,
                source.getUser().getUid()));
        customFieldList.add(createOutboundQuoteCustomField(PartnercoreConstants.QUOTE_CREATOR_NAME,
                source.getUser().getName()));
        customFieldList.add(createOutboundQuoteCustomField(PartnercoreConstants.QUOTE_OWNER_EMAIL,
            source.getUser().getUid()));
        String specialBidJustification =  source.getSpecialBidReason()!= null && StringUtils.isNotBlank(
            source.getSpecialBidBusinessJustification()) ? source.getSpecialBidBusinessJustification()
            : getDefaultJustificationCode();
        customFieldList.add(createOutboundQuoteCustomField(PartnercoreConstants.JUSTIFICATIONKEY,
            specialBidJustification));
        String transactionPriceLevel =  StringUtils.isNotBlank(
                source.getTransactionPriceLevel()) ? source.getTransactionPriceLevel()
                : getDefaultTransactionPriceLevel();
        customFieldList.add(createOutboundQuoteCustomField(PartnercoreConstants.TRANSACTION_PRICE_LEVEL,
                transactionPriceLevel));

        return customFieldList;
    }

    protected PartnerCpqQuoteCustomFieldRequestData createOutboundQuoteCustomField(
            final String name, final String content) {
        final PartnerCpqQuoteCustomFieldRequestData customField = new PartnerCpqQuoteCustomFieldRequestData();
        customField.setContent(content);
        customField.setName(name);
        return customField;
    }

    public String getDefaultDestinationChannel() {
        return defaultDestinationChannel;
    }

    public String getDefaultSalesOrganization() {
        return defaultSalesOrganization;
    }

    public String getDefaultFulfillmentSource() {
        return defaultFulfillmentSource;
    }

    public String getDefaultOpportunityId() {
        return defaultOpportunityId;
    }

    public String getDefaultJustificationCode() {
        return defaultJustificationCode;
    }

    public IbmProductService getProductService() {
        return productService;
    }

    public String getDefaultTransactionPriceLevel() {
        return defaultTransactionPriceLevel;
    }

    public String getDefaultSalesApplication() {
        return defaultSalesApplication;
    }

    }
