package com.ibm.commerce.partner.core.util.model;

import de.hybris.platform.catalog.model.CatalogModel;
public class CatalogModelTestDataGenerator {

    public static CatalogModel createCatalogModel(final String catalogId) {
        CatalogModel catalogModel = new CatalogModel();
        catalogModel.setId(catalogId);
        return catalogModel;
    }

}
