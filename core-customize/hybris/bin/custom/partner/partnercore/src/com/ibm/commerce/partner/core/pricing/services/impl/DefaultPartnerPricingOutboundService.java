package com.ibm.commerce.partner.core.pricing.services.impl;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.exceptions.IbmWebServiceFailureException;
import com.ibm.commerce.partner.core.model.IbmPartnerConsumedDestinationOAuthCredentialModel;
import com.ibm.commerce.partner.core.order.price.data.request.DealRegRequestData;
import com.ibm.commerce.partner.core.order.price.data.request.EntitledPriceLookUpRequestData;
import com.ibm.commerce.partner.core.order.price.data.request.FullPriceLookUpRequestData;
import com.ibm.commerce.partner.core.order.price.data.response.DealRegResponseData;
import com.ibm.commerce.partner.core.order.price.data.response.PriceLookUpResponseData;
import com.ibm.commerce.partner.core.pricing.services.PartnerPricingOutboundService;
import com.ibm.commerce.partner.core.services.IbmConsumedDestinationService;
import com.ibm.commerce.partner.core.services.IbmOutboundIntegrationService;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.sap.productconfig.services.model.CPQOrderEntryProductInfoModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

/**
 * Implementation for {@link PartnerPricingOutboundService}
 */
public class DefaultPartnerPricingOutboundService implements PartnerPricingOutboundService {

    private final Converter<AbstractOrderModel, EntitledPriceLookUpRequestData> entitledPriceLookUpRequestDataConverter;
    private final Converter<AbstractOrderModel, FullPriceLookUpRequestData> fullPriceLookUpRequestDataConverter;
    private final Converter<AbstractOrderModel, DealRegRequestData> dealRegRequestDataConverter;
    private final IbmConsumedDestinationService consumedDestinationService;
    private final IbmOutboundIntegrationService outboundIntegrationService;

    public DefaultPartnerPricingOutboundService(
        final Converter<AbstractOrderModel, EntitledPriceLookUpRequestData> entitledPriceLookUpRequestDataConverter,
        final Converter<AbstractOrderModel, FullPriceLookUpRequestData> fullPriceLookUpRequestDataConverter,
        final Converter<AbstractOrderModel, DealRegRequestData> dealRegRequestDataConverter,
        final IbmConsumedDestinationService consumedDestinationService,
        final IbmOutboundIntegrationService outboundIntegrationService) {
        this.entitledPriceLookUpRequestDataConverter = entitledPriceLookUpRequestDataConverter;
        this.fullPriceLookUpRequestDataConverter = fullPriceLookUpRequestDataConverter;
        this.dealRegRequestDataConverter = dealRegRequestDataConverter;
        this.consumedDestinationService = consumedDestinationService;
        this.outboundIntegrationService = outboundIntegrationService;
    }

    @Override
    public PriceLookUpResponseData getEntitledPrice(final AbstractOrderModel orderModel) {
        final EntitledPriceLookUpRequestData requestBody = getEntitledPriceLookUpRequestDataConverter().convert(
            orderModel);
        final ConsumedDestinationModel destinationModel = getConsumedDestinationService().findActiveConsumedDestinationByIdAndTargetId(
            PartnercoreConstants.PRICING_SERVICE_ENTITLED_PRICE_CONSUMED_DESTINATION_ID,
            PartnercoreConstants.PRICING_SERVICE_DESTINATION_ID);
        if (destinationModel == null) {
            throw new IbmWebServiceFailureException(String.format("No destination found for %s",
                PartnercoreConstants.PRICING_SERVICE_ENTITLED_PRICE_CONSUMED_DESTINATION_ID));
        }
        final HttpHeaders headers = getOutboundIntegrationService().getHeaders(destinationModel);
        populateHeaders(destinationModel, headers);
        return getOutboundIntegrationService().sendRequest(HttpMethod.POST,
            destinationModel.getUrl(), headers, requestBody, PriceLookUpResponseData.class,
            HttpStatus.OK);
    }

