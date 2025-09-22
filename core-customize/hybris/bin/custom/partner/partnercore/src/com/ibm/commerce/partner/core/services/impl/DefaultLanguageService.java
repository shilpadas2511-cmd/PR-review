package com.ibm.commerce.partner.core.services.impl;

import com.ibm.commerce.partner.core.services.LanguageService;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.site.BaseSiteService;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

/**
 * This class is to maintain language related code.
 */
public class DefaultLanguageService implements LanguageService {

    private final CommonI18NService commonI18NService;

    private final BaseSiteService baseSiteService;

    public DefaultLanguageService(final CommonI18NService commonI18NService,
        final BaseSiteService baseSiteService) {
        this.commonI18NService = commonI18NService;
        this.baseSiteService = baseSiteService;
    }

    /**
     * get Language Model for the isoCode if present else get default language from the current base
     * site.
     *
     * @param isoCode
     * @return LanguageModel
     */
    @Override
    public LanguageModel getOrDefaultLanguage(String isoCode) {
        LanguageModel languageModel = null;
        if (StringUtils.isNotBlank(isoCode)) {
            languageModel = getCommonI18NService().getLanguage(isoCode);
        }
        if (Objects.isNull(languageModel)) {
            languageModel = getBaseSiteService().getCurrentBaseSite().getDefaultLanguage();
        }
        return languageModel;
    }

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

}
