package com.ibm.commerce.partner.core.search.solrfacetsearch.resolvers.impl;

import com.ibm.commerce.common.core.model.IbmPartProductModel;
import com.ibm.commerce.common.core.product.service.IbmProductService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractValueResolverTest;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class ProductCodeResolverTest extends AbstractValueResolverTest {

    private static final String PART_PRODUCT_CODE = "partProductCode";
    private static final String PRODUCT_CODE = "productCode";
    @InjectMocks
    ProductCodeResolver productCodeResolver;
    @Mock
    private InputDocument inputDocument;
    @Mock
    private IndexerBatchContext indexerBatchContext;
    @Mock
    private IndexedProperty indexedProperty;
    @Mock
    private Collection<IndexedProperty> indexedProperties;
    @Mock
    private SessionService sessionService;
    @Mock
    private JaloSession jaloSession;
    @Mock
    private Session session;
    @Mock
    ProductModel productModel;
    @Mock
    IbmProductService productService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        productCodeResolver = new ProductCodeResolver(productService);
        productCodeResolver.setSessionService(sessionService);
        productCodeResolver.setQualifierProvider(getQualifierProvider());
        indexedProperty = new IndexedProperty();
        indexedProperty.setName("INDEXED_PROPERTY_NAME");
        indexedProperty.setValueProviderParameters(new HashMap<>());
        indexedProperties = Collections.singletonList(indexedProperty);
        when(sessionService.getCurrentSession()).thenReturn(session);
        when(sessionService.getRawSession(Mockito.any())).thenReturn(jaloSession);
        Mockito.doNothing().when(jaloSession).removeLocalSessionContext();

    }

    @Test
    public void testAddFieldValues() throws FieldValueProviderException {
        List<IbmPartProductModel> partProducts = new ArrayList<>();
        IbmPartProductModel partProductModel = new IbmPartProductModel();
        partProductModel.setCode(PART_PRODUCT_CODE);
        partProducts.add(partProductModel);
        when(productModel.getCode()).thenReturn(PRODUCT_CODE);
        when(productService.getPartProducts(productModel)).thenReturn(partProducts);
        productCodeResolver.resolve(inputDocument, indexerBatchContext,
            indexedProperties, productModel);
        Assert.assertEquals(PRODUCT_CODE, productModel.getCode());
        Assert.assertEquals(PART_PRODUCT_CODE, partProductModel.getCode());
    }
}
