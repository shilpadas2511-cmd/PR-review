package com.ibm.commerce.partner.core.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PartnerUtilsTest
{
	@InjectMocks
	PartnerUtils partnerUtils;

	public static final String DATE_PATTERN = "yyyy-MM-dd";
	public static final String ORIGINAL_DATE_PATTERN = "MM/dd/yy hh:mm:ss";
	private static final String FORMATTED_VALUE = "2024-04-20";


	@Test
	public void validateGetAnniversaryMonth()
	{
		assertEquals("APRIL", PartnerUtils.getAnniversaryMonth("4"));
		assertEquals(StringUtils.EMPTY, PartnerUtils.getAnniversaryMonth("14"));
		assertEquals(StringUtils.EMPTY, PartnerUtils.getAnniversaryMonth("FOUR"));
	}

	@Test
	public void validateConvertStringToDate()
	{
		assertNull(PartnerUtils.convertStringToDate("4", DATE_PATTERN));
		assertNull(PartnerUtils.convertStringToDate("FOUR", DATE_PATTERN));
		assertNull(PartnerUtils.convertStringToDate("1984/07/10", DATE_PATTERN));
		final Date dateValue = PartnerUtils.convertStringToDate("1984-07-10", DATE_PATTERN);
		assertEquals(458245800000L, dateValue.getTime());
	}

	@Test
	public void validateConvertDateStringPattern()
	{
		assertNull(PartnerUtils.convertDateStringPattern("4", ORIGINAL_DATE_PATTERN, DATE_PATTERN));
		assertNull(PartnerUtils.convertDateStringPattern("FOUR",ORIGINAL_DATE_PATTERN, DATE_PATTERN));
		final String dateValue = PartnerUtils.convertDateStringPattern("4/20/24 23:00:00",ORIGINAL_DATE_PATTERN, DATE_PATTERN);
		assertEquals(FORMATTED_VALUE, dateValue);
	}
}
