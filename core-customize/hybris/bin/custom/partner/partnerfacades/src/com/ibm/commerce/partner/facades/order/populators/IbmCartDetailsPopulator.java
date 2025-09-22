package com.ibm.commerce.partner.facades.order.populators;

import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.sap.productconfig.services.model.CPQOrderEntryProductInfoModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import com.ibm.commerce.partner.company.data.IbmPartnerB2BUnitData;
import com.ibm.commerce.partner.company.endcustomer.data.IbmPartnerAgreementDetailData;
import com.ibm.commerce.partner.company.endcustomer.data.IbmPartnerEndCustomerB2BUnitData;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.enums.CpqPricingTypeEnum;
import com.ibm.commerce.partner.core.model.IbmB2BUnitModel;
import com.ibm.commerce.partner.core.model.IbmPartnerAgreementDetailModel;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.IbmPartnerEndCustomerB2BUnitModel;
import com.ibm.commerce.partner.core.model.IbmPartnerOpportunityModel;
import com.ibm.commerce.partner.core.model.PartnerCpqHeaderPricingDetailModel;
import com.ibm.commerce.partner.core.model.PartnerCpqPricingDetailModel;
import com.ibm.commerce.partner.core.model.PartnerProvisionFormModel;
import com.ibm.commerce.partner.core.model.PartnerQuestionsSelectionModel;
import com.ibm.commerce.partner.core.model.PartnerSpecialBidReasonModel;
import com.ibm.commerce.partner.core.user.service.PartnerUserService;
import com.ibm.commerce.partner.data.order.pricing.PartnerCpqHeaderPricingDetailData;
import com.ibm.commerce.partner.data.order.pricing.YtyYearData;
import com.ibm.commerce.partner.deal.data.IbmPartnerOpportunityData;
import com.ibm.commerce.partner.partnerquestions.data.PartnerQuestionsSelectionData;
import com.ibm.commerce.partner.provision.form.data.ProvisioningFormData;
import com.ibm.commerce.partner.specialbidreason.data.PartnerSpecialBidReasonData;


/**
 * To populate IBM cart updates {@link com.ibm.commerce.partner.core.model.IbmPartnerCartModel} as
 * source and {@link CartData} as target type.
 */
public class IbmCartDetailsPopulator implements Populator<IbmPartnerCartModel, CartData> {

    private final Converter<IbmPartnerAgreementDetailModel, IbmPartnerAgreementDetailData> agreementDataConverter;

    private final Converter<IbmPartnerEndCustomerB2BUnitModel, IbmPartnerEndCustomerB2BUnitData> endCustomerB2BUnitDataConverter;
    private final Converter<IbmB2BUnitModel, IbmPartnerB2BUnitData> b2bUnitDataConverter;
    private final Converter<IbmPartnerOpportunityModel, IbmPartnerOpportunityData> opportunityDataConverter;
    private final Converter<PartnerSpecialBidReasonModel, PartnerSpecialBidReasonData> partnerSpecialBidReasonConverter;
    private final Converter<PartnerCpqHeaderPricingDetailModel, PartnerCpqHeaderPricingDetailData> partnerHeaderPricingDetailConverter;
    private final Converter<PartnerQuestionsSelectionModel, PartnerQuestionsSelectionData> partnerQuestionsSelectionDataConverter;
    private final Converter<PartnerCpqHeaderPricingDetailModel, List<YtyYearData>> partnerYtyOverrideConverter;
    private final ConfigurationService configurationService;
    private final Converter<PartnerProvisionFormModel, ProvisioningFormData> provisionFormConverter;
    private final Converter<CurrencyModel, CurrencyData> currencyConverter;

    private final PartnerUserService userService;

