package com.ibm.commerce.partner.core.partnerPidAgreement.service;

import com.ibm.commerce.partner.core.model.PartnerPIDAgreementModel;

/*
 Service interface for retrieving Partner PID agreement details.
 *
 * <p>This service provides access to {@link PartnerPIDAgreementModel} instances,
 * which define configuration flags and contractual information related to specific
 * Partner Product Identifiers (PIDs). These values are commonly used in
 * downstream pricing, billing, and subscription processes for IBM partner offerings.</p>
 */
public interface PartnerPidAgreementService {

    /**
     * Retrieves a {@link PartnerPIDAgreementModel} based on the provided Partner PID.
     *
     * @param pid the Partner PID to look up; must not be {@code null} or empty.
     * @return the {@link PartnerPIDAgreementModel} associated with the given PID, or {@code null}
     * if no match is found.
     * @throws IllegalArgumentException if the provided PID is {@code null} or invalid.
     */
    PartnerPIDAgreementModel getPIDAgreementByPid(String pid);

}
