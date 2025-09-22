package com.ibm.commerce.partner.facades.company.endcustomer.converter.populators;

import com.ibm.commerce.partner.company.endcustomer.data.IbmPartnerEndCustomerB2BUnitData;
import com.ibm.commerce.partner.core.model.IbmPartnerEndCustomerB2BUnitModel;
import com.ibm.commerce.partner.core.util.model.IbmPartnerEndCustomerB2BUnitModelTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import javolution.io.Struct.Bool;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

@UnitTest
public class EndCustomerGoeB2BUnitPopulatorTest {
    private static final String ID = "test@test.com";
    @InjectMocks
    EndCustomerGoeB2BUnitPopulator endCustomerGoeB2BUnitPopulator;

    IbmPartnerEndCustomerB2BUnitModel source;
    IbmPartnerEndCustomerB2BUnitData target;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        endCustomerGoeB2BUnitPopulator = new EndCustomerGoeB2BUnitPopulator();
        source = IbmPartnerEndCustomerB2BUnitModelTestDataGenerator.createModelTestData(ID);
        target = new IbmPartnerEndCustomerB2BUnitData();
    }

    @Test
    public void testPopulate() {
        source.setGoe(Boolean.TRUE);
        endCustomerGoeB2BUnitPopulator.populate(source, target);
        Assert.assertTrue(target.isGoe());
    }
}
