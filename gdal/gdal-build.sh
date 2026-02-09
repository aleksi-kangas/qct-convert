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
      -DCMAKE_BUILD_TYPE=Release \
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

read -p "GDAL build completed. Press any key to continue... " -n1 -s

cd "${GDAL_DIR}" || exit
