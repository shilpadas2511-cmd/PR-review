package com.ibm.commerce.partner.core.outbound.service.impl;

import com.ibm.commerce.partner.core.model.PartnerSpecialBidReasonModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.enums.SalesApplication;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.enumeration.EnumerationService;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.ibm.commerce.common.core.product.service.IbmProductService;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.IbmPartnerAgreementDetailModel;
import com.ibm.commerce.partner.core.model.IbmPartnerOpportunityModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.outboundservices.quote.data.request.PartnerCpqQuoteRequestData;
import com.ibm.commerce.partner.core.util.model.IbmPartnerQuoteDataModelGenerator;

/**
 * Test class for {@link DefaultPartnerSapCpiQuoteCustomFieldsMapperService}
 */
@UnitTest
public class DefaultPartnerSapCpiQuoteCustomFieldsMapperServiceTest {

    private static final String TEST_TRANSACTION_PRICE_LEVEL = "testTransactionPriceLevel";
    private static final String TEST_PROGRAM_TYPE = "testProgramType";
    private static final String TEST_OPPORTUNITY_CODE = "testOpportunityId";

    private static final String DEFAULT_DESTINATION_CHANNEL = "J";
    private static final String DEFAULT_SALES_ORGANIZATION = "testOrg";
    private static final String DEFAULT_FULFILLMENT_SOURCE = "testFulfillmentSource";
    private static final String DEFAULT_OPPORTUNITY_ID = "testOppId";
    private static final String DEFAULT_JUSTIFICATION_CODE = "testJustificationCode";
    private static final String DEFAULT_SALES_APPLICATION = "testSalesApplication";

    @InjectMocks
    private DefaultPartnerSapCpiQuoteCustomFieldsMapperService service;

    @Mock
    private IbmProductService productService;

    @Mock
    private UserModel user;

    @Mock
    private IbmPartnerOpportunityModel opportunity;

    @Mock
    private IbmPartnerAgreementDetailModel agreementDetail;

    private IbmPartnerQuoteModel quoteModel;

    @Mock
    private EnumerationService enumerationService;

