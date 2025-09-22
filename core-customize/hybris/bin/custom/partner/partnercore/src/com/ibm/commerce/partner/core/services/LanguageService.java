package com.ibm.commerce.partner.core.services;

import de.hybris.platform.core.model.c2l.LanguageModel;

/**
 * This interface is to declare language implementation related methods.
 */
public interface LanguageService {

    /**
     * Declare getOrDefaultLanguage method to implement and get the LanguageModel based on the isoCode.
     * @param isoCode
     * @return
     */
    LanguageModel getOrDefaultLanguage(String isoCode);
}
