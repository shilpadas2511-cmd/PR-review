package com.ibm.commerce.partner.core.strategy;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.commerceservices.customer.CustomerService;
import de.hybris.platform.commerceservices.strategies.UserPropertyMatchingStrategy;
import de.hybris.platform.core.model.user.UserModel;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Fetch Users based on preffered-username. This is only created to supported exceptional scenarios
 * where api call is made with preffered_username
 */
public class DefaultPartnerUserPropertyMatchingStrategy implements UserPropertyMatchingStrategy {

    private static final Pattern SAP_CUSTOMER_ID_PATTERN = Pattern.compile("^\\d{1,10}");

    private final CustomerService customerService;

    public DefaultPartnerUserPropertyMatchingStrategy(final CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    public <T extends UserModel> Optional<T> getUserByProperty(final String propertyValue,
        final Class<T> clazz) {
        validateParameterNotNull(propertyValue,
            "The property value used to identify a customer must not be null");
        validateParameterNotNull(clazz, "The class of returned user model must not be null");
        return Optional.ofNullable(
            (T) getCustomerService().getCustomerByCustomerId(propertyValue.toLowerCase()));
    }

    public CustomerService getCustomerService() {
        return customerService;
    }
}