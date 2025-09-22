package com.ibm.commerce.partner.core.util.model;

import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.user.AddressModel;

/**
 * TestDataGenerator for AddressModel
 */
public class AddressModelTestDataGenerator
{
	public static AddressModel createAddressModel(final String district, final String town, final String postalCode,
			final String email, final String firstName, final String lastName,
			final String streetName, final String streetNumber, final CountryModel country, final RegionModel region)
	{
		final AddressModel addressModel = new AddressModel();
		addressModel.setDistrict(district);
		addressModel.setTown(town);
		addressModel.setPostalcode(postalCode);
		addressModel.setEmail(email);
		addressModel.setFirstname(firstName);
		addressModel.setLastname(lastName);
		addressModel.setStreetname(streetName);
		addressModel.setStreetnumber(streetNumber);
		addressModel.setCountry(country);
		addressModel.setRegion(region);
		return addressModel;

	}

	public static AddressModel createAddressModel(final CountryModel country)
	{
		final AddressModel addressModel = new AddressModel();
		addressModel.setCountry(country);
		return addressModel;

	}
}
