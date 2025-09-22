package com.ibm.commerce.partner.core.search.solrfacetsearch.resolvers.impl;

import static org.mockito.Mockito.when;

import com.ibm.commerce.common.core.model.IbmDeploymentTypeModel;
import com.ibm.commerce.partner.core.product.service.IbmDeploymentTypeService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractValueResolverTest;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class ProductDeploymentTypeDisplayNameResolverTest extends AbstractValueResolverTest {

    private static final String FACET = "facetValue";
    private static final String DEPLOYMENT_NAME = "deploymentName";
    @InjectMocks
    ProductDeploymentTypeDisplayNameResolver productDeploymentTypeDisplayNameResolver;
    @Mock
    IbmDeploymentTypeService deploymentTypeService;
    @Mock
    SearchQuery query;
    @Mock
    private IndexedProperty indexedProperty;
    @Mock
    IbmDeploymentTypeModel deploymentValue;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        productDeploymentTypeDisplayNameResolver = new ProductDeploymentTypeDisplayNameResolver(
            deploymentTypeService);
    }

    @Test
    public void testGetDisplayName() {
        when(query.getLanguage()).thenReturn("en");
        when(deploymentTypeService.getDeploymentTypeForFacet(FACET)).thenReturn(deploymentValue);
        Locale LOCALE = new Locale("en");
        when(deploymentValue.getName(LOCALE)).thenReturn(DEPLOYMENT_NAME);
        String name = productDeploymentTypeDisplayNameResolver.getDisplayName(query,
            indexedProperty, FACET);
        Assert.assertEquals(DEPLOYMENT_NAME, name);

    }
}