    @Override
    public PriceLookUpResponseData getFullPrice(final AbstractOrderModel orderModel) {
        final FullPriceLookUpRequestData requestBody = getFullPriceLookUpRequestDataConverter().convert(
            orderModel);
        final ConsumedDestinationModel destinationModel = getConsumedDestinationService().findActiveConsumedDestinationByIdAndTargetId(
            PartnercoreConstants.PRICING_SERVICE_FULL_PRICE_CONSUMED_DESTINATION_ID,
            PartnercoreConstants.PRICING_SERVICE_DESTINATION_ID);
        if (destinationModel == null) {
            throw new IbmWebServiceFailureException(String.format("No destination found for %s",
                PartnercoreConstants.PRICING_SERVICE_FULL_PRICE_CONSUMED_DESTINATION_ID));
        }
        final HttpHeaders headers = getOutboundIntegrationService().getHeaders(destinationModel);
        populateHeaders(destinationModel, headers);
        return getOutboundIntegrationService().sendRequest(HttpMethod.POST,
            destinationModel.getUrl(), headers, requestBody, PriceLookUpResponseData.class,
            HttpStatus.OK);
    }

    @Override
    public List<DealRegResponseData> getDealRegDetail(final AbstractOrderModel orderModel) {
        final DealRegRequestData requestBody = getDealRegRequestDataConverter().convert(orderModel);
        final ConsumedDestinationModel destinationModel = getConsumedDestinationService().findActiveConsumedDestinationByIdAndTargetId(
            PartnercoreConstants.PRICING_SERVICE_DEAL_REG_CONSUMED_DESTINATION_ID,
            PartnercoreConstants.PRICING_SERVICE_DESTINATION_ID);
        final HttpHeaders headers = getOutboundIntegrationService().getHeaders(destinationModel);
        populateDealRegHeaders(destinationModel, headers);
        ParameterizedTypeReference<List<DealRegResponseData>> responseType = new ParameterizedTypeReference<>() {
        };
        return getOutboundIntegrationService().sendRequest(HttpMethod.POST,
            destinationModel.getUrl(), headers, requestBody, responseType,
            Arrays.asList(HttpStatus.OK));
    }

    @Override
    public String getProductInfo(final AbstractOrderEntryModel source, final String type) {

        if (CollectionUtils.isEmpty(source.getProductInfos()) || StringUtils.isBlank(type)) {
            return StringUtils.EMPTY;
        }
        final Optional<CPQOrderEntryProductInfoModel> typeInfo = source.getProductInfos().stream()
            .filter(CPQOrderEntryProductInfoModel.class::isInstance)
            .map(CPQOrderEntryProductInfoModel.class::cast)
            .filter(info -> type.equalsIgnoreCase(info.getCpqCharacteristicName())).findAny();
        if (typeInfo.isPresent()) {
            final CPQOrderEntryProductInfoModel infoModel = typeInfo.get();
            return infoModel.getCpqCharacteristicAssignedValues();
        }

        return StringUtils.EMPTY;
    }

    protected void populateHeaders(final ConsumedDestinationModel consumedDestination,
        HttpHeaders headers) {
        if (headers == null) {
            headers = new HttpHeaders();
        }
        if (consumedDestination.getCredential() instanceof IbmPartnerConsumedDestinationOAuthCredentialModel credentialModel) {
            headers.add("x-ibm-client-id", credentialModel.getClientId());
            headers.add("x-ibm-client-secret", credentialModel.getClientSecret());
        }
    }

    protected void populateDealRegHeaders(final ConsumedDestinationModel consumedDestination,
        HttpHeaders headers) {
        if (headers == null) {
            headers = new HttpHeaders();
        }
        if (consumedDestination.getCredential() instanceof IbmPartnerConsumedDestinationOAuthCredentialModel credentialModel) {
            headers.add("x-ibm-client-id", credentialModel.getClientId());
            headers.add("x-ibm-client-secret", credentialModel.getClientSecret());
            headers.add("x-acubic-api", credentialModel.getAcubicApi());
        }
    }

    public Converter<AbstractOrderModel, EntitledPriceLookUpRequestData> getEntitledPriceLookUpRequestDataConverter() {
        return entitledPriceLookUpRequestDataConverter;
    }

    public Converter<AbstractOrderModel, FullPriceLookUpRequestData> getFullPriceLookUpRequestDataConverter() {
        return fullPriceLookUpRequestDataConverter;
    }

    public Converter<AbstractOrderModel, DealRegRequestData> getDealRegRequestDataConverter() {
        return dealRegRequestDataConverter;
    }

    public IbmConsumedDestinationService getConsumedDestinationService() {
        return consumedDestinationService;
    }

    public IbmOutboundIntegrationService getOutboundIntegrationService() {
        return outboundIntegrationService;
    }
}
