cmake_minimum_required(VERSION 3.30 FATAL_ERROR)
project(libqct-export LANGUAGES CXX VERSION 0.0.1)

set(CMAKE_CXX_SCAN_FOR_MODULES ON)
set(CMAKE_CXX_STANDARD 23)

find_package(GDAL CONFIG REQUIRED)
find_package(PROJ CONFIG REQUIRED)
# This trick is required to populate the MY_PROJ_DIR with the correct path to proj.db
find_path(PROJ_DATA_DIR
        NAMES proj.db
        PATHS "${PROJ_DIR}"
        NO_DEFAULT_PATH
        REQUIRED
)
add_compile_definitions(MY_PROJ_DIR="${PROJ_DATA_DIR}")

add_library(${PROJECT_NAME}
        src/fpng.cpp
)
target_sources(${PROJECT_NAME}
        PUBLIC
        FILE_SET cxx_modules
        TYPE CXX_MODULES
        FILES
        src/qctexport.ixx

        src/exception.ixx
        src/exporter.ixx
        src/geotiff.ixx
        src/kml.ixx
        src/png.ixx
)
target_compile_features(${PROJECT_NAME} PUBLIC cxx_std_23)
target_compile_options(${PROJECT_NAME} PRIVATE
        $<$<CXX_COMPILER_ID:MSVC>:/W3>
        $<$<CXX_COMPILER_ID:Clang>:-Wall -Wno-elaborated-enum-class>)
target_include_directories(${PROJECT_NAME} PRIVATE include)
target_link_libraries(${PROJECT_NAME} PRIVATE libqct GDAL::GDAL PROJ::proj)

