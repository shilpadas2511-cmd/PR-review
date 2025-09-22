package com.ibm.commerce.partner.core.order.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.processengine.enums.ProcessState;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ibm.commerce.partner.core.daos.PartnerProcessDao;
import com.ibm.commerce.partner.core.model.PriceLookUpProcessModel;


@UnitTest
public class DefaultPartnerProcessServiceTest {

    @InjectMocks
    private DefaultPartnerProcessService defaultPartnerProcessService;

    @Mock
    private PartnerProcessDao partnerProcessDao;
    @Mock
    ModelService modelService;
    @Mock
    CartModel cart;
    @Mock
    private PriceLookUpProcessModel processModel;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        defaultPartnerProcessService =
                new DefaultPartnerProcessService(partnerProcessDao, modelService);
    }

    @Test
    public void testGetBusinessProcessList() {
        final String orderCode = "12345";
        final BusinessProcessModel process1 = new BusinessProcessModel();
        final BusinessProcessModel process2 = new BusinessProcessModel();
        final List<BusinessProcessModel> expectedProcesses = Arrays.asList(process1, process2);

        when(partnerProcessDao.getProcessListByCode(orderCode)).thenReturn(expectedProcesses);

        final List<BusinessProcessModel> actualProcesses =
                defaultPartnerProcessService.getBusinessProcessList(orderCode);

        assertEquals(expectedProcesses, actualProcesses);
    }

    @Test
    public void testGetPartnerProcessDao() {
        final PartnerProcessDao actualDao = defaultPartnerProcessService.getPartnerProcessDao();
        assertEquals(partnerProcessDao, actualDao);
    }

    @Test
    public void testRemoveCartFromOldProcess() {
        final PriceLookUpProcessModel process = new PriceLookUpProcessModel();
        process.setOrder(cart);
        when(cart.getCode()).thenReturn("000000");
        final List<BusinessProcessModel> processList = Arrays.asList(process);
        when(partnerProcessDao.getProcessListByCode("price-lookup-process-000000%"))
                .thenReturn(processList);
        assertNotNull(((PriceLookUpProcessModel) processList.get(0)).getOrder());
        defaultPartnerProcessService.removeCartFromOldProcess(cart);
        verify(modelService, times(1)).saveAll(processList);
        assertNull(((PriceLookUpProcessModel) processList.get(0)).getOrder());
    }

    @Test
    public void testRemoveCartFromOldProcessCartNull() {
        assertThrows(IllegalArgumentException.class,
                () -> defaultPartnerProcessService.removeCartFromOldProcess(null));
    }

    @Test
    public void testRemoveCartFromOldProcessCartCodeNull() {
        when(cart.getCode()).thenReturn(null);
        assertThrows(IllegalArgumentException.class,
                () -> defaultPartnerProcessService.removeCartFromOldProcess(cart));
    }

    @Test
    public void testRemoveCartFromOldProcessNotFound() {
        when(cart.getCode()).thenReturn("000000");
        when(partnerProcessDao.getProcessListByCode("price-lookup-process-000000%"))
                .thenReturn(Collections.emptyList());
        defaultPartnerProcessService.removeCartFromOldProcess(cart);
        verify(modelService, times(0)).saveAll();
    }

    @Test
    public void testCheckQuoteCartProcessCompleted_NullCart_ThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            defaultPartnerProcessService.checkQuoteCartProcessCompleted(null);
        });
        assertEquals("Cart and cart code must not be null", exception.getMessage());
    }

    @Test
    public void testCheckQuoteCartProcessCompleted_NullCartCode_ThrowsException() {
        when(cart.getCode()).thenReturn(null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            defaultPartnerProcessService.checkQuoteCartProcessCompleted(cart);
        });
        assertEquals("Cart and cart code must not be null", exception.getMessage());
    }

    @Test
    public void testCheckQuoteCartProcessCompleted_NoProcessesFound_ReturnsTrue() {
        when(cart.getCode()).thenReturn("CART123");
        when(defaultPartnerProcessService.getBusinessProcessList(anyString())).thenReturn(
            Collections.emptyList());
        boolean result = defaultPartnerProcessService.checkQuoteCartProcessCompleted(cart);
        assertTrue(result);
    }

    @Test
    public void testCheckQuoteCartProcessCompleted_ProcessesNotRunning_ReturnsTrue() {
        when(cart.getCode()).thenReturn("CART123");
        when(defaultPartnerProcessService.getBusinessProcessList(anyString())).thenReturn(
            Arrays.asList(processModel));
        when(processModel.getState()).thenReturn(ProcessState.SUCCEEDED);
        boolean result = defaultPartnerProcessService.checkQuoteCartProcessCompleted(cart);
        assertTrue(result);
    }

    @Test
    public void testCheckQuoteCartProcessCompleted_AtLeastOneProcessRunning_ReturnsFalse() {
        when(cart.getCode()).thenReturn("CART123");
        when(defaultPartnerProcessService.getBusinessProcessList(anyString())).thenReturn(
            Arrays.asList(processModel));
        when(processModel.getState()).thenReturn(ProcessState.RUNNING);
        boolean result = defaultPartnerProcessService.checkQuoteCartProcessCompleted(cart);
        assertFalse(result);
    }
}
