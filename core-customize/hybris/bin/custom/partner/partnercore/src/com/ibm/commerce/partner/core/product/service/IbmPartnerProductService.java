package com.ibm.commerce.partner.core.product.service;

import com.ibm.commerce.common.core.product.service.IbmProductService;
import de.hybris.platform.core.model.product.ProductModel;

/**
 * Extends {@link IbmProductService}
 */
public interface IbmPartnerProductService extends IbmProductService {

    /**
     * Validates if the product is valid multipid product.
     *
     * @param product object of {@link ProductModel}
     * @return true/false
     */
    boolean isValidMultiPid(ProductModel product);

}
