#!/bin/bash
# To be executed in '<repository root>/gdal' after executing 'gdal-build.sh'

GDAL_DIR="${PWD}"
INSTALL_DIR="${GDAL_DIR}/install"

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
echo "Detected platform: ${PLATFORM}"
NATIVE_DIR="${GDAL_DIR}/../qct/native/${PLATFORM}"
rm -rf "${NATIVE_DIR}"
mkdir -p "${NATIVE_DIR}"
echo "Native directory: ${NATIVE_DIR}"

# Copy GDAL library
find "${INSTALL_DIR}" \( -path "*/bin/*.dll" -o -path "*/lib/libgdal${LIB_EXT}*" \) \
    \( -type f -o -type l \) \
    -not -path "*/cmake/*" \
    -exec cp -v {} "${NATIVE_DIR}/" \;

# Copy GDAL JNI library
if [ "${PLATFORM}" == "windows" ]; then
    find "${INSTALL_DIR}/jni" -type f -name "gdalalljni.dll" \
    -exec cp -v {} "${NATIVE_DIR}/" \;
else
    find "${INSTALL_DIR}/jni" \( -type f -o -type l \) -name "libgdalalljni${LIB_EXT}" \
    -exec cp -v {} "${NATIVE_DIR}/" \;
fi

GDAL_LIB=$(find "${NATIVE_DIR}" -maxdepth 1 -type f \( -name "gdal.dll" -o -name "libgdal${LIB_EXT}*" \) | head -n 1)
GDAL_JNI_LIB=$(find "${NATIVE_DIR}" -maxdepth 1 -type f -name "*gdalalljni*" | head -n 1)
echo "GDAL library: ${GDAL_LIB}"
echo "GDAL JNI library: ${GDAL_JNI_LIB}"

# Copy runtime dependencies of GDAL library
if [ -z "${GDAL_LIB}" ]; then
    echo "Error: Missing GDAL library"
else
    cmake -DBINARY_FILE="${GDAL_LIB}" \
          -DSEARCH_DIRECTORIES="${CONDA_LIBRARY_DIR}" \
          -DOUTPUT_DIR="${NATIVE_DIR}" \
          -P runtime-dependencies.cmake
fi

# Copy runtime dependencies of GDAL JNI library
if [ -z "${GDAL_JNI_LIB}" ]; then
    echo "Error: Missing GDAL JNI library"
else
    cmake -DBINARY_FILE="${GDAL_JNI_LIB}" \
          -DSEARCH_DIRECTORIES="${CONDA_LIBRARY_DIR}" \
          -DOUTPUT_DIR="${NATIVE_DIR}" \
          -P runtime-dependencies.cmake
fi

# Copy GDAL & PROJ share data
SHARE_DIR="${GDAL_DIR}/../qct/share"
rm -rf "${SHARE_DIR}"
mkdir -p "${SHARE_DIR}"
echo "Copying GDAL and PROJ data to ${SHARE_DIR}..."

if [ -d "${INSTALL_DIR}/share/gdal" ]; then
    cp -r "${INSTALL_DIR}/share/gdal" "${SHARE_DIR}/"
    echo "GDAL data copied."
fi

if [ -d "${CONDA_SHARE_DIR}/proj" ]; then
    cp -r "${CONDA_SHARE_DIR}/proj" "${SHARE_DIR}/"
    echo "PROJ data copied from Conda."
fi

echo "GDAL libraries copied."
