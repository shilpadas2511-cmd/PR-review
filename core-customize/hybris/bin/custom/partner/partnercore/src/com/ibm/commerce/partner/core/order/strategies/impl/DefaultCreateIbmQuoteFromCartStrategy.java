/**
 *
 */
package com.ibm.commerce.partner.core.order.strategies.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.enums.CpqPricingTypeEnum;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.IbmPartnerPidCartModel;
import com.ibm.commerce.partner.core.model.IbmPartnerPidQuoteEntryModel;
import com.ibm.commerce.partner.core.model.IbmPartnerPidQuoteModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteEntryModel;
import com.ibm.commerce.partner.core.model.IbmPartnerQuoteModel;
import com.ibm.commerce.partner.core.model.PartnerCpqHeaderPricingDetailModel;
import com.ibm.commerce.partner.core.model.PartnerCpqPricingDetailModel;
import com.ibm.commerce.partner.core.services.PriceLookUpService;
import com.sap.hybris.sapcpqquoteintegration.model.CpqPricingDetailModel;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.order.AbstractOrderEntryTypeService;
import de.hybris.platform.order.strategies.CreateQuoteFromCartStrategy;
import de.hybris.platform.order.strategies.impl.GenericAbstractOrderCloningStrategy;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;


/**
 * Default implementation of {@link CreateQuoteFromCartStrategy}.
 */
