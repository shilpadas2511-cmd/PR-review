package com.ibm.commerce.partner.core.order.strategies.impl;

import com.ibm.commerce.partner.core.constants.GeneratedPartnercoreConstants.Enumerations;
import com.ibm.commerce.partner.core.enums.PartnerStatus;
import com.ibm.commerce.partner.core.jalo.ErrorDetails;
import com.ibm.commerce.partner.core.model.ErrorDetailsModel;
import com.ibm.commerce.partner.core.model.IbmPartnerCartEntryModel;
import com.ibm.commerce.partner.core.util.model.AbstractOrderEntryModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.AbstractOrderModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.CartModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.CommerceCartParameterTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.ProductConfigurationModelDataTestGenerator;
import com.ibm.commerce.partner.core.util.model.ProductModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.UserModelTestDataGenerator;
import com.ibm.commerce.partner.enums.data.DisplayTypeData;
import com.ibm.commerce.partner.error.ErrorDetailsData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.sap.productconfig.services.exceptions.ConfigurationNotFoundException;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationPersistenceService;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import groovyjarjarantlr4.v4.codegen.model.MatchSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import io.netty.util.internal.StringUtil;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
public class DefaultPartnerProductConfigurationStrategyTest {

    private static final String CONFIG_ID = "234";
    private static final String UID = "test@test.com";
    private static final String PRODUCT_CODE = "PRODTest01";

    private static final String CART_ID = "Test Cart 01";
    private static final String ERROR_MESSAGE = "ProductConfigurationModel with id='%s' not found";

    @InjectMocks
    DefaultPartnerProductConfigurationStrategy partnerProductConfigurationStrategy;
    @Mock
    ModelService modelService;
    @Mock
    ProductConfigurationPersistenceService persistenceService;
    @Mock
    KeyGenerator keyGenerator;

    AbstractOrderEntryModel orderEntry;
    CommerceCartParameter parameter;
    ProductConfigurationModel productConfigurationModel;

    @Mock
    ConfigurationService configurationService;

    @Before
    public void setUp() {
        final Configuration configuration = mock(Configuration.class);
        given(configuration.getBoolean(Mockito.any(), Mockito.any())).willReturn(true);

        given(configurationService.getConfiguration()).willReturn(configuration);
        MockitoAnnotations.initMocks(this);
        partnerProductConfigurationStrategy = new DefaultPartnerProductConfigurationStrategy(modelService,
            keyGenerator, persistenceService,configurationService);
        UserModel userModel = UserModelTestDataGenerator.createUserModel(UID);
        AbstractOrderModel orderModel = AbstractOrderModelTestDataGenerator.createAbstractOrderModel(userModel);
        ProductModel productModel = ProductModelTestDataGenerator.createProductModel(PRODUCT_CODE);
        orderEntry = AbstractOrderEntryModelTestDataGenerator.createAbstractOrderaEntry(orderModel, productModel);
        parameter = CommerceCartParameterTestDataGenerator.createCommerceCartParameter(CONFIG_ID, false);
        productConfigurationModel = ProductConfigurationModelDataTestGenerator.createProductConfigurationModel(CONFIG_ID);
        when(persistenceService.getByConfigId(parameter.getConfigId())).thenReturn(productConfigurationModel);
    }

    @Test
    public void testCreateAndProductConfigurationInEntry() {
        when(modelService.create(ProductConfigurationModel.class)).thenReturn(productConfigurationModel);
        when(persistenceService.getByConfigId(parameter.getConfigId())).thenThrow(new ConfigurationNotFoundException(String.format(ERROR_MESSAGE, CONFIG_ID)));
        partnerProductConfigurationStrategy.createAndAddProductConfigurationInEntry(orderEntry, parameter);
        assertNotNull(parameter);
    }

    @Test
    public void testCreateAndProductConfigurationInEntryParamterFalse() {
        when(modelService.create(ProductConfigurationModel.class)).thenReturn(productConfigurationModel);
        parameter.setPartProduct(Boolean.FALSE);
        partnerProductConfigurationStrategy.createAndAddProductConfigurationInEntry(orderEntry, parameter);
        Assert.assertFalse(parameter.isPartProduct());
    }
    @Test
    public void testCreateAndProductConfigurationInEntryParamter() {
        parameter.setConfigId(StringUtils.EMPTY);
        partnerProductConfigurationStrategy.createAndAddProductConfigurationInEntry(orderEntry, parameter);
        Assert.assertTrue(parameter.getConfigId().isEmpty());
    }
    @Test
    public void testCreateAndProductConfigurationInEntryConfigNotNull() {
        when(persistenceService.getByConfigId(parameter.getConfigId())).thenReturn(productConfigurationModel);
        partnerProductConfigurationStrategy.createAndAddProductConfigurationInEntry(orderEntry, parameter);
        assertNotNull(productConfigurationModel);
    }
    @Test
    public void testCreateAndProductConfigurationProductEmpty() {
        CartModel cartModel = CartModelTestDataGenerator.createCartModel(CART_ID);
        List<AbstractOrderEntryModel> orderEntryModels = new ArrayList<>();
        cartModel.setEntries(orderEntryModels);
        parameter.setCart(cartModel);
        AbstractOrderEntryModel abstractOrderEntryModel = new AbstractOrderEntryModel();
        ProductModel productModel = new ProductModel();
        productModel.setCode(StringUtils.EMPTY);
        abstractOrderEntryModel.setProduct(productModel);
        partnerProductConfigurationStrategy.findPidEntry(parameter);
        Assert.assertTrue(abstractOrderEntryModel.getProduct().getCode().isEmpty());
    }
    @Test
    public void testCreateAndProductConfigurationPidEmpty() {
        CartModel cartModel = CartModelTestDataGenerator.createCartModel(CART_ID);
        List<AbstractOrderEntryModel> orderEntryModels = new ArrayList<>();
        cartModel.setEntries(orderEntryModels);
        parameter.setCart(cartModel);
        parameter.setPidId(StringUtils.EMPTY);
        AbstractOrderEntryModel abstractOrderEntryModel = new AbstractOrderEntryModel();
        ProductModel productModel = new ProductModel();
        productModel.setCode(StringUtils.EMPTY);
        abstractOrderEntryModel.setProduct(productModel);
        partnerProductConfigurationStrategy.findPidEntry(parameter);
        Assert.assertTrue(parameter.getPidId().isEmpty());
    }

