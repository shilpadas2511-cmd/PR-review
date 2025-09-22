package com.ibm.commerce.partner.core.services.impl;

import com.ibm.commerce.common.core.model.IbmVariantProductModel;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.enums.CpqPricingTypeEnum;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.PartnerCpqHeaderPricingDetailModel;
import com.ibm.commerce.partner.core.model.PartnerCpqPricingDetailModel;
import com.ibm.commerce.partner.core.services.PriceLookUpService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * Default implementation of the PriceLookUpService interface.
 */
public class DefaultPriceLookUpService implements PriceLookUpService {

    private final ModelService modelService;
    private static final int NUMBER_HUNDRED = 100;

    public DefaultPriceLookUpService(ModelService modelService) {
        this.modelService = modelService;
    }

    /**
     * Get the list of child entries from the given AbstractOrderModel.
     *
     * @param source AbstractOrderModel from which child entries are to be retrieved.
     * @return List of AbstractOrderEntryModel representing child entries.
     */
    @Override
    public List<AbstractOrderEntryModel> getChildEntriesList(AbstractOrderModel source) {
        List<AbstractOrderEntryModel> childEntriesList = new ArrayList<>();
        source.getEntries().forEach(entry -> {
            if (CollectionUtils.isNotEmpty(entry.getChildEntries())) {
                childEntriesList.addAll(entry.getChildEntries());
            }
        });
        return childEntriesList;
    }

    /**
     * Find a child entry in the main entry based on the part number.
     *
     * @param mainEntry  AbstractOrderEntryModel representing the main entry.
     * @param partNumber Part number to search for.
     * @return Optional containing the AbstractOrderEntryModel if found, otherwise empty.
     */
    @Override
    public Optional<AbstractOrderEntryModel> getChildEntry(AbstractOrderEntryModel mainEntry,
        String partNumber, int itemNumber) {
        return mainEntry.getChildEntries().stream().filter(
            entry -> entry.getProduct().getCode().equals(partNumber) && entry.getEntryNumber()
                .equals(getEntryNumberFromItemNumber(itemNumber, mainEntry))).findFirst();
    }

    public int getEntryNumberFromItemNumber(int itemNumber, AbstractOrderEntryModel mainEntry) {
        return (itemNumber - ((mainEntry.getEntryNumber() + NumberUtils.INTEGER_ONE)
            * NUMBER_HUNDRED));
    }

    /**
     * Find an entry in the cart based on the response entry number.
     *
     * @param cart       AbstractOrderModel representing the cart.
     * @param pidCode    Response pidCode to search for.
     * @param configCode Response entry number to search for.
     * @return Optional containing the AbstractOrderEntryModel if found, otherwise empty.
     */
    @Override
    public Optional<AbstractOrderEntryModel> findPidEntryByEntryNumber(AbstractOrderModel cart,
        String pidCode, String configCode) {
        if (StringUtils.isBlank(configCode) || StringUtils.isBlank(pidCode) || cart == null
            || CollectionUtils.isEmpty(cart.getEntries())) {
            return Optional.empty();
        }
        return cart.getEntries().stream()
            .filter(entry -> entry.getProduct() instanceof IbmVariantProductModel).filter(entry ->
                ((IbmVariantProductModel) entry.getProduct()).getPartNumber().equals(pidCode)
                    && entry.getProductConfiguration() != null && configCode.equals(
                    entry.getProductConfiguration().getConfigurationId())).findFirst();
    }


    /**
     * Find a entry in the cart based on the part number.
     *
     * @param mainEntry  AbstractOrderEntryModel representing the main entry.
     * @param partNumber Part number to search for.
     * @return Optional containing the AbstractOrderEntryModel if found, otherwise empty.
     */
    @Override
    public Optional<AbstractOrderEntryModel> getMainEntry(AbstractOrderModel order,
        String partNumber) {
        return order.getEntries().stream()
            .filter(entry -> entry.getProduct().getCode().equals(partNumber)).findFirst();
    }

    /**
     * Retrieves or creates a PartnerCpqPricingDetailModel for an order entry.
     *
     * @param entry           The order entry.
     * @param pricingTypeEnum pricingTypeEnum
     * @return The PartnerCpqPricingDetailModel.
     */
    @Override
    public Optional<PartnerCpqPricingDetailModel> getCpqPricingDetail(
        final AbstractOrderEntryModel entry, final CpqPricingTypeEnum pricingTypeEnum) {
        return entry.getCpqPricingDetails().stream()
            .filter(PartnerCpqPricingDetailModel.class::isInstance)
            .map(PartnerCpqPricingDetailModel.class::cast)
            .filter(pricing -> pricingTypeEnum.getCode().equals(pricing.getPricingType()))
            .findAny();
    }

    @Override
    public PartnerCpqPricingDetailModel getEntryCpqPricingDetail(
        final AbstractOrderEntryModel entry, final CpqPricingTypeEnum pricingTypeEnum) {
        return entry.getCpqPricingDetails().stream()
            .filter(PartnerCpqPricingDetailModel.class::isInstance)
            .map(PartnerCpqPricingDetailModel.class::cast)
            .filter(pricing -> pricingTypeEnum.getCode().equals(pricing.getPricingType()))
            .findAny().orElseGet(() -> createEntryCpqPricing(entry, pricingTypeEnum));
    }

