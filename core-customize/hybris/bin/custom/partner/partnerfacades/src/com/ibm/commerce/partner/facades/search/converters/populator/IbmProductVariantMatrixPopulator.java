package com.ibm.commerce.partner.facades.search.converters.populator;

import com.ibm.commerce.common.core.model.IbmVariantProductModel;
import com.ibm.commerce.partner.core.services.PartnerProductSearchAvailabilityService;
import com.ibm.commerce.partner.facades.product.data.IbmDeploymentTypeData;
import de.hybris.platform.commercefacades.product.converters.populator.ProductVariantMatrixPopulator;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.VariantMatrixElementData;
import de.hybris.platform.commercefacades.product.data.VariantOptionData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.variants.model.GenericVariantProductModel;
import de.hybris.platform.variants.model.VariantProductModel;
import de.hybris.platform.variants.model.VariantValueCategoryModel;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;

/**
 * IbmProductVariantMatrixPopulator use to create deployment types and configurator code inder
 * variant option
 */
public class IbmProductVariantMatrixPopulator extends ProductVariantMatrixPopulator {

    private PartnerProductSearchAvailabilityService partnerProductSearchAvailabilityService;

    public IbmProductVariantMatrixPopulator(
        PartnerProductSearchAvailabilityService partnerProductSearchAvailabilityService) {
        this.partnerProductSearchAvailabilityService = partnerProductSearchAvailabilityService;
    }


    @Override
    public void populate(final ProductModel productModel, final ProductData productData)
        throws ConversionException {
        final Collection<VariantProductModel> variants = getVariants(productModel);
        productData.setMultidimensional(Boolean.valueOf(CollectionUtils.isNotEmpty(variants)));

        if (productData.getMultidimensional().booleanValue()) {
            final VariantMatrixElementData nodeZero = createNode(null, null);
            final VariantProductModel starterVariant = getStarterVariant(productModel, variants);

            for (final VariantProductModel variant : variants) {
                if (variant instanceof GenericVariantProductModel) {
                    createNodesForVariant(variant, nodeZero);
                }
            }

            orderTree(nodeZero.getElements());
            productData.setVariantMatrix(nodeZero.getElements());
        }

    }

    @Override
    protected void createNodesForVariant(final VariantProductModel variant,
        VariantMatrixElementData currentParentNode) {
        final List<VariantValueCategoryModel> valuesCategories = getVariantValuesCategories(
            variant);
        for (final VariantValueCategoryModel valueCategory : valuesCategories) {
            final VariantMatrixElementData existingNode = getExistingNode(currentParentNode,
                valueCategory);

            if (existingNode == null) {
                final VariantMatrixElementData createdNode = createNode(currentParentNode,
                    valueCategory);
                createdNode.getVariantOption().setCode(variant.getCode());
                currentParentNode = createdNode;
                createDeploymentTypes(variant, currentParentNode);
            } else {
                currentParentNode = existingNode;
                createDeploymentTypes(variant, currentParentNode);
            }
        }
    }

    protected void createDeploymentTypes(VariantProductModel variant,
        VariantMatrixElementData currentParentNode) {
        if (variant instanceof IbmVariantProductModel variantProductModel) {
            currentParentNode.getVariantOption()
                .setConfiguratorCode(variantProductModel.getConfiguratorCode());
            createDeploymentTypeInformation(currentParentNode.getVariantOption(),
                variantProductModel);
        }
    }

    protected void createDeploymentTypeInformation(VariantOptionData variantOptionData,
        IbmVariantProductModel variantProductModel) {
        IbmDeploymentTypeData deploymentTypeData = new IbmDeploymentTypeData();
        deploymentTypeData.setCode(variantProductModel.getDeploymentType().getCode());
        deploymentTypeData.setName(variantProductModel.getDeploymentType().getName());
        deploymentTypeData.setTagCode(variantProductModel.getDeploymentType().getTagCode());
        variantOptionData.setDeploymentType(deploymentTypeData);
    }

    @Override
    public Collection<VariantProductModel> getVariants(final ProductModel productModel) {
        if (productModel instanceof IbmVariantProductModel) {
            ProductModel baseProduct = ((IbmVariantProductModel) productModel).getBaseProduct();
            return getPartnerProductSearchAvailabilityService().getVariants(baseProduct);
        }
        return Collections.emptyList();
    }

    public PartnerProductSearchAvailabilityService getPartnerProductSearchAvailabilityService() {
        return partnerProductSearchAvailabilityService;
    }


}
