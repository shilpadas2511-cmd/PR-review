package com.ibm.commerce.partner.occ.mapping.mappers;

import com.ibm.commerce.partner.company.data.IbmB2BUnitData;
import com.ibm.commerce.partner.enums.data.DisplayTypeData;
import com.ibm.commerce.partnerwebservicescommons.company.dto.IbmB2BUnitWsDTO;
import com.ibm.commerce.partnerwebservicescommons.enums.dto.DisplayTypeWsDTO;
import de.hybris.platform.webservicescommons.mapping.mappers.AbstractCustomMapper;
import ma.glasnost.orika.MappingContext;

/**
 * Mapper for IbmB2bUnitDataMapper
 */
public class IbmB2BUnitDataMapper<A extends IbmB2BUnitWsDTO, B extends IbmB2BUnitData> extends
    AbstractCustomMapper<A, B> {

    @Override
    public void mapAtoB(A source, B target, MappingContext context) {
        if (source.getType() != null) {
            target.setType(getDisplayTypeData(source.getType()));
        }
    }

    protected DisplayTypeData getDisplayTypeData(DisplayTypeWsDTO wsDTO) {
        DisplayTypeData displayTypeData = new DisplayTypeData();
        displayTypeData.setCode(wsDTO.getCode());
        displayTypeData.setName(wsDTO.getName());
        return displayTypeData;
    }
}
