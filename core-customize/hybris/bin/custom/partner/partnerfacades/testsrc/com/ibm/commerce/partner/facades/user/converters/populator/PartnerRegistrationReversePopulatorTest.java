package com.ibm.commerce.partner.facades.user.converters.populator;

import static org.easymock.EasyMock.eq;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.ibm.commerce.partner.company.data.IbmB2BUnitData;
import com.ibm.commerce.partner.core.b2b.company.service.PartnerB2BUnitService;
import com.ibm.commerce.partner.core.model.PartnerB2BCustomerModel;
import com.ibm.commerce.partner.core.services.LanguageService;
import com.ibm.commerce.partner.core.util.model.B2BUnitModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.UserGroupModelTestDataGenerator;
import com.ibm.commerce.partner.data.PartnerB2BRegistrationData;
import com.ibm.commerce.partner.facades.util.B2BRegistrationTestDataGenerator;
import com.ibm.commerce.partner.facades.util.CountryTestDataGenerator;
import com.ibm.commerce.partner.facades.util.IBMB2BUnitTestDataGenerator;
import com.ibm.commerce.partner.facades.util.LanguageTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.user.UserService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * test class for PartnerRegistrationReversePopulator
 */
@UnitTest
public class PartnerRegistrationReversePopulatorTest {

    private static final String DEFAULTB2BUNIT = "defaultB2BUnit";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String NAME = "John Doe";
    private static final String ISO_CODE_US = "US";
    private static final String ISO_CODE_EN = "en";
    private static final String UNIT_ID = "UNIT_ID";
    private String ROLE = "ecarepartner";
    private static final String SITE_ID = "siteId123";
    @InjectMocks
    private PartnerRegistrationReversePopulator partnerRegistrationReversePopulator;
    @Mock
    PartnerB2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;
    @Mock
    CommonI18NService commonI18NService;
    @Mock
    LanguageService languageService;
    @Mock
    CustomerNameStrategy customerNameStrategy;
    @Mock
    UserService userService;
    @Mock
    LanguageData languageData;
    @Mock
    LanguageData languageDataWithoutIsoCode;
    CountryData countryData;
    @Mock
    B2BUnitModel b2BUnitModel;
    IbmB2BUnitData ibmB2BUnitData;
    IbmB2BUnitData ibmB2BUnitDataWithoutSiteId;
    @Mock
    UserGroupModel userGroupModel;
    PartnerB2BRegistrationData partnerB2BRegistrationData;
    PartnerB2BRegistrationData partnerB2BRegistrationData1;
    PartnerB2BRegistrationData partnerB2BRegistrationData2;
    PartnerB2BRegistrationData partnerB2BRegistrationData3;
    PartnerB2BRegistrationData partnerB2BRegistrationData4;
    PartnerB2BRegistrationData partnerB2BRegistrationData5;
    PartnerB2BRegistrationData partnerB2BRegistrationData6;
    PartnerB2BCustomerModel partnerB2BCustomerModel;
    List<String> roles;

