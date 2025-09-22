
package com.ibm.commerce.partner.core.search.solrfacetsearch.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Test class for {@link PartnerSearchResponseRootCategoryDataSearchPopulator}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PartnerSearchResponseRootCategoryDataSearchPopulatorTest
{
	private static final String ROOT_CATEGORY_JSON = "{\"code\":\"Aspera\"}";
	private static final String ROOT_CATEGORY = "Aspera";

	@InjectMocks
	PartnerSearchResponseRootCategoryDataSearchPopulator partnerSearchResponseRootCategoryDataSearchPopulator;
	String categoryJsonCode = "categoryjson";
	@Mock
	SolrSearchResponse solrSearchResponse;
	ProductSearchPageData<?, SearchResultValueData> stateitemProductSearchPageData;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		partnerSearchResponseRootCategoryDataSearchPopulator = new PartnerSearchResponseRootCategoryDataSearchPopulator(
				categoryJsonCode);
	}

	@Test
	public void testPopulate()
	{
		createData(categoryJsonCode, ROOT_CATEGORY_JSON);
		partnerSearchResponseRootCategoryDataSearchPopulator.populate(solrSearchResponse, stateitemProductSearchPageData);
		Assert.assertEquals(ROOT_CATEGORY, stateitemProductSearchPageData.getRootCategoryData().getCode());
	}

	@Test
	public void testPopulateSearchResultNull()
	{
		stateitemProductSearchPageData = new ProductSearchPageData<>();
		partnerSearchResponseRootCategoryDataSearchPopulator.populate(solrSearchResponse, stateitemProductSearchPageData);
		Assert.assertNull(stateitemProductSearchPageData.getRootCategoryData());
	}

	@Test
	public void testPopulateCategoryJsonCodeNull()
	{
		createData("code", ROOT_CATEGORY_JSON);
		partnerSearchResponseRootCategoryDataSearchPopulator.populate(solrSearchResponse, stateitemProductSearchPageData);
		Assert.assertNull(stateitemProductSearchPageData.getRootCategoryData());
	}

	@Test(expected = RuntimeException.class)
	public void testPopulateJsonProcessingException()
	{
		createData(categoryJsonCode, "Aspera");
		partnerSearchResponseRootCategoryDataSearchPopulator.populate(solrSearchResponse, stateitemProductSearchPageData);
	}

	private void createData(final String fieldName, final String fieldValue)
	{
		final SearchResultValueData searchResultValueData = new SearchResultValueData();

		final Map<String, Object> searchResultValues = new HashMap();
		searchResultValues.put(fieldName, fieldValue);
		searchResultValueData.setValues(searchResultValues);

		final List<SearchResultValueData> searchResultValueDataList = new ArrayList<>();
		searchResultValueDataList.add(searchResultValueData);

		stateitemProductSearchPageData = new ProductSearchPageData<>();
		stateitemProductSearchPageData.setResults(searchResultValueDataList);
	}
}
