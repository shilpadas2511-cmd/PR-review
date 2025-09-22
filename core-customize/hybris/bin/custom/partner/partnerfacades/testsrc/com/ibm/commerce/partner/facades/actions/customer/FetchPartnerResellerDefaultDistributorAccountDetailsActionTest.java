package com.ibm.commerce.partner.facades.actions.customer;

import com.ibm.commerce.partner.core.accountservice.services.PartnerAccountServiceOutboundIntegrationService;
import com.ibm.commerce.partner.core.company.distributor.data.response.PartnerDistributorSiteIdResponseData;
import com.ibm.commerce.partner.core.company.reseller.data.response.PartnerResellerSiteIdResponseData;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.PartnerResellerSiteBusinessProcessModel;
import com.ibm.commerce.partner.core.util.data.PartnerDistributorSiteIdResponseTestDataGenerator;
import com.ibm.commerce.partner.core.util.data.PartnerResellerSiteIdResponseTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.BusinessProcessParameterModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.CustomerModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.PartnerResellerSiteBusinessProcessModelTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction.Transition;
import de.hybris.platform.processengine.model.BusinessProcessParameterModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@UnitTest
public class FetchPartnerResellerDefaultDistributorAccountDetailsActionTest {

    private static final String EMAIL = "test@customer.com";
    private static final String DIST_NUMBER = "0007796517";
    @InjectMocks
    FetchPartnerResellerDefaultDistributorAccountDetailsAction fetchPartnerResellerDefaultDistributorAccountDetailsAction;

    @Mock
    PartnerAccountServiceOutboundIntegrationService outboundIntegrationService;

	 @Mock
	 ModelService modelService;

    Integer maxRetryAllowed;
    Integer retryDelay;

    PartnerResellerSiteBusinessProcessModel processModel;

    PartnerResellerSiteIdResponseData resellerSiteIdResponseData;
    Collection<BusinessProcessParameterModel> processParameterModels;
    CustomerModel customerModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        fetchPartnerResellerDefaultDistributorAccountDetailsAction = new FetchPartnerResellerDefaultDistributorAccountDetailsAction(
            maxRetryAllowed, retryDelay, outboundIntegrationService);
		  fetchPartnerResellerDefaultDistributorAccountDetailsAction.setModelService(modelService);
        resellerSiteIdResponseData = PartnerResellerSiteIdResponseTestDataGenerator.createResellerResponseData(
            null, null, null);
        processParameterModels = new ArrayList<>();
        resellerSiteIdResponseData = PartnerResellerSiteIdResponseTestDataGenerator.createResellerResponseData(
            DIST_NUMBER, null, null);
        List<PartnerDistributorSiteIdResponseData> distributorSiteDetails = new ArrayList<>();
        PartnerDistributorSiteIdResponseData distributorSiteIdResponseData = PartnerDistributorSiteIdResponseTestDataGenerator.createDistributorResponseData();
        distributorSiteDetails.add(distributorSiteIdResponseData);
        Mockito.when(outboundIntegrationService.getDistributorSiteId(Mockito.anyString(),
            Mockito.anyString())).thenReturn(distributorSiteDetails);
    }

    @Test
    public void testExecuteAction_NOK_OnContextParamNotPresent() throws Exception {
        processModel = PartnerResellerSiteBusinessProcessModelTestDataGenerator.createContextParams(
            processParameterModels);
        processModel.setContextParameters(null);
        Transition transition = fetchPartnerResellerDefaultDistributorAccountDetailsAction.executeAction(
            processModel);
        Assert.assertEquals(Transition.NOK, transition);
    }

    @Test
    public void testExecuteAction_OK() throws Exception {
        BusinessProcessParameterModel processParameterModel = BusinessProcessParameterModelTestDataGenerator.createProcessParameterModel(
            PartnercoreConstants.PARTNER_RESELLE_UNIT_PROCESS_RESELLER_PAYLOAD_CONTEXT_PARAM,
            resellerSiteIdResponseData);
        customerModel = CustomerModelTestDataGenerator.createCustomerModel(EMAIL);
        BusinessProcessParameterModel customerProcessParameterModel = BusinessProcessParameterModelTestDataGenerator.createProcessParameterModel(
            PartnercoreConstants.PARTNER_RESELLE_UNIT_PROCESS_CUSTOMER_PAYLOAD_CONTEXT_PARAM,
            customerModel);
        processParameterModels.add(processParameterModel);
        processParameterModels.add(customerProcessParameterModel);
        processModel = PartnerResellerSiteBusinessProcessModelTestDataGenerator.createContextParams(
            processParameterModels);
        Transition transition = fetchPartnerResellerDefaultDistributorAccountDetailsAction.executeAction(
            processModel);
        processModel = PartnerResellerSiteBusinessProcessModelTestDataGenerator.createContextParams(
            processParameterModels);
        Assert.assertEquals(Transition.OK, transition);
    }

    @Test
    public void testExecuteAction_NOK_OnCustomerAsNull() throws Exception {
        BusinessProcessParameterModel processParameterModel = BusinessProcessParameterModelTestDataGenerator.createProcessParameterModel(
            PartnercoreConstants.PARTNER_RESELLE_UNIT_PROCESS_RESELLER_PAYLOAD_CONTEXT_PARAM,
            resellerSiteIdResponseData);
        BusinessProcessParameterModel customerProcessParameterModel = BusinessProcessParameterModelTestDataGenerator.createProcessParameterModel(
            PartnercoreConstants.PARTNER_RESELLE_UNIT_PROCESS_CUSTOMER_PAYLOAD_CONTEXT_PARAM,
            null);
        processParameterModels.add(processParameterModel);
        processParameterModels.add(customerProcessParameterModel);
        processModel = PartnerResellerSiteBusinessProcessModelTestDataGenerator.createContextParams(
            processParameterModels);
        Transition transition = fetchPartnerResellerDefaultDistributorAccountDetailsAction.executeAction(
            processModel);
        Assert.assertEquals(Transition.NOK, transition);
    }

    @Test
    public void testExecuteAction_NOK_OnDefaultDistributorNotPresent() throws Exception {
        BusinessProcessParameterModel processParameterModel = BusinessProcessParameterModelTestDataGenerator.createProcessParameterModel(
            PartnercoreConstants.PARTNER_RESELLE_UNIT_PROCESS_RESELLER_PAYLOAD_CONTEXT_PARAM,
            null);
        processParameterModels.add(processParameterModel);
        processModel = PartnerResellerSiteBusinessProcessModelTestDataGenerator.createContextParams(
            processParameterModels);
        Transition transition = fetchPartnerResellerDefaultDistributorAccountDetailsAction.executeAction(
            processModel);
        Assert.assertEquals(Transition.NOK, transition);
    }
}
