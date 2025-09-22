package com.ibm.commerce.partner.facades.actions.customer;

import com.ibm.commerce.partner.core.model.PartnerResellerSiteBusinessProcessModel;
import com.ibm.commerce.partner.core.model.PartnerStoreFrontCustomerProcessModel;
import com.ibm.commerce.partner.core.util.model.B2BCustomerModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.PartnerResellerSiteBusinessProcessModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.PartnerStoreFrontCustomerProcessModelTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction.Transition;
import de.hybris.platform.processengine.enums.ProcessState;
import java.util.ArrayList;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

@UnitTest
public class CheckAllSubProcessCompletionActionTest {

    private static final String EMAIL = "test@test.com";

    @InjectMocks
    CheckAllSubProcessCompletionAction checkAllSubProcessCompletionAction;

    Integer maxRetryAllowed;
    Integer retryDelay;

    PartnerStoreFrontCustomerProcessModel customerProcessModel;
    Collection<PartnerResellerSiteBusinessProcessModel> resellerSiteBusinessProcessModels;
    PartnerResellerSiteBusinessProcessModel resellerSiteBusinessProcessModel;
    B2BCustomerModel customerModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        checkAllSubProcessCompletionAction = new CheckAllSubProcessCompletionAction(maxRetryAllowed,
            retryDelay);
        customerModel = B2BCustomerModelTestDataGenerator.createB2BCustomerModel(
            EMAIL);
        resellerSiteBusinessProcessModels = new ArrayList<>();
    }

    @Test
    public void testExecute_Transition_OK() throws Exception {
        resellerSiteBusinessProcessModel = PartnerResellerSiteBusinessProcessModelTestDataGenerator.createResellerProcessModel(
            ProcessState.SUCCEEDED, null);
        resellerSiteBusinessProcessModels.add(resellerSiteBusinessProcessModel);
        customerProcessModel = PartnerStoreFrontCustomerProcessModelTestDataGenerator.createProcessModel(
            customerModel, resellerSiteBusinessProcessModels);
        Transition transition = checkAllSubProcessCompletionAction.executeAction(customerProcessModel);
        Assert.assertEquals(Transition.OK, transition);
    }

    @Test
    public void testExecute_Transition_NOK() throws Exception {
        resellerSiteBusinessProcessModel = PartnerResellerSiteBusinessProcessModelTestDataGenerator.createResellerProcessModel(
            ProcessState.RUNNING, null);
        resellerSiteBusinessProcessModels.add(resellerSiteBusinessProcessModel);
        customerProcessModel = PartnerStoreFrontCustomerProcessModelTestDataGenerator.createProcessModel(
            customerModel, resellerSiteBusinessProcessModels);
        Transition transition = checkAllSubProcessCompletionAction.executeAction(customerProcessModel);
        Assert.assertEquals(Transition.NOK, transition);
    }
}
