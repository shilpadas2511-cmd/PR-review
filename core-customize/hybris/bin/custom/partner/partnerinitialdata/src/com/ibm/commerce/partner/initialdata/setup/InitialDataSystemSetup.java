/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.initialdata.setup;

import com.ibm.commerce.partner.initialdata.constants.PartnerInitialDataConstants;
import com.ibm.commerce.partner.initialdata.impl.PartnerCoreDataImportService;
import com.ibm.commerce.partner.initialdata.impl.PartnerSampleDataImportService;
import de.hybris.platform.commerceservices.setup.AbstractSystemSetup;
import de.hybris.platform.commerceservices.setup.data.ImportData;
import de.hybris.platform.commerceservices.setup.events.CoreDataImportedEvent;
import de.hybris.platform.commerceservices.setup.events.SampleDataImportedEvent;
import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetup.Process;
import de.hybris.platform.core.initialization.SystemSetup.Type;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.initialization.SystemSetupParameter;
import de.hybris.platform.core.initialization.SystemSetupParameterMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


/**
 * This class provides hooks into the system's initialization and update processes.
 */
@SystemSetup(extension = PartnerInitialDataConstants.EXTENSIONNAME)
public class InitialDataSystemSetup extends AbstractSystemSetup {
    private static final Logger LOG = LoggerFactory.getLogger(InitialDataSystemSetup.class);
    private static final String IMPORT_CORE_DATA = "importCoreData";
    private static final String IMPORT_SAMPLE_DATA = "importSampleData";
    private static final String ACTIVATE_SOLR_CRON_JOBS = "activateSolrCronJobs";
    private static final String CATALOG_AND_STORE_NAME = "partner";

    private final PartnerCoreDataImportService partnerCoreDataImportService;
    private final PartnerSampleDataImportService partnerSampleDataImportService;
    private final boolean dataImportEnabled;


    InitialDataSystemSetup(final PartnerCoreDataImportService partnerCoreDataImportService,
                           final PartnerSampleDataImportService partnerSampleDataImportService,
                           boolean dataImportEnabled) {
        this.partnerCoreDataImportService = partnerCoreDataImportService;
        this.partnerSampleDataImportService = partnerSampleDataImportService;
        this.dataImportEnabled = dataImportEnabled;
    }

    /**
     * Generates the Dropdown and Multi-select boxes for the project data import
     */
    @Override
    @SystemSetupParameterMethod
    public List<SystemSetupParameter> getInitializationOptions() {
        final List<SystemSetupParameter> params = new ArrayList<SystemSetupParameter>();
        params.add(createBooleanSystemSetupParameter(IMPORT_CORE_DATA, "Import Core Data", true));
        params.add(
                createBooleanSystemSetupParameter(IMPORT_SAMPLE_DATA, "Import Sample Data", true));
        params.add(
                createBooleanSystemSetupParameter(ACTIVATE_SOLR_CRON_JOBS, "Activate Solr Cron Jobs",
                        true));
        // Add more Parameters here as you require

        return params;
    }
    /**
     * Implement this method to create initial objects. This method will be called by system creator
     * during initialization and system update. Be sure that this method can be called repeatedly.
     *
     * @param context the context provides the selected parameters and values
     */
    @SystemSetup(type = Type.ESSENTIAL, process = Process.ALL)
    public void createEssentialData(final SystemSetupContext context) {
        // Add Essential Data here as you require
        final String extensionName = context.getExtensionName();
        if (isDataImportEnabled()) {
            LOG.info("*********************************** createEssentialData Start ************************************************");
            getSetupImpexService().importImpexFile(
                    String.format("/%s/import/coredata/common/b2bunits.impex",
                            extensionName),
                    false);
            getSetupImpexService().importImpexFile(
                    String.format("/%s/import/coredata/integration/partnerProduct.impex",
                            extensionName),
                    false);
            getSetupImpexService().importImpexFile(
                    String.format("/%s/import/coredata/common/ibmPartnerUserGroup.impex",
                            extensionName),
                    false);
            getSetupImpexService().importImpexFile(
                    String.format("/%s/import/coredata/common/specialBidReasons.impex",
                            extensionName),
                    false);
            LOG.info("*********************************** createEssentialData End ************************************************");
        }

    }

    /**
     * Implement this method to create data that is used in your project. This method will be called
     * during the system initialization. <br> Add import data for each site you have configured
     *
     * @param context the context provides the selected parameters and values
     */
    @SystemSetup(type = Type.PROJECT, process = Process.ALL)
    public void createProjectData(final SystemSetupContext context) {
        if (isDataImportEnabled()) {
            LOG.info("*********************************** createProjectData Start ************************************************");
            List<ImportData> importData = getImportData();
            importCoreData(context, importData);
            importSampleData(context, importData);
            LOG.info("*********************************** createProjectData End************************************************");
        }
    }

    /**
     * @param context    - The System Context associated with the current process
     * @param importData - List of ImportData to be loaded
     */
    private void importCoreData(SystemSetupContext context, List<ImportData> importData) {
        getPartnerCoreDataImportService().execute(this, context, importData);
        getEventService().publishEvent(new CoreDataImportedEvent(context, importData));
    }

    /**
     * @param context    - The System Context associated with the current process
     * @param importData - List of ImportData to be loaded
     */
    private void importSampleData(SystemSetupContext context, List<ImportData> importData) {
        getPartnerSampleDataImportService().execute(this, context, importData);
        getEventService().publishEvent(new SampleDataImportedEvent(context, importData));
    }

    /**
     * <pre>
     * final List<ImportData> importData = new ArrayList<ImportData>();
     *
     * final ImportData importData = new ImportData();
     * importData.setProductCatalogName(SAMPLE_PRODUCT_CATALOG_NAME);
     * importData.setContentCatalogNames(Arrays.asList(SAMPLE_CONTENT_CATALOG_NAME));
     * importData.setStoreNames(Arrays.asList(SAMPLE_STORE_NAME));
     * importData.add(importData);
     *
     * @return
     */
    private List<ImportData> getImportData() {
        final List<ImportData> importDataList = new ArrayList<ImportData>();
        final ImportData importData = new ImportData();
        importData.setProductCatalogName(CATALOG_AND_STORE_NAME);
        importData.setContentCatalogNames(List.of(CATALOG_AND_STORE_NAME));
        importData.setStoreNames(List.of(CATALOG_AND_STORE_NAME));
        importDataList.add(importData);
        return importDataList;
    }

    PartnerSampleDataImportService getPartnerSampleDataImportService() {
        return partnerSampleDataImportService;
    }

    PartnerCoreDataImportService getPartnerCoreDataImportService() {
        return partnerCoreDataImportService;
    }

    public boolean isDataImportEnabled() {
        LOG.info(
            "*****InitialDataSystemSetup******* dataImportEnabled is :{}",
            dataImportEnabled);
        return dataImportEnabled;
    }
}
