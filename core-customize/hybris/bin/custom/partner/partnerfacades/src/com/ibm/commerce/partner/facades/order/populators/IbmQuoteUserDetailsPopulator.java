package com.ibm.commerce.partner.facades.order.populators;

import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import de.hybris.platform.commercefacades.quote.data.QuoteData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

/**
 * Populator class for QuoteCreater and QuoteSubmitter
 */
public class IbmQuoteUserDetailsPopulator implements Populator<IbmPartnerQuoteModel, QuoteData> {

    public IbmQuoteUserDetailsPopulator(Converter<UserModel, CustomerData> b2bCustomerConverter) {
        this.b2bCustomerConverter = b2bCustomerConverter;
    }

    private Converter<UserModel, CustomerData> b2bCustomerConverter;

    /**
     * populate method to populate QuoteData userDetail from IbmPartnerQuoteModel
     *
     * @param ibmPartnerQuoteModel the source object
     * @param quoteData            the target to fill
     * @throws ConversionException
     */
    @Override
    public void populate(IbmPartnerQuoteModel ibmPartnerQuoteModel, QuoteData quoteData)
        throws ConversionException {
        if (ibmPartnerQuoteModel.getCreator() != null) {
            CustomerData quoteCreaterData = getB2bCustomerConverter().convert(
                ibmPartnerQuoteModel.getCreator());
            quoteData.setQuoteCreater(quoteCreaterData);
        }
        if (ibmPartnerQuoteModel.getSubmitter() != null) {
            CustomerData quoteSubmitterData = getB2bCustomerConverter().convert(
                ibmPartnerQuoteModel.getSubmitter());
            quoteData.setQuoteSubmitter(quoteSubmitterData);
        }
    }

    public Converter<UserModel, CustomerData> getB2bCustomerConverter() {
        return b2bCustomerConverter;
    }

}
