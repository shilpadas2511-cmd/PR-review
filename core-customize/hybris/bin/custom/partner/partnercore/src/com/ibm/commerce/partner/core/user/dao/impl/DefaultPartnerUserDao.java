package com.ibm.commerce.partner.core.user.dao.impl;

import com.ibm.commerce.partner.core.model.PartnerEmployeeModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.user.daos.impl.DefaultUserDao;

import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.ibm.commerce.partner.core.model.PartnerB2BCustomerModel;
import com.ibm.commerce.partner.core.user.dao.PartnerUserDao;

/**
 * Implementation class of PartnerUserDao.
 */
public class DefaultPartnerUserDao extends DefaultUserDao implements PartnerUserDao {

    private static final String ACTIVE_PARTNER_CUSTOMER_QUERY =
        "SELECT {" + ItemModel.PK + "} FROM {" + PartnerB2BCustomerModel._TYPECODE + "} "
            + "WHERE {" + B2BCustomerModel.ACTIVE + "} = ?" + B2BCustomerModel.ACTIVE;
    private static final String PARTNER_EMPLOYEE_QUERY =
        "SELECT {" + ItemModel.PK + "} FROM {" + PartnerEmployeeModel._TYPECODE + "} ";
    private static final String CUSTOMER_BY_EMAIL_QUERY =
        "SELECT {" + ItemModel.PK + "} FROM {" + B2BCustomerModel._TYPECODE + "} "
            + "WHERE {" + B2BCustomerModel.EMAIL + "} = ?" + B2BCustomerModel.EMAIL;

    /**
     * Fetch all active PartnerB2BCustomers.
     *
     * @return
     */
    public List<PartnerB2BCustomerModel> getActivePartnerB2BCustomers() {
        final FlexibleSearchQuery query = new FlexibleSearchQuery(ACTIVE_PARTNER_CUSTOMER_QUERY);
        query.addQueryParameter(B2BCustomerModel.ACTIVE, Boolean.TRUE);
        final SearchResult<PartnerB2BCustomerModel> result = getFlexibleSearchService().search(
            query);
        return CollectionUtils.isNotEmpty(result.getResult()) ? result.getResult()
            : Collections.emptyList();
    }

    /**
     * Fetch all Partner Employees.
     *
     * @return List  of  PartnerEmployeeModel
     */
    public List<PartnerEmployeeModel> getAllPartnerEmployee() {
        final FlexibleSearchQuery query = new FlexibleSearchQuery(PARTNER_EMPLOYEE_QUERY);
        final SearchResult<PartnerEmployeeModel> result = getFlexibleSearchService().search(query);
        return CollectionUtils.isNotEmpty(result.getResult()) ? result.getResult()
            : Collections.emptyList();
    }

    /**
     * Returns the {@link B2BCustomerModel} for the specified email.
     *
     * @param email the email of the customer
     * @return the matching {@link B2BCustomerModel}, or {@code null} if none found
     */
    @Override
    public B2BCustomerModel getCustomerByEmail(String email) {
        final FlexibleSearchQuery query = new FlexibleSearchQuery(CUSTOMER_BY_EMAIL_QUERY);
        query.addQueryParameter(B2BCustomerModel.EMAIL, email);
        final SearchResult<PartnerB2BCustomerModel> result = getFlexibleSearchService().search(
            query);
        return CollectionUtils.isNotEmpty(result.getResult()) ? result.getResult().get(0) : null;
    }

}
