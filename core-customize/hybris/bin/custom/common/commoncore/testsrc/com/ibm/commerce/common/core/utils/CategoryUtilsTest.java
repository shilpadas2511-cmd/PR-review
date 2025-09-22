package com.ibm.commerce.common.core.utils;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.category.model.ConfigurationCategoryModel;
import de.hybris.platform.variants.model.VariantCategoryModel;
import de.hybris.platform.variants.model.VariantValueCategoryModel;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

@UnitTest
public class CategoryUtilsTest {

    @Test
    public void testIsUtlevelCategory_NotVariantCategory() {
        CategoryModel category = mock(CategoryModel.class);
        boolean result = CategoryUtils.isUtlevelCategory(category);
        assertTrue(result);
    }

    @Test
    public void testIsUtlevelCategory_VariantCategory() {
        VariantCategoryModel category = mock(VariantCategoryModel.class);
        boolean result = CategoryUtils.isUtlevelCategory(category);
        assertFalse(result);
    }

    @Test
    public void testIsUtlevelCategory_VariantValueCategory() {
        VariantValueCategoryModel category = mock(VariantValueCategoryModel.class);
        boolean result = CategoryUtils.isUtlevelCategory(category);
        assertFalse(result);
    }

    @Test
    public void testIsUtlevelCategory_ConfigurationCategory() {
        ConfigurationCategoryModel category = mock(ConfigurationCategoryModel.class);
        boolean result = CategoryUtils.isUtlevelCategory(category);
        assertFalse(result);
    }

    @Test
    public void testIsUtlevelCategory_ClassificationClassModel() {
        ClassificationClassModel category = mock(ClassificationClassModel.class);
        boolean result = CategoryUtils.isUtlevelCategory(category);
        assertFalse(result);
    }

    @Test
    public void testGetUtlevelCategories_EmptySet() {
        Set<CategoryModel> categories = Collections.emptySet();
        Set<CategoryModel> result = CategoryUtils.getUtlevelCategories(categories);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetUtlevelCategories_NoUtlevelCategories() {
        VariantCategoryModel variantCategory = mock(VariantCategoryModel.class);
        Set<CategoryModel> categories = new HashSet<>(Arrays.asList(variantCategory));
        Set<CategoryModel> result = CategoryUtils.getUtlevelCategories(categories);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetUtlevelCategories_HasUtlevelCategories() {
        CategoryModel utlevelCategory = mock(CategoryModel.class);
        Set<CategoryModel> categories = new HashSet<>(Arrays.asList(utlevelCategory));
        Set<CategoryModel> result = CategoryUtils.getUtlevelCategories(categories);
        assertEquals(1, result.size());
        assertTrue(result.contains(utlevelCategory));
    }

    @Test
    public void testGetUtlevelCategoriesCollection_EmptyCollection() {
        Collection<CategoryModel> categories = Collections.emptyList();
        Collection<CategoryModel> result = CategoryUtils.getUtlevelCategories(categories);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetUtlevelCategoriesCollection_NoUtlevelCategories() {
        VariantValueCategoryModel variantValueCategory = mock(VariantValueCategoryModel.class);
        Collection<CategoryModel> categories = Arrays.asList(variantValueCategory);
        Collection<CategoryModel> result = CategoryUtils.getUtlevelCategories(categories);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetUtlevelCategoriesCollection_HasUtlevelCategories() {
        CategoryModel utlevelCategory = mock(CategoryModel.class);
        Collection<CategoryModel> categories = Arrays.asList(utlevelCategory);
        Collection<CategoryModel> result = CategoryUtils.getUtlevelCategories(categories);
        assertEquals(1, result.size());
        assertTrue(result.contains(utlevelCategory));
    }
}

