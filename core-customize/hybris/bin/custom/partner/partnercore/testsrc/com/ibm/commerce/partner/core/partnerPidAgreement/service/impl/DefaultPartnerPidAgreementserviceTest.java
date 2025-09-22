package com.ibm.commerce.partner.core.partnerPidAgreement.service.impl;

import com.ibm.commerce.partner.core.model.PartnerPIDAgreementModel;
import com.ibm.commerce.partner.core.partnerPidAgreement.dao.PartnerPidAgreementDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DefaultPartnerPidAgreementServiceTest {

    private PartnerPidAgreementDao partnerPidAgreementDao;
    private DefaultPartnerPidAgreementService service;

    @BeforeEach
    void setUp() {
        partnerPidAgreementDao = mock(PartnerPidAgreementDao.class);
        service = new DefaultPartnerPidAgreementService(partnerPidAgreementDao);
    }

    @Test
    void testGetPIDAgreementByPid_ReturnsModel() {
        String testPid = "PID123";
        PartnerPIDAgreementModel mockModel = new PartnerPIDAgreementModel();
        when(partnerPidAgreementDao.getPIDAgreementByPid(testPid)).thenReturn(mockModel);

        PartnerPIDAgreementModel result = service.getPIDAgreementByPid(testPid);

        assertNotNull(result);
        assertEquals(mockModel, result);
        verify(partnerPidAgreementDao).getPIDAgreementByPid(testPid);
    }

    @Test
    void testGetPIDAgreementByPid_ReturnsNull() {
        String testPid = "NON_EXISTENT_PID";
        when(partnerPidAgreementDao.getPIDAgreementByPid(testPid)).thenReturn(null);

        PartnerPIDAgreementModel result = service.getPIDAgreementByPid(testPid);

        assertNull(result);
        verify(partnerPidAgreementDao).getPIDAgreementByPid(testPid);
    }
}