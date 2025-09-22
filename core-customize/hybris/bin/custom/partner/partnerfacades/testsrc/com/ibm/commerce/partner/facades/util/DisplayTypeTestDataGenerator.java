package com.ibm.commerce.partner.facades.util;

import com.ibm.commerce.partner.enums.data.DisplayTypeData;

/**
 * TestDataGenerator class for DisplayTypeTestDataGenerator
 */
public class DisplayTypeTestDataGenerator {
    /**
     * createDisplayTypeData
     * @param code
     * @param name
     * @return
     */
    public static DisplayTypeData createDisplayTypeData(String code,String name)
    {
        DisplayTypeData displayTypeData = new DisplayTypeData();
        displayTypeData.setCode(code);
        displayTypeData.setName(name);
        return displayTypeData;
    }

    /**
     * setting code for displayType
     * @param code
     * @return
     */
    public static DisplayTypeData createDisplayTypeCodeData(String code)
    {
        DisplayTypeData displayTypeData = new DisplayTypeData();
        displayTypeData.setCode(code);
        return displayTypeData;
    }

}
