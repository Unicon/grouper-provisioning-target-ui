# Changelog

## [1.2.0] - 2026-04-15

### Added
- Support for Grouper v6. Not likely to work with Grouper v4, so use release 1.1.0 for older Grouper versions.

### Improved
- Docker Compose integrated test setup
- Upgraded Gradle framework

---

## [1.1.0] - 2023-12-29

### Added
- **Provisioner-based form** - New `custom.provisioningTarget2.*` property namespace that maps directly to Grouper provisioner `configId` values (Option 1 in documentation),
replacing the legacy attribute-based approach (Option 2 in documentation).
- **Migration support** - The form displays both legacy attribute-based fields and new provisioner-based fields side-by-side to assist in transitioning between the two approaches.
- **Non-wheel user support** - Group admins with proper ACL privilege on specific provisioners can now manage provisioning targets without being in the wheel group. Users only see provisioners they are authorized to assign (via `provisioner.{id}.groupAllowedToAssign`).
- **Improved error handling** - When nothing is configurable for a user, the error is only shown once instead of repeatedly.
- **Gradle bootstrap** - Gradle wrapper added to the repository, removing the need for a pre-installed Gradle.
- **Grouper v4.x compatibility** - Updated to work with Grouper v2.3 through v4.x APIs.

---

## [1.0.0] - 2019-10-30

### Added
- **Attribute-based provisioning form** - Group admins can configure provisioning settings via a custom form driven by Grouper attributes defined under `etc:attribute:provisioningTargets`. Attribute subfolders become category headings; attribute display names and descriptions become field labels and help text.
- **Configurable field types** - Attributes can be rendered as yes/no dropdowns, true/false dropdowns, user-defined select lists, or plain text fields via `grouper-ui.properties` settings.
- **Default value support** - Attributes can specify a default value that is applied when no value has been assigned yet.
- **Candidate attribute gating** - A folder/group must be tagged with the attribute specified by `custom.provisioningTargetCandidate.attributeDefName` before the form is shown.
- **Grouper UI library integration** - Switched from server-side builds to the Grouper UI library, simplifying the build and deployment process.
- **Docker Compose test environment** - Local development environment using Docker Compose with Postgres (Grouper registry) and LDAP (subject source) containers, along with a pre-configured Grouper image.
- **Gradle build** - Build system for compiling the jar artifact and managing container lifecycle tasks (`runContainer`, `stopContainer`, `removeContainer`).

---

## [Initial Release] - 2016-05-24

- Initial implementation of a custom provisioning target attribute form for the Grouper UI, enabling group admins to configure provisioning-related attributes without direct access to Grouper admin tools.
