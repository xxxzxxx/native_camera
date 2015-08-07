package com.primitive.natives.camera.service;

import com.primitive.natives.model.ByteArrayParcelableList;
import com.primitive.natives.camera.service.ImageCalculatorCallbackInterface;

interface ImageCalculatorServiceInterface
{
	oneway void register_callback(ImageCalculatorCallbackInterface callback);
	oneway void unregister_callback(ImageCalculatorCallbackInterface callback);
	oneway void setup(in byte[] license);
	oneway void setup_enroll();
	oneway void command_enroll();
	oneway void setup_authenticate();
	oneway void setup_verification();
	oneway void setup_capture();
	oneway void cancel();
}
