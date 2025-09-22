package com.ibm.commerce.partner.core.outbound.service.impl;

import com.ibm.commerce.common.core.product.service.IbmProductService;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.enums.CpqPricingTypeEnum;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.PartnerCpqPricingDetailModel;
import com.ibm.commerce.partner.core.outbound.service.PartnerSapCpiQuoteMapperService;
import com.ibm.commerce.partner.core.outboundservices.quote.data.request.PartnerCpqPriceDetailRequestData;
import com.ibm.commerce.partner.core.outboundservices.quote.data.request.PartnerCpqPricesRequestData;
import com.ibm.commerce.partner.core.outboundservices.quote.data.request.PartnerCpqQuoteRequestData;
import com.ibm.commerce.partner.core.outboundservices.quote.data.request.PartnerCpqTotalPriceDetailRequestData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.sap.productconfig.services.model.CPQOrderEntryProductInfoModel;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;

/**
 * Default Partner SapCpiQuotePrice MapperService class is used to map the populate or map the quote
 * model field values to Quote SAPCPQOutboundQuotes data object
 */
public class DefaultPartnerSapCpiQuotePricesMapperService implements
    PartnerSapCpiQuoteMapperService<QuoteModel, PartnerCpqQuoteRequestData> {

    protected static final Logger LOG = Logger.getLogger(
        DefaultPartnerSapCpiQuotePricesMapperService.class);

    private static final String DRMSG = "DrMs";
    private final IbmProductService productService;

    public DefaultPartnerSapCpiQuotePricesMapperService(final IbmProductService productService) {
        this.productService = productService;
    }

    @Override
    public PartnerCpqQuoteRequestData map(final QuoteModel quoteModel,
        final PartnerCpqQuoteRequestData target) {
        return mapQuoteToCPQOutboundQuote((IbmPartnerQuoteModel) quoteModel, target);
    }


    protected PartnerCpqQuoteRequestData mapQuoteToCPQOutboundQuote(
        final IbmPartnerQuoteModel source, final PartnerCpqQuoteRequestData target) {
        target.setPrices(mapPriceDetails(source));
        return target;
    }


    protected PartnerCpqPricesRequestData mapPriceDetails(final IbmPartnerQuoteModel source) {
        final PartnerCpqPricesRequestData sapCPQOutboundPrices = new PartnerCpqPricesRequestData();
        if (CollectionUtils.isNotEmpty(source.getEntries())) {
            sapCPQOutboundPrices.setPrice(
                source.getEntries().stream().map(pidEntry -> getPidPricingDetails(pidEntry, source))
                    .filter(CollectionUtils::isNotEmpty).flatMap(Collection::stream).toList());
        }
        sapCPQOutboundPrices.setTotalDetail(mapTotalDetails(source));
        return sapCPQOutboundPrices;
    }

    protected List<PartnerCpqPriceDetailRequestData> getPidPricingDetails(
        final AbstractOrderEntryModel pidEntry, final IbmPartnerQuoteModel mainQuote) {
        if (pidEntry == null || CollectionUtils.isEmpty(pidEntry.getChildEntries())) {
            return Collections.emptyList();
        }
        return pidEntry.getChildEntries().stream()
            .map(partEntry -> createPrice(partEntry, mainQuote)).filter(Objects::nonNull).toList();
    }


    protected PartnerCpqPriceDetailRequestData createPrice(AbstractOrderEntryModel partEntry,
        final IbmPartnerQuoteModel mainQuote) {
        if (partEntry == null || CollectionUtils.isEmpty(partEntry.getCpqPricingDetails())) {
            return null;
        }
        final Optional<PartnerCpqPricingDetailModel> optionalFullPrice = partEntry.getCpqPricingDetails()
            .stream().filter(PartnerCpqPricingDetailModel.class::isInstance)
            .map(PartnerCpqPricingDetailModel.class::cast).filter(
                pricingDetailModel -> CpqPricingTypeEnum.FULL.getCode()
                    .equals(pricingDetailModel.getPricingType())).findAny();
        if (optionalFullPrice.isEmpty()) {
            return null;
        }
        final PartnerCpqPricingDetailModel pricingDetailModel = optionalFullPrice.get();
        final PartnerCpqPriceDetailRequestData itemPriceDetails = new PartnerCpqPriceDetailRequestData();
        itemPriceDetails.setUnitPrice(pricingDetailModel.getNetPrice());
        itemPriceDetails.setExtendedPrice(pricingDetailModel.getExtendedListPrice());
        itemPriceDetails.setOptimalPrice(pricingDetailModel.getListPrice());
        itemPriceDetails.setDiscount(
            StringUtils.defaultIfBlank(pricingDetailModel.getDiscountPercent(),
                NumberUtils.INTEGER_ZERO.toString()));
        itemPriceDetails.setBidUnitPrice(pricingDetailModel.getRolledUpNetPrice());
        itemPriceDetails.setCommittedTermPriceChangeUplift(NumberUtils.INTEGER_ZERO.toString());
        itemPriceDetails.setCommittedTermPriceChangeFrequency(NumberUtils.INTEGER_ZERO.toString());
        itemPriceDetails.setRenewalTermPriceChangeUplift(NumberUtils.INTEGER_ZERO.toString());
        itemPriceDetails.setRenewalTermPriceChangeFrequency(NumberUtils.INTEGER_ZERO.toString());

        itemPriceDetails.setBidTotalCommitPrice(pricingDetailModel.getRolledUpBidExtendedPrice());
        itemPriceDetails.setBpUnitPrice(pricingDetailModel.getRolledUpListPrice());
        itemPriceDetails.setBpTotalCommitPrice(pricingDetailModel.getRolledUpExtendedListPrice());
        itemPriceDetails.setChannelMargin(mainQuote.getTotalChannelMargin().toString());
        itemPriceDetails.setOptimalPriceDiscount(
            String.valueOf(getOptimalPriceDiscount(mainQuote)));
        itemPriceDetails.setExtendedUnitPrice(pricingDetailModel.getExtendedUnitPrice().toString());
        itemPriceDetails.setEccRequest(pricingDetailModel.getEccRequest());
        itemPriceDetails.setEccOverrided(
            StringUtils.defaultIfBlank(pricingDetailModel.getEccOverrideFields(),
                StringUtils.EMPTY));
        final AbstractOrderEntryModel pidEntry = partEntry.getMasterEntry();
        itemPriceDetails.setConfigurationId(getConfigId(pidEntry));
        itemPriceDetails.setPartNumber(getProductService().getProductCode(partEntry.getProduct()));
        itemPriceDetails.setDrEligible(
            getProductInfo(pidEntry, PartnercoreConstants.ORDER_ENTRY_DEAL_REG_KEY_DEAL_REG_FLAG));
        itemPriceDetails.setDrMsg(DRMSG);
        itemPriceDetails.setDrGrpId(
            getProductInfo(pidEntry, PartnercoreConstants.ORDER_ENTRY_DEAL_REG_KEY_DEAL_REG_ID));
        itemPriceDetails.setEndDate(getProductInfo(pidEntry, PartnercoreConstants.ENDDATE));
        itemPriceDetails.setDealRegGrp(
            getProductInfo(pidEntry, PartnercoreConstants.ORDER_ENTRY_DEAL_REG_KEY_DEAL_REG_GROUP));
        itemPriceDetails.setYtyPercentage(
            pricingDetailModel.getYtyPercentage() != null ? (pricingDetailModel.getYtyPercentage()
                .toString())
                : StringUtils.EMPTY);
        itemPriceDetails.setYtyPercentageDefault(
            pricingDetailModel.getYtyPercentageDefault() != null
                ? (pricingDetailModel.getYtyPercentageDefault()
                .toString())
                : StringUtils.EMPTY);
        return itemPriceDetails;
    }

    private double getOptimalPriceDiscount(IbmPartnerQuoteModel mainQuote) {
        return 100 * ((mainQuote.getTotalFullPrice() - mainQuote.getTotalOptimalPrice())
            / mainQuote.getTotalFullPrice());
    }

    private double getOptimalPrice(IbmPartnerQuoteModel mainQuote) {
        return mainQuote.getTotalFullPrice() - (
            (getOptimalPriceDiscount(mainQuote) * mainQuote.getTotalFullPrice()) / 100);
    }

    protected String getConfigId(AbstractOrderEntryModel entry) {
        if (entry != null && entry.getProductConfiguration() != null) {
            return StringUtils.defaultIfBlank(entry.getProductConfiguration().getConfigurationId(),
                StringUtils.EMPTY);
        }
        return StringUtils.EMPTY;
    }

    protected PartnerCpqTotalPriceDetailRequestData mapTotalDetails(
        final IbmPartnerQuoteModel source) {
        final PartnerCpqTotalPriceDetailRequestData totalDetails = new PartnerCpqTotalPriceDetailRequestData();
        totalDetails.setNetPrice(
            source.getTotalPrice() != null ? (source.getTotalPrice().toString())
                : StringUtils.EMPTY);
        totalDetails.setBidTotalCommitPrice(
            source.getTotalBidExtendedPrice() != null ? (source.getTotalBidExtendedPrice()
                .toString()) : StringUtils.EMPTY);
        totalDetails.setBpTotalCommitPrice(
            source.getTotalBpExtendedPrice() != null ? (source.getTotalBpExtendedPrice().toString())
                : StringUtils.EMPTY);
        totalDetails.setChannelMargin(
            source.getTotalChannelMargin() != null ? (source.getTotalChannelMargin().toString())
                : StringUtils.EMPTY);
        totalDetails.setOptimalPrice(
            source.getTotalOptimalPrice() != null ? (String.valueOf(getOptimalPrice(source)))
                : NumberUtils.DOUBLE_ZERO.toString());
        totalDetails.setYtyPercentage(
            source.getYtyPercentage() != null ? (source.getYtyPercentage().toString())
                : StringUtils.EMPTY);
        totalDetails.setDiscount(
            source.getTotalDiscounts() != null ? (source.getTotalDiscounts().toString())
                : StringUtils.EMPTY);
        totalDetails.setOptimalPriceDiscount(
            source.getTotalDiscounts() != null ? (source.getTotalDiscounts().toString())
                : StringUtils.EMPTY);
        totalDetails.setTotalMepPrice(
            source.getTotalMEPPrice() != null ? (source.getTotalMEPPrice().toString())
                : NumberUtils.DOUBLE_ZERO.toString());
        return totalDetails;
    }

    protected String getProductInfo(final AbstractOrderEntryModel source, final String type) {

        if (CollectionUtils.isEmpty(source.getProductInfos()) || StringUtils.isBlank(type)) {
            return StringUtils.EMPTY;
        }
        final Optional<CPQOrderEntryProductInfoModel> typeInfo = source.getProductInfos().stream()
            .filter(CPQOrderEntryProductInfoModel.class::isInstance)
            .map(CPQOrderEntryProductInfoModel.class::cast)
            .filter(info -> type.equalsIgnoreCase(info.getCpqCharacteristicName())).findAny();
        if (typeInfo.isPresent()) {
            final CPQOrderEntryProductInfoModel infoModel = typeInfo.get();
            return infoModel.getCpqCharacteristicAssignedValues();
        }

        return StringUtils.EMPTY;
    }


    public IbmProductService getProductService() {
        return productService;
    }

}
