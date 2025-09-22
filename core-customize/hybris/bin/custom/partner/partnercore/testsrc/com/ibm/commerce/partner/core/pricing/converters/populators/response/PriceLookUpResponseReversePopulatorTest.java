package com.ibm.commerce.partner.core.pricing.converters.populators.response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;

import com.ibm.commerce.partner.core.enums.CpqPricingTypeEnum;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.PartnerCpqPricingDetailModel;
import com.ibm.commerce.partner.core.model.PartnerCpqHeaderPricingDetailModel;
import com.ibm.commerce.partner.core.order.price.data.response.PriceLookUpConfigurationsResponseData;
import com.ibm.commerce.partner.core.order.price.data.response.PriceLookUpHeaderResponseData;
import com.ibm.commerce.partner.core.order.price.data.response.PriceLookUpItemsResponseData;
import com.ibm.commerce.partner.core.order.price.data.response.PriceLookUpResponseData;
import com.ibm.commerce.partner.core.services.PriceLookUpService;
import com.ibm.commerce.partner.core.services.impl.DefaultPriceLookUpService;
import com.ibm.commerce.partner.core.util.data.PriceLookUpConfigurationsResponseTestDataGenerator;
import com.ibm.commerce.partner.core.util.data.PriceLookUpResponseTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.AbstractOrderEntryModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.CpqPricingDetailModelTestDataGenerator;
import com.sap.hybris.sapcpqquoteintegration.model.CpqPricingDetailModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.opensaml.xmlsec.signature.P;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PriceLookUpResponseReversePopulatorTest {
    private static final double PRICE = 12.0;
    private static final String DISCOUNT_AMT = "10.0";
    private static final String DISCOUNT_PER = "5.0";
    private static final String ROLLED_NETPRICE = "8.0";
    private static final String PRICE_TYPE = "entitled";
    private static final int ENTRY_NUMBER = 123;
    private static final String partNumber = "P123";


    @InjectMocks
    PriceLookUpResponseReversePopulator priceLookUpResponseReversePopulator;

    @Mock
    ModelService modelService;
    @Mock
    PriceLookUpService priceLookUpService;
    @Mock
    PriceLookUpResponseData source;
    @Mock
    IbmPartnerCartModel target;
    @Mock
    PriceLookUpHeaderResponseData priceLookUpHeaderResponseData;
    @Mock
    CpqPricingTypeEnum cpqPricingTypeEnum;
    @Mock
    PartnerCpqPricingDetailModel partnerCpqPricingDetailModel;

    @Mock
    PriceLookUpItemsResponseData priceLookUpItemsResponseData;

    @Mock
    AbstractOrderEntryModel abstractOrderEntryModel;

    @Mock
    AbstractOrderEntryModel abstractOrderEntryModel1;
    @Mock
    ProductModel productModel;

    @Mock
    private List<String> priceNotAvailableErrorCodes;

    @Mock
    PartnerCpqHeaderPricingDetailModel partnerCpqHeaderPricingDetailModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        priceNotAvailableErrorCodes = Arrays.asList("E001", "E002");
        priceLookUpResponseReversePopulator = new PriceLookUpResponseReversePopulator(modelService,
            priceLookUpService, priceNotAvailableErrorCodes);
        when(modelService.create(PartnerCpqPricingDetailModel.class)).thenReturn(partnerCpqPricingDetailModel);
        when(modelService.create(PartnerCpqHeaderPricingDetailModel.class)).thenReturn(partnerCpqHeaderPricingDetailModel);
        when(priceLookUpService.getHeaderCpqPricingDetail(any(IbmPartnerCartModel.class), any(CpqPricingTypeEnum.class)))
            .thenReturn(partnerCpqHeaderPricingDetailModel);
        when(abstractOrderEntryModel.getProduct()).thenReturn(productModel);
        when(productModel.getCode()).thenReturn("P123");
    }
    @Test
    public void populateOrderHeader(){
       when(priceLookUpHeaderResponseData.getTotalExtendedPrice()).thenReturn(PRICE);
        when(cpqPricingTypeEnum.getCode()).thenReturn(CpqPricingTypeEnum.ENTITLED.getCode());
        when(target.getTotalEntitledPrice()).thenReturn(PRICE);
        when(target.getTotalPrice()).thenReturn(PRICE);
        when(target.getFullPriceReceived()).thenReturn(Boolean.TRUE);
        when(target.getTotalDiscounts()).thenReturn(PRICE);
        when(target.getYtyPercentage()).thenReturn(PRICE);
        when(target.getTotalBidExtendedPrice()).thenReturn(PRICE);
        when(target.getTotalMEPPrice()).thenReturn(PRICE);
        when(target.getTotalOptimalPrice()).thenReturn(PRICE);
        when(target.getTotalChannelMargin()).thenReturn(PRICE);
        when(target.getTotalBpExtendedPrice()).thenReturn(PRICE);
        when(target.getTransactionPriceLevel()).thenReturn(DISCOUNT_AMT);
        priceLookUpResponseReversePopulator.populateOrderHeader(priceLookUpHeaderResponseData,target,cpqPricingTypeEnum);
        Mockito.verify(target).setYtyPercentage(9.0);
        Mockito.verify(target).setTotalFullPrice(100.0);
        Mockito.verify(target).setTotalDiscounts(10.0);
        Mockito.verify(target).setYtyPercentage(20.0);
        Mockito.verify(target).setTotalBidExtendedPrice(30.0);
        Mockito.verify(target).setTotalMEPPrice(40.0);
        Mockito.verify(target).setTotalOptimalPrice(50.0);
        Mockito.verify(target).setTotalChannelMargin(60.0);
        Mockito.verify(target).setTotalBpExtendedPrice(70.0);
        Mockito.verify(target).setTransactionPriceLevel("level1");
    }

    @Test
    public void TestPopulateItems(){
        Mockito.when(partnerCpqPricingDetailModel.getNetPrice()).thenReturn(ROLLED_NETPRICE);
        Mockito.when(partnerCpqPricingDetailModel.getExtendedListPrice()).thenReturn(ROLLED_NETPRICE);
        Mockito.when(partnerCpqPricingDetailModel.getListPrice()).thenReturn(ROLLED_NETPRICE);
        Mockito.when(partnerCpqPricingDetailModel.getDiscountAmount()).thenReturn(ROLLED_NETPRICE);
        Mockito.when(partnerCpqPricingDetailModel.getDiscountPercent()).thenReturn(ROLLED_NETPRICE);
        Mockito.when(partnerCpqPricingDetailModel.getRolledUpListPrice()).thenReturn(ROLLED_NETPRICE);
        Mockito.when(partnerCpqPricingDetailModel.getRolledUpExtendedListPrice()).thenReturn(ROLLED_NETPRICE);
        Mockito.when(partnerCpqPricingDetailModel.getRolledUpBidExtendedPrice()).thenReturn(ROLLED_NETPRICE);
        Mockito.when(partnerCpqPricingDetailModel.getRolledUpNetPrice()).thenReturn(ROLLED_NETPRICE);
        Mockito.when(partnerCpqPricingDetailModel.getPricingStrategy()).thenReturn(ROLLED_NETPRICE);
        Mockito.when(partnerCpqPricingDetailModel.getEccRequest()).thenReturn(DISCOUNT_AMT);
        Mockito.when(partnerCpqPricingDetailModel.getEccOverrideFields()).thenReturn(DISCOUNT_AMT);
        Mockito.when(partnerCpqPricingDetailModel.getExtendedUnitPrice()).thenReturn(PRICE);
        Mockito.when(partnerCpqPricingDetailModel.getChannelMargin()).thenReturn(PRICE);
        Mockito.when(partnerCpqPricingDetailModel.getYtyPercentage()).thenReturn(PRICE);
        priceLookUpResponseReversePopulator.populateItems(priceLookUpItemsResponseData,partnerCpqPricingDetailModel);
        Mockito.verify(partnerCpqPricingDetailModel, atLeastOnce()).setYtyPercentage(anyDouble());
    }
 @Test
    public void testCreateCpqPricingforEntitled(){
        Mockito.when(modelService.create(PartnerCpqPricingDetailModel.class)).thenReturn(partnerCpqPricingDetailModel);
        Mockito.when(partnerCpqPricingDetailModel.getOrderEntry()).thenReturn(abstractOrderEntryModel);
        Mockito.when(partnerCpqPricingDetailModel.getPricingType()).thenReturn(CpqPricingTypeEnum.ENTITLED.getCode());
        priceLookUpResponseReversePopulator.createCpqPricing(abstractOrderEntryModel,cpqPricingTypeEnum);
        PartnerCpqPricingDetailModel createdPricing = priceLookUpResponseReversePopulator.createCpqPricing(abstractOrderEntryModel, cpqPricingTypeEnum);
        assertNotNull(createdPricing);
        Mockito.verify(modelService, atLeastOnce()).create(PartnerCpqPricingDetailModel.class);
    }
    @Test
    public void testCreateCpqPricingforFull(){
        Mockito.when(modelService.create(PartnerCpqPricingDetailModel.class)).thenReturn(partnerCpqPricingDetailModel);
        Mockito.when(partnerCpqPricingDetailModel.getOrderEntry()).thenReturn(abstractOrderEntryModel);
        Mockito.when(partnerCpqPricingDetailModel.getPricingType()).thenReturn(CpqPricingTypeEnum.FULL.getCode());
        priceLookUpResponseReversePopulator.createCpqPricing(abstractOrderEntryModel,cpqPricingTypeEnum);
        PartnerCpqPricingDetailModel createdPricing = priceLookUpResponseReversePopulator.createCpqPricing(abstractOrderEntryModel, cpqPricingTypeEnum);
        assertNotNull(createdPricing);
        Mockito.verify(modelService, atLeastOnce()).create(PartnerCpqPricingDetailModel.class);
    }

    @Test
    public void getCpqPricingDetail(){
        CpqPricingTypeEnum pricingType = CpqPricingTypeEnum.ENTITLED;
        List<CpqPricingDetailModel> pricingDetails = List.of(partnerCpqPricingDetailModel);
        Mockito.when(abstractOrderEntryModel.getCpqPricingDetails()).thenReturn(pricingDetails);
        Mockito.when(partnerCpqPricingDetailModel.getPricingType()).thenReturn(CpqPricingTypeEnum.ENTITLED.getCode());
        priceLookUpResponseReversePopulator.getCpqPricingDetail(abstractOrderEntryModel, pricingType);
        PartnerCpqPricingDetailModel createdPricing = priceLookUpResponseReversePopulator.createCpqPricing(abstractOrderEntryModel, cpqPricingTypeEnum);
        assertNotNull(createdPricing);
        Mockito.verify(modelService, atLeastOnce()).create(PartnerCpqPricingDetailModel.class);
    }
    @Test
    public void testProcessItemsWithChildEntry() {
        CpqPricingTypeEnum pricingType = CpqPricingTypeEnum.ENTITLED;
        abstractOrderEntryModel1 = new AbstractOrderEntryModel();
        when(abstractOrderEntryModel.getProduct()).thenReturn(productModel);
        List<AbstractOrderEntryModel> abstractOrderEntryModelList = List.of(abstractOrderEntryModel1);
        Optional<AbstractOrderEntryModel> optionalAbstractOrderEntryModel = Optional.of(abstractOrderEntryModel1);
        when(abstractOrderEntryModel.getChildEntries()).thenReturn(abstractOrderEntryModelList);
        when(priceLookUpItemsResponseData.getPartNumber()).thenReturn(partNumber);
        when(priceLookUpItemsResponseData.getItemNumber()).thenReturn(ENTRY_NUMBER);
        when(priceLookUpService.getChildEntry(abstractOrderEntryModel, partNumber, ENTRY_NUMBER))
            .thenReturn(optionalAbstractOrderEntryModel);
        when(priceLookUpResponseReversePopulator.getCpqPricingDetail(abstractOrderEntryModel, pricingType))
            .thenReturn(partnerCpqPricingDetailModel);
        doNothing().when(priceLookUpResponseReversePopulator).populateItems(priceLookUpItemsResponseData, partnerCpqPricingDetailModel);
        doNothing().when(modelService).save(partnerCpqPricingDetailModel);
        priceLookUpResponseReversePopulator.processItems(priceLookUpItemsResponseData, abstractOrderEntryModel, pricingType);
        Mockito.verify(partnerCpqPricingDetailModel).setEccPriceAvailable(Boolean.FALSE);
    }

    @Test
    public void testPopulate_withNullSource() {
        priceLookUpResponseReversePopulator.populate(null, target);
        // Should not throw
    }

    @Test
    public void testPopulate_withEmptyConfigurations() {
        PriceLookUpResponseData source = mock(PriceLookUpResponseData.class);
        Mockito.when(source.getHeader()).thenReturn(priceLookUpHeaderResponseData);
        Mockito.when(source.getType()).thenReturn(CpqPricingTypeEnum.ENTITLED);
        Mockito.when(source.getConfigurations()).thenReturn(new ArrayList<>());
        Mockito.when(priceLookUpService.getHeaderCpqPricingDetail(any(), any())).thenReturn(mock(PartnerCpqHeaderPricingDetailModel.class));
        priceLookUpResponseReversePopulator.populate(source, target);
        // Should not throw
    }

    @Test
    public void testProcessConfiguration_mainEntryNotPresent() {
        PriceLookUpConfigurationsResponseData config = mock(PriceLookUpConfigurationsResponseData.class);
        Mockito.when(config.getPid()).thenReturn("pid");
        Mockito.when(config.getConfigurationId()).thenReturn("cid");
        Mockito.when(priceLookUpService.findPidEntryByEntryNumber(target, "pid", "cid")).thenReturn(Optional.empty());
        priceLookUpResponseReversePopulator.processConfiguration(config, target, CpqPricingTypeEnum.ENTITLED);
        // Should not throw
    }

    @Test
    public void testProcessItems_entryNull() {
        priceLookUpResponseReversePopulator.processItems(priceLookUpItemsResponseData, null, CpqPricingTypeEnum.ENTITLED);
        // Should not throw
    }

    @Test
    public void testProcessItems_childEntryNotPresent() {
        Mockito.when(priceLookUpService.getChildEntry(abstractOrderEntryModel, partNumber, ENTRY_NUMBER)).thenReturn(Optional.empty());
        Mockito.when(priceLookUpItemsResponseData.getPartNumber()).thenReturn(partNumber);
        Mockito.when(priceLookUpItemsResponseData.getItemNumber()).thenReturn(ENTRY_NUMBER);
        priceLookUpResponseReversePopulator.processItems(priceLookUpItemsResponseData, abstractOrderEntryModel, CpqPricingTypeEnum.ENTITLED);
        // Should not throw
    }

    @Test
    public void testGetCpqPricingDetail_createsNew() {
        when(abstractOrderEntryModel.getCpqPricingDetails()).thenReturn(new ArrayList<>());
        PartnerCpqPricingDetailModel created = mock(PartnerCpqPricingDetailModel.class);
        when(modelService.create(PartnerCpqPricingDetailModel.class)).thenReturn(created);
        PartnerCpqPricingDetailModel result = priceLookUpResponseReversePopulator.getCpqPricingDetail(abstractOrderEntryModel, CpqPricingTypeEnum.ENTITLED);
        assertNotNull(result);
    }

    @Test
    public void testPopulateOrderHeader_fullType() {
        Mockito.when(priceLookUpHeaderResponseData.getTotalExtendedPrice()).thenReturn(PRICE);
        Mockito.when(cpqPricingTypeEnum.getCode()).thenReturn(CpqPricingTypeEnum.FULL.getCode());
        Mockito.when(priceLookUpService.getHeaderCpqPricingDetail(any(), any())).thenReturn(mock(PartnerCpqHeaderPricingDetailModel.class));
        priceLookUpResponseReversePopulator.populateOrderHeader(priceLookUpHeaderResponseData, target, CpqPricingTypeEnum.FULL);
        // Should not throw
    }

    @Test
    public void testPopulateCpqHeaderPrice() {
        PartnerCpqHeaderPricingDetailModel headerDetail = mock(PartnerCpqHeaderPricingDetailModel.class);
        PriceLookUpHeaderResponseData header = mock(PriceLookUpHeaderResponseData.class);
        priceLookUpResponseReversePopulator.populateCpqHeaderPrice(headerDetail, header);
        // Should not throw
    }

    @Test
    public void testPopulateItems_allNulls() {
        PartnerCpqPricingDetailModel detail = mock(PartnerCpqPricingDetailModel.class);
        PriceLookUpItemsResponseData item = mock(PriceLookUpItemsResponseData.class);
        priceLookUpResponseReversePopulator.populateItems(item, detail);
        // Should not throw
    }

    @Test
    public void testPopulateItems_errorCodeBlank_setsEccPriceAvailableTrue() {
        PartnerCpqPricingDetailModel detail = mock(PartnerCpqPricingDetailModel.class);
        PriceLookUpItemsResponseData item = mock(PriceLookUpItemsResponseData.class);
        when(item.getErrorCode()).thenReturn("");
        priceLookUpResponseReversePopulator.populateItems(item, detail);
        Mockito.verify(detail).setEccPriceAvailable(Boolean.TRUE);
    }

    @Test
    public void testPopulateItems_errorCodeInList_setsEccPriceAvailableFalse() {
        PartnerCpqPricingDetailModel detail = mock(PartnerCpqPricingDetailModel.class);
        PriceLookUpItemsResponseData item = mock(PriceLookUpItemsResponseData.class);
        when(item.getErrorCode()).thenReturn("E001");
        List<String> errorCodes = Arrays.asList("E001", "E002");
        PriceLookUpResponseReversePopulator populator = new PriceLookUpResponseReversePopulator(modelService, priceLookUpService, errorCodes);
        populator.populateItems(item, detail);
        Mockito.verify(detail).setEccPriceAvailable(Boolean.FALSE);
    }

    @Test
    public void testPopulateItems_errorCodeNotInList_logsInfo() {
        PartnerCpqPricingDetailModel detail = mock(PartnerCpqPricingDetailModel.class);
        PriceLookUpItemsResponseData item = mock(PriceLookUpItemsResponseData.class);
        when(item.getErrorCode()).thenReturn("SOME_OTHER_ERROR");
        List<String> errorCodes = Arrays.asList("E001", "E002");
        PriceLookUpResponseReversePopulator populator = new PriceLookUpResponseReversePopulator(modelService, priceLookUpService, errorCodes);
        populator.populateItems(item, detail);
        Mockito.verify(detail, Mockito.never()).setEccPriceAvailable(Mockito.anyBoolean());
    }

    @Test
    public void testProcessConfiguration_mainEntryPresent_executesAllLogic() {
        PriceLookUpConfigurationsResponseData config = mock(PriceLookUpConfigurationsResponseData.class);
        AbstractOrderEntryModel mainEntry = mock(AbstractOrderEntryModel.class);
        PartnerCpqPricingDetailModel pricingDetail = mock(PartnerCpqPricingDetailModel.class);
        List<PriceLookUpItemsResponseData> items = List.of(mock(PriceLookUpItemsResponseData.class));
        when(config.getPid()).thenReturn("pid");
        when(config.getConfigurationId()).thenReturn("cid");
        when(config.getTotalExtendedPrice()).thenReturn(123.45);
        when(config.getTotalBidExtendedPrice()).thenReturn(678.90);
        when(config.getItems()).thenReturn(items);
        when(priceLookUpService.findPidEntryByEntryNumber(target, "pid", "cid")).thenReturn(Optional.of(mainEntry));
        when(mainEntry.getCpqPricingDetails()).thenReturn(new ArrayList<>());
        when(modelService.create(PartnerCpqPricingDetailModel.class)).thenReturn(pricingDetail);
        when(pricingDetail.getPricingType()).thenReturn(CpqPricingTypeEnum.ENTITLED.getCode());
        // Run for ENTITLED
        priceLookUpResponseReversePopulator.processConfiguration(config, target, CpqPricingTypeEnum.ENTITLED);
        // Run for FULL
        priceLookUpResponseReversePopulator.processConfiguration(config, target, CpqPricingTypeEnum.FULL);
        Mockito.verify(mainEntry, Mockito.atLeastOnce()).setTotalPrice(123.45);
        Mockito.verify(pricingDetail, Mockito.atLeastOnce()).setTotalExtendedPrice(123.45);
        Mockito.verify(pricingDetail, Mockito.atLeastOnce()).setRolledUpBidExtendedPrice("678.9");
        Mockito.verify(modelService, Mockito.atLeastOnce()).save(pricingDetail);
        Mockito.verify(modelService, Mockito.atLeastOnce()).save(mainEntry);
    }

    @Test
    public void testProcessItems_childEntryPresent_executesAllLogic() {
        PriceLookUpItemsResponseData item = mock(PriceLookUpItemsResponseData.class);
        AbstractOrderEntryModel entry = mock(AbstractOrderEntryModel.class);
        AbstractOrderEntryModel child = mock(AbstractOrderEntryModel.class);
        PartnerCpqPricingDetailModel pricingDetail = mock(PartnerCpqPricingDetailModel.class);
        when(priceLookUpService.getChildEntry(entry, null, 0)).thenReturn(Optional.of(child));
        when(child.getCpqPricingDetails()).thenReturn(new ArrayList<>());
        when(modelService.create(PartnerCpqPricingDetailModel.class)).thenReturn(pricingDetail);
        when(pricingDetail.getPricingType()).thenReturn(CpqPricingTypeEnum.ENTITLED.getCode());
        priceLookUpResponseReversePopulator.processItems(item, entry, CpqPricingTypeEnum.ENTITLED);
        Mockito.verify(pricingDetail, Mockito.atLeastOnce()).setModifiedtime(Mockito.any());
        Mockito.verify(modelService, Mockito.atLeastOnce()).save(pricingDetail);
    }

    @Test
    public void testPopulateItems_discountPercentageNull_setsDiscountPercentNull() {
        PartnerCpqPricingDetailModel detail = mock(PartnerCpqPricingDetailModel.class);
        PriceLookUpItemsResponseData item = mock(PriceLookUpItemsResponseData.class);
        when(item.getDiscountPercentage()).thenReturn(null);
        priceLookUpResponseReversePopulator.populateItems(item, detail);
        Mockito.verify(detail).setDiscountPercent(null);
    }
}