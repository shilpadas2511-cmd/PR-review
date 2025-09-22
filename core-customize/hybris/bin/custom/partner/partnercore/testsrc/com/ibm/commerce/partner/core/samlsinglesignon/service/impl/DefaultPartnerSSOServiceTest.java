package com.ibm.commerce.partner.core.samlsinglesignon.service.impl;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.samlsinglesignon.SamlUserGroupDAO;
import de.hybris.platform.samlsinglesignon.model.SamlUserGroupModel;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultPartnerSSOServiceTest {

    public static final String ID = "123";
    public static final String ID_2 = "234";
    public static final String NAME = "testName";
    public static final String UID = "aa@mail.com";
    public static final String ROLE_1 = "1";
    public static final String ROLE_2 = "2";
    public static final String ROLE_3 = "3";
	 public static final String USER_INFO_NOT_EMPTY = "User info must not be empty";

    @InjectMocks
    DefaultPartnerSSOService partnerSSOService;
    @Mock
    private SamlUserGroupDAO samlUserGroupDAO;
    @Mock
    private SamlUserGroupModel samlUserGroupModel;
    @Mock
    private UserModel userModel;
    @Mock
    private TypeModel employeeType;
    @Mock
    private UserService userService;
    Collection roles;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        partnerSSOService = new DefaultPartnerSSOService() {

            @Override
            protected SSOUserMapping findMapping(final Collection<String> roles) {
                return this.findMappingInDatabase(roles);
            }
        };
        partnerSSOService.setUserService(userService);
        when(userModel.getUid()).thenReturn(UID);
        partnerSSOService.setSamlUserGroupDAO(samlUserGroupDAO);
        when(samlUserGroupModel.getUserType()).thenReturn(employeeType);
        when(samlUserGroupDAO.findSamlUserGroup(anyString())).thenReturn(
            Optional.of(samlUserGroupModel));
        roles = new ArrayList<String>(Arrays.asList(ROLE_1, ROLE_2, ROLE_3));
    }

    /**
     * test method for getOrCreateSSOUser method which is override ootb code for not creating new
     * user when user does not have specified role or user does not exist in Backoffice.
     */
    @Test
    public void testGetOrCreateSSOUser() {
        when(userService.getUserForUID(ID)).thenReturn(userModel);
        final UserModel userModel = partnerSSOService.getOrCreateSSOUser(ID, NAME, roles);
        Assert.assertEquals(UID, userModel.getUid());
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenNoUserFound() {
        when(userService.getUserForUID(ID)).thenThrow(IllegalArgumentException.class);
        final UserModel userModel = partnerSSOService.getOrCreateSSOUser(ID_2, NAME, roles);
        Assert.assertNull(userModel);
    }

	 @Test
	 public void testGetOrCreateSSOUserIdNull()
	 {
		 assertThrows(USER_INFO_NOT_EMPTY, IllegalArgumentException.class,
				 () -> partnerSSOService.getOrCreateSSOUser(null, NAME, roles));
	 }

	 @Test
	 public void testGetOrCreateSSOUserNameNull()
	 {
		 assertThrows(USER_INFO_NOT_EMPTY, IllegalArgumentException.class,
				 () -> partnerSSOService.getOrCreateSSOUser(ID, null, roles));
	 }
}
