package com.ibm.commerce.partner.facades.company.endcustomer.converter.populators;

import com.ibm.commerce.partner.company.endcustomer.data.IbmPartnerDivestitureRetentionData;
import com.ibm.commerce.partner.core.model.IbmPartnerDivestitureRetentionModel;
import com.ibm.commerce.partner.core.util.model.IbmPartnerDivestitureRetentionModelTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import java.util.Date;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

@UnitTest
public class PartnerDivestitureRetentionPopulatorTest {

    private final static String ENTMT_TYPE = "Test Type";
    private final static String ENTMT_DESC = "Test Desc";
    private final static Date RETAINED_ENDDATE = new Date();
    private final static String SAP_DIVSSTTR_CODE = "SAP DIVSSTTR";


    @InjectMocks
    PartnerDivestitureRetentionPopulator divestitureRetentionPopulator;

    IbmPartnerDivestitureRetentionModel source;
    IbmPartnerDivestitureRetentionData target;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        divestitureRetentionPopulator = new PartnerDivestitureRetentionPopulator();
        source = IbmPartnerDivestitureRetentionModelTestDataGenerator.createDivestitureRetentionModel(ENTMT_TYPE, ENTMT_DESC, RETAINED_ENDDATE, SAP_DIVSSTTR_CODE);
        target = new IbmPartnerDivestitureRetentionData();
    }

    @Test
    public void testPopulate() {
        divestitureRetentionPopulator.populate(source, target);
        Assert.assertEquals(ENTMT_TYPE, target.getEntmtType());
        Assert.assertEquals(ENTMT_DESC, target.getEntmtTypeDesc());
        Assert.assertEquals(SAP_DIVSSTTR_CODE, target.getSapDivsttrCode());
    }


    @Test
    public void testPopulateWithRetainedEndDateNull() {
        source = IbmPartnerDivestitureRetentionModelTestDataGenerator.createDivestitureRetentionModel(ENTMT_TYPE, ENTMT_DESC, null, SAP_DIVSSTTR_CODE);
        divestitureRetentionPopulator.populate(source, target);
        Assert.assertEquals(ENTMT_TYPE, target.getEntmtType());
        Assert.assertEquals(ENTMT_DESC, target.getEntmtTypeDesc());
        Assert.assertEquals(SAP_DIVSSTTR_CODE, target.getSapDivsttrCode());
        Assert.assertNull(target.getRetainedEndDate());
    }


}
