package com.ibm.commerce.partner.core.provisionform.convertors.populators;


import com.ibm.commerce.common.core.model.IbmPartProductModel;
import com.ibm.commerce.common.core.model.IbmVariantProductModel;
import com.ibm.commerce.common.core.model.PartnerProductSetModel;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.quote.provision.form.data.request.ProvisionFormItemDetailsRequestData;
import com.ibm.commerce.partner.core.quote.provision.form.data.request.ProvisionFormItemsRequestData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.session.SessionService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * ProvisionFormItemDetailsRequestPopulator. It used to prepare the provision form request object
 * with items information.
 */
public class ProvisionFormItemDetailsRequestPopulator implements
    Populator<AbstractOrderModel, ProvisionFormItemsRequestData> {


    private final SessionService sessionService;
    private final Map<String, PartnerProductSetModel> partnerProductSets = new HashMap<>();

    private ConfigurationService configurationService;

    public ProvisionFormItemDetailsRequestPopulator(SessionService sessionService,
        ConfigurationService configurationService) {
        this.sessionService = sessionService;
        this.configurationService = configurationService;
    }

    /*
     * populate the cart model object to ProvisionFormItemsRequestData object.
     * @param  source
     * @param target
     * */
    @Override
    public void populate(AbstractOrderModel source, ProvisionFormItemsRequestData target)
        throws ConversionException {
        if (source instanceof IbmPartnerCartModel cartModel) {
            prepareItemDetailsQuote(cartModel, target);
        }
    }

    /*
     * prepare the item details for request
     * @param  cartModel
     * @param target
     * */
    protected void prepareItemDetailsQuote(IbmPartnerCartModel cartModel,
        ProvisionFormItemsRequestData target) {
        List<ProvisionFormItemDetailsRequestData> itemList = cartModel.getEntries().stream()
            .flatMap(entryModel -> entryModel.getChildEntries().stream())
            .map(this::createItemDetailsRequestData)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        target.setItems(itemList);
    }

    protected ProvisionFormItemDetailsRequestData createItemDetailsRequestData(
        AbstractOrderEntryModel childEntry) {
        if (isValidPartProduct(childEntry)) {
            return createItemDetails(childEntry);
        }
        return null;
    }


    /*
     * check product type as  Saas Product or not
     * @param  childEntry
     * @return boolean
     * */
    protected boolean isValidPartProduct(AbstractOrderEntryModel childEntry) {

        if (getConfigurationService().getConfiguration()
            .getBoolean(PartnercoreConstants.PROVISIONING_FORMS_FEATURE_FLAG, false)) {
            if (childEntry.getMasterEntry()
                .getProduct() instanceof IbmVariantProductModel pidProduct
                && PartnercoreConstants.DEPLOYMENT_TYPE_SAAS_SAPCODE.equalsIgnoreCase(
                pidProduct.getDeploymentType().getSapCode())) {
                return true;
            }
        }
        return false;
    }

    protected ProvisionFormItemDetailsRequestData createItemDetails(
        AbstractOrderEntryModel childEntry) {
        IbmPartProductModel partProductModel = (IbmPartProductModel) childEntry.getProduct();
        IbmVariantProductModel pidProduct = (IbmVariantProductModel) childEntry.getMasterEntry()
            .getProduct();
        ProvisionFormItemDetailsRequestData itemDetails = new ProvisionFormItemDetailsRequestData();

        itemDetails.setPartNumber(partProductModel.getCode());
        itemDetails.setDescription(partProductModel.getDescription());
        itemDetails.setQuantity(childEntry.getQuantity().intValue());

        setAdditionalProductDetails(partProductModel, itemDetails, pidProduct);

        storeProductSetCode(partProductModel);

        return itemDetails;
    }

    protected void setAdditionalProductDetails(IbmPartProductModel partProductModel,
        ProvisionFormItemDetailsRequestData itemDetails, IbmVariantProductModel pidProduct) {
        itemDetails.setPid(pidProduct.getPartNumber());
        itemDetails.setPidDescription(pidProduct.getDescription());

        if (partProductModel.getProductSetCode() != null) {
            itemDetails.setProductSetCode(partProductModel.getProductSetCode().getCode());
        }
    }

    protected void storeProductSetCode(IbmPartProductModel partProductModel) {
        if (partProductModel.getProductSetCode() != null) {
            partnerProductSets.put(partProductModel.getProductSetCode().getCode(),
                partProductModel.getProductSetCode());
            sessionService.setAttribute("partnerSetCodes", partnerProductSets);
        }
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }
}
