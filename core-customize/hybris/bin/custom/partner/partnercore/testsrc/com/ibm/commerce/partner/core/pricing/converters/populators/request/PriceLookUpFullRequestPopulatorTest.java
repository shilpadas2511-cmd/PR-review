package com.ibm.commerce.partner.core.pricing.converters.populators.request;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.order.price.data.request.FullPriceLookUpItemRequestData;
import com.ibm.commerce.partner.core.order.price.data.request.FullPriceLookUpRequestData;
import com.ibm.commerce.partner.core.order.price.data.request.PriceLookUpHeaderRequestData;
import com.ibm.commerce.partner.core.util.model.AbstractOrderEntryModelTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.session.SessionService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

@UnitTest
public class PriceLookUpFullRequestPopulatorTest {

    private PriceLookUpFullRequestPopulator populator;
    private Converter<AbstractOrderModel, PriceLookUpHeaderRequestData> headerConverter;
    private Converter<AbstractOrderEntryModel, FullPriceLookUpItemRequestData> itemConverter;

    private SessionService sessionService;

    private final int SIZE = 2;
    private Set<String> ytyEligibleLicenceTypeCodes;

    @Before
    public void setUp() {
        headerConverter = mock(Converter.class);
        itemConverter = mock(Converter.class);
        ytyEligibleLicenceTypeCodes = new HashSet<String>();
        ytyEligibleLicenceTypeCodes.add("LCMNTSPT");
        ytyEligibleLicenceTypeCodes.add("RNWMNTSP");
        sessionService = mock(SessionService.class);
        populator = new PriceLookUpFullRequestPopulator(headerConverter, itemConverter,
            sessionService, ytyEligibleLicenceTypeCodes);
    }

    @Test
    public void testPopulate() {
        AbstractOrderModel order = new IbmPartnerCartModel();
        AbstractOrderEntryModel orderEntry1 = AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntryModel(
            0, Collections.singletonList(new AbstractOrderEntryModel()));
        AbstractOrderEntryModel orderEntry2 = AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntryModel(
            1, Collections.singletonList(new AbstractOrderEntryModel()));
        AbstractOrderEntryModel childOrderEntry1 = AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntry(
            0);
        AbstractOrderEntryModel childOrderEntry2 = AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntry(
            0);
        orderEntry1.setChildEntries(Collections.singleton(childOrderEntry1));
        orderEntry2.setChildEntries(Collections.singleton(childOrderEntry2));
        PriceLookUpHeaderRequestData headerData = new PriceLookUpHeaderRequestData();
        when(headerConverter.convert(order)).thenReturn(headerData);
        List<AbstractOrderEntryModel> entries = new ArrayList<>();
        entries.add(orderEntry1);
        entries.add(orderEntry2);
        order.setEntries(entries);

        FullPriceLookUpItemRequestData itemData1 = new FullPriceLookUpItemRequestData();
        FullPriceLookUpItemRequestData itemData2 = new FullPriceLookUpItemRequestData();
        when(itemConverter.convert(orderEntry1)).thenReturn(itemData1);
        when(itemConverter.convert(orderEntry2)).thenReturn(itemData2);

        FullPriceLookUpRequestData target = new FullPriceLookUpRequestData();
        populator.populate(order, target);

        assertEquals(headerData, target.getHeader());
        assertEquals(SIZE, target.getItems().size());
    }

    @Test
    public void testPopulate_withChildEntriesAndSessionService() {
        AbstractOrderModel order = new IbmPartnerCartModel();
        AbstractOrderEntryModel orderEntry = AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntryModel(
            0, Collections.emptyList());
        AbstractOrderEntryModel childOrderEntry = AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntry(0);
        orderEntry.setChildEntries(Collections.singleton(childOrderEntry));
        order.setEntries(Collections.singletonList(orderEntry));
        PriceLookUpHeaderRequestData headerData = new PriceLookUpHeaderRequestData();
        when(headerConverter.convert(order)).thenReturn(headerData);
        FullPriceLookUpItemRequestData itemData = new FullPriceLookUpItemRequestData();
        when(itemConverter.convert(childOrderEntry)).thenReturn(itemData);
        FullPriceLookUpRequestData target = new FullPriceLookUpRequestData();
        populator.populate(order, target);
        assertEquals(headerData, target.getHeader());
        assertEquals(1, target.getItems().size());
        verify(sessionService, times(1)).removeAttribute(PartnercoreConstants.SUB_ID_TO_ITEM_NUMBER_MAP);
    }
}