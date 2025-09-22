package com.ibm.commerce.partner.core.daos;

import de.hybris.platform.processengine.model.BusinessProcessModel;
import java.util.List;

/**
 * Doa have methods related to the business process
 */
public interface PartnerProcessDao {

    List<BusinessProcessModel> getProcessListByCode(
        String orderCode);
}
