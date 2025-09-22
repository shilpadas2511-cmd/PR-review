package com.ibm.commerce.partner.facades.user.converters.populator;

import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.springframework.util.Assert;

/**
 * Populates Address Street Details on Address Model
 */
public class AddressBasicDetailsReversePopulator implements Populator<AddressData, AddressModel> {

    @Override
    public void populate(AddressData addressData, AddressModel addressModel)
        throws ConversionException {
        Assert.notNull(addressData, "Parameter addressData cannot be null.");
        Assert.notNull(addressModel, "Parameter addressModel cannot be null.");
        addressModel.setFirstname(addressData.getFirstName());
        addressModel.setLastname(addressData.getLastName());
        addressModel.setEmail(addressData.getEmail());
        addressModel.setCompany(addressData.getCompanyName());
        addressModel.setTown(addressData.getTown());
        addressModel.setPostalcode(addressData.getPostalCode());
        addressModel.setPhone1(addressData.getPhone());
        addressModel.setCellphone(addressData.getCellphone());
        addressModel.setDistrict(addressData.getDistrict());
    }
}
