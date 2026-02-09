@echo off
SET PATH=%PATH%;../native/windows
SET GDAL_DATA=../share/gdal
set PROJ_DATA=../share/proj
java -cp "../lib/*" -Djava.library.path="../native/windows" com.github.aleksikangas.qct.ui.QctApplication
