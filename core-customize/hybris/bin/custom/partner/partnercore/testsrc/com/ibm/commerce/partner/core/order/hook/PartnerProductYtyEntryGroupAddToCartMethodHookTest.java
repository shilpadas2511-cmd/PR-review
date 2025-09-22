package com.ibm.commerce.partner.core.order.hook;

import static org.mockito.Mockito.*;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.order.EntryGroup;
import com.ibm.commerce.partner.core.order.strategies.PartnerEntryGroupStrategy;
import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

@UnitTest
public class PartnerProductYtyEntryGroupAddToCartMethodHookTest {

    @Mock
    private PartnerEntryGroupStrategy entryGroupStrategy;

    @Mock
    private CommerceCartParameter parameter;

    @Mock
    private CommerceCartModification result;

    @Mock
    private AbstractOrderEntryModel orderEntry;

    @Mock
    private EntryGroup entryGroup;

    @InjectMocks
    private PartnerProductYtyEntryGroupAddToCartMethodHook hook;

    @Mock
    Set<Integer> integerSet;
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        hook = new PartnerProductYtyEntryGroupAddToCartMethodHook(entryGroupStrategy);
    }

    @Test
    public void testAfterAddToCart_success() throws CommerceCartModificationException {
        when(result.getQuantityAdded()).thenReturn(1L);
        when(result.getEntry()).thenReturn(orderEntry);
        when(parameter.isPartProduct()).thenReturn(true);
        when(entryGroupStrategy.getEntryGroup(orderEntry, parameter)).thenReturn(entryGroup);
        when(entryGroup.getGroupNumber()).thenReturn(1);

        Set<Integer> expectedGroupNumbers = new HashSet<>();
        expectedGroupNumbers.add(1);

        when(parameter.getEntryGroupNumbers()).thenReturn(expectedGroupNumbers);

        hook.afterAddToCart(parameter, result);

        verify(entryGroupStrategy).getEntryGroup(orderEntry, parameter);
        verify(entryGroup).getGroupNumber();
        verify(result).setEntryGroupNumbers(expectedGroupNumbers);
        verify(orderEntry).setEntryGroupNumbers(expectedGroupNumbers);
    }



    @Test
    public void testAfterAddToCart_noQuantityAdded() throws CommerceCartModificationException {
        when(result.getQuantityAdded()).thenReturn(0L);
        hook.afterAddToCart(parameter, result);
        verify(entryGroupStrategy, never()).getEntryGroup(any(), any());
        verify(result, never()).setEntryGroupNumbers(any());
    }

    @Test
    public void testAfterAddToCart_notPartProduct() throws CommerceCartModificationException {
        when(result.getQuantityAdded()).thenReturn(1L);
        when(parameter.isPartProduct()).thenReturn(false);
        hook.afterAddToCart(parameter, result);
        verify(entryGroupStrategy, never()).getEntryGroup(any(), any());
        verify(result, never()).setEntryGroupNumbers(any());
    }

    @Test
    public void testAfterAddToCart_entryGroupNull() throws CommerceCartModificationException {
        when(result.getQuantityAdded()).thenReturn(1L);
        when(result.getEntry()).thenReturn(orderEntry);
        when(parameter.isPartProduct()).thenReturn(true);
        when(entryGroupStrategy.getEntryGroup(orderEntry, parameter)).thenReturn(null);
        hook.afterAddToCart(parameter, result);
        verify(entryGroupStrategy).getEntryGroup(orderEntry, parameter);
        verify(result, never()).setEntryGroupNumbers(any());
    }
    @Test
    public void testBeforeAddToCart_doesNothing() {
        hook.beforeAddToCart(parameter);

        verifyNoInteractions(entryGroupStrategy, parameter, result, orderEntry, entryGroup);
    }


}