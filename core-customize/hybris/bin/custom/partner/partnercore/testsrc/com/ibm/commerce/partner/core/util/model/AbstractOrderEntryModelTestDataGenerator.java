package com.ibm.commerce.partner.core.util.model;

import com.ibm.commerce.common.core.model.IbmVariantProductModel;
import com.sap.hybris.sapcpqquoteintegration.model.CpqPricingDetailModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import java.util.Collection;

import com.ibm.commerce.common.core.model.IbmPartProductModel;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;

import java.util.List;
import java.util.Set;


public class AbstractOrderEntryModelTestDataGenerator {

    public static AbstractOrderEntryModel createAbstractOrderEntry(final Integer entryNumber) {
        final AbstractOrderEntryModel abstractOrderEntryModel = new AbstractOrderEntryModel();
        abstractOrderEntryModel.setEntryNumber(entryNumber);
        return abstractOrderEntryModel;
    }

    public static AbstractOrderEntryModel createAbstractOrderEntryModel(final Integer entryNumber,
        final Collection<AbstractOrderEntryModel> childEntries) {
        final AbstractOrderEntryModel abstractOrderEntryModel = new AbstractOrderEntryModel();
        abstractOrderEntryModel.setEntryNumber(entryNumber);
        abstractOrderEntryModel.setChildEntries(childEntries);
        return abstractOrderEntryModel;
    }

    public static AbstractOrderEntryModel createAbstractOrderEntryModel(final Integer entryNumber,
        final Collection<AbstractOrderEntryModel> childEntries, ProductModel productModel) {
        final AbstractOrderEntryModel abstractOrderEntryModel = new AbstractOrderEntryModel();
        abstractOrderEntryModel.setEntryNumber(entryNumber);
        abstractOrderEntryModel.setChildEntries(childEntries);
        abstractOrderEntryModel.setProduct(productModel);
        return abstractOrderEntryModel;
    }


    public static AbstractOrderEntryModel createAbstractOrderEntry(final Integer entryNumber,
        final IbmPartProductModel partProductModel) {
        final AbstractOrderEntryModel abstractOrderEntryModel = new AbstractOrderEntryModel();
        abstractOrderEntryModel.setEntryNumber(entryNumber);
        abstractOrderEntryModel.setProduct(partProductModel);
        return abstractOrderEntryModel;
    }

    public static AbstractOrderEntryModel createAbstractOrderaEntry(final AbstractOrderModel orderModel, final ProductModel productModel) {
        final AbstractOrderEntryModel abstractOrderEntryModel = new AbstractOrderEntryModel();
        abstractOrderEntryModel.setProduct(productModel);
        abstractOrderEntryModel.setOrder(orderModel);
        return abstractOrderEntryModel;
    }

    public static AbstractOrderEntryModel createAbstractOrderEntry(Set<Integer> entryGroupNumbers) {
        final AbstractOrderEntryModel abstractOrderEntryModel = new AbstractOrderEntryModel();
        abstractOrderEntryModel.setEntryGroupNumbers(entryGroupNumbers);
        return abstractOrderEntryModel;
    }

    public static AbstractOrderEntryModel createAbstractOrderEntry() {
        final AbstractOrderEntryModel abstractOrderEntryModel = new AbstractOrderEntryModel();
        return abstractOrderEntryModel;
    }
    public static AbstractOrderEntryModel createAbstractOrderEntry(AbstractOrderEntryModel masterEntry, Long QUANTITY, IbmVariantProductModel ibmVariantProductModel, List<CpqPricingDetailModel> listCpqPricingDetailModel) {
        final AbstractOrderEntryModel abstractOrderEntryModel = new AbstractOrderEntryModel();
        abstractOrderEntryModel.setMasterEntry(masterEntry);
        abstractOrderEntryModel.setQuantity(QUANTITY);
        abstractOrderEntryModel.setProduct(ibmVariantProductModel);
        abstractOrderEntryModel.setCpqPricingDetails(listCpqPricingDetailModel);
        return abstractOrderEntryModel;
    }

    public static AbstractOrderEntryModel createAbstractOrderEntryForProduct(IbmVariantProductModel ibmVariantProductModel, ProductConfigurationModel productConfigurationModel) {
        final AbstractOrderEntryModel abstractOrderEntryModel = new AbstractOrderEntryModel();
        abstractOrderEntryModel.setProduct(ibmVariantProductModel);
        abstractOrderEntryModel.setProductConfiguration(productConfigurationModel);
        return abstractOrderEntryModel;
    }

    public static AbstractOrderEntryModel createAbstractOrderEntryForProduct(List<AbstractOrderEntryProductInfoModel> value, ProductConfigurationModel productConfigurationModel) {
        final AbstractOrderEntryModel abstractOrderEntryModel = new AbstractOrderEntryModel();
        abstractOrderEntryModel.setProductInfos(value);
        abstractOrderEntryModel.setProductConfiguration(productConfigurationModel);
        return abstractOrderEntryModel;
    }

}
