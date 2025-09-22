package com.ibm.commerce.partner.facades.order.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ibm.commerce.data.order.QuoteCollaboratorsData;
import com.ibm.commerce.partner.core.cart.services.PartnerCommerceCartService;
import com.ibm.commerce.partner.core.enums.CpqPricingTypeEnum;
import com.ibm.commerce.partner.core.event.CartPriceLookUpEvent;
import com.ibm.commerce.partner.core.model.IbmPartnerCartEntryModel;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.PartnerCpqHeaderPricingDetailModel;
import com.ibm.commerce.partner.core.model.PartnerCpqPricingDetailModel;
import com.ibm.commerce.partner.core.order.services.PartnerProcessService;
import com.ibm.commerce.partner.core.order.services.PidCartFactory;
import com.ibm.commerce.partner.core.order.services.impl.DefaultPidCartFactory;
import com.ibm.commerce.partner.core.quote.services.PartnerSapCpqQuoteService;
import com.ibm.commerce.partner.core.services.PriceLookUpService;
import com.ibm.commerce.partner.core.util.data.CommerceCartModificationTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.AbstractOrderEntryModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.AbstractOrderModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.CartModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.CommerceCartParameterTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.IbmPartnerCartModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.PartnerCpqHeaderPricingDetailModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.UserModelTestDataGenerator;
import com.ibm.commerce.partner.data.order.entry.pricing.PartnerOverrideEntryPriceData;
import com.ibm.commerce.partner.data.order.entry.pricing.PartnerOverrideHeaderPriceData;
import com.ibm.commerce.partner.facades.util.AddToCartParamsTestDataGenerator;
import com.ibm.commerce.partner.facades.util.CartModificationTestDataGenerator;
import com.ibm.commerce.partner.facades.util.CartTestDataGenerator;
import com.ibm.commerce.partner.facades.util.OrderEntryTestDataGenerator;
import com.ibm.commerce.partner.facades.util.ProductDataTestGenerator;
import com.ibm.commerce.partner.facades.util.QuoteTestDataGenerator;
import com.ibm.commerce.partner.facades.validator.PartnerOverridePriceValidator;
import com.sap.hybris.sapcpqquoteintegration.model.CpqPricingDetailModel;
import de.hybris.platform.b2b.services.B2BCartService;
import de.hybris.platform.b2bacceleratorfacades.exception.DomainException;
import de.hybris.platform.b2bacceleratorfacades.exception.EntityValidationException;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.CartQuoteUpdateValidator;
import de.hybris.platform.commercefacades.order.data.AddToCartParams;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.quote.data.QuoteData;
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class DefaultPartnerCartFacadeTest {

    private static final String ACTING_USER_UID = "ACTING_USER_UID";
    private static final String COLLABORATOR_EMAIL_ONE = "test1@example.com";
    private static final String COLLABORATOR_EMAIL_TWO = "test2@example.com";
    private static final String COLLABORATOR_EMAIL_THREE = "test3@example.com";

    @Mock
    private Converter<AddToCartParams, CommerceCartParameter> commerceCartParameterConverter;
    @Mock
    private CommerceCartService commerceCartService;
    @Mock
    private Converter<CommerceCartModification, CartModificationData> cartModificationConverter;

    @Mock
    private Converter<PartnerOverrideHeaderPriceData, PartnerCpqHeaderPricingDetailModel> partnerCartHeaderPricingDetailsReverseConverter;

    @Mock
    private Converter<PartnerOverrideEntryPriceData, PartnerCpqPricingDetailModel> partnerCartEntryPricingDetailsReverseConverter;

	 @Mock
	 private Converter<AbstractOrderModel, List<CartModificationData>> groupCartModificationListConverter;

    @Mock
    private PidCartFactory pidCartFactory;
    @Mock
    private SessionService sessionService;
    @Mock
    private ModelService modelService;
    @Mock
    private CartFacade cartFacade;
    @InjectMocks
    private DefaultPidCartFactory defaultPidCartFactory;
    @Mock
    private B2BCartService cartService;
    @Mock
    private UserService userService;
    @Mock
    CartQuoteUpdateValidator cartQuoteUpdateValidator;
    @Mock
    private UserModel customer;
    @Mock
    private EventService eventService;
    @Mock
    private PartnerProcessService partnerProcessService;
    @Mock
    private PartnerSapCpqQuoteService partnerSapCpqQuoteService;
    @Mock
    private ConfigurationService configurationService;
    @Mock
    IbmPartnerQuoteModel quoteModel;
    @Mock
    private PriceLookUpService priceLookUpService;
    @Mock
    private PartnerOverridePriceValidator partnerOverridePriceValidator;
    @Mock
    IbmPartnerCartModel ibmPartnerCartModel;
     PartnerCommerceCartService partnerCommerceCartService;

    private DefaultPartnerCartFacade defaultPartnerCartFacade;

    @Mock
    private CustomerEmailResolutionService customerEmailResolutionService;

    @Before
    public void setUp() {

        defaultPartnerCartFacade = new DefaultPartnerCartFacade(
            commerceCartParameterConverter,
            commerceCartService,
            cartModificationConverter, partnerCartHeaderPricingDetailsReverseConverter,
            partnerCartEntryPricingDetailsReverseConverter, pidCartFactory, sessionService,
            modelService, eventService, partnerProcessService, priceLookUpService,
            partnerOverridePriceValidator, NumberUtils.INTEGER_TWO, partnerCommerceCartService,
            partnerSapCpqQuoteService, configurationService,customerEmailResolutionService);

        defaultPidCartFactory = mock(DefaultPidCartFactory.class);
        cartService = mock(B2BCartService.class);
        userService = mock(UserService.class);

        defaultPartnerCartFacade.setCartFacade(cartFacade);
        defaultPidCartFactory.setUserService(userService);
        defaultPartnerCartFacade.setCartService(cartService);
        defaultPartnerCartFacade.setSessionService(sessionService);
        defaultPartnerCartFacade.setCartQuoteUpdateValidator(cartQuoteUpdateValidator);
        defaultPartnerCartFacade.setUserService(userService);
		defaultPartnerCartFacade.setGroupCartModificationListConverter(groupCartModificationListConverter);
    }

    @Test
    public void testAddOrderEntryWithNull() throws CommerceCartModificationException {
        final OrderEntryData cartEntry = OrderEntryTestDataGenerator.createOrderEntryData();
        final ProductData productData = ProductDataTestGenerator.createProductData("765");
        cartEntry.setProduct(productData);
        cartEntry.setEntryNumber(1);
        cartEntry.setQuantity(4L);
        final AddToCartParams params = new AddToCartParams();
        when(cartFacade.addToCart(any())).thenReturn(null);
        final CartModificationData cartModificationData = defaultPartnerCartFacade.addOrderEntry(
            cartEntry);
        assertNull(cartModificationData);
    }

    @Test
    public void testAddOrderEntry_CartModification() throws CommerceCartModificationException {
        final OrderEntryData cartEntry = OrderEntryTestDataGenerator.createOrderEntryData();
        final ProductData productData = ProductDataTestGenerator.createProductData("765");
        cartEntry.setProduct(productData);
        cartEntry.setEntryNumber(1);
        cartEntry.setQuantity(4L);
        final AddToCartParams params = new AddToCartParams();
        final CartModificationData cartModificationData = CartModificationTestDataGenerator.createCartModificationData();
        cartModificationData.setQuantityAdded(1L);
        cartModificationData.setEntry(cartEntry);
        when(cartFacade.addToCart(any())).thenReturn(cartModificationData);
        productData.setName("test");
        final CartModificationData cartModificationDataResult = defaultPartnerCartFacade.addOrderEntry(
            cartEntry);
        assertEquals(1, cartModificationDataResult.getQuantityAdded());
    }

    @Test(expected = EntityValidationException.class)
    public void testAddOrderEntry_WithOutEntries() {
        final OrderEntryData cartEntry = OrderEntryTestDataGenerator.createOrderEntryData();
        defaultPartnerCartFacade.addOrderEntry(cartEntry);
    }

    @Test(expected = EntityValidationException.class)
    public void testAddOrderEntry_WithProduct() {
        final OrderEntryData cartEntry = OrderEntryTestDataGenerator.createOrderEntryData();
        final ProductData productData = ProductDataTestGenerator.createProductData();
        cartEntry.setProduct(productData);
        defaultPartnerCartFacade.addOrderEntry(cartEntry);
    }

    @Test(expected = EntityValidationException.class)
    public void testAddOrderEntry_WithProductCode() {
        final OrderEntryData cartEntry = OrderEntryTestDataGenerator.createOrderEntryData();
        final ProductData productData = ProductDataTestGenerator.createProductData("765");
        cartEntry.setProduct(productData);
        defaultPartnerCartFacade.addOrderEntry(cartEntry);
    }

    @Test(expected = EntityValidationException.class)
    public void testAddOrderEntry_WithEntryNumber() {
        final OrderEntryData cartEntry = OrderEntryTestDataGenerator.createOrderEntryData();
        final ProductData productData = ProductDataTestGenerator.createProductData("765");
        cartEntry.setProduct(productData);
        cartEntry.setEntryNumber(4);
        defaultPartnerCartFacade.addOrderEntry(cartEntry);
    }


    @Test
    public void testAddOrderEntry_CartModification_test() throws CommerceCartModificationException {
        final OrderEntryData cartEntry = OrderEntryTestDataGenerator.createOrderEntryData();
        final ProductData productData = ProductDataTestGenerator.createProductData("765");
        cartEntry.setProduct(productData);
        cartEntry.setEntryNumber(1);
        cartEntry.setQuantity(4L);
        final AddToCartParams params = new AddToCartParams();
        final CartModificationData cartModificationData = new CartModificationData();
        cartModificationData.setQuantityAdded(0);
        cartModificationData.setEntry(cartEntry);
        when(cartFacade.addToCart(any())).thenReturn(cartModificationData);
        productData.setName("test");
        defaultPartnerCartFacade.addOrderEntry(cartEntry);
    }

    @Test
    public void testUpdateOrderntry() throws CommerceCartModificationException {
        final OrderEntryData cartEntry = OrderEntryTestDataGenerator.createOrderEntryData();
        final ProductData productData = ProductDataTestGenerator.createProductData("765");
        cartEntry.setProduct(productData);
        cartEntry.setEntryNumber(1);
        cartEntry.setQuantity(4L);
        final AddToCartParams params = new AddToCartParams();
        final CartModificationData cartModificationData = CartModificationTestDataGenerator.createCartModificationData();
        final CartData cartData = CartTestDataGenerator.createCartData();
        when(cartFacade.addToCart(any())).thenReturn(cartModificationData);
        when(cartFacade.getSessionCart()).thenReturn(cartData);
        final AddToCartParams addToCartParams = AddToCartParamsTestDataGenerator.createAddToCartParams();
        final CommerceCartParameter commerceCartParameter = CommerceCartParameterTestDataGenerator
            .createCommerceCartParamterModel();
        commerceCartParameter.setCreateNewEntry(true);
        given(commerceCartParameterConverter.convert(any(AddToCartParams.class))).willReturn(
            commerceCartParameter);
        given(cartModificationConverter.convert(any())).willReturn(cartModificationData);
        when(commerceCartService.updateQuantityForCartEntry(any()))
            .thenReturn(CommerceCartModificationTestDataGenerator.createCartModificationData());
        when(userService.getCurrentUser()).thenReturn(customer);
        when(cartQuoteUpdateValidator.isCartQuoteUpdateAllowed(any(), any(), any())).thenReturn(
            true);
        final CartModificationData cartModificationDataResult = defaultPartnerCartFacade.updateOrderEntry(
            cartEntry);
        assertEquals(0, cartModificationDataResult.getQuantityAdded());
    }

    @Test(expected = EntityValidationException.class)
    public void testUpdateWithEntriesException() throws CommerceCartModificationException {
        final OrderEntryData cartEntry = OrderEntryTestDataGenerator.createOrderEntryData();
        final ProductData productData = ProductDataTestGenerator.createProductData("765");
        cartEntry.setProduct(productData);
        cartEntry.setEntryNumber(1);
        cartEntry.setQuantity(0L);
        final List<OrderEntryData> entries = new ArrayList<>();
        final OrderEntryData childEntry1 = OrderEntryTestDataGenerator.createOrderEntryData();
        final OrderEntryData childEntry2 = OrderEntryTestDataGenerator.createOrderEntryData();
        entries.add(childEntry1);
        entries.add(childEntry2);
        cartEntry.setEntries(entries);
        final CartModificationData cartModificationData = CartModificationTestDataGenerator.createCartModificationData();
        when(cartFacade.addToCart(any())).thenReturn(cartModificationData);
        when(cartFacade.getSessionCart()).thenReturn(new CartData());
        when(userService.getCurrentUser()).thenReturn(customer);
        when(cartQuoteUpdateValidator.isCartQuoteUpdateAllowed(any(), any(), any())).thenReturn(
            true);
        defaultPartnerCartFacade.updateOrderEntry(cartEntry);
    }

    @Test
    public void testPidEntries() throws CommerceCartModificationException {
        final AbstractOrderEntryModel abstractOrderEntryModel = AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntry();
        defaultPidCartFactory = mock(DefaultPidCartFactory.class);
        final Collection<AbstractOrderEntryModel> pidEntries = new ArrayList<>();
        final AbstractOrderEntryModel abstractOrderEntryModel1 = AbstractOrderEntryModelTestDataGenerator
            .createAbstractOrderEntry();
        final CartModel pidCartModel = new CartModel();
        abstractOrderEntryModel1.setOrder(pidCartModel);
        pidEntries.add(abstractOrderEntryModel1);
        abstractOrderEntryModel.setChildEntries(pidEntries);
        final CartModel cartModel = CartModelTestDataGenerator.createCartModel("123");
        when(pidCartFactory.createCart("345")).thenReturn(cartModel);
        final CartModel cartModel1 = defaultPartnerCartFacade.getPidCart(abstractOrderEntryModel,
            "345", true);
        assertNotNull(cartModel1);
        assertEquals("123", cartModel1.getCode());

    }

    @Test
    public void testPidEntriesFalse() throws CommerceCartModificationException {
        final AbstractOrderEntryModel abstractOrderEntryModel = AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntry();
        final Collection<AbstractOrderEntryModel> pidEntries = new ArrayList<>();
        final AbstractOrderEntryModel abstractOrderEntryModel1 = AbstractOrderEntryModelTestDataGenerator
            .createAbstractOrderEntry();
        final CartModel pidCartModel = CartModelTestDataGenerator.createCartModel("8989");
        abstractOrderEntryModel1.setOrder(pidCartModel);
        pidEntries.add(abstractOrderEntryModel1);
        abstractOrderEntryModel.setChildEntries(pidEntries);
        final CartModel cartModel = defaultPartnerCartFacade.getPidCart(abstractOrderEntryModel,
            "345", false);
        assertEquals("8989", cartModel.getCode());
    }


    @Test(expected = NullPointerException.class)
    public void testPidEntriesNull() {
        final AbstractOrderEntryModel abstractOrderEntryModel = AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntry();
        final CartModel cartModel = defaultPartnerCartFacade.getPidCart(null, "345", false);
        assertNull(cartModel.getCode());
    }

    @Test
    public void testGetPidEntries() {
        final AbstractOrderModel abstractOrderModel = AbstractOrderModelTestDataGenerator.createAbstractOrderModel();
        final List<AbstractOrderEntryModel> entries = new ArrayList<>();
        final AbstractOrderEntryModel abstractOrderEntryModel = AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntry();
        abstractOrderEntryModel.setEntryNumber(1);
        entries.add(abstractOrderEntryModel);
        abstractOrderModel.setEntries(entries);
        final AbstractOrderEntryModel entry = defaultPartnerCartFacade.getPidEntry(
            abstractOrderModel, 1);
        assertEquals("1", entry.getEntryNumber().toString());

    }

    @Test
    public void testGetPidEntriesWithNull() {
        final AbstractOrderModel abstractOrderModel = AbstractOrderModelTestDataGenerator.createAbstractOrderModel();
        final List<AbstractOrderEntryModel> entries = new ArrayList<>();
        final AbstractOrderEntryModel abstractOrderEntryModel = AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntry();
        abstractOrderEntryModel.setEntryNumber(1);
        entries.add(abstractOrderEntryModel);
        abstractOrderModel.setEntries(entries);
        final AbstractOrderEntryModel entry = defaultPartnerCartFacade.getPidEntry(
            abstractOrderModel, 3);
        assertNull(entry);
    }

    @Test
    public void testCreateChildEntries() {
        final CartModificationData cartModificationData = CartModificationTestDataGenerator.createCartModificationData();
        final OrderEntryData entryData = OrderEntryTestDataGenerator.createOrderEntryData();
        entryData.setQuantity(3L);
        final ProductData productData = ProductDataTestGenerator.createProductData();
        productData.setCode("6543");
        entryData.setProduct(productData);
        entryData.setEntryNumber(1);
        cartModificationData.setEntry(entryData);
        cartModificationData.setStatusCode("success");
        //create part list
        final List<OrderEntryData> partList = new ArrayList<>();
        final OrderEntryData entryData1 = OrderEntryTestDataGenerator.createOrderEntryData();
        entryData1.setPidId("6543");
        entryData1.setProduct(productData);
        entryData1.setEntryNumber(1);
        entryData1.setQuantity(1L);
        partList.add(entryData1);

        final CartModel cartModel = CartModelTestDataGenerator.createCartModel("432");

        final List<AbstractOrderEntryModel> entries = new ArrayList<>();
        final AbstractOrderEntryModel abstractOrderEntryModel = AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntry();
        abstractOrderEntryModel.setEntryNumber(1);
        // chile entries
        final Collection<AbstractOrderEntryModel> chileEntries = new ArrayList<>();
        final AbstractOrderEntryModel childEntry = AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntry();
        final CartModel cartmodelChild = CartModelTestDataGenerator.createCartModel("4321");
        childEntry.setOrder(cartmodelChild);
        chileEntries.add(childEntry);
        final List<AbstractOrderEntryModel> childEntriesForCart = new ArrayList<>();
        childEntriesForCart.add(childEntry);
        cartmodelChild.setEntries(childEntriesForCart);
        abstractOrderEntryModel.setChildEntries(chileEntries);
        entries.add(abstractOrderEntryModel);
        cartModel.setEntries(entries);
        when(cartService.getSessionCart()).thenReturn(cartModel);
        final List<CartModificationData> modificationDataList = new ArrayList<>();
        defaultPartnerCartFacade.createChildEntries(modificationDataList, cartModificationData,
            partList, false);
        assertNull(sessionService.getAttribute("partCart"));
    }

    @Test(expected = DomainException.class)
    public void testAddOrderEntryBuyerQuoteError() {
        final List<OrderEntryData> cartEntries = new ArrayList<>();
        final CartData cartData = CartTestDataGenerator.createCartData();
        final QuoteData quoteData = QuoteTestDataGenerator.createQuoteData();
        quoteData.setState(QuoteState.CREATED);
        cartData.setQuoteData(quoteData);
        when(defaultPartnerCartFacade.getCurrentCart()).thenReturn(cartData);
        final OrderEntryData orderEntryData = OrderEntryTestDataGenerator.createOrderEntryData();
        orderEntryData.setPartProduct(false);
        cartEntries.add(orderEntryData);
        final UserModel userModel = new UserModel();
        userModel.setUid("433");
        when(userService.getCurrentUser()).thenReturn(userModel);
        final String actingUserId = "433";
        when(sessionService.getAttribute(ACTING_USER_UID)).thenReturn(actingUserId);
        //quote throw error
        defaultPartnerCartFacade.updateOrderEntryList(cartEntries);
    }

    @Test(expected = DomainException.class)
    public void testAddOrderEntrySellerQuoteError() {
        final List<OrderEntryData> cartEntries = new ArrayList<>();
        final CartData cartData = CartTestDataGenerator.createCartData();
        final QuoteData quoteData = QuoteTestDataGenerator.createQuoteData();
        quoteData.setState(QuoteState.CREATED);
        cartData.setQuoteData(quoteData);
        when(defaultPartnerCartFacade.getCurrentCart()).thenReturn(cartData);
        final OrderEntryData orderEntryData = OrderEntryTestDataGenerator.createOrderEntryData();
        orderEntryData.setPartProduct(false);
        cartEntries.add(orderEntryData);
        final UserModel userModel = UserModelTestDataGenerator.createUserModel("433");
        when(userService.getCurrentUser()).thenReturn(userModel);
        final String actingUserId = "233";
        when(sessionService.getAttribute(ACTING_USER_UID)).thenReturn(actingUserId);
        //quote throw error
        defaultPartnerCartFacade.updateOrderEntryList(cartEntries);
    }

    @Test(expected = DomainException.class)
    public void testAddOrderEntryList() {
        final List<OrderEntryData> cartEntries = new ArrayList<>();
        final CartData cartData = CartTestDataGenerator.createCartData();
        final QuoteData quoteData = QuoteTestDataGenerator.createQuoteData();
        quoteData.setState(QuoteState.CREATED);
        cartData.setQuoteData(quoteData);
        when(defaultPartnerCartFacade.getCurrentCart()).thenReturn(cartData);
        final OrderEntryData orderEntryData = OrderEntryTestDataGenerator.createOrderEntryData();
        orderEntryData.setPartProduct(false);
        cartEntries.add(orderEntryData);
        final UserModel userModel = UserModelTestDataGenerator.createUserModel("433");
        defaultPartnerCartFacade.setUserService(userService);
        when(userService.getCurrentUser()).thenReturn(userModel);
        final String actingUserId = "";
        when(sessionService.getAttribute(ACTING_USER_UID)).thenReturn(actingUserId);
		when(cartQuoteUpdateValidator.isCartQuoteUpdateAllowed(any(), any(), any())).thenReturn(true);
        defaultPartnerCartFacade.addOrderEntryList(cartEntries);

    }

    @Test
    public void testUpdateOrderEntryList() {
        final List<OrderEntryData> cartEntries = new ArrayList<>();
        final CartData cartData = CartTestDataGenerator.createCartData();
        final QuoteData quoteData = QuoteTestDataGenerator.createQuoteData();
        quoteData.setState(QuoteState.CREATED);
        cartData.setQuoteData(quoteData);
        when(defaultPartnerCartFacade.getCurrentCart()).thenReturn(cartData);
        final OrderEntryData orderEntryData = new OrderEntryData();
        orderEntryData.setPartProduct(false);
        orderEntryData.setProduct(ProductDataTestGenerator.createProductData("765"));
        orderEntryData.setQuantity(1L);
        orderEntryData.setEntryNumber(1);
        cartEntries.add(orderEntryData);
        final OrderEntryData orderEntryData1 = new OrderEntryData();
        orderEntryData1.setPartProduct(true);
        cartEntries.add(orderEntryData1);
        final UserModel userModel = UserModelTestDataGenerator.createUserModel("433");
        defaultPartnerCartFacade.setUserService(userService);
        when(userService.getCurrentUser()).thenReturn(userModel);
        final String actingUserId = "";
        when(sessionService.getAttribute(ACTING_USER_UID)).thenReturn(actingUserId);
        when(cartQuoteUpdateValidator.isCartQuoteUpdateAllowed(any(), any(), any()))
                .thenReturn(true);
        when(groupCartModificationListConverter.convert(any(), any())).thenReturn(
                Arrays.asList(CartModificationTestDataGenerator.createCartModificationData()));
        final CommerceCartParameter commerceCartParameter =
                CommerceCartParameterTestDataGenerator.createCommerceCartParamterModel();
        given(commerceCartParameterConverter.convert(any(AddToCartParams.class)))
                .willReturn(commerceCartParameter);
        final CartModificationData cartModificationData =
                CartModificationTestDataGenerator.createCartModificationData();
        cartModificationData.setQuantity(10);
        cartModificationData.setQuantity(9L);
        given(cartModificationConverter.convert(any())).willReturn(cartModificationData);
        final List<CartModificationData> res =
                defaultPartnerCartFacade.updateOrderEntryList(cartEntries);
        assertEquals(1, res.size());
    }

    @Test
    public void updatePricesTest() {
        IbmPartnerCartModel cartModel = new IbmPartnerCartModel();
		cartModel.setQuoteReference(quoteModel);
        when(cartService.getSessionCart()).thenReturn(cartModel);
        defaultPartnerCartFacade.updatePrices();
        verify(partnerProcessService).removeCartFromOldProcess(cartModel);
        verify(eventService).publishEvent(any(CartPriceLookUpEvent.class));
    }

    @Test
    public void testUpdatePrices_NoQuoteReference() {
        final CartModel cartModel = CartModelTestDataGenerator.createCartModel("433", null);
        defaultPartnerCartFacade.updatePrices();
        verify(partnerProcessService, never()).removeCartFromOldProcess(cartModel);
        verify(eventService, never()).publishEvent(any(CartPriceLookUpEvent.class));
        Assert.assertNull(cartModel.getQuoteReference());
    }

    @Test
    public void testUpdatePrices_NoCartModel() {
        when(cartService.getSessionCart()).thenReturn(null);
        defaultPartnerCartFacade.updatePrices();
        verify(partnerProcessService, never()).removeCartFromOldProcess(any(CartModel.class));
        verify(eventService, never()).publishEvent(any(CartPriceLookUpEvent.class));
        Assert.assertNull(cartService.getSessionCart());
    }

    @Test
    public void testRemoveOverriddenPrices(){
        double overridenDiscount=10.0;
        double overridenPrice=100;
        IbmPartnerCartModel ibmPartnerCartModel = new IbmPartnerCartModel();
        ibmPartnerCartModel.setCode("67");
        PartnerCpqHeaderPricingDetailModel partnerCpqHeaderPricingDetail= PartnerCpqHeaderPricingDetailModelTestDataGenerator.createPartnerCpqHeaderPricingDetail(overridenDiscount,overridenPrice);
   when(defaultPartnerCartFacade.getPriceLookUpService().getHeaderCpqPricingDetail(
       ibmPartnerCartModel, CpqPricingTypeEnum.FULL)).thenReturn(partnerCpqHeaderPricingDetail);
   defaultPartnerCartFacade.removeOverriddenPrices(ibmPartnerCartModel);
        Assert.assertNull(partnerCpqHeaderPricingDetail.getOverrideTotalDiscount());
        Assert.assertNull(partnerCpqHeaderPricingDetail.getOverrideTotalPrice());
    }

    @Test
    public void testRemoveOverridenEntryPrices(){
        IbmPartnerCartModel ibmPartnerCartModel = new IbmPartnerCartModel();
        ibmPartnerCartModel.setCode("67");
        final List<AbstractOrderEntryModel> entries = new ArrayList<>();
        final AbstractOrderEntryModel abstractOrderEntryModel = new AbstractOrderEntryModel();
        entries.add(0,abstractOrderEntryModel);
        ibmPartnerCartModel.setEntries(entries);
        final List<AbstractOrderEntryModel> childEntries = new ArrayList<>();
        IbmPartnerCartEntryModel ibmPartnerCartEntryModel = new IbmPartnerCartEntryModel();
        ibmPartnerCartEntryModel.setChildEntries(childEntries);
        final AbstractOrderEntryModel abstractOrderEntryModel1 = new AbstractOrderEntryModel();
        final List<CpqPricingDetailModel> pricingDetails = new ArrayList<>();
        PartnerCpqPricingDetailModel partnerCpqPricingDetailModel = new PartnerCpqPricingDetailModel();
        partnerCpqPricingDetailModel.setPricingType("FULL");
        pricingDetails.add(partnerCpqPricingDetailModel);
        abstractOrderEntryModel1.setCpqPricingDetails(pricingDetails);
        childEntries.add(0,abstractOrderEntryModel1);
        abstractOrderEntryModel.setChildEntries(childEntries);
        when(defaultPartnerCartFacade.getPriceLookUpService().getCpqPricingDetail(
            abstractOrderEntryModel1, CpqPricingTypeEnum.FULL)).thenReturn(
            Optional.of(partnerCpqPricingDetailModel));

        defaultPartnerCartFacade.removeOverriddenEntryPrices(ibmPartnerCartModel);
        Assert.assertNull(partnerCpqPricingDetailModel.getOverrideDiscount());
        Assert.assertNull(partnerCpqPricingDetailModel.getOverrideBidUnitPrice());
    }

	 @Test
	 public void testGetEntry()
	 {
		 final CartModel cartModel = new CartModel();
		 final List<AbstractOrderEntryModel> entries = new ArrayList<>();
		 final IbmPartnerCartEntryModel abstractOrderEntryModel = new IbmPartnerCartEntryModel();
		 abstractOrderEntryModel.setEntryNumber(1);
		 entries.add(abstractOrderEntryModel);
		 cartModel.setEntries(entries);
		 final AbstractOrderEntryModel entry = defaultPartnerCartFacade.getEntry(cartModel, 1);
		 assertEquals("1", entry.getEntryNumber().toString());

	 }

	 @Test
	 public void testGetEntry_Null()
	 {
		 final CartModel cartModel = new CartModel();
		 final List<AbstractOrderEntryModel> entries = new ArrayList<>();
		 cartModel.setEntries(null);
		 Assert.assertThrows(IllegalArgumentException.class, () -> defaultPartnerCartFacade.getEntry(cartModel, 0));
	 }

	 @Test
	 public void testGetCart() throws CommerceCartModificationException
	 {
		 final double overridenDiscount = 10.0;
		 final double overridenPrice = 100;
		 final IbmPartnerCartModel cartModel = IbmPartnerCartModelTestDataGenerator.createCartModel("test");
		 final List<AbstractOrderEntryModel> entries = new ArrayList<>();
		 final List<AbstractOrderEntryModel> partEntries = new ArrayList<>();
		 final AbstractOrderEntryModel abstractOrderEntryModel = AbstractOrderEntryModelTestDataGenerator
				 .createAbstractOrderEntry(100);
		 final IbmPartnerCartEntryModel partEntryModel = new IbmPartnerCartEntryModel();
		 abstractOrderEntryModel.setEntryNumber(1);
		 entries.add(abstractOrderEntryModel);
		 cartModel.setEntries(entries);
		 partEntries.add(partEntryModel);
		 abstractOrderEntryModel.setChildEntries(partEntries);
		 abstractOrderEntryModel.setMasterEntry(partEntryModel);
		 partEntryModel.setOrder(IbmPartnerCartModelTestDataGenerator.createCartModel("MyTest"));
		 final PartnerCpqHeaderPricingDetailModel partnerCpqHeaderPricingDetail = PartnerCpqHeaderPricingDetailModelTestDataGenerator
				 .createPartnerCpqHeaderPricingDetail(overridenDiscount, overridenPrice);
		 when(defaultPartnerCartFacade.getPriceLookUpService().getHeaderCpqPricingDetail(cartModel, CpqPricingTypeEnum.FULL))
				 .thenReturn(partnerCpqHeaderPricingDetail);
		 defaultPartnerCartFacade.getCart(cartModel);
		 Assert.assertFalse(cartModel.getCalculated());
	 }

	 @Test(expected = CommerceCartModificationException.class)
	 public void testGetCartWithException() throws CommerceCartModificationException
	 {
		 final double overridenDiscount = 10.0;
		 final double overridenPrice = 100;
		 final IbmPartnerCartModel cartModel = IbmPartnerCartModelTestDataGenerator.createCartModel("test");
		 final List<AbstractOrderEntryModel> entries = new ArrayList<>();
		 final List<AbstractOrderEntryModel> partEntries = new ArrayList<>();
		 final AbstractOrderEntryModel abstractOrderEntryModel = AbstractOrderEntryModelTestDataGenerator
				 .createAbstractOrderEntry(100);
		 final IbmPartnerCartEntryModel partEntryModel = new IbmPartnerCartEntryModel();
		 abstractOrderEntryModel.setEntryNumber(1);
		 entries.add(abstractOrderEntryModel);
		 cartModel.setEntries(entries);
		 partEntries.add(partEntryModel);
		 abstractOrderEntryModel.setChildEntries(partEntries);
		 final PartnerCpqHeaderPricingDetailModel partnerCpqHeaderPricingDetail = PartnerCpqHeaderPricingDetailModelTestDataGenerator
				 .createPartnerCpqHeaderPricingDetail(overridenDiscount, overridenPrice);
		 when(defaultPartnerCartFacade.getPriceLookUpService().getHeaderCpqPricingDetail(cartModel, CpqPricingTypeEnum.FULL))
				 .thenReturn(partnerCpqHeaderPricingDetail);
		 defaultPartnerCartFacade.getCart(cartModel);
	 }

     @Test
     public void testUpdateHeaderPriceDetails() throws CommerceCartModificationException {
         final double overridenDiscount = 10.0;
         final double overridenPrice = 100;
         final PartnerCpqHeaderPricingDetailModel partnerCpqHeaderPricingDetail =
                 PartnerCpqHeaderPricingDetailModelTestDataGenerator
                         .createPartnerCpqHeaderPricingDetail(overridenDiscount, overridenPrice);
         final PartnerOverrideHeaderPriceData partnerOverrideHeaderPriceData =
                 new PartnerOverrideHeaderPriceData();
         partnerOverrideHeaderPriceData.setOverrideTotalDiscount(overridenDiscount);
         partnerOverrideHeaderPriceData.setOverrideTotalPrice(overridenPrice);
         final IbmPartnerCartModel ibmPartnerCartModel =
                 IbmPartnerCartModelTestDataGenerator.createCartModel("test");
         when(cartService.getSessionCart()).thenReturn(ibmPartnerCartModel);
         when(defaultPartnerCartFacade.getPriceLookUpService()
                 .getHeaderCpqPricingDetail(ibmPartnerCartModel, CpqPricingTypeEnum.FULL))
                         .thenReturn(partnerCpqHeaderPricingDetail);
         defaultPartnerCartFacade.updateHeaderPriceDetails(partnerOverrideHeaderPriceData);
         assertTrue(ibmPartnerCartModel.getIsPriceOverridden());
         assertFalse(ibmPartnerCartModel.getCalculated());
     }


     @Test(expected = CommerceCartModificationException.class)
     public void testUpdateEntryPriceDetails() throws CommerceCartModificationException {
         final double overridenDiscount = 10.0;
         final double overridenPrice = 100;
         final PartnerOverrideEntryPriceData partnerOverrideEntryPriceData =
                 new PartnerOverrideEntryPriceData();
         partnerOverrideEntryPriceData.setEntryNumber(1);
         partnerOverrideEntryPriceData.setOverrideDiscount(overridenDiscount);
         partnerOverrideEntryPriceData.setOverridePrice(overridenPrice);

         final IbmPartnerCartModel cartModel =
                 IbmPartnerCartModelTestDataGenerator.createCartModel("test");
         final List<AbstractOrderEntryModel> entries = new ArrayList<>();
         final List<AbstractOrderEntryModel> partEntries = new ArrayList<>();
         final AbstractOrderEntryModel abstractOrderEntryModel =
                 AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntry(100);
         final IbmPartnerCartEntryModel partEntryModel = new IbmPartnerCartEntryModel();
         partEntryModel.setEntryNumber(1);
         abstractOrderEntryModel.setEntryNumber(11);
         entries.add(partEntryModel);
         cartModel.setEntries(entries);
         partEntries.add(abstractOrderEntryModel);
         partEntryModel.setChildEntries(partEntries);
         partEntryModel.setMasterEntry(abstractOrderEntryModel);
         partEntryModel.setOrder(IbmPartnerCartModelTestDataGenerator.createCartModel("MyTest"));
         when(cartService.getSessionCart()).thenReturn(cartModel);
         final PartnerCpqPricingDetailModel partnerCpqPricingDetailModel =
                 new PartnerCpqPricingDetailModel();
         partnerCpqPricingDetailModel.setPricingType("FULL");
         when(defaultPartnerCartFacade.getPriceLookUpService().getCpqPricingDetail(
                 partEntryModel, CpqPricingTypeEnum.FULL))
                         .thenReturn(Optional.of(partnerCpqPricingDetailModel));
         defaultPartnerCartFacade.updateEntryPriceDetails(partnerOverrideEntryPriceData);
     }

    @Test
    public void testAddCollaborator_Failure_ExceedsLimit() {
        QuoteCollaboratorsData data = new QuoteCollaboratorsData();
        List<String> collaboratorEmails = List.of(COLLABORATOR_EMAIL_ONE, COLLABORATOR_EMAIL_TWO,
            COLLABORATOR_EMAIL_THREE);
        data.setCollaboratorEmails(collaboratorEmails);
        Set<String> existingCollaborators = new HashSet<>(collaboratorEmails);
        when(cartService.getSessionCart()).thenReturn(ibmPartnerCartModel);
        when(ibmPartnerCartModel.getQuoteReference()).thenReturn(quoteModel);
        when(ibmPartnerCartModel.getCollaboratorEmails()).thenReturn(existingCollaborators);
        boolean result = defaultPartnerCartFacade.addCollaborator(data);
        assertFalse(result);
        verify(ibmPartnerCartModel, never()).setCollaboratorEmails(any());
        verify(modelService, never()).save(ibmPartnerCartModel);
    }

    @Test(expected = NullPointerException.class)
    public void testAddCollaborator_Success_WithinLimit() {
        QuoteCollaboratorsData data = new QuoteCollaboratorsData();
        List<String> collaboratorEmails = List.of(COLLABORATOR_EMAIL_ONE);
        data.setCollaboratorEmails(collaboratorEmails);
        when(cartService.getSessionCart()).thenReturn(ibmPartnerCartModel);
        when(ibmPartnerCartModel.getQuoteReference()).thenReturn(quoteModel);
        when(ibmPartnerCartModel.getCollaboratorEmails()).thenReturn(null);
        boolean result = defaultPartnerCartFacade.addCollaborator(data);
        assertTrue(result);
        verify(modelService).save(ibmPartnerCartModel);
    }

    @Test
    public void testRemoveCollaborator_Success() {
        QuoteCollaboratorsData data = new QuoteCollaboratorsData();
        List<String> collaboratorEmails = List.of(COLLABORATOR_EMAIL_ONE);
        Set<String> existingCollaboratorEmails = Set.of(COLLABORATOR_EMAIL_ONE);
        data.setCollaboratorEmails(collaboratorEmails);
        when(cartService.getSessionCart()).thenReturn(ibmPartnerCartModel);
        when(ibmPartnerCartModel.getQuoteReference()).thenReturn(quoteModel);
        when(ibmPartnerCartModel.getCollaboratorEmails()).thenReturn(existingCollaboratorEmails);
        boolean result = defaultPartnerCartFacade.removeCollaborator(data);
        assertTrue(result);
        verify(modelService).save(ibmPartnerCartModel);
    }
    @Test
    public void testRemoveCollaborator_Failure() {
        QuoteCollaboratorsData data = new QuoteCollaboratorsData();
        List<String> collaboratorEmails = List.of(COLLABORATOR_EMAIL_ONE);
        Set<String> existingCollaboratorEmails = Set.of(COLLABORATOR_EMAIL_TWO);
        data.setCollaboratorEmails(collaboratorEmails);
        when(cartService.getSessionCart()).thenReturn(ibmPartnerCartModel);
        when(ibmPartnerCartModel.getQuoteReference()).thenReturn(null);
        when(ibmPartnerCartModel.getCollaboratorEmails()).thenReturn(existingCollaboratorEmails);
        boolean result = defaultPartnerCartFacade.removeCollaborator(data);
        assertFalse(result);
    }

}

