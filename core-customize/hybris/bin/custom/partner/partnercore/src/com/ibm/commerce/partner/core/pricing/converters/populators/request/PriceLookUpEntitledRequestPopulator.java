package com.ibm.commerce.partner.core.pricing.converters.populators.request;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.order.price.data.request.EntitledPriceLookUpItemRequestData;
import com.ibm.commerce.partner.core.order.price.data.request.EntitledPriceLookUpRequestData;
import com.ibm.commerce.partner.core.order.price.data.request.FullPriceLookUpItemRequestData;
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
import org.apache.commons.collections4.CollectionUtils;

/**
 * Populator class responsible for populating EntitledPriceLookUpRequestData from an
 * AbstractOrderModel.
 */
public class PriceLookUpEntitledRequestPopulator implements
    Populator<AbstractOrderModel, EntitledPriceLookUpRequestData> {

    private Converter<AbstractOrderModel, PriceLookUpHeaderRequestData> priceLookUpHeaderEntitledDataConverter;

    private Converter<AbstractOrderEntryModel, EntitledPriceLookUpItemRequestData> priceLookUpItemsDataConverter;

    private final Set<String> ytyEligibleLicenceTypeCodes;

    private final SessionService sessionService;

    public PriceLookUpEntitledRequestPopulator(
        Converter<AbstractOrderModel, PriceLookUpHeaderRequestData> priceLookUpHeaderEntitledDataConverter,
        Converter<AbstractOrderEntryModel, EntitledPriceLookUpItemRequestData> priceLookUpItemsDataConverter,
        final Set<String> ytyEligibleLicenceTypeCodes,
        SessionService sessionService) {
        this.priceLookUpHeaderEntitledDataConverter = priceLookUpHeaderEntitledDataConverter;
        this.priceLookUpItemsDataConverter = priceLookUpItemsDataConverter;
        this.ytyEligibleLicenceTypeCodes = ytyEligibleLicenceTypeCodes;
        this.sessionService = sessionService;
    }

    /**
     * Populates the target EntitledPriceLookUpRequestData object with information extracted from
     * the source AbstractOrderModel.
     *
     * @param source the AbstractOrderModel containing the source data
     * @param target the EntitledPriceLookUpRequestData object to be populated
     * @throws ConversionException if an error occurs during conversion
     */
    @Override
    public void populate(AbstractOrderModel source, EntitledPriceLookUpRequestData target)
        throws ConversionException {
        if (source != null) {
            target.setHeader(getPriceLookUpHeaderEntitledDataConverter().convert(source));
            List<EntitledPriceLookUpItemRequestData> items = new ArrayList<>();
            source.getEntries().forEach(cartEntry -> {
                Map<String, AbstractOrderEntryModel> subIdToItemNumberMap = PartnerOrderUtils.getSubIdEntryNumber(
                    cartEntry,getYtyEligibleLicenceTypeCodes());
                sessionService.setAttribute(PartnercoreConstants.SUB_ID_TO_ITEM_NUMBER_MAP,
                    subIdToItemNumberMap);
                cartEntry.getChildEntries().forEach(childEntry -> {
                    items.add(getPriceLookUpItemsDataConverter().convert(childEntry));
                });
                sessionService.removeAttribute(PartnercoreConstants.SUB_ID_TO_ITEM_NUMBER_MAP);
            });
            target.setItems(items);
        }
    }

    public Converter<AbstractOrderEntryModel, EntitledPriceLookUpItemRequestData> getPriceLookUpItemsDataConverter() {
        return priceLookUpItemsDataConverter;
    }

    public Converter<AbstractOrderModel, PriceLookUpHeaderRequestData> getPriceLookUpHeaderEntitledDataConverter() {
        return priceLookUpHeaderEntitledDataConverter;
    }

    public Set<String> getYtyEligibleLicenceTypeCodes() {
        return ytyEligibleLicenceTypeCodes;
    }
}
