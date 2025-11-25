#!/usr/bin/env bash
set -euo pipefail

# Script helper to build desktop releases for forestech_app
# Usage: ./scripts/build_desktop.sh

ROOT_DIR="$(dirname "$(realpath "$0")")/.."
cd "$ROOT_DIR"

echo "[1/4] flutter pub get"
flutter pub get

echo "[2/4] Generating platform icons (requires assets/icon.png)"
if flutter pub run flutter_launcher_icons:main; then
  echo "Icons generated"
else
  echo "flutter_launcher_icons failed (plugin might not be available)." >&2
fi

OS="$(uname -s)"
case "$OS" in
  Linux*)
    echo "[3/4] Building Linux release..."
    flutter build linux --release
    echo "Linux build output: build/linux/x64/release/bundle"
    ;;
  Darwin*)
    echo "[3/4] Building macOS release..."
    flutter build macos --release
    echo "macOS build complete"
    ;;
  MINGW*|MSYS*|CYGWIN*|Windows*)
    echo "[3/4] Building Windows release..."
    flutter build windows --release
    echo "Windows build output: build\\windows\\runner\\Release\\"
    ;;
  *)
    echo "Unknown OS: ${OS}. Attempting Linux build as default."
    flutter build linux --release || echo "Build failed or unsupported on this host."
    ;;
esac

echo "Done."
