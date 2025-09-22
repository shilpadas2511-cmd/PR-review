package com.ibm.commerce.partner.core.util.model;

import com.ibm.commerce.partner.core.model.IbmPartnerConsumedDestinationOAuthCredentialModel;

public class IBMPartnerConsumedDestinationOAuthCredentialModelTestDataGenerator {
    public static IbmPartnerConsumedDestinationOAuthCredentialModel createPartnerQuoteOAuthCredentialModel() {
        IbmPartnerConsumedDestinationOAuthCredentialModel iBMPartnerConsumedDestinationOAuthCredentialModel = new IbmPartnerConsumedDestinationOAuthCredentialModel();
        iBMPartnerConsumedDestinationOAuthCredentialModel.setAcubicApi("test");
        iBMPartnerConsumedDestinationOAuthCredentialModel.setClientId("test");
        iBMPartnerConsumedDestinationOAuthCredentialModel.setClientSecret("test");
        return iBMPartnerConsumedDestinationOAuthCredentialModel;
    }

}
