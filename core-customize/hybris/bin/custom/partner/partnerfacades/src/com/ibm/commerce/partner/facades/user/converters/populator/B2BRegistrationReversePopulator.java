package com.ibm.commerce.partner.facades.user.converters.populator;


import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2bcommercefacades.data.B2BRegistrationData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * This Reverser Populator is for transferring the data from B2BRegistrationData to
 * B2BCustomerModel
 */
public class B2BRegistrationReversePopulator implements
    Populator<B2BRegistrationData, B2BCustomerModel> {


    /**
     * Overriding the populate method and setting the source data to target object
     *
     * @param source the source object
     * @param target the target to fill
     */
    @Override
    public void populate(final B2BRegistrationData source, final B2BCustomerModel target)
        throws ConversionException {
        target.setEmail(source.getEmail());
    }

}