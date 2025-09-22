package com.ibm.commerce.partner.occ.mapping.mappers;

import com.ibm.commerce.partner.company.endcustomer.data.IbmPartnerEndCustomerB2BUnitData;
import com.ibm.commerce.partnerwebservicescommons.company.endcustomer.dto.IbmPartnerEndCustomerB2BUnitWsDTO;
import ma.glasnost.orika.MappingContext;

/**
 * Mapper for {@link IbmPartnerEndCustomerB2BUnitData}
 */
public class IbmPartnerEndCustomerB2BUnitDataMapper extends
    IbmB2BUnitDataMapper<IbmPartnerEndCustomerB2BUnitWsDTO, IbmPartnerEndCustomerB2BUnitData> {

    @Override
    public void mapAtoB(IbmPartnerEndCustomerB2BUnitWsDTO source,
        IbmPartnerEndCustomerB2BUnitData target, MappingContext context) {
        super.mapAtoB(source, target, context);
        target.setGoe(source.isGoe());
    }
}
