package com.ibm.commerce.partner.initialdata.patch.release.structure;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.*;


@RunWith(MockitoJUnitRunner.class)
class StructureStateTest {

    @Test
    void testIsAfterLogic() {
        assertTrue(StructureState.V2.isAfter(StructureState.V1));
        assertTrue(StructureState.V3.isAfter(StructureState.V2));
        assertTrue(StructureState.LAST.isAfter(StructureState.V1));
        assertTrue(StructureState.LAST.isAfter(StructureState.V2));
        assertTrue(StructureState.LAST.isAfter(StructureState.V3));

        assertFalse(StructureState.V1.isAfter(StructureState.V2));
        assertFalse(StructureState.V1.isAfter(StructureState.LAST));
        assertFalse(StructureState.V2.isAfter(StructureState.V3));
        assertFalse(StructureState.V1.isAfter(StructureState.V1));
    }

    @Test
    void testIsAfterWithUnrelatedValueReturnsFalse() {
        de.hybris.platform.patches.organisation.StructureState unrelatedState =
            new de.hybris.platform.patches.organisation.StructureState() {
                @Override
                public boolean isAfter(
                    de.hybris.platform.patches.organisation.StructureState other) {
                    return false;
                }
            };
        assertFalse(StructureState.V1.isAfter(unrelatedState));
    }
}