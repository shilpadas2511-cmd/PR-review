package com.ibm.commerce.partner.facades.user.impl;

import com.ibm.commerce.partner.core.model.PartnerB2BCustomerModel;
import com.ibm.commerce.partner.core.user.service.PartnerUserService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2bacceleratorfacades.exception.CustomerAlreadyExistsException;
import de.hybris.platform.b2bcommercefacades.data.B2BRegistrationData;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
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
public class DefaultPartnerRegistrationFacadeTest {

    @Mock
    private ModelService modelService;

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private Configuration configuration;

    @Mock
    private Converter<B2BRegistrationData, PartnerB2BCustomerModel> partnerRegistrationReverseConverter;

    @Mock
    private B2BRegistrationData registrationData;

    @Mock
    private PartnerB2BCustomerModel customerModel;

    @Mock
    private PartnerUserService userService;

    private DefaultPartnerRegistrationFacade registrationFacade;

    @Before
    public void setUp() {
        registrationFacade = new DefaultPartnerRegistrationFacade(
            modelService,
            userService,
            partnerRegistrationReverseConverter,
            configurationService
        );
    }

    @Test
    public void testRegister_UserNotExists_JwtDisabled() throws Exception {
        Mockito.when(registrationData.getEmail()).thenReturn("test@example.com");
        Mockito.when(userService.isUserExisting("test@example.com")).thenReturn(false);
        Mockito.when(partnerRegistrationReverseConverter.convert(registrationData)).thenReturn(customerModel);

        registrationFacade.register(registrationData);

        Mockito.verify(userService).isUserExisting("test@example.com");
        Mockito.verify(partnerRegistrationReverseConverter).convert(registrationData);
        Mockito.verify(modelService).save(customerModel);
    }

    @Test(expected = CustomerAlreadyExistsException.class)
    public void testRegister_UserAlreadyExists_ShouldThrowException() throws Exception {
        Mockito.when(registrationData.getEmail()).thenReturn("existing@example.com");
        Mockito.when(userService.isUserExisting("existing@example.com")).thenReturn(true);

        registrationFacade.register(registrationData);
    }

    @Test
    public void testGetPartnerRegistrationReverseConverter() {
        Assert.assertEquals(
            partnerRegistrationReverseConverter,
            registrationFacade.getPartnerRegistrationReverseConverter()
        );
    }

    @Test
    public void testGetUserService() {
        Assert.assertEquals(userService, registrationFacade.getUserService());
    }

    @Test
    public void testGetModelService() {
        Assert.assertEquals(modelService, registrationFacade.getModelService());
    }

    @Test
    public void testGetConfigurationService() {
        Assert.assertEquals(configurationService, registrationFacade.getConfigurationService());
    }
}
