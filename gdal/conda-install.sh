#!/bin/bash
conda create -n gdal -c conda-forge -y \
  ant \
  blosc \
  cmake \
  compilers \
  ninja \
  libtool \
  lz4 \
  proj \
  swig \
  unixodbc
read -p -r "Conda GDAL environment install complete. Activate with 'conda activate gdal'. Press any key to continue... " -n1 -s
