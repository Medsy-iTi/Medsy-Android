## Description
Describe your changes in detail here. Please include the motivation for this change and the problem it solves.

### Medsy Patient App
- Implemented streaming capabilities for request results (`StreamRequestResultUseCase`).
- Added support for custom items without a known product ID (nullable `productId`).
- Resolved merge conflicts with `develop` across navigation (`RootNavDisplay.kt`), domain usecases, and network DTOs.
- Fixed `Navigation 3` serialization bug in `OrderReview` routing by migrating `LongArray` to `List<Long>`.
- Addressed bug where selecting a single product would show all products on the `Order Review` screen due to empty `selectedItemIds`.

## Type of change
- [x] Bug fix (non-breaking change which fixes an issue)
- [x] New feature (non-breaking change which adds functionality)
- [ ] Breaking change (fix or feature that would cause existing functionality to not work as expected)
- [ ] Refactoring (improving code structure without changing behavior)

## How Has This Been Tested?
Please describe the tests that you ran to verify your changes.
- [x] Tested Order Review flow to verify single/multiple item selection.
- [x] Verified navigation serialization and arguments passing.
- [x] Checked Gradle builds for the `presentation` and `app` modules.

## Checklist:
- [x] I have performed a self-review of my own code
- [x] My changes generate no new warnings or errors
- [ ] I have added tests that prove my fix is effective or that my feature works
- [ ] New and existing unit tests pass locally with my changes
