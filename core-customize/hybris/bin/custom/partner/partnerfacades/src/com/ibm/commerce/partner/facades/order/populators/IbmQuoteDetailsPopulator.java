/**
 *
 */

package com.ibm.commerce.partner.facades.order.populators;

import com.ibm.commerce.partner.company.data.IbmPartnerB2BUnitData;
import com.ibm.commerce.partner.company.endcustomer.data.IbmPartnerAgreementDetailData;
import com.ibm.commerce.partner.company.endcustomer.data.IbmPartnerEndCustomerB2BUnitData;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.IbmB2BUnitModel;
import com.ibm.commerce.partner.core.model.IbmPartnerAgreementDetailModel;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.IbmPartnerEndCustomerB2BUnitModel;
import com.ibm.commerce.partner.core.model.IbmPartnerOpportunityModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.PartnerCpqHeaderPricingDetailModel;
import com.ibm.commerce.partner.core.model.PartnerProvisionFormModel;
import com.ibm.commerce.partner.core.model.PartnerQuestionsSelectionModel;
import com.ibm.commerce.partner.core.model.PartnerSpecialBidReasonModel;
import com.ibm.commerce.partner.core.order.services.PartnerCommerceOrderService;
import com.ibm.commerce.partner.core.user.service.PartnerUserService;
import com.ibm.commerce.partner.data.order.pricing.PartnerCpqHeaderPricingDetailData;
import com.ibm.commerce.partner.deal.data.IbmPartnerOpportunityData;
import com.ibm.commerce.partner.enums.data.DisplayTypeData;
import com.ibm.commerce.partner.order.data.PartnerOrderData;
import com.ibm.commerce.partner.partnerquestions.data.PartnerQuestionsSelectionData;
import com.ibm.commerce.partner.provision.form.data.ProvisioningFormData;
import com.ibm.commerce.partner.specialbidreason.data.PartnerSpecialBidReasonData;
import de.hybris.platform.commercefacades.quote.data.QuoteData;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;


/**
 * To populate IBM Quote updates {@link com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel} as
 * source and {@link QuoteData} as target type.
 */
public class IbmQuoteDetailsPopulator implements Populator<IbmPartnerQuoteModel, QuoteData> {

    private final Converter<IbmPartnerAgreementDetailModel, IbmPartnerAgreementDetailData> agreementDataConverter;
    private final Converter<IbmPartnerEndCustomerB2BUnitModel, IbmPartnerEndCustomerB2BUnitData> endCustomerB2BUnitDataConverter;
    private final Converter<IbmB2BUnitModel, IbmPartnerB2BUnitData> b2bUnitDataConverter;
    private final Converter<IbmPartnerOpportunityModel, IbmPartnerOpportunityData> opportunityDataConverter;
    private final Converter<PartnerSpecialBidReasonModel, PartnerSpecialBidReasonData> partnerSpecialBidReasonConverter;
    private final Converter<HybrisEnumValue, DisplayTypeData> displayTypeDataConverter;
    private final Converter<PartnerCpqHeaderPricingDetailModel, PartnerCpqHeaderPricingDetailData> partnerHeaderPricingDetailConverter;

    private final Converter<PartnerQuestionsSelectionModel, PartnerQuestionsSelectionData> partnerQuestionsSelectionDataConverter;

    private final Converter<OrderModel, PartnerOrderData> ibmPartnerOrderConverter;

    private final Converter<PartnerProvisionFormModel, ProvisioningFormData> provisionFormConverter;

    private final Converter<CurrencyModel, CurrencyData> currencyConverter;

    private final String dateFormat;

    private PartnerUserService userService;

    private final ConfigurationService configurationService;

    private final PartnerCommerceOrderService commerceOrderService;


