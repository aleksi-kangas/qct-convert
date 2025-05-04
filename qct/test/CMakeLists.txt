cmake_minimum_required(VERSION 3.30 FATAL_ERROR)
project(libqct-test LANGUAGES CXX)

set(CMAKE_CXX_SCAN_FOR_MODULES ON)
set(CMAKE_CXX_STANDARD 23)

enable_testing()

find_package(GTest CONFIG REQUIRED)

add_executable(${PROJECT_NAME} test.cpp
        georef/georef_test.cpp
)
target_link_libraries(${PROJECT_NAME} PRIVATE libqct GTest::gtest GTest::gtest_main GTest::gmock GTest::gmock_main)
target_compile_options(${PROJECT_NAME} PRIVATE
        $<$<CXX_COMPILER_ID:MSVC>:/W3>
        $<$<CXX_COMPILER_ID:Clang>:-Wall -Wno-elaborated-enum-class>
        $<$<CXX_COMPILER_ID:GNU>:-Wall>)
target_compile_features(${PROJECT_NAME} PUBLIC cxx_std_23)

include(GoogleTest)
gtest_discover_tests(${PROJECT_NAME})
