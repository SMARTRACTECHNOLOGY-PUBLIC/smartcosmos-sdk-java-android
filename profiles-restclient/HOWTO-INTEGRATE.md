# How-to: Integrate Profiles RestClient in Android

## Android Manifest

To enable network access for the client app, the app requires internet access. Add the [INTERNET](http://developer.android.com/reference/android/Manifest.permission.html#INTERNET "INTERNET PERMISSION") permission to your *AndroidManifest.xml*:

	<?xml version="1.0" encoding="utf-8"?>
	<manifest xmlns:android="http://schemas.android.com/apk/res/android"
	    package="com.example.myprofilesapp"
	    android:versionCode="1"
	    android:versionName="1.0" >
	 
	    <!-- add INTERNET permission here -->
	    <uses-permission android:name="android.permission.INTERNET" />
	 
	    <!-- other declarations... -->
	 
	</manifest>

## Libraries

Add following library files to the /libs folder (version numbers may vary):

 - utility-1.0.jar (Run **mvn clean install** to create the jar) 
 - profiles-restclient-1.1.jar (Run **mvn clean install** to create the jar)
 - retrofit-1.9.0.jar (Maven [link](http://mvnrepository.com/artifact/com.squareup.retrofit/retrofit "RetroFit in Maven"))
 - gson-2.4.jar (Maven [link](http://mvnrepository.com/artifact/com.google.code.gson/gson "GSON in Maven"))

Notes:

 - These libraries have been tested with Android only. It is not guaranteed that they work with other Java VMs.
 - From version 1.1 on, profiles-restclient supports javadoc. Build the profiles-restclient with Maven to create the javadoc jar.

## App Activity

To access a Profiles endpoint, create a worker task by extending an AsyncTask and run the ProfilesRestClient inside of doInBackground. Using the AsyncTask prevents blocking the App UI (so the user does not believe the app has crashed).

	package com.example.myprofilesapp;
	 
	/* other imports */
	 
	// Import stuff for Profiles Client
	import com.smartrac.profiles.ProfilesRestClient;
	import com.smartrac.profiles.ProfilesRestClient.ProfilesRestResult;
	 
	public class MyActivity extends Activity {
	 
	    /* other activity functions */
	 
	    // Run Worker Task
	    public void onQueryProfiles() {
	        new MyProfilesTask().execute(this);
	    }
	 
	    // Implementation of Profiles Worker Task
	    private class MyProfilesTask extends AsyncTask<ParamsType, ProgressType, ResultType) {
	 
	        @Override
	        protected ResultType doInBackground(ParamsType... params) {
	             
	            try {
	                // Provide server address, username and password
	                // (for https:// the server needs a valid SSL certificate assigned by a trusted root CA)
	                // This is just a dumb example, do not hard-code the credentials in the app!!!
	                // Instead create an input dialog for the credentials.
	                ProfilesRestClient client = new ProfilesRestClient("https://profiles.example.com", "user-foo", "pw-bar");
	                // Access the method of the client
	                ProfilesRestResult result = client.getVerificationMessage("RR",  0);
	                if (result.httpStatus == 200) {
	                    String message = result.message();
	                } else {
	                    // handle bad HTTP status
	                }
	            } catch (Exception e) {
	                // handle the exception error...
	            }
	        }
	    }
	}

## Security Considerations

To secure the app and the Profiles service, perform following steps:

1. Never hard-code user/password credentials in the app. APK files can be easily reverse engineered, so it would be easy to obtain those keys.
2. Use SSL encrypted connections whenever possible. Obtain a SSL certificate from a trusted root CA for the server.
3. Avoid using Profiles Admin role users for the app. Create users with the standard User role instead and use them with the app.