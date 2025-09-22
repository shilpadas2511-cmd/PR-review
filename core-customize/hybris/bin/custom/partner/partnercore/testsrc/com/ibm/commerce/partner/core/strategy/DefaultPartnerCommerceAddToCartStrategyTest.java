package com.ibm.commerce.partner.core.strategy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceAddToCartStrategy;
import de.hybris.platform.util.Config;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.selectivecartservices.strategies.SelectiveCartAddToCartStrategy;


@UnitTest
class DefaultPartnerCommerceAddToCartStrategyTest {

    @InjectMocks
    private DefaultPartnerCommerceAddToCartStrategy strategy;

    @Mock
    private SelectiveCartAddToCartStrategy selectiveCartAddToCartStrategy;

    @Mock
    private CommerceCartParameter parameter;

    @Mock
    private CommerceCartModification modification;

    private DefaultPartnerCommerceAddToCartStrategy spyStrategy;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        spyStrategy = Mockito.spy(strategy);
    }

    @Test
    void testAddToCart_WhenWishListDisabled() throws CommerceCartModificationException {
        // Stub parent class (DefaultCommerceAddToCartStrategy) behavior
        Mockito.doReturn(modification).when((DefaultCommerceAddToCartStrategy) spyStrategy)
            .addToCart(parameter);
        CommerceCartModification result = spyStrategy.addToCart(parameter);
        Assertions.assertEquals(modification, result);
        Mockito.verify((DefaultCommerceAddToCartStrategy) spyStrategy, Mockito.times(1))
            .addToCart(parameter);
        Mockito.verify(selectiveCartAddToCartStrategy, Mockito.never())
            .addToCart(Mockito.any(CommerceCartParameter.class));
    }

    @Test
    void testAddToCart_WhenWishListEnabled() throws CommerceCartModificationException {
        try (MockedStatic<Config> mockedConfig = Mockito.mockStatic(Config.class)) {
            // Wishlist enabled
            mockedConfig.when(() -> Config.getBoolean("partner.wish.list.enabled", false))
                .thenReturn(true);
            // Ensure spy returns our mocked selectiveCart strategy
            Mockito.doReturn(selectiveCartAddToCartStrategy).when(spyStrategy)
                .getSelectiveCartAddToCartStrategy();

            // Stub selectiveCart strategy
            Mockito.when(selectiveCartAddToCartStrategy.addToCart(parameter))
                .thenReturn(modification);
            CommerceCartModification result = spyStrategy.addToCart(parameter);
            Assertions.assertEquals(modification, result);
            Mockito.verify(selectiveCartAddToCartStrategy, Mockito.times(1)).addToCart(parameter);
        }
    }

    @Test
    void testGetSelectiveCartAddToCartStrategy() {
        Assertions.assertEquals(selectiveCartAddToCartStrategy,
            strategy.getSelectiveCartAddToCartStrategy());
    }
}
