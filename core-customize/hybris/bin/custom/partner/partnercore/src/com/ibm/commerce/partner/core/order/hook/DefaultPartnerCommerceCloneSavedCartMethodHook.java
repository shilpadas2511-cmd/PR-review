package com.ibm.commerce.partner.core.order.hook;

import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.model.IbmPartnerPidCartEntryModel;
import com.ibm.commerce.partner.core.model.IbmPartnerPidCartModel;
import de.hybris.platform.commerceservices.order.hook.CommerceCloneSavedCartMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceSaveCartParameter;
import de.hybris.platform.commerceservices.service.data.CommerceSaveCartResult;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.strategies.ordercloning.CloneAbstractOrderStrategy;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import java.util.Optional;
import org.apache.commons.collections4.CollectionUtils;

/**
 * Partner Commerce Hook to Clone ChildEntires
 */
public class DefaultPartnerCommerceCloneSavedCartMethodHook implements
    CommerceCloneSavedCartMethodHook {

    private final ModelService modelService;
    private final KeyGenerator keyGenerator;
    private final CloneAbstractOrderStrategy cloneAbstractOrderStrategy;

    public DefaultPartnerCommerceCloneSavedCartMethodHook(final ModelService modelService,
        final KeyGenerator keyGenerator,
        final CloneAbstractOrderStrategy cloneAbstractOrderStrategy) {
        this.modelService = modelService;
        this.keyGenerator = keyGenerator;
        this.cloneAbstractOrderStrategy = cloneAbstractOrderStrategy;
    }

    @Override
    public void beforeCloneSavedCart(final CommerceSaveCartParameter parameters) {
        //Deliberately Left empty
    }

    @Override
    public void afterCloneSavedCart(final CommerceSaveCartParameter parameters,
        final CommerceSaveCartResult saveCartResult) {
        final CartModel savedCart = saveCartResult.getSavedCart();
        final CartModel originalCart = parameters.getCart();
        savedCart.getEntries().forEach(savedCartEntry -> {

            Optional<AbstractOrderEntryModel> originalEntry = originalCart.getEntries().stream()
                .filter(entry -> entry.getEntryNumber().equals(savedCartEntry.getEntryNumber()))
                .findAny();

            if (originalEntry.isPresent()) {

                savedCartEntry.setProductConfiguration(
                    originalEntry.get().getProductConfiguration());
                if (CollectionUtils.isNotEmpty(originalEntry.get().getChildEntries())) {
                    IbmPartnerPidCartModel originalPidOrder = (IbmPartnerPidCartModel) originalEntry.get()
                        .getChildEntries().iterator().next().getOrder();
                    final String orderCode =
                        PartnercoreConstants.PID_CART + PartnercoreConstants.UNDERSCORE
                            + originalEntry.get().getProduct().getCode()
                            + PartnercoreConstants.UNDERSCORE + getKeyGenerator().generate()
                            .toString();

                    AbstractOrderModel clone = getCloneAbstractOrderStrategy().clone(null, null,
                        originalPidOrder, orderCode, IbmPartnerPidCartModel.class,
                        IbmPartnerPidCartEntryModel.class);
                    getModelService().save(clone);
                    savedCartEntry.setChildEntries(clone.getEntries());
                    getModelService().save(savedCartEntry);
                }
            }
        });
    }

    public KeyGenerator getKeyGenerator() {
        return keyGenerator;
    }

    public CloneAbstractOrderStrategy getCloneAbstractOrderStrategy() {
        return cloneAbstractOrderStrategy;
    }

    public ModelService getModelService() {
        return modelService;
    }
}
