package com.ibm.commerce.partner.facades.actions.customer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import com.ibm.commerce.partner.company.data.IbmPartnerB2BUnitData;
import com.ibm.commerce.partner.core.company.distributor.data.response.PartnerDistributorSiteIdResponseData;
import com.ibm.commerce.partner.core.company.reseller.data.response.PartnerResellerSiteCustomerTierInfoResponseData;
import com.ibm.commerce.partner.core.company.reseller.data.response.PartnerResellerSiteIdResponseData;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.PartnerResellerSiteBusinessProcessModel;
import com.ibm.commerce.partner.core.model.PartnerStoreFrontCustomerProcessModel;
import com.ibm.commerce.partner.core.util.data.PartnerDistributorSiteIdResponseTestDataGenerator;
import com.ibm.commerce.partner.core.util.data.PartnerResellerSiteCustomerTierInfoResponseTestDataGenerator;
import com.ibm.commerce.partner.core.util.data.PartnerResellerSiteIdResponseTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.B2BCustomerModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.B2BUnitModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.BusinessProcessParameterModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.CustomerModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.PartnerResellerSiteBusinessProcessModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.PartnerStoreFrontCustomerProcessModelTestDataGenerator;
import com.ibm.commerce.partner.facades.company.PartnerB2BUnitFacade;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction.Transition;
import de.hybris.platform.processengine.enums.ProcessState;
import de.hybris.platform.processengine.model.BusinessProcessParameterModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@UnitTest
public class PersistResellerDetailsActionTest {

    private static final String DIST_NUMBER = "1000123";
    private static final String CUSTOMER_UID = "test@test.com";
    private static final String PARENT_UID = "1l3h5ulb";

    @InjectMocks
    PersistResellerDetailsAction persistResellerDetailsAction;

    @Mock
    Converter<PartnerResellerSiteIdResponseData, IbmPartnerB2BUnitData> partnerB2BUnitDataConverter;
    @Mock
    PartnerB2BUnitFacade partnerB2BUnitFacade;
    @Mock
    ModelService modelService;

    Integer maxRetryAllowed;
    Integer retryDelay;
    @Mock
    PartnerResellerSiteBusinessProcessModel resellerSiteBusinessProcessModel;

    Collection<BusinessProcessParameterModel> processParameterModels;
    PartnerResellerSiteIdResponseData resellerSiteIdResponseData;

