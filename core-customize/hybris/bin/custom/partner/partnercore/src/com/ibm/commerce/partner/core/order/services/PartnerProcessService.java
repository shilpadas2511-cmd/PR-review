package com.ibm.commerce.partner.core.order.services;

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import java.util.List;


/**
 * Service have methods related to the business process
 */
public interface PartnerProcessService {

    void removeCartFromOldProcess(CartModel cart);

    /**
     * Fetches the businessProcess created for that order code
     *
     * @return BusinessProcess
     */
    List<BusinessProcessModel> getBusinessProcessList(String orderCode);

    /**
     * checks  the quote cart business process is in running state
     */
    boolean checkQuoteCartProcessCompleted(CartModel cart);
}
