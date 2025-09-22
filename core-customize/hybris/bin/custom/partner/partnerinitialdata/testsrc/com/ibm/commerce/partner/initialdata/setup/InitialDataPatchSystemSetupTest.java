package com.ibm.commerce.partner.initialdata.setup;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.initialdata.constants.PartnerInitialDataConstants;
import de.hybris.platform.commerceservices.setup.SetupSolrIndexerService;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.configuration.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class InitialDataPatchSystemSetupTest {

    private InitialDataPatchSystemSetup systemSetup;
    @Mock
    private SystemSetupContext setupContext;
    @Mock
    private SetupSolrIndexerService setupSolrIndexerService;
    private ConfigurationService configurationService;
    private Configuration configuration;

    private InitialDataPatchSystemSetup setupSpy;
    private SystemSetupContext mockContext;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        systemSetup = new InitialDataPatchSystemSetup();
        configurationService = mock(ConfigurationService.class);
        configuration = mock(Configuration.class);

        when(configurationService.getConfiguration()).thenReturn(configuration);
        systemSetup.setConfigurationService(configurationService);
        setupSpy = spy(new InitialDataPatchSystemSetup());
        mockContext = mock(SystemSetupContext.class);
        setupSpy.setSetupSolrIndexerService(setupSolrIndexerService);
    }

    @Test
    void testIsPatchImportEnabled_WhenEnabled() {
        when(configuration.getBoolean(PartnercoreConstants.AUTOMATE_PATCH_IMPORT_ENABLED,
            false)).thenReturn(true);

        boolean result = systemSetup.isPatchImportEnabled();

        assertTrue(result, "Patch import should be enabled");
        verify(configuration).getBoolean(PartnercoreConstants.AUTOMATE_PATCH_IMPORT_ENABLED, false);
    }

    @Test
    void testIsPatchImportEnabled_WhenDisabled() {
        when(configuration.getBoolean(PartnercoreConstants.AUTOMATE_PATCH_IMPORT_ENABLED,
            false)).thenReturn(false);

        boolean result = systemSetup.isPatchImportEnabled();

        assertFalse(result, "Patch import should be disabled");
        verify(configuration).getBoolean(PartnercoreConstants.AUTOMATE_PATCH_IMPORT_ENABLED, false);
    }

    @Test
    public void testCreateProjectData_whenPatchImportEnabled() {
        Mockito.doNothing().when(setupSpy).createProjectData(Mockito.any());
        // Act
        setupSpy.createProjectData(setupContext);
        // Assert
        Mockito.verify(setupSpy, Mockito.times(1)).createProjectData(setupContext);
    }

    @Test
    public void testCreateProjectData_whenPatchImportDisabled() {
        when(configurationService.getConfiguration()).thenReturn(configuration);
        when(configuration.getBoolean(PartnercoreConstants.AUTOMATE_PATCH_IMPORT_ENABLED,
            false)).thenReturn(false);
        verify(setupSpy, times(0)).createProjectData(setupContext);
    }

    @Test
    public void testIsSolrPartnerIndexingEnabled_ReturnsTrue() {
        when(configuration.getBoolean(
            PartnercoreConstants.SOLR_PARTNER_INDEXING_POST_PATCH_IMPORT_ENABLED,
            false)).thenReturn(true);
        boolean result = systemSetup.isSolrPartnerIndexingEnabled();
        assertTrue(result);
    }

    @Test
    public void testIsSolrPartnerQuoteIndexingEnabled_ReturnsTrue() {
        when(configuration.getBoolean(
            PartnercoreConstants.SOLR_PARTNER_QUOTE_INDEXING_POST_PATCH_IMPORT_ENABLED,
            false)).thenReturn(true);
        boolean result = systemSetup.isSolrPartnerQuoteIndexingEnabled();
        assertTrue(result);
    }

    @Test
    void testTriggerFullSolrIndexing_BothEnabled() {
        doReturn(true).when(setupSpy).isSolrPartnerIndexingEnabled();
        doReturn(true).when(setupSpy).isSolrPartnerQuoteIndexingEnabled();

        setupSpy.triggerFullSolrIndexing();

        verify(setupSolrIndexerService).executeSolrIndexerCronJob(
            PartnerInitialDataConstants.SOLR_PARTNER_INDEX, true);
        verify(setupSolrIndexerService).executeSolrIndexerCronJob(
            PartnerInitialDataConstants.SOLR_PARTNER_QUOTE_INDEX, true);
    }
}
