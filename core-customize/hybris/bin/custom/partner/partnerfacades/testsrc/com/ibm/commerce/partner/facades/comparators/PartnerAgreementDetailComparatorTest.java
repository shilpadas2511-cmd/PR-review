package com.ibm.commerce.partner.facades.comparators;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.ibm.commerce.partner.company.endcustomer.data.IbmPartnerAgreementDetailData;
import com.ibm.commerce.partner.facades.util.IbmPartnerAgreementDetailTestDataGenerator;


/**
 * Test class for {@link PartnerAgreementDetailComparator}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PartnerAgreementDetailComparatorTest {

    private final static String AGREEMENT_LEVEL = "testLevel";
    private final static String AGREEMENT_LEVEL2 = "testLevel2";
    private final static String PROGRAM_TYPE = "testProgramType";
    private final static String PROGRAM_TYPE2 = "testProgramType2";
    private final static String AGREEMENT_NUMBER_1 = "1234";
    private final static String AGREEMENT_NUMBER_2 = "3456";
    private final static String AGREEMENT_OPTION_1 = "1234";
    private final static String AGREEMENT_OPTION_2 = "3456";

    @InjectMocks
    PartnerAgreementDetailComparator partnerAgreementDetailComparator;

    IbmPartnerAgreementDetailData b2bUnitAgreementDetail;
    IbmPartnerAgreementDetailData partnerAgreementDetailData;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        partnerAgreementDetailComparator = new PartnerAgreementDetailComparator();
        partnerAgreementDetailData = IbmPartnerAgreementDetailTestDataGenerator
            .createIbmPartnerAgreementDetailData(AGREEMENT_NUMBER_1, AGREEMENT_LEVEL, PROGRAM_TYPE);
        partnerAgreementDetailData.setAgreementOption(AGREEMENT_OPTION_1);
    }

    @Test
    public void testCompare() {
        b2bUnitAgreementDetail = IbmPartnerAgreementDetailTestDataGenerator.createIbmPartnerAgreementDetailData(
            AGREEMENT_NUMBER_1,
            AGREEMENT_LEVEL, PROGRAM_TYPE);
        b2bUnitAgreementDetail.setAgreementOption(AGREEMENT_OPTION_1);
        final int result = partnerAgreementDetailComparator.compare(b2bUnitAgreementDetail,
            partnerAgreementDetailData);
        Assert.assertEquals(0, result);
    }

    @Test
    public void testCompareProgramTypeNotEqual() {
        b2bUnitAgreementDetail = IbmPartnerAgreementDetailTestDataGenerator.createIbmPartnerAgreementDetailData(
            AGREEMENT_NUMBER_1,
            AGREEMENT_LEVEL, PROGRAM_TYPE2);
        b2bUnitAgreementDetail.setAgreementOption(AGREEMENT_OPTION_1);
        final int result = partnerAgreementDetailComparator.compare(b2bUnitAgreementDetail,
            partnerAgreementDetailData);
        Assert.assertEquals(1, result);
    }

    @Test
    public void testComparePartnerAgreementNumberNotEqual() {
        b2bUnitAgreementDetail = IbmPartnerAgreementDetailTestDataGenerator.createIbmPartnerAgreementDetailData(
            AGREEMENT_NUMBER_2,
            AGREEMENT_LEVEL, PROGRAM_TYPE);
        final int result = partnerAgreementDetailComparator.compare(b2bUnitAgreementDetail,
            partnerAgreementDetailData);
        b2bUnitAgreementDetail.setAgreementOption(AGREEMENT_OPTION_1);
        Assert.assertEquals(1, result);
    }


    @Test
    public void testCompareAgreementOptionNotEqual() {
        b2bUnitAgreementDetail = IbmPartnerAgreementDetailTestDataGenerator.createIbmPartnerAgreementDetailData(
            AGREEMENT_NUMBER_1,
            AGREEMENT_LEVEL, PROGRAM_TYPE);
        b2bUnitAgreementDetail.setAgreementOption(AGREEMENT_OPTION_2);
        final int result = partnerAgreementDetailComparator.compare(b2bUnitAgreementDetail,
            partnerAgreementDetailData);
        Assert.assertEquals(1, result);
    }

    @Test
    public void testCompareAgreementLevelNotEqual() {
        b2bUnitAgreementDetail = IbmPartnerAgreementDetailTestDataGenerator.createIbmPartnerAgreementDetailData(
            AGREEMENT_NUMBER_1,
            AGREEMENT_LEVEL2, PROGRAM_TYPE);
        b2bUnitAgreementDetail.setAgreementOption(AGREEMENT_OPTION_1);
        final int result = partnerAgreementDetailComparator.compare(b2bUnitAgreementDetail,
            partnerAgreementDetailData);
        Assert.assertEquals(1, result);
    }
}
