package com.ibm.commerce.partner.core.partnerSalesOrg.dao;

import com.ibm.commerce.partner.core.model.PartnerSalesOrganisationModel;
import java.util.List;

/**
 * Data Access Object interface for handling operations related to
 * {@link PartnerSalesOrganisationModel}.
 *
 * <p>This interface defines the contract for retrieving {@link PartnerSalesOrganisationModel}
 * instances using business-specific attributes such as their unique codes.</p>
 *
 * @see PartnerSalesOrganisationModel
 */
public interface PartnerSalesOrgDao {

    /**
     * Retrieves a list of {@link PartnerSalesOrganisationModel} instances that match the provided
     * list of codes.
     *
     * @param salesOrgCodes a list of unique codes representing partner sales organisations
     * @return a list of {@link PartnerSalesOrganisationModel} objects that match the provided
     * codes; the list will be empty if no matches are found
     */
    List<PartnerSalesOrganisationModel> getSalesOrgsByCodes(List<String> salesOrgCodes);

}
