package com.ibm.commerce.partner.occ.mapping.mappers;

import com.ibm.commerce.partner.company.data.IbmB2BUnitData;
import com.ibm.commerce.partnerwebservicescommons.company.dto.IbmB2BUnitWsDTO;
import com.ibm.commerce.partnerwebservicescommons.enums.dto.DisplayTypeWsDTO;
import de.hybris.bootstrap.annotations.UnitTest;
import ma.glasnost.orika.MappingContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

@UnitTest
public class IbmB2BUnitDataMapperTest {
    private static final String DISPLAY_CODE = "";
    private static final String DISPLAY_NAME = "";

    @InjectMocks
    IbmB2BUnitDataMapper ibmB2BUnitDataMapper;
    IbmB2BUnitWsDTO ibmB2BUnitWsDTO;

    IbmB2BUnitData ibmB2BUnitData;
    MappingContext context;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ibmB2BUnitWsDTO = new IbmB2BUnitWsDTO();
        DisplayTypeWsDTO displayTypeWsDTO = new DisplayTypeWsDTO();
        displayTypeWsDTO.setCode(DISPLAY_CODE);
        displayTypeWsDTO.setName(DISPLAY_NAME);
        ibmB2BUnitWsDTO.setType(displayTypeWsDTO);
        ibmB2BUnitDataMapper = new IbmB2BUnitDataMapper();
        ibmB2BUnitData = new IbmB2BUnitData();
    }

    @Test
    public void testMapAtoB() {
        DisplayTypeWsDTO displayTypeWsDTO = new DisplayTypeWsDTO();
        displayTypeWsDTO.setCode(DISPLAY_CODE);
        displayTypeWsDTO.setName(DISPLAY_NAME);
        ibmB2BUnitDataMapper.mapAtoB(ibmB2BUnitWsDTO, ibmB2BUnitData, context);
        Assert.assertEquals(DISPLAY_CODE, ibmB2BUnitData.getType().getCode());
        Assert.assertEquals(DISPLAY_NAME, ibmB2BUnitData.getType().getName());
    }
}
