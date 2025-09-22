package com.ibm.commerce.partner.core.services.impl;


import com.ibm.commerce.partner.core.daos.IbmConsumedDestinationDao;
import com.ibm.commerce.partner.core.services.IbmConsumedDestinationService;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.services.impl.DefaultDestinationService;


/**
 * Implementation for @{@link IbmConsumedDestinationService}
 */
public class DefaultIbmConsumedDestinationService extends DefaultDestinationService implements
    IbmConsumedDestinationService {

    private final IbmConsumedDestinationDao consumedDestinationDao;

    public DefaultIbmConsumedDestinationService(
        final IbmConsumedDestinationDao consumedDestinationDao) {
        this.consumedDestinationDao = consumedDestinationDao;
    }

    @Override
    public ConsumedDestinationModel findActiveConsumedDestinationByIdAndTargetId(final String id,
        final String destinationTargetId) {
        return getConsumedDestinationDao().findActiveConsumedDestinationByIdAndTargetId(id,
            destinationTargetId);
    }

    public IbmConsumedDestinationDao getConsumedDestinationDao() {
        return consumedDestinationDao;
    }
}
