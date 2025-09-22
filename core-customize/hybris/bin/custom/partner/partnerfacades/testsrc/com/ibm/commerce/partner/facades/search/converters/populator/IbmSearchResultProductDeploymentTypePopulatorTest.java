package com.ibm.commerce.partner.facades.search.converters.populator;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.facades.product.data.IbmDeploymentTypeData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class IbmSearchResultProductDeploymentTypePopulatorTest {

    private static final String DEPLOYMENT_TYPE_CODE = "12345";
    private static final String DEPLOYMENT_TYPE_NAME = "name";
    @InjectMocks
    private IbmSearchResultProductDeploymentTypePopulator populator;
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
        populator.populate(source, target);
        IbmDeploymentTypeData deploymentTypeData = target.getDeploymentType();
        assertEquals(DEPLOYMENT_TYPE_CODE, deploymentTypeData.getCode());
        assertEquals(DEPLOYMENT_TYPE_NAME, deploymentTypeData.getName());
    }

    private Map<String, Object> createSampleValues() {
        Map<String, Object> values = new HashMap<>();
        values.put(PartnercoreConstants.DEPLOYMENT_TYPE_CODE, DEPLOYMENT_TYPE_CODE);
        values.put(PartnercoreConstants.DEPLOYMENT_TYPE_NAME, DEPLOYMENT_TYPE_NAME);
        return values;
    }
}
