package com.ibm.commerce.partner.initialdata.patch.release;

import com.ibm.commerce.partner.initialdata.patch.release.structure.StructureState;
import de.hybris.platform.patches.Release;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.mockito.Mockito;

public class AbstractPartnerPatchTest {

    private static final String PATCH_ID = "PATCH-TEST";
    private static final String PATCH_NAME = "Test Patch";

    private Release release;
    private StructureState structureState;
    private PatchImporter testPatch;

    @Before
    public void setUp() {
        release = Mockito.mock(Release.class);
        structureState = Mockito.mock(StructureState.class);

        Mockito.when(release.getReleaseId()).thenReturn("REL-001");

        Map<Integer, String> impexMap = new LinkedHashMap<>();
        impexMap.put(1, "test1.impex");
        impexMap.put(2, "test2.impex");

        testPatch = Mockito.spy(new PatchImporter(PATCH_ID, PATCH_NAME, release, impexMap));
    }

    @Test
    public void testGetPatchDescription_ReturnsExpectedDescription() {
        String description = testPatch.getPatchDescription();
        Assert.assertTrue(description.contains(PATCH_ID));
        Assert.assertTrue(description.contains("REL-001"));
    }

    @Test
    public void testCreateProjectData_CallsCreateGlobalData() {
        Mockito.doNothing().when(testPatch).createGlobalData(Mockito.anySet(), Mockito.anyBoolean());

        testPatch.createProjectData(structureState);

        Mockito.verify(testPatch).createGlobalData(Set.of(), false);
    }
}
