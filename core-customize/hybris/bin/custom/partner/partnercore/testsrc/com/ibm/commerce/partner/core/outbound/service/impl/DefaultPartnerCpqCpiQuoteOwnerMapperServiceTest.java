package com.ibm.commerce.partner.core.outbound.service.impl;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteCustomerModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.core.model.user.UserModel;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@UnitTest
public class DefaultPartnerCpqCpiQuoteOwnerMapperServiceTest {

    private static final String TEST_EMAIL = "owner@partner.com";
    private static final String TEST_NAME = "Jane Owner";

    private DefaultPartnerCpqCpiQuoteOwnerMapperService mapperService;

    @Mock
    private CustomerEmailResolutionService customerEmailResolutionService;

    @Mock
    private IbmPartnerQuoteModel quoteModel;

    @Mock
    private B2BCustomerModel b2bCustomerModel;

    @Mock
    private UserModel genericUser;

    private Set<SAPCPQOutboundQuoteCustomerModel> outboundSet;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mapperService = new DefaultPartnerCpqCpiQuoteOwnerMapperService(customerEmailResolutionService);
        outboundSet = new HashSet<>();
    }

    @Test
    public void testMap_WithB2BCustomer_PopulatesCustomerCodeAndName() {
        Mockito.when(quoteModel.getCreator()).thenReturn(b2bCustomerModel);
        Mockito.when(customerEmailResolutionService.getEmailForCustomer(b2bCustomerModel)).thenReturn(TEST_EMAIL);
        Mockito.when(b2bCustomerModel.getName()).thenReturn(TEST_NAME);

        mapperService.map(quoteModel, outboundSet);

        Assert.assertEquals(1, outboundSet.size());
        SAPCPQOutboundQuoteCustomerModel result = outboundSet.iterator().next();
        Assert.assertEquals(TEST_EMAIL, result.getCustomerCode());
        Assert.assertEquals(TEST_NAME, result.getName());
        Assert.assertEquals(PartnercoreConstants.QUOTE_OWNER_CPQ, result.getRoleType());
    }

    @Test
    public void testMap_WithNonB2BCustomer_OnlyRoleTypeSet() {
        Mockito.when(quoteModel.getCreator()).thenReturn(genericUser);

        mapperService.map(quoteModel, outboundSet);

        Assert.assertEquals(1, outboundSet.size());
        SAPCPQOutboundQuoteCustomerModel result = outboundSet.iterator().next();
        Assert.assertEquals(null, result.getCustomerCode());
        Assert.assertEquals(null, result.getName());
        Assert.assertEquals(PartnercoreConstants.QUOTE_OWNER_CPQ, result.getRoleType());
    }

    @Test
    public void testGetCustomerEmailResolutionService_ReturnsInjectedBean() {
        Assert.assertNotNull(mapperService.getCustomerEmailResolutionService());
        Assert.assertEquals(customerEmailResolutionService, mapperService.getCustomerEmailResolutionService());
    }
}
