package com.ibm.commerce.partner.facades.company.endcustomer.converter.populators;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ibm.commerce.partner.company.endcustomer.data.IbmPartnerDivestitureRetentionData;
import com.ibm.commerce.partner.company.endcustomer.data.IbmPartnerEndCustomerB2BUnitData;
import com.ibm.commerce.partner.core.model.IbmPartnerDivestitureRetentionModel;
import com.ibm.commerce.partner.core.model.IbmPartnerEndCustomerB2BUnitModel;
import com.ibm.commerce.partner.core.util.model.IbmPartnerDivestitureRetentionModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.IbmPartnerEndCustomerB2BUnitModelTestDataGenerator;
import com.ibm.commerce.partner.facades.comparators.IbmPartnerDivestitureRetentionModelComparator;
import com.ibm.commerce.partner.facades.util.IbmPartnerDivestitureRetentionTestDataGenerator;

@UnitTest
public class EndCustomerDivestitureRetentionReversePopulatorTest {

    private static final String ID = "test@test.com";

    private final static String ENTMT_TYPE = "Test Type";
    private final static String ENTMT_DESC = "Test Desc";
    private final static Date RETAINED_ENDDATE = new Date();
	 private final static String RETAINED_ENDDATE_DATA = "2024-04-12";
    private final static String SAP_DIVSSTTR_CODE = "Test divsstr";

	 private final static String ENTMT_TYPE1 = "Test1 Type";
	 private final static String ENTMT_DESC1 = "Test1 Desc";

    @InjectMocks
    EndCustomerDivestitureRetentionReversePopulator retentionReversePopulator;

    @Mock
    Converter<IbmPartnerDivestitureRetentionData, IbmPartnerDivestitureRetentionModel> divestitureRetentionDataReverseConverter;

    @Mock
    IbmPartnerDivestitureRetentionModelComparator partnerDivestitureRetentionModelComparator;

	 IbmPartnerEndCustomerB2BUnitData source;
	 IbmPartnerEndCustomerB2BUnitModel target;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        retentionReversePopulator = new EndCustomerDivestitureRetentionReversePopulator(divestitureRetentionDataReverseConverter, partnerDivestitureRetentionModelComparator);
		  source = new IbmPartnerEndCustomerB2BUnitData();
		  target = IbmPartnerEndCustomerB2BUnitModelTestDataGenerator.createModelTestData(ID);
    }

	 @Test
	 public void testPopulate()
	 {
		 final List<IbmPartnerDivestitureRetentionData> divestitureRetentions = sourceDataSetUp();
		 final List<IbmPartnerDivestitureRetentionModel> targetRetentions = targetDataSetUp();

		 when(divestitureRetentionDataReverseConverter.convertAll(divestitureRetentions)).thenReturn(targetRetentions);
		 when(partnerDivestitureRetentionModelComparator.compare(any(IbmPartnerDivestitureRetentionModel.class),
				 any(IbmPartnerDivestitureRetentionModel.class)))
				 .thenReturn(0);
		 retentionReversePopulator.populate(source, target);
		 Assert.assertEquals(1, target.getDivestitureRetentions().size());
	 }

	 @Test
	 public void testPopulateNewRetentions() throws ParseException
	 {
		 final List<IbmPartnerDivestitureRetentionData> divestitureRetentions = sourceDataSetUp();
		 final List<IbmPartnerDivestitureRetentionModel> targetRetentions = targetDataSetUp();

		 final List<IbmPartnerDivestitureRetentionModel> ibmPartnerDivestitureRetentionModels = new ArrayList<>();
		 final IbmPartnerDivestitureRetentionModel divestitureRetentionModel = IbmPartnerDivestitureRetentionModelTestDataGenerator
				 .createDivestitureRetentionModel(ENTMT_TYPE1, ENTMT_DESC1, RETAINED_ENDDATE, SAP_DIVSSTTR_CODE);
		 ibmPartnerDivestitureRetentionModels.add(divestitureRetentionModel);

		 when(divestitureRetentionDataReverseConverter
				 .convertAll(divestitureRetentions)).thenReturn(ibmPartnerDivestitureRetentionModels);
		 when(partnerDivestitureRetentionModelComparator.compare(any(IbmPartnerDivestitureRetentionModel.class),
				 any(IbmPartnerDivestitureRetentionModel.class)))
				 .thenReturn(1);
		 retentionReversePopulator.populate(source, target);
		 Assert.assertEquals(2, target.getDivestitureRetentions().size());
	 }

	 @Test
	 public void testPopulateSourceDivestitureRetentionsEmpty()
	 {
		 retentionReversePopulator.populate(source, target);
		 Assert.assertNull(target.getDivestitureRetentions());
	 }

	 @Test
	 public void testPopulateTargetDivestitureRetentionsEmpty()
	 {
		 final List<IbmPartnerDivestitureRetentionData> divestitureRetentions = sourceDataSetUp();
		 retentionReversePopulator.populate(source, target);
		 Assert.assertEquals(0, target.getDivestitureRetentions().size());
	 }

	 private List<IbmPartnerDivestitureRetentionData> sourceDataSetUp()
	 {
		 final List<IbmPartnerDivestitureRetentionData> divestitureRetentions = new ArrayList<>();
		 final IbmPartnerDivestitureRetentionData divestitureRetentionData = IbmPartnerDivestitureRetentionTestDataGenerator
				 .createRetentionData(ENTMT_TYPE, ENTMT_DESC, RETAINED_ENDDATE_DATA, SAP_DIVSSTTR_CODE);
		 divestitureRetentions.add(divestitureRetentionData);
		 source.setDivestitureRetentions(divestitureRetentions);
		 return divestitureRetentions;
	 }

	 private List<IbmPartnerDivestitureRetentionModel> targetDataSetUp()
	 {
		 final List<IbmPartnerDivestitureRetentionModel> targetRetentions = new ArrayList<>();
		 final IbmPartnerDivestitureRetentionModel targetRetentionModel = IbmPartnerDivestitureRetentionModelTestDataGenerator
				 .createDivestitureRetentionModel(ENTMT_TYPE, ENTMT_DESC, RETAINED_ENDDATE, SAP_DIVSSTTR_CODE);
		 targetRetentions.add(targetRetentionModel);
		 target.setDivestitureRetentions(targetRetentions);
		 return targetRetentions;
	 }
}