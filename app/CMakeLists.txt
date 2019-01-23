
cmake_minimum_required(VERSION 3.6.0)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds it for you.
# Gradle automatically packages shared libraries with your APK.

set(NATIVE_SOURCES_PATH "src/main/cpp")
set(OPUS_DIR_PATH "src/main/cpp/opus")

set(OPUS_SOURCES
        "${OPUS_DIR_PATH}/src/opus.c"
        "${OPUS_DIR_PATH}/src/opus_decoder.c"
        "${OPUS_DIR_PATH}/src/opus_encoder.c"
        "${OPUS_DIR_PATH}/src/opus_multistream.c"
        "${OPUS_DIR_PATH}/src/opus_multistream_encoder.c"
        "${OPUS_DIR_PATH}/src/opus_multistream_decoder.c"
        "${OPUS_DIR_PATH}/src/repacketizer.c"
        "${OPUS_DIR_PATH}/src/opus_projection_encoder.c"
        "${OPUS_DIR_PATH}/src/opus_projection_decoder.c"
        "${OPUS_DIR_PATH}/src/mapping_matrix.c")

set(OPUS_SOURCES_FLOAT
        "${OPUS_DIR_PATH}/src/analysis.c"
        "${OPUS_DIR_PATH}/src/mlp.c"
        "${OPUS_DIR_PATH}/src/mlp_data.c")

set(CELT_SOURCES
        "${OPUS_DIR_PATH}/celt/bands.c"
        "${OPUS_DIR_PATH}/celt/celt.c"
        "${OPUS_DIR_PATH}/celt/celt_encoder.c"
        "${OPUS_DIR_PATH}/celt/celt_decoder.c"
        "${OPUS_DIR_PATH}/celt/cwrs.c"
        "${OPUS_DIR_PATH}/celt/entcode.c"
        "${OPUS_DIR_PATH}/celt/entdec.c"
        "${OPUS_DIR_PATH}/celt/entenc.c"
        "${OPUS_DIR_PATH}/celt/kiss_fft.c"
        "${OPUS_DIR_PATH}/celt/laplace.c"
        "${OPUS_DIR_PATH}/celt/mathops.c"
        "${OPUS_DIR_PATH}/celt/mdct.c"
        "${OPUS_DIR_PATH}/celt/modes.c"
        "${OPUS_DIR_PATH}/celt/pitch.c"
        "${OPUS_DIR_PATH}/celt/celt_lpc.c"
        "${OPUS_DIR_PATH}/celt/quant_bands.c"
        "${OPUS_DIR_PATH}/celt/rate.c"
        "${OPUS_DIR_PATH}/celt/vq.c")

set(CELT_SOURCES_SSE
        "${OPUS_DIR_PATH}/celt/x86/x86cpu.c"
        "${OPUS_DIR_PATH}/celt/x86/x86_celt_map.c"
        "${OPUS_DIR_PATH}/celt/x86/pitch_sse.c")

set(CELT_SOURCES_SSE2
        "${OPUS_DIR_PATH}/celt/x86/pitch_sse2.c"
        "${OPUS_DIR_PATH}/celt/x86/vq_sse2.c")

set(CELT_SOURCES_SSE4_1
        "${OPUS_DIR_PATH}/celt/x86/celt_lpc_sse4_1.c"
        "${OPUS_DIR_PATH}/celt/x86/pitch_sse4_1.c")

set(CELT_SOURCES_ARM
        "${OPUS_DIR_PATH}/celt/arm/armcpu.c"
        "${OPUS_DIR_PATH}/celt/arm/arm_celt_map.c")

set(CELT_SOURCES_ARM_ASM
        "${OPUS_DIR_PATH}/celt/arm/celt_pitch_xcorr_arm.s")

set(CELT_AM_SOURCES_ARM_ASM
        "${OPUS_DIR_PATH}/celt/arm/armopts.s.in")

