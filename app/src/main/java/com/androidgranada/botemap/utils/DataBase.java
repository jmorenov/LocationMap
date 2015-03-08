package com.androidgranada.botemap.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.PointF;

import com.androidgranada.botemap.objects.User;
import com.androidgranada.botemap.utils.Session;

public class DataBase extends SQLiteOpenHelper
{

    protected static String TablaUser = "User";
    private String crearUsers = "CREATE TABLE IF NOT EXISTS "
            + DataBase.TablaUser
            + " (id INTEGER PRIMARY KEY NOT NULL, User TEXT, posicionX INTEGER , "
            + " posicionY INTEGER, correo TEXT, estado TEXT, password TEXT, contactosComprobados TEXT);";
    protected static String TablaMensaje = "Notificaciones";
    private String crearMensajes = "CREATE TABLE IF NOT EXISTS "
            + DataBase.TablaMensaje
            + " (id INTEGER PRIMARY KEY NOT NULL, emisor TEXT, posicionX INTEGER , "
            + " posicionY INTEGER, receptor TEXT, contenido TEXT);";
    private static int version = 4;
    private static String name = "primavera.db";
    private static SQLiteDatabase.CursorFactory factory = null;


    public DataBase(Context context)
    {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // Log.i(this.getClass().toString(), "Creando base de datos");

        db.execSQL(crearUsers);
        db.execSQL(crearMensajes);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        /*db.execSQL("DROP TABLE IF EXISTS " + TablaRegalos);
        db.execSQL("DROP TABLE IF EXISTS " + TablaNotificaciones);
		db.execSQL("DROP TABLE IF EXISTS " + TablaUser);
		db.execSQL("DROP TABLE IF EXISTS " + TablaEmpresas);*/
        onCreate(db);
    }

    /**
     * ********************UserS**********************************
     */

    public synchronized User obtenerUser(User User)
    {
        try
        {
            SQLiteDatabase db = getReadableDatabase();
            String[] valores_recuperar = {"id", "User",
                    "posicionX", "posicionY", "correo",
                    "estado", "password", "contactosComprobados"};


            Cursor c = db.query(TablaUser, valores_recuperar,
                    "id=" + User.getID(), null, null, null, null, null);

            if (c.getCount() == 0)
                return null;

            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
            {

                User.setID(c.getString(0));
                User.setNombreUser(c.getString(1));
                User.setPosicion(new PointF(c.getFloat(2), c.getFloat(3)));
                User.setCorreo(c.getString(4));
                User.setEstado(c.getString(5));
                User.setPassword(c.getString(6));
                User.setContactosComprobados(c.getString(7));

            }

            db.close();
            c.close();
        }
        catch (Exception e)
        {
            e.getMessage();
        }


        return User;
    }

    public synchronized int insertarUser(User User)
    {
        int idUser = 0;

        /*if (obtenerUser(User) != null) {
            eliminarUser(User);
            idUser = Integer.parseInt(User.getID());
        }*/
        SQLiteDatabase db = getWritableDatabase();
        if (db != null)
        {
            ContentValues values = new ContentValues();
            // values.put("id", Double.parseDouble(notificacion.getId()));
            values.put("id", User.getID());
            values.put("User", User.getNombreUser());
            values.put("posicionX", (int) User.getPosicion().x);
            values.put("posicionY", (int) User.getPosicion().y);
            values.put("correo", User.getCorreo());
            values.put("estado", User.getEstado());
            values.put("password", User.getPassword());
            values.put("contactosComprobados", User.getContactosComprobados());

            idUser = (int) db.insert(TablaUser, null, values);
            User.setID(String.valueOf(idUser));

        }
        db.close();

        return idUser;
    }

    public synchronized int eliminarUser(User User)
    {
        int idUser = 0;
        SQLiteDatabase db = getWritableDatabase();


        idUser = db.delete(TablaUser, "id=" + User.getID(), null);
        db.close();

        return idUser;
    }

    public synchronized User obtenerUserLogueado()
    {
        User User = new User();

        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM " + TablaUser
                + " WHERE password <> '' ", null);

        if (c.getCount() == 0)
            return null;

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
        {

            User.setID(c.getString(0));
            User.setNombreUser(c.getString(1));
            User.setPosicion(new PointF(c.getFloat(2), c.getFloat(3)));
            User.setCorreo(c.getString(4));
            User.setEstado(c.getString(5));
            User.setPassword(c.getString(6));
            User.setContactosComprobados(c.getString(7));

        }

        db.close();
        c.close();

        return User;
    }

    public synchronized boolean obtenerContactos()
    {
        SQLiteDatabase db = getReadableDatabase();
        try
        {
            Cursor c = db.rawQuery("SELECT * FROM " + TablaUser + " WHERE password=''", null);

            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
            {
                User User = new User();
                User.setID(c.getString(0));
                Session.contacts.add(this.obtenerUser(User));
            }

            db.close();
            c.close();

            return false;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        return true;
    }

    /**
     * ********************MENSAJES**********************************
     */
/*
    public synchronized Mensaje obtenerMensaje(Mensaje mensaje)
    {
        try
        {
            SQLiteDatabase db = getReadableDatabase();
            String[] valores_recuperar = {"id", "emisor",
                    "posicionX", "posicionY", "receptor",
                    "contenido"};


            Cursor c = db.query(TablaMensaje, valores_recuperar,
                    "id=" + mensaje.getID(), null, null, null, null, null);

            if (c.getCount() == 0)
                return null;

            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
            {

                mensaje.setID(c.getString(0));
                mensaje.setEmisorID(c.getString(1));
                mensaje.setPosicion(new PointF(c.getInt(2), c.getInt(3)));
                mensaje.setReceptorID(c.getString(4));
                mensaje.setContenido(c.getString(5));

            }

            db.close();
            c.close();
        }
        catch (Exception e)
        {
            e.getMessage();
        }


        return mensaje;
    }

    public synchronized int insertarMensaje(Mensaje mensaje)
    {
        int idMensaje = 0;

        if (obtenerMensaje(mensaje) != null)
        {
            eliminarMensaje(mensaje);
            idMensaje = Integer.parseInt(mensaje.getID());
        }
        SQLiteDatabase db = getWritableDatabase();
        if (db != null)
        {
            ContentValues values = new ContentValues();
            values.put("id", mensaje.getID());
            values.put("emisor", mensaje.getEmisorID());
            values.put("posicionX", (int) mensaje.getPosicion().x);
            values.put("posicionY", (int) mensaje.getPosicion().y);
            values.put("receptor", mensaje.getReceptorID());
            values.put("contenido", mensaje.getContenido());


            idMensaje = (int) db.insert(TablaMensaje, null, values);
            mensaje.setID(String.valueOf(idMensaje));

        }
        db.close();

        return idMensaje;
    }

    public synchronized int eliminarMensaje(Mensaje mensaje)
    {
        int idMensaje = 0;
        SQLiteDatabase db = getWritableDatabase();

        idMensaje = db.delete(TablaMensaje, "id=" + mensaje.getID(), null);
        db.close();

        return idMensaje;
    }
*/

}
