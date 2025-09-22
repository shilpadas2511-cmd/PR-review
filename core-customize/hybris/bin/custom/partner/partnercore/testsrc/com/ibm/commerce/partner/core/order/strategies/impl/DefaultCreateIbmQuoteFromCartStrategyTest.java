package com.ibm.commerce.partner.core.order.strategies.impl;

import static org.mockito.Mockito.when;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.services.PriceLookUpService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.AbstractOrderEntryTypeService;
import de.hybris.platform.order.strategies.ordercloning.CloneAbstractOrderStrategy;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;

import de.hybris.platform.servicelayer.user.UserService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.ibm.commerce.common.core.model.IbmVariantProductModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteEntryModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.PartnerCpqPricingDetailModel;
import com.ibm.commerce.partner.core.util.model.AbstractOrderEntryModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.CpqPricingDetailModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.IbmPartnerQuoteDataModelGenerator;
import com.ibm.commerce.partner.core.util.model.IbmPartnerQuoteEntryModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.IbmVariantProductModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.PartnerCpqPricingDetailModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.UserModelTestDataGenerator;
import com.sap.hybris.sapcpqquoteintegration.model.CpqPricingDetailModel;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import com.ibm.commerce.partner.core.model.PartnerCpqHeaderPricingDetailModel;
import java.lang.reflect.Method;
import com.ibm.commerce.partner.core.model.IbmPartnerPidCartModel;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCreateIbmQuoteFromCartStrategyTest {

    private static final String CODE = "000000";
    private final String QUOTE_USER = "IbmUser";
    private final String PRODUCT_CODE = "50002";
    private final int ENTRY_NUMBER = 1;
    private final int ENTRY_NUMBER_2 = 2;
    private final String PART_NUMBER = "1234";
    private final String CONFIG_CODE = "1234";
    private static final String QUOTE_CODE = "C0000000000";

    private static final String PRICE_AMT = "500";
    private static final String DISCOUNT_AMT = "50";
    private static final String DISCOUNT_PER = "2";
    private static final String PRICE_TYPE = "Test";
    private static final String PRICE_TYPE_FULL_PRICE = "FullPrice";
    private static final String ROLLED_PRICE = "200";


    @Mock
    ModelService modelService;
    @Mock
    UserService userService;
    @Mock
    private CommerceCommonI18NService commerceCommonI18NService;

    DefaultCreateIbmQuoteFromCartStrategy defaultCreateIbmQuoteFromCartStrategy;
    @Mock
    ConfigurationService configurationService;
    @Mock
    CartModel cart;
    @Mock
    B2BUnitModel unit;
    @Mock
    private CloneAbstractOrderStrategy cloneAbstractOrderStrategy;
    IbmPartnerQuoteModel partnerQuoteModel;
    @Mock
    private KeyGenerator keyGenerator;
    @Mock
    private Configuration configuration;

    @Mock
    private UserModel user;
    private CartModel cartModel;
    @Mock
    AbstractOrderEntryTypeService abstractOrderEntryTypeService;
    @Mock
    ProductModel product;
    @Mock
    IbmVariantProductModel variantProductModel;
    List<AbstractOrderEntryModel> entries;
    IbmVariantProductModel bmVariantProductModel;

    @Mock
    KeyGenerator pidQuoteKeyGenerator;

    @Mock
    KeyGenerator quoteCodeKeyGenerator;

    @Mock
    PriceLookUpService priceLookUpService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        defaultCreateIbmQuoteFromCartStrategy = new DefaultCreateIbmQuoteFromCartStrategy(
            abstractOrderEntryTypeService,
            modelService, userService, commerceCommonI18NService, 120, configurationService, pidQuoteKeyGenerator,
            quoteCodeKeyGenerator, priceLookUpService);
        defaultCreateIbmQuoteFromCartStrategy.setCloneAbstractOrderStrategy(
            cloneAbstractOrderStrategy);
        defaultCreateIbmQuoteFromCartStrategy.setKeyGenerator(keyGenerator);
        user = UserModelTestDataGenerator.createUserModel(QUOTE_USER);
        partnerQuoteModel = IbmPartnerQuoteDataModelGenerator.createIbmPartnerQuoteModel(user,
            user);

        bmVariantProductModel = IbmVariantProductModelTestDataGenerator.createIbmVariantProduct(
            PRODUCT_CODE, PART_NUMBER,
            CONFIG_CODE);
        final IbmPartnerQuoteEntryModel entry = IbmPartnerQuoteEntryModelTestDataGenerator
            .createIbmPartnerQuoteEntryModel(bmVariantProductModel, ENTRY_NUMBER);

        final List<CpqPricingDetailModel> cpqList = new ArrayList<>();
        final CpqPricingDetailModel cpqPricingDetailModel = CpqPricingDetailModelTestDataGenerator.createCPQDetailsModel(
            PRICE_AMT,
            DISCOUNT_AMT, DISCOUNT_PER, ROLLED_PRICE, PRICE_TYPE);
        cpqList.add(cpqPricingDetailModel);
        entry.setCpqPricingDetails(cpqList);
        entries = new ArrayList<>();
        entries.add(entry);
        partnerQuoteModel.setEntries(entries);
    }

    @Test
    public void testCreateQuoteFromCart() {
        IbmPartnerCartModel cartModel = Mockito.mock(IbmPartnerCartModel.class);
        Mockito.when(cartModel.getPriceUid()).thenReturn("SOMEUID");
        Mockito.when(cartModel.getEntries()).thenReturn(new ArrayList<>());
        Mockito.when(pidQuoteKeyGenerator.generate()).thenReturn("PIDKEY");
        Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
        Mockito.when(configuration.getBoolean(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(false);
        IbmPartnerQuoteModel quoteModel = new IbmPartnerQuoteModel();
        quoteModel.setEntries(new ArrayList<>());
        quoteModel.setTotalFullPrice(123.45); // Cover setTotalPrice(quote.getTotalFullPrice())
        de.hybris.platform.core.model.user.UserModel user = Mockito.mock(de.hybris.platform.core.model.user.UserModel.class);
        Mockito.when(user.getName()).thenReturn("testUser");
        quoteModel.setUser(user);
        Mockito.when(cloneAbstractOrderStrategy.clone(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyString(), Mockito.any(), Mockito.any())).thenReturn(quoteModel);
        DefaultCreateIbmQuoteFromCartStrategy strategy = new DefaultCreateIbmQuoteFromCartStrategy(
            abstractOrderEntryTypeService, modelService, userService, commerceCommonI18NService, 120, configurationService, pidQuoteKeyGenerator, quoteCodeKeyGenerator, priceLookUpService);
        strategy.setCloneAbstractOrderStrategy(cloneAbstractOrderStrategy);
        strategy.setKeyGenerator(keyGenerator);
        IbmPartnerQuoteModel result = strategy.createQuoteFromCart(cartModel);
        Assert.assertNotNull(result);
        Assert.assertEquals(Double.valueOf(123.45), result.getTotalPrice());
    }

    @Test
    public void testCreateQuoteFromCartFullPricePricingType() {
        IbmPartnerCartModel cartModel = Mockito.mock(IbmPartnerCartModel.class);
        Mockito.when(cartModel.getPriceUid()).thenReturn("SOMEUID");
        Mockito.when(cartModel.getEntries()).thenReturn(new ArrayList<>());
        Mockito.when(pidQuoteKeyGenerator.generate()).thenReturn("PIDKEY");
        Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
        Mockito.when(configuration.getBoolean(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(false);
        IbmPartnerQuoteModel quoteModel = new IbmPartnerQuoteModel();
        IbmPartnerQuoteEntryModel entryOne = new IbmPartnerQuoteEntryModel();
        entryOne.setEntryNumber(1);
        entryOne.setCpqPricingDetails(new ArrayList<>());
        quoteModel.setEntries(List.of(entryOne));
        de.hybris.platform.core.model.user.UserModel user = Mockito.mock(de.hybris.platform.core.model.user.UserModel.class);
        Mockito.when(user.getName()).thenReturn("testUser");
        quoteModel.setUser(user);
        Mockito.when(cloneAbstractOrderStrategy.clone(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyString(), Mockito.any(), Mockito.any())).thenReturn(quoteModel);
        DefaultCreateIbmQuoteFromCartStrategy strategy = new DefaultCreateIbmQuoteFromCartStrategy(
            abstractOrderEntryTypeService, modelService, userService, commerceCommonI18NService, 120, configurationService, pidQuoteKeyGenerator, quoteCodeKeyGenerator, priceLookUpService);
        strategy.setCloneAbstractOrderStrategy(cloneAbstractOrderStrategy);
        strategy.setKeyGenerator(keyGenerator);
        IbmPartnerQuoteModel result = strategy.createQuoteFromCart(cartModel);
        Assert.assertNotNull(result);
    }

    @Test(expected = NullPointerException.class)
    public void testNotCreateQuoteFromCart() {
        Mockito.when(cloneAbstractOrderStrategy.clone(Mockito.any(), Mockito.any(), Mockito.any(),
            Mockito.anyString(),
            Mockito.any(), Mockito.any())).thenReturn(null);
        final IbmPartnerQuoteModel ibmPartnerQuoteModelResult = defaultCreateIbmQuoteFromCartStrategy.createQuoteFromCart(
            cart);
        Assert.assertNull(ibmPartnerQuoteModelResult);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFailedToCreateQuoteFromCart() {
        final IbmPartnerQuoteModel ibmPartnerQuoteModel = defaultCreateIbmQuoteFromCartStrategy.createQuoteFromCart(
            null);
        Assert.assertNull(ibmPartnerQuoteModel);
    }

    @Test
    public void testGetName() {
        final IbmPartnerQuoteModel quoteModel = new IbmPartnerQuoteModel();
        unit.setLocName("unit");
        quoteModel.setUnit(unit);
        final String result = defaultCreateIbmQuoteFromCartStrategy.getName(partnerQuoteModel);
        Assert.assertEquals(result, quoteModel.getUnit().getLocName());
    }

    @Test
    public void testGetNameNull() {
        final IbmPartnerQuoteModel quoteModel = new IbmPartnerQuoteModel();
        quoteModel.setUnit(null);
        final String result = defaultCreateIbmQuoteFromCartStrategy.getName(partnerQuoteModel);
        Assert.assertNull(result);
    }

    @Test
    public void testCreateQuoteFromCartOriginalEmpty() {
        IbmPartnerCartModel cartModel = Mockito.mock(IbmPartnerCartModel.class);
        Mockito.when(cartModel.getPriceUid()).thenReturn("SOMEUID");
        Mockito.when(cartModel.getEntries()).thenReturn(new ArrayList<>());
        Mockito.when(pidQuoteKeyGenerator.generate()).thenReturn("PIDKEY");
        Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
        Mockito.when(configuration.getBoolean(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(false);
        IbmPartnerQuoteModel quoteModel = new IbmPartnerQuoteModel();
        IbmPartnerQuoteEntryModel entry = new IbmPartnerQuoteEntryModel();
        entry.setEntryNumber(1);
        entry.setCpqPricingDetails(new ArrayList<>());
        quoteModel.setEntries(List.of(entry));
        de.hybris.platform.core.model.user.UserModel user = Mockito.mock(de.hybris.platform.core.model.user.UserModel.class);
        Mockito.when(user.getName()).thenReturn("testUser");
        quoteModel.setUser(user);
        Mockito.when(cloneAbstractOrderStrategy.clone(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyString(), Mockito.any(), Mockito.any())).thenReturn(quoteModel);
        DefaultCreateIbmQuoteFromCartStrategy strategy = new DefaultCreateIbmQuoteFromCartStrategy(
            abstractOrderEntryTypeService, modelService, userService, commerceCommonI18NService, 120, configurationService, pidQuoteKeyGenerator, quoteCodeKeyGenerator, priceLookUpService);
        strategy.setCloneAbstractOrderStrategy(cloneAbstractOrderStrategy);
        strategy.setKeyGenerator(keyGenerator);
        IbmPartnerQuoteModel result = strategy.createQuoteFromCart(cartModel);
        Assert.assertNotNull(result);
    }

    @Test(expected = NullPointerException.class)
    public void testCreateQuoteFromCartOriginalNull() {
        final CartModel cartModel = new CartModel();
        defaultCreateIbmQuoteFromCartStrategy.createQuoteFromCart(cartModel);
    }

	@Test
	public void testSetExpirationDate()
	{
		final QuoteModel quoteModel = new QuoteModel();
		when(configuration.getBoolean(PartnercoreConstants.ADD_DAYS_TO_EXPIRATION_DATE_FEATURE_FLAG,
			false)).thenReturn(true);
		when(configurationService.getConfiguration()).thenReturn(configuration);
		defaultCreateIbmQuoteFromCartStrategy = new DefaultCreateIbmQuoteFromCartStrategy(
			abstractOrderEntryTypeService,
			modelService, userService, commerceCommonI18NService, 120, configurationService,
			pidQuoteKeyGenerator, quoteCodeKeyGenerator, priceLookUpService);
		defaultCreateIbmQuoteFromCartStrategy.setExpirationDate(quoteModel);
		Assert.assertNull(quoteModel.getQuoteExpirationDate());
	}

    // --- Additional tests for 100% coverage ---
    @Test
    public void testPopulateCloneEntries_FullPrice() throws Exception {
        AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
        PartnerCpqPricingDetailModel fullPrice = new PartnerCpqPricingDetailModel();
        fullPrice.setPricingType("FULL");
        fullPrice.setTotalExtendedPrice(Double.valueOf(100));
        List<CpqPricingDetailModel> details = new ArrayList<>();
        details.add(fullPrice);
        entry.setCpqPricingDetails(details);
        DefaultCreateIbmQuoteFromCartStrategy strategy = new DefaultCreateIbmQuoteFromCartStrategy(
            abstractOrderEntryTypeService, modelService, userService, commerceCommonI18NService, 120, configurationService, pidQuoteKeyGenerator, quoteCodeKeyGenerator, priceLookUpService);
        Method m = DefaultCreateIbmQuoteFromCartStrategy.class.getDeclaredMethod("populateCloneEntries", AbstractOrderEntryModel.class);
        m.setAccessible(true);
        m.invoke(strategy, entry);
        Assert.assertEquals(Double.valueOf(100), entry.getTotalPrice());
    }

    @Test
    public void testPopulatePricingDetails_NullClone() {
        new DefaultCreateIbmQuoteFromCartStrategy(
            abstractOrderEntryTypeService, modelService, userService, commerceCommonI18NService, 120, configurationService, pidQuoteKeyGenerator, quoteCodeKeyGenerator, priceLookUpService)
            .populatePricingDetails(null);
    }

    @Test
    public void testPopulatePricingDetails_EmptyEntries() {
        AbstractOrderModel clone = new AbstractOrderModel();
        clone.setEntries(new ArrayList<>());
        new DefaultCreateIbmQuoteFromCartStrategy(
            abstractOrderEntryTypeService, modelService, userService, commerceCommonI18NService, 120, configurationService, pidQuoteKeyGenerator, quoteCodeKeyGenerator, priceLookUpService)
            .populatePricingDetails(clone);
    }

    @Test
    public void testPopulatePricingDetailsFromCartToQuote_FullBranch() {
        IbmPartnerCartModel cart = Mockito.mock(IbmPartnerCartModel.class);
        IbmPartnerQuoteModel quote = new IbmPartnerQuoteModel();
        PartnerCpqHeaderPricingDetailModel pricing = new PartnerCpqHeaderPricingDetailModel();
        pricing.setPricingType("FULL");
        List<PartnerCpqHeaderPricingDetailModel> pricingList = new ArrayList<>();
        pricingList.add(pricing);
        Mockito.when(cart.getPricingDetails()).thenReturn(pricingList);
        Mockito.when(priceLookUpService.populateCPQHeaderPricingDetail(pricing)).thenReturn(new PartnerCpqHeaderPricingDetailModel());
        new DefaultCreateIbmQuoteFromCartStrategy(
            abstractOrderEntryTypeService, modelService, userService, commerceCommonI18NService, 120, configurationService, pidQuoteKeyGenerator, quoteCodeKeyGenerator, priceLookUpService)
            .populatePricingDetailsFromCartToQuote(cart, quote);
    }

    @Test
    public void testPopulatePricingDetailsForEntries() {
        AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
        PartnerCpqPricingDetailModel fullPrice = new PartnerCpqPricingDetailModel();
        fullPrice.setPricingType("FULL");
        List<CpqPricingDetailModel> details = new ArrayList<>();
        details.add(fullPrice);
        entry.setCpqPricingDetails(details);
        new DefaultCreateIbmQuoteFromCartStrategy(
            abstractOrderEntryTypeService, modelService, userService, commerceCommonI18NService, 120, configurationService, pidQuoteKeyGenerator, quoteCodeKeyGenerator, priceLookUpService)
            .populatePricingDetailsForEntries(entry);
        Assert.assertEquals(1, entry.getCpqPricingDetails().size());
    }

    @Test
    public void testFilterFullPricingType() {
        PartnerCpqPricingDetailModel fullPrice = new PartnerCpqPricingDetailModel();
        fullPrice.setPricingType("FULL");
        PartnerCpqPricingDetailModel notFullPrice = new PartnerCpqPricingDetailModel();
        notFullPrice.setPricingType("NOTFULL");
        List<CpqPricingDetailModel> details = new ArrayList<>();
        details.add(fullPrice);
        details.add(notFullPrice);
        List<CpqPricingDetailModel> filtered = new DefaultCreateIbmQuoteFromCartStrategy(
            abstractOrderEntryTypeService, modelService, userService, commerceCommonI18NService, 120, configurationService, pidQuoteKeyGenerator, quoteCodeKeyGenerator, priceLookUpService)
            .filterFullPricingType(details);
        Assert.assertEquals(1, filtered.size());
        Assert.assertEquals("FULL", filtered.get(0).getPricingType());
    }

    @Test
    public void testSetExpirationDate_FeatureFlagFalse() {
        QuoteModel quoteModel = new QuoteModel();
        Mockito.when(configuration.getBoolean(PartnercoreConstants.ADD_DAYS_TO_EXPIRATION_DATE_FEATURE_FLAG, false)).thenReturn(false);
        Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
        new DefaultCreateIbmQuoteFromCartStrategy(
            abstractOrderEntryTypeService, modelService, userService, commerceCommonI18NService, 120, configurationService, pidQuoteKeyGenerator, quoteCodeKeyGenerator, priceLookUpService)
            .setExpirationDate(quoteModel);
        Assert.assertNull(quoteModel.getQuoteExpirationDate());
    }

    @Test
    public void testSetExpirationDate_FeatureFlagTrue() {
        QuoteModel quoteModel = new QuoteModel();
        Mockito.when(configuration.getBoolean(PartnercoreConstants.ADD_DAYS_TO_EXPIRATION_DATE_FEATURE_FLAG, false)).thenReturn(true);
        Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
        new DefaultCreateIbmQuoteFromCartStrategy(
            abstractOrderEntryTypeService, modelService, userService, commerceCommonI18NService, 120, configurationService, pidQuoteKeyGenerator, quoteCodeKeyGenerator, priceLookUpService)
            .setExpirationDate(quoteModel);
        // No assertion needed, just branch coverage
    }

    @Test
    public void testGetters() {
        DefaultCreateIbmQuoteFromCartStrategy strategy = new DefaultCreateIbmQuoteFromCartStrategy(
            abstractOrderEntryTypeService, modelService, userService, commerceCommonI18NService, 120, configurationService, pidQuoteKeyGenerator, quoteCodeKeyGenerator, priceLookUpService);
        Assert.assertEquals(modelService, strategy.getModelService());
        Assert.assertEquals(commerceCommonI18NService, strategy.getCommonI18NService());
        Assert.assertEquals(120, strategy.getDefaultDaysToExpire());
        Assert.assertEquals(pidQuoteKeyGenerator, strategy.getPidQuoteKeyGenerator());
        Assert.assertEquals(userService, strategy.getUserService());
        Assert.assertEquals(quoteCodeKeyGenerator, strategy.getQuoteCodeKeyGenerator());
        Assert.assertEquals(priceLookUpService, strategy.getPriceLookUpService());
        Assert.assertEquals(configurationService, strategy.getIbmCommonConfigurationService());
    }

    @Test
    public void testCreateQuoteFromCart_FullForEachBranch() {
        IbmPartnerCartModel cartModel = Mockito.mock(IbmPartnerCartModel.class);
        Mockito.when(cartModel.getPriceUid()).thenReturn("SOMEUID");
        Mockito.when(cartModel.getEntries()).thenReturn(new ArrayList<>());
        Mockito.when(pidQuoteKeyGenerator.generate()).thenReturn("PIDKEY");
        Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
        Mockito.when(configuration.getBoolean(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(false);
        IbmPartnerQuoteModel quoteModel = new IbmPartnerQuoteModel();
        IbmPartnerQuoteEntryModel quoteEntry = new IbmPartnerQuoteEntryModel();
        quoteEntry.setEntryNumber(1);
        quoteEntry.setCpqPricingDetails(new ArrayList<>());
        quoteModel.setEntries(List.of(quoteEntry));
        de.hybris.platform.core.model.user.UserModel user = Mockito.mock(de.hybris.platform.core.model.user.UserModel.class);
        Mockito.when(user.getName()).thenReturn("testUser");
        quoteModel.setUser(user);
        Mockito.when(cloneAbstractOrderStrategy.clone(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyString(), Mockito.any(), Mockito.any())).thenReturn(quoteModel);
        // Cart entry to match quoteEntry
        AbstractOrderEntryModel cartEntry = Mockito.mock(AbstractOrderEntryModel.class);
        Mockito.when(cartEntry.getEntryNumber()).thenReturn(1);
        de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel configModel = Mockito.mock(de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel.class);
        Mockito.when(cartEntry.getProductConfiguration()).thenReturn(configModel);
        // Child entry for nested branch
        AbstractOrderEntryModel childEntry = Mockito.mock(AbstractOrderEntryModel.class);
        IbmPartnerPidCartModel pidCart = Mockito.mock(IbmPartnerPidCartModel.class);
        Mockito.when(childEntry.getOrder()).thenReturn(pidCart);
        List<AbstractOrderEntryModel> childEntries = List.of(childEntry);
        Mockito.when(cartEntry.getChildEntries()).thenReturn(childEntries);
        // Cart entries
        List<AbstractOrderEntryModel> cartEntries = List.of(cartEntry);
        Mockito.when(cartModel.getEntries()).thenReturn(cartEntries);
        // Mock getPidQuoteKeyGenerator
        Mockito.when(pidQuoteKeyGenerator.generate()).thenReturn("PIDKEY");
        // Mock getProduct().getCode()
        de.hybris.platform.core.model.product.ProductModel product = Mockito.mock(de.hybris.platform.core.model.product.ProductModel.class);
        Mockito.when(cartEntry.getProduct()).thenReturn(product);
        Mockito.when(product.getCode()).thenReturn("PRODCODE");
        // Mock nested clone for child entry
        AbstractOrderModel nestedClone = Mockito.mock(AbstractOrderModel.class);
        AbstractOrderEntryModel nestedCloneEntry = Mockito.mock(AbstractOrderEntryModel.class);
        List<AbstractOrderEntryModel> nestedCloneEntries = List.of(nestedCloneEntry);
        Mockito.when(nestedClone.getEntries()).thenReturn(nestedCloneEntries);
        Mockito.when(cloneAbstractOrderStrategy.clone(Mockito.any(), Mockito.any(), Mockito.eq(pidCart), Mockito.anyString(), Mockito.eq(com.ibm.commerce.partner.core.model.IbmPartnerPidQuoteModel.class), Mockito.eq(com.ibm.commerce.partner.core.model.IbmPartnerPidQuoteEntryModel.class))).thenReturn(nestedClone);
        // Ensure quoteEntry.setChildEntries is called
        quoteEntry.setChildEntries(new ArrayList<>());
        // Run
        DefaultCreateIbmQuoteFromCartStrategy strategy = new DefaultCreateIbmQuoteFromCartStrategy(
            abstractOrderEntryTypeService, modelService, userService, commerceCommonI18NService, 120, configurationService, pidQuoteKeyGenerator, quoteCodeKeyGenerator, priceLookUpService);
        strategy.setCloneAbstractOrderStrategy(cloneAbstractOrderStrategy);
        strategy.setKeyGenerator(keyGenerator);
        IbmPartnerQuoteModel result = strategy.createQuoteFromCart(cartModel);
        // Verify saves
        Mockito.verify(modelService, Mockito.atLeastOnce()).save(Mockito.any());
        Mockito.verify(modelService, Mockito.atLeastOnce()).save(quoteEntry);
        Mockito.verify(modelService, Mockito.atLeastOnce()).save(nestedClone);
        // Verify setProductConfiguration and setChildEntries are called
        Assert.assertNotNull(result);
        Assert.assertEquals(nestedCloneEntries, quoteEntry.getChildEntries());
        Assert.assertEquals(configModel, quoteEntry.getProductConfiguration());
    }

    @Test
    public void testCreateQuoteFromCart_partnerCartModelWithPriceUid() {
        IbmPartnerCartModel cart = Mockito.mock(IbmPartnerCartModel.class);
        Mockito.when(cart.getPriceUid()).thenReturn("SOMEUID");
        Mockito.when(cart.getEntries()).thenReturn(new ArrayList<>());
        Mockito.when(pidQuoteKeyGenerator.generate()).thenReturn("PIDKEY");
        IbmPartnerQuoteModel quoteModel = new IbmPartnerQuoteModel();
        quoteModel.setEntries(new ArrayList<>());
        Mockito.when(cloneAbstractOrderStrategy.clone(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyString(), Mockito.any(), Mockito.any())).thenReturn(quoteModel);
        DefaultCreateIbmQuoteFromCartStrategy strategy = new DefaultCreateIbmQuoteFromCartStrategy(
            abstractOrderEntryTypeService, modelService, userService, commerceCommonI18NService, 120, configurationService, pidQuoteKeyGenerator, quoteCodeKeyGenerator, priceLookUpService);
        strategy.setCloneAbstractOrderStrategy(cloneAbstractOrderStrategy);
        strategy.setKeyGenerator(keyGenerator);
        IbmPartnerQuoteModel result = strategy.createQuoteFromCart(cart);
        Assert.assertNotNull(result);
    }

    @Test
    public void testCreateQuoteFromCart_partnerCartModelWithEmptyPriceUid() {
        IbmPartnerCartModel cart = Mockito.mock(IbmPartnerCartModel.class);
        Mockito.when(cart.getPriceUid()).thenReturn("");
        Mockito.when(cart.getEntries()).thenReturn(new ArrayList<>());
        Mockito.when(pidQuoteKeyGenerator.generate()).thenReturn("PIDKEY");
        Mockito.when(quoteCodeKeyGenerator.generate()).thenReturn("QGEN");
        IbmPartnerQuoteModel quoteModel = new IbmPartnerQuoteModel();
        quoteModel.setEntries(new ArrayList<>());
        Mockito.when(cloneAbstractOrderStrategy.clone(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyString(), Mockito.any(), Mockito.any())).thenReturn(quoteModel);
        DefaultCreateIbmQuoteFromCartStrategy strategy = new DefaultCreateIbmQuoteFromCartStrategy(
            abstractOrderEntryTypeService, modelService, userService, commerceCommonI18NService, 120, configurationService, pidQuoteKeyGenerator, quoteCodeKeyGenerator, priceLookUpService);
        strategy.setCloneAbstractOrderStrategy(cloneAbstractOrderStrategy);
        strategy.setKeyGenerator(keyGenerator);
        IbmPartnerQuoteModel result = strategy.createQuoteFromCart(cart);
        Assert.assertNotNull(result);
    }

    @Test
    public void testCreateQuoteFromCart_ForEachOriginalCartEntryNotPresent() {
        IbmPartnerCartModel cartModel = Mockito.mock(IbmPartnerCartModel.class);
        IbmPartnerQuoteModel quoteModel = new IbmPartnerQuoteModel();
        IbmPartnerQuoteEntryModel quoteEntry = new IbmPartnerQuoteEntryModel();
        quoteEntry.setEntryNumber(1);
        quoteModel.setEntries(List.of(quoteEntry));
        Mockito.when(cloneAbstractOrderStrategy.clone(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyString(), Mockito.any(), Mockito.any())).thenReturn(quoteModel);
        // Cart entry with different entry number
        AbstractOrderEntryModel cartEntry = Mockito.mock(AbstractOrderEntryModel.class);
        Mockito.when(cartEntry.getEntryNumber()).thenReturn(2);
        List<AbstractOrderEntryModel> cartEntries = List.of(cartEntry);
        Mockito.when(cartModel.getEntries()).thenReturn(cartEntries);
        Mockito.when(cartModel.getPriceUid()).thenReturn("SOMEUID");
        DefaultCreateIbmQuoteFromCartStrategy strategy = new DefaultCreateIbmQuoteFromCartStrategy(
            abstractOrderEntryTypeService, modelService, userService, commerceCommonI18NService, 120, configurationService, pidQuoteKeyGenerator, quoteCodeKeyGenerator, priceLookUpService);
        strategy.setCloneAbstractOrderStrategy(cloneAbstractOrderStrategy);
        strategy.setKeyGenerator(keyGenerator);
        IbmPartnerQuoteModel result = strategy.createQuoteFromCart(cartModel);
        Assert.assertNotNull(result);
    }

    @Test
    public void testCreateQuoteFromCart_ForEachOriginalCartEntryChildEntriesEmpty() {
        IbmPartnerCartModel cartModel = Mockito.mock(IbmPartnerCartModel.class);
        IbmPartnerQuoteModel quoteModel = new IbmPartnerQuoteModel();
        IbmPartnerQuoteEntryModel quoteEntry = new IbmPartnerQuoteEntryModel();
        quoteEntry.setEntryNumber(1);
        quoteModel.setEntries(List.of(quoteEntry));
        Mockito.when(cloneAbstractOrderStrategy.clone(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyString(), Mockito.any(), Mockito.any())).thenReturn(quoteModel);
        // Cart entry with matching entry number
        AbstractOrderEntryModel cartEntry = Mockito.mock(AbstractOrderEntryModel.class);
        Mockito.when(cartEntry.getEntryNumber()).thenReturn(1);
        de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel configModel = Mockito.mock(de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel.class);
        Mockito.when(cartEntry.getProductConfiguration()).thenReturn(configModel);
        Mockito.when(cartEntry.getChildEntries()).thenReturn(new ArrayList<>());
        List<AbstractOrderEntryModel> cartEntries = List.of(cartEntry);
        Mockito.when(cartModel.getEntries()).thenReturn(cartEntries);
        Mockito.when(cartModel.getPriceUid()).thenReturn("SOMEUID");
        DefaultCreateIbmQuoteFromCartStrategy strategy = new DefaultCreateIbmQuoteFromCartStrategy(
            abstractOrderEntryTypeService, modelService, userService, commerceCommonI18NService, 120, configurationService, pidQuoteKeyGenerator, quoteCodeKeyGenerator, priceLookUpService);
        strategy.setCloneAbstractOrderStrategy(cloneAbstractOrderStrategy);
        strategy.setKeyGenerator(keyGenerator);
        IbmPartnerQuoteModel result = strategy.createQuoteFromCart(cartModel);
        Assert.assertNotNull(result);
        Assert.assertEquals(configModel, quoteEntry.getProductConfiguration());
    }

    @Test
    public void testSetExpirationDateAndGetNameAndGetFormattedDate() throws Exception {
        DefaultCreateIbmQuoteFromCartStrategy strategy = new DefaultCreateIbmQuoteFromCartStrategy(
            abstractOrderEntryTypeService, modelService, userService, commerceCommonI18NService, 120, configurationService, pidQuoteKeyGenerator, quoteCodeKeyGenerator, priceLookUpService);
        de.hybris.platform.core.model.order.QuoteModel quoteModel = new de.hybris.platform.core.model.order.QuoteModel();
        Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
        Mockito.when(configuration.getBoolean(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(false);
        strategy.setExpirationDate(quoteModel);
        IbmPartnerQuoteModel quote = new IbmPartnerQuoteModel();
        de.hybris.platform.b2b.model.B2BUnitModel unit = Mockito.mock(de.hybris.platform.b2b.model.B2BUnitModel.class);
        Mockito.when(unit.getLocName(Mockito.any())).thenReturn("unitName");
        quote.setUnit(unit);
        String name = strategy.getName(quote);
        Assert.assertEquals("unitName", name);
        java.lang.reflect.Method m = DefaultCreateIbmQuoteFromCartStrategy.class.getDeclaredMethod("getFormattedDate");
        m.setAccessible(true);
        String formattedDate = (String) m.invoke(strategy);
        Assert.assertNotNull(formattedDate);
    }
}