set(CELT_SOURCES_ARM_NEON_INTR
        "${OPUS_DIR_PATH}/celt/arm/celt_neon_intr.c"
        "${OPUS_DIR_PATH}/celt/arm/pitch_neon_intr.c")

set(CELT_SOURCES_ARM_NE10
        "${OPUS_DIR_PATH}/celt/arm/celt_fft_ne10.c"
        "${OPUS_DIR_PATH}/celt/arm/celt_mdct_ne10.c")

set(SILK_SOURCES
        "${OPUS_DIR_PATH}/silk/CNG.c"
        "${OPUS_DIR_PATH}/silk/code_signs.c"
        "${OPUS_DIR_PATH}/silk/init_decoder.c"
        "${OPUS_DIR_PATH}/silk/decode_core.c"
        "${OPUS_DIR_PATH}/silk/decode_frame.c"
        "${OPUS_DIR_PATH}/silk/decode_parameters.c"
        "${OPUS_DIR_PATH}/silk/decode_indices.c"
        "${OPUS_DIR_PATH}/silk/decode_pulses.c"
        "${OPUS_DIR_PATH}/silk/decoder_set_fs.c"
        "${OPUS_DIR_PATH}/silk/dec_API.c"
        "${OPUS_DIR_PATH}/silk/enc_API.c"
        "${OPUS_DIR_PATH}/silk/encode_indices.c"
        "${OPUS_DIR_PATH}/silk/encode_pulses.c"
        "${OPUS_DIR_PATH}/silk/gain_quant.c"
        "${OPUS_DIR_PATH}/silk/interpolate.c"
        "${OPUS_DIR_PATH}/silk/LP_variable_cutoff.c"
        "${OPUS_DIR_PATH}/silk/NLSF_decode.c"
        "${OPUS_DIR_PATH}/silk/NSQ.c"
        "${OPUS_DIR_PATH}/silk/NSQ_del_dec.c"
        "${OPUS_DIR_PATH}/silk/PLC.c"
        "${OPUS_DIR_PATH}/silk/shell_coder.c"
        "${OPUS_DIR_PATH}/silk/tables_gain.c"
        "${OPUS_DIR_PATH}/silk/tables_LTP.c"
        "${OPUS_DIR_PATH}/silk/tables_NLSF_CB_NB_MB.c"
        "${OPUS_DIR_PATH}/silk/tables_NLSF_CB_WB.c"
        "${OPUS_DIR_PATH}/silk/tables_other.c"
        "${OPUS_DIR_PATH}/silk/tables_pitch_lag.c"
        "${OPUS_DIR_PATH}/silk/tables_pulses_per_block.c"
        "${OPUS_DIR_PATH}/silk/VAD.c"
        "${OPUS_DIR_PATH}/silk/control_audio_bandwidth.c"
        "${OPUS_DIR_PATH}/silk/quant_LTP_gains.c"
        "${OPUS_DIR_PATH}/silk/VQ_WMat_EC.c"
        "${OPUS_DIR_PATH}/silk/HP_variable_cutoff.c"
        "${OPUS_DIR_PATH}/silk/NLSF_encode.c"
        "${OPUS_DIR_PATH}/silk/NLSF_VQ.c"
        "${OPUS_DIR_PATH}/silk/NLSF_unpack.c"
        "${OPUS_DIR_PATH}/silk/NLSF_del_dec_quant.c"
        "${OPUS_DIR_PATH}/silk/process_NLSFs.c"
        "${OPUS_DIR_PATH}/silk/stereo_LR_to_MS.c"
        "${OPUS_DIR_PATH}/silk/stereo_MS_to_LR.c"
        "${OPUS_DIR_PATH}/silk/stereo_quant_pred.c"
        "${OPUS_DIR_PATH}/silk/check_control_input.c"
        "${OPUS_DIR_PATH}/silk/control_SNR.c"
        "${OPUS_DIR_PATH}/silk/init_encoder.c"
        "${OPUS_DIR_PATH}/silk/control_codec.c"
        "${OPUS_DIR_PATH}/silk/A2NLSF.c"
        "${OPUS_DIR_PATH}/silk/ana_filt_bank_1.c"
        "${OPUS_DIR_PATH}/silk/biquad_alt.c"
        "${OPUS_DIR_PATH}/silk/bwexpander_32.c"
        "${OPUS_DIR_PATH}/silk/bwexpander.c"
        "${OPUS_DIR_PATH}/silk/debug.c"
        "${OPUS_DIR_PATH}/silk/decode_pitch.c"
        "${OPUS_DIR_PATH}/silk/inner_prod_aligned.c"
        "${OPUS_DIR_PATH}/silk/lin2log.c"
        "${OPUS_DIR_PATH}/silk/log2lin.c"
        "${OPUS_DIR_PATH}/silk/LPC_analysis_filter.c"
        "${OPUS_DIR_PATH}/silk/LPC_inv_pred_gain.c"
        "${OPUS_DIR_PATH}/silk/table_LSF_cos.c"
        "${OPUS_DIR_PATH}/silk/NLSF2A.c"
        "${OPUS_DIR_PATH}/silk/NLSF_stabilize.c"
        "${OPUS_DIR_PATH}/silk/NLSF_VQ_weights_laroia.c"
        "${OPUS_DIR_PATH}/silk/pitch_est_tables.c"
        "${OPUS_DIR_PATH}/silk/resampler.c"
        "${OPUS_DIR_PATH}/silk/resampler_down2_3.c"
        "${OPUS_DIR_PATH}/silk/resampler_down2.c"
        "${OPUS_DIR_PATH}/silk/resampler_private_AR2.c"
        "${OPUS_DIR_PATH}/silk/resampler_private_down_FIR.c"
        "${OPUS_DIR_PATH}/silk/resampler_private_IIR_FIR.c"
        "${OPUS_DIR_PATH}/silk/resampler_private_up2_HQ.c"
        "${OPUS_DIR_PATH}/silk/resampler_rom.c"
        "${OPUS_DIR_PATH}/silk/sigm_Q15.c"
        "${OPUS_DIR_PATH}/silk/sort.c"
        "${OPUS_DIR_PATH}/silk/sum_sqr_shift.c"
        "${OPUS_DIR_PATH}/silk/stereo_decode_pred.c"
        "${OPUS_DIR_PATH}/silk/stereo_encode_pred.c"
        "${OPUS_DIR_PATH}/silk/stereo_find_predictor.c"
        "${OPUS_DIR_PATH}/silk/LPC_fit.c"
        "${OPUS_DIR_PATH}/silk/stereo_encode_pred.c")

