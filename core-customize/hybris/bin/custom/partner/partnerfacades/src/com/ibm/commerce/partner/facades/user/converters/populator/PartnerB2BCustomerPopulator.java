package com.ibm.commerce.partner.facades.user.converters.populator;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.company.data.IbmPartnerB2BUnitData;
import com.ibm.commerce.partner.core.b2b.company.service.PartnerB2BUnitService;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.IbmB2BUnitModel;
import com.ibm.commerce.partner.core.model.IbmPartnerB2BUnitModel;
import com.ibm.commerce.partner.enums.data.DisplayTypeData;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.session.SessionService;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Populates {@link CustomerData} from {@link B2BCustomerModel}
 */
public class PartnerB2BCustomerPopulator implements Populator<CustomerModel, CustomerData> {

    private final Converter<IbmB2BUnitModel, IbmPartnerB2BUnitData> b2bUnitDataConverter;
    private final String defaultPartnerB2BUnitId;
    private final SessionService sessionService;
    private static final String SESSION_ATTR_CUSTOMER_CEID = "CEID";
    private final PartnerB2BUnitService<B2BUnitModel, B2BCustomerModel> b2BUnitService;
    private ConfigurationService configurationService;


    public PartnerB2BCustomerPopulator(
        final Converter<IbmB2BUnitModel, IbmPartnerB2BUnitData> b2bUnitDataConverter,
        final String defaultPartnerB2BUnitId, final SessionService sessionService,
        final PartnerB2BUnitService<B2BUnitModel, B2BCustomerModel> b2BUnitService,
        final ConfigurationService configurationService) {
        this.b2bUnitDataConverter = b2bUnitDataConverter;
        this.defaultPartnerB2BUnitId = defaultPartnerB2BUnitId;
        this.sessionService = sessionService;
        this.b2BUnitService = b2BUnitService;
        this.configurationService = configurationService;
    }


    @Override
    public void populate(final CustomerModel customerModel, final CustomerData customerData)
        throws ConversionException {
        final B2BCustomerModel b2BCustomerModel = (B2BCustomerModel) customerModel;
        if (isCountryRolloutEnabled()) {
            getB2BUnitService().setActiveSitesToCustomer(b2BCustomerModel, defaultPartnerB2BUnitId,
                isResellerTier1Enabled());
        } else if (!isResellerTier1Enabled()) {
            getB2BUnitService().setNonTier1SitesToCustomer(b2BCustomerModel,
                defaultPartnerB2BUnitId);
        }

        final String siteCeid = sessionService.getAttribute(SESSION_ATTR_CUSTOMER_CEID);
        final List<String> ceidList =
            StringUtils.isNotEmpty(siteCeid) ? Arrays.asList(siteCeid.split(","))
                : Collections.emptyList();
        if (CollectionUtils.isNotEmpty(ceidList)) {
            if (b2BCustomerModel.getDefaultB2BUnit() != null
                && !b2BCustomerModel.getDefaultB2BUnit().getGroups().isEmpty()) {

                List<IbmPartnerB2BUnitModel> unitModels = b2BCustomerModel.getDefaultB2BUnit()
                    .getGroups().stream()
                    .filter(parentb2bModel -> parentb2bModel instanceof IbmB2BUnitModel
                        && ceidList.stream()
                        .anyMatch(ceid -> ceid.equalsIgnoreCase(parentb2bModel.getUid())))
                    .map(
                        parentb2bModel -> (IbmPartnerB2BUnitModel) b2BCustomerModel.getDefaultB2BUnit())
                    .toList();

                setSitesToCustomer(customerData, unitModels);
            }
            List<IbmPartnerB2BUnitModel> unitModels = b2BCustomerModel.getGroups().stream()
                .filter(IbmPartnerB2BUnitModel.class::isInstance)
                .filter(unit -> unit.getGroups().stream()
                    .anyMatch(parentUid -> ceidList.stream()
                        .anyMatch(ceid -> ceid.equalsIgnoreCase(parentUid.getUid()))))
                .map(IbmPartnerB2BUnitModel.class::cast)
                .toList();
            setSitesToCustomer(customerData, unitModels);

            if (isVadCeidPrmEnabled() && CollectionUtils.isNotEmpty(customerData.getSites())) {
                List<IbmPartnerB2BUnitData> sortedSites = customerData.getSites().stream()
                    .sorted(Comparator.comparing(
                        site -> !PartnercoreConstants.DISTRIBUTOR_CPQ.equalsIgnoreCase(
                            Optional.ofNullable(site.getType())
                                .map(DisplayTypeData::getCode)
                                .orElse("")
                        )
                    ))
                    .toList();
                customerData.setSites(sortedSites);
            }
        }
        if (CollectionUtils.isEmpty(ceidList)) {
            List<IbmPartnerB2BUnitModel> unitModels = customerModel.getGroups().stream().
                filter(IbmPartnerB2BUnitModel.class::isInstance).
                filter(unit -> !getDefaultPartnerB2BUnitId().equalsIgnoreCase(unit.getUid())).
                map(IbmPartnerB2BUnitModel.class::cast).toList();
            setSitesToCustomer(customerData, unitModels);

        }
    }

    private void setSitesToCustomer(CustomerData customerData,
        List<IbmPartnerB2BUnitModel> unitModels) {
        if (CollectionUtils.isNotEmpty(unitModels)) {
            customerData.setSites(getB2bUnitDataConverter().convertAll(unitModels));
        }
    }

    public Converter<IbmB2BUnitModel, IbmPartnerB2BUnitData> getB2bUnitDataConverter() {
        return b2bUnitDataConverter;
    }

    public String getDefaultPartnerB2BUnitId() {
        return defaultPartnerB2BUnitId;
    }

    /**
     * @return b2BUnitService
     */
    public PartnerB2BUnitService<B2BUnitModel, B2BCustomerModel> getB2BUnitService() {
        return b2BUnitService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public boolean isResellerTier1Enabled() {
        return getConfigurationService().getConfiguration()
            .getBoolean(PartnercoreConstants.TIER_1_RESLLER_FEATURE_FLAG, false);
    }

    public boolean isVadCeidPrmEnabled() {
        return !getConfigurationService().getConfiguration()
            .getBoolean(PartnercoreConstants.VAD_CEID_PRM_DISABLE, true);
    }

    public boolean isCountryRolloutEnabled() {
        return getConfigurationService().getConfiguration()
            .getBoolean(PartnercoreConstants.COUNTRY_ROLLOUT_FEATURE_FLAG, false);
    }

}
