package com.ibm.commerce.partner.core.pricing.services.impl;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.enums.CpqPricingTypeEnum;
import com.ibm.commerce.partner.core.model.IbmPartnerConsumedDestinationOAuthCredentialModel;
import com.ibm.commerce.partner.core.order.price.data.request.DealRegRequestData;
import com.ibm.commerce.partner.core.order.price.data.request.EntitledPriceLookUpRequestData;
import com.ibm.commerce.partner.core.order.price.data.request.FullPriceLookUpRequestData;
import com.ibm.commerce.partner.core.order.price.data.request.PriceLookUpHeaderRequestData;
import com.ibm.commerce.partner.core.order.price.data.response.DealRegResponseData;
import com.ibm.commerce.partner.core.order.price.data.response.PriceLookUpResponseData;
import com.ibm.commerce.partner.core.services.IbmConsumedDestinationService;
import com.ibm.commerce.partner.core.services.IbmOutboundIntegrationService;
import com.ibm.commerce.partner.core.util.data.DealRegRequestTestDataGenerator;
import com.ibm.commerce.partner.core.util.data.EntitledPriceLookUpRequestTestDataGenerator;
import com.ibm.commerce.partner.core.util.data.FullPriceLookUpRequestTestDataGenerator;
import com.ibm.commerce.partner.core.util.data.PriceLookUpHeaderRequesTestDataGenerator;
import com.ibm.commerce.partner.core.util.data.PriceLookUpResponseTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.AbstractOrderModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.ConsumedDestinationModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.IBMPartnerConsumedDestinationOAuthCredentialModelTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.apiregistryservices.model.AbstractCredentialModel;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import java.util.Collections;
import java.util.Arrays;
import static org.junit.Assert.assertEquals;
import de.hybris.platform.sap.productconfig.services.model.CPQOrderEntryProductInfoModel;
import org.junit.rules.ExpectedException;
import com.ibm.commerce.partner.core.exceptions.IbmWebServiceFailureException;
import org.junit.Rule;

@UnitTest
public class DefaultPartnerPricingOutboundServiceTest {

    private static final String URL = "https://accountservice.com/price";

    @InjectMocks
    DefaultPartnerPricingOutboundService defaultPartnerPricingOutboundService;

    @Mock
    Converter<AbstractOrderModel, EntitledPriceLookUpRequestData> entitledPriceLookUpRequestDataConverter;
    @Mock
    Converter<AbstractOrderModel, FullPriceLookUpRequestData> fullPriceLookUpRequestDataConverter;
    @Mock
    Converter<AbstractOrderModel, DealRegRequestData> dealRegRequestDataConverter;
    @Mock
    IbmConsumedDestinationService consumedDestinationService;
    @Mock
    IbmOutboundIntegrationService outboundIntegrationService;

    AbstractOrderModel orderModel;
    EntitledPriceLookUpRequestData entitledPriceLookUpRequestData;
    ConsumedDestinationModel destinationModel;

