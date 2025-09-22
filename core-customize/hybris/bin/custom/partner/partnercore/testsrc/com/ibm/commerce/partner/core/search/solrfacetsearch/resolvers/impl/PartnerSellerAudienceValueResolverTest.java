package com.ibm.commerce.partner.core.search.solrfacetsearch.resolvers.impl;

import com.ibm.commerce.common.core.model.IbmPartProductModel;
import com.ibm.commerce.common.core.model.IbmVariantProductModel;
import com.ibm.commerce.common.core.model.SellerAudienceMaskModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractValueResolverTest;
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

/**
 * test class for setting part product's seller audience mask into pid product.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class PartnerSellerAudienceValueResolverTest extends AbstractValueResolverTest {

    private final static String AUDIENCE_CODE_RES = "RES";
    @InjectMocks
    private PartnerSellerAudienceValueResolver partnerSellerAudienceValueResolver;
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
    IbmVariantProductModel variantProductModel;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        indexedProperty = new IndexedProperty();
        indexedProperty.setName(INDEXED_PROPERTY_NAME);
        indexedProperty.setValueProviderParameters(new HashMap<>());
        indexedProperties = Collections.singletonList(indexedProperty);
        when(sessionService.getCurrentSession()).thenReturn(session);
        when(sessionService.getRawSession(Mockito.any())).thenReturn(jaloSession);
        Mockito.doNothing().when(jaloSession).removeLocalSessionContext();
        partnerSellerAudienceValueResolver.setSessionService(sessionService);
        variantProductModel = new IbmVariantProductModel();
    }

    /**
     * test method for setting seller audience mask of PartProduct to pidProduct.
     *
     * @throws FieldValueProviderException
     */
    @Test
    public void testAddFieldValues() throws FieldValueProviderException {
        IbmPartProductModel partProduct = new IbmPartProductModel();
        partProduct.setCode("partProduct1");
        SellerAudienceMaskModel sellerAudienceMask = new SellerAudienceMaskModel();
        sellerAudienceMask.setCode(AUDIENCE_CODE_RES);
        partProduct.setSellerAudienceMasks(Collections.singletonList(sellerAudienceMask));
        variantProductModel.setPartProducts(Collections.singletonList(partProduct));
        partnerSellerAudienceValueResolver.resolve(inputDocument, indexerBatchContext,
                indexedProperties, variantProductModel);
        Assert.assertEquals(AUDIENCE_CODE_RES,
                variantProductModel.getPartProducts().get(0).getSellerAudienceMasks().get(0)
                        .getCode());
    }

    /**
     * test method for setting seller audience mask when pid does not have any part Product
     *
     * @throws FieldValueProviderException
     */
    @Test
    public void testAddFieldValuesWithNoPartProduct() throws FieldValueProviderException {
        partnerSellerAudienceValueResolver.resolve(inputDocument, indexerBatchContext,
                indexedProperties, variantProductModel);
        Assert.assertNull(variantProductModel.getPartProducts());
    }

    /**
     * test method for setting seller audience when part product does not have audience masks.
     *
     * @throws FieldValueProviderException
     */
    @Test
    public void testAddFieldValuesWithNoAudienceMasks() throws FieldValueProviderException {
        IbmPartProductModel partProduct = new IbmPartProductModel();
        partProduct.setCode("partProduct1");
        variantProductModel.setPartProducts(Collections.singletonList(partProduct));
        partnerSellerAudienceValueResolver.resolve(inputDocument, indexerBatchContext,
                indexedProperties, variantProductModel);
        Assert.assertNotNull(variantProductModel.getPartProducts());
        Assert.assertNull(variantProductModel.getPartProducts().get(0).getSellerAudienceMasks());
    }
}
