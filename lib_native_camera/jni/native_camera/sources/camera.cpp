#define DEBUG_LOG 0

#include "camera.hpp"

#include "pmv_android_logger.h"

namespace primitive
{
	volatile bool isDeleting = false;

	void main_rec_callback(const sp<IMemory>& mem, void *cookie)
	{
		pmv_start_log();
		CamContext* context = reinterpret_cast<CamContext*>(cookie);
		if (context == NULL) 
		{
			LOGE_if (DEBUG_LOG,"context is NULL in main_rec_callback");
			return;
		}
		size_t offset;
		size_t size;
		sp<IMemoryHeap> heap = mem->getMemory(&offset, &size);
		unsigned char* inBuf = ((unsigned char*)heap->base()) + offset;

		if (!isDeleting)
		{
			context->rec_cb(inBuf, context->rec_cb_cookie);
		}
	}

	CaptureCamera::CaptureCamera() 
	{
		pmv_start_log();
		hasCamera = false;
		err_cb = NULL;
		err_cb_cookie = NULL;
		mCamContext = new CamContext();
		mCamContext->rec_cb = NULL;
		mCamContext->rec_cb_cookie = NULL;
		rec_flag = FRAME_CALLBACK_FLAG_NOOP;
	}

	CaptureCamera::~CaptureCamera() 
	{
		pmv_start_log();
		releaseCamera();
		if (mCamContext)
		{
			delete mCamContext;
		}
	}

	void CaptureCamera::setSurface(int* surface)
	{
		pmv_start_log();
		mSurface = reinterpret_cast<Surface*> (surface);
	}

	bool CaptureCamera::initCamera()
	{
		pmv_start_log();
		camera = Camera::connect();
		//make sure camera hardware is alive
		if (camera->getStatus() != NO_ERROR)
		{
			pmv_debug_log("camera initialization failed");
			return false;
		}

		camera->setErrorCallback(err_cb, err_cb_cookie);

		if (camera->setPreviewDisplay(mSurface) != NO_ERROR){
			pmv_debug_log("setPreviewDisplay failed");
			return false;
		}

		const char* params = 
					"preview-format=yuv420sp;"
					"preview-frame-rate=15;"
					"picture-size=355x288;"
					"preview-size=355x288;"
					"antibanding=auto;"
					"antibanding-values=off,50hz,60hz,auto;"
					"effect-values=mono,negative,solarize,pastel,mosaic,resize,sepia,posterize,whiteboard,blackboard,aqua;"
					"jpeg-quality=100;"
					"jpeg-thumbnail-height=240;"
					"jpeg-thumbnail-quality=90;"
					"jpeg-thumbnail-width=320;"
					"luma-adaptation=0;"
					"nightshot-mode=0;"
					"picture-format=jpeg;"
					"whitebalance=auto;"
					"whitebalance-values=auto,custom,incandescent,fluorescent,daylight,cloudy,twilight,shade";

		String8 params8 = String8(params, 510);
		camera->setParameters(params8);
		if (mCamContext->rec_cb)
		{
			camera->setPreviewCallback(main_rec_callback, mCamContext, rec_flag);
			isDeleting = false;
		}
		hasCamera = true;
		return true;
	}

	void CaptureCamera::releaseCamera()
	{
		pmv_start_log();
		if (hasCamera)
		{
			isDeleting = true;
			camera->setPreviewCallback(NULL, NULL, FRAME_CALLBACK_FLAG_NOOP);
			camera->setErrorCallback(NULL, NULL);
			camera->disconnect();
			hasCamera = false;
		}
	}

	void CaptureCamera::setRecordingCallback(frame_cb cb, void* cookie, int flag)
	{
		pmv_start_log();
		CamContext* temp = new CamContext();
		temp->rec_cb = cb;
		temp->rec_cb_cookie = cookie;
		rec_flag=flag;
		if (hasCamera){
			if (temp->rec_cb == NULL)
			{
				isDeleting = true;
				camera->setPreviewCallback(NULL, NULL, FRAME_CALLBACK_FLAG_NOOP);
			}
			else
			{
				camera->setPreviewCallback(main_rec_callback, temp, rec_flag);
				isDeleting = false;
			}
		}
		delete mCamContext;
		mCamContext = temp;
	}

	void CaptureCamera::setErrorCallback(error_cb ecb, void* cookie)
	{
		pmv_start_log();
		err_cb = ecb;
		err_cb_cookie = cookie;
		if (hasCamera)
		{
			camera->setErrorCallback(err_cb, err_cb_cookie);
		}
	}

	void CaptureCamera::startPreview()
	{
		pmv_start_log();
		if (hasCamera)
		{
			camera->startPreview();
		}
	}

	void CaptureCamera::stopPreview()
	{
		pmv_start_log();
		if (hasCamera)
		{
			camera->stopPreview();
		}
	}
}//namespace