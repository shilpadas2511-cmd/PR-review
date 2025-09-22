package com.ibm.commerce.partner.core.actions.order;

import com.ibm.commerce.partner.core.actions.PartnerAbstractSimpleDecisionAction;
import com.ibm.commerce.partner.core.model.PriceLookUpProcessModel;
import com.ibm.commerce.partner.core.order.price.data.response.DealRegResponseData;
import com.ibm.commerce.partner.core.pricing.services.PartnerPricingOutboundService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.processengine.model.ProcessTaskModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@UnitTest
public class DealRegActionTest {

    @Mock
    private PartnerPricingOutboundService mockPricingOutboundService;

    @Mock
    private Converter<List<DealRegResponseData>, AbstractOrderModel> mockResponseReverseDataConverter;

    private DealRegAction dealRegAction;

    @Mock
    private ModelService modelService;

    @Mock
    private AbstractOrderModel orderModel;

    @Mock
    private Collection<ProcessTaskModel> processTask;

    private PriceLookUpProcessModel processModel;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        dealRegAction = new DealRegAction(3, 1000, mockPricingOutboundService, mockResponseReverseDataConverter);
        processModel = new PriceLookUpProcessModel();
    }

    @Test
    public void testExecuteAction_Success() throws IOException {
        List<DealRegResponseData> responseData = new ArrayList<>();

        dealRegAction.setModelService(modelService);
        Mockito.when(mockPricingOutboundService.getDealRegDetail(orderModel)).thenReturn(responseData);
        Mockito.when(mockResponseReverseDataConverter.convert(responseData, orderModel)).thenReturn(orderModel);

        PartnerAbstractSimpleDecisionAction.Transition result = dealRegAction.executeAction(processModel);

        Assert.assertEquals(PartnerAbstractSimpleDecisionAction.Transition.OK, result);
    }

    @Test
    public void testExecuteAction_Failure() throws IOException {
        processModel.setCurrentTasks(processTask);

        dealRegAction.setModelService(modelService);
        Mockito.when(mockPricingOutboundService.getDealRegDetail(processModel.getOrder())).thenReturn(null);

        PartnerAbstractSimpleDecisionAction.Transition result = dealRegAction.executeAction(processModel);

        Assert.assertEquals(PartnerAbstractSimpleDecisionAction.Transition.NOK, result);
    }
}
