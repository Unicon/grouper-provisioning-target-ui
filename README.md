Custom Provisioning Target Form
===============================

## Use Case
Some users need to be able to configure provisioning related settings (attributes), but the Grouper UI doesn't currently make that very user friendly.

## Solution
This solution will add functionality to the Grouper UI that enables group admins to configure various attribute related to provisioning.
 
Grouper admins can define Grouper attributes and manage security around those attributes via the standard Grouper mechanisms. Forms-specific 
metadata about each attribute is stored in the `grouper-ui.properties` file where element type, default values, and list of values can be specified. 

## Requirements
This enhancement requires a functioning Grouper UI.

## Build and Installation
The core code can be compiled by running `./gradlew jar`. The artifact library/jar will be found in `./build/libs/`. This jar needs to be placed in appropriate lib directory. It is `TOMCAT_HOME/webapps/grouper/WEB-INF/lib/`. (It is anticipated that this will be applied to a patched app directory.) 

`src/main/webapp/WEB-INF/grouperUi2/group/` contains a directory structure and two jsp files that need to be placed in the expanded Grouper UI webapp: `TOMCAT_HOME/webapps/grouper/WEB-INF/grouperUi2/group/`.

## Execution
(There is nothing to directly execute.)

## Properties
The **Grouper UI settings** are used to control how the attributes are rendered on the Provisioning Target page.

> Reference to [attributeName] in the following text refer to a fully qualified attribute name that has had the colons swapped for hyphens.
> So `etc:attribute:provisioningTargets:google:googleFavoriteFood` needs to be "encoded" as `etc-attribute-provisioningTargets-google-googleFavoriteFood`.

There are four types of elements that the attribute can be rendered as: Yes/No, True/False, user definable dropdown, and the default, a textbox.

This is specified as `grouperUi.provisioningTarget.[attributeName].type=<type>`. Possible options include, `yesNo`, `trueFalse`, and `select`. If a 
textbox is desired, this property should be commented out, deleted, or left empty. 
 

|Property Name|Default Value|Notes|
|-------------|-------------|-----|
|custom.provisioningTarget.[attributeName].type|yesNo, trueFalse, select, (empty/non-existent)|The type of the attribute to render as.|
|custom.provisioningTarget.[attributeName].default|(n/a)|(optional) The value to set if no value is already set. (see below)|
|custom.provisioningTarget.[attributeName].lov|(n/a)|a comma separate list of values|

If it is a `select` type, the default value, if defined, needs to be in the `lov` list. If the type is `yesNo`, then the default value 
must be `yes` or `no`, likewise the default value of a `trueFalse` type must be `true` or `false`, if defined.  

### Property Examples

```
# Indicatess which attribute must be on a folder/group to allow it have Provisioning Targets assigned to it.
custom.provisioningTargetCandidate.attributeDefName = etc:attribute:provisioningTargets:provisioningCandidates

# A dropdown with no default.
custom.provisioningTarget.etc-attribute-provisioningTargets-google-googleFavoriteFood.lov=pizza,ice cream, chocolate

# A yes/no with a default
custom.provisioningTarget.etc-attribute-provisioningTargets-google-googleSwitchToO365.type=yesNo
custom.provisioningTarget.etc-attribute-provisioningTargets-google-googleSwitchToO365.default=no

# A true/false with a default
custom.provisioningTarget.etc-attribute-provisioningTargets-google-googleIrishGetsTheTitle.type=trueFalse
custom.provisioningTarget.etc-attribute-provisioningTargets-google-googleIrishGetsTheTitle.default=true

# Setting a default of a textbox type
custom.provisioningTarget.etc-attribute-provisioningTargets-ad-adOrgUnit.default=something

```

## Permissions
Folders or groups must be tagged with an attribute name, specified by the `custom.provisioningTargetCandidate.attributeDefName property`.
Subjects that are allowed to create groups under this folder must have "Read Attribute" privs on the folder where the attribute is placed.
The subjects must also have `view` privs on the attribute Def

By default, no provisioning targets attributes are available for subjects (group admins) to assign to their groups. Group admins must be given `read` and `update` (assign)
 privs on the Attribute Def of the attribute set(s) that they are allowed to assign.  

## Local Development
This project has been supplemented with Docker. Docker's usage allows for quickly deploying the deployed artifact to a
consistent, repeatable, local Grouper environment, which facilitates consistent testing.

Docker (or docker-machine/boot2docker for Windows and OS X installations) should be locally installed. If using docker-machine is being used
the proper environment variables must be setup (i.e. those displayed by running `boot2docker up` or `docker-machine env <vm name>`). `docker-machine ip <vm name>` will return the IP of the 

Running `gradle clean && gradle runContainer` will compile the jar, build on top of the `grouper-demo` image (this could take 10-20 minutes
 the first time depending upon the network bandwidth available), and start an image. The image can be connected to from a browser by going to the port . `docker ps` will display info about the running container. Running
 `docker exec -it grouper-dev bash` will allow one to connect into the running image. 

When testing is complete, `exit` to leave the running container. Then run `gradle clean` to clean
  the environment. Now you are ready to make the necessary code changes and start over again.

The following test work against this container:

Using jsmith (limited user):

1. Create a group under Test/Provisioning Enabled Groups. From the group menu, select Provisioning Attributes. The no permissions message displayed.

Using banderson (admin):

1. Grant `read` and `update` privs to the `jsmith` user for the `etc:attribute:provisioningTargets:google:googleProvisioningTargetDef` attribute definition.

Using jsmith:

1. Refresh the page, and the Google attributes should appear.

Using banderson (admin):

1. Grant `read` and `update` privs to the `jsmith` user for the `etc:attribute:provisioningTargets:ad:adProvisioningTargetDef` attribute definition.

Using jsmith:

1. Refresh the page, and the AD attributes should appear as well.
1. Set some values and save.
1. Go back in an confirm the values saved properly.

