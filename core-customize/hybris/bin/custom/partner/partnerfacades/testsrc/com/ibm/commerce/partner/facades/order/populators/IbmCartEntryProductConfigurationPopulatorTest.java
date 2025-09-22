package com.ibm.commerce.partner.facades.order.populators;

import com.ibm.commerce.partner.core.util.model.AbstractOrderEntryModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.ProductConfigurationModelDataTestGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

@UnitTest
public class IbmCartEntryProductConfigurationPopulatorTest {

    @InjectMocks
    private IbmCartEntryProductConfigurationPopulator ibmCartEntryProductConfigurationPopulator;

    private AbstractOrderEntryModel abstractOrderEntryModel;
    private OrderEntryData orderEntryData;
    private static final String CONFIG_ID = "321";
    private static final int ENTRY_NUMBER = 1;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ibmCartEntryProductConfigurationPopulator = new IbmCartEntryProductConfigurationPopulator();
        orderEntryData = new OrderEntryData();
        abstractOrderEntryModel = AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntry(ENTRY_NUMBER);
    }

    @Test
    public void testPopulateWithConfigId() {
        ProductConfigurationModel productConfigurationModel = ProductConfigurationModelDataTestGenerator.createProductConfigurationModel(CONFIG_ID);
        abstractOrderEntryModel.setProductConfiguration(productConfigurationModel);
        ibmCartEntryProductConfigurationPopulator.populate(abstractOrderEntryModel, orderEntryData);
        Assert.assertNotNull(orderEntryData);
        Assert.assertNotNull(orderEntryData.getConfigId());
        Assert.assertEquals(CONFIG_ID, orderEntryData.getConfigId());
    }

    @Test
    public void testPopulateWithOut_ConfigId() {
        ProductConfigurationModel productConfigurationModel = ProductConfigurationModelDataTestGenerator.createProductConfigurationModel(CONFIG_ID);
        ibmCartEntryProductConfigurationPopulator.populate(abstractOrderEntryModel, orderEntryData);
        Assert.assertNull(orderEntryData.getConfigId());
    }
}
