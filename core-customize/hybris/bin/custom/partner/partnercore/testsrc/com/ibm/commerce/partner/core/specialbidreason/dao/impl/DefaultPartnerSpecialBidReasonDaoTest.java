package com.ibm.commerce.partner.core.specialbidreason.dao.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ibm.commerce.partner.core.model.PartnerB2BCustomerModel;
import com.ibm.commerce.partner.core.model.PartnerSpecialBidReasonModel;
import com.ibm.commerce.partner.core.util.model.PartnerSpecialBidReasonTestDataGenerator;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class DefaultPartnerSpecialBidReasonDaoTest {

    @InjectMocks
    private DefaultPartnerSpecialBidReasonDao defaultPartnerSpecialBidReasonDao;
    @Mock
    FlexibleSearchService flexibleSearchService;

    private PartnerSpecialBidReasonModel partnerSpecialBidReasonModel;

    private static final String CODE="testcode";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        defaultPartnerSpecialBidReasonDao = new DefaultPartnerSpecialBidReasonDao(
            flexibleSearchService);
    }

    @Test
    public void testGetSpecialBidReasonDetails() {
        SearchResult<PartnerSpecialBidReasonModel> searchResult = mock(SearchResult.class);
        PartnerSpecialBidReasonModel partnerSpecialBidReasonModel = PartnerSpecialBidReasonTestDataGenerator.createSpecialBidreason(CODE,true);
        PartnerSpecialBidReasonModel partnerSpecialBidReasonModel1 = PartnerSpecialBidReasonTestDataGenerator.createSpecialBidreason(CODE,true);

        doReturn(searchResult).when(flexibleSearchService).search(any(FlexibleSearchQuery.class));
        doReturn(Arrays.asList(partnerSpecialBidReasonModel, partnerSpecialBidReasonModel1)).when(searchResult).getResult();
        List<PartnerSpecialBidReasonModel> results = defaultPartnerSpecialBidReasonDao.getAllSpecialBidReasonDetails();
        Assert.assertEquals(2, results.size());
        Assert.assertEquals(CODE, results.get(0).getCode());
    }


    @Test(expected = NullPointerException.class)
    public void testGetSpecialBidReasonDetailsResultsAsEmpty() {
        SearchResult<PartnerB2BCustomerModel> searchResult = mock(SearchResult.class);
        doReturn(searchResult).when(flexibleSearchService).search(any(FlexibleSearchQuery.class));
        doReturn(Arrays.asList()).when(searchResult).getResult();
        List<PartnerSpecialBidReasonModel> results = defaultPartnerSpecialBidReasonDao.getAllSpecialBidReasonDetails();
        Assert.assertTrue(results.isEmpty());
    }

    @Test
    public void testGetSpecialBidReasonById()
    {
        PartnerSpecialBidReasonModel partnerSpecialBidReasonModel =mock(PartnerSpecialBidReasonModel.class);
        final List<Object> partnerSpecialBidReasonModelList = Arrays.asList(partnerSpecialBidReasonModel);
        given(partnerSpecialBidReasonModel.getCode()).willReturn(CODE);
        SearchResult<Object> searchResult = mock(SearchResult.class);
        when(flexibleSearchService.search(any(FlexibleSearchQuery.class))).thenReturn(searchResult);
        when(searchResult.getResult()).thenReturn(partnerSpecialBidReasonModelList);
        final PartnerSpecialBidReasonModel result = defaultPartnerSpecialBidReasonDao.getSpecialBidReasonById(CODE);
        Assert.assertNotNull(result);
        Assert.assertEquals(partnerSpecialBidReasonModel, result);
        Assert.assertEquals(CODE, result.getCode());
    }


}
