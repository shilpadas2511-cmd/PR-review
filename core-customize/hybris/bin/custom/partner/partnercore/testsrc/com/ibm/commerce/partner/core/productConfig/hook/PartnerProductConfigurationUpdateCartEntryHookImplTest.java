package com.ibm.commerce.partner.core.productConfig.hook;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit test for {@link PartnerProductConfigurationUpdateCartEntryHookImpl}.
 */
public class PartnerProductConfigurationUpdateCartEntryHookImplTest {

    @InjectMocks
    private PartnerProductConfigurationUpdateCartEntryHookImpl hook;

    @Mock
    private CommerceCartParameter parameter;

    @Mock
    private CommerceCartModification result;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testBeforeUpdateCartEntry() {
        hook.beforeUpdateCartEntry(parameter);
    }

    @Test
    public void testAfterUpdateCartEntry() {
        hook.afterUpdateCartEntry(parameter, result);
    }
}
