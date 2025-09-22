package com.ibm.commerce.partner.core.util.model;

import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.site.BaseSiteService;
import java.util.Set;

public class CommerceCartParameterTestDataGenerator {

    public static CommerceCartParameter createCommerceCartParamter(final CartModel cartModel, final PriceData basePrice, final PriceData totalPrice) {
        CommerceCartParameter commerceCartParameter = new CommerceCartParameter();
        commerceCartParameter.setCart(cartModel);
        return commerceCartParameter;
    }

    public static CommerceCartParameter createCommerceCartParamterModel(final IbmPartnerCartModel cartModel) {
        CommerceCartParameter commerceCartParameter = new CommerceCartParameter();
        commerceCartParameter.setCart(cartModel);
        return commerceCartParameter;

    }

    public static CommerceCartParameter createCommerceCartParameter(final CartModel cartModel, final String pid, final Long entryNumber) {
        CommerceCartParameter commerceCartParameter = new CommerceCartParameter();
        commerceCartParameter.setCart(cartModel);
        commerceCartParameter.setPidId(pid);
        commerceCartParameter.setEntryNumber(entryNumber);
        return commerceCartParameter;
    }

    public static CommerceCartParameter createCommerceCartParamterModel() {
        CommerceCartParameter commerceCartParameter = new CommerceCartParameter();
        return commerceCartParameter;
    }

    public static CommerceCartParameter createCommerceCartParameter(String configId, boolean isPartProduct) {
        CommerceCartParameter commerceCartParameter = new CommerceCartParameter();
        commerceCartParameter.setConfigId(configId);
        commerceCartParameter.setPartProduct(isPartProduct);
        return commerceCartParameter;
    }

    public static CommerceCartParameter updateCommerceCartParameter(final ProductModel productModel) {
        CommerceCartParameter commerceCartParameter = new CommerceCartParameter();
        commerceCartParameter.setProduct(productModel);
        return commerceCartParameter;
    }

    public static CommerceCartParameter prepareCommerceCartParameter(boolean isPartProduct, Set<Integer> entryGroupNumbers) {
        CommerceCartParameter commerceCartParameter = new CommerceCartParameter();
        commerceCartParameter.setPartProduct(isPartProduct);
        commerceCartParameter.setEntryGroupNumbers(entryGroupNumbers);
        return commerceCartParameter;
    }
}
