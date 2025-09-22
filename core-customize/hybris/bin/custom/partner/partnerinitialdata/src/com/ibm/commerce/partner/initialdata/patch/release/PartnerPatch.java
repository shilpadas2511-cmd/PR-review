package com.ibm.commerce.partner.initialdata.patch.release;

import de.hybris.platform.patches.Patch;
import de.hybris.platform.patches.organisation.ImportLanguage;

import java.util.Set;

/**
 * Represents a specialized patch interface for partner-specific patch implementations.
 * <p>
 * Extends the generic {@link Patch} interface to introduce additional methods tailored for partner
 * data setup and import.
 */
public interface PartnerPatch extends Patch {

    /**
     * Creates  global data required by the patch.
     *
     * @param languages           the set of languages to be considered for the import
     * @param updateLanguagesOnly flag indicating whether only language-specific data should be
     *                            updated
     */
    void createGlobalData(final Set<ImportLanguage> languages, boolean updateLanguagesOnly);
}
