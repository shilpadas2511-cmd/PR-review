package com.ibm.commerce.partner.core.util.model;

import com.ibm.commerce.partner.core.model.PartnerResellerSiteBusinessProcessModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.processengine.enums.ProcessState;
import de.hybris.platform.processengine.model.BusinessProcessParameterModel;
import java.util.Collection;
import java.util.Optional;

public class PartnerResellerSiteBusinessProcessModelTestDataGenerator {

    public static PartnerResellerSiteBusinessProcessModel createResellerProcessModel(ProcessState processState, B2BUnitModel unitModel) {
        PartnerResellerSiteBusinessProcessModel resellerSiteBusinessProcessModel = new PartnerResellerSiteBusinessProcessModel();
        resellerSiteBusinessProcessModel.setState(processState);
        resellerSiteBusinessProcessModel.setUnit(unitModel);
        return resellerSiteBusinessProcessModel;
    }

    public static PartnerResellerSiteBusinessProcessModel createContextParams(
        Collection<BusinessProcessParameterModel> optionalProcessParam) {
        PartnerResellerSiteBusinessProcessModel processModel = new PartnerResellerSiteBusinessProcessModel();
        processModel.setContextParameters(optionalProcessParam);
        return processModel;
    }

}
