package com.ibm.commerce.partner.facades.strategies.impl;


import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.commerceservices.order.CommerceSaveCartService;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.CartException;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.selectivecartfacades.strategies.impl.SelectiveCartLoaderStrategy;
import de.hybris.platform.selectivecartservices.SelectiveCartService;
import de.hybris.platform.selectivecartservices.order.impl.DefaultSelectiveCartFactory;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.LockedException;

import com.ibm.commerce.partner.core.cart.services.impl.DefaultIbmCommerceCartService;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultIbmCartLoaderStrategyTest {

	@InjectMocks
    DefaultIbmCartLoaderStrategy defaultIbmCartLoaderStrategy;

    @Mock
    SelectiveCartLoaderStrategy selectiveCartLoaderStrategy;
	@Mock
    DefaultIbmCommerceCartService commerceCartService;
    @Mock
    UserService userService;
    @Mock
    SelectiveCartService selectiveCartService;
    @Mock
    DefaultSelectiveCartFactory selectiveCartFactory;
    @Mock
    CommerceSaveCartService commerceSaveCartService;
    @Mock
    CartModel cartModel;
    @Mock
    private CartService cartService;
    @Mock
    private BaseSiteService baseSiteService;
    @Mock
    private ConfigurationService configurationService;
    @Mock
    private Configuration configuration;
    @Mock
    CustomerModel customerModel;
    @Mock
    BaseSiteModel baseSiteModel;
    @Mock
    IbmPartnerQuoteModel quoteModel;
    @Mock
    CustomerModel userModel;
    @Mock
    private CustomerEmailResolutionService customerEmailResolutionService;


    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        lenient().when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSiteModel);
		lenient().when(baseSiteModel.getUid()).thenReturn("site123");
		lenient().when(userModel.getCustomerID()).thenReturn("customer123");
		lenient().when(userService.getCurrentUser()).thenReturn(userModel);
        lenient().when(configurationService.getConfiguration()).thenReturn(configuration);
        defaultIbmCartLoaderStrategy = new DefaultIbmCartLoaderStrategy(userService,
            selectiveCartFactory,
            selectiveCartService, commerceSaveCartService, commerceCartService,
            configurationService, customerEmailResolutionService);
        defaultIbmCartLoaderStrategy.setCartService(cartService);
        defaultIbmCartLoaderStrategy.setUserService(userService);
        defaultIbmCartLoaderStrategy.setBaseSiteService(baseSiteService);
        defaultIbmCartLoaderStrategy.setCommerceCartService(commerceCartService);

    }

    @Test
    public void testIsQuoteBeingEdited() {
        final String cartID = "testCartID";
        final QuoteModel mockQuote = mock(QuoteModel.class);
        final UserModel mockUser = mock(UserModel.class);
        when(commerceCartService.getCartModelForCodeAndSiteIds(eq(cartID), any(), any()))
            .thenReturn(cartModel);
        when(cartModel.getQuoteReference()).thenReturn(mockQuote);
        when(cartModel.getUser()).thenReturn(mockUser);
        when(mockUser.getUid()).thenReturn("testUser");

        assertThrows(LockedException.class,
            () -> defaultIbmCartLoaderStrategy.isQuoteBeingEdited(cartID));
    }

    @Test
    public void testIsQuoteBeingEdited_NoQuoteReference() {
        final String cartID = "testCartID";
        when(
            commerceCartService.getCartModelForCodeAndSiteIds(eq(cartID), any(), any())).thenReturn(
            cartModel);
        when(cartModel.getQuoteReference()).thenReturn(null);

        defaultIbmCartLoaderStrategy.isQuoteBeingEdited(cartID);
    }

    @Test
   public void testGetConfigurationService(){
        configurationService= defaultIbmCartLoaderStrategy.getConfigurationService();

   }

	@Test
	public void testLoadUserCart_whenCartExceptionAndFeatureFlagEnabled_shouldThrowCartException() throws NullPointerException
	{
		final String cartId = "regularCart123";
		final String selectiveCartId = "selectivecartsite123customer123";
		lenient().when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSiteModel);
		lenient().when(baseSiteModel.getUid()).thenReturn("site123");
		lenient().when(userModel.getCustomerID()).thenReturn("customer123");
		lenient().when(userService.getCurrentUser()).thenReturn(userModel);
		lenient().when(configurationService.getConfiguration()).thenReturn(configuration);
		final DefaultIbmCartLoaderStrategy spyStrategy = Mockito.spy(defaultIbmCartLoaderStrategy);

		final CartException ex = assertThrows(CartException.class, () -> spyStrategy.loadUserCart(cartId, true));

        lenient().doNothing().when(spyStrategy).loadUserCart(anyString(), anyBoolean());

	}

}