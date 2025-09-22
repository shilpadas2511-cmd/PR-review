package com.ibm.commerce.partner.core.inboundservices.persistence.hook;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;

import com.ibm.commerce.common.core.model.IbmProductModel;
import com.ibm.commerce.common.core.model.IbmVariantProductModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.inboundservices.persistence.PersistenceContext;
import de.hybris.platform.inboundservices.persistence.hook.PrePersistHook;
import de.hybris.platform.integrationservices.item.DefaultIntegrationItem;
import de.hybris.platform.odata2services.odata.persistence.StorageRequest;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;

/**
 * PrePersistHook to update product super categories before saving.
 */
public class IbmProductRemoveSuperCategoriesPrePersistHook implements PrePersistHook {


    private final ModelService modelService;

    public IbmProductRemoveSuperCategoriesPrePersistHook(ModelService modelService) {
        this.modelService = modelService;
    }

    @Override
    public Optional<ItemModel> execute(ItemModel item, PersistenceContext context) {
        if (item instanceof IbmProductModel productModel) {
            try {
                if (isMultiPidProduct(productModel)) {
                    return Optional.of(item);
                }
                Set<String> receivedCategoryCodes = getCategoryCodesFromContext(context,
                    ProductModel.SUPERCATEGORIES);
                if(CollectionUtils.isNotEmpty(productModel.getSupercategories())){
                    Collection<CategoryModel> filteredCategories = productModel.getSupercategories()
                        .stream()
                        .filter(category -> receivedCategoryCodes.contains(category.getCode()))
                        .collect(Collectors.toSet());
                    productModel.setSupercategories(filteredCategories);
                    getModelService().save(productModel);
                }
            } catch (RuntimeException e) {
                throw new IllegalStateException(PartnercoreConstants.ERROR_WHILE_UPDATING_PRODUCT_SUPERCATEGORIES, e);
            }
        }
        return Optional.of(item);
    }

    /**
     * Check if product has a MultiPid variant.
     */
    public boolean isMultiPidProduct(IbmProductModel productModel) {
        return Optional.ofNullable(productModel.getVariants())
            .orElse(Collections.emptyList())
            .stream()
            .filter(IbmVariantProductModel.class::isInstance)
            .map(IbmVariantProductModel.class::cast)
            .map(IbmVariantProductModel::getDeploymentType)
            .filter(Objects::nonNull)
            .map(dt -> dt.getCode())
            .filter(Objects::nonNull)
            .anyMatch(code -> PartnercoreConstants.DEPLOYMENT_TYPE_BESPOKE_MULTIPID.equalsIgnoreCase(code));
    }

    /**
     * Extract category codes from PersistenceContext attribute.
     */
    public Set<String> getCategoryCodesFromContext(PersistenceContext context,
        String attributeName) {
        Object attributeValue = ((StorageRequest) context)
            .getPersistenceContext()
            .getIntegrationItem()
            .getAttribute(attributeName);

        if (attributeValue instanceof Collection<?>) {
            return ((Collection<?>) attributeValue).stream()
                .filter(DefaultIntegrationItem.class::isInstance)
                .map(DefaultIntegrationItem.class::cast)
                .map(integrationItem -> Objects.toString(integrationItem.getAttribute(PartnercoreConstants.CODE), null))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    public ModelService getModelService() {
        return modelService;
    }


}
