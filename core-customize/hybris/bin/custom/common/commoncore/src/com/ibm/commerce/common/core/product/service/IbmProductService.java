package com.ibm.commerce.common.core.product.service;

import com.ibm.commerce.common.core.model.IbmDeploymentTypeModel;
import com.ibm.commerce.common.core.model.IbmPartProductModel;
import com.ibm.commerce.common.core.model.IbmVariantProductModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import java.util.List;

/**
 * Extends {@link ProductService}
 */
public interface IbmProductService extends ProductService {

    /**
     * Fetches the PartProducts Linked to ProductModel
     *
     * @param productModel
     * @return
     */
    List<IbmPartProductModel> getPartProducts(ProductModel productModel);

    /**
     * Fetches the pidProduct Linked to ProductModel
     *
     * @param productModel
     * @return
     */
    IbmVariantProductModel getPidProduct(ProductModel productModel, String pidCode);

    /**
     * Fetches utlevel30 Category Linked to Product
     *
     * @param productModel
     * @return
     */
    CategoryModel getUtLevel30Category(ProductModel productModel);

    /**
     * Fetches utlevel30 Category Linked to Product
     *
     * @param productModel
     * @return
     */
    CategoryModel getUtLevel30Category(ProductModel productModel, String pidCode);


    /**
     * Fetches the PartProducts Linked to ProductModel
     *
     * @param productModel
     * @return
     */
    IbmDeploymentTypeModel getDeploymentType(ProductModel productModel);

    /**
     * Returns Product Code for different Type of Product
     *
     * @param productModel
     * @return
     */
    String getProductCode(ProductModel productModel);

}
