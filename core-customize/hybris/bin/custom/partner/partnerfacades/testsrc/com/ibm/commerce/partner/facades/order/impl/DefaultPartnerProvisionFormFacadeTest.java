package com.ibm.commerce.partner.facades.order.impl;

import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.order.strategies.PartnerFormDetailsStrategy;
import de.hybris.platform.order.CartService;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import static org.mockito.ArgumentMatchers.any;
import org.junit.Test;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

public class DefaultPartnerProvisionFormFacadeTest {

    @Mock
    private CartService cartService;

    @Mock
    private PartnerFormDetailsStrategy partnerFormDetailsStrategy;

    @InjectMocks
    private DefaultPartnerProvisionFormFacade partnerProvisionFormFacade;

    @Mock
    private IbmPartnerCartModel cartModel;

    @Mock
    IbmPartnerQuoteModel quoteModel;

    @Before
    public void setUp()  {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUpdateProvisionFormEditors_whenQuoteReferenceIsNotNull() {
        Mockito.when(cartService.getSessionCart()).thenReturn(cartModel);
        Mockito.when(cartModel.getQuoteReference()).thenReturn(quoteModel);
        partnerProvisionFormFacade.updateProvisionFormEditors();
        verify(partnerFormDetailsStrategy, times(1)).fetchFormDetails(cartModel);
    }

    @Test
    public void testUpdateProvisionFormEditors_whenQuoteReferenceIsNull() {
        Mockito.when(cartService.getSessionCart()).thenReturn(cartModel);
        Mockito.when(cartModel.getQuoteReference()).thenReturn(null);
        partnerProvisionFormFacade.updateProvisionFormEditors();
        verify(partnerFormDetailsStrategy, times(0)).fetchFormDetails(any());
    }
}