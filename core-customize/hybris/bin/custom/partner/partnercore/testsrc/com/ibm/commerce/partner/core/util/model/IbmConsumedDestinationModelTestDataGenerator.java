package com.ibm.commerce.partner.core.util.model;

import com.ibm.commerce.partner.core.model.IbmConsumedDestinationModel;

public class IbmConsumedDestinationModelTestDataGenerator {

    public static IbmConsumedDestinationModel createIbmConsumedDestinationModel(final String URL, String customUri) {
        IbmConsumedDestinationModel destinationModel = new IbmConsumedDestinationModel();
        destinationModel.setUrl(URL);
        destinationModel.setCustomUri(customUri);
        return destinationModel;
    }

}