//
// Created by sven on 17/5/10.
//


#include <time.h>
#include "art.h"
#include "art_7_0.h"
#include "common.h"

void replace_7_0(JNIEnv *env, jobject src, jobject dest) {
    art::mirror::ArtMethod *smeth =
            (art::mirror::ArtMethod *) env->FromReflectedMethod(src);

    art::mirror::ArtMethod *dmeth =
            (art::mirror::ArtMethod *) env->FromReflectedMethod(dest);

    reinterpret_cast<art::mirror::Class *>(dmeth->declaring_class_)->clinit_thread_id_ =
            reinterpret_cast<art::mirror::Class *>(smeth->declaring_class_)->clinit_thread_id_;
    reinterpret_cast<art::mirror::Class *>(dmeth->declaring_class_)->status_ = art::mirror::Class::Status::kStatusInitializing;
    LOGD("replace_7_0 status_: %d , %d",
         reinterpret_cast<art::mirror::Class *>(dmeth->declaring_class_)->status_,
         reinterpret_cast<art::mirror::Class *>(smeth->declaring_class_)->status_);
    //for reflection invoke
    reinterpret_cast<art::mirror::Class *>(dmeth->declaring_class_)->super_class_ = 0;

    smeth->declaring_class_ = dmeth->declaring_class_;
    smeth->access_flags_ = dmeth->access_flags_ | 0x0001;
    smeth->dex_code_item_offset_ = dmeth->dex_code_item_offset_;
    smeth->dex_method_index_ = dmeth->dex_method_index_;
    smeth->method_index_ = dmeth->method_index_;
    smeth->hotness_count_ = dmeth->hotness_count_;

    smeth->ptr_sized_fields_.dex_cache_resolved_methods_ =
            dmeth->ptr_sized_fields_.dex_cache_resolved_methods_;
    smeth->ptr_sized_fields_.dex_cache_resolved_types_ =
            dmeth->ptr_sized_fields_.dex_cache_resolved_types_;

    smeth->ptr_sized_fields_.entry_point_from_jni_ =
            dmeth->ptr_sized_fields_.entry_point_from_jni_;
    smeth->ptr_sized_fields_.entry_point_from_quick_compiled_code_ =
            dmeth->ptr_sized_fields_.entry_point_from_quick_compiled_code_;

    LOGD("replace_7_0: %d , %d",
         smeth->ptr_sized_fields_.entry_point_from_quick_compiled_code_,
         dmeth->ptr_sized_fields_.entry_point_from_quick_compiled_code_);

}

void setFieldFlag_7_0(JNIEnv *env, jobject field) {
    art::mirror::ArtField *artField =
            (art::mirror::ArtField *) env->FromReflectedField(field);
    artField->access_flags_ = artField->access_flags_ & (~0x0002) | 0x0001;
    LOGD("setFieldFlag_7_0: %d ", artField->access_flags_);
}
