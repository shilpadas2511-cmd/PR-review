package com.ibm.commerce.partner.core.inboundservices.persistence.hook.service.impl;

import com.ibm.commerce.partner.core.b2b.company.service.PartnerB2BUnitService;
import com.ibm.commerce.partner.core.model.CpqIbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.CpqIbmPartnerSpecialBidReasonModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.PartnerSpecialBidReasonModel;
import com.ibm.commerce.partner.core.specialbidreason.service.PartnerSpecialBidReasonService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class DefaultQuoteCreationSpecialBidMapperServiceTest {

    @Mock
    private ModelService modelService;

    @Mock
    private PartnerSpecialBidReasonService partnerSpecialBidReasonService;

    @Mock
    private PartnerB2BUnitService partnerB2BUnitService;

    @InjectMocks
    private DefaultQuoteCreationSpecialBidMapperService mapperService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mapperService = new DefaultQuoteCreationSpecialBidMapperService(
            modelService,
            partnerSpecialBidReasonService,
            partnerB2BUnitService
        );
    }

    @Test
    public void testMap_withValidSpecialBidReasonAndJustification() {
        CpqIbmPartnerQuoteModel cpqModel = new CpqIbmPartnerQuoteModel();
        IbmPartnerQuoteModel target = new IbmPartnerQuoteModel();

        CpqIbmPartnerSpecialBidReasonModel cpqReason = new CpqIbmPartnerSpecialBidReasonModel();
        cpqReason.setCode("reason1");

        PartnerSpecialBidReasonModel resolvedReason = new PartnerSpecialBidReasonModel();

        Set<CpqIbmPartnerSpecialBidReasonModel> cpqReasons = new HashSet<>();
        cpqReasons.add(cpqReason);
        cpqModel.setSpecialBidReasons(cpqReasons);
        cpqModel.setSpecialBidBusinessJustification("Valid justification");

        Mockito.when(partnerSpecialBidReasonService.getSpecialBidReasonById("reason1"))
            .thenReturn(resolvedReason);

        mapperService.map(cpqModel, target);

        org.junit.jupiter.api.Assertions.assertTrue(
            target.getSpecialBidReasons().contains(resolvedReason));
        org.junit.jupiter.api.Assertions.assertEquals(
            "Valid justification",
            target.getSpecialBidBusinessJustification()
        );
    }

    @Test
    public void testMap_withNullSpecialBidReason_shouldSkipMapping() {
        CpqIbmPartnerQuoteModel cpqModel = new CpqIbmPartnerQuoteModel();
        IbmPartnerQuoteModel target = new IbmPartnerQuoteModel();

        cpqModel.setSpecialBidReasons(null);
        cpqModel.setSpecialBidBusinessJustification("Only justification");

        mapperService.map(cpqModel, target);

        org.junit.jupiter.api.Assertions.assertNull(target.getSpecialBidReasons());
        org.junit.jupiter.api.Assertions.assertEquals(
            "Only justification",
            target.getSpecialBidBusinessJustification()
        );
    }

    @Test
    public void testMap_withEmptySpecialBidReasonSet_shouldNotSetReasons() {
        CpqIbmPartnerQuoteModel cpqModel = new CpqIbmPartnerQuoteModel();
        IbmPartnerQuoteModel target = new IbmPartnerQuoteModel();

        cpqModel.setSpecialBidReasons(Collections.emptySet());
        cpqModel.setSpecialBidBusinessJustification(null);

        mapperService.map(cpqModel, target);

        org.junit.jupiter.api.Assertions.assertNull(target.getSpecialBidReasons());
        org.junit.jupiter.api.Assertions.assertNull(target.getSpecialBidBusinessJustification());
    }

    @Test
    public void testMap_withUnresolvableSpecialBidReason_shouldSkipNulls() {
        CpqIbmPartnerQuoteModel cpqModel = new CpqIbmPartnerQuoteModel();
        IbmPartnerQuoteModel target = new IbmPartnerQuoteModel();

        CpqIbmPartnerSpecialBidReasonModel cpqReason = new CpqIbmPartnerSpecialBidReasonModel();
        cpqReason.setCode("unknown");

        Set<CpqIbmPartnerSpecialBidReasonModel> cpqReasons = new HashSet<>();
        cpqReasons.add(cpqReason);
        cpqModel.setSpecialBidReasons(cpqReasons);

        Mockito.when(partnerSpecialBidReasonService.getSpecialBidReasonById("unknown"))
            .thenReturn(null);

        mapperService.map(cpqModel, target);

        org.junit.jupiter.api.Assertions.assertNull(target.getSpecialBidReasons());
    }

    @Test
    public void testGetters_shouldReturnInjectedDependencies() {
        org.junit.jupiter.api.Assertions.assertSame(
            partnerSpecialBidReasonService,
            mapperService.getPartnerSpecialBidReasonService()
        );
        org.junit.jupiter.api.Assertions.assertSame(
            partnerB2BUnitService,
            mapperService.getPartnerB2BUnitService()
        );
        org.junit.jupiter.api.Assertions.assertSame(
            modelService,
            mapperService.getModelService()
        );
    }
}
