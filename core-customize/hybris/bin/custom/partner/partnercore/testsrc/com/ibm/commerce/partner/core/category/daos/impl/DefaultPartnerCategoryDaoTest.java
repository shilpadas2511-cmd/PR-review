package com.ibm.commerce.partner.core.category.daos.impl;

import com.ibm.commerce.common.core.model.IbmCategoryModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@UnitTest
public class DefaultPartnerCategoryDaoTest {

    @InjectMocks
    DefaultPartnerCategoryDao partnerCategoryDao;
    @Mock
    CatalogVersionModel catalogVersion;
    private static final String CODE = "123";
    @Mock
    FlexibleSearchService flexibleSearchService;

    @Mock
    private SearchResult result;
    @Mock
    IbmCategoryModel ibmCategoryModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        partnerCategoryDao = new DefaultPartnerCategoryDao();
        partnerCategoryDao.setFlexibleSearchService(flexibleSearchService);
    }

    @Test
    public void testFindCategoriesByCpqId() {
        Mockito.when(flexibleSearchService.search(anyString(), anyMap()))
            .thenReturn(result);
        Mockito.when(result.getResult()).thenReturn(Arrays.asList(ibmCategoryModel));
        IbmCategoryModel result = partnerCategoryDao.findCategoriesByCpqId(catalogVersion, CODE);
        Assert.assertNotNull(result);
    }

    @Test
    public void testWhenResultIsNull() {
        Mockito.when(flexibleSearchService.search(anyString(), anyMap()))
            .thenReturn(null);
        IbmCategoryModel actualCategory = partnerCategoryDao.findCategoriesByCpqId(catalogVersion,
            CODE);
        Assert.assertNull(actualCategory);
    }

    @Test
    public void testFindCategoriesByCpqIdWithNullResult() {
        Mockito.when(flexibleSearchService.search(anyString(), anyMap()))
            .thenReturn(result);
        Mockito.when(result.getResult()).thenReturn(null);
        IbmCategoryModel result = partnerCategoryDao.findCategoriesByCpqId(catalogVersion, CODE);
        Assert.assertNull(result);
    }

}
