package com.ibm.commerce.partner.core.accountservice.services;

import com.ibm.commerce.partner.core.company.distributor.data.response.PartnerDistributorSiteIdResponseData;
import com.ibm.commerce.partner.core.company.reseller.data.response.PartnerResellerSiteIdResponseData;
import java.util.List;

/**
 * Interface to interact with Partner Account Service
 */
public interface PartnerAccountServiceOutboundIntegrationService {

    /**
     * Fetches SiteId for Customer
     *
     * @param partnerId
     * @return
     */
    List<PartnerResellerSiteIdResponseData> getResellerSiteId(String accountUser);

    /**
     * Fetches SiteId for Customer
     *
     * @param partnerId
     * @return
     */
    List<PartnerDistributorSiteIdResponseData> getDistributorSiteId(String distributorNumber,
        String resellerAccountUser);
}
