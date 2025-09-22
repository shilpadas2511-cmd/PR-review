package com.ibm.commerce.partner.core.provisionform.Dao;

import com.ibm.commerce.common.core.model.PartnerProductSetModel;

/**
 * this method is used to  fetch all the model related provision form
 */
public interface PartnerProvisionFormDao {

    /**
     * This method is used to fetch PartnerProductSetModel by code.
     *
     * @param code The request data contains info needed to be sent for the code
     * @return PartnerProductSetModel
     */
    PartnerProductSetModel getProductSet(String code);

}
