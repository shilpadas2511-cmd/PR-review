package com.ibm.commerce.partner.initialdata.patch.release;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.initialdata.patch.release.structure.StructureState;
import de.hybris.platform.patches.Release;
import de.hybris.platform.patches.organisation.ImportLanguage;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import java.util.Map;
import java.util.Set;

import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PatchImporter extends AbstractPartnerPatch {

    private static final Logger LOG = LoggerFactory.getLogger(PatchImporter.class);

    public Map<Integer, String> getImpexFileMap() {
        return impexFileMap;
    }

    public void setImpexFileMap(Map<Integer, String> impexFileMap) {
        this.impexFileMap = impexFileMap;
    }

    private Map<Integer, String> impexFileMap;
    private ConfigurationService configurationService;

    /**
     * Constructs an instance of {@code AbstractPartnerPatch} with the given patch metadata and name
     * of impex files to be imported
     *
     * @param patchId      the unique identifier for the patch
     * @param patchName    the human-readable name of the patch
     * @param release      the release to which this patch belongs
     * @param impexFileMap name of impex files to be imported
     */
    public PatchImporter(String patchId, String patchName, Release release,
        Map<Integer, String> impexFileMap) {
        super(patchId, patchName, release, StructureState.V1);
        setImpexFileMap(impexFileMap);
        getPatchInformation();

    }

    @Override
    public void createGlobalData(Set<ImportLanguage> languages, boolean updateLanguagesOnly) {
        if (isPatchImportEnabled()) {
            LOG.info(
                "PatchImporter :: createGlobalData() with languages {} & updateLanguagesOnly : {}",
                languages, updateLanguagesOnly);
            impexFileMap.forEach(
                (impexSequence, impexFileName) -> importPatchImpexFile(impexFileName, languages,
                    updateLanguagesOnly));
        } else {
            LOG.info(
                "PatchImporter :: createGlobalData() :: isPatchImportEnabled is {},So Skipping it..",
                isPatchImportEnabled());
        }
    }


    /**
     * Imports a patch-specific Impex file with optional language-specific data handling.
     *
     * @param impexFileName       the name of the Impex file to import
     * @param languages           the set of languages to be considered for localized data import
     * @param updateLanguagesOnly flag indicating whether only language-specific data should be
     *                            updated
     */
    public void importPatchImpexFile(String impexFileName, Set<ImportLanguage> languages,
        boolean updateLanguagesOnly) {
        LOG.info("PatchImporter :: importPatchImpexFile :: Impex File Name : {}", impexFileName);
        importGlobalData(impexFileName, languages, updateLanguagesOnly);
    }


    public ConfigurationService getConfigurationService() {
        return configurationService;
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
     * Sets the {@link ConfigurationService} used to retrieve configuration properties.
     *
     * @param configurationService the configuration service to be used by this class.
     */
    public void setConfigurationService(
        ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    /**
     * Print the Patch Execution Details
     *
     */
    public void getPatchInformation() {
        LOG.info("""
                ========== PatchImporter - Patch Execution Details ==========
                Patch ID        : {}
                Patch Name      : {}
                Release ID      : {}
                Structure State : {}
                Impex Files to Execute:
                {}
                =============================================================
                """,
            getPatchId(),
            getPatchName(),
            getRelease().getReleaseId(),
            getStructureState(),
            impexFileMap.entrySet().stream()
                .map(e -> String.format("  - Sequence: %s | File: %s", e.getKey(), e.getValue()))
                .collect(Collectors.joining("\n"))
        );
    }
}
