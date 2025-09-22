package com.ibm.commerce.partner.core.services.impl;

import static org.mockito.Mockito.when;

import com.ibm.commerce.partner.core.util.model.LanguageModelGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.site.BaseSiteService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@UnitTest
public class DefaultLanguageServiceTest {

    private static final String ISOCODE = "en";
    private static final String DEFAULT_ISOCODE = "default";

    @InjectMocks
    DefaultLanguageService defaultLanguageService;
    @Mock
    CommonI18NService commonI18NService;
    @Mock
    BaseSiteService baseSiteService;
    LanguageModel expectedLanguageModel;
    LanguageModel expectedDefaultLanguageModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        expectedLanguageModel = LanguageModelGenerator.createLanguageModel(ISOCODE);
        expectedDefaultLanguageModel = LanguageModelGenerator.createLanguageModel(DEFAULT_ISOCODE);
    }

    @Test
    public void testGetOrDefaultLanguage() {
        when(commonI18NService.getLanguage(ISOCODE)).thenReturn(expectedLanguageModel);
        LanguageModel actualLanguageModel = defaultLanguageService.getOrDefaultLanguage(ISOCODE);
        Assert.assertEquals(expectedLanguageModel, actualLanguageModel);
    }

    @Test
    public void testGetOrDefaultLanguageWithoutIsoCode() {
        BaseSiteModel baseSiteModel = new BaseSiteModel();
        baseSiteModel.setDefaultLanguage(expectedDefaultLanguageModel);
        when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSiteModel);
        LanguageModel actualLanguageModel = defaultLanguageService.getOrDefaultLanguage(null);
        Assert.assertEquals(expectedDefaultLanguageModel, actualLanguageModel);
    }
}
