package com.ibm.commerce.partner.core.provisionform.convertors.populators;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.quote.provision.form.data.request.ProvisionFormRequestData;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ProvisionFormRequestPopulatorTest {

    private ProvisionFormRequestPopulator populator;

    @Mock
    private IbmPartnerCartModel cartModel;

    @Mock
    private ProvisionFormRequestData requestData;

    @Mock
    private B2BCustomerModel b2bCustomerModel;

    @Mock
    private CustomerEmailResolutionService customerEmailResolutionService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        populator = new ProvisionFormRequestPopulator(customerEmailResolutionService);
    }

    @Test
    public void testPopulate_withIbmPartnerCartModel_andValidB2BCustomer()
        throws ConversionException {
        String userEmail = "testuser@domain.com";
        when(cartModel.getUser()).thenReturn(b2bCustomerModel);
        when(b2bCustomerModel.getUid()).thenReturn(userEmail);
        populator.populate(cartModel, requestData);
        verify(requestData).setAllowedEditorEmails(List.of(userEmail));
    }

    @Test
    public void testPopulate_withNullUserEmail() throws ConversionException {
        when(cartModel.getUser()).thenReturn(null);
        populator.populate(cartModel, requestData);
        verify(requestData).setAllowedEditorEmails(List.of());
    }


    @Test
    public void testGetUserEmail_withB2BCustomerModel() {
        String userEmail = "testuser@domain.com";
        when(cartModel.getUser()).thenReturn(b2bCustomerModel);
        when(b2bCustomerModel.getUid()).thenReturn(userEmail);
        String email = populator.getUserEmail(cartModel);
        assertEquals(userEmail, email);
    }


    @Test
    public void testPopulate_withEmptyCartEntries() throws ConversionException {
        when(cartModel.getEntries()).thenReturn(List.of());
        populator.populate(cartModel, requestData);
        verify(requestData).setAllowedEditorEmails(List.of());
    }
}
