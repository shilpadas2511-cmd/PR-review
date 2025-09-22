package com.ibm.commerce.partner.facades.company.converter.populators;

import com.ibm.commerce.partner.company.data.IbmB2BUnitData;
import com.ibm.commerce.partner.core.model.IbmB2BUnitModel;
import com.ibm.commerce.partner.core.model.PartnerSalesOrganisationModel;
import com.ibm.commerce.partner.facades.PartnerSalesOrganizationFacade;
import com.ibm.commerce.partnerwebservicescommons.company.dto.IbmPartnerSalesOrganisationData;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;

/**
 * Reverse populator for {@link B2BUnitModel} from {@link B2BUnitData}.
 *
 * <p>This populator adds support for IBM customizations on top of the standard Hybris B2B unit
 * data structure.</p>
 */
public class B2BUnitSalesOrgReversePopulator implements Populator<B2BUnitData, B2BUnitModel> {

    private final PartnerSalesOrganizationFacade salesOrganizationFacade;

    /**
     * Constructs a new {@code B2BUnitSalesOrgReversePopulator} with the provided delegate
     * populator.
     *
     * @param salesOrganizationFacade partnerSalesOrganizationFacade
     */
    public B2BUnitSalesOrgReversePopulator(
        final PartnerSalesOrganizationFacade salesOrganizationFacade) {
        this.salesOrganizationFacade = salesOrganizationFacade;
    }

    /**
     * Populates the target {@link B2BUnitModel} from the source {@link B2BUnitData}, delegating to
     * IBM-specific populator if both source and target are IBM types.
     *
     * @param source the source DTO object
     * @param target the target model object
     * @throws ConversionException if an error occurs during population
     */
    @Override
    public void populate(B2BUnitData source, B2BUnitModel target) throws ConversionException {
        if (source instanceof IbmB2BUnitData b2BUnitData
            && target instanceof IbmB2BUnitModel b2bUnitModel) {
            populate(b2BUnitData, b2bUnitModel);
        }
    }

    protected void populate(IbmB2BUnitData source, IbmB2BUnitModel target)
        throws ConversionException {
        if (CollectionUtils.isEmpty(source.getSalesOrganisations())) {
            return;
        }

        if (CollectionUtils.isEmpty(target.getPartnerSalesOrganisations())) {
            final Set<PartnerSalesOrganisationModel> newSalesOrgs = getSalesOrganizationFacade().getOrCreateSalesOrgs(
                source.getSalesOrganisations());
            target.setPartnerSalesOrganisations(newSalesOrgs);
            return;
        }

        final Map<String, List<IbmPartnerSalesOrganisationData>> salesOrgMap = source.getSalesOrganisations()
            .stream().collect(Collectors.groupingBy(IbmPartnerSalesOrganisationData::getCode));

        List<IbmPartnerSalesOrganisationData> newSalesOrgs = new ArrayList<>();
        Set<PartnerSalesOrganisationModel> newSalesOrgsLinks = new HashSet<>();

        final Map<String, List<PartnerSalesOrganisationModel>> unitExistingSaleOrgModels = target.getPartnerSalesOrganisations()
            .stream().collect(Collectors.groupingBy(PartnerSalesOrganisationModel::getCode));

        salesOrgMap.forEach((key, value) -> {
            final List<PartnerSalesOrganisationModel> unitSalesOrgModels = unitExistingSaleOrgModels.get(
                key);
            if (unitSalesOrgModels == null) {
                newSalesOrgs.add(value.get(0));
            } else {
                newSalesOrgsLinks.addAll(unitSalesOrgModels);
            }
        });

        if (CollectionUtils.isNotEmpty(newSalesOrgs)) {
            newSalesOrgsLinks.addAll(getSalesOrganizationFacade().getOrCreateSalesOrgs(newSalesOrgs));
        }

        target.setPartnerSalesOrganisations(newSalesOrgsLinks);
    }

    public PartnerSalesOrganizationFacade getSalesOrganizationFacade() {
        return salesOrganizationFacade;
    }
}
