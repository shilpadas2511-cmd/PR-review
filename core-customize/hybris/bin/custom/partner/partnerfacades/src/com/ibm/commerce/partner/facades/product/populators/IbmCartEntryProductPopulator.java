package com.ibm.commerce.partner.facades.product.populators;

import com.ibm.commerce.common.core.model.IbmDeploymentTypeModel;
import com.ibm.commerce.common.core.model.IbmPartProductModel;
import com.ibm.commerce.common.core.model.IbmVariantProductModel;
import com.ibm.commerce.partner.core.enums.PartProductType;
import com.ibm.commerce.partner.enums.data.DisplayTypeData;
import com.ibm.commerce.partner.facades.product.data.IbmDeploymentTypeData;
import com.ibm.commerce.partner.facades.strategies.impl.PartnerPartProductTypeStrategy;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import java.util.Locale;
import java.util.Objects;

/**
 * populating product data inside the cart entry
 */
public class IbmCartEntryProductPopulator implements
    Populator<ProductModel, ProductData> {

    private final PartnerPartProductTypeStrategy partnerPartProductTypeStrategy;
    private final Converter<HybrisEnumValue, DisplayTypeData> displayTypeDataConverter;
    private final CommerceCommonI18NService commerceCommonI18NService;

    public IbmCartEntryProductPopulator(
        PartnerPartProductTypeStrategy partnerPartProductTypeStrategy,
        Converter<HybrisEnumValue, DisplayTypeData> displayTypeDataConverter,
        CommerceCommonI18NService commerceCommonI18NService) {
        this.partnerPartProductTypeStrategy = partnerPartProductTypeStrategy;
        this.displayTypeDataConverter = displayTypeDataConverter;
        this.commerceCommonI18NService = commerceCommonI18NService;
    }



    /**
     * @param productModel the source object
     * @param productData  the target to fill
     * @throws ConversionException
     */
    @Override
    public void populate(ProductModel productModel, ProductData productData)
        throws ConversionException {

        Locale locale = getCommerceCommonI18NService().getCurrentLocale();
        productData.setDescription(productModel.getDescription(locale));
        if (productModel instanceof IbmPartProductModel partProduct) {
            productData.setSapMaterialCode(partProduct.getSapMaterialCode());
            productData.setDeploymentType(getDeploymentType(partProduct.getDeploymentType()));
            PartProductType productType = getPartnerPartProductTypeStrategy().getProductType(
                partProduct);
            if (Objects.nonNull(productType)) {
                productData.setType(getDisplayTypeDataConverter().convert(productType));
            }
        }
        if (productModel instanceof IbmVariantProductModel variantProduct) {
            productData.setDeploymentType(getDeploymentType(variantProduct.getDeploymentType()));
        }

    }

    private IbmDeploymentTypeData getDeploymentType(IbmDeploymentTypeModel ibmDeploymentTypeModel) {
        IbmDeploymentTypeData deploymentTypeData = new IbmDeploymentTypeData();
        deploymentTypeData.setCode(ibmDeploymentTypeModel.getCode());
        deploymentTypeData.setName(ibmDeploymentTypeModel.getName());
        return deploymentTypeData;
    }

    public CommerceCommonI18NService getCommerceCommonI18NService() {
        return commerceCommonI18NService;
    }

    public PartnerPartProductTypeStrategy getPartnerPartProductTypeStrategy() {
        return partnerPartProductTypeStrategy;
    }

    public Converter<HybrisEnumValue, DisplayTypeData> getDisplayTypeDataConverter() {
        return displayTypeDataConverter;
    }
    
}
