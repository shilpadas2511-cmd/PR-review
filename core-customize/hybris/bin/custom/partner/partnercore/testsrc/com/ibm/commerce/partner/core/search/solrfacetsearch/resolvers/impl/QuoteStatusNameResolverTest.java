package com.ibm.commerce.partner.core.search.solrfacetsearch.resolvers.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * test class is index Quote status name
 */

@UnitTest
public class QuoteStatusNameResolverTest {

    @InjectMocks
    QuoteStatusNameResolver quoteStatusNameResolver;
    @Mock
    FieldNameProvider fieldNameProvider;
    @Mock
    CommerceCommonI18NService commerceCommonI18NService;
    @Mock
    EnumerationService enumerationService;
    @Mock
    IndexConfig indexConfig;
    @Mock
    IndexedProperty indexedProperty;
    @Mock
    IbmPartnerQuoteModel quoteModel;
    @Mock
    LanguageModel languageModel;
    @Mock
    private I18NService i18nService;

    @Before
    public void setup()  throws Exception {
        MockitoAnnotations.openMocks(this);
        quoteStatusNameResolver = new QuoteStatusNameResolver(fieldNameProvider,
            commerceCommonI18NService,
            enumerationService);
        quoteStatusNameResolver.getCommerceCommonI18NService();
        Field i18nField = quoteStatusNameResolver.getClass().getSuperclass()
            .getDeclaredField("i18nService");
        i18nField.setAccessible(true);
        i18nField.set(quoteStatusNameResolver, i18nService);
        quoteStatusNameResolver.setEnumerationService(enumerationService);
        ReflectionTestUtils.setField(quoteStatusNameResolver, "i18nService", i18nService);

    }

    @Test
    public void testGetFieldValues_ShouldReturnFieldValues_whenValidStatus() throws FieldValueProviderException{
        when(indexedProperty.isLocalized()).thenReturn(true);
        when(indexConfig.getLanguages()).thenReturn(Collections.singletonList(languageModel));
        when(i18nService.getCurrentLocale()).thenReturn(Locale.ENGLISH);

        when(commerceCommonI18NService.getCurrentLocale()).thenReturn(Locale.ENGLISH);
        when(quoteModel.getState()).thenReturn(QuoteState.BUYER_APPROVED);
        when(enumerationService.getEnumerationName(any(), any())).thenReturn("BuyerApproved");
        when(fieldNameProvider.getFieldNames(eq(indexedProperty), any())).thenReturn(Collections.singletonList("statusField"));

        Collection<FieldValue> result = quoteStatusNameResolver.getFieldValues(indexConfig, indexedProperty, quoteModel);

        assertNotNull(result);
        assertEquals(1, result.size());
        FieldValue fieldValue = result.iterator().next();
        assertEquals("statusField", fieldValue.getFieldName());
        assertEquals("BuyerApproved", fieldValue.getValue());
    }

    @Test
    public void testGetFieldValues_throwException_whenNotLocalized() throws FieldValueProviderException{
        when(indexedProperty.isLocalized()).thenReturn(false);
        assertThrows(FieldValueProviderException.class, () -> quoteStatusNameResolver.getFieldValues(indexConfig, indexedProperty, quoteModel));
    }

}