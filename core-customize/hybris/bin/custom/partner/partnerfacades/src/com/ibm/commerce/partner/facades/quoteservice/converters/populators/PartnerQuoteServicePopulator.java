package com.ibm.commerce.partner.facades.quoteservice.converters.populators;

import com.ibm.commerce.partner.company.data.IbmB2BUnitData;
import com.ibm.commerce.partner.core.company.data.response.PartnerSiteIdResponseData;
import com.ibm.commerce.partner.core.order.approvalComments.data.response.PartnerQuoteApprovalsInfoResponseData;
import de.hybris.platform.commercefacades.comment.data.CommentData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import io.github.resilience4j.core.lang.NonNull;

/**
 * Populates {@link CommentData} from {@link PartnerQuoteApprovalsInfoResponseData}
 */
public class PartnerQuoteServicePopulator implements
    Populator<PartnerQuoteApprovalsInfoResponseData, CommentData> {

    /**
     *
     * @param partnerQuoteApprovalsInfoResponseData the source object
     * @param commentData the target to fill
     * @throws ConversionException
     */
    @Override
    public void populate(
        @NonNull PartnerQuoteApprovalsInfoResponseData partnerQuoteApprovalsInfoResponseData,
        CommentData commentData) throws ConversionException {
        commentData.setText(partnerQuoteApprovalsInfoResponseData.getBpApprovalComment());
        commentData.setCreationDate(partnerQuoteApprovalsInfoResponseData.getDateResolved());
    }
}
