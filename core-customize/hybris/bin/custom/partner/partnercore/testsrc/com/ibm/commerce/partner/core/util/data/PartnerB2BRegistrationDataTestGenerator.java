package com.ibm.commerce.partner.core.util.data;

import com.ibm.commerce.partner.data.PartnerB2BRegistrationData;

public class PartnerB2BRegistrationDataTestGenerator {

    public static PartnerB2BRegistrationData createCustomerModel(String emailId) {
        PartnerB2BRegistrationData partnerB2BRegistrationData = new PartnerB2BRegistrationData();
        partnerB2BRegistrationData.setEmail(emailId);
        return partnerB2BRegistrationData;
    }
}
