cmake_minimum_required(VERSION 3.31 FATAL_ERROR)
project(qct-convert LANGUAGES CXX VERSION 0.0.1)

set(CMAKE_CXX_SCAN_FOR_MODULES ON)
set(CMAKE_CXX_STANDARD 23)

enable_testing()

add_subdirectory(qct)
add_subdirectory(qct-export)

add_executable(${PROJECT_NAME} CLI11.hpp main.cpp)
target_compile_features(${PROJECT_NAME} PUBLIC cxx_std_23)
target_compile_options(${PROJECT_NAME} PRIVATE
        $<$<CXX_COMPILER_ID:MSVC>:/W3>
        $<$<CXX_COMPILER_ID:Clang>:-Wall -Wno-elaborated-enum-class>)
target_link_libraries(${PROJECT_NAME} PRIVATE libqct libqct-export)

if (WIN32)
    set(CMAKE_VS_INCLUDE_INSTALL_TO_DEFAULT_BUILD 1)
    add_custom_command(TARGET ${PROJECT_NAME} POST_BUILD
            COMMAND ${CMAKE_COMMAND} -E copy_if_different
            $<TARGET_RUNTIME_DLLS:${PROJECT_NAME}>
            $<TARGET_FILE_DIR:${PROJECT_NAME}>
            COMMAND_EXPAND_LISTS)
endif ()

include(InstallRequiredSystemLibraries)
set(CPACK_GENERATOR "ZIP")
set(CPACK_PACKAGE_NAME "qct-convert")
set(CPACK_PACKAGE_VERSION "${PROJECT_VERSION}")
set(CPACK_PACKAGE_FILE_NAME "${CPACK_PACKAGE_NAME}-${CMAKE_SYSTEM_NAME}-${CMAKE_SYSTEM_PROCESSOR}")
install(TARGETS ${PROJECT_NAME}
        DESTINATION .
        RUNTIME)
install(TARGETS ${PROJECT_NAME}
        DESTINATION .
        RUNTIME_DEPENDENCIES
        PRE_EXCLUDE_REGEXES "api-ms-" "ext-ms-"
        POST_EXCLUDE_REGEXES ".*system32/.*\\.dll")
install(FILES ${CMAKE_SOURCE_DIR}/README.md
        DESTINATION .)
include(CPack)
