/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.ibm.commerce.partner.core.specialbidreason.service.impl;

import com.ibm.commerce.partner.core.model.PartnerSpecialBidReasonModel;
import com.ibm.commerce.partner.core.specialbidreason.dao.PartnerSpecialBidReasonDao;
import com.ibm.commerce.partner.core.specialbidreason.service.PartnerSpecialBidReasonService;
import java.util.List;

/**
 * DefaultPartnerSpecialBidReasonService is used to fetch the specialbid reason model information
 * from dao class.
 */
public class DefaultPartnerSpecialBidReasonService implements PartnerSpecialBidReasonService {

    private final PartnerSpecialBidReasonDao specialBidReasonDao;

    public DefaultPartnerSpecialBidReasonService(
        PartnerSpecialBidReasonDao specialBidReasonDao) {
        this.specialBidReasonDao = specialBidReasonDao;
    }

    /**
     * get all the Special bid reason information
     *
     * @return PartnerSpecialBidReasonModel- send list of PartnerSpecialBidReasonModel
     */
    @Override
    public List<PartnerSpecialBidReasonModel> getAllSpecialBidReasonDetails() {
        return getSpecialBidReasonDao().getAllSpecialBidReasonDetails();
    }

    /**
     * get special bid reason model by code
     *
     * @param code
     * @return
     */
    @Override
    public PartnerSpecialBidReasonModel getSpecialBidReasonById(String code) {
        return getSpecialBidReasonDao().getSpecialBidReasonById(code);
    }

    /**
     * get special bid reason model by code
     *
     * @param codes
     * @return
     */
    @Override
    public List<PartnerSpecialBidReasonModel> getSpecialBidReasonByIds(List<String> codes) {
        return getSpecialBidReasonDao().getSpecialBidReasonByIds(codes);
    }

    public PartnerSpecialBidReasonDao getSpecialBidReasonDao() {
        return specialBidReasonDao;
    }
}
