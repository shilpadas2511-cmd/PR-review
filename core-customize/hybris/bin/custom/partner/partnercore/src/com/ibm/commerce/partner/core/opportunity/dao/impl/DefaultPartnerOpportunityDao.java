package com.ibm.commerce.partner.core.opportunity.dao.impl;

import com.ibm.commerce.partner.core.model.IbmPartnerOpportunityModel;
import com.ibm.commerce.partner.core.opportunity.dao.PartnerOpportunityDao;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Implementation for {@link PartnerOpportunityDao}
 */
public class DefaultPartnerOpportunityDao extends
    DefaultGenericDao<IbmPartnerOpportunityModel> implements PartnerOpportunityDao {

    public DefaultPartnerOpportunityDao() {
        super(IbmPartnerOpportunityModel._TYPECODE);
    }


    @Override
    public IbmPartnerOpportunityModel fetch(final String opportunityId) {
        if (StringUtils.isEmpty(opportunityId)) {
            return null;
        }
        Map<String, Object> params = new HashMap<>();
        params.put(IbmPartnerOpportunityModel.CODE, opportunityId);
        List<IbmPartnerOpportunityModel> opportunityModels = find(params);
        return CollectionUtils.isEmpty(opportunityModels) ? null : opportunityModels.get(0);
    }
}
