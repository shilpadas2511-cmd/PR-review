package com.ibm.commerce.partner.core.actions.order;

import com.ibm.commerce.partner.core.enums.CpqPricingTypeEnum;
import com.ibm.commerce.partner.core.exceptions.IbmWebServiceFailureException;
import com.ibm.commerce.partner.core.model.PriceLookUpProcessModel;
import com.ibm.commerce.partner.core.order.price.data.response.PriceLookUpResponseData;
import com.ibm.commerce.partner.core.pricing.services.PartnerPricingOutboundService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.processengine.model.ProcessTaskModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import java.io.IOException;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@UnitTest
public class EntitledPriceActionTest {

    @Mock
    private PartnerPricingOutboundService mockPricingOutboundService;

    @Mock
    private Converter<PriceLookUpResponseData, AbstractOrderModel> mockResponseReverseDataConverter;

    private EntitledPriceAction entitledPriceAction;

    @Mock
    private Collection<ProcessTaskModel> processTask;

    @Mock
    private ModelService modelService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this); // ✅ modern replacement
        entitledPriceAction = new EntitledPriceAction(3, 1000, mockPricingOutboundService, mockResponseReverseDataConverter);
    }

    @Test
    public void testExecuteAction_Success() throws IOException {
        PriceLookUpProcessModel processModel = new PriceLookUpProcessModel();
        AbstractOrderModel orderModel = new AbstractOrderModel();
        PriceLookUpResponseData responseData = new PriceLookUpResponseData();
        responseData.setType(CpqPricingTypeEnum.ENTITLED);

        entitledPriceAction.setModelService(modelService);

        Mockito.when(mockPricingOutboundService.getEntitledPrice(processModel.getOrder())).thenReturn(responseData);
        Mockito.when(mockResponseReverseDataConverter.convert(responseData, orderModel)).thenReturn(orderModel);

        EntitledPriceAction.Transition result = entitledPriceAction.executeAction(processModel);

        Assert.assertEquals(EntitledPriceAction.Transition.OK, result);
    }

    @Test
    public void testExecuteAction_Failure() throws IOException {
        PriceLookUpProcessModel processModel = new PriceLookUpProcessModel();
        processModel.setCurrentTasks(processTask);
        AbstractOrderModel orderModel = new AbstractOrderModel();

        Mockito.when(mockPricingOutboundService.getEntitledPrice(orderModel))
            .thenThrow(new IbmWebServiceFailureException("Test Exception"));

        EntitledPriceAction.Transition result = entitledPriceAction.executeAction(processModel);

        Assert.assertEquals(EntitledPriceAction.Transition.NOK, result);
    }
}
