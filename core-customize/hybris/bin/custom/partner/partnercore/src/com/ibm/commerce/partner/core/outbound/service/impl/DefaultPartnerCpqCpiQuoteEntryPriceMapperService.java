package com.ibm.commerce.partner.core.outbound.service.impl;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.enums.CpqPricingTypeEnum;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteEntryModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.PartnerCpqPriceModel;
import com.ibm.commerce.partner.core.model.PartnerCpqPricingDetailModel;
import com.ibm.commerce.partner.core.outbound.service.PartnerCpqCpiQuoteEntryMapperService;
import com.ibm.commerce.partner.core.pricing.services.PartnerPricingOutboundService;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteItemModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.sap.productconfig.services.model.CPQOrderEntryProductInfoModel;
import java.util.Optional;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;


/**
 * Default Partner PartnerCpqCpiQuoteEntryPrice MapperService class is used to map the populate or
 * map the quote model field values to SAPCPQOutboundQuoteItem data object
 */
public class DefaultPartnerCpqCpiQuoteEntryPriceMapperService implements
    PartnerCpqCpiQuoteEntryMapperService<AbstractOrderEntryModel, SAPCPQOutboundQuoteItemModel> {

    private PartnerPricingOutboundService partnerPricingOutboundService;

    private static final String DRMSG = "DrMs";

    public DefaultPartnerCpqCpiQuoteEntryPriceMapperService(
        PartnerPricingOutboundService partnerPricingOutboundService) {
        this.partnerPricingOutboundService = partnerPricingOutboundService;
    }

    @Override
    public void map(AbstractOrderEntryModel abstractOrderEntryModel,
        SAPCPQOutboundQuoteItemModel sapcpqOutboundQuoteItemModel, QuoteModel quoteModel) {

        sapcpqOutboundQuoteItemModel.setCpqPrice(
            createPricing(abstractOrderEntryModel, (IbmPartnerQuoteModel) quoteModel));
    }

    protected PartnerCpqPriceModel createPricing(AbstractOrderEntryModel partEntry,
        IbmPartnerQuoteModel mainQuote) {
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
        final PartnerCpqPriceModel itemPriceDetails = new PartnerCpqPriceModel();

        itemPriceDetails.setEntry(((IbmPartnerQuoteEntryModel) partEntry));
        itemPriceDetails.setPricingType(pricingDetailModel.getPricingType());
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
        itemPriceDetails.setChannelMargin(String.valueOf(mainQuote.getTotalChannelMargin()));
        itemPriceDetails.setOptimalPriceDiscount(
            pricingDetailModel.getOptimalDiscount() == null ? StringUtils.EMPTY
                : String.valueOf(pricingDetailModel.getOptimalDiscount()));
        itemPriceDetails.setExtendedUnitPrice(
            String.valueOf(pricingDetailModel.getExtendedUnitPrice()));
        itemPriceDetails.setEccRequest(pricingDetailModel.getEccRequest());
        itemPriceDetails.setEccOverrided(
            StringUtils.defaultIfBlank(pricingDetailModel.getEccOverrideFields(),
                StringUtils.EMPTY));
        final AbstractOrderEntryModel pidEntry = partEntry.getMasterEntry();

        itemPriceDetails.setDrEligible(getPartnerPricingOutboundService().getProductInfo(partEntry,
            PartnercoreConstants.ORDER_ENTRY_DEAL_REG_KEY_DEAL_REG_FLAG));
        itemPriceDetails.setDrMsg(DRMSG);
        itemPriceDetails.setDrGrpId(getPartnerPricingOutboundService().getProductInfo(partEntry,
            PartnercoreConstants.ORDER_ENTRY_DEAL_REG_KEY_DEAL_REG_ID));
        itemPriceDetails.setEndDate(StringUtils.EMPTY);
        itemPriceDetails.setDealRegGrp(getPartnerPricingOutboundService().getProductInfo(partEntry,
            PartnercoreConstants.ORDER_ENTRY_DEAL_REG_KEY_DEAL_REG_GROUP));
        itemPriceDetails.setYtyPercentage(
            pricingDetailModel.getYtyPercentage() != null ? String.valueOf(
                (pricingDetailModel.getYtyPercentage())) : StringUtils.EMPTY);
        itemPriceDetails.setYtyPercentageDefault(
            pricingDetailModel.getYtyPercentageDefault() != null ? String.valueOf(
                (pricingDetailModel.getYtyPercentageDefault())) : StringUtils.EMPTY);
        return itemPriceDetails;

    }

    protected double getOptimalPriceDiscount(IbmPartnerQuoteModel mainQuote) {
        return 100 * ((mainQuote.getTotalFullPrice() - mainQuote.getTotalOptimalPrice())
            / mainQuote.getTotalFullPrice());
    }


    protected String getConfigId(AbstractOrderEntryModel entry) {
        if (entry != null && entry.getProductConfiguration() != null) {
            return StringUtils.defaultIfBlank(entry.getProductConfiguration().getConfigurationId(),
                StringUtils.EMPTY);
        }
        return StringUtils.EMPTY;
    }

    public PartnerPricingOutboundService getPartnerPricingOutboundService() {
        return partnerPricingOutboundService;
    }

}
