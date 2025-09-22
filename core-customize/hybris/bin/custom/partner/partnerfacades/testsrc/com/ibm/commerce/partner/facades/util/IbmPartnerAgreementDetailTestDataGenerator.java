package com.ibm.commerce.partner.facades.util;

import com.ibm.commerce.partner.company.endcustomer.data.IbmPartnerAgreementDetailData;

public class IbmPartnerAgreementDetailTestDataGenerator {

    public static IbmPartnerAgreementDetailData createIbmPartnerAgreementDetailData(
        final String agreementNumber, final String agreementLevel, final String programType) {
        IbmPartnerAgreementDetailData ibmPartnerAgreementDetailData = new IbmPartnerAgreementDetailData();
        ibmPartnerAgreementDetailData.setAgreementNumber(agreementNumber);
        ibmPartnerAgreementDetailData.setAgreementLevel(agreementLevel);
        ibmPartnerAgreementDetailData.setProgramType(programType);
        return ibmPartnerAgreementDetailData;
    }

    public static IbmPartnerAgreementDetailData updateIbmPartnerAgreementDetailData(
        final String agreementNumber, final String agreementLevel, final String programType,
        String agreementOption, String anniversaryMonth) {
        IbmPartnerAgreementDetailData ibmPartnerAgreementDetailData = createIbmPartnerAgreementDetailData(
            agreementNumber, agreementLevel, programType);
        ibmPartnerAgreementDetailData.setAgreementOption(agreementOption);
        ibmPartnerAgreementDetailData.setAnniversaryMonth(anniversaryMonth);
        return ibmPartnerAgreementDetailData;
    }

}
