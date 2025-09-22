package com.ibm.commerce.partner.core.inboundservices.persistence.hook;

import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteStatusModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.inboundservices.persistence.PersistenceContext;
import de.hybris.platform.inboundservices.persistence.hook.PostPersistHook;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.lang3.StringUtils;
import com.ibm.commerce.partner.core.order.strategies.impl.DefaultPartnerQuoteUpdateStateStrategy;


/**
 * Update the Quote state using PostPersistHook based on the values of eccQuoteStatus and
 * cpqQuoteStatus.
 */
public class PartnerQuoteStatusPostPersistHook implements PostPersistHook {

    private final ModelService modelService;
    private final QuoteService quoteService;
    private DefaultPartnerQuoteUpdateStateStrategy quoteUpdateStateStrategy;

    public PartnerQuoteStatusPostPersistHook(final ModelService modelService,
        QuoteService quoteService,
        DefaultPartnerQuoteUpdateStateStrategy quoteUpdateStateStrategy) {
        this.modelService = modelService;
        this.quoteService = quoteService;
        this.quoteUpdateStateStrategy = quoteUpdateStateStrategy;
    }


    public void execute(ItemModel item , PersistenceContext context) {
        if (item instanceof final IbmPartnerQuoteStatusModel statusModel) {
            setQuoteState(statusModel);
        }
    }


    /**
     * Update the Quote state based on the values of eccQuoteStatus and cpqQuoteStatus.
     *
     * @param statusModel
     */
    protected void setQuoteState(final IbmPartnerQuoteStatusModel statusModel) {

        final IbmPartnerQuoteModel quote = ((IbmPartnerQuoteModel) getQuoteService().getCurrentQuoteForCode(
            statusModel.getCode()));

       if (StringUtils.isNotEmpty(statusModel.getCpqQuoteStatus())) {
            getQuoteUpdateStateStrategy().updatePartnerQuoteState(quote, statusModel.getCpqQuoteStatus(),
                statusModel.getEccQuoteStatus());
        }
        quote.setCpqExternalQuoteId(statusModel.getCpqExternalQuoteId());
        quote.setEccQuoteNumber(statusModel.getEccQuoteNumber());
        getModelService().save(quote);
        getModelService().refresh(quote);
    }
    
    public ModelService getModelService() {
        return modelService;
    }

    public QuoteService getQuoteService() {
        return quoteService;
    }
    public DefaultPartnerQuoteUpdateStateStrategy getQuoteUpdateStateStrategy() {
        return quoteUpdateStateStrategy;
    }

}
