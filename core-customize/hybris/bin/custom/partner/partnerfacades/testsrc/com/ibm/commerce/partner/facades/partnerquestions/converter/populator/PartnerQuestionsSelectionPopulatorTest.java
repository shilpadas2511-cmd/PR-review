package com.ibm.commerce.partner.facades.partnerquestions.converter.populator;

import com.ibm.commerce.partner.core.model.PartnerQuestionsModel;
import com.ibm.commerce.partner.core.model.PartnerQuestionsSelectionModel;
import com.ibm.commerce.partner.enums.data.DisplayTypeData;
import com.ibm.commerce.partner.partnerquestions.data.PartnerQuestionsData;
import com.ibm.commerce.partner.partnerquestions.data.PartnerQuestionsSelectionData;
import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public class PartnerQuestionsSelectionPopulatorTest {

    @Mock
    private Converter<PartnerQuestionsModel, PartnerQuestionsData> partnerQuestionsConverter;

    @InjectMocks
    private PartnerQuestionsSelectionPopulator populator;

    @Mock
    private PartnerQuestionsSelectionModel source;

    @Mock
    private PartnerQuestionsSelectionData target;

    @Mock
    private PartnerQuestionsModel partnerQuestionsModel;

    @Mock
    private PartnerQuestionsData partnerQuestionsData;

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private Configuration configuration;

    @Mock
    private Converter<HybrisEnumValue, DisplayTypeData> displayTypeDataConverter;

    private static final String CODE = "QCODE";
    private static final String NAME = "QNAME";
    private static final String DESCRIPTION = "QDESC";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(configurationService.getConfiguration()).thenReturn(configuration);
        populator = new PartnerQuestionsSelectionPopulator(displayTypeDataConverter);
    }

    @Test
    public void testPopulate() throws ConversionException {

        when(source.getAnswer()).thenReturn(true);
        when(source.getQuestion()).thenReturn(partnerQuestionsModel);
        when(partnerQuestionsModel.getCode()).thenReturn(CODE);
        when(partnerQuestionsModel.getName()).thenReturn(NAME);
        when(partnerQuestionsModel.getDescription()).thenReturn(DESCRIPTION);
        populator.populate(source, target);
        verify(target).setAnswer(true);
        verify(target).setCode(CODE);
        verify(target).setName(NAME);
        verify(target).setDescription(DESCRIPTION);
    }

    @Test(expected = ConversionException.class)
    public void testPopulateWithException() throws ConversionException {
        doThrow(new ConversionException("Conversion failed")).when(source).getQuestion();
        populator.populate(source, target);

    }

}
