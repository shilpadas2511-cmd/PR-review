package com.ibm.commerce.partner.core.partnerSalesOrg.service.impl;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import com.ibm.commerce.partner.core.model.PartnerSalesOrganisationModel;
import com.ibm.commerce.partner.core.partnerSalesOrg.dao.PartnerSalesOrgDao;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit test class for {@link DefaultPartnerSalesOrgService}.
 */
public class DefaultPartnerSalesOrgServiceTest {

    @InjectMocks
    private DefaultPartnerSalesOrgService partnerSalesOrgService;

    @Mock
    private PartnerSalesOrgDao partnerSalesOrgDao;

    private static final String SALES_ORG_CODE_1 = "ORG001";
    private static final String SALES_ORG_CODE_2 = "ORG002";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        partnerSalesOrgService = new DefaultPartnerSalesOrgService(partnerSalesOrgDao);
    }

    /**
     * Test case for getSalesOrgsByCodes with valid codes.
     */
    @Test
    public void testGetSalesOrgsByCodes() {
        PartnerSalesOrganisationModel model1 = mock(PartnerSalesOrganisationModel.class);
        PartnerSalesOrganisationModel model2 = mock(PartnerSalesOrganisationModel.class);

        List<PartnerSalesOrganisationModel> mockResult = Arrays.asList(model1, model2);
        doReturn(mockResult).when(partnerSalesOrgDao).getSalesOrgsByCodes(Arrays.asList(SALES_ORG_CODE_1, SALES_ORG_CODE_2));

        List<PartnerSalesOrganisationModel> result = partnerSalesOrgService.getSalesOrgsByCodes(Arrays.asList(SALES_ORG_CODE_1, SALES_ORG_CODE_2));

        Assert.assertEquals(2, result.size());
        Assert.assertEquals(mockResult, result);
    }

    /**
     * Test case for getSalesOrgsByCodes with empty list.
     */
    @Test
    public void testGetSalesOrgsByCodes_EmptyInput() {
        doReturn(Collections.emptyList()).when(partnerSalesOrgDao).getSalesOrgsByCodes(Collections.emptyList());

        List<PartnerSalesOrganisationModel> result = partnerSalesOrgService.getSalesOrgsByCodes(Collections.emptyList());

        Assert.assertTrue(result.isEmpty());
    }

    /**
     * Test case for getSalesOrgsByCodes with null input.
     */
    @Test
    public void testGetSalesOrgsByCodes_NullInput() {
        doReturn(Collections.emptyList()).when(partnerSalesOrgDao).getSalesOrgsByCodes(null);

        List<PartnerSalesOrganisationModel> result = partnerSalesOrgService.getSalesOrgsByCodes(null);

        Assert.assertTrue(result.isEmpty());
    }
}
