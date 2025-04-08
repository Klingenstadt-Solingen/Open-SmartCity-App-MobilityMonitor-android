# [Changelog](https://keepachangelog.com/en/1.1.0/)

All significant changes (incl. versioning) are documented here.

## [Unreleased](https://git-dev.solingen.de/smartcityapp/modules/oscamobilitymonitor-android/-/releases)

## [1.2.3](https://git-dev.solingen.de/smartcityapp/modules/oscamobilitymonitor-android/-/tags/1.2.3)

### Changed
- Updated libraries
- Updated readme

## [1.2.2](https://git-dev.solingen.de/smartcityapp/modules/oscamobilitymonitor-android/-/tags/1.2.2)

### Changed
- Update readme

## [1.2.1](https://git-dev.solingen.de/smartcityapp/modules/oscamobilitymonitor-android/-/tags/1.2.1)

### Changed
- Update mobility data on interval

## [1.2.0](https://git-dev.solingen.de/smartcityapp/modules/oscamobilitymonitor-android/-/tags/1.2.0)

### Added

- Deeplink for mobility

### Changed

- Update all dependencies

### Fixed

- Marker NoSuchMethod exception crash due to outdated library

## [v1.1.2](https://git-dev.solingen.de/smartcityapp/modules/oscamobilitymonitor-android/-/tags/1.1.2)

### Fixed

- Misaligned user location due to unwanted callbacks

## [v1.1.1](https://git-dev.solingen.de/smartcityapp/modules/oscamobilitymonitor-android/-/tags/1.1.1)

### Fixed

- Default location being used even if actual location is available

## [v1.1.0](https://git-dev.solingen.de/smartcityapp/modules/oscamobilitymonitor-android/-/tags/1.1.0)

### Changed
- Update all dependencies

## [v1.0.2](https://git-dev.solingen.de/smartcityapp/modules/oscamobilitymonitor-android/-/tags/1.0.2)

### Fixed

- Crash if location permission is not given by using the default location instead

## [v1.0.1](https://git-dev.solingen.de/smartcityapp/modules/oscamobilitymonitor-android/-/tags/1.0.1)

### Add

* Show user location on map
* Enable "My Location" button

### Changed

* Zoom the map on the user location initially instead of bounds of all points or the atlantic if no points are provided
* Made size of map icons to be 2 times the original image (3 times previously)

### Fixed

* Misaligned symbols next to the departure estimate text by making the text have static width
* Convert departure time string from utc to local time so it is displayed correctly
* Deeplinks to third party apps (escooter/carsharing) now work properly by opening the app or a link to the Play Store instead of crashing

## [v1.0.0](https://git-dev.solingen.de/smartcityapp/modules/oscamobilitymonitor-android/-/tags/1.0.0)
