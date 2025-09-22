package com.ibm.commerce.partner.facades.specialbidreason.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ibm.commerce.partner.core.model.PartnerSpecialBidReasonModel;
import com.ibm.commerce.partner.core.specialbidreason.service.PartnerSpecialBidReasonService;
import com.ibm.commerce.partner.core.util.model.PartnerSpecialBidReasonTestDataGenerator;
import com.ibm.commerce.partner.specialbidreason.data.PartnerSpecialBidReasonData;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import com.ibm.commerce.partner.facades.order.PartnerCartFacade;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class DefaultPartnerSpecialBidBidReasonFacadeTest {

    private DefaultPartnerSpecialBidBidReasonFacade partnerSpecialBidBidReasonFacade;
    private PartnerSpecialBidReasonService partnerSpecialBidReasonService;
    private Converter<PartnerSpecialBidReasonModel, PartnerSpecialBidReasonData> partnerSpecialBidReasonConverter;

    private static final String REASON_CODE1 = "Code1";
    private static final String REASON_CODE2 = "Code2";
    private static final String REASON_NAME1 = "Name1";

    private static final String REASON_NAME2 = "Name2";

    @Mock
    private ConfigurationService configurationService;
    @Mock
    private Configuration configuration;
    @Mock
    private CartService cartService;

    @Mock
    ModelService modelService;

    @Before
    public void setUp() {
        partnerSpecialBidReasonService = mock(PartnerSpecialBidReasonService.class);
        partnerSpecialBidReasonConverter = mock(Converter.class);
        when(configurationService.getConfiguration()).thenReturn(configuration);
        partnerSpecialBidBidReasonFacade = new DefaultPartnerSpecialBidBidReasonFacade(
            partnerSpecialBidReasonService, partnerSpecialBidReasonConverter, configurationService,
            cartService, modelService);
    }

    @Test
    public void testGetSpecialBidReason() {

        List<PartnerSpecialBidReasonModel> specialBidReasonModelList = new ArrayList<>();
        PartnerSpecialBidReasonModel model1 = PartnerSpecialBidReasonTestDataGenerator.createSpecialBidreason(
            REASON_CODE1, REASON_NAME1);
        PartnerSpecialBidReasonModel model2 = PartnerSpecialBidReasonTestDataGenerator.createSpecialBidreason(
            REASON_CODE2, REASON_NAME2);
        specialBidReasonModelList.add(model1);
        specialBidReasonModelList.add(model2);

        when(partnerSpecialBidReasonService.getAllSpecialBidReasonDetails()).thenReturn(
            specialBidReasonModelList);

        List<PartnerSpecialBidReasonData> specialBidReasonDataList = new ArrayList<>();

        PartnerSpecialBidReasonData data1 = PartnerSpecialBidReasonTestDataGenerator.createSpecialBidreasonData(
            REASON_CODE1, REASON_NAME1);
        PartnerSpecialBidReasonData data2 = PartnerSpecialBidReasonTestDataGenerator.createSpecialBidreasonData(
            REASON_CODE2, REASON_NAME2);
        specialBidReasonDataList.add(data1);
        specialBidReasonDataList.add(data2);
        when(partnerSpecialBidReasonConverter.convert(model1)).thenReturn(data1);
        when(partnerSpecialBidReasonConverter.convert(model2)).thenReturn(data2);

        List<PartnerSpecialBidReasonData> result = partnerSpecialBidBidReasonFacade.getAllSpecialBidReasonDetails();

        assertEquals(2, result.size());
        assertEquals(REASON_CODE1, result.get(0).getCode());
        assertEquals(REASON_NAME1, result.get(0).getName());
        assertEquals(REASON_CODE2, result.get(1).getCode());
        assertEquals(REASON_NAME2, result.get(1).getName());

    }


    @Test
    public void testGetAllSpecialBidReasonDetailsEmptyList() {
        when(partnerSpecialBidReasonService.getAllSpecialBidReasonDetails()).thenReturn(
            new ArrayList<>());
        List<PartnerSpecialBidReasonData> result = partnerSpecialBidBidReasonFacade.getAllSpecialBidReasonDetails();
        assertTrue(result.isEmpty());
    }


}
