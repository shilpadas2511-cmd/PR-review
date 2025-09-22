package com.ibm.commerce.partner.initialdata.impl;

import de.hybris.platform.commerceservices.dataimport.impl.SampleDataImportService;
import de.hybris.platform.util.Config;

/**
 * service to add custom Sample impexes that needs to load during init/update
 */
public class PartnerSampleDataImportService extends SampleDataImportService {
    /**
     * @param extensionName
     */
    @Override
    protected void importCommonData(final String extensionName) {
        super.importCommonData(extensionName);
    }
    @Override
    protected void importContentCatalog(final String extensionName, final String contentCatalogName)
    {
        super.importContentCatalog(extensionName,contentCatalogName);
        getSetupImpexService()
                .importImpexFile(String.format("/%s/import/sampledata/contentCatalogs/%sContentCatalog/cms-content-homepage.impex",
                        extensionName, contentCatalogName), false);
    }
    @Override
    protected void importProductCatalog(final String extensionName, final String productCatalogName)
    {
        super.importProductCatalog(extensionName,productCatalogName);
    }
}