    public IbmCartDetailsPopulator(
        final Converter<IbmPartnerAgreementDetailModel, IbmPartnerAgreementDetailData> agreementDataConverter,
        final Converter<IbmPartnerEndCustomerB2BUnitModel, IbmPartnerEndCustomerB2BUnitData> endCustomerB2BUnitDataConverter,
        final Converter<IbmB2BUnitModel, IbmPartnerB2BUnitData> b2bUnitDataConverter,
        final Converter<IbmPartnerOpportunityModel, IbmPartnerOpportunityData> opportunityDataConverter,
        final Converter<PartnerSpecialBidReasonModel, PartnerSpecialBidReasonData> partnerSpecialBidReasonConverter,
        final Converter<PartnerCpqHeaderPricingDetailModel, PartnerCpqHeaderPricingDetailData> partnerHeaderPricingDetailConverter,
        final Converter<PartnerQuestionsSelectionModel, PartnerQuestionsSelectionData> partnerQuestionsSelectionDataConverter,
        final Converter<PartnerCpqHeaderPricingDetailModel, List<YtyYearData>> partnerYtyOverrideConverter,
        final Converter<PartnerProvisionFormModel, ProvisioningFormData> provisionFormConverter,
        final ConfigurationService configurationService,
        final PartnerUserService userService,
        final Converter<CurrencyModel, CurrencyData> currencyConverter)

	 {
        this.agreementDataConverter = agreementDataConverter;
        this.endCustomerB2BUnitDataConverter = endCustomerB2BUnitDataConverter;
        this.b2bUnitDataConverter = b2bUnitDataConverter;
        this.opportunityDataConverter = opportunityDataConverter;
        this.partnerSpecialBidReasonConverter = partnerSpecialBidReasonConverter;
        this.partnerHeaderPricingDetailConverter = partnerHeaderPricingDetailConverter;
        this.partnerQuestionsSelectionDataConverter = partnerQuestionsSelectionDataConverter;
        this.partnerYtyOverrideConverter = partnerYtyOverrideConverter;
        this.provisionFormConverter = provisionFormConverter;
        this.configurationService = configurationService;
        this.userService = userService;
        this.currencyConverter = currencyConverter;
    }


    @Override
    public void populate(final IbmPartnerCartModel source, final CartData target)
        throws ConversionException {
        Assert.notNull(source, "Parameter source cannot be null.");
        Assert.notNull(target, "Parameter target cannot be null.");

        if (source.getOpportunity() != null) {
            target.setOpportunity(getOpportunityDataConverter().convert(source.getOpportunity()));
        }
        if (source.getAgreementDetail() != null) {
            target.setAgreementDetail(
                getAgreementDataConverter().convert(source.getAgreementDetail()));
        }
        populateUnits(source, target);

        if (CollectionUtils.isNotEmpty(source.getPricingDetails())) {
            target.setPartnerCpqHeaderPricingDetails(
                Converters.convertAll(source.getPricingDetails(),
                    getpartnerHeaderPricingDetailConverter()));

            final PartnerCpqHeaderPricingDetailModel partnerCpqHeaderPricingDetailModel = getPartnerCpqHeaderPricingDetail(
                source);
            if (Objects.nonNull(partnerCpqHeaderPricingDetailModel)) {
                target.setYtyYears(
                    getPartnerYtyOverrideConverter().convert(partnerCpqHeaderPricingDetailModel));
            }
        }

        target.setModifiedtime(source.getModifiedtime());
        target.setYtyPercentage(source.getYtyPercentage());
        target.setTotalMEPPrice(source.getTotalMEPPrice());
        target.setTotalFullPrice(source.getTotalFullPrice());
        target.setTotalBidExtendedPrice(source.getTotalBidExtendedPrice());
        target.setDiscount(source.getTotalDiscounts());
        target.setErrorMessage(source.getErrorMesaage());
        target.setQuoteExpirationDate(source.getQuoteExpirationDate());
        target.setFullPriceReceived(BooleanUtils.isTrue(source.getFullPriceReceived()));
        setEditable(source, target);
        target.setPriceStale(BooleanUtils.isTrue(source.getPriceStale()));
        populateSpecialBidDetails(source, target);
        if (CollectionUtils.isNotEmpty(source.getPartnerQuestionsSelections())) {
            target.setPartnerQuestionsSelections(
                Converters.convertAll(source.getPartnerQuestionsSelections(),
                    getPartnerQuestionsSelectionDataConverter()));
        }
        target.setVadView(userService.isVadView(source,userService.getCurrentUser()));
        if (source.getProvisionForms() != null) {
            if (StringUtils.isNotEmpty(source.getProvisionForms().getErrors())) {
               target.setProvisionFormError(source.getProvisionForms().getErrors());
            } else {
                Set<PartnerProvisionFormModel> forms = source.getProvisionForms()
                    .getPartnerProvisionForm();
                target.setProvisionForms(Converters.convertAll(forms, getProvisionFormConverter()));
            }
        }
        if (CollectionUtils.isNotEmpty(source.getCollaboratorEmails())) {
            target.setCollaboratorEmails(new ArrayList<>(source.getCollaboratorEmails()));
        }
        if (source.getCurrency() != null) {
            target.setCurrency(getCurrencyConverter().convert(source.getCurrency()));
        }
        if (StringUtils.isNotBlank(source.getCpqExternalQuoteId())) {
            target.setCpqQuoteID(source.getCpqExternalQuoteId());
        }
    }

