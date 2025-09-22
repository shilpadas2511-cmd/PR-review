package com.ibm.commerce.partner.core.event.solr.listener;

import com.ibm.commerce.partner.core.event.solr.ItemHotIndexUpdateEvent;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfigService;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.exceptions.FacetConfigServiceException;
import de.hybris.platform.solrfacetsearch.enums.IndexerOperationValues;
import de.hybris.platform.solrfacetsearch.indexer.IndexerService;
import de.hybris.platform.solrfacetsearch.indexer.exceptions.IndexerException;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Event Listener for {@link ItemHotIndexUpdateEvent}
 */
public class PartnerItemHotIndexUpdateEventListener extends
    AbstractEventListener<ItemHotIndexUpdateEvent> {

    private static final Logger LOG = Logger.getLogger(
        PartnerItemHotIndexUpdateEventListener.class);
    private final FacetSearchConfigService facetSearchConfigService;
    private final IndexerService indexerService;

    public PartnerItemHotIndexUpdateEventListener(
        final FacetSearchConfigService facetSearchConfigService,
        final IndexerService indexerService) {
        this.facetSearchConfigService = facetSearchConfigService;
        this.indexerService = indexerService;
    }

    @Override
    protected void onEvent(final ItemHotIndexUpdateEvent event) {

        try {
            final FacetSearchConfig facetSearchConfig = getFacetSearchConfigService().getConfiguration(
                event.getFacetIndexName());

            if (facetSearchConfig == null) {
                return;
            }
            IndexerOperationValues indexerOperation = event.getIndexOperation();
            IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes()
                .get(event.getIndexTypeName());
            List<PK> pks = event.getItemModels().stream().map(ItemModel::getPk).toList();
            switch (event.getIndexOperation()) {
                case UPDATE ->
                    getIndexerService().updateTypeIndex(facetSearchConfig, indexedType, pks);
                case DELETE ->
                    getIndexerService().deleteTypeIndex(facetSearchConfig, indexedType, pks);
                default -> throw new IndexerException(
                    "Unsupported indexer operation: " + indexerOperation);
            }

        } catch (FacetConfigServiceException | IndexerException e) {
            LOG.error(String.format("HotUpdate failed for items: %s", event.getItemModels()), e);
            throw new RuntimeException(e);
        }
    }

    public FacetSearchConfigService getFacetSearchConfigService() {
        return facetSearchConfigService;
    }

    public IndexerService getIndexerService() {
        return indexerService;
    }
}
