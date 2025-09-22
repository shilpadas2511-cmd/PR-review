package com.ibm.commerce.partner.facades.company.endcustomer.converter.populators;

import com.ibm.commerce.partner.company.endcustomer.data.IbmPartnerDivestitureRetentionData;
import com.ibm.commerce.partner.company.endcustomer.data.IbmPartnerEndCustomerB2BUnitData;
import com.ibm.commerce.partner.core.model.IbmPartnerDivestitureRetentionModel;
import com.ibm.commerce.partner.core.model.IbmPartnerEndCustomerB2BUnitModel;
import com.ibm.commerce.partner.core.util.model.IbmPartnerDivestitureRetentionModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.IbmPartnerEndCustomerB2BUnitModelTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@UnitTest
public class EndCustomerDivestitureRetentionPopulatorTest {
    private static final String ID = "test@test.com";
    private final static String ENTMT_TYPE = "Test Type";
    private final static String ENTMT_DESC = "Test Desc";
    private final static Date RETAINED_ENDDATE = new Date();
    private final static String SAP_DIVSSTTR_CODE = "Test divsstr";

    @InjectMocks
    EndCustomerDivestitureRetentionPopulator endCustomerDivestitureRetentionPopulator;

    @Mock
    Converter<IbmPartnerDivestitureRetentionModel, IbmPartnerDivestitureRetentionData> divestitureRetentionDataConverter;

    IbmPartnerEndCustomerB2BUnitData target;
    IbmPartnerEndCustomerB2BUnitModel source;

    Collection<IbmPartnerDivestitureRetentionModel> ibmPartnerDivestitureRetentionModels;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        endCustomerDivestitureRetentionPopulator = new EndCustomerDivestitureRetentionPopulator(divestitureRetentionDataConverter);
        target = new IbmPartnerEndCustomerB2BUnitData();
        source = IbmPartnerEndCustomerB2BUnitModelTestDataGenerator.createModelTestData(ID);
        ibmPartnerDivestitureRetentionModels = new ArrayList<>();
        IbmPartnerDivestitureRetentionModel retentionModel = IbmPartnerDivestitureRetentionModelTestDataGenerator.createDivestitureRetentionModel(ENTMT_TYPE, ENTMT_DESC, RETAINED_ENDDATE, SAP_DIVSSTTR_CODE);
        ibmPartnerDivestitureRetentionModels.add(retentionModel);
        source.setDivestitureRetentions(ibmPartnerDivestitureRetentionModels);
    }

    @Test
    public void testPopulate() {
        endCustomerDivestitureRetentionPopulator.populate(source, target);
        Assert.assertNotNull(target.getDivestitureRetentions());
    }

    @Test
    public void testPopulateWhenDivestitureRetentionNull() {
        source.setDivestitureRetentions(null);
        endCustomerDivestitureRetentionPopulator.populate(source, target);
        Assert.assertNull(target.getDivestitureRetentions());
    }

}
