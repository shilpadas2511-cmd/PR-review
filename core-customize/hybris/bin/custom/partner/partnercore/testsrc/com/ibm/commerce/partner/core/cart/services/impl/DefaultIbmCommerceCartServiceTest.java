package com.ibm.commerce.partner.core.cart.services.impl;

import static com.google.common.base.CharMatcher.any;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ibm.commerce.partner.core.cart.dao.impl.DefaultIbmCommerceCartDao;
import com.ibm.commerce.partner.core.cart.strategies.PartnerCartUpdateStrategy;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.exceptions.IbmWebServiceFailureException;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.IbmPartnerPidCartModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.PartnerProvisionFormsModel;
import com.ibm.commerce.partner.core.provisionform.services.impl.DefaultPartnerProvisionFormOutboundIntegrationService;
import com.ibm.commerce.partner.core.quote.provision.form.data.request.ProvisionFormItemsRequestData;
import com.ibm.commerce.partner.core.quote.provision.form.data.request.ProvisionFormRequestData;
import com.ibm.commerce.partner.core.quote.provision.form.data.response.ProvisionFormResponseData;
import com.ibm.commerce.partner.core.utils.PartnerOrderUtils;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import java.util.List;
import java.util.Set;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultIbmCommerceCartServiceTest {


    @InjectMocks
    private DefaultIbmCommerceCartService defaultIbmCommerceCartService;

    @Mock
    private DefaultIbmCommerceCartDao commerceCartDao;

    @Mock
    private IbmPartnerPidCartModel pidCart;

    @Mock
    private BaseSiteModel currentBaseSite;

    @Mock
    private CartEntryModel cartEntry;

    @Mock
    private CartEntryModel mainEntry;

    @Mock
    private CartModel order;

    @Mock
    private UserService userService;

    String errorMessage;

    @Mock
    private DefaultPartnerProvisionFormOutboundIntegrationService partnerProvisionFormOutboundIntegrationService;

    @Mock
    private ModelService modelService;

    @Mock
    private IbmPartnerCartModel ibmPartnerCartModel;

    @Mock
    private AbstractOrderModel abstractOrderModel;

    @Mock
    private PartnerProvisionFormsModel provisionFormsModel;

    @Mock
    private ProvisionFormRequestData provisionFormRequestData;

    @Mock
    private ProvisionFormResponseData provisionFormResponseData;

    @Mock
    private Converter<AbstractOrderModel, ProvisionFormRequestData> provisionFormRequestDataConverter;

    @Mock
    private Converter<ProvisionFormResponseData, AbstractOrderModel> provisionFormResponseReverseConverter;

    @Mock
    private Converter<AbstractOrderModel, ProvisionFormItemsRequestData> provisionFormItemsPatchRequestDataConverter;

    @Mock
    private PartnerProvisionFormsModel partnerProvisionFormsModel;

    @Mock
    private UserModel currentUser;

    private DefaultIbmCommerceCartService defaultIbmCommerceCartService1;

    @Mock
    private UserService provisionUserService;

    @Mock
    private ConfigurationService configurationService;
    @Mock
    private Configuration configuration;
    @Mock
    private CustomerEmailResolutionService customerEmailResolutionService;

    @Mock
    private PartnerCartUpdateStrategy cartUpdateQuestionSelectionStrategy;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(configuration.getBoolean(PartnercoreConstants.PROVISIONING_FORMS_FEATURE_FLAG,
            false)).thenReturn(false);
        when(configurationService.getConfiguration()).thenReturn(configuration);
        Double cart1MilPriceValue = 1000000.00d;
        defaultIbmCommerceCartService1 = new DefaultIbmCommerceCartService(commerceCartDao,
            provisionFormRequestDataConverter, null, provisionFormResponseReverseConverter,
            userService, errorMessage, configurationService,
            provisionFormItemsPatchRequestDataConverter, customerEmailResolutionService,
            cartUpdateQuestionSelectionStrategy, cart1MilPriceValue);
    }

    @Test
    public void testGetCartByCodeAndSiteIdsAndStore_Success() {
        CustomerModel customerModel = mock(CustomerModel.class);
        BaseSiteModel siteModel = mock(BaseSiteModel.class);
        String cartCode = "testCode";
        B2BUnitModel b2bUnitModel = mock(B2BUnitModel.class);
        CartModel expectedCartModel = mock(CartModel.class);

        when(customerModel.getGroups()).thenReturn(Set.of(b2bUnitModel));
        when(
            commerceCartDao.getCartByCodeAndSiteIdsAndStore(eq(cartCode), anyList(), eq(siteModel)))
            .thenReturn(expectedCartModel);

        CartModel result = defaultIbmCommerceCartService.getCartModelForCodeAndSiteIds(cartCode,
            customerModel, siteModel);
        verify(commerceCartDao).getCartByCodeAndSiteIdsAndStore(eq(cartCode), anyList(),
            eq(siteModel));
        assertEquals(expectedCartModel, result);
    }

    @Test
    public void testGetCartModelForCodeAndSiteIds_NullUserModel() {
        BaseSiteModel siteModel = mock(BaseSiteModel.class);
        String cartCode = "testCode";

        assertThrows(IllegalArgumentException.class, () -> {
            defaultIbmCommerceCartService.getCartModelForCodeAndSiteIds(cartCode, null, siteModel);
        });
    }


    @Test
    public void testGetCartModelForCodeAndSiteIds_NullCartCode() {
        UserModel userModel = mock(UserModel.class);
        BaseSiteModel siteModel = mock(BaseSiteModel.class);

        assertThrows(IllegalArgumentException.class, () -> {
            defaultIbmCommerceCartService.getCartModelForCodeAndSiteIds(
                null, userModel, siteModel);
        });
    }

    @Test
    public void testGetCartModelForCodeAndSiteIds_NullSite() {
        UserModel userModel = mock(UserModel.class);
        String cartCode = "testCode";

        assertThrows(IllegalArgumentException.class, () -> {
            defaultIbmCommerceCartService.getCartModelForCodeAndSiteIds(
                cartCode, userModel, null);
        });
    }

    @Test
    public void testGetMainCartCode_validScenario() {
        String code = "validCode";
        when(commerceCartDao.getPidCartByCodeAndStore(code, currentBaseSite)).thenReturn(pidCart);
        when(pidCart.getEntries()).thenReturn(List.of(cartEntry));
        when(cartEntry.getMasterEntry()).thenReturn(mainEntry);
        when(mainEntry.getOrder()).thenReturn(order);
        when(order.getCode()).thenReturn("orderCode");

        String result = defaultIbmCommerceCartService.getMainCartCode(code, currentBaseSite);
        assertEquals("orderCode", result);
    }

    @Test
    public void testGetMainCartCode_pidCartNotFound() {
        String code = "validCode";
        when(commerceCartDao.getPidCartByCodeAndStore(code, currentBaseSite)).thenReturn(null);

        String result = defaultIbmCommerceCartService.getMainCartCode(code, currentBaseSite);
        assertNull(result);
    }

    @Test
    public void testCreateProvisionForm() {
        when(provisionFormRequestDataConverter.convert(order)).thenReturn(provisionFormRequestData);
        when(partnerProvisionFormOutboundIntegrationService.create(
            provisionFormRequestData)).thenReturn(provisionFormResponseData);

        defaultIbmCommerceCartService.createProvisionForm(order);

        assertEquals(provisionFormRequestData, provisionFormRequestDataConverter.convert(order));
        assertEquals(provisionFormResponseData,
            partnerProvisionFormOutboundIntegrationService.create(provisionFormRequestData));
    }

    @Test
    public void testUpdateProvisionForm_withErrorInProvisionForms() {
        defaultIbmCommerceCartService = Mockito.mock(DefaultIbmCommerceCartService.class);
        when(ibmPartnerCartModel.getProvisionForms()).thenReturn(provisionFormsModel);
        when(provisionFormsModel.getErrors()).thenReturn("Some error message");
        when(provisionFormRequestDataConverter.convert(abstractOrderModel)).thenReturn(
            provisionFormRequestData);
        when(partnerProvisionFormOutboundIntegrationService.create(provisionFormRequestData))
            .thenReturn(provisionFormResponseData);
        defaultIbmCommerceCartService.updateProvisionForm(ibmPartnerCartModel);
        verify(defaultIbmCommerceCartService).updateProvisionForm(ibmPartnerCartModel);
    }

    @Test
    public void testUpdateProvisionForm_withValidProvisionForm() throws Exception {
        String carID = "cartId";
        when(ibmPartnerCartModel.getProvisionForms()).thenReturn(provisionFormsModel);
        when(provisionFormsModel.getErrors()).thenReturn(null);
        when(provisionFormsModel.getCode()).thenReturn("cartId");
        when(provisionFormRequestDataConverter.convert(abstractOrderModel)).thenReturn(
            provisionFormRequestData);
        when(partnerProvisionFormOutboundIntegrationService.patch(anyObject(), anyString()))
            .thenReturn(provisionFormResponseData);
        when(provisionFormResponseReverseConverter.convert(provisionFormResponseData,
            ibmPartnerCartModel))
            .thenReturn(null);

        defaultIbmCommerceCartService.updateProvisionForm(ibmPartnerCartModel);

    }

    @Test
    public void testUpdateProvisionForm_withIbmWebServiceFailureException() throws Exception {
        given(ibmPartnerCartModel.getProvisionForms()).willReturn(provisionFormsModel);
        given(provisionFormsModel.getErrors()).willReturn(null);
        given(provisionFormsModel.getCode()).willReturn("cartId");
        given(provisionFormRequestDataConverter.convert(abstractOrderModel))
            .willReturn(provisionFormRequestData);
        given(partnerProvisionFormOutboundIntegrationService.patch(anyObject(), anyString()))
            .willThrow(new IbmWebServiceFailureException("Web service failure"));
        given(modelService.create(PartnerProvisionFormsModel.class)).willReturn(
            partnerProvisionFormsModel);
        given(provisionUserService.getCurrentUser()).willReturn(currentUser);
        given(currentUser.getUid()).willReturn("cartID");

        defaultIbmCommerceCartService.updateProvisionForm(ibmPartnerCartModel);
        verify(defaultIbmCommerceCartService).createErrorProvisionForm(ibmPartnerCartModel);
    }

    @Test
    public void testCreateErrorProvisionForm_withExistingProvisionFormsWithoutErrors() {
        when(ibmPartnerCartModel.getProvisionForms()).thenReturn(provisionFormsModel);
        when(provisionFormsModel.getErrors()).thenReturn(null);
        when(modelService.create(PartnerProvisionFormsModel.class)).thenReturn(provisionFormsModel);
        when(userService.getCurrentUser()).thenReturn(mock(UserModel.class));

        defaultIbmCommerceCartService.createErrorProvisionForm(ibmPartnerCartModel);
        verify(modelService).save(ibmPartnerCartModel);
    }

    @Test
    public void validateProvisionForms_shouldUpdateProvisionForm_whenSaasProductExists() {
        IbmPartnerCartModel cart = new IbmPartnerCartModel();
        IbmPartnerQuoteModel ibmPartnerQuoteModel = new IbmPartnerQuoteModel();
        cart.setQuoteReference(ibmPartnerQuoteModel);

        try (MockedStatic<PartnerOrderUtils> utils = Mockito.mockStatic(PartnerOrderUtils.class)) {
            when(configuration.getBoolean(PartnercoreConstants.PROVISIONING_FORMS_FEATURE_FLAG,
                false)).thenReturn(true);
            utils.when(() -> PartnerOrderUtils.checkSaasProduct(cart)).thenReturn(true);

            defaultIbmCommerceCartService.validateProvisionForms(cart);
        }
    }

    @Test
    public void validateProvisionForms_shouldClearProvisionForms_whenNotSaasProduct() {
        IbmPartnerCartModel cart = new IbmPartnerCartModel();
        IbmPartnerQuoteModel ibmPartnerQuoteModel = new IbmPartnerQuoteModel();
        cart.setQuoteReference(ibmPartnerQuoteModel);
        PartnerProvisionFormsModel form = new PartnerProvisionFormsModel();
        form.setCode("code123");
        cart.setProvisionForms(form);

        try (MockedStatic<PartnerOrderUtils> utils = Mockito.mockStatic(PartnerOrderUtils.class)) {
            when(configuration.getBoolean(PartnercoreConstants.PROVISIONING_FORMS_FEATURE_FLAG,
                false)).thenReturn(true);
            utils.when(() -> PartnerOrderUtils.checkSaasProduct(cart)).thenReturn(false);

            defaultIbmCommerceCartService.setModelService(modelService);
            defaultIbmCommerceCartService.validateProvisionForms(cart);

            assertNull(cart.getProvisionForms());
            verify(modelService).save(cart);
        }
    }

    @Test
    public void validateProvisionForms_shouldDoNothing_whenFeatureFlagIsFalse() {
        CartModel cart = new CartModel();
        QuoteModel quoteModel = new QuoteModel();
        cart.setQuoteReference(quoteModel);

        when(configuration.getBoolean(PartnercoreConstants.PROVISIONING_FORMS_FEATURE_FLAG,
            false)).thenReturn(false);

        defaultIbmCommerceCartService.validateProvisionForms(cart);
        verify(modelService, never()).save(any());
    }

    @Test
    public void validateProvisionForms_shouldDoNothing_whenNoQuoteReference() {
        CartModel cart = new CartModel();
        when(configuration.getBoolean(PartnercoreConstants.PROVISIONING_FORMS_FEATURE_FLAG,
            false)).thenReturn(true);
        defaultIbmCommerceCartService.validateProvisionForms(cart);
        verify(modelService, never()).save(any());
    }

    @Test
    public void determineCartCode_shouldReturnMainCartCode_ifPrefixMatchesAndMainExists() {
        String pidCartCode = "PID_CART123";
        String expectedMainCode = "MAIN_CART";
        BaseSiteModel site = mock(BaseSiteModel.class);
        when(defaultIbmCommerceCartService.getMainCartCode(pidCartCode, site)).thenReturn(
            expectedMainCode);
        String result = defaultIbmCommerceCartService.determineCartCode(pidCartCode, site);
        assertEquals(expectedMainCode, result);
    }

    @Test
    public void determineCartCode_shouldReturnOriginalCode_ifMainCartCodeIsNull() {
        String pidCartCode = "PID_CART456";
        BaseSiteModel site = new BaseSiteModel();
        when(defaultIbmCommerceCartService.getMainCartCode(pidCartCode, site)).thenReturn(null);
        String result = defaultIbmCommerceCartService.determineCartCode(pidCartCode, site);
        assertEquals(pidCartCode, result);
    }

    @Test
    public void determineCartCode_shouldReturnOriginalCode_ifPrefixDoesNotMatch() {
        String regularCode = "CART789";
        BaseSiteModel site = new BaseSiteModel();
        String result = defaultIbmCommerceCartService.determineCartCode(regularCode, site);
        assertEquals(regularCode, result);
    }

    @Test
    public void getProvisionFormRequestDataConverter_shouldReturnInjectedConverter() {
        Converter<AbstractOrderModel, ProvisionFormRequestData> mockConverter = mock(
            Converter.class);
        defaultIbmCommerceCartService.getProvisionFormRequestDataConverter();
    }

}

