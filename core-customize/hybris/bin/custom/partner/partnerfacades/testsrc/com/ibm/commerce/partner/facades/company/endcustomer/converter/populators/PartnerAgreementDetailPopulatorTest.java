package com.ibm.commerce.partner.facades.company.endcustomer.converter.populators;

import com.ibm.commerce.partner.company.endcustomer.data.IbmPartnerAgreementDetailData;
import com.ibm.commerce.partner.core.model.IbmPartnerAgreementDetailModel;
import com.ibm.commerce.partner.core.util.model.IbmPartnerAgreementDetailModelTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

@UnitTest
public class PartnerAgreementDetailPopulatorTest {

    private final static String AGREEMENT_NUMBER = "123";
    private final static String AGREEMENT_LEVEL = "1";
    private final static String PROGRAM_TYPE = "Test";

    @InjectMocks
    PartnerAgreementDetailPopulator partnerAgreementDetailPopulator;

    IbmPartnerAgreementDetailModel agreementDetailModel;
    IbmPartnerAgreementDetailData agreementDetailData;
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        partnerAgreementDetailPopulator = new PartnerAgreementDetailPopulator();
        agreementDetailModel = IbmPartnerAgreementDetailModelTestDataGenerator.createIbmPartnerAgreementDetailModel(AGREEMENT_NUMBER, AGREEMENT_LEVEL, PROGRAM_TYPE, null);
        agreementDetailData = new IbmPartnerAgreementDetailData();
    }

    @Test
    public void testPopulate() {
        partnerAgreementDetailPopulator.populate(agreementDetailModel, agreementDetailData);
        Assert.assertEquals(AGREEMENT_LEVEL, agreementDetailData.getAgreementLevel());
        Assert.assertEquals(AGREEMENT_NUMBER, agreementDetailData.getAgreementNumber());
    }
}
