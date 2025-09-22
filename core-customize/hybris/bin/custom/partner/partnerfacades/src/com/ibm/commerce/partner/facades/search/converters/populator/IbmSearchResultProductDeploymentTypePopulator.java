package com.ibm.commerce.partner.facades.search.converters.populator;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.facades.product.data.IbmDeploymentTypeData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.converters.populator.SearchResultProductPopulator;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * populate Product custom attributes from solr to product data
 */
public class IbmSearchResultProductDeploymentTypePopulator extends
    SearchResultProductPopulator implements Populator<SearchResultValueData, ProductData> {


    @Override
    public void populate(SearchResultValueData source, ProductData target)
        throws ConversionException {

        target.setDeploymentType(getDeploymentType(source));
    }

    public IbmDeploymentTypeData getDeploymentType(SearchResultValueData source)
        throws ConversionException {

        IbmDeploymentTypeData deploymentTypeData = new IbmDeploymentTypeData();
        deploymentTypeData.setCode(
            this.<String>getValue(source, PartnercoreConstants.DEPLOYMENT_TYPE_CODE));
        deploymentTypeData.setName(
            this.<String>getValue(source, PartnercoreConstants.DEPLOYMENT_TYPE_NAME));
        deploymentTypeData.setTagCode(
            this.<String>getValue(source, PartnercoreConstants.DEPLOYMENT_TYPE_TAG_CODE));
        return deploymentTypeData;
    }

}
