package com.ibm.commerce.partner.core.inboundservices.persistence.hook;

import static com.ibm.commerce.partner.core.constants.PartnercoreConstants.CPQ_QUOTE_COMMON_API_FEATURE_DISABLED;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.ibm.commerce.partner.core.event.QuoteSubmittedEvent;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.inboundservices.persistence.impl.DefaultPersistenceContext;
import de.hybris.platform.integrationservices.item.IntegrationItem;
import de.hybris.platform.odata2services.odata.persistence.StorageRequest;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class PartnerQuoteStatusPrePersistHookTest {

    // === Constants ===
    private  final String TEST_QUOTE_CODE_1 = "QUOTE-123";
    private  final String TEST_QUOTE_CODE_2 = "QUOTE-456";
    private  final String TEST_EMAIL = "test@example.com";
    private  final String INTEGRATION_KEY = "IN_REVIEW";

    // === Mocks ===
    @Mock
    private QuoteService quoteService;
    @Mock
    private ModelService modelService;
    @Mock
    private EventService eventService;
    @Mock
    private ConfigurationService configurationService;
    @Mock
    private Configuration configuration;
    @Mock
    private StorageRequest persistenceContext;
    @Mock
    private DefaultPersistenceContext defaultPersistenceContext;
    @Mock
    private IntegrationItem integrationItem;

    private PartnerQuoteStatusPrePersistHook prePersistHook;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(configurationService.getConfiguration()).thenReturn(configuration);
        when(persistenceContext.getPersistenceContext()).thenReturn(defaultPersistenceContext);
        when(defaultPersistenceContext.getIntegrationItem()).thenReturn(integrationItem);

        prePersistHook = new PartnerQuoteStatusPrePersistHook(
            quoteService, modelService, eventService, configurationService);
    }

    @Test
    public void testExecuteQuoteStatus_SuccessfulExecution() {
        IbmPartnerQuoteModel quoteModel = new IbmPartnerQuoteModel();
        quoteModel.setCode(TEST_QUOTE_CODE_1);
        quoteModel.setVersion(1);
        quoteModel.setCollaboratorEmails(new HashSet<>(Collections.singleton(TEST_EMAIL)));
        quoteModel.setState(QuoteState.IN_REVIEW);

        when(quoteService.getCurrentQuoteForCode(TEST_QUOTE_CODE_1)).thenReturn(quoteModel);
        when(integrationItem.getAttribute(anyString())).thenReturn(integrationItem);
        when(integrationItem.getIntegrationKey()).thenReturn(INTEGRATION_KEY);
        when(configuration.getBoolean(eq(CPQ_QUOTE_COMMON_API_FEATURE_DISABLED),
            anyBoolean())).thenReturn(true);

        Optional<ItemModel> result = prePersistHook.execute(quoteModel, persistenceContext);

        verify(modelService).detach(quoteModel);
        verify(modelService).save(quoteModel);
        verify(eventService).publishEvent(any(QuoteSubmittedEvent.class));
        assertTrue(result.isPresent());
        assertEquals(quoteModel, result.get());
    }

    @Test
    public void testExecuteQuoteStatus_WithMissingAttributes() {
        IbmPartnerQuoteModel quoteModel = new IbmPartnerQuoteModel();
        quoteModel.setCode(TEST_QUOTE_CODE_2);

        when(quoteService.getCurrentQuoteForCode(TEST_QUOTE_CODE_2)).thenReturn(quoteModel);
        when(integrationItem.getAttribute(anyString())).thenReturn(null);

        Optional<ItemModel> result = prePersistHook.execute(quoteModel, persistenceContext);

        verify(modelService).detach(quoteModel);
        verify(modelService).save(quoteModel);
        verify(eventService, never()).publishEvent(any());
        assertTrue(result.isPresent());
        assertEquals(quoteModel, result.get());
    }

    @Test
    public void testExecute_WithNonPartnerQuoteModel() {
        AbstractOrderModel nonQuoteModel = new AbstractOrderModel();

        Optional<ItemModel> result = prePersistHook.execute(nonQuoteModel, persistenceContext);

        assertFalse(result.isPresent());
    }

    @Test
    public void testIsQuoteCommonApiFeatureDisabled_ReturnsTrue() {
        when(configuration.getBoolean(CPQ_QUOTE_COMMON_API_FEATURE_DISABLED, true)).thenReturn(
            true);
        assertTrue(prePersistHook.isQuoteCommonApiFeatureDisabled());
    }

    @Test
    public void testIsQuoteCommonApiFeatureDisabled_ReturnsFalse() {
        when(configuration.getBoolean(CPQ_QUOTE_COMMON_API_FEATURE_DISABLED, true)).thenReturn(
            false);
        assertFalse(prePersistHook.isQuoteCommonApiFeatureDisabled());
    }

    @Test
    public void testGetEventService() {
        assertEquals(eventService, prePersistHook.getEventService());
    }

    @Test
    public void testGetModelService() {
        assertEquals(modelService, prePersistHook.getModelService());
    }

    @Test
    public void testGetQuoteService() {
        assertEquals(quoteService, prePersistHook.getQuoteService());
    }

    @Test
    public void testGetConfigurationService() {
        assertEquals(configurationService, prePersistHook.getConfigurationService());
    }
}