    /*
     * // Get cpq headerpricingdetails to populate partnerCpqHeaderPricingDetail in CartWSDTO
     */
    protected PartnerCpqHeaderPricingDetailModel getPartnerCpqHeaderPricingDetail(
            final IbmPartnerCartModel cart) {

        final Optional<PartnerCpqHeaderPricingDetailModel> partnerCpqHeaderPricingDetailOptional =
                cart.getPricingDetails().stream()
                        .filter(Objects::nonNull)
                        .filter(pricing -> null != pricing.getPricingType()
                                && CpqPricingTypeEnum.FULL.getCode()
                                        .equals(pricing.getPricingType()))
                        .findAny();

        return partnerCpqHeaderPricingDetailOptional.orElse(null);
    }

    private void populateSpecialBidDetails(final IbmPartnerCartModel source,
            final CartData target) {
        final Boolean isMultipleSpecialBidDisabled = getConfigurationService().getConfiguration()
            .getBoolean(PartnercoreConstants.FLAG_SPECIAL_BID_REASONS_MULTI_SELECT_DISABLED,
                Boolean.TRUE);
        if (BooleanUtils.isTrue(isMultipleSpecialBidDisabled)) {
            if (source.getSpecialBidReason() != null) {
                target.setSpecialBidReason(
                    getPartnerSpecialBidReasonConverter().convert(source.getSpecialBidReason()));
            }
        } else {
            if (Objects.nonNull(source.getSpecialBidReasons())) {
                target.setSpecialBidReasons(getPartnerSpecialBidReasonConverter().convertAll(
                    source.getSpecialBidReasons()));
            }
        }
        target.setSpecialBidBusinessJustification(source.getSpecialBidBusinessJustification());
        target.setSpecialBid(BooleanUtils.isTrue(source.isSpecialBid()));
    }

    private void setEditable(final IbmPartnerCartModel source, final CartData target) {
        final Boolean isSpecialBidEnabled = getConfigurationService().getConfiguration()
            .getBoolean(PartnercoreConstants.FEATURE_FLAG_ENABLE_QUOTE_EDIT_SPECIAL_BID,
                Boolean.FALSE);
        if (BooleanUtils.isFalse(isSpecialBidEnabled)) {
            target.setEditable(Boolean.FALSE);
            return;
        }
        if (CollectionUtils.isNotEmpty(target.getEntries())) {
            final boolean allEditable = target.getEntries().stream().flatMap(cartEntry -> {
                // if childEntries are empty or null, then set editable attribute of target as false
                if (CollectionUtils.isEmpty(cartEntry.getEntries())) {
                    return Stream.of(false);
                }
                if ((source.getEntries().stream()
                    .map(AbstractOrderEntryModel.class::cast)
                    .flatMap(cartsEntry -> cartsEntry.getChildEntries().stream())
                    .filter(childEntry -> isHeaderEditableForObsolete(childEntry))
                    .anyMatch(childEntry -> isPriceAvailableInECC(childEntry)))) {
                    return Stream.of(false);
                }
                return cartEntry.getEntries().stream().map(childEntry -> childEntry.isEditable());
            }).allMatch(editable -> editable);

            target.setEditable(allEditable);
        }
    }

