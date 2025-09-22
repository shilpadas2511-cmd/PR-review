/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.core.user.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.ibm.commerce.common.core.model.SellerAudienceMaskModel;
import com.ibm.commerce.partner.core.model.PartnerB2BCustomerModel;
import com.ibm.commerce.partner.core.model.PartnerEmployeeModel;
import com.ibm.commerce.partner.sso.prm.data.IbmPartnerSSOUserTokenInboundData;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;
import java.util.List;


/**
 * Interface to get session country
 */
public interface PartnerUserService extends UserService {

    /**
     * This method will be used to get Country based on current user and session cart
     */
    CountryModel getCountry();

    /**
     * This method will be used to get Country from user and order
     *
     * @param userModel
     * @param orderModel
     */
    CountryModel getCountry(UserModel userModel, AbstractOrderModel orderModel);

    /**
     * This method will be used to set Country in Session based on CurrentUser and CurrentCart
     */
    void getAndSetCurrentCountry();

    /**
     * This method will be used to set Country in Session
     *
     * @param userModel
     * @param orderModel
     */
    void getAndSetCurrentCountry(UserModel userModel, AbstractOrderModel orderModel);

    /**
     * This method will be used to set Country in Session.
     *
     * @param country
     */
    void setCurrentCountry(final CountryModel country);

    /**
     * Fetches SellerAudienceMask for Current User
     *
     * @return
     */
    SellerAudienceMaskModel getSellerAudienceMaskForCurrentUser();

    /**
     * Fetches SellerAudienceMask for User
     *
     * @param userModel
     * @return
     */
    SellerAudienceMaskModel getSellerAudienceMask(UserModel userModel);

    /**
     * Create and Start the Business Process on CutomerModel.
     *
     * @param customerModel
     * @return
     */
    StoreFrontCustomerProcessModel createUpdateSiteIdBusinessProcess(
        PartnerB2BCustomerModel customerModel);

    /**
     * Get all Active PartnerB2BCustomers
     *
     * @return
     */
    List<PartnerB2BCustomerModel> getActivePartnerB2BCustomers();

    /**
     * Validates the user details with jwt token and enables or disables existing customer and
     * creates new user if needed
     *
     * @param decodedJWT
     * @param userName
     * @return boolean
     */
    boolean isJWTAuthenticatedUser(final DecodedJWT decodedJWT, final String userName);

    /**
     * Fetch all  Partner Employees.
     *
     * @return List  of PartnerEmployeeModel
     */
    List<PartnerEmployeeModel> getAllPartnerEmployees();

    /**
     * This method determines whether VAD view is enabled or not for  Current User
     *
     * @param abstractOrderModel the order model that needs to be checked. It must not be null.
     * @param userModel          the user model representing the viewer. It must not be null.
     * @return true if the order can be viewed by the user; false otherwise.
     */
    public boolean isVadView(final AbstractOrderModel abstractOrderModel,
        final UserModel userModel);

    /**
     * This method validates jwt token signature with jwks public key and decodes the jwt token
     *
     * @param jwtToken   - encoded jwtToken
     * @param isFallback - public key got changed, hence need to invalidate cache
     * @return DecodedJWT
     */
    public DecodedJWT getDecodedJwtToken(String jwtToken, boolean isFallback);

    /**
     * Enables or disables existing customer
     *
     * @param user
     * @param isSoftwareQuoting
     */
    void enableOrDisableB2BCustomer(UserModel user, boolean isSoftwareQuoting);

    /**
     * Returns the {@link B2BCustomerModel} for the specified email.
     *
     * @param email the email of the customer
     * @return the matching {@link B2BCustomerModel}, or {@code null} if none found
     */
    B2BCustomerModel getCustomerByEmail(String email);

}

