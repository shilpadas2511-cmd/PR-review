package com.ibm.commerce.partner.facades.actions.customer;

import static com.google.common.base.Verify.verify;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ibm.commerce.partner.core.accountservice.services.PartnerAccountServiceOutboundIntegrationService;
import com.ibm.commerce.partner.core.company.data.response.PartnerSiteCustomerAddressInfoResponseData;
import com.ibm.commerce.partner.core.company.data.response.PartnerSiteCustomerInfoResponseData;
import com.ibm.commerce.partner.core.company.reseller.data.response.PartnerResellerSiteIdResponseData;
import com.ibm.commerce.partner.core.model.PartnerResellerSiteBusinessProcessModel;
import com.ibm.commerce.partner.core.model.PartnerStoreFrontCustomerProcessModel;
import com.ibm.commerce.partner.core.util.data.PartnerResellerSiteIdResponseTestDataGenerator;
import com.ibm.commerce.partner.core.util.data.PartnerSiteCustomerAddressInfoResponseTestDataGenerator;
import com.ibm.commerce.partner.core.util.data.PartnerSiteCustomerInfoResponseTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.B2BCustomerModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.PartnerResellerSiteBusinessProcessModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.PartnerStoreFrontCustomerProcessModelTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction.Transition;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@UnitTest
public class FetchPartnerResellerAccountDetailsActionTest {

    private static String EMAIL = "test@test.com";
    private static String DIST_NUMBER = "71001231";

    private static final String ADDRESS_LINE1 = "Shinjuku Mitsui Bld.49F";
    private static final String ADDRESS_LINE2 = "2-1-1 Nishishinjuku";
    private static final String COUNTRY_CODE = "JPN";
    private static final String CITY = "Shinjuku-ku";
    private static final String POSTAL_CODE = "163-0449";
    private static final String REGION_CODE = "13";
    private static final String REGION_DESC = "Tokyo";
    private static final String ACCOUNT_ID = "sap0007970368";
    private static final String BUSINESS_PROCSS_CODE = "10001";

    @InjectMocks
    FetchPartnerResellerAccountDetailsAction fetchPartnerResellerAccountDetailsAction;

    @Mock
    PartnerAccountServiceOutboundIntegrationService outboundIntegrationService;

    List<String> eligibleCountryList;
    @Mock
    BusinessProcessService businessProcessService;
    @Mock
    KeyGenerator processCodeGenerator;

    @Mock
    ModelService modelService;

    Integer maxRetryAllowed;
    Integer retryDelay;

    PartnerStoreFrontCustomerProcessModel partnerStoreFrontCustomerProcessModel;
    B2BCustomerModel b2BCustomerModel;
    List<PartnerResellerSiteIdResponseData> resellerSites;
    @Before
    public void setUp () {
        MockitoAnnotations.initMocks(this);
        fetchPartnerResellerAccountDetailsAction = new FetchPartnerResellerAccountDetailsAction(maxRetryAllowed, retryDelay, outboundIntegrationService, businessProcessService, processCodeGenerator);
        fetchPartnerResellerAccountDetailsAction.setModelService(modelService);
        b2BCustomerModel = B2BCustomerModelTestDataGenerator.createB2BCustomerModel(EMAIL);
        partnerStoreFrontCustomerProcessModel = PartnerStoreFrontCustomerProcessModelTestDataGenerator.createProcessModel(b2BCustomerModel, null);
        resellerSites = new ArrayList<>();
        PartnerResellerSiteIdResponseData resellerSiteIdResponseData = PartnerResellerSiteIdResponseTestDataGenerator.createResellerResponseData(DIST_NUMBER,null, null);
        resellerSiteIdResponseData.setAccountId(ACCOUNT_ID);
        PartnerSiteCustomerAddressInfoResponseData addressInfoResponseData = PartnerSiteCustomerAddressInfoResponseTestDataGenerator.createAddressInfoData(ADDRESS_LINE1, ADDRESS_LINE2, CITY, COUNTRY_CODE, POSTAL_CODE, REGION_CODE, REGION_DESC);
        PartnerSiteCustomerInfoResponseData customerInfoResponseData = PartnerSiteCustomerInfoResponseTestDataGenerator.createCustomerInfo(addressInfoResponseData, null);
        resellerSiteIdResponseData.setCustomerInfo(customerInfoResponseData);
        resellerSites.add(resellerSiteIdResponseData);
    }

