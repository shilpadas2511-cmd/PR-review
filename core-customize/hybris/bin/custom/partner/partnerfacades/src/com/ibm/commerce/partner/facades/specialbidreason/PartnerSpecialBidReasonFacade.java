package com.ibm.commerce.partner.facades.specialbidreason;

import com.ibm.commerce.partner.specialbidreason.data.PartnerSpecialBidReasonData;
import java.util.List;

/**
 * * Facade Interface to get the Special Bid Reason Information
 */
public interface PartnerSpecialBidReasonFacade {

    /**
     *
     * @return
     */
    List<PartnerSpecialBidReasonData> getAllSpecialBidReasonDetails();

    /**
     * Fetches all active SpecialBidReasons and also updates "selected" flag to know which reasons
     * are already selected for the cart.
     *
     * @return
     */
    List<PartnerSpecialBidReasonData> getAllSpecialBidReasonDetailsWithSelection();


    /**
     * Fetches all active SpecialBidReasons and also updates "selected" flag to know which reasons
     * are already selected for the cart.
     */
    void saveInCart(List<PartnerSpecialBidReasonData> specialBidReasons);
}
