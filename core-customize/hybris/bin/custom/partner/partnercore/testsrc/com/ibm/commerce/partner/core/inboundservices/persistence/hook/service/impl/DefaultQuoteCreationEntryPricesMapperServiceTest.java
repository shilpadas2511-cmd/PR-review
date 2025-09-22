package com.ibm.commerce.partner.core.inboundservices.persistence.hook.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ibm.commerce.partner.core.inboundservices.persistence.hook.service.PartnerQuoteCreationPriceMapperService;
import com.ibm.commerce.partner.core.inboundservices.persistence.hook.service.impl.DefaultQuoteCreationEntryMapperService;
import com.ibm.commerce.partner.core.inboundservices.persistence.hook.service.impl.DefaultQuoteCreationEntryPricesMapperService;
import com.ibm.commerce.partner.core.model.CpqIbmPartnerEntryPricingDetailsModel;
import com.ibm.commerce.partner.core.model.CpqIbmPartnerQuoteEntryModel;
import com.ibm.commerce.partner.core.model.CpqIbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteEntryModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.PartnerCpqPricingDetailModel;
import com.ibm.commerce.partner.core.user.service.PartnerUserService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultQuoteCreationEntryPricesMapperServiceTest {

    @Mock
    private ModelService modelService;

    @InjectMocks
    private DefaultQuoteCreationEntryPricesMapperService priceMappers;

    private CpqIbmPartnerEntryPricingDetailsModel partnerCpqPricingDetails;
    private IbmPartnerQuoteEntryModel quoteEntry;
    private PartnerCpqPricingDetailModel cpqPricingDetail;


    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        priceMappers = new DefaultQuoteCreationEntryPricesMapperService(
            modelService);
        partnerCpqPricingDetails = new CpqIbmPartnerEntryPricingDetailsModel();
        quoteEntry = new IbmPartnerQuoteEntryModel();
        cpqPricingDetail = new PartnerCpqPricingDetailModel();

        when(modelService.create(PartnerCpqPricingDetailModel.class)).thenReturn(cpqPricingDetail);
    }

    @Test
    public void testMap() {
        partnerCpqPricingDetails.setTotalExtendedPrice(500.0);
        partnerCpqPricingDetails.setRolledUpBidExtendedPrice(200.0);

        priceMappers.mapPricing(partnerCpqPricingDetails, quoteEntry);

        verify(modelService).create(PartnerCpqPricingDetailModel.class);
        verify(modelService).save(cpqPricingDetail);
        verify(modelService).save(quoteEntry);

        assertEquals(quoteEntry, cpqPricingDetail.getOrderEntry());
        assertEquals("FULL", cpqPricingDetail.getPricingType());
        assertEquals("500.0", cpqPricingDetail.getNetPrice());
        assertEquals("200.0", cpqPricingDetail.getRolledUpBidExtendedPrice());
    }
    @Test
    public void testMapPricing_NullPartnerCpqPricingDetails() {
        assertThrows(NullPointerException.class, () -> {
            priceMappers.mapPricing(null, quoteEntry);
        });
    }

}