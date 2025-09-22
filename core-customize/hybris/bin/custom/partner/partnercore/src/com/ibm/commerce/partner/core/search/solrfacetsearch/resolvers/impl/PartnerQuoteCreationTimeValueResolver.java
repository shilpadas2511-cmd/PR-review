package com.ibm.commerce.partner.core.search.solrfacetsearch.resolvers.impl;

import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractValueResolver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;


/**
 * This resolver class is to get the creationtime from quote
 */
public class PartnerQuoteCreationTimeValueResolver extends AbstractValueResolver<IbmPartnerQuoteModel, Object, Object>
{

	/**
	 * This resolver class is to get the creationtime from quote and index attribute
	 *
	 * @param inputDocument
	 * @param indexerBatchContext
	 * @param indexedProperty
	 * @param ibmPartnerQuoteModel
	 * @param valueResolverContext
	 * @throws FieldValueProviderException
	 */
	@Override
	protected void addFieldValues(final InputDocument inputDocument, final IndexerBatchContext indexerBatchContext,
			final IndexedProperty indexedProperty, final IbmPartnerQuoteModel ibmPartnerQuoteModel,
			final ValueResolverContext<Object, Object> valueResolverContext) throws FieldValueProviderException
	{
		String formattedCreationTime;

		try
		{
			final Date creationTime = ibmPartnerQuoteModel.getCreationtime();

			// Parse the full date string first using the correct format
			final SimpleDateFormat dateParseFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");

			// Convert the Date object to String and parse it
			final String creationTimeStr = creationTime.toString(); // This should already be a Date object
			final Date parsedDate = dateParseFormat.parse(creationTimeStr);

			// Format the parsed Date to 'yyyy-MM-dd' with T00:00:00Z to comply with Solr's date field format
			final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			final String formattedDate = dateFormat.format(parsedDate);

			// Ensure the time is set to 00:00:00 and the timezone is UTC ('Z')
			formattedCreationTime = formattedDate + PartnercoreConstants.DEFAULT_QUOTE_SEARCH_TIME_PATTERN; // Add the time and timezone

		}
		catch (final ParseException e)
		{
			throw new FieldValueProviderException("Error formatting creation time", e);
		}

		if (formattedCreationTime != null)
		{
			// Add the formatted date (with T00:00:00Z) to Solr
			inputDocument.addField(indexedProperty, formattedCreationTime);
		}
	}
}
