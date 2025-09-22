package com.ibm.commerce.partner.facades.company.endcustomer.converter.populators;

import com.ibm.commerce.partner.company.endcustomer.data.IbmPartnerAgreementDetailData;
import com.ibm.commerce.partner.core.model.IbmPartnerAgreementDetailModel;
import com.ibm.commerce.partner.facades.util.IbmPartnerAgreementDetailTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

@UnitTest
public class PartnerAgreementDetailReversePopulatorTest {

    private static final String AGREEMENT_NUMBER = "1231";
    private static final String AGREEMENT_LEVEL = "1";
    private static final String PROGRAM_TYPE = "Test";
    private static final String AGREEMENT_OPTION = "option";
    private static final String ANNIVERSARY_MONTH = "March";
    @InjectMocks
    PartnerAgreementDetailReversePopulator partnerAgreementDetailReversePopulator;

    IbmPartnerAgreementDetailData agreementDetailData;

    IbmPartnerAgreementDetailModel agreementDetailModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        partnerAgreementDetailReversePopulator = new PartnerAgreementDetailReversePopulator();
        agreementDetailModel = new IbmPartnerAgreementDetailModel();
        agreementDetailData = IbmPartnerAgreementDetailTestDataGenerator.updateIbmPartnerAgreementDetailData(AGREEMENT_NUMBER,AGREEMENT_LEVEL,PROGRAM_TYPE, AGREEMENT_OPTION, ANNIVERSARY_MONTH);
    }

    @Test
    public void testPopulate() {
        partnerAgreementDetailReversePopulator.populate(agreementDetailData, agreementDetailModel);
        Assert.assertEquals(AGREEMENT_LEVEL, agreementDetailModel.getAgreementLevel());
        Assert.assertEquals(AGREEMENT_NUMBER, agreementDetailModel.getAgreementNumber());
        Assert.assertEquals(AGREEMENT_OPTION, agreementDetailModel.getAgreementOption());
    }
}
