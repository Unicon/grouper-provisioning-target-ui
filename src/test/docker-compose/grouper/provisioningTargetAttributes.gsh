grouperSession = GrouperSession.startRootSession();
addStem("etc:attribute", "provisioningTargets", "Provisioning Targets")

attributeStem = StemFinder.findByName(GrouperSession.staticGrouperSession(), "etc:attribute:provisioningTargets", true);
attrDef = attributeStem.addChildAttributeDef("provisioningCandidatesDef", AttributeDefType.attr);
attrDef.setAssignToGroup(true);
attrDef.setAssignToStem(true);
attrDef.setValueType(AttributeDefValueType.string);
attrDef.store();

attrDefName = attributeStem.addChildAttributeDefName(attrDef,  "provisioningCandidates", "provisioningCandidates");
attrDefName.setDescription("Grants groups eligibility to be assign provisioning attributes.");
attrDefName.store();

//-------

addStem("etc:attribute:provisioningTargets", "google", "Google")
attributeStem = StemFinder.findByName(GrouperSession.staticGrouperSession(), "etc:attribute:provisioningTargets:google", true);
attrDef = attributeStem.addChildAttributeDef("googleProvisioningTargetDef", AttributeDefType.attr);
attrDef.setAssignToGroup(true);
attrDef.setValueType(AttributeDefValueType.string);
attrDef.store();

attrDefName = attributeStem.addChildAttributeDefName(attrDef,  "googleFavoriteFood", "Google's Favorite Food");
attrDefName.setDescription("The food to bring to parties.");
attrDefName.store();

attrDefName = attributeStem.addChildAttributeDefName(attrDef,  "googleSwitchToO365", "Switch to Office 365?");
attrDefName.setDescription("Should we switch?");
attrDefName.store();

attrDefName = attributeStem.addChildAttributeDefName(attrDef,  "googleIrishGetsTheTitle", "Will the Irish get the title?");
attrDefName.setDescription("What do you think?");
attrDefName.store();

attrDefName = attributeStem.addChildAttributeDefName(attrDef,  "googleFirstName", "Your first name");
attrDefName.setDescription("Feel free to share");
attrDefName.store();


addStem("etc:attribute:provisioningTargets", "ad", "Active Directory")
attributeStem = StemFinder.findByName(GrouperSession.staticGrouperSession(), "etc:attribute:provisioningTargets:ad", true);
attrDef = attributeStem.addChildAttributeDef("adProvisioningTargetDef", AttributeDefType.attr);
attrDef.setAssignToGroup(true);
attrDef.setValueType(AttributeDefValueType.string);
attrDef.store();

attrDefName = attributeStem.addChildAttributeDefName(attrDef,  "adOrgUnit", "Organizational Unit");
attrDefName.setDescription("The OU to provisioning.");
attrDefName.store();

addRootStem("test", "test");
addStem("test", "provisionEnabledGroups", "Provision Enabled Groups");
grantPriv("test:provisionEnabledGroups", "jsmith", NamingPrivilege.CREATE);