package com.ibm.commerce.partner.core.inboundservices.persistence.hook.service.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


import com.ibm.commerce.partner.core.model.CpqIbmPartnerHeaderPricingDetailsModel;
import com.ibm.commerce.partner.core.model.CpqIbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultQuoteCreationHeaderPriceMapperServiceTest {

    @Mock
    private ModelService modelService;
    @InjectMocks
    DefaultQuoteCreationHeaderPriceMapperService defaultQuoteCreationHeaderPriceMapperService;

    private CpqIbmPartnerQuoteModel cpqIbmPartnerQuoteModel;
    private IbmPartnerQuoteModel ibmPartnerQuoteModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        defaultQuoteCreationHeaderPriceMapperService = new DefaultQuoteCreationHeaderPriceMapperService(
            modelService);

        cpqIbmPartnerQuoteModel = new CpqIbmPartnerQuoteModel();
        ibmPartnerQuoteModel = new IbmPartnerQuoteModel();

        CpqIbmPartnerHeaderPricingDetailsModel pricingDetails = getCpqIbmPartnerHeaderPricingDetailsModel();
        cpqIbmPartnerQuoteModel.setCpqPricingDetails(pricingDetails);
    }
    @Test
    public void shouldNotMapWhenPricingDetailsObjectIsEmpty() {
        CpqIbmPartnerHeaderPricingDetailsModel emptyDetails = new CpqIbmPartnerHeaderPricingDetailsModel();
        cpqIbmPartnerQuoteModel.setCpqPricingDetails(emptyDetails);

        defaultQuoteCreationHeaderPriceMapperService.map(cpqIbmPartnerQuoteModel, ibmPartnerQuoteModel);

        assertNull(ibmPartnerQuoteModel.getTotalBidExtendedPrice());
        assertNull(ibmPartnerQuoteModel.getTotalBpExtendedPrice());
        assertNull(ibmPartnerQuoteModel.getTotalChannelMargin());
        assertNull(ibmPartnerQuoteModel.getTotalDiscounts());
        assertNull(ibmPartnerQuoteModel.getTotalOptimalPrice());
        assertNull(ibmPartnerQuoteModel.getTotalPrice());
        assertNull(ibmPartnerQuoteModel.getTotalMEPPrice());
        assertNull(ibmPartnerQuoteModel.getTransactionPriceLevel());
        assertNull(ibmPartnerQuoteModel.getTotalFullPrice());
        assertNull(ibmPartnerQuoteModel.getSubtotal());
    }

    @Test
    public void shouldSkipMappingWhenPricingDetailsIsNotEmptyButStillNullInside() {
        CpqIbmPartnerQuoteModel mockModel = new CpqIbmPartnerQuoteModel() {
            @Override
            public CpqIbmPartnerHeaderPricingDetailsModel getCpqPricingDetails() {
                return null;
            }
        };

        defaultQuoteCreationHeaderPriceMapperService.map(mockModel, ibmPartnerQuoteModel);


        assertNull(ibmPartnerQuoteModel.getTotalBidExtendedPrice());
    }

    @Test
    public void shouldNotMapWhenPricingDetailsObjectIsNonNullButAllFieldsAreNull() {

        CpqIbmPartnerHeaderPricingDetailsModel pricingDetails = new CpqIbmPartnerHeaderPricingDetailsModel();


        cpqIbmPartnerQuoteModel.setCpqPricingDetails(pricingDetails);

        defaultQuoteCreationHeaderPriceMapperService.map(cpqIbmPartnerQuoteModel, ibmPartnerQuoteModel);


        assertNull(ibmPartnerQuoteModel.getTotalBidExtendedPrice());
        assertNull(ibmPartnerQuoteModel.getTotalPrice());
    }
    @Test
    public void shouldHandleNullTotalExtendedPrice() {
        CpqIbmPartnerHeaderPricingDetailsModel pricingDetails = new CpqIbmPartnerHeaderPricingDetailsModel();
        pricingDetails.setTotalExtendedPrice(null);
        cpqIbmPartnerQuoteModel.setCpqPricingDetails(pricingDetails);

        defaultQuoteCreationHeaderPriceMapperService.map(cpqIbmPartnerQuoteModel, ibmPartnerQuoteModel);


        assertNull(ibmPartnerQuoteModel.getTotalPrice());
        assertNull(ibmPartnerQuoteModel.getTotalFullPrice());
        assertNull(ibmPartnerQuoteModel.getSubtotal());
    }


    @Test
    public void shouldNotMapWhenPricingDetailsIsNull() {
        cpqIbmPartnerQuoteModel.setCpqPricingDetails(null);

        defaultQuoteCreationHeaderPriceMapperService.map(cpqIbmPartnerQuoteModel, ibmPartnerQuoteModel);
        assertNull(ibmPartnerQuoteModel.getTotalBidExtendedPrice());

    }

    @Test
    public void shouldReturnModelService() {
        assertNotNull(defaultQuoteCreationHeaderPriceMapperService.getModelService());
    }

    @Test
    public void shouldMapHeaderPricingDetails() {
        defaultQuoteCreationHeaderPriceMapperService.map(cpqIbmPartnerQuoteModel,
            ibmPartnerQuoteModel);

        assertNotNull(ibmPartnerQuoteModel.getTotalBidExtendedPrice());
        assertNotNull(ibmPartnerQuoteModel.getTotalBpExtendedPrice());
        assertNotNull(ibmPartnerQuoteModel.getTotalChannelMargin());
        assertNotNull(ibmPartnerQuoteModel.getTotalDiscounts());
        assertNotNull(ibmPartnerQuoteModel.getTotalOptimalPrice());
        assertNotNull(ibmPartnerQuoteModel.getTotalBidExtendedPrice());
        assertNotNull(ibmPartnerQuoteModel.getTotalPrice());
        assertNotNull(ibmPartnerQuoteModel.getTotalMEPPrice());
        assertNotNull(ibmPartnerQuoteModel.getTransactionPriceLevel());
        assertNotNull(ibmPartnerQuoteModel.getTotalFullPrice());
        assertNotNull(ibmPartnerQuoteModel.getSubtotal());
    }

    public CpqIbmPartnerHeaderPricingDetailsModel getCpqIbmPartnerHeaderPricingDetailsModel() {
        CpqIbmPartnerHeaderPricingDetailsModel headerPricingDetails = new CpqIbmPartnerHeaderPricingDetailsModel();
        headerPricingDetails.setTotalBidExtendedPrice(100.00);
        headerPricingDetails.setTotalBpExtendedPrice(200.00);
        headerPricingDetails.setTotalChannelMargin(10.00);
        headerPricingDetails.setTotalDiscount(15.00);
        headerPricingDetails.setTotalOptimalPrice(250.00);
        headerPricingDetails.setTotalYTY(95.00);
        headerPricingDetails.setTotalExtendedPrice(300.00);
        headerPricingDetails.setTotalMEPPrice(275.00);
        headerPricingDetails.setTransactionPriceLevel("test");
        return headerPricingDetails;
    }

}
