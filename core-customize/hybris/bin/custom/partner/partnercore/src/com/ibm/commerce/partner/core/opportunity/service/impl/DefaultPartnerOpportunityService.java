package com.ibm.commerce.partner.core.opportunity.service.impl;

import com.ibm.commerce.partner.core.b2b.company.service.PartnerB2BUnitService;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.exceptions.IbmWebServiceFailureException;
import com.ibm.commerce.partner.core.model.IbmPartnerB2BUnitModel;
import com.ibm.commerce.partner.core.model.IbmPartnerOpportunityModel;
import com.ibm.commerce.partner.core.opportunity.dao.PartnerOpportunityDao;
import com.ibm.commerce.partner.core.opportunity.data.response.OpportunityDetailsSearchResponseData;
import com.ibm.commerce.partner.core.opportunity.service.PartnerOpportunityOutboundService;
import com.ibm.commerce.partner.core.opportunity.service.PartnerOpportunityService;
import com.ibm.commerce.partner.core.user.service.PartnerUserService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.Collections;
import java.util.List;


import java.util.Objects;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;


/**
 * Implementation for {@link PartnerOpportunityService}
 */
public class DefaultPartnerOpportunityService implements PartnerOpportunityService {

    private final String RESELLER_TYPE = "RESELLER";
    private final String DISTRIUTOR_TYPE = "DISTRIBUTOR";


    static final Logger LOG = Logger.getLogger(DefaultPartnerOpportunityService.class);


    private final PartnerOpportunityDao opportunityDao;
    private final PartnerOpportunityOutboundService opportunityOutboundService;
    private final ModelService modelService;
    private final PartnerB2BUnitService<B2BUnitModel, B2BCustomerModel> b2BUnitService;
    private final PartnerUserService userService;
    private final ConfigurationService configurationService;
    private final PartnerB2BUnitService partnerB2BUnitService;

    public DefaultPartnerOpportunityService(final PartnerOpportunityDao opportunityDao,
        final PartnerOpportunityOutboundService opportunityOutboundService,
        final ModelService modelService,
        final PartnerB2BUnitService<B2BUnitModel, B2BCustomerModel> b2BUnitService,
        PartnerUserService userService,
        final ConfigurationService configurationService,
        final PartnerB2BUnitService partnerB2BUnitService) {
        this.opportunityDao = opportunityDao;
        this.opportunityOutboundService = opportunityOutboundService;
        this.modelService = modelService;
        this.b2BUnitService = b2BUnitService;
        this.userService = userService;
        this.configurationService = configurationService;
        this.partnerB2BUnitService = partnerB2BUnitService;
    }

    @Override
    public IbmPartnerOpportunityModel get(final String opportunityId) {
        return getOpportunityDao().fetch(opportunityId);
    }


    /**
     * Retrieve opportunity details from the Opportunity Service by validating the authentication
     * token. The opportunityValidatorStrategy will ensure that the resellerCEID belongs to the
     * logged-in user.
     *
     * @param distributorID
     * @param resellerID
     * @param customerICN
     * @return List<OpportunityDetailsResponseData>
     */
    public List<OpportunityDetailsSearchResponseData> getOpportunities(final String resellerID,
        final String distributorID, final String customerICN) {
        boolean isDistributorAssociated = false;
        final B2BUnitModel reseller = getUnit(resellerID);
        final String resellerCEID = getParentUid(reseller);

        String distributorCEID = null;
        List<OpportunityDetailsSearchResponseData> responseData;
        B2BUnitModel distributor = null;
        if (!isDistributorFeatureDisabled()) {
            distributor = getUnit(distributorID);
            if (getPartnerB2BUnitService().isUserAssociatedUnit(
                distributor)) {
                distributorCEID = getParentUid(distributor);
                isDistributorAssociated = true;
            }
        }

        try {
            responseData = getOpportunityByAuthToken(resellerCEID, distributorCEID, customerICN,
                isDistributorAssociated);
        } catch (final IbmWebServiceFailureException ex) {
            LOG.error(buildErrorMessage(resellerCEID, distributorCEID), ex.getCause());
            return Collections.emptyList();
        }
        if (!isDistributorFeatureDisabled() && CollectionUtils.isNotEmpty(responseData)) {
            if (isDistributorAssociated) {
                final String distributorCEIDFinal = distributorCEID;
                return responseData.stream()
                    .filter(
                        item -> Objects.equals(featureCheck(item.getResellerCEID()), resellerCEID)
                            && Objects.equals(featureCheck(item.getDistributorCEID()),
                            distributorCEIDFinal)
                            && Objects.equals(featureCheck(item.getCustomerNumber()), customerICN))
                    .toList();
            } else {
                return responseData.stream()
                    .filter(
                        item -> Objects.equals(featureCheck(item.getResellerCEID()), resellerCEID)
                            && Objects.equals(featureCheck(item.getCustomerNumber()), customerICN))
                    .toList();
            }
        } else {
            return responseData;
        }
    }


