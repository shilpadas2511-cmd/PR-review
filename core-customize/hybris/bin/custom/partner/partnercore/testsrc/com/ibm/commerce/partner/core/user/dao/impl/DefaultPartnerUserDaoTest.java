package com.ibm.commerce.partner.core.user.dao.impl;

import com.ibm.commerce.partner.core.model.PartnerB2BCustomerModel;
import com.ibm.commerce.partner.core.model.PartnerEmployeeModel;
import com.ibm.commerce.partner.core.util.model.PartnerB2BCustomerModelTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@UnitTest
public class DefaultPartnerUserDaoTest {

    private static final String CUSTOMER_NAME1 = "Martin";
    private static final String CUSTOMER_NAME2 = "John";
    private static final String CUSTOMER_UID1 = "0007000379";
    private static final String CUSTOMER_UID2 = "0007121464";
    private static final String CUSTOMER_EMAIL = "martin@example.com";

    @InjectMocks
    DefaultPartnerUserDao defaultPartnerUserDao;

    @Mock
    FlexibleSearchService flexibleSearchService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        defaultPartnerUserDao = new DefaultPartnerUserDao();
        defaultPartnerUserDao.setFlexibleSearchService(flexibleSearchService);
    }

    @Test
    public void testGetActivePartnerB2BCustomers() {
        SearchResult<PartnerB2BCustomerModel> searchResult = Mockito.mock(SearchResult.class);
        PartnerB2BCustomerModel partnerB2BCustomerModel = PartnerB2BCustomerModelTestDataGenerator
            .createCustomerModel(CUSTOMER_NAME1, CUSTOMER_UID1, null, true);
        PartnerB2BCustomerModel partnerB2BCustomerModel1 = PartnerB2BCustomerModelTestDataGenerator
            .createCustomerModel(CUSTOMER_NAME2, CUSTOMER_UID2, null, true);

        Mockito.doReturn(searchResult).when(flexibleSearchService)
            .search(Mockito.any(FlexibleSearchQuery.class));
        Mockito.doReturn(Arrays.asList(partnerB2BCustomerModel, partnerB2BCustomerModel1))
            .when(searchResult).getResult();

        List<PartnerB2BCustomerModel> results = defaultPartnerUserDao.getActivePartnerB2BCustomers();

        Assert.assertEquals(2, results.size());
        Assert.assertEquals(CUSTOMER_UID1, results.get(0).getUid());
    }

    @Test
    public void testGetActivePartnerB2BCustomers_ResultsAsEmpty() {
        SearchResult<PartnerB2BCustomerModel> searchResult = Mockito.mock(SearchResult.class);

        Mockito.doReturn(searchResult).when(flexibleSearchService)
            .search(Mockito.any(FlexibleSearchQuery.class));
        Mockito.doReturn(Collections.emptyList()).when(searchResult).getResult();

        List<PartnerB2BCustomerModel> results = defaultPartnerUserDao.getActivePartnerB2BCustomers();

        Assert.assertTrue(results.isEmpty());
    }

    @Test
    public void testGetAllPartnerEmployees() {
        SearchResult<PartnerEmployeeModel> searchResult = Mockito.mock(SearchResult.class);
        PartnerEmployeeModel partnerEmployeeModel1 = new PartnerEmployeeModel();
        partnerEmployeeModel1.setUid(CUSTOMER_UID1);
        PartnerEmployeeModel partnerEmployeeModel2 = new PartnerEmployeeModel();

        Mockito.doReturn(searchResult).when(flexibleSearchService)
            .search(Mockito.any(FlexibleSearchQuery.class));
        Mockito.doReturn(Arrays.asList(partnerEmployeeModel1, partnerEmployeeModel2))
            .when(searchResult).getResult();

        List<PartnerEmployeeModel> results = defaultPartnerUserDao.getAllPartnerEmployee();

        Assert.assertEquals(2, results.size());
        Assert.assertEquals(CUSTOMER_UID1, results.get(0).getUid());
    }

    @Test
    public void testGetAllPartnerEmployees_Null() {
        SearchResult<PartnerEmployeeModel> searchResult = Mockito.mock(SearchResult.class);

        Mockito.doReturn(searchResult).when(flexibleSearchService)
            .search(Mockito.any(FlexibleSearchQuery.class));
        Mockito.doReturn(Collections.emptyList()).when(searchResult).getResult();

        List<PartnerEmployeeModel> results = defaultPartnerUserDao.getAllPartnerEmployee();

        Assert.assertTrue(results.isEmpty());
    }

    @Test
    public void testGetCustomerByEmail_Found() {
        SearchResult<PartnerB2BCustomerModel> searchResult = Mockito.mock(SearchResult.class);

        PartnerB2BCustomerModel customer = PartnerB2BCustomerModelTestDataGenerator
            .createCustomerModel(CUSTOMER_NAME1, CUSTOMER_UID1, null, true);
        customer.setEmail(CUSTOMER_EMAIL);

        Mockito.doReturn(searchResult).when(flexibleSearchService)
            .search(Mockito.any(FlexibleSearchQuery.class));
        Mockito.doReturn(Arrays.asList(customer)).when(searchResult).getResult();

        PartnerB2BCustomerModel result =
            (PartnerB2BCustomerModel) defaultPartnerUserDao.getCustomerByEmail(CUSTOMER_EMAIL);

        Assert.assertNotNull(result);
        Assert.assertEquals(CUSTOMER_EMAIL, result.getEmail());
    }

    @Test
    public void testGetCustomerByEmail_NotFound() {
        SearchResult<PartnerB2BCustomerModel> searchResult = Mockito.mock(SearchResult.class);

        Mockito.doReturn(searchResult).when(flexibleSearchService)
            .search(Mockito.any(FlexibleSearchQuery.class));
        Mockito.doReturn(Collections.emptyList()).when(searchResult).getResult();

        PartnerB2BCustomerModel result = (PartnerB2BCustomerModel) defaultPartnerUserDao.getCustomerByEmail(
            "nonexistent@example.com");

        Assert.assertNull(result);
    }
}
