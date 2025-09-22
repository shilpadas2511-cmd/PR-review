package com.ibm.commerce.partner.core.search.solrfacetsearch.resolvers.impl;

import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.enumeration.EnumerationService;
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

/**
 * indexing Quote status name
 */
public class QuoteStatusNameResolver extends AbstractPropertyFieldValueProvider implements
    FieldValueProvider, Serializable {


    private final FieldNameProvider fieldNameProvider;

    private final CommerceCommonI18NService commerceCommonI18NService;
    private EnumerationService enumerationService;

    public QuoteStatusNameResolver(final FieldNameProvider fieldNameProvider,
        final CommerceCommonI18NService commerceCommonI18NService,
        final EnumerationService enumerationService) {
        this.fieldNameProvider = fieldNameProvider;
        this.commerceCommonI18NService = commerceCommonI18NService;
        this.enumerationService = enumerationService;
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
        final IndexedProperty indexedProperty, final Object model)
        throws FieldValueProviderException {
        final Collection<FieldValue> fieldValues = new ArrayList<>();
        if (model instanceof final IbmPartnerQuoteModel quote) {
            if (!indexedProperty.isLocalized()) {
                throw new FieldValueProviderException("The status name must be localized");
            }

            final Collection<LanguageModel> languages = indexConfig.getLanguages();

            for (final LanguageModel language : languages) {
                fieldValues.addAll(createFieldValue(quote, language, indexedProperty));

            }


        }
        return fieldValues;
    }

    /**
     * @param quote
     * @param language
     * @param indexedProperty
     * @return
     */
    protected List<FieldValue> createFieldValue(final IbmPartnerQuoteModel quote,
        final LanguageModel language, final IndexedProperty indexedProperty) {
        final List<FieldValue> fieldValues = new ArrayList<>();
        QuoteState status = quote.getState();
        final String statusName = getEnumerationValue(status);
        if (statusName != null && !statusName.equalsIgnoreCase("Duplication Draft")
            && !statusName.equalsIgnoreCase("Cancelled")) {
            addFieldValues(fieldValues, indexedProperty, language, statusName);
        }
        return fieldValues;
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
            language != null ? language.getIsocode() : null);
        for (final String fieldName : fieldNames) {
            fieldValues.add(new FieldValue(fieldName, value));
        }
    }

    private String getEnumerationValue(final Object object) {
        return enumerationService.getEnumerationName((HybrisEnumValue) object,
            i18nService.getCurrentLocale());

    }

    protected FieldNameProvider getFieldNameProvider() {
        return fieldNameProvider;
    }

    public CommerceCommonI18NService getCommerceCommonI18NService() {
        return commerceCommonI18NService;
    }

    public void setEnumerationService(final EnumerationService enumerationService) {
        this.enumerationService = enumerationService;
    }
}