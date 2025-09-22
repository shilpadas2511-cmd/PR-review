package com.ibm.commerce.partner.core.order.strategies.impl;

import com.ibm.commerce.partner.core.enums.CpqPricingTypeEnum;
import com.ibm.commerce.partner.core.model.*;
import com.ibm.commerce.partner.core.services.PriceLookUpService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.strategies.QuoteUserIdentificationStrategy;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPartnerUpdateQuoteFromCartStrategyTest {

    private TestableStrategy strategy;

    @Mock
    private PriceLookUpService priceLookUpService;
    @Mock
    private ConfigurationService configurationService;
    @Mock
    private ModelService modelService;
    @Mock
    private KeyGenerator pidQuoteKeyGenerator;
    @Mock
    private KeyGenerator guidKeyGenerator;
    @Mock
    private EventService eventService;
    @Mock
    private QuoteUserIdentificationStrategy quoteUserIdentificationStrategy;

    @Mock
    private IbmPartnerCartModel cart;
    @Mock
    private IbmPartnerQuoteModel outdatedQuote;
    @Mock
    private IbmPartnerQuoteModel updatedQuote;
    @Mock
    private UserModel userModel;
    @Mock
    private IbmPartnerCartEntryModel cartEntry;
    @Mock
    private IbmPartnerQuoteEntryModel quoteEntry;
    @Mock
    private ErrorDetailsModel errorDetailsModel;
    @Mock
    private Configuration configuration;

    @Before
    public void setUp() {
        strategy = new TestableStrategy(priceLookUpService, configurationService, modelService,
            pidQuoteKeyGenerator, guidKeyGenerator, eventService);
        strategy.setQuoteUserIdentificationStrategy(quoteUserIdentificationStrategy);
        strategy.setMockedCloneResult(updatedQuote);

        when(configurationService.getConfiguration()).thenReturn(configuration);
        when(configuration.getBoolean(anyString(), eq(Boolean.TRUE))).thenReturn(Boolean.TRUE);
        when(quoteUserIdentificationStrategy.getCurrentQuoteUser()).thenReturn(userModel);

        when(cart.getQuoteReference()).thenReturn(outdatedQuote);
        when(cart.getEntries()).thenReturn(Collections.singletonList(cartEntry));
        when(outdatedQuote.getCode()).thenReturn("QUOTE-001");
        when(outdatedQuote.getVersion()).thenReturn(1);
        when(outdatedQuote.getState()).thenReturn(QuoteState.BUYER_DRAFT);
        when(outdatedQuote.getSubmittedDate()).thenReturn(new Date());
        when(outdatedQuote.getCreator()).thenReturn(userModel);
        when(outdatedQuote.getPreviousEstimatedTotal()).thenReturn(1000.0);

        when(cartEntry.getEntryNumber()).thenReturn(1);
        when(cartEntry.getErrorDetails()).thenReturn(errorDetailsModel);

        when(quoteEntry.getEntryNumber()).thenReturn(1);
        when(updatedQuote.getEntries()).thenReturn(Collections.singletonList(quoteEntry));

        when(errorDetailsModel.getDescription()).thenReturn("Sample error");
        when(modelService.create(ErrorDetailsModel.class)).thenReturn(new ErrorDetailsModel());
        when(guidKeyGenerator.generate()).thenReturn("GUID-001");
    }

    @Test
    public void testUpdateQuoteFromCart_Success() {
        IbmPartnerQuoteModel result = strategy.updateQuoteFromCart(cart);

        assertNotNull(result);
        assertEquals(updatedQuote, result);

        verify(modelService).save(outdatedQuote);
        verify(modelService).save(cartEntry);
        verify(modelService).remove(errorDetailsModel);
        verify(modelService, atLeastOnce()).create(ErrorDetailsModel.class);
    }

    @Test(expected = IllegalStateException.class)
    public void testUpdateQuoteFromCart_QuoteReferenceNull() {
        when(cart.getQuoteReference()).thenReturn(null);
        strategy.updateQuoteFromCart(cart);
    }

    @Test
    public void testCloneErrorDetails() {
        ErrorDetailsModel original = mock(ErrorDetailsModel.class);
        when(original.getDescription()).thenReturn("desc");
        when(guidKeyGenerator.generate()).thenReturn("ID-123");
        ErrorDetailsModel created = new ErrorDetailsModel();
        when(modelService.create(ErrorDetailsModel.class)).thenReturn(created);

        ErrorDetailsModel result = strategy.cloneErrorDetails(original);

        assertEquals("desc", result.getDescription());
        assertEquals("ID-123", result.getCode());
    }

    @Test
    public void testPostProcess() {
        CartModel cart = new CartModel();
        IbmPartnerQuoteModel quote = new IbmPartnerQuoteModel();
        strategy.postProcess(cart, quote);
        assertEquals(cart, quote.getCartReference());
        assertEquals(quote, cart.getQuoteReference());
    }

    @Test
    public void testGetQuoteForCart_Valid() {
        QuoteModel result = strategy.getQuoteForCart(cart);
        assertEquals(outdatedQuote, result);
    }

    @Test(expected = IllegalStateException.class)
    public void testGetQuoteForCart_Invalid() {
        when(cart.getQuoteReference()).thenReturn(null);
        strategy.getQuoteForCart(cart);
    }

    /**
     * Custom testable subclass to override protected clone method
     */
    private static class TestableStrategy extends DefaultPartnerUpdateQuoteFromCartStrategy {

        private IbmPartnerQuoteModel mockedCloneResult;

        public TestableStrategy(PriceLookUpService pls, ConfigurationService configService,
            ModelService modelService, KeyGenerator pidGen,
            KeyGenerator guidGen, EventService eventService) {
            super(pls, configService, modelService, pidGen, guidGen, eventService);
        }

        public void setMockedCloneResult(IbmPartnerQuoteModel cloneResult) {
            this.mockedCloneResult = cloneResult;
        }

        @Override
        protected IbmPartnerQuoteModel clone(CartModel source, Optional<String> code) {
            return mockedCloneResult;
        }
    }
}
