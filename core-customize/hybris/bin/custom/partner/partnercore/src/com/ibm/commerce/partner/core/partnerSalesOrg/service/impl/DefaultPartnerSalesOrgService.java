package com.ibm.commerce.partner.core.partnerSalesOrg.service.impl;

import com.ibm.commerce.partner.core.model.PartnerSalesOrganisationModel;
import com.ibm.commerce.partner.core.partnerSalesOrg.dao.PartnerSalesOrgDao;
import com.ibm.commerce.partner.core.partnerSalesOrg.service.PartnerSalesOrgService;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.List;

/**
 * Default implementation of the {@link PartnerSalesOrgService} interface.
 *
 * <p>This service is responsible for handling the business logic related to
 * {@link PartnerSalesOrganisationModel}, such as retrieving, resolving, and creating sales
 * organisation models based on codes.</p>
 *
 * <p>It leverages {@link PartnerSalesOrgDao} for database access and {@link ModelService}
 * for persistence operations.</p>
 *
 * @see PartnerSalesOrgService
 * @see PartnerSalesOrganisationModel
 * @see PartnerSalesOrgDao
 */
public class DefaultPartnerSalesOrgService implements PartnerSalesOrgService {

    private final PartnerSalesOrgDao salesOrgDao;

    public DefaultPartnerSalesOrgService(final PartnerSalesOrgDao salesOrgDao) {
        this.salesOrgDao = salesOrgDao;
    }

    /**
     * Retrieves a list of {@link PartnerSalesOrganisationModel} instances corresponding to the
     * provided sales org codes.
     *
     * @param salesOrgCodes a list of unique codes representing partner sales organisations
     * @return a list of {@link PartnerSalesOrganisationModel} objects matching the provided codes;
     * the list may be empty if no matches are found
     */
    public List<PartnerSalesOrganisationModel> getSalesOrgsByCodes(List<String> salesOrgCodes) {
        return getSalesOrgDao().getSalesOrgsByCodes(salesOrgCodes);
    }

    /**
     * Returns the {@link PartnerSalesOrgDao} used by this service.
     *
     * @return the partner sales org DAO instance
     */
    public PartnerSalesOrgDao getSalesOrgDao() {
        return salesOrgDao;
    }

}
