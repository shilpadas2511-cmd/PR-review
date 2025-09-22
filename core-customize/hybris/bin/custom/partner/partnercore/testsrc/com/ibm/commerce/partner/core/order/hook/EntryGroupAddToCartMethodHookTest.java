package com.ibm.commerce.partner.core.order.hook;

import com.ibm.commerce.partner.core.order.services.PartnerEntryGroupService;
import com.ibm.commerce.partner.core.order.strategies.PartnerEntryGroupStrategy;
import com.ibm.commerce.partner.core.util.data.CommerceCartModificationTestDataGenerator;
import com.ibm.commerce.partner.core.util.data.EntryGroupTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.AbstractOrderEntryModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.CommerceCartParameterTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@UnitTest
public class EntryGroupAddToCartMethodHookTest {

    private static final Integer ENTRY_NUMBER1 = 12;
    private static final Integer ENTRY_NUMBER2 = 13;

    @InjectMocks
    EntryGroupAddToCartMethodHook entryGroupAddToCartMethodHook;

    @Mock
    PartnerEntryGroupStrategy entryGroupStrategy;

    AbstractOrderEntryModel orderEntryModel;

    CommerceCartParameter commerceCartParameter;
    CommerceCartModification commerceCartModification;

    @Mock
    ConfigurationService configurationService;

    @Mock
    PartnerEntryGroupService entryGroupService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Set<Integer> entryGroupNumbers = new HashSet<>();
        entryGroupNumbers.add(ENTRY_NUMBER1);
        entryGroupNumbers.add(ENTRY_NUMBER2);
        orderEntryModel = AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntry(entryGroupNumbers);
        entryGroupAddToCartMethodHook = new EntryGroupAddToCartMethodHook(entryGroupStrategy,configurationService,entryGroupService);
        commerceCartModification = CommerceCartModificationTestDataGenerator.createCartModificationData();
        commerceCartModification.setEntry(orderEntryModel);
        commerceCartModification.setQuantityAdded(4);
        commerceCartParameter = CommerceCartParameterTestDataGenerator.prepareCommerceCartParameter(false, entryGroupNumbers);
    }

    @Test
    public void testAfterAddToCart() throws CommerceCartModificationException {
        EntryGroup entryGroup = EntryGroupTestDataGenerator.createEntryGroup(null);
        Mockito.when(entryGroupStrategy.getEntryGroup(Mockito.any(), Mockito.any())).thenReturn(entryGroup);
        entryGroupAddToCartMethodHook.afterAddToCart(commerceCartParameter, commerceCartModification);
        Assert.assertEquals(3, commerceCartModification.getEntry().getEntryGroupNumbers().size());
    }

    @Test
    public void testBeforeAddToCart() {
        entryGroupAddToCartMethodHook.beforeAddToCart(commerceCartParameter);
    }
}
