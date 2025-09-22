package com.ibm.commerce.partner.facades.quote;

import com.ibm.commerce.partnerwebservicescommons.dto.search.request.PartnerImportQuoteFileRequestData;
import de.hybris.platform.commercefacades.comment.data.CommentData;
import de.hybris.platform.commercefacades.order.QuoteFacade;
import de.hybris.platform.commercefacades.quote.data.QuoteData;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.QuoteModel;
import java.util.List;

/**
 * Extends {@link QuoteFacade}
 */
public interface PartnerQuoteFacade extends QuoteFacade {


    List<CommentData> fetchApprovalComments(final String quoteId);

    QuoteData getCloneQuote(QuoteModel quoteModel, String name);

    void updateQuotestatus(QuoteModel quoteModel, QuoteState state);

    /**
     *
     * @param fileRequestData
     * @return QuoteData
     */

    QuoteData createImportedQuote(PartnerImportQuoteFileRequestData fileRequestData);
}
