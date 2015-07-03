#pragma once

#define BUFLENGTH 152064

//STRING8
#include "jni.h"

#include <binder/IMemory.h>
#include <gui/Surface.h>
#include <ui/Camera.h>

namespace primitive
{
	typedef void (*frame_cb)(void* mem, void *cookie);
	typedef void (*error_cb)(int err, void *cookie);

	struct CameraContext{
		frame_cb rec_cb;
		void* rec_cb_cookie;
	};

	class CaptureCamera {
		bool hasCamera;
		sp<Camera> camera;
		sp<Surface> mSurface;
		error_cb err_cb;
		void* err_cb_cookie;
		int rec_flag;
		CamContext* mCamContext;

	public:
		CaptureCamera();
		virtual ~CaptureCamera();
		virtual void setSurface(int* surface);
		virtual bool initCamera();
		virtual void releaseCamera();
		virtual void setRecordingCallback(frame_cb cb, void* cookie, int flag=FLAG_CAMERA);
		virtual void setErrorCallback(error_cb ecb, void* cookie);
		virtual void startPreview();
		virtual void stopPreview();
	};
}
