/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.core.currency.services.impl;

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.ibm.commerce.partner.core.currency.daos.PartnerCurrencyDao;
import com.ibm.commerce.partner.core.currency.services.PartnerCurrencyService;


/**
 * Implementation class for
 * {@link com.ibm.commerce.partner.core.currency.services.PartnerCurrencyService}
 */
public class DefaultPartnerCurrencyService implements PartnerCurrencyService {


    private final PartnerCurrencyDao currencyDao;

    public DefaultPartnerCurrencyService(final PartnerCurrencyDao currencyDao) {
        this.currencyDao = currencyDao;
    }

    /**
     * this method is used to fetch the active currency for ISO or SAP code
     *
     * @param codeOrSapCode isoCode or Sap Code
     * @return activeCurrencies active currencies Currency database
     */
    @Override
    public List<CurrencyModel> getActiveCurrencies(List<String> codeOrSapCode) {
        List<CurrencyModel> activeCurrencies = null;
        try {
            if (CollectionUtils.isNotEmpty(codeOrSapCode)) {
                activeCurrencies = getCurrencyDao().findActiveCurrency(codeOrSapCode);
            }
        } catch (Exception e) {
            throw new UnknownIdentifierException("Exception while fetching active currency", e);
        }
        return activeCurrencies;

    }

    /**
     * @return the currencyDao
     */
    public PartnerCurrencyDao getCurrencyDao() {
        return currencyDao;
    }

}
