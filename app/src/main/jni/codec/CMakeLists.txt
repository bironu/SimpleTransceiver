cmake_minimum_required(VERSION 3.4.1)

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -DFIXED_POINT -DEXPORT=\"\"")
include_directories(speex-1.2rc1/include)

add_library(speex_jni
            SHARED
            speex_jni.cpp
            speex-1.2rc1/libspeex/speex.c
            speex-1.2rc1/libspeex/speex_callbacks.c
            speex-1.2rc1/libspeex/bits.c
            speex-1.2rc1/libspeex/modes.c
            speex-1.2rc1/libspeex/nb_celp.c
            speex-1.2rc1/libspeex/exc_20_32_table.c
            speex-1.2rc1/libspeex/exc_5_256_table.c
            speex-1.2rc1/libspeex/exc_5_64_table.c
            speex-1.2rc1/libspeex/exc_8_128_table.c
            speex-1.2rc1/libspeex/exc_10_32_table.c
            speex-1.2rc1/libspeex/exc_10_16_table.c
            speex-1.2rc1/libspeex/filters.c
            speex-1.2rc1/libspeex/quant_lsp.c
            speex-1.2rc1/libspeex/ltp.c
            speex-1.2rc1/libspeex/lpc.c
            speex-1.2rc1/libspeex/lsp.c
            speex-1.2rc1/libspeex/vbr.c
            speex-1.2rc1/libspeex/gain_table.c
            speex-1.2rc1/libspeex/gain_table_lbr.c
            speex-1.2rc1/libspeex/lsp_tables_nb.c
            speex-1.2rc1/libspeex/cb_search.c
            speex-1.2rc1/libspeex/vq.c
            speex-1.2rc1/libspeex/window.c
            speex-1.2rc1/libspeex/high_lsp_tables.c
              )


find_library(log-lib
             log )

target_link_libraries(speex_jni
                      ${log-lib} )