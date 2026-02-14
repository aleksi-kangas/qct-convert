#!/bin/bash
# To be executed in '<repository root>/gdal' after executing 'gdal-build.sh'

# --- CI Debugging Setup ---
set -e          # Exit on error
set -o pipefail # Capture errors in piped commands
# set -x        # Uncomment this to see every command executed (very loud, but helpful for CI)

# Function to handle errors and print line numbers
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

# Copy GDAL library
echo "Step: Copying GDAL libraries from ${INSTALL_DIR}..."
find "${INSTALL_DIR}" \( -path "*/bin/*.dll" -o -path "*/lib/libgdal${LIB_EXT}*" \) \
    \( -type f -o -type l \) \
    -not -path "*/cmake/*" \
    -exec cp -v {} "${NATIVE_DIR}/" \; || { echo "Failed to find or copy GDAL libs"; exit 1; }

# Copy GDAL JNI library
echo "Step: Copying JNI libraries..."
if [ "${PLATFORM}" == "windows" ]; then
    find "${INSTALL_DIR}/jni" -type f -name "gdalalljni.dll" \
    -exec cp -v {} "${NATIVE_DIR}/" \; || { echo "JNI copy failed (Windows)"; exit 1; }
else
    find "${INSTALL_DIR}/jni" \( -type f -o -type l \) -name "libgdalalljni${LIB_EXT}" \
    -exec cp -v {} "${NATIVE_DIR}/" \; || { echo "JNI copy failed (Unix)"; exit 1; }
fi

GDAL_LIB=$(find "${NATIVE_DIR}" -maxdepth 1 -type f \( -name "gdal.dll" -o -name "libgdal${LIB_EXT}*" \) | head -n 1)
GDAL_JNI_LIB=$(find "${NATIVE_DIR}" -maxdepth 1 -type f -name "*gdalalljni*" | head -n 1)

echo "GDAL library found at: ${GDAL_LIB:-NOT FOUND}"
echo "GDAL JNI library found at: ${GDAL_JNI_LIB:-NOT FOUND}"

# Copy runtime dependencies of GDAL library
if [ -z "${GDAL_LIB}" ]; then
    echo "Error: Missing GDAL library in ${NATIVE_DIR}"
    exit 1
else
    echo "Step: Resolving dependencies for ${GDAL_LIB}..."
    cmake -DBINARY_FILE="${GDAL_LIB}" \
          -DSEARCH_DIRECTORIES="${CONDA_LIBRARY_DIR}" \
          -DOUTPUT_DIR="${NATIVE_DIR}" \
          -P runtime-dependencies.cmake || { echo "CMake dependency resolution failed for GDAL_LIB"; exit 1; }
fi

# Copy runtime dependencies of GDAL JNI library
if [ -z "${GDAL_JNI_LIB}" ]; then
    echo "Error: Missing GDAL JNI library in ${NATIVE_DIR}"
    exit 1
else
    echo "Step: Resolving dependencies for ${GDAL_JNI_LIB}..."
    cmake -DBINARY_FILE="${GDAL_JNI_LIB}" \
          -DSEARCH_DIRECTORIES="${CONDA_LIBRARY_DIR}" \
          -DOUTPUT_DIR="${NATIVE_DIR}" \
          -P runtime-dependencies.cmake || { echo "CMake dependency resolution failed for JNI_LIB"; exit 1; }
fi

# Copy GDAL & PROJ share data
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