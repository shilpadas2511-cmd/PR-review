/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.core.user.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ibm.commerce.partner.core.enums.IbmPartnerB2BUnitType;
import com.ibm.commerce.partner.core.login.data.response.IbmIdPublicKeyResponseData;
import com.ibm.commerce.partner.core.login.data.response.IbmIdPublicKeyResponseListData;
import com.ibm.commerce.partner.core.login.services.IbmIdOutboundIntegration;
import com.ibm.commerce.partner.core.model.IbmConsumedDestinationModel;
import com.ibm.commerce.partner.core.model.IbmPartnerB2BUnitModel;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.PartnerEmployeeModel;
import com.ibm.commerce.partner.core.oauth2.provider.custom.CustomJwksCacheKey;
import com.ibm.commerce.partner.core.services.IbmConsumedDestinationService;
import com.ibm.commerce.partner.core.services.IbmOutboundIntegrationService;
import com.ibm.commerce.partner.data.PartnerB2BRegistrationData;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bacceleratorfacades.registration.B2BRegistrationFacade;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commerceservices.customer.CustomerService;
import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.regioncache.key.CacheKey;
import de.hybris.platform.regioncache.region.CacheRegion;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.user.impl.DefaultUserService;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import java.util.Map;
import javax.annotation.Resource;

import com.ibm.commerce.common.core.constants.CommonCoreConstants;
import com.ibm.commerce.common.core.model.SellerAudienceMaskModel;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.IbmPartnerSellerAudienceUserGroupModel;
import com.ibm.commerce.partner.core.model.PartnerB2BCustomerModel;
import com.ibm.commerce.partner.core.strategy.PartnerSessionCountryStrategy;
import com.ibm.commerce.partner.core.user.dao.PartnerUserDao;
import com.ibm.commerce.partner.core.user.service.PartnerUserService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;

/**
 * Common service class to get country from session
 */
