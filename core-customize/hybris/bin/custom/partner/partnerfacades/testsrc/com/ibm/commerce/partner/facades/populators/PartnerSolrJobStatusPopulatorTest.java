package com.ibm.commerce.partner.facades.populators;

import com.ibm.commerce.partner.data.solrindexer.SolrIndexJobResponseData;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.solrfacetsearch.model.indexer.cron.SolrIndexerCronJobModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PartnerCronjobStatusPopulatorTest {

    private PartnerSolrJobStatusPopulator populator;
    private SolrIndexerCronJobModel source;
    private SolrIndexJobResponseData target;

    @BeforeEach
    void setUp() {
        populator = new PartnerSolrJobStatusPopulator();
        source = mock(SolrIndexerCronJobModel.class);
        target = new SolrIndexJobResponseData();
    }

    @Test
    void testPopulate_AllFieldsPresent() {
        Date now = new Date();

        when(source.getCode()).thenReturn("testJob");
        when(source.getCreationtime()).thenReturn(now);
        when(source.getModifiedtime()).thenReturn(now);
        when(source.getStartTime()).thenReturn(now);
        when(source.getEndTime()).thenReturn(now);
        when(source.getStatus()).thenReturn(CronJobStatus.RUNNING);
        when(source.getResult()).thenReturn(CronJobResult.SUCCESS);

        populator.populate(source, target);

        assertEquals("testJob", target.getCode());
        assertEquals(now, target.getCreationDate());
        assertEquals(now, target.getLastModifiedDate());
        assertEquals(now, target.getStartDate());
        assertEquals(now, target.getEndDate());
        assertEquals("RUNNING", target.getSyncStatus());
        assertEquals("SUCCESS", target.getSyncResult());
    }

    @Test
    void testPopulate_SomeFieldsNull() {
        when(source.getCode()).thenReturn("codeOnly");
        when(source.getCreationtime()).thenReturn(null);
        when(source.getModifiedtime()).thenReturn(null);
        when(source.getStartTime()).thenReturn(null);
        when(source.getEndTime()).thenReturn(null);
        when(source.getStatus()).thenReturn(null);
        when(source.getResult()).thenReturn(null);

        populator.populate(source, target);

        assertEquals("codeOnly", target.getCode());
        assertNull(target.getCreationDate());
        assertNull(target.getLastModifiedDate());
        assertNull(target.getStartDate());
        assertNull(target.getEndDate());
        assertNull(target.getSyncStatus());
        assertNull(target.getSyncResult());
    }

    @Test
    void testPopulate_BlankCodeNotSet() {
        when(source.getCode()).thenReturn("  ");  // Blank code
        populator.populate(source, target);
        assertNull(target.getCode());
    }
}
