#!/bin/bash
# To be executed in '<repository root>/gdal'

GDAL_DIR="${PWD}"
INSTALL_DIR="${GDAL_DIR}/install"

#  Remove existing build files, if any
rm -rf src/build
rm -rf "${INSTALL_DIR}"

# Create build directories
mkdir src/build
mkdir "${INSTALL_DIR}"
cd src/build || exit

# Build GDAL with CMake
cmake -G Ninja \
      -DCMAKE_PREFIX_PATH="${CONDA_PREFIX}" \
      -DCMAKE_INSTALL_PREFIX="${INSTALL_DIR}" \
      -UGDAL_ENABLE_DRIVER_* -UOGR_ENABLE_DRIVER_* \
      -DGDAL_BUILD_OPTIONAL_DRIVERS:BOOL=OFF -DOGR_BUILD_OPTIONAL_DRIVERS:BOOL=OFF \
      -DGDAL_SET_INSTALL_RELATIVE_RPATH=ON \
      -DOGR_ENABLE_DRIVER_KML=ON \
      -DBUILD_TESTING=OFF \
      -DBUILD_PYTHON_BINDINGS=OFF \
      -DBUILD_JAVA_BINDINGS=ON -DGDAL_JAVA_INSTALL_DIR="${INSTALL_DIR}/java" -DGDAL_JAVA_JNI_INSTALL_DIR="${INSTALL_DIR}/jni" \
      ..
cmake --build . --config Release --parallel
cmake --build . --target install --config Release

cd "${GDAL_DIR}" || exit

# Platform detection
case "$(uname -s)" in
    Darwin*)
        PLATFORM="macos"
        LIB_EXT="*.dylib"
        SEARCH_DIRECTORIES="${CONDA_PREFIX}/lib"
        ;;
    Linux*)
        PLATFORM="linux"
        LIB_EXT="*.so*"
        SEARCH_DIRECTORIES="${CONDA_PREFIX}/lib"
        ;;
    CYGWIN*|MINGW*|MSYS*|Windows_NT)
        PLATFORM="windows"
        LIB_EXT="*.dll"
        SEARCH_DIRECTORIES="${CONDA_PREFIX}/Library/bin"
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
find "${INSTALL_DIR}" \( -path "*/bin/*.dll" -o -path "*/lib/libgdal.${LIB_EXT}*" \) \
    -type f -not -path "*/cmake/*" \
    -exec cp -v {} "${NATIVE_DIR}/" \;

# Copy the GDAL JNI Library
if [ "${PLATFORM}" == "windows" ]; then
    find "${INSTALL_DIR}/jni" -type f -name "gdalalljni.dll" -exec cp -v {} "${NATIVE_DIR}/" \;
else
    find "${INSTALL_DIR}/jni" -type f -name "libgdalalljni${LIB_EXT}" -exec cp -v {} "${NATIVE_DIR}/" \;
fi

GDAL_LIB=$(find "${NATIVE_DIR}" -maxdepth 1 -type f \( -name "gdal.dll" -o -name "libgdal.${LIB_EXT}*" \) | head -n 1)
GDAL_JNI_LIB=$(find "${NATIVE_DIR}" -maxdepth 1 -type f -name "*gdalalljni*" | head -n 1)
echo "GDAL library: ${GDAL_LIB}"
echo "GDAL JNI library: ${GDAL_JNI_LIB}"

if [ -z "${GDAL_LIB}" ]; then
    echo "Error: Missing GDAL library"
else
    cmake -DBINARY_FILE="${GDAL_LIB}" \
          -DSEARCH_DIRECTORIES="${SEARCH_DIRECTORIES}" \
          -DOUTPUT_DIR="${NATIVE_DIR}" \
          -P runtime-dependencies.cmake
fi

if [ -z "${GDAL_JNI_LIB}" ]; then
    echo "Error: Missing GDAL JNI library"
else
    cmake -DBINARY_FILE="${GDAL_JNI_LIB}" \
          -DSEARCH_DIRECTORIES="${SEARCH_DIRECTORIES}" \
          -DOUTPUT_DIR="${NATIVE_DIR}" \
          -P runtime-dependencies.cmake
fi

read -p "GDAL build completed. Press any key to continue... " -n1 -s
