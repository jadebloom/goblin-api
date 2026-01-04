# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/) and [Angular](https://github.com/angular/angular/blob/main/CHANGELOG.md).

## [Unreleased]
### Added
- Added expenses' deletion based on their expense category's ID.
### Changed
- Changed the JWT token subject to contain user's ID too.
- Deleting a non-existing entity now returns HTTP 404, instead of a silent HTTP 204.
### Deprecated
- For soon-to-be removed features.
### Removed
- For now removed features.
### Fixed
- For any bug fixes.
### Security
- In case of vulnerabilities.

## [1.1.1] - 2026-01-02
### Fixed
- Fixed the mistmatches, caused by branching conflicts related to the version 1.1.0.
  
## [1.1.0] - 2026-01-02
### Added
- Added authentication and authorization, based on Spring Security.
- Added validation inside services.
- Added User entity and according relations to Currency, ExpenseCategory and Expense.
### Changed
- Updated tests and documentation.
