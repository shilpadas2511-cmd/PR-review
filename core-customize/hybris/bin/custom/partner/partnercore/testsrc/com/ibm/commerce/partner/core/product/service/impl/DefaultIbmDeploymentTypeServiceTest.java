package com.ibm.commerce.partner.core.product.service.impl;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.ibm.commerce.common.core.model.IbmDeploymentTypeModel;
import com.ibm.commerce.partner.core.product.dao.IbmDeploymentTypeDao;


/**
 * Test class for {@link DefaultIbmDeploymentTypeService}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultIbmDeploymentTypeServiceTest
{
	private static final String TEST = "test";

	@InjectMocks
	DefaultIbmDeploymentTypeService defaultIbmDeploymentTypeService;

	@Mock
	IbmDeploymentTypeDao deploymentTypeDao;
	@Mock
	IbmDeploymentTypeModel ibmDeploymentTypeModel;



	@Before
	public void setUp()
	{
		 String defaultDeploymentType ="Perpetual";
		MockitoAnnotations.initMocks(this);
		defaultIbmDeploymentTypeService = new DefaultIbmDeploymentTypeService(deploymentTypeDao,defaultDeploymentType);
	}

	@Test
	public void testGetDeploymentTypeForFacet()
	{
		when(deploymentTypeDao.getDeploymentTypeForFacet(TEST)).thenReturn(ibmDeploymentTypeModel);
		final IbmDeploymentTypeModel resultIbmDeploymentTypeModel = defaultIbmDeploymentTypeService.getDeploymentTypeForFacet(TEST);
		Assert.assertEquals(ibmDeploymentTypeModel, resultIbmDeploymentTypeModel);
	}
}
