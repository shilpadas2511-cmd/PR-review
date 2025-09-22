package com.ibm.commerce.partner.facades.populators;

import de.hybris.platform.commercefacades.order.data.AddToCartParams;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.session.SessionService;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * populator to populate custom attribute created in entryGroupinfo
 */
public class PartnerCommerceCartParameterPopulator implements
    Populator<AddToCartParams, CommerceCartParameter> {

    private final SessionService sessionService;
    private final ProductService productService;

    public PartnerCommerceCartParameterPopulator(final SessionService sessionService,
        final ProductService productService) {
        this.sessionService = sessionService;
        this.productService = productService;
    }

    /**
     * @param addToCartParams       the source object
     * @param commerceCartParameter the target to fill
     * @throws ConversionException
     */
    @Override
    public void populate(AddToCartParams addToCartParams,
        CommerceCartParameter commerceCartParameter) throws ConversionException {
        commerceCartParameter.setPartProduct(addToCartParams.isPartProduct());
        commerceCartParameter.setConfigurationInfos(addToCartParams.getConfigurationInfos());
        commerceCartParameter.setConfigId(addToCartParams.getConfigId());
        commerceCartParameter.setCreateNewEntry(addToCartParams.isCreateNewEntry());
        if (StringUtils.isNotEmpty(addToCartParams.getPidId())) {
            commerceCartParameter.setPidId(addToCartParams.getPidId());
            CartModel partCart = getSessionService().getAttribute("partCart");
            if (Objects.nonNull(partCart)) {
                commerceCartParameter.setCart(partCart);
            }
        }
        if (StringUtils.isNotEmpty(addToCartParams.getConfiguratorPidId())) {
            final ProductModel productForCode = getProductService().getProductForCode(
                addToCartParams.getConfiguratorPidId());
            commerceCartParameter.setConfiguratorPid(productForCode);
        }
        if (Objects.nonNull(addToCartParams.getEntryStatus())) {
            commerceCartParameter.setEntryStatus(addToCartParams.getEntryStatus());
    }
        if  (Objects.nonNull (addToCartParams.getErrorDetails()) && StringUtils.isNotEmpty(addToCartParams.getErrorDetails().getDescription())) {
            commerceCartParameter.setErrorDetails(addToCartParams.getErrorDetails());
        }
        if(Objects.nonNull(addToCartParams.getCommerceRampUpData())){
            commerceCartParameter.setCommerceRampUpData(addToCartParams.getCommerceRampUpData());
        }
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    public ProductService getProductService() {
        return productService;
    }
}
