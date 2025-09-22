package com.ibm.commerce.partner.core.b2b.company.service;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.catalog.model.CompanyModel;
import de.hybris.platform.core.model.user.UserModel;
import java.util.List;

/**
 * Extends Implementation for B2BUnitService
 */
public interface PartnerB2BUnitService<T extends CompanyModel, U extends UserModel> extends
    B2BUnitService<T, U> {

    /**
     * Find unit by uid.
     *
     * @param uid                         the uid
     * @param isSearchRestrictionDisabled isSearchRestrictionDisabled
     * @return the b2 b unit model
     */
    T getUnitForUid(String uid, boolean isSearchRestrictionDisabled);

    /**
     * Validates if the B2BUnitModel is active or not for the Current User
     *
     * @param b2BUnitModel
     * @return
     */
    boolean isActive(B2BUnitModel b2BUnitModel);

    /**
     * This method sets the default b2b site of the user
     *
     * @param customerModel - the user model whose default site needs to updated
     * @param b2BUnitModels - the fresh site list of the user
     */
    public void setDefaultB2BUnit(B2BCustomerModel customerModel, List<B2BUnitModel> b2BUnitModels);

    /**
     * This method checks whether the b2b unit is present in the user group
     *
     * @param b2bUnit - the b2b unit which need to be checked
     * @return boolean
     */
    public boolean isUserAssociatedUnit(B2BUnitModel b2bUnit);

    /**
     * This method is used to set the default partnerB2BUnit on the customer
     *
     * @param b2bCustomerModel
     * @param defaultPartnerB2BUnitId
     */
    public void setDefaultPartnerB2BUnit(final B2BCustomerModel b2bCustomerModel,
        String defaultPartnerB2BUnitId);

    /**
     * This method is used to set active sites on the b2BCustomerModel
     *
     * @param b2bCustomerModel
     * @param defaultPartnerB2BUnitId
     * @param isResellerTier1Enabled
     */
    public void setActiveSitesToCustomer(final B2BCustomerModel b2bCustomerModel,
        final String defaultPartnerB2BUnitId, final boolean isResellerTier1Enabled);

    /**
     * This method is used to set non tier1 sites on the b2BCustomerModel
     *
     * @param b2bCustomerModel
     * @param defaultPartnerB2BUnitId
     */
    public void setNonTier1SitesToCustomer(final B2BCustomerModel b2bCustomerModel,
        final String defaultPartnerB2BUnitId);

}
