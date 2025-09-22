package com.ibm.commerce.partner.facades.populators;

import com.ibm.commerce.partner.data.solrindexer.SolrIndexJobResponseData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.solrfacetsearch.model.indexer.cron.SolrIndexerCronJobModel;
import org.apache.commons.lang3.StringUtils;

/**
 * Populator implementation that transfers data from a {@link SolrIndexerCronJobModel} to a
 * {@link SolrIndexJobResponseData}.
 * <p>
 * This class is used to populate relevant cron job data such as code, timestamps, status, and
 * result into a response data object used for external representation (e.g., REST responses).
 * </p>
 */
public class PartnerSolrJobStatusPopulator implements
    Populator<SolrIndexerCronJobModel, SolrIndexJobResponseData> {

    /**
     * Populates the target {@link SolrIndexJobResponseData} with values from the source
     * {@link SolrIndexerCronJobModel}.
     * <p>
     * Fields populated include the cron job code, creation date, last modified date, start and end
     * times, execution status, and result.
     * </p>
     *
     * @param source the source {@link SolrIndexerCronJobModel} containing the cron job data
     * @param target the target {@link SolrIndexJobResponseData} to be populated
     * @throws ConversionException if an error occurs during population (not currently thrown in
     *                             this implementation)
     */
    @Override
    public void populate(SolrIndexerCronJobModel source,
        SolrIndexJobResponseData target) throws ConversionException {
        if (StringUtils.isNotBlank(source.getCode())) {
            target.setCode(source.getCode());
        }
        if (null != source.getCreationtime()) {
            target.setCreationDate(source.getCreationtime());
        }
        if (null != source.getModifiedtime()) {
            target.setLastModifiedDate(source.getModifiedtime());
        }
        if (null != source.getStartTime()) {
            target.setStartDate(source.getStartTime());
        }
        if (null != source.getEndTime()) {
            target.setEndDate(source.getEndTime());
        }
        if (null != source.getStatus()) {
            target.setSyncStatus(source.getStatus().getCode());
        }
        if (null != source.getResult()) {
            target.setSyncResult(source.getResult().getCode());
        }
    }
}
