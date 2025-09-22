package com.ibm.commerce.partner.core.event;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.ProvisionFormProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Event listener to trigger the  process update editors email process upon UpdateProvisionFormEditorsEvent.
 */
public class UpdateProvisionFormEditorsEventListener extends AbstractEventListener<UpdateProvisionFormEditorsEvent> {

    private final ModelService modelService;
    private final BusinessProcessService businessProcessService;
    private final KeyGenerator processCodeGenerator;

    /**
     * Constructs a UpdateProvisionFormEditorsEventListener with the given dependencies.
     *  @param modelService           ModelService instance for model operations.
     * @param businessProcessService BusinessProcessService instance for process handling.
     */
    public UpdateProvisionFormEditorsEventListener(final ModelService modelService,
        final BusinessProcessService businessProcessService,
        final KeyGenerator processCodeGenerator) {
        this.modelService = modelService;
        this.businessProcessService = businessProcessService;
        this.processCodeGenerator = processCodeGenerator;
    }

    @Override
    protected void onEvent(UpdateProvisionFormEditorsEvent updateProvisionFormEditorsEvent) {

        if (updateProvisionFormEditorsEvent.getOrder() instanceof final IbmPartnerCartModel partnerCart) {
            final ProvisionFormProcessModel provisionFormProcessModel = createProcess(updateProvisionFormEditorsEvent);
            provisionFormProcessModel.setOrder(updateProvisionFormEditorsEvent.getOrder());
            getModelService().save(provisionFormProcessModel);
            getBusinessProcessService().startProcess(provisionFormProcessModel);
        }
    }

    /**
     * Creates a ProvisionFormProcessModel for the given UpdateProvisionFormEditorsEvent.
     *
     * @param event The UpdateProvisionFormEditorsEvent for which the process is created.
     * @return The created ProvisionFormProcessModel.
     */
    protected ProvisionFormProcessModel createProcess(final UpdateProvisionFormEditorsEvent event) {
        return getBusinessProcessService().createProcess(getProcessCodeGenerator().generateFor(
                PartnercoreConstants.PROVISION_FORM_UPDATE_EDITORS_SERVICE_PROCESS_CODE + PartnercoreConstants.HYPHEN
                    + event.getOrder().getCode()).toString(),
            PartnercoreConstants.PROVISION_FORM_UPDATE_EDITORS_SERVICE_PROCESS_CODE);
    }


    /**
     * Retrieves the ModelService instance.
     *
     * @return The ModelService instance.
     */
    public ModelService getModelService() {
        return modelService;
    }

    /**
     * Retrieves the BusinessProcessService instance.
     *
     * @return The BusinessProcessService instance.
     */
    public BusinessProcessService getBusinessProcessService() {
        return businessProcessService;
    }

    public KeyGenerator getProcessCodeGenerator() {
        return processCodeGenerator;
    }


}
