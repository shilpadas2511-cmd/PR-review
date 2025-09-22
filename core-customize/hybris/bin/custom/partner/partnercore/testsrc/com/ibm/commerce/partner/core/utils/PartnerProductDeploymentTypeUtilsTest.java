package com.ibm.commerce.partner.core.utils;

import com.ibm.commerce.common.core.model.IbmDeploymentTypeModel;
import com.ibm.commerce.common.core.model.IbmVariantProductModel;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PartnerProductDeploymentTypeUtilsTest {

    public static final String SAAS = "SaaS";
    public static final String BESPOKE = "Bespoke";
    private IbmVariantProductModel product;
    private IbmDeploymentTypeModel deploymentType;

    @Before
    public void setUp() {
        product = mock(IbmVariantProductModel.class);
        deploymentType = mock(IbmDeploymentTypeModel.class);
        when(product.getDeploymentType()).thenReturn(deploymentType);
    }

    @Test
    public void testIsSaasProductTrue() {
        when(deploymentType.getCode()).thenReturn(SAAS);
        assertTrue(PartnerProductDeploymentTypeUtils.isSaasProduct(product));
    }

    @Test
    public void testIsBespokeProductTrue() {
        when(deploymentType.getCode()).thenReturn(BESPOKE);
        assertTrue(PartnerProductDeploymentTypeUtils.isBespokeProduct(product));
    }

    @Test
    public void testIsBespokeProductFalse() {
        when(deploymentType.getCode()).thenReturn(SAAS);
        assertFalse(PartnerProductDeploymentTypeUtils.isBespokeProduct(product));
    }

    @Test(expected = NullPointerException.class)
    public void testIsSaasProductWithNullDeploymentTypeThrowsException() {
        when(product.getDeploymentType()).thenReturn(null);
        PartnerProductDeploymentTypeUtils.isSaasProduct(product);
    }

    @Test(expected = NullPointerException.class)
    public void testIsBespokeProductWithNullDeploymentTypeThrowsException() {
        when(product.getDeploymentType()).thenReturn(null);
        PartnerProductDeploymentTypeUtils.isBespokeProduct(product);
    }
}
