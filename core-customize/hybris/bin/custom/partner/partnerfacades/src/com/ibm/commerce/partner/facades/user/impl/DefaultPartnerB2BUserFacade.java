package com.ibm.commerce.partner.facades.user.impl;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.enums.IbmPartnerB2BUnitType;
import com.ibm.commerce.partner.sso.prm.data.IbmPartnerSSOUserTokenInboundData;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bcommercefacades.company.impl.DefaultB2BUserFacade;
import de.hybris.platform.b2bcommercefacades.data.B2BRegistrationData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.ibm.commerce.partner.company.data.IbmPartnerB2BUnitData;
import com.ibm.commerce.partner.core.b2b.company.service.PartnerB2BUnitService;
import com.ibm.commerce.partner.core.country.services.PartnerCountryService;
import com.ibm.commerce.partner.core.currency.services.PartnerCurrencyService;
import com.ibm.commerce.partner.core.model.PartnerB2BCustomerModel;
import com.ibm.commerce.partner.core.model.PartnerEmployeeModel;
import com.ibm.commerce.partner.core.user.service.PartnerUserService;
import com.ibm.commerce.partner.core.utils.PartnerB2BUnitUtils;
import com.ibm.commerce.partner.data.PartnerB2BRegistrationData;
import com.ibm.commerce.partner.facades.company.PartnerB2BUnitFacade;
import com.ibm.commerce.partner.facades.user.PartnerB2BUserFacade;

/**
 * Default implementation of {PartnerB2BUserFacade}
 */
