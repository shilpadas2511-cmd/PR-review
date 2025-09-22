package com.ibm.commerce.partner.core.productConfig.hook;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.hook.CommerceUpdateCartEntryHook;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.sap.productconfig.services.impl.ProductConfigurationUpdateCartEntryHookImpl;

/**
 * Custom implementation of the {@link CommerceUpdateCartEntryHook} interface, extending
 * {@link ProductConfigurationUpdateCartEntryHookImpl} to provide partner-specific behavior for
 * updating cart entries with product configuration.
 *
 * <p>This class allows for partner-specific extensions of the before and after
 * cart update operations while maintaining product configuration consistency.</p>
 */
public class PartnerProductConfigurationUpdateCartEntryHookImpl extends
    ProductConfigurationUpdateCartEntryHookImpl implements CommerceUpdateCartEntryHook {

    @Override
    public void afterUpdateCartEntry(final CommerceCartParameter parameter,
        final CommerceCartModification result) {
        //
    }

    @Override
    public void beforeUpdateCartEntry(final CommerceCartParameter parameter) {
        //
    }

}
