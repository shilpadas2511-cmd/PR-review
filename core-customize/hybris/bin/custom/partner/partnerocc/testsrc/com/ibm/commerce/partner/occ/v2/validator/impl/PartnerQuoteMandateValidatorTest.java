package com.ibm.commerce.partner.occ.v2.validator.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ibm.commerce.partner.core.model.IbmPartnerCartEntryModel;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.IbmPartnerOpportunityModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.PartnerCpqHeaderPricingDetailModel;
import com.ibm.commerce.partner.core.model.PartnerCpqPricingDetailModel;
import com.ibm.commerce.partner.core.model.PartnerSpecialBidReasonModel;
import com.ibm.commerce.partner.core.util.model.CartModelTestDataGenerator;
import com.sap.hybris.sapcpqquoteintegration.model.CpqPricingDetailModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commerceservices.enums.SalesApplication;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.services.model.CPQOrderEntryProductInfoModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.time.TimeService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Test class for {@link PartnerQuoteMandateValidator}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PartnerQuoteMandateValidatorTest {

    private static final String CODE = "00000001";
    private static final String END_CUSTOMER_MISSING = "Involved Party information is invalid. To update the information, create a new cart.";
    private static final String RESELLER_MISSING = "Involved Party information is invalid. To update the information, create a new cart.";
    private static final String DISTRIBUTOR_MISSING = "Involved Party information is invalid. To update the information, create a new cart.";
    private static final String CART_NOT_CALCULATED = "Pricing is still being calculated. Please wait 10 minutes and try again.";
    private static final String MISSING_PRODUCT = "You do not have any products configured. Select 'Add more' to configure a product.";
    private static final String CHILD_ENTRY_MISSING = "[PID#]Configuration error. Select 'Edit' on [PID#] to update the configuration.";
    private static final String QUOTE_EXPIRED = "Quote is expired. Please update my quote expiration date. ";
    private static final String FULL_PRICE_MISSING = "Quote is not having the correct prices. Please wait 10 minutes and try again.";
    private static final String OPPORTUNITY_INVALID = "Opportunity id is invalid. ";
    private static final String FULL_PRICE_STALE = "Your pricing is stale and needs to be updated, please click the 'Update Pricing' button to refresh your pricing.";
    private static final String SPECIAL_BID_JUSTIFICATION_MISSING = "You've initiated a special bid, please ensure a special bid exception code and business justification have been entered on the Special bid tab";

    private static final String SPECIAL_BID_JUSTIFICATION = "business justification";


    private IbmPartnerCartModel cartModel;
    @Mock
    private List<AbstractOrderEntryModel> cartEntries;

    private PartnerQuoteMandateValidator partnerQuoteMandateValidator;
    @Mock
    private B2BUnitModel soldThroughUnit;
    @Mock
    private B2BUnitModel billToUnit;
    @Mock
    private B2BUnitModel unit;
    @Mock
    ProductModel productModel;
    @Mock
    List<AbstractOrderEntryModel> childEntries;
    @Mock
    Date quoteExpirationDate;
    @Mock
    TimeService timeService;
    @Mock
    EventService eventService;
    @Mock
    private ConfigurationService configurationService;
    @Mock
    private Configuration configuration;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(configurationService.getConfiguration()).thenReturn(configuration);
        partnerQuoteMandateValidator = new PartnerQuoteMandateValidator(eventService,
            configurationService);
        partnerQuoteMandateValidator.setTimeService(timeService);
    }

    @Test
    public void validateEmptyUnit() {
        cartModel = CartModelTestDataGenerator.quoteValidFields(CODE, null, billToUnit,
            soldThroughUnit, true, cartEntries, quoteExpirationDate);
        assertThrows(END_CUSTOMER_MISSING, CommerceCartModificationException.class,
            () -> partnerQuoteMandateValidator.validateMandatoryField(cartModel));
    }

    @Test
    public void validateEmptyBillToUnit() {
        cartModel = CartModelTestDataGenerator.quoteValidFields(CODE, unit, null, soldThroughUnit,
            true, cartEntries, quoteExpirationDate);
        assertThrows(DISTRIBUTOR_MISSING, CommerceCartModificationException.class,
            () -> partnerQuoteMandateValidator.validateMandatoryField(cartModel));
    }

    @Test
    public void validateEmptySoldToUnit() {
        cartModel = CartModelTestDataGenerator.quoteValidFields(CODE, unit, billToUnit, null, true,
            cartEntries, quoteExpirationDate);
        assertThrows(RESELLER_MISSING, CommerceCartModificationException.class,
            () -> partnerQuoteMandateValidator.validateMandatoryField(cartModel));
    }

    @Test
    public void validateCalculated() {
        cartModel = CartModelTestDataGenerator.quoteValidFields(CODE, unit, billToUnit,
            soldThroughUnit, false, cartEntries, quoteExpirationDate);
        assertThrows(CART_NOT_CALCULATED, CommerceCartModificationException.class,
            () -> partnerQuoteMandateValidator.validateMandatoryField(cartModel));
    }

    @Test
    public void validateEmptyEntries() {
        cartModel = CartModelTestDataGenerator.quoteValidFields(CODE, unit, billToUnit,
            soldThroughUnit, true, null, quoteExpirationDate);
        assertThrows(MISSING_PRODUCT, CommerceCartModificationException.class,
            () -> partnerQuoteMandateValidator.validateMandatoryField(cartModel));
    }

    @Test
    public void validatedEmptyProduct() {
        final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
        final List<AbstractOrderEntryModel> entryModels = new ArrayList<>();
        entry.setProduct(null);
        entryModels.add(entry);
        cartModel = CartModelTestDataGenerator.quoteValidFields(CODE, unit, billToUnit,
            soldThroughUnit, true, entryModels, quoteExpirationDate);
        assertThrows(MISSING_PRODUCT, CommerceCartModificationException.class,
            () -> partnerQuoteMandateValidator.validateMandatoryField(cartModel));
    }

    @Test
    public void validateEmptyChildEntries() {
        final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
        final List<AbstractOrderEntryModel> entryModels = new ArrayList<>();
        entry.setProduct(productModel);
        entry.setChildEntries(Collections.emptyList());
        entryModels.add(entry);
        cartModel = CartModelTestDataGenerator.quoteValidFields(CODE, unit, billToUnit,
            soldThroughUnit, true, entryModels, quoteExpirationDate);
        assertThrows(CHILD_ENTRY_MISSING, CommerceCartModificationException.class,
            () -> partnerQuoteMandateValidator.validateMandatoryField(cartModel));

    }

    @Test
    public void validateProductNotEmptyEntries() throws CommerceCartModificationException {
        final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
        final List<AbstractOrderEntryModel> entryModels = new ArrayList<>();
        entry.setProduct(productModel);
        entry.setChildEntries(childEntries);
        entryModels.add(entry);
        when(timeService.getCurrentTime()).thenReturn(quoteExpirationDate);
        cartModel = CartModelTestDataGenerator.quoteValidFields(CODE, unit, billToUnit,
            soldThroughUnit, true, entryModels, quoteExpirationDate);
        cartModel.setFullPriceReceived(true);
        partnerQuoteMandateValidator.validateMandatoryField(cartModel);
        Assert.assertNotNull(entry.getProduct());
    }

    @Test
    public void validateEmptyQuoteExpirationDate() {
        final Date expirationDate = new Date();
        cartModel = CartModelTestDataGenerator.quoteValidFields(CODE, unit, billToUnit,
            soldThroughUnit, false, cartEntries,
            expirationDate);
        assertThrows(QUOTE_EXPIRED, CommerceCartModificationException.class,
            () -> partnerQuoteMandateValidator.validateMandatoryField(cartModel));
    }

    @Test
    public void validateNotEmptyQuoteExpirationDate() throws CommerceCartModificationException {
        when(timeService.getCurrentTime()).thenReturn(quoteExpirationDate);
        cartModel = CartModelTestDataGenerator.quoteValidFields(CODE, unit, billToUnit,
            soldThroughUnit, true, cartEntries, quoteExpirationDate);
        cartModel.setFullPriceReceived(true);
        partnerQuoteMandateValidator.validateMandatoryField(cartModel);
        Assert.assertNotNull(cartModel.getQuoteExpirationDate());
    }

    @Test
    public void validateQuoteExpirationDateNull() {
        cartModel = CartModelTestDataGenerator.quoteValidFields(CODE, unit, billToUnit,
            soldThroughUnit, true, cartEntries,
            null);
        assertThrows(QUOTE_EXPIRED, CommerceCartModificationException.class,
            () -> partnerQuoteMandateValidator.validateMandatoryField(cartModel));
    }

    @Test
    public void validateQuoteExpirationDate() {
        when(timeService.getCurrentTime()).thenReturn(new Date());
        cartModel = CartModelTestDataGenerator.quoteValidFields(CODE, unit, billToUnit,
            soldThroughUnit, true, cartEntries,
            quoteExpirationDate);
        assertThrows(QUOTE_EXPIRED, CommerceCartModificationException.class,
            () -> partnerQuoteMandateValidator.validateMandatoryField(cartModel));
    }

    @Test
    public void validateFullPriceReceivedFalse() {
        when(timeService.getCurrentTime()).thenReturn(quoteExpirationDate);
        cartModel = CartModelTestDataGenerator.quoteValidFields(CODE, unit, billToUnit,
            soldThroughUnit, true, cartEntries,
            quoteExpirationDate);
        cartModel.setFullPriceReceived(false);
        assertThrows(FULL_PRICE_MISSING, CommerceCartModificationException.class,
            () -> partnerQuoteMandateValidator.validateMandatoryField(cartModel));
    }

    @Test
    public void ValidateValidOpportunityId() throws CommerceCartModificationException {
        when(timeService.getCurrentTime()).thenReturn(quoteExpirationDate);
        String opportunityId = "123";
        IbmPartnerQuoteModel quoteModel = createQuoteModel(opportunityId);
        quoteModel.setSalesApplication(SalesApplication.PRM_COMMERCE_WEB);
        CPQOrderEntryProductInfoModel productInfo = new CPQOrderEntryProductInfoModel();
        productInfo.setCpqCharacteristicName("opportunityId");
        productInfo.setCpqCharacteristicAssignedValues(opportunityId);
        AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
        quoteModel.setEntries(List.of(entry));
        cartModel = CartModelTestDataGenerator.quoteValidFields(CODE, unit, billToUnit,
            soldThroughUnit, true, cartEntries, quoteExpirationDate);
        cartModel.setFullPriceReceived(true);
        partnerQuoteMandateValidator.validateMandatoryField(cartModel);
        Assert.assertNotNull(quoteModel.getSalesApplication());
    }

    private IbmPartnerQuoteModel createQuoteModel(String opportunityCode) {
        IbmPartnerQuoteModel quoteModel = new IbmPartnerQuoteModel();
        IbmPartnerOpportunityModel opportunity = new IbmPartnerOpportunityModel();
        opportunity.setCode(opportunityCode);
        quoteModel.setOpportunity(opportunity);
        return quoteModel;
    }

    @Test
    public void validatePriceStale() {
        when(timeService.getCurrentTime()).thenReturn(quoteExpirationDate);
        cartModel = CartModelTestDataGenerator.quoteValidFields(CODE, unit, billToUnit,
            soldThroughUnit, true, cartEntries,
            quoteExpirationDate);
        cartModel.setPriceStale(true);
        assertThrows(FULL_PRICE_STALE, CommerceCartModificationException.class,
            () -> partnerQuoteMandateValidator.validateMandatoryField(cartModel));
    }

    @Test
    public void validateItemLevelOverridePrice() throws CommerceCartModificationException {
        final AbstractOrderEntryModel partEntry = new AbstractOrderEntryModel();
        final List<AbstractOrderEntryModel> entryModels = new ArrayList<>();
        final AbstractOrderEntryModel childEntry = new AbstractOrderEntryModel();
        final List<AbstractOrderEntryModel> childEntryModels = new ArrayList<>();
        partEntry.setProduct(productModel);
        partEntry.setChildEntries(childEntryModels);
        final PartnerCpqPricingDetailModel pricingDetailModel = new PartnerCpqPricingDetailModel();
        pricingDetailModel.setOverrideBidUnitPrice(897.90);
        List<CpqPricingDetailModel> cpqPricingDetailModels = new ArrayList<>();
        cpqPricingDetailModels.add(pricingDetailModel);
        childEntry.setCpqPricingDetails(cpqPricingDetailModels);
        childEntryModels.add(childEntry);
        entryModels.add(partEntry);
        cartModel = CartModelTestDataGenerator.quoteValidFields(CODE, unit, billToUnit,
            soldThroughUnit, true, entryModels, quoteExpirationDate);
        cartModel.setFullPriceReceived(true);
        assertTrue(partnerQuoteMandateValidator.validateEntryLevelOverridePricing(cartModel));
    }

    @Test
    public void validateItemLevelOverrideDiscount() throws CommerceCartModificationException {
        final AbstractOrderEntryModel partEntry = new AbstractOrderEntryModel();
        final List<AbstractOrderEntryModel> entryModels = new ArrayList<>();
        final AbstractOrderEntryModel childEntry = new AbstractOrderEntryModel();
        final List<AbstractOrderEntryModel> childEntryModels = new ArrayList<>();
        partEntry.setProduct(productModel);
        partEntry.setChildEntries(childEntryModels);
        final PartnerCpqPricingDetailModel pricingDetailModel = new PartnerCpqPricingDetailModel();
        pricingDetailModel.setOverrideDiscount(8.90);
        List<CpqPricingDetailModel> cpqPricingDetailModels = new ArrayList<>();
        cpqPricingDetailModels.add(pricingDetailModel);
        childEntry.setCpqPricingDetails(cpqPricingDetailModels);
        childEntryModels.add(childEntry);
        entryModels.add(partEntry);
        cartModel = CartModelTestDataGenerator.quoteValidFields(CODE, unit, billToUnit,
            soldThroughUnit, true, entryModels, quoteExpirationDate);
        cartModel.setFullPriceReceived(true);
        assertTrue(partnerQuoteMandateValidator.validateEntryLevelOverridePricing(cartModel));
    }

    @Test
    public void validateHeaderLevelOverride() throws CommerceCartModificationException {
        final AbstractOrderEntryModel partEntry = new AbstractOrderEntryModel();
        final List<AbstractOrderEntryModel> entryModels = new ArrayList<>();
        final AbstractOrderEntryModel childEntry = new AbstractOrderEntryModel();
        final List<AbstractOrderEntryModel> childEntryModels = new ArrayList<>();
        partEntry.setChildEntries(childEntryModels);
        List<PartnerCpqHeaderPricingDetailModel> partnerCpqHeaderPricingDetailModelList = new ArrayList<>();
        final PartnerCpqHeaderPricingDetailModel partnerCpqHeaderPricingDetailModel = new PartnerCpqHeaderPricingDetailModel();
        partnerCpqHeaderPricingDetailModelList.add(partnerCpqHeaderPricingDetailModel);
        childEntryModels.add(childEntry);
        final PartnerCpqPricingDetailModel pricingDetailModel = new PartnerCpqPricingDetailModel();
        List<CpqPricingDetailModel> cpqPricingDetailModels = new ArrayList<>();
        cpqPricingDetailModels.add(pricingDetailModel);
        childEntry.setCpqPricingDetails(cpqPricingDetailModels);
        entryModels.add(partEntry);
        cartModel = CartModelTestDataGenerator.quoteValidFields(CODE, unit, billToUnit,
            soldThroughUnit, true, entryModels, quoteExpirationDate);
        cartModel.setFullPriceReceived(true);
        cartModel.setPricingDetails(partnerCpqHeaderPricingDetailModelList);
        assertTrue(partnerQuoteMandateValidator.validateHeaderOverridePricing(cartModel));
    }

    @Test
    public void validateHeaderLevelOverrideNull() throws CommerceCartModificationException {
        when(timeService.getCurrentTime()).thenReturn(quoteExpirationDate);
        final AbstractOrderEntryModel partEntry = new AbstractOrderEntryModel();
        final List<AbstractOrderEntryModel> entryModels = new ArrayList<>();
        final AbstractOrderEntryModel childEntry = new AbstractOrderEntryModel();
        final List<AbstractOrderEntryModel> childEntryModels = new ArrayList<>();
        partEntry.setProduct(productModel);
        partEntry.setChildEntries(childEntryModels);
        final PartnerCpqPricingDetailModel pricingDetailModel = new PartnerCpqPricingDetailModel();
        List<CpqPricingDetailModel> cpqPricingDetailModels = new ArrayList<>();
        cpqPricingDetailModels.add(pricingDetailModel);
        childEntry.setCpqPricingDetails(cpqPricingDetailModels);
        childEntryModels.add(childEntry);
        entryModels.add(partEntry);
        cartModel = CartModelTestDataGenerator.quoteValidFields(CODE, unit, billToUnit,
            soldThroughUnit, true, entryModels, quoteExpirationDate);
        cartModel.setFullPriceReceived(true);
        partnerQuoteMandateValidator.validateMandatoryField(cartModel);
    }

    @Test
    public void validateSpecialBidReason() throws CommerceCartModificationException {
        when(timeService.getCurrentTime()).thenReturn(quoteExpirationDate);
        final AbstractOrderEntryModel partEntry = new AbstractOrderEntryModel();
        final List<AbstractOrderEntryModel> entryModels = new ArrayList<>();
        final AbstractOrderEntryModel childEntry = new AbstractOrderEntryModel();
        final List<AbstractOrderEntryModel> childEntryModels = new ArrayList<>();
        partEntry.setProduct(productModel);
        partEntry.setChildEntries(childEntryModels);
        final PartnerCpqPricingDetailModel pricingDetailModel = new PartnerCpqPricingDetailModel();
        pricingDetailModel.setOverrideDiscount(8.90);
        List<CpqPricingDetailModel> cpqPricingDetailModels = new ArrayList<>();
        cpqPricingDetailModels.add(pricingDetailModel);
        childEntry.setCpqPricingDetails(cpqPricingDetailModels);
        childEntryModels.add(childEntry);
        entryModels.add(partEntry);
        cartModel = CartModelTestDataGenerator.quoteValidFields(CODE, unit, billToUnit,
            soldThroughUnit, true, entryModels, quoteExpirationDate);
        PartnerSpecialBidReasonModel partnerSpecialBidReasonModel = new PartnerSpecialBidReasonModel();
        partnerSpecialBidReasonModel.setCode(null);
        cartModel.setSpecialBidReason(partnerSpecialBidReasonModel);
        cartModel.setFullPriceReceived(true);
    }

    @Test
    public void validateSpecialJustificationNull() throws CommerceCartModificationException {
        when(timeService.getCurrentTime()).thenReturn(quoteExpirationDate);
        final AbstractOrderEntryModel partEntry = new AbstractOrderEntryModel();
        final List<AbstractOrderEntryModel> entryModels = new ArrayList<>();
        final AbstractOrderEntryModel childEntry = new AbstractOrderEntryModel();
        final List<AbstractOrderEntryModel> childEntryModels = new ArrayList<>();
        partEntry.setProduct(productModel);
        partEntry.setChildEntries(childEntryModels);
        final PartnerCpqPricingDetailModel pricingDetailModel = new PartnerCpqPricingDetailModel();
        pricingDetailModel.setOverrideDiscount(8.90);
        List<CpqPricingDetailModel> cpqPricingDetailModels = new ArrayList<>();
        cpqPricingDetailModels.add(pricingDetailModel);
        childEntry.setCpqPricingDetails(cpqPricingDetailModels);
        childEntryModels.add(childEntry);
        entryModels.add(partEntry);
        cartModel = CartModelTestDataGenerator.quoteValidFields(CODE, unit, billToUnit,
            soldThroughUnit, true, entryModels, quoteExpirationDate);
        PartnerSpecialBidReasonModel partnerSpecialBidReasonModel = new PartnerSpecialBidReasonModel();
        partnerSpecialBidReasonModel.setCode("test1");
        cartModel.setSpecialBidReason(partnerSpecialBidReasonModel);
        cartModel.setFullPriceReceived(true);
    }

    @Test
    public void validateSpecialBidReasonNull() throws CommerceCartModificationException {
        final AbstractOrderEntryModel partEntry = new AbstractOrderEntryModel();
        final List<AbstractOrderEntryModel> entryModels = new ArrayList<>();
        final AbstractOrderEntryModel childEntry = new AbstractOrderEntryModel();
        final List<AbstractOrderEntryModel> childEntryModels = new ArrayList<>();
        partEntry.setProduct(productModel);
        partEntry.setChildEntries(childEntryModels);
        final PartnerCpqPricingDetailModel pricingDetailModel = new PartnerCpqPricingDetailModel();
        pricingDetailModel.setOverrideDiscount(8.90);
        List<CpqPricingDetailModel> cpqPricingDetailModels = new ArrayList<>();
        cpqPricingDetailModels.add(pricingDetailModel);
        childEntry.setCpqPricingDetails(cpqPricingDetailModels);
        childEntryModels.add(childEntry);
        entryModels.add(partEntry);
        cartModel = CartModelTestDataGenerator.quoteValidFields(CODE, unit, billToUnit,
            soldThroughUnit, true, entryModels, quoteExpirationDate);
        cartModel.setFullPriceReceived(true);
    }


    @Test(expected = CommerceCartModificationException.class)
    public void testValidateSpecialBidReasonDetailsQuoteRefMissing() throws Exception {
        IbmPartnerCartModel cartModel = mock(IbmPartnerCartModel.class);
        when(cartModel.getQuoteReference()).thenReturn(null);
        partnerQuoteMandateValidator.validateSpecialBidReasonDetails(cartModel,
            SPECIAL_BID_JUSTIFICATION);
    }

    @Test(expected = CommerceCartModificationException.class)
    public void testValidateSpecialBidReasonDetailsJustificationTooLong() throws Exception {
        IbmPartnerCartModel cartModel = mock(IbmPartnerCartModel.class);
        QuoteModel quoteModel = mock(QuoteModel.class);
        when(cartModel.getQuoteReference()).thenReturn(quoteModel);
        String businessJustification = StringUtils.repeat("a", 2001);
        partnerQuoteMandateValidator.validateSpecialBidReasonDetails(cartModel,
            businessJustification);
    }

    @Test(expected = CommerceCartModificationException.class)
    public void testValidateSpecialBidReasonDetails_PriceInvalid()
        throws CommerceCartModificationException {
        IbmPartnerCartModel cartModel = mock(IbmPartnerCartModel.class);
        QuoteModel quoteModel = mock(QuoteModel.class);
        when(cartModel.getQuoteReference()).thenReturn(quoteModel);
        partnerQuoteMandateValidator.validateSpecialBidReasonDetails(cartModel,
            SPECIAL_BID_JUSTIFICATION);
        doReturn(false).when(partnerQuoteMandateValidator).validateHeaderOverridePricing(cartModel);
    }

    @Test
    public void testValidateMandatoryField_specialBidAttributesInvalid_shouldThrowException() {
        IbmPartnerCartModel cartModel = mock(IbmPartnerCartModel.class);
        when(cartModel.getSpecialBidReason()).thenReturn(null);
        CommerceCartModificationException exception = assertThrows(
            CommerceCartModificationException.class, () -> {
                partnerQuoteMandateValidator.validateMandatoryField(cartModel);
            });
    }


}

