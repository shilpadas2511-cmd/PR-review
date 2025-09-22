package com.ibm.commerce.partner.core.quote.services.impl;

import com.ibm.commerce.data.order.PartnerCpqQuoteCollaboratorsRequestData;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.IbmConsumedDestinationModel;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.IbmPartnerConsumedDestinationOAuthCredentialModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.order.approvalComments.data.response.PartnerApprovalsResponseData;
import com.ibm.commerce.partner.core.order.approvalComments.data.response.PartnerQuoteApprovalsInfoResponseData;
import com.ibm.commerce.partner.core.order.approvalComments.data.response.PartnerQuoteApprovalsResponseData;
import com.ibm.commerce.partner.core.outboundservices.quote.data.request.PartnerCPQCreateQuoteRequestData;
import com.ibm.commerce.partner.core.outboundservices.quote.data.response.PartnerCPQCreateQuoteResponseData;
import com.ibm.commerce.partner.core.outboundservices.quote.data.response.PartnerCPQSubmitQuoteResponseData;
import com.ibm.commerce.partner.core.outboundservices.quote.data.response.PartnerCPQValidateQuoteResponseData;
import com.ibm.commerce.partner.core.services.IbmConsumedDestinationService;
import com.ibm.commerce.partner.core.services.IbmOutboundIntegrationService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import java.util.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

@UnitTest
public class DefaultPartnerSapCpqQuoteServiceTest {

    @InjectMocks
    private DefaultPartnerSapCpqQuoteService service;

    @Mock
    private IbmConsumedDestinationService consumedDestinationService;

    @Mock
    private IbmOutboundIntegrationService outboundIntegrationService;

    @Mock
    private Converter converter;

    @Mock
    private PartnerApprovalsResponseData approvalsResponseData;

    @Mock
    private PartnerQuoteApprovalsResponseData approvalsInfoResponseData;

    private IbmConsumedDestinationModel destinationModel;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new DefaultPartnerSapCpqQuoteService(consumedDestinationService,
            outboundIntegrationService, converter);
        destinationModel = new IbmConsumedDestinationModel();
        destinationModel.setCustomUri("https://dummy.com/api/quote/v2");

