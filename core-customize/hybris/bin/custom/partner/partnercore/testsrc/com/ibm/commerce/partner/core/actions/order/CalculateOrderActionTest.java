package com.ibm.commerce.partner.core.actions.order;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ibm.commerce.partner.core.actions.PartnerAbstractSimpleDecisionAction;
import com.ibm.commerce.partner.core.cart.services.PartnerCommerceCartService;
import com.ibm.commerce.partner.core.cart.strategies.PartnerCartUpdateStrategy;
import com.ibm.commerce.partner.core.cart.strategies.impl.DefaultPartnerCartUpdateReasonSelectionStrategy;
import com.ibm.commerce.partner.core.model.IbmPartnerCartEntryModel;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.PriceLookUpProcessModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.processengine.model.ProcessTaskModel;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@UnitTest
public class CalculateOrderActionTest {

    @Mock
    private CalculationService mockCalculationService;

    @Mock
    private CalculateOrderAction calculateOrderAction;

    @Mock
    Collection<ProcessTaskModel> processTask;
    @Mock
    private ModelService modelService;

    @Mock
    private IbmPartnerCartModel cartModel;

    @Mock
    private IbmPartnerQuoteModel quoteModel;

    @Mock
    private IbmPartnerCartEntryModel ibmPartnerCartEntryModel;

    @Mock
    private IbmPartnerCartEntryModel leafEntry1;
    @Mock
    private IbmPartnerCartEntryModel leafEntry2;
    @Mock
    private IbmPartnerCartEntryModel intermediateEntry;
    @Mock
    private IbmPartnerCartEntryModel rootEntry;

    @Mock
    private PartnerCommerceCartService commerceCartService;

    @Mock
    private DefaultPartnerCartUpdateReasonSelectionStrategy partnerCartUpdateStrategy;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        MockitoAnnotations.openMocks(this);
        calculateOrderAction = new CalculateOrderAction(3, 1000, mockCalculationService,commerceCartService,partnerCartUpdateStrategy);
        calculateOrderAction.setModelService(modelService);
    }

    @Test
    public void testExecuteAction_Success() throws Exception {
        IbmPartnerCartModel cartModel = new IbmPartnerCartModel();
        cartModel.setCode("67");
        final List<AbstractOrderEntryModel> entries = new ArrayList<>();
        final AbstractOrderEntryModel abstractOrderEntryModel = new IbmPartnerCartEntryModel();
        final List<AbstractOrderEntryModel> childEntries = new ArrayList<>();
        abstractOrderEntryModel.setChildEntries(childEntries);
        entries.add(0,abstractOrderEntryModel);
        cartModel.setEntries(entries);
        PriceLookUpProcessModel processModel = new PriceLookUpProcessModel();
        processModel.setCurrentTasks(processTask);
        processModel.setOrder(cartModel);
        PartnerAbstractSimpleDecisionAction.Transition result = calculateOrderAction.executeAction(
            processModel);

        assertEquals(PartnerAbstractSimpleDecisionAction.Transition.OK, result);
        verify(mockCalculationService).calculate(processModel.getOrder());
    }

    @Test
    public void testExecuteAction_Failure() throws Exception {
        IbmPartnerCartModel cartModel = new IbmPartnerCartModel();
        cartModel.setCode("67");
        final List<AbstractOrderEntryModel> entries = new ArrayList<>();
        final AbstractOrderEntryModel abstractOrderEntryModel = new IbmPartnerCartEntryModel();
        final List<AbstractOrderEntryModel> childEntries = new ArrayList<>();
        abstractOrderEntryModel.setChildEntries(childEntries);
        entries.add(0,abstractOrderEntryModel);
        cartModel.setEntries(entries);
        PriceLookUpProcessModel processModel = new PriceLookUpProcessModel();
        processModel.setCurrentTasks(processTask);
        processModel.setOrder(cartModel);

        doThrow(new RuntimeException("Test exception")).when(mockCalculationService)
            .calculate(processModel.getOrder());

        PartnerAbstractSimpleDecisionAction.Transition result = calculateOrderAction.executeAction(
            processModel);

        assertEquals(PartnerAbstractSimpleDecisionAction.Transition.NOK, result);
        verify(mockCalculationService).calculate(processModel.getOrder());
    }

    @Test
    public void testSetIsPriceOverridden(){
        IbmPartnerCartModel cartModel = new IbmPartnerCartModel();
        cartModel.setCode("67");
        final List<AbstractOrderEntryModel> entries = new ArrayList<>();
        final AbstractOrderEntryModel abstractOrderEntryModel = new IbmPartnerCartEntryModel();
        final List<AbstractOrderEntryModel> childEntries = new ArrayList<>();
        abstractOrderEntryModel.setChildEntries(childEntries);
        entries.add(0,abstractOrderEntryModel);
        cartModel.setEntries(entries);
        calculateOrderAction.setIsPriceOverridden(cartModel);
        verify(modelService).save(cartModel);
    }

    @Test
    public void testSetFullPriceReceivedFlag_quoteExists_entriesEmpty() {
        when(cartModel.getQuoteReference()).thenReturn(quoteModel);
        when(cartModel.getEntries()).thenReturn(Collections.emptyList());

        calculateOrderAction.setFullPriceReceivedFlag(cartModel);

        verify(cartModel).setFullPriceReceived(Boolean.TRUE);
        verify(modelService).save(cartModel);
    }

    @Test
    public void testSetFullPriceReceivedFlag_quoteNull_entriesEmpty() {
        when(cartModel.getQuoteReference()).thenReturn(null);
        when(cartModel.getEntries()).thenReturn(Collections.emptyList());
        calculateOrderAction.setFullPriceReceivedFlag(cartModel);
    }

    @Test
    public void testSetFullPriceReceivedFlag_quoteExists_entriesNotEmpty() {
        when(cartModel.getQuoteReference()).thenReturn(quoteModel);
        when(cartModel.getEntries()).thenReturn(List.of(ibmPartnerCartEntryModel));
        calculateOrderAction.setFullPriceReceivedFlag(cartModel);
    }
    @Test
    public void testSetIsPriceOverridden_RecursiveTraversalAndSaving() {
        when(leafEntry1.getChildEntries()).thenReturn(null);
        when(leafEntry2.getChildEntries()).thenReturn(null);
        when(intermediateEntry.getChildEntries()).thenReturn(List.of(leafEntry1, leafEntry2));
        when(rootEntry.getChildEntries()).thenReturn(List.of(intermediateEntry));
        calculateOrderAction.setIsPriceOverridden(rootEntry);
        verify(leafEntry1).setIsPriceOverridden(Boolean.FALSE);
        verify(leafEntry2).setIsPriceOverridden(Boolean.FALSE);
        verify(modelService).save(leafEntry1);
        verify(modelService).save(leafEntry2);
        verify(modelService, never()).save(rootEntry);
        verify(modelService, never()).save(intermediateEntry);
    }

}