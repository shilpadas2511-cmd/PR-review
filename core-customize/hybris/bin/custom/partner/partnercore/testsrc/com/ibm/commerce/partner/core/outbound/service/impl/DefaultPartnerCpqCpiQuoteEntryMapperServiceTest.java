package com.ibm.commerce.partner.core.outbound.service.impl;

import com.ibm.commerce.common.core.model.IbmVariantProductModel;
import com.ibm.commerce.common.core.product.service.IbmProductService;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.pricing.services.PartnerPricingOutboundService;
import com.ibm.commerce.partner.core.quote.services.PartnerQuoteService;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteItemModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.sap.productconfig.services.model.CPQOrderEntryProductInfoModel;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;
import de.hybris.platform.variants.model.VariantProductModel;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

/**
 * Test class for {@link DefaultPartnerCpqCpiQuoteEntryMapperService}
 */
public class DefaultPartnerCpqCpiQuoteEntryMapperServiceTest {

    private static final String DEFAULT_TRANSACTION_TYPE = "StandardTransaction";
    private static final String PRODUCT_CODE = "PRODUCT_CODE";
    private static final String CONFIG_ID = "CONFIG_ID";
    private static final String CONFIGURATOR_CODE = "CONFIGURATOR_CODE";
    private static final String START_DATE = "2023-01-01";

    @InjectMocks
    private DefaultPartnerCpqCpiQuoteEntryMapperService mapperService;

    @Mock
    private IbmProductService productService;

    @Mock
    private AbstractOrderEntryModel quoteEntryModel;

    @Mock
    private AbstractOrderEntryModel pidEntry;

    @Mock
    private IbmVariantProductModel pidProduct;

    @Mock
    private VariantProductModel pidVariantProduct;

    @Mock
    private QuoteModel quoteModel;

    @Mock
    private CPQOrderEntryProductInfoModel productInfoModel;

    @Mock
    private ProductConfigurationModel productConfigurationModel;

    @Mock
    private PartnerPricingOutboundService partnerPricingOutboundService;

    @Mock
    private PartnerQuoteService partnerQuoteService;

