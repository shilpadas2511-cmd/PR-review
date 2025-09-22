package com.ibm.commerce.partner.core.order.strategies.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.enums.QuoteAction;
import de.hybris.platform.commerceservices.order.strategies.QuoteStateSelectionStrategy;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import de.hybris.platform.core.model.order.QuoteModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPartnerQuoteUpdateStateStrategyTest {

    private DefaultPartnerQuoteUpdateStateStrategy strategy;

    @Mock
    QuoteStateSelectionStrategy quoteStateSelectionStrategy;
    @Mock
    private UserModel userModel;
    @Mock
    private ConfigurationService configurationService;
    @Mock
    private Configuration configuration;

    private static final String QUOTE_STATUS_ORDERED_LIST = "CREATED,SUBMITTED,APPROVED,ORDERED,NOT_APPROVED,EXPIRED,READY_TO_ORDER";

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(configuration.getBoolean(PartnercoreConstants.QUOTE_STATUS_UPDATES_FEATURE_FLAG, false)).thenReturn(false);
        when(configurationService.getConfiguration()).thenReturn(configuration);
        strategy = new DefaultPartnerQuoteUpdateStateStrategy(QUOTE_STATUS_ORDERED_LIST,
            configurationService);
    }

    @Test
    public void testUpdateQuoteState_CloneBuyerCreated() {
        var quoteModel = new IbmPartnerQuoteModel();
        quoteModel.setState(QuoteState.CLONE_BUYER_CREATED);

        strategy.updateQuoteState(QuoteAction.APPROVE, quoteModel, userModel);

        assertEquals(QuoteState.CLONE_BUYER_DRAFT, quoteModel.getState());
    }

    @Test
    public void testUpdatePartnerQuoteState_AwaitingInternalApproval() {
        var quoteModel = new IbmPartnerQuoteModel();

        strategy.updatePartnerQuoteState(quoteModel, "Awaiting Internal Approval", "N/A");

        assertEquals(QuoteState.IN_REVIEW, quoteModel.getState());
    }

    @Test
    public void testUpdatePartnerQuoteState_Rejected() {
        var quoteModel = new IbmPartnerQuoteModel();

        strategy.updatePartnerQuoteState(quoteModel, "REJECTED", "N/A");

        assertEquals(QuoteState.NOT_APPROVED, quoteModel.getState());
    }

    @Test
    public void testUpdatePartnerQuoteState_Expired() {
        var quoteModel = new IbmPartnerQuoteModel();

        strategy.updatePartnerQuoteState(quoteModel, "EXPIRED", "ANY_STATE");

        assertEquals(QuoteState.EXPIRED, quoteModel.getState());
    }

    @Test
    public void testUpdatePartnerQuoteState_ReadyToOrder() {
        var quoteModel = new IbmPartnerQuoteModel();

        strategy.updatePartnerQuoteState(quoteModel, "SUBMITTED", "Ready to order");

        assertEquals(QuoteState.READY_TO_ORDER, quoteModel.getState());
    }

    @Test
    public void testUpdatePartnerQuoteState_Ordered() {
        var quoteModel = new IbmPartnerQuoteModel();

        strategy.updatePartnerQuoteState(quoteModel, "SUBMITTED", "ORDERED");

        assertEquals(QuoteState.ORDERED, quoteModel.getState());
    }

    @Test
    public void testUpdatePartnerQuoteState_InvalidStates() {
        var quoteModel = new IbmPartnerQuoteModel();

        strategy.updatePartnerQuoteState(quoteModel, "INVALID_STATE", "INVALID_STATE");

        assertNull(quoteModel.getState());
    }

    @Test
    public void testUpdateQuoteState_NonCloneBuyerCreated_CallsSuper() {
        var quoteModel = new IbmPartnerQuoteModel();
        quoteModel.setState(QuoteState.SUBMITTED); // Not CLONE_BUYER_CREATED
        // Should call super.updateQuoteState (no exception means it works)
        strategy.updateQuoteState(QuoteAction.APPROVE, quoteModel, userModel);
        // No assertion needed, just ensure no exception and state unchanged
        assertEquals(QuoteState.SUBMITTED, quoteModel.getState());
    }

    @Test
    public void testUpdateQuoteActiveIndex_CancelledState() {
        var quoteModel = new IbmPartnerQuoteModel();
        quoteModel.setState(QuoteState.CANCELLED);
        // Should set quoteIndexActive to false
        strategy.updateQuoteState(QuoteAction.APPROVE, quoteModel, userModel);
        assertEquals(Boolean.FALSE, quoteModel.getQuoteIndexActive());
    }

    @Test
    public void testUpdatePartnerQuoteState_OrderedStatusArrayAndFlagTrue() {
        var quoteModel = new IbmPartnerQuoteModel();
        // Simulate feature flag true
        when(configuration.getBoolean(PartnercoreConstants.QUOTE_STATUS_UPDATES_FEATURE_FLAG, false)).thenReturn(true);
        // eccState matches orderedStatusArray ("ordered" is in the list)
        strategy.updatePartnerQuoteState(quoteModel, "SUBMITTED", "ordered");
        assertEquals(QuoteState.ORDERED, quoteModel.getState());
    }

    @Test
    public void testUpdatePartnerQuoteState_OrderedStatusArrayAndFlagFalse() {
        var quoteModel = new IbmPartnerQuoteModel();
        // Simulate feature flag false
        when(configuration.getBoolean(PartnercoreConstants.QUOTE_STATUS_UPDATES_FEATURE_FLAG, false)).thenReturn(false);
        // eccState does not match orderedStatusArray, but flag is false so should still set ORDERED
        strategy.updatePartnerQuoteState(quoteModel, "SUBMITTED", "not_in_list");
        assertEquals(QuoteState.ORDERED, quoteModel.getState());
    }

    @Test
    public void testIsQuoteStatusOrderedFlag_True() {
        when(configuration.getBoolean(PartnercoreConstants.QUOTE_STATUS_UPDATES_FEATURE_FLAG, false)).thenReturn(true);
        assertEquals(true, strategy.isQuoteStatusOrderedFlag());
    }

    @Test
    public void testIsQuoteStatusOrderedFlag_False() {
        when(configuration.getBoolean(PartnercoreConstants.QUOTE_STATUS_UPDATES_FEATURE_FLAG, false)).thenReturn(false);
        assertEquals(false, strategy.isQuoteStatusOrderedFlag());
    }

    @Test
    public void testGetConfigurationService() {
        assertEquals(configurationService, strategy.getConfigurationService());
    }

    @Test
    public void testUpdatePartnerQuoteState_SubmittedAndNotApplicable() {
        var quoteModel = new IbmPartnerQuoteModel();
        strategy.updatePartnerQuoteState(quoteModel, "SUBMITTED", "N/A");
        assertEquals(QuoteState.IN_REVIEW, quoteModel.getState());
    }

    @Test
    public void testUpdatePartnerQuoteState_SubmittedNotInList_FlagTrue() {
        var quoteModel = new IbmPartnerQuoteModel();
        when(configuration.getBoolean(PartnercoreConstants.QUOTE_STATUS_UPDATES_FEATURE_FLAG, false)).thenReturn(true);
        // eccState not in orderedStatusArray, flag true, should not set ORDERED
        strategy.updatePartnerQuoteState(quoteModel, "SUBMITTED", "not_in_list");
        // Should not set state to ORDERED
        assertNull(quoteModel.getState());
    }

    @Test
    public void testUpdatePartnerQuoteState_RejectedNotNotApplicable() {
        var quoteModel = new IbmPartnerQuoteModel();
        strategy.updatePartnerQuoteState(quoteModel, "REJECTED", "READY_TO_ORDER");
        // Should not set state to NOT_APPROVED
        assertNull(quoteModel.getState());
    }

    @Test
    public void testUpdatePartnerQuoteState_AwaitingInternalApprovalNotNotApplicable() {
        var quoteModel = new IbmPartnerQuoteModel();
        strategy.updatePartnerQuoteState(quoteModel, "AWAITING_INTERNAL_APPROVAL", "READY_TO_ORDER");
        // Should not set state to IN_REVIEW
        assertNull(quoteModel.getState());
    }

    @Test
    public void testUpdatePartnerQuoteState_AwaitingInternalApproval_ExactConstant() {
        var quoteModel = new IbmPartnerQuoteModel();
        strategy.updatePartnerQuoteState(quoteModel, PartnercoreConstants.AWAITING_INTERNAL_APPROVAL, PartnercoreConstants.NOT_APPLICABLE);
        assertEquals(QuoteState.IN_REVIEW, quoteModel.getState());
    }

    @Test
    public void testUpdatePartnerQuoteState_Rejected_ExactConstant() {
        var quoteModel = new IbmPartnerQuoteModel();
        strategy.updatePartnerQuoteState(quoteModel, PartnercoreConstants.REJECTED, PartnercoreConstants.NOT_APPLICABLE);
        assertEquals(QuoteState.NOT_APPROVED, quoteModel.getState());
    }

    @Test
    public void testUpdatePartnerQuoteState_Expired_ExactConstant() {
        var quoteModel = new IbmPartnerQuoteModel();
        strategy.updatePartnerQuoteState(quoteModel, PartnercoreConstants.EXPIRED, "ANY_STATE");
        assertEquals(QuoteState.EXPIRED, quoteModel.getState());
    }

    @Test
    public void testUpdatePartnerQuoteState_ReadyToOrder_ExactConstant() {
        var quoteModel = new IbmPartnerQuoteModel();
        strategy.updatePartnerQuoteState(quoteModel, PartnercoreConstants.SUBMITTED, PartnercoreConstants.READY_TO_ORDER);
        assertEquals(QuoteState.READY_TO_ORDER, quoteModel.getState());
    }

    @Test
    public void testUpdatePartnerQuoteState_Ordered_ExactConstant() {
        var quoteModel = new IbmPartnerQuoteModel();
        when(configuration.getBoolean(PartnercoreConstants.QUOTE_STATUS_UPDATES_FEATURE_FLAG, false)).thenReturn(true);
        strategy.updatePartnerQuoteState(quoteModel, PartnercoreConstants.SUBMITTED, "ORDERED");
        assertEquals(QuoteState.ORDERED, quoteModel.getState());
    }

    @Test
    public void testUpdateQuoteActiveIndex_Direct() throws Exception {
        var quoteModel = new IbmPartnerQuoteModel();
        quoteModel.setState(QuoteState.CANCELLED);
        // Use reflection to call private method if needed
        var method = DefaultPartnerQuoteUpdateStateStrategy.class.getDeclaredMethod("updateQuoteActiveIndex", QuoteModel.class);
        method.setAccessible(true);
        method.invoke(strategy, quoteModel);
        assertEquals(Boolean.FALSE, quoteModel.getQuoteIndexActive());
    }

    @Test
    public void testUpdatePartnerQuoteState_SubmittedAndNotApplicable_ExactMatch() {
        var quoteModel = new IbmPartnerQuoteModel();
        strategy.updatePartnerQuoteState(quoteModel, PartnercoreConstants.SUBMITTED, PartnercoreConstants.NOT_APPLICABLE); // SUBMITTED = "Submitted", NOT_APPLICABLE = "N/A"
        assertEquals(QuoteState.IN_REVIEW, quoteModel.getState());
    }
}
