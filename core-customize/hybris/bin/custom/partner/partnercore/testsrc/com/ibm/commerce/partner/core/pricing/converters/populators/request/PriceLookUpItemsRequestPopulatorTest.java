package com.ibm.commerce.partner.core.pricing.converters.populators.request;


import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.order.price.data.request.CommonPriceLookUpItemsRequestData;
import com.ibm.commerce.partner.core.utils.PartnerOrderUtils;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.session.SessionService;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Map;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

import org.mockito.MockedStatic;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PriceLookUpItemsRequestPopulatorTest {

    @Mock
    private SessionService sessionService;

    @Mock
    private AbstractOrderEntryModel source;

    @Mock
    private CommonPriceLookUpItemsRequestData target;

    @Mock
    private Map<String, AbstractOrderEntryModel> subIdToItemNumberMap;

    @InjectMocks
    private PriceLookUpItemsRequestPopulator populator;
    @Mock
    private ConfigurationService configurationService;
    private final String ePartLicenceTypeCode = "RNWMNTSP";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        populator = new PriceLookUpItemsRequestPopulator(sessionService, ePartLicenceTypeCode,
            configurationService);
    }

    @Test
    public void testPopulateWithValidData() throws ConversionException {
        String subId = "sub123";
        String standAloneFlag = "Y";
        AbstractOrderEntryModel mockOrderEntry = mock(AbstractOrderEntryModel.class);
        try (MockedStatic<PartnerOrderUtils> utils = mockStatic(PartnerOrderUtils.class)) {
            utils.when(() -> PartnerOrderUtils.getProductInfo(mockOrderEntry,
                PartnercoreConstants.ORDER_ENTRY_SUB_ID)).thenReturn(subId);
            utils.when(() -> PartnerOrderUtils.getProductInfo(mockOrderEntry,
                PartnercoreConstants.ORDER_ENTRY_STANDALONE)).thenReturn(standAloneFlag);
            when(sessionService.getAttribute(
                PartnercoreConstants.SUB_ID_TO_ITEM_NUMBER_MAP)).thenReturn(subIdToItemNumberMap);
            when(subIdToItemNumberMap.containsKey(subId)).thenReturn(false);
            populator.populate(mockOrderEntry, target);
            verify(target, times(1)).setIsStandalone(standAloneFlag);
            verify(target, times(1)).setRelatedItemNumber(String.valueOf(target.getItemNumber()));
        }
    }

    @Test
    public void testPopulateWithSubIdFoundInMap() throws ConversionException {
        String subId = "sub123";
        String standAloneFlag = "Y";
        String itemNumber = "123";
        AbstractOrderEntryModel mockOrderEntry = mock(AbstractOrderEntryModel.class);
        AbstractOrderEntryModel mockSubOrderEntry = mock(AbstractOrderEntryModel.class);
        try (MockedStatic<PartnerOrderUtils> utils = mockStatic(PartnerOrderUtils.class)) {
            utils.when(() -> PartnerOrderUtils.getProductInfo(mockOrderEntry,
                PartnercoreConstants.ORDER_ENTRY_SUB_ID)).thenReturn(subId);
            utils.when(() -> PartnerOrderUtils.getProductInfo(mockOrderEntry,
                PartnercoreConstants.ORDER_ENTRY_STANDALONE)).thenReturn(standAloneFlag);
            utils.when(() -> PartnerOrderUtils.getItemNumber(mockSubOrderEntry))
                .thenReturn(Integer.valueOf(itemNumber));
            when(sessionService.getAttribute(
                PartnercoreConstants.SUB_ID_TO_ITEM_NUMBER_MAP)).thenReturn(subIdToItemNumberMap);
            when(subIdToItemNumberMap.containsKey(subId)).thenReturn(true);
            when(subIdToItemNumberMap.get(subId)).thenReturn(mockSubOrderEntry);
            populator.populate(mockOrderEntry, target);
            verify(target, times(1)).setIsStandalone(standAloneFlag);
            verify(target, times(1)).setRelatedItemNumber(itemNumber);
        }
    }

    @Test
    public void testPopulateWithEmptySubId() throws ConversionException {
        String standAloneFlag = "N";
        AbstractOrderEntryModel mockOrderEntry = mock(AbstractOrderEntryModel.class);
        try (MockedStatic<PartnerOrderUtils> utils = mockStatic(PartnerOrderUtils.class)) {
            utils.when(() -> PartnerOrderUtils.getProductInfo(mockOrderEntry,
                PartnercoreConstants.ORDER_ENTRY_SUB_ID)).thenReturn("");
            utils.when(() -> PartnerOrderUtils.getProductInfo(mockOrderEntry,
                PartnercoreConstants.ORDER_ENTRY_STANDALONE)).thenReturn(standAloneFlag);
            populator.populate(mockOrderEntry, target);
            verify(target, times(1)).setIsStandalone(standAloneFlag);
            verify(target, never()).setRelatedItemNumber(anyString());
        }
    }

    @Test(expected = ConversionException.class)
    public void testPopulateWithNullSource() throws ConversionException {
        populator.populate(null, target);
    }

    @Test
    public void testPopulateWithNullSubIdToItemNumberMap() throws ConversionException {
        String subId = "sub123";
        String standAloneFlag = "Y";
        AbstractOrderEntryModel mockOrderEntry = mock(AbstractOrderEntryModel.class);
        try (MockedStatic<PartnerOrderUtils> utils = mockStatic(PartnerOrderUtils.class)) {
            utils.when(() -> PartnerOrderUtils.getProductInfo(mockOrderEntry,
                PartnercoreConstants.ORDER_ENTRY_SUB_ID)).thenReturn(subId);
            utils.when(() -> PartnerOrderUtils.getProductInfo(mockOrderEntry,
                PartnercoreConstants.ORDER_ENTRY_STANDALONE)).thenReturn(standAloneFlag);
            when(sessionService.getAttribute(
                PartnercoreConstants.SUB_ID_TO_ITEM_NUMBER_MAP)).thenReturn(null);
            populator.populate(mockOrderEntry, target);
            verify(target, times(1)).setIsStandalone(standAloneFlag);
            verify(target, times(1)).setRelatedItemNumber(String.valueOf(target.getItemNumber()));
        }
    }

    @Test
    public void testPopulateWithEmptySubIdToItemNumberMap() throws ConversionException {
        String subId = "sub123";
        String standAloneFlag = "Y";
        AbstractOrderEntryModel mockOrderEntry = mock(AbstractOrderEntryModel.class);
        Map<String, AbstractOrderEntryModel> emptyMap = mock(Map.class);
        try (MockedStatic<PartnerOrderUtils> utils = mockStatic(PartnerOrderUtils.class)) {
            utils.when(() -> PartnerOrderUtils.getProductInfo(mockOrderEntry,
                PartnercoreConstants.ORDER_ENTRY_SUB_ID)).thenReturn(subId);
            utils.when(() -> PartnerOrderUtils.getProductInfo(mockOrderEntry,
                PartnercoreConstants.ORDER_ENTRY_STANDALONE)).thenReturn(standAloneFlag);
            when(sessionService.getAttribute(
                PartnercoreConstants.SUB_ID_TO_ITEM_NUMBER_MAP)).thenReturn(emptyMap);
            when(emptyMap.containsKey(subId)).thenReturn(false);
            populator.populate(mockOrderEntry, target);
            verify(target, times(1)).setIsStandalone(standAloneFlag);
            verify(target, times(1)).setRelatedItemNumber(String.valueOf(target.getItemNumber()));
        }
    }

    @Test
    public void testPopulateWithSubIdFoundInMapAndNullItemNumber() throws ConversionException {
        String subId = "sub123";
        String standAloneFlag = "Y";
        AbstractOrderEntryModel mockOrderEntry = mock(AbstractOrderEntryModel.class);
        AbstractOrderEntryModel mockSubOrderEntry = mock(AbstractOrderEntryModel.class);
        Map<String, AbstractOrderEntryModel> map = mock(Map.class);
        try (MockedStatic<PartnerOrderUtils> utils = mockStatic(PartnerOrderUtils.class)) {
            utils.when(() -> PartnerOrderUtils.getProductInfo(mockOrderEntry,
                PartnercoreConstants.ORDER_ENTRY_SUB_ID)).thenReturn(subId);
            utils.when(() -> PartnerOrderUtils.getProductInfo(mockOrderEntry,
                PartnercoreConstants.ORDER_ENTRY_STANDALONE)).thenReturn(standAloneFlag);
            utils.when(() -> PartnerOrderUtils.getItemNumber(mockSubOrderEntry)).thenReturn(null);
            when(sessionService.getAttribute(
                PartnercoreConstants.SUB_ID_TO_ITEM_NUMBER_MAP)).thenReturn(map);
            when(map.containsKey(subId)).thenReturn(true);
            when(map.get(subId)).thenReturn(mockSubOrderEntry);
            populator.populate(mockOrderEntry, target);
            verify(target, times(1)).setIsStandalone(standAloneFlag);
            verify(target, times(1)).setRelatedItemNumber("null");
        }
    }

    @Test
    public void testPopulateWithNullStandAloneFlag() throws ConversionException {
        String subId = "sub123";
        AbstractOrderEntryModel mockOrderEntry = mock(AbstractOrderEntryModel.class);
        Map<String, AbstractOrderEntryModel> map = mock(Map.class);
        try (MockedStatic<PartnerOrderUtils> utils = mockStatic(PartnerOrderUtils.class)) {
            utils.when(() -> PartnerOrderUtils.getProductInfo(mockOrderEntry,
                PartnercoreConstants.ORDER_ENTRY_SUB_ID)).thenReturn(subId);
            utils.when(() -> PartnerOrderUtils.getProductInfo(mockOrderEntry,
                PartnercoreConstants.ORDER_ENTRY_STANDALONE)).thenReturn(null);
            when(sessionService.getAttribute(
                PartnercoreConstants.SUB_ID_TO_ITEM_NUMBER_MAP)).thenReturn(map);
            when(map.containsKey(subId)).thenReturn(false);
            populator.populate(mockOrderEntry, target);
            verify(target, times(1)).setIsStandalone(null);
            verify(target, times(1)).setRelatedItemNumber(String.valueOf(target.getItemNumber()));
        }
    }

    @Test
    public void testPopulateWithNullSubId() throws ConversionException {
        String standAloneFlag = "Y";
        AbstractOrderEntryModel mockOrderEntry = mock(AbstractOrderEntryModel.class);
        try (MockedStatic<PartnerOrderUtils> utils = mockStatic(PartnerOrderUtils.class)) {
            utils.when(() -> PartnerOrderUtils.getProductInfo(mockOrderEntry,
                PartnercoreConstants.ORDER_ENTRY_SUB_ID)).thenReturn(null);
            utils.when(() -> PartnerOrderUtils.getProductInfo(mockOrderEntry,
                PartnercoreConstants.ORDER_ENTRY_STANDALONE)).thenReturn(standAloneFlag);
            populator.populate(mockOrderEntry, target);
            verify(target, times(1)).setIsStandalone(standAloneFlag);
            verify(target, never()).setRelatedItemNumber(anyString());
        }
    }

}
