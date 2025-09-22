package com.ibm.commerce.common.core.product.service.impl;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.impl.DefaultProductService;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.ibm.commerce.common.core.model.IbmDeploymentTypeModel;
import com.ibm.commerce.common.core.model.IbmPartProductModel;
import com.ibm.commerce.common.core.model.IbmProductModel;
import com.ibm.commerce.common.core.model.IbmVariantProductModel;
import com.ibm.commerce.common.core.product.service.IbmProductService;
import com.ibm.commerce.common.core.utils.CategoryUtils;

/**
 * Implementation for {@link IbmProductService}
 */
public class DefaultIbmProductService extends DefaultProductService implements IbmProductService {

    @Override
    public List<IbmPartProductModel> getPartProducts(final ProductModel productModel) {
        if (productModel instanceof final IbmVariantProductModel variantProductModel) {
            return variantProductModel.getPartProducts();
        }
        if (productModel == null || CollectionUtils.isEmpty(productModel.getVariants())) {
            return Collections.emptyList();
        }

        return productModel.getVariants().stream().filter(IbmVariantProductModel.class::isInstance)
            .map(IbmVariantProductModel.class::cast)
            .filter(variant -> CollectionUtils.isNotEmpty(variant.getPartProducts()))
            .flatMap(variant -> variant.getPartProducts().stream()).toList();
    }

    @Override
    public IbmVariantProductModel getPidProduct(final ProductModel productModel,
        final String pidCode) {

        if (StringUtils.isNotBlank(pidCode)
            && productModel instanceof final IbmPartProductModel partProductModel) {
            return CollectionUtils.isNotEmpty(partProductModel.getPidProducts())
                ? partProductModel.getPidProducts().stream()
                .filter(pidProduct -> pidCode.equals(pidProduct.getPartNumber())).findAny()
                .orElse(null) : null;
        }
        return null;
    }

    @Override
    public CategoryModel getUtLevel30Category(final ProductModel productModel) {
        final Collection<CategoryModel> superCategories = getSuperCategories(productModel);
        if (CollectionUtils.isEmpty(superCategories)) {
            return null;
        }
        return superCategories.stream().filter(CategoryUtils::isUtlevelCategory).findAny()
            .orElse(null);

    }

    @Override
    public CategoryModel getUtLevel30Category(final ProductModel productModel,
        final String pidCode) {
        final Collection<CategoryModel> superCategories = getSuperCategories(productModel, pidCode);
        if (CollectionUtils.isEmpty(superCategories)) {
            return null;
        }
        return superCategories.stream().filter(CategoryUtils::isUtlevelCategory).findAny()
            .orElse(null);
    }

    @Override
    public IbmDeploymentTypeModel getDeploymentType(final ProductModel productModel) {

        if (productModel instanceof final IbmPartProductModel partProductModel) {
            return partProductModel.getDeploymentType();
        }
        if (productModel instanceof final IbmVariantProductModel pidProductModel) {
            return pidProductModel.getDeploymentType();
        }
        return null;
    }

    @Override
    public String getProductCode(final ProductModel productModel) {
        if (productModel instanceof final IbmVariantProductModel pidProduct) {
            return pidProduct.getPartNumber();
        }
        return productModel.getCode();
    }

    protected Collection<CategoryModel> getSuperCategories(final ProductModel productModel,
        final String pidCode) {
        IbmVariantProductModel variantProductModel = null;
        if (productModel instanceof final IbmPartProductModel partProductModel && StringUtils.isNotBlank(
            pidCode)) {
            final int size = CollectionUtils.size(partProductModel.getPidProducts());
            if (size != NumberUtils.INTEGER_ZERO) {
                variantProductModel = partProductModel.getPidProducts().stream()
                    .filter(pidProduct -> pidCode.equals(pidProduct.getPartNumber())).findAny()
							 .orElse(null);
            }
        }
        return getSuperCategories(variantProductModel);
    }

    protected Collection<CategoryModel> getSuperCategories(final ProductModel productModel) {
        ProductModel baseProduct = null;
        if (productModel instanceof final IbmPartProductModel partProductModel) {
            final int size = CollectionUtils.size(partProductModel.getPidProducts());
            if (size != NumberUtils.INTEGER_ZERO) {
                baseProduct = partProductModel.getPidProducts().get(0);
            }
        } else if (productModel instanceof IbmProductModel) {
            baseProduct = productModel;
        } else if (productModel instanceof final IbmVariantProductModel variantProductModel) {
            baseProduct = variantProductModel.getBaseProduct();
        }

        return baseProduct != null ? baseProduct.getSupercategories() : Collections.emptyList();
    }
}
