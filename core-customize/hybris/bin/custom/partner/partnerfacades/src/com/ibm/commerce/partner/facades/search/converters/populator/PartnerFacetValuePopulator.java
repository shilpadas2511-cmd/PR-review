package com.ibm.commerce.partner.facades.search.converters.populator;

import de.hybris.platform.commerceservices.search.facetdata.FacetValueData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;

/**
 * Populates ChildValues for FacetValue
 *
 * @param <QUERY>
 * @param <STATE>
 */
public class PartnerFacetValuePopulator<QUERY, STATE> implements
    Populator<FacetValueData<QUERY>, FacetValueData<STATE>> {

    private Converter<FacetValueData<QUERY>, FacetValueData<STATE>> childFacetValueConverter;

    public PartnerFacetValuePopulator(
        final Converter<FacetValueData<QUERY>, FacetValueData<STATE>> childFacetValueConverter) {
        this.childFacetValueConverter = childFacetValueConverter;
    }


    @Override
    public void populate(final FacetValueData<QUERY> source, final FacetValueData<STATE> target) {
        if (CollectionUtils.isNotEmpty(source.getChildValues())) {
            List<FacetValueData<STATE>> childFacetValueList = source.getChildValues().stream()
                .map(childValue -> {
                    FacetValueData<STATE> childFacetValue = getChildFacetValueConverter().convert(
                        childValue);
                    populate(childValue, childFacetValue);
                    return childFacetValue;
                }).toList();
            target.setChildValues(childFacetValueList);
        }
    }

    public Converter<FacetValueData<QUERY>, FacetValueData<STATE>> getChildFacetValueConverter() {
        return childFacetValueConverter;
    }
}
