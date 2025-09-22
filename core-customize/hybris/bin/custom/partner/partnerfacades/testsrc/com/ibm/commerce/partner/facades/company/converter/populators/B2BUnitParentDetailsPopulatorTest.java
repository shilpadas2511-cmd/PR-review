package com.ibm.commerce.partner.facades.company.converter.populators;

import com.ibm.commerce.partner.company.data.IbmB2BUnitData;
import com.ibm.commerce.partner.core.b2b.company.service.PartnerB2BUnitService;
import com.ibm.commerce.partner.core.model.IbmB2BUnitModel;
import com.ibm.commerce.partner.core.model.IbmPartnerB2BUnitModel;
import com.ibm.commerce.partner.core.util.model.B2BUnitModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.IbmB2BUnitModelTestDataGenerator;
import com.ibm.commerce.partner.facades.util.B2BUnitTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.commerceservices.model.OrgUnitModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@UnitTest
public class B2BUnitParentDetailsPopulatorTest {

    private static final String PARENT_UID = "1000101";
    private static final String PARENT_NAME = "test";
    private static final String UID = "00101012";

    @InjectMocks
    B2BUnitParentDetailsPopulator b2BUnitParentDetailsPopulator;

    @Mock
    PartnerB2BUnitService b2BUnitService;

    @Mock
    Converter<B2BUnitModel, B2BUnitData> b2BUnitDataConverter;

    IbmB2BUnitModel ibmB2BUnitModel;

    IbmB2BUnitData ibmB2BUnitData;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testPopulate() {
        b2BUnitParentDetailsPopulator = new B2BUnitParentDetailsPopulator(b2BUnitService,
            b2BUnitDataConverter);
        ibmB2BUnitModel = IbmB2BUnitModelTestDataGenerator.createIbmB2BUnitModel(UID, null, null);
        B2BUnitModel b2BUnitModel = B2BUnitModelTestDataGenerator.createB2BUnitModelActive(
            PARENT_UID, true);
        Mockito.when(b2BUnitService.getParent(ibmB2BUnitModel)).thenReturn(b2BUnitModel);
        B2BUnitData b2BUnitData = B2BUnitTestDataGenerator.prepareB2BUnitData(PARENT_UID,
            PARENT_NAME);
        Mockito.when(b2BUnitDataConverter.convert(b2BUnitModel)).thenReturn(b2BUnitData);
        ibmB2BUnitData = new IbmB2BUnitData();
        b2BUnitParentDetailsPopulator.populate(ibmB2BUnitModel, ibmB2BUnitData);
        Assert.assertNotNull(ibmB2BUnitData.getUnit());
        Assert.assertEquals(PARENT_UID, ibmB2BUnitData.getUnit().getUid());
        Assert.assertEquals(PARENT_NAME, ibmB2BUnitData.getUnit().getName());
    }

    @Test
    public void testPopulateMisMatch() {
        OrgUnitModel mismatchSource = new OrgUnitModel();
        ibmB2BUnitData = new IbmB2BUnitData();
        Mockito.when(b2BUnitService.getParent(ibmB2BUnitModel)).thenReturn(mismatchSource);
        b2BUnitParentDetailsPopulator.populate(ibmB2BUnitModel, ibmB2BUnitData);
        Assert.assertNull(ibmB2BUnitData.getUnit());

    }
}
