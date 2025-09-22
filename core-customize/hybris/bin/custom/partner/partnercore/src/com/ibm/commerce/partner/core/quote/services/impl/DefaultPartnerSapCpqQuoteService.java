package com.ibm.commerce.partner.core.quote.services.impl;


import com.ibm.commerce.data.order.PartnerCpqQuoteCollaboratorsRequestData;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.exceptions.IbmWebServiceFailureException;
import com.ibm.commerce.partner.core.model.IbmConsumedDestinationModel;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.IbmPartnerConsumedDestinationOAuthCredentialModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.order.approvalComments.data.response.PartnerApprovalsResponseData;
import com.ibm.commerce.partner.core.order.approvalComments.data.response.PartnerQuoteApprovalsInfoResponseData;
import com.ibm.commerce.partner.core.outboundservices.quote.data.request.PartnerCPQCreateQuoteRequestData;
import com.ibm.commerce.partner.core.outboundservices.quote.data.request.PartnerCPQRemoveProductConfigRequestData;
import com.ibm.commerce.partner.core.outboundservices.quote.data.response.PartnerCPQCreateQuoteResponseData;
import com.ibm.commerce.partner.core.outboundservices.quote.data.response.PartnerCPQSubmitQuoteResponseData;
import com.ibm.commerce.partner.core.outboundservices.quote.data.response.PartnerCPQValidateQuoteResponseData;
import com.ibm.commerce.partner.core.quote.services.PartnerSapCpqQuoteService;
import com.ibm.commerce.partner.core.services.IbmConsumedDestinationService;
import com.ibm.commerce.partner.core.services.IbmOutboundIntegrationService;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * This class is integrating to CPQ Quote Service
 */
public class DefaultPartnerSapCpqQuoteService implements PartnerSapCpqQuoteService {

    private static final Logger LOG = LoggerFactory.getLogger(
        DefaultPartnerSapCpqQuoteService.class);

    private static final String QUOTE_SERVICE_INTEGRATION_ERROR_MESSAGE = "Connection issue while fetching data in CPQ";
    private final IbmConsumedDestinationService consumedDestinationService;
    private final IbmOutboundIntegrationService outboundIntegrationService;
    private final Converter<AbstractOrderModel,PartnerCPQCreateQuoteRequestData > partnerCPQCreateQuoteRequestDataConverter ;
    private final CartService cartService;
    public DefaultPartnerSapCpqQuoteService(
        IbmConsumedDestinationService consumedDestinationService,
        IbmOutboundIntegrationService outboundIntegrationService,
        Converter<AbstractOrderModel, PartnerCPQCreateQuoteRequestData> partnerCPQCreateQuoteRequestDataConverter,CartService cartService) {
        this.consumedDestinationService = consumedDestinationService;
        this.outboundIntegrationService = outboundIntegrationService;
        this.partnerCPQCreateQuoteRequestDataConverter = partnerCPQCreateQuoteRequestDataConverter;
        this.cartService= cartService;
    }

    /**
     *
     * @param cpqExternalQuoteId
     * @return PartnerApprovalsResponseData
     */

    protected PartnerApprovalsResponseData getApprovalDetail(final String cpqExternalQuoteId) {
        final IbmConsumedDestinationModel destinationModel = (IbmConsumedDestinationModel) getConsumedDestinationService().findActiveConsumedDestinationByIdAndTargetId(
            PartnercoreConstants.CPQ_APPROVAL_COMMENTS_CONSUMED_DESTINATION_ID,
            PartnercoreConstants.CPQ_APPROVAL_COMMENTS_DESTINATION_ID);
        final HttpHeaders headers = getOutboundIntegrationService().getHeaders(destinationModel);
        populateCommonQuoteHeaders(destinationModel, headers);
        final String url = UriComponentsBuilder.fromHttpUrl(destinationModel.getCustomUri())
            .buildAndExpand(cpqExternalQuoteId).encode().toUriString();
        final Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("cpqExternalQuoteId", cpqExternalQuoteId);
        final String urlTemplate = getOutboundIntegrationService().buildUrlWithParams(url,
            uriVariables);
        return getOutboundIntegrationService().sendRequest(HttpMethod.GET,
            urlTemplate, headers, null, PartnerApprovalsResponseData.class,
            HttpStatus.OK);
    }

