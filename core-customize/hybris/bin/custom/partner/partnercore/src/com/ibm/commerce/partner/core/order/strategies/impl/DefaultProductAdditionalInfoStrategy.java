package com.ibm.commerce.partner.core.order.strategies.impl;


import com.ibm.commerce.partner.core.model.IbmPartnerCartEntryModel;
import com.ibm.commerce.partner.core.model.PartnerCommerceRampUpModel;
import com.ibm.commerce.partner.core.model.PartnerRampUpSummaryModel;
import com.ibm.commerce.partner.core.order.strategies.ProductAdditionalInfoStrategy;
import com.ibm.commerce.partner.data.order.entry.CommerceRampUpData;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.commons.collections4.CollectionUtils;

/**
 * This class is used to save CommerceRampUpData for SaaS product
 */
public class DefaultProductAdditionalInfoStrategy implements ProductAdditionalInfoStrategy {


    private final ModelService modelService;

    public DefaultProductAdditionalInfoStrategy(ModelService modelService) {
        this.modelService = modelService;
    }

    /**
     * This method is used to create CommerceRampUpData from the CommerceCartParameter and save it.
     *
     * @param parameter The request data contains info needed to be sent for the
     *                  CommerceCartParameter
     * @param result    The request data contains info needed to be sent for the
     *                  CommerceCartModification
     */
    @Override
    public void addInfo(CommerceCartParameter parameter, CommerceCartModification result) {
        if (result.getEntry() instanceof IbmPartnerCartEntryModel ibmPartnerCartEntryModel) {
            CommerceRampUpData commerceRampUpData = parameter.getCommerceRampUpData();
            PartnerCommerceRampUpModel partnerCommerceRampUpModel = getModelService().create(
                PartnerCommerceRampUpModel.class);
            partnerCommerceRampUpModel.setCode(parameter.getCart().getCode() + UUID.randomUUID());
            partnerCommerceRampUpModel.setRampUpPeriod(commerceRampUpData.getRampUpPeriod());
            List<PartnerRampUpSummaryModel> partnerRampUpSummaryModelList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(commerceRampUpData.getRampUpSummary())) {
                commerceRampUpData.getRampUpSummary().forEach(rampUpSummaryData -> {
                    PartnerRampUpSummaryModel partnerRampUpSummaryModel = getModelService().create(
                        PartnerRampUpSummaryModel.class);
                    partnerRampUpSummaryModel.setCode(
                        parameter.getCart().getCode() + UUID.randomUUID());
                    partnerRampUpSummaryModel.setRampUpPeriodDuration(
                        rampUpSummaryData.getRampUpPeriodDuration());
                    partnerRampUpSummaryModel.setRampUpQuantity(
                        rampUpSummaryData.getRampUpQuantity());
                    partnerRampUpSummaryModelList.add(partnerRampUpSummaryModel);
                });
            }
            if (CollectionUtils.isNotEmpty(partnerRampUpSummaryModelList)) {
                getModelService().saveAll(partnerRampUpSummaryModelList);
                partnerCommerceRampUpModel.setPartnerRampUpSummary(partnerRampUpSummaryModelList);
            }
            getModelService().save(partnerCommerceRampUpModel);
            ibmPartnerCartEntryModel.setCommerceRampUp(partnerCommerceRampUpModel);
            getModelService().save(ibmPartnerCartEntryModel);
        }
    }


    public ModelService getModelService() {
        return modelService;
    }
}