    @Test
    public void testExecuteAction_ProcessNull_NOK() throws Exception {
        Transition transition = fetchPartnerResellerAccountDetailsAction.executeAction(null);
        assertEquals(Transition.NOK, transition);
    }

    @Test
    public void testExecution_CustomerAsNull_NOK() throws Exception {
        partnerStoreFrontCustomerProcessModel.setCustomer(null);
        Transition transition = fetchPartnerResellerAccountDetailsAction.executeAction(partnerStoreFrontCustomerProcessModel);
        assertEquals(Transition.NOK, transition);
    }

    @Test
    public void testExecution() throws Exception {
        when(outboundIntegrationService.getResellerSiteId(b2BCustomerModel.getUid())).thenReturn(resellerSites);
        when(processCodeGenerator.generateFor(Mockito.any())).thenReturn(BUSINESS_PROCSS_CODE);
        PartnerResellerSiteBusinessProcessModel resellerSiteBusinessProcessModel = PartnerResellerSiteBusinessProcessModelTestDataGenerator.createResellerProcessModel(null, null);
        when(businessProcessService.createProcess(Mockito.anyString(), Mockito.anyString(), Mockito.anyMap())).thenReturn(resellerSiteBusinessProcessModel);
        Transition transition = fetchPartnerResellerAccountDetailsAction.executeAction(partnerStoreFrontCustomerProcessModel);
        assertEquals(Transition.OK, transition);
    }
    @Test
    public void testExecuteAction_returnsNOK_whenResellerSitesHaveIneligibleCountry() throws Exception {
        CustomerModel customer = new CustomerModel();
        customer.setUid("customer-001");
        PartnerStoreFrontCustomerProcessModel processModel = new PartnerStoreFrontCustomerProcessModel();
        processModel.setCustomer(customer);
        PartnerResellerSiteIdResponseData siteData = mock(PartnerResellerSiteIdResponseData.class);
        PartnerSiteCustomerInfoResponseData customerInfo = mock(PartnerSiteCustomerInfoResponseData.class);
        PartnerSiteCustomerAddressInfoResponseData address = mock(PartnerSiteCustomerAddressInfoResponseData.class);
        when(siteData.getCustomerInfo()).thenReturn(customerInfo);
        when(siteData.getAccountId()).thenReturn("site-001");
        when(customerInfo.getAddress()).thenReturn(address);
        when(address.getCountryCode()).thenReturn("IN");
        when(outboundIntegrationService.getResellerSiteId("customer-001")).thenReturn(List.of(siteData));
        fetchPartnerResellerAccountDetailsAction.setEligibleCountryList(List.of("US", "DE"));
        fetchPartnerResellerAccountDetailsAction.executeAction(processModel);

    }
    @Test
    public void testExecuteAction_returnsNOK_whenNoEligibleResellerSiteExists() throws Exception {
        PartnerStoreFrontCustomerProcessModel model = new PartnerStoreFrontCustomerProcessModel();
        CustomerModel customer = new CustomerModel();
        customer.setUid("test-customer");
        model.setCustomer(customer);
        PartnerSiteCustomerAddressInfoResponseData address = mock(PartnerSiteCustomerAddressInfoResponseData.class);
        when(address.getCountryCode()).thenReturn("IN");
        PartnerSiteCustomerInfoResponseData customerInfo = mock(PartnerSiteCustomerInfoResponseData.class);
        when(customerInfo.getAddress()).thenReturn(address);
        fetchPartnerResellerAccountDetailsAction.setEligibleCountryList(null);
        var result = fetchPartnerResellerAccountDetailsAction.executeAction(model);
        assertEquals(FetchPartnerResellerAccountDetailsAction.Transition.NOK, result);
    }
}
