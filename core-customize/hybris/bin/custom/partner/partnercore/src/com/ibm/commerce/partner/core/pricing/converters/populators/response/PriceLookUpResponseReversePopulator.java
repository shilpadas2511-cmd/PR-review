package com.ibm.commerce.partner.core.pricing.converters.populators.response;

import com.ibm.commerce.partner.core.enums.CpqPricingTypeEnum;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.PartnerCpqHeaderPricingDetailModel;
import com.ibm.commerce.partner.core.model.PartnerCpqPricingDetailModel;
import com.ibm.commerce.partner.core.order.price.data.response.PriceLookUpConfigurationsResponseData;
import com.ibm.commerce.partner.core.order.price.data.response.PriceLookUpHeaderResponseData;
import com.ibm.commerce.partner.core.order.price.data.response.PriceLookUpItemsResponseData;
import com.ibm.commerce.partner.core.order.price.data.response.PriceLookUpResponseData;
import com.ibm.commerce.partner.core.services.PriceLookUpService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * Populator to reverse populate PriceLookUpResponseData to AbstractOrderModel.
 */
public class PriceLookUpResponseReversePopulator implements
    Populator<PriceLookUpResponseData, AbstractOrderModel> {

    private final static Logger LOG = Logger.getLogger(PriceLookUpResponseReversePopulator.class);
    private final ModelService modelService;
    private final PriceLookUpService priceLookUpService;
    private final List<String> priceNotAvailableErrorCodes;

    private final static String PRICING_ERROR_MESSAGE = "Pricing Error: %s";

    public PriceLookUpResponseReversePopulator(final ModelService modelService,
        final PriceLookUpService priceLookUpService,
        final List<String> priceNotAvailableErrorCodes) {
        this.modelService = modelService;
        this.priceLookUpService = priceLookUpService;
        this.priceNotAvailableErrorCodes = priceNotAvailableErrorCodes;
    }


    /**
     * Populates the target AbstractOrderModel with data from the source PriceLookUpResponseData.
     *
     * @param source The source PriceLookUpResponseData.
     * @param target The target AbstractOrderModel.
     * @throws ConversionException if an error occurs during conversion.
     */
    @Override
    public void populate(final PriceLookUpResponseData source, final AbstractOrderModel target)
        throws ConversionException {
        if (source != null) {
            populateOrderHeader(source.getHeader(), target, source.getType());
            source.getConfigurations()
                .forEach(config -> processConfiguration(config, target, source.getType()));
        }
    }

    /**
     * Processes each configuration from PriceLookUpResponseData and updates the order accordingly.
     *
     * @param config          The configuration data.
     * @param target          The target AbstractOrderModel.
     * @param pricingTypeEnum pricingTypeEnum
     */
    protected void processConfiguration(final PriceLookUpConfigurationsResponseData config,
        final AbstractOrderModel target, final CpqPricingTypeEnum pricingTypeEnum) {
        final Optional<AbstractOrderEntryModel> mainEntry = getPriceLookUpService().findPidEntryByEntryNumber(
            target, config.getPid(), config.getConfigurationId());
        mainEntry.ifPresent(mainOrderEntry -> {
            final PartnerCpqPricingDetailModel partnerCpqPricingDetail = getCpqPricingDetail(
                mainOrderEntry, pricingTypeEnum);
            if (CpqPricingTypeEnum.ENTITLED.equals(pricingTypeEnum)) {
                mainOrderEntry.setTotalPrice(config.getTotalExtendedPrice());
                partnerCpqPricingDetail.setTotalExtendedPrice(config.getTotalExtendedPrice());
            } else {
                partnerCpqPricingDetail.setTotalExtendedPrice(config.getTotalExtendedPrice());
            }
            partnerCpqPricingDetail.setRolledUpBidExtendedPrice(
                config.getTotalBidExtendedPrice() != null ? String.valueOf(
                    config.getTotalBidExtendedPrice()) : null);
            partnerCpqPricingDetail.setModifiedtime(new Date());
            getModelService().save(partnerCpqPricingDetail);
            getModelService().save(mainOrderEntry);
            config.getItems().forEach(item -> processItems(item, mainOrderEntry, pricingTypeEnum));
        });


    }

    /**
     * Processes each item in a configuration and updates the order entry accordingly.
     *
     * @param item            The item data.
     * @param entry           The main order entry.
     * @param pricingTypeEnum pricingTypeEnum
     */
    protected void processItems(final PriceLookUpItemsResponseData item,
        final AbstractOrderEntryModel entry, final CpqPricingTypeEnum pricingTypeEnum) {
        if (entry != null) {
            final Optional<AbstractOrderEntryModel> childEntry = getPriceLookUpService().getChildEntry(
                entry, item.getPartNumber(), item.getItemNumber());
            childEntry.ifPresent(child -> {
                final PartnerCpqPricingDetailModel partnerCpqPricingDetail = getCpqPricingDetail(
                    child, pricingTypeEnum);
                populateItems(item, partnerCpqPricingDetail);
                partnerCpqPricingDetail.setModifiedtime(new Date());
                getModelService().save(partnerCpqPricingDetail);
            });
        }
    }

    /**
     * Retrieves or creates a PartnerCpqPricingDetailModel for an order entry.
     *
     * @param entry           The order entry.
     * @param pricingTypeEnum pricingTypeEnum
     * @return The PartnerCpqPricingDetailModel.
     */
    protected PartnerCpqPricingDetailModel getCpqPricingDetail(final AbstractOrderEntryModel entry,
        final CpqPricingTypeEnum pricingTypeEnum) {
        return entry.getCpqPricingDetails().stream()
            .filter(PartnerCpqPricingDetailModel.class::isInstance)
            .map(PartnerCpqPricingDetailModel.class::cast)
            .filter(pricing -> pricingTypeEnum.getCode().equals(pricing.getPricingType())).findAny()
            .orElseGet(() -> createCpqPricing(entry, pricingTypeEnum));
    }

    /**
     * Creates a new PartnerCpqPricingDetailModel for an order entry.
     *
     * @param entry           The order entry.
     * @param pricingTypeEnum pricing Type
     * @return The created PartnerCpqPricingDetailModel.
     */
    protected PartnerCpqPricingDetailModel createCpqPricing(final AbstractOrderEntryModel entry,
        final CpqPricingTypeEnum pricingTypeEnum) {
        final PartnerCpqPricingDetailModel cpqPricingDetail = getModelService().create(
            PartnerCpqPricingDetailModel.class);
        cpqPricingDetail.setOrderEntry(entry);
        cpqPricingDetail.setPricingType(CpqPricingTypeEnum.ENTITLED.equals(pricingTypeEnum)
            ? CpqPricingTypeEnum.ENTITLED.getCode() : CpqPricingTypeEnum.FULL.getCode());
        return cpqPricingDetail;
    }

    /**
     * Populates the order header with data from PriceLookUpHeaderResponseData.
     *
     * @param header          The header data.
     * @param target          The target AbstractOrderModel.
     * @param pricingTypeEnum pricingTypeEnum
     */
    protected void populateOrderHeader(final PriceLookUpHeaderResponseData header,
        final AbstractOrderModel target, final CpqPricingTypeEnum pricingTypeEnum) {
        if (target instanceof final IbmPartnerCartModel cartTarget) {
            final double totalPrice = header.getTotalExtendedPrice();
            if (CpqPricingTypeEnum.ENTITLED.equals(pricingTypeEnum)) {
                cartTarget.setTotalEntitledPrice(totalPrice);
                cartTarget.setTotalPrice(totalPrice);
            } else {
                cartTarget.setTotalFullPrice(totalPrice);
                cartTarget.setFullPriceReceived(Boolean.TRUE);
            }
            target.setTotalDiscounts(header.getTotalDiscount());
            cartTarget.setYtyPercentage(header.getTotalYTY());
            cartTarget.setTotalBidExtendedPrice(header.getTotalBidExtendedPrice());
            cartTarget.setTotalMEPPrice(header.getTotalMEPPrice());
            cartTarget.setTotalOptimalPrice(header.getTotalOptimalPrice());
            cartTarget.setTotalChannelMargin(header.getTotalChannelMargin());
            cartTarget.setTotalBpExtendedPrice(header.getTotalBpExtendedPrice());
            cartTarget.setTransactionPriceLevel(header.getTransactionPriceLevel());
            PartnerCpqHeaderPricingDetailModel partnerCpqHeaderPricingDetail = getPriceLookUpService().getHeaderCpqPricingDetail(
                cartTarget, pricingTypeEnum);
            populateCpqHeaderPrice(partnerCpqHeaderPricingDetail,header);
        }
    }

    /**
     * Populates the PartnerCpqPricingDetailModel with data from PriceLookUpItemsResponseData.
     *
     * @param item             The item data.
     * @param cpqPricingDetail The PartnerCpqPricingDetailModel to populate.
     */
    protected void populateItems(final PriceLookUpItemsResponseData item,
        final PartnerCpqPricingDetailModel cpqPricingDetail) {
        cpqPricingDetail.setNetPrice(
            item.getInitialPrice() != null ? String.valueOf(item.getInitialPrice()) : null);
        cpqPricingDetail.setExtendedListPrice(
            item.getExtendedPrice() != null ? String.valueOf(item.getExtendedPrice()) : null);
        cpqPricingDetail.setListPrice(
            item.getOptimalPrice() != null ? String.valueOf(item.getOptimalPrice()) : null);
        cpqPricingDetail.setDiscountAmount(
            item.getOptimalDiscount() != null ? String.valueOf(item.getOptimalDiscount()) : null);
        cpqPricingDetail.setOptimalDiscount(item.getOptimalDiscount());
        cpqPricingDetail.setDiscountPercent(
            item.getDiscountPercentage() != null ? String.valueOf(item.getDiscountPercentage())
                : null);
        cpqPricingDetail.setRolledUpListPrice(
            item.getBPInitialPrice() != null ? String.valueOf(item.getBPInitialPrice()) : null);
        cpqPricingDetail.setRolledUpExtendedListPrice(
            item.getBPExtendedPrice() != null ? String.valueOf(item.getBPExtendedPrice()) : null);
        cpqPricingDetail.setRolledUpBidExtendedPrice(
            item.getBidExtendedPrice() != null ? String.valueOf(item.getBidExtendedPrice()) : null);
        cpqPricingDetail.setRolledUpNetPrice(
            item.getBidInitialPrice() != null ? String.valueOf(item.getBidInitialPrice()) : null);
        cpqPricingDetail.setPricingStrategy(item.getPricingStrategy());
        cpqPricingDetail.setEccRequest(item.getEccRequest());
        cpqPricingDetail.setEccOverrideFields(item.getEccOverrideFields());
        cpqPricingDetail.setExtendedUnitPrice(item.getExtendedUnitPrice());
        cpqPricingDetail.setChannelMargin(item.getStdPartnerDiscount());
        cpqPricingDetail.setYtyPercentage(item.getYtyPercentage());
        cpqPricingDetail.setYtyPercentageDefault(item.getDefaultYTYPercentage());
        if (StringUtils.isBlank(item.getErrorCode())) {
            cpqPricingDetail.setEccPriceAvailable(Boolean.TRUE);
        } else if (getPriceNotAvailableErrorCodes().stream()
            .anyMatch(errorCode -> errorCode.equalsIgnoreCase(item.getErrorCode()))) {
            cpqPricingDetail.setEccPriceAvailable(Boolean.FALSE);
        } else {
            LOG.info(String.format(PRICING_ERROR_MESSAGE,
                StringUtils.defaultString(item.getErrorCode())));
        }
    }

    protected void populateCpqHeaderPrice(PartnerCpqHeaderPricingDetailModel partnerCpqHeaderPricingDetail,PriceLookUpHeaderResponseData header ){
        partnerCpqHeaderPricingDetail.setTotalExtendedPrice(header.getTotalExtendedPrice());
        partnerCpqHeaderPricingDetail.setTotalDiscount(header.getTotalDiscount());
        partnerCpqHeaderPricingDetail.setYtyPercentage(header.getTotalYTY());
        partnerCpqHeaderPricingDetail.setTotalBidExtendedPrice(header.getTotalBidExtendedPrice());
        partnerCpqHeaderPricingDetail.setTotalMEPPrice(header.getTotalMEPPrice());
        partnerCpqHeaderPricingDetail.setTotalOptimalPrice(header.getTotalOptimalPrice());
        partnerCpqHeaderPricingDetail.setTotalChannelMargin(header.getTotalChannelMargin());
        partnerCpqHeaderPricingDetail.setTotalBpExtendedPrice(header.getTotalBpExtendedPrice());
        partnerCpqHeaderPricingDetail.setTransactionPriceLevel(header.getTransactionPriceLevel());
        partnerCpqHeaderPricingDetail.setModifiedtime(new Date());
        partnerCpqHeaderPricingDetail.setInitialTotalExtendedPrice(header.getTotalExtendedPrice());
        partnerCpqHeaderPricingDetail.setTotalUSDExtendedPrice(header.getTotalUSDExtendedPrice());
        partnerCpqHeaderPricingDetail.setTotalUSDBidExtendedPrice(header.getTotalUSDBidExtendedPrice());
        getModelService().save(partnerCpqHeaderPricingDetail);
    }

    public ModelService getModelService() {
        return modelService;
    }

    public PriceLookUpService getPriceLookUpService() {
        return priceLookUpService;
    }

    public List<String> getPriceNotAvailableErrorCodes() {
        return priceNotAvailableErrorCodes;
    }
}
