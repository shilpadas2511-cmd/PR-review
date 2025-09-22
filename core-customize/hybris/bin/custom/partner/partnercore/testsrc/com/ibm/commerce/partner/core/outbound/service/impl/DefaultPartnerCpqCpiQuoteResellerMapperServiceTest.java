package com.ibm.commerce.partner.core.outbound.service.impl;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteCustomerModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import org.mockito.Mockito;

/**
 * Test class for {@link DefaultPartnerCpqCpiQuoteResellerMapperService}
 */
@UnitTest
public class DefaultPartnerCpqCpiQuoteResellerMapperServiceTest {

    private DefaultPartnerCpqCpiQuoteResellerMapperService service;
    private IbmPartnerQuoteModel quoteModel;
    private B2BUnitModel unitModel;
    private Set<SAPCPQOutboundQuoteCustomerModel> sapcpqOutboundQuoteCustomerModels;

    @Before
    public void setUp() {
        service = new DefaultPartnerCpqCpiQuoteResellerMapperService();
        quoteModel = Mockito.mock(IbmPartnerQuoteModel.class);
        unitModel = Mockito.mock(B2BUnitModel.class);
        sapcpqOutboundQuoteCustomerModels = new HashSet<>();

        Mockito.when(quoteModel.getSoldThroughUnit()).thenReturn(unitModel);
        Mockito.when(unitModel.getUid()).thenReturn("testUser");
        Mockito.when(unitModel.getName()).thenReturn("Test User");
    }

    @Test
    public void testMap() {
        service.map(quoteModel, sapcpqOutboundQuoteCustomerModels);

        Assert.assertEquals(1, sapcpqOutboundQuoteCustomerModels.size());

        SAPCPQOutboundQuoteCustomerModel result = sapcpqOutboundQuoteCustomerModels.iterator().next();
        Assert.assertEquals("testUser", result.getCustomerCode());
        Assert.assertEquals("Test User", result.getName());
        Assert.assertEquals(PartnercoreConstants.RESELLER_CPQ, result.getRoleType());
    }
}
