package com.ibm.commerce.partner.core.product.service.impl;

import com.ibm.commerce.common.core.model.IbmDeploymentTypeModel;
import com.ibm.commerce.common.core.model.IbmPartProductModel;
import com.ibm.commerce.common.core.model.IbmVariantProductModel;
import com.ibm.commerce.common.core.product.service.impl.DefaultIbmProductService;
import com.ibm.commerce.partner.core.product.service.IbmPartnerProductService;
import com.ibm.commerce.partner.core.utils.PartnerProductDeploymentTypeUtils;
import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.catalog.model.ProductReferenceModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.List;
import java.util.Objects;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;

/**
 * Extends Implementation for IbmProductService
 */
public class DefaultIbmPartnerProductService extends DefaultIbmProductService implements
    IbmPartnerProductService {

    private static final Logger LOG = Logger.getLogger(DefaultIbmPartnerProductService.class);
    private static final String ERROR_PART_DEPLOYMENT_TYPE = "Part Product is not supposed to have any deployment. Deployment will only be calculated based on PIDs";

    @Override
    public List<IbmPartProductModel> getPartProducts(final ProductModel productModel) {
        if (productModel instanceof final IbmVariantProductModel variantProductModel
            && isValidMultiPid(productModel)) {
            return variantProductModel.getProductReferences().stream()
                .map(ProductReferenceModel::getTarget).filter(Objects::nonNull).filter(
                    product -> ArticleApprovalStatus.APPROVED.equals(product.getApprovalStatus()))
                .flatMap(product -> getPartProducts(product).stream()).toList();
        }
        return super.getPartProducts(productModel);
    }

    @Override
    public IbmDeploymentTypeModel getDeploymentType(final ProductModel productModel) {

        if (productModel instanceof IbmPartProductModel) {
            LOG.error(ERROR_PART_DEPLOYMENT_TYPE);
            throw new UnsupportedOperationException(ERROR_PART_DEPLOYMENT_TYPE);
        }
        if (productModel instanceof final IbmVariantProductModel pidProductModel) {
            return pidProductModel.getDeploymentType();
        }
        return null;
    }

    @Override
    public boolean isValidMultiPid(final ProductModel product) {
        return product instanceof IbmVariantProductModel variantProductModel
            && PartnerProductDeploymentTypeUtils.isBeespokeMultiPid(variantProductModel)
            && CollectionUtils.isNotEmpty(variantProductModel.getProductReferences());
    }
}
