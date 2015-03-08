package com.androidgranada.botemap.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.PointF;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.androidgranada.botemap.objects.User;
import com.androidgranada.botemap.utils.Global;
import com.androidgranada.botemap.utils.JSONParser;
import com.androidgranada.botemap.utils.Session;

/**
 * Created by jmorenov on 20/03/14.
 */
public class Contacts extends AsyncTask<String, String, String>
{

    JSONParser jParser = new JSONParser();
    Context context;
    ArrayList<String> correos = new ArrayList<String>();
    ArrayList<HashMap<String, String>> contacts = new ArrayList<HashMap<String, String>>();

    public Contacts(Context context)
    {
        this.context = context;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
    }

    protected String doInBackground(String... args)
    {
        getContactsFromMobile();
        for (int i = 0; i < correos.size(); i++)
        {
            Log.i("CORREOS: ", correos.get(i));
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("mail", correos.get(i)));
            JSONObject json = jParser.makeHttpRequest("" + Global.SERVER_URL + "users.php", "POST", params);
            if (json != null)
                contacts.add(Global.getuser(json));
        }
        return null;
    }

    protected void onPostExecute(String file_url)
    {
        if (contacts.size() > 0)
        {
            Session.contacts = new ArrayList<User>();
            for (int i = 0; i < contacts.size(); i++)
            {
                HashMap<String, String> map = contacts.get(i);
                if (map != null)
                {
                    User u = new User(
                            map.get(Global.TAG_ID),
                            map.get(Global.TAG_MAIL),
                            map.get(Global.TAG_USERNAME),
                            map.get(Global.TAG_NAME),
                            map.get(Global.TAG_STATE)
                    );
                    if (map.get(Global.TAG_POINT_X) != "null" && map.get(Global.TAG_POINT_Y) != "null")
                    {
                        u.setPosicion(new PointF(
                                Float.parseFloat(map.get(Global.TAG_POINT_X)),
                                Float.parseFloat(map.get(Global.TAG_POINT_Y))
                        ));
                    }
                    Session.contacts.add(u);
                }

            }
            Session.user.setContactosComprobados("S");
        }
    }

    /*public void getContactsFromMobile() {

        String email;

        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;

        Uri EmailCONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
        String DATA = ContactsContract.CommonDataKinds.Email.DATA;

        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null, DISPLAY_NAME + " ASC");
        if (cursor.getCount() > 0) {
            String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
            Cursor emailCursor = contentResolver.query(EmailCONTENT_URI, null, EmailCONTACT_ID + " = ?", new String[]{contact_id}, null);
            Log.e("LLEGOOO 2", "seep");
            while (emailCursor.moveToNext()) {
                email = emailCursor.getString(emailCursor.getColumnIndex(DATA));
                correos.add(email);
            }
            emailCursor.close();
        }
    }*/

    public void getContactsFromMobile()
    {

        String phoneNumber = null;
        String email = null;

        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

        Uri EmailCONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
        String DATA = ContactsContract.CommonDataKinds.Email.DATA;

        ContentResolver contentResolver = context.getContentResolver();

        Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null, DISPLAY_NAME + " ASC");

        // Loop for every contact in the phone
        if (cursor.getCount() > 0)
        {

            while (cursor.moveToNext())
            {

                String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
                String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));

                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));

                if (hasPhoneNumber > 0)
                {
                    Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[]{contact_id}, null);

                    while (phoneCursor.moveToNext())
                    {
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                    }

                    phoneCursor.close();
                    Cursor emailCursor = contentResolver.query(EmailCONTENT_URI, null, EmailCONTACT_ID + " = ?", new String[]{contact_id}, null);

                    while (emailCursor.moveToNext())
                    {

                        email = emailCursor.getString(emailCursor.getColumnIndex(DATA));
                        correos.add(email);
                    }

                    emailCursor.close();
                }
            }
        }
    }

}

