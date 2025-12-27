@echo off
cd ..
if exist ..\qct\native\ (
    del /q ..\qct\native\*
)
copy install\lib\gdal*.dll ..\qct\native\
