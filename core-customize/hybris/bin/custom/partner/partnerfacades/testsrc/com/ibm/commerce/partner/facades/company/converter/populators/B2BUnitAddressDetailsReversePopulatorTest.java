package com.ibm.commerce.partner.facades.company.converter.populators;

import com.ibm.commerce.partner.core.util.model.AddressModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.CountryModelTestDataGenerator;
import com.ibm.commerce.partner.facades.comparators.PartnerAddressComparator;
import com.ibm.commerce.partner.facades.util.AddressTestDataGenerator;
import com.ibm.commerce.partner.facades.util.CountryTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@UnitTest
public class B2BUnitAddressDetailsReversePopulatorTest {

    private final static String LINE1 = "20 Cooper Square";
    private final static String LINE2 = "47 W 13th St";
    private final static String ADDRESS_LINE1 = "47 W 13th St";
    private final static String ADDRESS_LINE2 = "20 Cooper Square";
    private final static String COUNTRY_ISO_CODE = "USA";
    private final static String TOWN = "Newyork";
    private static final String POSTAL_CODE = "13141";

    @InjectMocks
    B2BUnitAddressDetailsReversePopulator addressDetailsReversePopulator;

    @Mock
    Converter<AddressData, AddressModel> addressReverseConverter;

    @Mock
    PartnerAddressComparator addressComparator;

    @Mock
    B2BCommerceUnitService b2BCommerceUnitService;

    @Mock
    ModelService modelService;

    B2BUnitData b2BUnitData;

    B2BUnitModel b2BUnitModel;

    CountryModel countryModel;

    List<AddressModel> addressModels = new ArrayList<>();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        addressDetailsReversePopulator = new B2BUnitAddressDetailsReversePopulator(
                addressReverseConverter, addressComparator, b2BCommerceUnitService, modelService);
        b2BUnitData = new B2BUnitData();
        countryModel = CountryModelTestDataGenerator.createTestData();
        AddressModel addressModel = AddressModelTestDataGenerator.createAddressModel(null, TOWN,
                POSTAL_CODE, null, null, null, LINE1, LINE2, countryModel, null);
        addressModels.add(addressModel);
        b2BUnitModel = new B2BUnitModel();
        List<AddressModel> addressModelList = new ArrayList<>();
        AddressModel addressModel1 = AddressModelTestDataGenerator.createAddressModel(null, null,
                POSTAL_CODE, null, null, null, ADDRESS_LINE1, ADDRESS_LINE2, countryModel, null);
        addressModelList.add(addressModel1);
        b2BUnitModel.setAddresses(addressModelList);
        List<AddressData> addressDataList = new ArrayList<>();
        AddressData addressData = AddressTestDataGenerator.createAddressData(LINE1, LINE2, TOWN,
                CountryTestDataGenerator.prepareCountryData(COUNTRY_ISO_CODE));
        addressDataList.add(addressData);
        b2BUnitData.setAddresses(addressDataList);
        Mockito.when(addressReverseConverter.convertAll(b2BUnitData.getAddresses()))
                .thenReturn(addressModels);
    }

    @Test
    public void testPopulate_AddressNull() {
        B2BUnitData unitData = new B2BUnitData();
        addressDetailsReversePopulator.populate(unitData, b2BUnitModel);
        Assert.assertNotNull(b2BUnitModel.getAddresses());
        List<AddressModel> addressModels = (List<AddressModel>) b2BUnitModel.getAddresses();
        Assert.assertNull(addressModels.get(0).getLine1());
    }

    @Test
    public void testPopulate() {
        b2BUnitModel.setAddresses(new ArrayList<>());
        addressDetailsReversePopulator.populate(b2BUnitData, b2BUnitModel);
        Assert.assertNotNull(b2BUnitModel.getAddresses());
        Assert.assertEquals(0, b2BUnitModel.getAddresses().size());
    }

    @Test
    public void testPopulate_Address() {
        addressDetailsReversePopulator.populate(b2BUnitData, b2BUnitModel);
        List<AddressModel> addressModels = (List<AddressModel>) b2BUnitModel.getAddresses();
        Assert.assertEquals(LINE1, addressModels.get(0).getStreetnumber());
        Assert.assertEquals(LINE2, addressModels.get(0).getStreetname());
    }

    @Test
    public void testPopulate_MatchingAddress() {
        Optional<AddressModel> addressModel = b2BUnitModel.getAddresses().stream().findFirst();
        Mockito.when(addressComparator.compare(addressModel.get(), addressModels.get(0)))
                .thenReturn(0);
        addressDetailsReversePopulator.populate(b2BUnitData, b2BUnitModel);
        List<AddressModel> addressModels = (List<AddressModel>) b2BUnitModel.getAddresses();
        Assert.assertEquals(LINE1, addressModels.get(0).getStreetnumber());
        Assert.assertEquals(LINE2, addressModels.get(0).getStreetname());
    }

    @Test
    public void testPopulate_NotMatchingAddress() {
        Optional<AddressModel> addressModel = b2BUnitModel.getAddresses().stream().findFirst();
        Mockito.when(addressComparator.compare(addressModel.get(), addressModels.get(0)))
                .thenReturn(1);
        addressDetailsReversePopulator.populate(b2BUnitData, b2BUnitModel);
        List<AddressModel> addressModels = (List<AddressModel>) b2BUnitModel.getAddresses();
        Assert.assertEquals(LINE1, addressModels.get(0).getStreetnumber());
        Assert.assertEquals(LINE2, addressModels.get(0).getStreetname());
    }
    @Test
    public void testGetModelService() {
        Assert.assertNotNull(addressDetailsReversePopulator.getModelService());
    }
}