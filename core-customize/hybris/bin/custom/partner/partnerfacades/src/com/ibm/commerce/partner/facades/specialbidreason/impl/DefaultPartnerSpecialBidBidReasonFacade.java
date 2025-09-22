package com.ibm.commerce.partner.facades.specialbidreason.impl;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.PartnerSpecialBidReasonModel;
import com.ibm.commerce.partner.core.specialbidreason.service.PartnerSpecialBidReasonService;
import com.ibm.commerce.partner.facades.specialbidreason.PartnerSpecialBidReasonFacade;
import com.ibm.commerce.partner.specialbidreason.data.PartnerSpecialBidReasonData;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * DefaultPartnerSpecialBidBidFacade is used to fetch the specialbid model information from Service
 * class.
 */
public class DefaultPartnerSpecialBidBidReasonFacade implements PartnerSpecialBidReasonFacade {

    private final PartnerSpecialBidReasonService specialBidReasonService;
    private final Converter<PartnerSpecialBidReasonModel, PartnerSpecialBidReasonData> specialBidReasonConverter;
    private final ConfigurationService configurationService;
    private final CartService cartService;
    private final ModelService modelService;

    public DefaultPartnerSpecialBidBidReasonFacade(
        PartnerSpecialBidReasonService specialBidReasonService,
        Converter<PartnerSpecialBidReasonModel, PartnerSpecialBidReasonData> specialBidReasonConverter,
        ConfigurationService configurationService, final CartService cartService,
        final ModelService modelService) {
        this.specialBidReasonService = specialBidReasonService;
        this.specialBidReasonConverter = specialBidReasonConverter;
        this.configurationService = configurationService;
        this.cartService = cartService;
        this.modelService = modelService;
    }

    /**
     * +
     *
     * @return the PartnerSpecialBidReasonData - List of Special bid reason data
     */
    @Override
    public List<PartnerSpecialBidReasonData> getAllSpecialBidReasonDetails() {
        List<PartnerSpecialBidReasonModel> specialBidReasons = getSpecialBidReasonService().getAllSpecialBidReasonDetails();
        return getSpecialBidReasonConverter().convertAll(specialBidReasons);
    }

    /**
     * Returns the list of special bid reason details based on the multi-select feature flag.
     * <p>
     * If multi-select is disabled (flag is true), returns the full list directly. Otherwise,
     * updates and returns the list with selection status based on the session cart.
     *
     * @return the list of {@link PartnerSpecialBidReasonData} with or without selection applied
     */
    @Override
    public List<PartnerSpecialBidReasonData> getAllSpecialBidReasonDetailsWithSelection() {
        final List<PartnerSpecialBidReasonData> specialBidReasonList = getAllSpecialBidReasonDetails();
        final boolean isMultipleSpecialBidDisabled = getConfigurationService().getConfiguration()
            .getBoolean(PartnercoreConstants.FLAG_SPECIAL_BID_REASONS_MULTI_SELECT_DISABLED,
                Boolean.TRUE);
        if (BooleanUtils.isTrue(isMultipleSpecialBidDisabled)) {
            return specialBidReasonList;
        }
        if (CollectionUtils.isNotEmpty(specialBidReasonList)
            && getCartService().getSessionCart() instanceof IbmPartnerCartModel cartModel
            && CollectionUtils.isNotEmpty(cartModel.getSpecialBidReasons())) {
            final Set<PartnerSpecialBidReasonModel> existingReasons = cartModel.getSpecialBidReasons();

            final List<String> existingReasonCodes = existingReasons.stream()
                .map(PartnerSpecialBidReasonModel::getCode).toList();
            specialBidReasonList.forEach(
                data -> data.setSelected(existingReasonCodes.contains(data.getCode())));
        }

        return specialBidReasonList;
    }

    @Override
    public void saveInCart(final List<PartnerSpecialBidReasonData> specialBidReasons) {
        if (getCartService().getSessionCart() instanceof IbmPartnerCartModel cartModel) {
            Set<PartnerSpecialBidReasonModel> specialBidReasonByIdsSet = new HashSet<>();
            if (CollectionUtils.isNotEmpty(specialBidReasons)) {
                final List<String> selectedReasonCodes = specialBidReasons.stream()
                    .map(PartnerSpecialBidReasonData::getCode).filter(StringUtils::isNotBlank)
                    .toList();
                final List<PartnerSpecialBidReasonModel> specialBidReasonByIds = getSpecialBidReasonService().getSpecialBidReasonByIds(
                    selectedReasonCodes);
                if (CollectionUtils.isNotEmpty(specialBidReasonByIds)) {
                    specialBidReasonByIdsSet.addAll(specialBidReasonByIds);
                }
            }

            cartModel.setSpecialBidReasons(specialBidReasonByIdsSet);
            getModelService().save(cartModel);
        }
    }

    public Converter<PartnerSpecialBidReasonModel, PartnerSpecialBidReasonData> getSpecialBidReasonConverter() {
        return specialBidReasonConverter;
    }

    public PartnerSpecialBidReasonService getSpecialBidReasonService() {
        return specialBidReasonService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public CartService getCartService() {
        return cartService;
    }

    public ModelService getModelService() {
        return modelService;
    }
}
