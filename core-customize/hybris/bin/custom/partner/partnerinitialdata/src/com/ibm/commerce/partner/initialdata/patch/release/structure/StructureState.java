package com.ibm.commerce.partner.initialdata.patch.release.structure;

public enum StructureState implements de.hybris.platform.patches.organisation.StructureState {
    V1, V2, V3, LAST;

    @Override
    public boolean isAfter(final de.hybris.platform.patches.organisation.StructureState structureState) {
        if (this == structureState) {
            return false;
        }
        for (final de.hybris.platform.patches.organisation.StructureState iterateValue : values()) {
            if (structureState.equals(iterateValue)) {
                return true;
            }
            if (this.equals(iterateValue)) {
                return false;
            }
        }
        return false;
    }
}
