package com.ibm.commerce.partner.core.event.solr;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;
import de.hybris.platform.solrfacetsearch.enums.IndexerOperationValues;
import java.util.List;

/**
 * Event for Hot Update or Delete Of Items
 */
public class ItemHotIndexUpdateEvent extends AbstractEvent {

    private final String facetIndexName;
    private final String indexTypeName;
    private final IndexerOperationValues indexOperation;
    private final List<? extends ItemModel> itemModels;

    public ItemHotIndexUpdateEvent(final String facetIndexName, final String indexTypeName,
        final IndexerOperationValues indexOperation, final List<? extends ItemModel> itemModels) {
        this.facetIndexName = facetIndexName;
        this.indexTypeName = indexTypeName;
        this.indexOperation = indexOperation;
        this.itemModels = itemModels;
    }

    public List<? extends ItemModel> getItemModels() {
        return itemModels;
    }

    public IndexerOperationValues getIndexOperation() {
        return indexOperation;
    }

    public String getFacetIndexName() {
        return facetIndexName;
    }

    public String getIndexTypeName() {
        return indexTypeName;
    }
}
