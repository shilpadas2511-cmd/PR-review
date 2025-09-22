package com.ibm.commerce.partner.facades.company.converter.populators;

import static org.mockito.Mockito.*;

import com.ibm.commerce.partner.company.data.IbmB2BUnitData;
import com.ibm.commerce.partner.core.model.IbmB2BUnitModel;
import com.ibm.commerce.partner.core.model.PartnerSalesOrganisationModel;
import com.ibm.commerce.partner.facades.PartnerSalesOrganizationFacade;
import com.ibm.commerce.partnerwebservicescommons.company.dto.IbmPartnerSalesOrganisationData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit test class for {@link B2BUnitSalesOrgReversePopulator}.
 */
public class B2BUnitSalesOrgReversePopulatorTest {

    @InjectMocks
    private B2BUnitSalesOrgReversePopulator populator;

    @Mock
    private PartnerSalesOrganizationFacade partnerSalesOrganizationFacade;

    private IbmB2BUnitData source;
    private IbmB2BUnitModel target;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        populator = new B2BUnitSalesOrgReversePopulator(partnerSalesOrganizationFacade);

        source = new IbmB2BUnitData();
        target = new IbmB2BUnitModel();
    }

    @Test
    public void testPopulate_withEmptySalesOrgsInSource() throws ConversionException {
        source.setSalesOrganisations(Collections.emptyList());
        target.setPartnerSalesOrganisations(new HashSet<>());
        populator.populate(source, target);
        Assert.assertTrue(target.getPartnerSalesOrganisations().isEmpty());
    }

    @Test
    public void testPopulate_withEmptySalesOrgsInTarget() throws ConversionException {
        IbmPartnerSalesOrganisationData orgData = new IbmPartnerSalesOrganisationData();
        orgData.setCode("CODE1");

        Set<PartnerSalesOrganisationModel> createdSalesOrgs = new HashSet<>();
        PartnerSalesOrganisationModel model = mock(PartnerSalesOrganisationModel.class);
        createdSalesOrgs.add(model);

        source.setSalesOrganisations(Collections.singletonList(orgData));
        when(partnerSalesOrganizationFacade.getOrCreateSalesOrgs(any())).thenReturn(createdSalesOrgs);

        target.setPartnerSalesOrganisations(Collections.emptySet());

        populator.populate(source, target);

        Assert.assertEquals(1, target.getPartnerSalesOrganisations().size());
        Assert.assertTrue(target.getPartnerSalesOrganisations().contains(model));
    }

    @Test
    public void testPopulate_withExistingSalesOrgs() throws ConversionException {
        IbmPartnerSalesOrganisationData orgData1 = new IbmPartnerSalesOrganisationData();
        orgData1.setCode("ORG1");

        IbmPartnerSalesOrganisationData orgData2 = new IbmPartnerSalesOrganisationData();
        orgData2.setCode("ORG2");

        PartnerSalesOrganisationModel existingModel = mock(PartnerSalesOrganisationModel.class);
        when(existingModel.getCode()).thenReturn("ORG1");

        PartnerSalesOrganisationModel createdModel = mock(PartnerSalesOrganisationModel.class);
        when(createdModel.getCode()).thenReturn("ORG2");

        Set<PartnerSalesOrganisationModel> existingSet = new HashSet<>();
        existingSet.add(existingModel);

        Set<PartnerSalesOrganisationModel> newSet = new HashSet<>();
        newSet.add(createdModel);

        source.setSalesOrganisations(Arrays.asList(orgData1, orgData2));
        target.setPartnerSalesOrganisations(existingSet);

        when(partnerSalesOrganizationFacade.getOrCreateSalesOrgs(any())).thenReturn(newSet);

        populator.populate(source, target);

        Set<PartnerSalesOrganisationModel> result = target.getPartnerSalesOrganisations();
        Assert.assertEquals(2, result.size());
        Assert.assertTrue(result.contains(existingModel));
        Assert.assertTrue(result.contains(createdModel));
    }

    @Test
    public void testGenericPopulate_dispatchesToIbmPopulate() throws ConversionException {
        B2BUnitData genericSource = source; // since IbmB2BUnitData extends B2BUnitData
        B2BUnitModel genericTarget = target; // since IbmB2BUnitModel extends B2BUnitModel

        IbmPartnerSalesOrganisationData orgData = new IbmPartnerSalesOrganisationData();
        orgData.setCode("NEW");

        PartnerSalesOrganisationModel created = mock(PartnerSalesOrganisationModel.class);
        when(created.getCode()).thenReturn("NEW");

        source.setSalesOrganisations(Collections.singletonList(orgData));
        target.setPartnerSalesOrganisations(new HashSet<>());

        when(partnerSalesOrganizationFacade.getOrCreateSalesOrgs(any())).thenReturn(Set.of(created));

        populator.populate(genericSource, genericTarget);

        Assert.assertEquals(1, target.getPartnerSalesOrganisations().size());
    }
}
