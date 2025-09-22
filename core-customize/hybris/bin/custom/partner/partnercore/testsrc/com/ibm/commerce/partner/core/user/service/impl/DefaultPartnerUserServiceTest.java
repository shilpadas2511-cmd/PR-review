package com.ibm.commerce.partner.core.user.service.impl;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ibm.commerce.partner.core.enums.IbmPartnerB2BUnitType;
import com.ibm.commerce.partner.core.login.data.response.IbmIdPublicKeyResponseData;
import com.ibm.commerce.partner.core.login.data.response.IbmIdPublicKeyResponseListData;
import com.ibm.commerce.partner.core.model.IbmConsumedDestinationModel;
import com.ibm.commerce.partner.core.model.IbmPartnerB2BUnitModel;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.PartnerEmployeeModel;
import com.ibm.commerce.partner.core.services.IbmConsumedDestinationService;
import com.ibm.commerce.partner.core.services.IbmOutboundIntegrationService;
import com.ibm.commerce.partner.data.PartnerB2BRegistrationData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2bacceleratorfacades.exception.CustomerAlreadyExistsException;
import de.hybris.platform.b2bacceleratorfacades.exception.RegistrationNotEnabledException;
import de.hybris.platform.b2bacceleratorfacades.registration.B2BRegistrationFacade;
import de.hybris.platform.commerceservices.customer.CustomerService;
import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.regioncache.region.CacheRegion;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.common.core.constants.CommonCoreConstants;
import com.ibm.commerce.common.core.model.SellerAudienceMaskModel;
import com.ibm.commerce.partner.core.login.data.response.IbmIdUserDetailsResponseData;
import com.ibm.commerce.partner.core.login.services.IbmIdOutboundIntegration;
import com.ibm.commerce.partner.core.model.PartnerB2BCustomerModel;
import com.ibm.commerce.partner.core.strategy.PartnerSessionCountryStrategy;
import com.ibm.commerce.partner.core.user.dao.PartnerUserDao;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPartnerUserServiceTest {

    private static final String EXPECTED_ISO_CODE = "US";
    private static final String CUSTOMER_UID = "customer@gmail.com";

    @InjectMocks
    private final DefaultPartnerUserService partnerUserService = new DefaultPartnerUserService();

    @InjectMocks
    private final DefaultPartnerUserService userService = new DefaultPartnerUserService();

    @Mock
    private PartnerSessionCountryStrategy sessionCountryStrategy;
    @Mock
    private UserModel currentUser;
    @Mock
    private CountryModel country;
    @Mock
    private AbstractOrderModel orderModel;
    @Mock
    private SessionService sessionService;
    @Mock
    Set<PrincipalGroupModel> setGroup;
    @Mock
    ConfigurationService configurationService;
    @Mock
    BusinessProcessService businessProcessService;
    @Mock
    PartnerUserDao partnerUserDao;
    @Mock
    KeyGenerator processCodeGenerator;
    @Mock
    Configuration configuration;
    @Mock
    StoreFrontCustomerProcessModel processModel;
    @Mock
    ModelService modelService;
    @Mock
    IbmIdOutboundIntegration ibmIdOutboundIntegration;
    @Mock
    IbmIdUserDetailsResponseData userDetails;
    @Mock
    private IbmPartnerCartModel ibmPartnerCartModel;
    @Mock
    private IbmPartnerQuoteModel ibmPartnerQuoteModel;
    @Mock
    private IbmPartnerB2BUnitModel ibmPartnerB2BUnitModel;
    @Mock
    private UserModel user;
    @Mock
    private IbmConsumedDestinationService consumedDestinationService;
    @Mock
    private IbmOutboundIntegrationService outboundIntegrationService;
    @Mock
    private IbmConsumedDestinationModel consumedDestinationModel;
    @Mock
    private IbmIdPublicKeyResponseListData ibmIdPublicKeyResponseListData;
    @Mock
    private DecodedJWT decodedJWT;
    @Mock
    private Claim countryCodeClaim;
    @Mock
    private Claim givenNameClaim;
    @Mock
    private Claim familyNameClaim;
    @Mock
    private Claim partnerWorldClaim;
    @Mock
    private Claim emailClaim;
    @Mock
    private B2BCustomerModel b2BCustomerModel;
    @Mock
    private UserModel userModel;
    @Mock
    private CustomerService customerService;

    @Mock
    private CacheRegion customCacheRegion;

    @Mock
    B2BRegistrationFacade b2bRegistrationFacade;

    private final String JWT_TOKEN = "test.jwt.token";
    private final String BASE64MODULUS = "base-64ModULUS";
    private final String EXPONENT = "AQAB";

    private static final String TEST_USER_NAME = "testUser@example.com";
    private static final String TEST_EMAIL = "testUser@example.com";
    private static final String COUNTRY = "US";
    private static final String LASTNAME = "Doe";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        partnerUserService.setSessionService(sessionService);
        partnerUserService.setModelService(modelService);
        userService.setSessionService(sessionService);
        userService.setModelService(modelService);
        Mockito.when(sessionService.getAttribute(Mockito.anyString())).thenReturn(currentUser);
        Mockito.when(country.getIsocode()).thenReturn(EXPECTED_ISO_CODE);
        Mockito.when(userService.getCurrentUser()).thenReturn(user);
        Mockito.when(user.getGroups()).thenReturn(Set.of(ibmPartnerB2BUnitModel));
        Mockito.when(ibmPartnerB2BUnitModel.getType())
            .thenReturn(IbmPartnerB2BUnitType.DISTRIBUTOR);
        Mockito.when(customerService.getCustomerByCustomerId(
            Mockito.anyString())).thenReturn(b2BCustomerModel);
        Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
        Mockito.when(decodedJWT.getClaim(PartnercoreConstants.JWT_COUNTRY_CODE)).thenReturn(countryCodeClaim);
        Mockito.when(decodedJWT.getClaim(PartnercoreConstants.JWT_GIVEN_NAME)).thenReturn(givenNameClaim);
        Mockito.when(decodedJWT.getClaim(PartnercoreConstants.JWT_FAMILY_NAME)).thenReturn(familyNameClaim);

        Mockito.when(countryCodeClaim.asString()).thenReturn(COUNTRY);
        Mockito.when(givenNameClaim.asString()).thenReturn(TEST_USER_NAME);
        Mockito.when(familyNameClaim.asString()).thenReturn(LASTNAME);
    }

    @Test
    public void testGetCountry() {
        Mockito.when(sessionCountryStrategy.getSessionCountry(currentUser)).thenReturn(country);
        Mockito.when(sessionService.getAttribute(Mockito.anyString())).thenReturn(currentUser);
        final CountryModel result = partnerUserService.getCountry();
        assert result == country;
    }

    @Test
    public void testGetCountryWithUserAndOrder() {
        Mockito.when(sessionCountryStrategy.getSessionCountry(currentUser, orderModel))
            .thenReturn(country);
        final CountryModel result = partnerUserService.getCountry(currentUser, orderModel);
        assert result == country;
    }

    @Test
    public void testGetAndSetCurrentCountry() {
        Mockito.when(partnerUserService.getCountry()).thenReturn(country);
        partnerUserService.getAndSetCurrentCountry();
        Assert.assertEquals(EXPECTED_ISO_CODE, country.getIsocode());
    }

    @Test
    public void testGetAndSetCurrentCountryWhenCountryNull() {
        Mockito.when(partnerUserService.getCountry()).thenReturn(null);
        partnerUserService.getAndSetCurrentCountry();
        Mockito.verify(sessionService).setAttribute(CommonCoreConstants.SESSION_COUNTRY,
            CommonCoreConstants.EMPTY_SESSION_COUNTRY);

    }


    @Test
    public void testGetAndSetCurrentCountryWithUserAndOrder() {
        Mockito.when(partnerUserService.getCountry(currentUser, orderModel)).thenReturn(country);
        partnerUserService.getAndSetCurrentCountry(currentUser, orderModel);
        Assert.assertEquals(EXPECTED_ISO_CODE, country.getIsocode());
    }

    @Test
    public void testGetAndSetCurrentCountryWithUserAndOrderWithCountryNull() {
        Mockito.when(partnerUserService.getCountry(currentUser, orderModel)).thenReturn(null);
        partnerUserService.getAndSetCurrentCountry(currentUser, orderModel);
        Mockito.verify(sessionService).setAttribute(CommonCoreConstants.SESSION_COUNTRY,
            CommonCoreConstants.EMPTY_SESSION_COUNTRY);
    }

    @Test
    public void testGetSellerAudienceMaskNull() {
        final SellerAudienceMaskModel sellerAudienceMaskModel1 = partnerUserService.getSellerAudienceMaskForCurrentUser();
        Assert.assertNull(sellerAudienceMaskModel1);
    }

    @Test
    public void testCreateUpdateSiteIdBusinessProcess() {
        final PartnerB2BCustomerModel customerModel = new PartnerB2BCustomerModel();
        customerModel.setUid(CUSTOMER_UID);

        final String generatedProcessCode = "generatedProcessCode";
        Mockito.when(processCodeGenerator.generateFor(
                PartnercoreConstants.CUSTOMER_SITE_ID_PROCESS_CODE + PartnercoreConstants.HYPHEN
                    + customerModel.getUid()))
            .thenReturn(new StringBuilder(generatedProcessCode));
        Mockito.when(businessProcessService.createProcess(Mockito.anyString(), Mockito.anyString()))
            .thenReturn(
                processModel);
        partnerUserService.createUpdateSiteIdBusinessProcess(customerModel);
        Assert.assertEquals(CUSTOMER_UID, customerModel.getUid());
    }

    @Test
    public void testGetActivePartnerB2BCustomers() {
        final List<PartnerB2BCustomerModel> customers = new ArrayList<>();
        final PartnerB2BCustomerModel partnerB2BCustomerModel = new PartnerB2BCustomerModel();
        partnerB2BCustomerModel.setCustomerID(CUSTOMER_UID);
        customers.add(partnerB2BCustomerModel);
        Mockito.when(partnerUserService.getActivePartnerB2BCustomers()).thenReturn(customers);
        final List<PartnerB2BCustomerModel> results = partnerUserService.getActivePartnerB2BCustomers();
        Assert.assertEquals(CUSTOMER_UID, results.get(0).getCustomerID());
    }

    @Test
    public void testAllPartnerEmployee() {
        final List<PartnerEmployeeModel> employeeModels = new ArrayList<>();
        final PartnerEmployeeModel partnerEmployeeModel = new PartnerEmployeeModel();
        partnerEmployeeModel.setUid(CUSTOMER_UID);
        employeeModels.add(partnerEmployeeModel);
        Mockito.when(partnerUserService.getAllPartnerEmployees()).thenReturn(employeeModels);
        final List<PartnerEmployeeModel> results = partnerUserService.getAllPartnerEmployees();
        Assert.assertEquals(CUSTOMER_UID, results.get(0).getUid());
    }

    @Test
    public void isUserVADTestforCart() {

        userService.isVadView(ibmPartnerQuoteModel, user);
    }

    @Test
    public void isUserVADTestforquote() {
        Mockito.when(ibmPartnerQuoteModel.getBillToUnit()).thenReturn(ibmPartnerB2BUnitModel);
        userService.isVadView(ibmPartnerQuoteModel, user);
    }

    @Test
    public void verifyVADforCart() {
        Mockito.when(ibmPartnerCartModel.getBillToUnit()).thenReturn(ibmPartnerB2BUnitModel);
        userService.isVadView(ibmPartnerCartModel, user);
    }

    @Test
    public void verifyVADforQuote() {
        Mockito.when(ibmPartnerQuoteModel.getBillToUnit()).thenReturn(ibmPartnerB2BUnitModel);
        userService.isVadView(ibmPartnerQuoteModel, user);
    }

    @Test
    public void testGetJwksPublicKey() {
        List<IbmIdPublicKeyResponseData> keys = new ArrayList<>();
        keys.add(createIbmIdPublicKeyResponseData());
        Mockito.when(ibmIdPublicKeyResponseListData.getKeys()).thenReturn(keys);
        Mockito.when(consumedDestinationService.findActiveConsumedDestinationByIdAndTargetId(
                "ibmIdJwtLoginJwksPublicKeyConsumedDestination", "prmService"))
            .thenReturn(consumedDestinationModel);
        Mockito.when(consumedDestinationModel.getUrl())
            .thenReturn("https://mypw-uat.us-south.containers.appdomain.cloud/login/auth/sso/jwks");
        Mockito.when(outboundIntegrationService.sendRequest(
                HttpMethod.GET,
                consumedDestinationModel.getUrl(), null, null,
                IbmIdPublicKeyResponseListData.class, HttpStatus.OK))
            .thenReturn(ibmIdPublicKeyResponseListData);
        Assert.assertNotNull(userService.getJwksPublicKey());
    }

    @Test
    public void testValidateJwtTokenSignature() {
        Assert.assertNull(
            userService.validateJwtTokenSignature(JWT_TOKEN, BASE64MODULUS, EXPONENT));
    }

    @Test
    public void testValidateJwtTokenSignatureError() {
        Assert.assertNull(
            userService.validateJwtTokenSignature("testtoken", BASE64MODULUS, EXPONENT));
    }

    @Test
    public void testGetOrSetCachedPublicKey() {
        Assert.assertNull(userService.getOrSetCachedPublicKey(false));
        Assert.assertNull(userService.getOrSetCachedPublicKey(true));
    }

    @Test
    public void testIsJWTAuthenticatedUser() {
        Mockito.when(emailClaim.asString()).thenReturn(TEST_EMAIL);

        Mockito.when(decodedJWT.getClaim(PartnercoreConstants.JWT_EMAIL)).thenReturn(emailClaim);
        Assert.assertFalse(
            partnerUserService.isJWTAuthenticatedUser(decodedJWT, "testuser123@gmail.com"));
        Assert.assertFalse(userService.isJWTAuthenticatedUser(decodedJWT, "testuser123@gmail.com"));
    }

    @Test
    public void testIsSoftwareQuoting() {
        Map<String, String> role1 = new HashMap<>();
        role1.put(PartnercoreConstants.JWT_ROLEAPINAME, PartnercoreConstants.JWT_QUOTINGSOFTWARE);
        List<Map<String, String>> roles = Arrays.asList(role1);
        Map<String, Object> countryEnterprise1 = new HashMap<>();
        countryEnterprise1.put(PartnercoreConstants.JWT_ROLES, roles);
        List<Map<String, Object>> countryEnterprises = Arrays.asList(countryEnterprise1);

        Map<String, Object> enterprise1 = new HashMap<>();
        enterprise1.put(PartnercoreConstants.JWT_COUNTRYENTERPRISES, countryEnterprises);
        List<Map<String, Object>> wwEnterprises = Arrays.asList(enterprise1);
        Map<String, Object> partnerWorldMap = new HashMap<>();
        partnerWorldMap.put(PartnercoreConstants.JWT_WWENTERPRISES, wwEnterprises);
        Mockito.when(partnerWorldClaim.asMap()).thenReturn(partnerWorldMap);
        Mockito.when(decodedJWT.getClaim(PartnercoreConstants.JWT_PARTNERWORLD))
            .thenReturn(partnerWorldClaim);
        partnerUserService.isSoftwareQuoting(TEST_USER_NAME, decodedJWT);
        Assert.assertTrue(userService.isSoftwareQuoting("avnetinc@ibm.com", decodedJWT));
    }

    @Test
    public void testEnableB2BCustomer() {
        Mockito.when(b2BCustomerModel.getActive()).thenReturn(false);
        userService.enableOrDisableB2BCustomer(b2BCustomerModel, true);
        Mockito.verify(b2BCustomerModel).setActive(true);
        Mockito.verify(b2BCustomerModel).setLoginDisabled(false);
        Mockito.verify(modelService).save(b2BCustomerModel);
    }

    @Test
    public void testDisableB2BCustomer() {
        Mockito.when(b2BCustomerModel.getActive()).thenReturn(true);
        userService.enableOrDisableB2BCustomer(b2BCustomerModel, false);
        Mockito.verify(b2BCustomerModel).setActive(false);
        Mockito.verify(b2BCustomerModel).setLoginDisabled(true);
        Mockito.verify(modelService).save(b2BCustomerModel);
    }

    @Test(expected = UnknownIdentifierException.class)
    public void testCreateOrUpdateUserWithToken() {
        String userName = "noCustomerUser";
        Mockito.when(userService.getUserForUID(userName))
            .thenThrow(new UnknownIdentifierException("Not found"))
            .thenReturn(userModel); // fallback call at the end
        Mockito.when(customerService.getCustomerByCustomerId(userName)).thenReturn(null);
        userService.createOrUpdateUserWithToken(userName, decodedJWT, false);
        Mockito.verify(sessionService, Mockito.never()).setAttribute(Mockito.any(), Mockito.any());
        Mockito.verify(userService).enableOrDisableB2BCustomer(userModel, false);
    }


    @Test(expected = NullPointerException.class)
    public void testGetDecodedJwtToken() {
        partnerUserService.getDecodedJwtToken(JWT_TOKEN, false);
    }

    public IbmIdPublicKeyResponseData createIbmIdPublicKeyResponseData() {
        IbmIdPublicKeyResponseData ibmIdPublicKeyResponseData = new IbmIdPublicKeyResponseData();
        ibmIdPublicKeyResponseData.setKeyId("ibm-ice-mypw-login");
        ibmIdPublicKeyResponseData.setKeyType("RSA");
        ibmIdPublicKeyResponseData.setAlgorithm("RS256");
        ibmIdPublicKeyResponseData.setUse("sig");
        ibmIdPublicKeyResponseData.setExponent(EXPONENT);
        ibmIdPublicKeyResponseData.setModulus(BASE64MODULUS);
        return ibmIdPublicKeyResponseData;
    }

    @Test
    public void testRegisterNewB2BCustomer_success()
        throws RegistrationNotEnabledException, CustomerAlreadyExistsException {
        // Arrange
        Mockito.doNothing().when(b2bRegistrationFacade)
            .register(Mockito.any(PartnerB2BRegistrationData.class));

        // Act
        userService.registerNewB2BCustomer(TEST_USER_NAME, decodedJWT);

        // Assert
        ArgumentCaptor<PartnerB2BRegistrationData> captor =
            ArgumentCaptor.forClass(PartnerB2BRegistrationData.class);
        Mockito.verify(b2bRegistrationFacade, Mockito.times(1)).register(captor.capture());

        PartnerB2BRegistrationData captured = captor.getValue();
        Assert.assertEquals(TEST_USER_NAME, captured.getUid());
        Assert.assertEquals(TEST_USER_NAME, captured.getEmail());
        Assert.assertTrue(captured.isActive());
        Assert.assertEquals(COUNTRY, captured.getDefaultCountry().getIsocode());
        Assert.assertEquals(PartnercoreConstants.DEFAULT_LANG_ISOCODE,
            captured.getDefaultLanguage().getIsocode());
        Assert.assertEquals(TEST_USER_NAME, captured.getFirstName());
        Assert.assertEquals(LASTNAME, captured.getLastName());
        Assert.assertTrue(captured.getRoles().contains(PartnercoreConstants.B2BCUSTOMERGROUP));
        Assert.assertTrue(captured.getRoles().contains(PartnercoreConstants.RES));
    }

    @Test
    public void testRegisterNewB2BCustomer_failure_throwsInvalidClientException() {
        try {
            Mockito.doThrow(new InvalidClientException("DB error"))
                .when(b2bRegistrationFacade)
                .register(Mockito.any(PartnerB2BRegistrationData.class));
            userService.registerNewB2BCustomer(TEST_USER_NAME, decodedJWT);
        } catch (Exception e) {
            Assert.assertEquals("Unable to register user", e.getMessage());
        }
    }
}