    PartnerDistributorSiteIdResponseData distributorResponse;
    CustomerModel customerModel;
    @Mock
    private BusinessProcessParameterModel businessProcessParameterModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        persistResellerDetailsAction = new PersistResellerDetailsAction(maxRetryAllowed, retryDelay,
            partnerB2BUnitDataConverter, partnerB2BUnitFacade);
        persistResellerDetailsAction.setModelService(modelService);
        resellerSiteBusinessProcessModel = PartnerResellerSiteBusinessProcessModelTestDataGenerator.createResellerProcessModel(
            ProcessState.CREATED, null);
        processParameterModels = new ArrayList<>();
        distributorResponse = PartnerDistributorSiteIdResponseTestDataGenerator.createDistributorResponseData();
        PartnerResellerSiteCustomerTierInfoResponseData tierInfo = PartnerResellerSiteCustomerTierInfoResponseTestDataGenerator.createTierInfoResponseData(
            true, true);
        resellerSiteIdResponseData = PartnerResellerSiteIdResponseTestDataGenerator.createResellerResponseData(
            DIST_NUMBER, tierInfo, distributorResponse);
        B2BUnitModel b2BUnitModel = B2BUnitModelTestDataGenerator.createB2BUnitModelActive(
            PARENT_UID, true);
        when(partnerB2BUnitFacade.getOrCreate(Mockito.any())).thenReturn(b2BUnitModel);
        B2BCustomerModel b2BCustomerModel = B2BCustomerModelTestDataGenerator.createB2BCustomerModel(
            CUSTOMER_UID);
        Collection<PartnerResellerSiteBusinessProcessModel> subProcess = new ArrayList<>();
        PartnerResellerSiteBusinessProcessModel processModel = PartnerResellerSiteBusinessProcessModelTestDataGenerator.createResellerProcessModel(
            ProcessState.CREATED, b2BUnitModel);
        subProcess.add(processModel);
        PartnerStoreFrontCustomerProcessModel parentProcessModel = PartnerStoreFrontCustomerProcessModelTestDataGenerator.createProcessModel(
            b2BCustomerModel, subProcess);
        resellerSiteBusinessProcessModel.setParentProcess(parentProcessModel);
    }

    @Test
    public void testExecuteAction_NOK() throws Exception {
        Transition transition = persistResellerDetailsAction.executeAction(
            resellerSiteBusinessProcessModel);
        assertEquals(Transition.NOK, transition);
    }

    @Test
    public void testExecuteAction_CustomerAsNull() throws Exception {
        BusinessProcessParameterModel resellerProcessParameterModel = BusinessProcessParameterModelTestDataGenerator.createProcessParameterModel(
				  PartnercoreConstants.PARTNER_RESELLE_UNIT_PROCESS_RESELLER_DISTRIBUTOR_PAYLOAD_CONTEXT_PARAM,
            resellerSiteIdResponseData);
        customerModel = CustomerModelTestDataGenerator.createCustomerModel(CUSTOMER_UID);
        BusinessProcessParameterModel customerProcessParameterModel = BusinessProcessParameterModelTestDataGenerator.createProcessParameterModel(
            PartnercoreConstants.PARTNER_RESELLE_UNIT_PROCESS_CUSTOMER_PAYLOAD_CONTEXT_PARAM, null);
        processParameterModels.add(resellerProcessParameterModel);
        processParameterModels.add(customerProcessParameterModel);
        resellerSiteBusinessProcessModel.setContextParameters(processParameterModels);
        Transition transition = persistResellerDetailsAction.executeAction(
            resellerSiteBusinessProcessModel);
        assertEquals(Transition.NOK, transition);
    }

    @Test
    public void testExecuteAction_ResellerAsNull() throws Exception {
        BusinessProcessParameterModel resellerProcessParameterModel = BusinessProcessParameterModelTestDataGenerator.createProcessParameterModel(
				  PartnercoreConstants.PARTNER_RESELLE_UNIT_PROCESS_RESELLER_DISTRIBUTOR_PAYLOAD_CONTEXT_PARAM, null);
        customerModel = CustomerModelTestDataGenerator.createCustomerModel(CUSTOMER_UID);
        BusinessProcessParameterModel customerProcessParameterModel = BusinessProcessParameterModelTestDataGenerator.createProcessParameterModel(
            PartnercoreConstants.PARTNER_RESELLE_UNIT_PROCESS_CUSTOMER_PAYLOAD_CONTEXT_PARAM, null);
        processParameterModels.add(resellerProcessParameterModel);
        processParameterModels.add(customerProcessParameterModel);
        resellerSiteBusinessProcessModel.setContextParameters(processParameterModels);
        resellerSiteBusinessProcessModel.setContextParameters(null);
        Transition transition = persistResellerDetailsAction.executeAction(
            resellerSiteBusinessProcessModel);
        assertEquals(Transition.NOK, transition);
    }

    @Test
    public void testExecuteAction_OK() throws Exception {
        BusinessProcessParameterModel resellerProcessParameterModel = BusinessProcessParameterModelTestDataGenerator.createProcessParameterModel(
				  PartnercoreConstants.PARTNER_RESELLE_UNIT_PROCESS_RESELLER_DISTRIBUTOR_PAYLOAD_CONTEXT_PARAM,
            resellerSiteIdResponseData);
        customerModel = CustomerModelTestDataGenerator.createCustomerModel(CUSTOMER_UID);
        BusinessProcessParameterModel customerProcessParameterModel = BusinessProcessParameterModelTestDataGenerator.createProcessParameterModel(
            PartnercoreConstants.PARTNER_RESELLE_UNIT_PROCESS_CUSTOMER_PAYLOAD_CONTEXT_PARAM,
            customerModel);
        processParameterModels.add(resellerProcessParameterModel);
        processParameterModels.add(customerProcessParameterModel);
        resellerSiteBusinessProcessModel.setContextParameters(processParameterModels);
        Transition transition = persistResellerDetailsAction.executeAction(
            resellerSiteBusinessProcessModel);
        assertEquals(Transition.OK, transition);
    }
    @Test
    public void testExecuteAction_ResellerNull_CustomerNotNull() throws Exception {
        CustomerModel customerModel = CustomerModelTestDataGenerator.createCustomerModel(CUSTOMER_UID);

        BusinessProcessParameterModel resellerParam = BusinessProcessParameterModelTestDataGenerator.createProcessParameterModel(
            PartnercoreConstants.PARTNER_RESELLE_UNIT_PROCESS_RESELLER_DISTRIBUTOR_PAYLOAD_CONTEXT_PARAM,
            null);

        BusinessProcessParameterModel customerParam = BusinessProcessParameterModelTestDataGenerator.createProcessParameterModel(
            PartnercoreConstants.PARTNER_RESELLE_UNIT_PROCESS_CUSTOMER_PAYLOAD_CONTEXT_PARAM,
            customerModel);

        processParameterModels.add(resellerParam);
        processParameterModels.add(customerParam);
        resellerSiteBusinessProcessModel.setContextParameters(processParameterModels);

        Transition transition = persistResellerDetailsAction.executeAction(resellerSiteBusinessProcessModel);
        assertEquals(Transition.NOK, transition);
    }
    @Test
    public void testExecuteAction_B2BUnitModelNull() throws Exception {
        when(partnerB2BUnitFacade.getOrCreate(Mockito.any())).thenReturn(null);

        BusinessProcessParameterModel resellerParam = BusinessProcessParameterModelTestDataGenerator.createProcessParameterModel(
            PartnercoreConstants.PARTNER_RESELLE_UNIT_PROCESS_RESELLER_DISTRIBUTOR_PAYLOAD_CONTEXT_PARAM,
            resellerSiteIdResponseData);

        customerModel = CustomerModelTestDataGenerator.createCustomerModel(CUSTOMER_UID);
        BusinessProcessParameterModel customerParam = BusinessProcessParameterModelTestDataGenerator.createProcessParameterModel(
            PartnercoreConstants.PARTNER_RESELLE_UNIT_PROCESS_CUSTOMER_PAYLOAD_CONTEXT_PARAM,
            customerModel);

        processParameterModels.add(resellerParam);
        processParameterModels.add(customerParam);
        resellerSiteBusinessProcessModel.setContextParameters(processParameterModels);

        Transition transition = persistResellerDetailsAction.executeAction(resellerSiteBusinessProcessModel);
        assertEquals(Transition.NOK, transition);
    }


}
