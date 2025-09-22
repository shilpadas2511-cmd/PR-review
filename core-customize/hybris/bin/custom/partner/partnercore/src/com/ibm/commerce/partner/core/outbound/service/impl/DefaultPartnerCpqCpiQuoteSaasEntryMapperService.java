package com.ibm.commerce.partner.core.outbound.service.impl;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.IbmPartnerPidQuoteEntryModel;
import com.ibm.commerce.partner.core.model.PartnerCommerceRampUpModel;
import com.ibm.commerce.partner.core.model.PartnerRampUpSummaryModel;
import com.ibm.commerce.partner.core.model.SAPCPQCommerceRampUpModel;
import com.ibm.commerce.partner.core.model.SAPCPQRampUpSummaryModel;
import com.ibm.commerce.partner.core.outbound.service.PartnerCpqCpiQuoteEntryMapperService;
import com.ibm.commerce.partner.core.pricing.services.PartnerPricingOutboundService;
import com.ibm.commerce.partner.core.quote.services.PartnerQuoteService;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteItemModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

/**
 * Default Partner PartnerCpqCpiQuoteSaasEntryPrice MapperService class is used to map the populate
 * or map the quote model field values to SAPCPQOutboundQuoteItem data object
 */
public class DefaultPartnerCpqCpiQuoteSaasEntryMapperService implements
    PartnerCpqCpiQuoteEntryMapperService<AbstractOrderEntryModel, SAPCPQOutboundQuoteItemModel> {

    private final PartnerQuoteService partnerQuoteService;

    private final PartnerPricingOutboundService partnerPricingOutboundService;

    public DefaultPartnerCpqCpiQuoteSaasEntryMapperService(
        final PartnerQuoteService partnerQuoteService,
        final PartnerPricingOutboundService partnerPricingOutboundService) {
        this.partnerQuoteService = partnerQuoteService;
        this.partnerPricingOutboundService = partnerPricingOutboundService;
    }

    @Override
    public void map(AbstractOrderEntryModel quoteEntryModel,
        SAPCPQOutboundQuoteItemModel sapcpqOutboundQuoteItemModel, QuoteModel quoteModel) {
        sapcpqOutboundQuoteItemModel.setRequestedStartDate(
            getPartnerQuoteService().getProductInfoFormatted(quoteEntryModel,
                PartnercoreConstants.REQUESTED_START_DATE));
        sapcpqOutboundQuoteItemModel.setBillingFrequency(
            getPartnerPricingOutboundService().getProductInfo(quoteEntryModel,
                PartnercoreConstants.BILLING_FREQUENCY));
        sapcpqOutboundQuoteItemModel.setRenewalType(
            getPartnerPricingOutboundService().getProductInfo(quoteEntryModel,
                PartnercoreConstants.RENEWAL_TYPE));
        sapcpqOutboundQuoteItemModel.setContractTerm(
            getPartnerPricingOutboundService().getProductInfo(quoteEntryModel,
                PartnercoreConstants.CONTRACT_TERM));
        Optional.ofNullable(getPartnerPricingOutboundService()
                .getProductInfo(quoteEntryModel, PartnercoreConstants.TOTAL_TERM))
            .map(term -> PartnercoreConstants.NOT_APPLICABLE.equalsIgnoreCase(term) ? "" : term)
            .ifPresent(sapcpqOutboundQuoteItemModel::setTotalTerm);
        sapcpqOutboundQuoteItemModel.setRequestedStartDateRequired(
            getRequestedStartDateRequired(quoteEntryModel));
        if ((quoteEntryModel instanceof IbmPartnerPidQuoteEntryModel partEntry)
            && partEntry.getCommerceRampUp() != null) {
            sapcpqOutboundQuoteItemModel.setCommerceRampUp(
                prepareCommerceRampUp(partEntry));
        }
    }

    /**
     * These method will return String value of either Yes or No
     *
     * @param quoteEntryModel
     * @return String
     */
    protected String getRequestedStartDateRequired(AbstractOrderEntryModel quoteEntryModel) {
        String value = getPartnerPricingOutboundService().getProductInfo(quoteEntryModel,
            PartnercoreConstants.REQUESTED_START_DATE_REQUIRED);

        return StringUtils.equalsIgnoreCase(PartnercoreConstants.YES, value)
            ? value
            : PartnercoreConstants.NO;
    }

    /**
     * These method will return SAPCPQCommerceRampUPModel
     *
     * @param partEntry
     * @return SAPCPQCommerceRampUPModel
     */
    protected SAPCPQCommerceRampUpModel prepareCommerceRampUp(
        IbmPartnerPidQuoteEntryModel partEntry) {
        SAPCPQCommerceRampUpModel rampUPModel = new SAPCPQCommerceRampUpModel();
        rampUPModel.setId(partEntry.getCommerceRampUp().getCode());
        rampUPModel.setRampUpPeriod(
            partEntry.getCommerceRampUp().getRampUpPeriod());
        rampUPModel.setRampUpSummary(
            rampUpSummaryCollection(partEntry.getCommerceRampUp()));
        return rampUPModel;
    }

    /**
     * These method will return list of SAPCPQRampUpSummaryModel
     *
     * @param rampUpModel
     * @return List<SAPCPQRampUpSummaryModel>
     */
    protected List<SAPCPQRampUpSummaryModel> rampUpSummaryCollection(
        PartnerCommerceRampUpModel rampUpModel) {
        return rampUpModel.getPartnerRampUpSummary().stream()
            .map(partnerRampUpSummaryModel -> prepareRampUpSummary(
                partnerRampUpSummaryModel)).collect(
                Collectors.toList());
    }

    /**
     * These method will return SAPCPQRampUpSummaryModel
     *
     * @param partnerRampUpSummary
     * @return SAPCPQRampUpSummaryModel
     */
    protected SAPCPQRampUpSummaryModel prepareRampUpSummary(
        PartnerRampUpSummaryModel partnerRampUpSummary) {
        SAPCPQRampUpSummaryModel sapCpqRampUpSummary = new SAPCPQRampUpSummaryModel();
        sapCpqRampUpSummary.setId(partnerRampUpSummary.getCode());
        sapCpqRampUpSummary.setRampUpQuantity(partnerRampUpSummary.getRampUpQuantity());
        sapCpqRampUpSummary.setRampUpPeriodDuration(partnerRampUpSummary.getRampUpPeriodDuration());
        return sapCpqRampUpSummary;
    }

    public PartnerQuoteService getPartnerQuoteService() {
        return partnerQuoteService;
    }

    public PartnerPricingOutboundService getPartnerPricingOutboundService() {
        return partnerPricingOutboundService;
    }

}
