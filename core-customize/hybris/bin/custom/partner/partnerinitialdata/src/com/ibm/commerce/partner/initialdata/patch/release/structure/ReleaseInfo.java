package com.ibm.commerce.partner.initialdata.patch.release.structure;

import de.hybris.platform.patches.Release;

public class ReleaseInfo implements Release {
    @Override
    public String getReleaseId() {
        return getCode();
    }

    private String code;

    public ReleaseInfo(String releaseId) {
        setCode(releaseId);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
