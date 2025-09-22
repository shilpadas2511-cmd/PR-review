package com.ibm.commerce.partner.core.outbound.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ibm.commerce.partner.core.cart.services.PartnerCommerceCartService;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.IbmPartnerB2BUnitModel;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.order.dao.DefaultPartnerSapCpiQuoteDao;
import com.ibm.commerce.partner.core.order.strategies.PartnerQuoteValidationStrategy;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commerceservices.enums.QuoteAction;
import de.hybris.platform.commerceservices.enums.QuoteUserType;
import de.hybris.platform.commerceservices.event.QuoteSalesRepSubmitEvent;
import de.hybris.platform.commerceservices.order.UpdateQuoteFromCartStrategy;
import de.hybris.platform.commerceservices.order.dao.CommerceQuoteDao;
import de.hybris.platform.commerceservices.order.strategies.QuoteActionValidationStrategy;
import de.hybris.platform.commerceservices.order.strategies.QuoteStateSelectionStrategy;
import de.hybris.platform.commerceservices.order.strategies.QuoteUpdateStateStrategy;
import de.hybris.platform.commerceservices.order.strategies.QuoteUserTypeIdentificationStrategy;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.model.ModelService;

import de.hybris.platform.store.BaseStoreModel;
import java.util.Collections;
import java.util.Optional;

