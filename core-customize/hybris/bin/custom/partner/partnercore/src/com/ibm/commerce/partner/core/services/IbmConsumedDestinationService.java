package com.ibm.commerce.partner.core.services;

import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.services.DestinationService;


/**
 * Interface for @{@link ConsumedDestinationModel}
 */
public interface IbmConsumedDestinationService extends DestinationService {

    /**
     * Returns Actiive ConsumedDestination by id and Destination Target Id
     *
     * @param id
     * @param destinationTargetId
     * @return
     */
    ConsumedDestinationModel findActiveConsumedDestinationByIdAndTargetId(String id,
        String destinationTargetId);
}
