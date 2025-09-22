package com.ibm.commerce.partner.core.order.strategies.impl;

import static de.hybris.platform.testframework.Assert.assertEquals;
import static org.easymock.EasyMock.anyString;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.or;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.ExpectedCount.times;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.order.services.PartnerEntryGroupService;
import com.ibm.commerce.partner.core.utils.PartnerOrderUtils;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class DefaultPartnerPartEntryGroupStrategyTest {
    @InjectMocks
    DefaultPartnerPartEntryGroupStrategy defaultPartnerPartEntryGroupStrategy;
    @Mock
    private PartnerEntryGroupService entryGroupService;

    @Mock
    private AbstractOrderModel orderModel;

    @Mock
    private AbstractOrderEntryModel orderEntryModel;

    @Mock
    private CommerceCartParameter commerceCartParameter;
    @Mock
    private CommerceCartParameter cartParameter;
    @Mock
    private AbstractOrderEntryModel orderEntry;

    @Mock
    private EntryGroup entryGroup;
    @Mock
    PartnerOrderUtils partnerOrderUtils;

    @Mock
    ModelService modelService;
    @Mock private Set<String> ytyEligibleLicenceTypeCodes;
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ytyEligibleLicenceTypeCodes = new HashSet<>(Arrays.asList("LIC1", "LIC2"));
        orderEntryModel = new AbstractOrderEntryModel();
    }

    @Test
    public void testGetEntryGroup_WhenPartProductAndEligibleYTY() {
        when(commerceCartParameter.isPartProduct()).thenReturn(true);
        when(orderEntryModel.getOrder()).thenReturn(orderModel);
        when(orderModel.getEntries()).thenReturn(Arrays.asList(orderEntryModel));
        when(orderEntryModel.getProduct()).thenReturn((mock(ProductModel.class)));
        EntryGroup entryGroup = mock(EntryGroup.class);
        when(entryGroupService.getEntryGroup(any(), anyString())).thenReturn(null);
        when(entryGroupService.createYtyEntryGroup(eq(orderEntryModel.getOrder()), eq(orderEntryModel), anyString()))
            .thenReturn(entryGroup);
        EntryGroup result = defaultPartnerPartEntryGroupStrategy.getEntryGroup(orderEntryModel, commerceCartParameter);
        assertNotNull(result);
        assertEquals(entryGroup, result);
    }

    @Test
    public void testGetEntryGroup_WhenNotPartProduct() {
        when(commerceCartParameter.isPartProduct()).thenReturn(false);
        EntryGroup result = defaultPartnerPartEntryGroupStrategy.getEntryGroup(orderEntryModel, commerceCartParameter);
        assertNull(result);
    }

    @Test
    public void testGetEntryGroup_WhenEligibleYTYIsLessThanTwo() {

        when(commerceCartParameter.isPartProduct()).thenReturn(true);
        when(orderEntryModel.getOrder()).thenReturn(orderModel);
        when(orderModel.getEntries()).thenReturn(Arrays.asList(orderEntryModel));
        when(orderEntryModel.getProduct()).thenReturn(mock(ProductModel.class));
        EntryGroup result = defaultPartnerPartEntryGroupStrategy.getEntryGroup(orderEntryModel, commerceCartParameter);
        assertNull(result);
    }

    @Test
    public void testAddTreeToOrder_WhenEntryGroupIsNotAlreadyPresent() {
        EntryGroup entryGroup = mock(EntryGroup.class);
        when(entryGroup.getLabel()).thenReturn("TestLabel");
        List<EntryGroup> entryGroups = new ArrayList<>();
        when(orderModel.getEntryGroups()).thenReturn(entryGroups);
        defaultPartnerPartEntryGroupStrategy.addTreeToOrder(orderModel, entryGroup);

    }

    @Test
    public void testAddTreeToOrder_WhenEntryGroupAlreadyPresent() {
        EntryGroup entryGroup = mock(EntryGroup.class);
        when(entryGroup.getLabel()).thenReturn("TestLabel");
        List<EntryGroup> entryGroups = new ArrayList<>();
        entryGroups.add(entryGroup);
        when(orderModel.getEntryGroups()).thenReturn(entryGroups);

        defaultPartnerPartEntryGroupStrategy.addTreeToOrder(orderModel, entryGroup);
        assertEquals(1, entryGroups.size());
    }

    @Test
    public void testGetEntryGroupService() {
        assertNotNull(defaultPartnerPartEntryGroupStrategy.getEntryGroupService());
    }

}
