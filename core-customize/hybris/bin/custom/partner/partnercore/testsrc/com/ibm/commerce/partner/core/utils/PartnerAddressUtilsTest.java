package com.ibm.commerce.partner.core.utils;

import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.user.AddressModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PartnerAddressUtilsTest {

    private AddressModel address1;
    private AddressModel address2;
    private CountryModel countryModel;
    private RegionModel regionModel;

    @BeforeEach
    void setUp() {
        address1 = mock(AddressModel.class);
        address2 = mock(AddressModel.class);
        countryModel = mock(CountryModel.class);
        regionModel = mock(RegionModel.class);

        when(address1.getCountry()).thenReturn(countryModel);
        when(address2.getCountry()).thenReturn(countryModel);

        when(address1.getRegion()).thenReturn(regionModel);
        when(address2.getRegion()).thenReturn(regionModel);

        when(address1.getDistrict()).thenReturn("Santa Clara");
        when(address2.getDistrict()).thenReturn("Santa Clara");

        when(address1.getTown()).thenReturn("San Jose");
        when(address2.getTown()).thenReturn("San Jose");

        when(address1.getPostalcode()).thenReturn("95123");
        when(address2.getPostalcode()).thenReturn("95123");

        when(address1.getEmail()).thenReturn("test@example.com");
        when(address2.getEmail()).thenReturn("test@example.com");

        when(address1.getStreetname()).thenReturn("Main Street");
        when(address2.getLine1()).thenReturn("Main Street");

        when(address1.getStreetnumber()).thenReturn("123");
        when(address2.getLine2()).thenReturn("123");

        when(address1.getFirstname()).thenReturn("John");
        when(address2.getFirstname()).thenReturn("John");

        when(address1.getLastname()).thenReturn("Doe");
        when(address2.getLastname()).thenReturn("Doe");
    }

    @Test
    void testAddressesAreEqual() {
        assertTrue(PartnerAddressUtils.areAddressesEqual(address1, address2));
    }

    @Test
    void testAddressesAreNotEqual_DifferentTown() {
        when(address2.getTown()).thenReturn("Los Angeles");
        assertFalse(PartnerAddressUtils.areAddressesEqual(address1, address2));
    }

    @Test
    void testAddressesAreNotEqual_NullAddress() {
        assertFalse(PartnerAddressUtils.areAddressesEqual(null, address2));
        assertFalse(PartnerAddressUtils.areAddressesEqual(address1, null));
        assertFalse(PartnerAddressUtils.areAddressesEqual(null, null));
    }

    @Test
    void testAddressesAreNotEqual_DifferentStreetName() {
        when(address2.getLine1()).thenReturn("Different Street");
        assertFalse(PartnerAddressUtils.areAddressesEqual(address1, address2));
    }
}