    @Mock
    private CatalogVersionService catalogVersionService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mapperService = new DefaultPartnerCpqCpiQuoteEntryMapperService(
            DEFAULT_TRANSACTION_TYPE,
            productService,
            partnerPricingOutboundService,
            partnerQuoteService,catalogVersionService
        );
    }

    @Test
    public void testMap() {
        SAPCPQOutboundQuoteItemModel sapcpqOutboundQuoteItemModel = new SAPCPQOutboundQuoteItemModel();
        Mockito.when(quoteEntryModel.getMasterEntry()).thenReturn(pidEntry);
        Mockito.when(quoteEntryModel.getProduct()).thenReturn(pidProduct);
        Mockito.when(pidEntry.getProduct()).thenReturn(pidProduct);
        Mockito.when(productService.getProductCode(pidProduct)).thenReturn(PRODUCT_CODE);
        Mockito.when(pidProduct.getConfiguratorCode()).thenReturn(CONFIGURATOR_CODE);
        Mockito.when(quoteEntryModel.getQuantity()).thenReturn(1L);
        Mockito.when(quoteEntryModel.getProductInfos()).thenReturn(Collections.singletonList(productInfoModel));

        mapperService.map(quoteEntryModel, sapcpqOutboundQuoteItemModel, quoteModel);

        Assert.assertEquals(PRODUCT_CODE, sapcpqOutboundQuoteItemModel.getPartNumber());
        Assert.assertEquals(PRODUCT_CODE, sapcpqOutboundQuoteItemModel.getProductCode());
        Assert.assertEquals(CONFIGURATOR_CODE, sapcpqOutboundQuoteItemModel.getProductSystemId());
        Assert.assertEquals("1", sapcpqOutboundQuoteItemModel.getExternalItemID());
        Assert.assertEquals("1", sapcpqOutboundQuoteItemModel.getQuantity());
        Assert.assertEquals(StringUtils.EMPTY, sapcpqOutboundQuoteItemModel.getConfigurationId());
        Assert.assertEquals(DEFAULT_TRANSACTION_TYPE, sapcpqOutboundQuoteItemModel.getTransactionType());
    }

    @Test
    public void testMap_ProductNull() {
        SAPCPQOutboundQuoteItemModel sapcpqOutboundQuoteItemModel = new SAPCPQOutboundQuoteItemModel();
        Mockito.when(quoteEntryModel.getMasterEntry()).thenReturn(pidEntry);
        Mockito.when(quoteEntryModel.getProduct()).thenReturn(null);
        Mockito.when(pidEntry.getProduct()).thenReturn(pidProduct);
        Mockito.when(productService.getProductCode(pidProduct)).thenReturn(PRODUCT_CODE);
        Mockito.when(pidProduct.getConfiguratorCode()).thenReturn(CONFIGURATOR_CODE);
        Mockito.when(quoteEntryModel.getQuantity()).thenReturn(1L);
        Mockito.when(quoteEntryModel.getProductInfos()).thenReturn(Collections.singletonList(productInfoModel));
        Mockito.when(productInfoModel.getCpqCharacteristicAssignedValues()).thenReturn(START_DATE);

        mapperService.map(quoteEntryModel, sapcpqOutboundQuoteItemModel, quoteModel);

        Assert.assertNull(sapcpqOutboundQuoteItemModel.getPartNumber());
        Assert.assertNull(sapcpqOutboundQuoteItemModel.getProductCode());
        Assert.assertNull(sapcpqOutboundQuoteItemModel.getProductSystemId());
        Assert.assertEquals("1", sapcpqOutboundQuoteItemModel.getExternalItemID());
        Assert.assertEquals("1", sapcpqOutboundQuoteItemModel.getQuantity());
        Assert.assertEquals(StringUtils.EMPTY, sapcpqOutboundQuoteItemModel.getConfigurationId());
        Assert.assertEquals(DEFAULT_TRANSACTION_TYPE, sapcpqOutboundQuoteItemModel.getTransactionType());
    }

    @Test
    public void testMap_ProductMisMatch() {
        SAPCPQOutboundQuoteItemModel sapcpqOutboundQuoteItemModel = new SAPCPQOutboundQuoteItemModel();
        Mockito.when(quoteEntryModel.getMasterEntry()).thenReturn(pidEntry);
        Mockito.when(quoteEntryModel.getProduct()).thenReturn(pidProduct);
        Mockito.when(pidEntry.getProduct()).thenReturn(pidVariantProduct);
        Mockito.when(productService.getProductCode(pidProduct)).thenReturn(PRODUCT_CODE);
        Mockito.when(pidProduct.getConfiguratorCode()).thenReturn(CONFIGURATOR_CODE);
        Mockito.when(quoteEntryModel.getQuantity()).thenReturn(1L);
        Mockito.when(quoteEntryModel.getProductInfos()).thenReturn(Collections.singletonList(productInfoModel));
        Mockito.when(productInfoModel.getCpqCharacteristicName()).thenReturn(PartnercoreConstants.STARTDATE);
        Mockito.when(productInfoModel.getCpqCharacteristicAssignedValues()).thenReturn(START_DATE);

        mapperService.map(quoteEntryModel, sapcpqOutboundQuoteItemModel, quoteModel);

        Assert.assertEquals(PRODUCT_CODE, sapcpqOutboundQuoteItemModel.getPartNumber());
        Assert.assertEquals(PRODUCT_CODE, sapcpqOutboundQuoteItemModel.getProductCode());
        Assert.assertNull(sapcpqOutboundQuoteItemModel.getProductSystemId());
    }

    @Test
    public void testGetConfigId() {
        Mockito.when(quoteEntryModel.getProductConfiguration()).thenReturn(null);
        Assert.assertEquals(StringUtils.EMPTY, mapperService.getConfigId(quoteEntryModel));
    }

    @Test
    public void testGetConfigId_EntryNull() {
        Assert.assertEquals(StringUtils.EMPTY, mapperService.getConfigId(null));
    }

    @Test
    public void testGetConfigId_null() {
        Mockito.when(quoteEntryModel.getProductConfiguration()).thenReturn(productConfigurationModel);
        Mockito.when(productConfigurationModel.getConfigurationId()).thenReturn(CONFIG_ID);

        Assert.assertEquals(CONFIG_ID, mapperService.getConfigId(quoteEntryModel));
    }

    @Test
    public void testGetDefaultTransactionType() {
        Assert.assertEquals(DEFAULT_TRANSACTION_TYPE, mapperService.getDefaultTransactionType());
    }

    @Test
    public void testGetProductService() {
        Assert.assertEquals(productService, mapperService.getProductService());
    }
}
