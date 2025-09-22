package com.ibm.commerce.partner.core.outbound.service.impl;

import com.ibm.commerce.common.core.model.IbmVariantProductModel;
import com.ibm.commerce.common.core.product.service.IbmProductService;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.outbound.service.PartnerSapCpiQuoteMapperService;
import com.ibm.commerce.partner.core.outboundservices.quote.data.request.*;
import com.ibm.commerce.partner.core.pricing.services.PartnerPricingOutboundService;
import com.ibm.commerce.partner.core.quote.services.PartnerQuoteService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.sap.productconfig.services.model.CPQOrderEntryProductInfoModel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Default Partner SapCpiQuoteProduct MapperService class is used to map the populate or map the
 * quote model field values to Quote SAPCPQOutboundQuotes data object
 */
public class DefaultPartnerSapCpiQuoteProductMapperService implements
    PartnerSapCpiQuoteMapperService<QuoteModel, PartnerCpqQuoteRequestData> {

    protected static final Logger LOG = Logger.getLogger(
        DefaultPartnerSapCpiQuoteProductMapperService.class);

    private final String defaultTransactionType;

    private final IbmProductService productService;
    private final PartnerQuoteService partnerQuoteService;
    private final PartnerPricingOutboundService partnerPricingOutboundService;

    public DefaultPartnerSapCpiQuoteProductMapperService(
        final String defaultTransactionType,
        final IbmProductService productService, PartnerQuoteService partnerQuoteService,
        PartnerPricingOutboundService partnerPricingOutboundService) {
        this.defaultTransactionType = defaultTransactionType;
        this.productService = productService;
        this.partnerQuoteService = partnerQuoteService;
        this.partnerPricingOutboundService = partnerPricingOutboundService;
    }

    @Override
    public PartnerCpqQuoteRequestData map(final QuoteModel quoteModel,
        final PartnerCpqQuoteRequestData target) {
        return mapQuoteToCPQOutboundQuote((IbmPartnerQuoteModel) quoteModel, target);
    }

    protected PartnerCpqQuoteRequestData mapQuoteToCPQOutboundQuote(
        final IbmPartnerQuoteModel source, final PartnerCpqQuoteRequestData target) {
        target.setProducts(mapProducts(source));
        return target;
    }

    protected List<PartnerCpqProductRequestData> mapProducts(final IbmPartnerQuoteModel source) {
        if (CollectionUtils.isNotEmpty(source.getEntries())) {
            return source.getEntries().stream()
                .filter(pidEntry -> CollectionUtils.isNotEmpty(pidEntry.getChildEntries()))
                .flatMap(pidEntry -> pidEntry.getChildEntries().stream())
                .map(this::mapPartProductToOutboundProduct).toList();
        }
        return Collections.emptyList();
    }

    protected PartnerCpqProductRequestData mapPartProductToOutboundProduct(
        AbstractOrderEntryModel partEntry) {
        final PartnerCpqProductRequestData outboundProduct = new PartnerCpqProductRequestData();
        AbstractOrderEntryModel pidEntry = partEntry.getMasterEntry();
        if (Objects.nonNull(partEntry.getProduct())) {
            outboundProduct.setPartNumber(
                getProductService().getProductCode(partEntry.getProduct()));
            if (pidEntry.getProduct() instanceof IbmVariantProductModel pidProduct) {
                outboundProduct.setConfiguratorId(pidProduct.getConfiguratorCode());
            }
        }
        outboundProduct.setStartDate(getPartnerQuoteService().getProductInfoFormatted(partEntry,
            PartnercoreConstants.STARTDATE));
        outboundProduct.setEndDate(getPartnerQuoteService().getProductInfoFormatted(partEntry,
            PartnercoreConstants.ENDDATE));
        outboundProduct.setStartDateChanged(
            getPartnerPricingOutboundService().getProductInfo(partEntry,
                PartnercoreConstants.STARTDATECHANGED));
        outboundProduct.setEndDateChanged(
            getPartnerPricingOutboundService().getProductInfo(partEntry,
                PartnercoreConstants.ENDDATECHANGED));
        outboundProduct.setBackDatedStartDate(
            getPartnerPricingOutboundService().getProductInfo(partEntry,
                PartnercoreConstants.BACKDATEDSTARTDATE));
        outboundProduct.setQuantity(partEntry.getQuantity().toString());
        outboundProduct.setRolledUpItemNumber("1"); // need to set dynamic value
        outboundProduct.setTransactionType(getDefaultTransactionType());
        outboundProduct.setConfigurationId(getConfigId(pidEntry));
        return outboundProduct;
    }

    protected String getConfigId(AbstractOrderEntryModel entry) {
        if (entry != null && entry.getProductConfiguration() != null) {
            return StringUtils.defaultIfBlank(entry.getProductConfiguration().getConfigurationId(),
                StringUtils.EMPTY);
        }
        return StringUtils.EMPTY;
    }

    public IbmProductService getProductService() {
        return productService;
    }

    public String getDefaultTransactionType() {
        return defaultTransactionType;
    }

    public PartnerQuoteService getPartnerQuoteService() {
        return partnerQuoteService;
    }

    public PartnerPricingOutboundService getPartnerPricingOutboundService() {
        return partnerPricingOutboundService;
    }
}
