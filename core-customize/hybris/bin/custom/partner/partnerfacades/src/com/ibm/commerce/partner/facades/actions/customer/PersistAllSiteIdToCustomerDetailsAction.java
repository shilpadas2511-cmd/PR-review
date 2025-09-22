package com.ibm.commerce.partner.facades.actions.customer;

import com.ibm.commerce.partner.core.actions.PartnerAbstractSimpleDecisionAction;
import com.ibm.commerce.partner.core.b2b.company.service.PartnerB2BUnitService;
import com.ibm.commerce.partner.core.cronjob.PartnerB2BCustomerJob;
import com.ibm.commerce.partner.core.model.PartnerResellerSiteBusinessProcessModel;
import com.ibm.commerce.partner.core.model.PartnerStoreFrontCustomerProcessModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.task.RetryLaterException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;

/**
 * Persiste Reseller Details
 */
public class PersistAllSiteIdToCustomerDetailsAction extends
    PartnerAbstractSimpleDecisionAction<PartnerStoreFrontCustomerProcessModel> {

    private static final Logger LOG = Logger.getLogger(PersistAllSiteIdToCustomerDetailsAction.class);
    private final String defaultB2BUnitCode;
    private final PartnerB2BUnitService<B2BUnitModel, B2BCustomerModel> b2BUnitService;

    protected PersistAllSiteIdToCustomerDetailsAction(final Integer maxRetryAllowed,
        final Integer retryDelay, final String defaultB2BUnitCode,
        final PartnerB2BUnitService<B2BUnitModel, B2BCustomerModel> b2BUnitService) {
        super(maxRetryAllowed, retryDelay);
        this.defaultB2BUnitCode = defaultB2BUnitCode;
        this.b2BUnitService = b2BUnitService;
    }

    @Override

    public Transition executeAction(final PartnerStoreFrontCustomerProcessModel processModel)
        throws RetryLaterException, Exception {

        if (processModel.getCustomer() instanceof B2BCustomerModel b2BCustomerModel
            && CollectionUtils.isNotEmpty(processModel.getSubProcesses())) {
            List<B2BUnitModel> b2BUnitModels = processModel.getSubProcesses().stream()
                .map(PartnerResellerSiteBusinessProcessModel::getUnit).filter(Objects::nonNull)
                .distinct().toList();
            removeAllExitingB2BUnits(b2BCustomerModel);
            if (CollectionUtils.isNotEmpty(b2BUnitModels)) {
                b2BCustomerModel.getGroups().addAll(b2BUnitModels);
                getB2BUnitService().setDefaultB2BUnit(b2BCustomerModel, b2BUnitModels);
                getModelService().save(b2BCustomerModel);
                LOG.debug("Successfully add AllSiteIdToCustomer");
                return Transition.OK;
            } else {
                final B2BUnitModel defaultB2BUnit = getB2BUnitService().getUnitForUid(
                    getDefaultB2BUnitCode(), true);
                b2BCustomerModel.getGroups().add(defaultB2BUnit);
                b2BCustomerModel.setDefaultB2BUnit(defaultB2BUnit);
                getModelService().save(b2BCustomerModel);
            }
        }
        LOG.error(
            String.format("Error occurred in the customer unit business process [%s] ", processModel.getCode()));
        return Transition.NOK;
    }

    protected void removeAllExitingB2BUnits(B2BCustomerModel b2BCustomerModel) {
        if (CollectionUtils.isNotEmpty(b2BCustomerModel.getGroups())) {
            Set<PrincipalGroupModel> groups = new HashSet<>(b2BCustomerModel.getGroups());
            List<PrincipalGroupModel> existingB2BUnits = groups.stream()
                .filter(B2BUnitModel.class::isInstance).toList();
            existingB2BUnits.forEach(groups::remove);
            b2BCustomerModel.setGroups(groups);
        }
    }

    public String getDefaultB2BUnitCode() {
        return defaultB2BUnitCode;
    }

    public PartnerB2BUnitService<B2BUnitModel, B2BCustomerModel> getB2BUnitService() {
        return b2BUnitService;
    }
}
