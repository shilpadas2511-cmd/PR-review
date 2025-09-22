package com.ibm.commerce.partner.core.order.services.impl;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.daos.PartnerProcessDao;
import com.ibm.commerce.partner.core.model.PriceLookUpProcessModel;
import com.ibm.commerce.partner.core.order.services.PartnerProcessService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.processengine.enums.ProcessState;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;

/**
 * Service have methods related to the business process
 */
public class DefaultPartnerProcessService implements PartnerProcessService {

    private PartnerProcessDao partnerProcessDao;
    private  ModelService modelService;


    public DefaultPartnerProcessService(PartnerProcessDao partnerProcessDao, ModelService modelService ){
        this.partnerProcessDao = partnerProcessDao;
        this.modelService = modelService;

    }

    /**
     * removing cart refrences from the old business processes of the cart.
     *
     * @param cart
     */
    @Override
    public void removeCartFromOldProcess(final CartModel cart)
    {
        if (cart == null || cart.getCode() == null)
        {
            // Log an error or throw an exception if the cart or cart code is null
            throw new IllegalArgumentException("Cart and cart code must not be null");
        }
        final String processCode = PartnercoreConstants.PRICING_SERVICE_PROCESS_CODE + PartnercoreConstants.HYPHEN + cart.getCode()
            + PartnercoreConstants.PERCENTAGE;
        final List<BusinessProcessModel> businessProcessList = getBusinessProcessList(processCode);

        if (CollectionUtils.isNotEmpty(businessProcessList))
        {
            businessProcessList.stream().filter(PriceLookUpProcessModel.class::isInstance).map(PriceLookUpProcessModel.class::cast)
                .forEach(partnerProcess -> partnerProcess.setOrder(null));
            getModelService().saveAll(businessProcessList);
        }

    }

    /**
     * checks  the quote cart business process is in running state
     *
     * @return true if it is running state else false.
     */
    @Override
    public boolean checkQuoteCartProcessCompleted(final CartModel cart) {
        if (cart == null) {
            throw new IllegalArgumentException("Cart and cart code must not be null");
        }

        final String processCode =
            PartnercoreConstants.QUOTE_PRICING_SERVICE_PROCESS_CODE + PartnercoreConstants.HYPHEN
                + cart.getCode()
                + PartnercoreConstants.PERCENTAGE;
        final List<BusinessProcessModel> businessProcessList = getBusinessProcessList(processCode);

        if (CollectionUtils.isNotEmpty(businessProcessList)) {
            return businessProcessList.stream()
                .filter(PriceLookUpProcessModel.class::isInstance)
                .map(PriceLookUpProcessModel.class::cast)
                .noneMatch(
                    partnerProcess -> ProcessState.RUNNING.equals(partnerProcess.getState()));

        }
        return true;
    }

    /**
     * Fetches the businessProcess created for that order code
     *
     * @return BusinessProcess
     */
    @Override
    public List<BusinessProcessModel> getBusinessProcessList(String orderCode) {
        return getPartnerProcessDao().getProcessListByCode(orderCode);
    }

    public PartnerProcessDao getPartnerProcessDao() {
        return partnerProcessDao;
    }

    public ModelService getModelService() {
        return modelService;
    }

}
