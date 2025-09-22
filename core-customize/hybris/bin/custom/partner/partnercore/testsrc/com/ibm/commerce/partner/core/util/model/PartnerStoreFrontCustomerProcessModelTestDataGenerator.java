package com.ibm.commerce.partner.core.util.model;

import com.ibm.commerce.partner.core.model.PartnerResellerSiteBusinessProcessModel;
import com.ibm.commerce.partner.core.model.PartnerStoreFrontCustomerProcessModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import java.util.Collection;

public class PartnerStoreFrontCustomerProcessModelTestDataGenerator {

    public static PartnerStoreFrontCustomerProcessModel createProcessModel(B2BCustomerModel customerModel, Collection<PartnerResellerSiteBusinessProcessModel> subProcess) {
        PartnerStoreFrontCustomerProcessModel processModel = new PartnerStoreFrontCustomerProcessModel();
        processModel.setCustomer(customerModel);
        processModel.setSubProcesses(subProcess);
        return processModel;
    }

}
