package com.ibm.commerce.partner.facades.search.converters.populator;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import de.hybris.platform.commercefacades.product.data.VariantOptionData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/*
*  IbmVariantOptionProductPopulator poulate the  SearchResultValueData to VariantOptionData
* */
public class IbmVariantOptionProductPopulator implements
    Populator<SearchResultValueData, VariantOptionData> {


    /**
     * Populate the target instance with values from the source instance.
     *
     * @param searchResultValueData the source object
     * @param variantOptionData     the target to fill
     * @throws ConversionException if an error occurs
     */
    @Override
    public void populate(SearchResultValueData searchResultValueData,
        VariantOptionData variantOptionData) throws ConversionException {
        variantOptionData.setConfiguratorCode((String) getValue(searchResultValueData,  PartnercoreConstants.CONFIGURATOR_CODE));
        variantOptionData.setTagCode((String) getValue(searchResultValueData,  PartnercoreConstants.DEPLOYMENT_TYPE_TAG_CODE));

    }
    protected <T> T getValue(final SearchResultValueData source, final String propertyName)
    {
        if (source.getValues() == null)
        {
            return null;
        }

        // DO NOT REMOVE the cast (T) below, while it should be unnecessary it is required by the javac compiler
        return (T) source.getValues().get(propertyName);
    }
}
