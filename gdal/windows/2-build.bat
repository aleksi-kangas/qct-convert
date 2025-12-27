@echo off
cd ..
if exist src\build rd /s /q src\build
mkdir src\build
cd src\build || exit /b 1
cmake -UGDAL_ENABLE_DRIVER_* -UOGR_ENABLE_DRIVER_* ^
      -DGDAL_BUILD_OPTIONAL_DRIVERS:BOOL=OFF -DOGR_BUILD_OPTIONAL_DRIVERS:BOOL=OFF ^
      -DGDAL_SET_INSTALL_RELATIVE_RPATH=ON ^
      -DCMAKE_INSTALL_PREFIX=../../install ^
      -DBUILD_JAVA_BINDINGS=ON -DGDAL_JAVA_INSTALL_DIR=../../install/lib -DGDAL_JAVA_JNI_INSTALL_DIR=../../install/lib ^
      ..
cmake --build . --parallel 4
cmake --build . --target install
