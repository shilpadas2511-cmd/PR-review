package com.ibm.commerce.partner.core.inboundservices.persistence.hook;

import com.ibm.commerce.partner.core.inboundservices.persistence.hook.service.PartnerQuoteCreationMapperService;
import com.ibm.commerce.partner.core.model.CpqIbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.inboundservices.persistence.PersistenceContext;
import de.hybris.platform.inboundservices.persistence.hook.PostPersistHook;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.List;


/**
 * create new quote in commerce using the  CpqIbmPartnerQuoteModel
 */
public class PartnerQuoteCreationPostPersistHook implements PostPersistHook {

    private final ModelService modelService;
    private List<PartnerQuoteCreationMapperService<CpqIbmPartnerQuoteModel, IbmPartnerQuoteModel>> partnerQuoteCreationMappers;
    private final QuoteService quoteService;

    public PartnerQuoteCreationPostPersistHook(
        ModelService modelService,
        List<PartnerQuoteCreationMapperService<CpqIbmPartnerQuoteModel, IbmPartnerQuoteModel>> partnerQuoteCreationMappers,
        QuoteService quoteService) {
        this.modelService = modelService;
        this.partnerQuoteCreationMappers = partnerQuoteCreationMappers;
        this.quoteService = quoteService;
    }

    /**
     * Executes the mapping and saving process for a given {@link CpqIbmPartnerQuoteModel} item.
     * @param item the input model to process, expected to be of type {@link CpqIbmPartnerQuoteModel}.
     *             If it is not an instance of {@link CpqIbmPartnerQuoteModel}, the method does nothing.
     * @throws IllegalArgumentException if {@code item} is null.
     */
    public void execute(ItemModel item, PersistenceContext context) {
        if (item instanceof final CpqIbmPartnerQuoteModel cpqIbmPartnerQuote) {
            final IbmPartnerQuoteModel ibmPartnerQuoteModel = getQuoteOrCreate(cpqIbmPartnerQuote);

            getPartnerQuoteCreationMappers().forEach(
                mapper -> mapper.map(cpqIbmPartnerQuote, ibmPartnerQuoteModel));
            ibmPartnerQuoteModel.setCalculated(Boolean.TRUE);
            getModelService().save(ibmPartnerQuoteModel);
        }
    }

    private IbmPartnerQuoteModel getQuoteOrCreate(CpqIbmPartnerQuoteModel cpqIbmPartnerQuote) {

        IbmPartnerQuoteModel ibmPartnerQuoteModel;
        try {
            ibmPartnerQuoteModel = ((IbmPartnerQuoteModel) getQuoteService().getCurrentQuoteForCode(
                cpqIbmPartnerQuote.getCode()));
        } catch (ModelNotFoundException e) {
            ibmPartnerQuoteModel = getModelService().create(IbmPartnerQuoteModel.class);
            ibmPartnerQuoteModel.setVersion(1);
            return ibmPartnerQuoteModel;
        }

        return ibmPartnerQuoteModel;
    }

    public List<PartnerQuoteCreationMapperService<CpqIbmPartnerQuoteModel, IbmPartnerQuoteModel>> getPartnerQuoteCreationMappers() {
        return partnerQuoteCreationMappers;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public QuoteService getQuoteService() {
        return quoteService;
    }

}
