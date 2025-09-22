package com.ibm.commerce.partner.core.b2b.company.service.impl;

import com.ibm.commerce.partner.core.model.IbmPartnerB2BUnitModel;
import com.ibm.commerce.partner.core.util.model.B2BCustomerModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.B2BUnitModelTestDataGenerator;
import com.ibm.commerce.partner.core.utils.PartnerB2BUnitUtils;
import com.ibm.commerce.partner.core.utils.PartnerCountryUtils;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@UnitTest
public class DefaultPartnerB2BUnitServiceTest {

	private static final String UID = "100001";
	private static final String UID2 = "100002";
	private static final String GROUP_UID = "100001";
	private static final String EMAIL = "test@test.com";

	@InjectMocks
	DefaultPartnerB2BUnitService defaultPartnerB2BUnitService;

	@Mock
	private SearchRestrictionService searchRestrictionService;

	@Mock
	private UserService userService;

	@Mock
	B2BUnitModel b2BUnitModel;
	@Mock
	B2BUnitModel currentB2BUnitModel;
	@Mock
	UserModel currentUser;
	@Mock
	B2BCustomerModel b2BCustomerModel;

	@Mock
	ModelService modelService;
	@Mock
	IbmPartnerB2BUnitModel site;
	@Mock
	CountryModel country;
	@Mock
	CurrencyModel currency;
	@Mock
	private PrincipalGroupModel group1;
	@Mock
	private B2BUnitModel unit1, unit2;
	@Mock
	PartnerB2BUnitUtils partnerB2BUnitUtils;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		defaultPartnerB2BUnitService = new DefaultPartnerB2BUnitService(modelService);
		defaultPartnerB2BUnitService.setSearchRestrictionService(searchRestrictionService);
		defaultPartnerB2BUnitService.setUserService(userService);
	}

	@Test
	public void testGetUnitForUid() {
		b2BUnitModel = B2BUnitModelTestDataGenerator.prepareB2bUnitModel(UID);
		Mockito.when(userService.getUserGroupForUID(UID, B2BUnitModel.class)).thenReturn(b2BUnitModel);
		final B2BUnitModel unitForUid = defaultPartnerB2BUnitService.getUnitForUid(UID, true);
		Assert.assertEquals(unitForUid.getUid(), UID);
	}

	@Test
	public void testGetUnitForUidSearchRestrictionEnabled() {
		b2BUnitModel = B2BUnitModelTestDataGenerator.prepareB2bUnitModel(UID);
		Mockito.when(userService.getUserGroupForUID(UID, B2BUnitModel.class)).thenReturn(b2BUnitModel);
		final B2BUnitModel unitForUid = defaultPartnerB2BUnitService.getUnitForUid(UID, false);
		Assert.assertEquals(unitForUid.getUid(), UID);
	}

	@Test
	public void testIsActive() {
		b2BUnitModel = B2BUnitModelTestDataGenerator.prepareB2bUnitModel(UID);
		Mockito.when(userService.getCurrentUser()).thenReturn(currentUser);
		Mockito.when(currentUser.getGroups()).thenReturn(Set.of(b2BUnitModel));
		final DefaultPartnerB2BUnitService mockDefaultPartnerB2BUnitService = Mockito.spy(defaultPartnerB2BUnitService);
		Mockito.doReturn(true).when(mockDefaultPartnerB2BUnitService)
			.isActive(ArgumentMatchers.any(B2BUnitModel.class), ArgumentMatchers.any(B2BUnitModel.class));
		Assert.assertTrue(mockDefaultPartnerB2BUnitService.isActive(b2BUnitModel));
	}

	@Test
	public void testIsActiveFalse() {
		b2BUnitModel = B2BUnitModelTestDataGenerator.prepareB2bUnitModel(UID);
		Mockito.when(userService.getCurrentUser()).thenReturn(currentUser);
		Mockito.when(currentUser.getGroups()).thenReturn(Set.of(b2BUnitModel));
		final DefaultPartnerB2BUnitService mockDefaultPartnerB2BUnitService = Mockito.spy(defaultPartnerB2BUnitService);
		Mockito.doReturn(false).when(mockDefaultPartnerB2BUnitService)
			.isActive(ArgumentMatchers.any(B2BUnitModel.class), ArgumentMatchers.any(B2BUnitModel.class));
		Assert.assertFalse(mockDefaultPartnerB2BUnitService.isActive(b2BUnitModel));
	}

	@Test
	public void testIsActiveCurrentUserNull() {
		b2BUnitModel = B2BUnitModelTestDataGenerator.prepareB2bUnitModel(UID);
		Mockito.when(userService.getCurrentUser()).thenReturn(null);
		Assert.assertFalse(defaultPartnerB2BUnitService.isActive(b2BUnitModel));
	}

	@Test
	public void testIsActiveCurrentUserGroupsEmpty() {
		b2BUnitModel = B2BUnitModelTestDataGenerator.prepareB2bUnitModel(UID);
		Mockito.when(userService.getCurrentUser()).thenReturn(currentUser);
		Mockito.when(currentUser.getGroups()).thenReturn(Collections.EMPTY_SET);
		Assert.assertFalse(defaultPartnerB2BUnitService.isActive(b2BUnitModel));
	}

	@Test
	public void testIsActiveWithGroupUnit() {
		b2BUnitModel = B2BUnitModelTestDataGenerator.prepareB2bUnitModel(UID2);
		currentB2BUnitModel = B2BUnitModelTestDataGenerator.prepareB2bUnitModel(UID2);
		Assert.assertTrue(defaultPartnerB2BUnitService.isActive(b2BUnitModel, currentB2BUnitModel));
	}

	@Test
	public void testIsActiveGroupUnitNull() {
		currentB2BUnitModel = B2BUnitModelTestDataGenerator.prepareB2bUnitModel(UID2);
		Assert.assertFalse(defaultPartnerB2BUnitService.isActive(null, currentB2BUnitModel));
	}

	@Test
	public void testIsActiveCurrentUnitNull() {
		b2BUnitModel = B2BUnitModelTestDataGenerator.prepareB2bUnitModel(UID2);
		Assert.assertFalse(defaultPartnerB2BUnitService.isActive(b2BUnitModel, null));
	}

	@Test
	public void testIsActiveCurrenUnitNotEqual() {
		b2BUnitModel = B2BUnitModelTestDataGenerator.prepareB2bUnitModel(UID);
		currentB2BUnitModel = B2BUnitModelTestDataGenerator.prepareB2bUnitModel(UID2);
		Assert.assertFalse(defaultPartnerB2BUnitService.isActive(b2BUnitModel, currentB2BUnitModel));
	}

	@Test
	public void testIsActiveUnitParentUnitTrue() {
		b2BUnitModel = B2BUnitModelTestDataGenerator.prepareB2bUnitModel(UID);
		currentB2BUnitModel = B2BUnitModelTestDataGenerator.prepareB2bUnitModel(UID2);
		final DefaultPartnerB2BUnitService mockDefaultPartnerB2BUnitService = Mockito.spy(defaultPartnerB2BUnitService);
		Mockito.doReturn(true).when(mockDefaultPartnerB2BUnitService)
			.isParentUnit(ArgumentMatchers.any(B2BUnitModel.class), ArgumentMatchers.any(B2BUnitModel.class));
		Mockito.doReturn(false).when(mockDefaultPartnerB2BUnitService)
			.isDistributorUnit(ArgumentMatchers.any(B2BUnitModel.class), ArgumentMatchers.any(B2BUnitModel.class));
		Assert.assertTrue(mockDefaultPartnerB2BUnitService.isActive(b2BUnitModel, currentB2BUnitModel));
	}

	@Test
	public void testIsActiveUnitDistributorUnitTrue() {
		b2BUnitModel = B2BUnitModelTestDataGenerator.prepareB2bUnitModel(UID);
		currentB2BUnitModel = B2BUnitModelTestDataGenerator.prepareB2bUnitModel(UID2);
		final DefaultPartnerB2BUnitService mockDefaultPartnerB2BUnitService = Mockito.spy(defaultPartnerB2BUnitService);
		Mockito.doReturn(false).when(mockDefaultPartnerB2BUnitService)
			.isParentUnit(ArgumentMatchers.any(B2BUnitModel.class), ArgumentMatchers.any(B2BUnitModel.class));
		Mockito.doReturn(true).when(mockDefaultPartnerB2BUnitService)
			.isDistributorUnit(ArgumentMatchers.any(B2BUnitModel.class), ArgumentMatchers.any(B2BUnitModel.class));
		Assert.assertTrue(mockDefaultPartnerB2BUnitService.isActive(b2BUnitModel, currentB2BUnitModel));
	}

	@Test
	public void testIsDistributorUnit() {
		b2BUnitModel = B2BUnitModelTestDataGenerator.createReportingOrganization(UID2);
		currentB2BUnitModel = B2BUnitModelTestDataGenerator.prepareB2bUnitModel(UID2);
		Assert.assertTrue(defaultPartnerB2BUnitService.isDistributorUnit(b2BUnitModel, currentB2BUnitModel));
	}

	@Test
	public void testIsDistributorUnitParentUnitTrue() {
		b2BUnitModel = B2BUnitModelTestDataGenerator.createReportingOrganization(UID);
		currentB2BUnitModel = B2BUnitModelTestDataGenerator.prepareB2bUnitModel(UID2);
		final DefaultPartnerB2BUnitService mockDefaultPartnerB2BUnitService = Mockito.spy(defaultPartnerB2BUnitService);
		Mockito.doReturn(true).when(mockDefaultPartnerB2BUnitService)
			.isParentUnit(ArgumentMatchers.any(B2BUnitModel.class), ArgumentMatchers.any(B2BUnitModel.class));
		Assert.assertTrue(mockDefaultPartnerB2BUnitService.isDistributorUnit(b2BUnitModel, currentB2BUnitModel));
	}

	@Test
	public void testIsDistributorB2BUnitWithGroups() {
		b2BUnitModel = B2BUnitModelTestDataGenerator.createReportingOrganizationGroups(UID, GROUP_UID);
		currentB2BUnitModel = B2BUnitModelTestDataGenerator.prepareB2bUnitModel(UID);
		Assert.assertTrue(defaultPartnerB2BUnitService.isDistributorUnit(b2BUnitModel, currentB2BUnitModel));
	}

	@Test
	public void testIsDistributorB2BCurrentUnitNotEqual() {
		b2BUnitModel = B2BUnitModelTestDataGenerator.createReportingOrganization(UID);
		currentB2BUnitModel = B2BUnitModelTestDataGenerator.prepareB2bUnitModel(UID2);
		Assert.assertFalse(defaultPartnerB2BUnitService.isDistributorUnit(b2BUnitModel, currentB2BUnitModel));
	}

	@Test
	public void testIsDistributorB2BUnitNull() {
		currentB2BUnitModel = B2BUnitModelTestDataGenerator.prepareB2bUnitModel(UID2);
		Assert.assertFalse(defaultPartnerB2BUnitService.isDistributorUnit(null, currentB2BUnitModel));
	}

	@Test
	public void testIsDistributorCurrentUnitNull() {
		b2BUnitModel = B2BUnitModelTestDataGenerator.prepareB2bUnitModel(UID2);
		Assert.assertFalse(defaultPartnerB2BUnitService.isDistributorUnit(b2BUnitModel, null));
	}

	@Test
	public void testIsParentUnit() {
		b2BUnitModel = B2BUnitModelTestDataGenerator.crateB2BUnitModelGroups(UID, GROUP_UID);
		currentB2BUnitModel = B2BUnitModelTestDataGenerator.prepareB2bUnitModel(UID);
		Assert.assertTrue(defaultPartnerB2BUnitService.isParentUnit(b2BUnitModel, currentB2BUnitModel));
	}

	@Test
	public void testIsParentB2BUnitNull() {
		currentB2BUnitModel = B2BUnitModelTestDataGenerator.prepareB2bUnitModel(UID);
		Assert.assertFalse(defaultPartnerB2BUnitService.isParentUnit(null, currentB2BUnitModel));
	}

	@Test
	public void testIsParentCurrentUnitNull() {
		b2BUnitModel = B2BUnitModelTestDataGenerator.prepareB2bUnitModel(UID);
		Assert.assertFalse(defaultPartnerB2BUnitService.isParentUnit(b2BUnitModel, null));
	}

	@Test
	public void testIsParentB2BCurrentUnitNotEqual() {
		b2BUnitModel = B2BUnitModelTestDataGenerator.crateB2BUnitModelGroups(UID, GROUP_UID);
		currentB2BUnitModel = B2BUnitModelTestDataGenerator.prepareB2bUnitModel(UID2);
		Assert.assertFalse(defaultPartnerB2BUnitService.isParentUnit(b2BUnitModel, currentB2BUnitModel));
	}

	@Test
	public void testIsParentB2BUnitGroupsEmpty() {
		b2BUnitModel = B2BUnitModelTestDataGenerator.prepareB2bUnitModel(UID);
		currentB2BUnitModel = B2BUnitModelTestDataGenerator.prepareB2bUnitModel(UID2);
		Assert.assertFalse(defaultPartnerB2BUnitService.isParentUnit(b2BUnitModel, currentB2BUnitModel));
	}

	@Test
	public void testSetDefaultB2BUnit() {
		List<B2BUnitModel> b2BUnitModels = new ArrayList<>();
		b2BCustomerModel = B2BCustomerModelTestDataGenerator.createB2BCustomerModel(EMAIL);
		b2BUnitModel = B2BUnitModelTestDataGenerator.prepareB2bUnitModel(UID);
		b2BUnitModels.add(b2BUnitModel);
		defaultPartnerB2BUnitService.setDefaultB2BUnit(b2BCustomerModel, b2BUnitModels);
		Assert.assertNotNull(b2BCustomerModel.getDefaultB2BUnit());
	}

	@Test
	public void testIsUserAssociatedUnitTrue() {
		b2BUnitModel = B2BUnitModelTestDataGenerator.prepareB2bUnitModel(UID);
		Mockito.when(userService.getCurrentUser()).thenReturn(currentUser);
		Mockito.when(currentUser.getGroups()).thenReturn(Set.of(b2BUnitModel));
		final DefaultPartnerB2BUnitService mockDefaultPartnerB2BUnitService = Mockito.spy(defaultPartnerB2BUnitService);
		Assert.assertTrue(mockDefaultPartnerB2BUnitService.isUserAssociatedUnit(b2BUnitModel));
	}

	@Test
	public void testIsUserAssociatedUnitFalse() {
		b2BUnitModel = B2BUnitModelTestDataGenerator.prepareB2bUnitModel(UID);
		Mockito.when(userService.getCurrentUser()).thenReturn(currentUser);
		Mockito.when(currentUser.getGroups()).thenReturn(null);
		final DefaultPartnerB2BUnitService mockDefaultPartnerB2BUnitService = Mockito.spy(defaultPartnerB2BUnitService);
		Assert.assertFalse(mockDefaultPartnerB2BUnitService.isUserAssociatedUnit(b2BUnitModel));
	}

	@Test
	public void testSetActiveSitesToCustomer() {
		String DEFAULT_UID = "partnerB2BUnit";
		b2BUnitModel = B2BUnitModelTestDataGenerator.prepareB2bUnitModel(UID);
		List<B2BUnitModel> activeUnitModels = new ArrayList<>();
		activeUnitModels.add(b2BUnitModel);
		Set<PrincipalGroupModel> groups = new HashSet<>();
		PrincipalGroupModel group = new PrincipalGroupModel();
		B2BCustomerModel customerModel = new B2BCustomerModel();
		groups.add(group);
		groups.addAll(activeUnitModels);
		customerModel.setGroups(groups);

		final DefaultPartnerB2BUnitService mockDefaultPartnerB2BUnitService =
			Mockito.spy(defaultPartnerB2BUnitService);

		Mockito.when(site.getUid()).thenReturn("uid");

		// If these utils are not actually invoked by the code path, they can be left out.
		// If needed, wrap in mockStatic:
		try (MockedStatic<PartnerCountryUtils> pc = Mockito.mockStatic(PartnerCountryUtils.class)) {
			pc.when(() -> PartnerCountryUtils.isCountryActive(country)).thenReturn(false);
			pc.when(() -> PartnerCountryUtils.isCurrencyActive(currency)).thenReturn(false);

			mockDefaultPartnerB2BUnitService.setActiveSitesToCustomer(b2BCustomerModel, DEFAULT_UID, false);
			Mockito.verify(modelService, Mockito.never()).save(b2BCustomerModel);
		}
	}

	@Test
	public void testSaveFilteredCustomerGroups_WithUnitModels() {
		List<B2BUnitModel> unitModels = List.of(unit1, unit2);
		Set<PrincipalGroupModel> groups = new HashSet<>();

		defaultPartnerB2BUnitService.setDefaultB2BUnit(b2BCustomerModel, unitModels);
		defaultPartnerB2BUnitService.saveFilteredCustomerGroups(unitModels, groups, b2BCustomerModel, "defaultUnit");
		// No assertion: expecting no exception
	}

	@Test
	public void testSaveFilteredCustomerGroups_WithEmptyUnitModels() {
		List<B2BUnitModel> unitModels = Collections.emptyList();
		Set<PrincipalGroupModel> groups = new HashSet<>();

		defaultPartnerB2BUnitService.setDefaultPartnerB2BUnit(b2BCustomerModel, "defaultUnit");
		defaultPartnerB2BUnitService.saveFilteredCustomerGroups(unitModels, groups, b2BCustomerModel, "defaultUnit");
		// No assertion: expecting no exception
	}

	@Test
	public void testSaveFilteredCustomerGroups_WithNullUnitModels() {
		Set<PrincipalGroupModel> groups = new HashSet<>();

		defaultPartnerB2BUnitService.setDefaultPartnerB2BUnit(b2BCustomerModel, "defaultUnit");
		defaultPartnerB2BUnitService.saveFilteredCustomerGroups(null, groups, b2BCustomerModel, "defaultUnit");
		// No assertion: expecting no exception
	}

	@Test
	public void testSetNonTier1SitesToCustomer_WithNoGroups() {
		Mockito.when(b2BCustomerModel.getGroups()).thenReturn(null);
		defaultPartnerB2BUnitService.setNonTier1SitesToCustomer(b2BCustomerModel, "defaultUnit");
		// No assertion: expecting no exception
	}

	@Test
	public void testSetActiveSitesToCustomer_Exception() {
		B2BCustomerModel customer = new B2BCustomerModel();
		customer.setUid("testUser");

		try (MockedStatic<PartnerB2BUnitUtils> utilsMock = Mockito.mockStatic(PartnerB2BUnitUtils.class)) {
			utilsMock.when(() -> PartnerB2BUnitUtils.findAnyNotActiveSite(customer, "unit123"))
				.thenThrow(new RuntimeException("Simulated Exception"));

			// Replace assertDoesNotThrow with try/catch and fail on exception
			try {
				defaultPartnerB2BUnitService.setActiveSitesToCustomer(customer, "unit123", false);
			} catch (Throwable t) {
				Assert.fail("setActiveSitesToCustomer should not throw, but threw: " + t.getMessage());
			}

			// Call again to mirror original double-invocation intent
			try {
				defaultPartnerB2BUnitService.setActiveSitesToCustomer(customer, "unit123", false);
			} catch (Throwable t) {
				Assert.fail("Second call should not throw, but threw: " + t.getMessage());
			}
		}
	}

	@Test
	public void testSetNonTier1SitesToCustomer_Exception() {
		B2BCustomerModel customer = new B2BCustomerModel();
		customer.setUid("testUser");
		customer.setGroups(Set.of(group1));

		DefaultPartnerB2BUnitService spyService = Mockito.spy(defaultPartnerB2BUnitService);
		try (MockedStatic<PartnerB2BUnitUtils> utilsMock = Mockito.mockStatic(PartnerB2BUnitUtils.class)) {
			utilsMock.when(() -> PartnerB2BUnitUtils.getGroups(customer))
				.thenThrow(new RuntimeException("Simulated"));

			// Should swallow/handle internally; test just ensures no exception leaks
			try {
				spyService.setNonTier1SitesToCustomer(customer, "unit123");
			} catch (Throwable t) {
				Assert.fail("setNonTier1SitesToCustomer should not throw, but threw: " + t.getMessage());
			}
		}
	}

	@Test
	public void testSetDefaultB2BUnit_SameAsCurrent() {
		B2BUnitModel unit = B2BUnitModelTestDataGenerator.prepareB2bUnitModel(UID);
		B2BCustomerModel customer = new B2BCustomerModel();
		customer.setDefaultB2BUnit(unit); // same as first in list

		List<B2BUnitModel> units = new ArrayList<>();
		units.add(unit);

		defaultPartnerB2BUnitService.setDefaultB2BUnit(customer, units);

		// should skip setting again (silent pass)
		Assert.assertEquals(unit, customer.getDefaultB2BUnit());
	}

	@Test
	public void testSetActiveSitesToCustomer_ElseIfBranch() {
		B2BCustomerModel customer = new B2BCustomerModel();
		customer.setUid("testUser");

		try (MockedStatic<PartnerB2BUnitUtils> utilsMock = Mockito.mockStatic(PartnerB2BUnitUtils.class)) {
			utilsMock.when(() -> PartnerB2BUnitUtils.findAnyNotActiveSite(customer, "unit123"))
				.thenReturn(false);

			DefaultPartnerB2BUnitService spyService = Mockito.spy(defaultPartnerB2BUnitService);
			Mockito.doNothing().when(spyService).setNonTier1SitesToCustomer(customer, "unit123");

			spyService.setActiveSitesToCustomer(customer, "unit123", false);
			Mockito.verify(spyService).setNonTier1SitesToCustomer(customer, "unit123");
		}
	}

	@Test
	public void testSetActiveSitesToCustomer_IfBranchWithActiveUnits() {
		B2BCustomerModel customer = new B2BCustomerModel();
		customer.setUid("testUser");

		List<B2BUnitModel> activeUnits =
			List.of(B2BUnitModelTestDataGenerator.prepareB2bUnitModel("uid"));
		Set<PrincipalGroupModel> groups = new HashSet<>();

		try (MockedStatic<PartnerB2BUnitUtils> utilsMock = Mockito.mockStatic(PartnerB2BUnitUtils.class)) {
			utilsMock.when(() -> PartnerB2BUnitUtils.findAnyNotActiveSite(customer, "unit123"))
				.thenReturn(true);
			utilsMock.when(() -> PartnerB2BUnitUtils.getGroups(customer)).thenReturn(groups);
			utilsMock.when(() -> PartnerB2BUnitUtils.filteredActiveSites(customer)).thenReturn(activeUnits);
			utilsMock.when(() -> PartnerB2BUnitUtils.filteredNonTier1Sites(activeUnits)).thenReturn(activeUnits);

			DefaultPartnerB2BUnitService spyService = Mockito.spy(defaultPartnerB2BUnitService);
			Mockito.doNothing().when(spyService).saveFilteredCustomerGroups(
				ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()
			);

			spyService.setActiveSitesToCustomer(customer, "unit123", false);

			Mockito.verify(spyService).saveFilteredCustomerGroups(activeUnits, groups, customer, "unit123");
		}
	}

	@Test
	public void testSetNonTier1SitesToCustomer_WithUnitModels() {
		B2BCustomerModel customer = new B2BCustomerModel();
		customer.setUid("testUser");
		PrincipalGroupModel group = Mockito.mock(PrincipalGroupModel.class);
		customer.setGroups(Set.of(group));
		B2BUnitModel unitModel = B2BUnitModelTestDataGenerator.prepareB2bUnitModel("unit123");
		List<B2BUnitModel> unitModels = List.of(unitModel);

		try (MockedStatic<PartnerB2BUnitUtils> utilsMock = Mockito.mockStatic(PartnerB2BUnitUtils.class)) {
			utilsMock.when(() -> PartnerB2BUnitUtils.getGroups(customer))
				.thenReturn(Set.of(group));
			utilsMock.when(() -> PartnerB2BUnitUtils.getCustomerB2bUnits(customer))
				.thenReturn(unitModels);
			utilsMock.when(() -> PartnerB2BUnitUtils.filteredNonTier1Sites(unitModels))
				.thenReturn(unitModels);

			DefaultPartnerB2BUnitService spyService = Mockito.spy(defaultPartnerB2BUnitService);
			Mockito.doNothing().when(spyService)
				.saveFilteredCustomerGroups(unitModels, Set.of(group), customer, "unit123");

			spyService.setNonTier1SitesToCustomer(customer, "unit123");
			Mockito.verify(spyService)
				.saveFilteredCustomerGroups(unitModels, Set.of(group), customer, "unit123");
		}
	}
}
