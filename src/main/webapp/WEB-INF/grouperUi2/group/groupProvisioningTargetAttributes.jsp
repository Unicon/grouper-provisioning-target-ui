<%@ include file="../assetsJsp/commonTaglib.jsp"%>

            <%-- for the new group or new stem button --%>
            <input type="hidden" name="objectStemId" value="${grouperRequestContainer.groupContainer.guiGroup.group.parentUuid}" />

            <div class="bread-header-container">
              ${grouperRequestContainer.groupContainer.guiGroup.breadcrumbs}
              <div class="page-header blue-gradient">
                <h1> <i class="fa fa-group"></i> ${grouper:escapeHtml(grouperRequestContainer.groupContainer.guiGroup.group.displayExtension)}
                <br /><small>${grouper:escapeHtml(grouperRequestContainer.groupContainer.guiGroup.group.description)}</small></h1>
              </div>
            </div>
            <div class="row-fluid">
              <div class="span12">
              <form id="editTargetProvisioning" class="form-horizontal">

                <input type="hidden" name="groupId" value="${grouperRequestContainer.groupContainer.guiGroup.group.id}" />


                  <c:if test="${not empty attributeDefinitions}">
                  <div id="provTargetsMetadataId" >

                  <c:forEach items="${attributeDefinitions}" var="provisioningTarget">
                      <h3 class="page-header">${grouper:escapeHtml(provisioningTarget.key.displayExtension)}</h3>

                      <c:forEach items="${provisioningTarget.value}" var="attr">
                          <div class="control-group">
                              <label for="${grouper:escapeHtml(attr.name)}" class="control-label">${grouper:escapeHtml(attr.displayExtension)}:</label>

                              <div class="controls">
                                  <%--
                                  <input type="text" id="${grouper:escapeHtml(attr.name)}" name="${grouper:escapeHtml(attr.name)}"
                                         value="${grouper:escapeHtml(assignedGroupAttributes[attr.name])}"/>
                                  --%>
                                  <c:out value="${assignedGroupAttributes[attr.name]}" escapeXml="false" />
                                  <span class="help-block">${grouper:escapeHtml(attr.description)}</span>
                              </div>
                          </div>
                      </c:forEach>

                  </c:forEach>

                  </div>


                    <div class="form-actions">
                        <a href="#" class="btn btn-primary" onclick="ajax('../app/UiV2GroupProvisioningTarget.groupEditAttributesSubmit', {formIds: 'editTargetProvisioning'}); return false;">Update</a>
                        <a href="#" class="btn btn-cancel" onclick="return guiV2link('operation=UiV2Group.viewGroup&groupId=${grouperRequestContainer.groupContainer.guiGroup.group.id}');" >Cancel</a>
                    </div>
                  </c:if>
                  <c:if test="${empty attributeDefinitions}">
                    <div id="provTargetsMetadataId" >
                        You are not authorized to configure any provisioning targets.
                    </div>

                      <div class="form-actions">
                          <a href="#" class="btn btn-cancel" onclick="return guiV2link('operation=UiV2Group.viewGroup&groupId=${grouperRequestContainer.groupContainer.guiGroup.group.id}');" >Go back</a>
                      </div>
                  </c:if>
                </form>

                </div>
              </div>
