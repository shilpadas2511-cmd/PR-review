package com.ibm.commerce.partner.facades.user.converters.populator;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.country.services.PartnerCountryService;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;


/**
 * Populates Address Street Details on Address Model
 */
public class AddressRegionDetailsReversePopulator implements Populator<AddressData, AddressModel> {

    private final CommonI18NService commonI18NService;
    private final ModelService modelService;
    private final PartnerCountryService partnerCountryService;
    private final ConfigurationService configurationService;
    private static final Logger LOG = LoggerFactory.getLogger(
        AddressRegionDetailsReversePopulator.class);

    public AddressRegionDetailsReversePopulator(final CommonI18NService commonI18NService,
        ModelService modelService, PartnerCountryService partnerCountryService,
        ConfigurationService configurationService) {
        this.commonI18NService = commonI18NService;
        this.modelService = modelService;
        this.partnerCountryService = partnerCountryService;
        this.configurationService = configurationService;
    }

    @Override
    public void populate(AddressData addressData, AddressModel addressModel)
        throws ConversionException {
        Assert.notNull(addressData, "Parameter addressData cannot be null.");
        Assert.notNull(addressModel, "Parameter addressModel cannot be null.");
        if (addressData.getRegion() == null || StringUtils.isBlank(
            addressData.getRegion().getIsocode())) {
            return;
        }
        final String isocode = addressData.getRegion().getIsocode();
        try {
            final RegionModel regionModel = getCommonI18NService().getRegion(
                addressModel.getCountry(), isocode);
            addressModel.setRegion(regionModel);
        } catch (final UnknownIdentifierException e) {
            if (!isRegionAutomationDisabled()) {
                final RegionModel region = createRegion(addressData);
                if (null != region) {
                    addressModel.setRegion(region);
                }
            } else {
                throw new ConversionException("No region with the code " + isocode + " found.",
                    e);
            }
        } catch (final AmbiguousIdentifierException e) {
            throw new ConversionException(
                "More than one region with the code " + isocode + " found.", e);
        }
    }


    /**
     * Creates a new {@link RegionModel} object for the country and region data.
     *
     * @return the newly created {@link RegionModel}
     */

    public RegionModel createRegion(AddressData addressData) {
        Assert.notNull(addressData.getRegion(), "regionData can not be null");
        CountryData countryData = addressData.getCountry();
        RegionData regionData = addressData.getRegion();
        String regionName = addressData.getTown();
        RegionModel createRegion = null;
        try {
            CountryModel country =
                null != countryData.getIsocode() ? getPartnerCountryService().getByCodeOrSapCode(
                    countryData.getIsocode()) : null;
            if (null != country) {
                createRegion = getModelService().create(RegionModel.class);
                createRegion.setIsocode(regionData.getIsocode());
                createRegion.setName(regionName);
                createRegion.setCountry(country);
                getModelService().save(createRegion);
            }
        } catch (RuntimeException e) {
            LOG.error("Failed to save region models", e);
        }
        return createRegion;
    }


    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public PartnerCountryService getPartnerCountryService() {
        return partnerCountryService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public boolean isRegionAutomationDisabled() {
        return configurationService.getConfiguration()
            .getBoolean(PartnercoreConstants.REGION_AUTOMATION_LOAD_DISABLED,
                true);
    }
}