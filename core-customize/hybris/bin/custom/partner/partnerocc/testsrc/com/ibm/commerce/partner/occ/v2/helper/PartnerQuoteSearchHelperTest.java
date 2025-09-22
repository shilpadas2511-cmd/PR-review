package com.ibm.commerce.partner.occ.v2.helper;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.search.facetdata.PartnerQuoteSearchPageData;
import com.ibm.commerce.partner.core.user.service.PartnerUserService;
import com.ibm.commerce.partner.facades.quote.search.PartnerQuoteSearchFacade;
import com.ibm.commerce.partnerwebservicescommons.dto.search.facetdata.PartnerQuoteSearchPageWsDTO;
import com.ibm.commerce.partnerwebservicescommons.dto.search.request.PartnerQuoteSearchRequestWsDTO;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commercefacades.quote.data.QuoteData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.enums.SearchQueryContext;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryTermData;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

@RunWith(MockitoJUnitRunner.class)
public class PartnerQuoteSearchHelperTest {

    @InjectMocks
    private PartnerQuoteSearchHelper partnerQuoteSearchHelper;
    @Mock
    private PartnerQuoteSearchFacade partnerQuoteSearchFacade;
    @Mock
    private Converter<SolrSearchQueryData, SearchStateData> solrSearchStateConverter;
    @Mock
    private PartnerUserService partnerUserService;
    @Mock
    private B2BCustomerModel b2bCustomerModel;
    @Mock
    private B2BUnitModel b2bUnitModel;
    @Mock
    private DataMapper dataMapper;

    @Before
    public void setUp() {
        UserModel mockUser = new UserModel();
        mockUser.setUid("testUser");
        B2BUnitModel mockB2BUnit = new B2BUnitModel();
        mockB2BUnit.setUid("testUnit");
        Set<PrincipalGroupModel> groups = new HashSet<>();
        groups.add(mockB2BUnit);
        mockUser.setGroups(groups);
        Mockito.when(partnerUserService.getCurrentUser()).thenReturn(mockUser);
        Mockito.when(dataMapper.map(Mockito.any(), Mockito.eq(PartnerQuoteSearchPageWsDTO.class)))
            .thenReturn(new PartnerQuoteSearchPageWsDTO());
    }

    @Test
    public void testSearchQuotes_Success_AllQuotes() {
        PartnerQuoteSearchRequestWsDTO searchRequestWsDTO = new PartnerQuoteSearchRequestWsDTO();
        searchRequestWsDTO.setQuoteType(PartnercoreConstants.QUOTE_SEARCH_QUOTE_TYPE_ALL_QUOTES);
        searchRequestWsDTO.setFromDate(String.valueOf(new Date()));
        searchRequestWsDTO.setToDate(String.valueOf(new Date()));
        SearchStateData searchStateData = new SearchStateData();
        PartnerQuoteSearchPageData<SearchStateData, QuoteData> searchPageData = new PartnerQuoteSearchPageData<>();
        Mockito.when(solrSearchStateConverter.convert(Mockito.any())).thenReturn(searchStateData);
        Mockito.when(partnerQuoteSearchFacade.textSearch(Mockito.any(), Mockito.any())).thenReturn(searchPageData);
        PartnerQuoteSearchPageWsDTO result = partnerQuoteSearchHelper.searchQuotes(
            "test-query", searchRequestWsDTO, 0, 10, "name-asc", "name", "DEFAULT"
        );
        Assert.assertNotNull(result);
    }

    @Test
    public void testSearchQuotes_Success_MyQuotes() {
        PartnerQuoteSearchRequestWsDTO searchRequestWsDTO = new PartnerQuoteSearchRequestWsDTO();
        searchRequestWsDTO.setQuoteType(PartnercoreConstants.QUOTE_SEARCH_QUOTE_TYPE_MY_QUOTES);
        searchRequestWsDTO.setFromDate(String.valueOf(new Date()));
        searchRequestWsDTO.setToDate(String.valueOf(new Date()));
        SearchStateData searchStateData = new SearchStateData();
        PartnerQuoteSearchPageData<SearchStateData, QuoteData> searchPageData = new PartnerQuoteSearchPageData<>();
        Mockito.when(solrSearchStateConverter.convert(Mockito.any())).thenReturn(searchStateData);
        Mockito.when(partnerQuoteSearchFacade.textSearch(Mockito.any(), Mockito.any())).thenReturn(searchPageData);

        UserModel mockUser = Mockito.spy(new UserModel());
        Mockito.doReturn(de.hybris.platform.core.PK.fromLong(12345L)).when(mockUser).getPk();
        mockUser.setUid("testUser");
        Set<PrincipalGroupModel> groups = new HashSet<>();
        mockUser.setGroups(groups);
        Mockito.when(partnerUserService.getCurrentUser()).thenReturn(mockUser);

        PartnerQuoteSearchPageWsDTO result = partnerQuoteSearchHelper.searchQuotes(
            "test-query", searchRequestWsDTO, 0, 10, "name-asc", "name", "DEFAULT"
        );
        Assert.assertNotNull(result);
    }

