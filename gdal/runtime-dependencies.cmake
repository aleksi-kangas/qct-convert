if (NOT BINARY_FILE OR NOT SEARCH_DIRECTORIES OR NOT OUTPUT_DIR)
    message(FATAL_ERROR "BINARY_FILE, SEARCH_DIRECTORIES and OUTPUT_DIR must be provided")
endif()

file(MAKE_DIRECTORY ${OUTPUT_DIR})

file(GET_RUNTIME_DEPENDENCIES
    LIBRARIES "${BINARY_FILE}"
    DIRECTORIES "${SEARCH_DIRECTORIES}"
    RESOLVED_DEPENDENCIES_VAR _resolved
    UNRESOLVED_DEPENDENCIES_VAR _unresolved
    PRE_EXCLUDE_REGEXES
        "api-ms-.*" "ext-ms-.*" "kernel32.dll" "user32.dll" "shell32.dll"   # Windows
        "libc\\.so.*" "libpthread\\.so.*" "libgcc_s\\.so.*" "libdl\\.so.*"  # Linux
        "/usr/lib/.*" "/System/Library/.*"                                  # macOS
    POST_EXCLUDE_REGEXES
        ".*system32/.*\\.dll"  # Windows
)

foreach(_file ${_resolved})
    file(COPY ${_file} DESTINATION ${OUTPUT_DIR})
    message(STATUS "Bundled: ${_file}")
endforeach()

if(_unresolved)
    message(WARNING "Could not find these dependencies: ${_unresolved}")
endif()