package com.ibm.commerce.partner.core.keygenerator;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import de.hybris.platform.servicelayer.keygenerator.impl.PersistentKeyGenerator;
import java.util.Date;
import java.util.UUID;


/**
 * Unique Uid key Generator. It appends currentTime of 12 chars to UUID key
 */
public class UniqueUidKeyGenerator extends PersistentKeyGenerator {

    @Override
    public Object generate() {
        return new Date().getTime() + PartnercoreConstants.HYPHEN + UUID.randomUUID().toString();
    }

    @Override
    public Object generateFor(Object object) {
        if (object instanceof String code) {
            return code + PartnercoreConstants.HYPHEN + UUID.randomUUID().toString();
        }
        return super.generateFor(object);
    }
}
