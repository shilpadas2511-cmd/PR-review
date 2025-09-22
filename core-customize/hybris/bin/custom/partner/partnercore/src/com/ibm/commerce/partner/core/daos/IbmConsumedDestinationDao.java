package com.ibm.commerce.partner.core.daos;

import de.hybris.platform.apiregistryservices.dao.DestinationDao;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;


public interface IbmConsumedDestinationDao extends DestinationDao
{
	/**
	 * Returns Active ConsumedDestination by id and destinationTargetId
	 *
	 * @param id
	 * @param destinationTargetId
	 * @return
	 */
	ConsumedDestinationModel findActiveConsumedDestinationByIdAndTargetId(String id, String destinationTargetId);
}
