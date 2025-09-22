package com.ibm.commerce.partner.facades.populators;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import com.ibm.commerce.partner.core.model.PartnerEmployeeModel;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.servicelayer.user.UserService;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;

/**
 * Populator for transferring the data from CustomerData to EmployeeModel
 */
public class PartnerEmployeeReversePopulator implements Populator<CustomerData, EmployeeModel> {

    private final UserService userService;

    private final CustomerNameStrategy customerNameStrategy;

    /**
     * @param userService
     * @param customerNameStrategy
     */
    public PartnerEmployeeReversePopulator(final UserService userService,
        final CustomerNameStrategy customerNameStrategy) {

        this.customerNameStrategy = customerNameStrategy;
        this.userService = userService;

    }

    /**
     * Overriding the populate method and setting the source data to target object
     *
     * @param source the source object
     * @param target the target to fill
     */

    @Override
    public void populate(final CustomerData source, final EmployeeModel target) {


            validateParameterNotNull(source, "Parameter source cannot be null.");
            validateParameterNotNull(target, "Parameter target cannot be null.");

            target.setUid(source.getUid());
            target.setName(
                getCustomerNameStrategy().getName(source.getFirstName(), source.getLastName()));

            if (CollectionUtils.isNotEmpty(source.getRoles())) {
                Set<PrincipalGroupModel> userGroups = new HashSet<>();
                source.getRoles().stream().forEach(
                    role -> {
                        UserGroupModel userGroupModel = getUserService().getUserGroupForUID(role);
                        if (userGroupModel != null) {
                            userGroups.add(userGroupModel);
                        }
                    });
                target.setGroups(userGroups);
            } else {
                target.setGroups(Collections.emptySet());
            }
            target.setLoginDisabled(!source.isActive());
            target.setBackOfficeLoginDisabled(!source.isActive());

        }

    public CustomerNameStrategy getCustomerNameStrategy() {
        return customerNameStrategy;
    }

    public UserService getUserService() {
        return userService;
    }
}


