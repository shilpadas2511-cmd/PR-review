package com.ibm.commerce.partner.core.inboundservices.persistence.hook.service.impl;

import com.ibm.commerce.common.core.model.PartnerProductSetModel;
import com.ibm.commerce.partner.core.inboundservices.persistence.hook.service.impl.DefaultQuoteCreationProvisioningFormMapperService;
import com.ibm.commerce.partner.core.model.*;
import com.ibm.commerce.partner.core.provisionform.service.PartnerProvisionFormService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.*;

@UnitTest
public class DefaultQuoteCreationProvisioningFormMapperServiceTest {

    @InjectMocks
    private DefaultQuoteCreationProvisioningFormMapperService defaultService;

    @Mock
    private ModelService modelService;

    @Mock
    private PartnerProvisionFormService partnerProvisionFormService;

    @Mock
    private CpqIbmPartnerQuoteModel cpqIbmPartnerQuoteModel;

    @Mock
    private IbmPartnerQuoteModel ibmPartnerQuoteModel;

    @Mock
    private CpqPartnerProvisionFormsModel cpqPartnerProvisionFormsModel;

    @Mock
    private CpqPartnerProvisionFormModel cpqPartnerProvisionFormModel;

    @Mock
    private PartnerProvisionFormsModel partnerProvisionFormsModel;

    @Mock
    private PartnerProvisionFormModel partnerProvisionFormModel;

    @Mock
    private PartnerProductSetModel partnerProductSetModel;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        defaultService = new DefaultQuoteCreationProvisioningFormMapperService(modelService, partnerProvisionFormService);

    }

    @Test
    public void testMap_ProvisionFormsExist() {
        Set<CpqPartnerProvisionFormModel> cpqProvisionFormSet = new HashSet<>();
        cpqProvisionFormSet.add(cpqPartnerProvisionFormModel);
        when(cpqIbmPartnerQuoteModel.getProvisionForms()).thenReturn(cpqPartnerProvisionFormsModel);
        when(cpqPartnerProvisionFormsModel.getCpqPartnerProvisionForm()).thenReturn(cpqProvisionFormSet);
        when(modelService.create(PartnerProvisionFormsModel.class)).thenReturn(partnerProvisionFormsModel);
        when(modelService.create(PartnerProvisionFormModel.class)).thenReturn(partnerProvisionFormModel);
        when(partnerProvisionFormService.getProductSet(anyString())).thenReturn(partnerProductSetModel);
        defaultService.map(cpqIbmPartnerQuoteModel, ibmPartnerQuoteModel);
        verify(modelService).save(partnerProvisionFormsModel);
        verify(modelService).save(partnerProvisionFormModel);
        verify(ibmPartnerQuoteModel).setProvisionForms(partnerProvisionFormsModel);
    }

    @Test
    public void testMap_NoProvisionForms() {
        when(cpqIbmPartnerQuoteModel.getProvisionForms()).thenReturn(null);
        defaultService.map(cpqIbmPartnerQuoteModel, ibmPartnerQuoteModel);
        verify(modelService, never()).save(any());
        verify(ibmPartnerQuoteModel, never()).setProvisionForms(any());
    }

}