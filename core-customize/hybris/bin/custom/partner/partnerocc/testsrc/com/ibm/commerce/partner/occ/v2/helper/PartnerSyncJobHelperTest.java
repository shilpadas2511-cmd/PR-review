package com.ibm.commerce.partner.occ.v2.helper;

import de.hybris.platform.cmsfacades.data.SyncJobData;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PartnerSyncJobHelperTest {

    @Mock
    private FlexibleSearchService flexibleSearchService;

    @Mock
    private Converter<Optional<CronJobModel>, SyncJobData> syncJobConverter;

    @InjectMocks
    private PartnerSyncJobHelper partnerSyncJobHelper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        partnerSyncJobHelper = new PartnerSyncJobHelper(flexibleSearchService, syncJobConverter);
    }

    @Test
    void testFetchSyncJobByCode_Success() {
        String cronJobCode = "testJobCode";
        CronJobModel mockCronJob = mock(CronJobModel.class);

        when(flexibleSearchService.searchUnique(any(FlexibleSearchQuery.class))).thenReturn(
            mockCronJob);

        Optional<CronJobModel> result = partnerSyncJobHelper.fetchSyncJobByCode(cronJobCode);

        assertTrue(result.isPresent());
        assertEquals(mockCronJob, result.get());
        verify(flexibleSearchService).searchUnique(any(FlexibleSearchQuery.class));
    }

    @Test
    void testFetchSyncJobByCode_NotFound() {
        String cronJobCode = "nonExistentJob";

        when(flexibleSearchService.searchUnique(any(FlexibleSearchQuery.class))).thenReturn(null);

        Optional<CronJobModel> result = partnerSyncJobHelper.fetchSyncJobByCode(cronJobCode);

        assertTrue(result.isEmpty());
        verify(flexibleSearchService).searchUnique(any(FlexibleSearchQuery.class));
    }

    @Test
    void testGetSyncJobStatusByCode_Success() {
        String cronJobCode = "validCode";
        CronJobModel mockCronJob = mock(CronJobModel.class);
        SyncJobData mockSyncJobData = new SyncJobData();

        when(flexibleSearchService.searchUnique(any(FlexibleSearchQuery.class))).thenReturn(
            mockCronJob);
        when(syncJobConverter.convert(Optional.of(mockCronJob))).thenReturn(mockSyncJobData);

        SyncJobData result = partnerSyncJobHelper.getSyncJobStatusByCode(cronJobCode);

        assertNotNull(result);
        assertEquals(mockSyncJobData, result);
        verify(syncJobConverter).convert(Optional.of(mockCronJob));
    }

    @Test
    void testGetSyncJobStatusByCode_ThrowsException() {
        String cronJobCode = "invalidCode";

        when(flexibleSearchService.searchUnique(any(FlexibleSearchQuery.class)))
            .thenThrow(ModelNotFoundException.class);

        assertThrows(ModelNotFoundException.class,
            () -> partnerSyncJobHelper.getSyncJobStatusByCode(cronJobCode));

        verify(flexibleSearchService).searchUnique(any(FlexibleSearchQuery.class));
    }
}
