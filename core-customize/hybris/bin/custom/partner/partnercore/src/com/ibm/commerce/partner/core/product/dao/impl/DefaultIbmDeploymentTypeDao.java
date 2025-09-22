/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.core.product.dao.impl;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.Assert;

import com.ibm.commerce.common.core.model.IbmDeploymentTypeModel;
import com.ibm.commerce.partner.core.product.dao.IbmDeploymentTypeDao;


/**
 * DAO class to get deployment type model for a given code
 */
public class DefaultIbmDeploymentTypeDao implements IbmDeploymentTypeDao
{
	private final FlexibleSearchService flexibleSearchService;
	private static final String GET_DEPLOYMENT_TYPE_NAME_FOR_CODE = "SELECT {" + ItemModel.PK + "} FROM {"
		+ IbmDeploymentTypeModel._TYPECODE + " } WHERE {" + IbmDeploymentTypeModel.CODE + "}= ?code";
	public DefaultIbmDeploymentTypeDao(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	@Override
	public IbmDeploymentTypeModel getDeploymentTypeForFacet(final String code)
	{
		Assert.notNull(code, "deployment code must not be null");
		final FlexibleSearchQuery query = new FlexibleSearchQuery(GET_DEPLOYMENT_TYPE_NAME_FOR_CODE);
		query.addQueryParameter("code", code);
		final SearchResult<IbmDeploymentTypeModel> result= getFlexibleSearchService().search(query);
		if (null != result && CollectionUtils.isNotEmpty(result.getResult())) {
			return result.getResult().get(0);
		}
		return null;
	}


	/**
	 * @return the flexibleSearchService
	 */
	public FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

}
