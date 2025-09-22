package com.ibm.commerce.partner.facades.company.impl;

import com.ibm.commerce.partner.core.model.PartnerSalesOrganisationModel;
import com.ibm.commerce.partner.core.partnerSalesOrg.service.PartnerSalesOrgService;
import com.ibm.commerce.partner.facades.PartnerSalesOrganizationFacade;
import com.ibm.commerce.partnerwebservicescommons.company.dto.IbmPartnerSalesOrganisationData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;

/**
 * Impelmentation of {@link PartnerSalesOrganizationFacade}
 */
public class DefaultPartnerSalesOrganizationFacade implements PartnerSalesOrganizationFacade {

    private final PartnerSalesOrgService salesOrgService;

    private final Converter<IbmPartnerSalesOrganisationData, PartnerSalesOrganisationModel> salesOrganisationReverseConverter;

    private final ModelService modelService;

    public DefaultPartnerSalesOrganizationFacade(final PartnerSalesOrgService salesOrgService,
        final Converter<IbmPartnerSalesOrganisationData, PartnerSalesOrganisationModel> salesOrganisationReverseConverter,
        final ModelService modelService) {
        this.salesOrgService = salesOrgService;
        this.salesOrganisationReverseConverter = salesOrganisationReverseConverter;
        this.modelService = modelService;
    }

    @Override
    public Set<PartnerSalesOrganisationModel> getOrCreateSalesOrgs(
        final List<IbmPartnerSalesOrganisationData> salesOrganisations) {
        if (CollectionUtils.isEmpty(salesOrganisations)) {
            return Collections.emptySet();
        }
        Set<PartnerSalesOrganisationModel> allSalesOrgModels = new HashSet<>();
        final Map<String, List<IbmPartnerSalesOrganisationData>> salesOrgMap = salesOrganisations.stream()
            .collect(Collectors.groupingBy(IbmPartnerSalesOrganisationData::getCode));
        final List<PartnerSalesOrganisationModel> salesOrgModels = getSalesOrgService().getSalesOrgsByCodes(
            salesOrgMap.keySet().stream().toList());
        List<IbmPartnerSalesOrganisationData> newSalesOrgs = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(salesOrgModels)) {
            allSalesOrgModels.addAll(salesOrgModels);
            final Map<String, List<PartnerSalesOrganisationModel>> existingModels = salesOrgModels.stream()
                .collect(Collectors.groupingBy(PartnerSalesOrganisationModel::getCode));
            salesOrgMap.forEach((key, value) -> {
                if (existingModels.get(key) == null) {
                    newSalesOrgs.add(value.get(0));
                }
            });
        } else {
            newSalesOrgs.addAll(salesOrganisations);
        }
        final List<PartnerSalesOrganisationModel> newSalesOrgModels = getSalesOrganisationReverseConverter().convertAll(
            newSalesOrgs);
        getModelService().saveAll(newSalesOrgModels);
        allSalesOrgModels.addAll(newSalesOrgModels);
        return allSalesOrgModels;
    }

    public PartnerSalesOrgService getSalesOrgService() {
        return salesOrgService;
    }

    public Converter<IbmPartnerSalesOrganisationData, PartnerSalesOrganisationModel> getSalesOrganisationReverseConverter() {
        return salesOrganisationReverseConverter;
    }

    public ModelService getModelService() {
        return modelService;
    }
}