set(SILK_SOURCES_SSE4_1
        "${OPUS_DIR_PATH}/silk/x86/NSQ_sse4_1.c"
        "${OPUS_DIR_PATH}/silk/x86/NSQ_del_dec_sse4_1.c"
        "${OPUS_DIR_PATH}/silk/x86/x86_silk_map.c"
        "${OPUS_DIR_PATH}/silk/x86/VAD_sse4_1.c"
        "${OPUS_DIR_PATH}/silk/x86/VQ_WMat_EC_sse4_1.c")

set(SILK_SOURCES_ARM_NEON_INTR
        "${OPUS_DIR_PATH}/silk/arm/arm_silk_map.c"
        "${OPUS_DIR_PATH}/silk/arm/biquad_alt_neon_intr.c"
        "${OPUS_DIR_PATH}/silk/arm/LPC_inv_pred_gain_neon_intr.c"
        "${OPUS_DIR_PATH}/silk/arm/NSQ_del_dec_neon_intr.c"
        "${OPUS_DIR_PATH}/silk/arm/NSQ_neon.c")

set(SILK_SOURCES_FIXED
        "${OPUS_DIR_PATH}/silk/fixed/LTP_analysis_filter_FIX.c"
        "${OPUS_DIR_PATH}/silk/fixed/LTP_scale_ctrl_FIX.c"
        "${OPUS_DIR_PATH}/silk/fixed/corrMatrix_FIX.c"
        "${OPUS_DIR_PATH}/silk/fixed/encode_frame_FIX.c"
        "${OPUS_DIR_PATH}/silk/fixed/find_LPC_FIX.c"
        "${OPUS_DIR_PATH}/silk/fixed/find_LTP_FIX.c"
        "${OPUS_DIR_PATH}/silk/fixed/find_pitch_lags_FIX.c"
        "${OPUS_DIR_PATH}/silk/fixed/find_pred_coefs_FIX.c"
        "${OPUS_DIR_PATH}/silk/fixed/noise_shape_analysis_FIX.c"
        "${OPUS_DIR_PATH}/silk/fixed/process_gains_FIX.c"
        "${OPUS_DIR_PATH}/silk/fixed/regularize_correlations_FIX.c"
        "${OPUS_DIR_PATH}/silk/fixed/residual_energy16_FIX.c"
        "${OPUS_DIR_PATH}/silk/fixed/residual_energy_FIX.c"
        "${OPUS_DIR_PATH}/silk/fixed/warped_autocorrelation_FIX.c"
        "${OPUS_DIR_PATH}/silk/fixed/apply_sine_window_FIX.c"
        "${OPUS_DIR_PATH}/silk/fixed/autocorr_FIX.c"
        "${OPUS_DIR_PATH}/silk/fixed/burg_modified_FIX.c"
        "${OPUS_DIR_PATH}/silk/fixed/k2a_FIX.c"
        "${OPUS_DIR_PATH}/silk/fixed/k2a_Q16_FIX.c"
        "${OPUS_DIR_PATH}/silk/fixed/pitch_analysis_core_FIX.c"
        "${OPUS_DIR_PATH}/silk/fixed/vector_ops_FIX.c"
        "${OPUS_DIR_PATH}/silk/fixed/schur64_FIX.c"
        "${OPUS_DIR_PATH}/silk/fixed/schur_FIX.c")

