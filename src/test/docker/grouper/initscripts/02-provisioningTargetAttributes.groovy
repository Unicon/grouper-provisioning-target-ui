import edu.internet2.middleware.grouper.*
import edu.internet2.middleware.grouper.attr.*

Stem attributeStem = new StemSave().
        assignName("etc:attribute:provisioningTargets").
        assignDisplayExtension("Provisioning Targets").
        assignCreateParentStemsIfNotExist(true).
        save()

AttributeDef attrDef = new AttributeDefSave().
        assignName("${attributeStem.name}:provisioningCandidatesDef").
        assignAttributeDefType(AttributeDefType.attr).
        assignToGroup(true).
        assignToStem(true).
        assignValueType(AttributeDefValueType.string).
        save()

AttributeDefName attrDefName = new AttributeDefNameSave(attrDef).
        assignName("${attributeStem.name}:provisioningCandidates").
        assignDescription("Grants groups eligibility to be assign provisioning attributes.").
        save()

//-------

def demoAttributeStem = new StemSave().
        assignName("${attributeStem.name}:google").
        assignDisplayExtension("Google").
        save()

AttributeDef demoAttrDef = new AttributeDefSave().
        assignName("${demoAttributeStem.name}:googleProvisioningTargetDef").
        assignAttributeDefType(AttributeDefType.attr).
        assignToGroup(true).
        assignValueType(AttributeDefValueType.string).
        save()

List<Tuple3<String, String, String>> demoAttrNames = [
        new Tuple3<String, String, String>("googleFavoriteFood", "Google's Favorite Food", "The food to bring to parties."),
        new Tuple3<String, String, String>("googleSwitchToO365", "Switch to Office 365?", "Should we switch?"),
        new Tuple3<String, String, String>("googleIrishGetsTheTitle", "Will the Irish get the title?", "What do you think?"),
        new Tuple3<String, String, String>("googleFirstName", "Your first name", "Feel free to share"),
]

demoAttrNames.each {extension, displayExtension, description ->
    AttributeDefName adn = new AttributeDefNameSave(demoAttrDef).
            assignName("${demoAttributeStem.name}:${extension}").
            assignDisplayExtension(displayExtension).
            assignDescription(description).
            save()
}

//-------

def adAttributeStem = new StemSave().
        assignName("${attributeStem.name}:ad").
        assignDisplayExtension("Active Directory").
        save()

AttributeDef adAttrDef = new AttributeDefSave().
        assignName("${adAttributeStem.name}:adProvisioningTargetDef").
        assignAttributeDefType(AttributeDefType.attr).
        assignToGroup(true).
        assignValueType(AttributeDefValueType.string).
        save()

AttributeDefName adAttrDefName = new AttributeDefNameSave(adAttrDef).
        assignName("${adAttributeStem.name}:adOrgUnit").
        assignDisplayExtension("Organizational Unit").
        assignDescription("The OU to provisioning.").
        save()

//-------

Group group = new GroupSave().assignName("test:testGroup").assignCreateParentStemsIfNotExist(true).save()
AttributeAssign attributeAssign = group.attributeDelegate.assignAttribute(attrDefName).attributeAssign
