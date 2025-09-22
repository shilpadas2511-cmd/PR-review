package com.ibm.commerce.partner.core.pricing.converters.populators.response;

import com.ibm.commerce.common.core.product.service.IbmProductService;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.order.price.data.response.DealRegResponseData;
import com.ibm.commerce.partner.core.services.PriceLookUpService;
import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.sap.productconfig.services.model.CPQOrderEntryProductInfoModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Populator to map Deal Registration response data to CPQOrderEntryProductInfoModel.
 */
public class DealRegResponsePopulator implements
    Populator<List<DealRegResponseData>, AbstractOrderModel> {

    private ModelService modelService;

    private final IbmProductService productService;

    /**
     * Constructs a DealRegResponsePopulator with the specified dependencies.
     *
     * @param priceLookUpService Service for performing price lookup operations.
     * @param modelService       Service for interacting with model objects.
     */
    public DealRegResponsePopulator(
        ModelService modelService, final IbmProductService productService) {
        this.modelService = modelService;
        this.productService = productService;
    }

    /**
     * Populates CPQOrderEntryProductInfoModel based on Deal Registration response data.
     *
     * @param dealRegResponses List of DealRegResponseData.
     * @param orderModel       The order model to populate.
     * @throws ConversionException if an error occurs during conversion.
     */
    @Override
    public void populate(List<DealRegResponseData> dealRegResponses, AbstractOrderModel orderModel)
        throws ConversionException {

        orderModel.getEntries().forEach(entry -> entry.getChildEntries().forEach(childEntry -> {
            Optional<DealRegResponseData> deal = dealRegResponses.stream().filter(
                response -> getProductService().getProductCode(childEntry.getProduct())
                    .equals(response.getPartNum())).findAny();
            deal.ifPresent(response -> createCpqOrderEntryProductInfoModel(childEntry, response));
        }));

    }

    /**
     * Creates CPQOrderEntryProductInfoModel based on Deal Registration response data and saves it.
     *
     * @param childEntry The child entry model.
     * @param deal       The DealRegResponseData.
     */
    protected void createCpqOrderEntryProductInfoModel(AbstractOrderEntryModel childEntry,
        DealRegResponseData deal) {
        Map<String, String> dealResponseMap = createMap(deal);
        dealResponseMap.entrySet().forEach(entry -> {
            Optional<CPQOrderEntryProductInfoModel> optionalCPQOrderEntryProductInfoModel = childEntry.getProductInfos()
                .stream().filter(CPQOrderEntryProductInfoModel.class::isInstance)
                .map(CPQOrderEntryProductInfoModel.class::cast)
                .filter(info -> entry.getKey().equals(info.getCpqCharacteristicName())).findAny();
            CPQOrderEntryProductInfoModel productInfo = optionalCPQOrderEntryProductInfoModel.orElseGet(
                () -> createProductInfo(entry.getKey(), childEntry));
            productInfo.setCpqCharacteristicAssignedValues(entry.getValue());
            getModelService().save(productInfo);
        });
    }

    /**
     * Creates a new CPQOrderEntryProductInfoModel instance.
     *
     * @param key        The characteristic name.
     * @param childEntry The child entry model.
     * @return The created CPQOrderEntryProductInfoModel.
     */
    private CPQOrderEntryProductInfoModel createProductInfo(String key,
        AbstractOrderEntryModel childEntry) {
        CPQOrderEntryProductInfoModel productInfo = new CPQOrderEntryProductInfoModel();
        productInfo.setCpqCharacteristicName(key);
        productInfo.setConfiguratorType(ConfiguratorType.CPQCONFIGURATOR);
        productInfo.setOrderEntry(childEntry);
        return productInfo;
    }

    /**
     * Creates a JSON object with initial Deal Registration response data.
     *
     * @param deal The DealRegResponseData.
     * @return The JSONObject containing initial data.
     */
    private Map<String, String> createMap(DealRegResponseData deal) {
        Map<String, String> spSys = new HashMap<>(5);
        spSys.put(PartnercoreConstants.ORDER_ENTRY_DEAL_REG_KEY_DEAL_REG_FLAG,
            deal.getDealRegFlag());
        spSys.put(PartnercoreConstants.ORDER_ENTRY_DEAL_REG_KEY_DEAL_REG_ID, deal.getDealRegId());
        spSys.put(PartnercoreConstants.ORDER_ENTRY_DEAL_REG_KEY_DEAL_REG_GROUP,
            deal.getDealRegGrp());
        spSys.put(PartnercoreConstants.ORDER_ENTRY_DEAL_REG_KEY_DEAL_REG_END_DATE,
            deal.getEndDate());
        return spSys;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public IbmProductService getProductService() {
        return productService;
    }
}
