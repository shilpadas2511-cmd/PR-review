package com.ibm.commerce.partner.core.product.dao.impl;

import static org.mockito.ArgumentMatchers.any;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.ibm.commerce.common.core.model.IbmDeploymentTypeModel;


/**
 * Test class for {@link DefaultIbmDeploymentTypeDao}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultIbmDeploymentTypeDaoTest {

    @InjectMocks
    DefaultIbmDeploymentTypeDao defaultIbmDeploymentTypeDao;

    @Mock
    FlexibleSearchService flexibleSearchService;
    @Mock
    SearchResult result;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        defaultIbmDeploymentTypeDao = new DefaultIbmDeploymentTypeDao(flexibleSearchService);
    }

    @Test
    public void testGetDeploymentTypeForFacet() {
        final IbmDeploymentTypeModel ibmDeploymentTypeModel = Mockito.mock(
            IbmDeploymentTypeModel.class);
        Mockito.when(flexibleSearchService.search(any(FlexibleSearchQuery.class)))
            .thenReturn(result);
        Mockito.when(result.getResult()).thenReturn(Arrays.asList(ibmDeploymentTypeModel));
        final IbmDeploymentTypeModel result = defaultIbmDeploymentTypeDao.getDeploymentTypeForFacet(
            "test");
        Assert.assertNotNull(result);
        Assert.assertEquals(ibmDeploymentTypeModel, result);
    }

    @Test
    public void testGetDeploymentTypeForFacetNull() {
        Mockito.when(flexibleSearchService.search(any(FlexibleSearchQuery.class)))
            .thenReturn(result);
        Mockito.when(result.getResult()).thenReturn(null);
        final IbmDeploymentTypeModel result = defaultIbmDeploymentTypeDao.getDeploymentTypeForFacet(
            "test");
        Assert.assertNull(result);
    }

    @Test
    public void testResultNull() {
        Mockito.when(flexibleSearchService.search(any(FlexibleSearchQuery.class))).thenReturn(null);
        final IbmDeploymentTypeModel result = defaultIbmDeploymentTypeDao.getDeploymentTypeForFacet(
            "test");
        Assert.assertNull(result);
    }
}
