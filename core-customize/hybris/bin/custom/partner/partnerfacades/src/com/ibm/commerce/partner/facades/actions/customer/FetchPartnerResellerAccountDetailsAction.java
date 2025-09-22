package com.ibm.commerce.partner.facades.actions.customer;

import com.ibm.commerce.partner.core.accountservice.services.PartnerAccountServiceOutboundIntegrationService;
import com.ibm.commerce.partner.core.actions.PartnerAbstractSimpleDecisionAction;
import com.ibm.commerce.partner.core.company.reseller.data.response.PartnerResellerSiteIdResponseData;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.PartnerResellerSiteBusinessProcessModel;
import com.ibm.commerce.partner.core.model.PartnerStoreFrontCustomerProcessModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.task.RetryLaterException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;

/**
 * Fetch PartnerReseller Information
 */
public class FetchPartnerResellerAccountDetailsAction extends
    PartnerAbstractSimpleDecisionAction<PartnerStoreFrontCustomerProcessModel> {


    private final PartnerAccountServiceOutboundIntegrationService outboundIntegrationService;


    private List<String> eligibleCountryList;


    private final BusinessProcessService businessProcessService;


    private final KeyGenerator processCodeGenerator;

    protected FetchPartnerResellerAccountDetailsAction(final Integer maxRetryAllowed,
        final Integer retryDelay,
        final PartnerAccountServiceOutboundIntegrationService outboundIntegrationService,
        final BusinessProcessService businessProcessService,
        final KeyGenerator processCodeGenerator) {
        super(maxRetryAllowed, retryDelay);
        this.outboundIntegrationService = outboundIntegrationService;
        this.businessProcessService = businessProcessService;
        this.processCodeGenerator = processCodeGenerator;
    }

    @Override
    public Transition executeAction(
        final PartnerStoreFrontCustomerProcessModel partnerStoreFrontCustomerProcessModel)
        throws RetryLaterException, Exception {
        if (partnerStoreFrontCustomerProcessModel == null
            || partnerStoreFrontCustomerProcessModel.getCustomer() == null) {
            return Transition.NOK;
        }
        final CustomerModel cus = partnerStoreFrontCustomerProcessModel.getCustomer();
        List<PartnerResellerSiteIdResponseData> resellerSites = getOutboundIntegrationService().getResellerSiteId(
            cus.getUid());

        if (CollectionUtils.isNotEmpty(resellerSites)) {
            for (PartnerResellerSiteIdResponseData resellerSite : resellerSites) {

                if (resellerSite.getCustomerInfo() != null
                    && resellerSite.getCustomerInfo().getAddress() != null && (
                    CollectionUtils.isEmpty(getEligibleCountryList())
                        || getEligibleCountryList().contains(
                        resellerSite.getCustomerInfo().getAddress().getCountryCode()))) {

                    String businessProcessCode = getProcessCodeGenerator().generateFor(
                        PartnercoreConstants.PARTNER_RESELLE_UNIT_PROCESS_CODE
                            + PartnercoreConstants.HYPHEN + cus.getUid()
                            + PartnercoreConstants.HYPHEN + resellerSite.getAccountId()).toString();
                    Map<String, Object> contextParameters = new HashMap<>();
                    contextParameters.put(
                        PartnercoreConstants.PARTNER_RESELLE_UNIT_PROCESS_RESELLER_PAYLOAD_CONTEXT_PARAM,
                        resellerSite);
                    contextParameters.put(
                        PartnercoreConstants.PARTNER_RESELLE_UNIT_PROCESS_CUSTOMER_PAYLOAD_CONTEXT_PARAM,
                        cus);

                    PartnerResellerSiteBusinessProcessModel resellerSiteBusinessProcessModel = getBusinessProcessService().createProcess(
                        businessProcessCode, PartnercoreConstants.PARTNER_RESELLE_UNIT_PROCESS_CODE,
                        contextParameters);
                    resellerSiteBusinessProcessModel.setParentProcess(
                        partnerStoreFrontCustomerProcessModel);
                    getModelService().save(resellerSiteBusinessProcessModel);
                    getBusinessProcessService().startProcess(resellerSiteBusinessProcessModel);
                }

            }
            return Transition.OK;
        }
        return Transition.NOK;
    }

    public KeyGenerator getProcessCodeGenerator() {
        return processCodeGenerator;
    }

    public BusinessProcessService getBusinessProcessService() {
        return businessProcessService;
    }

    public List<String> getEligibleCountryList() {
        return eligibleCountryList;
    }

    public void setEligibleCountryList(final List<String> eligibleCountryList) {
        this.eligibleCountryList = eligibleCountryList;
    }

    public PartnerAccountServiceOutboundIntegrationService getOutboundIntegrationService() {
        return outboundIntegrationService;
    }
}
