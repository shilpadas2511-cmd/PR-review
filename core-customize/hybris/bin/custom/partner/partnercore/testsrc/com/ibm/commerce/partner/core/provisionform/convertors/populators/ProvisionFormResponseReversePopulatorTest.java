package com.ibm.commerce.partner.core.provisionform.convertors.populators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.anySet;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ibm.commerce.common.core.model.PartnerProductSetModel;
import com.ibm.commerce.partner.core.model.IbmPartnerCartModel;
import com.ibm.commerce.partner.core.model.PartnerProvisionFormModel;
import com.ibm.commerce.partner.core.model.PartnerProvisionFormsModel;
import com.ibm.commerce.partner.core.quote.provision.form.data.response.ProvisionFormProductResponseData;
import com.ibm.commerce.partner.core.quote.provision.form.data.response.ProvisionFormResponseData;
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ProvisionFormResponseReversePopulatorTest {

    @Mock
    private ModelService modelService;

    @Mock
    private SessionService sessionService;

    @Mock
    private UserService userService;

    @Mock
    private UserModel currentUser;

    @Mock
    private ProvisionFormResponseData provisionFormResponseData;

    @Mock
    private IbmPartnerCartModel cartModel;

    @Mock
    private PartnerProvisionFormsModel provisionForms;

    @Mock
    private PartnerProvisionFormModel provisionFormModel;

    @Mock
    private PartnerProvisionFormsModel partnerProvisionFormsModel;

    @Mock
    private PartnerProvisionFormModel partnerProvisionFormModel;

    @Mock
    private ProvisionFormProductResponseData provisionFormProductResponseData;

    @Mock
    private PartnerProductSetModel partnerProductSetModel;

    private ProvisionFormResponseReversePopulator populator;
    @Mock
    private CustomerEmailResolutionService customerEmailResolutionService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        populator = new ProvisionFormResponseReversePopulator(modelService, sessionService,
            userService, customerEmailResolutionService);

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(currentUser.getUid()).thenReturn("testUserUid");
        when(cartModel.getProvisionForms()).thenReturn(provisionForms);
        when(modelService.create(PartnerProvisionFormsModel.class)).thenReturn(provisionForms);
        when(modelService.create(PartnerProvisionFormModel.class)).thenReturn(provisionFormModel);
    }

    @Test
    public void testPopulateWhenProvisionFormsIsNull() {
        when(cartModel.getProvisionForms()).thenReturn(null);
        when(provisionFormResponseData.getId()).thenReturn("testFormId");
        when(provisionFormResponseData.getForms()).thenReturn(
            Collections.singletonList(provisionFormProductResponseData));

        Map<String, PartnerProductSetModel> mockProductSets = new HashMap<>();
        mockProductSets.put("productSetCode", mock(PartnerProductSetModel.class));
        when(sessionService.getAttribute("partnerSetCodes")).thenReturn(mockProductSets);
        when(provisionFormProductResponseData.getProvisioningFormUrl()).thenReturn("url");
        when(provisionFormProductResponseData.getProvisioningFormId()).thenReturn("formId");
        when(provisionFormProductResponseData.getProductSetCode()).thenReturn("productSetCode");

        populator.populate(provisionFormResponseData, cartModel);

        verify(modelService).create(PartnerProvisionFormsModel.class);
        verify(provisionForms).setCode("testFormId");
        verify(provisionForms).setAllowedEditUsers("testUserUid");
        verify(modelService).save(provisionForms);
        verify(cartModel).setProvisionForms(provisionForms);
    }

    @Test
    public void testPopulateWhenProvisionFormsIsNotNull() {
        when(provisionFormResponseData.getId()).thenReturn("testFormId");
        when(provisionFormResponseData.getForms()).thenReturn(
            Collections.singletonList(provisionFormProductResponseData));

        Map<String, PartnerProductSetModel> mockProductSets = new HashMap<>();
        mockProductSets.put("productSetCode", mock(PartnerProductSetModel.class));
        when(sessionService.getAttribute("partnerSetCodes")).thenReturn(mockProductSets);
        when(provisionFormProductResponseData.getProvisioningFormUrl()).thenReturn("url");
        when(provisionFormProductResponseData.getProvisioningFormId()).thenReturn("formId");
        when(provisionFormProductResponseData.getProductSetCode()).thenReturn("productSetCode");

        populator.populate(provisionFormResponseData, cartModel);

        verify(modelService, times(0)).create(PartnerProvisionFormsModel.class);
        verify(provisionForms).setPartnerProvisionForm(anySet());
        verify(modelService).save(provisionForms);
        verify(cartModel).setProvisionForms(provisionForms);
    }

    @Test
    public void testPopulate_WhenValidData_UpdatesCartModel() {
        String currentUserUid = "user123";
        String formCode = "formCode123";
        String productSetCode = "productSetCode123";
        String expectedProductSetCode = "productSetCode123";
        Map<String, PartnerProductSetModel> partnerProductSets = Map.of(
            productSetCode, partnerProductSetModel
        );
        when(provisionFormResponseData.getId()).thenReturn(formCode);
        when(provisionFormResponseData.getForms()).thenReturn(
            Collections.singletonList((provisionFormProductResponseData)));
        when(userService.getCurrentUser()).thenReturn(mock(UserModel.class));
        when(userService.getCurrentUser().getUid()).thenReturn(currentUserUid);
        when(cartModel.getProvisionForms()).thenReturn(partnerProvisionFormsModel);
        when(partnerProvisionFormsModel.getAllowedEditUsers()).thenReturn(currentUserUid);
        when(sessionService.getAttribute("partnerSetCodes")).thenReturn(partnerProductSets);
        when(provisionFormProductResponseData.getProductSetCode()).thenReturn(productSetCode);
        when(modelService.create(PartnerProvisionFormsModel.class)).thenReturn(
            partnerProvisionFormsModel);
        when(modelService.create(PartnerProvisionFormModel.class)).thenReturn(
            partnerProvisionFormModel);

        Set<PartnerProvisionFormModel> provisionFormSet = new HashSet<>();
        provisionFormSet.add(partnerProvisionFormModel);

        populator.populate(provisionFormResponseData, cartModel);

        verify(modelService, times(1)).save(partnerProvisionFormsModel);
        verify(cartModel, times(1)).setProvisionForms(partnerProvisionFormsModel);
        assertEquals(currentUserUid, partnerProvisionFormsModel.getAllowedEditUsers());
    }

    @Test
    public void testPopulate_WhenProvisionFormsIsNull_CreatesProvisionForms() {
        String currentUserUid = "user123";
        when(cartModel.getProvisionForms()).thenReturn(null);
        when(modelService.create(PartnerProvisionFormsModel.class)).thenReturn(
            partnerProvisionFormsModel);
        when(partnerProvisionFormsModel.getAllowedEditUsers()).thenReturn(currentUserUid);

        populator.populate(provisionFormResponseData, cartModel);

        verify(modelService, times(1)).create(PartnerProvisionFormsModel.class);
    }

    @Test
    public void testCreateProvisionForm() {
        String formUrl = "http://example.com";
        String formId = "form123";
        String productSetCode = "productSetCode123";
        String currentUserUid = "user123";
        String expectedProductSetCode = "productSetCode123";
        Map<String, PartnerProductSetModel> partnerProductSets = Map.of(
            productSetCode, partnerProductSetModel
        );

        when(provisionFormProductResponseData.getProvisioningFormUrl()).thenReturn(formUrl);
        when(provisionFormProductResponseData.getProvisioningFormId()).thenReturn(formId);
        when(sessionService.getAttribute("partnerSetCodes")).thenReturn(Map.of(productSetCode, mock(
            PartnerProductSetModel.class)));
        when(partnerProvisionFormsModel.getAllowedEditUsers()).thenReturn(currentUserUid);
        when(modelService.create(PartnerProvisionFormModel.class)).thenReturn(
            partnerProvisionFormModel);
        when(partnerProvisionFormModel.getUrl()).thenReturn(formUrl);
        when(sessionService.getAttribute("partnerSetCodes")).thenReturn(partnerProductSets);
        when(provisionFormProductResponseData.getProductSetCode()).thenReturn(productSetCode);
        PartnerProvisionFormModel result = populator.createProvisionForm(
            provisionFormProductResponseData);
        assertNotNull(result);
        assertEquals(formUrl, result.getUrl());
    }

    @Test
    public void testPopulate_WhenProvisionFormsHasErrors() {
        when(provisionFormResponseData.getId()).thenReturn("testFormId");
        when(provisionFormResponseData.getForms()).thenReturn(Collections.singletonList(provisionFormProductResponseData));
        when(provisionForms.getAllowedEditUsers()).thenReturn("testUserUid");
        when(provisionForms.getErrors()).thenReturn("Some error");
        Map<String, PartnerProductSetModel> mockProductSets = new HashMap<>();
        mockProductSets.put("productSetCode", mock(PartnerProductSetModel.class));
        when(sessionService.getAttribute("partnerSetCodes")).thenReturn(mockProductSets);
        when(provisionFormProductResponseData.getProvisioningFormUrl()).thenReturn("url");
        when(provisionFormProductResponseData.getProvisioningFormId()).thenReturn("formId");
        when(provisionFormProductResponseData.getProductSetCode()).thenReturn("productSetCode");

        populator.populate(provisionFormResponseData, cartModel);

        verify(provisionForms).setErrors("");
        verify(modelService).save(provisionForms);
        verify(cartModel).setProvisionForms(provisionForms);
    }

}
