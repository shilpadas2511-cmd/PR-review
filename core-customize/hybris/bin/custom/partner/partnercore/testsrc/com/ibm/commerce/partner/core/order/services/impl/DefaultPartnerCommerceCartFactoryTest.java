package com.ibm.commerce.partner.core.order.services.impl;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import com.ibm.commerce.partner.core.exceptions.IbmWebServiceFailureException;
import com.ibm.commerce.partner.core.outboundservices.quote.data.response.PartnerCPQCreateQuoteResponseData;
import com.ibm.commerce.partner.core.quote.services.PartnerSapCpqQuoteService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.strategies.NetGrossStrategy;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserNetCheckingStrategy;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.tx.Transaction;
import de.hybris.platform.util.Config;

import org.apache.commons.configuration.Configuration;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;

/**
 * Test class for {@link DefaultPartnerCommerceCartFactory}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPartnerCommerceCartFactoryTest {

    private static final String GUID = "0abc00edf000";
    private static final String CART_CODE = "000000";

    @InjectMocks
    private DefaultPartnerCommerceCartFactory defaultPartnerCommerceCartFactory;

    @Mock
    private NetGrossStrategy netGrossStrategy;
    @Mock
    private BaseSiteService baseSiteService;
    @Mock
    private BaseStoreService baseStoreService;
    @Mock
    private KeyGenerator guidKeyGenerator;
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
    private IbmPartnerCartModel ibmPartnerCart;
    @Mock
    private KeyGenerator quoteCodeKeyGenerator;
    @Mock
    private CartModel cart;
    @Mock
    private ConfigurationService configurationService;
    @Mock
    private Configuration configuration;

    @Mock
    private PartnerSapCpqQuoteService partnerSapCpqQuoteService;

    private MockedStatic<Config> configMock;

    @Before
    public void setUp() {
        configurationService = mock(ConfigurationService.class);
        configMock = mockStatic(Config.class);
        configMock.when(() -> Config.getString(JaloSession.CART_TYPE, "Cart")).thenReturn("Cart");
        when(configurationService.getConfiguration()).thenReturn(configuration);
        defaultPartnerCommerceCartFactory = new DefaultPartnerCommerceCartFactory(
            quoteCodeKeyGenerator, modelService, partnerSapCpqQuoteService, configurationService);
        defaultPartnerCommerceCartFactory.setNetGrossStrategy(netGrossStrategy);
        defaultPartnerCommerceCartFactory.setBaseSiteService(baseSiteService);
        defaultPartnerCommerceCartFactory.setBaseStoreService(baseStoreService);
        defaultPartnerCommerceCartFactory.setGuidKeyGenerator(guidKeyGenerator);
        defaultPartnerCommerceCartFactory.setKeyGenerator(keyGenerator);
        defaultPartnerCommerceCartFactory.setModelService(modelService);
        defaultPartnerCommerceCartFactory.setUserService(userService);
        defaultPartnerCommerceCartFactory.setCommonI18NService(commonI18NService);
        defaultPartnerCommerceCartFactory.setUserNetCheckingStrategy(userNetCheckingStrategy);
        when(defaultPartnerCommerceCartFactory.getQuoteCodeKeyGenerator().generate()).thenReturn(
            CART_CODE);
        when(guidKeyGenerator.generate()).thenReturn(GUID);
    }

    @After
    public void closeStaticMock() {
        configMock.close();
    }

    @Test
    public void testCreateCartInternal() {
        when(defaultPartnerCommerceCartFactory.createQuoteInCPQDisabled()).thenReturn(false);
        when(modelService.create(any(String.class))).thenReturn(cart);
        when(cart.getCode()).thenReturn(CART_CODE);
        final CartModel resultCart = defaultPartnerCommerceCartFactory.createCartInternal();

        Assert.assertNotNull(resultCart);
        Assert.assertEquals(CART_CODE, resultCart.getCode());
    }

    @Test
    public void testGenerateQuoteInCPQ_Success() {
        IbmPartnerCartModel cartModel = new IbmPartnerCartModel();
        when(partnerSapCpqQuoteService.createQuoteInCPQ(any()))
            .thenReturn(createCPQResponse("externalQuoteId123"));

        defaultPartnerCommerceCartFactory.generateQuoteInCPQ(cartModel);

        Assert.assertEquals("externalQuoteId123", cartModel.getCpqExternalQuoteId());
    }

    @Test(expected = IbmWebServiceFailureException.class)
    public void testGenerateQuoteInCPQ_NullResponse_ThrowsException() {
        when(defaultPartnerCommerceCartFactory.createQuoteInCPQDisabled()).thenReturn(false);
        when(partnerSapCpqQuoteService.createQuoteInCPQ(any())).thenReturn(null);
        when(ibmPartnerCart.getCode()).thenReturn("testCart");
        defaultPartnerCommerceCartFactory.generateQuoteInCPQ(ibmPartnerCart);
    }


    @Test
    public void testCreateCart_WithCPQDisabled() {
        when(defaultPartnerCommerceCartFactory.createQuoteInCPQDisabled()).thenReturn(true);
        when(modelService.create(any(String.class))).thenReturn(ibmPartnerCart);
        when(ibmPartnerCart.getCode()).thenReturn(CART_CODE);
        when(defaultPartnerCommerceCartFactory.getQuoteCodeKeyGenerator().generate()).thenReturn(
            CART_CODE);
        doNothing().when(modelService).save(any());
        CartModel resultCart = defaultPartnerCommerceCartFactory.createCart();
        Assert.assertNotNull(resultCart);
        Assert.assertEquals(CART_CODE, resultCart.getCode());
    }

    private PartnerCPQCreateQuoteResponseData createCPQResponse(String quoteId) {
        PartnerCPQCreateQuoteResponseData response = new PartnerCPQCreateQuoteResponseData();
        response.setQuoteId(quoteId);
        return response;
    }

    @Test
    public void testCreateCart_WithCPQEnabled_SuccessfulTransaction() {
        when(modelService.create("Cart")).thenReturn(cart);
        doNothing().when(modelService).save(any());
        Transaction mockTransaction = mock(Transaction.class);

        try (MockedStatic<Transaction> transactionMock = mockStatic(Transaction.class)) {
            transactionMock.when(Transaction::current).thenReturn(mockTransaction);

            doNothing().when(mockTransaction).begin();
            doNothing().when(mockTransaction).commit();
            CartModel resultCart = defaultPartnerCommerceCartFactory.createCart();
            verify(mockTransaction).begin();
            verify(mockTransaction).commit();
            verify(modelService).save(resultCart);
            verify(mockTransaction, never()).rollback();
        }
    }

    @Test
    public void testCreateCart_WithCPQEnabled_ThrowsException() {
        try {
            defaultPartnerCommerceCartFactory.createCart();
        } catch (RuntimeException thrown) {
            Assert.assertTrue(thrown.getMessage().contains("unable to create cart"));
            Assert.assertTrue(thrown.getCause() instanceof RuntimeException);
        }

    }
}
