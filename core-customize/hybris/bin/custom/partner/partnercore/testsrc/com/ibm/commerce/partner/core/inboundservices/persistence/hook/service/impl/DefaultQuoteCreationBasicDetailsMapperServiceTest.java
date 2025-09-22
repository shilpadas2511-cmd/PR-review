package com.ibm.commerce.partner.core.inboundservices.persistence.hook.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.enums.PartnerQuoteChannelEnum;
import com.ibm.commerce.partner.core.inboundservices.persistence.hook.service.impl.DefaultQuoteCreationBasicDetailsMapperService;
import com.ibm.commerce.partner.core.model.CpqIbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.CpqIbmPartnerUserModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.order.strategies.impl.DefaultPartnerQuoteUpdateStateStrategy;
import com.ibm.commerce.partner.core.user.service.PartnerUserService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.enums.SalesApplication;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.store.services.BaseStoreService;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultQuoteCreationBasicDetailsMapperServiceTest {

    @InjectMocks
    DefaultQuoteCreationBasicDetailsMapperService defaultQuoteCreationBasicDetailsMapperService;
    @Mock
    private CommonI18NService commonI18NService;
    @Mock
    private PartnerUserService partnerUserService;
    @Mock
    private CpqIbmPartnerQuoteModel cpqIbmPartnerQuote;
    @Mock
    private IbmPartnerQuoteModel ibmPartnerQuoteModel;
    @Mock
    private CpqIbmPartnerUserModel quoteCreator;
    @Mock
    private CpqIbmPartnerUserModel quoteSubmitter;
    @Mock
    private CurrencyModel currencyModel;
    @Mock
    private UserModel quoteCreatorUserModel;
    @Mock
    private UserModel quoteSubmiterUserModel;
    @Mock
    private BaseStoreService baseStoreService;
    @Mock
    private DefaultPartnerQuoteUpdateStateStrategy quoteUpdateStateStrategy;

    @Mock
    SalesApplication salesApplication;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        defaultQuoteCreationBasicDetailsMapperService = new DefaultQuoteCreationBasicDetailsMapperService(
            partnerUserService, commonI18NService, baseStoreService,quoteUpdateStateStrategy);
    }

    @Test
    public void shouldMapBasicDetails() {
        String code = "Q123";
        String name = "Test Quote";
        String creatorEmail = "creator@example.com";
        String submitterEmail = "submitter@example.com";
        String currencyCode = "USD";
        Date date = new Date();
        Date submittedDate = new Date();
        Date expirationDate = new Date();

        when(cpqIbmPartnerQuote.getCode()).thenReturn(code);
        when(cpqIbmPartnerQuote.getName()).thenReturn(name);
        when(cpqIbmPartnerQuote.getQuoteCreator()).thenReturn(quoteCreator);
        when(cpqIbmPartnerQuote.getQuoteSubmitter()).thenReturn(quoteSubmitter);
        when(cpqIbmPartnerQuote.getCurrency()).thenReturn(currencyCode);
        when(cpqIbmPartnerQuote.getDate()).thenReturn(date);
        when(cpqIbmPartnerQuote.getSubmittedDate()).thenReturn(submittedDate);
        when(cpqIbmPartnerQuote.getQuoteExpirationDate()).thenReturn(expirationDate);

        when(quoteCreator.getEmail()).thenReturn(creatorEmail);
        when(quoteSubmitter.getEmail()).thenReturn(submitterEmail);
        quoteCreatorUserModel.setUid(creatorEmail);
        when(partnerUserService.getUserForUID(creatorEmail)).thenReturn(quoteCreatorUserModel);
        quoteSubmiterUserModel.setUid(submitterEmail);
        when(partnerUserService.getUserForUID(submitterEmail)).thenReturn(quoteSubmiterUserModel);
        when(commonI18NService.getCurrency(currencyCode)).thenReturn(currencyModel);
        when(cpqIbmPartnerQuote.getCpqDistributionChannel()).thenReturn(
            PartnerQuoteChannelEnum.H.getCode());

        defaultQuoteCreationBasicDetailsMapperService.map(cpqIbmPartnerQuote, ibmPartnerQuoteModel);

        verify(ibmPartnerQuoteModel).setCode(code);
        verify(ibmPartnerQuoteModel).setName(name);
        verify(ibmPartnerQuoteModel).setCreator(quoteCreatorUserModel);
        verify(ibmPartnerQuoteModel).setSubmitter(quoteSubmiterUserModel);
        verify(ibmPartnerQuoteModel).setCurrency(currencyModel);
        verify(ibmPartnerQuoteModel).setDate(date);
        verify(ibmPartnerQuoteModel).setSubmittedDate(submittedDate);
        verify(ibmPartnerQuoteModel).setQuoteExpirationDate(expirationDate);
    }

    @Test
    public void testSetPartnerquoteDetails_Success() {
        String email = "submitter@example.com";
        String currency = "USD";
        String cpqQuoteNumber = "Q12345";
        String eccQuoteNumber = "ECC123";
        String cpqQuoteExternalId = "EXT123";
        String salesApplication = "SalesApp1";
        String cpqDistributionChannel = "J";
        CpqIbmPartnerUserModel cpqIbmPartnerUserModel = mock(CpqIbmPartnerUserModel.class);
        when(cpqIbmPartnerQuote.getQuoteSubmitter()).thenReturn(cpqIbmPartnerUserModel);
        when(cpqIbmPartnerUserModel.getEmail()).thenReturn(email);
        when(partnerUserService.getUserForUID(email)).thenReturn(mock(UserModel.class));
        when(cpqIbmPartnerQuote.getCurrency()).thenReturn(currency);
        when(commonI18NService.getCurrency(currency)).thenReturn(mock(CurrencyModel.class));
        when(cpqIbmPartnerQuote.getDate()).thenReturn(new Date());
        when(cpqIbmPartnerQuote.getSubmittedDate()).thenReturn(new Date());
        when(cpqIbmPartnerQuote.getQuoteExpirationDate()).thenReturn(new Date());
        when(cpqIbmPartnerQuote.getCpqQuoteNumber()).thenReturn(cpqQuoteNumber);
        when(cpqIbmPartnerQuote.getEccQuoteNumber()).thenReturn(eccQuoteNumber);
        when(cpqIbmPartnerQuote.getCpqQuoteExternalId()).thenReturn(cpqQuoteExternalId);
        when(cpqIbmPartnerQuote.getSalesApplication()).thenReturn(
            SalesApplication.valueOf(salesApplication));
        when(cpqIbmPartnerQuote.getCpqDistributionChannel()).thenReturn(cpqDistributionChannel);
        defaultQuoteCreationBasicDetailsMapperService.setPartnerquoteDetails(cpqIbmPartnerQuote, ibmPartnerQuoteModel);
        verify(ibmPartnerQuoteModel).setSubmitter(any());
        verify(ibmPartnerQuoteModel).setCurrency(any());
        verify(ibmPartnerQuoteModel).setDate(any());
        verify(ibmPartnerQuoteModel).setSubmittedDate(any());
        verify(ibmPartnerQuoteModel).setQuoteExpirationDate(any());
        verify(ibmPartnerQuoteModel).setCpqQuoteNumber(cpqQuoteNumber);
        verify(ibmPartnerQuoteModel).setEccQuoteNumber(eccQuoteNumber);
        verify(ibmPartnerQuoteModel).setCpqExternalQuoteId(cpqQuoteExternalId);
        verify(ibmPartnerQuoteModel).setSalesApplication(SalesApplication.valueOf(salesApplication));
        verify(ibmPartnerQuoteModel).setCpqDistributionChannel(cpqDistributionChannel);
    }

    @Test
    public void testSetPartnerquoteDetails_EmptySubmitterEmail() {
        when(cpqIbmPartnerQuote.getQuoteSubmitter()).thenReturn(null);
        defaultQuoteCreationBasicDetailsMapperService.setPartnerquoteDetails(cpqIbmPartnerQuote, ibmPartnerQuoteModel);
        verify(ibmPartnerQuoteModel, never()).setSubmitter(any());
    }

    @Test
    public void testSetPartnerquoteDetails_NullCurrency() {
        when(cpqIbmPartnerQuote.getCurrency()).thenReturn(null);
        defaultQuoteCreationBasicDetailsMapperService.setPartnerquoteDetails(cpqIbmPartnerQuote, ibmPartnerQuoteModel);
        verify(ibmPartnerQuoteModel, never()).setCurrency(any());
    }

    @Test
    public void testSetPartnerquoteDetails_EmptyCpqDistributionChannel() {
        when(cpqIbmPartnerQuote.getCpqDistributionChannel()).thenReturn(null);
        defaultQuoteCreationBasicDetailsMapperService.setPartnerquoteDetails(cpqIbmPartnerQuote, ibmPartnerQuoteModel);
        verify(ibmPartnerQuoteModel).setCpqDistributionChannel(PartnerQuoteChannelEnum.J.getCode());
    }

    @Test
    public void testSetPartnerquoteDetails_EmptyEccQuoteNumber() {
        when(cpqIbmPartnerQuote.getEccQuoteNumber()).thenReturn("");
        defaultQuoteCreationBasicDetailsMapperService.setPartnerquoteDetails(cpqIbmPartnerQuote, ibmPartnerQuoteModel);
        verify(ibmPartnerQuoteModel, never()).setEccQuoteNumber(any());
    }

    @Test
    public void testSetPartnerquoteDetails_ValidCpqQuoteNumber() {
        String cpqQuoteNumber = "Q12345";
        when(cpqIbmPartnerQuote.getCpqQuoteNumber()).thenReturn(cpqQuoteNumber);
        defaultQuoteCreationBasicDetailsMapperService.setPartnerquoteDetails(cpqIbmPartnerQuote, ibmPartnerQuoteModel);
        verify(ibmPartnerQuoteModel).setCpqQuoteNumber(cpqQuoteNumber);
    }
    @Test
    public void testUpdatePartnerQuoteState_WhenStatusesPresent() {
        CpqIbmPartnerQuoteModel cpqQuote = mock(CpqIbmPartnerQuoteModel.class);
        IbmPartnerQuoteModel ibmQuote = mock(IbmPartnerQuoteModel.class);

        when(cpqQuote.getCpqQuoteStatus()).thenReturn("Submitted");
        when(cpqQuote.getEccQuoteStatus()).thenReturn("Approved");

        DefaultPartnerQuoteUpdateStateStrategy strategy = mock(DefaultPartnerQuoteUpdateStateStrategy.class);
        defaultQuoteCreationBasicDetailsMapperService = new DefaultQuoteCreationBasicDetailsMapperService(
            mock(PartnerUserService.class),
            mock(CommonI18NService.class),
            mock(BaseStoreService.class),
            strategy
        );
        defaultQuoteCreationBasicDetailsMapperService.setQuoteDetails(cpqQuote, ibmQuote);
        verify(strategy).updatePartnerQuoteState(ibmQuote, "Submitted", "Approved");
    }
    @Test
    public void testUpdatePartnerQuoteState_NotCalled_WhenStatusesMissing() {
        CpqIbmPartnerQuoteModel cpqQuote = mock(CpqIbmPartnerQuoteModel.class);
        IbmPartnerQuoteModel ibmQuote = mock(IbmPartnerQuoteModel.class);

        when(cpqQuote.getCpqQuoteStatus()).thenReturn("Submitted");
        when(cpqQuote.getEccQuoteStatus()).thenReturn(""); // Empty

        DefaultPartnerQuoteUpdateStateStrategy strategy = mock(DefaultPartnerQuoteUpdateStateStrategy.class);
        defaultQuoteCreationBasicDetailsMapperService = new DefaultQuoteCreationBasicDetailsMapperService(
            mock(PartnerUserService.class),
            mock(CommonI18NService.class),
            mock(BaseStoreService.class),
            strategy
        );
        defaultQuoteCreationBasicDetailsMapperService.setQuoteDetails(cpqQuote, ibmQuote);
        verify(strategy, never()).updatePartnerQuoteState(any(), any(), any());
    }

}
