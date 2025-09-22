package com.ibm.commerce.partner.facades.search.solrfacetsearch.converters.populator;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.common.core.model.SellerAudienceMaskModel;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.user.service.PartnerUserService;
import com.ibm.commerce.partner.core.util.SellerAudienceMaskModelTestDataGenerator;
import com.ibm.commerce.partner.facades.util.SearchFilterQueryDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.search.data.SearchFilterQueryData;
import de.hybris.platform.commercefacades.search.data.SearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.FilterQueryOperator;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.core.model.user.UserModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * This test class used for writing test cases for PartnerSolrSearchQueryEncoderPopulator.
 */
@UnitTest
public class PartnerSolrSearchQueryEncoderPopulatorTest {

    private static final String RES_AUDIENCE_MASK = "RES";

    public static final String DEPLOYMENT_TYPE_CODE = "deploymentType";

    @InjectMocks
    private PartnerSolrSearchQueryEncoderPopulator partnerSolrSearchQueryEncoderPopulator;

    @Mock
    private SolrSearchQueryData source;
    @Mock
    private SearchQueryData target;
    @Mock
    private PartnerUserService userService;
    @Mock
    SearchFilterQueryData searchFilterQueryData;
    @Mock
    SearchFilterQueryData searchFilterQueryData1;
    @Mock
    SearchFilterQueryData searchFilterQueryData2;
    @Mock
    List<SearchFilterQueryData> searchFilterQueries;
    String defaultSellerAudienceValue = "RES";

    String defaultDeploymentType = "defaultDeploymentType";
    List<SearchFilterQueryData> searchFilterQueries1;
    @Mock
    SellerAudienceMaskModel sellerAudienceMaskModel;
    @Mock
    SellerAudienceMaskModel sellerAudienceMaskModel1;

