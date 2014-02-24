package com.medialoha.android.acratester;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;


@ReportsCrashes(
    formKey = "", // This is required for backward compatibility but not used

    formUri = "http://127.0.0.1/",
    formUriBasicAuthLogin = "", 
    formUriBasicAuthPassword = "",
    
    httpMethod = org.acra.sender.HttpSender.Method.PUT,
    reportType = org.acra.sender.HttpSender.Type.JSON,
    
    logcatArguments = { "-t", "100", "-v", "time" }
)
public class ACRATesterApp extends Application {
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		ACRA.init(this);
	}

}
