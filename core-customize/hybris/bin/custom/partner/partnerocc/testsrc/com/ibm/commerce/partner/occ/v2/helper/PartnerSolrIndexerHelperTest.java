package com.ibm.commerce.partner.occ.v2.helper;

import com.ibm.commerce.partner.data.solrindexer.SolrIndexJobRequestData;
import com.ibm.commerce.partner.data.solrindexer.SolrIndexJobResponseData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.solrfacetsearch.model.config.SolrFacetSearchConfigModel;
import de.hybris.platform.solrfacetsearch.model.indexer.cron.SolrIndexerCronJobModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PartnerSolrIndexerHelperTest {

    private UserService userService;
    private ModelService modelService;
    private CronJobService cronJobService;
    private FlexibleSearchService flexibleSearchService;
    private Converter<SolrIndexerCronJobModel, SolrIndexJobResponseData> converter;

    @InjectMocks
    private PartnerSolrIndexerHelper helper;
    @Mock
    private Logger logger;
    @Mock
    private SolrIndexerCronJobModel indexerCronJob;

    @BeforeEach
    void setup() {
        userService = mock(UserService.class);
        modelService = mock(ModelService.class);
        cronJobService = mock(CronJobService.class);
        flexibleSearchService = mock(FlexibleSearchService.class);
        converter = mock(Converter.class);

        helper = new PartnerSolrIndexerHelper(userService, modelService, cronJobService,
            flexibleSearchService, converter, 3000);
    }

    @Test
    void testValidateSolrIndexRequest_Valid() {
        SolrIndexJobRequestData request = new SolrIndexJobRequestData();
        request.setIndexName("myIndex");
        request.setIndexerOperation("full");

        assertTrue(helper.validateSolrIndexRequest(request));
    }

    @Test
    void testValidateSolrIndexRequest_Invalid() {
        assertFalse(helper.validateSolrIndexRequest(null));
        SolrIndexJobRequestData invalid = new SolrIndexJobRequestData();
        assertFalse(helper.validateSolrIndexRequest(invalid));
    }

    @Test
    void testProcessIndexJob_SuccessWithoutTrigger() throws CMSItemNotFoundException {
        SolrIndexJobRequestData request = new SolrIndexJobRequestData();
        request.setIndexName("index");
        request.setIndexerOperation("full");

        SolrIndexerCronJobModel cronJob = mock(SolrIndexerCronJobModel.class);
        SolrIndexJobResponseData response = new SolrIndexJobResponseData();

        when(cronJob.getStatus()).thenReturn(CronJobStatus.FINISHED);
        when(flexibleSearchService.searchUnique(any())).thenReturn(cronJob);
        when(converter.convert(cronJob)).thenReturn(response);

        SolrIndexJobResponseData result = helper.processIndexJob(request, false);

        assertNotNull(result);
        assertEquals(response, result);
    }

    @Test
    void testProcessIndexJob_TriggerIfNotRunning() throws CMSItemNotFoundException {
        SolrIndexJobRequestData request = new SolrIndexJobRequestData();
        request.setIndexName("index");
        request.setIndexerOperation("full");

        SolrIndexerCronJobModel cronJob = mock(SolrIndexerCronJobModel.class);
        SolrFacetSearchConfigModel config = mock(SolrFacetSearchConfigModel.class);
        SolrIndexJobResponseData response = new SolrIndexJobResponseData();

        when(cronJob.getStatus()).thenReturn(CronJobStatus.FINISHED);
        when(flexibleSearchService.searchUnique(any()))
            .thenReturn(cronJob)
            .thenReturn(config);
        when(userService.getAdminUser()).thenReturn(null);
        when(converter.convert(cronJob)).thenReturn(response);

        SolrIndexJobResponseData result = helper.processIndexJob(request, true);

        verify(modelService).save(cronJob);
        verify(cronJobService).performCronJob(cronJob, false);
        assertEquals(response, result);
    }

    @Test
    void testProcessIndexJob_MissingConfig_ThrowsCMSItemNotFound() {
        SolrIndexJobRequestData request = new SolrIndexJobRequestData();
        request.setIndexName("index");
        request.setIndexerOperation("full");

        SolrIndexerCronJobModel cronJob = mock(SolrIndexerCronJobModel.class);
        when(cronJob.getStatus()).thenReturn(CronJobStatus.FINISHED);
        when(flexibleSearchService.searchUnique(any()))
            .thenReturn(cronJob)
            .thenReturn(null);

        assertThrows(CMSItemNotFoundException.class, () -> {
            helper.processIndexJob(request, true);
        });
    }

    @Test
    void testProcessIndexJob_InvalidRequest_ThrowsIllegalArgument() {
        SolrIndexJobRequestData request = new SolrIndexJobRequestData();

        assertThrows(IllegalArgumentException.class, () -> {
            helper.processIndexJob(request, true);
        });
    }

    @Test
    void testProcessIndexJob_MissingCronJob_ThrowsCMSItemNotFound() {
        SolrIndexJobRequestData request = new SolrIndexJobRequestData();
        request.setIndexName("index");
        request.setIndexerOperation("full");

        when(flexibleSearchService.searchUnique(any())).thenReturn(null);

        assertThrows(CMSItemNotFoundException.class, () -> {
            helper.processIndexJob(request, false);
        });
    }
    @Test
    public void testTriggerIndexCronJob_ShouldTriggerJobAndHandleSleep() {
        when(userService.getAdminUser()).thenReturn(null);
        indexerCronJob = mock(SolrIndexerCronJobModel.class);
        when(indexerCronJob.getCode()).thenReturn("testCronJob");
        helper.triggerIndexCronJob(indexerCronJob);
        verify(indexerCronJob).setSessionUser(null);
        verify(modelService).save(indexerCronJob);
        verify(cronJobService).performCronJob(indexerCronJob, false);
        verify(modelService).refresh(indexerCronJob);
    }

    @Test
    public void testTriggerIndexCronJob_ShouldHandleInterruptedException() throws Exception {
        indexerCronJob = mock(SolrIndexerCronJobModel.class);
        PartnerSolrIndexerHelper spyHelper = Mockito.spy(helper);
        doReturn(1L).when(spyHelper).getSolrJobTriggerWait();
        Thread.currentThread().interrupt();
        when(indexerCronJob.getCode()).thenReturn("testCronJob");
        spyHelper.triggerIndexCronJob(indexerCronJob);
        verify(modelService).refresh(indexerCronJob);
        Thread.interrupted();
    }
}
