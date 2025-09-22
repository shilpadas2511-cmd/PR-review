package com.ibm.commerce.partner.core.util.model;

import de.hybris.platform.apiregistryservices.model.AbstractCredentialModel;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;

import java.util.Map;


public class ConsumedDestinationModelTestDataGenerator {

    public static ConsumedDestinationModel createConsumedDestinationModel(final String url, final AbstractCredentialModel credentialModel) {
        final ConsumedDestinationModel destinationModel = new ConsumedDestinationModel();
        destinationModel.setUrl(url);
        destinationModel.setCredential(credentialModel);
        return destinationModel;
    }

	 public static ConsumedDestinationModel createConsumedDestinationModel(
			 final Map<String, String> additionalProperties)
	 {
		 final ConsumedDestinationModel destinationModel = new ConsumedDestinationModel();
		 destinationModel.setAdditionalProperties(additionalProperties);
		 return destinationModel;
	 }
}
