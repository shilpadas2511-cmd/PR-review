package com.ibm.commerce.partner.initialdata.patch.release;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import de.hybris.platform.patches.Release;
import de.hybris.platform.patches.organisation.ImportLanguage;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.configuration.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class PatchImporterTest {

    private PatchImporter patchImporter;
    private ConfigurationService configurationService;
    private Configuration configuration;

    private final String PATCH_ID = "PATCH001";
    private final String PATCH_NAME = "Test Patch";
    private final Release release = mock(Release.class);

    @BeforeEach
    void setUp() {
        configurationService = mock(ConfigurationService.class);
        configuration = mock(Configuration.class);

        Map<Integer, String> impexMap = new LinkedHashMap<>();
        impexMap.put(1, "test1.impex");
        impexMap.put(2, "test2.impex");

        when(release.getReleaseId()).thenReturn("REL-001");

        patchImporter = spy(new PatchImporter(PATCH_ID, PATCH_NAME, release, impexMap));
        patchImporter.setConfigurationService(configurationService);
        when(configurationService.getConfiguration()).thenReturn(configuration);
    }

    @Test
    void testIsPatchImportEnabled_WhenTrue() {
        when(configuration.getBoolean(PartnercoreConstants.AUTOMATE_PATCH_IMPORT_ENABLED, false))
            .thenReturn(true);

        assertTrue(patchImporter.isPatchImportEnabled());
    }

    @Test
    void testIsPatchImportEnabled_WhenFalse() {
        when(configuration.getBoolean(PartnercoreConstants.AUTOMATE_PATCH_IMPORT_ENABLED, false))
            .thenReturn(false);

        assertFalse(patchImporter.isPatchImportEnabled());
    }

    @Test
    void testCreateGlobalData_WhenEnabled_ImportsFiles() {
        when(configuration.getBoolean(PartnercoreConstants.AUTOMATE_PATCH_IMPORT_ENABLED, false))
            .thenReturn(true);

        // Spy method call
        doNothing().when(patchImporter).importPatchImpexFile(anyString(), anySet(), anyBoolean());

        Set<ImportLanguage> languages = new HashSet<>();

        patchImporter.createGlobalData(languages, false);

        verify(patchImporter, times(2)).importPatchImpexFile(anyString(), eq(languages), eq(false));
    }

    @Test
    void testCreateGlobalData_WhenDisabled_DoesNotImportFiles() {
        when(configuration.getBoolean(PartnercoreConstants.AUTOMATE_PATCH_IMPORT_ENABLED, false))
            .thenReturn(false);

        Set<ImportLanguage> languages = new HashSet<>();

        patchImporter.createGlobalData(languages, true);

        verify(patchImporter, never()).importPatchImpexFile(anyString(), anySet(), anyBoolean());
    }

}
