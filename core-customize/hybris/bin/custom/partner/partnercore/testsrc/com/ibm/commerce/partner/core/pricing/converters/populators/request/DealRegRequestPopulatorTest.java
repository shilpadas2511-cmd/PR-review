package com.ibm.commerce.partner.core.pricing.converters.populators.request;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;

import java.util.Collections;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.ibm.commerce.common.core.product.service.IbmProductService;
import com.ibm.commerce.partner.core.b2b.company.service.PartnerB2BUnitService;
import com.ibm.commerce.partner.core.model.IbmB2BUnitModel;
import com.ibm.commerce.partner.core.model.IbmPartnerB2BUnitModel;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.order.price.data.request.DealRegRequestData;
import com.ibm.commerce.partner.core.services.PriceLookUpService;
import com.ibm.commerce.partner.core.util.model.AbstractOrderEntryModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.CountryModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.IbmPartProductModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.IbmPartnerB2BUnitModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.IbmPartnerEndCustomerB2BUnitModelTestDataGenerator;
import com.thoughtworks.xstream.converters.ConversionException;

@UnitTest
public class DealRegRequestPopulatorTest {

    @Mock
    private PriceLookUpService mockPriceLookUpService;

    @Mock
    private PartnerB2BUnitService mockDefaultB2BUnitService;

    @Mock
    private IbmProductService mockProductService;

    @InjectMocks
    private DealRegRequestPopulator dealRegRequestPopulator;

    private AbstractOrderModel source;

    private AbstractOrderEntryModel orderEntry;

    private AbstractOrderEntryModel childOrderEntry;

    IbmPartnerB2BUnitModel reseller;

    IbmB2BUnitModel parentReseller;

    private final String CUSTOMER_ICN = "TestUnit";
    private final String COUNTRY = "USA";

    private final String PRODUCT_CODE = "D1AZELL";
    private final String CEID = "resellerCEID";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        source = new IbmPartnerCartModel();

        orderEntry = AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntryModel(
            0,
            Collections.singletonList(new AbstractOrderEntryModel()));

        childOrderEntry = AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntry(
            0);
        childOrderEntry.setProduct(
            IbmPartProductModelTestDataGenerator.createProductData(PRODUCT_CODE));

        orderEntry.setChildEntries(Collections.singleton(childOrderEntry));

        final B2BUnitModel endCustomer = IbmPartnerEndCustomerB2BUnitModelTestDataGenerator.createModelTestData(
            CUSTOMER_ICN);
        parentReseller = IbmPartnerB2BUnitModelTestDataGenerator.createIbmPartnerB2BUnitModel(
            CEID, null, null);
        reseller = IbmPartnerB2BUnitModelTestDataGenerator.createIbmPartnerB2BUnitModel(
            null, null, null);
        endCustomer.setCountry(CountryModelTestDataGenerator.createTestData());
        source.setUnit(endCustomer);
        ((IbmPartnerCartModel) source).setSoldThroughUnit(reseller);
    }

    @Test
    public void testPopulate_Success() throws ConversionException {
        when(mockProductService.getProductCode(Mockito.any())).thenReturn(PRODUCT_CODE);
        when(mockPriceLookUpService.getChildEntriesList(source)).thenReturn(
            Collections.singletonList(childOrderEntry));
        when(mockDefaultB2BUnitService.getParent(reseller)).thenReturn(parentReseller);

        final DealRegRequestData target = new DealRegRequestData();
        dealRegRequestPopulator.populate(source, target);

        assertEquals(Boolean.FALSE, target.getIsCreditRebillQuote());
        assertEquals(CUSTOMER_ICN, target.getCustomerICN());
        assertEquals(COUNTRY, target.getQuoteCountry());
        assertEquals(PRODUCT_CODE, target.getParts().get(0).getPartNumber());
        assertEquals(CEID, target.getResellerCEID());
    }

    @Test
    public void testPopulate_NullSource() throws ConversionException {
        final DealRegRequestData target = new DealRegRequestData();

        dealRegRequestPopulator.populate(null, target);

        verifyZeroInteractions(mockPriceLookUpService);
        assertEquals(null, target.getIsCreditRebillQuote());
        assertEquals(null, target.getCustomerICN());
        assertEquals(null, target.getQuoteCountry());
        assertEquals(null, target.getParts());
        assertEquals(null, target.getCpqQuoteNum());
        assertEquals(null, target.getSapDistributionChannel());
    }

    @Test
    public void testPopulate_NoChildEntry() throws ConversionException {
		 when(mockPriceLookUpService.getChildEntriesList(source)).thenReturn(null);

        final DealRegRequestData target = new DealRegRequestData();
        dealRegRequestPopulator.populate(source, target);

        assertEquals(Boolean.FALSE, target.getIsCreditRebillQuote());
        assertEquals(CUSTOMER_ICN, target.getCustomerICN());
        assertEquals(COUNTRY, target.getQuoteCountry());
		  assertNull(target.getParts());
    }

    @Test
    public void testPopulate_NoCEID() throws ConversionException {
        when(mockDefaultB2BUnitService.getParent(reseller)).thenReturn(null);
        reseller.setGroups(new HashSet<>());
        ((IbmPartnerCartModel) source).setSoldThroughUnit(reseller);

        final DealRegRequestData target = new DealRegRequestData();
        dealRegRequestPopulator.populate(source, target);

        assertEquals(Boolean.FALSE, target.getIsCreditRebillQuote());
        assertEquals(CUSTOMER_ICN, target.getCustomerICN());
        assertEquals(COUNTRY, target.getQuoteCountry());
        assertNull(target.getResellerCEID());
    }

    @Test
    public void testGetDefaultDistributionChannel() {
        // Create a populator with a known defaultDistributionChannel
        String expectedDefault = "DEFAULT_CHANNEL";
        DealRegRequestPopulator populator = new DealRegRequestPopulator(
            mockPriceLookUpService, mockDefaultB2BUnitService, mockProductService, expectedDefault, "FULFILL_SRC");
        assertEquals(expectedDefault, populator.getDefaultDistributionChannel());
    }

}