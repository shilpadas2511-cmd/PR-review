package com.ibm.commerce.partner.facades.company.converter.populators;

import com.ibm.commerce.partner.core.model.PartnerSalesOrganisationModel;
import com.ibm.commerce.partnerwebservicescommons.company.dto.IbmPartnerSalesOrganisationData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for {@link PartnerSalesOrgReversePopulator}
 */
public class PartnerSalesOrgReversePopulatorTest {

    private PartnerSalesOrgReversePopulator populator;

    @Before
    public void setUp() {
        populator = new PartnerSalesOrgReversePopulator();
    }

    @Test
    public void testPopulate_shouldSetCode() throws ConversionException {
        // Given
        IbmPartnerSalesOrganisationData source = new IbmPartnerSalesOrganisationData();
        source.setCode("ORG001");

        PartnerSalesOrganisationModel target = new PartnerSalesOrganisationModel();

        // When
        populator.populate(source, target);

        // Then
        Assert.assertEquals("ORG001", target.getCode());
    }

    @Test(expected = NullPointerException.class)
    public void testPopulate_withNullSource_shouldThrowException() throws ConversionException {
        populator.populate(null, new PartnerSalesOrganisationModel());
    }

    @Test(expected = NullPointerException.class)
    public void testPopulate_withNullTarget_shouldThrowException() throws ConversionException {
        populator.populate(new IbmPartnerSalesOrganisationData(), null);
    }
}
