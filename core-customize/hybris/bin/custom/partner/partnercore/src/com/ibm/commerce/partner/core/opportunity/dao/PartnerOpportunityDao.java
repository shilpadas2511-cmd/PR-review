package com.ibm.commerce.partner.core.opportunity.dao;

import com.ibm.commerce.partner.core.model.IbmPartnerOpportunityModel;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;

/**
 * Dao for {@link com.ibm.commerce.partner.core.model.IbmPartnerOpportunityModel}
 */
public interface PartnerOpportunityDao extends GenericDao<IbmPartnerOpportunityModel> {

    /**
     * Fetches {@link IbmPartnerOpportunityModel} based on opportunityId
     *
     * @param opportunityId
     * @return
     */
    IbmPartnerOpportunityModel fetch(String opportunityId);
}
