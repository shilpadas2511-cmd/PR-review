package com.ibm.commerce.partner.core.search.solrfacetsearch.resolvers.impl;

import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractPropertyFieldValueProvider;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.apache.commons.collections.CollectionUtils;

import com.ibm.commerce.common.core.model.IbmPartProductModel;
import com.ibm.commerce.common.core.product.service.IbmProductService;

/**
 * indexing PID product name and Part Product name associated to PID
 */
public class ProductNameResolver extends AbstractPropertyFieldValueProvider implements
    FieldValueProvider, Serializable {


	private transient FieldNameProvider fieldNameProvider;

	private transient CommerceCommonI18NService commerceCommonI18NService;

	private transient IbmProductService productService;

    public ProductNameResolver(final FieldNameProvider fieldNameProvider,
        final CommerceCommonI18NService commerceCommonI18NService,
        final IbmProductService productService) {
        this.fieldNameProvider = fieldNameProvider;
        this.commerceCommonI18NService = commerceCommonI18NService;
        this.productService = productService;
    }


    /**
     * @param indexConfig
     * @param indexedProperty
     * @param model
     * @return
     * @throws FieldValueProviderException
     */
    @Override
    public Collection<FieldValue> getFieldValues(final IndexConfig indexConfig,
        final IndexedProperty indexedProperty, final Object model) throws FieldValueProviderException {
        final Collection<FieldValue> fieldValues = new ArrayList<>();
        if (model instanceof final ProductModel product) {
            if (!indexedProperty.isLocalized()) {
                throw new FieldValueProviderException("The part name must be localized");
            }

            final Collection<LanguageModel> languages = indexConfig.getLanguages();

            for (final LanguageModel language : languages) {
                fieldValues.addAll(createFieldValue(product, language, indexedProperty));
                final List<IbmPartProductModel> partProducts = getProductService().getPartProducts(
                    product);
                if (CollectionUtils.isNotEmpty(partProducts)) {
                    for (final IbmPartProductModel partProduct : partProducts) {
                        fieldValues.addAll(
                            createFieldValue(partProduct, language, indexedProperty));
                    }
                }

            }


        }
        return fieldValues;
    }

    /**
     * @param product
     * @param language
     * @param indexedProperty
     * @return
     */
    protected List<FieldValue> createFieldValue(final ProductModel product,
        final LanguageModel language, final IndexedProperty indexedProperty) {
        final List<FieldValue> fieldValues = new ArrayList<>();

        //The  current site must be set in the session.
        final Locale currentLocale = this.getCommerceCommonI18NService()
            .getLocaleForLanguage(language);
        final String partName = translateToPart(product.getName(currentLocale));
        if (partName != null) {
            addFieldValues(fieldValues, indexedProperty, language, partName);
        }

        return fieldValues;
    }

    private String translateToPart(final String pProductName) {
        if (pProductName == null) {
            return null;
        }
        //Simple test conversion.
        return pProductName.toLowerCase().replaceAll("\\s", " ");
    }

    /**
     * @param fieldValues
     * @param indexedProperty
     * @param language
     * @param value
     */
    protected void addFieldValues(final List<FieldValue> fieldValues,
        final IndexedProperty indexedProperty, final LanguageModel language, final Object value) {
        final Collection<String> fieldNames = getFieldNameProvider().getFieldNames(indexedProperty,
            language == null ? null : language.getIsocode());
        for (final String fieldName : fieldNames) {
            fieldValues.add(new FieldValue(fieldName, value));
        }
    }

    protected FieldNameProvider getFieldNameProvider() {
        return fieldNameProvider;
    }

    public CommerceCommonI18NService getCommerceCommonI18NService() {
        return commerceCommonI18NService;
    }


    public IbmProductService getProductService() {
        return productService;
    }
}