    /**
     *
     * @param consumedDestination
     * @param headers
     */
    protected void populateCommonQuoteHeaders(
        final ConsumedDestinationModel consumedDestination,
        HttpHeaders headers) {
        if (headers == null) {
            headers = new HttpHeaders();
        }
        if (consumedDestination.getCredential() instanceof IbmPartnerConsumedDestinationOAuthCredentialModel credentialModel) {
            headers.add(PartnercoreConstants.CPQ_QUOTE_ID, credentialModel.getClientId());
            headers.add(PartnercoreConstants.CPQ_QUOTE_PASSWORD, credentialModel.getClientSecret());
            headers.add(PartnercoreConstants.CPQ_QUOTE_ACUBICAPI, credentialModel.getAcubicApi());
        }
    }

    /**
     *
     * @param partnerApprovalsResponseData
     * @return List<PartnerQuoteApprovalsInfoResponseData>
     */
    protected List<PartnerQuoteApprovalsInfoResponseData> sortApprovalComments(final PartnerApprovalsResponseData partnerApprovalsResponseData) {
        if(CollectionUtils.isNotEmpty(partnerApprovalsResponseData.getApproval().getApprovalInfos())) {
            LOG.debug("The Comment list is not Empty  from CPQ");
            List<PartnerQuoteApprovalsInfoResponseData> approvalsInfoResponseFilterData=partnerApprovalsResponseData.getApproval().getApprovalInfos().stream().filter(partnerApprovalsInfoResponseData -> Objects.nonNull(partnerApprovalsInfoResponseData.getDateResolved()) && Objects.nonNull(partnerApprovalsInfoResponseData.getBpApprovalComment())).toList();
            if(CollectionUtils.isEmpty(approvalsInfoResponseFilterData)){
                LOG.debug("The Comment list is Empty after null check");
                return Collections.emptyList();
            }
            List<PartnerQuoteApprovalsInfoResponseData> sortedComments = approvalsInfoResponseFilterData.stream()
                .sorted(Comparator.comparing(PartnerQuoteApprovalsInfoResponseData::getDateResolved).reversed())
                .collect(Collectors.toList());
            LOG.debug("The Comment list is sorted based on Date");
            return sortedComments;
        }
        LOG.debug("The Comment list is Empty  from CPQ");
        return  Collections.emptyList();
    }

    /**
     *
     * @param cpqExternalId
     * @return List<PartnerQuoteApprovalsInfoResponseData>
     */
    public List<PartnerQuoteApprovalsInfoResponseData> fetchApprovalCommentsforQuote(final String cpqExternalId){
        try{
            PartnerApprovalsResponseData partnerApprovalsResponseData = getApprovalDetail(cpqExternalId);
            if((Objects.isNull(partnerApprovalsResponseData)) || Objects.isNull(partnerApprovalsResponseData.getApproval())){
                throw new IbmWebServiceFailureException(QUOTE_SERVICE_INTEGRATION_ERROR_MESSAGE);
            }
            else {
                return sortApprovalComments(partnerApprovalsResponseData);
            }
        }
        catch (final IbmWebServiceFailureException ex){
            return  Collections.emptyList();
        }
    }

    /**
     * Sends collaborator information for the given IBM Partner Quote Model to CPQ.
     *
     * <p>This method retrieves the appropriate destination model, prepares headers,
     * builds the request URL, and sends the collaborator data via an HTTP POST request.
     *
     * @param ibmPartnerQuoteModel The IBM Partner Quote Model containing collaborator details.
     * @throws IbmWebServiceFailureException If the request fails due to a web service error.
     */
    @Override
    public void postCollaboratorInfo(IbmPartnerQuoteModel ibmPartnerQuoteModel)
        throws IbmWebServiceFailureException {
        final IbmConsumedDestinationModel destinationModel = (IbmConsumedDestinationModel) getConsumedDestinationService().findActiveConsumedDestinationByIdAndTargetId(
            PartnercoreConstants.CPQ_QUOTE_SUBMIT_CONSUMED_DESTINATION_ID,
            PartnercoreConstants.CPQ_QUOTE_SUBMIT_DESTINATION_ID);
        final HttpHeaders headers = getOutboundIntegrationService().getHeaders(
            destinationModel);
        populateCommonQuoteHeaders(destinationModel, headers);
        final Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put(PartnercoreConstants.CPQ_EXTERNAL_QUOTE_ID,
            ibmPartnerQuoteModel.getCpqExternalQuoteId());
        final String urlTemplate = getOutboundIntegrationService().buildUrlWithParams(
            UriComponentsBuilder.fromHttpUrl(destinationModel.getCustomUri())
                .buildAndExpand(ibmPartnerQuoteModel.getCpqExternalQuoteId()).encode()
                .toUriString(),
            uriVariables);
        getOutboundIntegrationService().sendRequest(HttpMethod.POST,
            urlTemplate, headers, prepareCpqCollaboratorRequestData(ibmPartnerQuoteModel),
            Void.class,
            Arrays.asList(HttpStatus.OK));
    }


