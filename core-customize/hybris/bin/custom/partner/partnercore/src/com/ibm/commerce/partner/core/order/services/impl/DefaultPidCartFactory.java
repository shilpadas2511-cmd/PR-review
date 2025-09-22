package com.ibm.commerce.partner.core.order.services.impl;

import com.ibm.commerce.partner.core.order.services.PidCartFactory;
import com.ibm.commerce.partner.core.quote.services.PartnerSapCpqQuoteService;
import de.hybris.platform.commerceservices.strategies.NetGrossStrategy;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserNetCheckingStrategy;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;

/**
 * PID Cart Factory implementation
 */
public class DefaultPidCartFactory extends DefaultPartnerCommerceCartFactory implements
    PidCartFactory {

    private final static String PID_CART = "PIDCART";
    private final static String SEPERATOR = "_";

    private final NetGrossStrategy netGrossStrategy;
    private final BaseSiteService baseSiteService;
    private final BaseStoreService baseStoreService;
    private final KeyGenerator guidKeyGenerator;
    private ApplicationContext ctx;
    private final KeyGenerator keyGenerator;
    private final ModelService modelService;
    private final UserService userService;
    private final CommonI18NService commonI18NService;
    private final UserNetCheckingStrategy userNetCheckingStrategy;
    private final String cartType;
    private PartnerSapCpqQuoteService partnerSapCpqQuoteService;
    private ConfigurationService configurationService;


    public DefaultPidCartFactory(final NetGrossStrategy netGrossStrategy,
        final BaseSiteService baseSiteService, final BaseStoreService baseStoreService,
        final KeyGenerator guidKeyGenerator, final UserService userService,
        final ModelService modelService, final KeyGenerator keyGenerator,
        final CommonI18NService commonI18NService,
        final UserNetCheckingStrategy userNetCheckingStrategy, final String cartType,
        final KeyGenerator quoteCodeKeyGenerator,PartnerSapCpqQuoteService partnerSapCpqQuoteService,
        ConfigurationService configurationService) {
        super(quoteCodeKeyGenerator,modelService,partnerSapCpqQuoteService,configurationService );
        this.netGrossStrategy = netGrossStrategy;
        this.baseSiteService = baseSiteService;
        this.baseStoreService = baseStoreService;
        this.guidKeyGenerator = guidKeyGenerator;
        this.keyGenerator = keyGenerator;
        this.modelService = modelService;
        this.userService = userService;
        this.commonI18NService = commonI18NService;
        this.userNetCheckingStrategy = userNetCheckingStrategy;
        this.cartType = cartType;
    }


    @Override
    public CartModel createCart(final String pidCode) {
        final String cartId = PID_CART + SEPERATOR + pidCode;
        final CartModel cartModel = createCartInternal();
        cartModel.setCode(cartId + SEPERATOR + cartModel.getCode());
        cartModel.setVisible(Boolean.FALSE);
        getModelService().save(cartModel);
        return cartModel;
    }

    @Override
    protected CartModel createCartInternal() {
        final UserModel user = getUserService().getCurrentUser();
        final CurrencyModel currency = getCommonI18NService().getCurrentCurrency();
        final String cartModelTypeCode = StringUtils.defaultIfBlank(getCartType(), "Cart");
        final CartModel cart = getModelService().create(cartModelTypeCode);
        cart.setCode(getKeyGenerator().generate().toString());
        cart.setUser(user);
        cart.setCurrency(currency);
        cart.setDate(new Date());
        cart.setNet(getUserNetCheckingStrategy().isNetUser(user));
        cart.setNet(getNetGrossStrategy().isNet());
        cart.setSite(getBaseSiteService().getCurrentBaseSite());
        cart.setStore(getBaseStoreService().getCurrentBaseStore());
        cart.setGuid(getGuidKeyGenerator().generate().toString());

        return cart;
    }

    public UserNetCheckingStrategy getUserNetCheckingStrategy() {
        return userNetCheckingStrategy;
    }

    @Override
    public KeyGenerator getGuidKeyGenerator() {
        return guidKeyGenerator;
    }

    public ApplicationContext getCtx() {
        return ctx;
    }

    public KeyGenerator getKeyGenerator() {
        return keyGenerator;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public UserService getUserService() {
        return userService;
    }

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    @Override
    public NetGrossStrategy getNetGrossStrategy() {
        return netGrossStrategy;
    }

    @Override
    public BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

    @Override
    public BaseStoreService getBaseStoreService() {
        return baseStoreService;
    }

    public String getCartType() {
        return cartType;
    }

    public void setCtx(final ApplicationContext ctx) {
        super.setApplicationContext(ctx);
        this.ctx = ctx;
    }

}
