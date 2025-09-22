package com.ibm.commerce.partner.facades.search.converters.populator;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;


/**
 * Unit test class for IbmSearchResultProductPopulator
 */

@UnitTest
public class IbmSearchResultProductPopulatorTest {

    private static final String CONFIGURATOR_CODE = "12345";
    @InjectMocks
    IbmSearchResultProductPopulator ibmSearchResultProductPopulator;
    private ProductData target;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        target = new ProductData();
    }

    @Test
    public void testPopulate() throws ConversionException {
        SearchResultValueData source = new SearchResultValueData();
        source.setValues(createSampleValues());
        ibmSearchResultProductPopulator.populate(source, target);
        assertEquals(CONFIGURATOR_CODE, target.getConfiguratorCode());
    }

    private Map<String, Object> createSampleValues() {
        Map<String, Object> values = new HashMap<>();
        values.put(PartnercoreConstants.CONFIGURATOR_CODE, "12345");
        return values;
    }

    @Test(expected = ConversionException.class)
    public void testPopulateConversionException() throws ConversionException {
        SearchResultValueData source = new SearchResultValueData() {
            @Override
            public Map<String, Object> getValues() {
                throw new ConversionException("Test Conversion Exception");
            }
        };
        ibmSearchResultProductPopulator.populate(source, target);
        Assert.assertNull(target.getConfiguratorCode());
    }
}

