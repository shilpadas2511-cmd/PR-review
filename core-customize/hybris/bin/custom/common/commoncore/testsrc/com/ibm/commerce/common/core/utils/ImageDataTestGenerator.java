package com.ibm.commerce.common.core.utils;

import de.hybris.platform.commercefacades.product.data.ImageData;

public class ImageDataTestGenerator {

    public static ImageData createMediaContainer(String format) {
        ImageData imageData = new ImageData();
        imageData.setFormat(format);
        return imageData;
    }
}
