package com.ibm.commerce.partner.core.events;

import com.ibm.commerce.common.core.model.IbmCategoryModel;
import com.ibm.commerce.partner.core.category.daos.PartnerCategoryDao;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.inboundservices.persistence.PersistenceContext;
import de.hybris.platform.inboundservices.persistence.hook.PrePersistHook;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.collections4.CollectionUtils;


/**
 * Populates category relation of CPQ Inbound object
 */
public class PartnerCpiCategoryPersistenceHook implements PrePersistHook {

    private PartnerCategoryDao categoryDao;
    private CatalogVersionService catalogVersionService;
    private ModelService modelService;

    public PartnerCpiCategoryPersistenceHook(PartnerCategoryDao categoryDao,
        CatalogVersionService catalogVersionService, ModelService modelService) {
        this.categoryDao = categoryDao;
        this.catalogVersionService = catalogVersionService;
        this.modelService = modelService;
    }

    @Override
    public Optional<ItemModel> execute(ItemModel item, PersistenceContext context) {
        if (item instanceof IbmCategoryModel) {
            final IbmCategoryModel categoryModel = (IbmCategoryModel) item;
            recursiveItemUpdate(categoryModel, null);
            return Optional.empty();
        }

        return Optional.of(item);
    }

    private void recursiveItemUpdate(CategoryModel categoryModel, IbmCategoryModel parentCategory) {

        CatalogVersionModel catalogVersionModel = catalogVersionService.getCatalogVersion(
            "partnerProductCatalog", "Staged");
        // Retrieve category model by cpqId
        IbmCategoryModel requiredCategory = (IbmCategoryModel) categoryDao.findCategoriesByCpqId(
            catalogVersionModel, categoryModel.getCode());
        if (requiredCategory != null) {

            // Set code to update the existing category model
            categoryModel.setCode(requiredCategory.getCode());

            //Set the super category for the child category
            populateSuperCategoryToChild(parentCategory, requiredCategory);
        }

        if (CollectionUtils.isNotEmpty(categoryModel.getCategories())) {
            // Recursive call to set the super category for all child category
            recursiveItemUpdate(categoryModel.getCategories().get(0), requiredCategory);
        }
    }

    private void populateSuperCategoryToChild(IbmCategoryModel parentCategory,
        IbmCategoryModel requiredCategory) {
        if (parentCategory != null) {
            List<CategoryModel> categories = new ArrayList<>();
            categories.add(parentCategory);
            if (CollectionUtils.isNotEmpty(requiredCategory.getSupercategories())) {
                categories.addAll(requiredCategory.getSupercategories());
            }
            requiredCategory.setSupercategories(categories);
            modelService.save(requiredCategory);
        }
    }
}
