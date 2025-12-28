#!/bin/bash

# Directories
gdal_dir=$(dirname "$PWD")
repository_root_dir=$(dirname "$gdal_dir")

# Remove existing build files, if any
cd "$gdal_dir" || exit
sudo rm -rf "$gdal_dir/src/build"
sudo rm -rf "$gdal_dir/install"
mkdir "$gdal_dir/src/build"
cd "$gdal_dir/src/build" || exit

# Build
cmake -UGDAL_ENABLE_DRIVER_* -UOGR_ENABLE_DRIVER_* \
      -DGDAL_BUILD_OPTIONAL_DRIVERS:BOOL=OFF -DOGR_BUILD_OPTIONAL_DRIVERS:BOOL=OFF \
      -DOGR_ENABLE_DRIVER_KML=ON \
      -DCMAKE_C_COMPILER=/usr/bin/gcc -DCMAKE_CXX_COMPILER=/usr/bin/g++ \
      -DGDAL_SET_INSTALL_RELATIVE_RPATH=ON \
      -DCMAKE_INSTALL_PREFIX="$gdal_dir/install" \
      -DBUILD_JAVA_BINDINGS=ON -DGDAL_JAVA_INSTALL_DIR="$gdal_dir/install/lib" -DGDAL_JAVA_JNI_INSTALL_DIR="$gdal_dir/install/lib" \
      ..
cmake --build . --parallel 4
# Install
sudo cmake --build . --target install

# Copy native libraries
cd "$repository_root_dir" || exit
rm -f "$repository_root_dir/qct/native/macos/"*.dylib
cp "$gdal_dir/install/lib/"*.dylib "$repository_root_dir/qct/native/macos"
