package com.ibm.commerce.partner.core.inboundservices.persistence.hook.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ibm.commerce.partner.core.inboundservices.persistence.hook.service.PartnerQuoteCreationPriceMapperService;
import com.ibm.commerce.partner.core.inboundservices.persistence.hook.service.impl.DefaultQuoteCreationEntryMapperService;
import com.ibm.commerce.partner.core.model.CPQIbmPartnerOrderEntryProductInfoModel;
import com.ibm.commerce.partner.core.model.CpqIbmPartnerEntryPricingDetailsModel;
import com.ibm.commerce.partner.core.model.CpqIbmPartnerQuoteEntryModel;
import com.ibm.commerce.partner.core.model.CpqIbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.CpqIbmPartnerUserModel;
import com.ibm.commerce.partner.core.model.CpqPartnerCommerceRampUpModel;
import com.ibm.commerce.partner.core.model.CpqPartnerRampUpSummaryModel;
import com.ibm.commerce.partner.core.model.IbmPartnerPidQuoteEntryModel;
import com.ibm.commerce.partner.core.model.IbmPartnerPidQuoteModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteEntryModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.PartnerCommerceRampUpModel;
import com.ibm.commerce.partner.core.model.PartnerCpqPricingDetailModel;
import com.ibm.commerce.partner.core.model.PartnerRampUpSummaryModel;
import com.ibm.commerce.partner.core.user.service.PartnerUserService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.sap.productconfig.services.model.CPQOrderEntryProductInfoModel;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Appender;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class DefaultQuoteCreationEntryMapperServiceTest {

    @InjectMocks
     DefaultQuoteCreationEntryMapperService mapperService;
    @Mock
    private ModelService modelService;

    @Mock
    private ProductService productService;

    @Mock
    private PartnerUserService partnerUserService;

    @Mock
    private UnitService unitService;

    @Mock
    private PartnerQuoteCreationPriceMapperService priceMappers;
    @Mock
    KeyGenerator pidQuoteKeyGenerator;
    @Mock
    private CatalogVersionService catalogVersionService;
    @Mock
    IbmPartnerPidQuoteModel ibmPartnerPidQuoteModel;
    @Mock
    private CatalogVersionModel catalogVersionModel;
    @Mock
    private UnitModel unitModel;
    @Mock
    private CpqIbmPartnerQuoteEntryModel childEntry;
    @Mock
    private IbmPartnerQuoteEntryModel quoteEntry;
    @Mock private ProductModel productModel;
    @Mock
    private IbmPartnerQuoteModel quoteModel;
    @Mock
    private CpqIbmPartnerQuoteModel cpqQuoteModel;
    @Mock
    PartnerCommerceRampUpModel partnerCommerceRampUpModel;
    @Mock
    private IbmPartnerPidQuoteEntryModel pidQuoteEntry;
    @Mock
    private CpqIbmPartnerEntryPricingDetailsModel pricingDetails;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mapperService = new DefaultQuoteCreationEntryMapperService(modelService, productService, partnerUserService, unitService,
            pidQuoteKeyGenerator, priceMappers, catalogVersionService);
    }

    @Test
    public void testMap_shouldMapQuoteEntriesSuccessfully() {
        CpqIbmPartnerQuoteModel cpqQuoteModel = mock(CpqIbmPartnerQuoteModel.class);
        IbmPartnerQuoteModel ibmQuoteModel = mock(IbmPartnerQuoteModel.class);

        CpqIbmPartnerQuoteEntryModel entry = mock(CpqIbmPartnerQuoteEntryModel.class);
        when(cpqQuoteModel.getEntries()).thenReturn(List.of(entry));

        CatalogVersionModel catalogVersionModel = mock(CatalogVersionModel.class);
        when(catalogVersionService.getCatalogVersion("partnerProductCatalog", "Online")).thenReturn(catalogVersionModel);

        when(modelService.create(IbmPartnerQuoteEntryModel.class)).thenReturn(mock(IbmPartnerQuoteEntryModel.class));
        mapperService.map(cpqQuoteModel, ibmQuoteModel);
    }


    @Test
    public void testCreateChildEntries_successfulFlowpart1() {
        IbmPartnerPidQuoteModel pidQuoteModel = new IbmPartnerPidQuoteModel();
        IbmPartnerPidQuoteEntryModel pidQuoteEntry = new IbmPartnerPidQuoteEntryModel();

        when(modelService.create(IbmPartnerPidQuoteModel.class)).thenReturn(pidQuoteModel);
        when(modelService.create(IbmPartnerPidQuoteEntryModel.class)).thenReturn(pidQuoteEntry);
        when(unitService.getUnitForCode("pieces")).thenReturn(unitModel);
        when(productService.getProductForCode(catalogVersionModel, "PROD001")).thenReturn(productModel);
        when(pidQuoteKeyGenerator.generate()).thenReturn("KEY123");

        when(childEntry.getEntryNumber()).thenReturn("1");
        when(childEntry.getQuantity()).thenReturn("5");
        when(childEntry.getProduct()).thenReturn("PROD001");
        when(childEntry.getPartnerCpqPricingDetails()).thenReturn(null);
        when(childEntry.getProductInfos()).thenReturn(null);
        when(childEntry.getCommerceRampUp()).thenReturn(null);
        when(quoteEntry.getProduct()).thenReturn(productModel);
        when(productModel.getCode()).thenReturn("PROD001");

        Collection<CpqIbmPartnerQuoteEntryModel> children = Collections.singletonList(childEntry);
        mapperService.createChildEntries(catalogVersionModel, children, quoteEntry, quoteModel, cpqQuoteModel);
        verify(modelService).save(pidQuoteModel);
        verify(modelService).save(quoteEntry);

    }
    @Test
    public void testCreateRampUpSummaryData() {
        CpqPartnerRampUpSummaryModel cpqSummary = new CpqPartnerRampUpSummaryModel();
        cpqSummary.setId("RAMP001");
        cpqSummary.setRampUpQuantity("1");
        cpqSummary.setRampUpPeriodDuration("12");
        PartnerRampUpSummaryModel expectedModel = new PartnerRampUpSummaryModel();
        when(modelService.create(PartnerRampUpSummaryModel.class)).thenReturn(expectedModel);
        PartnerRampUpSummaryModel result = mapperService.createRampUpSummaryData(cpqSummary);
        assertEquals("RAMP001", result.getCode());
        assertEquals("12", result.getRampUpPeriodDuration());

        verify(modelService).create(PartnerRampUpSummaryModel.class);
    }

    @Test
    public void testCreateCommerceRampUp() {
        CpqPartnerCommerceRampUpModel cpqModel = new CpqPartnerCommerceRampUpModel();
        cpqModel.setId("CMP001");
        cpqModel.setRampUpPeriod("Q2");
        CpqPartnerRampUpSummaryModel summaryModel = new CpqPartnerRampUpSummaryModel();
        summaryModel.setId("SUM001");
        summaryModel.setRampUpQuantity("6");
        summaryModel.setRampUpPeriodDuration("12");
        when(modelService.create(CpqPartnerRampUpSummaryModel.class)).thenReturn(summaryModel);
        cpqModel.setCpqPartnerRampUpSummary(Collections.singletonList(summaryModel));
        PartnerRampUpSummaryModel partnerSummary = new PartnerRampUpSummaryModel();
        partnerSummary.setCode("Test");
        when(modelService.create(PartnerCommerceRampUpModel.class)).thenReturn(partnerCommerceRampUpModel);
        when(modelService.create(PartnerRampUpSummaryModel.class)).thenReturn(partnerSummary);
        when(mapperService.createRampUpSummaryData(summaryModel)).thenReturn(partnerSummary);
        mapperService.createCommerceRampUp(cpqModel, pidQuoteEntry);
        verify(modelService).create(PartnerCommerceRampUpModel.class);
        verify(partnerCommerceRampUpModel).setCode("CMP001");
        verify(partnerCommerceRampUpModel).setRampUpPeriod("Q2");
        verify(partnerCommerceRampUpModel).setPartnerRampUpSummary(Collections.singletonList(partnerSummary));
        verify(modelService).save(partnerCommerceRampUpModel);
        verify(pidQuoteEntry).setCommerceRampUp(partnerCommerceRampUpModel);
        verify(modelService).save(pidQuoteEntry);
    }
    @Test
    public void testCreateProductInfo_validDate() {
        CPQIbmPartnerOrderEntryProductInfoModel infoData = new CPQIbmPartnerOrderEntryProductInfoModel();
        infoData.setName("STARTDATE");
        infoData.setValues("2024-04-24"); // must match ORIGINAL_DATE_PATTERN

        CpqIbmPartnerQuoteModel quoteModel = new CpqIbmPartnerQuoteModel();

        List<?> result = mapperService.createProductInfo(infoData, quoteModel);
        assertEquals(1, result.size());
        CPQOrderEntryProductInfoModel model = (CPQOrderEntryProductInfoModel) result.get(0);
        assertEquals("STARTDATE", model.getCpqCharacteristicName());
        assertEquals("2024-04-24", model.getCpqCharacteristicAssignedValues());
    }

    @Test
    public void testCreateProductInfo_nonDateField() {
        CPQIbmPartnerOrderEntryProductInfoModel infoData = new CPQIbmPartnerOrderEntryProductInfoModel();
        infoData.setName("OTHER");
        infoData.setValues("SomeValue");

        CpqIbmPartnerQuoteModel quoteModel = new CpqIbmPartnerQuoteModel();

        List<?> result = mapperService.createProductInfo(infoData, quoteModel);
        assertEquals(1, result.size());
        CPQOrderEntryProductInfoModel model = (CPQOrderEntryProductInfoModel) result.get(0);
        assertEquals("OTHER", model.getCpqCharacteristicName());
        assertEquals("SomeValue", model.getCpqCharacteristicAssignedValues());
    }
    @Test
    public void testCreateProductInfos_shouldAddProductInfosAndSave() {
        CPQIbmPartnerOrderEntryProductInfoModel infoData = new CPQIbmPartnerOrderEntryProductInfoModel();
        infoData.setName("TestName");
        infoData.setValues("TestValue");

        CpqIbmPartnerQuoteModel quoteModel = new CpqIbmPartnerQuoteModel();

        IbmPartnerPidQuoteEntryModel pidQuoteEntry = new IbmPartnerPidQuoteEntryModel();
        pidQuoteEntry.setProductInfos(null);

        AbstractOrderEntryProductInfoModel mockInfoModel = mock(AbstractOrderEntryProductInfoModel.class);
        mapperService.createProductInfos(Collections.singletonList(infoData), pidQuoteEntry, quoteModel);

        assertNotNull(pidQuoteEntry.getProductInfos());
        assertEquals(1, pidQuoteEntry.getProductInfos().size());
    }
    @Test
    public void testCreateProductConfiguration_success() {
        CpqIbmPartnerQuoteEntryModel entry = new CpqIbmPartnerQuoteEntryModel();
        entry.setConfigurationId("config-123");
        entry.setProduct("productCode123");

        CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);
        IbmPartnerQuoteEntryModel quoteEntry = new IbmPartnerQuoteEntryModel();

        ProductModel mockProduct = mock(ProductModel.class);
        when(productService.getProductForCode(catalogVersion, "productCode123"))
            .thenReturn(mockProduct);

        CpqIbmPartnerQuoteModel cpqQuote = new CpqIbmPartnerQuoteModel();
        CpqIbmPartnerUserModel user = new CpqIbmPartnerUserModel();
        user.setEmail("user@example.com");
        cpqQuote.setQuoteCreator(user);

        UserModel userModel = new UserModel();
        userModel.setUid("user@example.com");
        cpqQuote.setQuoteCreator(user);

        when(partnerUserService.getUserForUID("user@example.com")).thenReturn(userModel);

        ProductConfigurationModel mockConfig = new ProductConfigurationModel();
        when(modelService.create(ProductConfigurationModel.class)).thenReturn(mockConfig);
        mapperService.createProductConfiguration(entry, catalogVersion, quoteEntry, cpqQuote);
        assertEquals("config-123", mockConfig.getConfigurationId());
        assertEquals(Collections.singletonList(mockProduct), mockConfig.getProduct());
        assertEquals(userModel, mockConfig.getUser());

        verify(modelService).save(mockConfig);
        assertEquals(mockConfig, quoteEntry.getProductConfiguration());
    }
    @Test
    public void testLogErrorLogsProperMessage() {

        String fieldName = "startDate";
        Exception exception = new IllegalArgumentException("Invalid date format");

        CpqIbmPartnerQuoteModel cpqQuoteModel = new CpqIbmPartnerQuoteModel();
        cpqQuoteModel.setCode("QUOTE123");
        cpqQuoteModel.setCpqQuoteNumber("CPQ456");
        cpqQuoteModel.setCpqQuoteExternalId("EXT789");

        Appender mockAppender = mock(Appender.class);
        org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(DefaultQuoteCreationEntryMapperService.class);
        logger.addAppender(mockAppender);
        mapperService.logError(fieldName, exception, cpqQuoteModel);
        ArgumentCaptor<LoggingEvent> captor = ArgumentCaptor.forClass(LoggingEvent.class);
        verify(mockAppender).doAppend(captor.capture());

        LoggingEvent loggingEvent = captor.getValue();
        String logMessage = String.valueOf(loggingEvent.getMessage());

        assert logMessage.contains("Invalid value encountered for field [startDate]");
        assert logMessage.contains("QUOTE123");
        assert logMessage.contains("CPQ456");
        assert logMessage.contains("EXT789");
        assert logMessage.contains("Invalid date format");
    }
    @Test
    public void testCreateQuoteEntry_Success() {
        CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);
        CpqIbmPartnerQuoteEntryModel entry = new CpqIbmPartnerQuoteEntryModel();
        entry.setEntryNumber("123");
        entry.setQuantity("10");
        entry.setProduct("prod-001");

        IbmPartnerQuoteEntryModel createdEntry = new IbmPartnerQuoteEntryModel();
        ProductModel productModel = new ProductModel();
        UnitModel unitModel = new UnitModel();

        when(modelService.create(IbmPartnerQuoteEntryModel.class)).thenReturn(createdEntry);
        when(productService.getProductForCode(catalogVersion, "prod-001")).thenReturn(productModel);
        when(unitService.getUnitForCode("pieces")).thenReturn(unitModel);

        IbmPartnerQuoteEntryModel result = mapperService.createQuoteEntry(catalogVersion, entry);

        assertEquals("123", result.getCpqExternalQuoteEntryId());
        assertEquals(productModel, result.getProduct());
        assertEquals(unitModel, result.getUnit());
    }

    @Test
    public void testCreateQuoteEntry_InvalidQuantity() {
        CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);
        CpqIbmPartnerQuoteEntryModel entry = new CpqIbmPartnerQuoteEntryModel();
        entry.setEntryNumber("123");
        entry.setQuantity("invalid-qty");

        when(modelService.create(IbmPartnerQuoteEntryModel.class)).thenReturn(new IbmPartnerQuoteEntryModel());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> mapperService.createQuoteEntry(catalogVersion, entry));

        assertTrue(exception.getMessage().contains("Invalid quantity format"));
    }

    @Test
    public void testCreateQuoteEntry_ProductNotFound() {
        CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);
        CpqIbmPartnerQuoteEntryModel entry = new CpqIbmPartnerQuoteEntryModel();
        entry.setQuantity("5");
        entry.setProduct("nonexistent-prod");

        when(modelService.create(IbmPartnerQuoteEntryModel.class)).thenReturn(new IbmPartnerQuoteEntryModel());
        when(productService.getProductForCode(catalogVersion, "nonexistent-prod")).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> mapperService.createQuoteEntry(catalogVersion, entry));

        assertTrue(exception.getMessage().contains("Product with code nonexistent-prod not found"));
    }

    @Test
    public void testCreateQuoteEntry_UnitNotFound() {
        CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);
        CpqIbmPartnerQuoteEntryModel entry = new CpqIbmPartnerQuoteEntryModel();
        entry.setQuantity("5");
        entry.setProduct("product-1");

        when(modelService.create(IbmPartnerQuoteEntryModel.class)).thenReturn(new IbmPartnerQuoteEntryModel());
        when(productService.getProductForCode(any(), any())).thenReturn(new ProductModel());
        when(unitService.getUnitForCode("pieces")).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> mapperService.createQuoteEntry(catalogVersion, entry));

        assertTrue(exception.getMessage().contains("Unit for code 'PIECES' not found"));
    }

}
