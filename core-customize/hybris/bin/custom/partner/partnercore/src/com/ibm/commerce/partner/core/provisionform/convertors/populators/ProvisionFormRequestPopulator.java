package com.ibm.commerce.partner.core.provisionform.convertors.populators;

import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.quote.provision.form.data.request.ProvisionFormRequestData;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ProvisionFormRequestPopulator. It used to prepare the provision form request object with email
 * information.
 */
public class ProvisionFormRequestPopulator implements
    Populator<AbstractOrderModel, ProvisionFormRequestData> {

    private final CustomerEmailResolutionService customerEmailResolutionService;

    public ProvisionFormRequestPopulator(
        CustomerEmailResolutionService customerEmailResolutionService) {
        this.customerEmailResolutionService = customerEmailResolutionService;
    }

    /*
     * populate the cart model object to ProvisionFormRequestData object.
     * @param  source
     * @param target
     * */
    @Override
    public void populate(AbstractOrderModel source, ProvisionFormRequestData target)
        throws ConversionException {
        if (source instanceof IbmPartnerCartModel cartModel) {
            setEditorEmailsQuote(target, cartModel, source);
        }
    }

    protected void setEditorEmailsQuote(ProvisionFormRequestData target,
        IbmPartnerCartModel cartModel, AbstractOrderModel source) {

        String userEmail = getUserEmail(cartModel);

        List<String> emailList = Stream.of(userEmail)
            .filter(email -> email != null)
            .collect(Collectors.toList());

        target.setAllowedEditorEmails(emailList);
    }

    protected String getUserEmail(IbmPartnerCartModel cartModel) {
        return Optional.ofNullable(cartModel.getUser())
            .filter(user -> user instanceof B2BCustomerModel)
            .map(user -> getCustomerEmailResolutionService().getEmailForCustomer(
                (B2BCustomerModel) user))
            .orElse(null);
    }

    public CustomerEmailResolutionService getCustomerEmailResolutionService() {
        return customerEmailResolutionService;
    }

}