set(SILK_SOURCES_FIXED_SSE4_1
        "${OPUS_DIR_PATH}/silk/fixed/x86/vector_ops_FIX_sse4_1.c"
        "${OPUS_DIR_PATH}/silk/fixed/x86/burg_modified_FIX_sse4_1.c")

set(SILK_SOURCES_FIXED_ARM_NEON_INTR
        "${OPUS_DIR_PATH}/silk/fixed/arm/warped_autocorrelation_FIX_neon_intr.c")

set(SILK_SOURCES_FLOAT
        "${OPUS_DIR_PATH}/silk/float/apply_sine_window_FLP.c"
        "${OPUS_DIR_PATH}/silk/float/corrMatrix_FLP.c"
        "${OPUS_DIR_PATH}/silk/float/encode_frame_FLP.c"
        "${OPUS_DIR_PATH}/silk/float/find_LPC_FLP.c"
        "${OPUS_DIR_PATH}/silk/float/find_LTP_FLP.c"
        "${OPUS_DIR_PATH}/silk/float/find_pitch_lags_FLP.c"
        "${OPUS_DIR_PATH}/silk/float/find_pred_coefs_FLP.c"
        "${OPUS_DIR_PATH}/silk/float/LPC_analysis_filter_FLP.c"
        "${OPUS_DIR_PATH}/silk/float/LTP_analysis_filter_FLP.c"
        "${OPUS_DIR_PATH}/silk/float/LTP_scale_ctrl_FLP.c"
        "${OPUS_DIR_PATH}/silk/float/noise_shape_analysis_FLP.c"
        "${OPUS_DIR_PATH}/silk/float/process_gains_FLP.c"
        "${OPUS_DIR_PATH}/silk/float/regularize_correlations_FLP.c"
        "${OPUS_DIR_PATH}/silk/float/residual_energy_FLP.c"
        "${OPUS_DIR_PATH}/silk/float/warped_autocorrelation_FLP.c"
        "${OPUS_DIR_PATH}/silk/float/wrappers_FLP.c"
        "${OPUS_DIR_PATH}/silk/float/autocorrelation_FLP.c"
        "${OPUS_DIR_PATH}/silk/float/burg_modified_FLP.c"
        "${OPUS_DIR_PATH}/silk/float/bwexpander_FLP.c"
        "${OPUS_DIR_PATH}/silk/float/energy_FLP.c"
        "${OPUS_DIR_PATH}/silk/float/inner_product_FLP.c"
        "${OPUS_DIR_PATH}/silk/float/k2a_FLP.c"
        "${OPUS_DIR_PATH}/silk/float/LPC_inv_pred_gain_FLP.c"
        "${OPUS_DIR_PATH}/silk/float/pitch_analysis_core_FLP.c"
        "${OPUS_DIR_PATH}/silk/float/scale_copy_vector_FLP.c"
        "${OPUS_DIR_PATH}/silk/float/scale_vector_FLP.c"
        "${OPUS_DIR_PATH}/silk/float/schur_FLP.c"
        "${OPUS_DIR_PATH}/silk/float/sort_FLP.c")

