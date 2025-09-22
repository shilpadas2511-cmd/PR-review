package com.ibm.commerce.partner.core.outbound.service.impl;

import com.ibm.commerce.common.core.model.PartnerProductSetModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.PartnerProvisionFormModel;
import com.ibm.commerce.partner.core.model.PartnerProvisionFormsModel;
import com.ibm.commerce.partner.core.model.SAPCPQProvisionFormsModel;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteModel;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Test class for {@link DefaultPartnerCpqCpiQuoteProvisionFormMapperService}
 */
@UnitTest
public class DefaultPartnerCpqCpiQuoteProvisionFormMapperServiceTest {

    @InjectMocks
    DefaultPartnerCpqCpiQuoteProvisionFormMapperService defaultPartnerCpqCpiQuoteProvisionFormMapperService;

    @Mock
    private IbmPartnerQuoteModel quoteModel;

    @Mock
    private SAPCPQOutboundQuoteModel sapcpqOutboundQuoteModel;

    @Mock
    private PartnerProvisionFormModel partnerProvisionForm;

    @Mock
    private PartnerProvisionFormsModel partnerProvisionsForm;

    @Mock
    private PartnerProductSetModel productSetModel;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testMap_shouldMapProvisionFormsSuccessfully() {
        Mockito.when(quoteModel.getProvisionForms()).thenReturn(partnerProvisionsForm);
        Mockito.when(partnerProvisionsForm.getPartnerProvisionForm()).thenReturn(Collections.singleton(partnerProvisionForm));
        Mockito.when(partnerProvisionForm.getCode()).thenReturn("code123");
        Mockito.when(partnerProvisionForm.getUrl()).thenReturn("http://example.com");
        Mockito.when(partnerProvisionForm.getProductSetCode()).thenReturn(productSetModel);
        Mockito.when(partnerProvisionForm.getProductSetCode().getCode()).thenReturn("productSetCode123");

        defaultPartnerCpqCpiQuoteProvisionFormMapperService.map(quoteModel, sapcpqOutboundQuoteModel);

        Mockito.verify(sapcpqOutboundQuoteModel).setProvisionFormDetails(org.mockito.ArgumentMatchers.any());
    }

    @Test
    public void testSetProvisionForm_shouldMapPartnerProvisionFormToSapcpqProvisionFormModel() {
        Mockito.when(partnerProvisionForm.getCode()).thenReturn("code123");
        Mockito.when(partnerProvisionForm.getUrl()).thenReturn("http://example.com");
        Mockito.when(partnerProvisionForm.getProductSetCode()).thenReturn(productSetModel);
        Mockito.when(partnerProvisionForm.getProductSetCode().getCode()).thenReturn("productSetCode123");

        SAPCPQProvisionFormsModel result = defaultPartnerCpqCpiQuoteProvisionFormMapperService.setProvisionForm(partnerProvisionForm);

        Assert.assertNotNull(result);
        Assert.assertEquals("code123", result.getId());
        Assert.assertEquals("http://example.com", result.getUrl());
        Assert.assertEquals("productSetCode123", result.getProductSetCode());
    }

    @Test
    public void testMapQuotePricesToCPQOutboundQuote_shouldMapProvisionForms() {
        Mockito.when(quoteModel.getProvisionForms()).thenReturn(partnerProvisionsForm);
        Mockito.when(partnerProvisionsForm.getPartnerProvisionForm()).thenReturn(Collections.singleton(partnerProvisionForm));
        Mockito.when(partnerProvisionForm.getCode()).thenReturn("code123");
        Mockito.when(partnerProvisionForm.getUrl()).thenReturn("http://example.com");
        Mockito.when(partnerProvisionForm.getProductSetCode()).thenReturn(productSetModel);
        Mockito.when(partnerProvisionForm.getProductSetCode().getCode()).thenReturn("productSetCode123");

        List<SAPCPQProvisionFormsModel> result = defaultPartnerCpqCpiQuoteProvisionFormMapperService.mapQuoteProvisionFormsToCPQOutboundQuote(quoteModel);

        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("code123", result.get(0).getId());
        Assert.assertEquals("http://example.com", result.get(0).getUrl());
        Assert.assertEquals("productSetCode123", result.get(0).getProductSetCode());
    }
}
