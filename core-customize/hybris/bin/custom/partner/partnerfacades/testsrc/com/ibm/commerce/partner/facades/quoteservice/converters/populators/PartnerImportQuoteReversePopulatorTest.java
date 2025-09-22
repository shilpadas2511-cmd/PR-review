package com.ibm.commerce.partner.facades.quoteservice.converters.populators;

import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partnerwebservicescommons.dto.search.request.PartnerImportQuoteFileRequestData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Currency;
import java.util.Date;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@UnitTest
public class PartnerImportQuoteReversePopulatorTest {

    private ModelService modelService;
    private CommonI18NService commonI18NService;
    private KeyGenerator keyGenerator;
    private UserService userService;
    private Converter<PartnerImportQuoteFileRequestData, MediaModel> mediaConverter;

    private PartnerImportQuoteReversePopulator populator;
    private CurrencyModel currencyModel;

    @BeforeEach
    void setUp() {
        modelService = mock(ModelService.class);
        commonI18NService = mock(CommonI18NService.class);
        keyGenerator = mock(KeyGenerator.class);
        userService = mock(UserService.class);
        mediaConverter = mock(Converter.class);

        populator = new PartnerImportQuoteReversePopulator(modelService, commonI18NService, keyGenerator, userService, mediaConverter);
    }

    @Test
    public void testPopulate() {
        PartnerImportQuoteFileRequestData requestData = new PartnerImportQuoteFileRequestData();
        IbmPartnerQuoteModel quoteModel = new IbmPartnerQuoteModel();
        CustomerModel mockUser = mock(CustomerModel.class);
        MediaModel mockMedia = new MediaModel();
        when(keyGenerator.generate()).thenReturn("QUOTE123");
        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(commonI18NService.getCurrency("USD")).thenReturn(currencyModel);
        when(modelService.create(MediaModel.class)).thenReturn(mockMedia);
        when(mediaConverter.convert(eq(requestData), any(MediaModel.class))).thenReturn(mockMedia);
        populator.populate(requestData, quoteModel);
        assertEquals("QUOTE123", quoteModel.getCode());
        assertEquals(QuoteState.IMPORT_FILE_UPLOAD_INITIATED, quoteModel.getState());
        assertEquals(mockUser, quoteModel.getUser());
        assertNotNull(quoteModel.getDate());
        assertEquals(mockMedia, quoteModel.getProposalDocument());
        verify(modelService).save(mockMedia);
        verify(mediaConverter).convert(eq(requestData), any(MediaModel.class));
    }
    @Test
    public void testcommonI18NService(){
        commonI18NService = populator.getCommonI18NService();
    }
    @Test
    public void testSetters() {
        CommonI18NService mockCommonI18NService = mock(CommonI18NService.class);
        ModelService mockModelService = mock(ModelService.class);
        UserService mockUserService = mock(UserService.class);

        populator.setCommonI18NService(mockCommonI18NService);
        populator.setModelService(mockModelService);
        populator.setUserService(mockUserService);

        assertEquals(mockCommonI18NService, populator.getCommonI18NService());
        assertEquals(mockModelService, populator.getModelService());
        assertEquals(mockUserService, populator.getUserService());
    }
    @Test
    public void testCreateMediaForImportQuote() {
        PartnerImportQuoteFileRequestData requestData = new PartnerImportQuoteFileRequestData();
        MediaModel mockMedia = mock(MediaModel.class);

        when(modelService.create(MediaModel.class)).thenReturn(mockMedia);
        when(mediaConverter.convert(eq(requestData), any(MediaModel.class))).thenReturn(mockMedia);

        MediaModel result = populator.createMediaForImportQuote(requestData);

        assertEquals(mockMedia, result);
        verify(modelService).save(mockMedia);
    }


}