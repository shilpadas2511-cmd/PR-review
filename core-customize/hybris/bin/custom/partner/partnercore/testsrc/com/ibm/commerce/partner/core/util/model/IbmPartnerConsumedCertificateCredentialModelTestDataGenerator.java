package com.ibm.commerce.partner.core.util.model;

import com.ibm.commerce.partner.core.model.IbmPartnerConsumedCertificateCredentialModel;

public class IbmPartnerConsumedCertificateCredentialModelTestDataGenerator {

    public static IbmPartnerConsumedCertificateCredentialModel createCertificateCredentialModel(String password) {
        IbmPartnerConsumedCertificateCredentialModel certificateCredentialModel = new IbmPartnerConsumedCertificateCredentialModel();
        certificateCredentialModel.setPassword(password);
        return certificateCredentialModel;
    }
}