        // IMPORTANT: Always return a non-null URL so sendRequest stubs match anyString()
        Mockito.when(outboundIntegrationService.buildUrlWithParams(
                Mockito.anyString(), Mockito.anyMap()))
            .thenReturn("https://dummy.com/api/quote/v2/resolved");
    }

    @Test
    public void testPopulateCommonQuoteHeaders_withCredential() {
        IbmPartnerConsumedDestinationOAuthCredentialModel credential = new IbmPartnerConsumedDestinationOAuthCredentialModel();
        credential.setClientId("id");
        credential.setClientSecret("secret");
        credential.setAcubicApi("api");
        destinationModel.setCredential(credential);

        HttpHeaders headers = new HttpHeaders();
        service.populateCommonQuoteHeaders(destinationModel, headers);

        Assert.assertTrue(headers.containsKey(PartnercoreConstants.CPQ_QUOTE_ID));
        Assert.assertTrue(headers.containsKey(PartnercoreConstants.CPQ_QUOTE_PASSWORD));
        Assert.assertTrue(headers.containsKey(PartnercoreConstants.CPQ_QUOTE_ACUBICAPI));
    }

    @Test
    public void testPopulateCommonQuoteHeaders_withoutCredential() {
        HttpHeaders headers = new HttpHeaders();
        service.populateCommonQuoteHeaders(destinationModel, headers);
        Assert.assertTrue(headers.isEmpty());
    }

    @Test
    public void testSortApprovalComments_sorted() {
        PartnerQuoteApprovalsInfoResponseData a1 = new PartnerQuoteApprovalsInfoResponseData();
        a1.setBpApprovalComment("c1");
        a1.setDateResolved(new Date(System.currentTimeMillis() - 10000));

        PartnerQuoteApprovalsInfoResponseData a2 = new PartnerQuoteApprovalsInfoResponseData();
        a2.setBpApprovalComment("c2");
        a2.setDateResolved(new Date());

        List<PartnerQuoteApprovalsInfoResponseData> infos = Arrays.asList(a1, a2);

        Mockito.when(approvalsResponseData.getApproval()).thenReturn(approvalsInfoResponseData);
        Mockito.when(approvalsInfoResponseData.getApprovalInfos()).thenReturn(infos);

        List<PartnerQuoteApprovalsInfoResponseData> result = service.sortApprovalComments(
            approvalsResponseData);

        Assert.assertEquals("c2", result.get(0).getBpApprovalComment());
    }

    @Test
    public void testSortApprovalComments_emptyInfos() {
        Mockito.when(approvalsResponseData.getApproval()).thenReturn(approvalsInfoResponseData);
        Mockito.when(approvalsInfoResponseData.getApprovalInfos())
            .thenReturn(Collections.emptyList());

        List<PartnerQuoteApprovalsInfoResponseData> result = service.sortApprovalComments(
            approvalsResponseData);
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void testFetchApprovalCommentsforQuote_nullResponse() {
        DefaultPartnerSapCpqQuoteService spy = Mockito.spy(service);
        Mockito.doReturn(null).when(spy).getApprovalDetail("id");

        List<PartnerQuoteApprovalsInfoResponseData> result = spy.fetchApprovalCommentsforQuote(
            "id");
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void testFetchApprovalCommentsforQuote_success() {
        PartnerQuoteApprovalsInfoResponseData info = new PartnerQuoteApprovalsInfoResponseData();
        info.setBpApprovalComment("ok");
        info.setDateResolved(new Date());

        Mockito.when(approvalsResponseData.getApproval()).thenReturn(approvalsInfoResponseData);
        Mockito.when(approvalsInfoResponseData.getApprovalInfos())
            .thenReturn(Collections.singletonList(info));

        DefaultPartnerSapCpqQuoteService spy = Mockito.spy(service);
        Mockito.doReturn(approvalsResponseData).when(spy).getApprovalDetail("id");

        List<PartnerQuoteApprovalsInfoResponseData> result = spy.fetchApprovalCommentsforQuote(
            "id");
        Assert.assertEquals(1, result.size());
    }

    @Test
    public void testPrepareCpqCollaboratorRequestData_withEmails() {
        IbmPartnerCartModel cart = new IbmPartnerCartModel();
        // FIX: use a real Set, not a List cast
        cart.setCollaboratorEmails(new HashSet<>(Arrays.asList("a@test.com", "b@test.com")));

        PartnerCpqQuoteCollaboratorsRequestData data = service.prepareCpqCollaboratorRequestData(
            cart);
        Assert.assertEquals(2, data.getCollaborators().size());
    }

    @Test
    public void testPrepareCpqCollaboratorRequestData_withQuote() {
        IbmPartnerQuoteModel quote = new IbmPartnerQuoteModel();
        // FIX: use Collections.singleton to get a Set
        quote.setCollaboratorEmails(Collections.singleton("x@test.com"));

        PartnerCpqQuoteCollaboratorsRequestData data = service.prepareCpqCollaboratorRequestData(
            quote);
        Assert.assertEquals(1, data.getCollaborators().size());
    }

    @Test
    public void testPrepareRemoveCpqCollaboratorRequestData() {
        PartnerCpqQuoteCollaboratorsRequestData data = service.prepareRemoveCpqCollaboratorRequestData(
            Collections.singletonList("remove@test.com"));
        Assert.assertEquals(1, data.getCollaborators().size());
    }

    @Test
    public void testCreateQuoteInCPQ() throws Exception {
        IbmPartnerCartModel cart = new IbmPartnerCartModel();
        cart.setPriceUid("p1");

        Mockito.when(consumedDestinationService.findActiveConsumedDestinationByIdAndTargetId(
                Mockito.anyString(), Mockito.anyString()))
            .thenReturn(destinationModel);

        Mockito.when(outboundIntegrationService.getHeaders(destinationModel))
            .thenReturn(new HttpHeaders());
        Mockito.when(converter.convert(cart)).thenReturn(new PartnerCPQCreateQuoteRequestData());
        Mockito.when(
                outboundIntegrationService.sendRequest(Mockito.eq(HttpMethod.POST), Mockito.anyString(),
                    Mockito.any(), Mockito.any(), Mockito.eq(PartnerCPQCreateQuoteResponseData.class),
                    Mockito.eq(HttpStatus.OK)))
            .thenReturn(new PartnerCPQCreateQuoteResponseData());

        PartnerCPQCreateQuoteResponseData response = service.createQuoteInCPQ(cart);
        Assert.assertNotNull(response);
    }

    @Test
    public void testCpqQuoteValidation() {
        IbmPartnerQuoteModel quote = new IbmPartnerQuoteModel();
        quote.setCpqExternalQuoteId("q1");

        Mockito.when(consumedDestinationService.findActiveConsumedDestinationByIdAndTargetId(
                Mockito.anyString(), Mockito.anyString()))
            .thenReturn(destinationModel);
        Mockito.when(outboundIntegrationService.getHeaders(destinationModel))
            .thenReturn(new HttpHeaders());
        Mockito.when(
                outboundIntegrationService.sendRequest(Mockito.eq(HttpMethod.PUT), Mockito.anyString(),
                    Mockito.any(), Mockito.isNull(),
                    Mockito.eq(PartnerCPQValidateQuoteResponseData.class), Mockito.anyList()))
            .thenReturn(new PartnerCPQValidateQuoteResponseData());

        PartnerCPQValidateQuoteResponseData result = service.cpqQuoteValidation(quote);
        Assert.assertNotNull(result);
    }

    @Test
    public void testCpqQuoteSubmit() {
        IbmPartnerQuoteModel quote = new IbmPartnerQuoteModel();
        quote.setCpqExternalQuoteId("q2");
        UserModel user = new UserModel();
        AddressModel addr = new AddressModel();
        addr.setEmail("test@test.com");
        user.setName("tester");
        user.setAddresses(Collections.singleton(addr));
        quote.setUser(user);

        Mockito.when(consumedDestinationService.findActiveConsumedDestinationByIdAndTargetId(
                Mockito.anyString(), Mockito.anyString()))
            .thenReturn(destinationModel);
        Mockito.when(outboundIntegrationService.getHeaders(destinationModel))
            .thenReturn(new HttpHeaders());
        Mockito.when(
                outboundIntegrationService.sendRequest(Mockito.eq(HttpMethod.PUT), Mockito.anyString(),
                    Mockito.any(), Mockito.isNull(),
                    Mockito.eq(PartnerCPQSubmitQuoteResponseData.class), Mockito.anyList()))
            .thenReturn(new PartnerCPQSubmitQuoteResponseData());

        PartnerCPQSubmitQuoteResponseData result = service.cpqQuoteSubmit(quote);
        Assert.assertNotNull(result);
    }

    @Test
    public void testAddCollaboratorsToCpq() throws Exception {
        IbmPartnerCartModel cart = new IbmPartnerCartModel();
        cart.setCpqExternalQuoteId("id");
        IbmPartnerQuoteModel quote = new IbmPartnerQuoteModel();
        quote.setCpqExternalQuoteId("qid");
        cart.setQuoteReference(quote);

        Mockito.when(consumedDestinationService.findActiveConsumedDestinationByIdAndTargetId(
                Mockito.anyString(), Mockito.anyString()))
            .thenReturn(destinationModel);
        Mockito.when(outboundIntegrationService.getHeaders(destinationModel))
            .thenReturn(new HttpHeaders());

        service.addCollaboratorsToCpq(cart);

        Mockito.verify(outboundIntegrationService)
            .sendRequest(Mockito.eq(HttpMethod.POST), Mockito.anyString(),
                Mockito.any(), Mockito.any(), Mockito.eq(Void.class), Mockito.anyList());
    }

    @Test
    public void testRemoveCollaboratorsToCpq() throws Exception {
        IbmPartnerCartModel cart = new IbmPartnerCartModel();
        cart.setCpqExternalQuoteId("cid");
        IbmPartnerQuoteModel ref = new IbmPartnerQuoteModel();
        ref.setCpqExternalQuoteId("qid");
        cart.setQuoteReference(ref);

        Mockito.when(consumedDestinationService.findActiveConsumedDestinationByIdAndTargetId(
                Mockito.anyString(), Mockito.anyString()))
            .thenReturn(destinationModel);
        Mockito.when(outboundIntegrationService.getHeaders(destinationModel))
            .thenReturn(new HttpHeaders());

        service.removeCollaboratorsToCpq(cart, Collections.singletonList("remove@test.com"));

        Mockito.verify(outboundIntegrationService)
            .sendRequest(Mockito.eq(HttpMethod.POST), Mockito.anyString(),
                Mockito.any(), Mockito.any(), Mockito.eq(Void.class), Mockito.anyList());
    }

    @Test
    public void testGetApprovalDetail_success() {
        String quoteId = "qid123";
        PartnerApprovalsResponseData mockResponse = new PartnerApprovalsResponseData();

        Mockito.when(consumedDestinationService.findActiveConsumedDestinationByIdAndTargetId(
                Mockito.anyString(), Mockito.anyString()))
            .thenReturn(destinationModel);
        Mockito.when(outboundIntegrationService.getHeaders(destinationModel))
            .thenReturn(new HttpHeaders());
        Mockito.when(
                outboundIntegrationService.buildUrlWithParams(Mockito.anyString(), Mockito.anyMap()))
            .thenReturn("https://dummy.com/api/quote/v2");
        Mockito.when(outboundIntegrationService.sendRequest(
                Mockito.eq(HttpMethod.GET), Mockito.anyString(), Mockito.any(),
                Mockito.isNull(), Mockito.eq(PartnerApprovalsResponseData.class),
                Mockito.eq(HttpStatus.OK)))
            .thenReturn(mockResponse);

        PartnerApprovalsResponseData result = service.getApprovalDetail(quoteId);

        Assert.assertNotNull(result);
        Mockito.verify(outboundIntegrationService).sendRequest(
            Mockito.eq(HttpMethod.GET), Mockito.anyString(), Mockito.any(),
            Mockito.isNull(), Mockito.eq(PartnerApprovalsResponseData.class),
            Mockito.eq(HttpStatus.OK));
    }

    @Test
    public void testPostCollaboratorInfo_success() throws Exception {
        IbmPartnerQuoteModel quote = new IbmPartnerQuoteModel();
        quote.setCpqExternalQuoteId("qid456");

        Mockito.when(consumedDestinationService.findActiveConsumedDestinationByIdAndTargetId(
                Mockito.anyString(), Mockito.anyString()))
            .thenReturn(destinationModel);
        Mockito.when(outboundIntegrationService.getHeaders(destinationModel))
            .thenReturn(new HttpHeaders());
        Mockito.when(
                outboundIntegrationService.buildUrlWithParams(Mockito.anyString(), Mockito.anyMap()))
            .thenReturn("https://dummy.com/api/quote/v2");

        service.postCollaboratorInfo(quote);

        Mockito.verify(outboundIntegrationService).sendRequest(
            Mockito.eq(HttpMethod.POST), Mockito.anyString(),
            Mockito.any(), Mockito.any(),
            Mockito.eq(Void.class), Mockito.anyList());
    }

    @Test
    public void testPopulateCommonQuoteHeaders_whenHeadersNull() {
        IbmPartnerConsumedDestinationOAuthCredentialModel credential = new IbmPartnerConsumedDestinationOAuthCredentialModel();
        credential.setClientId("id");
        credential.setClientSecret("secret");
        credential.setAcubicApi("api");
        destinationModel.setCredential(credential);

        // pass null headers
        service.populateCommonQuoteHeaders(destinationModel, null);
        // no exception means the branch headers == null executed
    }

    @Test
    public void testSortApprovalComments_whenApprovalInfosNull() {
        Mockito.when(approvalsResponseData.getApproval()).thenReturn(approvalsInfoResponseData);
        Mockito.when(approvalsInfoResponseData.getApprovalInfos()).thenReturn(null);

        List<PartnerQuoteApprovalsInfoResponseData> result =
            service.sortApprovalComments(approvalsResponseData);

        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void testSortApprovalComments_whenApprovalInfosEmptyAfterFilter() {
        Mockito.when(approvalsResponseData.getApproval()).thenReturn(approvalsInfoResponseData);
        // empty list triggers the "empty after null check" branch
        Mockito.when(approvalsInfoResponseData.getApprovalInfos())
            .thenReturn(Collections.emptyList());

        List<PartnerQuoteApprovalsInfoResponseData> result =
            service.sortApprovalComments(approvalsResponseData);

        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void testPrepareRemoveCpqCollaboratorRequestData_whenNullEmails() {
        PartnerCpqQuoteCollaboratorsRequestData data =
            service.prepareRemoveCpqCollaboratorRequestData(null);

        Assert.assertNotNull(data);
        Assert.assertTrue(data.getCollaborators().isEmpty());
    }

    @Test
    public void testSortApprovalComments_whenAllFilteredOut() {
        // mock approval info object
        PartnerQuoteApprovalsInfoResponseData approvalInfo =
            Mockito.mock(PartnerQuoteApprovalsInfoResponseData.class);

        // make fields null/invalid so filter removes it
        Mockito.when(approvalInfo.getBpApprovalComment()).thenReturn(null);

        List<PartnerQuoteApprovalsInfoResponseData> infos = new ArrayList<>();
        infos.add(approvalInfo);

        // wrap inside approval
        PartnerQuoteApprovalsResponseData approval = Mockito.mock(
            PartnerQuoteApprovalsResponseData.class);
        Mockito.when(approval.getApprovalInfos()).thenReturn(infos);
        Mockito.when(approvalsResponseData.getApproval()).thenReturn(approval);

        // call method
        List<PartnerQuoteApprovalsInfoResponseData> result =
            service.sortApprovalComments(approvalsResponseData);

        // verify branch hit
        Assert.assertTrue(result.isEmpty());
    }

}
