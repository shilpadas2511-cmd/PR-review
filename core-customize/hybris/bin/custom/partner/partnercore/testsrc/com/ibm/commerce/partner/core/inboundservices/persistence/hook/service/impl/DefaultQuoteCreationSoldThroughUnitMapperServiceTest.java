package com.ibm.commerce.partner.core.inboundservices.persistence.hook.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ibm.commerce.partner.core.b2b.company.service.PartnerB2BUnitService;
import com.ibm.commerce.partner.core.country.services.PartnerCountryService;
import com.ibm.commerce.partner.core.inboundservices.persistence.hook.service.impl.DefaultQuoteCreationSoldThroughUnitMapperService;
import com.ibm.commerce.partner.core.model.CpqIbmPartnerAddressModel;
import com.ibm.commerce.partner.core.model.CpqIbmPartnerCompanyUnitModel;
import com.ibm.commerce.partner.core.model.CpqIbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.CpqIbmPartnerUnitModel;
import com.ibm.commerce.partner.core.model.IbmPartnerB2BUnitModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.utils.PartnerAddressUtils;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CompanyModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultQuoteCreationSoldThroughUnitMapperServiceTest {

    @InjectMocks
    DefaultQuoteCreationSoldThroughUnitMapperService defaultQuoteCreationSoldThroughUnitMapperService;

    @Mock
    private ModelService modelService;
    @Mock
    private PartnerB2BUnitService partnerB2BUnitService;
    @Mock
    private CommonI18NService commonI18NService;
    @Mock
    private PartnerCountryService countryService;
    @Mock
    private IbmPartnerQuoteModel quoteModel;
    @Mock private IbmPartnerB2BUnitModel soldThroughUnit;
    @Mock private CpqIbmPartnerUnitModel cpqSoldThroughUnit;
    @Mock private IbmPartnerB2BUnitModel reportingOrg;
    @Mock
    private CountryModel countryModel;
    @Mock
    private PartnerAddressUtils partnerAddressUtils;

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
        defaultQuoteCreationSoldThroughUnitMapperService = new DefaultQuoteCreationSoldThroughUnitMapperService(
            modelService, partnerB2BUnitService, commonI18NService, countryService);
        quoteModel = new IbmPartnerQuoteModel();
    }

    @Test
    public void testMapExistingUnitNull() {

        CpqIbmPartnerUnitModel reportingOrganization = new CpqIbmPartnerUnitModel();
        reportingOrganization.setUid(REPORTING_ORGANIZATION_UID);

        CpqIbmPartnerAddressModel cpqAddress = createCpqIbmAddressModel(TOWN,
            POSTAL_CODE, FIRST_NAME, LAST_NAME, STREET_NAME, STREET_NUMBER, COUNTRY_CODE,
            REGION_CODE);

        CpqIbmPartnerUnitModel cpqSoldThroughUnit = createCpqIbmPartnerUnitModel(UNIT_UID,
            UNIT_NAME, UNIT_CURRENCY, UNIT_COUNTRY, reportingOrganization, cpqAddress);

        CpqIbmPartnerQuoteModel cpqIbmPartnerQuoteModel = new CpqIbmPartnerQuoteModel();
        cpqIbmPartnerQuoteModel.setReseller(cpqSoldThroughUnit);

        IbmPartnerB2BUnitModel existingUnit = null;
        CountryModel countryModel = new CountryModel();
        countryModel.setIsocode(UNIT_COUNTRY);
        CurrencyModel currencyModel = new CurrencyModel();
        currencyModel.setIsocode(UNIT_CURRENCY);
        AddressModel addressModel = new AddressModel();
        RegionModel regionModel = new RegionModel();
        regionModel.setIsocode(REGION_CODE);

        when(countryService.getByCodeOrSapCode(COUNTRY_CODE)).thenReturn(countryModel);
        when(modelService.create(AddressModel.class)).thenReturn(addressModel);
        when(commonI18NService.getRegion(countryModel, REGION_CODE)).thenReturn(regionModel);
        when(modelService.create(IbmPartnerB2BUnitModel.class)).thenReturn(
            new IbmPartnerB2BUnitModel());
        when(partnerB2BUnitService.getUnitForUid(UNIT_UID, true)).thenReturn(existingUnit);

        defaultQuoteCreationSoldThroughUnitMapperService.map(cpqIbmPartnerQuoteModel, quoteModel);

        assertNotNull(quoteModel.getSoldThroughUnit());
        IbmPartnerB2BUnitModel soldThroughUnit = (IbmPartnerB2BUnitModel) quoteModel.getSoldThroughUnit();
        assertEquals(UNIT_UID, soldThroughUnit.getUid());
        assertEquals(UNIT_NAME, soldThroughUnit.getName());
        assertEquals(countryModel, soldThroughUnit.getCountry());
    }

    @Test
    public void testMapWithExistingUnit() {
        CpqIbmPartnerUnitModel reportingOrganization = new CpqIbmPartnerUnitModel();
        reportingOrganization.setUid(REPORTING_ORGANIZATION_UID);

        CpqIbmPartnerAddressModel cpqAddress = createCpqIbmAddressModel(TOWN,
            POSTAL_CODE, FIRST_NAME, LAST_NAME, STREET_NAME, STREET_NUMBER, COUNTRY_CODE,
            REGION_CODE);

        CpqIbmPartnerUnitModel cpqSoldThroughUnit = createCpqIbmPartnerUnitModel(UNIT_UID,
            UNIT_NAME, UNIT_CURRENCY, UNIT_COUNTRY, reportingOrganization, cpqAddress);

        CpqIbmPartnerQuoteModel cpqIbmPartnerQuoteModel = new CpqIbmPartnerQuoteModel();
        cpqIbmPartnerQuoteModel.setReseller(cpqSoldThroughUnit);

        IbmPartnerB2BUnitModel existingUnit = new IbmPartnerB2BUnitModel();
        existingUnit.setUid(UNIT_UID);

        when(modelService.create(IbmPartnerB2BUnitModel.class)).thenReturn(
            new IbmPartnerB2BUnitModel());
        when(partnerB2BUnitService.getUnitForUid(UNIT_UID, true)).thenReturn(existingUnit);

        defaultQuoteCreationSoldThroughUnitMapperService.map(cpqIbmPartnerQuoteModel, quoteModel);

        assertNotNull(quoteModel.getSoldThroughUnit());
        IbmPartnerB2BUnitModel soldThroughUnit = (IbmPartnerB2BUnitModel) quoteModel.getSoldThroughUnit();
        assertEquals(UNIT_UID, soldThroughUnit.getUid());
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

    public CpqIbmPartnerUnitModel createCpqIbmPartnerUnitModel(String uid, String name,
        String currency, String country, CpqIbmPartnerUnitModel reportingOrganization,
        CpqIbmPartnerAddressModel cpqAddress) {
        CpqIbmPartnerUnitModel cpqSoldThroughUnit = new CpqIbmPartnerUnitModel();
        cpqSoldThroughUnit.setUid(uid);
        cpqSoldThroughUnit.setName(name);
        cpqSoldThroughUnit.setCurrency(currency);
        cpqSoldThroughUnit.setCountry(country);
        cpqSoldThroughUnit.setReportingOrganization(reportingOrganization);
        cpqSoldThroughUnit.setAddress(cpqAddress);
        return cpqSoldThroughUnit;
    }
    @Test
    public void testSetUnit_WithValidFields_ShouldMapCorrectly() {
        countryModel = new CountryModel();
        when(cpqSoldThroughUnit.getUid()).thenReturn("unit123");
        when(cpqSoldThroughUnit.getCurrency()).thenReturn("USD");
        when(cpqSoldThroughUnit.getName()).thenReturn("Test Unit");
        when(commonI18NService.getCurrentCurrency()).thenReturn(null);
        when(cpqSoldThroughUnit.getReportingOrganization()).thenReturn(cpqSoldThroughUnit);
        when(reportingOrg.getUid()).thenReturn("org123");
        when(partnerB2BUnitService.getUnitForUid("org123", true)).thenReturn(reportingOrg);
        when(cpqSoldThroughUnit.getCountry()).thenReturn("US");
        when(countryService.getByCodeOrSapCode("US")).thenReturn(countryModel);
        defaultQuoteCreationSoldThroughUnitMapperService.setUnit(soldThroughUnit, cpqSoldThroughUnit, quoteModel);
        verify(soldThroughUnit).setUid("unit123");
        verify(soldThroughUnit).setCurrency(null);
        verify(soldThroughUnit).setName("Test Unit");
        verify(soldThroughUnit).setCountry(countryModel);
    }
    @Test
    public void testSetParent_WhenUserGroupExists() {
        CpqIbmPartnerUnitModel cpqUnit = new CpqIbmPartnerUnitModel();
        CpqIbmPartnerUnitModel parentUnit = new CpqIbmPartnerUnitModel();
        parentUnit.setUid("parentUID");
        parentUnit.setName("Parent Group");
        cpqUnit.setParent(parentUnit);
        IbmPartnerB2BUnitModel soldThroughUnit = new IbmPartnerB2BUnitModel();
        CompanyModel existingGroup = new CompanyModel();
        existingGroup.setUid("parentUID");
        when(partnerB2BUnitService.getUnitForUid("parentUID")).thenReturn(existingGroup);
        defaultQuoteCreationSoldThroughUnitMapperService.setParent(soldThroughUnit, cpqUnit);
        assertTrue(soldThroughUnit.getGroups().contains(existingGroup));
        verify(modelService, never()).create(UserGroupModel.class);
        verify(modelService, never()).save(any());
    }

    @Test
    public void testSetParent_WhenUserGroupDoesNotExist() {
        CpqIbmPartnerUnitModel cpqUnit = new CpqIbmPartnerUnitModel();
        CpqIbmPartnerUnitModel parentUnit = new CpqIbmPartnerUnitModel();
        parentUnit.setUid("newParentUID");
        parentUnit.setName("New Parent Group");
        cpqUnit.setParent(parentUnit);

        IbmPartnerB2BUnitModel soldThroughUnit = new IbmPartnerB2BUnitModel();

        when(partnerB2BUnitService.getUnitForUid("newParentUID")).thenReturn(null);

        UserGroupModel newGroup = new UserGroupModel();
        when(modelService.create(UserGroupModel.class)).thenReturn(newGroup);
        defaultQuoteCreationSoldThroughUnitMapperService.setParent(soldThroughUnit, cpqUnit);
        verify(modelService).create(UserGroupModel.class);
        verify(modelService).save(newGroup);
        assertEquals("newParentUID", newGroup.getUid());
        assertEquals("New Parent Group", newGroup.getName());
        assertTrue(soldThroughUnit.getGroups().contains(newGroup));
    }
    @Test
    public void testConvertAddress_WhenAddressIsNew_ShouldAddAddress() {
        CpqIbmPartnerAddressModel cpqAddress = new CpqIbmPartnerAddressModel();
        cpqAddress.setTown("Berlin");
        cpqAddress.setPostalCode("10115");
        cpqAddress.setFirstName("Anna");
        cpqAddress.setLastName("Müller");
        cpqAddress.setStreetName("Hauptstr.");
        cpqAddress.setStreetNumber("42");
        cpqAddress.setCountry("DE");
        cpqAddress.setRegion("BE");

        CpqIbmPartnerUnitModel cpqUnit = new CpqIbmPartnerUnitModel();
        cpqUnit.setAddress(cpqAddress);

        IbmPartnerB2BUnitModel targetUnit = new IbmPartnerB2BUnitModel();
        targetUnit.setAddresses(new ArrayList<>());

        CountryModel countryModel = new CountryModel();
        RegionModel regionModel = new RegionModel();

        AddressModel newAddress = new AddressModel();
        when(modelService.create(AddressModel.class)).thenReturn(newAddress);
        when(commonI18NService.getCountry("DE")).thenReturn(countryModel);
        when(commonI18NService.getRegion(countryModel, "BE")).thenReturn(regionModel);
        when(partnerAddressUtils.areAddressesEqual(any(), any())).thenReturn(false);
        defaultQuoteCreationSoldThroughUnitMapperService.convertAddress(cpqUnit, targetUnit);
        verify(modelService).create(AddressModel.class);
        assertEquals(1, targetUnit.getAddresses().size());
        assertTrue(targetUnit.getAddresses().contains(newAddress));
        verify(modelService, never()).save(newAddress);
    }

    @Test
    public void testConvertAddress_WhenAddressExists_ShouldNotAddDuplicate() {
        CpqIbmPartnerAddressModel cpqAddress = new CpqIbmPartnerAddressModel();
        cpqAddress.setTown("ExistingTown");
        cpqAddress.setCountry("US");

        CpqIbmPartnerUnitModel cpqUnit = new CpqIbmPartnerUnitModel();
        cpqUnit.setAddress(cpqAddress);

        AddressModel existingAddress = new AddressModel();

        IbmPartnerB2BUnitModel targetUnit = new IbmPartnerB2BUnitModel();
        targetUnit.setAddresses(Collections.singletonList(existingAddress));

        AddressModel newAddress = new AddressModel();
        when(modelService.create(AddressModel.class)).thenReturn(newAddress);
        when(commonI18NService.getCountry("US")).thenReturn(new CountryModel());
        when(partnerAddressUtils.areAddressesEqual(existingAddress, newAddress)).thenReturn(true);
        defaultQuoteCreationSoldThroughUnitMapperService.convertAddress(cpqUnit, targetUnit);
        verify(modelService).create(AddressModel.class);
        assertEquals(1, targetUnit.getAddresses().size());
    }

    @Test
    public void testConvertAddress_WhenCpqAddressIsNull_ShouldDoNothing() {
        CpqIbmPartnerUnitModel cpqUnit = new CpqIbmPartnerUnitModel(); // no address set
        IbmPartnerB2BUnitModel targetUnit = new IbmPartnerB2BUnitModel();
        defaultQuoteCreationSoldThroughUnitMapperService.convertAddress(cpqUnit, targetUnit);
        assertNull(targetUnit.getAddresses());
    }
    @Test
    public void testLogError_ShouldLogFormattedMessage() {

        CpqIbmPartnerQuoteModel cpqQuote = new CpqIbmPartnerQuoteModel();
        cpqQuote.setCode("Q123");
        cpqQuote.setCpqQuoteNumber("CPQ-456");
        cpqQuote.setCpqQuoteExternalId("EXT-789");

        Exception ex = new IllegalArgumentException("Test error");

        defaultQuoteCreationSoldThroughUnitMapperService.logError("Country", ex, cpqQuote);
    }
    @Test
    public void testConvertAddress_ShouldSetRegion_WhenRegionIsPresentAndValid() {
        String regionCode = "CA";
        String countryCode = "US";

        CpqIbmPartnerAddressModel cpqAddress = new CpqIbmPartnerAddressModel();
        cpqAddress.setCountry(countryCode);
        cpqAddress.setRegion(regionCode);

        CpqIbmPartnerUnitModel cpqUnit = new CpqIbmPartnerUnitModel();
        cpqUnit.setAddress(cpqAddress);

        IbmPartnerB2BUnitModel targetUnit = new IbmPartnerB2BUnitModel();
        targetUnit.setAddresses(new ArrayList<>());

        CountryModel country = new CountryModel();
        RegionModel region = new RegionModel();

        AddressModel addressModel = new AddressModel();

        when(modelService.create(AddressModel.class)).thenReturn(addressModel);
        when(commonI18NService.getCountry(countryCode)).thenReturn(country);
        when(commonI18NService.getRegion(country, regionCode)).thenReturn(region);
        mockStatic(PartnerAddressUtils.class);
        when(partnerAddressUtils.areAddressesEqual(any(), any())).thenReturn(false);
        defaultQuoteCreationSoldThroughUnitMapperService.convertAddress(cpqUnit, targetUnit);
        assertEquals(region, addressModel.getRegion());
        assertTrue(targetUnit.getAddresses().contains(addressModel));
    }
    @Test
    public void testConvertAddress_ShouldNotSetRegion_WhenRegionIsInvalid() {
        CpqIbmPartnerAddressModel cpqAddress = new CpqIbmPartnerAddressModel();
        cpqAddress.setCountry("US");
        cpqAddress.setRegion("INVALID_REGION");

        CpqIbmPartnerUnitModel cpqUnit = new CpqIbmPartnerUnitModel();
        cpqUnit.setAddress(cpqAddress);

        IbmPartnerB2BUnitModel targetUnit = new IbmPartnerB2BUnitModel();
        targetUnit.setAddresses(new ArrayList<>());

        CountryModel country = new CountryModel();
        AddressModel addressModel = new AddressModel();

        when(modelService.create(AddressModel.class)).thenReturn(addressModel);
        when(commonI18NService.getCountry("US")).thenReturn(country);
        when(commonI18NService.getRegion(country, "INVALID_REGION")).thenReturn(null);
        when(partnerAddressUtils.areAddressesEqual(any(), any())).thenReturn(false);
        defaultQuoteCreationSoldThroughUnitMapperService.convertAddress(cpqUnit, targetUnit);
        assertNull(addressModel.getRegion());
        assertTrue(targetUnit.getAddresses().contains(addressModel));
    }
}