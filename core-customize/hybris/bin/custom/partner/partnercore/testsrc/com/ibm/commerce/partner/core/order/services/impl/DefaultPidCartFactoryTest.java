package com.ibm.commerce.partner.core.order.services.impl;

import static org.mockito.Mockito.when;

import com.ibm.commerce.partner.core.quote.services.PartnerSapCpqQuoteService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.strategies.NetGrossStrategy;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserNetCheckingStrategy;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;


/**
 * Test class for {@link DefaultPidCartFactory}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPidCartFactoryTest {

    private static final String GUID = "0abc00edf000";
    private static final String CART_CODE = "000000";

    private static String PID_CART_CODE = "pidCartCode";

    @InjectMocks
    DefaultPidCartFactory defaultPidCartFactory;

    @Mock
    private NetGrossStrategy netGrossStrategy;
    @Mock
    private BaseSiteService baseSiteService;
    @Mock
    private BaseStoreService baseStoreService;
    @Mock
    private KeyGenerator guidKeyGenerator;
    @Mock
    private ApplicationContext ctx;
    @Mock
    private KeyGenerator keyGenerator;
    @Mock
    private ModelService modelService;
    @Mock
    private UserService userService;
    @Mock
    private CommonI18NService commonI18NService;
    @Mock
    private UserNetCheckingStrategy userNetCheckingStrategy;
    @Mock
    private UserModel user;
    @Mock
    private CartModel cart;
    private String cartType;
    @Mock
    KeyGenerator pidQuoteKeyGenerator;
    @Mock
    private PartnerSapCpqQuoteService partnerSapCpqQuoteService;

    @Mock
    private ConfigurationService configurationService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        defaultPidCartFactory = new DefaultPidCartFactory(netGrossStrategy, baseSiteService,
            baseStoreService, guidKeyGenerator, userService, modelService, keyGenerator,
            commonI18NService, userNetCheckingStrategy, cartType, pidQuoteKeyGenerator,partnerSapCpqQuoteService,configurationService);
        when(userService.getCurrentUser()).thenReturn(user);
        when(modelService.create("Cart")).thenReturn(cart);
        when(keyGenerator.generate()).thenReturn(CART_CODE);
        when(guidKeyGenerator.generate()).thenReturn(GUID);
    }

    @Test
    public void testCreateCart() {
        final CartModel resultCart = defaultPidCartFactory.createCart(PID_CART_CODE);
        Assert.assertNotNull(resultCart);
        Assert.assertEquals(cart, resultCart);
    }

    @Test
    public void testCreateCartInternal() {
        final CartModel resultCart = defaultPidCartFactory.createCartInternal();
        Assert.assertNotNull(resultCart);
        Assert.assertEquals(cart, resultCart);
    }

    @Test
    public void testGetCtx() {
        defaultPidCartFactory.setCtx(ctx);
        Assert.assertEquals(ctx, defaultPidCartFactory.getCtx());
    }
}