    /**
     * Sends the updated list of collaborator emails from the given {@link IbmPartnerCartModel} to
     * the external CPQ (Configure Price Quote) system.
     *
     * <p>This method is typically invoked after updating the collaborator emails
     * in the cart and is responsible for syncing those collaborators with CPQ.</p>
     *
     * @param ibmPartnerCartModel the cart model containing the updated list of collaborator emails;
     *                            must not be null and must have a valid quote reference
     */
    @Override
    public void addCollaboratorsToCpq(IbmPartnerCartModel ibmPartnerCartModel)
        throws IbmWebServiceFailureException {
        final IbmConsumedDestinationModel destinationModel = (IbmConsumedDestinationModel) getConsumedDestinationService().findActiveConsumedDestinationByIdAndTargetId(
            PartnercoreConstants.CPQ_QUOTE_SUBMIT_CONSUMED_DESTINATION_ID,
            PartnercoreConstants.CPQ_QUOTE_SUBMIT_DESTINATION_ID);
        final HttpHeaders headers = getOutboundIntegrationService().getHeaders(
            destinationModel);
        populateCommonQuoteHeaders(destinationModel, headers);
        final Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put(PartnercoreConstants.CPQ_EXTERNAL_QUOTE_ID,
            ibmPartnerCartModel.getCpqExternalQuoteId());
        final String urlTemplate = getOutboundIntegrationService().buildUrlWithParams(
            UriComponentsBuilder.fromHttpUrl(destinationModel.getCustomUri())
                .buildAndExpand(ibmPartnerCartModel.getQuoteReference().getCpqExternalQuoteId())
                .encode()
                .toUriString(),
            uriVariables);
        getOutboundIntegrationService().sendRequest(HttpMethod.POST,
            urlTemplate, headers, prepareCpqCollaboratorRequestData(ibmPartnerCartModel),
            Void.class,
            Arrays.asList(HttpStatus.OK));
    }


    /**
     * Prepares the CPQ collaborator request data by extracting collaborator emails from the given
     * IBM Partner Cart Model.
     *
     * <p>This method initializes a {@link PartnerCpqQuoteCollaboratorsRequestData} object
     * and populates it with the list of collaborator emails if available.
     *
     * @param ibmPartnerCartModel The IBM Partner Quote Model containing collaborator email
     *                            details.
     * @return A {@link PartnerCpqQuoteCollaboratorsRequestData} object populated with collaborator
     * emails.
     */
    public final PartnerCpqQuoteCollaboratorsRequestData prepareCpqCollaboratorRequestData(
        IbmPartnerCartModel ibmPartnerCartModel) {
        PartnerCpqQuoteCollaboratorsRequestData partnerCpqQuoteCollaboratorsRequestData = new PartnerCpqQuoteCollaboratorsRequestData();
        partnerCpqQuoteCollaboratorsRequestData.setCollaborators(
            CollectionUtils.isNotEmpty(ibmPartnerCartModel.getCollaboratorEmails())
                ? new ArrayList<>(ibmPartnerCartModel.getCollaboratorEmails())
                : new ArrayList<>());
        return partnerCpqQuoteCollaboratorsRequestData;
    }


    /**
     * Prepares the CPQ collaborator request data by extracting collaborator emails from the given
     * IBM Partner Quote Model.
     *
     * <p>This method initializes a {@link PartnerCpqQuoteCollaboratorsRequestData} object
     * and populates it with the list of collaborator emails if available.
     *
     * @param ibmPartnerQuoteModel The IBM Partner Quote Model containing collaborator email
     *                             details.
     * @return A {@link PartnerCpqQuoteCollaboratorsRequestData} object populated with collaborator
     * emails.
     */
    public final PartnerCpqQuoteCollaboratorsRequestData prepareCpqCollaboratorRequestData(
        IbmPartnerQuoteModel ibmPartnerQuoteModel) {
        PartnerCpqQuoteCollaboratorsRequestData partnerCpqQuoteCollaboratorsRequestData = new PartnerCpqQuoteCollaboratorsRequestData();
        partnerCpqQuoteCollaboratorsRequestData.setCollaborators(
            CollectionUtils.isNotEmpty(ibmPartnerQuoteModel.getCollaboratorEmails())
                ? new ArrayList<>(ibmPartnerQuoteModel.getCollaboratorEmails())
                : new ArrayList<>());
        return partnerCpqQuoteCollaboratorsRequestData;
    }

