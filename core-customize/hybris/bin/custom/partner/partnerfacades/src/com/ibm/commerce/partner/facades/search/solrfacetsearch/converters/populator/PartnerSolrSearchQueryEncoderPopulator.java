package com.ibm.commerce.partner.facades.search.solrfacetsearch.converters.populator;

import com.ibm.commerce.common.core.model.SellerAudienceMaskModel;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.user.service.PartnerUserService;
import de.hybris.platform.commercefacades.search.data.SearchFilterQueryData;
import de.hybris.platform.commercefacades.search.data.SearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.FilterQueryOperator;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * This class is used to add the searchAvailability in FilterQuery.
 */
public class PartnerSolrSearchQueryEncoderPopulator implements
    Populator<SolrSearchQueryData, SearchQueryData> {

    
    private PartnerUserService userService;

    private final String defaultSellerAudienceValue;

    private final String defaultDeploymentType;

    public PartnerSolrSearchQueryEncoderPopulator(PartnerUserService userService,
        String defaultSellerAudienceValue, final String defaultDeploymentType) {
        this.userService = userService;
        this.defaultSellerAudienceValue = defaultSellerAudienceValue;
        this.defaultDeploymentType = defaultDeploymentType;
    }

    @Override
    public void populate(final SolrSearchQueryData source, final SearchQueryData target)
        throws ConversionException {
        if (CollectionUtils.isEmpty(target.getFilterQueries())) {
            target.setFilterQueries(new ArrayList<>());
        }
        addSearchAvailability(source, target);
        addSellerAudienceType(source, target);
        addDeploymentType(source, target);

    }

    /**
     * add sellerAudienceType in filter query.
     *
     * @param source
     * @param target
     */
    protected void addSearchAvailability(SolrSearchQueryData source, SearchQueryData target) {
        final Optional<SearchFilterQueryData> optionalSearchFilterQueryData = isNotPresent(target,
            PartnercoreConstants.SEARCH_AVAILABILITY);
        if (optionalSearchFilterQueryData.isEmpty()) {
            final SearchFilterQueryData solrSearchFilterQuery = createSearchFilterQueryData(
                PartnercoreConstants.SEARCH_AVAILABILITY,
                Collections.singleton(Boolean.TRUE.toString()), FilterQueryOperator.AND);
            target.getFilterQueries().add(solrSearchFilterQuery);
        }
    }

    /**
     * add sellerAudienceType in filter query.
     *
     * @param source
     * @param target
     */
    protected void addSellerAudienceType(SolrSearchQueryData source, SearchQueryData target) {
        final SellerAudienceMaskModel userSellerAudienceMask = getUserService().getSellerAudienceMaskForCurrentUser();

        final String sellerAudienceCode =
            userSellerAudienceMask != null ? userSellerAudienceMask.getCode()
                : getDefaultSellerAudienceValue();
        final Optional<SearchFilterQueryData> optionalSearchFilterQueryData = isNotPresent(target,
            PartnercoreConstants.SELLER_AUDIENCE_TYPE);
        if (optionalSearchFilterQueryData.isEmpty()) {
            final SearchFilterQueryData solrSearchFilterQuery = createSearchFilterQueryData(
                PartnercoreConstants.SELLER_AUDIENCE_TYPE,
                Collections.singleton(sellerAudienceCode), FilterQueryOperator.AND);
            target.getFilterQueries().add(solrSearchFilterQuery);
        }
    }

    /**
     * add sellerAudienceType in filter query.
     *
     * @param source
     * @param target
     */
    protected void addDeploymentType(SolrSearchQueryData source, SearchQueryData target) {
        final Optional<SearchFilterQueryData> optionalDeploymentTypeFilter = isNotPresent(target,
            PartnercoreConstants.DEPLOYMENT_TYPE_CODE);
        if (optionalDeploymentTypeFilter.isEmpty()) {
            final SearchFilterQueryData solrSearchFilterQuery = createSearchFilterQueryData(
                PartnercoreConstants.DEPLOYMENT_TYPE_CODE,
            new HashSet<>(Arrays.asList(getDefaultDeploymentType().split(PartnercoreConstants.COMMA))), FilterQueryOperator.OR);
            target.getFilterQueries().add(solrSearchFilterQuery);
        } else {
            optionalDeploymentTypeFilter.get()
                .setValues(Collections.singleton(getDefaultDeploymentType()));
        }
    }

    protected Optional<SearchFilterQueryData> isNotPresent(final SearchQueryData target,
        String key) {

        if (StringUtils.isBlank(key)) {
            return Optional.empty();
        }
        return target.getFilterQueries().stream()
            .filter(filterQueryData -> key.equals(filterQueryData.getKey())).findAny();
    }

    protected SearchFilterQueryData createSearchFilterQueryData(String code, Set<String> values,
        FilterQueryOperator operator) {
        final SearchFilterQueryData solrSearchFilterQuery = new SearchFilterQueryData();
        solrSearchFilterQuery.setKey(code);
        solrSearchFilterQuery.setValues(values);
        solrSearchFilterQuery.setOperator(operator);
        return solrSearchFilterQuery;
    }

    public PartnerUserService getUserService() {
        return userService;
    }

    public String getDefaultSellerAudienceValue() {
        return defaultSellerAudienceValue;
    }

    public String getDefaultDeploymentType() {
        return defaultDeploymentType;
    }
}

