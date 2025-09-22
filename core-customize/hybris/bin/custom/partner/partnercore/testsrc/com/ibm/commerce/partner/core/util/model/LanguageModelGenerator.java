package com.ibm.commerce.partner.core.util.model;

import com.ibm.commerce.common.core.model.IbmVariantProductModel;
import de.hybris.platform.core.model.c2l.LanguageModel;

public class LanguageModelGenerator {
    public static LanguageModel createLanguageModel(final String isocode ) {
        LanguageModel languageModel = new LanguageModel();
        languageModel.setIsocode(isocode);
        return languageModel;
    }
}
