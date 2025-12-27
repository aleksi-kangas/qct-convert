#!/bin/bash
cd ..
export GDAL_VERSION=v3.12.0
git clone --depth 1 --branch "$GDAL_VERSION" https://github.com/OSGeo/gdal.git src