public class DefaultCreateIbmQuoteFromCartStrategy extends
    GenericAbstractOrderCloningStrategy<IbmPartnerQuoteModel, IbmPartnerQuoteEntryModel, CartModel> implements
    CreateQuoteFromCartStrategy {

    private static final String PID_QUOTE = "PIDQUOTE";
    protected final AbstractOrderEntryTypeService abstractOrderEntryTypeService;
    private final ModelService modelService;
    private UserService userService;
    @Resource(name = "ibmCommonConfigurationService")
    private ConfigurationService ibmCommonConfigurationService;

    private final CommerceCommonI18NService commonI18NService;
    private final int defaultDaysToExpire;
    private final KeyGenerator pidQuoteKeyGenerator;
    private final KeyGenerator quoteCodeKeyGenerator;
    private final PriceLookUpService priceLookUpService;

    public DefaultCreateIbmQuoteFromCartStrategy(
        AbstractOrderEntryTypeService abstractOrderEntryTypeService, ModelService modelService,
        UserService userService,
        final CommerceCommonI18NService commonI18NService, final int defaultDaysToExpire,
        ConfigurationService ibmCommonConfigurationService, final KeyGenerator pidQuoteKeyGenerator,
        KeyGenerator quoteCodeKeyGenerator, PriceLookUpService priceLookUpService) {
        super(IbmPartnerQuoteModel.class, IbmPartnerQuoteEntryModel.class, CartModel.class);
        this.abstractOrderEntryTypeService = abstractOrderEntryTypeService;
        this.modelService = modelService;
        this.userService = userService;
        this.commonI18NService = commonI18NService;
        this.defaultDaysToExpire = defaultDaysToExpire;
        this.ibmCommonConfigurationService = ibmCommonConfigurationService;
        this.pidQuoteKeyGenerator = pidQuoteKeyGenerator;
        this.quoteCodeKeyGenerator = quoteCodeKeyGenerator;
        this.priceLookUpService = priceLookUpService;
    }


    @Override
    public IbmPartnerQuoteModel createQuoteFromCart(final CartModel cart) {
        validateParameterNotNullStandardMessage("cart", cart);
        String quoteCode = null;
        if (cart instanceof IbmPartnerCartModel partnerCartModel) {
            quoteCode = StringUtils.isNotEmpty(partnerCartModel.getPriceUid())
                ? partnerCartModel.getPriceUid() : getQuoteCodeKeyGenerator().generate().toString();
        }

        final IbmPartnerQuoteModel quote = clone(cart, Optional.ofNullable(quoteCode));
        quote.setDate(new Date());
        if (quote.getTotalFullPrice() != null) {
            quote.setTotalPrice(quote.getTotalFullPrice());
        }
        quote.setCalculated(cart.getCalculated());
        quote.setCreator(getUserService().getCurrentUser());
        quote.setQuoteIndexActive(true);
        quote.setCpqQuoteNumber(quote.getCode());
        populatePricingDetailsFromCartToQuote((IbmPartnerCartModel) cart, quote);
        getModelService().save(quote);
        quote.getEntries().forEach(quoteEntry -> {
            populateCloneEntries(quoteEntry);
            populatePricingDetailsForEntries(quoteEntry);
            Optional<AbstractOrderEntryModel> originalCartEntry = cart.getEntries().stream()
                .filter(entry -> entry.getEntryNumber().equals(quoteEntry.getEntryNumber()))
                .findAny();

            if (originalCartEntry.isPresent()) {
                quoteEntry.setProductConfiguration(
                    originalCartEntry.get().getProductConfiguration());
                if (CollectionUtils.isNotEmpty(originalCartEntry.get().getChildEntries())) {
                    IbmPartnerPidCartModel originalPidCart = (IbmPartnerPidCartModel) originalCartEntry.get()
                        .getChildEntries().iterator().next().getOrder();

                    final String orderCode =
                        PID_QUOTE + PartnercoreConstants.UNDERSCORE + originalCartEntry.get()
                            .getProduct().getCode() + PartnercoreConstants.UNDERSCORE
                            + getPidQuoteKeyGenerator().generate().toString();

                    AbstractOrderModel clone = getCloneAbstractOrderStrategy().clone(null, null,
                        originalPidCart, orderCode, IbmPartnerPidQuoteModel.class,
                        IbmPartnerPidQuoteEntryModel.class);
                    populatePricingDetails(clone);
                    getModelService().save(clone);
                    quoteEntry.setChildEntries(clone.getEntries());
                    getModelService().save(quoteEntry);
                }
            }
        });

        postProcess(cart, quote);

        return quote;
    }

    private void populateCloneEntries(AbstractOrderEntryModel pidCart) {
        if (CollectionUtils.isNotEmpty(pidCart.getCpqPricingDetails())) {
            pidCart.getCpqPricingDetails().stream()
                .filter(
                    pricing -> CpqPricingTypeEnum.FULL.getCode().equals(pricing.getPricingType()))
                .findFirst().ifPresent((fullPrice -> pidCart.setTotalPrice(
                    ((PartnerCpqPricingDetailModel) fullPrice).getTotalExtendedPrice())));
        }
    }

    /**
     * populating pricing details form list
     *
     * @param clone abstractOrderModel
     */
    protected void populatePricingDetails(AbstractOrderModel clone) {
        if (clone != null && clone.getEntries() != null) {
            clone.getEntries().forEach(this::populatePricingDetailsForEntries);
        }
    }

    protected void populatePricingDetailsFromCartToQuote(IbmPartnerCartModel cartModel,
        IbmPartnerQuoteModel ibmPartnerQuote) {
        cartModel.getPricingDetails().forEach(cartCpqPricing -> {
            if (cartCpqPricing.getPricingType()
                .equalsIgnoreCase(CpqPricingTypeEnum.FULL.getCode())) {
                PartnerCpqHeaderPricingDetailModel cpqPricingDetail = getPriceLookUpService().populateCPQHeaderPricingDetail(
                    cartCpqPricing);
                cpqPricingDetail.setIbmPartnerQuote(ibmPartnerQuote);
                getModelService().save(cpqPricingDetail);
            }
        });
    }

    /**
     * Setting and filtering full pricing from the pricing list.
     *
     * @param orderEntry orderEntryModel
     * @return setting modified price data of CpqPricingDetailModel
     */
    protected void populatePricingDetailsForEntries(AbstractOrderEntryModel orderEntry) {
        List<CpqPricingDetailModel> prices = orderEntry.getCpqPricingDetails();
        if (CollectionUtils.isNotEmpty(prices)) {
            orderEntry.setCpqPricingDetails(filterFullPricingType(prices));
        }
    }

    /**
     * filtering full pricing from the pricing list.
     *
     * @param prices the list of CpqPricingDetailModel data
     * @return modified price data of CpqPricingDetailModel
     */
    protected List<CpqPricingDetailModel> filterFullPricingType(
        List<CpqPricingDetailModel> prices) {
        return prices.stream()
            .filter(pricing -> CpqPricingTypeEnum.FULL.getCode().equals(pricing.getPricingType()))
            .collect(Collectors.toList());
    }


    @Override
    protected void postProcess(final CartModel cart, final IbmPartnerQuoteModel quote) {

        setExpirationDate(quote);
        quote.setName(getName(quote) + getFormattedDate());
    }

    protected void setExpirationDate(QuoteModel quoteModel) {
        if (getIbmCommonConfigurationService().getConfiguration()
            .getBoolean(PartnercoreConstants.ADD_DAYS_TO_EXPIRATION_DATE_FEATURE_FLAG, false)) {
            return;
        }
        Date currentDate = new Date();
        quoteModel.setQuoteExpirationDate(DateUtils.addDays(currentDate, getDefaultDaysToExpire()));
    }

    private String getFormattedDate() {
        return DateFormatUtils.format(new Date(), PartnercoreConstants.QUOTE_NAME_DATE_FORMAT);
    }

    protected String getName(final IbmPartnerQuoteModel quote) {
        Locale currentLocale = getCommonI18NService().getCurrentLocale();
        return quote.getUnit() != null ? quote.getUnit().getLocName(currentLocale)
            : quote.getUser().getName();
    }

    public ModelService getModelService() {
        return modelService;
    }

    public CommerceCommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public int getDefaultDaysToExpire() {
        return defaultDaysToExpire;
    }

    public KeyGenerator getPidQuoteKeyGenerator() {
        return pidQuoteKeyGenerator;
    }

    public UserService getUserService() {
        return userService;
    }

    public KeyGenerator getQuoteCodeKeyGenerator() {
        return quoteCodeKeyGenerator;
    }

    public PriceLookUpService getPriceLookUpService() {
        return priceLookUpService;
    }

    public ConfigurationService getIbmCommonConfigurationService() {
        return ibmCommonConfigurationService;
    }
}
