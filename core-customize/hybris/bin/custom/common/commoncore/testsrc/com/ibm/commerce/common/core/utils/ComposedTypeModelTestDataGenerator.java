package com.ibm.commerce.common.core.utils;

import de.hybris.platform.core.model.type.ComposedTypeModel;

public class ComposedTypeModelTestDataGenerator {

    public static ComposedTypeModel createComposeType(String composeId) {
        ComposedTypeModel composedTypeModel = new ComposedTypeModel();
        composedTypeModel.setCode(composeId);
        return composedTypeModel;
    }
}
