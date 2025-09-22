package com.ibm.commerce.partner.core.partnerPidAgreement.dao;

import com.ibm.commerce.partner.core.model.PartnerPIDAgreementModel;

/**
 * Data Access Object (DAO) interface for retrieving {@link PartnerPIDAgreementModel} instances
 * based on Partner PID values.
 * <p>
 * This interface abstracts the data retrieval logic for Partner PID Agreements* </p>
 */
public interface PartnerPidAgreementDao {

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
