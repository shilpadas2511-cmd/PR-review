package com.ibm.commerce.partner.initialdata.setup;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.initialdata.constants.PartnerInitialDataConstants;
import de.hybris.platform.commerceservices.setup.SetupSolrIndexerService;
import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetup.Process;
import de.hybris.platform.core.initialization.SystemSetup.Type;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.initialization.SystemSetupParameter;
import de.hybris.platform.core.initialization.SystemSetupParameterMethod;
import de.hybris.platform.patches.AbstractPatchesSystemSetup;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * This class provides hooks into the system's update processes for patch Import.
 */
@SystemSetup(extension = PartnerInitialDataConstants.EXTENSIONNAME)
public class InitialDataPatchSystemSetup extends AbstractPatchesSystemSetup {

    private static final Logger LOG = LoggerFactory.getLogger(InitialDataPatchSystemSetup.class);
    private ConfigurationService configurationService;
    private SetupSolrIndexerService setupSolrIndexerService;

    /**
     * Implement this method to create Project data required for system to run smoothly. This method
     * will be called during the system Update.
     *
     * @param setupContext the context provides the selected parameters and values
     */
    @Override
    @SystemSetup(type = Type.PROJECT, process = Process.UPDATE)
    public void createProjectData(final SystemSetupContext setupContext) {
        if (isPatchImportEnabled()) {
            LOG.info(
                "InitialDataPatchSystemSetup :: createProjectData() :: isPatchImportEnabled is {}",
                isPatchImportEnabled());
            super.createProjectData(setupContext);
            triggerFullSolrIndexing();
        } else {
            LOG.info(
                "InitialDataPatchSystemSetup :: createProjectData() :: isPatchImportEnabled is {},"
                    + "So Skipping it..", isPatchImportEnabled());
        }
    }

    /**
     * Method to initialize the SystemSetupParameter during system Update or Initialization.
     */
    @Override
    @SystemSetupParameterMethod
    public List<SystemSetupParameter> getInitializationOptions() {
        return super.getInitializationOptions();
    }

    /**
     * Checks whether the automated patch import feature is enabled.
     *
     * @return {@code true} if patch import is enabled via App configuration; {@code false}
     * otherwise.
     */
    public boolean isPatchImportEnabled() {
        return configurationService.getConfiguration()
            .getBoolean(PartnercoreConstants.AUTOMATE_PATCH_IMPORT_ENABLED, false);
    }

    /**
     * For Solr Full Indexing(partnerIndex and partnerQuoteIndex)
     */
    public void triggerFullSolrIndexing() {
        if (isSolrPartnerIndexingEnabled()) {
            setupSolrIndexerService.executeSolrIndexerCronJob(
                PartnerInitialDataConstants.SOLR_PARTNER_INDEX, true);
        }
        if (isSolrPartnerQuoteIndexingEnabled()) {
            setupSolrIndexerService.executeSolrIndexerCronJob(
                PartnerInitialDataConstants.SOLR_PARTNER_QUOTE_INDEX, true);
        }
    }

    /**
     * Sets the {@link ConfigurationService} used to retrieve configuration properties.
     *
     * @param configurationService the configuration service to be used by this class.
     */
    public void setConfigurationService(
        ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    /**
     * Sets the {@link SetupSolrIndexerService} used to perform Solr index setup operations.
     *
     * @param setupSolrIndexerService the Solr indexer service to be set
     */
    public void setSetupSolrIndexerService(
        SetupSolrIndexerService setupSolrIndexerService) {
        this.setupSolrIndexerService = setupSolrIndexerService;
    }

    /**
     * Checks whether full Solr partnerIndex is enabled after patch import
     *
     * @return {@code true} if full Solr indexing should be triggered after patch import
     * {@code false} otherwise
     */
    public boolean isSolrPartnerIndexingEnabled() {
        return configurationService.getConfiguration()
            .getBoolean(PartnercoreConstants.SOLR_PARTNER_INDEXING_POST_PATCH_IMPORT_ENABLED,
                false);
    }

    /**
     * Checks whether full Solr partnerQuoteIndex is enabled after patch import
     *
     * @return {@code true} if full Solr indexing should be triggered after patch import
     * {@code false} otherwise
     */
    public boolean isSolrPartnerQuoteIndexingEnabled() {
        return configurationService.getConfiguration()
            .getBoolean(PartnercoreConstants.SOLR_PARTNER_QUOTE_INDEXING_POST_PATCH_IMPORT_ENABLED,
                false);
    }

}
