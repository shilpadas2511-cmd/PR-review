package com.ibm.commerce.partner.core.outbound.service.impl;

import com.ibm.commerce.partner.core.enums.CpqPricingTypeEnum;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.PartnerCPQQuotePriceModel;
import com.ibm.commerce.partner.core.model.PartnerCpqHeaderPricingDetailModel;
import com.ibm.commerce.partner.core.model.PartnerCpqPricingDetailModel;
import com.ibm.commerce.partner.core.outbound.service.PartnerCpqCpiQuoteMapperService;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteModel;
import de.hybris.platform.core.model.order.QuoteModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * Default Partner PartnerCpqCpiQuotePrice MapperService class is used to map the populate or map
 * the quote model field values to SAPCPQOutboundQuote data object
 */
public class DefaultPartnerCpqCpiQuotePriceMapperService implements
    PartnerCpqCpiQuoteMapperService<QuoteModel, SAPCPQOutboundQuoteModel> {

    @Override
    public void map(QuoteModel quoteModel, SAPCPQOutboundQuoteModel sapcpqOutboundQuoteModel) {
        sapcpqOutboundQuoteModel.setQuotePrice(
            mapQuotePricesToCPQOutboundQuote((IbmPartnerQuoteModel) quoteModel));
    }

    protected PartnerCPQQuotePriceModel mapQuotePricesToCPQOutboundQuote(
        IbmPartnerQuoteModel source) {

        PartnerCPQQuotePriceModel totalDetails = new PartnerCPQQuotePriceModel();
        totalDetails.setQuote(source);
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
            source.getTotalOptimalPrice() != null ? (String.valueOf(source.getTotalOptimalPrice()))
                : NumberUtils.DOUBLE_ZERO.toString());
        totalDetails.setYtyPercentage(
            source.getYtyPercentage() != null ? (source.getYtyPercentage().toString())
                : StringUtils.EMPTY);
        totalDetails.setDiscount(
            source.getTotalDiscounts() != null ? (source.getTotalDiscounts().toString())
                : StringUtils.EMPTY);
        totalDetails.setNetPrice(
            source.getTotalFullPrice() != null ? (source.getTotalFullPrice().toString())
                : StringUtils.EMPTY);
        totalDetails.setOptimalPriceDiscount(String.valueOf(getOptimalPriceDiscount(source)));
        totalDetails.setTotalMepPrice(
            source.getTotalMEPPrice() != null ? (source.getTotalMEPPrice().toString())
                : NumberUtils.DOUBLE_ZERO.toString());
        totalDetails.setUsdTotalExtendedPrice(
            cpqHeaderPricingDetails(source).getTotalUSDExtendedPrice() != null
                ? (cpqHeaderPricingDetails(source).getTotalUSDExtendedPrice().toString())
                : StringUtils.EMPTY);
        totalDetails.setUsdTotalBidCommitPrice(
            cpqHeaderPricingDetails(source).getTotalUSDBidExtendedPrice() != null
                ? (cpqHeaderPricingDetails(source).getTotalUSDBidExtendedPrice().toString())
                : StringUtils.EMPTY);
        
        return totalDetails;
    }

    protected double getOptimalPrice(IbmPartnerQuoteModel mainQuote) {
        return mainQuote.getTotalFullPrice() - (
            (getOptimalPriceDiscount(mainQuote) * mainQuote.getTotalFullPrice()) / 100);
    }

    protected PartnerCpqHeaderPricingDetailModel cpqHeaderPricingDetails(
        IbmPartnerQuoteModel mainQuote) {
        return mainQuote.getPricingDetailsQuote().stream()
            .map(PartnerCpqHeaderPricingDetailModel.class::cast).filter(
                pricingDetailModel -> CpqPricingTypeEnum.FULL.getCode()
                    .equals(pricingDetailModel.getPricingType())).findAny().get();
    }

    protected double getOptimalPriceDiscount(IbmPartnerQuoteModel mainQuote) {
        return 100 * ((mainQuote.getTotalFullPrice() - mainQuote.getTotalOptimalPrice())
            / mainQuote.getTotalFullPrice());
    }
}