    @Test
    public void testSetQuoteCloneEntryStatus_ValidEntryStatus() {
        IbmPartnerCartEntryModel entryModel = new IbmPartnerCartEntryModel();
        CommerceCartParameter parameter = new CommerceCartParameter();
        PartnerStatus status = PartnerStatus.CLONE_INITIATED;
        DisplayTypeData entryStatus = new DisplayTypeData();
        entryStatus.setCode(status.getCode());
        parameter.setEntryStatus(entryStatus);
        partnerProductConfigurationStrategy.setQuoteCloneEntryStatus(entryModel, parameter);
        assertEquals(status, entryModel.getEntryStatus());
    }

    @Test
    public void testSetQuoteCloneEntryStatus_NullEntryStatus() {
        IbmPartnerCartEntryModel entryModel = new IbmPartnerCartEntryModel();
        CommerceCartParameter parameter = new CommerceCartParameter();
        parameter.setEntryStatus(null);
        partnerProductConfigurationStrategy.setQuoteCloneEntryStatus(entryModel, parameter);
        assertNull(entryModel.getEntryStatus());
    }


    @Test
    public void testSetQuoteCloneEntryStatus_WithErrorDetails() {
        IbmPartnerCartEntryModel entryModel = new IbmPartnerCartEntryModel();
        CommerceCartParameter parameter = new CommerceCartParameter();
        ErrorDetailsData errorDetails = new ErrorDetailsData();
        errorDetails.setDescription("An error occurred");
        parameter.setErrorDetails(errorDetails);

        ErrorDetailsModel errorDetailsModel = new ErrorDetailsModel();
        when(modelService.create(ErrorDetailsModel.class)).thenReturn(errorDetailsModel);
        when(keyGenerator.generate()).thenReturn(new Object()); // Assuming this generates a mock GuidKey

        partnerProductConfigurationStrategy.setQuoteCloneEntryStatus(entryModel, parameter);

        assertNotNull(entryModel.getErrorDetails());
        assertEquals("An error occurred", entryModel.getErrorDetails().getDescription());
        verify(modelService).create(ErrorDetailsModel.class);
        verify(keyGenerator).generate();
    }

    @Test
    public void testSetQuoteCloneEntryStatus_NullErrorDetails() {
        IbmPartnerCartEntryModel entryModel = new IbmPartnerCartEntryModel();
        CommerceCartParameter parameter = new CommerceCartParameter();
        parameter.setErrorDetails(null);

        partnerProductConfigurationStrategy.setQuoteCloneEntryStatus(entryModel, parameter);

        assertNull(entryModel.getErrorDetails());
    }

    @Test
    public void testGetGuidKeyGenerator() {
        assertEquals(keyGenerator, partnerProductConfigurationStrategy.getGuidKeyGenerator());
    }

    @Test
    public void testSetQuoteCloneEntryStatus_ErrorDetailsWithEmptyDescription() {
        IbmPartnerCartEntryModel entryModel = new IbmPartnerCartEntryModel();
        CommerceCartParameter parameter = new CommerceCartParameter();
        ErrorDetailsData errorDetails = new ErrorDetailsData();
        errorDetails.setDescription(""); // Empty description
        parameter.setErrorDetails(errorDetails);
        partnerProductConfigurationStrategy.setQuoteCloneEntryStatus(entryModel, parameter);
        assertNull(entryModel.getErrorDetails());
    }

    @Test
    public void testFindPidEntry_MatchOnProductCode() {
        // Setup cart and entries
        CartModel cartModel = CartModelTestDataGenerator.createCartModel(CART_ID);
        List<AbstractOrderEntryModel> orderEntryModels = new ArrayList<>();
        AbstractOrderEntryModel entry1 = new AbstractOrderEntryModel();
        entry1.setEntryNumber(1);
        ProductModel product1 = new ProductModel();
        product1.setCode("ABC123");
        entry1.setProduct(product1);
        orderEntryModels.add(entry1);
        cartModel.setEntries(orderEntryModels);
        parameter.setCart(cartModel);
        parameter.setEntryNumber(99); // Does not match entry1's entryNumber
        parameter.setPidId("abc123"); // Matches product code, case-insensitive
        // Should match on product code, not entry number
        Assert.assertTrue(partnerProductConfigurationStrategy.findPidEntry(parameter).isPresent());
        Assert.assertEquals(entry1, partnerProductConfigurationStrategy.findPidEntry(parameter).get());
    }

}
