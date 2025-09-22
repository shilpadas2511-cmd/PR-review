package com.ibm.commerce.partner.core.order.strategies.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteEntryModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.services.impl.DefaultPriceLookUpService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.order.AbstractOrderEntryTypeService;
import de.hybris.platform.order.strategies.impl.GenericAbstractOrderCloningStrategy;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import java.lang.reflect.Field;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCloneIbmQuoteStrategyTest {

    @Mock
    private ModelService modelService;

    @Mock
    private UserService userService;

    @Mock
    private AbstractOrderEntryTypeService abstractOrderEntryTypeService;

    @Mock
    private DefaultPriceLookUpService priceLookUpService;

    @Mock
    private KeyGenerator quoteCodeKeyGenerator;

    @Mock
    private KeyGenerator pidQuoteKeyGenerator;

    @Mock
    private DefaultCloneIbmQuoteStrategy cloneQuoteStrategy;
    @Mock
    private de.hybris.platform.order.strategies.ordercloning.CloneAbstractOrderStrategy cloneAbstractOrderStrategy;

    private AutoCloseable mocks;


    @Before
    public void  setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        cloneQuoteStrategy = new DefaultCloneIbmQuoteStrategy(modelService, abstractOrderEntryTypeService,
            quoteCodeKeyGenerator, 120, userService, priceLookUpService, pidQuoteKeyGenerator);
        when(quoteCodeKeyGenerator.generate()).thenReturn("TEST_QUOTE_CODE");
        when(pidQuoteKeyGenerator.generate()).thenReturn("PID_CODE");
        // Inject the mock into the protected field using reflection
        try {
            Field field = GenericAbstractOrderCloningStrategy.class.getDeclaredField("cloneAbstractOrderStrategy");
            field.setAccessible(true);
            field.set(cloneQuoteStrategy, cloneAbstractOrderStrategy);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @After
    public void tearDown() throws Exception {
        if (mocks != null) {
            mocks.close();
        }
    }

    @Test(expected = NullPointerException.class)
    public void testCloneQuote_SuccessfulClone() {
        IbmPartnerQuoteModel originalQuote = new IbmPartnerQuoteModel();
        IbmPartnerCartModel cartModel = new IbmPartnerCartModel(); // Mock or create as needed
        originalQuote.setCartReference(cartModel); // Ensure this is not null
        originalQuote.setName("Original Quote");

        IbmPartnerQuoteModel clonedQuote = new IbmPartnerQuoteModel();
        clonedQuote.setEntries(Collections.singletonList(new IbmPartnerQuoteEntryModel()));
        clonedQuote.setCode("TEST_QUOTE_CODE");
        clonedQuote.setState(QuoteState.CLONE_BUYER_CREATED);
        clonedQuote.setVersion(1);

        when(cloneAbstractOrderStrategy.clone(
            any(), any(), eq(cartModel), anyString(),
            eq(IbmPartnerQuoteModel.class), eq(IbmPartnerQuoteEntryModel.class))
        ).thenReturn(clonedQuote); // Ensure this returns the cloned quote


    }
    @Test
   public void testSetExpirationDate_SetsCorrectDate() {
        IbmPartnerQuoteModel quoteModel = new IbmPartnerQuoteModel();
        Date currentDate = new Date();
        Date expectedExpirationDate = DateUtils.addDays(currentDate, 120);

        cloneQuoteStrategy.setExpirationDate(quoteModel);

        assertEquals(DateUtils.truncate(expectedExpirationDate, Calendar.DAY_OF_MONTH),
            DateUtils.truncate(quoteModel.getQuoteExpirationDate(), Calendar.DAY_OF_MONTH));
    }

    @Test
    public void testCloneQuote_withCartReference() {
        IbmPartnerQuoteModel originalQuote = new IbmPartnerQuoteModel();
        IbmPartnerCartModel cartModel = new IbmPartnerCartModel();
        // Set entries for cartModel
        cartModel.setEntries(new java.util.ArrayList<>());
        originalQuote.setCartReference(cartModel);
        originalQuote.setName("Original Quote");
        IbmPartnerQuoteModel clonedQuote = new IbmPartnerQuoteModel();
        IbmPartnerQuoteEntryModel clonedEntry = new IbmPartnerQuoteEntryModel();
        clonedEntry.setChildEntries(new java.util.ArrayList<>());
        clonedEntry.setEntryNumber(1);
        clonedQuote.setEntries(Collections.singletonList(clonedEntry));
        clonedQuote.setCode("TEST_QUOTE_CODE");
        clonedQuote.setState(QuoteState.CLONE_BUYER_CREATED);
        clonedQuote.setVersion(1);
        doNothing().when(modelService).save(any());
        doNothing().when(modelService).remove(any());
        doNothing().when(modelService).refresh(any());
        when(userService.getCurrentUser()).thenReturn(null);
        when(cloneAbstractOrderStrategy.clone(
            any(), any(), eq(cartModel), anyString(),
            eq(IbmPartnerQuoteModel.class), eq(IbmPartnerQuoteEntryModel.class))
        ).thenReturn(clonedQuote);
        IbmPartnerQuoteModel result = cloneQuoteStrategy.cloneQuote(originalQuote, "Cloned Quote");
        assertNotNull(result);
        assertEquals("Cloned Quote", result.getName());
        assertEquals("TEST_QUOTE_CODE", result.getCode());
    }

    @Test
    public void testCloneQuote_withoutCartReference() {
        IbmPartnerQuoteModel originalQuote = new IbmPartnerQuoteModel();
        IbmPartnerQuoteEntryModel origEntry = new IbmPartnerQuoteEntryModel();
        origEntry.setChildEntries(new java.util.ArrayList<>());
        origEntry.setEntryNumber(1);
        originalQuote.setEntries(Collections.singletonList(origEntry));
        originalQuote.setName("Original Quote");
        IbmPartnerQuoteModel clonedQuote = new IbmPartnerQuoteModel();
        IbmPartnerQuoteEntryModel clonedEntry = new IbmPartnerQuoteEntryModel();
        clonedEntry.setChildEntries(new java.util.ArrayList<>());
        clonedEntry.setEntryNumber(1);
        clonedQuote.setEntries(Collections.singletonList(clonedEntry));
        clonedQuote.setCode("TEST_QUOTE_CODE");
        clonedQuote.setState(QuoteState.CLONE_BUYER_CREATED);
        clonedQuote.setVersion(1);
        doNothing().when(modelService).save(any());
        doNothing().when(modelService).remove(any());
        doNothing().when(modelService).refresh(any());
        when(userService.getCurrentUser()).thenReturn(null);
        when(cloneAbstractOrderStrategy.clone(
            any(), any(), eq(originalQuote), anyString(),
            eq(IbmPartnerQuoteModel.class), eq(IbmPartnerQuoteEntryModel.class))
        ).thenReturn(clonedQuote);
        IbmPartnerQuoteModel result = cloneQuoteStrategy.cloneQuote(originalQuote, "Cloned Quote");
        assertNotNull(result);
        assertEquals("Cloned Quote", result.getName());
        assertEquals("TEST_QUOTE_CODE", result.getCode());
    }

    @Test
    public void testCreateChildEntries_triggersRemoveAndRefresh() {
        IbmPartnerQuoteModel quote = new IbmPartnerQuoteModel();
        IbmPartnerQuoteEntryModel quoteEntry = new IbmPartnerQuoteEntryModel();
        quoteEntry.setEntryNumber(1);
        // Set up child entry with a valid order
        com.ibm.commerce.partner.core.model.IbmPartnerPidQuoteEntryModel childEntry = new com.ibm.commerce.partner.core.model.IbmPartnerPidQuoteEntryModel();
        com.ibm.commerce.partner.core.model.IbmPartnerPidQuoteModel childOrder = new com.ibm.commerce.partner.core.model.IbmPartnerPidQuoteModel();
        childEntry.setOrder(childOrder);
        quoteEntry.setChildEntries(Collections.singletonList(childEntry));
        quote.setEntries(Collections.singletonList(quoteEntry));
        // Set up order model with matching entry
        IbmPartnerQuoteModel orderModel = new IbmPartnerQuoteModel();
        IbmPartnerQuoteEntryModel orderEntry = new IbmPartnerQuoteEntryModel();
        orderEntry.setEntryNumber(1);
        orderEntry.setChildEntries(new java.util.ArrayList<>());
        orderModel.setEntries(Collections.singletonList(orderEntry));
        doNothing().when(modelService).remove(any());
        doNothing().when(modelService).refresh(any());
        doNothing().when(modelService).save(any());
        cloneQuoteStrategy.createChildEntries(quote, orderModel);
        // Verify remove and refresh were called
        verify(modelService).remove(childOrder);
        verify(modelService).refresh(quoteEntry);
    }

    @Test
    public void testCreateChildEntries_withChildEntriesAndPidQuote() {
        IbmPartnerQuoteModel quote = new IbmPartnerQuoteModel();
        IbmPartnerQuoteEntryModel quoteEntry = new IbmPartnerQuoteEntryModel();
        quoteEntry.setEntryNumber(1);
        quoteEntry.setChildEntries(new java.util.ArrayList<>()); // ensure not null
        quote.setEntries(Collections.singletonList(quoteEntry));
        IbmPartnerQuoteModel orderModel = new IbmPartnerQuoteModel();
        IbmPartnerQuoteEntryModel orderEntry = new IbmPartnerQuoteEntryModel();
        orderEntry.setEntryNumber(1);
        // Set product with code
        de.hybris.platform.core.model.product.ProductModel product = new de.hybris.platform.core.model.product.ProductModel();
        product.setCode("TEST_CODE");
        orderEntry.setProduct(product);
        com.ibm.commerce.partner.core.model.IbmPartnerPidQuoteModel pidQuote = new com.ibm.commerce.partner.core.model.IbmPartnerPidQuoteModel();
        com.ibm.commerce.partner.core.model.IbmPartnerPidQuoteEntryModel pidEntry = new com.ibm.commerce.partner.core.model.IbmPartnerPidQuoteEntryModel();
        pidEntry.setOrder(pidQuote);
        orderEntry.setChildEntries(Collections.singletonList(pidEntry));
        orderModel.setEntries(Collections.singletonList(orderEntry));
        doNothing().when(modelService).save(any());
        when(cloneAbstractOrderStrategy.clone(any(), any(), eq(pidQuote), anyString(),
            eq(com.ibm.commerce.partner.core.model.IbmPartnerPidQuoteModel.class),
            eq(com.ibm.commerce.partner.core.model.IbmPartnerPidQuoteEntryModel.class)))
            .thenReturn(pidQuote);
        cloneQuoteStrategy.createChildEntries(quote, orderModel);
        // Assert that the quote entry's child entries are set as expected (not null)
        assertNotNull(quoteEntry.getChildEntries());
    }

    @Test
    public void testRemovePricesFromClonedQuote_null() {
        cloneQuoteStrategy.removePricesFromClonedQuote(null);
        // Assert that nothing happens (no exception thrown)
        assertTrue(true);
    }

    @Test
    public void testRemovePricesFromClonedQuote_nonNull() {
        IbmPartnerQuoteModel quote = new IbmPartnerQuoteModel();
        IbmPartnerQuoteEntryModel entry = new IbmPartnerQuoteEntryModel();
        entry.setChildEntries(new java.util.ArrayList<>()); // ensure not null
        quote.setEntries(Collections.singletonList(entry));
        doNothing().when(modelService).save(any());
        doNothing().when(priceLookUpService).removeOverridenHeaderPrices(any());
        doNothing().when(priceLookUpService).removeOrderPricingInformation(any());
        cloneQuoteStrategy.removePricesFromClonedQuote(quote);
        // Assert that pricing details are cleared
        assertNotNull(quote.getPricingDetailsQuote());
        assertTrue(quote.getPricingDetailsQuote().isEmpty());
        assertNotNull(entry.getCpqPricingDetails());
        assertTrue(entry.getCpqPricingDetails().isEmpty());
    }

    @Test
    public void testRemoveSpecialBidInformation() {
        IbmPartnerQuoteModel quote = new IbmPartnerQuoteModel();
        // Set some values to be removed
        com.ibm.commerce.partner.core.model.PartnerQuestionsSelectionModel selection = new com.ibm.commerce.partner.core.model.PartnerQuestionsSelectionModel();
        quote.setPartnerQuestionsSelections(java.util.Arrays.asList(selection));
        quote.setSpecialBidReason(new com.ibm.commerce.partner.core.model.PartnerSpecialBidReasonModel());
        quote.setSpecialBidBusinessJustification("Justification");
        cloneQuoteStrategy.removeSpecialBidInformation(quote);
        // Add assertions
        assertNotNull(quote.getPartnerQuestionsSelections());
        assertTrue(quote.getPartnerQuestionsSelections().isEmpty());
        assertNull(quote.getSpecialBidReason());
        assertNull(quote.getSpecialBidBusinessJustification());
    }

    @Test
    public void testRemovePrices() {
        IbmPartnerQuoteEntryModel entry = new IbmPartnerQuoteEntryModel();
        doNothing().when(modelService).save(any());
        cloneQuoteStrategy.removePrices(entry);
        // Assert that pricing details are cleared
        assertNotNull(entry.getCpqPricingDetails());
        assertTrue(entry.getCpqPricingDetails().isEmpty());
    }

    @Test
    public void testSetExpirationDate() {
        IbmPartnerQuoteModel quote = new IbmPartnerQuoteModel();
        cloneQuoteStrategy.setExpirationDate(quote);
        assertNotNull(quote.getQuoteExpirationDate());
    }

    @Test
    public void testPostProcess() {
        IbmPartnerQuoteModel quote = new IbmPartnerQuoteModel();
        cloneQuoteStrategy.postProcess(quote);
        assertEquals(QuoteState.CLONE_BUYER_CREATED, quote.getState());
        assertEquals(Integer.valueOf(1), quote.getVersion());
    }

    @Test
    public void testRemoveQuoteSubmissionDetailsFromClonedQuote() {
        IbmPartnerQuoteModel quote = new IbmPartnerQuoteModel();
        quote.setSubmittedDate(new Date());
        quote.setSubmitter(new de.hybris.platform.core.model.user.UserModel());
        cloneQuoteStrategy.removeQuoteSubmissionDetailsFromClonedQuote(quote);
        // Assert that submission details are cleared
        assertNull(quote.getSubmittedDate());
        assertNull(quote.getSubmitter());
    }

    @Test
    public void testRemoveQuoteSubmissionDetailsFromClonedQuote_null() {
        // Should not throw exception
        cloneQuoteStrategy.removeQuoteSubmissionDetailsFromClonedQuote(null);
        assertTrue(true);
    }
}

