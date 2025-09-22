package com.ibm.commerce.partner.core.provisionform.convertors.populators;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.quote.provision.form.data.request.ProvisionFormBasicDetailsRequestData;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ProvisionFormBasicDetailsRequestPopulatorTest {

    private ProvisionFormBasicDetailsRequestPopulator populator;

    @Mock
    private IbmPartnerCartModel cartModel;

    @Mock
    private ProvisionFormBasicDetailsRequestData requestData;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        populator = new ProvisionFormBasicDetailsRequestPopulator();
    }

    @Test
    public void testPopulate_whenSourceIsIbmPartnerCartModel() throws ConversionException {
        when(cartModel.getPriceUid()).thenReturn("priceUid123");
        AbstractOrderModel source = cartModel;
        populator.populate(source, requestData);
        verify(requestData).setQuoteReferenceNumber("priceUid123");
    }

    @Test
    public void testPopulate_whenSourceIsNotIbmPartnerCartModel() throws ConversionException {
        AbstractOrderModel source = mock(AbstractOrderModel.class);
        populator.populate(source, requestData);
        verify(requestData, never()).setQuoteReferenceNumber(anyString());
    }
}
