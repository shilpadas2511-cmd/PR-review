/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.core.product.service;

import com.ibm.commerce.common.core.model.IbmDeploymentTypeModel;


/**
 * Interface to get deployment type for a given value
 */
public interface IbmDeploymentTypeService {

    /**
     * method to get deployment type for a given value
     *
     * @param facetValue
     * @return IbmDeploymentTypeModel
     */
    IbmDeploymentTypeModel getDeploymentTypeForFacet(String facetValue);

    /**
     * Validates if the deploymentType is Supported.
     * @param deploymentType
     * @return
     */
    boolean isDeploymentTypeSupported(String deploymentType);
}
