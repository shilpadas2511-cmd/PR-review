package com.ibm.commerce.partner.core.search.solrfacetsearch.resolvers.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ibm.commerce.common.core.model.IbmDeploymentTypeModel;
import com.ibm.commerce.common.core.product.service.IbmProductService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractValueResolverTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class ProductDeploymentTypeValueResolverTest extends AbstractValueResolverTest {

    private static final String DEPLOYMENT_CODE = "deploymentCode";

    @InjectMocks
    ProductDeploymentTypeValueResolver productDeploymentTypeValueResolver;
    @Mock
    private InputDocument inputDocument;
    @Mock
    private IndexerBatchContext indexerBatchContext;
    @Mock
    private IndexedProperty indexedProperty;
    @Mock
    private ProductModel productModel;
    @Mock
    IbmDeploymentTypeModel deploymentType;
    @Mock
    IbmProductService productService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        productDeploymentTypeValueResolver = new ProductDeploymentTypeValueResolver(productService);
    }

    @Test
    public void testAddFieldValues_shouldAddField_whenDeploymentTypeIsNotNull() throws FieldValueProviderException {

        when(productService.getDeploymentType(productModel)).thenReturn(deploymentType);
        when(deploymentType.getCode()).thenReturn(DEPLOYMENT_CODE);

        productDeploymentTypeValueResolver.addFieldValues(inputDocument, indexerBatchContext, indexedProperty, productModel,null);
        verify(inputDocument).addField(indexedProperty, DEPLOYMENT_CODE);
    }

    @Test
    public void testAddFieldValues_shouldNotAddField_whenDeploymentTypeIsNull() throws FieldValueProviderException {
        when(productService.getDeploymentType(productModel)).thenReturn(null);

        productDeploymentTypeValueResolver.addFieldValues(inputDocument, indexerBatchContext, indexedProperty, productModel,null);
        verify(inputDocument, never()).addField((String) any(), any());
    }

}
