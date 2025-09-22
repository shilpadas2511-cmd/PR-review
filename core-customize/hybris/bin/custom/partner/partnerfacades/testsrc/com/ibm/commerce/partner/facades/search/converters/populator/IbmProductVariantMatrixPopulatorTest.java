package com.ibm.commerce.partner.facades.search.converters.populator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ibm.commerce.common.core.model.IbmDeploymentTypeModel;
import com.ibm.commerce.common.core.model.IbmVariantProductModel;
import com.ibm.commerce.partner.core.services.PartnerProductSearchAvailabilityService;
import com.ibm.commerce.partner.facades.product.data.IbmDeploymentTypeData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.VariantMatrixElementData;
import de.hybris.platform.commercefacades.product.data.VariantOptionData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.variants.model.VariantProductModel;
import de.hybris.platform.variants.model.VariantTypeModel;
import de.hybris.platform.variants.model.VariantValueCategoryModel;
import java.util.Collection;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

@RunWith(org.mockito.junit.MockitoJUnitRunner.class)
public class IbmProductVariantMatrixPopulatorTest {

    public static final String DEPLOYMENT_CODE = "SaaS";
    public static final String DEPLOYMENT_NAME = "SaaS";
    public static final String DEPLOYMENT_TAGCODE = "SaaSTagcode";
    public static final String DEPLOYMENT_CONFIG = "CONFIG001";
    @Mock
    private PartnerProductSearchAvailabilityService partnerProductSearchAvailabilityService;

    @Mock
    private IbmVariantProductModel variant1;

    @Mock
    private IbmVariantProductModel productModel;

    @Mock
    private ProductModel baseProduct;

    @Mock
    private VariantValueCategoryModel valueCategory;

    @Mock
    private IbmDeploymentTypeModel deploymentType;

    private ProductData productData;
    @Mock
    private VariantProductModel variantProductModel;

    @Mock
    private VariantTypeModel variantTypeModel;

    @Spy
    @InjectMocks
    private IbmProductVariantMatrixPopulator populator;

