/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.core.currency.services;

import de.hybris.platform.core.model.c2l.CurrencyModel;

import java.util.List;


/**
 * Service class for {@link de.hybris.platform.core.model.c2l.CurrencyModel}
 */

public interface PartnerCurrencyService {

    /**
     * this method is used to fetch the List of active currency for ISO or SAP code
     *
     * @param codeOrSapCode sap code or iso code
     */
    List<CurrencyModel> getActiveCurrencies(List<String> codeOrSapCode);
}
