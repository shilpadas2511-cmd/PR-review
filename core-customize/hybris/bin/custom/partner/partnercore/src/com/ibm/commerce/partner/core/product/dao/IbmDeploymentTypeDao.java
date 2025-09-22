/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.core.product.dao;

import com.ibm.commerce.common.core.model.IbmDeploymentTypeModel;

/**
 * Interface class to get deployment type for a facet value
 */
public interface IbmDeploymentTypeDao {

    /**
     * method to get deployment type for a given value
     *
     * @param facetValue
     * @return IbmDeploymentTypeModel
     */
    IbmDeploymentTypeModel getDeploymentTypeForFacet(String facetValue);
}
