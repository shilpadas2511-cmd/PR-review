package com.ibm.commerce.partner.facades.provision.populators;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ibm.commerce.common.core.model.PartnerProductSetModel;
import com.ibm.commerce.partner.provision.form.data.PartnerProductSetData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.junit.Before;
import org.junit.Test;

public class PartnerProductSetPopulatorTest {

    private PartnerProductSetPopulator partnerProductSetPopulator;
    private PartnerProductSetModel partnerProductSetModel;
    private PartnerProductSetData partnerProductSetData;

    @Before
    public void setUp() {
        partnerProductSetPopulator = new PartnerProductSetPopulator();

        partnerProductSetModel = mock(PartnerProductSetModel.class);
        partnerProductSetData = mock(PartnerProductSetData.class);
    }

    @Test
    public void testPopulate_ValidData() throws ConversionException {
        String expectedCode = "ABC123";
        String expectedName = "Test Product Set";

        when(partnerProductSetModel.getCode()).thenReturn(expectedCode);
        when(partnerProductSetModel.getName()).thenReturn(expectedName);

        partnerProductSetPopulator.populate(partnerProductSetModel, partnerProductSetData);
        verify(partnerProductSetData).setCode(expectedCode);
        verify(partnerProductSetData).setName(expectedName);
    }

    @Test(expected = NullPointerException.class)
    public void testPopulate_NullModel() throws ConversionException {
        partnerProductSetPopulator.populate(null, partnerProductSetData);
    }

    @Test
    public void testPopulate_EmptyFields() throws ConversionException {
        when(partnerProductSetModel.getCode()).thenReturn("");
        when(partnerProductSetModel.getName()).thenReturn("");
        partnerProductSetPopulator.populate(partnerProductSetModel, partnerProductSetData);
        verify(partnerProductSetData).setCode("");
        verify(partnerProductSetData).setName("");
    }
}
