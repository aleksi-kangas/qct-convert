#!/bin/bash
conda create -y -n gdal -c conda-forge \
  cmake \
  ninja \
  compilers \
  ant \
  blosc \
  libtool \
  lz4 \
  proj \
  swig \
  unixodbc