    public IbmQuoteDetailsPopulator(
        final Converter<IbmPartnerAgreementDetailModel, IbmPartnerAgreementDetailData> agreementDataConverter,
        final Converter<IbmPartnerEndCustomerB2BUnitModel, IbmPartnerEndCustomerB2BUnitData> endCustomerB2BUnitDataConverter,
        final Converter<IbmB2BUnitModel, IbmPartnerB2BUnitData> b2bUnitDataConverter,
        final Converter<IbmPartnerOpportunityModel, IbmPartnerOpportunityData> opportunityDataConverter,
        final Converter<HybrisEnumValue, DisplayTypeData> displayTypeDataConverter,
        Converter<PartnerCpqHeaderPricingDetailModel, PartnerCpqHeaderPricingDetailData> partnerHeaderPricingDetailConverter,
        Converter<PartnerProvisionFormModel, ProvisioningFormData> provisionFormConverter,
        final String dateFormat,
        final Converter<PartnerSpecialBidReasonModel, PartnerSpecialBidReasonData> partnerSpecialBidReasonConverter,
        final Converter<PartnerQuestionsSelectionModel, PartnerQuestionsSelectionData> partnerQuestionsSelectionDataConverter,
        Converter<OrderModel, PartnerOrderData> ibmPartnerOrderConverter,
        PartnerUserService userService,
        PartnerCommerceOrderService commerceOrderService,
        final Converter<CurrencyModel, CurrencyData> currencyConverter,
        ConfigurationService configurationService) {
        this.agreementDataConverter = agreementDataConverter;
        this.endCustomerB2BUnitDataConverter = endCustomerB2BUnitDataConverter;
        this.b2bUnitDataConverter = b2bUnitDataConverter;
        this.opportunityDataConverter = opportunityDataConverter;
        this.displayTypeDataConverter = displayTypeDataConverter;
        this.partnerHeaderPricingDetailConverter = partnerHeaderPricingDetailConverter;
        this.provisionFormConverter = provisionFormConverter;
        this.dateFormat = dateFormat;
        this.partnerSpecialBidReasonConverter = partnerSpecialBidReasonConverter;
        this.partnerQuestionsSelectionDataConverter = partnerQuestionsSelectionDataConverter;
        this.ibmPartnerOrderConverter = ibmPartnerOrderConverter;
        this.userService = userService;
        this.commerceOrderService = commerceOrderService;
        this.currencyConverter = currencyConverter;
        this.configurationService = configurationService;
    }


