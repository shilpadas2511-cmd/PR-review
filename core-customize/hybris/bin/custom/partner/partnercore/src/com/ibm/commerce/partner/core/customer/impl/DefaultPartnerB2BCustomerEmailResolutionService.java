package com.ibm.commerce.partner.core.customer.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2bacceleratorservices.customer.impl.B2BCustomerEmailResolutionService;
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.lang.StringUtils;

/**
 * Extends implementation of {@link B2BCustomerEmailResolutionService}
 */
public class DefaultPartnerB2BCustomerEmailResolutionService implements
    CustomerEmailResolutionService {

    private final ConfigurationService configurationService;

    public DefaultPartnerB2BCustomerEmailResolutionService(
        final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @Override
    public String getEmailForCustomer(final CustomerModel customerModel) {
        validateParameterNotNullStandardMessage("customerModel", customerModel);

        if (customerModel instanceof B2BCustomerModel b2bCustomer) {
            final Boolean uidDisabled = getConfigurationService().getConfiguration()
                .getBoolean(PartnercoreConstants.FLAG_PARTNER_USER_SSO_UID_DISABLED, Boolean.TRUE);
            if (StringUtils.isNotBlank(b2bCustomer.getCustomerID()) && uidDisabled) {
                return b2bCustomer.getCustomerID();
            }
            return b2bCustomer.getEmail();
        }
        return customerModel.getUid();
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }
}
