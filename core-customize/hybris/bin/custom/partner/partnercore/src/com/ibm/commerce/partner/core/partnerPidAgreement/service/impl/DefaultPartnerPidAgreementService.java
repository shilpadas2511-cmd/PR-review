package com.ibm.commerce.partner.core.partnerPidAgreement.service.impl;

import com.ibm.commerce.partner.core.model.PartnerPIDAgreementModel;
import com.ibm.commerce.partner.core.partnerPidAgreement.dao.PartnerPidAgreementDao;
import com.ibm.commerce.partner.core.partnerPidAgreement.service.PartnerPidAgreementService;

/**
 * Default implementation of the {@link PartnerPidAgreementService} interface.
 *
 * <p>This service delegates the retrieval of {@link PartnerPIDAgreementModel} instances
 * to the underlying {@link PartnerPidAgreementDao}, typically used to access persistent storage or
 * external sources containing partner agreement data.</p>
 *
 * <p>Use this service to obtain configuration flags and metadata for a given Partner PID,
 * which may influence business logic such as pricing, billing, and product eligibility in the IBM
 * Partner ecosystem.</p>
 */
public class DefaultPartnerPidAgreementService implements PartnerPidAgreementService {

    private final PartnerPidAgreementDao partnerPidAgreementDao;

    /**
     * Constructs a new instance with the required DAO dependency.
     *
     * @param partnerPidAgreementDao the DAO used to retrieve Partner PID agreement data; must not
     *                               be {@code null}.
     */
    public DefaultPartnerPidAgreementService(PartnerPidAgreementDao partnerPidAgreementDao) {
        this.partnerPidAgreementDao = partnerPidAgreementDao;
    }

    /**
     * Retrieves a {@link PartnerPIDAgreementModel} associated with the provided Partner PID.
     *
     * <p>This method delegates the retrieval to the underlying {@link PartnerPidAgreementDao}.
     * It is used to access configuration flags such as whether BM (Billing Model) or PID logic
     * should be disabled for a specific product, or whether the product is flagged as SaaS.</p>
     *
     * @param pid the Partner Product Identifier (PID); must not be {@code null} or empty.
     * @return the corresponding {@link PartnerPIDAgreementModel} if found; otherwise {@code null}.
     * @throws IllegalArgumentException if {@code pid} is {@code null} or empty.
     */
    @Override
    public PartnerPIDAgreementModel getPIDAgreementByPid(String pid) {
        return getPartnerPidAgreementDao().getPIDAgreementByPid(pid);
    }

    public PartnerPidAgreementDao getPartnerPidAgreementDao() {
        return partnerPidAgreementDao;
    }
}