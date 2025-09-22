package com.ibm.commerce.partner.facades.enums.converter.populator;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.enumeration.EnumerationService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.ibm.commerce.partner.enums.data.DisplayTypeData;
import com.ibm.commerce.partner.facades.util.DisplayTypeTestDataGenerator;


/**
 * Test class for {@link EnumerationDisplayTypeDataPopulator}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class EnumerationDisplayTypeDataPopulatorTest
{
	private static final String AWAITING_APPROVAL = "AWAITING_APPROVAL";

	@InjectMocks
	EnumerationDisplayTypeDataPopulator enumerationDisplayTypeDataPopulator;
	@Mock
	EnumerationService enumerationService;
	@Mock
	CommerceCommonI18NService commonI18NService;

	HybrisEnumValue source;
	DisplayTypeData target;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		enumerationDisplayTypeDataPopulator = new EnumerationDisplayTypeDataPopulator(enumerationService, commonI18NService);
		source = mock(HybrisEnumValue.class);
		target = DisplayTypeTestDataGenerator.createDisplayTypeData(null, null);
		given(source.getCode()).willReturn(AWAITING_APPROVAL);
	}

	@Test
	public void testPopulate()
	{
		enumerationDisplayTypeDataPopulator.populate(source, target);
		Assert.assertEquals(AWAITING_APPROVAL, target.getCode());
	}
}
