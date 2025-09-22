package com.ibm.commerce.partner.core.search.solrfacetsearch.resolvers.impl;

import com.ibm.commerce.common.core.model.IbmDeploymentTypeModel;
import com.ibm.commerce.common.core.model.IbmPartProductModel;
import com.ibm.commerce.common.core.model.IbmVariantProductModel;
import com.ibm.commerce.common.core.product.service.IbmProductService;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.product.service.IbmDeploymentTypeService;
import com.ibm.commerce.partner.core.services.PartnerProductSearchAvailabilityService;
import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

public class PartnerProductSearchAvailabilityValueResolverTest {

    public static final String SAAS = "SAAS";
    private PartnerProductSearchAvailabilityService availabilityService;
    private IbmProductService productService;
    private PartnerProductSearchAvailabilityValueResolver resolver;

    private IndexedProperty indexedProperty;
    private InputDocument inputDocument;
    private IndexerBatchContext indexerBatchContext;
    private IbmVariantProductModel ibmProductModel;
    private IbmDeploymentTypeModel deploymentType;
    @Mock
    IbmDeploymentTypeService deploymentTypeService;
    @Before
    public void setUp() {
        availabilityService = mock(PartnerProductSearchAvailabilityService.class);
        productService = mock(IbmProductService.class);
        resolver = new PartnerProductSearchAvailabilityValueResolver(availabilityService, productService,deploymentTypeService);
        when(deploymentTypeService.isDeploymentTypeSupported(Mockito.anyString())).thenReturn(Boolean.TRUE);


        indexedProperty = mock(IndexedProperty.class);
        inputDocument = mock(InputDocument.class);
        indexerBatchContext = mock(IndexerBatchContext.class);
        ibmProductModel = mock(IbmVariantProductModel.class);

        deploymentType = mock(IbmDeploymentTypeModel.class);
        when(deploymentType.getCode()).thenReturn(SAAS);
        when(ibmProductModel.getDeploymentType()).thenReturn(deploymentType);
    }

    @Test
    public void testAddFieldValues_WithApprovedProductAndPartProduct_ReturnsTrue() throws Exception {
        IbmPartProductModel partProduct = mock(IbmPartProductModel.class);
        when(partProduct.getApprovalStatus()).thenReturn(ArticleApprovalStatus.APPROVED);
        List<IbmPartProductModel> partProducts = Arrays.asList(partProduct);

        when(productService.getPartProducts(ibmProductModel)).thenReturn(partProducts);
        when(ibmProductModel.getApprovalStatus()).thenReturn(ArticleApprovalStatus.APPROVED);
        when(availabilityService.isSearchAvailabilityForProduct(ibmProductModel,
            PartnercoreConstants.DEPLOYMENT_TYPE_BESPOKE)).thenReturn(true);

        resolver.addFieldValues(inputDocument, indexerBatchContext, indexedProperty, ibmProductModel, null);

        verify(inputDocument).addField(indexedProperty, true);
    }

    @Test
    public void testAddFieldValues_WithApprovedProduct_ApprovedParts_NotAvailable_ReturnsFalse() throws Exception {
        IbmPartProductModel partProduct = mock(IbmPartProductModel.class);
        when(partProduct.getApprovalStatus()).thenReturn(ArticleApprovalStatus.APPROVED);
        List<IbmPartProductModel> partProducts = Arrays.asList(partProduct);

        when(productService.getPartProducts(ibmProductModel)).thenReturn(partProducts);
        when(ibmProductModel.getApprovalStatus()).thenReturn(ArticleApprovalStatus.APPROVED);
        when(availabilityService.isSearchAvailabilityForProduct(ibmProductModel,
            PartnercoreConstants.DEPLOYMENT_TYPE_BESPOKE)).thenReturn(false);

        resolver.addFieldValues(inputDocument, indexerBatchContext, indexedProperty, ibmProductModel, null);

        verify(inputDocument).addField(indexedProperty, false);
    }

    @Test
    public void testAddFieldValues_WithUnapprovedProduct_ReturnsFalse() throws Exception {
        when(productService.getPartProducts(ibmProductModel)).thenReturn(Collections.emptyList());
        when(ibmProductModel.getApprovalStatus()).thenReturn(ArticleApprovalStatus.UNAPPROVED);

        resolver.addFieldValues(inputDocument, indexerBatchContext, indexedProperty, ibmProductModel, null);

        verify(inputDocument).addField(indexedProperty, false);
        verify(availabilityService, never()).isSearchAvailabilityForProduct(any(),
            PartnercoreConstants.DEPLOYMENT_TYPE_BESPOKE);
    }
}
