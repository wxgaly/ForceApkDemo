//
// Created by 汪旭光 on 2019-11-12.
//

#include <vector>
#include <istream>
#include <jni.h>
#include <string>
#include "android_log.h"

struct JavaVMExt {
    void *functions;
    void *runtime;
};

/**
 * instruction set
 */
enum class InstructionSet {
    kNone,
    kArm,
    kArm64,
    kThumb2,
    kX86,
    kX86_64,
    kMips,
    kMips64,
    kLast,
};

// Returns a special method that describes all callee saves being spilled to the stack.
enum CalleeSaveType {
    kSaveAll,
    kRefsOnly,
    kRefsAndArgs,
    kLastCalleeSaveType  // Value used for iteration
};

struct QuickMethodFrameInfo {
    uint32_t frame_size_in_bytes_;
    uint32_t core_spill_mask_;
    uint32_t fp_spill_mask_;
};

/**
 * 5.1，GcRoot中成员变量是指针类型，所以用void*代替GcRoot
 */
struct PartialRuntime51 {

    void *callee_save_methods_[kLastCalleeSaveType];  //5.0 5.1 void *
    void *pre_allocated_OutOfMemoryError_;
    void *pre_allocated_NoClassDefFoundError_;
    void *resolution_method_;
    void *imt_conflict_method_;
    // Unresolved method has the same behavior as the conflict method, it is used by the class linker
    // for differentiating between unfilled imt slots vs conflict slots in superclasses.
    void *imt_unimplemented_method_;
    void *default_imt_;  //5.0 5.1

    InstructionSet instruction_set_;
    QuickMethodFrameInfo callee_save_method_frame_infos_[kLastCalleeSaveType]; // QuickMethodFrameInfo = uint32_t * 3

    void *compiler_callbacks_;
    bool is_zygote_;
    bool must_relocate_;
    bool is_concurrent_gc_enabled_;
    bool is_explicit_gc_disabled_;
    bool dex2oat_enabled_;
    bool image_dex2oat_enabled_;

    std::string compiler_executable_;
    std::string patchoat_executable_;
    std::vector<std::string> compiler_options_;
    std::vector<std::string> image_compiler_options_;
    std::string image_location_;

    std::string boot_class_path_string_;
    std::string class_path_string_;
    std::vector<std::string> properties_;
};



extern "C"
JNIEXPORT jstring JNICALL
Java_wxgaly_android_proxy_1lib_NativeUtil_getString(JNIEnv *env, jclass clazz) {
    std::string hello = "Hello from C++";
    LOGD("hello is %s", hello.c_str());

    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_wxgaly_android_proxy_1lib_NativeUtil_disableDex2oat(JNIEnv *env, jclass clazz) {
    JavaVM *javaVM;
    env->GetJavaVM(&javaVM);
    JavaVMExt *javaVMExt = (JavaVMExt *) javaVM;
    void *runtime = javaVMExt->runtime;

    PartialRuntime51 *partialRuntime = (PartialRuntime51 *) runtime;
    partialRuntime->image_dex2oat_enabled_ = false;

//    LOGD("partialRuntime->instruction_set_ is %d", partialRuntime->instruction_set_);
    if (partialRuntime->instruction_set_ <= InstructionSet::kNone ||
        partialRuntime->instruction_set_ >= InstructionSet::kLast) {
        return static_cast<jboolean>(true);
    }

    return static_cast<jboolean>(partialRuntime->image_dex2oat_enabled_);
}