    /**
     * Retrieves a {@link B2BUnitModel} for the given unit ID.
     *
     * @param unitID the unique identifier of the B2B unit
     * @return an B2BUnitModel {@link } containing the unit if found, or empty if not available
     */
    protected B2BUnitModel getUnit(final String unitID) {
        return StringUtils.isNotEmpty(unitID)
            ? getB2BUnitService().getUnitForUid(unitID, Boolean.TRUE)
            : null;
    }

    /**
     * Retrieves the UID of the parent {@link B2BUnitModel} for the given unit.
     * <p>
     * If the unit is {@code null}, has an empty UID, or has no parent,
     * this method will return {@code null}.
     *
     * @param unit the {@link B2BUnitModel} whose parent UID should be retrieved
     * @return the UID of the parent unit, or {@code null} if not available
     */
    protected String getParentUid(final B2BUnitModel unit) {
        return (unit != null && StringUtils.isNotEmpty(unit.getUid())
            && getB2BUnitService().getParent(unit) != null)
            ? getB2BUnitService().getParent(unit).getUid()
            : null;
    }

    /**
     * Checks whether the distributor feature is disabled.
     * <p>
     * Reads the configuration property defined by
     * {@link PartnercoreConstants#OPPORTUNITY_DISTRIBUTOR_DISABLED_FEATURE_FLAG}.
     * If the property is not set, defaults to {@code true} (feature considered disabled).
     *
     * @return {@code true} if the distributor feature is disabled,
     *         {@code false} otherwise
     */
    protected boolean isDistributorFeatureDisabled() {
        return getConfigurationService().getConfiguration()
            .getBoolean(PartnercoreConstants.OPPORTUNITY_DISTRIBUTOR_DISABLED_FEATURE_FLAG, Boolean.TRUE);
    }

    /**
     * Builds an error message string for opportunity-related failures.
     * <p>
     * Always includes the {@code resellerCEID}. If {@code distributorCEID} is not empty,
     * it will also be appended; otherwise, nothing is added.
     *
     * @param resellerCEID    the reseller CEID (may be {@code null})
     * @param distributorCEID the distributor CEID (may be {@code null} or empty)
     * @return the formatted error message string
     */
    protected String buildErrorMessage(final String resellerCEID, final String distributorCEID) {
        return PartnercoreConstants.OPPORTUNITY_ERROR + resellerCEID +
            (StringUtils.isNotEmpty(distributorCEID) ? distributorCEID : StringUtils.EMPTY);
    }
    /**
     * The mock response is generated when the mock response flag is activated.
     *
     * @param opportunityNumber
     * @return List<OpportunityDetailsResponseData>
     */
    @Override
    public List<OpportunityDetailsSearchResponseData> fetchOpportunitiesByNumber(
        final String opportunityNumber) {
            try {
                return getOpportunityOutboundService().getOpportunitiesSearchByOpportunityNumber(
                    opportunityNumber, fetchResellerOrDistributorData(
                        (B2BCustomerModel) getUserService().getCurrentUser(), RESELLER_TYPE),
                    fetchResellerOrDistributorData(
                        (B2BCustomerModel) getUserService().getCurrentUser(), DISTRIUTOR_TYPE));
            } catch (final IbmWebServiceFailureException ex) {
                LOG.error(
                    "Unable to generate opportunities list for opportunityNumber :"
                        + opportunityNumber,
                    ex.getCause());
            }
        return Collections.emptyList();
    }

    /**
     * The mock response is generated when the mock response flag is activated.
     *
     * @param ownerMail
     * @return List<OpportunityDetailsResponseData>
     */
    @Override
    public List<OpportunityDetailsSearchResponseData> fetchOpportunitiesByOwnerMail(
        final String ownerMail) {
            try {
                return getOpportunityOutboundService().getOpportunitiesSearchByOwnerMail(
                    ownerMail, fetchResellerOrDistributorData(
                        (B2BCustomerModel) getUserService().getCurrentUser(), RESELLER_TYPE),
                    fetchResellerOrDistributorData(
                        (B2BCustomerModel) getUserService().getCurrentUser(), DISTRIUTOR_TYPE));
            } catch (final IbmWebServiceFailureException ex) {
                LOG.error(
                    "Unable to generate opportunities list for ownerMail :" + ownerMail,
                    ex.getCause());
            }
        return Collections.emptyList();
    }

