package com.ibm.commerce.partner.core.sapmodel.services.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import de.hybris.platform.store.BaseStoreModel;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test class for {@link PartnerSapBeanConfigurationHook}
 */
public class PartnerSapBeanConfigurationHookTest
{
	private static final String PARTNER = "partner";

	@InjectMocks
	PartnerSapBeanConfigurationHook partnerSapBeanConfigurationHook;

	@Mock
	Map<String, String> baseStoreBeanMap;
	@Mock
	BaseStoreModel baseStoreModel;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		partnerSapBeanConfigurationHook = new PartnerSapBeanConfigurationHook(baseStoreBeanMap);
	}

	@Test
	public void testGetBean()
	{
		given(baseStoreBeanMap.get(any(String.class))).willReturn(PARTNER);
		given(baseStoreModel.getUid()).willReturn(PARTNER);
		final String bean = partnerSapBeanConfigurationHook.getBean(baseStoreModel);
		Assert.assertEquals(PARTNER, bean);
	}

	@Test
	public void testGetPriority()
	{
		Assert.assertEquals(0, partnerSapBeanConfigurationHook.getPriority());
	}
}
