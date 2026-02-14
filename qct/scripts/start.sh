#!/bin/bash
export GDAL_DATA=../share/gdal
export PROJ_DATA=../share/proj
java -cp "../lib/*" -Djava.library.path="../native" com.github.aleksikangas.qct.ui.QctApplication
