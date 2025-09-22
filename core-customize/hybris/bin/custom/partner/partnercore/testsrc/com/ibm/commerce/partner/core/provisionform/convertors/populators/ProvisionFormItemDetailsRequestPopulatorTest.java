package com.ibm.commerce.partner.core.provisionform.convertors.populators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ibm.commerce.common.core.model.IbmDeploymentTypeModel;
import com.ibm.commerce.common.core.model.IbmPartProductModel;
import com.ibm.commerce.common.core.model.IbmVariantProductModel;
import com.ibm.commerce.common.core.model.PartnerProductSetModel;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.quote.provision.form.data.request.ProvisionFormItemDetailsRequestData;
import com.ibm.commerce.partner.core.quote.provision.form.data.request.ProvisionFormItemsRequestData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.session.SessionService;
import java.util.Collections;
import java.util.Map;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ProvisionFormItemDetailsRequestPopulatorTest {

    private ProvisionFormItemDetailsRequestPopulator populator;

    @Mock
    private SessionService sessionService;

    @Mock
    private IbmPartnerCartModel cartModel;

    @Mock
    private AbstractOrderEntryModel childEntry;

    @Mock
    private IbmPartProductModel partProduct;

    @Mock
    private IbmVariantProductModel pidProduct;

    @Mock
    private PartnerProductSetModel productSet;

    @Mock
    private IbmDeploymentTypeModel deploymentTypeModel;

    @Mock
    private ConfigurationService configurationService;
    @Mock
    private Configuration configuration;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(configuration.getBoolean(PartnercoreConstants.PROVISIONING_FORMS_FEATURE_FLAG, false)).thenReturn(true);
        when(configurationService.getConfiguration()).thenReturn(configuration);
        populator = new ProvisionFormItemDetailsRequestPopulator(sessionService,
            configurationService);
    }

    @Test
    public void testPopulate_PartnerCart_Success() {
        ProvisionFormItemsRequestData target = new ProvisionFormItemsRequestData();
        when(cartModel.getEntries()).thenReturn(Collections.singletonList(childEntry));
        when(childEntry.getChildEntries()).thenReturn(Collections.singletonList(childEntry));
        when(childEntry.getProduct()).thenReturn(partProduct);
        when(partProduct.getDeploymentType()).thenReturn(deploymentTypeModel);
        when(partProduct.getDeploymentType().getCode()).thenReturn("Saas");
        when(partProduct.getCode()).thenReturn("testPartNumber");
        when(partProduct.getDescription()).thenReturn("testDescription");
        when(childEntry.getQuantity()).thenReturn(1L);
        when(partProduct.getPartNumber()).thenReturn("testPID");
        when(partProduct.getPidProducts()).thenReturn(
            Collections.singletonList(mock(IbmVariantProductModel.class)));
        when(partProduct.getPidProducts().get(0).getDescription()).thenReturn("testPIDDescription");
        when(partProduct.getProductSetCode()).thenReturn(productSet);
        when(productSet.getCode()).thenReturn("testProductSetCode");

        populator.populate(cartModel, target);

        assertNotNull(target.getItems());
        assertEquals(1, target.getItems().size());

        ProvisionFormItemDetailsRequestData itemDetails = target.getItems().get(0);
        assertEquals("testPartNumber", itemDetails.getPartNumber());
        assertEquals("testDescription", itemDetails.getDescription());
        assertEquals("testPID", itemDetails.getPid());
        assertEquals("testPIDDescription", itemDetails.getPidDescription());
        assertEquals("testProductSetCode", itemDetails.getProductSetCode());

        verify(sessionService).setAttribute(eq("partnerSetCodes"), any(Map.class));

    }

    @Test
    public void testPopulate_PartnerCart_NoChildEntries() {
        ProvisionFormItemsRequestData target = new ProvisionFormItemsRequestData();
        when(cartModel.getEntries()).thenReturn(Collections.singletonList(childEntry));
        when(childEntry.getChildEntries()).thenReturn(Collections.emptyList());
        populator.populate(cartModel, target);

        assertNotNull(target.getItems());
        assertTrue(target.getItems().isEmpty());
    }


    @Test(expected = NullPointerException.class)
    public void testCreateItemDetailsRequestData_InvalidProduct() {
        when(childEntry.getProduct()).thenReturn(mock(IbmPartProductModel.class));
        ProvisionFormItemDetailsRequestData requestData = populator.createItemDetailsRequestData(
            childEntry);
        assertNull(requestData);
    }

    @Test
    public void testCreateItemDetailsRequestData_ValidProduct() {
        when(childEntry.getProduct()).thenReturn(partProduct);
        when(partProduct.getDeploymentType()).thenReturn(deploymentTypeModel);
        when(partProduct.getDeploymentType().getCode()).thenReturn("Saas");
        when(childEntry.getQuantity()).thenReturn(1L);
        when(partProduct.getCode()).thenReturn("testPartNumber");
        when(partProduct.getDescription()).thenReturn("testDescription");
        when(partProduct.getPartNumber()).thenReturn("testPID");
        when(partProduct.getPidProducts()).thenReturn(
            Collections.singletonList(mock(IbmVariantProductModel.class)));
        when(partProduct.getPidProducts().get(0).getDescription()).thenReturn("testPIDDescription");
        when(partProduct.getProductSetCode()).thenReturn(productSet);
        when(productSet.getCode()).thenReturn("testProductSetCode");
        ProvisionFormItemDetailsRequestData requestData = populator.createItemDetailsRequestData(
            childEntry);

        assertNotNull(requestData);
        assertEquals("testPartNumber", requestData.getPartNumber());
    }

    @Test
    public void testIsValidPartProduct_SaaS_True() {
        when(childEntry.getProduct()).thenReturn(partProduct);
        when(partProduct.getDeploymentType()).thenReturn(deploymentTypeModel);
        when(partProduct.getDeploymentType().getCode()).thenReturn("Saas");
        assertTrue(populator.isValidPartProduct(childEntry));
    }

    @Test
    public void testIsValidPartProduct_FeatureFlagFalse() {
        when(configuration.getBoolean(PartnercoreConstants.PROVISIONING_FORMS_FEATURE_FLAG, false)).thenReturn(false);
        when(childEntry.getProduct()).thenReturn(partProduct);
        assertFalse(populator.isValidPartProduct(childEntry));
    }

    @Test
    public void testIsValidPartProduct_NotIbmPartProductModel() {
        when(childEntry.getProduct()).thenReturn(mock(ProductModel.class)); // Not an IbmPartProductModel
        assertFalse(populator.isValidPartProduct(childEntry));
    }

    @Test
    public void testIsValidPartProduct_DeploymentTypeNotSaas() {
        when(childEntry.getProduct()).thenReturn(partProduct);
        when(partProduct.getDeploymentType()).thenReturn(deploymentTypeModel);
        when(deploymentTypeModel.getCode()).thenReturn("OnPrem");
        assertFalse(populator.isValidPartProduct(childEntry));
    }

    @Test
    public void testSetAdditionalProductDetails_ProductSetCodeNull() {
        ProvisionFormItemDetailsRequestData itemDetails = new ProvisionFormItemDetailsRequestData();
        when(partProduct.getPartNumber()).thenReturn("testPID");
        when(partProduct.getPidProducts()).thenReturn(Collections.emptyList());
        when(partProduct.getProductSetCode()).thenReturn(null);
        populator.setAdditionalProductDetails(partProduct, itemDetails, pidProduct);
        assertEquals("testPID", itemDetails.getPid());
        assertNull(itemDetails.getPidDescription());
        assertNull(itemDetails.getProductSetCode());
    }

    @Test
    public void testStoreProductSetCode_NullProductSetCode() {
        when(partProduct.getProductSetCode()).thenReturn(null);
        // Should not throw or set attribute
        populator.storeProductSetCode(partProduct);
        // No verification needed, just ensure no exception
    }

    @Test
    public void testGetSessionService() {
        assertEquals(sessionService, populator.getSessionService());
    }

    @Test
    public void testGetConfigurationService() {
        assertEquals(configurationService, populator.getConfigurationService());
    }

    @Test
    public void testCreateItemDetailsRequestData_InvalidPartProduct_ReturnsNull() {
        // Arrange: Make isValidPartProduct return false
        when(childEntry.getProduct()).thenReturn(mock(ProductModel.class)); // Not IbmPartProductModel
        // Act
        ProvisionFormItemDetailsRequestData result = populator.createItemDetailsRequestData(childEntry);
        // Assert
        assertNull(result);
    }

}