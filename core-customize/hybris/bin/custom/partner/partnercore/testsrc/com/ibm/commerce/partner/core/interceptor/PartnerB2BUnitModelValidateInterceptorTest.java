package com.ibm.commerce.partner.core.interceptor;

import com.ibm.commerce.partner.core.util.model.B2BCustomerModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.B2BUnitModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.UserGroupModelTestDataGenerator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.commerceservices.model.OrgUnitModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PartnerB2BUnitModelValidateInterceptorTest {

	private static final String UID = "10001";
	private static final String CHILD_UID = "20001";
	private static final String EMAIL = "test@test.com";

	@InjectMocks
	PartnerB2BUnitModelValidateInterceptor partnerB2BUnitModelValidateInterceptor;

	@Mock
	private InterceptorContext interceptorContext;

	@Mock
	B2BUnitModel b2BUnitModel;

	@Mock
	B2BCustomerModel b2BCustomerModel;

	@Mock
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;

	@Mock
	private UserService userService;

	@Mock
	private ModelService modelService;

	Set<B2BUnitModel> childUnits;
	B2BUnitModel childModel;

	@Mock
	OrgUnitModel orgUnitModel;

	@Before
	public void setUp() {
		partnerB2BUnitModelValidateInterceptor = new PartnerB2BUnitModelValidateInterceptor();
		partnerB2BUnitModelValidateInterceptor.setModelService(modelService);
		partnerB2BUnitModelValidateInterceptor.setUserService(userService);
		partnerB2BUnitModelValidateInterceptor.setB2bUnitService(b2bUnitService);

		Mockito.when(interceptorContext.getModelService()).thenReturn(modelService);
	}

	@Test
	public void testOnValidate() throws InterceptorException {
		Set<B2BCustomerModel> customerModelSet = new HashSet<>();
		Set<B2BUnitModel> b2BUnitModels = new HashSet<>();

		childUnits = new HashSet<>();
		b2BCustomerModel = B2BCustomerModelTestDataGenerator.createB2BCustomerModel(EMAIL);
		customerModelSet.add(b2BCustomerModel);
		b2BUnitModel = B2BUnitModelTestDataGenerator.createB2BUnitModel(UID, false, customerModelSet);
		b2BUnitModels.add(b2BUnitModel);

		childModel = B2BUnitModelTestDataGenerator.createB2BUnitModel(CHILD_UID, true, customerModelSet);
		childUnits.add(childModel);

		UserGroupModel userGroupModel = UserGroupModelTestDataGenerator.createUserGroupModel(UID);
		Mockito.when(userService.getUserGroupForUID(B2BConstants.B2BAPPROVERGROUP)).thenReturn(userGroupModel);
		Mockito.when(userService.isMemberOfGroup(b2BCustomerModel, userGroupModel)).thenReturn(false);
		Mockito.when(interceptorContext.getModelService().isNew(b2BUnitModel)).thenReturn(false);
		Mockito.when(b2bUnitService.getB2BUnits(b2BUnitModel)).thenReturn(childUnits);

		partnerB2BUnitModelValidateInterceptor.onValidate(b2BUnitModel, interceptorContext);

		Assert.assertFalse(childModel.getActive());
	}

	@Test
	public void testOnValidateNotB2BUnit() throws InterceptorException {
		partnerB2BUnitModelValidateInterceptor.onValidate(orgUnitModel, interceptorContext);
		Mockito.verify(userService, Mockito.times(0))
			.getUserGroupForUID(B2BConstants.B2BAPPROVERGROUP);
	}

	@Test
	public void testOnValidateUnitApproversEmpty() throws InterceptorException {
		b2BUnitModel = B2BUnitModelTestDataGenerator.prepareB2bUnitModel(UID);
		partnerB2BUnitModelValidateInterceptor.onValidate(b2BUnitModel, interceptorContext);
		Mockito.verify(userService, Mockito.times(0))
			.getUserGroupForUID(B2BConstants.B2BAPPROVERGROUP);
	}

	@Test
	public void testOnValidateActiveUnit() throws InterceptorException {
		b2BUnitModel = B2BUnitModelTestDataGenerator.createB2BUnitModelActive(UID, true);
		partnerB2BUnitModelValidateInterceptor.onValidate(b2BUnitModel, interceptorContext);
		Mockito.verify(b2bUnitService, Mockito.times(0)).getB2BUnits(b2BUnitModel);
	}

	@Test
	public void testOnValidateNewUnit() throws InterceptorException {
		b2BUnitModel = B2BUnitModelTestDataGenerator.createB2BUnitModelActive(UID, false);
		Mockito.when(interceptorContext.getModelService().isNew(b2BUnitModel)).thenReturn(true);
		partnerB2BUnitModelValidateInterceptor.onValidate(b2BUnitModel, interceptorContext);
		Mockito.verify(b2bUnitService, Mockito.times(0)).getB2BUnits(b2BUnitModel);
	}
}
