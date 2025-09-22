package com.ibm.commerce.partner.core.util.model;


import com.ibm.commerce.partner.core.order.approvalComments.data.response.PartnerQuoteApprovalsInfoResponseData;
import java.util.Date;

public class PartnerQuoteApprovalsInfoResponseDataTestGenerator {
    private static final String TEXT = "test";

    public static PartnerQuoteApprovalsInfoResponseData create()
    {
        final PartnerQuoteApprovalsInfoResponseData partnerQuoteApprovalsInfoResponseData = new PartnerQuoteApprovalsInfoResponseData();
        partnerQuoteApprovalsInfoResponseData.setDateResolved(new Date());
        partnerQuoteApprovalsInfoResponseData.setBpApprovalComment(TEXT);
        return partnerQuoteApprovalsInfoResponseData;
    }
}
