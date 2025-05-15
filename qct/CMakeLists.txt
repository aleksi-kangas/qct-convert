cmake_minimum_required(VERSION 3.30 FATAL_ERROR)
project(libqct LANGUAGES CXX VERSION 0.0.1)

set(CMAKE_CXX_SCAN_FOR_MODULES ON)
set(CMAKE_CXX_STANDARD 23)

add_library(${PROJECT_NAME})
target_sources(${PROJECT_NAME}
        PUBLIC
        FILE_SET cxx_modules
        TYPE CXX_MODULES
        FILES
        src/qct.ixx
        src/qct_file.ixx

        # common
        src/common/crtp.ixx
        src/common/exception.ixx

        # image
        src/image/decode/decode.ixx
        src/image/decode/decoder.ixx
        src/image/decode/huffman.ixx
        src/image/decode/palette.ixx
        src/image/decode/pp.ixx
        src/image/decode/rle.ixx
        src/image/index.ixx
        src/image/tile.ixx

        # georef
        src/georef/coefficients.ixx
        src/georef/coordinates.ixx
        src/georef/georef.ixx

        # meta
        src/meta/datum_shift.ixx
        src/meta/extended_data.ixx
        src/meta/magic_number.ixx
        src/meta/metadata.ixx
        src/meta/map_outline.ixx
        src/meta/file_format_version.ixx

        # palette
        src/palette/color.ixx
        src/palette/palette.ixx

        # util
        src/util/buffer.ixx
        src/util/reader.ixx
)
target_compile_features(${PROJECT_NAME} PUBLIC cxx_std_23)
target_compile_options(${PROJECT_NAME} PRIVATE
        $<$<CXX_COMPILER_ID:MSVC>:/W3>
        $<$<CXX_COMPILER_ID:Clang>:-Wall -Wno-elaborated-enum-class>)

enable_testing()
add_subdirectory(test)