set(OPUS_HEADEARS
        ${OPUS_DIR_PATH}/include/opus.h
        ${OPUS_DIR_PATH}/include/opus_multistream.h
        ${OPUS_DIR_PATH}/include/opus_projection.h
        ${OPUS_DIR_PATH}/src/opus_private.h
        ${OPUS_DIR_PATH}/src/analysis.h
        ${OPUS_DIR_PATH}/src/mapping_matrix.h
        ${OPUS_DIR_PATH}/src/mlp.h
        ${OPUS_DIR_PATH}/src/tansig_table.h
        ${OPUS_DIR_PATH}/celt/arch.h
        ${OPUS_DIR_PATH}/celt/bands.h
        ${OPUS_DIR_PATH}/celt/celt.h
        ${OPUS_DIR_PATH}/celt/cpu_support.h
        ${OPUS_DIR_PATH}/include/opus_types.h
        ${OPUS_DIR_PATH}/include/opus_defines.h
        ${OPUS_DIR_PATH}/include/opus_custom.h
        ${OPUS_DIR_PATH}/celt/cwrs.h
        ${OPUS_DIR_PATH}/celt/ecintrin.h
        ${OPUS_DIR_PATH}/celt/entcode.h
        ${OPUS_DIR_PATH}/celt/entdec.h
        ${OPUS_DIR_PATH}/celt/entenc.h
        ${OPUS_DIR_PATH}/celt/fixed_debug.h
        ${OPUS_DIR_PATH}/celt/fixed_generic.h
        ${OPUS_DIR_PATH}/celt/float_cast.h
        ${OPUS_DIR_PATH}/celt/_kiss_fft_guts.h
        ${OPUS_DIR_PATH}/celt/kiss_fft.h
        ${OPUS_DIR_PATH}/celt/laplace.h
        ${OPUS_DIR_PATH}/celt/mathops.h
        ${OPUS_DIR_PATH}/celt/mdct.h
        ${OPUS_DIR_PATH}/celt/mfrngcod.h
        ${OPUS_DIR_PATH}/celt/modes.h
        ${OPUS_DIR_PATH}/celt/os_support.h
        ${OPUS_DIR_PATH}/celt/pitch.h
        ${OPUS_DIR_PATH}/celt/celt_lpc.h
        ${OPUS_DIR_PATH}/celt/x86/celt_lpc_sse.h
        ${OPUS_DIR_PATH}/celt/quant_bands.h
        ${OPUS_DIR_PATH}/celt/rate.h
        ${OPUS_DIR_PATH}/celt/stack_alloc.h
        ${OPUS_DIR_PATH}/celt/vq.h
        ${OPUS_DIR_PATH}/celt/static_modes_float.h
        ${OPUS_DIR_PATH}/celt/static_modes_fixed.h
        ${OPUS_DIR_PATH}/celt/static_modes_float_arm_ne10.h
        ${OPUS_DIR_PATH}/celt/static_modes_fixed_arm_ne10.h
        ${OPUS_DIR_PATH}/celt/arm/armcpu.h
        ${OPUS_DIR_PATH}/celt/arm/fixed_armv4.h
        ${OPUS_DIR_PATH}/celt/arm/fixed_armv5e.h
        ${OPUS_DIR_PATH}/celt/arm/fixed_arm64.h
        ${OPUS_DIR_PATH}/celt/arm/kiss_fft_armv4.h
        ${OPUS_DIR_PATH}/celt/arm/kiss_fft_armv5e.h
        ${OPUS_DIR_PATH}/celt/arm/pitch_arm.h
        ${OPUS_DIR_PATH}/celt/arm/fft_arm.h
        ${OPUS_DIR_PATH}/celt/arm/mdct_arm.h
        ${OPUS_DIR_PATH}/celt/mips/fixed_generic_mipsr1.h
        ${OPUS_DIR_PATH}/celt/mips/kiss_fft_mipsr1.h
        ${OPUS_DIR_PATH}/celt/mips/mdct_mipsr1.h
        ${OPUS_DIR_PATH}/celt/mips/pitch_mipsr1.h
        ${OPUS_DIR_PATH}/celt/mips/vq_mipsr1.h
        ${OPUS_DIR_PATH}/celt/x86/pitch_sse.h
        ${OPUS_DIR_PATH}/celt/x86/vq_sse.h
        ${OPUS_DIR_PATH}/celt/x86/x86cpu.h
        ${OPUS_DIR_PATH}/silk/debug.h
        ${OPUS_DIR_PATH}/silk/control.h
        ${OPUS_DIR_PATH}/silk/errors.h
        ${OPUS_DIR_PATH}/silk/API.h
        ${OPUS_DIR_PATH}/silk/typedef.h
        ${OPUS_DIR_PATH}/silk/define.h
        ${OPUS_DIR_PATH}/silk/main.h
        ${OPUS_DIR_PATH}/silk/x86/main_sse.h
        ${OPUS_DIR_PATH}/silk/PLC.h
        ${OPUS_DIR_PATH}/silk/structs.h
        ${OPUS_DIR_PATH}/silk/tables.h
        ${OPUS_DIR_PATH}/silk/tuning_parameters.h
        ${OPUS_DIR_PATH}/silk/Inlines.h
        ${OPUS_DIR_PATH}/silk/MacroCount.h
        ${OPUS_DIR_PATH}/silk/MacroDebug.h
        ${OPUS_DIR_PATH}/silk/macros.h
        ${OPUS_DIR_PATH}/silk/NSQ.h
        ${OPUS_DIR_PATH}/silk/pitch_est_defines.h
        ${OPUS_DIR_PATH}/silk/resampler_private.h
        ${OPUS_DIR_PATH}/silk/resampler_rom.h
        ${OPUS_DIR_PATH}/silk/resampler_structs.h
        ${OPUS_DIR_PATH}/silk/SigProc_FIX.h
        ${OPUS_DIR_PATH}/silk/x86/SigProc_FIX_sse.h
        ${OPUS_DIR_PATH}/silk/arm/biquad_alt_arm.h
        ${OPUS_DIR_PATH}/silk/arm/LPC_inv_pred_gain_arm.h
        ${OPUS_DIR_PATH}/silk/arm/macros_armv4.h
        ${OPUS_DIR_PATH}/silk/arm/macros_armv5e.h
        ${OPUS_DIR_PATH}/silk/arm/macros_arm64.h
        ${OPUS_DIR_PATH}/silk/arm/SigProc_FIX_armv4.h
        ${OPUS_DIR_PATH}/silk/arm/SigProc_FIX_armv5e.h
        ${OPUS_DIR_PATH}/silk/arm/NSQ_del_dec_arm.h
        ${OPUS_DIR_PATH}/silk/arm/NSQ_neon.h
        ${OPUS_DIR_PATH}/silk/fixed/main_FIX.h
        ${OPUS_DIR_PATH}/silk/fixed/structs_FIX.h
        ${OPUS_DIR_PATH}/silk/fixed/arm/warped_autocorrelation_FIX_arm.h
        ${OPUS_DIR_PATH}/silk/fixed/mips/noise_shape_analysis_FIX_mipsr1.h
        ${OPUS_DIR_PATH}/silk/fixed/mips/warped_autocorrelation_FIX_mipsr1.h
        ${OPUS_DIR_PATH}/silk/float/main_FLP.h
        ${OPUS_DIR_PATH}/silk/float/structs_FLP.h
        ${OPUS_DIR_PATH}/silk/float/SigProc_FLP.h
        ${OPUS_DIR_PATH}/silk/mips/macros_mipsr1.h
        ${OPUS_DIR_PATH}/silk/mips/NSQ_del_dec_mipsr1.h
        ${OPUS_DIR_PATH}/silk/mips/sigproc_fix_mipsr1.h)

