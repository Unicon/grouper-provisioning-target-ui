/**
 * ****************************************************************************
 * Copyright 2014 Internet2
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ****************************************************************************
 */
package edu.internet2.middleware.grouper.grouperUi.serviceLogic;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.internet2.middleware.grouper.*;
import edu.internet2.middleware.grouper.app.provisioning.GrouperProvisioningAttributeValue;
import edu.internet2.middleware.grouper.app.provisioning.GrouperProvisioningService;
import edu.internet2.middleware.grouper.app.provisioning.GrouperProvisioningSettings;
import edu.internet2.middleware.grouper.app.provisioning.GrouperProvisioningTarget;
import edu.internet2.middleware.grouper.app.provisioning.ProvisionableGroupFinder;
import edu.internet2.middleware.grouper.attr.AttributeDefName;
import edu.internet2.middleware.grouper.attr.finder.AttributeDefNameFinder;

import edu.internet2.middleware.grouper.grouperUi.beans.json.GuiResponseJs;
import edu.internet2.middleware.grouper.grouperUi.beans.json.GuiScreenAction;
import edu.internet2.middleware.grouper.grouperUi.beans.json.GuiScreenAction.GuiMessageType;
import edu.internet2.middleware.grouper.privs.AccessPrivilege;
import edu.internet2.middleware.grouper.privs.AttributeDefPrivilege;
import edu.internet2.middleware.grouper.ui.GrouperUiFilter;
import edu.internet2.middleware.grouper.ui.util.GrouperUiUserData;
import edu.internet2.middleware.grouper.ui.util.GrouperUiConfig;
import edu.internet2.middleware.grouper.ui.util.GrouperUiUtils;

