package com.ibm.commerce.partner.core.util.model;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
public class CatalogVersionModelTestDataGenerator {

    public static CatalogVersionModel createCatalogVersionModel(final String version, final CatalogModel catalogModel) {
        CatalogVersionModel catalogVersionModel = new CatalogVersionModel();
        catalogVersionModel.setVersion(version);
        catalogVersionModel.setCatalog(catalogModel);
        return catalogVersionModel;
    }

    public static CatalogVersionModel createCatalogVersionModel() {
        CatalogVersionModel catalogVersionModel = new CatalogVersionModel();
        return catalogVersionModel;
    }
}
