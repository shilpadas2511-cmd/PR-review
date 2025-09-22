package com.ibm.commerce.partner.facades.actions.customer;

import com.ibm.commerce.partner.core.accountservice.services.PartnerAccountServiceOutboundIntegrationService;
import com.ibm.commerce.partner.core.actions.PartnerAbstractSimpleDecisionAction;
import com.ibm.commerce.partner.core.company.distributor.data.response.PartnerDistributorSiteIdResponseData;
import com.ibm.commerce.partner.core.company.reseller.data.response.PartnerResellerSiteIdResponseData;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.PartnerResellerSiteBusinessProcessModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.model.BusinessProcessParameterModel;
import de.hybris.platform.task.RetryLaterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;

/**
 * Fetch PartnerReseller Information
 */
public class FetchPartnerResellerDefaultDistributorAccountDetailsAction extends
    PartnerAbstractSimpleDecisionAction<PartnerResellerSiteBusinessProcessModel> {

    private final PartnerAccountServiceOutboundIntegrationService outboundIntegrationService;

    protected FetchPartnerResellerDefaultDistributorAccountDetailsAction(
        final Integer maxRetryAllowed, final Integer retryDelay,
        final PartnerAccountServiceOutboundIntegrationService outboundIntegrationService) {
        super(maxRetryAllowed, retryDelay);
        this.outboundIntegrationService = outboundIntegrationService;
    }

    @Override
    public Transition executeAction(final PartnerResellerSiteBusinessProcessModel processModel)
        throws RetryLaterException, Exception {

        if (CollectionUtils.isEmpty(processModel.getContextParameters())) {
            return Transition.NOK;
        }
        PartnerResellerSiteIdResponseData resellerSiteIdResponseData = getResellerData(
            processModel);
        CustomerModel customerModel = getCustomer(processModel);
        if (resellerSiteIdResponseData != null && customerModel != null) {
            List<PartnerDistributorSiteIdResponseData> distributorSiteDetails = getOutboundIntegrationService().getDistributorSiteId(
                resellerSiteIdResponseData.getDistNumber(), customerModel.getUid());
            if (CollectionUtils.isNotEmpty(distributorSiteDetails)) {
                //Each ResellerSite id is supposed to have only 1 Default Distributor
                resellerSiteIdResponseData.setPartnerInternalDistributorResponse(
                    distributorSiteDetails.get(0));
                //Setting the value of the distributor response in reseller response
                setProcessParameter(processModel, resellerSiteIdResponseData);
                return Transition.OK;
            }
        }

        return Transition.NOK;
    }

    protected void setProcessParameter(PartnerResellerSiteBusinessProcessModel processModel,
        PartnerResellerSiteIdResponseData resellerSiteIdResponseData) {

        Collection<BusinessProcessParameterModel> processParameterModels = new ArrayList();

        if (CollectionUtils.isNotEmpty(processModel.getContextParameters())) {

            // fetch and store the current process parameter in the list
            List<BusinessProcessParameterModel> processParameters = processModel.getContextParameters()
                .stream().collect(Collectors.toList());

            for (BusinessProcessParameterModel processParameter : processParameters) {
                //Add all the existing process parameter in new collection, else its give exeption
                processParameterModels.add(processParameter);
            }

            processParameterModels.add(
                createProcessParameter(processModel, resellerSiteIdResponseData));

            //Add the process parameter in context parameter of business process
            processModel.setContextParameters(processParameterModels);
        }
        getModelService().saveAll(processModel);
        getModelService().refresh(processModel);
    }

    protected BusinessProcessParameterModel createProcessParameter(
        PartnerResellerSiteBusinessProcessModel processModel,
        PartnerResellerSiteIdResponseData resellerSiteIdResponseData) {
        // create new business process parameter to add distributor data with reseller
        BusinessProcessParameterModel resellerDistributorProcessParameter = new BusinessProcessParameterModel();
        resellerDistributorProcessParameter.setName(
            PartnercoreConstants.PARTNER_RESELLE_UNIT_PROCESS_RESELLER_DISTRIBUTOR_PAYLOAD_CONTEXT_PARAM);
        resellerDistributorProcessParameter.setValue(resellerSiteIdResponseData);
        resellerDistributorProcessParameter.setProcess(processModel);
        getModelService().save(resellerDistributorProcessParameter);
        getModelService().refresh(resellerDistributorProcessParameter);
        return resellerDistributorProcessParameter;
    }

    protected PartnerResellerSiteIdResponseData getResellerData(
        final PartnerResellerSiteBusinessProcessModel processModel) {
        Optional<BusinessProcessParameterModel> optionalProcessParam = processModel.getContextParameters()
            .stream().filter(param -> param.getName().equals(
                PartnercoreConstants.PARTNER_RESELLE_UNIT_PROCESS_RESELLER_PAYLOAD_CONTEXT_PARAM))
            .findAny();
        if (optionalProcessParam.isPresent() && optionalProcessParam.get()
            .getValue() instanceof PartnerResellerSiteIdResponseData resellerSiteIdResponseData) {
            return resellerSiteIdResponseData;
        }
        return null;
    }

    protected CustomerModel getCustomer(
        final PartnerResellerSiteBusinessProcessModel processModel) {
        Optional<BusinessProcessParameterModel> optionalProcessParam = processModel.getContextParameters()
            .stream().filter(param -> param.getName().equals(
                PartnercoreConstants.PARTNER_RESELLE_UNIT_PROCESS_CUSTOMER_PAYLOAD_CONTEXT_PARAM))
            .findAny();
        if (optionalProcessParam.isPresent() && optionalProcessParam.get()
            .getValue() instanceof CustomerModel customerModel) {
            return customerModel;
        }
        return null;
    }

    public PartnerAccountServiceOutboundIntegrationService getOutboundIntegrationService() {
        return outboundIntegrationService;
    }
}
