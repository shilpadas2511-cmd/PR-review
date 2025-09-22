package com.ibm.commerce.partner.core.order.strategies.impl;

import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteEntryModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.PartnerCpqHeaderPricingDetailModel;
import com.ibm.commerce.partner.core.services.PriceLookUpService;
import com.ibm.commerce.partner.core.util.model.*;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.strategies.ordercloning.CloneAbstractOrderStrategy;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCreateIbmCartFromQuoteStrategyTest {

    private final int ENTRY_NUMBER = 1;
    private final String ORDER_CODE = "1234";

    String pricingType = "FULL";
    Double initialTotalExtendedPrice = 100.00;
    Double totalExtendedPrice = 100.0;
    Double totalMEPPrice = 100.0;
    Double totalDiscount = 5.0;
    Double ytyPercentage = 5.0;
    Double totalBidExtendedPrice = 100.0;
    Double totalOptimalPrice = 100.0;
    Double totalChannelMargin = 100.0;
    Double totalBpExtendedPrice = 100.0;
    String transactionPriceLevel = "TEST";

    @InjectMocks
    DefaultCreateIbmCartFromQuoteStrategy defaultCreateIbmCartFromQuoteStrategy;
    @Mock
    private CloneAbstractOrderStrategy cloneAbstractOrderStrategy;
    @Mock
    private IbmPartnerQuoteModel quoteModel;
    @Mock
    private KeyGenerator keyGenerator;
    @Mock
    private Configuration configuration;
    private IbmPartnerCartModel ibmPartnerCartModel;
    @Mock
    ModelService modelService;
    @Mock
    ProductModel product;
    @Mock
    private Locale locale;
    private UserModel user;

    @Mock
    private PriceLookUpService priceLookUpService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        defaultCreateIbmCartFromQuoteStrategy = new DefaultCreateIbmCartFromQuoteStrategy(
            modelService, priceLookUpService);
        defaultCreateIbmCartFromQuoteStrategy.setKeyGenerator(keyGenerator);
        defaultCreateIbmCartFromQuoteStrategy.setCloneAbstractOrderStrategy(
            cloneAbstractOrderStrategy);
        ibmPartnerCartModel = IbmPartnerCartModelTestDataGenerator.createCartModel(ORDER_CODE);
        AbstractOrderEntryModel abstractOrderEntryModel = AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntry(
            ENTRY_NUMBER);
        List<AbstractOrderEntryModel> entries = new ArrayList<>();
        entries.add(abstractOrderEntryModel);
        ibmPartnerCartModel.setEntries(entries);
        PartnerCpqHeaderPricingDetailModel partnerCpqHeaderPricingDetail = PartnerCpqHeaderPricingDetailModelTestDataGenerator.createPartnerCpqHeaderPricingDetail(
            pricingType, initialTotalExtendedPrice, totalExtendedPrice,
            totalMEPPrice, totalDiscount, ytyPercentage,
            totalBidExtendedPrice, totalOptimalPrice, totalChannelMargin,
            totalBpExtendedPrice, transactionPriceLevel);
        Collection<PartnerCpqHeaderPricingDetailModel> pricingDetail = new ArrayList<>();
        pricingDetail.add(partnerCpqHeaderPricingDetail);
        quoteModel.setPricingDetailsQuote(pricingDetail);
        Mockito.when(priceLookUpService.populateCPQHeaderPricingDetail(Mockito.any()))
            .thenReturn(partnerCpqHeaderPricingDetail);
    }

    @Test
    public void testCreateQuoteFromCart() {
        Collection<AbstractOrderEntryModel> childEntries = new ArrayList<>();
        AbstractOrderEntryModel childEntry = new AbstractOrderEntryModel();
        childEntries.add(childEntry);
        AbstractOrderEntryModel entry = AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntryModel(
            ENTRY_NUMBER, childEntries, product);
        List<AbstractOrderEntryModel> entries = new ArrayList<>();
        entries.add(entry);
        // Use IbmPartnerQuoteModel instead of QuoteModel
        IbmPartnerQuoteModel quoteModel = new IbmPartnerQuoteModel();
        quoteModel.setEntries(entries);
        Mockito.when(cloneAbstractOrderStrategy.clone(Mockito.any(), Mockito.any(), Mockito.any(),
            Mockito.anyString(), Mockito.any(),
            Mockito.any())).thenReturn(ibmPartnerCartModel);
        CartModel cartModel = defaultCreateIbmCartFromQuoteStrategy.createCartFromQuote(quoteModel);
        Assert.assertNotNull(cartModel.getCode());
        Assert.assertEquals(ORDER_CODE, cartModel.getCode());
    }

    @Test
    public void testCreateQuoteFromCartOriginalEmpty() {
        Collection<AbstractOrderEntryModel> childEntries = new ArrayList<>();
        AbstractOrderEntryModel entry = AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntryModel(
            ENTRY_NUMBER, childEntries, product);
        IbmPartnerQuoteModel quoteModel = new IbmPartnerQuoteModel();
        List<AbstractOrderEntryModel> entries = new ArrayList<>();
        quoteModel.setEntries(entries);
        entries.add(entry);
        PartnerCpqHeaderPricingDetailModel partnerCpqHeaderPricingDetail = PartnerCpqHeaderPricingDetailModelTestDataGenerator.createPartnerCpqHeaderPricingDetail(
            pricingType, initialTotalExtendedPrice, totalExtendedPrice,
            totalMEPPrice, totalDiscount, ytyPercentage,
            totalBidExtendedPrice, totalOptimalPrice, totalChannelMargin,
            totalBpExtendedPrice, transactionPriceLevel);
        Collection<PartnerCpqHeaderPricingDetailModel> pricingDetail = new ArrayList<>();
        pricingDetail.add(partnerCpqHeaderPricingDetail);
        quoteModel.setPricingDetailsQuote(pricingDetail);
        Mockito.when(priceLookUpService.populateCPQHeaderPricingDetail(Mockito.any()))
            .thenReturn(partnerCpqHeaderPricingDetail);
        Mockito.when(cloneAbstractOrderStrategy.clone(Mockito.any(), Mockito.any(), Mockito.any(),
            Mockito.anyString(), Mockito.any(),
            Mockito.any())).thenReturn(ibmPartnerCartModel);
        CartModel cartModel = defaultCreateIbmCartFromQuoteStrategy.createCartFromQuote(quoteModel);
        Assert.assertNotNull(cartModel.getCode());
        Assert.assertTrue(StringUtils.EMPTY, entry.getChildEntries().isEmpty());
        ;
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNotCreateCartFromQuote() {
        CartModel cartModel = defaultCreateIbmCartFromQuoteStrategy.createCartFromQuote(null);
        Assert.assertNull(cartModel);
    }

    @Test(expected = NullPointerException.class)
    public void testFailedToCreateCartFromQuote() {
        CartModel cartModel = defaultCreateIbmCartFromQuoteStrategy.createCartFromQuote(quoteModel);
        Assert.assertNull(cartModel);
    }

    @Test
    public void testCreateCartFromQuote_withPidChildEntryOrder() {
        // Setup mocks and data
        int entryNumber = 2;
        ProductModel productModel = new ProductModel();
        productModel.setCode("PROD001");

        // Create the PID child order and entry
        com.ibm.commerce.partner.core.model.IbmPartnerPidQuoteModel pidQuoteModel = new com.ibm.commerce.partner.core.model.IbmPartnerPidQuoteModel();
        com.ibm.commerce.partner.core.model.IbmPartnerPidQuoteEntryModel pidQuoteEntry = new com.ibm.commerce.partner.core.model.IbmPartnerPidQuoteEntryModel();
        pidQuoteEntry.setOrder(pidQuoteModel);
        java.util.Collection<AbstractOrderEntryModel> childEntries = new java.util.ArrayList<>();
        childEntries.add(pidQuoteEntry);

        // Create the main entry with child entries
        AbstractOrderEntryModel entry = AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntryModel(entryNumber, childEntries, productModel);
        java.util.List<AbstractOrderEntryModel> entries = new java.util.ArrayList<>();
        entries.add(entry);

        // Setup the quote (use IbmPartnerQuoteModel)
        IbmPartnerQuoteModel quote = new IbmPartnerQuoteModel();
        quote.setEntries(entries);

        // Setup the expected clone and its entries
        IbmPartnerCartModel expectedClone = IbmPartnerCartModelTestDataGenerator.createCartModel("CLONECODE");
        java.util.List<AbstractOrderEntryModel> cloneEntries = new java.util.ArrayList<>();
        AbstractOrderEntryModel cloneEntry = new AbstractOrderEntryModel();
        cloneEntries.add(cloneEntry);
        expectedClone.setEntries(cloneEntries);

        // Mock the main clone call to return a non-null cart
        Mockito.when(cloneAbstractOrderStrategy.clone(
                Mockito.any(de.hybris.platform.core.model.type.ComposedTypeModel.class),
                Mockito.any(de.hybris.platform.core.model.type.ComposedTypeModel.class),
                Mockito.any(de.hybris.platform.core.model.order.AbstractOrderModel.class),
                Mockito.anyString(),
                Mockito.eq(com.ibm.commerce.partner.core.model.IbmPartnerCartModel.class),
                Mockito.eq(com.ibm.commerce.partner.core.model.IbmPartnerCartEntryModel.class)
        )).thenReturn(ibmPartnerCartModel);

        // Mock the nested clone for the PID child order
        Mockito.when(cloneAbstractOrderStrategy.clone(
                Mockito.any(de.hybris.platform.core.model.type.ComposedTypeModel.class),
                Mockito.any(de.hybris.platform.core.model.type.ComposedTypeModel.class),
                Mockito.any(de.hybris.platform.core.model.order.AbstractOrderModel.class),
                Mockito.anyString(),
                Mockito.eq(com.ibm.commerce.partner.core.model.IbmPartnerPidCartModel.class),
                Mockito.eq(com.ibm.commerce.partner.core.model.IbmPartnerPidCartEntryModel.class)
        )).thenReturn(expectedClone);

        // Mock modelService.save
        Mockito.doNothing().when(modelService).save(Mockito.any());

        // Call the method under test
        CartModel cartModel = defaultCreateIbmCartFromQuoteStrategy.createCartFromQuote(quote);

        // Assert that the child entries were set from the clone
        Assert.assertEquals(cloneEntries, entry.getChildEntries().iterator().next().getOrder().getEntries());
        Mockito.verify(modelService, Mockito.atLeastOnce()).save(Mockito.any());
    }

}
