package com.ibm.commerce.partner.facades.quote.converter.populator;

import com.ibm.commerce.partnerwebservicescommons.dto.search.request.PartnerImportQuoteFileRequestData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static de.hybris.platform.testframework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@UnitTest
public class PartnerImportQuoteMediaReversePopulatorTest {

    @InjectMocks
    private PartnerImportQuoteMediaReversePopulator populator;

    @Mock
    private KeyGenerator mediaCodeKeyGenerator;

    @Mock
    private PartnerImportQuoteFileRequestData partnerImportQuoteFileRequestData;

    @Mock
    private MediaModel mediaModel;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testPopulate_shouldPopulateMediaModelWithCorrectValues() throws ConversionException {
        String fileName = "quote.csv";
        String format = "CSV";
        String fileSize = "123";
        String generatedCode = "generatedCode123";

        when(partnerImportQuoteFileRequestData.getFileName()).thenReturn(fileName);
        when(partnerImportQuoteFileRequestData.getFormat()).thenReturn(format);
        when(partnerImportQuoteFileRequestData.getFileSize()).thenReturn(fileSize);
        when(mediaCodeKeyGenerator.generate()).thenReturn(generatedCode);
        populator.populate(partnerImportQuoteFileRequestData, mediaModel);
        verify(mediaModel, times(1)).setCode(generatedCode);
        verify(mediaModel, times(1)).setDescription(fileName + " | " + format + " | " + fileSize);
    }

    @Test
    public void testPopulate_withNullPartnerImportQuoteFileRequestData_shouldNotPopulate() throws ConversionException {
        PartnerImportQuoteFileRequestData nullRequestData = null;
        populator.populate(nullRequestData, mediaModel);
        verify(mediaModel, times(0)).setCode(anyString());
        verify(mediaModel, times(0)).setDescription(anyString());
    }

    @Test
    public void testMediaDescriptionCreator_shouldReturnCorrectDescription() {
        String fileName = "quote.csv";
        String format = "CSV";
        String fileSize = "123";
        when(partnerImportQuoteFileRequestData.getFileName()).thenReturn(fileName);
        when(partnerImportQuoteFileRequestData.getFormat()).thenReturn(format);
        when(partnerImportQuoteFileRequestData.getFileSize()).thenReturn(fileSize);
        String description = populator.mediaDescriptionCreator(partnerImportQuoteFileRequestData);
        assertEquals("quote.csv | CSV | 123", description);
    }

    @Test
    public void testGetMediaCodeKeyGenerator() {
        KeyGenerator keyGenerator = populator.getMediaCodeKeyGenerator();
        assertNotNull(keyGenerator);
    }
}