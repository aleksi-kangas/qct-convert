@echo off
call conda create -n gdal -c conda-forge -y ^
  ant ^
  blosc ^
  cmake ^
  compilers ^
  ninja ^
  lz4 ^
  proj ^
  swig

echo.
echo Conda GDAL environment install complete. Activate with 'conda activate gdal'.
pause
