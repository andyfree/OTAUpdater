LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

# Include res dir from appcompat
appcompat_dir := ../../../frameworks/support/v7/appcompat/res
res_dirs := $(appcompat_dir) res

LOCAL_SRC_FILES := $(call all-subdir-java-files) $(call all-renderscript-files-under, src)

LOCAL_STATIC_JAVA_LIBRARIES := \
	ota-roottools \
	android-support-v4 \
	android-support-v7-appcompat

LOCAL_PACKAGE_NAME := OTAUpdater
LOCAL_CERTIFICATE := shared

LOCAL_RESOURCE_DIR := $(addprefix $(LOCAL_PATH)/, $(res_dirs))
LOCAL_AAPT_FLAGS := --auto-add-overlay

include $(BUILD_PACKAGE)

include $(CLEAR_VARS)

LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := ota-roottools:libs/RootTools.jar

include $(BUILD_MULTI_PREBUILT)
