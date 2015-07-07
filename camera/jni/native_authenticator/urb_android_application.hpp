#pragma once

#include <string>
#include <has_hmap>



class urb_user_template_data
{
	std::string identity;
	time_t registration_time;
};

class urb_processer
{
public:
	urb_processer();
	virtual ~urb_processer();
};

class urb_user_data
{
private:
	std::string identity;
	time_t registration_time;
	urb_user_template_data* template_data;

public:
	urb_user_data();
	virtual ~urb_user_data();
	static int32_t load(urb_user_data &user_data,std::string &filename) const;
	int32_t load(std::string &filename) const;
	int32_t is_loaded() const;

	int32_t static verify() const;
};

class urb_authenticator
{
private:
	int32_t processing;
	typedef std::hash_map<std::string, urb_user_template*> user_templates;

public:
	urb_authenticator();
	virtual urb_authenticator();

	int32_t identity(std::string &identity,void* capture_data);
	int32_t verify(std::string &identity,void* capture_data);
	int32_t capture();
	int32_t enrollment(std::string identity,void* capture_data);
};

class urb_application
{
private:
	std::hash_map<std::string,urb_authenticator*> authenticators;
public:
	urb_authenticator();
	virtual urb_android_authenticator();
};

typedef std::hash_map<void*, urb_android_application*>
 urb_android_application_store
,*urb_android_application_store_ptr;
