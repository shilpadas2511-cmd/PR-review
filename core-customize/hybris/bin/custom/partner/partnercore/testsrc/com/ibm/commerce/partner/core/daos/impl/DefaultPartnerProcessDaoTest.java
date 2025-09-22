package com.ibm.commerce.partner.core.daos.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPartnerProcessDaoTest {

    @Mock
    private FlexibleSearchService flexibleSearchService;

    @Mock
    private SearchResult<BusinessProcessModel> searchResult;

    @InjectMocks
    private DefaultPartnerProcessDao partnerProcessDao;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        partnerProcessDao = new DefaultPartnerProcessDao(flexibleSearchService);
    }

    @Test
    public void testGetProcessListByCode_ReturnsProcessList() {
        String orderCode = "ORDER123";
        List<BusinessProcessModel> mockResultList =
            java.util.List.of(Mockito.mock(BusinessProcessModel.class));

        Mockito.when(searchResult.getResult()).thenReturn(mockResultList);
        Mockito.when(flexibleSearchService.search(Mockito.any(FlexibleSearchQuery.class)))
            .thenReturn((SearchResult) searchResult); // cast to raw type

        List<BusinessProcessModel> result = partnerProcessDao.getProcessListByCode(orderCode);

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
    }

    @Test
    public void testGetProcessListByCode_ReturnsNullWhenNoResults() {
        String orderCode = "ORDER456";

        Mockito.when(searchResult.getResult()).thenReturn(Collections.emptyList());
        Mockito.when(flexibleSearchService.search(Mockito.any(FlexibleSearchQuery.class)))
            .thenReturn((SearchResult) searchResult);

        List<BusinessProcessModel> result = partnerProcessDao.getProcessListByCode(orderCode);

        Assert.assertNull(result);
    }

    @Test
    public void testGetProcessListByCode_ReturnsNullWhenSearchResultIsNull() {
        String orderCode = "ORDER789";

        Mockito.when(flexibleSearchService.search(Mockito.any(FlexibleSearchQuery.class)))
            .thenReturn(null);

        List<BusinessProcessModel> result = partnerProcessDao.getProcessListByCode(orderCode);

        Assert.assertNull(result);
    }
}