import java.util.Set;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.sap.hybris.sapquoteintegration.events.SapCpiQuoteBuyerSubmitEvent;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPartnerScpiQuoteServiceTest {

    DefaultPartnerScpiQuoteService defaultPartnerScpiQuoteService;
    @Mock
    QuoteModel quoteModel;
    @Mock
    UserModel userModel;
    @Mock
    EventService eventService;
    @Mock
    QuoteUpdateStateStrategy quoteUpdateStateStrategy;
    @Mock
    ModelService modelService;
    @Mock
    CartService cartService;
    @Mock
    QuoteUserTypeIdentificationStrategy quoteUserTypeIdentificationStrategy;
    @Mock
    Optional<QuoteUserType> optionalQuoteUserType;
    @Mock
    CartModel cartModel;
    @Mock
    private QuoteService quoteService;
    @Mock
    QuoteActionValidationStrategy quoteActionValidationStrategy;
    @Mock
    UpdateQuoteFromCartStrategy updateQuoteFromCartStrategy;

    @Mock
    CommerceQuoteDao commerceQuoteDao;
    @Mock
    QuoteStateSelectionStrategy quoteStateSelectionStrategy;

    @Mock
    private DefaultPartnerSapCpiQuoteDao partnerCommerceQuoteDao;
    @Mock
    private PartnerQuoteValidationStrategy partnerQuoteValidationStrategy;

    private CustomerModel mockCustomerModel;
    private UserModel mockQuoteUserModel;
    private BaseStoreModel mockStore;
    private PageableData mockPageableData;
    private SearchPageData<QuoteModel> mockSearchPageData;
    private IbmPartnerB2BUnitModel mockIbmPartnerB2BUnitModel;
    private QuoteModel mockQuoteModel;
    private CartModel mockCartModel;
    @Mock
    private PartnerCommerceCartService partnerCommerceCartService;
    @Mock
    private ConfigurationService configurationService;
    @Mock
    private Configuration configuration;

    @Before
    public void setUp() {
        when(configuration.getBoolean(PartnercoreConstants.PROVISIONING_FORMS_FEATURE_FLAG, false)).thenReturn(false);
        when(configurationService.getConfiguration()).thenReturn(configuration);
        defaultPartnerScpiQuoteService = new DefaultPartnerScpiQuoteService(configurationService,
            partnerCommerceQuoteDao, partnerQuoteValidationStrategy, partnerCommerceCartService);
        defaultPartnerScpiQuoteService.setEventService(eventService);
        defaultPartnerScpiQuoteService.setQuoteUpdateStateStrategy(quoteUpdateStateStrategy);
        defaultPartnerScpiQuoteService.setModelService(modelService);
        defaultPartnerScpiQuoteService.setCartService(cartService);
        defaultPartnerScpiQuoteService.setQuoteUserTypeIdentificationStrategy(
            quoteUserTypeIdentificationStrategy);
        defaultPartnerScpiQuoteService.setQuoteActionValidationStrategy(
            quoteActionValidationStrategy);
        defaultPartnerScpiQuoteService.setUpdateQuoteFromCartStrategy(updateQuoteFromCartStrategy);
        defaultPartnerScpiQuoteService.setCommerceQuoteDao(commerceQuoteDao);
        defaultPartnerScpiQuoteService.setQuoteStateSelectionStrategy(quoteStateSelectionStrategy);

        defaultPartnerScpiQuoteService.setUpdateQuoteFromCartStrategy(updateQuoteFromCartStrategy);
        defaultPartnerScpiQuoteService.setModelService(modelService);
        defaultPartnerScpiQuoteService.setCartService(cartService);
        defaultPartnerScpiQuoteService.setQuoteService(quoteService);
        mockCustomerModel = mock(CustomerModel.class);
        mockIbmPartnerB2BUnitModel = mock(IbmPartnerB2BUnitModel.class);
        mockQuoteUserModel = mock(UserModel.class);
        mockStore = mock(BaseStoreModel.class);
        mockPageableData = mock(PageableData.class);
        mockSearchPageData = new SearchPageData<>();
        mockQuoteModel = mock(QuoteModel.class);
        mockCartModel = mock(CartModel.class);
        when(
            quoteUpdateStateStrategy.updateQuoteState(any(QuoteAction.class), any(QuoteModel.class),
                any(UserModel.class))).thenReturn(quoteModel);
        when(quoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(
            any(UserModel.class))).thenReturn(optionalQuoteUserType);
        when(optionalQuoteUserType.isPresent()).thenReturn(true);
    }

    @Test
    public void testSubmitBuyerQuote() {
        when(optionalQuoteUserType.get()).thenReturn(QuoteUserType.BUYER);
        defaultPartnerScpiQuoteService.submitQuote(quoteModel, userModel);
        verify(eventService).publishEvent(any(SapCpiQuoteBuyerSubmitEvent.class));
    }

    @Test
    public void testSubmitSellerQuote() {
        when(optionalQuoteUserType.get()).thenReturn(QuoteUserType.SELLER);
        defaultPartnerScpiQuoteService.submitQuote(quoteModel, userModel);
        verify(eventService).publishEvent(any(QuoteSalesRepSubmitEvent.class));
    }

    @Test
    public void testSubmitSellerApproverQuote() {
        when(optionalQuoteUserType.get()).thenReturn(QuoteUserType.SELLERAPPROVER);
        defaultPartnerScpiQuoteService.submitQuote(quoteModel, userModel);
        verify(eventService, times(0)).publishEvent(any(QuoteSalesRepSubmitEvent.class));
        verify(eventService, times(0)).publishEvent(any(SapCpiQuoteBuyerSubmitEvent.class));
    }

    @Test
    public void testSubmitQuoteUserTypeNull() {
        when(optionalQuoteUserType.isPresent()).thenReturn(false);
        defaultPartnerScpiQuoteService.submitQuote(quoteModel, userModel);
        verify(eventService, times(0)).publishEvent(any(QuoteSalesRepSubmitEvent.class));
        verify(eventService, times(0)).publishEvent(any(SapCpiQuoteBuyerSubmitEvent.class));
    }

    @Test
    public void testSubmitSessionQuoteSameAsRequestedQuote() {
        when(cartService.hasSessionCart()).thenReturn(true);
        when(cartService.getSessionCart()).thenReturn(cartModel);
        when(cartModel.getQuoteReference()).thenReturn(quoteModel);
        when(optionalQuoteUserType.get()).thenReturn(QuoteUserType.BUYER);
        when(updateQuoteFromCartStrategy.updateQuoteFromCart(cartModel)).thenReturn(quoteModel);
        defaultPartnerScpiQuoteService.submitQuote(quoteModel, userModel);
        verify(eventService).publishEvent(any(SapCpiQuoteBuyerSubmitEvent.class));
    }

    @Test
    public void testGetQuoteList() {
        defaultPartnerScpiQuoteService.getQuoteList(mockCustomerModel, mockQuoteUserModel,
            mockStore, mockPageableData);
        verify(quoteStateSelectionStrategy).getAllowedStatesForAction(QuoteAction.LISTVIEW,
            mockQuoteUserModel);
    }

    @Test
    public void testGetQuoteListForSites() {
        when(configuration.getBoolean(PartnercoreConstants.PROVISIONING_FORMS_FEATURE_FLAG, false)).thenReturn(false);
        when(configurationService.getConfiguration()).thenReturn(configuration);
        defaultPartnerScpiQuoteService = new DefaultPartnerScpiQuoteService(configurationService,
            partnerCommerceQuoteDao, partnerQuoteValidationStrategy, null);
        defaultPartnerScpiQuoteService.setQuoteStateSelectionStrategy(quoteStateSelectionStrategy);
        when(quoteStateSelectionStrategy.getAllowedStatesForAction(QuoteAction.LISTVIEW,
            mockQuoteUserModel)).thenReturn(null);
        defaultPartnerScpiQuoteService.getQuoteList(mockCustomerModel, mockQuoteUserModel,
            mockStore, mockPageableData);
        verify(quoteStateSelectionStrategy).getAllowedStatesForAction(QuoteAction.LISTVIEW,
            mockQuoteUserModel);
    }

    @Test
    public void testGetQuoteByCodeAndSiteIdsAndStore_Success() {
        CustomerModel customerModel = mock(CustomerModel.class);
        UserModel quoteUserModel = mock(UserModel.class);
        BaseStoreModel store = mock(BaseStoreModel.class);
        String quoteCode = "testQuoteCode";
        B2BUnitModel b2bUnitModel = mock(B2BUnitModel.class);
        QuoteModel expectedQuoteModel = mock(QuoteModel.class);

        when(customerModel.getGroups()).thenReturn(Set.of(b2bUnitModel));
        when(quoteStateSelectionStrategy.getAllowedStatesForAction(QuoteAction.VIEW,
            quoteUserModel)).thenReturn(Set.of(QuoteState.BUYER_DRAFT));
        when(partnerCommerceQuoteDao.findUniqueQuoteByCodeAndSiteIdsAndStore(anyList(), eq(store),
            eq(quoteCode), anySet())).thenReturn(expectedQuoteModel);

        QuoteModel result = defaultPartnerScpiQuoteService.getQuoteByCodeAndSiteIdsAndStore(
            customerModel, quoteUserModel, store, quoteCode);

        verify(partnerCommerceQuoteDao).findUniqueQuoteByCodeAndSiteIdsAndStore(anyList(),
            eq(store), eq(quoteCode), anySet());
        assertEquals(expectedQuoteModel, result);
    }

    @Test
    public void testGetQuoteByCodeAndSiteIdsAndStore_CustomerWithoutGroups() {
        CustomerModel customerModel = mock(CustomerModel.class);
        UserModel quoteUserModel = mock(UserModel.class);
        BaseStoreModel store = mock(BaseStoreModel.class);
        String quoteCode = "testQuoteCode";

        when(customerModel.getGroups()).thenReturn(Collections.emptySet());
        when(quoteStateSelectionStrategy.getAllowedStatesForAction(QuoteAction.VIEW,
            quoteUserModel)).thenReturn(Set.of(QuoteState.BUYER_DRAFT));
        when(partnerCommerceQuoteDao.findUniqueQuoteByCodeAndSiteIdsAndStore(anyList(), eq(store),
            eq(quoteCode), anySet())).thenReturn(null);

        QuoteModel result = defaultPartnerScpiQuoteService.getQuoteByCodeAndSiteIdsAndStore(
            customerModel, quoteUserModel, store, quoteCode);

        verify(partnerCommerceQuoteDao).findUniqueQuoteByCodeAndSiteIdsAndStore(anyList(),
            eq(store), eq(quoteCode), anySet());
        assertEquals(null, result);
    }

    @Test
    public void testGetQuoteByCodeAndSiteIdsAndStore_NullCustomerModel() {
        UserModel quoteUserModel = mock(UserModel.class);
        BaseStoreModel store = mock(BaseStoreModel.class);
        String quoteCode = "testQuoteCode";

        assertThrows(IllegalArgumentException.class, () -> {
            defaultPartnerScpiQuoteService.getQuoteByCodeAndSiteIdsAndStore(null, quoteUserModel,
                store, quoteCode);
        });
    }

    @Test
    public void testGetQuoteByCodeAndSiteIdsAndStore_NullQuoteUserModel() {
        CustomerModel customerModel = mock(CustomerModel.class);
        BaseStoreModel store = mock(BaseStoreModel.class);
        String quoteCode = "testQuoteCode";

        assertThrows(IllegalArgumentException.class, () -> {
            defaultPartnerScpiQuoteService.getQuoteByCodeAndSiteIdsAndStore(customerModel, null,
                store, quoteCode);
        });
    }

    @Test
    public void testGetQuoteByCodeAndSiteIdsAndStore_NullQuoteCode() {
        CustomerModel customerModel = mock(CustomerModel.class);
        UserModel quoteUserModel = mock(UserModel.class);
        BaseStoreModel store = mock(BaseStoreModel.class);

        assertThrows(IllegalArgumentException.class, () -> {
            defaultPartnerScpiQuoteService.getQuoteByCodeAndSiteIdsAndStore(customerModel,
                quoteUserModel, store, null);
        });
    }

    @Test
    public void testGetQuoteByCodeAndSiteIdsAndStore_NullStore() {
        CustomerModel customerModel = mock(CustomerModel.class);
        UserModel quoteUserModel = mock(UserModel.class);
        String quoteCode = "testQuoteCode";

        assertThrows(IllegalArgumentException.class, () -> {
            defaultPartnerScpiQuoteService.getQuoteByCodeAndSiteIdsAndStore(customerModel,
                quoteUserModel, null, quoteCode);
        });
    }

    @Test
    public void testValidateQuoteEditBySiteIds() {
        defaultPartnerScpiQuoteService.validateQuoteEditBySiteIds(mockQuoteModel,
            mockQuoteUserModel, mockCartModel);
        verify(partnerQuoteValidationStrategy).validateQuoteEditBySiteIds(mockQuoteModel,
            mockQuoteUserModel, mockCartModel);
    }

    @Test
    public void testUpdateQuoteStatus_ValidQuote() {
        IbmPartnerQuoteModel quote = new IbmPartnerQuoteModel();
        IbmPartnerCartModel cart = new IbmPartnerCartModel();
        quote.setCartReference(cart);

        when(updateQuoteFromCartStrategy.updateQuoteFromCart(cart)).thenReturn(quote);

        defaultPartnerScpiQuoteService.updateQuoteStatus(quote, QuoteState.BUYER_DRAFT);

        assertEquals(QuoteState.BUYER_DRAFT, quote.getState());
        verify(modelService).save(quote);
    }

    @Test
    public void testUpdateQuoteStatus_NullCartReference() {
        IbmPartnerQuoteModel quote = new IbmPartnerQuoteModel();
        quote.setCartReference(null);
        defaultPartnerScpiQuoteService.updateQuoteStatus(quote, QuoteState.BUYER_DRAFT);
        //verify(quote.getCartReference() == null);
    }
    @Test
    public void testCreateQuoteFromCart() {
        QuoteModel result = defaultPartnerScpiQuoteService.createQuoteFromCart(cartModel, userModel);
        verify(partnerCommerceCartService, times(1)).createProvisionForm(cartModel);
    }
}