    /**
     * setup method for PartnerRegistrationReversePopulator class
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        partnerRegistrationReversePopulator = new PartnerRegistrationReversePopulator(
            DEFAULTB2BUNIT, b2bUnitService, commonI18NService, languageService,
            customerNameStrategy, userService);
        partnerB2BCustomerModel = new PartnerB2BCustomerModel();
        languageData = LanguageTestDataGenerator.prepareLanuage(ISO_CODE_EN);
        languageDataWithoutIsoCode = LanguageTestDataGenerator.prepareLanuage(null);
        countryData = CountryTestDataGenerator.prepareCountryData(ISO_CODE_US);
        ibmB2BUnitData = IBMB2BUnitTestDataGenerator.prepareIbmB2BUnitData(SITE_ID, null);
        ibmB2BUnitDataWithoutSiteId = IBMB2BUnitTestDataGenerator.prepareIbmB2BUnitData(null, null);
        b2BUnitModel = B2BUnitModelTestDataGenerator.prepareB2bUnitModel(SITE_ID);
        userGroupModel = UserGroupModelTestDataGenerator.createUserGroupModel(UNIT_ID);
        roles = new ArrayList<>();
        roles.add(ROLE);

        partnerB2BRegistrationData = B2BRegistrationTestDataGenerator.prepareCustomerData(
            FIRST_NAME, LAST_NAME, languageData, countryData, ibmB2BUnitData, roles);
        partnerB2BRegistrationData1 = B2BRegistrationTestDataGenerator.prepareCustomerData(
            FIRST_NAME, LAST_NAME, languageData, countryData, null, roles);
        partnerB2BRegistrationData2 = B2BRegistrationTestDataGenerator.prepareCustomerData(
            FIRST_NAME, LAST_NAME, null, countryData, ibmB2BUnitData, roles);
        partnerB2BRegistrationData3 = B2BRegistrationTestDataGenerator.prepareCustomerData(
            FIRST_NAME, LAST_NAME, languageDataWithoutIsoCode, countryData, ibmB2BUnitData, roles);
        partnerB2BRegistrationData4 = B2BRegistrationTestDataGenerator.prepareCustomerData(
            FIRST_NAME, LAST_NAME, languageData, countryData, null, roles);
        partnerB2BRegistrationData5 = B2BRegistrationTestDataGenerator.prepareCustomerData(
            FIRST_NAME, LAST_NAME, languageData, countryData, ibmB2BUnitDataWithoutSiteId, roles);
        partnerB2BRegistrationData6 = B2BRegistrationTestDataGenerator.prepareCustomerData(
                FIRST_NAME, LAST_NAME, languageData, countryData, ibmB2BUnitDataWithoutSiteId, null);

        Mockito.when(customerNameStrategy.getName(FIRST_NAME, LAST_NAME)).thenReturn(NAME);

    }

    /**
     * test method for populate method of PartnerRegistrationReversePopulator class
     */
    @Test
    public void testPopulate() {

        when(userService.getUserGroupForUID(anyString())).thenReturn(userGroupModel);
        when(b2bUnitService.getUnitForUid(SITE_ID, Boolean.TRUE)).thenReturn(b2BUnitModel);
        partnerRegistrationReversePopulator.populate(partnerB2BRegistrationData,
            partnerB2BCustomerModel);
        Assert.assertEquals(partnerB2BCustomerModel.getName(),
            FIRST_NAME.concat(" ").concat(LAST_NAME));
        Assert.assertTrue(partnerB2BCustomerModel.getActive());
    }

    @Test
    public void testPopulateWithRolesEmpty() {

        when(userService.getUserGroupForUID(anyString())).thenReturn(userGroupModel);
        when(b2bUnitService.getUnitForUid(SITE_ID, Boolean.TRUE)).thenReturn(b2BUnitModel);
        partnerRegistrationReversePopulator.populate(partnerB2BRegistrationData6,
                partnerB2BCustomerModel);
        Assert.assertEquals(partnerB2BCustomerModel.getName(),
                FIRST_NAME.concat(" ").concat(LAST_NAME));
        Assert.assertTrue(partnerB2BCustomerModel.getActive());
    }
    @Test
    public void testPopulateSiteIdNoIsocode() {
        when(b2bUnitService.getUnitForUid(eq(null), eq(Boolean.TRUE))).thenReturn(b2BUnitModel);
        when(userService.getUserGroupForUID(anyString())).thenReturn(userGroupModel);
        B2BUnitModel defaultB2BUnitModel = new B2BUnitModel();
        defaultB2BUnitModel.setUid(DEFAULTB2BUNIT);
        when(b2bUnitService.getUnitForUid(DEFAULTB2BUNIT, Boolean.TRUE)).thenReturn(
            defaultB2BUnitModel);
        partnerRegistrationReversePopulator.populate(partnerB2BRegistrationData5,
            partnerB2BCustomerModel);
        Assert.assertEquals(partnerB2BCustomerModel.getName(),
            FIRST_NAME.concat(" ").concat(LAST_NAME));
    }

