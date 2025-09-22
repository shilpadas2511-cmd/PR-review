package com.ibm.commerce.partner.core.partnerSalesOrg.dao.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import com.ibm.commerce.partner.core.model.PartnerSalesOrganisationModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit test class for {@link DefaultPartnerSalesOrgDao}.
 */
public class DefaultPartnerSalesOrgDaoTest {

    @InjectMocks
    private DefaultPartnerSalesOrgDao defaultPartnerSalesOrgDao;

    @Mock
    private FlexibleSearchService flexibleSearchService;

    private static final String SALES_ORG_CODE_1 = "ORG1";
    private static final String SALES_ORG_CODE_2 = "ORG2";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        defaultPartnerSalesOrgDao = new DefaultPartnerSalesOrgDao();
        defaultPartnerSalesOrgDao.setFlexibleSearchService(flexibleSearchService);
    }

    /**
     * Test case to check that DAO returns list of PartnerSalesOrganisationModel
     * when valid codes are provided.
     */
    @Test
    public void testGetSalesOrgsByCodes() {
        SearchResult<PartnerSalesOrganisationModel> searchResult = mock(SearchResult.class);
        PartnerSalesOrganisationModel model1 = mock(PartnerSalesOrganisationModel.class);
        PartnerSalesOrganisationModel model2 = mock(PartnerSalesOrganisationModel.class);

        doReturn(searchResult).when(flexibleSearchService).search(any(FlexibleSearchQuery.class));
        doReturn(Arrays.asList(model1, model2)).when(searchResult).getResult();

        List<PartnerSalesOrganisationModel> results = defaultPartnerSalesOrgDao.getSalesOrgsByCodes(
            Arrays.asList(SALES_ORG_CODE_1, SALES_ORG_CODE_2));

        Assert.assertEquals(2, results.size());
    }

    /**
     * Test case when salesOrgCodes is null.
     */
    @Test
    public void testGetSalesOrgsByCodes_Null() {
        List<PartnerSalesOrganisationModel> results = defaultPartnerSalesOrgDao.getSalesOrgsByCodes(null);
        Assert.assertTrue(results.isEmpty());
    }

    /**
     * Test case when salesOrgCodes is an empty list.
     */
    @Test
    public void testGetSalesOrgsByCodes_Empty() {
        List<PartnerSalesOrganisationModel> results = defaultPartnerSalesOrgDao.getSalesOrgsByCodes(Collections.emptyList());
        Assert.assertTrue(results.isEmpty());
    }

    /**
     * Test case when query returns an empty result list.
     */
    @Test
    public void testGetSalesOrgsByCodes_ResultEmpty() {
        SearchResult<PartnerSalesOrganisationModel> searchResult = mock(SearchResult.class);
        doReturn(searchResult).when(flexibleSearchService).search(any(FlexibleSearchQuery.class));
        doReturn(Collections.emptyList()).when(searchResult).getResult();

        List<PartnerSalesOrganisationModel> results = defaultPartnerSalesOrgDao.getSalesOrgsByCodes(
            Arrays.asList("ANY_CODE"));

        Assert.assertNotNull(results);
        Assert.assertTrue(results.isEmpty());
    }
}
