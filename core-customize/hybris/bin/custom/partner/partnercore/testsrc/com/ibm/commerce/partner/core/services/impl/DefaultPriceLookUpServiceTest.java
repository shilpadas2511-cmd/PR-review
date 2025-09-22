package com.ibm.commerce.partner.core.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang.math.NumberUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.ibm.commerce.common.core.model.IbmPartProductModel;
import com.ibm.commerce.common.core.model.IbmVariantProductModel;
import com.ibm.commerce.partner.core.enums.CpqPricingTypeEnum;
import com.ibm.commerce.partner.core.model.IbmPartnerCartEntryModel;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.PartnerCpqHeaderPricingDetailModel;
import com.ibm.commerce.partner.core.model.PartnerCpqPricingDetailModel;
import com.ibm.commerce.partner.core.util.model.AbstractOrderEntryModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.AbstractOrderModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.IbmVariantProductModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.ProductConfigurationModelDataTestGenerator;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPriceLookUpServiceTest {

    private static final String PIDCODE = "567";
    private static final String CONFIGID = "c123";
    private static final String CONFIGID2 = "c567";

    @InjectMocks
    private DefaultPriceLookUpService priceLookUpService;
    private AbstractOrderModel orderMock;
    private AbstractOrderEntryModel entryMock;
    private ProductModel productMock;
    @Mock
    private ModelService modelService;
    @Mock
    PartnerCpqHeaderPricingDetailModel newCpqHeaderPricingDetail;
    @Mock
    QuoteModel quoteModel;
    @Mock
    private IbmPartnerQuoteModel mockOrder;
    @Mock
    private IbmPartnerCartModel mockCart;
    @Mock
    private PartnerCpqHeaderPricingDetailModel mockPricingDetail;

    @Before
    public void setUp() {
        priceLookUpService = new DefaultPriceLookUpService(modelService);
        orderMock = mock(CartModel.class);
        entryMock = mock(AbstractOrderEntryModel.class);
        productMock = mock(IbmVariantProductModel.class);
    }

    @Test
    public void getChildEntriesList_ShouldReturnEmptyList_WhenNoChildEntries() {
        when(orderMock.getEntries()).thenReturn(Collections.singletonList(entryMock));
        when(entryMock.getChildEntries()).thenReturn(Collections.emptyList());
        final List<AbstractOrderEntryModel> result =
                priceLookUpService.getChildEntriesList(orderMock);
        assertEquals(0, result.size());
    }

    @Test
    public void getChildEntry_ShouldReturnEmptyOptional_WhenNoMatchingChildEntry() {
        when(entryMock.getChildEntries()).thenReturn(Collections.emptyList());
        final Optional<AbstractOrderEntryModel> result =
                priceLookUpService.getChildEntry(entryMock, "partNumber", 1);
        assertEquals(Optional.empty(), result);
    }


    @Test
    public void findPidEntryByEntryNumber_WithEmptyCart() {

        final String pidCode = "123";
        final String configCode = "532";
        final Optional<AbstractOrderEntryModel> result =
                priceLookUpService.findPidEntryByEntryNumber(null, pidCode, configCode);
        assertEquals(false, result.isPresent());
    }

    @Test
    public void findPidEntryByEntryNumber_WithEmptyCartEntries() {

        final AbstractOrderModel orderModel = new AbstractOrderModel();
        final Optional<AbstractOrderEntryModel> result =
                priceLookUpService.findPidEntryByEntryNumber(orderModel, PIDCODE, CONFIGID);
        assertEquals(false, result.isPresent());
    }

    @Test
    public void findPidEntryByEntryNumber_WithEmptyPid() {
        final AbstractOrderModel orderModel = new AbstractOrderModel();
        final Optional<AbstractOrderEntryModel> result =
                priceLookUpService.findPidEntryByEntryNumber(orderModel, null, CONFIGID);
        assertEquals(false, result.isPresent());
    }

    @Test
    public void findPidEntryByEntryNumber_WithEmptyConfig() {
        final AbstractOrderModel orderModel = new AbstractOrderModel();
        final Optional<AbstractOrderEntryModel> result =
                priceLookUpService.findPidEntryByEntryNumber(orderModel, PIDCODE, null);
        assertEquals(false, result.isPresent());
    }

    @Test
    public void findPidEntryByEntryNumber_WithVariantProduct() {

        final IbmVariantProductModel ibmVariantProductModel =
                IbmVariantProductModelTestDataGenerator.createIbmVariantProduct(PIDCODE, PIDCODE,
                        CONFIGID);
        final Collection<ProductConfigurationModel> productConfigurationList = new ArrayList<>();

        final ProductConfigurationModel productConfigurationModel =
                ProductConfigurationModelDataTestGenerator
                        .createProductConfigurationModel(CONFIGID);
        productConfigurationList.add(productConfigurationModel);
        ibmVariantProductModel.setProductConfiguration(productConfigurationList);
        final AbstractOrderEntryModel orderEntryModel =
                AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntry(1);
        orderEntryModel.setProduct(ibmVariantProductModel);
        orderEntryModel.setProductConfiguration(productConfigurationModel);
        final List<AbstractOrderEntryModel> entryList = new ArrayList<>();
        entryList.add(orderEntryModel);
        final AbstractOrderModel orderModel =
                AbstractOrderModelTestDataGenerator.createAbstractOrderModel();
        orderModel.setEntries(entryList);
        final Optional<AbstractOrderEntryModel> result =
                priceLookUpService.findPidEntryByEntryNumber(orderModel, PIDCODE, CONFIGID);
        assertEquals(true, result.isPresent());
    }

    @Test
    public void findPidEntryByEntryNumber_WithVariantProduct2() {

        final IbmVariantProductModel ibmVariantProductModel =
                IbmVariantProductModelTestDataGenerator.createIbmVariantProduct(PIDCODE, PIDCODE,
                        CONFIGID);
        final AbstractOrderEntryModel orderEntryModel =
                AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntry(1);
        orderEntryModel.setProduct(ibmVariantProductModel);
        final List<AbstractOrderEntryModel> entryList = new ArrayList<>();
        entryList.add(orderEntryModel);
        final AbstractOrderModel orderModel =
                AbstractOrderModelTestDataGenerator.createAbstractOrderModel();
        orderModel.setEntries(entryList);
        final Optional<AbstractOrderEntryModel> result =
                priceLookUpService.findPidEntryByEntryNumber(orderModel, PIDCODE, CONFIGID);
        assertEquals(false, result.isPresent());
    }

    @Test
    public void findPidEntryByEntryNumber_WithVariantProduct3() {

        final IbmVariantProductModel ibmVariantProductModel =
                IbmVariantProductModelTestDataGenerator.createIbmVariantProduct(PIDCODE, PIDCODE,
                        CONFIGID);
        final Collection<ProductConfigurationModel> productConfigurationList = new ArrayList<>();

        final ProductConfigurationModel productConfigurationModel =
                ProductConfigurationModelDataTestGenerator
                        .createProductConfigurationModel(CONFIGID2);
        productConfigurationList.add(productConfigurationModel);
        ibmVariantProductModel.setProductConfiguration(productConfigurationList);
        final AbstractOrderEntryModel orderEntryModel =
                AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntry(1);
        orderEntryModel.setProduct(ibmVariantProductModel);
        orderEntryModel.setProductConfiguration(productConfigurationModel);
        final List<AbstractOrderEntryModel> entryList = new ArrayList<>();
        entryList.add(orderEntryModel);
        final AbstractOrderModel orderModel =
                AbstractOrderModelTestDataGenerator.createAbstractOrderModel();
        orderModel.setEntries(entryList);
        final Optional<AbstractOrderEntryModel> result =
                priceLookUpService.findPidEntryByEntryNumber(orderModel, PIDCODE, CONFIGID);
        assertEquals(false, result.isPresent());
    }

    @Test
    public void testChildEntries() {
        final Collection<AbstractOrderEntryModel> childEntries = new ArrayList<>();
        final AbstractOrderEntryModel orderEntryModel =
                AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntry(1);
        childEntries.add(orderEntryModel);
        orderEntryModel.setChildEntries(childEntries);
        final List<AbstractOrderEntryModel> entryList = new ArrayList<>();
        entryList.add(orderEntryModel);
        final AbstractOrderModel orderModel =
                AbstractOrderModelTestDataGenerator.createAbstractOrderModel();
        orderModel.setEntries(entryList);
        final List<AbstractOrderEntryModel> result =
                priceLookUpService.getChildEntriesList(orderModel);
        assertNotNull(result);
        assertNotNull(result.iterator().next().getChildEntries());
        assertEquals(childEntries, result.iterator().next().getChildEntries());
    }

    @Test
    public void testGetMainEntry() {
        final IbmPartnerCartModel ibmPartnerCartModel = new IbmPartnerCartModel();
        ibmPartnerCartModel.setCode("67");
        final List<AbstractOrderEntryModel> entries = new ArrayList<>();
        final AbstractOrderEntryModel abstractOrderEntryModel = new AbstractOrderEntryModel();
        final IbmPartProductModel ibmPartProductModel2 = new IbmPartProductModel();
        ibmPartProductModel2.setCode("test1");
        abstractOrderEntryModel.setProduct(ibmPartProductModel2);
        entries.add(0, abstractOrderEntryModel);
        ibmPartnerCartModel.setEntries(entries);
        final IbmPartProductModel ibmPartProductModel1 = new IbmPartProductModel();
        ibmPartProductModel1.setCode("test2");
        final List<AbstractOrderEntryModel> chileEntries = new ArrayList<>();
        final IbmPartnerCartEntryModel ibmPartnerCartEntryModel = new IbmPartnerCartEntryModel();
        ibmPartnerCartEntryModel.setChildEntries(chileEntries);
        final AbstractOrderEntryModel abstractOrderEntryModel1 = new AbstractOrderEntryModel();
        abstractOrderEntryModel1.setProduct(ibmPartProductModel1);
        chileEntries.add(0, abstractOrderEntryModel1);
        abstractOrderEntryModel.setChildEntries(chileEntries);

        final Optional<AbstractOrderEntryModel> result =
                priceLookUpService.getMainEntry(ibmPartnerCartModel, "test1");
        assertTrue(result.isPresent());
        assertEquals(result.get(), abstractOrderEntryModel);
    }

    @Test
    public void testGetChildEntry() {
        final IbmPartnerCartModel ibmPartnerCartModel = new IbmPartnerCartModel();
        ibmPartnerCartModel.setCode("67");
        final List<AbstractOrderEntryModel> entries = new ArrayList<>();
        final AbstractOrderEntryModel abstractOrderEntryModel = new AbstractOrderEntryModel();
        final IbmPartProductModel ibmPartProductModel2 = new IbmPartProductModel();
        ibmPartProductModel2.setCode("test1");
        abstractOrderEntryModel.setProduct(ibmPartProductModel2);
        entries.add(0, abstractOrderEntryModel);
        ibmPartnerCartModel.setEntries(entries);
        final IbmPartProductModel ibmPartProductModel1 = new IbmPartProductModel();
        ibmPartProductModel1.setCode("test2");
        final List<AbstractOrderEntryModel> chileEntries = new ArrayList<>();
        final IbmPartnerCartEntryModel ibmPartnerCartEntryModel = new IbmPartnerCartEntryModel();
        ibmPartnerCartEntryModel.setChildEntries(chileEntries);
        final AbstractOrderEntryModel abstractOrderEntryModel1 = new AbstractOrderEntryModel();
        abstractOrderEntryModel1.setProduct(ibmPartProductModel1);
        chileEntries.add(0, abstractOrderEntryModel1);
        abstractOrderEntryModel.setChildEntries(chileEntries);
        abstractOrderEntryModel.setEntryNumber(12334);

        final Optional<AbstractOrderEntryModel> result =
                priceLookUpService.getChildEntry(ibmPartnerCartEntryModel, "test2",1);
        assertTrue(result.isPresent());
        assertEquals(result.get(), abstractOrderEntryModel1);
    }

    @Test
    public void testGetCpqPricingDetail() {
        final AbstractOrderEntryModel orderEntryModel =
                AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntry(1);
        final PartnerCpqPricingDetailModel cpqPricingDetail = new PartnerCpqPricingDetailModel();
        cpqPricingDetail.setPricingType("FULL");
        orderEntryModel.setCpqPricingDetails(List.of(cpqPricingDetail));
        final Optional<PartnerCpqPricingDetailModel> result =
                priceLookUpService.getCpqPricingDetail(orderEntryModel, CpqPricingTypeEnum.FULL);
        assertTrue(result.isPresent());
        assertEquals(result.get(), cpqPricingDetail);
    }

    @Test
    public void testGetCpqPricingDetailNotFound() {
        final AbstractOrderEntryModel orderEntryModel =
                AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntry(1);
        final PartnerCpqPricingDetailModel cpqPricingDetail = new PartnerCpqPricingDetailModel();
        cpqPricingDetail.setPricingType("FULL");
        orderEntryModel.setCpqPricingDetails(List.of(cpqPricingDetail));
        final Optional<PartnerCpqPricingDetailModel> result = priceLookUpService
                .getCpqPricingDetail(orderEntryModel, CpqPricingTypeEnum.ENTITLED);
        assertFalse(result.isPresent());
    }

    @Test
    public void testGetHeaderCpqPricingDetail() {
        final IbmPartnerCartModel cartModel = new IbmPartnerCartModel();
        final PartnerCpqHeaderPricingDetailModel cpqHeaderPricingDetail =
                new PartnerCpqHeaderPricingDetailModel();
        cpqHeaderPricingDetail.setPricingType("FULL");
        cartModel.setPricingDetails(List.of(cpqHeaderPricingDetail));
        final PartnerCpqHeaderPricingDetailModel result =
                priceLookUpService.getHeaderCpqPricingDetail(cartModel, CpqPricingTypeEnum.FULL);
        assertEquals(result, cpqHeaderPricingDetail);
    }

    @Test
    public void testGetHeaderCpqPricingDetailCreateNewPrice() {
        final IbmPartnerCartModel cartModel = new IbmPartnerCartModel();
        final PartnerCpqHeaderPricingDetailModel cpqHeaderPricingDetail =
                new PartnerCpqHeaderPricingDetailModel();
        cpqHeaderPricingDetail.setPricingType("FULL");
        cartModel.setPricingDetails(List.of(cpqHeaderPricingDetail));
        when(modelService.create(any(Class.class))).thenReturn(newCpqHeaderPricingDetail);
        final PartnerCpqHeaderPricingDetailModel result = priceLookUpService
                .getHeaderCpqPricingDetail(cartModel, CpqPricingTypeEnum.ENTITLED);
        assertNotEquals(result, cpqHeaderPricingDetail);
        assertEquals(result, newCpqHeaderPricingDetail);
    }

    @Test
    public void testCreateHeaderCpqPricing() {
        final IbmPartnerCartModel cartModel = new IbmPartnerCartModel();
        when(modelService.create(any(Class.class))).thenReturn(newCpqHeaderPricingDetail);
        final PartnerCpqHeaderPricingDetailModel result =
                priceLookUpService.createHeaderCpqPricing(cartModel, CpqPricingTypeEnum.ENTITLED);
        assertEquals(result, newCpqHeaderPricingDetail);
    }

    @Test
    public void testRemoveOverridenHeaderPrices() {

        final IbmPartnerCartModel ibmPartnerCartModel = new IbmPartnerCartModel();
        ibmPartnerCartModel.setCode("67");
        ibmPartnerCartModel.setQuoteReference(quoteModel);
        final List<PartnerCpqHeaderPricingDetailModel> partnerCpqHeaderPricingDetailModelList =
                new ArrayList<>();
        final PartnerCpqHeaderPricingDetailModel partnerCpqHeaderPricingDetailModel =
                new PartnerCpqHeaderPricingDetailModel();
        partnerCpqHeaderPricingDetailModel.setPricingType(CpqPricingTypeEnum.FULL.getCode());
        partnerCpqHeaderPricingDetailModel.setOverrideTotalDiscount(10.00);
        partnerCpqHeaderPricingDetailModelList.add(partnerCpqHeaderPricingDetailModel);
        ibmPartnerCartModel.setPricingDetails(partnerCpqHeaderPricingDetailModelList);
        priceLookUpService.removeOverridenHeaderPrices(ibmPartnerCartModel);
    }
    @Test
    public void testGetMaxYtyYear_returnsCorrectMaxSize() {
        IbmPartnerCartModel cart = mock(IbmPartnerCartModel.class);
        AbstractOrderEntryModel mainEntry1 = mock(AbstractOrderEntryModel.class);
        AbstractOrderEntryModel mainEntry2 = mock(AbstractOrderEntryModel.class);
        AbstractOrderEntryModel childEntry1 = mock(AbstractOrderEntryModel.class);
        AbstractOrderEntryModel childEntry2 = mock(AbstractOrderEntryModel.class);

        AbstractOrderModel childOrder1 = mock(AbstractOrderModel.class);
        AbstractOrderModel childOrder2 = mock(AbstractOrderModel.class);
        EntryGroup group1 = mock(EntryGroup.class);
        EntryGroup group2 = mock(EntryGroup.class);
        EntryGroup group3 = mock(EntryGroup.class);
        when(cart.getEntries()).thenReturn(List.of(mainEntry1, mainEntry2));
        when(mainEntry1.getChildEntries()).thenReturn(List.of(childEntry1));
        when(mainEntry2.getChildEntries()).thenReturn(List.of(childEntry2));
        when(childEntry1.getOrder()).thenReturn(childOrder1);
        when(childEntry2.getOrder()).thenReturn(childOrder2);
        when(childOrder1.getEntryGroups()).thenReturn(List.of(group1, group2));
        when(childOrder2.getEntryGroups()).thenReturn(List.of(group1, group2, group3));
        int max = priceLookUpService.getMaxYtyYear(cart);
        assertEquals(3, max);
    }

    @Test
    public void testGetMaxYtyYear_noChildEntries_returnsZero() {
        IbmPartnerCartModel cart = mock(IbmPartnerCartModel.class);
        AbstractOrderEntryModel entry = mock(AbstractOrderEntryModel.class);

        when(cart.getEntries()).thenReturn(List.of(entry));
        when(entry.getChildEntries()).thenReturn(List.of());

        int result = priceLookUpService.getMaxYtyYear(cart);

        assertEquals(0, result);
    }

    @Test
    public void testGetMaxYtyYear_nullEntryGroups_returnsZero() {
        IbmPartnerCartModel cart = mock(IbmPartnerCartModel.class);
        AbstractOrderEntryModel mainEntry = mock(AbstractOrderEntryModel.class);
        AbstractOrderEntryModel childEntry = mock(AbstractOrderEntryModel.class);
        AbstractOrderModel childOrder = mock(AbstractOrderModel.class);
        when(cart.getEntries()).thenReturn(List.of(mainEntry));
        when(mainEntry.getChildEntries()).thenReturn(List.of(childEntry));
        when(childEntry.getOrder()).thenReturn(childOrder);
        when(childOrder.getEntryGroups()).thenReturn(null);
        int result = priceLookUpService.getMaxYtyYear(cart);
        assertEquals(0, result);
    }
    @Test
    public void testRemoveOrderPricingInformation() {
        priceLookUpService.removeOrderPricingInformation(mockOrder);
        verify(mockOrder).setTotalMEPPrice(NumberUtils.DOUBLE_ZERO);
        verify(mockOrder).setTotalPrice(NumberUtils.DOUBLE_ZERO);
        verify(mockOrder).setTotalEntitledPrice(NumberUtils.DOUBLE_ZERO);
        verify(mockOrder).setTotalFullPrice(NumberUtils.DOUBLE_ZERO);
        verify(mockOrder).setTotalOptimalPrice(NumberUtils.DOUBLE_ZERO);
        verify(mockOrder).setTotalBidExtendedPrice(NumberUtils.DOUBLE_ZERO);
        verify(mockOrder).setTotalBpExtendedPrice(NumberUtils.DOUBLE_ZERO);
        verify(mockOrder).setYtyPercentage(NumberUtils.DOUBLE_ZERO);
        verify(mockOrder).setTotalDiscounts(NumberUtils.DOUBLE_ZERO);
        verify(mockOrder).setTotalChannelMargin(NumberUtils.DOUBLE_ZERO);
        verifyNoMoreInteractions(mockOrder);
    }
    @Test
    public void testPopulateYtyDiscount_withMaxEntryGroupSizeGreaterThanZero() {
        when(mockCart.getQuoteReference()).thenReturn(quoteModel);
        when(priceLookUpService.getMaxYtyYear(mockCart)).thenReturn(3);
        when(priceLookUpService.getHeaderCpqPricingDetail(mockCart, CpqPricingTypeEnum.FULL))
            .thenReturn(mockPricingDetail);
        priceLookUpService.populateYtyDiscount(mockCart);
        Map<String, Double> ytyYears = mockPricingDetail.getYtyYears();
        assertNotNull(ytyYears);
        assertEquals(3, ytyYears.size());

        for (int i = 1; i <= 3; i++) {
            String ytyLabel = PartnercoreConstants.YTY_GROUP_LABEL_PREFIX + (i + 1);
            assertTrue(ytyYears.containsKey(ytyLabel));
            assertEquals(NumberUtils.DOUBLE_ZERO, ytyYears.get(ytyLabel));
        }

        verify(modelService).save(mockPricingDetail);
    }

    @Test
    public void testPopulateYtyDiscount_withMaxEntryGroupSizeZero() {
        when(mockCart.getQuoteReference()).thenReturn(quoteModel);
        mockCart = new IbmPartnerCartModel();
        when(priceLookUpService.getMaxYtyYear(mockCart)).thenReturn(0);
        when(priceLookUpService.getHeaderCpqPricingDetail(mockCart, CpqPricingTypeEnum.FULL))
            .thenReturn(mockPricingDetail);
        priceLookUpService.populateYtyDiscount(mockCart);
        verify(mockPricingDetail).setYtyYears(null);
        verify(modelService).save(mockPricingDetail);
    }

    @Test
    public void testPopulateYtyDiscount_withNullQuoteReference() {
        when(mockCart.getQuoteReference()).thenReturn(null);
        priceLookUpService.populateYtyDiscount(mockCart);
        verifyNoInteractions(modelService);
    }
}
