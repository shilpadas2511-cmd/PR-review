/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.core.currency.daos;

import de.hybris.platform.core.model.c2l.CurrencyModel;

import java.util.List;


/*
 * DAO class for {@link de.hybris.platform.core.model.c2l.CurrencyModel}
 */
public interface PartnerCurrencyDao {

    /*
     * fetch Active currency for given SAP Code and ISO Code
     * @param currencyCodes
     */
    List<CurrencyModel> findActiveCurrency(List<String> currencyCodes);
}
