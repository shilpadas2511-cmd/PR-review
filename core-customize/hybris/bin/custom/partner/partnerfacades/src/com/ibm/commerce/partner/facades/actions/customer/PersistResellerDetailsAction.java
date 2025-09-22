package com.ibm.commerce.partner.facades.actions.customer;

import com.ibm.commerce.partner.company.data.IbmPartnerB2BUnitData;
import com.ibm.commerce.partner.core.actions.PartnerAbstractSimpleDecisionAction;
import com.ibm.commerce.partner.core.company.reseller.data.response.PartnerResellerSiteIdResponseData;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.cronjob.PartnerB2BCustomerJob;
import com.ibm.commerce.partner.core.model.PartnerResellerSiteBusinessProcessModel;
import com.ibm.commerce.partner.facades.company.PartnerB2BUnitFacade;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.model.BusinessProcessParameterModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.task.RetryLaterException;
import java.util.Optional;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;

/**
 * Persiste Reseller Details
 */
public class PersistResellerDetailsAction extends
    PartnerAbstractSimpleDecisionAction<PartnerResellerSiteBusinessProcessModel> {

    private static final Logger LOG = Logger.getLogger(PersistResellerDetailsAction.class);
    private final Converter<PartnerResellerSiteIdResponseData, IbmPartnerB2BUnitData> partnerB2BUnitDataConverter;


    private final PartnerB2BUnitFacade partnerB2BUnitFacade;


    protected PersistResellerDetailsAction(final Integer maxRetryAllowed, final Integer retryDelay,
        final Converter<PartnerResellerSiteIdResponseData, IbmPartnerB2BUnitData> partnerB2BUnitDataConverter,
        final PartnerB2BUnitFacade partnerB2BUnitFacade) {
        super(maxRetryAllowed, retryDelay);
        this.partnerB2BUnitDataConverter = partnerB2BUnitDataConverter;
        this.partnerB2BUnitFacade = partnerB2BUnitFacade;
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
            IbmPartnerB2BUnitData partnerB2BUnitData = getPartnerB2BUnitDataConverter().convert(
                resellerSiteIdResponseData);
            B2BUnitModel b2BUnitModel = getPartnerB2BUnitFacade().getOrCreate(partnerB2BUnitData);

            if (b2BUnitModel != null) {
                processModel.setUnit(b2BUnitModel);
                getModelService().saveAll(processModel, b2BUnitModel);
                return Transition.OK;
            }
        }
        LOG.error(
            String.format("Error occurred in the Reseller business process [%s] ", processModel.getCode()));
        return Transition.NOK;
    }

    protected PartnerResellerSiteIdResponseData getResellerData(
        final PartnerResellerSiteBusinessProcessModel processModel) {
        // Fetch the process parameter having distributor data with reseller
        Optional<BusinessProcessParameterModel> optionalProcessParam = processModel.getContextParameters()
            .stream().filter(param -> param.getName().equals(
                PartnercoreConstants.PARTNER_RESELLE_UNIT_PROCESS_RESELLER_DISTRIBUTOR_PAYLOAD_CONTEXT_PARAM))
            .findAny();
        if (optionalProcessParam.isPresent() && optionalProcessParam.get()
            .getValue() instanceof PartnerResellerSiteIdResponseData partnerResellerSiteIdResponseData) {
            return partnerResellerSiteIdResponseData;
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


    public Converter<PartnerResellerSiteIdResponseData, IbmPartnerB2BUnitData> getPartnerB2BUnitDataConverter() {
        return partnerB2BUnitDataConverter;
    }

    public PartnerB2BUnitFacade getPartnerB2BUnitFacade() {
        return partnerB2BUnitFacade;
    }

}
