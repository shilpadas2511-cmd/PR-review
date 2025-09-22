package com.ibm.commerce.partner.facades.company.converter.populators;

import com.ibm.commerce.partner.core.util.model.B2BUnitModelTestDataGenerator;
import com.ibm.commerce.partner.facades.util.B2BUnitTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@UnitTest
public class B2BUnitReportingOrganizationDetailsPopulatorTest {

    private static final String REPORTING_ORGANIZATION_UID = "70001023";

    @InjectMocks
    B2BUnitReportingOrganizationDetailsPopulator reportingOrganizationDetailsPopulator;

    @Mock
    Converter<B2BUnitModel, B2BUnitData> b2BUnitDataConverter;

    B2BUnitModel b2BUnitModel;
    B2BUnitData b2BUnitData;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        reportingOrganizationDetailsPopulator = new B2BUnitReportingOrganizationDetailsPopulator(b2BUnitDataConverter);
        b2BUnitModel = new B2BUnitModel();
        b2BUnitModel.setReportingOrganization(B2BUnitModelTestDataGenerator.createReportingOrganization(REPORTING_ORGANIZATION_UID));
        B2BUnitData unitData = B2BUnitTestDataGenerator.prepareB2BUnitData(REPORTING_ORGANIZATION_UID, null);
        Mockito.when(b2BUnitDataConverter.convert(b2BUnitModel.getReportingOrganization())).thenReturn(unitData);
        b2BUnitData = new B2BUnitData();
    }

    @Test
    public void testPopulate() {
        reportingOrganizationDetailsPopulator.populate(b2BUnitModel, b2BUnitData);
        Assert.assertNotNull(b2BUnitData.getReportingOrganization());
        Assert.assertEquals(REPORTING_ORGANIZATION_UID, b2BUnitData.getReportingOrganization().getUid());
    }

    @Test
    public void testPopulateWhenReportOrganizationIsNull() {
        b2BUnitModel.setReportingOrganization(null);
        reportingOrganizationDetailsPopulator.populate(b2BUnitModel, b2BUnitData);
        Assert.assertNull(b2BUnitData.getReportingOrganization());
    }
}
