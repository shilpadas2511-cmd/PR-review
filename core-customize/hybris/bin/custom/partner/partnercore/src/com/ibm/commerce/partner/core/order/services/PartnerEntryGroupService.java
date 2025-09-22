package com.ibm.commerce.partner.core.order.services;

import com.ibm.commerce.common.core.model.IbmDeploymentTypeModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.order.EntryGroupService;
import java.util.List;
import javax.annotation.Nonnull;


/**
 * This interface is to declare Entry Group related methods.
 */
public interface PartnerEntryGroupService extends EntryGroupService {


    /**
     * Fetches the category entryGroup by entryGroupCode
     *
     * @return EntryGroup
     */
    EntryGroup getCategoryEntryGroup(AbstractOrderEntryModel orderEntry, CommerceCartParameter parameter,List<EntryGroup> newEntryGroups);

    /**
     * Fetches the category entryGroup by entryGroupCode
     *
     * @return EntryGroup
     */
    EntryGroup getEntryGroup(AbstractOrderModel abstractOrderModel, String entryGroupCode);

    /**
     * creating  category entryGroup
     *
     * @return EntryGroup
     */
    EntryGroup createCategoryEntryGroup(AbstractOrderModel abstractOrderModel,
        CategoryModel category);

    /**
     * Fetches the PID entryGroup by entryGroupCode from order
     *
     * @return EntryGroup
     */
    EntryGroup getPidEntryGroup(AbstractOrderModel abstractOrderModel,
        AbstractOrderEntryModel entryModel, String entryGroupCode);

    /**
     * Fetches the PID entryGroup by entryGroupCode from entryGroups
     *
     * @param entryGroups
     * @param deploymentTypeModel
     * @param entryGroupCode
     * @return EntryGroup
     */
    EntryGroup getPidEntryGroup(List<EntryGroup> entryGroups,
        IbmDeploymentTypeModel deploymentTypeModel, String entryGroupCode);

    /**
     * Creates a new Pid EntryGroup
     *
     * @param abstractOrderEntryModel
     * @param parameter
     * @return
     */
    EntryGroup createPidEntryGroup(AbstractOrderEntryModel abstractOrderEntryModel,
        CommerceCartParameter parameter);

    /**
     * Add Group Number to newEntryGroups
     *
     * @param newEntryGroups
     * @param order
     */
    void addGroupNumbers(@Nonnull final List<EntryGroup> newEntryGroups,
        @Nonnull final AbstractOrderModel order);

    /**
     * creating  category entryGroup
     *
     * @return EntryGroup
     */
    EntryGroup createYtyEntryGroup(AbstractOrderModel abstractOrderModel,
        AbstractOrderEntryModel entryModel,String entryGroupLabel);

    /**
     * Fetches the pid entryGroup
     *
     * @return EntryGroup
     */
    EntryGroup getPidEntryGroup(AbstractOrderEntryModel orderEntry, CommerceCartParameter parameter,
        List<EntryGroup> newEntryGroups);

    /**
     * Fetches the configId entryGroup
     *
     * @return EntryGroup
     */
    EntryGroup getConfigIdEntryGroup(AbstractOrderEntryModel orderEntry,
        CommerceCartParameter parameter, List<EntryGroup> newEntryGroups);
}