    /**
     * setup method of PartnerSolrSearchQueryEncoderPopulatorTest class.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        partnerSolrSearchQueryEncoderPopulator = new PartnerSolrSearchQueryEncoderPopulator(userService,defaultSellerAudienceValue,defaultDeploymentType);
        partnerSolrSearchQueryEncoderPopulator.getUserService();
        partnerSolrSearchQueryEncoderPopulator.getDefaultSellerAudienceValue();
        Set<String> values = Collections.singleton(Boolean.TRUE.toString());
        searchFilterQueryData =
                SearchFilterQueryDataGenerator.createSearchFilterQueryData(PartnercoreConstants.SEARCH_AVAILABILITY,
                        FilterQueryOperator.AND, values);
        searchFilterQueryData1 =
                SearchFilterQueryDataGenerator.createSearchFilterQueryData(PartnercoreConstants.SELLER_AUDIENCE_TYPE,
                        FilterQueryOperator.AND, values);
        searchFilterQueryData2 = SearchFilterQueryDataGenerator.createSearchFilterQueryData(
            DEPLOYMENT_TYPE_CODE, FilterQueryOperator.AND, values);
        searchFilterQueries.add(searchFilterQueryData);
        searchFilterQueries.add(searchFilterQueryData1);
        searchFilterQueries.add(searchFilterQueryData2);

        sellerAudienceMaskModel =
                SellerAudienceMaskModelTestDataGenerator.createSellerAudienceMaskModel(RES_AUDIENCE_MASK);
        sellerAudienceMaskModel1 =
            SellerAudienceMaskModelTestDataGenerator.createSellerAudienceMaskModel();
    }

    /**
     * test method for create search filter queries with search availability true.
     */
    @Test
    public void testCreateSearchFilterQueriesWithSearchAvailability() {
        when(userService.getSellerAudienceMaskForCurrentUser()).thenReturn(sellerAudienceMaskModel);
        partnerSolrSearchQueryEncoderPopulator.populate(source, target);
        Assert.assertEquals(PartnercoreConstants.SEARCH_AVAILABILITY,
                searchFilterQueryData.getKey());
        Assert.assertEquals(PartnercoreConstants.SELLER_AUDIENCE_TYPE,
            searchFilterQueryData1.getKey());
        Assert.assertEquals(PartnercoreConstants.DEPLOYMENT_TYPE_CODE,
            searchFilterQueryData2.getKey());
    }
    @Test
    public void testIsNotPresentWithBlankKey() {
        Optional<SearchFilterQueryData> result = partnerSolrSearchQueryEncoderPopulator.isNotPresent(target, " ");
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void testCreateSearchFilterQueriesWithDeploymentFilterNull() {
        when(userService.getSellerAudienceMaskForCurrentUser()).thenReturn(sellerAudienceMaskModel);
        searchFilterQueries1 = new ArrayList<>();
        searchFilterQueries1.add(searchFilterQueryData2);
        when(target.getFilterQueries()).thenReturn(searchFilterQueries1);
        partnerSolrSearchQueryEncoderPopulator.populate(source, target);
        Assert.assertEquals(PartnercoreConstants.SEARCH_AVAILABILITY,
            searchFilterQueryData.getKey());
        Assert.assertEquals(PartnercoreConstants.SELLER_AUDIENCE_TYPE,
            searchFilterQueryData1.getKey());
        Assert.assertEquals(PartnercoreConstants.DEPLOYMENT_TYPE_CODE,
            searchFilterQueryData2.getKey());
    }

    /**
     * test method for when sellerAudience and searchResult already in filterQuery
     */
    @Test
    public void testCreateQueriesAlreadyHaveSellerAudienceAndSearchResult() {
        UserModel userModel = new UserModel();
        when(userService.getCurrentUser()).thenReturn(userModel);
        when(target.getFilterQueries()).thenReturn(searchFilterQueries);
        partnerSolrSearchQueryEncoderPopulator.populate(source, target);
        Assert.assertEquals(PartnercoreConstants.SEARCH_AVAILABILITY,
                searchFilterQueryData.getKey());
        Assert.assertEquals(PartnercoreConstants.SELLER_AUDIENCE_TYPE,
            searchFilterQueryData1.getKey());
        Assert.assertEquals(PartnercoreConstants.DEPLOYMENT_TYPE_CODE,
            searchFilterQueryData2.getKey());

    }
    @Test
    public void testCreateQueriesWithSellerAudienceMask() {
        UserModel userModel = new UserModel();
        when(userService.getCurrentUser()).thenReturn(userModel);
        when(userService.getSellerAudienceMaskForCurrentUser()).thenReturn(sellerAudienceMaskModel);
        partnerSolrSearchQueryEncoderPopulator.populate(source, target);
        Assert.assertEquals(PartnercoreConstants.SEARCH_AVAILABILITY,
            searchFilterQueryData.getKey());
        Assert.assertEquals(PartnercoreConstants.SELLER_AUDIENCE_TYPE,
            searchFilterQueryData1.getKey());
        Assert.assertEquals(PartnercoreConstants.DEPLOYMENT_TYPE_CODE,
            searchFilterQueryData2.getKey());

    }

    @Test
    public void testCreateQueriesWithDefaultSellerAudienceMask() {
        UserModel userModel = new UserModel();
        when(userService.getCurrentUser()).thenReturn(userModel);
        when(userService.getSellerAudienceMaskForCurrentUser()).thenReturn(sellerAudienceMaskModel1);
        partnerSolrSearchQueryEncoderPopulator.populate(source, target);
        Assert.assertEquals(PartnercoreConstants.SEARCH_AVAILABILITY,
            searchFilterQueryData.getKey());
        Assert.assertEquals(PartnercoreConstants.SELLER_AUDIENCE_TYPE,
            searchFilterQueryData1.getKey());
        Assert.assertEquals(PartnercoreConstants.DEPLOYMENT_TYPE_CODE,
            searchFilterQueryData2.getKey());

    }

   @Test
   public void testCreateSearchFilterQueriesEmpty() {
       List<SearchFilterQueryData> listSearchQueryData = Collections.emptyList();
       target.setFilterQueries(listSearchQueryData);
       partnerSolrSearchQueryEncoderPopulator.populate(source, target);
       Assert.assertEquals(PartnercoreConstants.SEARCH_AVAILABILITY,
           searchFilterQueryData.getKey());
       Assert.assertEquals(PartnercoreConstants.SELLER_AUDIENCE_TYPE,
           searchFilterQueryData1.getKey());
       Assert.assertEquals(PartnercoreConstants.DEPLOYMENT_TYPE_CODE,
           searchFilterQueryData2.getKey());
   }


}