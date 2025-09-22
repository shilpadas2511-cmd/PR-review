package com.ibm.commerce.common.core.utils;

import de.hybris.platform.core.model.media.MediaModel;

public class MediaModelTestDataGenerator {

    public static MediaModel createMediaModel(String mediaId) {
        MediaModel mediaModel = new MediaModel();
        mediaModel.setCode(mediaId);
        return mediaModel;
    }
}
