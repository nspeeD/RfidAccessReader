package com.speed.rfidaccessreader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.speed.rfidaccessreader.connections.DataStorage;
import com.speed.rfidaccessreader.models.User;
import com.speed.rfidaccessreader.utils.GuiUtils;
import com.speed.rfidaccessreader.utils.NFCForegroundUtil;
import com.speed.rfidaccessreader.utils.NfcUtils;

public class RfidActivity extends Activity {
	
	/**
	 * Data Elements
	 */
	private final static String TAG = "NFCREADER";
	public final static String KEYCODE = "XXXXXXXXX";
	private NFCForegroundUtil nfcForegroundUtil = null;
	
	/**
	 * View elements
	 */
	private LinearLayout mLayLinInfo;
	private LinearLayout mLayLinNoRfid;
	private ImageView mIVPhotoDetail;
	private TextView mTVName;
	private TextView mTVCompany;
	private TextView mTVNick;
	private Button mBTMoreInfo;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_rfid);
        
        //link elment with view
        mLayLinInfo = (LinearLayout) findViewById(R.id.layLinInfo);
        mLayLinNoRfid = (LinearLayout) findViewById(R.id.layLinNoRfid);
        mIVPhotoDetail = (ImageView) findViewById(R.id.ivPhotoDetail);
        mTVName = (TextView) findViewById(R.id.tvName);
        mTVCompany = (TextView) findViewById(R.id.tvCompany);
        mTVNick = (TextView) findViewById(R.id.tvNick);
        mBTMoreInfo = (Button) findViewById(R.id.btMoreInfo);
        
        //format text
        ((TextView) findViewById(R.id.tvHeadName)).setTypeface(GuiUtils.getFont(GuiUtils.ROBOTO_MEDIUM, this));
        ((TextView) findViewById(R.id.tvHeadNick)).setTypeface(GuiUtils.getFont(GuiUtils.ROBOTO_MEDIUM, this));
        ((TextView) findViewById(R.id.tvHeadCompany)).setTypeface(GuiUtils.getFont(GuiUtils.ROBOTO_MEDIUM, this));
        ((TextView) findViewById(R.id.tvHeadComment)).setTypeface(GuiUtils.getFont(GuiUtils.ROBOTO_MEDIUM, this));
        ((TextView) findViewById(R.id.tvHeadTag)).setTypeface(GuiUtils.getFont(GuiUtils.ROBOTO_MEDIUM, this));
        mBTMoreInfo.setTypeface(GuiUtils.getFont(GuiUtils.ROBOTO_BOLD, this));
        
        //Init NFC services
        nfcForegroundUtil = new NFCForegroundUtil(this);
        
        initListeners();
    }
    
    @Override
    public void onPause() {
	    super.onPause();
	    nfcForegroundUtil.disableForeground();
	}   

    @Override
	public void onResume() {
	    super.onResume();
	    nfcForegroundUtil.enableForeground();

	    if (!nfcForegroundUtil.getNfc().isEnabled()) {
	        Toast.makeText(getApplicationContext(), 
                    getResources().getString(R.string.infoStartNfc), 
                    Toast.LENGTH_LONG).show();
	        startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
	    }
	    
	    onNewIntent(getIntent());
	}

    /**
     * Listeners
     */
    private void initListeners () {
    	
    	mBTMoreInfo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivityContactDetail();
			}
		});
    }
    
    /**
     * Operation which starts ContactDetailActivity
     */
    private void startActivityContactDetail () {
    	
    	Intent i = new Intent(this, ContactDetailActivity.class);
    	startActivity(i);
    }
    
    /**
     * Get user from list of user
     * @param code -> ID from user
     * @return null if user not exist and return an user if exist
     */
    private User isValidUser (String code) {
    	
    	DataStorage db = new DataStorage(this);
    	User user = db.getUser(code);
    	if (user != null) {
			if (user.getId().equals(code)) {
				
				//Adding image to view 
				String sdcardImage = Environment.getExternalStorageDirectory().getAbsolutePath() + 
						"/RfidAccess/Images/" + user.getId() +".jpg";
				
				mIVPhotoDetail.setImageDrawable(Drawable.createFromPath(sdcardImage));
				
				return user;
			}
    	}
    	
    	return null;
    }
    
    @Override
    public void onNewIntent(Intent intent) {
//	    Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
//	    if (tag != null)
//	    	Toast.makeText(this, "TagID: " + NfcUtils.bytesToHex(tag.getId()), Toast.LENGTH_LONG).show();    
	    
	    // 1) Parse the intent and get the action that triggered this intent
        String action = intent.getAction();
        // 2) Check if it was triggered by a tag discovered interruption.
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            //  3) Get an instance of the TAG from the NfcAdapter
            Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            // 4) Get an instance of the Mifare classic card from this TAG intent
            MifareClassic mfc = MifareClassic.get(tagFromIntent);
            byte[] data;
            
            //  5.1) Connect to card
            try {       
	            mfc.connect();
	            boolean auth = false;
	            String cardData = null;
	            // 5.2) and get the number of sectors this card has..and loop thru these sectors
//	            int secCount = mfc.getSectorCount();
//	            int bCount = 0;
//	            int bIndex = 0;
//	            for(int j = 0; j < secCount; j++){
	                // 6.1) authenticate the sector
	//                auth = mfc.authenticateSectorWithKeyA(j, MifareClassic.KEY_DEFAULT);
	            	byte[] bts = new BigInteger(KEYCODE, 16).toByteArray();
//	            	auth = mfc.authenticateSectorWithKeyA(j, bts);
	            	auth = mfc.authenticateSectorWithKeyA(0, bts); //Our info is in sector 0
	                if(auth){
	                    // 6.2) In each sector - get the block count
//	                    bCount = mfc.getBlockCountInSector(j);
//	                    bIndex = 0;
//	                    for(int i = 0; i < bCount; i++){
//	                        bIndex = mfc.sectorToBlock(j);
	                        // 6.3) Read the block 2 (participant id is in this block)
	                        data = mfc.readBlock(1);    
	                        // 7) Convert the data into a string from Hex format.    
	                        User isUser = null;
	                        if (data != null && !data.toString().trim().equals("")) {
	                        	cardData = NfcUtils.bytesToHex(data);
		                        String ascii = NfcUtils.hexToASCII(cardData);
		                      //Delete DataBase File
		                    	String sdcardImage = Environment.getExternalStorageDirectory().getAbsolutePath() + "/RfidAccess/DataBase/" 
		                    	+ DataStorage.DATABASE_NAME + ".db";
		                		File imagesPath = new File(sdcardImage);
		                		if(imagesPath.exists()) {
		                			isUser = isValidUser(ascii.substring(0,8));
		                		}
//	                        }
//	                        bIndex++;
	                        }
	                        
	                        if (isUser != null) {
	                        	
	                        	mLayLinInfo.setVisibility(View.VISIBLE);
	                        	mLayLinNoRfid.setVisibility(View.GONE);
	                        	loadUserInView(isUser);
	                        }else {
	                        	
	                        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	                        	builder.setMessage(R.string.cantPass).setTitle(R.string.atention);
	                        	builder.setCancelable(true);
	                        	AlertDialog dialog = builder.create();
	                        	dialog.show();
	                        	mLayLinInfo.setVisibility(View.GONE);
	                        	mLayLinNoRfid.setVisibility(View.VISIBLE);
	                        }
	                }else{ // Authentication failed - Handle it
	                	Log.i(TAG,"Auth Failed");
	                	AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    	builder.setMessage(R.string.cantPass).setTitle(R.string.atention);
                    	builder.setCancelable(true);
                    	AlertDialog dialog = builder.create();
                    	dialog.show();
                    	mLayLinInfo.setVisibility(View.GONE);
                    	mLayLinNoRfid.setVisibility(View.VISIBLE);
	                }
//            }    
	        }catch (IOException e) { 
	        	// handle exception
	        }
        }
	}
    
    /**
     * Load user info on view 
     * @param user -> User data for load the view
     */
    private void loadUserInView (User user) {
    	mTVName.setText(user.getName());
    	mTVCompany.setText(user.getSurname());
    	mTVNick.setText(user.getNick());
    	
    	String sdcardImage = Environment.getExternalStorageDirectory().toString() + "/RfidAccess/Images/"
    			+ user.getNick();
		File imagesPath = new File(sdcardImage);
		
		if (imagesPath.exists()) {
			try { 
			
				FileInputStream input = new FileInputStream(imagesPath);

				Bitmap bitmap = BitmapFactory.decodeStream(input); //This gets the image
				mIVPhotoDetail.setImageBitmap(bitmap);
				input.close();
			}catch(Exception e) {
			    // handle exception
			}
		}
    }
}