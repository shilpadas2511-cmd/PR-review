package com.ibm.commerce.partner.core.interceptor;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.RemoveInterceptor;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.log4j.Logger;

/**
 * Custom implementation of {@link PartnerCartRemoveInterceptor} to handle deletion
 * of {@link IbmPartnerCartModel}.
 */
public class PartnerCartRemoveInterceptor implements
    RemoveInterceptor<IbmPartnerCartModel> {

    private static final Logger LOG = Logger.getLogger(
        PartnerCartRemoveInterceptor.class);

    @Override
    public void onRemove(IbmPartnerCartModel ibmPartnerCartModel,
        InterceptorContext interceptorContext) {

        StringBuilder builder = new StringBuilder();

        builder.append(String.format(
            PartnercoreConstants.CART_DELETION,
            ibmPartnerCartModel.getCode(),
            ibmPartnerCartModel.getUser().getUid(),
            ibmPartnerCartModel.getQuoteReference() != null
                ? ibmPartnerCartModel.getQuoteReference().getCode() : StringUtils.EMPTY,
            DateFormatUtils.format(new Date(), PartnercoreConstants.PRODUCT_CONFIG_DELETION_TS)
        ));

        LOG.info(builder.toString());

    }
}