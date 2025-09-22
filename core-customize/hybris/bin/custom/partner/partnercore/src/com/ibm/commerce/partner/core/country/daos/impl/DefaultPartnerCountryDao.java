package com.ibm.commerce.partner.core.country.daos.impl;

import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.servicelayer.i18n.daos.impl.DefaultCountryDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.ibm.commerce.partner.core.country.daos.PartnerCountryDao;

/**
 * Implementation class for {@link PartnerCountryDao}
 */
public class DefaultPartnerCountryDao extends DefaultCountryDao implements PartnerCountryDao {

    private static final String ACTIVE_COUNTRY_BY_CODE =
        "SELECT DISTINCT{ " + CountryModel.PK + "} FROM {"
            + CountryModel._TYPECODE + "} WHERE "
            + "{" + CountryModel.SAPCODE + "} IN (?countryCodes) OR {" + CountryModel.ISOCODE
            + "  } IN (?countryCodes) AND {"
            + CountryModel.ACTIVE + "}=?active";

    @Override
    public CountryModel fetchCountry(final String sapCode) {
        if (StringUtils.isBlank(sapCode)) {
            return null;
        }
        Map<String, Object> params = new HashMap();
        params.put(CountryModel.SAPCODE, sapCode);
        List<CountryModel> countryModels = this.find(params);
        return CollectionUtils.isNotEmpty(countryModels) ? countryModels.get(0) : null;
    }

    /**
     * this method is used to fetch the List of active currency for the codes
     *
     * @param countryCodes list of sap codes or iso codes
     * @return activeCurrencies active countries from database
     */
    @Override
    public List<CountryModel> findActiveCountries(List<String> countryCodes) {
        FlexibleSearchQuery query = new FlexibleSearchQuery(ACTIVE_COUNTRY_BY_CODE);
        query.addQueryParameter("countryCodes", countryCodes);
        query.addQueryParameter("active", Boolean.TRUE);
        SearchResult<CountryModel> country = getFlexibleSearchService().search(query);
        return country.getResult();

    }
}