    /**
     * Creates a quote in the CPQ system using the provided {@link IbmPartnerCartModel}.
     * <p>
     * Fetches the active CPQ destination configuration from the consumed destination service
     * Prepares HTTP headers, Converts the cart data into {@link PartnerCPQCreateQuoteRequestData}
     * request object, Builds the CPQ endpoint URL with RequestParam and Sends an HTTP POST request
     * to the CPQ endpoint and expects a {@link PartnerCPQCreateQuoteResponseData} in response
     * </p>
     *
     * @param cart the {@link IbmPartnerCartModel} cart data
     * @return a {@link PartnerCPQCreateQuoteResponseData} object containing the response
     * @throws IbmWebServiceFailureException if any error occurs while invoking the CPQ service
     */
    @Override
    public PartnerCPQCreateQuoteResponseData createQuoteInCPQ(IbmPartnerCartModel cart)
        throws IbmWebServiceFailureException {
        final IbmConsumedDestinationModel destinationModel = (IbmConsumedDestinationModel) getConsumedDestinationService().findActiveConsumedDestinationByIdAndTargetId(
            PartnercoreConstants.CPQ_CREATE_QUOTE_CONSUMED_DESTINATION_ID,
            PartnercoreConstants.CPQ_CREATE_QUOTE_DESTINATION_ID);
        final HttpHeaders headers = getOutboundIntegrationService().getHeaders(
            destinationModel);
        populateCommonQuoteHeaders(destinationModel, headers);
        final PartnerCPQCreateQuoteRequestData request = getPartnerCPQCreateQuoteRequestDataConverter().convert(
            cart);
        final Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put(PartnercoreConstants.QUOTE_NUMBER, cart.getPriceUid());
        final String urlTemplate = getOutboundIntegrationService().buildUrlWithParams(
            UriComponentsBuilder.fromHttpUrl(destinationModel.getCustomUri())
                .buildAndExpand(cart.getPriceUid()).encode()
                .toUriString(),
            uriVariables);
        return getOutboundIntegrationService().sendRequest(HttpMethod.POST,
            urlTemplate, headers, request,
            PartnerCPQCreateQuoteResponseData.class,
            HttpStatus.OK);
    }

    /**
     * Validates a quote in the CPQ system using the provided {@link IbmPartnerQuoteModel}.
     * <p>
     * Fetches the active CPQ destination configuration from the consumed destination service
     * Prepares HTTP headers,, Builds the CPQ endpoint URL with RequestParam and Sends an HTTP POST
     * request to the CPQ endpoint and expects a {@link PartnerCPQValidateQuoteResponseData} in
     * response
     * </p>
     */

    @Override
    public PartnerCPQValidateQuoteResponseData cpqQuoteValidation(
        IbmPartnerQuoteModel ibmPartnerQuoteModel) {

        final IbmConsumedDestinationModel destinationModel = (IbmConsumedDestinationModel) getConsumedDestinationService().findActiveConsumedDestinationByIdAndTargetId(
            PartnercoreConstants.CPQ_QUOTE_COMMON_VALIDATION_CONSUMED_DESTINATION_ID,
            PartnercoreConstants.CPQ_QUOTE_COMMON_VALIDATION_DESTINATION_ID);
        final HttpHeaders headers = getOutboundIntegrationService().getHeaders(
            destinationModel);
        populateCommonQuoteHeaders(destinationModel, headers);
        final String url = UriComponentsBuilder.fromHttpUrl(destinationModel.getCustomUri())
            .buildAndExpand(ibmPartnerQuoteModel.getCpqExternalQuoteId()).encode().toUriString();
        final Map<String, String> uriVariables = new HashMap<>();
        final String urlTemplate = getOutboundIntegrationService().buildUrlWithParams(url,
            uriVariables);

        return getOutboundIntegrationService().sendRequest(HttpMethod.PUT,
            urlTemplate, headers, null,
            PartnerCPQValidateQuoteResponseData.class,
            Arrays.asList(HttpStatus.OK, HttpStatus.BAD_REQUEST));
    }

    /**
     * Submit a quote in the CPQ system using the provided {@link IbmPartnerQuoteModel}.
     * <p>
     * Fetches the active CPQ destination configuration from the consumed destination service
     * Prepares HTTP headers,, Builds the CPQ endpoint URL with RequestParam and Sends an HTTP POST
     * request to the CPQ endpoint and expects a {@link PartnerCPQSubmitQuoteResponseData} in
     * response
     * </p>
     */