    @Test
    public void testPopulateWhenPartnerB2BCustomerModelGroupNotNull() {
        Set<PrincipalGroupModel> value = new HashSet<>();
        value.add(userGroupModel);
        partnerB2BCustomerModel.setGroups(value);
        when(userService.getUserGroupForUID(anyString())).thenReturn(userGroupModel);
        when(b2bUnitService.getUnitForUid(SITE_ID, Boolean.TRUE)).thenReturn(b2BUnitModel);
        partnerRegistrationReversePopulator.populate(partnerB2BRegistrationData,
            partnerB2BCustomerModel);
        Assert.assertTrue(partnerB2BCustomerModel.getGroups().contains(userGroupModel));
    }

    @Test
    public void testPopulateWhenGroupInstanceOfB2BUnitModel() {
        Set<PrincipalGroupModel> value = new HashSet<>();
        value.add(b2BUnitModel);
        partnerB2BCustomerModel.setGroups(value);
        when(userService.getUserGroupForUID(anyString())).thenReturn(userGroupModel);
        when(b2bUnitService.getUnitForUid(SITE_ID, Boolean.TRUE)).thenReturn(b2BUnitModel);
        partnerRegistrationReversePopulator.populate(partnerB2BRegistrationData,
                partnerB2BCustomerModel);
        Assert.assertTrue(partnerB2BCustomerModel.getGroups().contains(b2BUnitModel));
    }
    @Test
    public void testPopulateWhenGroupInstanceOfB2BUnitModelWithNoRoles() {
        Set<PrincipalGroupModel> value = new HashSet<>();
        value.add(b2BUnitModel);
        partnerB2BCustomerModel.setGroups(value);
        when(userService.getUserGroupForUID(anyString())).thenReturn(userGroupModel);
        when(b2bUnitService.getUnitForUid(SITE_ID, Boolean.TRUE)).thenReturn(b2BUnitModel);
        partnerRegistrationReversePopulator.populate(partnerB2BRegistrationData6,
                partnerB2BCustomerModel);
        Assert.assertTrue(partnerB2BCustomerModel.getGroups().contains(b2BUnitModel));
    }
    @Test
    public void testPopulateWhenSiteIdNull() {
        when(b2bUnitService.getUnitForUid(eq(null), eq(Boolean.TRUE))).thenReturn(b2BUnitModel);
        when(userService.getUserGroupForUID(anyString())).thenReturn(userGroupModel);
        B2BUnitModel defaultB2BUnitModel = new B2BUnitModel();
        defaultB2BUnitModel.setUid(DEFAULTB2BUNIT);
        when(b2bUnitService.getUnitForUid(DEFAULTB2BUNIT, Boolean.TRUE)).thenReturn(
            defaultB2BUnitModel);
        partnerRegistrationReversePopulator.populate(partnerB2BRegistrationData4,
            partnerB2BCustomerModel);
        Assert.assertEquals(partnerB2BCustomerModel.getName(),
            FIRST_NAME.concat(" ").concat(LAST_NAME));
        Assert.assertTrue(partnerB2BCustomerModel.getGroups().contains(userGroupModel));
        Assert.assertTrue(partnerB2BCustomerModel.getActive());
    }


    @Test
    public void testPopulateWhenDefaultB2BUnit() {
        when(b2bUnitService.getUnitForUid(eq(null), eq(Boolean.TRUE))).thenReturn(b2BUnitModel);
        when(customerNameStrategy.getName(anyString(), anyString())).thenReturn(
            FIRST_NAME + LAST_NAME);
        when(userService.getUserGroupForUID(anyString())).thenReturn(userGroupModel);
        B2BUnitModel defaultB2BUnitModel = new B2BUnitModel();
        defaultB2BUnitModel.setUid(DEFAULTB2BUNIT);
        when(b2bUnitService.getUnitForUid(DEFAULTB2BUNIT, Boolean.TRUE)).thenReturn(
            defaultB2BUnitModel);
        partnerRegistrationReversePopulator.populate(partnerB2BRegistrationData1,
            partnerB2BCustomerModel);
        Assert.assertTrue(partnerB2BCustomerModel.getGroups().contains(defaultB2BUnitModel));
        Assert.assertTrue(partnerB2BCustomerModel.getActive());

    }

