package com.ibm.commerce.partner.core.user.dao;

import com.ibm.commerce.partner.core.model.PartnerB2BCustomerModel;
import com.ibm.commerce.partner.core.model.PartnerEmployeeModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.daos.UserDao;
import java.util.List;

/**
 * This class is to declare the methods related to user.
 */
public interface PartnerUserDao extends UserDao {

    /**
     * Fetch all active PartnerB2BCustomers.
     * @return
     */
    List<PartnerB2BCustomerModel> getActivePartnerB2BCustomers();

    /**
     * Fetch all  Partner Employees.
     * @return List  of  PartnerEmployeeModel
     */

    List<PartnerEmployeeModel> getAllPartnerEmployee();

    /**
     * Returns the {@link B2BCustomerModel} for the specified email.
     *
     * @param email the email of the customer
     * @return the matching {@link B2BCustomerModel}, or {@code null} if none found
     */
    B2BCustomerModel getCustomerByEmail(String email);

}
