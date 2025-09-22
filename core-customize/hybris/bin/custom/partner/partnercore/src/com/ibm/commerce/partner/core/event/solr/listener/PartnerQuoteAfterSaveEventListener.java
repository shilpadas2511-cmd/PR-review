package com.ibm.commerce.partner.core.event.solr.listener;

import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.outbound.service.PartnerCommerceQuoteService;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.integrationservices.service.ItemModelSearchService;
import de.hybris.platform.solrfacetsearch.enums.IndexerOperationValues;
import de.hybris.platform.tx.AfterSaveEvent;
import de.hybris.platform.tx.AfterSaveListener;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.collections4.CollectionUtils;

/**
 * After Save Listner
 */
public class PartnerQuoteAfterSaveEventListener implements AfterSaveListener {

    private final ItemModelSearchService itemModelSearchService;
    private final PartnerCommerceQuoteService commerceQuoteService;

    public PartnerQuoteAfterSaveEventListener(final ItemModelSearchService itemModelSearchService,
        final PartnerCommerceQuoteService commerceQuoteService) {
        this.itemModelSearchService = itemModelSearchService;
        this.commerceQuoteService = commerceQuoteService;
    }


    @Override
    public void afterSave(final Collection<AfterSaveEvent> collection) {
        if (CollectionUtils.isNotEmpty(collection)) {
            final List<QuoteModel> quotes = collection.stream().map(this::getQuoteModel)
                .filter(Objects::nonNull).toList();

            if (CollectionUtils.isNotEmpty(quotes)) {
                QuoteModel[] quoteArray = new QuoteModel[quotes.size()];
                getCommerceQuoteService().triggerQuoteIndex(IndexerOperationValues.UPDATE,
                    quotes.toArray(quoteArray));
            }
        }
    }

    protected QuoteModel getQuoteModel(final AfterSaveEvent afterSaveEvent) {
        final Optional<ItemModel> itemModel = itemModelSearchService.nonCachingFindByPk(
            afterSaveEvent.getPk());
        if (itemModel.isPresent()) {
            final ItemModel model = itemModel.get();
            if (model instanceof CartModel cartModel && cartModel.getQuoteReference() != null) {
                return cartModel.getQuoteReference();
            }
            if (model instanceof OrderModel orderModel && orderModel.getQuoteReference() != null) {
                return orderModel.getQuoteReference();
            }

            if (model instanceof IbmPartnerQuoteModel partnerQuote
                && IbmPartnerQuoteModel._TYPECODE.equals(partnerQuote.getItemtype())) {
                return partnerQuote;
            }
        }
        return null;


    }

    public PartnerCommerceQuoteService getCommerceQuoteService() {
        return commerceQuoteService;
    }


    public ItemModelSearchService getItemModelSearchService() {
        return itemModelSearchService;
    }
}
