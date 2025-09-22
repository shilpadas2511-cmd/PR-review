package com.ibm.commerce.partner.core.order.services.impl;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.ibm.commerce.common.core.model.IbmDeploymentTypeModel;
import com.ibm.commerce.common.core.model.IbmPartProductModel;
import com.ibm.commerce.common.core.model.IbmVariantProductModel;
import com.ibm.commerce.common.core.product.service.IbmProductService;
import com.ibm.commerce.partner.core.order.IbmEntryGroup;
import com.ibm.commerce.partner.core.order.IbmPidEntryGroup;
import com.ibm.commerce.partner.core.util.model.AbstractOrderEntryModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.AbstractOrderModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.CategoryModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.CommerceCartParameterTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.IbmPartProductModelTestDataGenerator;
import com.ibm.commerce.partner.core.util.model.IbmVariantProductModelTestDataGenerator;



/**
 * Test class for {@link DefaultPartnerEntryGroupService}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPartnerEntryGroupServiceTest
{

	private static final String TEST_DEPLOYMENT_CODE = "testDeploymentCode";
	private static final String TEST_DEPLOYMENT_CODE1 = "testDeploymentCode1";
	private static final String TEST_ENTRY_GROUP = "testEntryGroup";
	private static final String TEST_ENTRY_GROUP1 = "testEntryGroup1";
	private static final String CATEGORY_CODE = "Aspera";
	private final String PRODUCT_CODE = "50002";
	private final int ENTRY_NUMBER = 1;
	private final String PART_NUMBER = "1234";
	private final String CONFIG_CODE = "1234";

	@InjectMocks
	DefaultPartnerEntryGroupService defaultPartnerEntryGroupService;

	@Mock
	private ModelService modelService;
	@Mock
	private IbmProductService productService;

	AbstractOrderModel abstractOrderModel;
	EntryGroup entryGroup;
	IbmPidEntryGroup ibmPidEntryGroup;
	List<EntryGroup> entryGroups;
	List<EntryGroup> childrenEntryGroups;
	IbmVariantProductModel pidProduct;
	CommerceCartParameter commerceCartParameter;
	IbmPartProductModel ibmPartProductModel;
	IbmDeploymentTypeModel deploymentTypeModel;

	@Mock
	ConfigurationService configurationService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		defaultPartnerEntryGroupService = new DefaultPartnerEntryGroupService(productService,configurationService);

		entryGroup = new IbmEntryGroup();
		entryGroup.setLabel(TEST_ENTRY_GROUP);
		entryGroup.setGroupNumber(0);

		ibmPidEntryGroup = new IbmPidEntryGroup();
		ibmPidEntryGroup.setLabel(TEST_ENTRY_GROUP);
		ibmPidEntryGroup.setDeploymentTypeCode(TEST_DEPLOYMENT_CODE);
		childrenEntryGroups = new ArrayList<>();
		childrenEntryGroups.add(ibmPidEntryGroup);

		entryGroup.setChildren(childrenEntryGroups);

		entryGroups = new ArrayList<>();
		entryGroups.add(entryGroup);

		abstractOrderModel = AbstractOrderModelTestDataGenerator.createAbstractOrderModel("test", entryGroups);
		pidProduct = IbmVariantProductModelTestDataGenerator.createIbmVariantProduct(PRODUCT_CODE, PART_NUMBER, CONFIG_CODE);
		commerceCartParameter = CommerceCartParameterTestDataGenerator.createCommerceCartParamter(null, null, null);
		ibmPartProductModel = IbmPartProductModelTestDataGenerator.createProductData(PART_NUMBER);
		deploymentTypeModel = new IbmDeploymentTypeModel();
		deploymentTypeModel.setCode(TEST_DEPLOYMENT_CODE);
		ibmPartProductModel.setDeploymentType(deploymentTypeModel);
	}

	@Test
	public void testGetEntryGroup()
	{
		final EntryGroup resultEntryGroup = defaultPartnerEntryGroupService.getEntryGroup(abstractOrderModel,
				TEST_ENTRY_GROUP);
		Assert.assertEquals(entryGroup, resultEntryGroup);
	}

	@Test
	public void testGetEntryGroupWithOrderNull()
	{
		final EntryGroup resultEntryGroup = defaultPartnerEntryGroupService.getEntryGroup(null, null);
		Assert.assertNull(resultEntryGroup);
	}

	@Test
	public void testGetEntryGroupWithNull()
	{
		abstractOrderModel = AbstractOrderModelTestDataGenerator.createAbstractOrderModel("test", null);
		final EntryGroup resultEntryGroup = defaultPartnerEntryGroupService.getEntryGroup(abstractOrderModel,
				TEST_ENTRY_GROUP);
		Assert.assertNull(resultEntryGroup);
	}

	@Test
	public void testCreateCategoryEntryGroup()
	{
		final CategoryModel categoryModel = CategoryModelTestDataGenerator.createCategoryModel(CATEGORY_CODE, null);
		final EntryGroup entryGroup = defaultPartnerEntryGroupService.createCategoryEntryGroup(abstractOrderModel, categoryModel);
		Assert.assertEquals(categoryModel.getCode(), entryGroup.getExternalReferenceId());
	}

	@Test
	public void testAddGroupNumbers()
	{
		final EntryGroup ibmEntryGroup = new IbmEntryGroup();
		ibmEntryGroup.setLabel(TEST_ENTRY_GROUP);
		ibmEntryGroup.setGroupNumber(0);
		final List<EntryGroup> entryGroupList = new ArrayList<>();
		entryGroupList.add(ibmEntryGroup);
		final AbstractOrderModel abstractOrderModel = AbstractOrderModelTestDataGenerator.createAbstractOrderModel("test",
				entryGroupList);
		defaultPartnerEntryGroupService.addGroupNumbers(entryGroupList, abstractOrderModel);
		Assert.assertEquals(1, entryGroupList.get(0).getGroupNumber().intValue());
	}

	@Test
	public void testGetPidEntryGroup()
	{
		final AbstractOrderEntryModel abstractOrderEntryModel = AbstractOrderEntryModelTestDataGenerator
				.createAbstractOrderEntry(ENTRY_NUMBER, ibmPartProductModel);
		final EntryGroup resultEntrygroup = defaultPartnerEntryGroupService.getPidEntryGroup(abstractOrderModel,
				abstractOrderEntryModel, TEST_ENTRY_GROUP);
		Assert.assertNotNull(resultEntrygroup);
	}

	@Test
	public void testGetPidEntryGroupWithOrderNull()
	{
		final AbstractOrderEntryModel abstractOrderEntryModel = AbstractOrderEntryModelTestDataGenerator
				.createAbstractOrderEntry(ENTRY_NUMBER, ibmPartProductModel);
		final EntryGroup resultEntrygroup = defaultPartnerEntryGroupService.getPidEntryGroup(null,
				abstractOrderEntryModel, TEST_ENTRY_GROUP);
		Assert.assertNull(resultEntrygroup);
	}

	@Test
	public void testGetPidEntryGroupNotIbmPartProduct()
	{
		final AbstractOrderEntryModel abstractOrderEntryModel = AbstractOrderEntryModelTestDataGenerator
				.createAbstractOrderEntry(ENTRY_NUMBER);
		abstractOrderEntryModel.setProduct(pidProduct);
		final EntryGroup resultEntrygroup = defaultPartnerEntryGroupService.getPidEntryGroup(abstractOrderModel,
				abstractOrderEntryModel,
				TEST_ENTRY_GROUP);
		Assert.assertNull(resultEntrygroup);
	}

	@Test
	public void testGetPidEntryGroupDeploymentTypeNotSame()
	{
		final IbmDeploymentTypeModel ibmDeploymentType = new IbmDeploymentTypeModel();
		ibmDeploymentType.setCode(TEST_DEPLOYMENT_CODE1);
		final EntryGroup resultEntrygroup = defaultPartnerEntryGroupService.getPidEntryGroup(entryGroups, ibmDeploymentType,
				TEST_ENTRY_GROUP);
		Assert.assertNull(resultEntrygroup);
	}

	@Test
	public void testGetPidEntryGroupWithIbmDeploymentType()
	{
		final EntryGroup resultEntrygroup = defaultPartnerEntryGroupService.getPidEntryGroup(entryGroups, deploymentTypeModel,
				TEST_ENTRY_GROUP);
		Assert.assertNotNull(resultEntrygroup);
	}

	@Test
	public void testGetPidEntryGroupNotSameEntryGroupName()
	{
		final EntryGroup resultEntrygroup = defaultPartnerEntryGroupService.getPidEntryGroup(entryGroups, deploymentTypeModel,
				TEST_ENTRY_GROUP1);
		Assert.assertNull(resultEntrygroup);
	}

	@Test
	public void testGetPidEntryGroupNull()
	{
		final EntryGroup resultEntrygroup = defaultPartnerEntryGroupService.getPidEntryGroup(null, deploymentTypeModel,
				TEST_ENTRY_GROUP);
		Assert.assertNull(resultEntrygroup);
	}

	@Test
	public void testGetPidEntryGroupDeploymentTypeNull()
	{
		final EntryGroup resultEntrygroup = defaultPartnerEntryGroupService.getPidEntryGroup(entryGroups, null,
				TEST_ENTRY_GROUP);
		Assert.assertNull(resultEntrygroup);
	}

	@Test
	public void testCreatePidEntryGroup()
	{
		final AbstractOrderEntryModel abstractOrderEntryModel = AbstractOrderEntryModelTestDataGenerator
				.createAbstractOrderEntry(0);
		when(productService.getPidProduct(abstractOrderEntryModel.getProduct(), commerceCartParameter.getPidId()))
				.thenReturn(pidProduct);
		final EntryGroup resultEntrygroup = defaultPartnerEntryGroupService.createPidEntryGroup(abstractOrderEntryModel,
				commerceCartParameter);
		Assert.assertNotNull(resultEntrygroup);
	}

	@Test
	public void testCreatePidEntryGroupWithIbmPartProduct()
	{
		final AbstractOrderEntryModel abstractOrderEntryModel = AbstractOrderEntryModelTestDataGenerator
				.createAbstractOrderEntry(0, ibmPartProductModel);
		when(productService.getPidProduct(abstractOrderEntryModel.getProduct(), commerceCartParameter.getPidId()))
				.thenReturn(pidProduct);
		final EntryGroup resultEntrygroup = defaultPartnerEntryGroupService.createPidEntryGroup(abstractOrderEntryModel,
				commerceCartParameter);
		Assert.assertNotNull(resultEntrygroup);
	}

	@Test
	public void testCreatePidEntryGroupWithDeploymentTypeNull()
	{
		final IbmPartProductModel partProductModel = IbmPartProductModelTestDataGenerator.createProductData(PART_NUMBER);
		partProductModel.setDeploymentType(null);
		final AbstractOrderEntryModel abstractOrderEntryModel = AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntry(0,
				partProductModel);
		when(productService.getPidProduct(abstractOrderEntryModel.getProduct(), commerceCartParameter.getPidId()))
				.thenReturn(pidProduct);
		final EntryGroup resultEntrygroup = defaultPartnerEntryGroupService.createPidEntryGroup(abstractOrderEntryModel,
				commerceCartParameter);
		Assert.assertNotNull(resultEntrygroup);
	}

	@Test
	public void testCreateYtyEntryGroup() {
		AbstractOrderModel orderModel = AbstractOrderModelTestDataGenerator.createAbstractOrderModel("test", null);
		AbstractOrderEntryModel entryModel = AbstractOrderEntryModelTestDataGenerator.createAbstractOrderEntry(1);
		String label = "YTY_LABEL";
		EntryGroup entryGroup = defaultPartnerEntryGroupService.createYtyEntryGroup(orderModel, entryModel, label);
		Assert.assertNotNull(entryGroup);
		Assert.assertEquals(label, entryGroup.getLabel());
		Assert.assertEquals(label, entryGroup.getExternalReferenceId());
		Assert.assertEquals(de.hybris.platform.core.enums.GroupType.YTY, entryGroup.getGroupType());
		Assert.assertFalse(entryGroup.getErroneous());
		Assert.assertNotNull(entryGroup.getChildren());
		Assert.assertTrue(entryGroup.getChildren().isEmpty());
	}
}
