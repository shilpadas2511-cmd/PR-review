import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import de.hybris.platform.servicelayer.model.ModelService
import com.ibm.commerce.partner.core.enums.CpqPricingTypeEnum
import com.ibm.commerce.partner.core.model.PartnerCpqHeaderPricingDetailModel
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel

// Fetch all carts
String cartQuery = 'SELECT {pk} FROM {IbmPartnerCart!}'
def cartResult = flexibleSearchService.search(new FlexibleSearchQuery(cartQuery))

// Fetch all quotes
String quoteQuery = 'SELECT {pk} FROM {IbmPartnerQuote!} ORDER BY {version} DESC'
def quoteResult = flexibleSearchService.search(new FlexibleSearchQuery(quoteQuery))

ModelService modelService = spring.getBean('modelService')

if (cartResult.result.size() > 0) {
    println String.format('%s ; %s ; %s', 'Cart ID', 'Updated', 'Remarks')

    cartResult.result.each { cart ->
        try {
            if (cart.getEntries() != null) {
                boolean fullPrice = true;
                boolean entitledPrice = true;
                if (cart.getQuoteReference() == null) {
                    if (cart.getFullPriceReceived()) {
                        // Update both Full and Entitled pricing if full price received
                        if (!cart.getPricingDetails().isEmpty()) {
                            for (PartnerCpqHeaderPricingDetailModel pricing : cart.getPricingDetails()) {
                                if (CpqPricingTypeEnum.FULL.getCode().equals(pricing.getPricingType())) {
                                    fullPrice = false;
                                } else if (CpqPricingTypeEnum.ENTITLED.getCode().equals(pricing.getPricingType())) {
                                    entitledPrice = false;
                                }
                            }
                        }
                    } else {
                        // Update only Entitled pricing if full price not received
                        fullPrice = false;
                        if (!cart.getPricingDetails().isEmpty()) {
                            for (PartnerCpqHeaderPricingDetailModel pricing : cart.getPricingDetails()) {
                                if (CpqPricingTypeEnum.ENTITLED.getCode().equals(pricing.getPricingType())) {
                                    entitledPrice = false;
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    // Update only Full pricing if quote reference is present
                    entitledPrice = false;
                    if (!cart.getPricingDetails().isEmpty()) {
                        for (PartnerCpqHeaderPricingDetailModel pricing : cart.getPricingDetails()) {
                            if (CpqPricingTypeEnum.FULL.getCode().equals(pricing.getPricingType())) {
                                fullPrice = false;
                                break;
                            }
                        }
                    }
                }
                if (fullPrice) {
                    updateFullCpqPricingDetail(cart, modelService)
                }
                if (entitledPrice) {
                    updateEntitledCpqPricingDetail(cart, modelService)
                }
            }
        } catch (Exception e) {
            println String.format('%s ; %s ; %s', cart.getCode(), 'N', e.getMessage())
        }
    }
} else {
    println 'No carts found.'
}

if (quoteResult.result.size() > 0) {
    println '******************************************************************************************'
    println String.format('%s ; %s ; %s', 'Quote ID', 'Updated', 'Remarks')

    quoteResult.result.each { quote ->
        try {
            if (quote.getPricingDetailsQuote().isEmpty()) {
                PartnerCpqHeaderPricingDetailModel fullCpqPricingDetail = modelService.create(PartnerCpqHeaderPricingDetailModel.class)
                fullCpqPricingDetail.setPricingType(CpqPricingTypeEnum.FULL.toString())
                fullCpqPricingDetail.setTotalExtendedPrice(quote.getTotalFullPrice())
                fullCpqPricingDetail.setInitialTotalExtendedPrice(quote.getTotalFullPrice())
                fullCpqPricingDetail.setTotalMEPPrice(quote.getTotalMEPPrice())
                fullCpqPricingDetail.setTotalDiscount(quote.getTotalDiscounts())
                fullCpqPricingDetail.setYtyPercentage(quote.getYtyPercentage())
                fullCpqPricingDetail.setTotalBidExtendedPrice(quote.getTotalBidExtendedPrice())
                fullCpqPricingDetail.setTotalOptimalPrice(quote.getTotalOptimalPrice())
                fullCpqPricingDetail.setTotalChannelMargin(quote.getTotalChannelMargin())
                fullCpqPricingDetail.setTotalBpExtendedPrice(quote.getTotalBpExtendedPrice())
                fullCpqPricingDetail.setTransactionPriceLevel(quote.getTransactionPriceLevel())
                fullCpqPricingDetail.setIbmPartnerQuote(quote);
                modelService.save(fullCpqPricingDetail);
                println String.format('%s ; %s ; %s', quote.getCode(), 'Y', 'Full Price')
            }
        } catch (Exception e) {
            println String.format('%s ; %s ; %s', quote.getCode(), 'N', 'Full Price - ' + e.getMessage())
        }
    }
} else {
    println 'No quotes found.'
}


/**
 * Updates Full CPQ Pricing Details
 */
def updateFullCpqPricingDetail(IbmPartnerCartModel cart, ModelService modelService) {
    try {
        PartnerCpqHeaderPricingDetailModel fullCpqPricingDetail = modelService.create(PartnerCpqHeaderPricingDetailModel.class)
        fullCpqPricingDetail.setPricingType(CpqPricingTypeEnum.FULL.toString())
        fullCpqPricingDetail.setTotalExtendedPrice(cart.getTotalFullPrice())
        fullCpqPricingDetail.setInitialTotalExtendedPrice(cart.getTotalFullPrice())
        updateCpqPricingDetail(fullCpqPricingDetail, cart, modelService)
        println String.format('%s ; %s ; %s', cart.getCode(), 'Y', 'Full Price')
    } catch (Exception e) {
        println String.format('%s ; %s ; %s', cart.getCode(), 'N', 'Full Price - ' + e.getMessage())
    }
}


/**
 * Updates Entitled CPQ Pricing Details
 */
def updateEntitledCpqPricingDetail(IbmPartnerCartModel cart, ModelService modelService) {
    try {
        PartnerCpqHeaderPricingDetailModel entitledCpqPricingDetail = modelService.create(PartnerCpqHeaderPricingDetailModel.class)
        entitledCpqPricingDetail.setPricingType(CpqPricingTypeEnum.ENTITLED.toString())
        entitledCpqPricingDetail.setTotalExtendedPrice(cart.getTotalEntitledPrice())
        updateCpqPricingDetail(entitledCpqPricingDetail, cart, modelService)
        println String.format('%s ; %s ; %s', cart.getCode(), 'Y', 'Entitled Price')
    } catch (Exception e) {
        println String.format('%s ; %s ; %s', cart.getCode(), 'N', 'Entitled Price - ' + e.getMessage())
    }
}


/**
 * Updates Common CPQ Pricing Details
 */
def updateCpqPricingDetail(PartnerCpqHeaderPricingDetailModel cpqPricingDetail, IbmPartnerCartModel cart, ModelService modelService) {
    cpqPricingDetail.setTotalMEPPrice(cart.getTotalMEPPrice())
    cpqPricingDetail.setTotalDiscount(cart.getTotalDiscounts())
    cpqPricingDetail.setYtyPercentage(cart.getYtyPercentage())
    cpqPricingDetail.setTotalBidExtendedPrice(cart.getTotalBidExtendedPrice())
    cpqPricingDetail.setTotalOptimalPrice(cart.getTotalOptimalPrice())
    cpqPricingDetail.setTotalChannelMargin(cart.getTotalChannelMargin())
    cpqPricingDetail.setTotalBpExtendedPrice(cart.getTotalBpExtendedPrice())
    cpqPricingDetail.setTransactionPriceLevel(cart.getTransactionPriceLevel())
    cpqPricingDetail.setIbmPartnerCart(cart)
    modelService.save(cpqPricingDetail)
}