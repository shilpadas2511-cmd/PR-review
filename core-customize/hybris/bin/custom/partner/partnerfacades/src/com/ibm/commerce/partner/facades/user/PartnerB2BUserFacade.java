package com.ibm.commerce.partner.facades.user;

import com.ibm.commerce.partner.company.data.IbmPartnerB2BUnitData;
import com.ibm.commerce.partner.data.PartnerB2BRegistrationData;
import com.ibm.commerce.partner.sso.prm.data.IbmPartnerSSOUserTokenInboundData;
import de.hybris.platform.b2bcommercefacades.company.B2BUserFacade;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import java.util.List;
import javax.security.auth.login.AccountNotFoundException;

/**
 * This interface is for declaring the user related methods.
 */
public interface PartnerB2BUserFacade extends B2BUserFacade {

    /**
     * To update default B2BUnit and its address of the partner B2BCustomer
     *
     * @param b2bRegistrationData
     * @throws AccountNotFoundException
     */
    void updateB2BCustomer(PartnerB2BRegistrationData b2bRegistrationData)
        throws AccountNotFoundException;

    /**
     * Update Employee if exists else Create, with given parameters.
     *
     * @param employeeData - the user data to be create or update.
     * @throws IllegalStateException
     */
    CustomerData updateOrCreateEmployee(CustomerData employeeData) throws IllegalStateException;

    /**
     * Enable or Disable Employee account.
     *
     * @param emailId - Employee email id.
     * @param active  - to enable or disable Employee account
     * @throws IllegalStateException
     */
    void enableOrDisableEmployee(String emailId, boolean active) throws IllegalStateException;

    /**
     * Fetch all Partner  Employee  list
     *
     * @return List  of  CustomerData
     * @throws IllegalStateException
     */
    List<CustomerData> getAllPartnerEmployees() throws IllegalStateException;

    /**
     * Update all Sites Linked to Business Partner  Employee  list
     */
    void updateSites(List<IbmPartnerB2BUnitData> sites);

    /**
     * This method validates jwt token signature with jwks public key and decodes the jwt token
     *
     * @param token - encoded jwtToken
     * @return DecodedJWT
     */
    IbmPartnerSSOUserTokenInboundData getDecodedToken(String token);


    /**
     * Attempt to retrieve the user using the iui identifier. If not found, retry using the
     * preferred_username. If the user is found: Check whether the user has the "QUOTING_SOFTWARE"
     * role. If the role is valid: Update the user details by converting
     * IbmPartnerSSOUserTokenInboundData to PartnerB2BCustomerModel using the converter populator.
     * Enable the user. If the role is invalid: Disable the user. If the user is not found: If the
     * role is valid: Create a new user by converting IbmPartnerSSOUserTokenInboundData to
     * PartnerB2BCustomerModel using the converter populator. Enable the user. If the role is
     * invalid: Log the event for monitoring purposes only.
     *
     * @param token - encoded jwtToken
     * @return DecodedJWT
     */
    void createOrUpdate(IbmPartnerSSOUserTokenInboundData token) ;

    /**
     * Updates the last login timestamp for the user with the given UID.
     *
     * @param uid the user UID
     */
    void updateLastLogin(String uid);

}