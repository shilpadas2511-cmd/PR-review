package com.ibm.commerce.partner.facades.company.endcustomer.converter.populators;

import com.ibm.commerce.partner.company.endcustomer.data.IbmPartnerEndCustomerB2BUnitData;
import com.ibm.commerce.partner.core.model.IbmPartnerEndCustomerB2BUnitModel;
import com.ibm.commerce.partner.core.util.model.IbmPartnerEndCustomerB2BUnitModelTestDataGenerator;
import com.ibm.commerce.partner.facades.util.IbmPartnerEndCustomerB2BUnitTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.lang.NonNull;

@UnitTest
public class EndCustomerGoeB2BUnitReversePopulatorTest {

    @InjectMocks
    EndCustomerGoeB2BUnitReversePopulator goeB2BUnitReversePopulator;

    IbmPartnerEndCustomerB2BUnitData source;
    IbmPartnerEndCustomerB2BUnitModel target;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        goeB2BUnitReversePopulator = new EndCustomerGoeB2BUnitReversePopulator();
        target = new IbmPartnerEndCustomerB2BUnitModel();
        source = IbmPartnerEndCustomerB2BUnitTestDataGenerator.createIbmPartnerEndCustomerB2BUnitData(true, null);
    }

    @Test
    public void testPopulate() {
        goeB2BUnitReversePopulator.populate(source, target);
        Assert.assertTrue(target.getGoe());
    }
}
