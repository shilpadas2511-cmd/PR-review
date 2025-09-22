package com.ibm.commerce.partner.facades.util;

import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.RegionData;

public class AddressTestDataGenerator {

    public static AddressData createAddressData(final String line1, final String line2, final String town, final CountryData countryData) {
        final AddressData addressData = new AddressData();
        addressData.setLine1(line1);
        addressData.setLine2(line2);
        addressData.setTown(town);
        addressData.setCountry(countryData);
        return addressData;
    }

    public static AddressData createAddress(final String firstName, final String lastName, final String company, final String customerEmailId, final String town, final String postalCode, final String phone, final String cellPhone, final String district) {
        final AddressData addressData = new AddressData();
       addressData.setFirstName(firstName);
        addressData.setTown(town);
        addressData.setDistrict(district);
        addressData.setTown(town);
        addressData.setPostalCode(postalCode);
        addressData.setEmail(customerEmailId);
        addressData.setLastName(lastName);
        addressData.setCompanyName(company);
        addressData.setPhone(phone);
        addressData.setCellphone(cellPhone);
        return addressData;
    }

    public static AddressData createCountry(final CountryData country) {
        final AddressData addressData = new AddressData();
        addressData.setCountry(country);
        return addressData;
    }

	 public static AddressData createAddressData(final RegionData regionData)
	 {
		 final AddressData addressData = new AddressData();
		 addressData.setRegion(regionData);
		 return addressData;
	 }
    public static AddressData createStreetData(final String streetName, final String streetNumber)
    {
        final AddressData addressData = new AddressData();
        addressData.setLine1(streetName);
        addressData.setLine2(streetNumber);
        return addressData;
    }
}
