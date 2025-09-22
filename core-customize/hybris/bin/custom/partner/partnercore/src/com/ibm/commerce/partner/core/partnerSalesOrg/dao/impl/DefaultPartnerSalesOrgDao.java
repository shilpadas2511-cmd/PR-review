package com.ibm.commerce.partner.core.partnerSalesOrg.dao.impl;

import com.ibm.commerce.partner.core.model.PartnerSalesOrganisationModel;
import com.ibm.commerce.partner.core.partnerSalesOrg.dao.PartnerSalesOrgDao;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import java.util.Collections;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;

/**
 * Default implementation of the {@link PartnerSalesOrgDao} interface.
 *
 * <p>This DAO class is responsible for accessing {@link PartnerSalesOrganisationModel} entities
 * from the database. It extends {@link DefaultGenericDao} to inherit basic generic DAO
 * functionalities and adds custom search logic using Hybris's {@link FlexibleSearchService}.</p>
 *
 * <p>The main purpose of this class is to fetch one or more {@link PartnerSalesOrganisationModel}
 * objects based on provided codes.</p>
 */
public class DefaultPartnerSalesOrgDao extends
    DefaultGenericDao<PartnerSalesOrganisationModel> implements PartnerSalesOrgDao {

    private static final String GET_PARTNER_SALES_ORG_BY_PID =
        "SELECT DISTINCT {pq:" + ItemModel.PK + "} FROM {" + PartnerSalesOrganisationModel._TYPECODE
            + " as pq } where {pq.CODE} in ( ?" + PartnerSalesOrganisationModel.CODE + " ) ";

    /**
     * Constructs a DAO for the {@link PartnerSalesOrganisationModel} type.
     */
    public DefaultPartnerSalesOrgDao() {
        super(PartnerSalesOrganisationModel._TYPECODE);
    }

    /**
     * Retrieves a list of {@link PartnerSalesOrganisationModel} instances whose codes are present
     * in the given list of sales organization codes.
     *
     * <p>Uses a flexible search query to efficiently fetch matching models from the database.</p>
     *
     * @param salesOrgCodes a list of sales organization codes to search for
     * @return a list of {@link PartnerSalesOrganisationModel} objects matching the provided codes;
     * the list may be empty if no matching models are found
     */
    @Override
    public List<PartnerSalesOrganisationModel> getSalesOrgsByCodes(List<String> salesOrgCodes) {
        if (CollectionUtils.isEmpty(salesOrgCodes)) {
            return Collections.emptyList();
        }
        FlexibleSearchQuery query = new FlexibleSearchQuery(GET_PARTNER_SALES_ORG_BY_PID);
        query.addQueryParameter(PartnerSalesOrganisationModel.CODE, salesOrgCodes);
        SearchResult<PartnerSalesOrganisationModel> salesOrgsSearchResult = getFlexibleSearchService().search(
            query);
        return salesOrgsSearchResult.getResult();
    }

}
