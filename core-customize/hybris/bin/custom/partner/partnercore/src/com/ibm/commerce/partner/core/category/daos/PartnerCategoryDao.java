package com.ibm.commerce.partner.core.category.daos;

import com.ibm.commerce.common.core.model.IbmCategoryModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;

/**
 * The DAO for the {@link CategoryModel}.
 *
 * @spring.bean categoryDao
 */
public interface PartnerCategoryDao {

    /**
     * Finds specific  category by cpqid and  for the specific {@link CatalogVersionModel}.
     *
     * @param catalogVersion the {@link CatalogVersionModel}
     * @param code the cpq id
     * @return one {@link CategoryModel}, or null if the {@link CatalogVersionModel} contains no
     * categories
     */
    public IbmCategoryModel findCategoriesByCpqId(final CatalogVersionModel catalogVersion, final String code);
}
