package com.ibm.commerce.partner.facades.company.endcustomer.converter.populators;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import com.ibm.commerce.partner.company.endcustomer.data.IbmPartnerDivestitureRetentionData;
import com.ibm.commerce.partner.core.model.IbmPartnerDivestitureRetentionModel;
import com.ibm.commerce.partner.facades.util.IbmPartnerDivestitureRetentionTestDataGenerator;

@UnitTest
public class PartnerDivestitureRetentionReversePopulatorTest {
    private final static String ENTMT_TYPE = "Test Type";
    private final static String ENTMT_DESC = "Test Desc";
	 private final static String RETAINED_ENDDATE = "2024-04-12";
	 private final static String RETAINED_ENDDATE_INVALID_PATTERN = "15/04/2024";
    private final static String SAP_DIVSSTTR_CODE = "SAP DIVSSTTR";

    @InjectMocks
    PartnerDivestitureRetentionReversePopulator retentionReversePopulator;

    IbmPartnerDivestitureRetentionData divestitureRetentionData;
    IbmPartnerDivestitureRetentionModel divestitureRetentionModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        retentionReversePopulator = new PartnerDivestitureRetentionReversePopulator();
        divestitureRetentionModel = new IbmPartnerDivestitureRetentionModel();
        divestitureRetentionData = IbmPartnerDivestitureRetentionTestDataGenerator.createRetentionData(ENTMT_TYPE, ENTMT_DESC, RETAINED_ENDDATE, SAP_DIVSSTTR_CODE);
    }

    @Test
    public void testPopulate() {
        retentionReversePopulator.populate(divestitureRetentionData, divestitureRetentionModel);
        Assert.assertEquals(ENTMT_TYPE, divestitureRetentionModel.getEntmtType());
        Assert.assertEquals(ENTMT_DESC, divestitureRetentionModel.getEntmtTypeDesc());
		  Assert.assertNotNull(divestitureRetentionModel.getRetainedEndDate());
    }

	 @Test
	 public void testPopulateRetainedEndDateNull()
	 {
		 divestitureRetentionData = IbmPartnerDivestitureRetentionTestDataGenerator.createRetentionData(ENTMT_TYPE, ENTMT_DESC,
				 null, SAP_DIVSSTTR_CODE);
		 retentionReversePopulator.populate(divestitureRetentionData, divestitureRetentionModel);
		 Assert.assertEquals(ENTMT_TYPE, divestitureRetentionModel.getEntmtType());
		 Assert.assertEquals(ENTMT_DESC, divestitureRetentionModel.getEntmtTypeDesc());
		 Assert.assertNull(divestitureRetentionModel.getRetainedEndDate());
	 }

	 @Test
	 public void testPopulateInvalidRetainedEndDatePattern()
	 {
		 divestitureRetentionData = IbmPartnerDivestitureRetentionTestDataGenerator.createRetentionData(ENTMT_TYPE, ENTMT_DESC,
				 RETAINED_ENDDATE_INVALID_PATTERN, SAP_DIVSSTTR_CODE);
		 retentionReversePopulator.populate(divestitureRetentionData, divestitureRetentionModel);
		 Assert.assertEquals(ENTMT_TYPE, divestitureRetentionModel.getEntmtType());
		 Assert.assertEquals(ENTMT_DESC, divestitureRetentionModel.getEntmtTypeDesc());
		 Assert.assertNull(divestitureRetentionModel.getRetainedEndDate());
	 }
}
