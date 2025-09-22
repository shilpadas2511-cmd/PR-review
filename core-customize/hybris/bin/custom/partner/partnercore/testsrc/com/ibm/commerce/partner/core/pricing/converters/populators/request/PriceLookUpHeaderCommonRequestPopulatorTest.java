package com.ibm.commerce.partner.core.pricing.converters.populators.request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.order.price.data.request.PriceLookUpHeaderRequestData;
import com.ibm.commerce.partner.core.util.model.B2BCustomerModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.CurrencyModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.IbmPartnerAgreementDetailModelTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import com.ibm.commerce.partner.core.model.IbmB2BUnitModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import com.ibm.commerce.partner.core.model.PartnerSalesOrganisationModel;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import de.hybris.platform.core.model.c2l.CountryModel;
import com.ibm.commerce.partner.core.model.IbmPartnerEndCustomerB2BUnitModel;

@UnitTest
public class PriceLookUpHeaderCommonRequestPopulatorTest {
    private PriceLookUpHeaderCommonRequestPopulator populator;
    private IbmPartnerCartModel cart;
    private final String CART_CODE = "TestCart";
    private final String PROGRAM_TYPE_PA = "PA";
    private final String PROGRAM_TYPE_PAE  = "PAE";
    private final String AGREEMENT_NUMBER = "1234";
    private final String COUNTRY = "USA";
    private final String UNIT_UID = "TestUid";
    private final String CUSTOMER_EMAIL = "user@test.com";
    private final String CURRENCY_ISO_CODE = "USD";

    @Before
    public void setUp() {
        populator = new PriceLookUpHeaderCommonRequestPopulator();
    }

    public void dataSetup() {
        cart = new IbmPartnerCartModel();
        cart.setCode(CART_CODE);
        cart.setAgreementDetail(
            IbmPartnerAgreementDetailModelTestDataGenerator.createIbmPartnerAgreementDetailModel(
                AGREEMENT_NUMBER, null, PROGRAM_TYPE_PA, null));
        IbmB2BUnitModel billToUnit = new IbmB2BUnitModel();
        billToUnit.setUid(UNIT_UID);
        CountryModel countryModel = new CountryModel();
        countryModel.setIsocode(COUNTRY);
        billToUnit.setCountry(countryModel);
        cart.setBillToUnit(billToUnit);
        cart.setUser(B2BCustomerModelTestDataGenerator.createB2BCustomerModel(CUSTOMER_EMAIL));
        cart.setCurrency(CurrencyModelTestDataGenerator.createCurrencyModel(CURRENCY_ISO_CODE));
        IbmB2BUnitModel soldThroughUnit = new IbmB2BUnitModel();
        soldThroughUnit.setUid(UNIT_UID);
        cart.setSoldThroughUnit(soldThroughUnit);
        IbmPartnerEndCustomerB2BUnitModel endCustomer = new IbmPartnerEndCustomerB2BUnitModel();
        endCustomer.setUid(UNIT_UID);
        endCustomer.setCountry(countryModel);
        cart.setUnit(endCustomer);
    }

    @Test
    public void testPopulate_AllFields() {
        dataSetup();
        PriceLookUpHeaderRequestData target = new PriceLookUpHeaderRequestData();
        populator.populate(cart, target);
        assertEquals(PROGRAM_TYPE_PA, target.getLob());
        assertEquals(AGREEMENT_NUMBER, target.getAgreementNumber());
        assertEquals(COUNTRY, target.getCountry());
        assertEquals(CURRENCY_ISO_CODE, target.getCurrency());
        assertEquals(UNIT_UID, target.getPayerSite());
        assertEquals(UNIT_UID, target.getResellerSite());
        assertEquals(CART_CODE, target.getQuoteId());
    }

    @Test
    public void testPopulate_NullAgreementDetail() {
        dataSetup();
        cart.setAgreementDetail(null);
        PriceLookUpHeaderRequestData target = new PriceLookUpHeaderRequestData();
        populator.populate(cart, target);
        assertNull(target.getLob());
        assertNull(target.getAgreementNumber());
    }

    @Test
    public void testPopulate_NullProgramType() {
        dataSetup();
        cart.getAgreementDetail().setProgramType(null);
        PriceLookUpHeaderRequestData target = new PriceLookUpHeaderRequestData();
        populator.populate(cart, target);
        assertNull(target.getLob());
        assertNull(target.getAgreementNumber());
    }

    @Test
    public void testPopulate_ProgramTypeNotPA() {
        dataSetup();
        cart.setAgreementDetail(IbmPartnerAgreementDetailModelTestDataGenerator.createIbmPartnerAgreementDetailModel(null, null, "PB", null));
        PriceLookUpHeaderRequestData target = new PriceLookUpHeaderRequestData();
        populator.populate(cart, target);
        assertEquals("PB", target.getLob());
        assertNull(target.getAgreementNumber());
    }

    @Test
    public void testPopulate_ProgramTypePAE() {
        dataSetup();
        cart.setAgreementDetail(IbmPartnerAgreementDetailModelTestDataGenerator.createIbmPartnerAgreementDetailModel(AGREEMENT_NUMBER, null, PROGRAM_TYPE_PAE, null));
        PriceLookUpHeaderRequestData target = new PriceLookUpHeaderRequestData();
        populator.populate(cart, target);
        assertEquals(PROGRAM_TYPE_PAE, target.getLob());
        assertNull(target.getAgreementNumber());
    }

