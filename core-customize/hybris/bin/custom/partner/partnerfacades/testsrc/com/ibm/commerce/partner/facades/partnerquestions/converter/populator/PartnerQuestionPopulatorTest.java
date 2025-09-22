package com.ibm.commerce.partner.facades.partnerquestions.converter.populator;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ibm.commerce.partner.core.model.PartnerQuestionsModel;
import com.ibm.commerce.partner.partnerquestions.data.PartnerQuestionsData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class PartnerQuestionPopulatorTest {

    @InjectMocks
    private PartnerQuestionPopulator populator;
    @Mock
    private PartnerQuestionsModel source;

    private PartnerQuestionsData target =  new PartnerQuestionsData();

    private static final String CODE = "testcode";
    private static final String NAME = "questionName";
    private static final String DESC = "GOEquestion";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        source = mock(PartnerQuestionsModel.class);
    }

    @Test
    public void testPopulate() throws ConversionException {
        when(source.getName()).thenReturn(NAME);
        given(source.getCode()).willReturn(CODE);
        given(source.getDescription()).willReturn(DESC);
        populator.populate(source,target);
        assertEquals(CODE, target.getCode());
        assertEquals(NAME, target.getName());
        assertEquals(DESC, target.getDescription());

    }

}
