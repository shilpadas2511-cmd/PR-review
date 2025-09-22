package com.ibm.commerce.partner.facades.oauth2.prm.converter.populators;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.sso.prm.data.IbmPartnerSSOUserTokenCountryEnterpriseInboundData;
import com.ibm.commerce.partner.sso.prm.data.IbmPartnerSSOUserTokenEnterpriseInboundData;
import com.ibm.commerce.partner.sso.prm.data.IbmPartnerSSOUserTokenInboundData;
import com.ibm.commerce.partner.sso.prm.data.IbmPartnerSSOUserTokenPartnerWorldInboundData;
import com.ibm.commerce.partner.sso.prm.data.IbmPartnerSSOUserTokenRolesInboundData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

/**
 * Populator that maps enterprise, country enterprise, and role information from a decoded JWT into
 * an {@link IbmPartnerSSOUserTokenInboundData} object.
 * <p>
 * This includes extracting and converting data for PartnerWorld, World Wide Enterprises, Country
 * Enterprises, and user roles into their respective model representations.
 */
public class PartnerSsoTokenEnterprisesPopulator implements
    Populator<DecodedJWT, IbmPartnerSSOUserTokenInboundData> {

    private static final String JWT_ENTERPRISE_ID = "wwentId";
    private static final String JWT_COUNTRY_ENTERPRISE_COUNTRY_CODE = "countryCode";
    private static final String JWT_COUNTRY_ENTERPRISE_CEID = "ceId";
    private static final String JWT_COUNTRY_ENTERPRISE_COMPANY_NAME = "companyName";
    private static final String JWT_ROLE_API_NAME = "roleAPIName";
    private static final String JWT_ROLE_TYPE_NAME = "roleTypeName";
    private static final String JWT_ROLE_TYPE = "roleType";
    private static final String JWT_ROLE_GROUP = "roleGroup";
    private static final String JWT_SCOPE = "scope";

    /**
     * Populates enterprise-related data from the decoded JWT into the target object.
     *
     * @param source the decoded JWT containing enterprise-related claims
     * @param target the target {@link IbmPartnerSSOUserTokenInboundData} to be populated
     * @throws ConversionException in case of errors during population
     */
    @SuppressWarnings("unchecked")
    @Override
    public void populate(DecodedJWT source, IbmPartnerSSOUserTokenInboundData target)
        throws ConversionException {

        Map<String, Object> partnerWorldMap = source.getClaim(PartnercoreConstants.JWT_PARTNERWORLD)
            .asMap();

        if (MapUtils.isEmpty(partnerWorldMap)) {
            return;
        }
        IbmPartnerSSOUserTokenPartnerWorldInboundData partnerWorld = new IbmPartnerSSOUserTokenPartnerWorldInboundData();
        final List<Map<String, Object>> enterprises = (List<Map<String, Object>>) partnerWorldMap.get(
            PartnercoreConstants.JWT_WWENTERPRISES);
        partnerWorld.setEnterprises(getEnterprises(enterprises));
        target.setPartnerWorld(partnerWorld);
    }


    /**
     * Converts enterprise maps into enterprise model objects.
     *
     * @param enterprises list of enterprise maps
     * @return list of {@link IbmPartnerSSOUserTokenEnterpriseInboundData}
     */
    @SuppressWarnings("unchecked")
    protected List<IbmPartnerSSOUserTokenEnterpriseInboundData> getEnterprises(
        List<Map<String, Object>> enterprises) {
        if (CollectionUtils.isEmpty(enterprises)) {
            return Collections.emptyList();
        }
        return enterprises.stream().map(enterpriseMap -> {
            IbmPartnerSSOUserTokenEnterpriseInboundData enterprise = new IbmPartnerSSOUserTokenEnterpriseInboundData();
            enterprise.setId(enterpriseMap.get(JWT_ENTERPRISE_ID).toString());
            final List<Map<String, Object>> countryEnterprises = (List<Map<String, Object>>) enterpriseMap.get(
                PartnercoreConstants.JWT_COUNTRYENTERPRISES);
            enterprise.setCountryEnterprises(getCountryEnterprises(countryEnterprises));
            return enterprise;
        }).toList();
    }


    /**
     * Converts country enterprise maps into country enterprise model objects.
     *
     * @param countryEnterprises list of country enterprise maps
     * @return list of {@link IbmPartnerSSOUserTokenCountryEnterpriseInboundData}
     */
    @SuppressWarnings("unchecked")
    protected List<IbmPartnerSSOUserTokenCountryEnterpriseInboundData> getCountryEnterprises(
        List<Map<String, Object>> countryEnterprises) {
        if (CollectionUtils.isEmpty(countryEnterprises)) {
            return Collections.emptyList();
        }
        return countryEnterprises.stream().map(countryEnterprise -> {
            IbmPartnerSSOUserTokenCountryEnterpriseInboundData countryEnterpriseInboundData = new IbmPartnerSSOUserTokenCountryEnterpriseInboundData();
            final List<Map<String, Object>> roles = (List<Map<String, Object>>) countryEnterprise.get(
                PartnercoreConstants.JWT_ROLES);
            countryEnterpriseInboundData.setRoles(getRoles(roles));
            countryEnterpriseInboundData.setCountryCode(
                countryEnterprise.get(JWT_COUNTRY_ENTERPRISE_COUNTRY_CODE).toString());
            countryEnterpriseInboundData.setCeid(
                countryEnterprise.get(JWT_COUNTRY_ENTERPRISE_CEID).toString());
            countryEnterpriseInboundData.setCompanyName(
                countryEnterprise.get(JWT_COUNTRY_ENTERPRISE_COMPANY_NAME).toString());
            return countryEnterpriseInboundData;
        }).toList();
    }


    /**
     * Converts role maps into role model objects.
     *
     * @param roles list of role maps
     * @return list of {@link IbmPartnerSSOUserTokenRolesInboundData}
     */
    protected List<IbmPartnerSSOUserTokenRolesInboundData> getRoles(
        List<Map<String, Object>> roles) {
        if (CollectionUtils.isEmpty(roles)) {
            return Collections.emptyList();
        }
        return roles.stream().map(roleMap -> {
            IbmPartnerSSOUserTokenRolesInboundData role = new IbmPartnerSSOUserTokenRolesInboundData();
            role.setRoleAPIName(roleMap.get(JWT_ROLE_API_NAME).toString());
            role.setRoleTypeName(roleMap.get(JWT_ROLE_TYPE_NAME).toString());
            role.setRoleGroup(JWT_ROLE_GROUP);
            role.setScope(roleMap.get(JWT_SCOPE).toString());
            role.setRoleType(roleMap.get(JWT_ROLE_TYPE).toString());
            return role;
        }).toList();
    }
}
