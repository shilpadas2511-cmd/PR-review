package com.ibm.commerce.partner.facades;

import com.ibm.commerce.partner.core.model.PartnerSalesOrganisationModel;
import com.ibm.commerce.partnerwebservicescommons.company.dto.IbmPartnerSalesOrganisationData;
import java.util.List;
import java.util.Set;

/**
 * Interface for {@link PartnerSalesOrganisationModel}
 */
public interface PartnerSalesOrganizationFacade {

    /**
     * Fetch the Sales organizations based on ibmPartnerSalesOrganisationData.code if it exist,
     * otherwise create the {@link PartnerSalesOrganisationModel}.
     *
     * @param salesOrganisations list of {@link IbmPartnerSalesOrganisationData}
     * @return Set of {@link PartnerSalesOrganisationModel}
     */
    Set<PartnerSalesOrganisationModel> getOrCreateSalesOrgs(
        List<IbmPartnerSalesOrganisationData> salesOrganisations);
}
