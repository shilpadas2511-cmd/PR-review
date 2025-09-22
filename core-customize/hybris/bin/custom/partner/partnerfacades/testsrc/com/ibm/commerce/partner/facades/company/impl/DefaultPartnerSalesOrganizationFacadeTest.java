package com.ibm.commerce.partner.facades.company.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.ibm.commerce.partner.core.model.PartnerSalesOrganisationModel;
import com.ibm.commerce.partner.core.partnerSalesOrg.service.PartnerSalesOrgService;
import com.ibm.commerce.partnerwebservicescommons.company.dto.IbmPartnerSalesOrganisationData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

public class DefaultPartnerSalesOrganizationFacadeTest {

    private static final String CODE_NEW = "NEW";
    private static final String CODE_EXISTING = "EXISTING";

    @InjectMocks
    private DefaultPartnerSalesOrganizationFacade facade;

    @Mock
    private PartnerSalesOrgService salesOrgService;

    @Mock
    private Converter<IbmPartnerSalesOrganisationData, PartnerSalesOrganisationModel> reverseConverter;

    @Mock
    private ModelService modelService;

    private IbmPartnerSalesOrganisationData existingSalesOrgData;
    private IbmPartnerSalesOrganisationData newSalesOrgData;
    private PartnerSalesOrganisationModel existingModel;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        facade = new DefaultPartnerSalesOrganizationFacade(salesOrgService, reverseConverter,
            modelService);

        // Existing Sales Org
        existingSalesOrgData = new IbmPartnerSalesOrganisationData();
        existingSalesOrgData.setCode(CODE_EXISTING);

        existingModel = new PartnerSalesOrganisationModel();
        existingModel.setCode(CODE_EXISTING);

        // New Sales Org
        newSalesOrgData = new IbmPartnerSalesOrganisationData();
        newSalesOrgData.setCode(CODE_NEW);
    }

    @Test
    public void testGetOrCreateSalesOrgs_AllNew() {
        List<IbmPartnerSalesOrganisationData> input = List.of(newSalesOrgData);
        when(salesOrgService.getSalesOrgsByCodes(any())).thenReturn(Collections.emptyList());

        PartnerSalesOrganisationModel newModel = new PartnerSalesOrganisationModel();
        newModel.setCode(CODE_NEW);
        when(reverseConverter.convertAll(any())).thenReturn(List.of(newModel));

        Set<PartnerSalesOrganisationModel> result = facade.getOrCreateSalesOrgs(input);

        Assert.assertEquals(1, result.size());
        Assert.assertTrue(result.stream().anyMatch(m -> CODE_NEW.equals(m.getCode())));
    }


    @Test
    public void testGetOrCreateSalesOrgs_Mixed() {
        List<IbmPartnerSalesOrganisationData> input = List.of(existingSalesOrgData,
            newSalesOrgData);
        when(salesOrgService.getSalesOrgsByCodes(any())).thenReturn(List.of(existingModel));

        PartnerSalesOrganisationModel newModel = new PartnerSalesOrganisationModel();
        newModel.setCode(CODE_NEW);
        when(reverseConverter.convertAll(any())).thenReturn(List.of(newModel));

        Set<PartnerSalesOrganisationModel> result = facade.getOrCreateSalesOrgs(input);

        Assert.assertEquals(2, result.size());
    }

    @Test
    public void testGetOrCreateSalesOrgs_EmptyInput() {
        Set<PartnerSalesOrganisationModel> result = facade.getOrCreateSalesOrgs(
            Collections.emptyList());

        verifyNoInteractions(salesOrgService);
        verifyNoInteractions(reverseConverter);
        verifyNoInteractions(modelService);

        Assert.assertTrue(result.isEmpty());
    }
}