    /**
     * The mock response is generated when the mock response flag is activated.
     *
     * @param customerNumber
     * @param countryCode
     * @return List<OpportunityDetailsResponseData>
     */
    @Override
    public List<OpportunityDetailsSearchResponseData> fetchOpportunitiesByCustomerNumber(
        final String customerNumber, final String countryCode) {
            try {
                return getOpportunityOutboundService().getOpportunitiesSearchByCustomerNumber(
                    customerNumber, fetchResellerOrDistributorData(
                        (B2BCustomerModel) getUserService().getCurrentUser(), RESELLER_TYPE),
                    fetchResellerOrDistributorData(
                        (B2BCustomerModel) getUserService().getCurrentUser(), DISTRIUTOR_TYPE));
            } catch (final IbmWebServiceFailureException ex) {
                LOG.error(
                    "Unable to generate opportunities list for customerNumber :" + customerNumber,
                    ex.getCause());
            }
        return Collections.emptyList();
    }

    /**
     * Validate the authToken by checking its expirationTs value. If the token is expired, update
     * the data object with a new bearer token and expiration timestamp. Then, pass the same token
     * to the opportunity service to retrieve the list of opportunities.
     *
     * @param resellerCEID
     * @param distributorCEID
     * @param customerICN
     * @param isDistributorAssociated
     * @return List<OpportunityDetailsResponseData>
     */
    protected List<OpportunityDetailsSearchResponseData> getOpportunityByAuthToken(
        final String resellerCEID, final String distributorCEID, final String customerICN,
        final boolean isDistributorAssociated) {

        return getOpportunityOutboundService().getOpportunities(resellerCEID, distributorCEID,
            customerICN, isDistributorAssociated);
    }

    /**
     * This method fetch reseller  and  distributor Ceid from current  logged-in user
     *
     * @param b2BCustomerModel
     * @param b2BUnitType
     * @return List<String>
     */
    protected List<String> fetchResellerOrDistributorData(final B2BCustomerModel b2BCustomerModel,
        final String b2BUnitType) {
        List<String> ceidList = b2BCustomerModel.getGroups().stream().filter(
                unit -> unit instanceof IbmPartnerB2BUnitModel b2bUnitModel
                    && (b2bUnitModel.getType().getCode().contains(b2BUnitType))).map(
                principalGroupModel -> getB2BUnitService().getParent(
                    (B2BUnitModel) principalGroupModel)).filter(Objects::nonNull)
            .map(B2BUnitModel::getUid).filter(Objects::nonNull).toList();
        return !ceidList.isEmpty() ? ceidList : Collections.emptyList();

    }

    /**
     * Checks whether the space check feature is disabled via configuration.
     *
     * @return true if the space check feature is disabled, otherwise false
     */
    protected boolean spaceCheckDisabled() {
        return getConfigurationService().getConfiguration()
            .getBoolean(PartnercoreConstants.OPPORTUNITY_SPACE_CHECK_DISABLED_FEATURE_FLAG, Boolean.TRUE);
    }

    /**
     * Normalizes a given string value based on the {@code spaceCheckDisabled()} flag.
     * <p>
     * - If the input value is {@code null}, this method returns {@code null}. <br>
     * - If {@code spaceCheckDisabled()} is {@code true}, the value is returned as-is. <br>
     * - Otherwise, the value is trimmed (leading and trailing whitespace removed). <br>
     * <p>
     * This method is typically used to ensure consistent string comparison
     * when matching CEIDs or customer identifiers, while avoiding {@link NullPointerException}.
     *
     * @param value the string to normalize (may be {@code null})
     * @return the normalized string (trimmed if required), or {@code null} if the input was {@code null}
     */
    protected String featureCheck(String value) {
        return !spaceCheckDisabled() && StringUtils.isNotBlank(value) ? value.trim() : value;
    }


    public ModelService getModelService() {
        return modelService;
    }

    public PartnerOpportunityOutboundService getOpportunityOutboundService() {
        return opportunityOutboundService;
    }

    public PartnerB2BUnitService<B2BUnitModel, B2BCustomerModel> getB2BUnitService() {
        return b2BUnitService;
    }

    public PartnerOpportunityDao getOpportunityDao() {
        return opportunityDao;
    }

    public PartnerUserService getUserService() {
        return userService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public PartnerB2BUnitService getPartnerB2BUnitService() {
        return partnerB2BUnitService;
    }
}
