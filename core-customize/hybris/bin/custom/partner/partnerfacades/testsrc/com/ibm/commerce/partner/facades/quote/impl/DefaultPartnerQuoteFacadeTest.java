package com.ibm.commerce.partner.facades.quote.impl;


import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.order.approvalComments.data.response.PartnerQuoteApprovalsInfoResponseData;
import com.ibm.commerce.partner.core.order.strategies.impl.DefaultCloneIbmQuoteStrategy;
import com.ibm.commerce.partner.core.outbound.service.impl.DefaultPartnerScpiQuoteService;
import com.ibm.commerce.partner.core.quote.services.PartnerSapCpqQuoteService;
import com.ibm.commerce.partner.core.util.model.PartnerQuoteApprovalsInfoResponseDataTestGenerator;
import com.ibm.commerce.partnerwebservicescommons.dto.search.request.PartnerImportQuoteFileRequestData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2bacceleratorfacades.exception.EntityValidationException;
import de.hybris.platform.commercefacades.comment.data.CommentData;
import de.hybris.platform.commercefacades.quote.data.QuoteData;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceQuoteService;
import de.hybris.platform.commerceservices.order.strategies.impl.DefaultQuoteUserIdentificationStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.access.AccessDeniedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPartnerQuoteFacadeTest {

    private static final String QUOTE_CODE = "quote_code";

    DefaultPartnerQuoteFacade defaultPartnerQuoteFacade;
    @Mock
    UserService userService;
    @Mock
    BaseStoreService baseStoreService;
    @Mock
    DefaultQuoteUserIdentificationStrategy quoteUserIdentificationStrategy;
    @Mock
    DefaultCommerceQuoteService commerceQuoteService;
    @Mock
    DefaultPartnerScpiQuoteService partnerCommerceQuoteService;
    @Mock
    QuoteModel quoteModel;
    @Mock
    CustomerModel currentUser;
    @Mock
    BaseStoreModel currentBaseStore;
    @Mock
    UserModel userModel;
    @Mock
    QuoteService quoteService;
    @Mock
    PartnerSapCpqQuoteService sapCpqQuoteService;
    @Mock
    private Converter<PartnerQuoteApprovalsInfoResponseData, CommentData> partnerQuoteServiceConverter;

    @Mock
    ModelService modelService;
    @Mock
    CartModel cartModel;

    @Mock
    UserModel currentQuoteUser;

    @Mock
    CommerceCartService commerceCartService;
    @Mock
    CartService cartService;
    @Mock
    private DefaultCloneIbmQuoteStrategy cloneIbmQuoteStrategy;

    @Mock
    private IbmPartnerQuoteModel clonedQuote;
    @Mock
    Converter<QuoteModel, QuoteData> quoteConverter;

    @Mock
    private QuoteData quoteData;
    @Mock
    private ConfigurationService configurationService;
    @Mock
    private Configuration configuration;
    @Mock
    private Converter<PartnerImportQuoteFileRequestData, IbmPartnerQuoteModel> partnerImportQuoteReverseConverter;


    private String quoteCode = "testQuoteCode";
    private String cloneName = "clonedQuoteName";
    private QuoteState state = QuoteState.CLONE_BUYER_SUCCESS;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(configurationService.getConfiguration()).thenReturn(configuration);
        defaultPartnerQuoteFacade = new DefaultPartnerQuoteFacade(partnerCommerceQuoteService,
            partnerQuoteServiceConverter, configurationService, cloneIbmQuoteStrategy, null);
        defaultPartnerQuoteFacade.setCommerceQuoteService(partnerCommerceQuoteService);
        defaultPartnerQuoteFacade.setUserService(userService);
        defaultPartnerQuoteFacade.setBaseStoreService(baseStoreService);
        defaultPartnerQuoteFacade.setQuoteUserIdentificationStrategy(
            quoteUserIdentificationStrategy);
        defaultPartnerQuoteFacade.setCommerceQuoteService(commerceQuoteService);
        defaultPartnerQuoteFacade.setSapCpqQuoteService(sapCpqQuoteService);
        defaultPartnerQuoteFacade.setUserService(userService);
        defaultPartnerQuoteFacade.setBaseStoreService(baseStoreService);
        defaultPartnerQuoteFacade.setQuoteUserIdentificationStrategy(
            quoteUserIdentificationStrategy);
        defaultPartnerQuoteFacade.setSapCpqQuoteService(sapCpqQuoteService);
        defaultPartnerQuoteFacade.setQuoteService(quoteService);
        defaultPartnerQuoteFacade.setModelService(modelService);
        defaultPartnerQuoteFacade.setCommerceCartService(commerceCartService);
        defaultPartnerQuoteFacade.setCartService(cartService);
        userModel = new UserModel();
        quoteModel = new QuoteModel();
        currentUser = new CustomerModel();
        currentBaseStore = new BaseStoreModel();
        cartModel = new CartModel();
    }

    @Test
    public void testSubmitQuote() {
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(baseStoreService.getCurrentBaseStore()).thenReturn(currentBaseStore);
        when(quoteUserIdentificationStrategy.getCurrentQuoteUser()).thenReturn(userModel);
        defaultPartnerQuoteFacade.getPartnerCommerceQuoteService()
            .submitQuote(quoteModel, userModel);
        defaultPartnerQuoteFacade.submitQuote(QUOTE_CODE);
        verify(commerceQuoteService, times(0)).submitQuote(any(), any());

    }

    @Test
    public void testFetchApprovalComments() {
        when(quoteService.getCurrentQuoteForCode(QUOTE_CODE)).thenReturn(quoteModel);
        quoteModel.setState(QuoteState.BUYER_APPROVED);
        quoteModel.setCpqExternalQuoteId(QUOTE_CODE);
        defaultPartnerQuoteFacade.fetchApprovalComments(QUOTE_CODE);
    }

    @Test
    public void testFetchApprovalComments_DraftState() {
        when(quoteService.getCurrentQuoteForCode(QUOTE_CODE)).thenReturn(quoteModel);
        quoteModel.setState(QuoteState.BUYER_DRAFT);
        quoteModel.setCpqExternalQuoteId(QUOTE_CODE);
        defaultPartnerQuoteFacade.fetchApprovalComments(QUOTE_CODE);
    }

    @Test
    public void testGetApprovalComments() {
        PartnerQuoteApprovalsInfoResponseData partnerQuoteApprovalsInfoResponseData = PartnerQuoteApprovalsInfoResponseDataTestGenerator.create();
        List<PartnerQuoteApprovalsInfoResponseData> partnerQuoteApprovalsInfoResponseDataList = new ArrayList<>();
        partnerQuoteApprovalsInfoResponseDataList.add(partnerQuoteApprovalsInfoResponseData);
        when(sapCpqQuoteService.fetchApprovalCommentsforQuote(QUOTE_CODE)).thenReturn(
            partnerQuoteApprovalsInfoResponseDataList);
        defaultPartnerQuoteFacade.getApprovalComments(QUOTE_CODE);
    }

    @Test
    public void testFetchApprovalComments_Null() {
        when(quoteService.getCurrentQuoteForCode(QUOTE_CODE)).thenReturn(null);
        defaultPartnerQuoteFacade.fetchApprovalComments(QUOTE_CODE);
    }

    @Test
    public void testFetchApprovalComments_Null_CPQ_Quote_Id() {
        when(quoteService.getCurrentQuoteForCode(QUOTE_CODE)).thenReturn(quoteModel);
        quoteModel.setState(QuoteState.BUYER_APPROVED);
        quoteModel.setCpqExternalQuoteId(null);
        defaultPartnerQuoteFacade.fetchApprovalComments(QUOTE_CODE);
    }

    @Test
    public void testFetchApprovalComments_Draft_Status() {
        defaultPartnerQuoteFacade.fetchApprovalComments(null);
    }


    @Test
    public void testEnableQuoteEdit_QuoteUpdateBySiteIdsEnabled() {
        String quoteCode = "testQuoteCode";
        currentUser = mock(CustomerModel.class);
        currentBaseStore = mock(BaseStoreModel.class);
        currentQuoteUser = mock(UserModel.class);
        quoteModel = mock(QuoteModel.class);
        cartModel = mock(CartModel.class);

        when(quoteUserIdentificationStrategy.getCurrentQuoteUser()).thenReturn(currentQuoteUser);
        when(baseStoreService.getCurrentBaseStore()).thenReturn(currentBaseStore);
        when(partnerCommerceQuoteService.getQuoteByCodeAndSiteIdsAndStore(any(), any(), any(),
            eq(quoteCode))).thenReturn(quoteModel);
        when(partnerCommerceQuoteService.loadQuoteAsSessionCart(any(), any())).thenReturn(
            cartModel);
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(quoteModel.getCartReference()).thenReturn(cartModel);
        defaultPartnerQuoteFacade.enableQuoteEdit(quoteCode);

        verify(quoteModel).setAssignee(currentQuoteUser);
        verify(modelService).save(quoteModel);
        verify(partnerCommerceQuoteService).assignQuoteToUser(quoteModel, currentQuoteUser,
            currentQuoteUser);
        verify(cartModel).setUser(currentQuoteUser);
        verify(modelService).saveAll(cartModel, quoteModel);
        verify(commerceCartService).calculateCart(any(CommerceCartParameter.class));
        verify(modelService).refresh(cartModel);
        verify(cartService).setSessionCart(cartModel);
    }

    @Test
    public void testGetCloneQuote_Success() {
        currentUser = mock(CustomerModel.class);
        currentBaseStore = mock(BaseStoreModel.class);
        currentQuoteUser = mock(UserModel.class);
        CartModel cartModel = mock(CartModel.class);
        quoteModel.setCode("123");

        when(quoteUserIdentificationStrategy.getCurrentQuoteUser()).thenReturn(currentQuoteUser);
        when(baseStoreService.getCurrentBaseStore()).thenReturn(currentBaseStore);
        when(partnerCommerceQuoteService.getQuoteByCodeAndSiteIdsAndStore(any(), any(), any(),
            eq(quoteCode))).thenReturn(quoteModel);
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(quoteConverter.convert(clonedQuote)).thenReturn(quoteData);

        QuoteData result = defaultPartnerQuoteFacade.getCloneQuote(quoteModel, cloneName);

        assertNotNull(result);
        assertEquals(quoteData, result);
        verify(modelService).save(clonedQuote);
    }

    @Test
    public void testGetCloneQuote_AccessDenied() {
        quoteModel.setCode(null);
        defaultPartnerQuoteFacade.getCloneQuote(quoteModel, cloneName);
    }

    @Test
    public void testUpdateQuoteStatus_Success() {
        quoteModel.setCode("123");

        defaultPartnerQuoteFacade.updateQuotestatus(quoteModel, state);

        verify(partnerCommerceQuoteService).updateQuoteStatus(quoteModel, state);
    }
    @Test
    public void testgetCloneIbmQuoteStrategy(){
        defaultPartnerQuoteFacade.getCloneIbmQuoteStrategy();
    }
    @Test
    public void testgetPartnerImportQuoteReverseConverter(){
        defaultPartnerQuoteFacade.getPartnerImportQuoteReverseConverter();
    }
    @Test
    public void testCreateImportedQuote() {
        // Arrange
        PartnerImportQuoteFileRequestData fileRequestData = new PartnerImportQuoteFileRequestData();
        IbmPartnerQuoteModel mockQuoteModel = mock(IbmPartnerQuoteModel.class);
        QuoteData mockQuoteData = new QuoteData();
        when(modelService.create(IbmPartnerQuoteModel.class))
            .thenReturn(mockQuoteModel);
        when(partnerImportQuoteReverseConverter.convert(fileRequestData, mockQuoteModel))
            .thenReturn(mockQuoteModel);
        when(defaultPartnerQuoteFacade.getPartnerImportQuoteReverseConverter()).thenReturn(partnerImportQuoteReverseConverter);

        // Act
        QuoteData result = defaultPartnerQuoteFacade.createImportedQuote(fileRequestData);

    }
}
