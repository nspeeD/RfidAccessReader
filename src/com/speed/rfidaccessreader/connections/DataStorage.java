package com.speed.rfidaccessreader.connections;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.speed.rfidaccessreader.R;
import com.speed.rfidaccessreader.models.User;

public class DataStorage  extends SQLiteOpenHelper{
	
	public final static String NAME = "nombre";
	public final static String SURNAME = "apellidos";
	public final static String PHOTO = "foto";
	public final static String ID = "codigo";
	public final static String NICK = "nick";
	public final static String FILE = "test.json";
	public final static String ARRAY = "array";
	
	
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "users";
    private static final String TABLE_USERS = "Usuarios";
	
	public DataStorage(Context context) {
	        super(context, Environment.getExternalStorageDirectory().getAbsolutePath() + "/RfidAccess/DataBase/" + DATABASE_NAME, null, DATABASE_VERSION);
	}
	 
	 /**
	  * Creating Tables
	  */
    @Override
    public void onCreate(SQLiteDatabase db) {
    	
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + ID + " TEXT," + NAME + " TEXT,"
                + SURNAME + " TEXT,"  + NICK + " TEXT" +")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }
 
    /**
     * Upgrading database
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
 
        // Create tables again
        onCreate(db);
    }
    
    /**
     * Getting single user from db
     * @param id -> ID from user
     * @return -> user if exist and null if not
     */
	public User getUser(String id) {
		
	    SQLiteDatabase db = this.getReadableDatabase();
	 
//	    Cursor cursor = db.query(TABLE_USERS, new String[] { ID,
//	    		NAME, SURNAME, NICK }, ID + "=?",
//	            new String[] { id }, null, null, null, null);
	    
	    String query = "select * from '" + TABLE_USERS + "' where "+ ID +"= '"+ id +"'";
	    Cursor cursor = db.rawQuery(query, null);
	    if (cursor != null && cursor.getCount() > 0)
	        cursor.moveToFirst();
	    
	    User user = null;
	    if (cursor.getCount() > 0)
	    	user = new User(cursor.getString(0),
	            cursor.getString(1), cursor.getString(2), null, cursor.getString(3));
	    // return contact
	    
	    cursor.close();
	    db.close();
	    return user;
	}
	
	/**
	 * Create the data base on sd card
	 */
	public void createDBOnSdCard () {
		
		String sdcard = Environment.getExternalStorageDirectory().toString() + "/RfidAccess/DataBase/";
		File file = new File(sdcard,DATABASE_NAME);
		
		SQLiteDatabase.openOrCreateDatabase(file + ".db", null);
	}
	
	/**
	 * Adding new user to data base
	 * @param user
	 */
    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(NAME, user.getName()); 
        values.put(SURNAME, user.getSurname()); 
        values.put(NICK, user.getNick()); 
        values.put(ID, user.getId()); 
 
        // Inserting Row
        db.insert(TABLE_USERS, null, values);
        db.close(); // Closing database connection
    }
	
    /**
     * Read all data from users data base
     * @param context
     * @return -> List of user obtained from sdcard json
     */
	public static List<User> readDataFromSdCard (Context context) {
		
		ArrayList<User> list = null;
		
		//Get External Storage
		String sdcard = Environment.getExternalStorageDirectory().toString() + "/RfidAccess/";
		File file = new File(sdcard,FILE);
		
		//Only if the file exists
		if (file.exists()) {
			list = new ArrayList<User> ();
			try { 
				
				BufferedReader br = new BufferedReader(new FileReader(file));
				StringBuilder jsonString = new StringBuilder();
				String line = "";
				
			    //Read the file 
			    while ((line = br.readLine()) != null) {
			        jsonString.append(line);
			    }
			    br.close();
			    
			    //Create Json with Gson Lib
			    Gson gson = new Gson();
			    JsonParser parser = new JsonParser();
			    JsonArray Jarray = parser.parse(jsonString.toString()).getAsJsonArray();

			    for(JsonElement obj : Jarray )
			    {
			    	User listUsers = gson.fromJson(obj, User.class);
			        list.add(listUsers);
			        
			        //Decode Image as Bitmap
					byte[] imageAsBytes = Base64.decode(listUsers.getPhoto().getBytes(), Base64.DEFAULT);
					writeImageOnSdCard(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length),
							listUsers.getId());
			    }
			} catch(Exception e) {
			    // handle exception
			}

		}else {
			
			Toast.makeText(context, context.getResources().getString(R.string.noDataFromSd), Toast.LENGTH_LONG).show();
		}
		
		return list;
	}
	
	/**
	 * Write an image on sdcard
	 * @param bitmap -> Bitmap to write in sd card
	 * @param id -> Id from user to set the name of file
	 */
	public static void writeImageOnSdCard (Bitmap bitmap, String id) {
		
		String sdcardImage = Environment.getExternalStorageDirectory().getAbsolutePath() + "/RfidAccess/Images";
		File imagesPath = new File(sdcardImage);
		// have the object build the directory structure, if needed.
		if (!imagesPath.exists()) {
			imagesPath.mkdirs();
		}
		
		try {
			//Write the file
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
			
			File outputFile = new File(imagesPath, "/" + id + ".jpg");
			outputFile.createNewFile();
			FileOutputStream out = new FileOutputStream(outputFile);
			out.write(bytes.toByteArray());
			out.flush();
			out.close();
		} catch (IOException e) {
			// handle exception
		}
	}
}