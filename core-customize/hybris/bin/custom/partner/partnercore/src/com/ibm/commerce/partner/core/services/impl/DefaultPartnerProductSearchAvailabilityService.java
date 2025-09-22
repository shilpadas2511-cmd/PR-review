package com.ibm.commerce.partner.core.services.impl;

import com.ibm.commerce.common.core.model.IbmPartProductModel;
import com.ibm.commerce.common.core.model.IbmVariantProductModel;
import com.ibm.commerce.common.core.model.SellerAudienceMaskModel;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.product.service.IbmDeploymentTypeService;
import com.ibm.commerce.partner.core.product.service.IbmPartnerProductService;
import com.ibm.commerce.partner.core.services.PartnerProductSearchAvailabilityService;
import com.ibm.commerce.partner.core.utils.PartnerProductDeploymentTypeUtils;
import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.variants.model.VariantProductModel;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;

/**
 * This  DefaultPartnerProductSearchAvailabilityStrategy class use to check Search availability set
 * SaaS product as false if product having the Saas and Bespoke
 */
public class DefaultPartnerProductSearchAvailabilityService implements
    PartnerProductSearchAvailabilityService {


    private final IbmPartnerProductService productService;

    private final IbmDeploymentTypeService deploymentTypeService;

    public DefaultPartnerProductSearchAvailabilityService(IbmPartnerProductService productService,
        final IbmDeploymentTypeService deploymentTypeService) {
        this.productService = productService;
        this.deploymentTypeService = deploymentTypeService;
    }

    /**
     * method for updating searchAvailability value.
     *
     * @param ibmProductModel return boolean
     */
    @Override
    public boolean isSearchAvailabilityForProduct(IbmVariantProductModel ibmProductModel,
        String deploymentType) {
        if (PartnercoreConstants.DEPLOYMENT_TYPE_BESPOKE.equals(deploymentType)) {
            return (getProductService()
                .getProductForCode(ibmProductModel.getCatalogVersion(),
                    ibmProductModel.getBaseProduct().getCode())
                .getVariants().stream()
                .filter(IbmVariantProductModel.class::isInstance)
                .map(IbmVariantProductModel.class::cast)
                .noneMatch(PartnerProductDeploymentTypeUtils::isBespokeProduct));
        } else if (PartnercoreConstants.DEPLOYMENT_TYPE_BESPOKE_MULTIPID.equals(deploymentType)) {
            return (getProductService()
                .getProductForCode(ibmProductModel.getCatalogVersion(),
                    ibmProductModel.getBaseProduct().getCode())
                .getVariants().stream()
                .filter(IbmVariantProductModel.class::isInstance)
                .map(IbmVariantProductModel.class::cast)
                .noneMatch(PartnerProductDeploymentTypeUtils::isBeespokeMultiPid));
        }
        return true;
    }

    /**
     * this method filter bespoke and saas product returns the variants
     *
     * @param productModel
     * @return Collection<VariantProductModel>
     */
    @Override
    public Collection<VariantProductModel> getVariants(final ProductModel productModel) {
        List<VariantProductModel> variants = productModel.getVariants().stream()
            .filter(product -> ArticleApprovalStatus.APPROVED.equals(product.getApprovalStatus()))
            .filter(IbmVariantProductModel.class::isInstance)
            .map(IbmVariantProductModel.class::cast)
            .filter(variant -> variant.getDeploymentType() != null).filter(variant -> {
                String code = variant.getDeploymentType().getCode();
                final boolean deploymentTypeSupported = getDeploymentTypeService().isDeploymentTypeSupported(
                    code);
                if (!deploymentTypeSupported) {
                    return Boolean.FALSE;
                }
                if (PartnerProductDeploymentTypeUtils.isBeespokeMultiPid(variant)
                    && !getProductService().isValidMultiPid(variant)) {
                    return Boolean.FALSE;
                }
                return deploymentTypeSupported;
            }).collect(Collectors.toList());

        boolean hasBespoke = variants.stream().filter(IbmVariantProductModel.class::isInstance)
            .map(IbmVariantProductModel.class::cast)
            .anyMatch(PartnerProductDeploymentTypeUtils::isBespokeProduct);

        boolean hasBespokeMultiPID = variants.stream()
            .filter(IbmVariantProductModel.class::isInstance)
            .map(IbmVariantProductModel.class::cast)
            .anyMatch(PartnerProductDeploymentTypeUtils::isBeespokeMultiPid);

        if (hasBespokeMultiPID) {
            variants.removeIf(var -> var instanceof IbmVariantProductModel &&
                (PartnerProductDeploymentTypeUtils.isSaasProduct((IbmVariantProductModel) var) ||
                    PartnerProductDeploymentTypeUtils.isBespokeProduct(
                        (IbmVariantProductModel) var)));
        } else if (hasBespoke) {
            variants.removeIf(var -> var instanceof IbmVariantProductModel
                && PartnerProductDeploymentTypeUtils.isSaasProduct((IbmVariantProductModel) var));
        }
        return filterAvailableProducts(variants);
    }

    /**
     * filter the products based on the APPROVED, part products exist and seller audience as RES
     *
     * @param variants
     * @return Collection<VariantProductModel>
     */
    public Collection<VariantProductModel> filterAvailableProducts(
        List<VariantProductModel> variants) {
        return variants.stream()
            .filter(IbmVariantProductModel.class::isInstance)
            .map(IbmVariantProductModel.class::cast)
            .filter(this::isSearchAvailable)
            .collect(Collectors.toList());
    }

    /**
     * check product is searchable variant product
     *
     * @param variant
     * @return true/false
     */
    private boolean isSearchAvailable(IbmVariantProductModel variant) {
        if (!ArticleApprovalStatus.APPROVED.equals(variant.getApprovalStatus())) {
            return false;
        }
        List<IbmPartProductModel> partProducts = getProductService().getPartProducts(variant);
        return CollectionUtils.isNotEmpty(filterSoftwareQuotingProducts(partProducts));
    }

    /**
     * check product is searchable Part Products
     *
     * @param partProducts list of Part Products
     * @return true/false
     */
    public List<IbmPartProductModel> filterSoftwareQuotingProducts(
        List<IbmPartProductModel> partProducts) {
        if (CollectionUtils.isEmpty(partProducts)) {
            return Collections.emptyList();
        }
        return partProducts.stream()
            .filter(part -> ArticleApprovalStatus.APPROVED.equals(part.getApprovalStatus())).filter(
                part -> Optional.ofNullable(part.getSellerAudienceMasks())
                    .orElse(Collections.emptyList()).stream().map(SellerAudienceMaskModel::getCode)
                    .anyMatch(PartnercoreConstants.RES::equals)).toList();
    }


    /**
     * get the product service method call
     *
     * @return productService
     */
    public IbmPartnerProductService getProductService() {
        return productService;
    }

    public IbmDeploymentTypeService getDeploymentTypeService() {
        return deploymentTypeService;
    }
}
