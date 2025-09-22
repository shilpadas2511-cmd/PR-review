package com.ibm.commerce.partner.core.pricing.converters.populators.response;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.ibm.commerce.common.core.product.service.IbmProductService;
import com.ibm.commerce.partner.core.order.price.data.response.DealRegResponseData;
import com.ibm.commerce.partner.core.util.model.AbstractOrderEntryModelTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.sap.productconfig.services.model.CPQOrderEntryProductInfoModel;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@UnitTest
public class DealRegResponsePopulatorTest {

    @Mock
    private IbmProductService mockProductService;

    @Mock
    ModelService modelService;

    @InjectMocks
    private DealRegResponsePopulator dealRegResponsePopulator;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testPopulate() {
        DealRegResponseData dealRegResponseData = new DealRegResponseData();
        dealRegResponseData.setPartNum("part123");
        List<DealRegResponseData> dealRegResponseList = Arrays.asList(dealRegResponseData);

        AbstractOrderEntryModel orderEntry = AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntryModel(
            0,
            Collections.singletonList(new AbstractOrderEntryModel()));

        AbstractOrderEntryModel childOrderEntry = AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntry(
            0);
        childOrderEntry.setProductInfos(
            Collections.singletonList(new CPQOrderEntryProductInfoModel()));
        orderEntry.setChildEntries(Collections.singletonList(childOrderEntry));

        AbstractOrderModel orderModel = new AbstractOrderModel();
        orderModel.setEntries(Collections.singletonList(orderEntry));
        when(mockProductService.getProductCode(any())).thenReturn("part123");

        dealRegResponsePopulator.populate(dealRegResponseList, orderModel);
        Optional<AbstractOrderEntryModel> pidEntry = orderModel.getEntries().stream().findFirst();
        if (pidEntry.isPresent()) {
            Optional<AbstractOrderEntryModel> childEntry = pidEntry.get().getChildEntries().stream()
                .findFirst();
            assertNotNull(childEntry.get().getProductInfos());
        }
    }

}