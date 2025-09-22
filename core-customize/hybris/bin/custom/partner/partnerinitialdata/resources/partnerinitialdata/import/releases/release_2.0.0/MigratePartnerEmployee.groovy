package partnerinitialdata


import com.ibm.commerce.partner.core.model.PartnerEmployeeModel
flexibleSearchService = spring.getBean("flexibleSearchService")
modelService = spring.getBean("modelService")
String query = "SELECT {pk} FROM {Employee} WHERE {uid} LIKE '%@%' ";
result = flexibleSearchService.search(query);
result.getResult().each { Employee ->
    if(!(Employee  instanceof PartnerEmployeeModel)) {
        PartnerEmployeeModel partnerEmployee = modelService.create(PartnerEmployeeModel.class);
        partnerEmployee.setUid(Employee.getUid());
        partnerEmployee.setName(Employee.getName());
        partnerEmployee.setDescription(Employee.getDescription());
        partnerEmployee.setProfilePicture(Employee.getProfilePicture());
        partnerEmployee.setDefaultPaymentAddress(Employee.getDefaultPaymentAddress());
        partnerEmployee.setDefaultShipmentAddress(Employee.getDefaultShipmentAddress());
        partnerEmployee.setSessionLanguage(Employee.getSessionLanguage());
        partnerEmployee.setSessionCurrency(Employee.getSessionCurrency());
        partnerEmployee.setLastLogin(Employee.getLastLogin());
        partnerEmployee.setTokens(Employee.getTokens());
        partnerEmployee.setBackOfficeLoginDisabled(Employee.getBackOfficeLoginDisabled());
        partnerEmployee.setLoginDisabled(Employee.isLoginDisabled());
        partnerEmployee.setGroups(Employee.getAllGroups());
        try {
            modelService.remove(Employee);
            println("Employee  Removed  from the  System")
            modelService.save(partnerEmployee);
            println("Migrated user ${partnerEmployee.getUid()} successfully.")
        } catch (Exception e) {
            println("Failed to migrate user ${partnerEmployee.getUid()}: ${e.message}")
        }
    }
}

