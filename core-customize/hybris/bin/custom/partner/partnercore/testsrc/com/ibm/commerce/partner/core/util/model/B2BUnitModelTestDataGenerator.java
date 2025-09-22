package com.ibm.commerce.partner.core.util.model;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.AddressModel;
import com.ibm.commerce.partner.core.model.IbmB2BUnitModel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class B2BUnitModelTestDataGenerator {

    public static IbmB2BUnitModel createB2BUnitModel(final String uid, final boolean active,
        final Set<B2BCustomerModel> b2BCustomerModels) {
        final IbmB2BUnitModel b2BUnitModel = new IbmB2BUnitModel();
        b2BUnitModel.setUid(uid);
        b2BUnitModel.setActive(active);
        b2BUnitModel.setApprovers(b2BCustomerModels);
        return b2BUnitModel;
    }

    public static IbmB2BUnitModel prepareB2bUnitModel(final String uid) {
        final IbmB2BUnitModel b2BUnitModel = new IbmB2BUnitModel();
        b2BUnitModel.setUid(uid);
        return b2BUnitModel;
    }

    public static IbmB2BUnitModel prepareB2BUnitAddress(final String uid,
        final List<AddressModel> addressModels) {
        final IbmB2BUnitModel b2BUnitModel = prepareB2bUnitModel(uid);
        b2BUnitModel.setAddresses(addressModels);
        return b2BUnitModel;
    }

    public static IbmB2BUnitModel createB2BUnitModelActive(final String uid, final boolean active) {
        final IbmB2BUnitModel b2BUnitModel = new IbmB2BUnitModel();
        b2BUnitModel.setUid(uid);
        b2BUnitModel.setActive(active);
        return b2BUnitModel;
    }

    public static IbmB2BUnitModel createReportingOrganization(final String uid) {
        final IbmB2BUnitModel b2BUnitModel = new IbmB2BUnitModel();
        b2BUnitModel.setReportingOrganization(prepareB2bUnitModel(uid));
        return b2BUnitModel;
    }

    public static IbmB2BUnitModel crateB2BUnitModel() {
        final IbmB2BUnitModel b2BUnitModel = new IbmB2BUnitModel();
        return b2BUnitModel;
    }

	 public static IbmB2BUnitModel crateB2BUnitModelGroups(final String uid, final String groupId)
	 {
		 final IbmB2BUnitModel b2BUnitModel = new IbmB2BUnitModel();
		 b2BUnitModel.setUid(uid);
		 final PrincipalGroupModel groupModel = new PrincipalGroupModel();
		 groupModel.setUid(groupId);
		 final Set<PrincipalGroupModel> groups = new HashSet<>();
		 groups.add(groupModel);
		 b2BUnitModel.setGroups(groups);
		 return b2BUnitModel;
	 }

	 public static IbmB2BUnitModel createReportingOrganizationGroups(final String uid, final String groupId)
	 {
		 final IbmB2BUnitModel b2BUnitModel = new IbmB2BUnitModel();
		 b2BUnitModel.setReportingOrganization(crateB2BUnitModelGroups(uid, groupId));
		 return b2BUnitModel;
	 }

}