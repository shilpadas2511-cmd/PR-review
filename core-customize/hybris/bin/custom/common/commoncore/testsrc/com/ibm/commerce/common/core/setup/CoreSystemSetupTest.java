package com.ibm.commerce.common.core.setup;

import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.initialization.SystemSetupParameter;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CoreSystemSetupTest {

    private SystemSetupContext context;
    private TestableCoreSystemSetup setup;

    private List<String> importedFiles;

    @Before
    public void setUp() {
        context = mock(SystemSetupContext.class);
        importedFiles = new ArrayList<>();
        setup = new TestableCoreSystemSetup(importedFiles);
    }

    @Test
    public void testCreateEssentialData_shouldImportAllEssentialFiles() {
        setup.createEssentialData(context);

        List<String> expectedFiles = Arrays.asList(
                "/commoncore/import/common/essential-data.impex",
                "/commoncore/import/common/countries.impex",
                "/commoncore/import/common/delivery-modes.impex",
                "/commoncore/import/common/themes.impex",
                "/commoncore/import/common/user-groups.impex",
                "/commoncore/import/common/cronjobs.impex"
        );

        assertEquals(expectedFiles, importedFiles);
    }

    @Test
    public void testCreateProjectData_shouldImportCockpitData_whenAccessRightsTrue() {
        setup.setImportAccessRights(true);
        setup.setExtensionNames(Arrays.asList("cmsbackoffice", "productcockpit", "customersupportbackoffice"));

        setup.createProjectData(context);

        assertTrue(importedFiles.contains("/commoncore/import/cockpits/cmscockpit/cmscockpit-users.impex"));
        assertTrue(importedFiles.contains("/commoncore/import/cockpits/cmscockpit/cmscockpit-access-rights.impex"));
        assertTrue(importedFiles.contains("/commoncore/import/cockpits/productcockpit/productcockpit-users.impex"));
        assertTrue(importedFiles.contains("/commoncore/import/cockpits/productcockpit/productcockpit-access-rights.impex"));
        assertTrue(importedFiles.contains("/commoncore/import/cockpits/productcockpit/productcockpit-constraints.impex"));
        assertTrue(importedFiles.contains("/commoncore/import/cockpits/cscockpit/cscockpit-users.impex"));
        assertTrue(importedFiles.contains("/commoncore/import/cockpits/cscockpit/cscockpit-access-rights.impex"));
    }

    @Test
    public void testCreateProjectData_shouldNotImport_whenAccessRightsFalse() {
        setup.setImportAccessRights(false);
        setup.setExtensionNames(Arrays.asList("productcockpit"));
        setup.createProjectData(context);
        assertTrue(importedFiles.isEmpty());
    }

    @Test
    public void testCreateProjectData_shouldImportForSpecificExtensions() {
        setup.setImportAccessRights(true);
        setup.setExtensionNames(Collections.singletonList("productcockpit"));

        setup.createProjectData(context);

        assertTrue(importedFiles.contains("/commoncore/import/cockpits/productcockpit/productcockpit-users.impex"));
        assertTrue(importedFiles.contains("/commoncore/import/cockpits/productcockpit/productcockpit-access-rights.impex"));
        assertTrue(importedFiles.contains("/commoncore/import/cockpits/productcockpit/productcockpit-constraints.impex"));
        assertEquals(3, importedFiles.size());
    }

    @Test
    public void testCreateProjectData_shouldNotImportForEmptyExtensionList() {
        setup.setImportAccessRights(true);
        setup.setExtensionNames(Collections.emptyList());

        setup.createProjectData(context);
        assertTrue(importedFiles.isEmpty());
    }

    @Test
    public void testGetInitializationOptions_shouldReturnExpectedParameters() {
        List<SystemSetupParameter> options = setup.getInitializationOptions();

        assertEquals(1, options.size());
        SystemSetupParameter param = options.get(0);
        assertEquals(CoreSystemSetup.IMPORT_ACCESS_RIGHTS, param.getKey());
        assertEquals("Import Users & Groups", param.getLabel());
    }

    @Test
    public void testGetExtensionNames_shouldReturnListFromRegistry() {
        CoreSystemSetup realSetup = new CoreSystemSetup();
        List<String> extensions = realSetup.getExtensionNames();
        assertNotNull(extensions);
        assertTrue(extensions instanceof List);
    }

    // Custom subclass to override protected methods and inject test state
    static class TestableCoreSystemSetup extends CoreSystemSetup {

        private final List<String> capturedFiles;
        private boolean importAccessRights = true;
        private List<String> extensionNames = Collections.emptyList();

        public TestableCoreSystemSetup(List<String> capturedFiles) {
            this.capturedFiles = capturedFiles;
        }

        @Override
        public void importImpexFile(SystemSetupContext context, String file) {
            capturedFiles.add(file);
        }

        @Override
        public boolean getBooleanSystemSetupParameter(SystemSetupContext context, String key) {
            return importAccessRights;
        }

        public void setImportAccessRights(boolean value) {
            this.importAccessRights = value;
        }

        public void setExtensionNames(List<String> names) {
            this.extensionNames = names;
        }

        @Override
        protected List<String> getExtensionNames() {
            return extensionNames;
        }
    }
}
