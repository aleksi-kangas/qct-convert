#!/bin/bash
# To be executed in '<repository root>/gdal' after executing 'gdal-build.sh'

set -e          # Exit on error
set -o pipefail # Capture errors in piped commands

error_handler() {
    echo "-------------------------------------------------------"
    echo "ERROR: Command failed at line $1"
    echo "Check the output above for specific details."
    echo "-------------------------------------------------------"
    exit 1
}

trap 'error_handler $LINENO' ERR
# --------------------------

GDAL_DIR="${PWD}"
INSTALL_DIR="${GDAL_DIR}/install"
NATIVE_DIR="${GDAL_DIR}/../qct/native/"

# Platform detection
case "$(uname -s)" in
    Darwin*)
        PLATFORM="macos"
        LIB_EXT="*.dylib"
        CONDA_LIBRARY_DIR="${CONDA_PREFIX}/lib"
        CONDA_SHARE_DIR="${CONDA_PREFIX}/share"
        ;;
    Linux*)
        PLATFORM="linux"
        LIB_EXT="*.so*"
        CONDA_LIBRARY_DIR="${CONDA_PREFIX}/lib"
        CONDA_SHARE_DIR="${CONDA_PREFIX}/share"
        ;;
    CYGWIN*|MINGW*|MSYS*|Windows_NT)
        PLATFORM="windows"
        LIB_EXT="*.dll"
        CONDA_LIBRARY_DIR="${CONDA_PREFIX}/Library/bin"
        CONDA_SHARE_DIR="${CONDA_PREFIX}/Library/share"
        ;;
    *)
        echo "Unsupported platform: $(uname -s)"
        exit 1
        ;;
esac

echo "Step: Initializing environment..."
echo "Detected platform: ${PLATFORM}"

echo "Cleaning and creating: ${NATIVE_DIR}"
rm -rf "${NATIVE_DIR}" || exit 1
mkdir -p "${NATIVE_DIR}" || exit 1

GDAL_LIB_SOURCE=$(find -L "${INSTALL_DIR}" \
    \( -path "*/bin/*.dll" -o -path "*/lib/libgdal${LIB_EXT}*" \) \
    -type f -not -path "*/cmake/*" | head -n 1)
GDAL_JNI_SOURCE=$(find -L "${INSTALL_DIR}" \
    -name "*gdalalljni*" \
    -type f | head -n 1)

if [ -z "${GDAL_LIB_SOURCE}" ] || [ -z "${GDAL_JNI_SOURCE}" ]; then
    echo "-------------------------------------------------------"
    echo "ERROR: Required libraries not found in source directory."
    echo "GDAL Source: ${GDAL_LIB_SOURCE:-MISSING}"
    echo "JNI Source:  ${GDAL_JNI_SOURCE:-MISSING}"
    echo "-------------------------------------------------------"

    echo "DEBUG: Listing entire source tree for investigation [${INSTALL_DIR}]:"
    ls -R -F "${INSTALL_DIR}"

    echo "-------------------------------------------------------"
    exit 1
fi

if [ "$PLATFORM" == "macos" ]; then
    echo "Step: Creating versioned symlink in native directory..."

    REAL_LIB_NAME=$(basename "${GDAL_LIB_SOURCE}")
    SYMLINK_NAME="libgdal.38.dylib"
    echo "Linking ${SYMLINK_NAME} -> ${REAL_LIB_NAME}"
    (cd "${NATIVE_DIR}" && ln -sf "${REAL_LIB_NAME}" "${SYMLINK_NAME}")
fi
cp -v "${GDAL_LIB_SOURCE}" "${NATIVE_DIR}/"
cp -v "${GDAL_JNI_SOURCE}" "${NATIVE_DIR}/"

GDAL_JNI_LIB="${NATIVE_DIR}/$(basename "${GDAL_JNI_SOURCE}")"

echo "Step: Resolving dependencies for ${PLATFORM} using CMake..."

cmake -DBINARY_FILE="${GDAL_JNI_LIB}" \
      -DSEARCH_DIRECTORIES="${NATIVE_DIR}" \
      -DOUTPUT_DIR="${NATIVE_DIR}" \
      -P runtime-dependencies.cmake || { echo "Dependency resolution failed for $LIB"; exit 1; }

SHARE_DIR="${GDAL_DIR}/../qct/share"
echo "Step: Setting up share directory at ${SHARE_DIR}"
rm -rf "${SHARE_DIR}" || exit 1
mkdir -p "${SHARE_DIR}" || exit 1

if [ -d "${INSTALL_DIR}/share/gdal" ]; then
    cp -rv "${INSTALL_DIR}/share/gdal" "${SHARE_DIR}/" || exit 1
    echo "GDAL data copied successfully."
else
    echo "Warning: GDAL share data folder not found at ${INSTALL_DIR}/share/gdal"
fi

if [ -d "${CONDA_SHARE_DIR}/proj" ]; then
    cp -rv "${CONDA_SHARE_DIR}/proj" "${SHARE_DIR}/" || exit 1
    echo "PROJ data copied from Conda."
else
    echo "Warning: PROJ share data folder not found at ${CONDA_SHARE_DIR}/proj"
fi

echo "Success: All GDAL components and dependencies copied."