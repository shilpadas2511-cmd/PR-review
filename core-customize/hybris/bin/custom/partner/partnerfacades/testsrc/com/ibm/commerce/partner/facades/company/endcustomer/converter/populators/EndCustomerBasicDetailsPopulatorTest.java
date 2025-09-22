package com.ibm.commerce.partner.facades.company.endcustomer.converter.populators;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.ibm.commerce.partner.company.endcustomer.data.IbmPartnerEndCustomerB2BUnitData;
import com.ibm.commerce.partner.core.model.IbmPartnerEndCustomerB2BUnitModel;
import com.ibm.commerce.partner.core.util.model.IbmPartnerEndCustomerB2BUnitModelTestDataGenerator;




/**
 * Test class for {@link EndCustomerBasicDetailsPopulator}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class EndCustomerBasicDetailsPopulatorTest
{
	private final static String DCID = "Test Dcid";

	@InjectMocks
	EndCustomerBasicDetailsPopulator endCustomerBasicDetailsPopulator;

	IbmPartnerEndCustomerB2BUnitModel source;
	IbmPartnerEndCustomerB2BUnitData target;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		endCustomerBasicDetailsPopulator = new EndCustomerBasicDetailsPopulator();
		target = new IbmPartnerEndCustomerB2BUnitData();
		source = IbmPartnerEndCustomerB2BUnitModelTestDataGenerator.createModelTestData(null, DCID);
	}

		@Test
		public void testPopulate()
		{
			endCustomerBasicDetailsPopulator.populate(source, target);
			Assert.assertEquals(DCID, target.getIbmCustomerDCID());
		}
}