public class DefaultPartnerB2BUserFacade extends DefaultB2BUserFacade implements
    PartnerB2BUserFacade {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultPartnerB2BUserFacade.class);

    private static final String USER_NOT_FOUND = "user with uid %s not found as Employee!";

    @Resource(name = "partnerB2BUnitFacade")
    private PartnerB2BUnitFacade partnerB2BUnitFacade;

    @Resource(name = "partnerEmployeeReverseConverter")
    private Converter<CustomerData, PartnerEmployeeModel> partnerEmployeeReverseConverter;

    @Resource(name = "partnerEmployeeConverter")
    private Converter<PartnerEmployeeModel, CustomerData> partnerEmployeeConverter;

    @Resource(name = "partnerUserService")
    private PartnerUserService partnerUserService;

    @Resource(name = "partnerB2BUnitService")
    private PartnerB2BUnitService<B2BUnitModel, B2BCustomerModel> b2BUnitService;

    @Value("${default.partner.b2bUnit.uid}")
    private String defaultPartnerB2BUnitId;

    private final PartnerCountryService countryService;

    private final PartnerCurrencyService currencyService;

    private final ConfigurationService configurationService;

    private final Converter<DecodedJWT, IbmPartnerSSOUserTokenInboundData> ssoTokenConverter;
    private final Converter<IbmPartnerSSOUserTokenInboundData, PartnerB2BRegistrationData> ssoTokenToB2BRegistrationConverter;
    private final Converter<B2BRegistrationData, PartnerB2BCustomerModel> registrationReverseConverter;
    private final String userValidRole;


    public DefaultPartnerB2BUserFacade(final PartnerCountryService countryService,
        final PartnerCurrencyService currencyService, final ConfigurationService configurationService,
        final Converter<DecodedJWT, IbmPartnerSSOUserTokenInboundData> ssoTokenConverter,
        final Converter<IbmPartnerSSOUserTokenInboundData, PartnerB2BRegistrationData> ssoTokenToB2BRegistrationConverter,
        final Converter<B2BRegistrationData, PartnerB2BCustomerModel> registrationReverseConverter,
        final String userValidRole) {
        this.countryService = countryService;
        this.currencyService = currencyService;
        this.configurationService = configurationService;
        this.ssoTokenConverter = ssoTokenConverter;
        this.ssoTokenToB2BRegistrationConverter = ssoTokenToB2BRegistrationConverter;
        this.registrationReverseConverter = registrationReverseConverter;
        this.userValidRole = userValidRole;
    }


    /**
     * To update default B2BUnit and its address of the partner B2BCustomer
     *
     * @param b2bRegistrationData
     */
    @Override
    public void updateB2BCustomer(final PartnerB2BRegistrationData b2bRegistrationData)
        throws AccountNotFoundException {
        final PartnerB2BCustomerModel partnerB2BCustomerModel = getUserService().getUserForUID(
            b2bRegistrationData.getEmail(), PartnerB2BCustomerModel.class);
        if (Objects.isNull(partnerB2BCustomerModel)) {
            LOG.debug(String.format(USER_NOT_FOUND, b2bRegistrationData.getEmail()));
            throw new AccountNotFoundException(
                String.format(USER_NOT_FOUND, b2bRegistrationData.getEmail()));
        }
        final B2BUnitModel b2BUnitModel = getPartnerB2BUnitFacade().getOrCreate(
            b2bRegistrationData.getSiteId());
        partnerB2BCustomerModel.setDefaultB2BUnit(b2BUnitModel);
        getModelService().save(partnerB2BCustomerModel);
    }

    /**
     * This method is for creating EmployeeModel with given data.
     *
     * @param employeeData - Employee details to update or create.
     * @throws IllegalStateException
     */
    @Override
    public CustomerData updateOrCreateEmployee(final CustomerData employeeData)
        throws IllegalStateException {
        UserModel userModel = null;
        PartnerEmployeeModel employeeModel = null;
        try {
            userModel = getUserService().getUserForUID(employeeData.getUid());
            if (userModel instanceof PartnerEmployeeModel) {
                employeeModel = (PartnerEmployeeModel) userModel;
                getPartnerEmployeeReverseConverter().convert(employeeData, employeeModel);
            } else {
                throw new IllegalStateException(
                    String.format(USER_NOT_FOUND, employeeData.getUid()));
            }
        } catch (UnknownIdentifierException ex) {
            employeeModel = getPartnerEmployeeReverseConverter().convert(employeeData);
        }
        getModelService().save(employeeModel);
        return getPartnerEmployeeConverter().convert(employeeModel);
    }

    /**
     * Disabling the employee if exits.
     *
     * @param emailId - Email id is used to fetch employee from the DB.
     * @param active  - active flag is to enable or disable the employee.
     * @throws IllegalStateException
     */
    @Override
    public void enableOrDisableEmployee(final String emailId, final boolean active)
        throws IllegalStateException {
        UserModel userModel = getUserService().getUserForUID(emailId);
        if (userModel instanceof EmployeeModel employeeModel) {
            employeeModel.setLoginDisabled(!active);
            employeeModel.setBackOfficeLoginDisabled(!active);
            getModelService().save(employeeModel);
        } else {
            throw new IllegalStateException(String.format(USER_NOT_FOUND, emailId));
        }

    }

    /**
     * @return List of CustomerData
     * @throws IllegalStateException
     */
    public List<CustomerData> getAllPartnerEmployees() throws IllegalStateException {
        List<PartnerEmployeeModel> partnerEmployeeModels = getPartnerUserService().getAllPartnerEmployees();
        return Converters.convertAll(partnerEmployeeModels, partnerEmployeeConverter);

    }

    /**
     * This method refreshes the b2b site list of the user
     *
     * @param sites
     */
    @Override
    public void updateSites(final List<IbmPartnerB2BUnitData> sites) {
        final UserModel currentUser = getUserService().getCurrentUser();
        List<IbmPartnerB2BUnitData> activeSites;
        if (currentUser instanceof B2BCustomerModel b2bCustomerModel) {
            boolean isCreateDefaultB2BUnit = true;
            Set<PrincipalGroupModel> groups = b2bCustomerModel.getGroups().stream()
                .filter(group -> !(group instanceof B2BUnitModel))
                .collect(Collectors.toSet());
            if (getConfigurationService().getConfiguration()
                .getBoolean(PartnercoreConstants.COUNTRY_ROLLOUT_FEATURE_FLAG, false)) {
                activeSites = filterActiveSites(sites);
            } else {
                activeSites = sites;
            }
            if (CollectionUtils.isNotEmpty(activeSites)) {
                List<B2BUnitModel> latestB2BUnits = activeSites.stream()
                    .map(site -> getPartnerB2BUnitFacade().getOrCreate(site))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
                if (!latestB2BUnits.isEmpty()) {
                    isCreateDefaultB2BUnit = false;
                    groups.addAll(latestB2BUnits);
                    getB2BUnitService().setDefaultB2BUnit(b2bCustomerModel, latestB2BUnits);
                }
            }
            b2bCustomerModel.setGroups(groups);
            if (isCreateDefaultB2BUnit) {
                b2bCustomerModel.setDefaultB2BUnit(
                    getPartnerB2BUnitFacade().getUnitByUid(getDefaultPartnerB2BUnitId(),
                        Boolean.TRUE));
            }
            getModelService().save(b2bCustomerModel);
        }
    }


    /**
     * Decodes the provided JWT token using the PartnerUserService. If the decoding fails due to an
     * expired or invalid public key, it retries after refreshing the key cache.
     *
     * @param token the JWT token to decode
     * @return an instance of {@link IbmPartnerSSOUserTokenInboundData} if decoding is successful;
     * otherwise, returns null
     */
    @Override
    public IbmPartnerSSOUserTokenInboundData getDecodedToken(final String token) {
        DecodedJWT decodedJWT = getPartnerUserService().getDecodedJwtToken(token, Boolean.FALSE);
        if (decodedJWT == null) {
            // In case of Public key has expired. It will fail to decode the token. Hence we will remove key from cache, fetch again and decode token.
            decodedJWT = getPartnerUserService().getDecodedJwtToken(token, Boolean.TRUE);
        }
        if (decodedJWT == null) {
            // if still token is null then, it means Token is invalid.
            return null;
        }

        if (Boolean.FALSE.equals(getConfigurationService().getConfiguration()
            .getBoolean(PartnercoreConstants.FLAG_PARTNER_USER_DECODED_TOKEN_PAYLOAD_DISABLED,
                Boolean.TRUE))) {
            if (Objects.nonNull(decodedJWT.getPayload())) {
                String decodedPayload = new String(
                    Base64.getUrlDecoder().decode(decodedJWT.getPayload()),
                    StandardCharsets.UTF_8
                );
                LOG.info(PartnercoreConstants.LOG_PARTNER_LOGIN_DECODED_TOKEN_PAYLOAD,
                    decodedPayload);
            }
        }
        return getSsoTokenConverter().convert(decodedJWT);
    }


    /**
     * Creates a new PartnerB2BCustomerModel or updates an existing one based on the provided
     * {@link IbmPartnerSSOUserTokenInboundData}. If the user has a valid role, the token is
     * converted into registration data and saved. If the user exists but does not have a valid
     * role, the user is disabled. Throws an exception if the user does not exist and lacks a valid
     * role.
     *
     * @param token the decoded token containing user information
     * @throws RuntimeException if the user data type is invalid or token is invalid with no
     *                          existing user
     */
    @Override
    public void createOrUpdate(final IbmPartnerSSOUserTokenInboundData token) {
        UserModel userModel = getUser(token);
        final boolean hasValidRole = hasValidRole(token);
        if (hasValidRole) {
            LOG.info(PartnercoreConstants.LOG_VALID_ROLE_FOR_IUI_USER,
                token.getUniqueSecurityName());
            final PartnerB2BRegistrationData registrationData = getSsoTokenToB2BRegistrationConverter().convert(
                token);
            if (userModel == null) {
                LOG.info(PartnercoreConstants.LOG_NEW_CUSTOMER_REGISTRATION,
                    token.getUniqueSecurityName());
                userModel = getRegistrationReverseConverter().convert(registrationData);
            } else if (userModel instanceof PartnerB2BCustomerModel partnerB2BCustomerModel) {
                LOG.info(PartnercoreConstants.LOG_UPDATE_EXISTING_CUSTOMER_DETAILS,
                    token.getUniqueSecurityName());
                userModel = getRegistrationReverseConverter().convert(registrationData,
                    partnerB2BCustomerModel);
            } else {
                throw new RuntimeException(
                    String.format(PartnercoreConstants.EXP_EXISTING_CUSTOMER_INCORRECT_TYPE,
                        token.getUniqueSecurityName()));
            }
            getModelService().save(userModel);
            LOG.info(PartnercoreConstants.LOG_USER_UPDATE_SUCCESSFUL,
                token.getUniqueSecurityName());
        } else if (userModel != null) {
            LOG.info(PartnercoreConstants.LOG_CUSTOMER_EXISTS_INVALID_ROLE,
                token.getUniqueSecurityName());
            getPartnerUserService().enableOrDisableB2BCustomer(userModel, Boolean.FALSE);
        } else {
            throw new RuntimeException(String.format(
                PartnercoreConstants.EXP_CUSTOMER_NOT_EXISTS_INVALID_ROLE,
                token.getUniqueSecurityName()));
        }
    }

    /**
     * Updates the last login timestamp for the user with the given UID.
     *
     * @param uid the user UID
     */
    @Override
    public void updateLastLogin(String uid) {
        if (StringUtils.isNotBlank(uid)) {
            final UserModel currentUser = getUser(uid);
            if (Objects.nonNull(currentUser)) {
                currentUser.setLastLogin(new Date());
                getModelService().save(currentUser);
            }
        }
    }


    /**
     * Retrieves the {@link UserModel} for a given SSO token. It first attempts to fetch the user by
     * `uniqueSecurityName`. If not found, it tries with `preferredUserName`.
     *
     * @param token the token containing identifiers for the user
     * @return the matched {@link UserModel}, or null if not found
     */
    protected UserModel getUser(IbmPartnerSSOUserTokenInboundData token) {
        LOG.info(PartnercoreConstants.LOG_FETCH_EXISTING_USER,
            token.getUniqueSecurityName());
        UserModel userModel = getUser(token.getUniqueSecurityName());
        if (userModel == null) {
            //May be user is legacy created with preferred User Name.
            userModel = getUser(token.getPreferredUserName());
        }

        if (Objects.nonNull(userModel)) {
            LOG.info(String.format(PartnercoreConstants.LOG_USER_EXISTS_FOR_IUI, userModel.getPk(),
                token.getUniqueSecurityName()));
        } else {
            LOG.info(PartnercoreConstants.LOG_USER_NOT_EXISTS_FOR_IUI,
                token.getUniqueSecurityName());
        }

        return userModel;
    }

    /**
     * Retrieves the {@link UserModel} by UID using {@link PartnerUserService}.
     *
     * @param uid the user ID to search for
     * @return the {@link UserModel} if found, otherwise null
     */
    protected UserModel getUser(String uid) {
        String userUid = uid.toLowerCase();
        try {
            return getPartnerUserService().getUserForUID(userUid);
        } catch (UnknownIdentifierException ex) {
            LOG.error(PartnercoreConstants.USER_NOT_EXISTS, userUid);
        }
        return null;
    }

    /**
     * Validates whether the user token contains a valid role based on predefined roles from the
     * configuration.
     *
     * @param token the user token containing partner world and role information
     * @return true if the user has a valid role; false otherwise
     */
    protected boolean hasValidRole(IbmPartnerSSOUserTokenInboundData token) {
        if (token.getPartnerWorld() == null || CollectionUtils.isEmpty(
            token.getPartnerWorld().getEnterprises())) {
            return false;
        }

        return token.getPartnerWorld().getEnterprises().stream().anyMatch(
            enterprise -> CollectionUtils.isNotEmpty(enterprise.getCountryEnterprises())
                && enterprise.getCountryEnterprises().stream().anyMatch(
                countryEnterprise -> CollectionUtils.isNotEmpty(countryEnterprise.getRoles())
                    && countryEnterprise.getRoles().stream()
                    .anyMatch(role -> getUserValidRole().equals(role.getRoleAPIName()))));
    }

    /**
     * This method filter the Active sites based on its type
     *
     * @param sites sites to be filtered
     * @return sites filtered sites
     */
    public List<IbmPartnerB2BUnitData> filterActiveSites(List<IbmPartnerB2BUnitData> sites) {
        List<IbmPartnerB2BUnitData> tobeFilteredSite = new ArrayList<>();
        List<String> isoCurrencyCodes = new ArrayList<>();
        List<String> isoCountryCodes = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(sites)) {
            for (IbmPartnerB2BUnitData site : sites) {
                if (Objects.nonNull(site.getType()) && StringUtils.isNotBlank(
                    site.getType().getCode())) {
                    tobeFilteredSite.add(site);
                    if (Objects.nonNull(site.getCountry()) && StringUtils.isNotBlank(
                        site.getCountry().getIsocode())) {
                        isoCountryCodes.add(site.getCountry().getIsocode());
                    }
                    if (!IbmPartnerB2BUnitType.RESELLER_TIER_2.getCode()
                        .equalsIgnoreCase(site.getType().getCode()) && Objects.nonNull(
                        site.getCurrency())
                        && StringUtils.isNotBlank(site.getCurrency().getIsocode())) {
                        isoCurrencyCodes.add(site.getCurrency().getIsocode());
                    }
                }
            }
        }

        if (CollectionUtils.isNotEmpty(tobeFilteredSite)) {
            List<String> activeCountryCodes = getCountryActiveCodes(isoCountryCodes);
            List<String> activeCurrencyCodes = getCurrencyActiveCodes(isoCurrencyCodes);

            //Filter Active Site as per the type
            List<IbmPartnerB2BUnitData> removeSites = filterSiteByType(tobeFilteredSite,
                activeCountryCodes, activeCurrencyCodes);
            sites.removeAll(removeSites);
        }
        return sites;

    }

    /**
     * this method will fetch all the nonactive sites to be removed
     *
     * @param sites               sites to be filtered
     * @param activeCountryCodes  active country codes
     * @param activeCurrencyCodes active currency codes
     * @return toBeRemoved sites to be removed
     */
    public List<IbmPartnerB2BUnitData> filterSiteByType(List<IbmPartnerB2BUnitData> sites,
        List<String> activeCountryCodes,
        List<String> activeCurrencyCodes) {
        List<IbmPartnerB2BUnitData> toBeRemoved = new ArrayList<>();
        for (IbmPartnerB2BUnitData site : sites) {
            String type = null != site.getType() ? site.getType().getCode() : null;
            String countryIsoCode =
                null != site.getCountry() ? site.getCountry().getIsocode() : null;
            String currencyIsoCode =
                null != site.getCurrency() ? site.getCurrency().getIsocode() : null;
            if (PartnerB2BUnitUtils.notActiveSiteByType(countryIsoCode, currencyIsoCode, type,
                activeCountryCodes,
                activeCurrencyCodes)) {
                toBeRemoved.add(site);
                LOG.debug("the site is not active :" + site.getUid());
            }
        }
        return toBeRemoved;
    }

    /**
     * To fetch the active country for ISO or SAP code
     *
     * @param codeOrSapCode list of iso/sap code
     * @return active countries from database
     */
    public List<String> getCountryActiveCodes(final List<String> codeOrSapCode) {
        List<CountryModel> activeCountries = countryService.getActiveCountriesByCodeOrSapCode(
            codeOrSapCode);
        return PartnerB2BUnitUtils.getCountrySapAndIsoCode(
            activeCountries);
    }

    /**
     * To fetch the active currency for ISO or SAP code
     *
     * @param codeOrSapCode list of iso/sap code
     * @return active currencies from database
     */
    public List<String> getCurrencyActiveCodes(final List<String> codeOrSapCode) {
        List<CurrencyModel> activeCurrencies = currencyService.getActiveCurrencies(
            codeOrSapCode);
        return PartnerB2BUnitUtils.getCurrencySapAndIsoCode(
            activeCurrencies);
    }

    public PartnerB2BUnitFacade getPartnerB2BUnitFacade() {
        return partnerB2BUnitFacade;
    }

    public void setPartnerB2BUnitFacade(PartnerB2BUnitFacade partnerB2BUnitFacade) {
        this.partnerB2BUnitFacade = partnerB2BUnitFacade;
    }

    public Converter<CustomerData, PartnerEmployeeModel> getPartnerEmployeeReverseConverter() {
        return partnerEmployeeReverseConverter;
    }

    public Converter<PartnerEmployeeModel, CustomerData> getPartnerEmployeeConverter() {
        return partnerEmployeeConverter;
    }

    public void setPartnerEmployeeReverseConverter(
        Converter<CustomerData, PartnerEmployeeModel> partnerEmployeeReverseConverter) {
        this.partnerEmployeeReverseConverter = partnerEmployeeReverseConverter;
    }

    public void setPartnerEmployeeConverter(
        Converter<PartnerEmployeeModel, CustomerData> partnerEmployeeConverter) {
        this.partnerEmployeeConverter = partnerEmployeeConverter;
    }

    public PartnerUserService getPartnerUserService() {
        return partnerUserService;
    }

    public void setPartnerUserService(PartnerUserService partnerUserService) {
        this.partnerUserService = partnerUserService;
    }

    public PartnerB2BUnitService<B2BUnitModel, B2BCustomerModel> getB2BUnitService() {
        return b2BUnitService;
    }

    public void setB2BUnitService(
        PartnerB2BUnitService<B2BUnitModel, B2BCustomerModel> b2BUnitService) {
        this.b2BUnitService = b2BUnitService;
    }

    public String getDefaultPartnerB2BUnitId() {
        return defaultPartnerB2BUnitId;
    }

    /**
     * @return countryService
     */
    public PartnerCountryService getCountryService() {
        return countryService;
    }

    /**
     * @return currencyService
     */
    public PartnerCurrencyService getCurrencyService() {
        return currencyService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public Converter<DecodedJWT, IbmPartnerSSOUserTokenInboundData> getSsoTokenConverter() {
        return ssoTokenConverter;
    }

    public Converter<IbmPartnerSSOUserTokenInboundData, PartnerB2BRegistrationData> getSsoTokenToB2BRegistrationConverter() {
        return ssoTokenToB2BRegistrationConverter;
    }

    public Converter<B2BRegistrationData, PartnerB2BCustomerModel> getRegistrationReverseConverter() {
        return registrationReverseConverter;
    }

    public String getUserValidRole() {
        return userValidRole;
    }
}