package com.ibm.commerce.partner.core.outbound.service.impl;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.outbound.service.PartnerCpqCpiQuoteCustomerMapperService;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteCustomerModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import java.util.Set;


public class DefaultPartnerCpqCpiQuoteOwnerMapperService implements
    PartnerCpqCpiQuoteCustomerMapperService<IbmPartnerQuoteModel, Set<SAPCPQOutboundQuoteCustomerModel>> {

    private final CustomerEmailResolutionService customerEmailResolutionService;

    public DefaultPartnerCpqCpiQuoteOwnerMapperService(
        final CustomerEmailResolutionService customerEmailResolutionService) {
        this.customerEmailResolutionService = customerEmailResolutionService;
    }


    @Override
    public void map(IbmPartnerQuoteModel quoteModel,
        Set<SAPCPQOutboundQuoteCustomerModel> sapcpqOutboundQuoteCustomerModels) {

        SAPCPQOutboundQuoteCustomerModel sapcpqOutboundQuoteCustomerModel = new SAPCPQOutboundQuoteCustomerModel();
        if (quoteModel.getCreator() instanceof B2BCustomerModel customerModel) {
            sapcpqOutboundQuoteCustomerModel.setCustomerCode(
                getCustomerEmailResolutionService().getEmailForCustomer(customerModel));

            sapcpqOutboundQuoteCustomerModel.setName(quoteModel.getCreator().getName());
        }
        sapcpqOutboundQuoteCustomerModel.setRoleType(PartnercoreConstants.QUOTE_OWNER_CPQ);

        sapcpqOutboundQuoteCustomerModels.add(sapcpqOutboundQuoteCustomerModel);

    }

    public CustomerEmailResolutionService getCustomerEmailResolutionService() {
        return customerEmailResolutionService;
    }
}
