package com.ibm.commerce.partner.core.util.data;

import de.hybris.platform.commerceservices.search.facetdata.FacetData;
import de.hybris.platform.commerceservices.search.facetdata.FacetValueData;
import java.util.List;

public class FacetTestDataGenerator {

    public static FacetData createFacetData(final String code, final List<FacetValueData> values) {
        FacetData facetData = new FacetData();
        facetData.setCode(code);
        facetData.setValues(values);
        facetData.setTopValues(values);
        return facetData;
    }
}
