package com.ibm.commerce.partner.occ.mapping.mappers;

import com.ibm.commerce.partner.company.endcustomer.data.IbmPartnerEndCustomerB2BUnitData;
import com.ibm.commerce.partner.facades.util.IbmPartnerEndCustomerB2BUnitTestDataGenerator;
import com.ibm.commerce.partner.facades.util.IbmPartnerEndCustomerB2BUnitWsDTOTestDataGenerator;
import com.ibm.commerce.partner.occ.mapping.mappers.IbmPartnerEndCustomerB2BUnitDataMapper;
import com.ibm.commerce.partnerwebservicescommons.company.endcustomer.dto.IbmPartnerEndCustomerB2BUnitWsDTO;
import de.hybris.bootstrap.annotations.UnitTest;
import ma.glasnost.orika.MappingContext;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertTrue;

@UnitTest
public class IbmPartnerEndCustomerB2BUnitDataMapperTest {

    private IbmPartnerEndCustomerB2BUnitDataMapper ibmPartnerEndCustomerB2BUnitDataMapper;
    @Mock
    private MappingContext mappingContext;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ibmPartnerEndCustomerB2BUnitDataMapper=new IbmPartnerEndCustomerB2BUnitDataMapper();
    }

    @Test
    public void testMapAtoB(){
        IbmPartnerEndCustomerB2BUnitWsDTO source = IbmPartnerEndCustomerB2BUnitWsDTOTestDataGenerator.createIbmPartnerEndCustomerB2BUnitDataDTO();
        source.setGoe(true);
        IbmPartnerEndCustomerB2BUnitData target = IbmPartnerEndCustomerB2BUnitTestDataGenerator.createIbmPartnerEndCustomerB2BUnitData();
        ibmPartnerEndCustomerB2BUnitDataMapper.mapAtoB(source, target, mappingContext);
        assertTrue(target.isGoe());

    }

    @Test(expected = NullPointerException.class)
    public void testMapAtoB_EmptySource(){
        IbmPartnerEndCustomerB2BUnitData target = IbmPartnerEndCustomerB2BUnitTestDataGenerator.createIbmPartnerEndCustomerB2BUnitData();
        ibmPartnerEndCustomerB2BUnitDataMapper.mapAtoB(null, target, mappingContext);
        assertTrue(target.isGoe());

    }

    @Test(expected = NullPointerException.class)
    public void testMapAtoB_EmptyTarget(){
        IbmPartnerEndCustomerB2BUnitWsDTO source = IbmPartnerEndCustomerB2BUnitWsDTOTestDataGenerator.createIbmPartnerEndCustomerB2BUnitDataDTO();
        source.setGoe(true);
        IbmPartnerEndCustomerB2BUnitData target = null;
        ibmPartnerEndCustomerB2BUnitDataMapper.mapAtoB(source, target, mappingContext);

    }
}
