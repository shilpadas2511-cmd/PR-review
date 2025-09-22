package com.ibm.commerce.partner.facades.search.converters.populator;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.converters.populator.SearchResultProductPopulator;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * populate Product custom attributes from solr to product data
 */
public class IbmSearchResultProductPopulator extends SearchResultProductPopulator implements
    Populator<SearchResultValueData, ProductData> {


    @Override
    public void populate(SearchResultValueData source, ProductData target)
        throws ConversionException {
        target.setConfiguratorCode(
            this.<String>getValue(source, PartnercoreConstants.CONFIGURATOR_CODE));
    }
}
