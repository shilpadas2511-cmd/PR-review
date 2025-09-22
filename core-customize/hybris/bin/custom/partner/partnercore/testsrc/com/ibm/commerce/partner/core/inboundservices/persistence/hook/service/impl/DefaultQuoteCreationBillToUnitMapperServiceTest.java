package com.ibm.commerce.partner.core.inboundservices.persistence.hook.service.impl;


import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ibm.commerce.partner.core.b2b.company.service.PartnerB2BUnitService;
import com.ibm.commerce.partner.core.country.services.PartnerCountryService;
import com.ibm.commerce.partner.core.inboundservices.persistence.hook.service.impl.DefaultQuoteCreationBillToUnitMapperService;
import com.ibm.commerce.partner.core.model.CpqIbmPartnerAddressModel;
import com.ibm.commerce.partner.core.model.CpqIbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.CpqIbmPartnerUnitModel;
import com.ibm.commerce.partner.core.model.IbmPartnerB2BUnitModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.catalog.model.CompanyModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultQuoteCreationBillToUnitMapperServiceTest {

    @InjectMocks
    DefaultQuoteCreationBillToUnitMapperService defaultQuoteCreationBillToUnitMapperService;

    @Mock
    private PartnerB2BUnitService partnerB2BUnitService;
    @Mock
    private CommonI18NService commonI18NService;
    @Mock
    private ModelService modelService;
    @Mock
    private PartnerCountryService countryService;

    @Mock
    private IbmPartnerQuoteModel quoteModel;

    @Mock
    private CpqIbmPartnerUnitModel cpqBillToUnit;
    @Mock
    private IbmPartnerB2BUnitModel billToUnit;
    @Mock
    private CpqIbmPartnerUnitModel cpqParent;
    @Mock
    private UserGroupModel userGroupModel;
    @Mock
    private UserGroupModel defaultUserGroupModel;
    @Mock
    private CompanyModel companyModel;
    @Mock
    private AddressModel newAddressModel;

    @Mock
    private AddressModel existingAddress;
    @Mock
    CountryModel countryModel;

    @Mock
    private RegionModel regionModel;
    private final String EMAIL = "test@example.com";
    private final String TOWN = "TestTown";
    private final String POSTAL_CODE = "12345";
    private final String FIRST_NAME = "John";
    private final String LAST_NAME = "Doe";
    private final String STREET_NAME = "Main Street";
    private final String STREET_NUMBER = "123";
    private final String COUNTRY_CODE = "US";
    private final String REGION_CODE = "CA";
    private final String UNIT_UID = "unit123";
    private final String UNIT_NAME = "Test Unit";
    private final String UNIT_CURRENCY = "USD";
    private final String UNIT_COUNTRY = "US";
    private final String UNIT_REPORTING_ORGANISATION = "parentUnit123";
    private final String REPORTING_ORGANIZATION_UID = "reportingOrgUid";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        defaultQuoteCreationBillToUnitMapperService = new DefaultQuoteCreationBillToUnitMapperService(
            modelService,
            partnerB2BUnitService, commonI18NService, countryService);
    }

    @Test
    public void testMapWithExistingBillToUnit() {
        CpqIbmPartnerQuoteModel cpqQuote = mock(CpqIbmPartnerQuoteModel.class);
        CpqIbmPartnerUnitModel cpqBillToUnit = mock(CpqIbmPartnerUnitModel.class);

        IbmPartnerB2BUnitModel existingBillToUnit = mock(IbmPartnerB2BUnitModel.class);
        IbmPartnerQuoteModel quoteModel = mock(IbmPartnerQuoteModel.class);

        when(cpqQuote.getDistributor()).thenReturn(cpqBillToUnit);
        when(cpqBillToUnit.getUid()).thenReturn("UID123");
        when(partnerB2BUnitService.getUnitForUid("UID123", true)).thenReturn(existingBillToUnit);

        defaultQuoteCreationBillToUnitMapperService.map(cpqQuote, quoteModel);

        verify(quoteModel).setBillToUnit(existingBillToUnit);
    }
    @Test
    public void testMapWithExistingBillToUnitwithName() {
        CpqIbmPartnerQuoteModel cpqQuote = mock(CpqIbmPartnerQuoteModel.class);
        CpqIbmPartnerUnitModel cpqBillToUnit = mock(CpqIbmPartnerUnitModel.class);

        IbmPartnerB2BUnitModel existingBillToUnit = mock(IbmPartnerB2BUnitModel.class);
        IbmPartnerQuoteModel quoteModel = mock(IbmPartnerQuoteModel.class);

        when(cpqQuote.getDistributor()).thenReturn(cpqBillToUnit);
        when(cpqBillToUnit.getUid()).thenReturn("Test");
        when(partnerB2BUnitService.getUnitForUid("Test", true)).thenReturn(existingBillToUnit);

        defaultQuoteCreationBillToUnitMapperService.map(cpqQuote, quoteModel);

        verify(quoteModel).setBillToUnit(existingBillToUnit);
    }


    @Test
    public void testMapExistingUnitNull() {

        CpqIbmPartnerUnitModel reportingOrganization = new CpqIbmPartnerUnitModel();
        reportingOrganization.setUid(REPORTING_ORGANIZATION_UID);

        B2BUnitModel b2bReportingOrg = new B2BUnitModel();
        b2bReportingOrg.setUid(REPORTING_ORGANIZATION_UID);

        CpqIbmPartnerAddressModel cpqAddress = createCpqIbmAddressModel(TOWN,
            POSTAL_CODE, FIRST_NAME, LAST_NAME, STREET_NAME, STREET_NUMBER, COUNTRY_CODE,
            REGION_CODE);

        CpqIbmPartnerUnitModel cpqBillToUnit = createCpqIbmPartnerUnitModel(UNIT_UID,
            UNIT_NAME, UNIT_CURRENCY, UNIT_COUNTRY, reportingOrganization, cpqAddress);
        cpqBillToUnit.setName("Test");

        CpqIbmPartnerQuoteModel cpqIbmPartnerQuoteModel = new CpqIbmPartnerQuoteModel();
        cpqIbmPartnerQuoteModel.setDistributor(cpqBillToUnit);

        IbmPartnerB2BUnitModel existingUnit = null;
        CountryModel countryModel = new CountryModel();
        countryModel.setIsocode(UNIT_COUNTRY);
        CurrencyModel currencyModel = new CurrencyModel();
        currencyModel.setIsocode(UNIT_CURRENCY);
        AddressModel addressModel = new AddressModel();
        RegionModel regionModel = new RegionModel();
        regionModel.setIsocode(REGION_CODE);

        when(partnerB2BUnitService.getUnitForUid(REPORTING_ORGANIZATION_UID, true)).thenReturn(
            b2bReportingOrg);
        when(modelService.create(AddressModel.class)).thenReturn(addressModel);
        when(commonI18NService.getCountry(COUNTRY_CODE)).thenReturn(new CountryModel());
        when(commonI18NService.getRegion(any(), eq(REGION_CODE))).thenReturn(new RegionModel());
        when(modelService.create(IbmPartnerB2BUnitModel.class)).thenReturn(
            new IbmPartnerB2BUnitModel());
        when(partnerB2BUnitService.getUnitForUid(UNIT_UID, true)).thenReturn(existingUnit);

        defaultQuoteCreationBillToUnitMapperService.map(cpqIbmPartnerQuoteModel, quoteModel);
        assertNotNull(quoteModel);
    }

    public CpqIbmPartnerUnitModel createCpqIbmPartnerUnitModel(String uid, String name,
        String currency, String country, CpqIbmPartnerUnitModel reportingOrganization,
        CpqIbmPartnerAddressModel cpqAddress) {
        CpqIbmPartnerUnitModel billToUnit = new CpqIbmPartnerUnitModel();
        billToUnit.setUid(uid);
        billToUnit.setName(name);
        billToUnit.setCurrency(currency);
        billToUnit.setCountry(country);
        billToUnit.setReportingOrganization(reportingOrganization);
        billToUnit.setAddress(cpqAddress);
        return billToUnit;
    }

    public CpqIbmPartnerAddressModel createCpqIbmAddressModel(String town, String postalCode,
        String firstName,
        String lastName, String streetName, String streetNumber, String countryCode,
        String regionCode) {
        CpqIbmPartnerAddressModel cpqAddress = new CpqIbmPartnerAddressModel();
        cpqAddress.setTown(town);
        cpqAddress.setPostalCode(postalCode);
        cpqAddress.setFirstName(firstName);
        cpqAddress.setLastName(lastName);
        cpqAddress.setStreetName(streetName);
        cpqAddress.setStreetNumber(streetNumber);
        cpqAddress.setCountry(countryCode);
        cpqAddress.setRegion(regionCode);
        return cpqAddress;
    }
    @Test
    public void testSetParent_withExistingUserGrouppartOne() {
        IbmPartnerB2BUnitModel billToUnit = new IbmPartnerB2BUnitModel();

        CpqIbmPartnerUnitModel parentUnit = new CpqIbmPartnerUnitModel();
        parentUnit.setUid("parentUid");
        CpqIbmPartnerUnitModel cpqBillToUnit = new CpqIbmPartnerUnitModel();
        cpqBillToUnit.setParent(parentUnit);
        CompanyModel existingUserGroup = new CompanyModel();
        existingUserGroup.setUid("parentUid");
        when(partnerB2BUnitService.getUnitForUid("parentUid")).thenReturn(existingUserGroup);
        defaultQuoteCreationBillToUnitMapperService.setParent(billToUnit, cpqBillToUnit);
        assertNotNull(billToUnit.getGroups());
        assertTrue(billToUnit.getGroups().contains(existingUserGroup));
    }
    @Test
    public void testLogAndThrowError_whenUidMissing_shouldThrowException() {
        CpqIbmPartnerQuoteModel quoteModel = new CpqIbmPartnerQuoteModel();
        quoteModel.setCode("Q123");
        quoteModel.setCpqQuoteNumber("CPQ456");
        quoteModel.setCpqQuoteExternalId("EXT789");

        CpqIbmPartnerUnitModel distributor = new CpqIbmPartnerUnitModel();
        distributor.setCountry("US");
        quoteModel.setDistributor(distributor);
        IbmPartnerQuoteModel targetQuote = new IbmPartnerQuoteModel();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            defaultQuoteCreationBillToUnitMapperService.map(quoteModel, targetQuote);
        });

        assertTrue(exception.getMessage().contains("Mandatory attribute [UID]"));
    }
    @Test
    public void testSetParent_withExistingUserGroup() {
        when(cpqBillToUnit.getParent()).thenReturn(cpqParent);
        when(cpqParent.getUid()).thenReturn("parentUid");
        companyModel = new CompanyModel();
        when(partnerB2BUnitService.getUnitForUid("parentUid")).thenReturn(companyModel);
        defaultQuoteCreationBillToUnitMapperService.setParent(billToUnit, cpqBillToUnit);
    }

    @Test
    public void testSetParent_withNewUserGroup() {
        when(cpqBillToUnit.getParent()).thenReturn(cpqParent);
        when(cpqParent.getUid()).thenReturn("parentUid");
        when(partnerB2BUnitService.getUnitForUid("parentUid")).thenReturn(null);

        when(modelService.create(UserGroupModel.class)).thenReturn(defaultUserGroupModel);
        defaultQuoteCreationBillToUnitMapperService.setParent(billToUnit, cpqBillToUnit);
        verify(modelService, times(1)).create(UserGroupModel.class);
    }

}
