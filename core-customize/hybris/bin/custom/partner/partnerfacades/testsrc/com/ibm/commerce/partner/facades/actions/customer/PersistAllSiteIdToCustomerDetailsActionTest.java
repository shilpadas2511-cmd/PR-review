package com.ibm.commerce.partner.facades.actions.customer;

import static de.hybris.platform.testframework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ibm.commerce.partner.core.b2b.company.service.PartnerB2BUnitService;
import com.ibm.commerce.partner.core.company.reseller.data.response.PartnerResellerSiteIdResponseData;
import com.ibm.commerce.partner.core.model.PartnerResellerSiteBusinessProcessModel;
import com.ibm.commerce.partner.core.model.PartnerStoreFrontCustomerProcessModel;
import com.ibm.commerce.partner.core.util.data.PartnerResellerSiteIdResponseTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.B2BCustomerModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.B2BUnitModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.PartnerResellerSiteBusinessProcessModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.PartnerStoreFrontCustomerProcessModelTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction.Transition;
import de.hybris.platform.processengine.enums.ProcessState;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.RetryLaterException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@UnitTest
public class PersistAllSiteIdToCustomerDetailsActionTest {

    private static final String EMAIL = "test@test.com";
    private static final String ACCOUNT_ID = "sap0007970368";
    private static final String DIST_NUMBER = "0007000379";
    private static final String UNIT_UID = "0007929043";
    private static final String EXISTING_UNIT_UID = "0030180542";
    @InjectMocks
    PersistAllSiteIdToCustomerDetailsAction persistAllSiteIdToCustomerDetailsAction;

    @Mock
    ModelService modelService;

    Integer maxRetryAllowed;
    Integer retryDelay;

    PartnerStoreFrontCustomerProcessModel customerProcessModel;
    B2BCustomerModel b2BCustomerModel;

    List<PartnerResellerSiteBusinessProcessModel> subProcesses;


    String defaultB2BUnitCode;
    @Mock
    PartnerB2BUnitService<B2BUnitModel, B2BCustomerModel> b2BUnitService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        defaultB2BUnitCode = "defaultPartnerB2BUnit";
        persistAllSiteIdToCustomerDetailsAction = new PersistAllSiteIdToCustomerDetailsAction(
            maxRetryAllowed, retryDelay, defaultB2BUnitCode, b2BUnitService);
        persistAllSiteIdToCustomerDetailsAction.setModelService(modelService);
        b2BCustomerModel = B2BCustomerModelTestDataGenerator.createB2BCustomerModel(EMAIL);
        B2BUnitModel unitModel = B2BUnitModelTestDataGenerator.createB2BUnitModel(UNIT_UID, true,
            null);
        B2BUnitModel existingUnitModel = B2BUnitModelTestDataGenerator.createB2BUnitModel(
            EXISTING_UNIT_UID, true, null);
        Set<PrincipalGroupModel> groups = new HashSet<>();
        groups.add(existingUnitModel);
        b2BCustomerModel.setGroups(groups);
        PartnerResellerSiteBusinessProcessModel subProcessModel = PartnerResellerSiteBusinessProcessModelTestDataGenerator.createResellerProcessModel(
            ProcessState.RUNNING, unitModel);
        subProcesses = new ArrayList<>();
        PartnerResellerSiteIdResponseData resellerSiteIdResponseData = PartnerResellerSiteIdResponseTestDataGenerator.createResellerResponseData(
            DIST_NUMBER, null, null);
        resellerSiteIdResponseData.setAccountId(ACCOUNT_ID);
        subProcesses.add(subProcessModel);
        customerProcessModel = PartnerStoreFrontCustomerProcessModelTestDataGenerator.createProcessModel(
            b2BCustomerModel, subProcesses);
    }

    @Test
    public void testExecute_OK() throws Exception {
        Transition transition = persistAllSiteIdToCustomerDetailsAction.executeAction(
            customerProcessModel);
        Assert.assertEquals(Transition.OK, transition);
    }

    @Test
    public void testExecute_NOK() throws Exception {
        customerProcessModel.setSubProcesses(null);
        Transition transition = persistAllSiteIdToCustomerDetailsAction.executeAction(
            customerProcessModel);
        Assert.assertEquals(Transition.NOK, transition);
    }
    @Test
    public void testGetDefaultB2BUnitCode() {
        String result = persistAllSiteIdToCustomerDetailsAction.getDefaultB2BUnitCode();
        assertEquals(defaultB2BUnitCode, result);
    }

    @Test
    public void testExecuteAction_WhenNoValidUnits_AssignsDefaultUnit() throws RetryLaterException, Exception {
        B2BCustomerModel b2bCustomer = new B2BCustomerModel();
        b2bCustomer.setGroups(new HashSet<>());
        String defaultUnitCode = "DEFAULT_UNIT";

        PartnerStoreFrontCustomerProcessModel processModel = new PartnerStoreFrontCustomerProcessModel();
        processModel.setCustomer(b2bCustomer);

        PartnerResellerSiteBusinessProcessModel subProcess = mock(PartnerResellerSiteBusinessProcessModel.class);
        when(subProcess.getUnit()).thenReturn(null);

        processModel.setSubProcesses(List.of(subProcess));

        B2BUnitModel defaultB2BUnit = mock(B2BUnitModel.class);
        when(b2BUnitService.getUnitForUid(defaultUnitCode, true)).thenReturn(defaultB2BUnit);
        persistAllSiteIdToCustomerDetailsAction.executeAction(processModel);
    }
}
