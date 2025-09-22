package com.ibm.commerce.partner.core.actions.order;

import com.ibm.commerce.partner.core.actions.PartnerAbstractSimpleDecisionAction;
import com.ibm.commerce.partner.core.model.PriceLookUpProcessModel;
import com.ibm.commerce.partner.core.util.model.AbstractOrderEntryModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.AbstractOrderModelTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@UnitTest
public class PartnerOrderEntriesCheckActionTest {

    private PartnerOrderEntriesCheckAction partnerOrderEntriesCheckAction;

    @Mock
    private AbstractOrderModel orderModel;

    private AutoCloseable closeable;

    @Before
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        partnerOrderEntriesCheckAction = new PartnerOrderEntriesCheckAction(3, 1000);
    }

    @After
    public void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void testExecuteAction_Success() throws Exception {
        PriceLookUpProcessModel processModel = new PriceLookUpProcessModel();
        orderModel = AbstractOrderModelTestDataGenerator.createAbstractOrderModel();
        List<AbstractOrderEntryModel> entries = new ArrayList<>();
        entries.add(AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntry());
        orderModel.setEntries(entries);
        processModel.setOrder(orderModel);

        PartnerAbstractSimpleDecisionAction.Transition result =
            partnerOrderEntriesCheckAction.executeAction(processModel);

        Assert.assertEquals(PartnerAbstractSimpleDecisionAction.Transition.OK, result);
    }

    @Test
    public void testExecuteAction_Failure() throws Exception {
        PriceLookUpProcessModel processModel = new PriceLookUpProcessModel();
        processModel.setOrder(orderModel);

        PartnerAbstractSimpleDecisionAction.Transition result =
            partnerOrderEntriesCheckAction.executeAction(processModel);

        Assert.assertEquals(PartnerAbstractSimpleDecisionAction.Transition.NOK, result);
    }
}
