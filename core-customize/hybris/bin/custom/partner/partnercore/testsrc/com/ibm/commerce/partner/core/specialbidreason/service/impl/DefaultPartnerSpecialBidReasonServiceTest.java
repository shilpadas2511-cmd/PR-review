package com.ibm.commerce.partner.core.specialbidreason.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ibm.commerce.partner.core.model.PartnerSpecialBidReasonModel;
import com.ibm.commerce.partner.core.specialbidreason.dao.PartnerSpecialBidReasonDao;
import com.ibm.commerce.partner.core.util.model.PartnerSpecialBidReasonTestDataGenerator;
import java.util.Arrays;
import java.util.List;
import org.easymock.Mock;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

public class DefaultPartnerSpecialBidReasonServiceTest {

    @Mock
    private PartnerSpecialBidReasonDao partnerSpecialBidReasonDao;

    @InjectMocks
    private DefaultPartnerSpecialBidReasonService specialBidReasonService;

    private static final String REASON_CODE1 = "Code1";
    private static final String REASON_CODE2 = "Code2";
    private static final String REASON_NAME1 = "Name1";

    private static final String REASON_NAME2 = "Name2";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        partnerSpecialBidReasonDao = mock(PartnerSpecialBidReasonDao.class);
        specialBidReasonService = new DefaultPartnerSpecialBidReasonService(
            partnerSpecialBidReasonDao);
    }


    @Test
    public void testGetAllSpecialBidReasonDetails() {
        List<PartnerSpecialBidReasonModel> specialBidReasonModelList = Arrays.asList(
            PartnerSpecialBidReasonTestDataGenerator.createSpecialBidreason(REASON_CODE1,
                REASON_NAME1),
            PartnerSpecialBidReasonTestDataGenerator.createSpecialBidreason(REASON_CODE2,
                REASON_NAME2));
        when(partnerSpecialBidReasonDao.getAllSpecialBidReasonDetails()).thenReturn(
            specialBidReasonModelList);

        List<PartnerSpecialBidReasonModel> result = specialBidReasonService.getAllSpecialBidReasonDetails();
        assertEquals(specialBidReasonModelList.size(), result.size());
        assertEquals(specialBidReasonModelList.get(0), result.get(0));
        assertEquals(specialBidReasonModelList.get(1), result.get(1));
    }

    @Test
    public void testGetSpecialBidReasonById() {
        String code = "123";
        PartnerSpecialBidReasonModel partnerSpecialBidReasonModel = new PartnerSpecialBidReasonModel();
        when(partnerSpecialBidReasonDao.getSpecialBidReasonById(code)).thenReturn(
            partnerSpecialBidReasonModel);
        PartnerSpecialBidReasonModel partnerSpecialBidReasonModel1 = specialBidReasonService.getSpecialBidReasonById(
            code);
        assertEquals(partnerSpecialBidReasonModel, partnerSpecialBidReasonModel1);
        verify(partnerSpecialBidReasonDao, times(1)).getSpecialBidReasonById(code);
    }


    @Test
    public void testEmptySpecialBidReasonDetails() {
        when(partnerSpecialBidReasonDao.getAllSpecialBidReasonDetails()).thenReturn(null);
        List<PartnerSpecialBidReasonModel> result = specialBidReasonService.getAllSpecialBidReasonDetails();
        assertNull(result);
    }
}
