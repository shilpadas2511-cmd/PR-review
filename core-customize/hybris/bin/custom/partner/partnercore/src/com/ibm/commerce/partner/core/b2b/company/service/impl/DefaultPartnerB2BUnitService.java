package com.ibm.commerce.partner.core.b2b.company.service.impl;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.b2b.company.service.PartnerB2BUnitService;
import com.ibm.commerce.partner.core.utils.PartnerB2BUnitUtils;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.impl.DefaultB2BUnitService;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.List;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation for {@link PartnerB2BUnitService}
 */
public class DefaultPartnerB2BUnitService extends DefaultB2BUnitService implements
    PartnerB2BUnitService<B2BUnitModel, B2BCustomerModel> {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultPartnerB2BUnitService.class);
    private final ModelService modelService;

    public DefaultPartnerB2BUnitService(final ModelService modelService) {
        this.modelService = modelService;
    }

    @Override
    public B2BUnitModel getUnitForUid(final String uid, final boolean isSearchRestrictionDisabled) {

        if (isSearchRestrictionDisabled) {
            getSearchRestrictionService().disableSearchRestrictions();
        }
        B2BUnitModel unitForUid = getUnitForUid(uid);
        if (isSearchRestrictionDisabled) {
            getSearchRestrictionService().enableSearchRestrictions();
        }
        return unitForUid;
    }

    @Override
    public boolean isActive(final B2BUnitModel b2bUnit) {

        final UserModel currentUser = getUserService().getCurrentUser();
        return currentUser != null && CollectionUtils.isNotEmpty(currentUser.getGroups())
            && currentUser.getGroups().stream().filter(B2BUnitModel.class::isInstance)
            .map(B2BUnitModel.class::cast).anyMatch(group -> isActive(group, b2bUnit));
    }

    protected boolean isActive(B2BUnitModel groupModel, B2BUnitModel currentB2BUnit) {
        return groupModel != null && currentB2BUnit != null && groupModel.getUid()
            .equals(currentB2BUnit.getUid()) || isDistributorUnit(groupModel, currentB2BUnit)
            || isParentUnit(groupModel, currentB2BUnit);
    }

    protected boolean isDistributorUnit(B2BUnitModel b2bUnit, B2BUnitModel currentB2BUnit) {

        return b2bUnit != null && currentB2BUnit != null
            && b2bUnit.getReportingOrganization() != null && (
            b2bUnit.getReportingOrganization().getUid().equals(currentB2BUnit.getUid())
                || isParentUnit(b2bUnit.getReportingOrganization(), currentB2BUnit));
    }

    protected boolean isParentUnit(B2BUnitModel b2bUnit, B2BUnitModel currentB2BUnit) {

        return b2bUnit != null && currentB2BUnit != null && CollectionUtils.isNotEmpty(
            b2bUnit.getGroups()) && b2bUnit.getGroups().stream()
            .anyMatch(group -> group.getUid().equals(currentB2BUnit.getUid()));
    }

    /**
     * This method sets the default b2b site of the user
     *
     * @param customerModel - the user model whose default site needs to updated
     * @param b2BUnitModels - the fresh site list of the user
     */
    public void setDefaultB2BUnit(final B2BCustomerModel customerModel,
        final List<B2BUnitModel> b2BUnitModels) {

        if (customerModel != null && CollectionUtils.isNotEmpty(b2BUnitModels)) {
            if (customerModel.getDefaultB2BUnit() == null || !b2BUnitModels.get(0)
                .equals(customerModel.getDefaultB2BUnit())) {
                customerModel.setDefaultB2BUnit(b2BUnitModels.get(0));
            }
        }
    }

    /**
     * This method checks whether the b2b unit is present in the user group
     *
     * @param b2bUnit - the b2b unit which need to be checked
     * @return boolean
     */
    @Override
    public boolean isUserAssociatedUnit(B2BUnitModel b2bUnit) {
        final UserModel currentUser = getUserService().getCurrentUser();
        return currentUser != null && CollectionUtils.isNotEmpty(currentUser.getGroups())
            && currentUser.getGroups().stream().anyMatch(group -> group.equals(b2bUnit));
    }

    /**
     * This method is used to set the default partnerB2BUnit on the customer
     *
     * @param b2bCustomerModel customer
     * @param defaultPartnerB2BUnitId parterB2Bunit
     */
    @Override
    public void setDefaultPartnerB2BUnit(final B2BCustomerModel b2bCustomerModel,
        final String defaultPartnerB2BUnitId) {
        b2bCustomerModel.setDefaultB2BUnit(getUnitForUid(defaultPartnerB2BUnitId,
            Boolean.TRUE));

    }


    /**
     * This method is used to set active sites on the customer
     *
     * @param b2bCustomerModel customer
     * @param defaultPartnerB2BUnitId parterB2Bunit
     * @param isResellerTier1Enabled
     */
    @Override
    public void setActiveSitesToCustomer(final B2BCustomerModel b2bCustomerModel,
        final String defaultPartnerB2BUnitId, final boolean isResellerTier1Enabled) {
        try {
            if (PartnerB2BUnitUtils.findAnyNotActiveSite(b2bCustomerModel,
                defaultPartnerB2BUnitId)) {
                final Set<PrincipalGroupModel> groups = PartnerB2BUnitUtils.getGroups(
                    b2bCustomerModel);
                List<B2BUnitModel> activeUnitModels = PartnerB2BUnitUtils.filteredActiveSites(
                    b2bCustomerModel);
                if (!isResellerTier1Enabled && CollectionUtils.isNotEmpty(activeUnitModels)) {
                    activeUnitModels = PartnerB2BUnitUtils.filteredNonTier1Sites(activeUnitModels);
                }
                saveFilteredCustomerGroups(activeUnitModels, groups, b2bCustomerModel,
                    defaultPartnerB2BUnitId);
            } else if (!isResellerTier1Enabled) {
                setNonTier1SitesToCustomer(b2bCustomerModel,
                    defaultPartnerB2BUnitId);
            }
        } catch (final Exception e) {
            LOG.error("Exception while saving the active sites on the Customer :"
                + b2bCustomerModel.getUid() + "Error:", e);
        }
    }

    /**
     * Filters and assigns non-tier-1 B2B sites to the specified B2B customer.
     * <p>
     * This method retrieves the customer's associated groups and B2B units, filters out any tier-1
     * sites from the unit list, and then updates the customer with the remaining units. If no valid
     * units are found, the method does not update the customer's groups. Any errors during the
     * process are logged, and no exception is propagated.
     *
     * @param b2bCustomerModel        the B2B customer to update
     * @param defaultPartnerB2BUnitId the ID of the default partner B2B unit to be used if necessary
     *                                in downstream processing
     */
    @Override
    public void setNonTier1SitesToCustomer(B2BCustomerModel b2bCustomerModel,
        String defaultPartnerB2BUnitId) {
        try {
            if (CollectionUtils.isNotEmpty(b2bCustomerModel.getGroups())) {
                final Set<PrincipalGroupModel> groups = PartnerB2BUnitUtils.getGroups(
                    b2bCustomerModel);
                List<B2BUnitModel> unitModels = PartnerB2BUnitUtils.getCustomerB2bUnits(
                    b2bCustomerModel);
                if (CollectionUtils.isNotEmpty(unitModels)) {
                    unitModels = PartnerB2BUnitUtils.filteredNonTier1Sites(unitModels);
                    saveFilteredCustomerGroups(unitModels, groups, b2bCustomerModel,
                        defaultPartnerB2BUnitId);
                }
            }
        } catch (final Exception e) {
            LOG.error(PartnercoreConstants.SAVE_NON_TIER1_ERROR_LOG, b2bCustomerModel.getUid());
        }
    }


    /**
     * Saves the filtered B2B customer groups and sets the appropriate default B2B unit for the
     * given customer.
     * <p>
     * If the provided list of {@code unitModels} is not empty, the units are added to the
     * customer's groups and the default B2B unit is set based on the provided list. Otherwise, a
     * fallback partner B2B unit is used as the default unit. Finally, the updated group set is
     * assigned to the customer and persisted.
     *
     * @param unitModels              the list of B2B units to assign to the customer, can be empty
     *                                or null
     * @param groups                  the set of principal groups to update with the B2B units
     * @param b2bCustomerModel        the B2B customer whose groups and default unit are being
     *                                modified
     * @param defaultPartnerB2BUnitId the ID of the default partner B2B unit to use when no unit
     *                                models are provided
     */
    public void saveFilteredCustomerGroups(List<B2BUnitModel> unitModels,
        Set<PrincipalGroupModel> groups,B2BCustomerModel b2bCustomerModel,
        String defaultPartnerB2BUnitId) {
        if (CollectionUtils.isNotEmpty(unitModels)) {
            groups.addAll(unitModels);
            setDefaultB2BUnit(b2bCustomerModel, unitModels);
        } else {
            setDefaultPartnerB2BUnit(b2bCustomerModel, defaultPartnerB2BUnitId);
        }
        b2bCustomerModel.setGroups(groups);
        modelService.save(b2bCustomerModel);
    }
}