    private void populateUnits(final IbmPartnerCartModel source, final CartData target) {
        if (source.getUnit() != null && source
                .getUnit() instanceof final IbmPartnerEndCustomerB2BUnitModel endCustomerB2BUnitModel) {
            target.setShipToUnit(
                    getEndCustomerB2BUnitDataConverter().convert(endCustomerB2BUnitModel));
        }

        if (source.getSoldThroughUnit() != null
                && source.getSoldThroughUnit() instanceof final IbmB2BUnitModel partnerB2BUnit) {
            target.setSoldThroughUnit(getB2bUnitDataConverter().convert(partnerB2BUnit));
        }

        if (source.getBillToUnit() != null
                && source.getBillToUnit() instanceof final IbmB2BUnitModel partnerB2BUnit) {
            target.setBillToUnit(getB2bUnitDataConverter().convert(partnerB2BUnit));
        }
    }

    /**
     * validator to check if entry is editable or not
     */
    protected boolean isHeaderEditableForObsolete(final AbstractOrderEntryModel childEntry) {
        return childEntry.getProductInfos().stream()
            .filter(CPQOrderEntryProductInfoModel.class::isInstance)
            .map(CPQOrderEntryProductInfoModel.class::cast)
            .filter(cpqInfo -> PartnercoreConstants.PRODUCT_SALE_STATE_CODE.equalsIgnoreCase(cpqInfo.getCpqCharacteristicName()))
            .anyMatch(info -> PartnercoreConstants.PRODUCT_SALE_STATE_CODE_VALUE.equalsIgnoreCase(info.getCpqCharacteristicAssignedValues()));
    }

    protected boolean isPriceAvailableInECC(final AbstractOrderEntryModel childEntry) {
        return childEntry.getCpqPricingDetails().stream()
            .map(PartnerCpqPricingDetailModel.class::cast)
            .allMatch(cpqPricingDetail -> BooleanUtils.isFalse(cpqPricingDetail.getEccPriceAvailable()));
    }



    public Converter<IbmPartnerAgreementDetailModel, IbmPartnerAgreementDetailData> getAgreementDataConverter() {
        return agreementDataConverter;
    }

    public Converter<IbmPartnerEndCustomerB2BUnitModel, IbmPartnerEndCustomerB2BUnitData> getEndCustomerB2BUnitDataConverter() {
        return endCustomerB2BUnitDataConverter;
    }

    public Converter<IbmB2BUnitModel, IbmPartnerB2BUnitData> getB2bUnitDataConverter() {
        return b2bUnitDataConverter;
    }

    public Converter<IbmPartnerOpportunityModel, IbmPartnerOpportunityData> getOpportunityDataConverter() {
        return opportunityDataConverter;
    }

    public Converter<PartnerSpecialBidReasonModel, PartnerSpecialBidReasonData> getPartnerSpecialBidReasonConverter() {
        return partnerSpecialBidReasonConverter;
    }

    public Converter<PartnerCpqHeaderPricingDetailModel, PartnerCpqHeaderPricingDetailData> getpartnerHeaderPricingDetailConverter() {
        return partnerHeaderPricingDetailConverter;
    }

    public Converter<PartnerQuestionsSelectionModel, PartnerQuestionsSelectionData> getPartnerQuestionsSelectionDataConverter() {
        return partnerQuestionsSelectionDataConverter;
    }
    public Converter<PartnerCpqHeaderPricingDetailModel, List<YtyYearData>> getPartnerYtyOverrideConverter() {
        return partnerYtyOverrideConverter;
    }

    public Converter<PartnerProvisionFormModel, ProvisioningFormData> getProvisionFormConverter() {
        return provisionFormConverter;
    }

    public PartnerUserService getUserService() {
        return userService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public Converter<CurrencyModel, CurrencyData> getCurrencyConverter() {
        return currencyConverter;
    }
}

