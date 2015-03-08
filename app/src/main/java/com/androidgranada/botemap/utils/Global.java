package com.androidgranada.botemap.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.PointF;
import android.widget.Toast;

import com.androidgranada.botemap.utils.DataBase;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.androidgranada.botemap.objects.User;

/**
 * Created by IgnacioErenas on 21/02/14.
 */

public class Global
{

    //public static String SERVER_URL = "http://fiestaprimavera.sytes.net:8080/";
    //public static String SERVER_URL = "http://192.168.1.128:8080/";
    public static String SERVER_URL = "http://192.168.1.128/";
    public static String TAG_SUCCESS = "success";
    public static String TAG_userS = "users";
    public static String TAG_ID = "ID";
    public static String TAG_MAIL = "Mail";
    public static String TAG_USERNAME = "Nombreuser";
    public static String TAG_NAME = "Nombre";
    public static String TAG_POINT_X = "Point_X";
    public static String TAG_POINT_Y = "Point_Y";
    public static String TAG_STATE = "Estado";

    public static DataBase db;

    public static boolean inicializar(Context context)
    {
        try
        {
            db = new DataBase(context);
            if (Session.user.getNombreUser().equals(""))
            {
                Session.user = db.obtenerUserLogueado();
                if (Session.user.getContactosComprobados().equals("S"))
                    return db.obtenerContactos();
            }
        } catch (Exception e)
        {
            return false;
        }
        return true;
    }

    public static void mostrarError(Activity activity)
    {
        Toast.makeText(
                activity.getApplicationContext(),
                "Error de Conexión", Toast.LENGTH_SHORT)
                .show();
    }

    public static boolean login(String mail, String pass)
    {
        JSONParser jParser = new JSONParser();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("mail", mail));
        params.add(new BasicNameValuePair("pass", pass));
        JSONObject json = jParser.makeHttpRequest("" + SERVER_URL + "login.php", "POST", params);
        HashMap<String, String> jsonMap = getuser(json);
        if (jsonMap != null)
        {
            Session.user = new User(jsonMap.get(TAG_USERNAME),
                    jsonMap.get(TAG_MAIL),
                    jsonMap.get(TAG_ID));
            if (!jsonMap.get(TAG_POINT_X).equals("null") && !jsonMap.get(TAG_POINT_Y).equals("null"))
            {
                PointF point = new PointF();
                point.x = Float.parseFloat(jsonMap.get(TAG_POINT_X));
                point.y = Float.parseFloat(jsonMap.get(TAG_POINT_Y));
                Session.user.setPosicion(point);
            }
            if (!jsonMap.get(TAG_NAME).equals(""))
                Session.user.setNombre(jsonMap.get(TAG_NAME));
            if (!jsonMap.get(TAG_STATE).equals(""))
                Session.user.setEstado(jsonMap.get(TAG_STATE));

            //db.insertaruser(Session.user);

            return true;
        }
        return false;
    }

    public static boolean registrar(String username, String pass, String cpass, String mail, String name)
    {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("mail", mail));
        params.add(new BasicNameValuePair("pass", pass));
        params.add(new BasicNameValuePair("cpass", cpass));
        params.add(new BasicNameValuePair("username", username));
        JSONParser jParser = new JSONParser();

        if (name != null)
            params.add(new BasicNameValuePair("name", name));
        JSONObject json = jParser.makeHttpRequest("" + SERVER_URL + "register.php", "POST", params);
        HashMap<String, String> jsonMap = getuser(json);
        if (jsonMap != null)
        {
            Session.user = new User(jsonMap.get(TAG_USERNAME),
                    jsonMap.get(TAG_MAIL),
                    jsonMap.get(TAG_ID));
            if (name != null && jsonMap.get(TAG_NAME) != null)
                Session.user.setNombre(jsonMap.get(TAG_NAME));

            //db.insertaruser(Session.user);

            return true;
        }
        return false;
    }


    public static boolean getSucessJSON(JSONObject json)
    {
        if (json == null)
            return false;
        try
        {
            return (json.getInt(TAG_SUCCESS)) == 1;
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public static ArrayList<HashMap<String, String>> getusers(JSONObject json)
    {
        try
        {
            JSONArray jsonArray;
            ArrayList<HashMap<String, String>> jsonArrayList;
            jsonArrayList = new ArrayList<HashMap<String, String>>();

            if (getSucessJSON(json))
            {
                jsonArray = json.getJSONArray(TAG_userS);
                for (int i = 0; i < jsonArray.length(); i++)
                {
                    JSONObject c = jsonArray.getJSONObject(i);
                    String id = c.getString(TAG_ID);
                    String name = c.getString(TAG_NAME);
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(TAG_ID, id);
                    map.put(TAG_NAME, name);
                    jsonArrayList.add(map);
                }
                return jsonArrayList;
            } else
            {
                return null;
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static HashMap<String, String> getuser(JSONObject json)
    {
        try
        {
            HashMap<String, String> jsonMap = new HashMap<String, String>();
            if (getSucessJSON(json))
            {
                String id = json.getString(TAG_ID);
                String mail = json.getString(TAG_MAIL);
                String username = json.getString(TAG_USERNAME);
                String name = json.getString(TAG_NAME);
                String point_x = json.getString(TAG_POINT_X);
                String point_y = json.getString(TAG_POINT_Y);
                String state = json.getString(TAG_STATE);
                jsonMap.put(TAG_ID, id);
                jsonMap.put(TAG_MAIL, mail);
                jsonMap.put(TAG_USERNAME, username);
                jsonMap.put(TAG_NAME, name);
                jsonMap.put(TAG_POINT_X, point_x);
                jsonMap.put(TAG_POINT_Y, point_y);
                jsonMap.put(TAG_STATE, state);
                return jsonMap;
            } else
            {
                return null;
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean enviarPosicion(User receptor)
    {
        return false;
    }

    public static boolean cambiarPosicion(PointF nuevaPosicion)
    {
        return false;
    }

    /*public static boolean agregarContacto(user contacto) {
        return false;
    }

    public static boolean eliminarContacto(user contacto) {
        return false;
    }

    public static boolean crearGrupo(Grupo grupo) {
        return false;
    }

    public static boolean eliminarGrupo(Grupo grupo) {
        return false;
    }

    public static boolean cambiarAdministrador(user nuevoAdministrador) {
        return false;
    }*/

    public boolean actualizaruser(User user)
    {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("mail", user.getCorreo()));
        params.add(new BasicNameValuePair("name", user.getNombre()));
        params.add(new BasicNameValuePair("point_x", user.getPosicion().x + ""));
        params.add(new BasicNameValuePair("point_y", user.getPosicion().y + ""));
        params.add(new BasicNameValuePair("username", user.getNombreUser()));
        params.add(new BasicNameValuePair("state", user.getNombreUser()));
        JSONParser jParser = new JSONParser();
        JSONObject json = jParser.makeHttpRequest("" + SERVER_URL + "update_users.php", "POST", params);
        return getSucessJSON(json);
    }

    /*public static boolean insertaruser(user user) {
        return false;
    }

    public static boolean eliminaruser(user user) {
        return false;
    }

    public static boolean actualizarImagenGrupo() {
        return false;
    }

    public static boolean compartirImagenGrupo(Grupo grupo) {
        return false;
    }

    public static boolean actualizarMapaContactos() {
        return false;
    }*/
}