    @Mock
    private PartnerSpecialBidReasonModel specialBidReason;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new DefaultPartnerSapCpiQuoteCustomFieldsMapperService(
            DEFAULT_DESTINATION_CHANNEL,
            DEFAULT_SALES_ORGANIZATION,
            DEFAULT_FULFILLMENT_SOURCE,
            DEFAULT_OPPORTUNITY_ID,
            DEFAULT_JUSTIFICATION_CODE,
            productService,
            TEST_TRANSACTION_PRICE_LEVEL,
            enumerationService,
            DEFAULT_SALES_APPLICATION
        );
    }

    @Test
    public void testMap() {
        final PartnerCpqQuoteRequestData target = new PartnerCpqQuoteRequestData();

        Mockito.when(agreementDetail.getProgramType()).thenReturn(TEST_PROGRAM_TYPE);
        Mockito.when(opportunity.getCode()).thenReturn(TEST_OPPORTUNITY_CODE);
        SalesApplication salesAppEnum = SalesApplication.PARTNER_COMMERCE;
        quoteModel = IbmPartnerQuoteDataModelGenerator.createOutdatedIbmPartnerQuoteModel(
            new Date(), user, opportunity,
            agreementDetail, TEST_TRANSACTION_PRICE_LEVEL);
        quoteModel.setSalesApplication(salesAppEnum);
        quoteModel.setSpecialBidReason(specialBidReason);
        quoteModel.setSpecialBidBusinessJustification("Justification");
        Mockito.when(enumerationService.getEnumerationName(salesAppEnum)).thenReturn("PARTNER_COMMERCE_DISPLAY_NAME");

        final PartnerCpqQuoteRequestData result = service.map(quoteModel, target);

        Assert.assertNotNull(result);
        Assert.assertNotNull(target.getCustomFields());
        Assert.assertEquals(PartnercoreConstants.EXPIRATIONDATE, target.getCustomFields().get(0).getName());
        Assert.assertNotNull(target.getCustomFields().get(0).getContent());
        Assert.assertEquals(PartnercoreConstants.OPPORTUNITYID, target.getCustomFields().get(3).getName());
        Assert.assertEquals(TEST_OPPORTUNITY_CODE, target.getCustomFields().get(3).getContent());
        Assert.assertEquals(PartnercoreConstants.DISTRIBUTIONCHANNELKEY, target.getCustomFields().get(4).getName());
        Assert.assertEquals(DEFAULT_DESTINATION_CHANNEL, target.getCustomFields().get(4).getContent());
        Assert.assertEquals(PartnercoreConstants.PROGRAMTYPE, target.getCustomFields().get(6).getName());
        Assert.assertEquals(TEST_PROGRAM_TYPE, target.getCustomFields().get(6).getContent());
        Assert.assertTrue(target.getCustomFields().stream()
            .anyMatch(f -> PartnercoreConstants.SOURCE.equals(f.getName()) &&
                "PARTNER_COMMERCE_DISPLAY_NAME".equals(f.getContent())));
        Assert.assertTrue(target.getCustomFields().stream()
            .anyMatch(f -> PartnercoreConstants.JUSTIFICATIONKEY.equals(f.getName()) &&
                "Justification".equals(f.getContent())));
    }

    @Test
    public void testMapWithNullFields() {
        final PartnerCpqQuoteRequestData target = new PartnerCpqQuoteRequestData();

        quoteModel = IbmPartnerQuoteDataModelGenerator.createOutdatedIbmPartnerQuoteModel(
            new Date(), user, null, null, null);
        final PartnerCpqQuoteRequestData result = service.map(quoteModel, target);

        Assert.assertNotNull(result);
        Assert.assertEquals(PartnercoreConstants.EXPIRATIONDATE, target.getCustomFields().get(0).getName());
        Assert.assertEquals(PartnercoreConstants.OPPORTUNITYID, target.getCustomFields().get(3).getName());
        Assert.assertEquals(DEFAULT_OPPORTUNITY_ID, target.getCustomFields().get(3).getContent());
        Assert.assertEquals(PartnercoreConstants.DISTRIBUTIONCHANNELKEY, target.getCustomFields().get(4).getName());
        Assert.assertEquals(DEFAULT_DESTINATION_CHANNEL, target.getCustomFields().get(4).getContent());
        Assert.assertEquals(PartnercoreConstants.PROGRAMTYPE, target.getCustomFields().get(6).getName());
        Assert.assertEquals(StringUtils.EMPTY, target.getCustomFields().get(6).getContent());
    }

    @Test
    public void testMapWithEmptyFields() {
        final PartnerCpqQuoteRequestData target = new PartnerCpqQuoteRequestData();

        quoteModel = IbmPartnerQuoteDataModelGenerator.createOutdatedIbmPartnerQuoteModel(
            new Date(), user, new IbmPartnerOpportunityModel(), agreementDetail, TEST_TRANSACTION_PRICE_LEVEL);

        final PartnerCpqQuoteRequestData result = service.map(quoteModel, target);

        Assert.assertNotNull(result);
        Assert.assertEquals(PartnercoreConstants.EXPIRATIONDATE, target.getCustomFields().get(0).getName());
        Assert.assertNotNull(target.getCustomFields().get(0).getContent());
        Assert.assertEquals(PartnercoreConstants.OPPORTUNITYID, target.getCustomFields().get(3).getName());
        Assert.assertEquals(DEFAULT_OPPORTUNITY_ID, target.getCustomFields().get(3).getContent());
        Assert.assertEquals(PartnercoreConstants.DISTRIBUTIONCHANNELKEY, target.getCustomFields().get(4).getName());
        Assert.assertEquals(DEFAULT_DESTINATION_CHANNEL, target.getCustomFields().get(4).getContent());
        Assert.assertEquals(PartnercoreConstants.PROGRAMTYPE, target.getCustomFields().get(6).getName());
        Assert.assertEquals(StringUtils.EMPTY, target.getCustomFields().get(6).getContent());
    }

    @Test
    public void testProductService() {
        final IbmProductService prodService = service.getProductService();
        Assert.assertNotNull(prodService);
    }

    @Test
    public void testDefaultDestinationChannel() {
        final String channel = service.getDefaultDestinationChannel();
        Assert.assertEquals(DEFAULT_DESTINATION_CHANNEL, channel);
    }
}