    /**
     * test method when No default Language present.
     */
    @Test
    public void testPopulateWhenNoDefaultLanguage() {
        when(b2bUnitService.getUnitForUid(eq(null), eq(Boolean.TRUE))).thenReturn(b2BUnitModel);
        when(customerNameStrategy.getName(anyString(), anyString())).thenReturn(
            FIRST_NAME + LAST_NAME);
        when(userService.getUserGroupForUID(anyString())).thenReturn(userGroupModel);
        when(b2bUnitService.getUnitForUid(SITE_ID, Boolean.TRUE)).thenReturn(b2BUnitModel);
        partnerRegistrationReversePopulator.populate(partnerB2BRegistrationData2,
            partnerB2BCustomerModel);
        Assert.assertNull(partnerB2BCustomerModel.getSessionLanguage());
    }

    @Test
    public void testPopulateWhenDefaultLanguageWithNoIsocode() {
        when(b2bUnitService.getUnitForUid(eq(null), eq(Boolean.TRUE))).thenReturn(b2BUnitModel);
        when(customerNameStrategy.getName(anyString(), anyString())).thenReturn(
            FIRST_NAME + LAST_NAME);
        when(userService.getUserGroupForUID(anyString())).thenReturn(userGroupModel);
        when(b2bUnitService.getUnitForUid(SITE_ID, Boolean.TRUE)).thenReturn(b2BUnitModel);
        partnerRegistrationReversePopulator.populate(partnerB2BRegistrationData3,
            partnerB2BCustomerModel);
        Assert.assertNull(partnerB2BCustomerModel.getSessionLanguage());
    }
    /**
     * test method when No default Country present.
     */
    @Test
    public void testPopulateWhenNoDefaultCountry() {

        partnerB2BRegistrationData.setDefaultCountry(null);
        when(userService.getUserGroupForUID(anyString())).thenReturn(userGroupModel);
        when(b2bUnitService.getUnitForUid(SITE_ID, Boolean.TRUE)).thenReturn(b2BUnitModel);
        partnerRegistrationReversePopulator.populate(partnerB2BRegistrationData,
            partnerB2BCustomerModel);
        Assert.assertEquals(partnerB2BCustomerModel.getName(),
            FIRST_NAME.concat(" ").concat(LAST_NAME));
        Assert.assertNull(partnerB2BCustomerModel.getDefaultCountry());
    }

    @Test
    public void testPopulateWhenBlankIsocodeCountry() {

        partnerB2BRegistrationData.getDefaultCountry().setIsocode("");
        when(userService.getUserGroupForUID(anyString())).thenReturn(userGroupModel);
        when(b2bUnitService.getUnitForUid(SITE_ID, Boolean.TRUE)).thenReturn(b2BUnitModel);
        partnerRegistrationReversePopulator.populate(partnerB2BRegistrationData,
            partnerB2BCustomerModel);
        Assert.assertEquals(partnerB2BCustomerModel.getName(),
            FIRST_NAME.concat(" ").concat(LAST_NAME));
        Assert.assertNull(partnerB2BCustomerModel.getDefaultCountry());
    }

    @Test
    public void testPopulateWithUidAndCustomerUid() {
        partnerB2BRegistrationData.setUid("user-uid-123");
        partnerB2BRegistrationData.setCustomerUid("customer-uid-456");

        when(b2bUnitService.getUnitForUid(SITE_ID, Boolean.TRUE)).thenReturn(b2BUnitModel);
        when(userService.getUserGroupForUID(anyString())).thenReturn(userGroupModel);

        partnerRegistrationReversePopulator.populate(partnerB2BRegistrationData,
            partnerB2BCustomerModel);

        Assert.assertEquals("user-uid-123", partnerB2BCustomerModel.getUid());
        Assert.assertEquals("customer-uid-456", partnerB2BCustomerModel.getCustomerID());
    }

}