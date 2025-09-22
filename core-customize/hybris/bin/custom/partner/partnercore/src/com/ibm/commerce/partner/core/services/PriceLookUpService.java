package com.ibm.commerce.partner.core.services;

import com.ibm.commerce.partner.core.enums.CpqPricingTypeEnum;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.PartnerCpqHeaderPricingDetailModel;
import com.ibm.commerce.partner.core.model.PartnerCpqPricingDetailModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import java.util.List;
import java.util.Optional;

/**
 * service to provide information during price look up Api consumption
 */
public interface PriceLookUpService {

    /**
     * return list of all the child entries in a cart associated with Main entry
     *
     * @param source
     * @return List<AbstractOrderEntryModel>
     */
    List<AbstractOrderEntryModel> getChildEntriesList(AbstractOrderModel source);

    /**
     * return child entry of a particular Main entry by Part number
     *
     * @param mainEntry
     * @param partNumber
     * @return Optional<AbstractOrderEntryModel>
     */
    Optional<AbstractOrderEntryModel> getChildEntry(AbstractOrderEntryModel mainEntry,
        String partNumber, int entryNumber);

    /**
     * return main entry Part number
     *
     * @param mainEntry
     * @param partNumber
     * @return Optional<AbstractOrderEntryModel>
     */
    Optional<AbstractOrderEntryModel> getMainEntry(AbstractOrderModel order,
        String partNumber);

    /**
     * fetch pid product entry from cart
     *
     * @param cart
     * @param pidCode
     * @param configCode
     * @return Optional<AbstractOrderEntryModel>
     */
    Optional<AbstractOrderEntryModel> findPidEntryByEntryNumber(AbstractOrderModel cart,
        String pidCode, String configCode);

    /**
     * fetch CpqPricingDetail from entry
     * @param entry
     * @param pricingTypeEnum
     * @return Optional<PartnerCpqPricingDetailModel>
     */
    Optional<PartnerCpqPricingDetailModel> getCpqPricingDetail(AbstractOrderEntryModel entry,
        CpqPricingTypeEnum pricingTypeEnum);

    PartnerCpqPricingDetailModel getEntryCpqPricingDetail(
        AbstractOrderEntryModel entry, CpqPricingTypeEnum pricingTypeEnum);

    /**
     * fetch CpqPricingDetail on cart
     * @param entry
     * @param pricingTypeEnum
     * @return PartnerCpqHeaderPricingDetailModel
     */
    PartnerCpqHeaderPricingDetailModel getHeaderCpqPricingDetail(IbmPartnerCartModel cart,
        CpqPricingTypeEnum pricingTypeEnum);

    /**
     * create CpqPricingDetail on cart
     * @param entry
     * @param pricingTypeEnum
     * @return PartnerCpqHeaderPricingDetailModel
     */
    PartnerCpqHeaderPricingDetailModel createHeaderCpqPricing(IbmPartnerCartModel cart,
        CpqPricingTypeEnum pricingTypeEnum);

    /*
     *Remove Header pricing details from quote cart when add/delete/edit happens in the quote cart
     */
    void removeOverridenHeaderPrices(AbstractOrderModel model);
/*
Method to populate YTY discount from cart to  Entry model
 */
    void populateYtyDiscount(IbmPartnerCartModel cart);

    PartnerCpqHeaderPricingDetailModel getHeaderPricingDetail(
        IbmPartnerCartModel cartModel);

    void removeOrderPricingInformation(IbmPartnerQuoteModel order);

    /*
    Method to get max no of eligible year of extension (YTY)
     */
    int getMaxYtyYear(IbmPartnerCartModel cart);

    PartnerCpqHeaderPricingDetailModel populateCPQHeaderPricingDetail(
        PartnerCpqHeaderPricingDetailModel original);
}
