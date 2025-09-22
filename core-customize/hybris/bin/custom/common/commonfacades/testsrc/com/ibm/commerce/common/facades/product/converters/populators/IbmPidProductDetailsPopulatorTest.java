package com.ibm.commerce.common.facades.product.converters.populators;

import com.ibm.commerce.common.core.model.IbmVariantProductModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

@UnitTest
public class IbmPidProductDetailsPopulatorTest {

    private static final String PART_NUMBER = "0000123";
    private static final String CONFIG_CODE = "IBM_Aspera_on_Cloud_cpq";

    @InjectMocks
    IbmPidProductDetailsPopulator ibmPidProductDetailsPopulator;

    IbmVariantProductModel source;
    ProductData target;

    /***
     * Setup method for IbmPidProductDetailsPopulator
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        source = new IbmVariantProductModel();
        target = new ProductData();
    }

    /**
     * test method for populating ProductData
     */
    @Test
    public void testPopulate() {
        source.setPartNumber(PART_NUMBER);
        source.setConfiguratorCode(CONFIG_CODE);
        ibmPidProductDetailsPopulator.populate(source, target);
        Assert.assertEquals(PART_NUMBER, target.getPartNumber());
        Assert.assertEquals(CONFIG_CODE, target.getConfiguratorCode());
    }

    /**
     * test method of populate method of IbmPidProductDetailsPopulator class when type is null.
     */
    @Test
    public void testPopulateWhenTypeIsNull() {
        source.setConfiguratorCode(null);
        ibmPidProductDetailsPopulator.populate(source, target);
        Assert.assertNull(target.getConfiguratorCode());
    }

    @Test
    public void testPopulateMismatch() {
        ProductModel misMatchSource = new ProductModel();
        ibmPidProductDetailsPopulator.populate(misMatchSource, target);
        Assert.assertNull(target.getConfiguratorCode());
    }
}
