#!/bin/bash
conda create -y -n gdal -c conda-forge \
  cmake \
  ninja \
  compilers \
  ant \
  proj \
  swig