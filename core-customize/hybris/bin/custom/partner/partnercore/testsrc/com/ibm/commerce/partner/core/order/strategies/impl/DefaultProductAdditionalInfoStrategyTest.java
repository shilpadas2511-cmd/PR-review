package com.ibm.commerce.partner.core.order.strategies.impl;

import com.ibm.commerce.partner.core.model.IbmPartnerCartEntryModel;
import com.ibm.commerce.partner.core.model.PartnerCommerceRampUpModel;
import com.ibm.commerce.partner.core.model.PartnerRampUpSummaryModel;
import com.ibm.commerce.partner.data.order.entry.CommerceRampUpData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.servicelayer.model.ModelService;
import org.eclipse.persistence.annotations.UnionPartitioning;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import de.hybris.platform.core.model.order.CartModel;
import static org.mockito.Mockito.lenient;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Collections;


@UnitTest
@ExtendWith(MockitoExtension.class)
class DefaultProductAdditionalInfoStrategyTest {

    @InjectMocks
    private DefaultProductAdditionalInfoStrategy strategy;

    @Mock
    private ModelService modelService;

    @Mock
    private CommerceCartParameter parameter;

    @Mock
    private CommerceCartModification result;

    @Mock
    private IbmPartnerCartEntryModel ibmPartnerCartEntryModel;

    @Mock
    private CommerceRampUpData commerceRampUpData;

    @Mock
    private CartModel cartModel;

    @BeforeEach
    public void setUp() {
        lenient().when(result.getEntry()).thenReturn(ibmPartnerCartEntryModel);
        lenient().when(parameter.getCart()).thenReturn(cartModel);
        lenient().when(cartModel.getCode()).thenReturn("CART123");
    }

    @Test
    public void testAddInfo_Success() {
        when(parameter.getCommerceRampUpData()).thenReturn(commerceRampUpData);
        when(commerceRampUpData.getRampUpPeriod()).thenReturn("12");

        PartnerCommerceRampUpModel partnerCommerceRampUpDataModel = mock(
            PartnerCommerceRampUpModel.class);
        when(modelService.create(PartnerCommerceRampUpModel.class)).thenReturn(
            partnerCommerceRampUpDataModel);

        strategy.addInfo(parameter, result);

        verify(modelService, times(1)).create(PartnerCommerceRampUpModel.class);
        verify(modelService, times(1)).save(partnerCommerceRampUpDataModel);
        verify(modelService, times(1)).save(ibmPartnerCartEntryModel);
        verify(modelService, times(1)).create(PartnerRampUpSummaryModel.class);
        verify(modelService, times(1)).save(any(PartnerRampUpSummaryModel.class));
    }

    @Test
    void testAddInfo_EntryNotIbmPartnerCartEntryModel() {
        AbstractOrderEntryModel notIbmEntry = mock(AbstractOrderEntryModel.class);
        when(result.getEntry()).thenReturn(notIbmEntry);
        strategy.addInfo(parameter, result);
        verify(modelService, times(0)).create(PartnerCommerceRampUpModel.class);
        verify(modelService, times(0)).save(any(PartnerCommerceRampUpModel.class));
    }

    @Test
    void testAddInfo_NullRampUpData() {
        when(parameter.getCommerceRampUpData()).thenReturn(null);
        strategy.addInfo(parameter, result);
        verify(modelService, times(0)).create(PartnerCommerceRampUpModel.class);
        verify(modelService, times(0)).save(any(PartnerCommerceRampUpModel.class));
    }

    @Test
    void testAddInfo_EmptyRampUpSummary() {
        when(parameter.getCommerceRampUpData()).thenReturn(commerceRampUpData);
        when(commerceRampUpData.getRampUpPeriod()).thenReturn("12");
        when(commerceRampUpData.getRampUpSummary()).thenReturn(Collections.emptyList());

        PartnerCommerceRampUpModel partnerCommerceRampUpDataModel = mock(PartnerCommerceRampUpModel.class);
        when(modelService.create(PartnerCommerceRampUpModel.class)).thenReturn(partnerCommerceRampUpDataModel);

        strategy.addInfo(parameter, result);
        verify(modelService, times(1)).create(PartnerCommerceRampUpModel.class);
        verify(modelService, times(1)).save(partnerCommerceRampUpDataModel);
        verify(modelService, times(1)).save(ibmPartnerCartEntryModel);
        verify(modelService, times(0)).create(PartnerRampUpSummaryModel.class);
    }

    @Test
    void testAddInfo_NonEmptyRampUpSummary() {
        when(parameter.getCommerceRampUpData()).thenReturn(commerceRampUpData);
        when(commerceRampUpData.getRampUpPeriod()).thenReturn("12");
        PartnerCommerceRampUpModel partnerCommerceRampUpDataModel = mock(PartnerCommerceRampUpModel.class);
        when(modelService.create(PartnerCommerceRampUpModel.class)).thenReturn(partnerCommerceRampUpDataModel);
        PartnerRampUpSummaryModel partnerRampUpSummaryModel = mock(PartnerRampUpSummaryModel.class);
        when(modelService.create(PartnerRampUpSummaryModel.class)).thenReturn(partnerRampUpSummaryModel);
        var rampUpSummaryData = mock(com.ibm.commerce.partner.data.order.entry.RampUpSummaryData.class);
        when(rampUpSummaryData.getRampUpPeriodDuration()).thenReturn("duration");
        when(rampUpSummaryData.getRampUpQuantity()).thenReturn("5");
        java.util.List<com.ibm.commerce.partner.data.order.entry.RampUpSummaryData> summaryList = java.util.Collections.singletonList(rampUpSummaryData);
        when(commerceRampUpData.getRampUpSummary()).thenReturn(summaryList);
        strategy.addInfo(parameter, result);
        verify(modelService, times(1)).saveAll(anyList());
        verify(partnerCommerceRampUpDataModel, times(1)).setPartnerRampUpSummary(anyList());
        verify(modelService, atLeastOnce()).save(partnerRampUpSummaryModel);
    }

    @Test
    void testGetModelService() {
        assertSame(modelService, strategy.getModelService());
    }
}