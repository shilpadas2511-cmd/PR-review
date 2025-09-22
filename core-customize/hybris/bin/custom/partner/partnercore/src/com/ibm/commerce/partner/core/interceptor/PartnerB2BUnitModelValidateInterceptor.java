package com.ibm.commerce.partner.core.interceptor;

import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2b.interceptor.B2BUnitModelValidateInterceptor;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

/**
 * Validator for B2BUnit Model. This validator overrides OOTB validator to remove Logic of B2BUnit
 * creation only by adminGroups.
 */
public class PartnerB2BUnitModelValidateInterceptor extends
    B2BUnitModelValidateInterceptor implements ValidateInterceptor {

    @Override
    public void onValidate(final Object model, final InterceptorContext ctx)
        throws InterceptorException {
        if (model instanceof final B2BUnitModel unit) {
            if (CollectionUtils.isNotEmpty(unit.getApprovers())) {
                final UserGroupModel b2bApproverGroup = getUserService().getUserGroupForUID(
                    B2BConstants.B2BAPPROVERGROUP);
                final Set<B2BCustomerModel> newApprovers = unit.getApprovers().stream().filter(
                        approver -> getUserService().isMemberOfGroup(approver, b2bApproverGroup))
                    .collect(Collectors.toSet());
                unit.setApprovers(newApprovers);
            }

            //ensures that all of a deactivated unit's subunit's are also deactivated (except in case of new unit).
            if (BooleanUtils.isFalse(unit.getActive()) && !ctx.getModelService().isNew(model)) {
                final Set<B2BUnitModel> childUnits = getB2bUnitService().getB2BUnits(unit);
                List<B2BUnitModel> activeChilds = childUnits.stream()
                    .filter(child -> BooleanUtils.isTrue(child.getActive())).toList();
                activeChilds.forEach(child -> child.setActive(Boolean.FALSE));
                getModelService().saveAll(activeChilds);
            }
        }
    }
}
