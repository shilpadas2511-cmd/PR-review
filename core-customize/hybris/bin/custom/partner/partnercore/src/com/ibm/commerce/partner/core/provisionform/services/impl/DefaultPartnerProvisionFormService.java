package com.ibm.commerce.partner.core.provisionform.services.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import com.ibm.commerce.common.core.model.PartnerProductSetModel;
import com.ibm.commerce.partner.core.provisionform.Dao.PartnerProvisionFormDao;
import com.ibm.commerce.partner.core.provisionform.service.PartnerProvisionFormService;

/**
 * This class is used to implementation related Partner Provision model when data is coming from
 * CPQ
 */
public class DefaultPartnerProvisionFormService implements PartnerProvisionFormService {


    private PartnerProvisionFormDao partnerProvisionFormDao;

    public DefaultPartnerProvisionFormService(PartnerProvisionFormDao partnerProvisionFormDao) {
        this.partnerProvisionFormDao = partnerProvisionFormDao;
    }

    /**
     * This method is used to fetech PartnerProductSetModel by code .
     *
     * @param code The request data contains info needed to be sent for the code
     * @return PartnerProductSetModel
     */
    public PartnerProductSetModel getProductSet(final String code) {
        validateParameterNotNullStandardMessage("code", code);
        return getPartnerProvisionFormDao().getProductSet(code);
    }

    public PartnerProvisionFormDao getPartnerProvisionFormDao() {
        return partnerProvisionFormDao;
    }

}
