package com.ibm.commerce.partner.core.util.model;

import com.ibm.commerce.partner.core.model.IbmPartnerAgreementDetailModel;
import com.ibm.commerce.partner.core.model.IbmPartnerEndCustomerB2BUnitModel;

public class IbmPartnerAgreementDetailModelTestDataGenerator {

    public static IbmPartnerAgreementDetailModel createIbmPartnerAgreementDetailModel(final String agreementNumber, final String agreementLevel, final String programType, final
        IbmPartnerEndCustomerB2BUnitModel unit) {
        IbmPartnerAgreementDetailModel model = new IbmPartnerAgreementDetailModel();
        model.setAgreementNumber(agreementNumber);
        model.setAgreementLevel(agreementLevel);
        model.setProgramType(programType);
        model.setUnit(unit);
        return model;
    }

}
