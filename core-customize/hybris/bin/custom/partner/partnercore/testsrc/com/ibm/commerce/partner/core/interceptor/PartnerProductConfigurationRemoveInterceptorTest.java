package com.ibm.commerce.partner.core.interceptor;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationPersistenceService;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Unit test for {@link PartnerProductConfigurationRemoveInterceptor}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PartnerProductConfigurationRemoveInterceptorTest {

    @InjectMocks
    private PartnerProductConfigurationRemoveInterceptor interceptor;

    @Mock
    private ProductConfigurationPersistenceService productConfigurationPersistenceService;
    @Mock
    private InterceptorContext interceptorContext;
    @Mock
    private ProductConfigurationModel productConfigurationModel;
    @Mock
    private AbstractOrderEntryModel orderEntryModel;
    @Mock
    private UserModel userModel;
    @Mock
    private AbstractOrderModel orderModel;
    @Mock
    private ProductModel productModel;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testOnRemove_NoOrderEntries() throws InterceptorException {
        String configId = "config123";
        when(productConfigurationModel.getConfigurationId()).thenReturn(configId);
        when(productConfigurationPersistenceService.getAllOrderEntriesByConfigId(configId))
            .thenReturn(Collections.emptyList());
        when(productConfigurationModel.getUser()).thenReturn(userModel);
        interceptor.onRemove(productConfigurationModel, interceptorContext);
        verify(productConfigurationPersistenceService).getAllOrderEntriesByConfigId(configId);
        verifyNoInteractions(interceptorContext);
    }

    @Test(expected = InterceptorException.class)
    public void testOnRemove_MultipleOrderEntries() throws InterceptorException {
        String configId = "config123";
        List<AbstractOrderEntryModel> orderEntries = List.of(orderEntryModel, orderEntryModel);
        when(productConfigurationModel.getConfigurationId()).thenReturn(configId);
        when(productConfigurationPersistenceService.getAllOrderEntriesByConfigId(configId))
            .thenReturn(orderEntries);
        interceptor.onRemove(productConfigurationModel, interceptorContext);
    }

    @Test
    public void testOnRemove_SingleOrderEntry() throws InterceptorException {
        String configId = "config123";
        when(productConfigurationModel.getConfigurationId()).thenReturn(configId);
        when(productConfigurationPersistenceService.getAllOrderEntriesByConfigId(configId))
            .thenReturn(List.of(orderEntryModel));
        when(productConfigurationModel.getUser()).thenReturn(userModel);
        when(orderEntryModel.getOrder()).thenReturn(orderModel);
        when(productConfigurationModel.getProduct()).thenReturn(List.of(productModel));
        when(productModel.getCode()).thenReturn("productCode123");
        interceptor.onRemove(productConfigurationModel, interceptorContext);
        verify(productConfigurationPersistenceService).getAllOrderEntriesByConfigId(configId);
    }
}
