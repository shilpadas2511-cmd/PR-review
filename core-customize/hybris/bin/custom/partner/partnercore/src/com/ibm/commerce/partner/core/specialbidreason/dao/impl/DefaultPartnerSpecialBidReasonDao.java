/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.core.specialbidreason.dao.impl;

import com.ibm.commerce.partner.core.model.PartnerSpecialBidReasonModel;
import com.ibm.commerce.partner.core.specialbidreason.dao.PartnerSpecialBidReasonDao;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;

/**
 * DefaultPartnerSpecialBidReasonDao is used to fetch the specialbid reason model information from
 * data base.
 */
public class DefaultPartnerSpecialBidReasonDao implements PartnerSpecialBidReasonDao {

    private static final String GET_ALL_SPEL_BID_REASON =
        "SELECT {s:" + ItemModel.PK + "} FROM {" + PartnerSpecialBidReasonModel._TYPECODE
            + " as s } where {s." + PartnerSpecialBidReasonModel.ACTIVE + "}=?"
            + PartnerSpecialBidReasonModel.ACTIVE + " ORDER BY {"
            + PartnerSpecialBidReasonModel.PRIORITY + "}";

    private static final String SPEL_BID_REASON_BY_CODE =
        "SELECT {s:" + ItemModel.PK + "} FROM {" + PartnerSpecialBidReasonModel._TYPECODE
            + " as s }" + " where{" + PartnerSpecialBidReasonModel.CODE + "} in (?"+PartnerSpecialBidReasonModel.CODE+") AND {s."
            + PartnerSpecialBidReasonModel.ACTIVE + "}=?"
            + PartnerSpecialBidReasonModel.ACTIVE + " ORDER BY {"
            + PartnerSpecialBidReasonModel.PRIORITY + "}";
    private final FlexibleSearchService flexibleSearchService;

    public DefaultPartnerSpecialBidReasonDao(FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

    /**
     * get all special bid reason information
     *
     * @return PartnerSpecialBidReasonModel- send list of PartnerSpecialBidReasonModel
     */
    @Override
    public List<PartnerSpecialBidReasonModel> getAllSpecialBidReasonDetails() {

        List<PartnerSpecialBidReasonModel> filterResult = null;
        final FlexibleSearchQuery query = new FlexibleSearchQuery(GET_ALL_SPEL_BID_REASON);
        query.addQueryParameter(PartnerSpecialBidReasonModel.ACTIVE, Boolean.TRUE);
        final SearchResult<PartnerSpecialBidReasonModel> result = getFlexibleSearchService().search(
            query);
        if (null != result && CollectionUtils.isNotEmpty(result.getResult())) {
            filterResult = result.getResult().stream()
                .filter(PartnerSpecialBidReasonModel::getActive)
                .collect(Collectors.toList());
        }
        return filterResult;
    }

    /**
     * get special bid reason information by code
     *
     * @param code
     * @return
     */
    @Override
    public PartnerSpecialBidReasonModel getSpecialBidReasonById(String code) {

        List<PartnerSpecialBidReasonModel> specialBidReasonModels = getSpecialBidReasonByIds(
            Collections.singletonList(code));
        return CollectionUtils.isNotEmpty(specialBidReasonModels) ? specialBidReasonModels.get(0)
            : null;
    }

    /**
     * get special bid reason information by code
     *
     * @param codes
     * @return
     */
    @Override
    public List<PartnerSpecialBidReasonModel> getSpecialBidReasonByIds(List<String> codes) {

        final FlexibleSearchQuery query = new FlexibleSearchQuery(SPEL_BID_REASON_BY_CODE);
        query.addQueryParameter(PartnerSpecialBidReasonModel.ACTIVE, Boolean.TRUE);
        query.addQueryParameter(PartnerSpecialBidReasonModel.CODE, codes);
        final SearchResult<PartnerSpecialBidReasonModel> result = getFlexibleSearchService().search(
            query);
        return result.getResult();
    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

}
