package com.medialoha.android.acratester.app;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.prefs.Preferences;

import org.acra.ACRA;
import org.acra.ACRAConfiguration;
import org.acra.ACRAConfigurationException;
import org.acra.ReportingInteractionMode;
import org.acra.collector.CrashReportData;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.medialoha.android.acratester.R;


public class MainActivity extends Activity {
	
	private static final String TAG = "ACRATester";
	
	private static final String PREF_FORM_URI = "form_uri";
	private static final String PREF_BASIC_AUTH_LOGIN = "basic_auth_login";
	private static final String PREF_BASIC_AUTH_PWD = "basic_auth_pwd";
	
	
	private EditText mETFormURI;
	private EditText mETBasicAuthLogin;
	private EditText mETBasicAuthPwd;
	private EditText mETExceptionMessage;

	private ToggleButton mTBEnableBasicAuth;
	private ToggleButton mTBEnableSilentReport;
	
	private SharedPreferences mPrefs;
	private ACRATesterException mException;
	
	

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    setContentView(R.layout.activity_main);
    
    mPrefs = getPreferences(MODE_PRIVATE);    
   
    initView();
    loadConfig();
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
  	getMenuInflater().inflate(R.menu.menu, menu);
  	
  	return super.onCreateOptionsMenu(menu);
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
  	
  	switch (item.getItemId()) {
  		case R.id.actionSaveConfig :
  				saveConfig();
  			break;
  		case R.id.actionLoadConfig :
  				loadConfig();
  			break;
  	
  		default : return super.onOptionsItemSelected(item);
  	}
  	
  	return true;
  }

  public void onSendCrashReportClick(View v) {
  	if (configureACRA()) {
  		String custom = mETExceptionMessage.getText().length()==0?null:mETExceptionMessage.getText().toString();
  		
  		if (mTBEnableSilentReport.isChecked()) {  			
  			ACRA.getErrorReporter().handleSilentException(new ACRATesterException(this, true, custom));
  			  			
  		} else { ACRA.getErrorReporter().handleException(new ACRATesterException(this, false, custom)); }
  	}
  }
  
   
  private boolean configureACRA() {
  	ACRAConfiguration cfg = ACRA.getConfig();
  	  	
  	try {
			cfg.setResToastText(R.string.ReportingInteractionText);
			cfg.setMode(ReportingInteractionMode.TOAST);
			
		} catch (ACRAConfigurationException e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			e.printStackTrace();
			
			return false;
		}
  	
  	cfg.setFormKey("");
  	cfg.setFormUri(mETFormURI.getText().toString());
  	  	
  	if (mTBEnableBasicAuth.isChecked()) {
  		cfg.setFormUriBasicAuthLogin(mETBasicAuthLogin.getText().toString());
  		cfg.setFormUriBasicAuthPassword(mETBasicAuthPwd.getText().toString());
  		
  	} else { 
  		cfg.setFormUriBasicAuthLogin(null);
  		cfg.setFormUriBasicAuthPassword(null); 
  	}  	
  	
  	//ACRA.setConfig(cfg);
  	
  	return true;
  }
  
  private void saveConfig() {
  	Editor mPrefsEditor = mPrefs.edit();
  	
  	mPrefsEditor.putString(PREF_FORM_URI, mETFormURI.getText().toString());
  	mPrefsEditor.putString(PREF_BASIC_AUTH_LOGIN, mETBasicAuthLogin.getText().toString());
  	mPrefsEditor.putString(PREF_BASIC_AUTH_PWD, mETBasicAuthPwd.getText().toString());
  	
  	mPrefsEditor.commit();
  	
  	Toast.makeText(this, "Configuation saved !", Toast.LENGTH_SHORT).show();
  }
  
  private void loadConfig() {
  	mETFormURI.setText(mPrefs.getString(PREF_FORM_URI, null));

    mETBasicAuthLogin.setText(mPrefs.getString(PREF_BASIC_AUTH_LOGIN, null));
    mETBasicAuthPwd.setText(mPrefs.getString(PREF_BASIC_AUTH_PWD, null));
  }
  
  private void initView() {
    mETFormURI = (EditText)findViewById(R.id.formURI);
    mETFormURI.setHint(Html.fromHtml("<small><small>http(s)://xxx.xxx.xxx.xxx/report/report.php</small></small>"));

    mETBasicAuthLogin = (EditText)findViewById(R.id.basicAuthLogin);

    mETBasicAuthPwd = (EditText)findViewById(R.id.basicAuthpassword);
    
    mTBEnableSilentReport = (ToggleButton)findViewById(R.id.enableSilentReport);
    mTBEnableBasicAuth = (ToggleButton)findViewById(R.id.enableBasicAuth);
    
    mETExceptionMessage = (EditText)findViewById(R.id.exceptionMsg);
  }
  
  
  // CLASS : ACRA TESTER EXCEPTION
  protected static class ACRATesterException extends Exception {
  	
  	private String mMessage;
  	private boolean isSilent;
  	

  	// CONSTRUCTOR
  	public ACRATesterException(Context context) {
  		isSilent = false;
  		mMessage = context.getString(R.string.DefaultExceptionMessage);
  	}
  	
  	public ACRATesterException(Context context, boolean silent, String message) {
  		isSilent = silent;
  		mMessage = message==null?context.getString(R.string.DefaultExceptionMessage):message;
  	}
  	
  	
  	@Override
  	public String getMessage() {
  		return (isSilent?"[SILENT] ":"")+mMessage;
  	}
  	
  	protected void setSilent(boolean enable) {
  		isSilent = enable;
  	}
  }
}