    @Test
    public void testPopulate_NullAgreementNumber() {
        dataSetup();
        cart.setAgreementDetail(IbmPartnerAgreementDetailModelTestDataGenerator.createIbmPartnerAgreementDetailModel(null, null, PROGRAM_TYPE_PA, null));
        PriceLookUpHeaderRequestData target = new PriceLookUpHeaderRequestData();
        populator.populate(cart, target);
        assertEquals(PROGRAM_TYPE_PA, target.getLob());
        assertNull(target.getAgreementNumber());
    }

    @Test
    public void testPopulate_NullEndCustomer() {
        dataSetup();
        cart.setUnit(null);
        PriceLookUpHeaderRequestData target = new PriceLookUpHeaderRequestData();
        populator.populate(cart, target);
        assertEquals(PROGRAM_TYPE_PA, target.getLob());
        assertNull(target.getCountry());
        assertEquals(CURRENCY_ISO_CODE, target.getCurrency());
        assertEquals(UNIT_UID, target.getPayerSite());
        assertNull(target.getSoldToSite());
        assertEquals(CART_CODE, target.getQuoteId());
    }

    @Test
    public void testPopulate_NullCountry() {
        dataSetup();
        cart.getUnit().setCountry(null);
        PriceLookUpHeaderRequestData target = new PriceLookUpHeaderRequestData();
        populator.populate(cart, target);
        assertEquals(PROGRAM_TYPE_PA, target.getLob());
        assertNull(target.getCountry());
        assertEquals(CURRENCY_ISO_CODE, target.getCurrency());
        assertEquals(UNIT_UID, target.getPayerSite());
        assertNull(target.getSoldToSite());
        assertEquals(CART_CODE, target.getQuoteId());
    }

    @Test
    public void testPopulate_NullBillToUnit() {
        dataSetup();
        cart.setBillToUnit(null);
        PriceLookUpHeaderRequestData target = new PriceLookUpHeaderRequestData();
        populator.populate(cart, target);
        assertNull(target.getPayerSite());
    }

    @Test
    public void testPopulate_NullSoldThroughUnit() {
        dataSetup();
        cart.setSoldThroughUnit(null);
        PriceLookUpHeaderRequestData target = new PriceLookUpHeaderRequestData();
        populator.populate(cart, target);
        assertNull(target.getResellerSite());
    }

    @Test
    public void testPopulate_NullCurrency() {
        dataSetup();
        cart.setCurrency(null);
        PriceLookUpHeaderRequestData target = new PriceLookUpHeaderRequestData();
        populator.populate(cart, target);
        assertNull(target.getCurrency());
    }

    @Test
    public void testPopulate_SalesOrgsEmpty() {
        dataSetup();
        ((IbmB2BUnitModel) cart.getBillToUnit()).setPartnerSalesOrganisations(Collections.emptySet());
        PriceLookUpHeaderRequestData target = new PriceLookUpHeaderRequestData();
        populator.populate(cart, target);
        assertNull(target.getSalesOrg());
    }

    @Test
    public void testPopulate_SalesOrgsNotEmpty() {
        dataSetup();
        PartnerSalesOrganisationModel salesOrg = new PartnerSalesOrganisationModel();
        salesOrg.setCode("SALES_ORG_CODE");
        Set<PartnerSalesOrganisationModel> salesOrgs = new HashSet<>();
        salesOrgs.add(salesOrg);
        ((IbmB2BUnitModel) cart.getBillToUnit()).setPartnerSalesOrganisations(salesOrgs);
        PriceLookUpHeaderRequestData target = new PriceLookUpHeaderRequestData();
        populator.populate(cart, target);
        assertEquals("SALES_ORG_CODE", target.getSalesOrg());
    }

    @Test
    public void testPopulate_DistributionChannelBlank() {
        dataSetup();
        cart.setCpqDistributionChannel("");
        PriceLookUpHeaderRequestData target = new PriceLookUpHeaderRequestData();
        populator.populate(cart, target);
        assertEquals("J", target.getDistChannel()); // PartnerQuoteChannelEnum.J.getCode()
    }

    @Test
    public void testPopulate_DistributionChannelNonBlank() {
        dataSetup();
        cart.setCpqDistributionChannel("X");
        PriceLookUpHeaderRequestData target = new PriceLookUpHeaderRequestData();
        populator.populate(cart, target);
        assertEquals("X", target.getDistChannel());
    }

    @Test
    public void testPopulate_PriceUidBlank() {
        dataSetup();
        cart.setPriceUid("");
        PriceLookUpHeaderRequestData target = new PriceLookUpHeaderRequestData();
        populator.populate(cart, target);
        assertEquals(cart.getCode(), target.getQuoteId());
    }

    @Test
    public void testPopulate_PriceUidNonBlank() {
        dataSetup();
        cart.setPriceUid("Y");
        PriceLookUpHeaderRequestData target = new PriceLookUpHeaderRequestData();
        populator.populate(cart, target);
        assertEquals("Y", target.getQuoteId());
    }

    @Test
    public void testPopulate_OrderModel() {
        PriceLookUpHeaderRequestData target = new PriceLookUpHeaderRequestData();
        com.ibm.commerce.partner.core.model.IbmPartnerCartModel source = new com.ibm.commerce.partner.core.model.IbmPartnerCartModel();
        populator.populate(source, target);
        assertNull(target.getLob());
        assertNull(target.getCountry());
        assertNull(target.getCurrency());
        assertNull(target.getPayerSite());
        assertNull(target.getResellerSite());
        assertNull(target.getQuoteId());
    }
}