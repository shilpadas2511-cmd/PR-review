package com.ibm.commerce.partner.core.customer.impl;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPartnerB2BCustomerEmailResolutionServiceTest {

    private static final String TEST_UID = "test_uid@ibm.com";
    private static final String TEST_EMAIL = "test_email@ibm.com";
    private static final String TEST_CUSTOMER_ID = "CUST12345";

    private DefaultPartnerB2BCustomerEmailResolutionService emailResolutionService;

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private Configuration configuration;

    @Mock
    private CustomerModel customerModel;

    @Mock
    private B2BCustomerModel b2bCustomerModel;

    @Before
    public void setUp() {
        Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
        emailResolutionService = new DefaultPartnerB2BCustomerEmailResolutionService(configurationService);
    }

    @Test
    public void testGetEmailForCustomer_NonB2BCustomer_ReturnsUid() {
        Mockito.when(customerModel.getUid()).thenReturn(TEST_UID);

        String result = emailResolutionService.getEmailForCustomer(customerModel);

        Assert.assertEquals(TEST_UID, result);
    }

    @Test
    public void testGetEmailForCustomer_B2BCustomer_UIDDisabledTrue_ReturnsCustomerID() {
        Mockito.when(configuration.getBoolean(PartnercoreConstants.FLAG_PARTNER_USER_SSO_UID_DISABLED, Boolean.TRUE))
            .thenReturn(Boolean.TRUE);
        Mockito.when(b2bCustomerModel.getCustomerID()).thenReturn(TEST_CUSTOMER_ID);

        String result = emailResolutionService.getEmailForCustomer(b2bCustomerModel);

        Assert.assertEquals(TEST_CUSTOMER_ID, result);
    }

    @Test
    public void testGetEmailForCustomer_B2BCustomer_UIDDisabledTrue_CustomerIDBlank_ReturnsEmail() {
        Mockito.when(configuration.getBoolean(PartnercoreConstants.FLAG_PARTNER_USER_SSO_UID_DISABLED, Boolean.TRUE))
            .thenReturn(Boolean.TRUE);
        Mockito.when(b2bCustomerModel.getCustomerID()).thenReturn("");
        Mockito.when(b2bCustomerModel.getEmail()).thenReturn(TEST_EMAIL);

        String result = emailResolutionService.getEmailForCustomer(b2bCustomerModel);

        Assert.assertEquals(TEST_EMAIL, result);
    }

    @Test
    public void testGetEmailForCustomer_B2BCustomer_UIDDisabledFalse_ReturnsEmail() {
        Mockito.when(configuration.getBoolean(PartnercoreConstants.FLAG_PARTNER_USER_SSO_UID_DISABLED, Boolean.TRUE))
            .thenReturn(Boolean.FALSE);
        Mockito.when(b2bCustomerModel.getCustomerID()).thenReturn(TEST_CUSTOMER_ID);
        Mockito.when(b2bCustomerModel.getEmail()).thenReturn(TEST_EMAIL);

        String result = emailResolutionService.getEmailForCustomer(b2bCustomerModel);

        Assert.assertEquals(TEST_EMAIL, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetEmailForCustomer_NullCustomer_ThrowsException() {
        emailResolutionService.getEmailForCustomer(null);
    }

    @Test
    public void testGetConfigurationService_NotNull() {
        Assert.assertNotNull(emailResolutionService.getConfigurationService());
    }
}
