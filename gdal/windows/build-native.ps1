function Get-CMakePrefixPath {
      $prefix = Read-Host "Enter CMAKE_PREFIX_PATH"
      if (-not $prefix) {
            Write-Error "No CMAKE_PREFIX_PATH provided. Exiting."
            exit 1
      }
      return $prefix
}

$prefixPath = Get-CMakePrefixPath
Write-Host "Using CMAKE_PREFIX_PATH: $prefixPath" -ForegroundColor Green

# Directories
$currentDir = Get-Location
$gdalDir = Split-Path $currentDir -Parent
$repositoryRootDir = Split-Path $gdalDir -Parent

# Remove existing build files, if any
Set-Location $gdalDir
Remove-Item -Recurse -Force "$gdalDir/src/build" -ErrorAction SilentlyContinue
Remove-Item -Recurse -Force "$gdalDir/install" -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Path "$gdalDir/src/build" -Force
Set-Location "$gdalDir/src/build"

# Build
cmake -UGDAL_ENABLE_DRIVER_* -UOGR_ENABLE_DRIVER_* `
      -DGDAL_BUILD_OPTIONAL_DRIVERS:BOOL=OFF -DOGR_BUILD_OPTIONAL_DRIVERS:BOOL=OFF `
      -DOGR_ENABLE_DRIVER_KML=ON `
      -DBUILD_TESTING=OFF `
      -DCMAKE_C_COMPILER=cl -DCMAKE_CXX_COMPILER=cl `
      -DGDAL_SET_INSTALL_RELATIVE_RPATH=ON `
      -DCMAKE_INSTALL_PREFIX="$gdalDir/install" `
      -DBUILD_PYTHON_BINDINGS=OFF `
      -DBUILD_JAVA_BINDINGS=ON -DGDAL_JAVA_INSTALL_DIR="$gdalDir/install/lib" -DGDAL_JAVA_JNI_INSTALL_DIR="$gdalDir/install/lib" `
      -DCMAKE_PREFIX_PATH="$prefixPath" `
      ..
cmake --build . --parallel

# Install
cmake --build . --target install

# Copy native libraries
Set-Location $repositoryRootDir
Remove-Item "$repositoryRootDir/qct/native/windows/gdal*.dll" -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Path "$repositoryRootDir/qct/native/windows/" -Force
Copy-Item -Path "$gdalDir/install/bin/*.dll" -Destination "$repositoryRootDir/qct/native/windows/"
Copy-Item -Path "$gdalDir/install/lib/*.dll" -Destination "$repositoryRootDir/qct/native/windows/"
