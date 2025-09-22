package com.ibm.commerce.common.facades.product.converters.populators;

import com.ibm.commerce.common.core.model.IbmVariantProductModel;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.springframework.lang.NonNull;

/**
 * Populates the Product Details in ProductData
 */
public class IbmPidProductDetailsPopulator implements Populator<ProductModel, ProductData> {

    @Override
    public void populate(@NonNull final ProductModel source, @NonNull final ProductData target)
        throws ConversionException {

        if (source instanceof IbmVariantProductModel variantProduct) {
            target.setConfiguratorCode(variantProduct.getConfiguratorCode());
            target.setPartNumber(variantProduct.getPartNumber());
        }
    }
}
