package com.ibm.commerce.partner.core.search.solrfacetsearch.populators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.IndexedPropertyValueData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchRequest;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchResponse;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PartnerSearchResponseRemoveFiltersPopulatorTest {
    private PartnerSearchResponseRemoveFiltersPopulator populator;

    @Mock
    private SolrSearchResponse solrSearchResponse;

    @Mock
    private SolrSearchRequest solrSearchRequest;

    @Mock
    private IndexedPropertyValueData<IndexedProperty> indexedPropertyValueData;

    @Mock
    private IndexedProperty indexedProperty;

    private List<String> internalFilterCodes;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        internalFilterCodes = new ArrayList<>();
        internalFilterCodes.add("internal_filter_1");
        internalFilterCodes.add("internal_filter_2");

        populator = new PartnerSearchResponseRemoveFiltersPopulator<>(internalFilterCodes);
    }

    @Test
    public void testPopulate_withFiltersToRemove() throws ConversionException {
        IndexedPropertyValueData<IndexedProperty> propertyToRemove = new IndexedPropertyValueData<>();
        IndexedPropertyValueData<IndexedProperty> propertyToKeep = new IndexedPropertyValueData<>();
        propertyToRemove.setIndexedProperty(indexedProperty);
        when(indexedProperty.getName()).thenReturn("internal_filter_1");

        propertyToKeep.setIndexedProperty(indexedProperty);
        when(indexedProperty.getName()).thenReturn("valid_filter");

        List<IndexedPropertyValueData<IndexedProperty>> indexedPropertyValues = new ArrayList<>();
        indexedPropertyValues.add(propertyToRemove);
        indexedPropertyValues.add(propertyToKeep);

        when(solrSearchResponse.getRequest()).thenReturn(solrSearchRequest);
        when(solrSearchRequest.getIndexedPropertyValues()).thenReturn(indexedPropertyValues);
        populator.populate(solrSearchResponse, null);
        Assert.assertEquals(solrSearchResponse.getRequest(), solrSearchRequest);


    }

    @Test
    public void testPopulate_withNoFiltersToRemove() throws ConversionException {
        internalFilterCodes.clear();
        populator = new PartnerSearchResponseRemoveFiltersPopulator<>(internalFilterCodes);

        IndexedPropertyValueData<IndexedProperty> propertyToKeep = new IndexedPropertyValueData<>();
        IndexedProperty indexedProperty = mock(IndexedProperty.class);
        propertyToKeep.setIndexedProperty(indexedProperty);
        List<IndexedPropertyValueData<IndexedProperty>> indexedPropertyValues = new ArrayList<>();
        indexedPropertyValues.add(propertyToKeep);
        when(solrSearchResponse.getRequest()).thenReturn(solrSearchRequest);
        when(solrSearchRequest.getIndexedPropertyValues()).thenReturn(indexedPropertyValues);
        populator.populate(solrSearchResponse, null);
        Assert.assertEquals(solrSearchResponse.getRequest(), solrSearchRequest);
    }

    @Test
    public void testPopulate_withNoMatchingFilters() throws ConversionException {
        List<String> internalFilterCodes = new ArrayList<>();
        internalFilterCodes.add("non_existent_filter");
        populator = new PartnerSearchResponseRemoveFiltersPopulator<>(internalFilterCodes);

        IndexedPropertyValueData<IndexedProperty> propertyToKeep = new IndexedPropertyValueData<>();
        IndexedProperty indexedProperty = mock(IndexedProperty.class);
        propertyToKeep.setIndexedProperty(indexedProperty);
        when(indexedProperty.getName()).thenReturn("valid_filter");
        List<IndexedPropertyValueData<IndexedProperty>> indexedPropertyValues = new ArrayList<>();
        indexedPropertyValues.add(propertyToKeep);
        when(solrSearchResponse.getRequest()).thenReturn(solrSearchRequest);
        when(solrSearchRequest.getIndexedPropertyValues()).thenReturn(indexedPropertyValues);
        populator.populate(solrSearchResponse, null);
        Assert.assertEquals(solrSearchResponse.getRequest(), solrSearchRequest);
    }

    @Test
    public void testPopulate_withNullSource() throws ConversionException {
        populator.populate(null, null);
    }


}