    @Override
    public void populate(final IbmPartnerQuoteModel source, final QuoteData target)
        throws ConversionException {
        Assert.notNull(source, "Parameter source cannot be null.");
        Assert.notNull(target, "Parameter target cannot be null.");

        if (source.getAgreementDetail() != null) {
            target.setAgreementDetail(
                getAgreementDataConverter().convert(source.getAgreementDetail()));
        }
        populateUnits(source, target);
        if (Objects.nonNull(source.getQuoteExpirationDate())) {
            target.setQuoteExpirationDate(source.getQuoteExpirationDate());
        }

        if (source.getOpportunity() != null) {
            target.setOpportunity(getOpportunityDataConverter().convert(source.getOpportunity()));
        }
        target.setYtyPercentage(source.getYtyPercentage());
        target.setTotalMEPPrice(source.getTotalMEPPrice());
        target.setTotalFullPrice(source.getTotalFullPrice());

        if (source.getCartReference() instanceof final IbmPartnerCartModel partnerCart) {
            target.setTotalBidExtendedPrice(partnerCart.getTotalBidExtendedPrice());
            target.setFullPriceReceived(BooleanUtils.isTrue(partnerCart.getFullPriceReceived()));
        } else {
            target.setTotalBidExtendedPrice(source.getTotalBidExtendedPrice());
            target.setFullPriceReceived(BooleanUtils.isTrue(source.getFullPriceReceived()));
        }

        target.setDiscount(source.getTotalDiscounts());

        if (source.getState() != null) {
            target.setQuoteStatus(getDisplayTypeDataConverter().convert(source.getState()));
        }
        if (source.getCpqQuoteStatus() != null) {
            target.setCpqQuoteStatus(
                getDisplayTypeDataConverter().convert(source.getCpqQuoteStatus()));
        }
        if (source.getSubmittedDate() != null) {
            final DateFormat dateFormat = new SimpleDateFormat(getDateFormat());
            final String submitDate = dateFormat.format(source.getSubmittedDate());
            target.setSubmittedDate(submitDate);
        }

        if (BooleanUtils.isTrue(isMultipleSpecialBidDisabled())) {
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

        if (source.getSalesApplication() != null) {
            target.setSalesApplication(
                getDisplayTypeDataConverter().convert(source.getSalesApplication()));
        }
        target.setSpecialBidBusinessJustification(source.getSpecialBidBusinessJustification());
        target.setEccQuoteNumber(source.getEccQuoteNumber());

        setSpecialBid(source, target);

        if (CollectionUtils.isNotEmpty(source.getPartnerQuestionsSelections())) {
            target.setPartnerQuestionsSelections(Converters.convertAll(
                source.getPartnerQuestionsSelections(),
                getPartnerQuestionsSelectionDataConverter()));
        }

        if (CollectionUtils.isNotEmpty(source.getPricingDetailsQuote())) {
            target.setPartnerCpqHeaderPricingDetails(
                Converters.convertAll(source.getPricingDetailsQuote(),
                    getpartnerHeaderPricingDetailConverter()));
        }
        target.setVadView(getUserService().isVadView(source, getUserService().getCurrentUser()));
        target.setCpqQuoteNumber(source.getCpqQuoteNumber());
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

    protected void setSpecialBid(final IbmPartnerQuoteModel source, final QuoteData target) {
        if (BooleanUtils.isTrue(isMultipleSpecialBidDisabled())) {
            if (ObjectUtils.isNotEmpty(source.getSpecialBidBusinessJustification())
                && ObjectUtils.isNotEmpty(source.getSpecialBidReason())) {
                target.setSpecialBid(Boolean.TRUE);
            }
        } else {
            if (ObjectUtils.isNotEmpty(source.getSpecialBidBusinessJustification())
                && ObjectUtils.isNotEmpty(source.getSpecialBidReasons())) {
                target.setSpecialBid(Boolean.TRUE);
            }
        }
    }

    private void populateUnits(final IbmPartnerQuoteModel source, final QuoteData target) {
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
        List<OrderModel> orderModelList = getCommerceOrderService().findOrdersByQuote(source);
        if (CollectionUtils.isNotEmpty(orderModelList)) {
            target.setOrders(Converters.convertAll(orderModelList,
                getIbmPartnerOrderConverter()));
        }
        if (source.getProvisionForms() != null) {
            if (StringUtils.isNotEmpty(source.getProvisionForms().getErrors()) ) {
                target.setProvisionFormError(source.getProvisionForms().getErrors());
            } else {
                Set<PartnerProvisionFormModel> forms = source.getProvisionForms()
                    .getPartnerProvisionForm();
                target.setProvisionForms(Converters.convertAll(forms, getProvisionFormConverter()));
            }
        }

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

    public Converter<HybrisEnumValue, DisplayTypeData> getDisplayTypeDataConverter() {
        return displayTypeDataConverter;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public Converter<PartnerSpecialBidReasonModel, PartnerSpecialBidReasonData> getPartnerSpecialBidReasonConverter() {
        return partnerSpecialBidReasonConverter;
    }

    public Converter<PartnerQuestionsSelectionModel, PartnerQuestionsSelectionData> getPartnerQuestionsSelectionDataConverter() {
        return partnerQuestionsSelectionDataConverter;
    }

    public PartnerUserService getUserService() {
        return userService;
    }

    public Converter<OrderModel, PartnerOrderData> getIbmPartnerOrderConverter() {
        return ibmPartnerOrderConverter;
    }

    public Converter<PartnerProvisionFormModel, ProvisioningFormData> getProvisionFormConverter() {
        return provisionFormConverter;
    }

    public PartnerCommerceOrderService getCommerceOrderService() {
        return commerceOrderService;
    }


    public Converter<PartnerCpqHeaderPricingDetailModel, PartnerCpqHeaderPricingDetailData> getpartnerHeaderPricingDetailConverter() {
        return partnerHeaderPricingDetailConverter;
    }

    public Converter<CurrencyModel, CurrencyData> getCurrencyConverter() {
        return currencyConverter;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public Boolean isMultipleSpecialBidDisabled() {
        return getConfigurationService().getConfiguration()
            .getBoolean(PartnercoreConstants.FLAG_SPECIAL_BID_REASONS_MULTI_SELECT_DISABLED,
                Boolean.TRUE);
    }

}