public class DefaultPartnerUserService extends DefaultUserService implements PartnerUserService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultPartnerUserService.class);

    @Resource(name = "sessionCountryStrategy")
    private transient PartnerSessionCountryStrategy sessionCountryStrategy;

    @Resource(name = "businessProcessService")
    private transient BusinessProcessService businessProcessService;

    @Resource(name = "partnerUserDao")
    private transient PartnerUserDao partnerUserDao;

    @Resource(name = "partnerUniqueUidKeyGenerator")
    protected transient KeyGenerator processCodeGenerator;

    @Resource(name = "ibmCommonConfigurationService")
    private transient ConfigurationService configurationService;

    @Resource(name = "ibmIdOutboundIntegration")
    private transient IbmIdOutboundIntegration ibmIdOutboundIntegration;

    @Resource(name = "ibmConsumedDestinationService")
    private transient IbmConsumedDestinationService consumedDestinationService;

    @Resource(name = "ibmOutboundIntegrationService")
    private transient IbmOutboundIntegrationService outboundIntegrationService;

    @Resource(name = "b2bRegistrationFacade")
    private B2BRegistrationFacade b2bRegistrationFacade;

    @Resource(name = "customCacheRegion")
    private CacheRegion customCacheRegion;

    @Resource(name = "customerService")
    private CustomerService customerService;

    public static final String USER_REGISTRATION_FAILED = "Unable to register user";
    public static final String IBM_ID_JWKS_PUBLIC_KEY = "IbmIdJwksPublicKey";
    public static final String PRM_SERVICE_DESTINATION_TARGET_ID = "prmService";
    public static final String JWKS_PUBLIC_KEY_CONSUMED_DESTINATION = "ibmIdJwtLoginJwksPublicKeyConsumedDestination";
    public static final String RSA_ALGORITHM = "RSA";
    public static final String TOKEN_VERIFICATION_FAILED = "Token can not be decoded. Token verification failed: ";
    public static final String CACHE_ACCESS_ERROR = "Error while accessing cache : ";
    public static final String USER_DOES_NOT_EXIST = "User does not exist : ";

    public DefaultPartnerUserService() {
    }

    @Override
    public CountryModel getCountry() {
        return getSessionCountryStrategy().getSessionCountry(getCurrentUser());
    }

    @Override
    public CountryModel getCountry(final UserModel userModel, final AbstractOrderModel orderModel) {
        return getSessionCountryStrategy().getSessionCountry(userModel, orderModel);
    }

    @Override
    public void getAndSetCurrentCountry() {
        final CountryModel country = getCountry();
        setCurrentCountry(country);
    }

    @Override
    public void getAndSetCurrentCountry(final UserModel userModel,
        final AbstractOrderModel orderModel) {
        final CountryModel country = getCountry(userModel, orderModel);
        setCurrentCountry(country);
    }

    @Override
    public void setCurrentCountry(final CountryModel country) {

        String sessionCountryCode = CommonCoreConstants.EMPTY_SESSION_COUNTRY;
        if (country != null) {
            sessionCountryCode = country.getIsocode();
        }
        getSessionService().setAttribute(CommonCoreConstants.SESSION_COUNTRY, sessionCountryCode);
    }

    @Override
    public SellerAudienceMaskModel getSellerAudienceMaskForCurrentUser() {

        return getSellerAudienceMask(getCurrentUser());
    }

    @Override
    public SellerAudienceMaskModel getSellerAudienceMask(final UserModel userModel) {
        return userModel.getGroups().stream()
            .filter(IbmPartnerSellerAudienceUserGroupModel.class::isInstance)
            .map(IbmPartnerSellerAudienceUserGroupModel.class::cast)
            .map(IbmPartnerSellerAudienceUserGroupModel::getSellerAudienceType).findFirst()
            .orElse(null);
    }

    public PartnerSessionCountryStrategy getSessionCountryStrategy() {
        return sessionCountryStrategy;
    }

    /**
     * Create and Start the Business Process on CustomerModel.
     *
     * @param customerModel
     * @return
     */
    @Override
    public StoreFrontCustomerProcessModel createUpdateSiteIdBusinessProcess(
        final PartnerB2BCustomerModel customerModel) {
        final StoreFrontCustomerProcessModel processModel = getBusinessProcessService().createProcess(
            getProcessCodeGenerator().generateFor(
                PartnercoreConstants.CUSTOMER_SITE_ID_PROCESS_CODE + PartnercoreConstants.HYPHEN
                    + customerModel.getUid()).toString(),
            PartnercoreConstants.CUSTOMER_SITE_ID_PROCESS_CODE);
        processModel.setCustomer(customerModel);
        getModelService().save(processModel);
        getBusinessProcessService().startProcess(processModel);
        return processModel;
    }

    /**
     * Fetch all Active PartnerB2BCustomers
     *
     * @return
     */

    @Override
    public List<PartnerB2BCustomerModel> getActivePartnerB2BCustomers() {
        return getPartnerUserDao().getActivePartnerB2BCustomers();
    }

    /**
     * Validates the user details with jwt token and enables or disables existing customer and
     * creates new user if needed
     *
     * @param decodedJWT
     * @param userName
     * @return boolean
     */
    public boolean isJWTAuthenticatedUser(final DecodedJWT decodedJWT, final String userName) {
        return isSoftwareQuoting(userName, decodedJWT);
    }

    /**
     * Validates the jwt token to check software quoting role and enables or disables existing
     * customer and creates new user if needed
     *
     * @param decodedJWT
     * @param userName
     * @return boolean
     */
    public boolean isSoftwareQuoting(String userName, DecodedJWT decodedJWT) {
        boolean isSoftwareQuotingRole = false;
        if (null != decodedJWT.getClaim(PartnercoreConstants.JWT_PARTNERWORLD)) {
            try {
                Map<String, Object> partnerWorldMap = decodedJWT.getClaim(
                    PartnercoreConstants.JWT_PARTNERWORLD).asMap();
                List<Map<String, Object>> wwEnterprises = (List<Map<String, Object>>) partnerWorldMap.get(
                    PartnercoreConstants.JWT_WWENTERPRISES);
                isSoftwareQuotingRole = wwEnterprises.stream()
                    .flatMap(enterprise -> ((List<Map<String, Object>>) enterprise.get(
                        PartnercoreConstants.JWT_COUNTRYENTERPRISES)).stream())
                    .flatMap(
                        countryEnterprise -> ((List<Map<String, String>>) countryEnterprise.get(
                            PartnercoreConstants.JWT_ROLES)).stream())
                    .anyMatch(role -> PartnercoreConstants.JWT_QUOTINGSOFTWARE.equals(
                        role.get(PartnercoreConstants.JWT_ROLEAPINAME)));
            } catch (Exception e) {
                LOG.error(PartnercoreConstants.ROLE_VALIDATE_EXCEPTION, e.getMessage());
            }
        }
        LOG.info(PartnercoreConstants.USER_SOFTWARE_QUOTING_ROLE,
            isSoftwareQuotingRole ? PartnercoreConstants.IS_AVAILABLE
                : PartnercoreConstants.IS_NOT_AVAILABLE, userName);
        createOrUpdateUserWithToken(userName, decodedJWT, isSoftwareQuotingRole);
        return isSoftwareQuotingRole;
    }


    /**
     * Enables or disables existing customer and creates new user if needed
     *
     * @param decodedJWT
     * @param userName
     * @param isSoftwareQuoting
     */
    public void createOrUpdateUserWithToken(String userName, DecodedJWT decodedJWT,
        boolean isSoftwareQuoting) {
        UserModel currentUser = null;
        try {
            currentUser = getUserForUID(userName);
            LOG.info(PartnercoreConstants.USER_EXISTS, currentUser.getUid());
        } catch (final UnknownIdentifierException ex) {
            LOG.error(USER_DOES_NOT_EXIST , userName);

            final CustomerModel customerByCustomerId = getCustomerService().getCustomerByCustomerId(
                userName);
            if(customerByCustomerId != null) {
                currentUser = customerByCustomerId;
            }
            if (isSoftwareQuoting && currentUser == null) {
                registerNewB2BCustomer(userName, decodedJWT);
            }
        }
        if (currentUser != null) {
            getSessionService().setAttribute(PartnercoreConstants.SESSION_TEMP_CUSTOMER,
                currentUser);
        }
        enableOrDisableB2BCustomer((null != currentUser ? currentUser : getUserForUID(userName)),
            isSoftwareQuoting);
    }

    /**
     * Enables or disables existing customer
     *
     * @param user
     * @param isSoftwareQuoting
     */
    public void enableOrDisableB2BCustomer(UserModel user, boolean isSoftwareQuoting) {
        if (user instanceof B2BCustomerModel b2BCustomerModel) {
            boolean needsUpdate = false;
            if (isSoftwareQuoting && (!Boolean.TRUE.equals(b2BCustomerModel.getActive())
                || b2BCustomerModel.isLoginDisabled())) {
                b2BCustomerModel.setActive(Boolean.TRUE);
                b2BCustomerModel.setLoginDisabled(false);
                needsUpdate = true;
                LOG.info(PartnercoreConstants.ACTIVATE_USER, user.getUid());
            } else if (!isSoftwareQuoting && (Boolean.TRUE.equals(b2BCustomerModel.getActive())
                || !b2BCustomerModel.isLoginDisabled())) {
                b2BCustomerModel.setActive(Boolean.FALSE);
                b2BCustomerModel.setLoginDisabled(true);
                needsUpdate = true;
                LOG.info(PartnercoreConstants.DEACTIVATE_USER, user.getUid());
            }
            if (needsUpdate) {
                getModelService().save(b2BCustomerModel);
                LOG.info(PartnercoreConstants.ACCOUNT_STATUS_UPDATE, user.getUid());
            }
        }
    }

    /**
     * Returns the {@link B2BCustomerModel} for the specified email.
     *
     * @param email the email of the customer
     * @return the matching {@link B2BCustomerModel}, or {@code null} if none found
     */
    @Override
    public B2BCustomerModel getCustomerByEmail(String email) {
        return getPartnerUserDao().getCustomerByEmail(email);
    }

    /**
     * Creates new Customer with Jwt Token details
     *
     * @param userName
     * @param decodedJWT
     */
    public void registerNewB2BCustomer(String userName, DecodedJWT decodedJWT) {
        LOG.info(PartnercoreConstants.NEW_CUST_REG, userName);
        PartnerB2BRegistrationData userData = new PartnerB2BRegistrationData();

        CountryData countryData = new CountryData();
        countryData.setIsocode(decodedJWT.getClaim(PartnercoreConstants.JWT_COUNTRY_CODE).asString());

        LanguageData languageData = new LanguageData();
        languageData.setIsocode(PartnercoreConstants.DEFAULT_LANG_ISOCODE);

        List<String> roles = new ArrayList<>();
        roles.add(PartnercoreConstants.B2BCUSTOMERGROUP);
        roles.add(PartnercoreConstants.RES);

        userData.setUid(userName);
        userData.setEmail(userName);
        userData.setActive(true);
        userData.setDefaultCountry(countryData);
        userData.setDefaultLanguage(languageData);
        userData.setFirstName(decodedJWT.getClaim(PartnercoreConstants.JWT_GIVEN_NAME).asString());
        userData.setLastName(decodedJWT.getClaim(PartnercoreConstants.JWT_FAMILY_NAME).asString());
        userData.setRoles(roles);

        try {
            getB2bRegistrationFacade().register(userData);
        } catch (final Exception e) {
            LOG.error(
                String.format(PartnercoreConstants.NEW_CUST_REG_FAILED, userName, e.getMessage()),
                e);
            throw new InvalidClientException(USER_REGISTRATION_FAILED);
        }
    }


    /**
     * @return List  of  PartnerEmployeeModel
     */
    public List<PartnerEmployeeModel> getAllPartnerEmployees() {
        return getPartnerUserDao().getAllPartnerEmployee();
    }

    /**
     * This method determines whether VAD view is enabled or not for  Current User
     *
     * @param abstractOrderModel the order model that needs to be checked.
     * @param userModel          the user model representing the viewer.
     * @return true if the VAD views enabled for Current User,false otherwise.
     */
    public boolean isVadView(final AbstractOrderModel abstractOrderModel,
        final UserModel userModel) {
        if (abstractOrderModel instanceof IbmPartnerCartModel ibmPartnerCartModel) {
            return verifyVADUser(ibmPartnerCartModel.getBillToUnit(), userModel);
        } else if (abstractOrderModel instanceof IbmPartnerQuoteModel ibmPartnerQuoteModel) {
            return verifyVADUser(ibmPartnerQuoteModel.getBillToUnit(), userModel);
        }
        return Boolean.FALSE;
    }

    /**
     * This method determines whether VAD view is enabled or not for  Current User
     *
     * @param partnerB2BUnitModel the b2bunit model that needs to be checked.
     * @param userModel           the user model representing the viewer.
     * @return true if the VAD views enabled for Current User,false otherwise.
     */
    protected boolean verifyVADUser(final B2BUnitModel partnerB2BUnitModel,
        final UserModel userModel) {
        return (partnerB2BUnitModel instanceof IbmPartnerB2BUnitModel b2bUnitModel
            && userModel != null && CollectionUtils.isNotEmpty(userModel.getGroups())
            && IbmPartnerB2BUnitType.DISTRIBUTOR.equals(b2bUnitModel.getType())
            && userModel.getGroups().contains(partnerB2BUnitModel));
    }

    /**
     * This method validates jwt token signature with jwks public key and decodes the jwt token
     *
     * @param jwtToken   - encoded jwtToken
     * @param isFallback - public key got changed, hence need to invalidate cache
     * @return DecodedJWT
     */
    public DecodedJWT getDecodedJwtToken(String jwtToken, boolean isFallback) {
        IbmIdPublicKeyResponseData publicKey = getOrSetCachedPublicKey(isFallback);
        return validateJwtTokenSignature(jwtToken, publicKey.getModulus(), publicKey.getExponent());
    }

    /**
     * This method fetches the public key from cache based on ttl or invalidates the cache to fetch
     * public key from PRM
     *
     * @param invalidateCache
     * @return IbmIdPublicKeyResponseData
     */
    public IbmIdPublicKeyResponseData getOrSetCachedPublicKey(boolean invalidateCache) {
        CacheKey cacheKey = new CustomJwksCacheKey(getCustomCacheRegion().getName(),
            IBM_ID_JWKS_PUBLIC_KEY);
        if (invalidateCache) {
            getCustomCacheRegion().remove(cacheKey, true);
            LOG.info(PartnercoreConstants.JWT_CACHE_INVALIDATION);
        }
        try {
            return (IbmIdPublicKeyResponseData) getCustomCacheRegion().getWithLoader(cacheKey,
                key -> getJwksPublicKey());
        } catch (Exception e) {
            LOG.error(CACHE_ACCESS_ERROR , e);
        }
        return getJwksPublicKey();
    }

    /**
     * This method validates the signature of jwt token with jwks public key
     *
     * @param jwtToken
     * @param base64Modulus
     * @param base64Exponent
     * @return DecodedJWT
     */
    public DecodedJWT validateJwtTokenSignature(String jwtToken, String base64Modulus,
        String base64Exponent) {
        try {
            DecodedJWT decodedJWT = JWT.decode(jwtToken);
            RSAPublicKey publicKey = getRSAPublicKey(base64Modulus, base64Exponent);
            Algorithm algorithm = Algorithm.RSA256(publicKey, null);
            algorithm.verify(decodedJWT);
            return decodedJWT;
        } catch (Exception e) {
            LOG.error(TOKEN_VERIFICATION_FAILED , e.getMessage());
            if (Boolean.FALSE.equals(getConfigurationService().getConfiguration()
                .getBoolean(PartnercoreConstants.FLAG_PARTNER_USER_TOKEN_LOGGER_DISABLED,
                    Boolean.TRUE))) {
                LOG.info(PartnercoreConstants.LOG_PARTNER_LOGIN_ENCODED_TOKEN, jwtToken);
            }
        }
        return null;
    }

    /**
     * This method constructs the RSA public key from the modulus and exponent of the jwks public
     * key
     *
     * @param base64Modulus
     * @param base64Exponent
     * @return RSAPublicKey
     */
    private static RSAPublicKey getRSAPublicKey(String base64Modulus, String base64Exponent)
        throws InvalidKeySpecException, NoSuchAlgorithmException {
        byte[] modulusBytes = Base64.getUrlDecoder().decode(base64Modulus);
        byte[] exponentBytes = Base64.getUrlDecoder().decode(base64Exponent);
        BigInteger modulus = new BigInteger(1, modulusBytes);
        BigInteger exponent = new BigInteger(1, exponentBytes);
        RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
        KeyFactory factory = KeyFactory.getInstance(RSA_ALGORITHM);
        return (RSAPublicKey) factory.generatePublic(spec);
    }

    /**
     * This method fetches the jwks public key from PRM
     *
     * @return IbmIdPublicKeyResponseData
     */
    public IbmIdPublicKeyResponseData getJwksPublicKey() {
        LOG.info(PartnercoreConstants.JWT_REALTIME_KEY_FETCH);
        final IbmConsumedDestinationModel consumedDestinationModel = (IbmConsumedDestinationModel) getConsumedDestinationService().findActiveConsumedDestinationByIdAndTargetId(
            JWKS_PUBLIC_KEY_CONSUMED_DESTINATION, PRM_SERVICE_DESTINATION_TARGET_ID);
        final HttpHeaders headers = getOutboundIntegrationService().getHeaders(
            consumedDestinationModel);
        IbmIdPublicKeyResponseListData ibmIdPublicKeyResponseListData = getOutboundIntegrationService().sendRequest(
            HttpMethod.GET,
            consumedDestinationModel.getUrl(), headers, null,
            IbmIdPublicKeyResponseListData.class, HttpStatus.OK);

        List<IbmIdPublicKeyResponseData> keys = ibmIdPublicKeyResponseListData.getKeys();
        return keys.get(0);
    }

    public BusinessProcessService getBusinessProcessService() {
        return businessProcessService;
    }

    public PartnerUserDao getPartnerUserDao() {
        return partnerUserDao;
    }

    public KeyGenerator getProcessCodeGenerator() {
        return processCodeGenerator;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public IbmIdOutboundIntegration getIbmIdOutboundIntegration() {
        return ibmIdOutboundIntegration;
    }

    public IbmConsumedDestinationService getConsumedDestinationService() {
        return consumedDestinationService;
    }

    public IbmOutboundIntegrationService getOutboundIntegrationService() {
        return outboundIntegrationService;
    }

    public CacheRegion getCustomCacheRegion() {
        return customCacheRegion;
    }

    public B2BRegistrationFacade getB2bRegistrationFacade() {
        return b2bRegistrationFacade;
    }

    public CustomerService getCustomerService() {
        return customerService;
    }
}