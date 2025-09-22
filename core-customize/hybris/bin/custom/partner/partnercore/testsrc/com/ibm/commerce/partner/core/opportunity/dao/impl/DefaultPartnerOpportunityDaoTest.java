package com.ibm.commerce.partner.core.opportunity.dao.impl;

import com.ibm.commerce.partner.core.model.IbmPartnerOpportunityModel;
import de.hybris.bootstrap.annotations.UnitTest;
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
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Test class for {@link DefaultPartnerOpportunityDao}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPartnerOpportunityDaoTest {

	private static final String OPPORTUNITY_ID = "test_opportunity_id";

	@InjectMocks
	private DefaultPartnerOpportunityDao defaultPartnerOpportunityDao;

	@Mock
	private IbmPartnerOpportunityModel ibmPartnerOpportunityModel;

	@Mock
	private FlexibleSearchService flexibleSearchService;

	@Mock
	private SearchResult<Object> searchResult;

	@Before
	public void setUp() {
		defaultPartnerOpportunityDao = new DefaultPartnerOpportunityDao();
		defaultPartnerOpportunityDao.setFlexibleSearchService(flexibleSearchService);
	}

	@Test
	public void testFetch() {
		List<Object> ibmPartnerOpportunityModelList = Arrays.asList(ibmPartnerOpportunityModel);

		Mockito.when(ibmPartnerOpportunityModel.getCode()).thenReturn(OPPORTUNITY_ID);
		Mockito.when(flexibleSearchService.search(Mockito.any(FlexibleSearchQuery.class))).thenReturn(searchResult);
		Mockito.when(searchResult.getResult()).thenReturn(ibmPartnerOpportunityModelList);

		IbmPartnerOpportunityModel result = defaultPartnerOpportunityDao.fetch(OPPORTUNITY_ID);

		Assert.assertNotNull(result);
		Assert.assertEquals(ibmPartnerOpportunityModel, result);
		Assert.assertEquals(OPPORTUNITY_ID, result.getCode());
	}

	@Test
	public void testFetchOpportunityIdNull() {
		Assert.assertNull(defaultPartnerOpportunityDao.fetch(null));
	}

	@Test
	public void testFetchSearchResultEmpty() {
		Mockito.when(flexibleSearchService.search(Mockito.any(FlexibleSearchQuery.class))).thenReturn(searchResult);
		Mockito.when(searchResult.getResult()).thenReturn(new ArrayList<>());

		Assert.assertNull(defaultPartnerOpportunityDao.fetch(OPPORTUNITY_ID));
	}
}
