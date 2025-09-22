package com.ibm.commerce.partner.facades.search.converters.populator;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.quote.data.QuoteData;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import de.hybris.platform.servicelayer.dto.converter.Converter;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.ibm.commerce.partner.order.data.PartnerOrderData;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PartnerQuoteSearchResultPopulatorTest {

    @InjectMocks
    private PartnerQuoteSearchResultPopulator populator;
    private SearchResultValueData source;
    private QuoteData target;

    private EnumerationService enumerationService;

    @Mock
    Converter<CurrencyModel, CurrencyData> currencyConverter;

    @Before
    public void setUp() {
        enumerationService = mock(EnumerationService.class);

        populator = new PartnerQuoteSearchResultPopulator(currencyConverter, "yyyy-MM-dd");
        source = mock(SearchResultValueData.class);
        final Map<String, Object> values = new HashMap<>();
        values.put(PartnerQuoteSearchResultPopulator.QUOTE_CODE, "Q123");
        values.put(PartnerQuoteSearchResultPopulator.QUOTE_NAME, "Test Quote");
        values.put(PartnerQuoteSearchResultPopulator.QUOTE_CREATION_NAME, new Date());
        values.put(PartnerQuoteSearchResultPopulator.QUOTE_SUBMITTED_DATE, new Date());
        values.put(PartnerQuoteSearchResultPopulator.QUOTE_STATE, QuoteState.CREATED.getCode());
        values.put(PartnerQuoteSearchResultPopulator.QUOTE_EXPIRATION_DATE, new Date());
        values.put(PartnerQuoteSearchResultPopulator.QUOTE_PROGRAM_TYPE, "Test Program");
        values.put(PartnerQuoteSearchResultPopulator.QUOTE_PRICE, 1000.0);
        values.put(PartnerQuoteSearchResultPopulator.QUOTE_CREATOR, "John Doe");
        values.put(PartnerQuoteSearchResultPopulator.STORE, "Test Store");
        values.put(PartnerQuoteSearchResultPopulator.QUOTE_VERSION, 1);
        values.put(PartnerQuoteSearchResultPopulator.QUOTE_CARTID, "Cart123");
        values.put(PartnerQuoteSearchResultPopulator.STATUSNAME, "Active");
        values.put(PartnerQuoteSearchResultPopulator.FULL_PRICE_RECEIVED, true);

        when(source.getValues()).thenReturn(values);
        target = new QuoteData();
    }

    @Test
    public void testPopulate_WithValidData_ShouldPopulateCorrectly() throws ConversionException {
        final Map<String, Object> values = new HashMap<>();
        values.put("code", "Q123");
        values.put("name", "Test Quote");
        values.put("creationtime", new Date());
        values.put("eccQuoteNumber", "ECC123");
        values.put("submittedDate", new Date());
        values.put("state", QuoteState.BUYER_APPROVED.toString());
        values.put("unitId", "UNIT123");
        values.put("unitName", "UnitName");
        values.put("quoteCreator", "John Doe");
        values.put("billToUnitId", "BILL123");
        values.put("billToUnitName", "Billing Unit");
        values.put("soldThroughUnitId", "SOLD123");
        values.put("soldThroughUnitName", "Sales Unit");
        values.put("salesApplication", "PARTNER_COMMERCE");
        values.put("fullPriceReceived", true);

        source.setValues(values);

        populator.populate(source, target);
        assert target.getCode().equals("Q123");
        assert target.getName().equals("Test Quote");
        assert target.getSubmittedDate() != null;
        assert target.getState() == QuoteState.CREATED;
        assert target.getQuoteExpirationDate() != null;
        assert target.getTotalBidExtendedPrice() == 1000.0;
        assert target.getQuoteCreater() != null;
        assert target.getQuoteCreater().getName().equals("John Doe");
        assert target.getStore().equals("Test Store");
        assert target.getVersion() == 1;
        assert target.getCartId().equals("Cart123");
        assert target.getStatusName().equals("Active");
    }

    @Test
    public void testPopulateWithCancelledState() throws ConversionException {
        final Map<String, Object> values = new HashMap<>(source.getValues());
        values.put(PartnerQuoteSearchResultPopulator.QUOTE_STATE, QuoteState.CANCELLED.getCode());
        when(source.getValues()).thenReturn(values);
        populator.populate(source, target);

    }

    @Test
    public void testGetValue() {
        final String result = populator.getValue(source,
            PartnerQuoteSearchResultPopulator.QUOTE_CODE);
        assert result.equals("Q123");
    }

    @Test
    public void testGetValueWhenValuesNull() {
        when(source.getValues()).thenReturn(null);

        final String code = populator.getValue(source,
            PartnerQuoteSearchResultPopulator.QUOTE_CODE);
        assertNull(code);
    }

    @Test
    public void testPopulate_WithOrderIds() throws ConversionException {
        final Map<String, Object> values = new HashMap<>();
        values.put("quoteCode", "Q123");
        values.put("quoteName", "Test Quote");
        values.put("quoteCreationDate", new Date());
        values.put("quoteSubmittedDate", new Date());
        values.put("quoteExpirationDate", new Date());
        values.put("quotePrice", 199.99);
        values.put("quoteCreator", "JohnDoe");
        values.put("store", "TestStore");
        values.put("quoteVersion", 3);
        values.put("quoteCartId", "CART123");
        values.put("statusName", "In Progress");
        values.put("fullPriceReceived", true);
        values.put("orderId", Arrays.asList("ORD001", "ORD002"));
        when(source.getValues()).thenReturn(values);
        populator.populate(source, target);
        final List<PartnerOrderData> result = target.getOrders();
        assertEquals(2, result.size());
        assertEquals("ORD001", result.get(0).getOrderId());
        assertEquals("ORD002", result.get(1).getOrderId());
    }

    @Test
    public void testPopulate_SalesAppAndCurrency() throws ConversionException {
        final Map<String, Object> values = new HashMap<>();
        values.put(PartnerQuoteSearchResultPopulator.SALES_APPLICATION, "APP1");
        values.put(PartnerQuoteSearchResultPopulator.CURRENCY_CODE, "USD");
        values.put("fullPriceReceived", Boolean.TRUE);
        when(source.getValues()).thenReturn(values);
        populator.populate(source, target);
        assertNotNull(target.getSalesApplication());
        assertEquals("APP1", target.getSalesApplication().getCode());
        assertNotNull(target.getCurrency());
        assertEquals("USD", target.getCurrency().getIsocode());
    }

    @Test
    public void testGetEnumerationService() throws Exception {
        final EnumerationService mockEnumService = Mockito.mock(EnumerationService.class);
        final Field enumServiceField = populator.getClass().getDeclaredField("enumerationService");
        enumServiceField.setAccessible(true);
        enumServiceField.set(populator, mockEnumService);
        final EnumerationService result = populator.getEnumerationService();
        assertNotNull(result);
    }


}