    @Override
    public PartnerCPQSubmitQuoteResponseData cpqQuoteSubmit(
        IbmPartnerQuoteModel ibmPartnerQuoteModel) {

        final IbmConsumedDestinationModel destinationModel = (IbmConsumedDestinationModel) getConsumedDestinationService().findActiveConsumedDestinationByIdAndTargetId(
            PartnercoreConstants.CPQ_QUOTE_COMMON_VALIDATION_SUBMIT_CONSUMED_DESTINATION_ID,
            PartnercoreConstants.CPQ_QUOTE_COMMON_VALIDATION_SUBMIT_DESTINATION_ID);
        final HttpHeaders headers = getOutboundIntegrationService().getHeaders(
            destinationModel);
        populateCommonQuoteHeaders(destinationModel, headers);
        final Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put(PartnercoreConstants.CPQ_EXTERNAL_QUOTE_ID,
            ibmPartnerQuoteModel.getCpqExternalQuoteId());
        final String urlTemplate = getOutboundIntegrationService().buildUrlWithParams(
            UriComponentsBuilder.fromHttpUrl(destinationModel.getCustomUri())
                .queryParam(PartnercoreConstants.CPQ_QUOTE_COMMON_SUBMITTER_NAME,
                    ibmPartnerQuoteModel.getUser().getName())
                .queryParam(PartnercoreConstants.CPQ_QUOTE_COMMON_SUBMITTER_ID,
                    ibmPartnerQuoteModel.getUser().getAddresses()
                        .stream().map(AddressModel::getEmail).filter(Objects::nonNull).findFirst()
                        .orElse(StringUtils.EMPTY))
                .buildAndExpand(ibmPartnerQuoteModel.getCpqExternalQuoteId()).encode()
                .toUriString(),
            uriVariables);
        return getOutboundIntegrationService().sendRequest(HttpMethod.PUT,
            urlTemplate, headers, null,
            PartnerCPQSubmitQuoteResponseData.class,
            Arrays.asList(HttpStatus.OK, HttpStatus.BAD_REQUEST));
    }

    /**
     * Sends a request to the CPQ system to remove one or more collaborators from the specified
     * quote.
     * <p>
     * This method builds the outbound integration request by resolving the appropriate
     * {@link IbmConsumedDestinationModel}, setting required headers, building the URI with path
     * variables, and preparing the request body using the list of removed collaborator emails.
     * </p>
     *
     * @param ibmPartnerCartModel the partner cart model containing the quote reference information
     * @param removedEmails       a list of collaborator email addresses to be removed from the CPQ
     *                            quote
     * @throws IbmWebServiceFailureException if the request to the CPQ service fails or returns an
     *                                       unexpected response
     */
    @Override
    public void removeCollaboratorsToCpq(IbmPartnerCartModel ibmPartnerCartModel,
        final List<String> removedEmails)
        throws IbmWebServiceFailureException {
        final IbmConsumedDestinationModel destinationModel = (IbmConsumedDestinationModel) getConsumedDestinationService().findActiveConsumedDestinationByIdAndTargetId(
            PartnercoreConstants.CPQ_QUOTE_REMOVE_COLLABORATORS_CONSUMED_DESTINATION_ID,
            PartnercoreConstants.CPQ_QUOTE_SUBMIT_DESTINATION_ID);
        final HttpHeaders headers = getOutboundIntegrationService().getHeaders(
            destinationModel);
        populateCommonQuoteHeaders(destinationModel, headers);
        final Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put(PartnercoreConstants.CPQ_EXTERNAL_QUOTE_ID,
            ibmPartnerCartModel.getQuoteReference().getCpqExternalQuoteId());
        final String urlTemplate = getOutboundIntegrationService().buildUrlWithParams(
            UriComponentsBuilder.fromHttpUrl(destinationModel.getCustomUri())
                .buildAndExpand(ibmPartnerCartModel.getCpqExternalQuoteId())
                .encode()
                .toUriString(),
            uriVariables);
        getOutboundIntegrationService().sendRequest(HttpMethod.POST,
            urlTemplate, headers, prepareRemoveCpqCollaboratorRequestData(removedEmails),
            Void.class,
            Arrays.asList(HttpStatus.OK));
    }

    public IbmOutboundIntegrationService getOutboundIntegrationService() {
        return outboundIntegrationService;
    }

