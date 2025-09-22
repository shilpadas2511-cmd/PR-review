package com.ibm.commerce.partner.facades.comparators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import com.ibm.commerce.partner.core.model.IbmPartnerDivestitureRetentionModel;
import com.ibm.commerce.partner.core.util.model.IbmPartnerDivestitureRetentionModelTestDataGenerator;
import de.hybris.bootstrap.annotations.UnitTest;
import java.util.Date;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.Test;

@UnitTest
public class IbmPartnerDivestitureRetentionModelComparatorTest {

    private IbmPartnerDivestitureRetentionModelComparator comparator;

    IbmPartnerDivestitureRetentionModel model;

    private final static String ENTMT_TYPE = "Type";
    private final static String ENTMT_DESC = "Description";
    private final static Date RETAINED_ENDDATE = new Date();
    private final static String SAP_DIVSSTTR_CODE = "Code";
    private final static String DIFFERENT_ENTMT_TYPE = "different Type";
    private final static String DIFFERENT_ENTMT_DESC = "different Description";
    private final static String DIFFERENT_SAP_DIVSSTTR_CODE = "different Code";

    @Before
    public void setUp() {
        comparator = new IbmPartnerDivestitureRetentionModelComparator();
        model = IbmPartnerDivestitureRetentionModelTestDataGenerator.createDivestitureRetentionModel(
            ENTMT_TYPE, ENTMT_DESC, RETAINED_ENDDATE, SAP_DIVSSTTR_CODE);
    }

    @Test
    public void compare_SameType_ReturnsZero() {
        int result = comparator.compare(model, model);
        assertEquals(0, result);
    }

    @Test
    public void compare_DifferentType_ReturnsNonZero() {

        IbmPartnerDivestitureRetentionModel model2 = IbmPartnerDivestitureRetentionModelTestDataGenerator.createDivestitureRetentionModel(
            DIFFERENT_ENTMT_TYPE, ENTMT_DESC, RETAINED_ENDDATE, SAP_DIVSSTTR_CODE);
        int result = comparator.compare(model, model2);
        assertNotEquals(0, result);
    }

    @Test
    public void compare_DifferentDescription_ReturnsNonZero() {

        IbmPartnerDivestitureRetentionModel model2 = IbmPartnerDivestitureRetentionModelTestDataGenerator.createDivestitureRetentionModel(
            ENTMT_TYPE, DIFFERENT_ENTMT_DESC, RETAINED_ENDDATE, DIFFERENT_SAP_DIVSSTTR_CODE);
        int result = comparator.compare(model, model2);
        assertNotEquals(0, result);
    }

    @Test
    public void compare_DifferentDate_ReturnsNonZero() {
        IbmPartnerDivestitureRetentionModel model2 = IbmPartnerDivestitureRetentionModelTestDataGenerator.createDivestitureRetentionModel(
            ENTMT_TYPE, ENTMT_DESC, DateUtils.addDays(RETAINED_ENDDATE,2), SAP_DIVSSTTR_CODE);
        int result = comparator.compare(model, model2);
        assertNotEquals(0, result);
    }

    @Test
    public void compare_DifferentCode_ReturnsNonZero() {
        IbmPartnerDivestitureRetentionModel model2 = IbmPartnerDivestitureRetentionModelTestDataGenerator.createDivestitureRetentionModel(
            ENTMT_TYPE, ENTMT_DESC, RETAINED_ENDDATE, DIFFERENT_SAP_DIVSSTTR_CODE);
        int result = comparator.compare(model, model2);
        assertNotEquals(0, result);
    }
}