    @Test
    public void testSearchQuotes_Success_IbmSellerQuotes() {
        PartnerQuoteSearchRequestWsDTO searchRequestWsDTO = new PartnerQuoteSearchRequestWsDTO();
        searchRequestWsDTO.setQuoteType(
            PartnercoreConstants.QUOTE_SEARCH_QUOTE_TYPE_IBM_SELLER_QUOTES);
        searchRequestWsDTO.setFromDate(String.valueOf(new Date()));
        searchRequestWsDTO.setToDate(String.valueOf(new Date()));
        SearchStateData searchStateData = new SearchStateData();
        PartnerQuoteSearchPageData<SearchStateData, QuoteData> searchPageData = new PartnerQuoteSearchPageData<>();
        Mockito.when(solrSearchStateConverter.convert(Mockito.any())).thenReturn(searchStateData);
        Mockito.when(partnerQuoteSearchFacade.textSearch(Mockito.any(), Mockito.any())).thenReturn(searchPageData);
        PartnerQuoteSearchPageWsDTO result = partnerQuoteSearchHelper.searchQuotes(
            "test-query", searchRequestWsDTO, 0, 10, "name-asc", "name", "DEFAULT"
        );
        Assert.assertNotNull(result);
    }

    @Test(expected = RequestParameterException.class)
    public void testDecodeContext_InvalidContext() {
        partnerQuoteSearchHelper.decodeContext("INVALID_CONTEXT");
    }

    @Test
    public void testDecodeContext_ValidContext() {
        SearchQueryContext result = partnerQuoteSearchHelper.decodeContext("DEFAULT");
        Assert.assertEquals(SearchQueryContext.DEFAULT, result);
    }

    @Test
    public void testDecodeContext_EmptyString() {
        SearchQueryContext result = partnerQuoteSearchHelper.decodeContext("");
        Assert.assertNull(result);
    }

    @Test
    public void testCreateSiteIdFilter() {
        Mockito.when(partnerUserService.getCurrentUser()).thenReturn(b2bCustomerModel);
        Mockito.when(b2bCustomerModel.getGroups()).thenReturn(Collections.singleton(b2bUnitModel));
        Mockito.when(b2bUnitModel.getUid()).thenReturn("test-group");
        List<SolrSearchQueryTermData> filterTerms = new ArrayList<>();
        List<SolrSearchQueryTermData> result = partnerQuoteSearchHelper.createSiteIdFilter(filterTerms);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("test-group", result.get(0).getValue());
    }

    @Test
    public void testCreateQueryTerm() {
        SolrSearchQueryTermData term = partnerQuoteSearchHelper.createQueryTerm("test-key", "test-value");
        Assert.assertEquals("test-key", term.getKey());
        Assert.assertEquals("test-value", term.getValue());
    }

    @Test
    public void testSearchQuotes_IBM_SellerQuotes_CoversSiteIdFilter() {
        PartnerQuoteSearchRequestWsDTO searchRequestWsDTO = new PartnerQuoteSearchRequestWsDTO();
        searchRequestWsDTO.setQuoteType(PartnercoreConstants.QUOTE_SEARCH_QUOTE_TYPE_IBM_SELLER_QUOTES);
        searchRequestWsDTO.setFromDate("2023-01-01");
        searchRequestWsDTO.setToDate("2023-12-31");

        B2BUnitModel mockB2BUnit = Mockito.mock(B2BUnitModel.class);
        Mockito.when(mockB2BUnit.getUid()).thenReturn("testUnit");
        Set<PrincipalGroupModel> groups = new HashSet<>();
        groups.add(mockB2BUnit);
        UserModel mockUser = new UserModel();
        mockUser.setGroups(groups);
        Mockito.when(partnerUserService.getCurrentUser()).thenReturn(mockUser);

        SearchStateData searchStateData = new SearchStateData();
        PartnerQuoteSearchPageData<SearchStateData, QuoteData> searchPageData = new PartnerQuoteSearchPageData<>();
        Mockito.when(solrSearchStateConverter.convert(Mockito.any())).thenReturn(searchStateData);
        Mockito.when(partnerQuoteSearchFacade.textSearch(Mockito.any(), Mockito.any())).thenReturn(searchPageData);

        PartnerQuoteSearchPageWsDTO result = partnerQuoteSearchHelper.searchQuotes(
            "test-query", searchRequestWsDTO, 0, 10, "name-asc", "name", "DEFAULT"
        );
        Assert.assertNotNull(result);
    }
}
