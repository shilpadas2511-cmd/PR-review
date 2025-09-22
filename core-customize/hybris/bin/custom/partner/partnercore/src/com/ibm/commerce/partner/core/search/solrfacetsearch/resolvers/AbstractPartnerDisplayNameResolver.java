/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.core.search.solrfacetsearch.resolvers;

import de.hybris.platform.solrfacetsearch.provider.impl.AbstractFacetValueDisplayNameProvider;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Class to get Locale
 */
public abstract class AbstractPartnerDisplayNameResolver  extends
    AbstractFacetValueDisplayNameProvider {
    private final Map<String, Locale> localeCache = new HashMap<>();
    public Locale getLocale(final String isoCode)
    {
        Locale result = localeCache.get(isoCode);
        if (result == null)
        {
            final String[] splitted_code = isoCode.split("_");
            if (splitted_code.length == 1)
            {
                result = new Locale(splitted_code[0]);
            }
            else
            {
                result = new Locale(splitted_code[0], splitted_code[1]);
            }

            localeCache.put(isoCode, result);
        }
        return result;
    }
}

