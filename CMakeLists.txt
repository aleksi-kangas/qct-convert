cmake_minimum_required(VERSION 3.30 FATAL_ERROR)
project(qct-convert LANGUAGES CXX VERSION 0.0.1)

set(CMAKE_CXX_SCAN_FOR_MODULES ON)
set(CMAKE_CXX_STANDARD 23)

add_subdirectory(qct)

add_executable(${PROJECT_NAME} main.cpp)
target_compile_features(${PROJECT_NAME} PUBLIC cxx_std_23)
target_compile_options(${PROJECT_NAME} PRIVATE
        $<$<CXX_COMPILER_ID:MSVC>:/W3>
        $<$<CXX_COMPILER_ID:Clang>:-Wall -Wno-elaborated-enum-class>
        $<$<CXX_COMPILER_ID:GNU>:-Wall>)
target_link_libraries(${PROJECT_NAME} PUBLIC libqct)
