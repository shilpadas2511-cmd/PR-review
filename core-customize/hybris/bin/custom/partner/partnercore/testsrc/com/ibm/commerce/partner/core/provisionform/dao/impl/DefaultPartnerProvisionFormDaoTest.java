package com.ibm.commerce.partner.core.provisionform.dao.impl;

import static de.hybris.platform.testframework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;

import com.ibm.commerce.common.core.model.PartnerProductSetModel;
import com.ibm.commerce.partner.core.provisionform.Dao.impl.DefaultPartnerProvisionFormDao;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


import java.util.Collections;

@UnitTest
public class DefaultPartnerProvisionFormDaoTest {

    private DefaultPartnerProvisionFormDao defaultPartnerProvisionFormDao;
    private FlexibleSearchService flexibleSearchService;

    @Before
    public void setUp() {
        flexibleSearchService = Mockito.mock(FlexibleSearchService.class);
        defaultPartnerProvisionFormDao = new DefaultPartnerProvisionFormDao(flexibleSearchService);
    }

    @Test
    public void testGetProductSet_CodeNotNullAndFound() {
        String code = "testCode";
        PartnerProductSetModel partnerProductSetModel = new PartnerProductSetModel();
        partnerProductSetModel.setCode(code);
        SearchResult<PartnerProductSetModel> searchResult = Mockito.mock(SearchResult.class);
        Mockito.when(searchResult.getResult()).thenReturn(Collections.singletonList(partnerProductSetModel));
        PartnerProductSetModel result = defaultPartnerProvisionFormDao.getProductSet(code);
        assertNotNull(result);
        assertEquals(code, result.getCode());
    }

    @Test
    public void testGetProductSet_CodeNotNullAndNotFound() {
        String code = "nonExistingCode";
        SearchResult<PartnerProductSetModel> searchResult = Mockito.mock(SearchResult.class);
        Mockito.when(searchResult.getResult()).thenReturn(Collections.emptyList());
        PartnerProductSetModel result = defaultPartnerProvisionFormDao.getProductSet(code);
        assertNull(result);
    }

    @Test
    public void testGetProductSet_codeNull() {
        String code = null;
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            defaultPartnerProvisionFormDao.getProductSet(code);
        });
        assertEquals("code code must not be null", exception.getMessage());
    }
}