    private VariantMatrixElementData matrixElement;
    @Mock
    private Populator<VariantProductModel, VariantMatrixElementData> variantMatrixElementPopulator;
    public static final String PARENT_NODE_ELEMENTS_SHOULD_NOT_BE_NULL = "Parent node elements should not be null";
    public static final String THERE_SHOULD_BE_EXACTLY_ONE_CHILD_ELEMENT = "There should be exactly one child element";
    public static final String VARIANT_OPTION_SHOULD_NOT_BE_NULL = "VariantOption should not be null";
    public static final String VARIANT_CODE_SHOULD_MATCH = "Variant code should match";
    public static final String CONFIGURATOR_CODE_SHOULD_MATCH = "Configurator code should match";
    public static final String DEPLOYMENT_TYPE_DATA_SHOULD_NOT_BE_NULL = "DeploymentTypeData should not be null";
    public static final String DEPLOYMENT_TYPE_CODE_SHOULD_MATCH = "Deployment type code should match";
    public static final String DEPLOYMENT_TYPE_NAME_SHOULD_MATCH = "Deployment type name should match";
    private VariantMatrixElementData parentNode;
    private static String variantCode = "VARIANT-001";
    private static String configuratorCode = "CONFIG-123";
    private static String deploymentTypeCode = "DEPLOY-CODE";
    private static String deploymentTypeName = "Deployment Name";

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        productData = new ProductData();
        matrixElement = new VariantMatrixElementData();
        when(productModel.getBaseProduct()).thenReturn(baseProduct);
    }


    @Test
    public void testPopulate_withVariants_shouldSetMultidimensionalAndVariantMatrix() {
        when(partnerProductSearchAvailabilityService.getVariants(baseProduct))
            .thenReturn(Collections.singletonList(variant1));
        populator.populate(productModel, productData);
        assertTrue(productData.getMultidimensional());
        assertNotNull(productData.getVariantMatrix());
    }

    @Test
    public void testPopulate_withNoVariants_shouldSetMultidimensionalFalse() {
        when(partnerProductSearchAvailabilityService.getVariants(baseProduct)).thenReturn(
            Collections.emptyList());
        populator.populate(productModel, productData);
        assertFalse(productData.getMultidimensional());
        assertNull(productData.getVariantMatrix());
    }

    @Test
    public void testGetVariants_withNonIbmVariantProductModel_shouldReturnEmpty() {
        ProductModel genericProduct = mock(ProductModel.class);
        Collection<VariantProductModel> result = populator.getVariants(genericProduct);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testCreateDeploymentTypes_withNonIbmVariantProduct_shouldDoNothing() {
        VariantProductModel nonIbmVariant = mock(VariantProductModel.class);
        VariantMatrixElementData node = new VariantMatrixElementData();
        node.setVariantOption(new VariantOptionData());
        populator.createDeploymentTypes(nonIbmVariant, node);
        assertNull(node.getVariantOption().getConfiguratorCode());
        assertNull(node.getVariantOption().getDeploymentType());
    }

    @Test
    public void testCreateDeploymentTypeInformation_shouldSetDeploymentData() {
        VariantOptionData option = new VariantOptionData();
        IbmVariantProductModel mockVariant = mock(IbmVariantProductModel.class);
        IbmDeploymentTypeModel mockDeployment = mock(IbmDeploymentTypeModel.class);
        when(mockVariant.getDeploymentType()).thenReturn(mockDeployment);
        when(mockDeployment.getCode()).thenReturn("CODE");
        when(mockDeployment.getName()).thenReturn("NAME");
        when(mockDeployment.getTagCode()).thenReturn("TAG");
        populator.createDeploymentTypeInformation(option, mockVariant);
        IbmDeploymentTypeData data = option.getDeploymentType();
        assertNotNull(data);
        assertEquals("CODE", data.getCode());
        assertEquals("NAME", data.getName());
        assertEquals("TAG", data.getTagCode());
    }


    @Test
    public void testCreateDeploymentTypes_withIbmVariant_shouldSetConfiguratorAndDeployment() {
        IbmVariantProductModel variant = mock(IbmVariantProductModel.class);
        VariantMatrixElementData node = new VariantMatrixElementData();
        VariantOptionData optionData = new VariantOptionData();
        node.setVariantOption(optionData);
        IbmDeploymentTypeModel deploymentTypeModel = mock(IbmDeploymentTypeModel.class);
        when(variant.getConfiguratorCode()).thenReturn(DEPLOYMENT_CONFIG);
        when(variant.getDeploymentType()).thenReturn(deploymentTypeModel);
        when(deploymentTypeModel.getCode()).thenReturn(DEPLOYMENT_CODE);
        when(deploymentTypeModel.getName()).thenReturn(DEPLOYMENT_NAME);
        when(deploymentTypeModel.getTagCode()).thenReturn(DEPLOYMENT_TAGCODE);
        populator.createDeploymentTypes(variant, node);
        assertEquals(DEPLOYMENT_CONFIG, optionData.getConfiguratorCode());
        IbmDeploymentTypeData deployment = optionData.getDeploymentType();
        assertNotNull(deployment);
        assertEquals(DEPLOYMENT_CODE, deployment.getCode());
        assertEquals(DEPLOYMENT_NAME, deployment.getName());
        assertEquals(DEPLOYMENT_TAGCODE, deployment.getTagCode());
    }

    @Test
    public void testCreateNodesForVariant_ShouldPopulateConfiguratorCodeAndDeploymentType() {
        IbmVariantProductModel variantProductModel = mock(IbmVariantProductModel.class);
        IbmDeploymentTypeModel deploymentTypeModel = mock(IbmDeploymentTypeModel.class);
        VariantValueCategoryModel valueCategoryModel = mock(VariantValueCategoryModel.class);
        populator.setVariantMatrixElementPopulator(variantMatrixElementPopulator);
        parentNode = new VariantMatrixElementData();
        parentNode.setElements(new java.util.ArrayList<>());
        doAnswer(invocation -> {
            Object target = invocation.getArgument(1);
            if (target instanceof VariantMatrixElementData) {
                VariantMatrixElementData elementData = (VariantMatrixElementData) target;
                elementData.setVariantOption(new VariantOptionData());
            }
            return null;
        }).when(variantMatrixElementPopulator).populate(any(), any());
        when(variantProductModel.getCode()).thenReturn(variantCode);
        when(variantProductModel.getConfiguratorCode()).thenReturn(configuratorCode);
        when(variantProductModel.getSupercategories()).thenReturn(
            Collections.singletonList(valueCategoryModel));

        when(variantProductModel.getDeploymentType()).thenReturn(deploymentTypeModel);
        when(deploymentTypeModel.getCode()).thenReturn(deploymentTypeCode);
        when(deploymentTypeModel.getName()).thenReturn(deploymentTypeName);

        populator.createNodesForVariant(variantProductModel, parentNode);

        assertNotNull(PARENT_NODE_ELEMENTS_SHOULD_NOT_BE_NULL, parentNode.getElements());
        assertEquals(THERE_SHOULD_BE_EXACTLY_ONE_CHILD_ELEMENT, 1, parentNode.getElements().size());

        VariantMatrixElementData createdNode = parentNode.getElements().get(0);
        VariantOptionData variantOption = createdNode.getVariantOption();

        assertNotNull(VARIANT_OPTION_SHOULD_NOT_BE_NULL, variantOption);
        assertEquals(VARIANT_CODE_SHOULD_MATCH, variantCode, variantOption.getCode());
        assertEquals(CONFIGURATOR_CODE_SHOULD_MATCH, configuratorCode,
            variantOption.getConfiguratorCode());

        IbmDeploymentTypeData deploymentTypeData = (IbmDeploymentTypeData) variantOption.getDeploymentType();
        assertNotNull(DEPLOYMENT_TYPE_DATA_SHOULD_NOT_BE_NULL, deploymentTypeData);
        assertEquals(DEPLOYMENT_TYPE_CODE_SHOULD_MATCH, deploymentTypeCode,
            deploymentTypeData.getCode());
        assertEquals(DEPLOYMENT_TYPE_NAME_SHOULD_MATCH, deploymentTypeName,
            deploymentTypeData.getName());
    }

}
