package com.ibm.commerce.partner.core.actions.order;

import static org.easymock.EasyMock.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ibm.commerce.partner.core.enums.CpqPricingTypeEnum;
import com.ibm.commerce.partner.core.exceptions.IbmWebServiceFailureException;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.PartnerCpqHeaderPricingDetailModel;
import com.ibm.commerce.partner.core.model.PriceLookUpProcessModel;
import com.ibm.commerce.partner.core.order.price.data.response.PriceLookUpResponseData;
import com.ibm.commerce.partner.core.pricing.services.PartnerPricingOutboundService;
import com.ibm.commerce.partner.core.services.PriceLookUpService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.processengine.model.ProcessTaskModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class FullPriceActionTest extends TestCase {

    @Mock
    private PartnerPricingOutboundService mockPricingOutboundService;

    @Mock
    private Converter<PriceLookUpResponseData, AbstractOrderModel> mockResponseReverseDataConverter;

    private FullPriceAction fullPriceAction;

    @Mock
    Collection<ProcessTaskModel> processTask;

    @Mock
    private PriceLookUpService priceLookUpService;

    @Mock
    ModelService modelService;
    @Mock private Converter<PriceLookUpResponseData, AbstractOrderModel> responseReverseDataConverter;
    @Mock private PriceLookUpProcessModel mockProcessModel;
    @Mock private IbmPartnerCartModel mockCart;
    @Mock private PriceLookUpResponseData mockResponseData;
    @Mock private AbstractOrderModel mockOrder;
    @Mock private PartnerCpqHeaderPricingDetailModel mockCpqPricingDetail;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        fullPriceAction = new FullPriceAction(3, 1000, mockPricingOutboundService,priceLookUpService,
            mockResponseReverseDataConverter);
    }

    @Test
    public void testExecuteAction_Success() throws IOException {
        PriceLookUpProcessModel processModel = new PriceLookUpProcessModel();
        AbstractOrderModel orderModel = new AbstractOrderModel();
        PriceLookUpResponseData responseData = new PriceLookUpResponseData();
        responseData.setType(CpqPricingTypeEnum.FULL);
        fullPriceAction.setModelService(modelService);

        when(mockPricingOutboundService.getFullPrice(processModel.getOrder())).thenReturn(
            responseData);
        when(mockResponseReverseDataConverter.convert(responseData, orderModel)).thenReturn(
            orderModel);

        FullPriceAction.Transition result = fullPriceAction.executeAction(processModel);

        assertEquals(FullPriceAction.Transition.OK, result);
    }

    @Test
    public void testExecuteAction_Failure() throws IOException {

        PriceLookUpProcessModel processModel = new PriceLookUpProcessModel();
        processModel.setCurrentTasks(processTask);
        AbstractOrderModel orderModel = new AbstractOrderModel();

        when(mockPricingOutboundService.getFullPrice(orderModel)).thenThrow(
            new IbmWebServiceFailureException("Test Exception"));

        FullPriceAction.Transition result = fullPriceAction.executeAction(processModel);

        assertEquals(FullPriceAction.Transition.NOK, result);

    }
    @Test
    public void testUpdateYtyForChildEntries() {
        when(mockProcessModel.getOrder()).thenReturn(mockCart);
        when(priceLookUpService.getHeaderPricingDetail(mockCart)).thenReturn(mockCpqPricingDetail);
        Map<String, Double> ytyYears = new HashMap<>();
        ytyYears.put("2023", 10.0);
        when(mockCpqPricingDetail.getYtyYears()).thenReturn(ytyYears);
        AbstractOrderEntryModel mockEntry = mock(AbstractOrderEntryModel.class);
        List<AbstractOrderEntryModel> entries = Collections.singletonList(mockEntry);
        when(mockCart.getEntries()).thenReturn(entries);
        when(mockEntry.getChildEntries()).thenReturn(Collections.singletonList(mock(AbstractOrderEntryModel.class)));
        fullPriceAction.updateYtyForChildEntries(mockCart);
        verify(priceLookUpService).getCpqPricingDetail(any(), eq(CpqPricingTypeEnum.FULL));
    }

}