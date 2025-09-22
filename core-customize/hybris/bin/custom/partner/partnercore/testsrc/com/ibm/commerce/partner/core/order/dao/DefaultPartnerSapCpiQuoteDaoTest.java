package com.ibm.commerce.partner.core.order.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ibm.commerce.partner.core.model.IbmPartnerB2BUnitModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.search.flexiblesearch.PagedFlexibleSearchService;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.search.exceptions.FlexibleSearchException;
import de.hybris.platform.store.BaseStoreModel;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPartnerSapCpiQuoteDaoTest {

    @InjectMocks
    DefaultPartnerSapCpiQuoteDao defaultPartnerSapCpiQuoteDao;

    @Mock
    FlexibleSearchService flexibleSearchService;

    @Mock
    PagedFlexibleSearchService pagedFlexibleSearchService;
    private final String mockPartnerQuoteCloneActiveStatus = "CLONE_CREATED,CLONE_FAILED";

    @Mock
    private SearchResult<Object> mockSearchResult;


    private BaseStoreModel mockStore;
    private PageableData mockPageableData;
    private SearchPageData mockSearchPageData;
    private IbmPartnerB2BUnitModel mockIbmPartnerB2BUnitModel;
    private Set<QuoteState> mockQuoteStates;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        defaultPartnerSapCpiQuoteDao.setFlexibleSearchService(flexibleSearchService);
        mockIbmPartnerB2BUnitModel = mock(IbmPartnerB2BUnitModel.class);
        mockStore = mock(BaseStoreModel.class);
        mockPageableData = mock(PageableData.class);
        mockSearchPageData = mock(SearchPageData.class);
        mockQuoteStates = new LinkedHashSet<>();
    }

    @Test
    public void testGetQuotesBySiteIds() {
        defaultPartnerSapCpiQuoteDao = new DefaultPartnerSapCpiQuoteDao(pagedFlexibleSearchService,
            mockPartnerQuoteCloneActiveStatus);
        mockQuoteStates.add(QuoteState.SUBMITTED);
        when(pagedFlexibleSearchService.search(anyList(), anyString(), anyMap(),
            any(PageableData.class))).thenReturn(
            mockSearchPageData);

        assertEquals(defaultPartnerSapCpiQuoteDao.getQuotesBySiteIds(
            Collections.singletonList(mockIbmPartnerB2BUnitModel), mockStore, mockPageableData,
            mockQuoteStates), mockSearchPageData);
    }

    @Test
    public void testFindUniqueQuoteByCodeAndSiteIdsAndStore() {
        BaseStoreModel store = mock(BaseStoreModel.class);
        Set<QuoteState> quoteStates = Collections.singleton(QuoteState.BUYER_DRAFT);
        String quoteCode = "testQuoteCode";

        QuoteModel expectedQuote = mock(QuoteModel.class);

        when(flexibleSearchService.searchUnique(any(FlexibleSearchQuery.class))).thenReturn(
            expectedQuote);

        QuoteModel actualQuote = defaultPartnerSapCpiQuoteDao.findUniqueQuoteByCodeAndSiteIdsAndStore(
            Collections.singletonList(mockIbmPartnerB2BUnitModel), store, quoteCode, quoteStates);

        assertEquals(expectedQuote, actualQuote);
        verify(flexibleSearchService).searchUnique(any(FlexibleSearchQuery.class));
    }

    @Test
    public void testFindUniqueQuoteByCodeAndSiteIdsAndStore_NullQuoteCode() {
        BaseStoreModel store = mock(BaseStoreModel.class);
        Set<QuoteState> quoteStates = Collections.singleton(QuoteState.BUYER_DRAFT);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            defaultPartnerSapCpiQuoteDao.findUniqueQuoteByCodeAndSiteIdsAndStore(Collections.singletonList(mockIbmPartnerB2BUnitModel),
                store, null, quoteStates)
        );
        assertEquals("Quote Code cannot be null", exception.getMessage());
    }

    @Test
    public void testFindUniqueQuoteByCodeAndSiteIdsAndStore_NullB2BUnitGroups() {
        BaseStoreModel store = mock(BaseStoreModel.class);
        Set<QuoteState> quoteStates = Collections.singleton(QuoteState.BUYER_DRAFT);
        String quoteCode = "testQuoteCode";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            defaultPartnerSapCpiQuoteDao.findUniqueQuoteByCodeAndSiteIdsAndStore(null, store,
                quoteCode, quoteStates)
        );
        assertEquals("sites must not be null", exception.getMessage());
    }

    @Test
    public void testFindUniqueQuoteByCodeAndSiteIdsAndStore_NullStore() {
        Set<QuoteState> quoteStates = Collections.singleton(QuoteState.BUYER_DRAFT);
        String quoteCode = "testQuoteCode";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            defaultPartnerSapCpiQuoteDao.findUniqueQuoteByCodeAndSiteIdsAndStore(Collections.singletonList(mockIbmPartnerB2BUnitModel),
                null, quoteCode, quoteStates)
        );
        assertEquals("Store must not be null", exception.getMessage());
    }

    @Test
    public void testFindUniqueQuoteByCodeAndSiteIdsAndStore_NullOrEmptyQuoteStates() {
        BaseStoreModel store = mock(BaseStoreModel.class);
        String quoteCode = "testQuoteCode";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            defaultPartnerSapCpiQuoteDao.findUniqueQuoteByCodeAndSiteIdsAndStore(Collections.singletonList(mockIbmPartnerB2BUnitModel),
                store, quoteCode, null)
        );
        assertEquals("Quote states cannot be null or empty", exception.getMessage());
    }

    @Test
    public void testFindUniqueQuoteByCodeAndSiteIdsAndStore_QuoteNotFound() {
        BaseStoreModel store = mock(BaseStoreModel.class);
        Set<QuoteState> quoteStates = Collections.singleton(QuoteState.BUYER_DRAFT);
        String quoteCode = "testQuoteCode";

        when(flexibleSearchService.searchUnique(any(FlexibleSearchQuery.class))).thenReturn(null);

        QuoteModel actualQuote = defaultPartnerSapCpiQuoteDao.findUniqueQuoteByCodeAndSiteIdsAndStore(
            Collections.singletonList(mockIbmPartnerB2BUnitModel), store, quoteCode, quoteStates);

        assertNull(actualQuote);
    }

    @Test
    public void testFindUniqueQuoteByCodeAndSiteIdsAndStore_FlexibleSearchServiceThrowsException() {
        BaseStoreModel store = mock(BaseStoreModel.class);
        Set<QuoteState> quoteStates = Collections.singleton(QuoteState.BUYER_DRAFT);
        String quoteCode = "testQuoteCode";

        when(flexibleSearchService.searchUnique(any(FlexibleSearchQuery.class)))
            .thenThrow(new FlexibleSearchException("Flexible search error"));

        FlexibleSearchException exception = assertThrows(FlexibleSearchException.class, () ->
            defaultPartnerSapCpiQuoteDao.findUniqueQuoteByCodeAndSiteIdsAndStore(Collections.singletonList(mockIbmPartnerB2BUnitModel),
                store, quoteCode, quoteStates)
        );
        assertEquals("Flexible search error", exception.getMessage());
    }

    @Test
    public void testGetActiveQuotesInCloneCreatedState() {
        IbmPartnerQuoteModel mockQuote = new IbmPartnerQuoteModel();
        defaultPartnerSapCpiQuoteDao = new DefaultPartnerSapCpiQuoteDao(pagedFlexibleSearchService,
            mockPartnerQuoteCloneActiveStatus);
        defaultPartnerSapCpiQuoteDao.setFlexibleSearchService(flexibleSearchService);
        when(mockSearchResult.getResult()).thenReturn(Collections.singletonList(mockQuote));
        when(flexibleSearchService.search(any(FlexibleSearchQuery.class))).thenReturn(mockSearchResult);

        List<IbmPartnerQuoteModel> result = defaultPartnerSapCpiQuoteDao.getActiveQuotesInCloneCreatedState();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockQuote, result.get(0));

        verify(flexibleSearchService).search(any(FlexibleSearchQuery.class));
    }
}