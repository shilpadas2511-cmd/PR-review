package com.ibm.commerce.partner.core.order.strategies.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.ibm.commerce.partner.core.order.services.PartnerProcessService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.servicelayer.user.UserService;
import java.time.Duration;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.LockedException;

@UnitTest
public class DefaultPartnerQuoteValidationStrategyTest {

    // Constants for test data
    private static final String TEST_QUOTE_CODE = "123";
    private static final String TEST_CART_CODE = "123";
    private static final String TEST_CUSTOMER_EMAIL = "test@example.com";

    private DefaultPartnerQuoteValidationStrategy strategy;

    @Mock
    private PartnerProcessService partnerProcessService;

    @Mock
    private UserService userService;

    @Mock
    private TimeService timeService;

    @Mock
    private ModelService modelService;

    @Mock
    private QuoteModel quoteModel;

    @Mock
    private CartModel cartModel;

    @Mock
    private UserModel userModel;

    @Mock
    private CustomerModel customerModel;

    @Mock
    private CustomerEmailResolutionService customerEmailResolutionService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        strategy = new DefaultPartnerQuoteValidationStrategy(
            partnerProcessService,
            customerEmailResolutionService,
            userService,
            30,
            timeService,
            modelService);
    }

    @Test
    public void testValidateQuoteEditBySiteIds_NullQuoteModel_ThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            strategy.validateQuoteEditBySiteIds(null, userModel, cartModel);
        });
        assertEquals("Parameter quoteModel can not be null", exception.getMessage());
    }

    @Test
    public void testValidateQuoteEditBySiteIds_NullUserModel_ThrowsException() {
        QuoteModel quoteModel = new QuoteModel();
        quoteModel.setCode(TEST_QUOTE_CODE);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            strategy.validateQuoteEditBySiteIds(quoteModel, null, cartModel);
        });
        assertEquals("Parameter userModel can not be null", exception.getMessage());
    }

    @Test
    public void testValidateQuoteEditBySiteIds_CanEditQuote_Success() {
        CartModel cartModel = new CartModel();
        cartModel.setCode(TEST_CART_CODE);
        cartModel.setUser(userModel);
        cartModel.setModifiedtime(new Date());

        QuoteModel quoteModel = new QuoteModel();
        quoteModel.setCode(TEST_QUOTE_CODE);
        quoteModel.setCartReference(cartModel);

        Date currentTime = new Date();
        when(timeService.getCurrentTime()).thenReturn(currentTime);
        when(partnerProcessService.checkQuoteCartProcessCompleted(cartModel)).thenReturn(true);

        strategy.validateQuoteEditBySiteIds(quoteModel, userModel, cartModel);
    }

    @Test
    public void testValidateQuoteEdit_AllParametersNull_ThrowsException() {
        assertThrows(IllegalArgumentException.class,
            () -> strategy.validateQuoteEdit(null, null, null));
    }

    @Test
    public void testValidateQuoteEdit_OneParameterNull_ThrowsException() {
        assertThrows(IllegalArgumentException.class,
            () -> strategy.validateQuoteEdit(quoteModel, null, cartModel));
        assertThrows(IllegalArgumentException.class,
            () -> strategy.validateQuoteEdit(null, userModel, cartModel));
        assertThrows(IllegalArgumentException.class,
            () -> strategy.validateQuoteEdit(quoteModel, userModel, null));
    }

    @Test
    public void testIsCartModifiedTimeExceedsThreshold_ExceedsThreshold() {
        Date modifiedTime = new Date(
            System.currentTimeMillis() - Duration.ofMinutes(31).toMillis());
        when(cartModel.getModifiedtime()).thenReturn(modifiedTime);
        when(timeService.getCurrentTime()).thenReturn(new Date());

        boolean result = strategy.isCartModifiedTimeExceedsThreshold(cartModel);
        assertTrue("Expected cart modified time to exceed threshold", result);
    }

    @Test
    public void testValidateQuoteEditBySiteIds_CannotEditQuote_ThrowsLockedException() {
        CartModel cartModel = new CartModel();
        cartModel.setCode(TEST_CART_CODE);
        cartModel.setUser(customerModel);
        cartModel.setModifiedtime(new Date());

        QuoteModel quoteModel = new QuoteModel();
        quoteModel.setCode(TEST_QUOTE_CODE);
        quoteModel.setCartReference(cartModel);

        Date currentTime = new Date();
        when(timeService.getCurrentTime()).thenReturn(currentTime);
        when(partnerProcessService.checkQuoteCartProcessCompleted(cartModel)).thenReturn(false);
        when(customerEmailResolutionService.getEmailForCustomer(customerModel)).thenReturn(
            TEST_CUSTOMER_EMAIL);

        DefaultPartnerQuoteValidationStrategy spyStrategy = spy(strategy);
        doReturn(false).when(spyStrategy).canEditQuote(quoteModel, cartModel, customerModel);

        LockedException exception = assertThrows(LockedException.class, () -> {
            spyStrategy.validateQuoteEditBySiteIds(quoteModel, customerModel, cartModel);
        });

        assertTrue(exception.getMessage().contains("This quote cannot be edited"));
    }
}
