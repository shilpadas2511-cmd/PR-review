package com.ibm.commerce.partner.core.pricing.converters.populators.request;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.order.price.data.request.FullPriceLookUpItemRequestData;
import com.ibm.commerce.partner.core.order.price.data.request.FullPriceLookUpRequestData;
import com.ibm.commerce.partner.core.order.price.data.request.PriceLookUpHeaderRequestData;
import com.ibm.commerce.partner.core.utils.PartnerOrderUtils;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.session.SessionService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.lang.NonNull;

/**
 * Populator class responsible for populating FullPriceLookUpRequestData from an AbstractOrderModel.
 * This class extracts relevant information from the order and populates the target data object for
 * full price lookup requests.
 */
public class PriceLookUpFullRequestPopulator implements
    Populator<AbstractOrderModel, FullPriceLookUpRequestData> {


    private Converter<AbstractOrderModel, PriceLookUpHeaderRequestData> priceLookUpHeaderFullDataConverter;

    private Converter<AbstractOrderEntryModel, FullPriceLookUpItemRequestData> priceLookUpItemsDataConverter;
    private final SessionService sessionService;
    private final Set<String> ytyEligibleLicenceTypeCodes;

    public PriceLookUpFullRequestPopulator(
        Converter<AbstractOrderModel, PriceLookUpHeaderRequestData> priceLookUpHeaderFullDataConverter,
        Converter<AbstractOrderEntryModel, FullPriceLookUpItemRequestData> priceLookUpItemsDataConverter,
        SessionService sessionService, final Set<String> ytyEligibleLicenceTypeCodes) {
        this.priceLookUpHeaderFullDataConverter = priceLookUpHeaderFullDataConverter;
        this.priceLookUpItemsDataConverter = priceLookUpItemsDataConverter;
        this.sessionService = sessionService;
        this.ytyEligibleLicenceTypeCodes = ytyEligibleLicenceTypeCodes;
    }

    /**
     * Populates the target FullPriceLookUpRequestData object with information extracted from the
     * source AbstractOrderModel.
     *
     * @param source the AbstractOrderModel containing the source data
     * @param target the FullPriceLookUpRequestData object to be populated
     * @throws ConversionException if an error occurs during conversion
     */
    @Override
    public void populate(@NonNull AbstractOrderModel source,
        @NonNull FullPriceLookUpRequestData target) throws ConversionException {

        target.setHeader(getPriceLookUpHeaderFullDataConverter().convert(source));
        final List<FullPriceLookUpItemRequestData> childEntryItems = new ArrayList<>();
        source.getEntries().forEach(cartEntry -> {
            Map<String, AbstractOrderEntryModel> subIdToItemNumberMap = PartnerOrderUtils.getSubIdEntryNumber(
                cartEntry, getYtyEligibleLicenceTypeCodes());
            sessionService.setAttribute(PartnercoreConstants.SUB_ID_TO_ITEM_NUMBER_MAP,
                subIdToItemNumberMap);
            cartEntry.getChildEntries().forEach(childEntry -> {
                childEntryItems.add(getPriceLookUpItemsDataConverter().convert(childEntry));
            });
            sessionService.removeAttribute(PartnercoreConstants.SUB_ID_TO_ITEM_NUMBER_MAP);
        });
        target.setItems(childEntryItems);
    }

    public Converter<AbstractOrderEntryModel, FullPriceLookUpItemRequestData> getPriceLookUpItemsDataConverter() {
        return priceLookUpItemsDataConverter;
    }

    public Converter<AbstractOrderModel, PriceLookUpHeaderRequestData> getPriceLookUpHeaderFullDataConverter() {
        return priceLookUpHeaderFullDataConverter;
    }

    public Set<String> getYtyEligibleLicenceTypeCodes() {
        return ytyEligibleLicenceTypeCodes;
    }
}
