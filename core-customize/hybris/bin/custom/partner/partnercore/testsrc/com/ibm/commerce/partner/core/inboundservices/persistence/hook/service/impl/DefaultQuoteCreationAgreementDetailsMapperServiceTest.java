package com.ibm.commerce.partner.core.inboundservices.persistence.hook.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.ArgumentCaptor;
import static org.junit.Assert.assertNull;

import com.ibm.commerce.partner.core.b2b.company.service.PartnerB2BUnitService;
import com.ibm.commerce.partner.core.inboundservices.persistence.hook.service.impl.DefaultQuoteCreationAgreementDetailsMapperService;
import com.ibm.commerce.partner.core.model.CpqIbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.IbmPartnerAgreementDetailModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultQuoteCreationAgreementDetailsMapperServiceTest {

    @InjectMocks
    DefaultQuoteCreationAgreementDetailsMapperService defaultQuoteCreationAgreementDetailsMapperService;

    @Mock
    private ModelService modelService;
    @Mock
    private PartnerB2BUnitService partnerB2BUnitService;

    @Mock
    private CpqIbmPartnerQuoteModel cpqIbmPartnerQuoteModel;
    @Mock
    private IbmPartnerQuoteModel ibmPartnerQuoteModel;
    @Mock
    private IbmPartnerAgreementDetailModel sourceAgreementDetail;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        defaultQuoteCreationAgreementDetailsMapperService = new DefaultQuoteCreationAgreementDetailsMapperService(
            modelService, partnerB2BUnitService);
    }

    @Test
    public void shouldReturnPartnerB2BUnitService() {
        PartnerB2BUnitService service = defaultQuoteCreationAgreementDetailsMapperService.getPartnerB2BUnitService();
        assertNotNull(service);
        assertEquals(partnerB2BUnitService, service);
    }

    private static class TestableQuoteCreationAgreementDetailsMapperService
        extends DefaultQuoteCreationAgreementDetailsMapperService {
        public TestableQuoteCreationAgreementDetailsMapperService(ModelService modelService,
            PartnerB2BUnitService partnerB2BUnitService) {
            super(modelService, partnerB2BUnitService);
        }

        public void callSetAgreementDetails(IbmPartnerAgreementDetailModel source,
            IbmPartnerAgreementDetailModel target) {
            super.setAgreementDetails(source, target);
        }
    }

    @Test
    public void shouldNotSetAnyFieldWhenCpqAgreementDetailIsNull() {
        IbmPartnerAgreementDetailModel agreementDetailModel = new IbmPartnerAgreementDetailModel();

        TestableQuoteCreationAgreementDetailsMapperService testableService =
            new TestableQuoteCreationAgreementDetailsMapperService(modelService, partnerB2BUnitService);

        testableService.callSetAgreementDetails(null, agreementDetailModel);

        assertNull(agreementDetailModel.getAgreementNumber());
        assertNull(agreementDetailModel.getCreationtime()); // important for coverage
    }


    @Test
    public void shouldVerifyAllSettersCalled() {
        IbmPartnerAgreementDetailModel cpqDetail = new IbmPartnerAgreementDetailModel();
        cpqDetail.setAgreementNumber("AG123");
        cpqDetail.setAgreementLevel("Level X");
        cpqDetail.setAgreementOption("Option A");
        cpqDetail.setProgramType("Program Z");
        cpqDetail.setAnniversaryMonth("June");

        when(cpqIbmPartnerQuoteModel.getAgreementDetail()).thenReturn(cpqDetail);
        IbmPartnerAgreementDetailModel createdDetail = new IbmPartnerAgreementDetailModel();
        when(modelService.create(IbmPartnerAgreementDetailModel.class)).thenReturn(createdDetail);

        defaultQuoteCreationAgreementDetailsMapperService.map(cpqIbmPartnerQuoteModel, ibmPartnerQuoteModel);

        assertEquals("AG123", createdDetail.getAgreementNumber());
        assertEquals("Level X", createdDetail.getAgreementLevel());
        assertEquals("Option A", createdDetail.getAgreementOption());
        assertEquals("Program Z", createdDetail.getProgramType());
        assertEquals("June", createdDetail.getAnniversaryMonth());
    }

    @Test
    public void shouldHandleEmptyAgreementDetailModel() {
        // Provide an empty agreement detail with no setters called
        IbmPartnerAgreementDetailModel emptyDetail = new IbmPartnerAgreementDetailModel();

        when(cpqIbmPartnerQuoteModel.getAgreementDetail()).thenReturn(emptyDetail);

        IbmPartnerAgreementDetailModel mockCreatedDetail = new IbmPartnerAgreementDetailModel();
        when(modelService.create(IbmPartnerAgreementDetailModel.class)).thenReturn(mockCreatedDetail);

        defaultQuoteCreationAgreementDetailsMapperService.map(cpqIbmPartnerQuoteModel, ibmPartnerQuoteModel);

        // Ensure the created model has null values in it since no setters were invoked
        assertNull(mockCreatedDetail.getAgreementNumber());
        assertNull(mockCreatedDetail.getAgreementLevel());
        assertNull(mockCreatedDetail.getAgreementOption());
        assertNull(mockCreatedDetail.getProgramType());
        assertNull(mockCreatedDetail.getAnniversaryMonth());
    }


    @Test
    public void shouldHandleAgreementDetailWithAllNullValues() {
        IbmPartnerAgreementDetailModel cpqDetail = new IbmPartnerAgreementDetailModel();
        cpqDetail.setAgreementNumber(null);  // null value
        cpqDetail.setAgreementLevel(null);   // null value
        cpqDetail.setAgreementOption(null);  // null value
        cpqDetail.setProgramType(null);      // null value
        cpqDetail.setAnniversaryMonth(null); // null value

        IbmPartnerAgreementDetailModel createdDetail = new IbmPartnerAgreementDetailModel();

        when(cpqIbmPartnerQuoteModel.getAgreementDetail()).thenReturn(cpqDetail);
        when(modelService.create(IbmPartnerAgreementDetailModel.class)).thenReturn(createdDetail);

        defaultQuoteCreationAgreementDetailsMapperService.map(cpqIbmPartnerQuoteModel, ibmPartnerQuoteModel);

        verify(modelService).create(IbmPartnerAgreementDetailModel.class);
        verify(ibmPartnerQuoteModel).setAgreementDetail(createdDetail);

        // Ensure that all fields are null in created detail
        assertNull(createdDetail.getAgreementNumber());
        assertNull(createdDetail.getAgreementLevel());
        assertNull(createdDetail.getAgreementOption());
        assertNull(createdDetail.getProgramType());
        assertNull(createdDetail.getAnniversaryMonth());
        assertNotNull(createdDetail.getCreationtime());


    }

    @Test
    public void shouldHandlePartialAgreementDetail() {
        IbmPartnerAgreementDetailModel cpqDetail = new IbmPartnerAgreementDetailModel();
        cpqDetail.setAgreementNumber(null);  // deliberately null
        cpqDetail.setAgreementLevel("Level X");  // present
        cpqDetail.setAgreementOption(null);  // null
        cpqDetail.setProgramType("Type Y");  // present
        cpqDetail.setAnniversaryMonth(null);  // null

        IbmPartnerAgreementDetailModel createdDetail = new IbmPartnerAgreementDetailModel();

        when(cpqIbmPartnerQuoteModel.getAgreementDetail()).thenReturn(cpqDetail);
        when(modelService.create(IbmPartnerAgreementDetailModel.class)).thenReturn(createdDetail);

        defaultQuoteCreationAgreementDetailsMapperService.map(cpqIbmPartnerQuoteModel, ibmPartnerQuoteModel);

        verify(modelService).create(IbmPartnerAgreementDetailModel.class);
        verify(ibmPartnerQuoteModel).setAgreementDetail(createdDetail);

        // check only the non-null values are set
        assertEquals("Level X", createdDetail.getAgreementLevel());
        assertEquals("Type Y", createdDetail.getProgramType());
        assertNotNull(createdDetail.getCreationtime());
    }

    @Test
    public void shouldHandleNullAgreementDetail() {
        when(cpqIbmPartnerQuoteModel.getAgreementDetail()).thenReturn(null);
        IbmPartnerAgreementDetailModel createdDetail = new IbmPartnerAgreementDetailModel();
        when(modelService.create(IbmPartnerAgreementDetailModel.class)).thenReturn(createdDetail);

        defaultQuoteCreationAgreementDetailsMapperService.map(cpqIbmPartnerQuoteModel, ibmPartnerQuoteModel);

        verify(modelService).create(IbmPartnerAgreementDetailModel.class);
        verify(ibmPartnerQuoteModel).setAgreementDetail(createdDetail);
        //assertNotNull(createdDetail.getCreationtime());
    }

    @Test
    public void shouldHandlePartialNullValuesInAgreementDetails() {
        // Prepare a source agreement detail with null fields
        IbmPartnerAgreementDetailModel emptyDetail = new IbmPartnerAgreementDetailModel();

        when(cpqIbmPartnerQuoteModel.getAgreementDetail()).thenReturn(emptyDetail);

        IbmPartnerAgreementDetailModel mockCreatedDetail = new IbmPartnerAgreementDetailModel();
        when(modelService.create(IbmPartnerAgreementDetailModel.class)).thenReturn(mockCreatedDetail);

        defaultQuoteCreationAgreementDetailsMapperService.map(cpqIbmPartnerQuoteModel, ibmPartnerQuoteModel);

        // Capture the model passed to quoteModel.setAgreementDetail
        ArgumentCaptor<IbmPartnerAgreementDetailModel> captor = ArgumentCaptor.forClass(IbmPartnerAgreementDetailModel.class);
        verify(ibmPartnerQuoteModel).setAgreementDetail(captor.capture());

        IbmPartnerAgreementDetailModel actualDetail = captor.getValue();
        assertNotNull(actualDetail);
        assertEquals(null, actualDetail.getAgreementNumber());
        assertEquals(null, actualDetail.getAgreementLevel());
        assertEquals(null, actualDetail.getAgreementOption());
        assertEquals(null, actualDetail.getProgramType());
        assertEquals(null, actualDetail.getAnniversaryMonth());
        assertNotNull(actualDetail.getCreationtime());
    }

    @Test
    public void shouldHandleNullAgreementDetailGracefully() {
        when(cpqIbmPartnerQuoteModel.getAgreementDetail()).thenReturn(null);

        IbmPartnerQuoteModel targetQuoteModel = new IbmPartnerQuoteModel();
        IbmPartnerAgreementDetailModel agreementDetailModel = new IbmPartnerAgreementDetailModel();
        when(modelService.create(IbmPartnerAgreementDetailModel.class)).thenReturn(agreementDetailModel);

        defaultQuoteCreationAgreementDetailsMapperService.map(cpqIbmPartnerQuoteModel, targetQuoteModel);

        // assert that agreementDetail is still created and set, even if source is null
        assertNotNull(targetQuoteModel.getAgreementDetail());
    }

    @Test
    public void shouldMapAgreementDetails() {
        IbmPartnerAgreementDetailModel targetAgreementDetail = new IbmPartnerAgreementDetailModel();
        when(modelService.create(IbmPartnerAgreementDetailModel.class)).thenReturn(
            targetAgreementDetail);
        when(cpqIbmPartnerQuoteModel.getAgreementDetail()).thenReturn(sourceAgreementDetail);

        String agreementNumber = "AG123";
        String agreementLevel = "Level 1";
        String agreementOption = "Option A";
        String programType = "Program X";
        String anniversaryMonth = "January";

        when(sourceAgreementDetail.getAgreementNumber()).thenReturn(agreementNumber);
        when(sourceAgreementDetail.getAgreementLevel()).thenReturn(agreementLevel);
        when(sourceAgreementDetail.getAgreementOption()).thenReturn(agreementOption);
        when(sourceAgreementDetail.getProgramType()).thenReturn(programType);
        when(sourceAgreementDetail.getAnniversaryMonth()).thenReturn(anniversaryMonth);

        defaultQuoteCreationAgreementDetailsMapperService.map(cpqIbmPartnerQuoteModel,
            ibmPartnerQuoteModel);

        assertEquals(agreementNumber, targetAgreementDetail.getAgreementNumber());
        assertEquals(agreementLevel, targetAgreementDetail.getAgreementLevel());
        assertEquals(agreementOption, targetAgreementDetail.getAgreementOption());
        assertEquals(programType, targetAgreementDetail.getProgramType());
        assertEquals(anniversaryMonth, targetAgreementDetail.getAnniversaryMonth());
        assertNotNull(targetAgreementDetail.getCreationtime());

        verify(modelService, times(1)).create(IbmPartnerAgreementDetailModel.class);
    }

}
