# Changelog

All notable changes to this project are documented here.
The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## [Unreleased]

### Fixed

- `JsonConfig`: maps in a config file (such as chances per item) are taken as they are, so a server can add and remove
  entries; before, they were merged with the defaults key by key (added entries were dropped, removed ones came back).

## [0.1.0] - 2026-11-15

The first version, built with Player Titles.

### Added

- Permissions through Fabric's and NeoForge's permission APIs, config files in JSON, server-side translations,
  chest screens, dialogs, items changed only in packets (with the player they are for), text widths and wrapping.
