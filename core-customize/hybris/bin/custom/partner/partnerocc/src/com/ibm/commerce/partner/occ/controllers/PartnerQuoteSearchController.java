/**
 *
 */
package com.ibm.commerce.partner.occ.controllers;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import de.hybris.platform.b2bocc.security.SecuredAccessConstants;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commercewebservicescommons.dto.search.pagedata.PaginationWsDTO;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.occ.v2.helper.PartnerQuoteSearchHelper;
import com.ibm.commerce.partnerwebservicescommons.dto.search.facetdata.PartnerQuoteSearchPageWsDTO;
import com.ibm.commerce.partnerwebservicescommons.dto.search.request.PartnerQuoteSearchRequestWsDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;


/**
 * Dedicated controller for Quotes search functionality using solr
 */

@RestController
@RequestMapping(value = "/{baseSiteId}/users/{userId}/quotes")
@ApiVersion("v2")
@Tag(name = "Partner Quotes")
public class PartnerQuoteSearchController extends PartnerBaseController
{


	@Resource(name = "partnerQuoteSearchHelper")
	protected PartnerQuoteSearchHelper quoteSearchHelper;
	@Resource(name = "timeService")
	private TimeService timeService;

	@GetMapping(value = "/search",produces = APPLICATION_JSON_VALUE)
	@Operation(operationId = "getQuotes", summary = "Retrieves a list of quotes.",
			description = "Retrieves a list of quotes and related search data, such as available facets, available sorting, and suggestions."
					+ " To enable suggestions, you need to have indexed properties configured to be used for the search.")
	@ApiBaseSiteIdAndUserIdParam
	@Secured(
	{ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
	public PartnerQuoteSearchPageWsDTO getQuotes(@Parameter(
			description = "Formatted query string. It contains query criteria like free text search, facet. The format is <freeTextSearch>:<sort>:<facetKey1>:<facetValue1>:...:<facetKeyN>:<facetValueN>.")
	@RequestParam(required = false)
	final String q, @Parameter(description = "Current result page. Default value is 0.")
	@RequestParam(defaultValue = DEFAULT_CURRENT_PAGE)
	final int currentPage, @Parameter(description = "Number of results returned per page.")
	@RequestParam(defaultValue = DEFAULT_PAGE_SIZE)
	final int pageSize, @Parameter(description = "Sorting method applied to the return results.")
	@RequestParam(required = false)
	final String sort,
			@Parameter(
					description = "Name of the search query template to be used in the search query. Examples: DEFAULT, SUGGESTIONS.")
			@RequestParam(required = false)
			final String searchQueryContext, @Parameter(description = "fromDate filter applied as a filter query")
			@RequestParam(required = false)
			final String fromDate, @Parameter(description = "toDate filter applied as a filter query")
			@RequestParam(required = false)
			final String toDate, @Parameter(description = "quoteType filter applied as a filter query")
			@RequestParam(required = false)
			final String quoteType,

			@ApiFieldsParam
			@RequestParam(defaultValue = DEFAULT_FIELD_SET)
			final String fields, final HttpServletResponse response) throws ParseException
	{

		final PartnerQuoteSearchRequestWsDTO searchRequestWsDTO = createRequest(fromDate, toDate, quoteType);
		final PartnerQuoteSearchPageWsDTO result = quoteSearchHelper.searchQuotes(q, searchRequestWsDTO, currentPage, pageSize,
				sort, addPaginationField(fields), searchQueryContext);

		setTotalCountHeader(response, result.getPagination());

		return result;
	}

	protected PartnerQuoteSearchRequestWsDTO createRequest(final String fromDate, final String toDate, final String quoteType)
	{
		final PartnerQuoteSearchRequestWsDTO requestWsDTO = new PartnerQuoteSearchRequestWsDTO();
		if (StringUtils.isNotBlank(fromDate))
		{
			requestWsDTO.setFromDate(fromDate);
			if (StringUtils.isBlank(toDate))
			{
				requestWsDTO.setToDate(
						DateFormatUtils.format(timeService.getCurrentTime(), PartnercoreConstants.DEFAULT_QUOTE_SEARCH_DATE_PATTERN));
			}
			else
			{
				requestWsDTO.setToDate(toDate);
			}

		}

		requestWsDTO.setQuoteType(quoteType);
		return requestWsDTO;
	}

	private Date convertStringToDate(final String toDate) throws ParseException
	{
		return new SimpleDateFormat(PartnercoreConstants.DEFAULT_QUOTE_SEARCH_DATE_PATTERN).parse(toDate);
	}

	protected void setTotalCountHeader(final HttpServletResponse response, final PaginationWsDTO paginationDto)
	{
		if (paginationDto != null && paginationDto.getTotalResults() != null)
		{
			response.setHeader(HEADER_TOTAL_COUNT, String.valueOf(paginationDto.getTotalResults()));
		}
	}
}

