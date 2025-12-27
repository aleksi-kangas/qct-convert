#!/bin/bash
cd ..
sudo rm -rf src/build
mkdir src/build
cd src/build || exit
cmake -UGDAL_ENABLE_DRIVER_* -UOGR_ENABLE_DRIVER_* \
      -DGDAL_BUILD_OPTIONAL_DRIVERS:BOOL=OFF -DOGR_BUILD_OPTIONAL_DRIVERS:BOOL=OFF \
      -DCMAKE_C_COMPILER=/usr/bin/gcc -DCMAKE_CXX_COMPILER=/usr/bin/g++ \
      -DGDAL_SET_INSTALL_RELATIVE_RPATH=ON \
      -DGDAL_IGNORE_FAILED_CONDITIONS=ON \
      -DCMAKE_INSTALL_PREFIX=../../install \
      -DBUILD_JAVA_BINDINGS=ON -DGDAL_JAVA_INSTALL_DIR=../../install/lib -DGDAL_JAVA_JNI_INSTALL_DIR=../../install/lib \
      ..
cmake --build . --parallel 4
sudo cmake --build . --target install
