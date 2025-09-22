package com.ibm.commerce.partner.core.order.services.impl;

import static org.junit.Assert.assertNotNull;

import com.ibm.commerce.partner.core.model.IbmPartnerCartEntryModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.TaxValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.ibm.commerce.partner.core.enums.CpqPricingTypeEnum;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.PartnerCpqPricingDetailModel;
import com.ibm.commerce.partner.core.util.model.AbstractOrderEntryModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.AbstractOrderModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.CommerceCheckoutParameterTestDataGenerator;
import com.sap.hybris.sapcpqquoteintegration.model.CpqPricingDetailModel;

/**
 * Test class for {@link DefaultPartnerCalculationService}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPartnerCalculationServiceTest
{

    @InjectMocks
    DefaultPartnerCalculationService defaultPartnerCalculationService;

    @Mock
    Map<TaxValue, Map<Set<TaxValue>, Double>> taxValueMap;
    CommerceCheckoutParameter commerceCheckoutParameter;
    @Mock
    IbmPartnerCartModel ibmCart;
    @Mock
    QuoteModel quoteModel;
    @Mock
    CartModel cart;
    @Mock
    ModelService modelService;

    IbmPartnerCartModel ibmCartModel;
    AbstractOrderModel abstractOrderModel;
    List<AbstractOrderEntryModel> entries;
    AbstractOrderEntryModel entry;
    List<CpqPricingDetailModel> price;
    PartnerCpqPricingDetailModel cpqPricingDetailModel;


    @Before
    public void setUp()
    {

        MockitoAnnotations.initMocks(this);
        defaultPartnerCalculationService = new DefaultPartnerCalculationService();
        defaultPartnerCalculationService. setModelService(modelService);
        entry = new AbstractOrderEntryModel();
        price = new ArrayList<>();
        cpqPricingDetailModel = new PartnerCpqPricingDetailModel();
        ibmCartModel = new IbmPartnerCartModel();
        ibmCartModel.setQuoteReference(quoteModel);
        price.add(cpqPricingDetailModel);
        entry.setCpqPricingDetails(price);
        entry.setOrder(ibmCartModel);
        entry.setEntryNumber(1);
        entries = new ArrayList<>();
        entries.add(entry);
        abstractOrderModel = AbstractOrderModelTestDataGenerator.createAbstractOrderModel(entries);
        commerceCheckoutParameter = CommerceCheckoutParameterTestDataGenerator.preparecheckoutParameter(ibmCart);
        final AbstractOrderEntryModel childOrderEntry1 = AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntry(
                0);
        entry.setChildEntries(Collections.singleton(childOrderEntry1));
        cpqPricingDetailModel.setTotalExtendedPrice(100.00);
        price.add(cpqPricingDetailModel);
        entry.setCpqPricingDetails(price);
        cart.setQuoteReference(quoteModel);
        entry.setOrder(cart);
    }

    @Test
    public void testCalculateEntriesFull() throws CalculationException {
        ibmCartModel.setQuoteReference(quoteModel);
        entry.setOrder(ibmCartModel);
        cpqPricingDetailModel.setPricingType(CpqPricingTypeEnum.FULL.getCode());
        defaultPartnerCalculationService.calculateEntries(abstractOrderModel, true);
        assertNotNull(entry);
    }

    @Test
    public void testCalculateEntriesEntitled() throws CalculationException {
        cpqPricingDetailModel.setPricingType(CpqPricingTypeEnum.ENTITLED.getCode());
        defaultPartnerCalculationService.calculateEntries(abstractOrderModel, true);
        assertNotNull(entry);
    }
    @Test
    public void testCalculateEntriesPriceNot() throws CalculationException {
        cart.setQuoteReference(null);
        entry.setOrder(null);
        defaultPartnerCalculationService.calculateEntries(abstractOrderModel, true);
        assertNotNull(entry);
    }
    @Test
    public void testCalculateEntriesEmpty() throws CalculationException {
        entry.setCpqPricingDetails(null);
        defaultPartnerCalculationService.calculateEntries(abstractOrderModel, true);
        Assert.assertNull(abstractOrderModel.getTotalPrice());
    }
    @Test
    public void testSetCalculatedStatus()  {
        final AbstractOrderEntryModel abstractOrderEntryModel = new IbmPartnerCartEntryModel();
        abstractOrderEntryModel.setCpqPricingDetails(null);
        defaultPartnerCalculationService.setCalculatedStatus(abstractOrderEntryModel);
        Assert.assertNull(abstractOrderModel.getTotalPrice());
    }

	 @Test
    public void testSetCalculatedStatusEntryNull()  {
         defaultPartnerCalculationService.setCalculatedStatus((AbstractOrderEntryModel) null);
			Assert.assertFalse(entry.getCalculated());
    }

    @Test
    public void testSetCalculatedStatusOrderNull()  {
        defaultPartnerCalculationService.setCalculatedStatus((IbmPartnerCartModel) null);
        Assert.assertNotNull(ibmCartModel);
    }
    @Test
    public void testCalculateEntriesOrderNull() throws CalculationException {
        entry.setCpqPricingDetails(null);
        defaultPartnerCalculationService.calculateEntries(abstractOrderModel, true);
        Assert.assertNull(abstractOrderModel.getTotalPrice());
    }

    @Test
    public void testSetCalculateEntries()  {
        IbmPartnerCartModel cartModel = new IbmPartnerCartModel();
        cartModel.setCode("67");
        final List<AbstractOrderEntryModel> entries = new ArrayList<>();
        final AbstractOrderEntryModel abstractOrderEntryModel = new IbmPartnerCartEntryModel();
        final List<AbstractOrderEntryModel> childEntries = new ArrayList<>();
        abstractOrderEntryModel.setChildEntries(childEntries);
        entries.add(0,abstractOrderEntryModel);
        cartModel.setEntries(entries);
        defaultPartnerCalculationService.setCalculatedStatus(cartModel);
        Assert.assertFalse(abstractOrderEntryModel.getCalculated());
    }
    @Test(expected = NullPointerException.class)
    public void testSetCalculateEntriesOrder()  {
        abstractOrderModel.setEntries(null);
        defaultPartnerCalculationService.setCalculatedStatus(abstractOrderModel);

    }

    @Test
    public void testSetCalculateChildEntriesEmpty()  {
        IbmPartnerCartModel cartModel = new IbmPartnerCartModel();
        cartModel.setCode("67");
        final List<AbstractOrderEntryModel> entries = new ArrayList<>();
        final AbstractOrderEntryModel abstractOrderEntryModel = new IbmPartnerCartEntryModel();
        abstractOrderEntryModel.setChildEntries(Collections.EMPTY_LIST);
        entries.add(0,abstractOrderEntryModel);
        cartModel.setEntries(entries);
        defaultPartnerCalculationService.setCalculatedStatus(cartModel);

    }
    @Test
    public void testSetCalculateTotal() throws CalculationException {
        IbmPartnerCartModel cartModel = new IbmPartnerCartModel();
        cartModel.setCode("67");
        final List<AbstractOrderEntryModel> entries = new ArrayList<>();
        final AbstractOrderEntryModel abstractOrderEntryModel = new IbmPartnerCartEntryModel();
        final List<AbstractOrderEntryModel> childEntries = new ArrayList<>();
        abstractOrderEntryModel.setChildEntries(childEntries);
        entries.add(0,abstractOrderEntryModel);
        cartModel.setEntries(entries);
        defaultPartnerCalculationService.calculateTotals(cartModel,true,taxValueMap) ;
        Mockito.verify(modelService).saveAll(ArgumentMatchers.anyCollection());
    }
    @Test
    public void testSetCalculateTotalEntryEmpty() throws CalculationException {
        IbmPartnerCartModel cartModel = new IbmPartnerCartModel();
        cartModel.setCode("67");
        final List<AbstractOrderEntryModel> entries = new ArrayList<>();
        final AbstractOrderEntryModel abstractOrderEntryModel = new IbmPartnerCartEntryModel();
        final List<AbstractOrderEntryModel> childEntries = new ArrayList<>();
        abstractOrderEntryModel.setChildEntries(childEntries);
        entries.add(0,abstractOrderEntryModel);
        cartModel.setEntries(entries);
        defaultPartnerCalculationService.calculateTotals(cartModel,true,taxValueMap) ;
        Mockito.verify(modelService).saveAll(ArgumentMatchers.anyCollection());
    }

    @Test
    public void testSetCalculatedStatusOrderWithQuoteReferenceAndPriceOverridden() {
        IbmPartnerCartModel cartModel = new IbmPartnerCartModel();
        cartModel.setQuoteReference(quoteModel);
        cartModel.setIsPriceOverridden(Boolean.TRUE);
        cartModel.setEntries(new ArrayList<>());
        defaultPartnerCalculationService.setCalculatedStatus(cartModel);
        Assert.assertFalse(cartModel.getCalculated());
        cartModel.setIsPriceOverridden(Boolean.FALSE);
        defaultPartnerCalculationService.setCalculatedStatus(cartModel);
        Assert.assertTrue(cartModel.getCalculated());
    }

    @Test
    public void testSetCalculatedStatusEntryWithChildEntries() {
        IbmPartnerCartEntryModel entry = new IbmPartnerCartEntryModel();
        PartnerCpqPricingDetailModel detail = new PartnerCpqPricingDetailModel();
        List<CpqPricingDetailModel> details = new ArrayList<>();
        details.add((CpqPricingDetailModel) detail);
        entry.setCpqPricingDetails(details);
        IbmPartnerCartEntryModel child1 = new IbmPartnerCartEntryModel();
        child1.setCalculated(Boolean.TRUE);
        IbmPartnerCartEntryModel child2 = new IbmPartnerCartEntryModel();
        child2.setCalculated(Boolean.TRUE);
        List<AbstractOrderEntryModel> children = new ArrayList<>();
        children.add(child1);
        children.add(child2);
        entry.setChildEntries(children);
        defaultPartnerCalculationService.setCalculatedStatus(entry);
        Assert.assertTrue(entry.getCalculated());
    }

    @Test
    public void testSetCalculatedStatusEntryWithNonOverriddenPrice() {
        IbmPartnerCartEntryModel entry = new IbmPartnerCartEntryModel();
        PartnerCpqPricingDetailModel detail = new PartnerCpqPricingDetailModel();
        List<CpqPricingDetailModel> details = new ArrayList<>();
        details.add((CpqPricingDetailModel) detail);
        entry.setCpqPricingDetails(details);
        entry.setIsPriceOverridden(Boolean.FALSE);
        defaultPartnerCalculationService.setCalculatedStatus(entry);
        Assert.assertTrue(entry.getCalculated());
        Mockito.verify(modelService).save(entry);
    }

    @Test
    public void testResetAllValues() throws CalculationException {
        AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
        defaultPartnerCalculationService.resetAllValues(entry);
        // No assertion needed, just ensure no exception is thrown
    }
}
