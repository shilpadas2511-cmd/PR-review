package com.ibm.commerce.partner.core.order.services.impl;

import com.ibm.commerce.common.core.model.IbmDeploymentTypeModel;
import com.ibm.commerce.common.core.model.IbmPartProductModel;
import com.ibm.commerce.common.core.model.IbmVariantProductModel;
import com.ibm.commerce.common.core.product.service.IbmProductService;
import com.ibm.commerce.partner.core.constants.PartnercoreConstants;
import com.ibm.commerce.partner.core.order.IbmCategoryEntryGroup;
import com.ibm.commerce.partner.core.order.IbmConfigIdEntryGroup;
import com.ibm.commerce.partner.core.order.IbmEntryGroup;
import com.ibm.commerce.partner.core.order.IbmPidEntryGroup;
import com.ibm.commerce.partner.core.order.services.PartnerEntryGroupService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.enums.GroupType;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.order.impl.DefaultEntryGroupService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nonnull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * This class is to define Entry Group related methods.
 */
public class DefaultPartnerEntryGroupService extends DefaultEntryGroupService implements
    PartnerEntryGroupService {

    private final IbmProductService productService;
    private final ConfigurationService configurationService;

    public DefaultPartnerEntryGroupService(final IbmProductService productService,
        final ConfigurationService configurationService) {
        this.productService = productService;
        this.configurationService = configurationService;
    }

    /**
     * fetch category Entry Group from order by entrygroupcode
     *
     * @param abstractOrderModel
     * @param entryGroupCode
     * @return EntryGroup
     */
    @Override
    public EntryGroup getEntryGroup(final AbstractOrderModel abstractOrderModel,
        final String entryGroupCode) {
        if (abstractOrderModel == null || CollectionUtils.isEmpty(
            abstractOrderModel.getEntryGroups()) || StringUtils.isBlank(entryGroupCode)) {
            return null;
        }

        return abstractOrderModel.getEntryGroups().stream().filter(IbmEntryGroup.class::isInstance)
            .filter(
                entryGroup -> StringUtils.equalsIgnoreCase(entryGroup.getLabel(), entryGroupCode))
            .findAny().orElse(null);
    }

    /**
     * create category entry group
     *
     * @param abstractOrderModel
     * @param category
     * @return EntryGroup
     */
    @Override
    public EntryGroup createCategoryEntryGroup(final AbstractOrderModel abstractOrderModel,
        final CategoryModel category) {
        final IbmCategoryEntryGroup entryGroup = new IbmCategoryEntryGroup();
        entryGroup.setErroneous(Boolean.FALSE);
        entryGroup.setExternalReferenceId(category.getCode());
        entryGroup.setGroupType(GroupType.CATEGORY);
        entryGroup.setLabel(category.getCode());
        entryGroup.setChildren(new ArrayList<>());
        return entryGroup;
    }

    /**
     * Add group number to Entry Group
     *
     * @param bundleEntryGroups
     * @param order
     */
    @Override
    public void addGroupNumbers(@Nonnull final List<EntryGroup> bundleEntryGroups,
        @Nonnull final AbstractOrderModel order) {
        final AtomicInteger groupNumber = new AtomicInteger(
            findMaxGroupNumber(order.getEntryGroups()));
        for (final EntryGroup bundleEntryGroup : bundleEntryGroups) {
            bundleEntryGroup.setGroupNumber(groupNumber.incrementAndGet());
        }
    }

    @Override
    public EntryGroup createYtyEntryGroup(final AbstractOrderModel abstractOrderModel,
        final AbstractOrderEntryModel entryModel, String entryGroupLabel) {
        final IbmCategoryEntryGroup entryGroup = new IbmCategoryEntryGroup();
        entryGroup.setErroneous(Boolean.FALSE);
        entryGroup.setExternalReferenceId(entryGroupLabel);
        entryGroup.setGroupType(GroupType.YTY);
        entryGroup.setLabel(entryGroupLabel);
        entryGroup.setChildren(new ArrayList<>());
        return entryGroup;
    }

    /**
     * fetch PID entry group from order by entryGroupCode
     *
     * @param abstractOrderModel
     * @param entryModel
     * @param entryGroupCode
     * @return EntryGroup
     */
    @Override
    public EntryGroup getPidEntryGroup(final AbstractOrderModel abstractOrderModel,
        final AbstractOrderEntryModel entryModel, final String entryGroupCode) {
        if (abstractOrderModel != null
            && entryModel.getProduct() instanceof IbmPartProductModel partProductModel) {
            return getPidEntryGroup(abstractOrderModel.getEntryGroups(),
                partProductModel.getDeploymentType(), entryGroupCode);
        }
        return null;
    }

    /**
     * fetch PID entry group from entry groups by entryGroupCode
     *
     * @param entryGroups
     * @param entryGroupCode
     * @return EntryGroup
     */
    @Override
    public EntryGroup getPidEntryGroup(final List<EntryGroup> entryGroups,
        final IbmDeploymentTypeModel deploymentTypeModel, final String entryGroupCode) {
        if (CollectionUtils.isEmpty(entryGroups) || deploymentTypeModel == null) {
            return null;
        }

        return entryGroups.stream()
            .filter(entryGroup -> CollectionUtils.isNotEmpty(entryGroup.getChildren()))
            .flatMap(entryGroup -> entryGroup.getChildren().stream())
            .filter(IbmPidEntryGroup.class::isInstance).map(IbmPidEntryGroup.class::cast).filter(
                entryGroup -> StringUtils.equalsIgnoreCase(entryGroup.getLabel(), entryGroupCode)
                    && StringUtils.equalsIgnoreCase(entryGroup.getDeploymentTypeCode(),
                    deploymentTypeModel.getCode())).findAny().orElse(null);
    }

    /**
     * create PID entry Group
     *
     * @param abstractOrderEntryModel
     * @param parameter
     * @return EntryGroup
     */
    @Override
    public EntryGroup createPidEntryGroup(final AbstractOrderEntryModel abstractOrderEntryModel,
        final CommerceCartParameter parameter) {
        IbmVariantProductModel pidProduct = getPidProduct(abstractOrderEntryModel, parameter);
        final IbmPidEntryGroup entryGroup = new IbmPidEntryGroup();
        entryGroup.setErroneous(Boolean.FALSE);
        entryGroup.setExternalReferenceId(pidProduct.getCode());
        entryGroup.setGroupType(GroupType.PID);
        entryGroup.setDeploymentTypeCode(
            getProductService().getDeploymentType(pidProduct).getCode());
        entryGroup.setLabel(pidProduct.getPartNumber());
        return entryGroup;
    }

    @Override
    public EntryGroup getCategoryEntryGroup(final AbstractOrderEntryModel orderEntry,
        CommerceCartParameter parameter, List<EntryGroup> newEntryGroups) {
        ProductModel productModel = getProductModel(parameter);
        final CategoryModel utLevel30Category = getProductService().getUtLevel30Category(
            productModel);
        EntryGroup categoryEntryGroup = getEntryGroup(orderEntry.getOrder(),
            utLevel30Category.getCode());
        if (categoryEntryGroup != null) {
            return categoryEntryGroup;
        }
        final EntryGroup newGroup = createCategoryEntryGroup(orderEntry.getOrder(),
            utLevel30Category);
        newEntryGroups.add(newGroup);
        return newGroup;
    }

    @Override
    public EntryGroup getPidEntryGroup(final AbstractOrderEntryModel orderEntry,
        final CommerceCartParameter parameter, List<EntryGroup> newEntryGroups) {
        IbmVariantProductModel productModel = getPidProduct(orderEntry, parameter);
        EntryGroup pidEntryGroup = getEntryGroup(orderEntry.getOrder(),
            productModel.getPartNumber());
        if (pidEntryGroup != null) {
            return pidEntryGroup;
        }

        final EntryGroup newGroup = createPidEntryGroup(orderEntry, parameter);
        newEntryGroups.add(newGroup);
        return newGroup;
    }

    @Override
    public EntryGroup getConfigIdEntryGroup(final AbstractOrderEntryModel orderEntry,
        final CommerceCartParameter parameter, final List<EntryGroup> newEntryGroups) {
        EntryGroup entryGroup = null;
        if (StringUtils.isNotBlank(parameter.getOldConfigId())) {
            //this is to support scenario of Editing a Configuration, which changes config id as well.
            entryGroup = getEntryGroup(orderEntry.getOrder(), parameter.getOldConfigId());
        }

        if (entryGroup != null) {
            //Update EntryGroup with latest Config Id
            entryGroup.setLabel(parameter.getConfigId());
            return entryGroup;
        }

        //this is to support scenario of Editing a Configuration, which changes config id as well.
        // Scenario of Multipid
        entryGroup = getEntryGroup(orderEntry.getOrder(), parameter.getConfigId());

        if (entryGroup != null) {
            return entryGroup;
        }

        //it means entry group does not exist for old or new ConfigId. Hence Create new Entry Group

        entryGroup = createConfigIdEntryGroup(orderEntry, parameter);
        newEntryGroups.add(entryGroup);
        return entryGroup;
    }

    public EntryGroup createConfigIdEntryGroup(final AbstractOrderEntryModel orderEntryModel,
        final CommerceCartParameter parameter) {
        final IbmConfigIdEntryGroup entryGroup = new IbmConfigIdEntryGroup();
        entryGroup.setErroneous(Boolean.FALSE);
        entryGroup.setExternalReferenceId(getConfiguratorPid(parameter).getCode());
        entryGroup.setGroupType(GroupType.CONFIG_ID);
        entryGroup.setLabel(parameter.getConfigId());
        entryGroup.setChildren(new ArrayList<>());
        return entryGroup;
    }

    protected IbmVariantProductModel getPidProduct(final AbstractOrderEntryModel orderEntry,
        CommerceCartParameter parameter) {

        final ProductModel productModel = getProductModel(parameter);
        if (productModel instanceof IbmVariantProductModel variantProductModel) {
            return variantProductModel;
        }
        if (orderEntry.getMasterEntry() != null && orderEntry.getMasterEntry()
            .getProduct() instanceof IbmVariantProductModel variantProductModel) {
            return variantProductModel;
        }
        return null;
    }

    protected ProductModel getProductModel(CommerceCartParameter parameter) {
        return parameter.getProduct();

    }

    protected ProductModel getConfiguratorPid(CommerceCartParameter parameter) {
        if (parameter.isPartProduct()) {
            return parameter.getProduct();
        }
        boolean isConfiguratorPidDisabled = getConfigurationService().getConfiguration()
            .getBoolean(PartnercoreConstants.FLAG_ORDER_CONFIGURATOR_PID_DISABLED, Boolean.TRUE);
        if (isConfiguratorPidDisabled || parameter.getConfiguratorPid() == null
            || parameter.getConfiguratorPid().equals(parameter.getProduct())) {
            return parameter.getProduct();
        }
        return parameter.getConfiguratorPid();
    }


    public IbmProductService getProductService() {
        return productService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }
}
