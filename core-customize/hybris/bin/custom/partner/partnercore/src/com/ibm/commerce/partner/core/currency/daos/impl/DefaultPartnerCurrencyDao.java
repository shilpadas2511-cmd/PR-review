/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.core.currency.daos.impl;

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.i18n.daos.impl.DefaultCurrencyDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.List;

import com.ibm.commerce.partner.core.currency.daos.PartnerCurrencyDao;


/*
 * Implementation Dao class for {@link com.ibm.commerce.partner.core.currency.daos.PartnerCurrencyDao}
 */
public class DefaultPartnerCurrencyDao extends DefaultCurrencyDao implements PartnerCurrencyDao {

    private static final String ACTIVE_CURRENCY_BY_CODE =
        "SELECT DISTINCT{" + CurrencyModel.PK + "} FROM {"
            + CurrencyModel._TYPECODE + "} WHERE " + "{" + CurrencyModel.SAPCODE
            + "} IN (?currencyCodes) OR {"
            + CurrencyModel.ISOCODE + "} IN (?currencyCodes) AND {" + CurrencyModel.ACTIVE
            + "}=?active";


    /*
     * fetch Active currency for given SAP Code and ISO Code
     * @param currencyCodes
     */
    @Override
    public List<CurrencyModel> findActiveCurrency(List<String> currencyCodes) {
        FlexibleSearchQuery query = new FlexibleSearchQuery(ACTIVE_CURRENCY_BY_CODE);
        query.addQueryParameter("currencyCodes", currencyCodes);
        query.addQueryParameter("active", Boolean.TRUE);
        SearchResult<CurrencyModel> currency = getFlexibleSearchService().search(query);
        return currency.getResult();

    }

}
