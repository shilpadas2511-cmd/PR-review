package com.ibm.commerce.partner.core.daos.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
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

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultIbmConsumedDestinationDaoTest {

    private static final String ID = "id";
    private static final String DESTINATION_TARGET_ID = "destinationTargetId";

    @InjectMocks
    DefaultIbmConsumedDestinationDao defaultIbmConsumedDestinationDao;

    @Mock
    FlexibleSearchService flexibleSearchService;

    @Mock
    SearchResult result;

    @Mock
    ConsumedDestinationModel consumedDestinationModel;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        defaultIbmConsumedDestinationDao = new DefaultIbmConsumedDestinationDao();
        defaultIbmConsumedDestinationDao.setFlexibleSearchService(flexibleSearchService);
    }

    @Test
    public void testFindActiveConsumedDestinationByIdAndTargetId() {
        Mockito.when(flexibleSearchService.search(Mockito.any(FlexibleSearchQuery.class)))
            .thenReturn(result);
        Mockito.when(result.getResult()).thenReturn(Arrays.asList(consumedDestinationModel));

        ConsumedDestinationModel resultModel = defaultIbmConsumedDestinationDao
            .findActiveConsumedDestinationByIdAndTargetId(ID, DESTINATION_TARGET_ID);

        Assert.assertNotNull(resultModel);
        Assert.assertEquals(consumedDestinationModel, resultModel);
    }

    @Test
    public void testFindActiveConsumedDestinationByIdAndTargetIdWithNullResult() {
        Mockito.when(flexibleSearchService.search(Mockito.any(FlexibleSearchQuery.class)))
            .thenReturn(null);

        ConsumedDestinationModel resultModel = defaultIbmConsumedDestinationDao
            .findActiveConsumedDestinationByIdAndTargetId(ID, DESTINATION_TARGET_ID);

        Assert.assertNull(resultModel);
    }

    @Test
    public void testFindActiveConsumedDestinationByIdAndTargetIdWithNullResults() {
        Mockito.when(flexibleSearchService.search(Mockito.any(FlexibleSearchQuery.class)))
            .thenReturn(result);
        Mockito.when(result.getResult()).thenReturn(null);

        ConsumedDestinationModel resultModel = defaultIbmConsumedDestinationDao
            .findActiveConsumedDestinationByIdAndTargetId(ID, DESTINATION_TARGET_ID);

        Assert.assertNull(resultModel);
    }
}
