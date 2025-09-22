package com.ibm.commerce.partner.core.provisionform.service;

import com.ibm.commerce.partner.core.quote.provision.form.data.request.ProvisionFormItemsRequestData;
import com.ibm.commerce.partner.core.quote.provision.form.data.request.ProvisionFormRequestData;
import com.ibm.commerce.partner.core.quote.provision.form.data.response.ProvisionFormDetailsResponseData;
import com.ibm.commerce.partner.core.quote.provision.form.data.response.ProvisionFormResponseData;

/**
 * This Integration class is used to get provision form token, fetch, create cart and patch service
 */
public interface PartnerProvisionFormOutboundIntegrationService {
    /**
     * This method is used to  create cart for provision form
     * @param provisionFormRequestData The request data contains info needed to be sent for the provisionFormRequestData
     * @return ProvisionFormResponseData
     */
    ProvisionFormResponseData create(ProvisionFormRequestData provisionFormRequestData);

    /**
     * This Method is used for fetching all the provision
     * form details by the form id
     * @param formId The request data contains info needed to be sent for the formId
     * @return ProvisionFormDetailsResponseData
     */
    ProvisionFormDetailsResponseData fetchFormDetails(String formId);

    /**
     * This method is used for update the provisioning form
     * @param provisionFormRequestData The request data contains info needed to be sent for the ProvisionFormItemsRequestData
     * @param cartId The request data contains info needed to be sent for the cartId
     * @return ProvisionFormResponseData
     */
    ProvisionFormResponseData patch(
        ProvisionFormItemsRequestData provisionFormRequestData, String cartId);
}