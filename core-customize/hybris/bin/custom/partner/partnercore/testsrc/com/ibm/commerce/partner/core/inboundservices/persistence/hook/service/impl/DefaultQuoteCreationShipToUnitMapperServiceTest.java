package com.ibm.commerce.partner.core.inboundservices.persistence.hook.service.impl;

import static de.hybris.platform.testframework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ibm.commerce.partner.core.b2b.company.service.PartnerB2BUnitService;
import com.ibm.commerce.partner.core.country.services.PartnerCountryService;
import com.ibm.commerce.partner.core.enums.IbmPartnerB2BUnitType;
import com.ibm.commerce.partner.core.inboundservices.persistence.hook.service.impl.DefaultQuoteCreationShipToUnitMapperService;
import com.ibm.commerce.partner.core.model.CpqIbmPartnerAddressModel;
import com.ibm.commerce.partner.core.model.CpqIbmPartnerEndCustomerUnitModel;
import com.ibm.commerce.partner.core.model.CpqIbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.IbmPartnerEndCustomerB2BUnitModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultQuoteCreationShipToUnitMapperServiceTest {

    @InjectMocks
    DefaultQuoteCreationShipToUnitMapperService defaultQuoteCreationShipToUnitMapperService;
    @Mock
    private ModelService modelService;
    @Mock
    private PartnerCountryService countryService;
    @Mock
    private PartnerB2BUnitService partnerB2BUnitService;
    @Mock
    private CommonI18NService commonI18NService;

    @Mock
    private CpqIbmPartnerQuoteModel cpqIbmPartnerQuoteModel;
    @Mock
    private IbmPartnerQuoteModel ibmPartnerQuoteModel;
    @Mock
    private CpqIbmPartnerEndCustomerUnitModel cpqEndCustomerUnit;

    private final String EMAIL = "test@example.com";
    private final String TOWN = "TestTown";
    private final String POSTAL_CODE = "12345";
    private final String FIRST_NAME = "John";
    private final String LAST_NAME = "Doe";
    private final String STREET_NAME = "Test Street";
    private final String STREET_NUMBER = "123";
    private final String COUNTRY_CODE = "US";
    private final String REGION_CODE = "TX";
    private final String UID = "testUid";
    private final String NAME = "Test Customer";
    private final String CURRENCY = "USD";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        defaultQuoteCreationShipToUnitMapperService = new DefaultQuoteCreationShipToUnitMapperService(
            modelService, countryService, partnerB2BUnitService, commonI18NService);
        cpqIbmPartnerQuoteModel = new CpqIbmPartnerQuoteModel();
        ibmPartnerQuoteModel = new IbmPartnerQuoteModel();
        CpqIbmPartnerAddressModel addressModel = createCpqIbmAddress(TOWN,
            POSTAL_CODE, STREET_NAME, STREET_NUMBER, COUNTRY_CODE, REGION_CODE);
        cpqEndCustomerUnit = createCpqEndCustomerUnit(UID, CURRENCY, NAME, false,
            COUNTRY_CODE, addressModel);
        cpqIbmPartnerQuoteModel.setEndCustomer(cpqEndCustomerUnit);
    }

    @Test
    public void shouldMapToExistingShipToUnit() {
        IbmPartnerEndCustomerB2BUnitModel existingUnit = new IbmPartnerEndCustomerB2BUnitModel();
        existingUnit.setUid(UID);
        when(partnerB2BUnitService.getUnitForUid(UID, true)).thenReturn(existingUnit);
        defaultQuoteCreationShipToUnitMapperService.map(cpqIbmPartnerQuoteModel,
            ibmPartnerQuoteModel);
        assertEquals(existingUnit, ibmPartnerQuoteModel.getUnit());
        verify(partnerB2BUnitService, times(1)).getUnitForUid(UID, true);
    }

    @Test
    public void shouldMapToNewShipToUnit() {
        AddressModel addressModel = new AddressModel();
        IbmPartnerEndCustomerB2BUnitModel newUnit = new IbmPartnerEndCustomerB2BUnitModel();

        when(modelService.create(AddressModel.class)).thenReturn(addressModel);
        when(modelService.create(IbmPartnerEndCustomerB2BUnitModel.class)).thenReturn(newUnit);
        when(commonI18NService.getCountry(COUNTRY_CODE)).thenReturn(new CountryModel());
        when(commonI18NService.getRegion(any(), eq(REGION_CODE))).thenReturn(new RegionModel());

        defaultQuoteCreationShipToUnitMapperService.map(cpqIbmPartnerQuoteModel,
            ibmPartnerQuoteModel);

        assertNotNull(ibmPartnerQuoteModel.getUnit());
        assertEquals(UID, newUnit.getUid());
        assertEquals(NAME, newUnit.getName());
        assertNotNull(newUnit.getAddresses());

        AddressModel mappedAddress = newUnit.getAddresses().iterator().next();
        assertEquals(TOWN, mappedAddress.getTown());
        assertEquals(POSTAL_CODE, mappedAddress.getPobox());
        assertEquals(STREET_NAME, mappedAddress.getStreetname());
        assertEquals(STREET_NUMBER, mappedAddress.getStreetnumber());

        verify(modelService, times(1)).create(IbmPartnerEndCustomerB2BUnitModel.class);
        verify(modelService, times(1)).create(AddressModel.class);
    }

    public CpqIbmPartnerEndCustomerUnitModel createCpqEndCustomerUnit(String uid, String currency,
        String name, boolean goe, String country, CpqIbmPartnerAddressModel addressModel) {
        CpqIbmPartnerEndCustomerUnitModel cpqEndCustomerUnitModel = new CpqIbmPartnerEndCustomerUnitModel();
        cpqEndCustomerUnitModel.setUid(uid);
        cpqEndCustomerUnitModel.setCurrency(currency);
        cpqEndCustomerUnitModel.setName(name);
        cpqEndCustomerUnitModel.setGoe(goe);
        cpqEndCustomerUnitModel.setCountry(country);
        cpqEndCustomerUnitModel.setType(IbmPartnerB2BUnitType.ENDCUSTOMER);
        cpqEndCustomerUnitModel.setAddress(addressModel);
        return cpqEndCustomerUnitModel;
    }

    public CpqIbmPartnerAddressModel createCpqIbmAddress(String town, String postalCode,
        String streetName, String streetNumber, String country, String region) {
        CpqIbmPartnerAddressModel cpqIbmAddressModel = new CpqIbmPartnerAddressModel();
        cpqIbmAddressModel.setTown(town);
        cpqIbmAddressModel.setPostalCode(postalCode);
        cpqIbmAddressModel.setStreetName(streetName);
        cpqIbmAddressModel.setStreetNumber(streetNumber);
        cpqIbmAddressModel.setCountry(country);
        cpqIbmAddressModel.setRegion(region);
        return cpqIbmAddressModel;
    }

}