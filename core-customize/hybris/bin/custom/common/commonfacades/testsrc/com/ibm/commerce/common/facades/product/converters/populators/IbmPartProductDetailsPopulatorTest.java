package com.ibm.commerce.common.facades.product.converters.populators;

import com.ibm.commerce.common.core.model.IbmPartProductModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

@UnitTest
public class IbmPartProductDetailsPopulatorTest {

    private static final String PART_NUMBER = "0000123";

    @InjectMocks
    IbmPartProductDetailsPopulator ibmPartProductDetailsPopulator;

    IbmPartProductModel source;
    ProductData target;

    /***
     * Setup method for IbmPartProductDetailsPopulator
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        source = new IbmPartProductModel();
        target = new ProductData();
    }

    /**
     * test method for populating ProductData
     */
    @Test
    public void testPopulate() {
        source.setPartNumber(PART_NUMBER);
        ibmPartProductDetailsPopulator.populate(source, target);
        Assert.assertEquals(PART_NUMBER, target.getPartNumber());
    }

    /**
     * test method of populate method of IbmPartProductDetailsPopulator class when type is null.
     */
    @Test
    public void testPopulateWhenTypeIsNull() {
        source.setPartNumber(null);
        ibmPartProductDetailsPopulator.populate(source, target);
        Assert.assertNull(target.getPartNumber());
    }

    /**
     * test method of populate method of IbmPartProductDetailsPopulator class when type is ProductModel.
     */
    @Test
    public void testPopulateWhenTypeMismatch() {
        ProductModel mismatchSource = new ProductModel();
        ibmPartProductDetailsPopulator.populate(mismatchSource, target);
        Assert.assertNull(target.getPartNumber());
    }
}