    public PartnerCpqPricingDetailModel createEntryCpqPricing(final AbstractOrderEntryModel entry,
        final CpqPricingTypeEnum pricingTypeEnum) {
        final PartnerCpqPricingDetailModel cpqPricingDetail = getModelService().create(
            PartnerCpqPricingDetailModel.class);
        cpqPricingDetail.setOrderEntry(entry);
        cpqPricingDetail.setPricingType(pricingTypeEnum.getCode().toString());
        return cpqPricingDetail;
    }

    /**
     * Retrieves or creates a PartnerCpqPricingDetailModel for an order entry.
     *
     * @param entry           The order entry.
     * @param pricingTypeEnum pricingTypeEnum
     * @return The PartnerCpqPricingDetailModel.
     */
    @Override
    public PartnerCpqHeaderPricingDetailModel getHeaderCpqPricingDetail(
        final IbmPartnerCartModel cart, final CpqPricingTypeEnum pricingTypeEnum) {
        return cart.getPricingDetails().stream()
            .filter(PartnerCpqHeaderPricingDetailModel.class::isInstance)
            .map(PartnerCpqHeaderPricingDetailModel.class::cast)
            .filter(pricing -> pricingTypeEnum.getCode().equals(pricing.getPricingType())).findAny()
            .orElseGet(() -> createHeaderCpqPricing(cart, pricingTypeEnum));
    }

    /**
     * Creates a new PartnerCpqPricingDetailModel for an order entry.
     *
     * @param entry           The order entry.
     * @param pricingTypeEnum pricing Type
     * @return The created PartnerCpqPricingDetailModel.
     */
    @Override
    public PartnerCpqHeaderPricingDetailModel createHeaderCpqPricing(final IbmPartnerCartModel cart,
        final CpqPricingTypeEnum pricingTypeEnum) {
        final PartnerCpqHeaderPricingDetailModel cpqPricingDetail = getModelService().create(
            PartnerCpqHeaderPricingDetailModel.class);
        cpqPricingDetail.setIbmPartnerCart(cart);
        cpqPricingDetail.setPricingType(pricingTypeEnum.getCode().toString());
        cpqPricingDetail.setInitialTotalExtendedPrice(cart.getTotalFullPrice());
        return cpqPricingDetail;
    }

    /*
     *Remove Header pricing details from quote /
     * cart when add/delete/edit happens in the quote cart
     */
    @Override
    public void removeOverridenHeaderPrices(AbstractOrderModel orderModel) {
        IbmPartnerCartModel cartModel = null;

        if (orderModel instanceof IbmPartnerCartModel cart) {
            cartModel = cart;
        } else if (orderModel instanceof IbmPartnerQuoteModel quote
            && quote.getCartReference() instanceof IbmPartnerCartModel cart) {
            cartModel = cart;
        }
        if (cartModel != null) {
            PartnerCpqHeaderPricingDetailModel partnerCpqHeaderPricingDetail = getHeaderPricingDetail(
                cartModel);
            if (partnerCpqHeaderPricingDetail != null) {
                partnerCpqHeaderPricingDetail.setOverrideTotalPrice(null);
                partnerCpqHeaderPricingDetail.setOverrideTotalDiscount(null);
                getModelService().save(partnerCpqHeaderPricingDetail);
            }
        }
    }

    @Override
    public void populateYtyDiscount(IbmPartnerCartModel cart) {
        if (null != cart.getQuoteReference()) {
            int maxEntryGroupSize = getMaxYtyYear(cart);
            PartnerCpqHeaderPricingDetailModel partnerCpqHeaderPricingDetail = getHeaderCpqPricingDetail(
                cart, CpqPricingTypeEnum.FULL);
            Map<String, Double> yty = new HashMap<>();
            if (maxEntryGroupSize > NumberUtils.INTEGER_ZERO) {
                for (int i = NumberUtils.INTEGER_ONE; i <= maxEntryGroupSize; i++) {
                    String ytyLabel =
                        PartnercoreConstants.YTY_GROUP_LABEL_PREFIX + (i + NumberUtils.INTEGER_ONE);
                    yty.put(ytyLabel, NumberUtils.DOUBLE_ZERO);
                }
            } else if (maxEntryGroupSize == NumberUtils.INTEGER_ZERO) {
                partnerCpqHeaderPricingDetail.setYtyYears(null);
            }
            if (MapUtils.isNotEmpty(yty)) {
                partnerCpqHeaderPricingDetail.setYtyYears(yty);
            }
            getModelService().save(partnerCpqHeaderPricingDetail);
        }
//Commented out this code as we want to revert YTY value back to 0 on any update in the cart bug id: SCFP-9302

           /* Map<String, Double> existingYTY = partnerCpqHeaderPricingDetail.getYtyYears();
            if (maxEntryGroupSize > NumberUtils.INTEGER_ZERO) {
                for (int i = NumberUtils.INTEGER_ONE; i <= maxEntryGroupSize; i++) {
                    String ytyLabel =
                        PartnercoreConstants.YTY_GROUP_LABEL_PREFIX + (i + NumberUtils.INTEGER_ONE);
                    if(MapUtils.isNotEmpty(existingYTY) && existingYTY.keySet().contains(ytyLabel)){
                        yty.put(ytyLabel, existingYTY.get(ytyLabel));
                    }else{
                        yty.put(ytyLabel, NumberUtils.DOUBLE_ZERO);
                    }
                }
            } else if (maxEntryGroupSize==NumberUtils.INTEGER_ZERO) {
                partnerCpqHeaderPricingDetail.setYtyYears(null);
            }*/

    }

