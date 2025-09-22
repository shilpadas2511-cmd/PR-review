package com.ibm.commerce.partner.facades.company.converter.populators;

import com.ibm.commerce.partner.core.util.model.AddressModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.B2BCustomerModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.B2BUnitModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.CountryModelTestDataGenerator;
import com.ibm.commerce.partner.facades.util.AddressTestDataGenerator;
import com.ibm.commerce.partner.facades.util.CountryTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.poi.ss.formula.functions.T;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@UnitTest
public class B2BUnitAddressDetailsPopulatorTest {

    private final static String EMAIL_ID = "test@test.com";
    private final static String UID = "001010223";

    private final static String LINE1 = "20 Cooper Square";
    private final static String LINE2 = "47 W 13th St";
    private final static String COUNTRY_ISO_CODE = "USA";
    private final static String TOWN = "Newyork";
    private static final String POSTAL_CODE = "13141";

    @InjectMocks
    B2BUnitAddressDetailsPopulator b2BUnitAddressDetailsPopulator;

    @Mock
    Converter<AddressModel, AddressData> addressConverter;

    B2BUnitModel unitModel;
    B2BUnitData unitData;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        b2BUnitAddressDetailsPopulator = new B2BUnitAddressDetailsPopulator(addressConverter);
        unitData = new B2BUnitData();
        List<AddressModel> addressModels = new ArrayList<>();
        CountryModel countryModel = CountryModelTestDataGenerator.createTestData();
        AddressModel addressModel = AddressModelTestDataGenerator.createAddressModel(null, TOWN,
            POSTAL_CODE, null, null, null, LINE1, LINE2, countryModel, null);
        addressModels.add(addressModel);
        addressModels.add(addressModel);
        AddressData addressData = AddressTestDataGenerator.createAddressData(LINE1, LINE2, TOWN,
            CountryTestDataGenerator.prepareCountryData(COUNTRY_ISO_CODE));
        unitModel = B2BUnitModelTestDataGenerator.prepareB2BUnitAddress(UID, addressModels);
        Mockito.when(addressConverter.convert(addressModel)).thenReturn(addressData);
    }

    @Test
    public void testPopulate() {
        b2BUnitAddressDetailsPopulator.populate(unitModel, unitData);
        Assert.assertNotNull(unitData.getAddresses());
        AddressData addressData = unitData.getAddresses().get(0);
        Assert.assertEquals(LINE1, addressData.getLine1());
        Assert.assertEquals(LINE2, addressData.getLine2());
        Assert.assertEquals(TOWN, addressData.getTown());
    }

    @Test
    public void testPopulate_Null() {
        unitModel.setAddresses(null);
        b2BUnitAddressDetailsPopulator.populate(unitModel, unitData);
        Assert.assertNull(unitData.getAddresses());

    }
}
