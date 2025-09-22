package com.ibm.commerce.partner.facades.user.converters.populator;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.country.services.PartnerCountryService;
import com.ibm.commerce.partner.facades.util.CountryTestDataGenerator;

import de.hybris.bootstrap.annotations.UnitTest;

import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;

import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.ibm.commerce.partner.core.util.model.AddressModelTestDataGenerator;
import com.ibm.commerce.partner.facades.util.AddressTestDataGenerator;
import com.ibm.commerce.partner.facades.util.RegionTestDataGenerator;


/**
 * Test class for {@link AddressRegionDetailsReversePopulator}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AddressRegionDetailsReversePopulatorTest {

    private static final String REGION_CODE = "CA";
    private static final String REGION_NAME = "California";

    @Mock
    private CommonI18NService commonI18NService;
    @Mock
    private ModelService modelService;
    @Mock
    private PartnerCountryService partnerCountryService;
    @Mock
    private ConfigurationService configurationService;
    @Mock
    private Configuration configuration;
    @Mock
    private CountryModel countryModel;
    @Mock
    private RegionModel regionModel;

    private AddressRegionDetailsReversePopulator populator;

    private AddressData addressData;
    private RegionData regionData;
    private CountryData countryData;
    private AddressModel addressModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        populator = new AddressRegionDetailsReversePopulator(
            commonI18NService, modelService, partnerCountryService, configurationService);

        // Test data
        regionData = RegionTestDataGenerator.createRegionData(REGION_CODE, REGION_NAME);
        countryData = CountryTestDataGenerator.prepareCountryData("US");
        addressData = AddressTestDataGenerator.createAddressData(regionData);
        addressData.setCountry(countryData);
        addressData.setTown(REGION_NAME);
        addressModel = AddressModelTestDataGenerator.createAddressModel(countryModel);
        when(configurationService.getConfiguration()).thenReturn(configuration);
    }

    @Test
    public void testPopulate_ValidRegion() {
        when(commonI18NService.getRegion(countryModel, REGION_CODE)).thenReturn(regionModel);
        populator.populate(addressData, addressModel);
        Assert.assertEquals(regionModel, addressModel.getRegion());
    }

    @Test
    public void testPopulate_RegionNull() {
        addressData.setRegion(null);
        populator.populate(addressData, addressModel);
        Assert.assertNull(addressModel.getRegion());
    }

    @Test(expected = ConversionException.class)
    public void testPopulate_UnknownRegion_RegionAutomationDisabled() {
        when(commonI18NService.getRegion(countryModel, REGION_CODE))
            .thenThrow(new UnknownIdentifierException("not found"));
        when(configuration.getBoolean(PartnercoreConstants.REGION_AUTOMATION_LOAD_DISABLED, true))
            .thenReturn(true);
        populator.populate(addressData, addressModel);
    }

    @Test
    public void testPopulate_UnknownRegion_RegionAutomationEnabled() {
        when(commonI18NService.getRegion(countryModel, REGION_CODE))
            .thenThrow(new UnknownIdentifierException("not found"));
        when(configuration.getBoolean(PartnercoreConstants.REGION_AUTOMATION_LOAD_DISABLED, true))
            .thenReturn(false);

        when(partnerCountryService.getByCodeOrSapCode("US")).thenReturn(countryModel);
        when(modelService.create(RegionModel.class)).thenReturn(regionModel);

        populator.populate(addressData, addressModel);

        verify(modelService).save(regionModel);
        Assert.assertEquals(regionModel, addressModel.getRegion());
    }

    @Test(expected = ConversionException.class)
    public void testPopulate_AmbiguousRegion() {
        when(commonI18NService.getRegion(countryModel, REGION_CODE))
            .thenThrow(new AmbiguousIdentifierException("ambiguous"));
        populator.populate(addressData, addressModel);
    }

    @Test
    public void testCreateRegion_Success() {
        when(partnerCountryService.getByCodeOrSapCode("US")).thenReturn(countryModel);
        when(modelService.create(RegionModel.class)).thenReturn(regionModel);

        RegionModel result = populator.createRegion(addressData);

        verify(regionModel).setIsocode(REGION_CODE);
        verify(regionModel).setName(REGION_NAME);
        verify(regionModel).setCountry(countryModel);
        verify(modelService).save(regionModel);
        Assert.assertEquals(regionModel, result);
    }

    @Test
    public void testCreateRegion_Failure() {
        addressData.getCountry().setIsocode(null);
        RegionModel result = populator.createRegion(addressData);
        Assert.assertNull(result);
    }

    @Test
    public void testIsRegionAutomationDisabled_True() {
        when(configuration.getBoolean(PartnercoreConstants.REGION_AUTOMATION_LOAD_DISABLED, true))
            .thenReturn(true);
        Assert.assertTrue(populator.isRegionAutomationDisabled());
    }

    @Test
    public void testIsRegionAutomationDisabled_False() {
        when(configuration.getBoolean(PartnercoreConstants.REGION_AUTOMATION_LOAD_DISABLED, true))
            .thenReturn(false);
        Assert.assertFalse(populator.isRegionAutomationDisabled());
    }

    @Test
    public void testGetConfigurationService() {
        ConfigurationService result = populator.getConfigurationService();
        Assert.assertEquals(configurationService, result);
    }
    @Test
    public void testCreateRegion_Exception() {
        Mockito.lenient().when(partnerCountryService.getByCodeOrSapCode("US")).thenReturn(countryModel);
        Mockito.lenient().when(countryModel.getIsocode()).thenReturn("US");
        Mockito.lenient().when(modelService.create(RegionModel.class)).thenReturn(null);
        Mockito.lenient().doThrow(new RuntimeException("Error")).when(modelService).save(regionModel);
        RegionModel result = populator.createRegion(addressData);
        Assert.assertNull(result);


    }
}