    @Override
    public PartnerCpqHeaderPricingDetailModel getHeaderPricingDetail(
        IbmPartnerCartModel cartModel) {
        Optional<PartnerCpqHeaderPricingDetailModel> partnerCpqHeaderPricingDetail = cartModel.getPricingDetails()
            .stream().filter(PartnerCpqHeaderPricingDetailModel.class::isInstance)
            .map(PartnerCpqHeaderPricingDetailModel.class::cast).filter(
                pricing -> CpqPricingTypeEnum.FULL.getCode().equals(pricing.getPricingType()))
            .findAny();
        if (partnerCpqHeaderPricingDetail.isPresent()) {
            return partnerCpqHeaderPricingDetail.get();
        }
        return null;
    }

    /**
     * Removes all pricing information from the given order header.
     *
     * @param order The order from which pricing information will be removed.
     */
    @Override
    public void removeOrderPricingInformation(final IbmPartnerQuoteModel order) {
        order.setTotalMEPPrice(NumberUtils.DOUBLE_ZERO);
        order.setTotalPrice(NumberUtils.DOUBLE_ZERO);
        order.setTotalEntitledPrice(NumberUtils.DOUBLE_ZERO);
        order.setTotalFullPrice(NumberUtils.DOUBLE_ZERO);
        order.setTotalOptimalPrice(NumberUtils.DOUBLE_ZERO);
        order.setTotalBidExtendedPrice(NumberUtils.DOUBLE_ZERO);
        order.setTotalBpExtendedPrice(NumberUtils.DOUBLE_ZERO);
        order.setYtyPercentage(NumberUtils.DOUBLE_ZERO);
        order.setTotalDiscounts(NumberUtils.DOUBLE_ZERO);
        order.setTotalChannelMargin(NumberUtils.DOUBLE_ZERO);
    }


    @Override
    public int getMaxYtyYear(IbmPartnerCartModel cart) {
        return cart.getEntries().stream()
            .map(entry -> entry.getChildEntries().stream()
                .findFirst()
                .map(childEntry -> {
                    if (childEntry.getOrder() != null && childEntry.getOrder().getEntryGroups() != null) {
                        return childEntry.getOrder().getEntryGroups().size();
                    }
                    return 0;
                })
                .orElse(0))
            .max(Integer::compare)
            .orElse(0);
    }

    @Override
    public PartnerCpqHeaderPricingDetailModel populateCPQHeaderPricingDetail(PartnerCpqHeaderPricingDetailModel original){
        final PartnerCpqHeaderPricingDetailModel cpqPricingDetail = getModelService().create(
            PartnerCpqHeaderPricingDetailModel.class);
        cpqPricingDetail.setPricingType(original.getPricingType());
        cpqPricingDetail.setOverrideTotalPrice(original.getOverrideTotalPrice());
        cpqPricingDetail.setOverrideTotalDiscount(original.getOverrideTotalDiscount());
        cpqPricingDetail.setInitialTotalExtendedPrice(
            original.getInitialTotalExtendedPrice());
        cpqPricingDetail.setTotalExtendedPrice(original.getTotalExtendedPrice());
        cpqPricingDetail.setTotalMEPPrice(original.getTotalMEPPrice());
        cpqPricingDetail.setTotalDiscount(original.getTotalDiscount());
        cpqPricingDetail.setYtyPercentage(original.getYtyPercentage());
        cpqPricingDetail.setTotalBidExtendedPrice(original.getTotalBidExtendedPrice());
        cpqPricingDetail.setTotalOptimalPrice(original.getTotalOptimalPrice());
        cpqPricingDetail.setTotalChannelMargin(original.getTotalChannelMargin());
        cpqPricingDetail.setTotalBpExtendedPrice(original.getTotalBpExtendedPrice());
        cpqPricingDetail.setTransactionPriceLevel(original.getTransactionPriceLevel());
        cpqPricingDetail.setTotalUSDExtendedPrice(original.getTotalUSDExtendedPrice());
        cpqPricingDetail.setTotalUSDBidExtendedPrice(original.getTotalUSDBidExtendedPrice());
        return cpqPricingDetail;
    }

    public ModelService getModelService() {
        return modelService;
    }
}
