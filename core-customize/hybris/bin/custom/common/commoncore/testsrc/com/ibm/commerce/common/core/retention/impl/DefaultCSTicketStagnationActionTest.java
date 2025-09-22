package com.ibm.commerce.common.core.retention.impl;

import de.hybris.platform.core.PK;
import de.hybris.platform.processing.model.FlexibleSearchRetentionRuleModel;
import de.hybris.platform.retention.ItemToCleanup;
import de.hybris.platform.retention.job.AfterRetentionCleanupJobPerformable;
import de.hybris.platform.ticket.constants.TicketsystemConstants;
import de.hybris.platform.ticket.enums.CsInterventionType;
import de.hybris.platform.ticket.enums.CsResolutionType;
import de.hybris.platform.ticket.model.CsTicketModel;
import de.hybris.platform.ticket.service.TicketBusinessService;
import de.hybris.platform.ticket.service.TicketException;
import de.hybris.platform.util.localization.Localization;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

public class DefaultCSTicketStagnationActionTest {

    @InjectMocks
    private final DefaultCSTicketStagnationAction cleanupAction = new DefaultCSTicketStagnationAction();

    @Mock
    private TicketBusinessService ticketBusinessService;

    @Mock
    private ItemToCleanup item;

    @Mock
    private FlexibleSearchRetentionRuleModel rule;

    @Mock
    private AfterRetentionCleanupJobPerformable retentionJob;

    @Mock
    private CsTicketModel csTicketModel;

    @Mock
    private Object nonTicketModel;

    @Mock
    private de.hybris.platform.servicelayer.model.ModelService modelService;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        cleanupAction.setModelService(modelService);
        cleanupAction.setTicketBusinessService(ticketBusinessService);
    }

    @Test
    public void shouldCleanupAndInvokeHooks() throws TicketException {
        PK pk = PK.fromLong(123L);
        when(item.getPk()).thenReturn(pk);
        when(modelService.get(pk)).thenReturn(csTicketModel);

        try (MockedStatic<Localization> localization = mockStatic(Localization.class)) {
            localization.when(() -> Localization.getLocalizedString(any()))
                    .thenReturn("Closed due to stagnation");

            cleanupAction.cleanup(retentionJob, rule, item);

            verify(ticketBusinessService).resolveTicket(
                    eq(csTicketModel),
                    eq(CsInterventionType.TICKETMESSAGE),
                    eq(CsResolutionType.CLOSED),
                    eq("Closed due to stagnation"));
        }
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowWhenItemIsNotCsTicketModel() {
        PK pk = PK.fromLong(123L);
        when(item.getPk()).thenReturn(pk);
        when(modelService.get(pk)).thenReturn(new Object());

        cleanupAction.cleanup(retentionJob, rule, item);
    }

    @Test
    public void shouldLogErrorWhenTicketResolutionFails() throws TicketException {
        PK pk = PK.fromLong(123L);
        when(item.getPk()).thenReturn(pk);
        when(modelService.get(pk)).thenReturn(csTicketModel);

        try (MockedStatic<Localization> localization = mockStatic(Localization.class)) {
            localization.when(() -> Localization.getLocalizedString(any()))
                    .thenReturn("Closed due to stagnation");

            doThrow(new TicketException("Simulated error"))
                    .when(ticketBusinessService)
                    .resolveTicket(any(), any(), any(), any());

            cleanupAction.cleanup(retentionJob, rule, item);

            verify(ticketBusinessService).resolveTicket(any(), any(), any(), any());
        }
    }

    @Test
    public void shouldReturnLocalizedMessage() {
        try (MockedStatic<Localization> localization = mockStatic(Localization.class)) {
            localization.when(() -> Localization.getLocalizedString(any()))
                    .thenReturn("Ticket closed due to inactivity");

            String result = cleanupAction.fetchLocalizedClosedTicketMessage();

            assertEquals("Ticket closed due to inactivity", result);
        }
    }

    @Test
    public void shouldUseFallbackMessageWhenLocalizationReturnsKey() {
        try (MockedStatic<Localization> localization = mockStatic(Localization.class)) {
            localization.when(() -> Localization.getLocalizedString(any()))
                    .thenReturn(TicketsystemConstants.SUPPORT_TICKET_STAGNATION_KEY);

            String result = cleanupAction.fetchLocalizedClosedTicketMessage();

            assertEquals(TicketsystemConstants.SUPPORT_TICKET_STAGNATION_DEFAULT_CLOSING_MESSAGE, result);
        }
    }
}
