/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.core.search.solrfacetsearch.resolvers.impl;

import com.ibm.commerce.partner.core.product.service.IbmDeploymentTypeService;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;

import java.util.Locale;

import com.ibm.commerce.common.core.model.IbmDeploymentTypeModel;
import com.ibm.commerce.partner.core.search.solrfacetsearch.resolvers.AbstractPartnerDisplayNameResolver;


/*
 *  This name provider will provide the display name for the facet
 */
public class ProductDeploymentTypeDisplayNameResolver extends AbstractPartnerDisplayNameResolver
{
	private final IbmDeploymentTypeService deploymentTypeService;

	public ProductDeploymentTypeDisplayNameResolver(final IbmDeploymentTypeService deploymentTypeService)
	{
		this.deploymentTypeService = deploymentTypeService;
	}

	@Override
	public String getDisplayName(final SearchQuery query, final IndexedProperty property, final String facetValue)
	{
		final Locale locale = getLocale(query.getLanguage());

		final IbmDeploymentTypeModel deploymentValue = getDeploymentTypeService().getDeploymentTypeForFacet(facetValue);
		return deploymentValue != null ? deploymentValue.getName(locale) : facetValue;
	}

	/**
	 * @return the deploymentTypeService
	 */
	public IbmDeploymentTypeService getDeploymentTypeService() {
		return deploymentTypeService;
	}

}
