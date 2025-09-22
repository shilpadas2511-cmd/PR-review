package com.ibm.commerce.partner.core.quote.services;

import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.order.approvalComments.data.response.PartnerQuoteApprovalsInfoResponseData;
import com.ibm.commerce.partner.core.outboundservices.quote.data.response.PartnerCPQSubmitQuoteResponseData;
import com.ibm.commerce.partner.core.outboundservices.quote.data.response.PartnerCPQValidateQuoteResponseData;
import com.ibm.commerce.partner.core.outboundservices.quote.data.response.PartnerCPQCreateQuoteResponseData;
import java.util.List;

/**
 * This interface is integrating to CPQ Quote Service
 */
public interface PartnerSapCpqQuoteService {

    /**
     *
     * @param cpqExternalId
     * @return List<PartnerQuoteApprovalsInfoResponseData>
     */
    List<PartnerQuoteApprovalsInfoResponseData> fetchApprovalCommentsforQuote(String cpqExternalId);

    /**
     * Sends collaborator information for the given IBM Partner Quote Model to CPQ.
     *
     * @param ibmPartnerQuoteModel The IBM Partner Quote Model containing collaborator details.
     */
    void postCollaboratorInfo(IbmPartnerQuoteModel ibmPartnerQuoteModel);

    /**
     * Validates the given partner quote with the external CPQ (Configure, Price, Quote) system.
     * <p>
     * The response indicates whether the quote is valid and may include details of any failed
     * validations.
     *
     * @param ibmPartnerQuoteModel the partner quote to validate
     * @return the CPQ validation response
     */
    PartnerCPQValidateQuoteResponseData cpqQuoteValidation(
        IbmPartnerQuoteModel ibmPartnerQuoteModel);

    /**
     * Submits the given partner quote to the external CPQ (Configure, Price, Quote) system.
     * <p>
     * The response contains submission status and identifiers returned by CPQ.
     *
     * @param ibmPartnerQuoteModel the partner quote to submit
     * @return the CPQ submission response
     */
    PartnerCPQSubmitQuoteResponseData cpqQuoteSubmit(IbmPartnerQuoteModel ibmPartnerQuoteModel);


    /**
     * Sends the updated list of collaborator emails from the given {@link IbmPartnerCartModel} to
     * the external CPQ (Configure Price Quote) system.
     *
     * <p>This method is typically invoked after updating the collaborator emails
     * in the cart and is responsible for syncing those collaborators with CPQ.</p>
     *
     * @param ibmPartnerCartModel the cart model containing the updated list of collaborator emails;
     *                            must not be null and must have a valid quote reference
     */
    void addCollaboratorsToCpq(IbmPartnerCartModel ibmPartnerCartModel);

    /**
    /**
     * Creates a quote in the CPQ system using the provided {@link IbmPartnerCartModel}.
     *
     * @param cart the {@link IbmPartnerCartModel} cart data
     * @return a {@link PartnerCPQCreateQuoteResponseData} object containing the response
     */
    PartnerCPQCreateQuoteResponseData createQuoteInCPQ(IbmPartnerCartModel cart);
    /*
     * Removes the specified collaborators from the given CPQ quote.
     *
     * <p>This method is expected to trigger an outbound call to the CPQ system using
     * the provided {@link IbmPartnerCartModel}, removing the collaborators identified
     * by their email addresses.</p>
     *
     * @param ibmPartnerCartModel the partner cart model containing quote details
     * @param removedEmails the list of email addresses representing collaborators to be removed
     */
    void removeCollaboratorsToCpq(IbmPartnerCartModel ibmPartnerCartModel,List<String> removedEmails);

}