    /**
     * Prepares a {@link PartnerCpqQuoteCollaboratorsRequestData} object containing the list of
     * email addresses to be removed as collaborators from a CPQ quote.
     *
     * <p>If the provided {@code removeEmails} list is not empty, it will be copied into
     * the collaborators field of the request object. Otherwise, an empty list will be set.</p>
     *
     * @param removeEmails a list of email addresses to be removed as collaborators; may be null or
     *                     empty
     * @return a {@link PartnerCpqQuoteCollaboratorsRequestData} object with the collaborators set
     */
    protected final PartnerCpqQuoteCollaboratorsRequestData prepareRemoveCpqCollaboratorRequestData(
        final List<String> removeEmails) {
        PartnerCpqQuoteCollaboratorsRequestData partnerCpqQuoteCollaboratorsRequestData = new PartnerCpqQuoteCollaboratorsRequestData();
        partnerCpqQuoteCollaboratorsRequestData.setCollaborators(
            CollectionUtils.isNotEmpty(removeEmails)
                ? new ArrayList<>(removeEmails)
                : new ArrayList<>());
        return partnerCpqQuoteCollaboratorsRequestData;
    }
/** Sends a request to the CPQ system to remove a configured product from the specified quote.
        * <p>
 * This method builds the outbound integration request by resolving the appropriate
 * {@link IbmConsumedDestinationModel}, setting required headers, building the URI with the
 * CPQ quote ID, and preparing the request body with the given configuration ID.
        * </p>
        *
        * @param configId the configuration ID of the product to be removed from the CPQ quote
 * @throws IbmWebServiceFailureException if the request to the CPQ service fails or returns an
 *                                       unexpected response
 */
@Override
public void removeProductConfigurationInCPQ(final String configId)
    throws IbmWebServiceFailureException {

    final CartModel sessionCart = getCartService().getSessionCart();
           final String cpqQuoteId = ((IbmPartnerCartModel) sessionCart).getCpqExternalQuoteId();

    final IbmConsumedDestinationModel destinationModel = (IbmConsumedDestinationModel) getConsumedDestinationService().findActiveConsumedDestinationByIdAndTargetId(
        PartnercoreConstants.CPQ_REMOVE_CONFIGURED_PRODUCTS_CONSUMED_DESTINATION_ID,
                       PartnercoreConstants.CPQ_REMOVE_CONFIGURED_PRODUCT_DESTINATION_ID);
    final HttpHeaders headers = getOutboundIntegrationService().getHeaders(
        destinationModel);
    populateCommonQuoteHeaders(destinationModel, headers);
    final String url = UriComponentsBuilder.fromHttpUrl(destinationModel.getCustomUri())
            .buildAndExpand(cpqQuoteId).encode().toUriString();
    final Map<String, String> uriVariables = new HashMap<>();
    uriVariables.put(PartnercoreConstants.QUOTE_ID_FROM_CPQ,cpqQuoteId);
        final String urlTemplate = getOutboundIntegrationService().buildUrlWithParams(url,
           Collections.emptyMap());
    getOutboundIntegrationService().sendRequest(HttpMethod.POST,
        urlTemplate, headers, preparePartnerCPQRemoveProductConfigRequestData(configId),
        Void.class,
        HttpStatus.OK);
}

    /**
     * Prepares a {@link PartnerCPQRemoveProductConfigRequestData} object containing configId.
     * @param configId configurationId
     * @return a {@link PartnerCPQRemoveProductConfigRequestData} object with the configurationId
     */
    public final PartnerCPQRemoveProductConfigRequestData preparePartnerCPQRemoveProductConfigRequestData(
         final String configId)
    {
        LOG.info(PartnercoreConstants.PREPARE_REMOVE_PRODUCT_CONFIG_REQUEST_DATA);
        final PartnerCPQRemoveProductConfigRequestData partnerCPQRemoveProductConfigRequestData = new PartnerCPQRemoveProductConfigRequestData();
        partnerCPQRemoveProductConfigRequestData.setConfigurationId(configId);
        return partnerCPQRemoveProductConfigRequestData;
    }

    public IbmConsumedDestinationService getConsumedDestinationService() {
        return consumedDestinationService;
    }

    public Converter<AbstractOrderModel, PartnerCPQCreateQuoteRequestData> getPartnerCPQCreateQuoteRequestDataConverter() {
        return partnerCPQCreateQuoteRequestDataConverter;
    }

    public CartService getCartService() {
        return cartService;
    }


}