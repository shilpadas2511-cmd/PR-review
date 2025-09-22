package com.ibm.commerce.partner.core.provisionform.services.impl;

import com.ibm.commerce.common.core.model.PartnerProductSetModel;
import com.ibm.commerce.partner.core.provisionform.Dao.PartnerProvisionFormDao;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static de.hybris.platform.testframework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@UnitTest
public class DefaultPartnerProvisionFormServiceTest {

    private DefaultPartnerProvisionFormService defaultPartnerProvisionFormService;
    private PartnerProvisionFormDao partnerProvisionFormDao;

    @Before
    public void setUp() {
        partnerProvisionFormDao = Mockito.mock(PartnerProvisionFormDao.class);
        defaultPartnerProvisionFormService = new DefaultPartnerProvisionFormService(partnerProvisionFormDao);
    }

    @Test
    public void testGetProductSet_ValidProductCode_Found() {
        String code = "testCode";
        PartnerProductSetModel partnerProductSetModel = new PartnerProductSetModel();
        partnerProductSetModel.setCode(code);
        when(partnerProvisionFormDao.getProductSet(code)).thenReturn(partnerProductSetModel);
        PartnerProductSetModel result = defaultPartnerProvisionFormService.getProductSet(code);
        assertNotNull(result);
        assertEquals(code, result.getCode());
    }

    @Test
    public void testGetProductSet_ValidProductCode_NotFound() {
        String productCode = "nonExistingCode";
        when(partnerProvisionFormDao.getProductSet(productCode)).thenReturn(null);
        PartnerProductSetModel result = defaultPartnerProvisionFormService.getProductSet(productCode);
        assertNull(result);
    }

    @Test
    public void testGetProductSet_NullProductCode() {
        String code = null;
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            defaultPartnerProvisionFormService.getProductSet(code);
        });
        assertEquals("productCode must not be null", exception.getMessage());
    }
}