import edu.internet2.middleware.grouper.userData.GrouperUserDataApi;
import edu.internet2.middleware.grouper.util.GrouperUtil;
import edu.internet2.middleware.subject.Subject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class UiV2GroupProvisioningTarget {

    private static final String PROVISIONING_TARGETS_ROOT_FOLDER = "etc:attribute:provisioningTargets";

    private static final String PROVISIONING_TARGETS_JSP = "/WEB-INF/grouperUi2/group/groupProvisioningTargetAttributes.jsp";

    private static final String MAIN_CONTENT_DIV_ID = "#grouperMainContentDivId";

    public static final String GROUPER_PROVISIONING_TARGET_CANDIDATE_ATTRIBUTE_DEF_NAME = "custom.provisioningTargetCandidate.attributeDefName";

    /** logger */
    private static final Log LOG = GrouperUtil.getLog(UiV2GroupProvisioningTarget.class);

    /**
     * Custom attributes handling
     */
    @SuppressWarnings("unchecked")
    public void groupEditAttributes(final HttpServletRequest request, HttpServletResponse response) {
        final String provisioningTargetCandidateAttributeDefName = GrouperUiConfig.retrieveConfig().propertyValueStringRequired(
                GROUPER_PROVISIONING_TARGET_CANDIDATE_ATTRIBUTE_DEF_NAME);

        final Subject loggedInSubject = GrouperUiFilter.retrieveSubjectLoggedIn();

        withCurrentGroupAndSession(request, new CurrentGroupAndSessionCallback() {

            @Override
            public void doWithGroupAndSession(Group group, GrouperSession session) {
                if (group.getAttributeDelegate().hasAttributeOrAncestorHasAttribute(provisioningTargetCandidateAttributeDefName, false)) {
                    Map<Stem, Set<AttributeDefName>> attributeDefinitionsByFolderName = getAllProvisioningTargetsAttributesGroupedByFolder(session);
                    Map<String, String> assignedGroupAttributes = new HashMap();
                    //Assigned Group attributes
                    for (AttributeDefName attr : flattenAllAttributeDefNames(attributeDefinitionsByFolderName)) {
                        String val = group.getAttributeValueDelegate().retrieveValueString(attr.getName());
                        val = (val == null ? "" : val);
                        assignedGroupAttributes.put(attr.getName(), buildUiElement(attr.getName(), val));
                    }

                    /* new provisioner objects */
                    Set<String> configuredProvisioners = GrouperUtil.splitTrimToSet(
                            GrouperUiConfig.retrieveConfig().propertyValueString("custom.provisioningTarget2.targetNames"), ",");

                    if (configuredProvisioners == null) {
                        configuredProvisioners = Collections.emptySet();
                    }

                    Map<String, List<Map<String, Object>>> provisioningTarget2Sections = new HashMap<>();

                    Map<String, GrouperProvisioningTarget> allTargets = GrouperProvisioningSettings.getTargets(true);

                    // loop each provisioner
                    for (String configId : configuredProvisioners) {
                        if (!allTargets.containsKey(configId)) {
                            LOG.error("Target '" + configId + "' defined in custom.provisioningTarget2.targetNames is an invalid provisioner name. Usable values are [" + GrouperUtil.join(allTargets.keySet().toArray(), ", ") + "]");
                            continue;
                        }
                        GrouperProvisioningTarget grouperProvisioningTarget = allTargets.get(configId);

                        boolean canAssignProvisioning = GrouperProvisioningService.isTargetEditable(grouperProvisioningTarget, loggedInSubject, group);
                        if (!canAssignProvisioning) {
                            LOG.warn("User not allowed to manager provisioner " + configId + ": " + loggedInSubject);
                            continue;
                        }

                        Map<String, Object> provPOJO = new HashMap<>();

                        String label = GrouperUiConfig.retrieveConfig().propertyValueString(
                                "custom.provisioningTarget2.targets." + configId + ".label",
                                "Sync to " + configId);
                        String description = GrouperUiConfig.retrieveConfig().propertyValueString(
                                "custom.provisioningTarget2.targets." + configId + ".description",
                                "Setting this to 'yes' will push this group and its members to " + configId + ".");
                        provPOJO.put("id", configId);
                        provPOJO.put("label", label);
                        provPOJO.put("description", description);

                        GrouperProvisioningAttributeValue provAttribute = new ProvisionableGroupFinder().assignGroup(group).assignTargetName(configId).findProvisionableGroupAttributeValue();
                        boolean isProvisioned = provAttribute == null ? false : provAttribute.isDoProvision();
                        provPOJO.put("isProvisioned", isProvisioned);

                        String section = GrouperUiConfig.retrieveConfig().propertyValueString(
                                "custom.provisioningTarget2.targets." + configId + ".section",
                                grouperProvisioningTarget.getName());
                        provPOJO.put("section", section);

                        if (!provisioningTarget2Sections.containsKey(section)) {
                            provisioningTarget2Sections.put(section, new ArrayList<Map<String, Object>>());
                        }
                        provisioningTarget2Sections.get(section).add(provPOJO);
                    }

                    //Send the data to the jsp
                    request.setAttribute("attributeDefinitions", attributeDefinitionsByFolderName);
                    request.setAttribute("assignedGroupAttributes", assignedGroupAttributes);
                    request.setAttribute("provisioningTarget2Sections", provisioningTarget2Sections);
                } else {
                    request.setAttribute("attributeDefinitions", null);
                    request.setAttribute("assignedGroupAttributes", null);
                    request.setAttribute("provisioningTarget2Sections", null);
                }
                GuiResponseJs.retrieveGuiResponseJs().addAction(GuiScreenAction.newInnerHtmlFromJsp(MAIN_CONTENT_DIV_ID, PROVISIONING_TARGETS_JSP));
            }
        });

    }


    /**
     * edit and assign attribute values.
     *
     * @param request
     * @param response
     */
    public void groupEditAttributesSubmit(final HttpServletRequest request, HttpServletResponse response) {
        withCurrentGroupAndSession(request, new CurrentGroupAndSessionCallback() {

            @Override
            public void doWithGroupAndSession(Group group, GrouperSession session) {
                for(AttributeDefName attr: getAllProvisioningTargetsAttributesDefNamesFlattened(session)) {
                    String attrValueReqParam = request.getParameter(attr.getName());
                    if(StringUtils.isBlank(attrValueReqParam)) {
                        //If incoming (UI) value is blank, just delete the value currently set for this attr
                        String assignedValue = group.getAttributeValueDelegate().retrieveValueString(attr.getName());
                        if(assignedValue != null) {
                            group.getAttributeValueDelegate().deleteValueString(attr.getName(), assignedValue);
                        }
                    }
                    else {
                        //For non-empty incoming values, just blindly assign them (it should update the current attr value, right?)
                        group.getAttributeValueDelegate().assignValue(attr.getName(), attrValueReqParam);
                    }
                }

                //UI dance
                GuiResponseJs guiResponseJs = GuiResponseJs.retrieveGuiResponseJs();

                //go to the view group screen
                guiResponseJs.addAction(
                        GuiScreenAction.newScript(String.format("guiV2link('operation=UiV2Group.viewGroup&groupId=%s')", group.getId())));

                //lets show a success message on the new screen
                guiResponseJs.addAction(GuiScreenAction.newMessage(GuiMessageType.success, "Data stored."));

                GrouperUserDataApi.recentlyUsedGroupAdd(GrouperUiUserData.grouperUiGroupNameForUserData(),
                        GrouperUiFilter.retrieveSubjectLoggedIn(), group);
            }
        });

    }

    /**
     * edit and assign attribute values.
     *
     * @param request
     * @param response
     */
    public void groupEditAttributes2Submit(final HttpServletRequest request, HttpServletResponse response) {
        final Subject loggedInSubject = GrouperUiFilter.retrieveSubjectLoggedIn();
        withCurrentGroupAndSession(request, new CurrentGroupAndSessionCallback() {

            @Override
            public void doWithGroupAndSession(Group group, GrouperSession session) {
                Set<String> configuredProvisioners = GrouperUtil.splitTrimToSet(
                        GrouperUiConfig.retrieveConfig().propertyValueString("custom.provisioningTarget2.targetNames"), ",");

                if (configuredProvisioners == null) {
                    configuredProvisioners = Collections.emptySet();
                }

                Map<String, GrouperProvisioningTarget> allTargets = GrouperProvisioningSettings.getTargets(true);

                for (String configId : configuredProvisioners) {
                    if (!allTargets.containsKey(configId)) {
                        LOG.error("Target '" + configId + "' defined in custom.provisioningTarget2.targetNames is an invalid provisioner name. Usable values are [" + GrouperUtil.join(allTargets.keySet().toArray(), ", ") + "]");
                        continue;
                    }

                    GrouperProvisioningTarget grouperProvisioningTarget = allTargets.get(configId);
                    if (grouperProvisioningTarget == null) {
                        throw new RuntimeException("Provisioner " + configId + " is an invalid provisioner name (not listed in custom.provisioningTarget2.targetNames");
                    }

                    String requestedValue = request.getParameter("provisioner-" + configId);
                    if (requestedValue == null) {
                        // this candidate not submitted
                        continue;
                    }

                    boolean isProvisionable = false;
                    if ("yes".equals(requestedValue)) {
                        isProvisionable = true;
                    } else if ("no".equals(requestedValue)) {
                        isProvisionable = false;
                    } else {
                        throw new RuntimeException("Invalid value for setting: '" + requestedValue + "'");
                    }

                    // if not provisionable, this doesn't have the option of removing the attribute entirely; it keeps the provisioning setting but sets the value to false
//                    GrouperProvisioningAttributeValue grouperProvisioningAttributeValue = new ProvisionableGroupSave().
//                            assignReplaceAllSettings(true).
//                            assignTargetName(configId).
//                            assignGroup(group).
//                            assignProvision(isProvisionable).
//                            save();

                    // this effectively deletes the attribute if it's not provisionable
                    GrouperProvisioningAttributeValue attributeValue = new GrouperProvisioningAttributeValue();
                    attributeValue.setDirectAssignment(isProvisionable);
                    attributeValue.setDoProvision(isProvisionable ? configId : null);
                    attributeValue.setTargetName(configId);

                    if (isProvisionable) {
                        GrouperProvisioningService.saveOrUpdateProvisioningAttributes(attributeValue, group);
                    } else {
                        GrouperProvisioningAttributeValue gpav = GrouperProvisioningService.getProvisioningAttributeValue(group, configId);
                        if (gpav != null) {
                            GrouperProvisioningService.deleteAttributeAssign(group, configId);
                        }
                    }
                }

                //UI dance
                GuiResponseJs guiResponseJs = GuiResponseJs.retrieveGuiResponseJs();

                //go to the view group screen
                guiResponseJs.addAction(
                        GuiScreenAction.newScript(String.format("guiV2link('operation=UiV2Group.viewGroup&groupId=%s')", group.getId())));

                //lets show a success message on the new screen
                guiResponseJs.addAction(GuiScreenAction.newMessage(GuiMessageType.success, "Data stored."));

                GrouperUserDataApi.recentlyUsedGroupAdd(GrouperUiUserData.grouperUiGroupNameForUserData(),
                        GrouperUiFilter.retrieveSubjectLoggedIn(), group);
            }
        });
    }

    private Map<Stem, Set<AttributeDefName>> getAllProvisioningTargetsAttributesGroupedByFolder(GrouperSession grouperSession) {
        //Custom attributes dance
        Stem rootProvisioningTargetsFolder = StemFinder.findByName(grouperSession, PROVISIONING_TARGETS_ROOT_FOLDER, true);
        Set<Stem> provisioningTargetsFolders = rootProvisioningTargetsFolder.getChildStems();

        final Map<Stem, Set<AttributeDefName>> attributeDefinitions = new HashMap();
        for (Stem folder : provisioningTargetsFolders) {
            Set<AttributeDefName> attrNames = new AttributeDefNameFinder()
                    .assignParentStemId(folder.getId()).assignStemScope(Stem.Scope.ONE)
                    .assignSubject(grouperSession.getSubject()).assignPrivileges(AttributeDefPrivilege.ATTR_READ_PRIVILEGES)
                    .findAttributeNames();

            if (attrNames.size() > 0) {
                attributeDefinitions.put(folder, attrNames);
            }
        }
        return attributeDefinitions;
    }

    private Set<AttributeDefName> flattenAllAttributeDefNames(Map<Stem, Set<AttributeDefName>> attributeDefNamesByFolderName) {
        Set<AttributeDefName> flattenedSetToUse = new HashSet<AttributeDefName>();
        Iterator<Set<AttributeDefName>> attrDefsIter = attributeDefNamesByFolderName.values().iterator();
        while (attrDefsIter.hasNext()) {
            flattenedSetToUse.addAll(attrDefsIter.next());
        }
        return flattenedSetToUse;
    }

    private Set<AttributeDefName> getAllProvisioningTargetsAttributesDefNamesFlattened(GrouperSession grouperSession) {
        return flattenAllAttributeDefNames(getAllProvisioningTargetsAttributesGroupedByFolder(grouperSession));
    }

    private CurrentGroupAndSession currentGroupAndSession(HttpServletRequest req) {
        final GrouperSession grouperSession = GrouperSession.start(GrouperUiFilter.retrieveSubjectLoggedIn());
        final Group group = RetrieveGroupHelperResult.retrieveGroupHelper(req, AccessPrivilege.UPDATE).getGroup();
        return new CurrentGroupAndSession(group, grouperSession);
    }

    /* Oh, java, just give me native tuples or simple structs values! */
    private static final class CurrentGroupAndSession {
        public final Group group;
        public final GrouperSession session;

        public CurrentGroupAndSession(Group group, GrouperSession session) {
            this.group = group;
            this.session = session;
        }
    }

    private void withCurrentGroupAndSession(HttpServletRequest request, CurrentGroupAndSessionCallback callback) {
        CurrentGroupAndSession currentGroupAndSession = null;
        try {
            currentGroupAndSession = currentGroupAndSession(request);
            if (currentGroupAndSession.group == null) {
                return;
            }
            callback.doWithGroupAndSession(currentGroupAndSession.group, currentGroupAndSession.session);
        }
        finally {
            if (currentGroupAndSession != null) {
                GrouperSession.stopQuietly(currentGroupAndSession.session);
            }
        }
    }

    private interface CurrentGroupAndSessionCallback {
        void doWithGroupAndSession(Group group, GrouperSession session);
    }

    public String escapeHtml(String input) {
        return GrouperUiUtils.escapeHtml(input, true);
    }

    public String getProperty(String name) {
        return (String) GrouperUiConfig.retrieveConfig().propertyValueString("custom.provisioningTarget." + name);
    }

    public String getProperty(String name, String defaultValue) {
        return (String) GrouperUiConfig.retrieveConfig().propertyValueString("custom.provisioningTarget." + name, defaultValue);
    }

    public String buildUiElement(String attributeName, String attributeValue) {
        final StringBuilder sb = new StringBuilder();
        final String escapedAttributeName = escapeHtml(attributeName);
        final String attributeDefault = getProperty(attributeName.replace(":", "-") + ".default");

        String escapedAttributeValue = escapeHtml(attributeValue == null || attributeValue.isEmpty() ? attributeDefault : attributeValue);
        //Something is cause null to be "null", so fix that.
        escapedAttributeValue = escapedAttributeValue == null || escapedAttributeValue.equalsIgnoreCase("null") ? null : escapedAttributeValue;

        final String attributeType = getProperty(attributeName.replace(":", "-") + ".type");

        if ("yesNo".equalsIgnoreCase(attributeType)) {
            sb.append("<select id=\"").append(escapedAttributeName).append("\" name=\"").append(escapedAttributeName).append("\">");
            sb.append("  <option value=\"yes\"" + (escapedAttributeValue.equalsIgnoreCase("yes") ? " selected " : "") + ">yes</option>");
            sb.append("  <option value=\"no\"" + (escapedAttributeValue.equalsIgnoreCase("no") ? " selected " : "") + ">no</option>");
            sb.append("</select>");

        } else if ("trueFalse".equalsIgnoreCase(attributeType)) {
            sb.append("<select id=\"").append(escapedAttributeName).append("\" name=\"").append(escapedAttributeName).append("\">");
            sb.append("  <option value=\"true\"" + (escapedAttributeValue.equalsIgnoreCase("true") ? " selected " : "") + ">true</option>");
            sb.append("  <option value=\"false\"" + (escapedAttributeValue.equalsIgnoreCase("false") ? " selected " : "") + ">false</option>");
            sb.append("</select>");

        } else if ("select".equalsIgnoreCase(attributeType)) {
            String[] lov = getProperty(attributeName.replace(":", "-") + ".lov").split(",");

            sb.append("<select id=\"").append(escapedAttributeName).append("\" name=\"").append(escapedAttributeName).append("\">");
            for (String value : lov) {
                if (attributeValue.equalsIgnoreCase(value)) {
                    sb.append("  <option value=\"").append(value).append("\" selected>").append(value).append("</option>");
                } else {
                    sb.append("  <option value=\"").append(value).append("\">").append(value).append("</option>");
                }
            }
            sb.append("</select>");

        } else {

            sb.append("<input type=\"text\" id=\"").append(escapedAttributeName)
                    .append("\" name=\"").append(escapedAttributeName).append("\" value=\"").append(escapedAttributeValue).append("\"/>");
        }

        return sb.toString();
    }

}
