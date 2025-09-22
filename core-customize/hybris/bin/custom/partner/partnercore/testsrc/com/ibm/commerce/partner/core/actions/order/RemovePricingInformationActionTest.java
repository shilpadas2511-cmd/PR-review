package com.ibm.commerce.partner.core.actions.order;

import com.ibm.commerce.partner.core.actions.PartnerAbstractSimpleDecisionAction;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.PriceLookUpProcessModel;
import com.ibm.commerce.partner.core.util.model.AbstractOrderEntryModelTestDataGenerator;
import com.sap.hybris.sapcpqquoteintegration.model.CpqPricingDetailModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.processengine.model.ProcessTaskModel;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.Collection;
import java.util.Collections;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@UnitTest
public class RemovePricingInformationActionTest {

    @Mock
    private ModelService mockModelService;

    private RemovePricingInformationAction removePricingInformationAction;

    @Mock
    private Collection<ProcessTaskModel> processTask;

    @Mock
    private CalculationService mockCalculationService;

    private AutoCloseable closeable;

    @Before
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        removePricingInformationAction =
            new RemovePricingInformationAction(3, 1000, mockCalculationService);
        removePricingInformationAction.setModelService(mockModelService);
    }

    @After
    public void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void testExecuteAction_Success() throws CalculationException {
        PriceLookUpProcessModel processModel = new PriceLookUpProcessModel();
        processModel.setCurrentTasks(processTask);

        IbmPartnerCartModel order = new IbmPartnerCartModel();
        AbstractOrderEntryModel orderEntry =
            AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntryModel(
                0, Collections.singletonList(new AbstractOrderEntryModel()));
        orderEntry.setCpqPricingDetails(Collections.singletonList(new CpqPricingDetailModel()));

        AbstractOrderEntryModel childOrderEntry =
            AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntry(0);
        childOrderEntry.setCpqPricingDetails(Collections.singletonList(new CpqPricingDetailModel()));

        orderEntry.setChildEntries(Collections.singleton(childOrderEntry));
        order.setEntries(Collections.singletonList(orderEntry));

        processModel.setOrder(order);

        PartnerAbstractSimpleDecisionAction.Transition result =
            removePricingInformationAction.executeAction(processModel);

        Assert.assertEquals(PartnerAbstractSimpleDecisionAction.Transition.OK, result);
    }

    @Test
    public void testExecuteAction_Success_NoChild() throws CalculationException {
        PriceLookUpProcessModel processModel = new PriceLookUpProcessModel();
        processModel.setCurrentTasks(processTask);

        IbmPartnerCartModel order = new IbmPartnerCartModel();
        AbstractOrderEntryModel orderEntry =
            AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntryModel(0, null);
        orderEntry.setCpqPricingDetails(Collections.singletonList(new CpqPricingDetailModel()));

        AbstractOrderEntryModel childOrderEntry =
            AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntry(0);
        childOrderEntry.setCpqPricingDetails(Collections.singletonList(new CpqPricingDetailModel()));

        order.setEntries(Collections.singletonList(orderEntry));
        processModel.setOrder(order);

        PartnerAbstractSimpleDecisionAction.Transition result =
            removePricingInformationAction.executeAction(processModel);

        Assert.assertEquals(PartnerAbstractSimpleDecisionAction.Transition.OK, result);
    }

    @Test
    public void testExecuteAction_Success_NoCpq() throws CalculationException {
        PriceLookUpProcessModel processModel = new PriceLookUpProcessModel();
        processModel.setCurrentTasks(processTask);

        IbmPartnerCartModel order = new IbmPartnerCartModel();
        AbstractOrderEntryModel orderEntry =
            AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntryModel(
                0, Collections.singletonList(new AbstractOrderEntryModel()));

        AbstractOrderEntryModel childOrderEntry =
            AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntry(0);

        orderEntry.setChildEntries(Collections.singleton(childOrderEntry));
        order.setEntries(Collections.singletonList(orderEntry));

        processModel.setOrder(order);

        PartnerAbstractSimpleDecisionAction.Transition result =
            removePricingInformationAction.executeAction(processModel);

        Assert.assertEquals(PartnerAbstractSimpleDecisionAction.Transition.OK, result);
    }

    @Test
    public void testExecuteAction_NullProcessModel() throws Exception {
        PriceLookUpProcessModel processModel = new PriceLookUpProcessModel();
        processModel.setCurrentTasks(processTask);

        PartnerAbstractSimpleDecisionAction.Transition result =
            removePricingInformationAction.executeAction(processModel);

        Assert.assertEquals(PartnerAbstractSimpleDecisionAction.Transition.NOK, result);
    }

    @Test
    public void testGetCalculationService_ReturnsInjectedService() {
        Assert.assertEquals(
            mockCalculationService, removePricingInformationAction.getCalculationService());
    }

    @Test
    public void testExecuteAction_ExceptionHandling() throws Exception {
        PriceLookUpProcessModel processModel = new PriceLookUpProcessModel();
        processModel.setCurrentTasks(processTask);

        IbmPartnerCartModel order = new IbmPartnerCartModel();
        processModel.setOrder(order);

        Mockito.doThrow(new RuntimeException("Simulated"))
            .when(mockModelService)
            .save(order);

        PartnerAbstractSimpleDecisionAction.Transition result =
            removePricingInformationAction.executeAction(processModel);

        Assert.assertEquals(PartnerAbstractSimpleDecisionAction.Transition.NOK, result);
    }
}
