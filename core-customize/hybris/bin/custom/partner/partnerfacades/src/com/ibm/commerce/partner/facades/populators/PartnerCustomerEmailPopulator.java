package com.ibm.commerce.partner.facades.populators;

import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * Populates Email based on {@link CustomerEmailResolutionService} which implements a common logic to fetch the email id.
 */
public class PartnerCustomerEmailPopulator implements Populator<CustomerModel, CustomerData> {

    private final CustomerEmailResolutionService customerEmailResolutionService;

    public PartnerCustomerEmailPopulator(
        final CustomerEmailResolutionService customerEmailResolutionService) {
        this.customerEmailResolutionService = customerEmailResolutionService;
    }

    @Override
    public void populate(final CustomerModel source, final CustomerData target)
        throws ConversionException {

        target.setEmail(getCustomerEmailResolutionService().getEmailForCustomer(source));
    }

    public CustomerEmailResolutionService getCustomerEmailResolutionService() {
        return customerEmailResolutionService;
    }
}
