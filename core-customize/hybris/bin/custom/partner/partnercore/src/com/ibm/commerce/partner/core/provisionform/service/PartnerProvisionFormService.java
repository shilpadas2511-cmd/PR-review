package com.ibm.commerce.partner.core.provisionform.service;

import com.ibm.commerce.common.core.model.PartnerProductSetModel;

/**
 * This class is used to implementation related Partner Provision model when data is coming from
 * CPQ
 */
public interface PartnerProvisionFormService {

    /**
     * This method is used to fetch PartnerProductSetModel by searching code
     *
     * @param code The request data contains info needed to be sent for the code
     * @return PartnerProductSetModel
     */
    PartnerProductSetModel getProductSet(String code);


}
