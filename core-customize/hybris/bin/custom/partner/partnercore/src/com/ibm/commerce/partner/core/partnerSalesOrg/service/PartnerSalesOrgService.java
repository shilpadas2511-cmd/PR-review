package com.ibm.commerce.partner.core.partnerSalesOrg.service;

import com.ibm.commerce.partner.core.model.PartnerSalesOrganisationModel;
import java.util.List;

/**
 * Service interface for managing operations related to {@link PartnerSalesOrganisationModel}.
 *
 * <p>
 * Provides business-level methods to extract sales organisation codes from DTOs and models,
 * retrieve existing sales organisation models, and resolve final model lists by matching or
 * creating as necessary.
 * </p>
 */
public interface PartnerSalesOrgService {

    /**
     * Fetches List of PartnerSalesOrganisationModel
     *
     * @param salesOrgCodes List of codes
     * @return list of PartnerSalesOrganisationModel
     */
    List<PartnerSalesOrganisationModel> getSalesOrgsByCodes(List<String> salesOrgCodes);
}
