LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_SRC_FILES := $(call all-subdir-java-files) $(call all-renderscript-files-under, src)

LOCAL_STATIC_JAVA_LIBRARIES := \
	ota-roottools \
	android-support-v4 \
	android-support-v7-appcompat

LOCAL_PACKAGE_NAME := UltimaOTA
LOCAL_CERTIFICATE := shared

include $(BUILD_PACKAGE)

include $(CLEAR_VARS)

LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := ota-roottools:libs/RootTools.jar

include $(BUILD_MULTI_PREBUILT)
