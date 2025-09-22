package com.ibm.commerce.partner.core.category.daos.impl;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.daos.impl.DefaultCategoryDao;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;

import com.ibm.commerce.common.core.model.IbmCategoryModel;
import com.ibm.commerce.partner.core.category.daos.PartnerCategoryDao;

/**
 * Default implementation for {@link PartnerCategoryDao}.
 */
public class DefaultPartnerCategoryDao extends DefaultCategoryDao implements PartnerCategoryDao  {
    @Override
    public IbmCategoryModel findCategoriesByCpqId(final CatalogVersionModel catalogVersion, final String code) {
		 final StringBuilder query = new StringBuilder("SELECT {cat." + ItemModel.PK + "} ");
        query.append("FROM {" + IbmCategoryModel._TYPECODE + " AS cat} ");
        query.append("WHERE {cat." + IbmCategoryModel.CPQID + "} = ?" + IbmCategoryModel.CPQID);
		  query.append(" AND {cat." + CategoryModel.CATALOGVERSION + "} = (?" + CategoryModel.CATALOGVERSION + ")");

        final Map<String, Object> params = new HashMap<String, Object>(2);
		  params.put(CategoryModel.CATALOGVERSION, catalogVersion);
        params.put(IbmCategoryModel.CPQID, code);

        final SearchResult<IbmCategoryModel> searchResult = search(query.toString(), params);
        if (null != searchResult && CollectionUtils.isNotEmpty(searchResult.getResult())) {
            return searchResult.getResult().get(0);
        }
        return null;
    }
}
