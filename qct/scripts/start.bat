@echo off
SET PATH=%PATH%;../native
SET GDAL_DATA=../share/gdal
set PROJ_DATA=../share/proj
java -cp "../lib/*" -Djava.library.path="../native" com.github.aleksikangas.qct.ui.QctApplication
