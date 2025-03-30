cmake_minimum_required(VERSION 3.30 FATAL_ERROR)
project(libqct-test LANGUAGES CXX)

set(CMAKE_CXX_SCAN_FOR_MODULES ON)
set(CMAKE_CXX_STANDARD 23)

include(FetchContent)
set(FETCHCONTENT_QUIET)
FetchContent_Declare(googletest
        GIT_REPOSITORY https://github.com/google/googletest
        GIT_TAG main)
FetchContent_MakeAvailable(googletest)
include(GoogleTest)

add_executable(${PROJECT_NAME} test.cpp
    geo/poly_test.cpp
)
target_link_libraries(${PROJECT_NAME} PRIVATE libqct gtest gmock gtest_main)
target_compile_options(${PROJECT_NAME} PRIVATE
        $<$<CXX_COMPILER_ID:MSVC>:/W3>
        $<$<CXX_COMPILER_ID:Clang>:-Wall -Wno-elaborated-enum-class>
        $<$<CXX_COMPILER_ID:GNU>:-Wall>)
target_compile_features(${PROJECT_NAME} PUBLIC cxx_std_23)

gtest_discover_tests(${PROJECT_NAME})
