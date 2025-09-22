package com.ibm.commerce.partner.core.inboundservices.persistence.hook;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.inboundservices.persistence.PersistenceContext;
import de.hybris.platform.inboundservices.persistence.hook.PrePersistHook;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.Optional;


/**
 * quote status pre persist hook for micro services
 */
public class PartnerQuoteStatusUpdatePrePersistHook implements PrePersistHook {

    private final ModelService modelService;

    public PartnerQuoteStatusUpdatePrePersistHook(ModelService modelService) {
        this.modelService = modelService;
    }

    @Override
    public Optional<ItemModel> execute(ItemModel item, PersistenceContext context) {
        getModelService().detach(item);
        return Optional.of(item);
    }

    public ModelService getModelService() {
        return modelService;
    }
}
