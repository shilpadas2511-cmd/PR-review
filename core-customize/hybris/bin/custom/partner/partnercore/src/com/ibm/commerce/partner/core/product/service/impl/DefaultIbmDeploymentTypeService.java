/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.core.product.service.impl;

import com.ibm.commerce.common.core.model.IbmDeploymentTypeModel;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.product.dao.IbmDeploymentTypeDao;
import com.ibm.commerce.partner.core.product.service.IbmDeploymentTypeService;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


/**
 * Service class to get deployment type for a code value
 */
public class DefaultIbmDeploymentTypeService implements IbmDeploymentTypeService
{
	private final IbmDeploymentTypeDao deploymentTypeDao;

	private final String defaultDeploymentType;

	private Set<String> activeDeploymentTypes;

	public DefaultIbmDeploymentTypeService(final IbmDeploymentTypeDao deploymentTypeDao,
        final String defaultDeploymentType)
	{
		this.deploymentTypeDao = deploymentTypeDao;
        this.defaultDeploymentType = defaultDeploymentType;
		setActiveDeploymentTypes();
    }

	@Override
	public IbmDeploymentTypeModel getDeploymentTypeForFacet(final String facetValue)
	{
		return getDeploymentTypeDao().getDeploymentTypeForFacet(facetValue);
	}

	@Override
	public boolean isDeploymentTypeSupported(final String deploymentType) {
		return getActiveDeploymentTypes().contains(deploymentType);
	}

	/**
	 * @return the deploymentTypeDao
	 */
	public IbmDeploymentTypeDao getDeploymentTypeDao() {
		return deploymentTypeDao;
	}

	/**
	 * get the all deployments
	 *
	 * @return deployment types
	 */
	public String getDefaultDeploymentType() {
		return defaultDeploymentType;
	}

	public Set<String> getActiveDeploymentTypes() {
		return activeDeploymentTypes;
	}

	public void setActiveDeploymentTypes() {
        this.activeDeploymentTypes = new HashSet<>(
            Arrays.asList(getDefaultDeploymentType().split(PartnercoreConstants.COMMA)));
	}
}
