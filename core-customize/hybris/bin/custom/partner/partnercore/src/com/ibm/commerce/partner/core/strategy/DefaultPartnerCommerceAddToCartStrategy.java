package com.ibm.commerce.partner.core.strategy;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceAddToCartStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.selectivecartservices.strategies.SelectiveCartAddToCartStrategy;
import de.hybris.platform.util.Config;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom {@link DefaultCommerceAddToCartStrategy} implementation for partner commerce.
 * <p>
 * This strategy replaces the default {@link SelectiveCartAddToCartStrategy} behavior
 * when the wishlist feature is not required, in order to avoid unnecessary performance
 * overhead caused by multiple database hits SCFP-9094
 */
public class DefaultPartnerCommerceAddToCartStrategy extends DefaultCommerceAddToCartStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(
        DefaultPartnerCommerceAddToCartStrategy.class);

    @Resource(name = "selectiveCartAddToCartStrategy")
    private SelectiveCartAddToCartStrategy selectiveCartAddToCartStrategy;

    /**
     * Adds a product to the cart.
     * <p>
     * This implementation conditionally delegates the add-to-cart operation
     * based on the <code>partner.wish.list.enabled</code> configuration flag:
     * </p>
     * <ul>
     *   <li>If the wishlist feature is <b>disabled</b>, the default
     *       {@link #addToCart(CommerceCartParameter)} from the parent strategy is used.</li>
     *   <li>If the wishlist feature is <b>enabled</b>, the
     *       {@link #getSelectiveCartAddToCartStrategy()} strategy is used instead.</li>
     * </ul>
     *
     * @param parameter the cart parameter containing product, quantity, and user context
     * @return the result of the add-to-cart operation, including status and modifications
     * @throws CommerceCartModificationException if the add-to-cart operation fails
     */
    @Override
    public CommerceCartModification addToCart(final CommerceCartParameter parameter)
        throws CommerceCartModificationException {
        Boolean wishListEnabled = Config.getBoolean("partner.wish.list.enabled", Boolean.FALSE);
        if (!wishListEnabled) {
            return super.addToCart(parameter);
        } else {
            return getSelectiveCartAddToCartStrategy().addToCart(
                parameter);
        }
    }

    public SelectiveCartAddToCartStrategy getSelectiveCartAddToCartStrategy() {
        return selectiveCartAddToCartStrategy;
    }
}
