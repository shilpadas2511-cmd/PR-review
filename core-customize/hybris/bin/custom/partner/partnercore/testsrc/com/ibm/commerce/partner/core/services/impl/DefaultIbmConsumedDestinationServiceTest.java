package com.ibm.commerce.partner.core.services.impl;

import com.ibm.commerce.partner.core.daos.IbmConsumedDestinationDao;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.when;

@UnitTest
public class DefaultIbmConsumedDestinationServiceTest {

    private static final String DESTINATION = "destinationTargetId";
    private static final String ID = "Id";

    @InjectMocks
    DefaultIbmConsumedDestinationService defaultIbmConsumedDestinationService;
    @Mock
    IbmConsumedDestinationDao consumedDestinationDao;
    @Mock
    ConsumedDestinationModel consumedDestinationModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testfindActiveConsumedDestinationByIdAndTargetId() {
        when(consumedDestinationDao.findActiveConsumedDestinationByIdAndTargetId(ID, DESTINATION)).thenReturn(consumedDestinationModel);
        ConsumedDestinationModel result=defaultIbmConsumedDestinationService.findActiveConsumedDestinationByIdAndTargetId(ID, DESTINATION);
        Assert.assertNotNull(consumedDestinationModel);
        Assert.assertEquals(result ,consumedDestinationModel);
    }

}