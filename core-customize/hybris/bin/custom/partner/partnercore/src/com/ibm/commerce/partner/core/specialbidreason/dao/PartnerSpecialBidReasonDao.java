/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.core.specialbidreason.dao;

import com.ibm.commerce.partner.core.model.PartnerSpecialBidReasonModel;
import java.util.List;

/**
 * * DAO Interface to get the Special Bid Reason Information
 */
public interface PartnerSpecialBidReasonDao {

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
     * @return  PartnerSpecialBidReasonModel
     */
    PartnerSpecialBidReasonModel getSpecialBidReasonById(String code);

    /**
     * get special bid reason information by codes
     *
     * @param codes
     * @return  List of PartnerSpecialBidReasonModel
     */
    List<PartnerSpecialBidReasonModel> getSpecialBidReasonByIds(List<String> codes);

}