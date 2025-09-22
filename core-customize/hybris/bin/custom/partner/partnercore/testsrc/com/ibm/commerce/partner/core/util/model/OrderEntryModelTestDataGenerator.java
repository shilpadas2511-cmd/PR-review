package com.ibm.commerce.partner.core.util.model;

import com.sap.hybris.sapcpqquoteintegration.model.CpqPricingDetailModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.flexiblesearch.performance.LimitStatementRawJDBCPerformanceTest;
import de.hybris.platform.sap.productconfig.services.model.CPQOrderEntryProductInfoModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrderEntryModelTestDataGenerator {

    public static List<AbstractOrderEntryModel> createEntries() {
        List<AbstractOrderEntryModel> entryList = new ArrayList<>();
        AbstractOrderEntryModel entry = new AbstractOrderEntryModel();

        AbstractOrderEntryModel childEntry = new AbstractOrderEntryModel();
        childEntry.setProductInfos(Collections.singletonList(new CPQOrderEntryProductInfoModel()));
        childEntry.setCpqPricingDetails(new ArrayList<>());

        entry.setChildEntries(Collections.singleton(childEntry));
        entry.setCpqPricingDetails(Collections.singletonList(new CpqPricingDetailModel()));
        entryList.add(entry);
        return entryList;
    }
}
