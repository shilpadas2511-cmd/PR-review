package com.ibm.commerce.partner.core.util.model;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.core.order.EntryGroup;

import java.util.List;


public class AbstractOrderModelTestDataGenerator
{

	public static AbstractOrderModel createAbstractOrderModel(final String code, final List<EntryGroup> entryGroups)
	{
		final AbstractOrderModel abstractOrderModel = new AbstractOrderModel();
		abstractOrderModel.setCode(code);
		abstractOrderModel.setEntryGroups(entryGroups);
		return abstractOrderModel;
	}

	public static AbstractOrderModel createAbstractOrderModel(final UserModel userModel) {
		AbstractOrderModel abstractOrderModel = new AbstractOrderModel();
		abstractOrderModel.setUser(userModel);
		return abstractOrderModel;
	}

	public static AbstractOrderModel createAbstractOrderModel() {
		AbstractOrderModel abstractOrderModel = new AbstractOrderModel();
		return abstractOrderModel;
	}
	public static AbstractOrderModel createAbstractOrderModel(final List<AbstractOrderEntryModel> entries) {
		AbstractOrderModel abstractOrderModel = new AbstractOrderModel();
		abstractOrderModel.setEntries(entries);
		return abstractOrderModel;
	}
}
