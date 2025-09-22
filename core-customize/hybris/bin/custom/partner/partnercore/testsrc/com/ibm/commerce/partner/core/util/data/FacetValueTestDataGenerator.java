package com.ibm.commerce.partner.core.util.data;

import de.hybris.platform.commerceservices.search.facetdata.FacetValueData;

public class FacetValueTestDataGenerator {

    public static FacetValueData createFacetValueData(final String code) {
        FacetValueData facetValueData = new FacetValueData();
        facetValueData.setCode(code);
        return facetValueData;
    }

}
