package com.ibm.commerce.partner.core.daos.impl;

import de.hybris.platform.apiregistryservices.dao.impl.DefaultDestinationDao;
import de.hybris.platform.apiregistryservices.model.AbstractDestinationModel;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.model.DestinationTargetModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;
import com.ibm.commerce.partner.core.daos.IbmConsumedDestinationDao;


/**
 * Implementation of @{@link IbmConsumedDestinationDao}
 */
public class DefaultIbmConsumedDestinationDao extends DefaultDestinationDao implements
    IbmConsumedDestinationDao {

    private static final String NOT_BLANK = " must not be blank";

    protected static final String GET_ALL_CD_BY_TARGET =
			 "SELECT {d:" + ItemModel.PK + "} FROM {" + ConsumedDestinationModel._TYPECODE
            + " AS d JOIN " + DestinationTargetModel._TYPECODE + " AS t ON {d:"
					 + AbstractDestinationModel.DESTINATIONTARGET + "}={t:" + ItemModel.PK
					 + "}} WHERE {d:" + AbstractDestinationModel.ID + "}=?" + AbstractDestinationModel.ID
            + " AND {t:" + DestinationTargetModel.ID + "}=?"
					 + AbstractDestinationModel.DESTINATIONTARGET + " AND {d:" + AbstractDestinationModel.ACTIVE + "}=?"
					 + AbstractDestinationModel.ACTIVE;

    @Override
    public ConsumedDestinationModel findActiveConsumedDestinationByIdAndTargetId(final String id,
        final String destinationTargetId) {
        Preconditions.checkArgument(StringUtils.isNotBlank(id),
				  AbstractDestinationModel.ID + NOT_BLANK);
        Preconditions.checkArgument(StringUtils.isNotBlank(destinationTargetId),
				  AbstractDestinationModel.ID + NOT_BLANK);
        final Map<String, Object> params = new HashMap<>();
		  params.put(AbstractDestinationModel.ID, id);
		  params.put(AbstractDestinationModel.ACTIVE, Boolean.TRUE);
		  params.put(AbstractDestinationModel.DESTINATIONTARGET, destinationTargetId);
        final FlexibleSearchQuery query = new FlexibleSearchQuery(GET_ALL_CD_BY_TARGET, params);
        final SearchResult<ConsumedDestinationModel> result = getFlexibleSearchService().search(
            query);
        if (null != result && CollectionUtils.isNotEmpty(result.getResult())) {
            return result.getResult().get(0);
        }
        return null;
    }
}
