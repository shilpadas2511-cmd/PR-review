package com.ibm.commerce.partner.facades.provision.populators;

import com.ibm.commerce.common.core.model.PartnerProductSetModel;
import com.ibm.commerce.partner.core.model.PartnerProvisionFormModel;
import com.ibm.commerce.partner.provision.form.data.PartnerProductSetData;
import com.ibm.commerce.partner.provision.form.data.ProvisioningFormData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;

public class ProvisionFormPopulatorTest {

    private ProvisionFormPopulator provisionFormPopulator;

    @Mock
    private Converter<PartnerProductSetModel, PartnerProductSetData> partnerProductSetConverterMock;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        provisionFormPopulator = new ProvisionFormPopulator(partnerProductSetConverterMock);
    }

    @Test
    public void testPopulate_withValidData() throws ConversionException {
        PartnerProvisionFormModel source = new PartnerProvisionFormModel();
        PartnerProductSetModel productSetModel = new PartnerProductSetModel();
        source.setProductSetCode(productSetModel);
        source.setUrl("http://example.com");
        source.setCode("PROV123");

        PartnerProductSetData partnerProductSetData = new PartnerProductSetData();
        Mockito.when(partnerProductSetConverterMock.convert(productSetModel))
            .thenReturn(partnerProductSetData);

        ProvisioningFormData target = new ProvisioningFormData();

        provisionFormPopulator.populate(source, target);

        assertNotNull(target.getProductSetCode());
        assertEquals(partnerProductSetData, target.getProductSetCode());
        assertEquals("http://example.com", target.getUrl());
        assertEquals("PROV123", target.getCode());
    }

    @Test
    public void testPopulate_withNullProductSetCode() throws ConversionException {
        PartnerProvisionFormModel source = new PartnerProvisionFormModel();
        source.setProductSetCode(null);
        source.setUrl("http://example.com");
        source.setCode("PROV123");

        ProvisioningFormData target = new ProvisioningFormData();

        provisionFormPopulator.populate(source, target);

        assertNull(target.getProductSetCode());
        assertEquals("http://example.com", target.getUrl());
        assertEquals("PROV123", target.getCode());
    }

    @Test
    public void testPopulate_withNullUrlAndCode() throws ConversionException {
        PartnerProvisionFormModel source = new PartnerProvisionFormModel();
        source.setProductSetCode(new PartnerProductSetModel());
        source.setUrl(null);
        source.setCode(null);

        PartnerProductSetData partnerProductSetData = new PartnerProductSetData();
        Mockito.when(partnerProductSetConverterMock.convert(source.getProductSetCode()))
            .thenReturn(partnerProductSetData);

        ProvisioningFormData target = new ProvisioningFormData();

        provisionFormPopulator.populate(source, target);

        assertNotNull(target.getProductSetCode());
        assertEquals(partnerProductSetData, target.getProductSetCode());
        assertNull(target.getUrl());
        assertNull(target.getCode());
    }

    @Test(expected = NullPointerException.class)
    public void testPopulate_withNullSource() {
        provisionFormPopulator.populate(null, new ProvisioningFormData());
    }

    @Test(expected = ConversionException.class)
    public void testPopulate_withConversionException() throws ConversionException {
        PartnerProvisionFormModel source = new PartnerProvisionFormModel();
        source.setProductSetCode(new PartnerProductSetModel());
        source.setUrl("http://example.com");
        source.setCode("PROV123");

        Mockito.when(partnerProductSetConverterMock.convert(source.getProductSetCode()))
            .thenThrow(new ConversionException("Conversion failed"));

        ProvisioningFormData target = new ProvisioningFormData();

        provisionFormPopulator.populate(source, target);
    }
}
