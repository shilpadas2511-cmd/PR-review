package com.ibm.commerce.partner.core.order.hook;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import com.ibm.commerce.partner.core.order.strategies.ProductAdditionalInfoStrategy;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.hook.CommerceAddToCartMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import java.util.Objects;

/**
 * This method hook class is created to create CommerceRampUpData from the CommerceCartParameter and save it.
 */
public class ProductAdditionalInfoAddToCartMethodHook implements CommerceAddToCartMethodHook {

    private final ProductAdditionalInfoStrategy productAdditionalInfoStrategy;

    public ProductAdditionalInfoAddToCartMethodHook(
        ProductAdditionalInfoStrategy productAdditionalInfoStrategy) {
        this.productAdditionalInfoStrategy = productAdditionalInfoStrategy;
    }

    @Override
    public void beforeAddToCart(CommerceCartParameter parameters)
         {
           /*
           no implementation
            */
    }

    /**
     *
     * @param parameters The request data contains info needed to be sent for the CommerceCartParameter
     * @param result     The request data contains info needed to be sent for the CommerceCartModification
     * @throws CommerceCartModificationException throws CommerceCartModificationException
     */
    @Override
    public void afterAddToCart(CommerceCartParameter parameters, CommerceCartModification result)
        throws CommerceCartModificationException {
        validateParameterNotNullStandardMessage("parameters", parameters);
        if (parameters.isPartProduct() && result.getEntry() != null && Objects.nonNull(parameters.getCommerceRampUpData())) {
            getProductAdditionalInfoStrategy().addInfo(parameters,result);
        }
    }

    public ProductAdditionalInfoStrategy getProductAdditionalInfoStrategy() {
        return productAdditionalInfoStrategy;
    }
}