    PriceLookUpHeaderRequestData priceLookUpHeaderRequestData;
    @Mock
    HttpHeaders headers;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        defaultPartnerPricingOutboundService = new DefaultPartnerPricingOutboundService(
            entitledPriceLookUpRequestDataConverter, fullPriceLookUpRequestDataConverter,
            dealRegRequestDataConverter, consumedDestinationService, outboundIntegrationService);
        priceLookUpHeaderRequestData = PriceLookUpHeaderRequesTestDataGenerator.createPriceLookUpHeaderData();
        IbmPartnerConsumedDestinationOAuthCredentialModel consumedOAuthCredentialModel = IBMPartnerConsumedDestinationOAuthCredentialModelTestDataGenerator.createPartnerQuoteOAuthCredentialModel();
        destinationModel = ConsumedDestinationModelTestDataGenerator.createConsumedDestinationModel(
            URL, consumedOAuthCredentialModel);

    }

    @Test
    public void testGetOutboundIntegrationService() {
        Mockito.when(consumedDestinationService.findActiveConsumedDestinationByIdAndTargetId(
            PartnercoreConstants.PRICING_SERVICE_ENTITLED_PRICE_CONSUMED_DESTINATION_ID,
            PartnercoreConstants.PRICING_SERVICE_DESTINATION_ID)).thenReturn(destinationModel);
        entitledPriceLookUpRequestData = EntitledPriceLookUpRequestTestDataGenerator.createEntitledPriceLookUp(
            priceLookUpHeaderRequestData);
        orderModel = AbstractOrderModelTestDataGenerator.createAbstractOrderModel(null, null);
        Mockito.when(entitledPriceLookUpRequestDataConverter.convert(Mockito.any()))
            .thenReturn(entitledPriceLookUpRequestData);
        Mockito.when(outboundIntegrationService.getHeaders(destinationModel))
            .thenReturn(null);
        PriceLookUpResponseData priceLookUpResponseData = PriceLookUpResponseTestDataGenerator.createResponseData(
            CpqPricingTypeEnum.ENTITLED);
        Mockito.when(outboundIntegrationService.sendRequest(HttpMethod.POST,
            destinationModel.getUrl(), null, entitledPriceLookUpRequestData,
            PriceLookUpResponseData.class,
            HttpStatus.OK)).thenReturn(priceLookUpResponseData);
        PriceLookUpResponseData responseData = defaultPartnerPricingOutboundService.getEntitledPrice(
            orderModel);
        Assert.assertNotNull(responseData);
        Assert.assertNotNull(responseData.getConfigurations());
    }

    @Test
    public void testGetFullPrice() {
        PriceLookUpResponseData priceLookUpResponseData = PriceLookUpResponseTestDataGenerator.createResponseData(
            CpqPricingTypeEnum.ENTITLED);
        FullPriceLookUpRequestData requestBody = FullPriceLookUpRequestTestDataGenerator.createPriceLookUpData(
            priceLookUpHeaderRequestData);
        Mockito.when(fullPriceLookUpRequestDataConverter.convert(orderModel))
            .thenReturn(requestBody);
        Mockito.when(consumedDestinationService.findActiveConsumedDestinationByIdAndTargetId(
            PartnercoreConstants.PRICING_SERVICE_FULL_PRICE_CONSUMED_DESTINATION_ID,
            PartnercoreConstants.PRICING_SERVICE_DESTINATION_ID)).thenReturn(destinationModel);
        Mockito.when(outboundIntegrationService.getHeaders(destinationModel))
            .thenReturn(null);
        Mockito.when(outboundIntegrationService.sendRequest(HttpMethod.POST,
            destinationModel.getUrl(), null, requestBody, PriceLookUpResponseData.class,
            HttpStatus.OK)).thenReturn(priceLookUpResponseData);
        PriceLookUpResponseData result = defaultPartnerPricingOutboundService.getFullPrice(
            orderModel);
        Assert.assertEquals(CpqPricingTypeEnum.ENTITLED, result.getType());
    }

    @Test
    public void testGetDealRegDetail() {
        IbmPartnerConsumedDestinationOAuthCredentialModel oAuthCredentialModel = IBMPartnerConsumedDestinationOAuthCredentialModelTestDataGenerator.createPartnerQuoteOAuthCredentialModel();
        destinationModel.setCredential(oAuthCredentialModel);
        Mockito.when(consumedDestinationService.findActiveConsumedDestinationByIdAndTargetId(
            PartnercoreConstants.PRICING_SERVICE_DEAL_REG_CONSUMED_DESTINATION_ID,
            PartnercoreConstants.PRICING_SERVICE_DESTINATION_ID)).thenReturn(destinationModel);
        Mockito.when(outboundIntegrationService.getHeaders(destinationModel))
            .thenReturn(null);
        DealRegRequestData dealRegRequestData = DealRegRequestTestDataGenerator.createDealRegRequestData();
        Mockito.when(dealRegRequestDataConverter.convert(Mockito.any()))
            .thenReturn(dealRegRequestData);
        List<DealRegResponseData> dealRegResponseDatas = defaultPartnerPricingOutboundService.getDealRegDetail(
            orderModel);
        Assert.assertNotNull(dealRegResponseDatas);
    }
    @Test
    public void testGetFullPriceHeaderNull() {
        PriceLookUpResponseData priceLookUpResponseData = PriceLookUpResponseTestDataGenerator.createResponseData(
                CpqPricingTypeEnum.ENTITLED);
        FullPriceLookUpRequestData requestBody = FullPriceLookUpRequestTestDataGenerator.createPriceLookUpData(
                priceLookUpHeaderRequestData);
        Mockito.when(fullPriceLookUpRequestDataConverter.convert(orderModel))
                .thenReturn(requestBody);
        Mockito.when(consumedDestinationService.findActiveConsumedDestinationByIdAndTargetId(
                PartnercoreConstants.PRICING_SERVICE_FULL_PRICE_CONSUMED_DESTINATION_ID,
                PartnercoreConstants.PRICING_SERVICE_DESTINATION_ID)).thenReturn(destinationModel);
        Mockito.when(outboundIntegrationService.getHeaders(destinationModel))
                .thenReturn(null);
        Mockito.when(outboundIntegrationService.sendRequest(HttpMethod.POST,
                destinationModel.getUrl(), null, requestBody, PriceLookUpResponseData.class,
                HttpStatus.OK)).thenReturn(priceLookUpResponseData);
        HttpHeaders headers = new HttpHeaders();
        defaultPartnerPricingOutboundService.populateHeaders(destinationModel,null);
        Assert.assertTrue(headers.isEmpty());
    }
    @Test
    public void testGetFullPriceHeaderNullNotInstance() {
        PriceLookUpResponseData priceLookUpResponseData = PriceLookUpResponseTestDataGenerator.createResponseData(
                CpqPricingTypeEnum.ENTITLED);
        FullPriceLookUpRequestData requestBody = FullPriceLookUpRequestTestDataGenerator.createPriceLookUpData(
                priceLookUpHeaderRequestData);
        Mockito.when(fullPriceLookUpRequestDataConverter.convert(orderModel))
                .thenReturn(requestBody);
        Mockito.when(consumedDestinationService.findActiveConsumedDestinationByIdAndTargetId(
                PartnercoreConstants.PRICING_SERVICE_FULL_PRICE_CONSUMED_DESTINATION_ID,
                PartnercoreConstants.PRICING_SERVICE_DESTINATION_ID)).thenReturn(destinationModel);
        Mockito.when(outboundIntegrationService.getHeaders(destinationModel))
                .thenReturn(null);
        Mockito.when(outboundIntegrationService.sendRequest(HttpMethod.POST,
                destinationModel.getUrl(), null, requestBody, PriceLookUpResponseData.class,
                HttpStatus.OK)).thenReturn(priceLookUpResponseData);
        AbstractCredentialModel value = new AbstractCredentialModel();
        destinationModel.setCredential(value);
        HttpHeaders headers = new HttpHeaders();
        defaultPartnerPricingOutboundService.populateHeaders(destinationModel,headers);
        Assert.assertFalse( destinationModel.getCredential() instanceof IbmPartnerConsumedDestinationOAuthCredentialModel);
    }
    @Test
    public void testGetDealRegDetailNull() {
        PriceLookUpResponseData priceLookUpResponseData = PriceLookUpResponseTestDataGenerator.createResponseData(
                CpqPricingTypeEnum.ENTITLED);
        FullPriceLookUpRequestData requestBody = FullPriceLookUpRequestTestDataGenerator.createPriceLookUpData(
                priceLookUpHeaderRequestData);
        Mockito.when(fullPriceLookUpRequestDataConverter.convert(orderModel))
                .thenReturn(requestBody);
        Mockito.when(consumedDestinationService.findActiveConsumedDestinationByIdAndTargetId(
                PartnercoreConstants.PRICING_SERVICE_FULL_PRICE_CONSUMED_DESTINATION_ID,
                PartnercoreConstants.PRICING_SERVICE_DESTINATION_ID)).thenReturn(destinationModel);
        Mockito.when(outboundIntegrationService.getHeaders(destinationModel))
                .thenReturn(null);
        Mockito.when(outboundIntegrationService.sendRequest(HttpMethod.POST,
                destinationModel.getUrl(), null, requestBody, PriceLookUpResponseData.class,
                HttpStatus.OK)).thenReturn(priceLookUpResponseData);
        ConsumedDestinationModel consumedDestination = new ConsumedDestinationModel();
        HttpHeaders headers = new HttpHeaders();
    defaultPartnerPricingOutboundService.populateDealRegHeaders(consumedDestination,null);
        Assert.assertTrue(headers.isEmpty());
    }
    @Test
    public void testGetDealRegDetaiNotInstance() {
        IbmPartnerConsumedDestinationOAuthCredentialModel oAuthCredentialModel = IBMPartnerConsumedDestinationOAuthCredentialModelTestDataGenerator.createPartnerQuoteOAuthCredentialModel();
        destinationModel.setCredential(oAuthCredentialModel);
        Mockito.when(consumedDestinationService.findActiveConsumedDestinationByIdAndTargetId(
                PartnercoreConstants.PRICING_SERVICE_DEAL_REG_CONSUMED_DESTINATION_ID,
                PartnercoreConstants.PRICING_SERVICE_DESTINATION_ID)).thenReturn(destinationModel);
        Mockito.when(outboundIntegrationService.getHeaders(destinationModel))
                .thenReturn(null);
        DealRegRequestData dealRegRequestData = DealRegRequestTestDataGenerator.createDealRegRequestData();
        Mockito.when(dealRegRequestDataConverter.convert(Mockito.any()))
                .thenReturn(dealRegRequestData);
        AbstractCredentialModel abstractCredentialModel = new AbstractCredentialModel();
        destinationModel.setCredential(abstractCredentialModel);
        defaultPartnerPricingOutboundService.populateDealRegHeaders(destinationModel,headers);
        Assert.assertNull( destinationModel.getCredential().getId());
    }

    @Test
    public void testGetProductInfoReturnsEmptyWhenProductInfosIsEmpty() {
        AbstractOrderEntryModel source = new AbstractOrderEntryModel();
        source.setProductInfos(Collections.emptyList());
        String result = defaultPartnerPricingOutboundService.getProductInfo(source, "someType");
        assertEquals(StringUtils.EMPTY, result);
    }

    @Test
    public void testGetProductInfoReturnsEmptyWhenTypeIsBlank() {
        AbstractOrderEntryModel source = new AbstractOrderEntryModel();
        CPQOrderEntryProductInfoModel info = new CPQOrderEntryProductInfoModel();
        source.setProductInfos(Arrays.asList(info));
        String result = defaultPartnerPricingOutboundService.getProductInfo(source, " ");
        assertEquals(StringUtils.EMPTY, result);
    }

    @Test
    public void testGetProductInfoReturnsEmptyWhenNoMatchingCharacteristicName() {
        AbstractOrderEntryModel source = new AbstractOrderEntryModel();
        CPQOrderEntryProductInfoModel info = new CPQOrderEntryProductInfoModel();
        info.setCpqCharacteristicName("otherType");
        source.setProductInfos(Arrays.asList(info));
        String result = defaultPartnerPricingOutboundService.getProductInfo(source, "someType");
        assertEquals(StringUtils.EMPTY, result);
    }

    @Test
    public void testGetEntitledPriceThrowsExceptionWhenNoDestination() {
        AbstractOrderModel orderModel = new AbstractOrderModel();
        Mockito.when(consumedDestinationService.findActiveConsumedDestinationByIdAndTargetId(
            PartnercoreConstants.PRICING_SERVICE_ENTITLED_PRICE_CONSUMED_DESTINATION_ID,
            PartnercoreConstants.PRICING_SERVICE_DESTINATION_ID)).thenReturn(null);

        expectedException.expect(IbmWebServiceFailureException.class);
        expectedException.expectMessage("No destination found for " + PartnercoreConstants.PRICING_SERVICE_ENTITLED_PRICE_CONSUMED_DESTINATION_ID);

        defaultPartnerPricingOutboundService.getEntitledPrice(orderModel);
    }

    @Test
    public void testGetFullPriceThrowsExceptionWhenNoDestination() {
        AbstractOrderModel orderModel = new AbstractOrderModel();
        Mockito.when(consumedDestinationService.findActiveConsumedDestinationByIdAndTargetId(
            PartnercoreConstants.PRICING_SERVICE_FULL_PRICE_CONSUMED_DESTINATION_ID,
            PartnercoreConstants.PRICING_SERVICE_DESTINATION_ID)).thenReturn(null);

        expectedException.expect(IbmWebServiceFailureException.class);
        expectedException.expectMessage("No destination found for " + PartnercoreConstants.PRICING_SERVICE_FULL_PRICE_CONSUMED_DESTINATION_ID);

        defaultPartnerPricingOutboundService.getFullPrice(orderModel);
    }

    @Test
    public void testGetProductInfoReturnsAssignedValuesWhenTypeMatches() {
        AbstractOrderEntryModel source = new AbstractOrderEntryModel();
        CPQOrderEntryProductInfoModel info = new CPQOrderEntryProductInfoModel();
        info.setCpqCharacteristicName("color");
        info.setCpqCharacteristicAssignedValues("red");
        source.setProductInfos(Arrays.asList(info));
        String result = defaultPartnerPricingOutboundService.getProductInfo(source, "color");
        assertEquals("red", result);
    }

}
