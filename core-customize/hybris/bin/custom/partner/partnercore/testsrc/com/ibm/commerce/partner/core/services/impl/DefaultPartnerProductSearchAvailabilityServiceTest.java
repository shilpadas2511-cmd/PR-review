package com.ibm.commerce.partner.core.services.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.ibm.commerce.common.core.model.*;
import com.ibm.commerce.common.core.product.service.IbmProductService;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.product.service.IbmDeploymentTypeService;
import com.ibm.commerce.partner.core.product.service.IbmPartnerProductService;
import com.ibm.commerce.partner.core.utils.PartnerProductDeploymentTypeUtils;
import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.variants.model.VariantProductModel;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultPartnerProductSearchAvailabilityServiceTest {

    private static final String DEFAULT_DEPLOYMENT_TYPES = "SaaS,Bespoke";
    public static final String BESPOKE = "Bespoke";
    public static final String SAA_S = "SaaS";
    public static final String NON_RES = "NON_RES";

    @Mock
    private IbmPartnerProductService productService;
    @Mock
    private IbmVariantProductModel variant;
    @Mock
    private IbmVariantProductModel baseVariant;
    @Mock
    private ProductModel baseProduct;
    @Mock
    private IbmDeploymentTypeModel saasDeploymentType;
    @Mock
    private IbmDeploymentTypeModel bespokeDeploymentType;
    @Mock
    private IbmPartProductModel approvedPartProduct;
    @Mock
    private IbmPartProductModel unapprovedPartProduct;
    @Mock
    private SellerAudienceMaskModel resAudience;
    @Mock
    private SellerAudienceMaskModel nonResAudience;

    private DefaultPartnerProductSearchAvailabilityService service;
    private MockedStatic<PartnerProductDeploymentTypeUtils> mockedDeploymentUtils;
    private static final String BASE_PRODUCT_CODE = "code";

    @Mock
    IbmDeploymentTypeService deploymentTypeService;

    @Before
    public void setUp() {
        service = new DefaultPartnerProductSearchAvailabilityService(productService,
            deploymentTypeService);
        mockedDeploymentUtils = mockStatic(PartnerProductDeploymentTypeUtils.class);
        when(deploymentTypeService.isDeploymentTypeSupported(Mockito.anyString())).thenReturn(Boolean.TRUE);
    }

    @After
    public void tearDown() {
        mockedDeploymentUtils.close();
    }

    @Test
    public void testIsSearchAvailabilityForProduct_withBespokeVariant_returnsFalse() {
        when(variant.getCatalogVersion()).thenReturn(null);
        when(variant.getBaseProduct()).thenReturn(baseVariant);

        when(baseVariant.getCode()).thenReturn(BASE_PRODUCT_CODE);
        when(productService.getProductForCode(null, BASE_PRODUCT_CODE)).thenReturn(baseProduct);

        IbmVariantProductModel bespoke = mock(IbmVariantProductModel.class);
        IbmVariantProductModel saas = mock(IbmVariantProductModel.class);

        when(baseProduct.getVariants()).thenReturn(Arrays.asList(bespoke, saas));

        mockedDeploymentUtils.when(
            () -> PartnerProductDeploymentTypeUtils.isBespokeProduct(bespoke)).thenReturn(true);
        mockedDeploymentUtils.when(() -> PartnerProductDeploymentTypeUtils.isBespokeProduct(saas))
            .thenReturn(false);

        boolean result = service.isSearchAvailabilityForProduct(variant,
            PartnercoreConstants.DEPLOYMENT_TYPE_BESPOKE);
        assertFalse(result);
    }

    @Test
    public void testIsSearchAvailabilityForProduct_withoutBespokeVariant_returnsTrue() {
        when(variant.getCatalogVersion()).thenReturn(null);
        when(variant.getBaseProduct()).thenReturn(baseVariant);
        when(baseVariant.getCode()).thenReturn(BASE_PRODUCT_CODE);
        when(productService.getProductForCode(null, BASE_PRODUCT_CODE)).thenReturn(baseProduct);

        IbmVariantProductModel saas = mock(IbmVariantProductModel.class);
        when(baseProduct.getVariants()).thenReturn(Collections.singletonList(saas));

        mockedDeploymentUtils.when(() -> PartnerProductDeploymentTypeUtils.isBespokeProduct(saas))
            .thenReturn(false);

        boolean result = service.isSearchAvailabilityForProduct(variant,
            PartnercoreConstants.DEPLOYMENT_TYPE_BESPOKE);
        assertTrue(result);
    }

    @Test
    public void testGetVariants_withBespoke_removesSaas() {
        ProductModel productModel = mock(ProductModel.class);

        IbmVariantProductModel bespoke = mock(IbmVariantProductModel.class);
        IbmVariantProductModel saas = mock(IbmVariantProductModel.class);

        IbmDeploymentTypeModel bespokeType = mock(IbmDeploymentTypeModel.class);
        IbmDeploymentTypeModel saasType = mock(IbmDeploymentTypeModel.class);

        when(bespoke.getDeploymentType()).thenReturn(bespokeType);
        when(bespokeType.getCode()).thenReturn(BESPOKE);

        when(saas.getDeploymentType()).thenReturn(saasType);
        when(saasType.getCode()).thenReturn(SAA_S);

        when(productModel.getVariants()).thenReturn(Arrays.asList(bespoke, saas));

        mockedDeploymentUtils.when(
            () -> PartnerProductDeploymentTypeUtils.isBespokeProduct(bespoke)).thenReturn(true);
        mockedDeploymentUtils.when(() -> PartnerProductDeploymentTypeUtils.isSaasProduct(saas))
            .thenReturn(true);

        when(bespoke.getApprovalStatus()).thenReturn(ArticleApprovalStatus.APPROVED);

        IbmPartProductModel partProduct = mock(IbmPartProductModel.class);
        SellerAudienceMaskModel audience = mock(SellerAudienceMaskModel.class);
        when(partProduct.getApprovalStatus()).thenReturn(ArticleApprovalStatus.APPROVED);
        when(audience.getCode()).thenReturn(PartnercoreConstants.RES);
        when(partProduct.getSellerAudienceMasks()).thenReturn(Collections.singletonList(audience));
        when(productService.getPartProducts(bespoke)).thenReturn(
            Collections.singletonList(partProduct));
        Collection<VariantProductModel> result = service.getVariants(productModel);
        assertEquals(1, result.size());
        assertTrue(result.contains(bespoke));
        assertFalse(result.contains(saas));
    }


    @Test
    public void testFilterAvailableProducts_onlyReturnsApprovedWithResAudience() {
        IbmVariantProductModel approvedVariant = mock(IbmVariantProductModel.class);
        IbmVariantProductModel unapprovedVariant = mock(IbmVariantProductModel.class);
        when(approvedVariant.getApprovalStatus()).thenReturn(ArticleApprovalStatus.APPROVED);
        when(unapprovedVariant.getApprovalStatus()).thenReturn(ArticleApprovalStatus.UNAPPROVED);
        when(approvedPartProduct.getApprovalStatus()).thenReturn(ArticleApprovalStatus.APPROVED);
        when(resAudience.getCode()).thenReturn(PartnercoreConstants.RES);
        when(approvedPartProduct.getSellerAudienceMasks()).thenReturn(
            Collections.singletonList(resAudience));
        when(productService.getPartProducts(approvedVariant)).thenReturn(
            Collections.singletonList(approvedPartProduct));

        List<VariantProductModel> variants = Arrays.asList(approvedVariant, unapprovedVariant);
        Collection<VariantProductModel> result = service.filterAvailableProducts(variants);
        assertEquals(1, result.size());
        assertTrue(result.contains(approvedVariant));
        assertFalse(result.contains(unapprovedVariant));
    }

    @Test
    public void testFilterAvailableProducts_returnsEmptyIfNoApprovedParts() {
        IbmVariantProductModel variant = mock(IbmVariantProductModel.class);
        when(variant.getApprovalStatus()).thenReturn(ArticleApprovalStatus.APPROVED);
        when(productService.getPartProducts(variant)).thenReturn(Collections.emptyList());
        List<VariantProductModel> variants = Collections.singletonList(variant);
        Collection<VariantProductModel> result = service.filterAvailableProducts(variants);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testFilterAvailableProducts_returnsEmptyIfNoResMask() {
        IbmVariantProductModel variant = mock(IbmVariantProductModel.class);
        when(variant.getApprovalStatus()).thenReturn(ArticleApprovalStatus.APPROVED);
        when(unapprovedPartProduct.getApprovalStatus()).thenReturn(ArticleApprovalStatus.APPROVED);
        when(nonResAudience.getCode()).thenReturn(NON_RES);
        when(unapprovedPartProduct.getSellerAudienceMasks()).thenReturn(
            Collections.singletonList(nonResAudience));
        when(productService.getPartProducts(variant)).thenReturn(
            Collections.singletonList(unapprovedPartProduct));

        List<VariantProductModel> variants = Collections.singletonList(variant);
        Collection<VariantProductModel> result = service.filterAvailableProducts(variants);

        assertTrue(result.isEmpty());
    }
}
