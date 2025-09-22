/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.core.specialbidreason.service;

import com.ibm.commerce.partner.core.model.PartnerSpecialBidReasonModel;
import java.util.List;

/**
 * * Service Interface to get the Special Bid Reason Information
 */
public interface PartnerSpecialBidReasonService {

    /**
     * get all special bid reason information
     *
     * @return list of PartnerSpecialBidReasonModel
     */
    List<PartnerSpecialBidReasonModel> getAllSpecialBidReasonDetails();

    /**
     * get special bid reason information by code
     *
     * @param code
     * @return
     */
    PartnerSpecialBidReasonModel getSpecialBidReasonById(String code);


    /**
     * get special bid reason information by code
     *
     * @param codes
     * @return
     */
    List<PartnerSpecialBidReasonModel> getSpecialBidReasonByIds(List<String> codes);

}
