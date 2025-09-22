package com.ibm.commerce.partner.occ.v2.helper;

import static de.hybris.platform.core.enums.QuoteState.SELLERAPPROVER_APPROVED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.anyString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.event.CartPriceLookUpEvent;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.PartnerSpecialBidReasonModel;
import com.ibm.commerce.partner.core.order.services.PartnerProcessService;
import com.ibm.commerce.partner.core.outbound.service.impl.DefaultPartnerScpiQuoteService;
import com.ibm.commerce.partner.core.specialbidreason.service.PartnerSpecialBidReasonService;
import com.ibm.commerce.partner.facades.partnerquestions.PartnerQuestionsFacade;
import com.ibm.commerce.partner.facades.quote.impl.DefaultPartnerQuoteFacade;
import com.ibm.commerce.partner.facades.specialbidreason.PartnerSpecialBidReasonFacade;
import com.ibm.commerce.partner.occ.v2.validator.PartnerQuoteValidator;
import com.ibm.commerce.partner.specialbidreason.data.PartnerSpecialBidReasonData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2bocc.exceptions.QuoteAssemblingException;
import de.hybris.platform.b2bocc.exceptions.QuoteException;
import de.hybris.platform.b2bocc.v2.helper.QuoteHelper;
import de.hybris.platform.commercefacades.quote.data.QuoteData;
import de.hybris.platform.commercefacades.voucher.exceptions.VoucherOperationException;
import de.hybris.platform.commerceservices.enums.QuoteAction;
import de.hybris.platform.commerceservices.enums.QuoteUserType;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.strategies.QuoteUserIdentificationStrategy;
import de.hybris.platform.commerceservices.order.strategies.QuoteUserTypeIdentificationStrategy;
import de.hybris.platform.commercewebservicescommons.dto.quote.QuoteWsDTO;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.processengine.enums.ProcessState;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PartnerQuoteHelperTest {

    private static final String QUOTE_CODE = "Quote";
    private static final String FIELDS = "FULL";

    @InjectMocks
    PartnerQuoteHelper partnerQuoteHelper;
    @Mock
    PartnerQuoteValidator partnerQuoteValidator;
    @Mock
    private ModelService modelService;
    @Mock
    private QuoteService quoteService;
    @Mock
    private IbmPartnerQuoteModel quoteModel;
    @Mock
    private DefaultPartnerQuoteFacade quoteFacade;
    @Mock
    private QuoteUserIdentificationStrategy quoteUserIdentificationStrategy;
    @Mock
    private QuoteUserTypeIdentificationStrategy quoteUserTypeIdentificationStrategy;
    @Mock
    IbmPartnerCartModel cartModel;

    @Mock
    DataMapper dataMapper;
    @Mock
    QuoteHelper quoteHelper;

    @Mock
    PartnerProcessService partnerProcessService;

    @Mock
    EventService eventService;
    @Mock
    private CartService cartService;
    @Mock
    private QuoteData quoteData;
    @Mock
    private QuoteWsDTO quoteWsDTO;
    @Mock
    Populator<CartModel, QuoteWsDTO> cartModelToQuoteWsDTOPopulator;
    @Mock
    private PartnerSpecialBidReasonService partnerSpecialBidReasonService;
    @Mock
    DefaultPartnerScpiQuoteService commerceQuoteService;

    private final UserModel userModel = new UserModel();
    private static final String EXCEPTION_MSG = "Special Reason code not exist in db";
    private static final String VALIDATION_ERROR = "Special Reason Validation Error";
    private static final String SPECIAL_BID_REASON_CODE = "reasonCode";
    private static final String BUSINESS_JUSTIFICATION = "Test justification";
    private static final String CLONED_NAME = "Cloned Quote";
    @Mock
     PartnerSpecialBidReasonFacade specialBidReasonFacade;
    @Mock
 PartnerQuestionsFacade questionsFacade;

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);
        partnerQuoteHelper = new PartnerQuoteHelper(modelService, partnerQuoteValidator,
            partnerProcessService, eventService, cartService, partnerSpecialBidReasonService,
            quoteFacade,
            commerceQuoteService,specialBidReasonFacade, questionsFacade){
            @Override
            public DataMapper getDataMapper() {
                return dataMapper;
            }

            @Override
            public Populator<CartModel, QuoteWsDTO> getCartModelToQuoteWsDTOPopulator() {
                return cartModelToQuoteWsDTOPopulator;
            }
        };
        partnerQuoteHelper.setQuoteFacade(quoteFacade);
        partnerQuoteHelper.setQuoteService(quoteService);
        partnerQuoteHelper
            .setQuoteUserTypeIdentificationStrategy(quoteUserTypeIdentificationStrategy);
        partnerQuoteHelper.setCartService(cartService);
        partnerQuoteHelper.setQuoteUserIdentificationStrategy(quoteUserIdentificationStrategy);
        when(quoteService.getCurrentQuoteForCode(QUOTE_CODE)).thenReturn(quoteModel);
        when(quoteModel.getState()).thenReturn(QuoteState.BUYER_DRAFT);
        when(quoteUserIdentificationStrategy.getCurrentQuoteUser()).thenReturn(userModel);
        when(quoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(userModel))
            .thenReturn(Optional.ofNullable(QuoteUserType.BUYER));


    }

    @Test
    public void testSubmitQuote()
        throws VoucherOperationException, CommerceCartModificationException {
        doNothing().when(quoteFacade).submitQuote(any());
        when(quoteModel.getState()).thenReturn(QuoteState.BUYER_DRAFT);
        partnerQuoteHelper.submitQuote(QUOTE_CODE);
    }

    @Test
    public void testSubmitQuoteSave()
        throws VoucherOperationException, CommerceCartModificationException {
        doNothing().when(quoteFacade).submitQuote(any());
        partnerQuoteHelper.setQuoteFacade(quoteFacade);
        partnerQuoteHelper.submitQuote(QUOTE_CODE);
    }

    @Test(expected = CommerceCartModificationException.class)
    public void testSubmitQuoteException()
        throws VoucherOperationException, CommerceCartModificationException {
        when(quoteModel.getState()).thenReturn(QuoteState.SELLER_REQUEST);
        partnerQuoteHelper.submitQuote(QUOTE_CODE);
    }

    @Test(expected = QuoteException.class)
    public void testSubmitQuoteModelException()
        throws VoucherOperationException, CommerceCartModificationException {
        given(quoteService.getCurrentQuoteForCode(Mockito.anyString()))
            .willThrow(ModelNotFoundException.class);
        partnerQuoteHelper.submitQuote(QUOTE_CODE);
    }

    @Test
    public void testUpdateCartWithSpecialBidReasonInformationSuccess() throws Exception {
        final PartnerSpecialBidReasonModel partnerSpecialBidReasonModel =
            mock(PartnerSpecialBidReasonModel.class);
        when(partnerSpecialBidReasonModel.getCode()).thenReturn(SPECIAL_BID_REASON_CODE);
        when(partnerSpecialBidReasonService.getSpecialBidReasonById(SPECIAL_BID_REASON_CODE))
            .thenReturn(partnerSpecialBidReasonModel);
        final IbmPartnerCartModel ibmPartnerCartModel = mock(IbmPartnerCartModel.class);
        when(cartService.getSessionCart()).thenReturn(ibmPartnerCartModel);
        partnerQuoteHelper.updateCartWithSpecialBidReasonInformation(SPECIAL_BID_REASON_CODE,
            BUSINESS_JUSTIFICATION);
        verify(partnerQuoteValidator).validateSpecialBidReasonDetails(ibmPartnerCartModel,
            BUSINESS_JUSTIFICATION);
        verify(ibmPartnerCartModel).setSpecialBidReason(partnerSpecialBidReasonModel);
        verify(ibmPartnerCartModel).setSpecialBidBusinessJustification(BUSINESS_JUSTIFICATION);
        verify(modelService).save(ibmPartnerCartModel);
    }

    @Test(expected = CommerceCartModificationException.class)
    public void testUpdateCartWithSpecialBidReasonInformationNotExist() throws Exception {

        when(partnerSpecialBidReasonService.getSpecialBidReasonById(SPECIAL_BID_REASON_CODE))
            .thenReturn(null);
        partnerQuoteHelper.updateCartWithSpecialBidReasonInformation(SPECIAL_BID_REASON_CODE,
            BUSINESS_JUSTIFICATION);
    }

    @Test
    public void testUpdateCartWithSpecialBidReasonInformation_InvalidReasonCode()
        throws CommerceCartModificationException, ModelNotFoundException {

        when(partnerQuoteHelper.getPartnerSpecialBidReasonService()
            .getSpecialBidReasonById(SPECIAL_BID_REASON_CODE)).thenReturn(null);
        final CommerceCartModificationException exception =
            assertThrows(CommerceCartModificationException.class,
                () -> partnerQuoteHelper.updateCartWithSpecialBidReasonInformation(
                    SPECIAL_BID_REASON_CODE, BUSINESS_JUSTIFICATION));
        assertEquals(EXCEPTION_MSG, exception.getMessage());
        verify(partnerQuoteValidator, never()).validateSpecialBidReasonDetails(any(), any());
        verify(modelService, never()).save(any());
    }

    @Test
    public void testUpdateCartWithSpecialBidReasonInformation_ModelNotFoundException()
        throws ModelNotFoundException, CommerceCartModificationException {

        when(partnerQuoteHelper.getPartnerSpecialBidReasonService()
            .getSpecialBidReasonById(SPECIAL_BID_REASON_CODE))
            .thenThrow(new ModelNotFoundException("Model not found"));

        final QuoteException exception = assertThrows(QuoteException.class,
            () -> partnerQuoteHelper.updateCartWithSpecialBidReasonInformation(
                SPECIAL_BID_REASON_CODE, BUSINESS_JUSTIFICATION));
        assertEquals(VALIDATION_ERROR, exception.getMessage());

        verify(partnerQuoteValidator, never()).validateSpecialBidReasonDetails(any(), any());
        verify(modelService, never()).save(any());
    }

    @Test(expected = QuoteAssemblingException.class)
    public void testGetQuoteWithException() {
        when(quoteFacade.getAllowedActions(QUOTE_CODE))
            .thenReturn(Set.of(QuoteAction.CREATE, QuoteAction.VIEW, QuoteAction.SAVE,
                QuoteAction.ORDER, QuoteAction.DISCOUNT, QuoteAction.EXPIRED));
        when(quoteData.getCode()).thenReturn(QUOTE_CODE);
        when(quoteFacade.getQuoteForCode(QUOTE_CODE)).thenReturn(quoteData);

        final QuoteWsDTO quoteWsDTOObj = partnerQuoteHelper.getQuote(QUOTE_CODE, FIELDS);
    }

    @Test
    public void testGetQuoteWithCartReferenceNull() {
        when(quoteFacade.getAllowedActions(QUOTE_CODE))
            .thenReturn(Set.of(QuoteAction.CREATE, QuoteAction.VIEW, QuoteAction.SAVE,
                QuoteAction.ORDER, QuoteAction.DISCOUNT, QuoteAction.EXPIRED));
        when(quoteData.getCode()).thenReturn(QUOTE_CODE);
        when(quoteFacade.getQuoteForCode(QUOTE_CODE)).thenReturn(quoteData);
        when(quoteModel.getCartReference()).thenReturn(null);
        when(quoteData.getState()).thenReturn(SELLERAPPROVER_APPROVED);

        when(quoteService.getCurrentQuoteForCode(QUOTE_CODE)).thenReturn(quoteModel);
        when(dataMapper.map(any(), eq(QuoteWsDTO.class), eq(FIELDS))).thenReturn(quoteWsDTO);

        final QuoteWsDTO quoteWsDTOObj = partnerQuoteHelper.getQuote(QUOTE_CODE, FIELDS);
        assertThat(quoteWsDTOObj).isSameAs(quoteWsDTO);
    }

    @Test
    public void testGetQuoteWithCartReferenceNullUserTypeNonBuyer() {
        when(quoteFacade.getAllowedActions(QUOTE_CODE))
            .thenReturn(Set.of(QuoteAction.CREATE, QuoteAction.VIEW, QuoteAction.SAVE,
                QuoteAction.ORDER, QuoteAction.DISCOUNT, QuoteAction.EXPIRED));
        when(quoteData.getCode()).thenReturn(QUOTE_CODE);
        when(quoteFacade.getQuoteForCode(QUOTE_CODE)).thenReturn(quoteData);
        when(quoteModel.getCartReference()).thenReturn(null);
        when(quoteData.getState()).thenReturn(SELLERAPPROVER_APPROVED);
        when(quoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(userModel))
            .thenReturn(Optional.ofNullable(QuoteUserType.SELLER));

        when(quoteService.getCurrentQuoteForCode(QUOTE_CODE)).thenReturn(quoteModel);
        when(dataMapper.map(any(), eq(QuoteWsDTO.class), eq(FIELDS))).thenReturn(quoteWsDTO);

        final QuoteWsDTO quoteWsDTOObj = partnerQuoteHelper.getQuote(QUOTE_CODE, FIELDS);
        assertThat(quoteWsDTOObj).isSameAs(quoteWsDTO);
    }

    @Test
    public void testGetQuoteWithCart() {
        when(quoteFacade.getAllowedActions(QUOTE_CODE))
            .thenReturn(Set.of(QuoteAction.CREATE, QuoteAction.VIEW, QuoteAction.SAVE,
                QuoteAction.ORDER, QuoteAction.DISCOUNT, QuoteAction.EXPIRED));
        when(quoteData.getCode()).thenReturn(QUOTE_CODE);
        when(quoteFacade.getQuoteForCode(QUOTE_CODE)).thenReturn(quoteData);
        when(quoteModel.getCartReference()).thenReturn(cartModel);
        when(quoteData.getState()).thenReturn(SELLERAPPROVER_APPROVED);

        when(quoteService.getCurrentQuoteForCode(QUOTE_CODE)).thenReturn(quoteModel);
        when(dataMapper.map(any(), eq(QuoteWsDTO.class), eq(FIELDS))).thenReturn(quoteWsDTO);
        doNothing().when(cartModelToQuoteWsDTOPopulator).populate(cartModel, quoteWsDTO);

        final QuoteWsDTO quoteWsDTOObj = partnerQuoteHelper.getQuote(QUOTE_CODE, FIELDS);
        assertThat(quoteWsDTOObj).isSameAs(quoteWsDTO);
    }

    @Test
    public void testGetQuoteWithStateBuyerDraft() {
        when(quoteFacade.getAllowedActions(QUOTE_CODE))
            .thenReturn(Set.of(QuoteAction.CREATE, QuoteAction.VIEW, QuoteAction.SAVE,
                QuoteAction.ORDER, QuoteAction.DISCOUNT, QuoteAction.EXPIRED));
        when(quoteData.getCode()).thenReturn(QUOTE_CODE);
        when(quoteFacade.getQuoteForCode(QUOTE_CODE)).thenReturn(quoteData);
        when(quoteModel.getCartReference()).thenReturn(cartModel);
        when(quoteData.getState()).thenReturn(QuoteState.BUYER_DRAFT);

        // FullPriceReceived True.
        when(cartModel.getFullPriceReceived()).thenReturn(true);
        when(cartModel.getCode()).thenReturn(QUOTE_CODE);

        when(quoteService.getCurrentQuoteForCode(QUOTE_CODE)).thenReturn(quoteModel);
        when(dataMapper.map(any(), eq(QuoteWsDTO.class), eq(FIELDS))).thenReturn(quoteWsDTO);

        QuoteWsDTO quoteWsDTOObj = partnerQuoteHelper.getQuote(QUOTE_CODE, FIELDS);
        assertThat(quoteWsDTOObj).isSameAs(quoteWsDTO);

        // FullPriceReceived false.
        when(cartModel.getFullPriceReceived()).thenReturn(false);
        when(cartModel.getErrorMesaage()).thenReturn("");

        quoteWsDTOObj = partnerQuoteHelper.getQuote(QUOTE_CODE, FIELDS);
        assertThat(quoteWsDTOObj).isSameAs(quoteWsDTO);
    }

    @Test
    public void testGetQuoteWithPriceLookUpNotRunning() {
        final String processCode =
            PartnercoreConstants.PRICING_SERVICE_PROCESS_CODE + PartnercoreConstants.HYPHEN
                + cartModel.getCode() + PartnercoreConstants.PERCENTAGE;
        setUpDataforGetQuoteWithPriceProcess(processCode, ProcessState.CREATED);
        final QuoteWsDTO quoteWsDTOObj = partnerQuoteHelper.getQuote(QUOTE_CODE, FIELDS);
        assertThat(quoteWsDTOObj).isSameAs(quoteWsDTO);
        verify(eventService).publishEvent(any());
    }

    @Test
    public void testGetQuoteWithPriceLookUpRunning() {
        final String processCode =
            PartnercoreConstants.PRICING_SERVICE_PROCESS_CODE + PartnercoreConstants.HYPHEN
                + cartModel.getCode() + PartnercoreConstants.PERCENTAGE;
        setUpDataforGetQuoteWithPriceProcess(processCode, ProcessState.RUNNING);
        final QuoteWsDTO quoteWsDTOObj = partnerQuoteHelper.getQuote(QUOTE_CODE, FIELDS);
        assertThat(quoteWsDTOObj).isSameAs(quoteWsDTO);
        verify(eventService, never()).publishEvent(any(CartPriceLookUpEvent.class));
    }

    @Test
    public void testGetQuoteWithQuotePriceLookUpNotRunning() {
        final String quoteProcessCode = PartnercoreConstants.QUOTE_PRICING_SERVICE_PROCESS_CODE
            + PartnercoreConstants.HYPHEN + cartModel.getCode()
            + PartnercoreConstants.PERCENTAGE;
        setUpDataforGetQuoteWithPriceProcess(quoteProcessCode, ProcessState.CREATED);
        final QuoteWsDTO quoteWsDTOObj = partnerQuoteHelper.getQuote(QUOTE_CODE, FIELDS);
        assertThat(quoteWsDTOObj).isSameAs(quoteWsDTO);
        verify(eventService).publishEvent(any(CartPriceLookUpEvent.class));
    }

    @Test
    public void testGetQuoteWithQuotePriceLookUpRunning() {
        final String quoteProcessCode = PartnercoreConstants.QUOTE_PRICING_SERVICE_PROCESS_CODE
            + PartnercoreConstants.HYPHEN + cartModel.getCode()
            + PartnercoreConstants.PERCENTAGE;
        setUpDataforGetQuoteWithPriceProcess(quoteProcessCode, ProcessState.RUNNING);
        final QuoteWsDTO quoteWsDTOObj = partnerQuoteHelper.getQuote(QUOTE_CODE, FIELDS);
        assertThat(quoteWsDTOObj).isSameAs(quoteWsDTO);
        verify(eventService, never()).publishEvent(any(CartPriceLookUpEvent.class));
    }

    public void setUpDataforGetQuoteWithPriceProcess(final String processCode,
        final ProcessState processState) {
        when(quoteFacade.getAllowedActions(QUOTE_CODE))
            .thenReturn(Set.of(QuoteAction.CREATE, QuoteAction.VIEW, QuoteAction.SAVE,
                QuoteAction.ORDER, QuoteAction.DISCOUNT, QuoteAction.EXPIRED));
        when(quoteData.getCode()).thenReturn(QUOTE_CODE);
        when(quoteFacade.getQuoteForCode(QUOTE_CODE)).thenReturn(quoteData);
        when(quoteModel.getCartReference()).thenReturn(cartModel);
        when(quoteData.getState()).thenReturn(QuoteState.BUYER_DRAFT);
        when(dataMapper.map(any(), eq(QuoteWsDTO.class), eq(FIELDS))).thenReturn(quoteWsDTO);
        when(cartModel.getFullPriceReceived()).thenReturn(false);
        when(cartModel.getErrorMesaage()).thenReturn("");
        final BusinessProcessModel quotePriceLookUpProcess = mock(BusinessProcessModel.class);
        when(quotePriceLookUpProcess.getState()).thenReturn(processState);
        when(partnerProcessService.getBusinessProcessList(processCode))
            .thenReturn(List.of(quotePriceLookUpProcess));
    }


    @Test
    public void testUpdateQuoteStatus_Success() {
        //when(quoteService.getCurrentQuoteForCode("123")).thenReturn(quoteModel);
        QuoteState state = QuoteState.BUYER_DRAFT;
        quoteFacade.updateQuotestatus(quoteModel, state);
        verify(quoteFacade).updateQuotestatus(quoteModel, state);
    }

    @Test
    public void testSetSessionCartFromQuote_Success() {
        String quoteCode = "Q-789";
        UserModel userModel1 = mock(UserModel.class);
        QuoteModel quoteModel = mock(QuoteModel.class);
        CartModel cartModel = mock(CartModel.class);
        //when(quoteModel.getCode()).thenReturn(quoteCode);
        when(quoteService.getCurrentQuoteForCode(quoteCode)).thenReturn(quoteModel);
        when(quoteModel.getCartReference()).thenReturn(cartModel);
        when(quoteUserIdentificationStrategy.getCurrentQuoteUser()).thenReturn(userModel1);
        partnerQuoteHelper.setSessionCartFromQuote(quoteCode);
        verify(commerceQuoteService).validateQuoteEditBySiteIds(quoteModel, userModel1, cartModel);
        verify(cartService).setSessionCart(cartModel);
    }

    @Test
    public void testUpdateQuoteStatus_SuccessTest() {
        String quoteCode = "quote123";
        QuoteState newState = QuoteState.BUYER_APPROVED;
        when(quoteService.getCurrentQuoteForCode(quoteCode)).thenReturn(quoteModel);
        partnerQuoteHelper.updateQuoteStatus(quoteCode, newState);
        verify(quoteFacade, times(1)).updateQuotestatus(quoteModel, newState);
    }

    @Test
    public void testUpdateQuoteStatus_QuoteNotFound() {
        String quoteCode = "quote123";
        QuoteState newState = QuoteState.BUYER_APPROVED;
        when(quoteService.getCurrentQuoteForCode(quoteCode)).thenReturn(null);
        assertDoesNotThrow(() -> partnerQuoteHelper.updateQuoteStatus(quoteCode, newState));
        verify(quoteFacade, never()).updateQuotestatus(any(), any());
    }

    @Test
    public void testUpdateQuoteStatus_ExceptionHandling() {
        String quoteCode = "quote123";
        QuoteState newState = QuoteState.BUYER_APPROVED;
        when(quoteService.getCurrentQuoteForCode(quoteCode)).thenThrow(
            ModelNotFoundException.class);
        assertThrows(ModelNotFoundException.class,
            () -> partnerQuoteHelper.updateQuoteStatus(quoteCode, newState));
        verify(quoteFacade, never()).updateQuotestatus(any(), any());
    }

    @Test
    public void testGetCartModelToQuoteWsDTOPopulator() {
        partnerQuoteHelper.getCartModelToQuoteWsDTOPopulator();
        assertNotNull(cartModelToQuoteWsDTOPopulator, "The Populator should not be null.");
    }

    @Test
    public void cloneQuoteModel_shouldReturnClonedQuoteWsDTO() {
        // Given
        final String quoteCode = "QUOTE123";
        final String name = "Cloned Quote";
        final String fields = "DEFAULT";

        QuoteModel quoteModel = mock(QuoteModel.class);
        QuoteData clonedQuoteData = mock(QuoteData.class);
        QuoteWsDTO quoteWsDTO = mock(QuoteWsDTO.class);

        // Mocks for clone process
        when(quoteService.getCurrentQuoteForCode(quoteCode)).thenReturn(quoteModel);
        when(quoteFacade.getCloneQuote(quoteModel, name)).thenReturn(clonedQuoteData);
        when(clonedQuoteData.getCode()).thenReturn("CLONE123");

        // Mocks for getQuoteWsDTO
        //when(quoteFacade.getAllowedActions(anyString())).thenReturn(Collections.emptyList());
        //when(quoteFacade.getQuoteRequestThreshold(anyString())).thenReturn(BigDecimal.ZERO);
        when(clonedQuoteData.getState()).thenReturn(QuoteState.BUYER_DRAFT);

        QuoteModel clonedQuoteModel = mock(QuoteModel.class);
        IbmPartnerCartModel cartModel = mock(IbmPartnerCartModel.class);

        when(clonedQuoteModel.getCartReference()).thenReturn(cartModel);
        when(quoteService.getCurrentQuoteForCode("CLONE123")).thenReturn(clonedQuoteModel);
        when(cartModel.getFullPriceReceived()).thenReturn(Boolean.TRUE);
        //when(cartModel.getErrorMesaage()).thenReturn(null);
        when(cartModel.getCode()).thenReturn("CART123");

        when(dataMapper.map(any(QuoteData.class), eq(QuoteWsDTO.class), eq(fields))).thenReturn(quoteWsDTO);
        when(dataMapper.map(any(QuoteWsDTO.class), eq(QuoteWsDTO.class), eq(fields))).thenReturn(quoteWsDTO);

        // When
        QuoteWsDTO result = partnerQuoteHelper.cloneQuoteModel(quoteCode, name, fields);

        // Then
        assertNotNull(result);
        assertEquals(quoteWsDTO, result);
        verify(quoteFacade).enableQuoteEdit("CLONE123");
        verify(quoteFacade).getCloneQuote(quoteModel, name);
        verify(dataMapper, atLeastOnce()).map(any(), eq(QuoteWsDTO.class), eq(fields));
    }

    @Test
    public void updateCartWithSpecialBidReasonInformation_shouldUpdateSuccessfully()
        throws Exception {
        // Given
        String justification = "Justified";
        String code = "REASON1";

        PartnerSpecialBidReasonData reasonData = new PartnerSpecialBidReasonData();
        reasonData.setCode(code);
        List<PartnerSpecialBidReasonData> reasonList = Collections.singletonList(reasonData);

        IbmPartnerCartModel cartModel = mock(IbmPartnerCartModel.class);
        PartnerSpecialBidReasonModel bidReasonModel = mock(PartnerSpecialBidReasonModel.class);

        // ✅ Correct mock setup
        when(cartService.getSessionCart()).thenReturn(cartModel);
        when(partnerSpecialBidReasonService.getSpecialBidReasonById(code)).thenReturn(
            bidReasonModel);
        when(bidReasonModel.getCode()).thenReturn(code); // This line must match the code!

        // When
        partnerQuoteHelper.updateCartWithSpecialBidReasonInformation(reasonList, justification);

        // Then
        verify(cartModel).setSpecialBidBusinessJustification(justification);
        verify(cartModel).setSpecialBidReasons(anySet());
        verify(modelService).save(cartModel);
    }

    @Test
    public void updateCartWithSpecialBidReasonInformation_shouldThrowExceptionWhenCodeInvalid() {
        // Given
        String justification = "Invalid test";
        String code = "INVALID_CODE";

        PartnerSpecialBidReasonData reasonData = new PartnerSpecialBidReasonData();
        reasonData.setCode(code);
        List<PartnerSpecialBidReasonData> reasonList = Collections.singletonList(reasonData);

        IbmPartnerCartModel cartModel = mock(IbmPartnerCartModel.class);
        PartnerSpecialBidReasonModel invalidModel = mock(PartnerSpecialBidReasonModel.class);

        when(cartService.getSessionCart()).thenReturn(cartModel);
        when(partnerSpecialBidReasonService.getSpecialBidReasonById(code)).thenReturn(invalidModel);
        when(invalidModel.getCode()).thenReturn("DIFFERENT_CODE");

        // When + Then
        assertThrows(CommerceCartModificationException.class, () ->
            partnerQuoteHelper.updateCartWithSpecialBidReasonInformation(reasonList,
                justification));
    }

    @Test
    public void updateCartWithSpecialBidReasonInformation_shouldThrowQuoteExceptionWhenModelNotFound() {
        // Given
        String justification = "Justify";
        String code = "REASON123";

        PartnerSpecialBidReasonData reasonData = new PartnerSpecialBidReasonData();
        reasonData.setCode(code);
        List<PartnerSpecialBidReasonData> reasonList = Collections.singletonList(reasonData);

        when(cartService.getSessionCart()).thenThrow(new ModelNotFoundException("Cart not found"));

        // When + Then
        assertThrows(QuoteException.class, () ->
            partnerQuoteHelper.updateCartWithSpecialBidReasonInformation(reasonList,
                justification));
    }

}


