package com.ibm.commerce.partner.facades.populators;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;
import static java.util.stream.Collectors.toSet;

import com.ibm.commerce.partner.core.model.PartnerEmployeeModel;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import java.util.Collection;
import java.util.Objects;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;

public class PartnerEmployeePopulator implements Populator<EmployeeModel, CustomerData> {

    private final CustomerNameStrategy customerNameStrategy;

    public PartnerEmployeePopulator(CustomerNameStrategy customerNameStrategy) {
        this.customerNameStrategy = customerNameStrategy;
    }


    @Override
    public void populate(final EmployeeModel source, final CustomerData target) {
        if (source instanceof PartnerEmployeeModel) {

            validateParameterNotNull(source, "Parameter source cannot be null.");
            validateParameterNotNull(target, "Parameter target cannot be null.");
            target.setUid(source.getUid());
            String[] names = getCustomerNameStrategy().splitName(source.getName());
            target.setFirstName(names[0]);
            target.setLastName(names[1]);
            target.setEmail(source.getUid());
            if (CollectionUtils.isNotEmpty(source.getGroups())) {
                Collection<String> roles = source.getGroups().stream()
                    .map(PrincipalGroupModel::getUid)
                    .collect(toSet());
                target.setRoles(roles);
            } else {
                target.setRoles(CollectionUtils.emptyCollection());
            }
            if(Objects.nonNull(source.getBackOfficeLoginDisabled())) {
                target.setActive(BooleanUtils.isTrue(!(source.getBackOfficeLoginDisabled().booleanValue()
                    && source.isLoginDisabled())));
            }
        }
    }

    public CustomerNameStrategy getCustomerNameStrategy() {
        return customerNameStrategy;
    }
}
