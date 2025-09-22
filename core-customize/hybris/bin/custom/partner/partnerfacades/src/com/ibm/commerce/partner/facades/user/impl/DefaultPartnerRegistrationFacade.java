package com.ibm.commerce.partner.facades.user.impl;


import com.ibm.commerce.partner.core.model.PartnerB2BCustomerModel;
import com.ibm.commerce.partner.core.user.service.PartnerUserService;
import de.hybris.platform.b2bacceleratorfacades.exception.CustomerAlreadyExistsException;
import de.hybris.platform.b2bacceleratorfacades.registration.impl.DefaultB2BRegistrationFacade;
import de.hybris.platform.b2bcommercefacades.data.B2BRegistrationData;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Overriding the OOTB implemented method to register the PartnerB2BCustomer
 */
public class DefaultPartnerRegistrationFacade extends DefaultB2BRegistrationFacade {

    private static final Logger LOG = LoggerFactory.getLogger(
        DefaultPartnerRegistrationFacade.class);

    private final Converter<B2BRegistrationData, PartnerB2BCustomerModel> partnerRegistrationReverseConverter;

    private final PartnerUserService userService;

    private final ModelService modelService;

    private final ConfigurationService configurationService;

    public DefaultPartnerRegistrationFacade(final ModelService modelService,
        final PartnerUserService userService,
        final Converter<B2BRegistrationData, PartnerB2BCustomerModel> partnerRegistrationReverseConverter,
        ConfigurationService configurationService) {
        this.modelService = modelService;
        this.userService = userService;
        this.partnerRegistrationReverseConverter = partnerRegistrationReverseConverter;
        this.configurationService = configurationService;
    }

    /**
     * Overriding the OOTB method to register the PartnerB2BCustomer with default partnerB2BUnit
     *
     * @param registrationData Registration data
     * @throws CustomerAlreadyExistsException if the customer already exists, throws
     *                                        CustomerAlreadyExistsException
     */
    @Override
    public void register(final B2BRegistrationData registrationData)
        throws CustomerAlreadyExistsException {
        final boolean userExists = getUserService().isUserExisting(registrationData.getEmail());
        if (userExists) {
            LOG.debug(
                String.format("user with uid %s already exists!", registrationData.getEmail()));
            throw new CustomerAlreadyExistsException(
                String.format("User with email %s  already exists.", registrationData.getEmail()));
        }
        PartnerB2BCustomerModel customerModel = getPartnerRegistrationReverseConverter().convert(
            registrationData);
        getModelService().save(customerModel);
    }

    public Converter<B2BRegistrationData, PartnerB2BCustomerModel> getPartnerRegistrationReverseConverter() {
        return partnerRegistrationReverseConverter;
    }

    public PartnerUserService getUserService() {
        return userService;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

}
