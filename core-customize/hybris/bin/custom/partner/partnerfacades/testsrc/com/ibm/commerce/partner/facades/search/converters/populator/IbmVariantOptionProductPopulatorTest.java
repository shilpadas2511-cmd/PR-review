package com.ibm.commerce.partner.facades.search.converters.populator;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import de.hybris.platform.commercefacades.product.data.VariantOptionData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class IbmVariantOptionProductPopulatorTest {

    public static final String CONFIG_CODE = "config123";
    public static final String TAG_CODE = "SaaS";
    private IbmVariantOptionProductPopulator populator;

    @Before
    public void setUp() {
        populator = new IbmVariantOptionProductPopulator();
    }

    @Test
    public void testPopulate_withConfiguratorCode_shouldSetConfiguratorCode() {

        SearchResultValueData searchResultValueData = new SearchResultValueData();
        Map<String, Object> values = new HashMap<>();
        values.put(PartnercoreConstants.CONFIGURATOR_CODE, CONFIG_CODE);
        values.put(PartnercoreConstants.DEPLOYMENT_TYPE_TAG_CODE, TAG_CODE);
        searchResultValueData.setValues(values);
        VariantOptionData variantOptionData = new VariantOptionData();
        populator.populate(searchResultValueData, variantOptionData);
        assertEquals(CONFIG_CODE, variantOptionData.getConfiguratorCode());
    }


    @Test
    public void testPopulate_withNullValues_shouldNotThrow() {
        SearchResultValueData searchResultValueData = new SearchResultValueData();
        searchResultValueData.setValues(null);
        VariantOptionData variantOptionData = new VariantOptionData();
        populator.populate(searchResultValueData, variantOptionData);
        assertNull(variantOptionData.getConfiguratorCode());
    }

    @Test
    public void testPopulate_withMissingConfiguratorCode_shouldSetNull() {
        SearchResultValueData searchResultValueData = new SearchResultValueData();
        Map<String, Object> values = new HashMap<>();
        values.put("test", "123");
        searchResultValueData.setValues(values);
        VariantOptionData variantOptionData = new VariantOptionData();
        populator.populate(searchResultValueData, variantOptionData);
        assertNull(variantOptionData.getConfiguratorCode());
    }
}
