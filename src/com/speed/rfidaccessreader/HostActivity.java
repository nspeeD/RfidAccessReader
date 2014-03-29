package com.speed.rfidaccessreader;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.speed.rfidaccessreader.connections.DataStorage;
import com.speed.rfidaccessreader.models.User;
import com.speed.rfidaccessreader.utils.GuiUtils;

public class HostActivity extends Activity {
	
	/**
	 * Data Elements
	 */
	private static final String USER_VALID = "1234";
	private static final String PASS_VALID = "1234";
	public static final String LIST = "list";
	
	/**
	 * View elements
	 */
	private EditText mETUser;
	private EditText mETPass;
	private Button mBTContinue;
	private Button mBTLoadData;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.activity_host);
        
        //link elment with view
        mETUser = (EditText) findViewById(R.id.etUser);
        mETPass = (EditText) findViewById(R.id.etPass);
        mBTContinue = (Button) findViewById(R.id.btContinueLog);
        mBTLoadData = (Button) findViewById(R.id.btLoadData);
        
        //format text
        mETPass.setTypeface(GuiUtils.getFont(GuiUtils.ROBOTO_LIGHT, getApplicationContext()));
        mETUser.setTypeface(GuiUtils.getFont(GuiUtils.ROBOTO_LIGHT, getApplicationContext()));
        
        initListeners();
    }

    /**
     * Listeners
     */
    private void initListeners () {
    	
    	mBTContinue.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (checkLoginData()) {
					startActivityRfid();
				}else {
					Toast.makeText(HostActivity.this, getResources().getString(R.string.noValidUser), Toast.LENGTH_LONG).show();
				}
			}
		});
    	
    	mBTLoadData.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				startLoad();
			}
		});
    }
    
    /**
     * Initiate the process of load data
     */
    private void startLoad () {
    	//Show loading dialog
    	final ProgressDialog dialog = ProgressDialog.show(this, "", 
                getResources().getString(R.string.loadingData), true);
		dialog.show();
		
		Thread lTimer = new Thread() {
            public void run() {
                try {
                	checkDataFromSdCard();
                	startLoadJsonData();
                }
                catch (Exception e) {
            		runOnUiThread(new Runnable() {
          			  public void run() {
          				Toast.makeText(getApplicationContext(), 
                              getResources().getString(R.string.loadDataFail), 
                              Toast.LENGTH_LONG).show();
          			  }
          			});
                }
                finally {
                	
                	dialog.dismiss();
                	runOnUiThread(new Runnable() {
            			  public void run() {
            				  Toast.makeText(getApplicationContext(), 
                                      getResources().getString(R.string.loadDataComplete), 
                                      Toast.LENGTH_LONG).show();
            			  }
            			});
                }
                
            }
        };
        lTimer.start();
    }
    
    /**
     * Check if sd card contains images and delete if is necessary
     */
    private void checkDataFromSdCard () {
    	
    	String sdcardImage = Environment.getExternalStorageDirectory().getAbsolutePath() + "/RfidAccess/Images";
		File imagesPath = new File(sdcardImage);
    	
		//Delete all photos
    	if (imagesPath.isDirectory()) {
    		File[] files = imagesPath.listFiles();
            for (int i = 0; i < files.length; i++) {
            	files[i].delete();
            }
        }
    	
    	//Delete all dataBase files
    	sdcardImage = Environment.getExternalStorageDirectory().getAbsolutePath() + "/RfidAccess/DataBase/";
		imagesPath = new File(sdcardImage);
		if(imagesPath.exists()) {
			File[] files = imagesPath.listFiles();
		      for(int i=0; i<files.length; i++) {
		           files[i].delete();
		      }
		}
    }
    
    /**
     * Load information from Json file
     */
    private void startLoadJsonData () {

		//Create DB for users and Images
		List<User> usersList = DataStorage.readDataFromSdCard(this);
		
		DataStorage ds = new DataStorage(this);
		for (int i = 0; i < usersList.size(); i++) {
			ds.addUser(usersList.get(i));
		}
		
		ds.createDBOnSdCard();
		ds.close();
    }
    
    /**
     * Start activity Rfid
     */
    private void startActivityRfid() {
    	Intent i = new Intent(this, RfidActivity.class);
		startActivity(i);
    }
    
    /**
     * Login
     */
    private boolean checkLoginData () {
    	
    	//Mock Data for fast access
    	mETUser.setText (USER_VALID);
    	mETPass.setText (PASS_VALID);
    	
    	if (mETUser.getText().toString().equals (USER_VALID) && 
    			mETPass.getText().toString().equals(PASS_VALID)) {
    		return true;
    	}else {
    		return false;
    	}
    }
}
