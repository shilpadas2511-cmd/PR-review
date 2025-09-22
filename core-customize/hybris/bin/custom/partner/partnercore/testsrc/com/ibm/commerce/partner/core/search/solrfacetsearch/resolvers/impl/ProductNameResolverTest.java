package com.ibm.commerce.partner.core.search.solrfacetsearch.resolvers.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.ibm.commerce.common.core.model.IbmPartProductModel;
import com.ibm.commerce.common.core.product.service.IbmProductService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@UnitTest
public class ProductNameResolverTest {

    private static final String PART_PRODUCT_CODE = "partProductCode";
    private static final String FIELD_NAME = "fieldName";
    private static final String LANGUAGE_ISOCODE = "en";

    private static final String PRODUCT_NAME = "productname";
    @InjectMocks
    ProductNameResolver productNameResolver;
    @Mock
    FieldNameProvider fieldNameProvider;
    @Mock
    CommerceCommonI18NService commerceCommonI18NService;
    @Mock
    IbmProductService productService;
    @Mock
    IndexConfig indexConfig;
    @Mock
    IndexedProperty indexedProperty;
    @Mock
    ProductModel productModel;
    @Mock
    Locale currentLocale;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        productNameResolver = new ProductNameResolver(fieldNameProvider, commerceCommonI18NService,
            productService);
    }

    @Test
    public void testGetFieldValuesLocalized() throws FieldValueProviderException {
        when(indexedProperty.isLocalized()).thenReturn(true);
        Collection<LanguageModel> languageModels = new ArrayList<>();
        LanguageModel language = new LanguageModel();
        language.setIsocode(LANGUAGE_ISOCODE);
        languageModels.add(language);
        when(indexConfig.getLanguages()).thenReturn(languageModels);
        List<IbmPartProductModel> partProducts = new ArrayList<>();
        IbmPartProductModel partProductModel = new IbmPartProductModel();
        partProductModel.setCode(PART_PRODUCT_CODE);
        partProducts.add(partProductModel);
        when(productService.getPartProducts(productModel)).thenReturn(partProducts);
        when(commerceCommonI18NService.getLocaleForLanguage(language)).thenReturn(currentLocale);
        when(productModel.getName(currentLocale)).thenReturn(PRODUCT_NAME);
        Collection<String> fieldNames = new ArrayList<>();
        fieldNames.add(FIELD_NAME);
        when(fieldNameProvider.getFieldNames(any(), any())).thenReturn(fieldNames);
        Collection<FieldValue> fieldValues = productNameResolver.getFieldValues(indexConfig,
            indexedProperty, productModel);
        Assert.assertEquals(FIELD_NAME,
            ((FieldValue) ((ArrayList) fieldValues).get(0)).getFieldName());
        Assert.assertEquals(PRODUCT_NAME,
            ((FieldValue) ((ArrayList) fieldValues).get(0)).getValue());
    }

    @Test(expected = FieldValueProviderException.class)
    public void testGetFieldValues() throws FieldValueProviderException {
        productNameResolver.getFieldValues(indexConfig, indexedProperty, productModel);
    }
}