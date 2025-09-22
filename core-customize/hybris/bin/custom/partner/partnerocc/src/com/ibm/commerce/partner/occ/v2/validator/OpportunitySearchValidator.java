package com.ibm.commerce.partner.occ.v2.validator;

import com.ibm.commerce.partner.core.b2b.company.service.PartnerB2BUnitService;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partnerwebservicescommons.dto.opportunity.OpportunitySearchRequestWsDTO;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import java.util.Objects;
import javax.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class OpportunitySearchValidator implements Validator {


    final PartnerB2BUnitService partnerB2BUnitService;

    private ConfigurationService configurationService;


    OpportunitySearchValidator(final PartnerB2BUnitService partnerB2BUnitService,
        ConfigurationService configurationService) {
        this.partnerB2BUnitService = partnerB2BUnitService;
        this.configurationService = configurationService;
    }

    @Override
    public boolean supports(final Class<?> arg0) {
        return String.class.isAssignableFrom(arg0);
    }


    @Override
    public void validate(final Object target, final Errors errors) {

        if (target instanceof OpportunitySearchRequestWsDTO opportunitySearchRequestWsDTO) {

            if (StringUtils.isEmpty(opportunitySearchRequestWsDTO.getCustomerICN())) {
                errors.rejectValue(PartnercoreConstants.CUSTOMER_ID, null,
                    PartnercoreConstants.CUSTOMER_NULL_ERROR);
            }
            if (StringUtils.isEmpty(opportunitySearchRequestWsDTO.getResellerID())) {
                errors.rejectValue(PartnercoreConstants.RESELLER_ID, null,
                    PartnercoreConstants.RESELLER_NULL_ERROR);
            }
            if (!isDistributorFeatureDisabled()) { // validate only when distributor feature is enabled
                final String distributorID = opportunitySearchRequestWsDTO.getDistributorID();

                if (StringUtils.isEmpty(distributorID)) {
                    errors.rejectValue(
                        PartnercoreConstants.DISTRIBUTOR_ID,
                        null,
                        PartnercoreConstants.DISTRIBUTER_NULL_ERROR
                    );
                    return;
                }

                final B2BUnitModel distributorUnit =
                    (B2BUnitModel) partnerB2BUnitService.getUnitForUid(distributorID, Boolean.TRUE);

                if (Objects.isNull(distributorUnit) ) {
                    errors.rejectValue(
                        PartnercoreConstants.DISTRIBUTOR_ID,
                        null,
                        PartnercoreConstants.OPPORTUNITY_DISTRIBUTOR_NULL_ERROR
                    );
                    return;
                }

                // Verify distributor is active if the active-feature check is enabled
                if (!verifyActiveFeatureDisabled() && !partnerB2BUnitService.isActive(
                    distributorUnit)) {
                    errors.rejectValue(
                        PartnercoreConstants.DISTRIBUTOR_ID,
                        null,
                        PartnercoreConstants.OPPORTUNITY_VALIDATION_ERROR
                    );
                }
            }
            final String resellerID = opportunitySearchRequestWsDTO.getResellerID();
            if (StringUtils.isNotEmpty(resellerID)) {
                final B2BUnitModel resellerUnit =
                    (B2BUnitModel) partnerB2BUnitService.getUnitForUid(resellerID, Boolean.TRUE);

                if (Objects.isNull(resellerUnit)) {
                    errors.rejectValue(
                        PartnercoreConstants.RESELLER_ID,
                        null,
                        PartnercoreConstants.OPPORTUNITY_RESELLER_NULL_ERROR
                    );
                    return;
                }

                // Verify reseller is active if the active-feature check is enabled
                if (!verifyActiveFeatureDisabled() && !partnerB2BUnitService.isActive(
                    resellerUnit)) {
                    errors.rejectValue(
                        PartnercoreConstants.RESELLER_ID,
                        null,
                        PartnercoreConstants.OPPORTUNITY_RESELLER_VALIDATION_ERROR
                    );
                }
            }
        }
    }

    /**
     * Checks whether the distributor feature is disabled.
     * <p>
     * Reads the configuration property defined by
     * {@link PartnercoreConstants#OPPORTUNITY_DISTRIBUTOR_DISABLED_FEATURE_FLAG}. If the property
     * is not set, defaults to {@code true} (feature considered disabled).
     *
     * @return {@code true} if the distributor feature is disabled, {@code false} otherwise
     */
    protected boolean isDistributorFeatureDisabled() {
        return getConfigurationService().getConfiguration()
            .getBoolean(PartnercoreConstants.OPPORTUNITY_DISTRIBUTOR_DISABLED_FEATURE_FLAG,
                Boolean.TRUE);
    }

    /**
     * Checks whether the 'active feature' validation is disabled via configuration.
     *
     * @return true if the active validation feature is disabled, otherwise false
     */
    protected boolean verifyActiveFeatureDisabled() {
        return getConfigurationService().getConfiguration()
            .getBoolean(PartnercoreConstants.OPPORTUNITY_SITEID_ACTIVE_CHECK_DISABLED_FEATURE_FLAG,
                Boolean.TRUE);
    }

    public PartnerB2BUnitService getPartnerB2BUnitService() {
        return partnerB2BUnitService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }
}