set(OPUS_HEADERS_DIRS
        ${OPUS_DIR_PATH}
        ${OPUS_DIR_PATH}/include
        ${OPUS_DIR_PATH}/src
        ${OPUS_DIR_PATH}/silk
        ${OPUS_DIR_PATH}/silk/x86
        ${OPUS_DIR_PATH}/silk/arm
        ${OPUS_DIR_PATH}/silk/fixed
        ${OPUS_DIR_PATH}/silk/fixed/arm
        ${OPUS_DIR_PATH}/silk/fixed/mips
        ${OPUS_DIR_PATH}/silk/float
        ${OPUS_DIR_PATH}/silk/mips
        ${OPUS_DIR_PATH}/celt
        ${OPUS_DIR_PATH}/celt/x86
        ${OPUS_DIR_PATH}/celt/arm
        ${OPUS_DIR_PATH}/celt/mips)

set(FIXED_POINT "1")

# Set required C flags for opus
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -DUSE_ALLOCA -DOPUS_BUILD")

# Add certain sources and C flags if we want to use fixed point or float
if (${FIXED_POINT})
    set(SILK_SOURCES ${SILK_SOURCES} ${SILK_SOURCES_FIXED})
    set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -DFIXED_POINT=1 -DDISABLE_FLOAT_API")
else ()
    set(SILK_SOURCES ${SILK_SOURCES} ${SILK_SOURCES_FLOAT})
    set(OPUS_SOURCES ${OPUS_SOURCES} ${OPUS_SOURCES_FLOAT})
endif ()



add_library( # Sets the name of the library.
        opus

        # Sets the library as a static library.
        STATIC

        # Provides a relative path to your source file(s).
        # Associated headers in the same location as their source
        # file are automatically included.
        # We include relative path to header files, because some of them
        # not in the same directory as sources.
        ${OPUS_SOURCES} ${CELT_SOURCES} ${SILK_SOURCES} ${OPUS_HEADEARS})

add_library( # Sets the name of the library.
        jniopus

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        ${NATIVE_SOURCES_PATH}/jniopus.c)

# Here we specify directories where compiler need to seek our headers
include_directories(${OPUS_HEADERS_DIRS})

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in the
# build script, prebuilt third-party libraries, or system libraries.

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log )

target_link_libraries(
        # Specifies the target library.
        jniopus

        # Specifies the libraries that should be linked to our target
        opus ${log-lib})


