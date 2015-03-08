package com.androidgranada.botemap.objects;

import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by IgnacioErenas on 20/02/14.
 */
public class User implements Parcelable
{
    public static final Creator<User> CREATOR = new Creator<User>()
    {
        public User createFromParcel(Parcel in)
        {
            return new User(in);
        }

        public User[] newArray(int size)
        {
            return new User[size];
        }
    };
    private String nombre = "", correo = "", ID = "", movil = "", nombreUser = "", estado = "", password = "", contactosComprobados = "N";
    private PointF posicion = new PointF();

    public User(String nombre, String correo, String ID, String movil, String nombreUser, String estado, String password, PointF posicion)
    {
        this.nombre = nombre;
        this.correo = correo;
        this.ID = ID;
        this.movil = movil;
        this.nombreUser = nombreUser;
        this.estado = estado;
        this.password = password;
        this.posicion = posicion;
    }

    public User(String ID, String correo, String nombreUser, String nombre, String estado)
    {
        this.nombre = nombre;
        this.correo = correo;
        this.ID = ID;
        this.nombreUser = nombreUser;
        this.estado = estado;
        this.posicion = new PointF(0, 0);
    }

    public User(String nombreUser, String correo, String ID)
    {
        this.nombreUser = nombreUser;
        this.correo = correo;
        this.ID = ID;
        this.posicion = new PointF(0, 0);
    }

    public User()
    {

    }

    public User(Parcel in)
    {
        this.nombre = in.readString();
        this.correo = in.readString();
        this.ID = in.readString();
        this.movil = in.readString();
        this.nombreUser = in.readString();
        this.estado = in.readString();
        this.password = in.readString();
        this.posicion = in.readParcelable(PointF.class.getClassLoader());
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeString(this.nombre);
        parcel.writeString(this.correo);
        parcel.writeString(this.ID);
        parcel.writeString(this.movil);
        parcel.writeString(this.nombreUser);
        parcel.writeString(this.estado);
        parcel.writeString(this.password);
        parcel.writeParcelable(this.posicion, 0);
    }

    public String getNombre()
    {
        return nombre;
    }

    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }

    public String getCorreo()
    {
        return correo;
    }

    public void setCorreo(String correo)
    {
        this.correo = correo;
    }

    public String getID()
    {
        return ID;
    }

    public void setID(String ID)
    {
        this.ID = ID;
    }

    public String getMovil()
    {
        return movil;
    }

    public void setMovil(String movil)
    {
        this.movil = movil;
    }

    public String getNombreUser()
    {
        return nombreUser;
    }

    public void setNombreUser(String nombreUser)
    {
        this.nombreUser = nombreUser;
    }

    public PointF getPosicion()
    {
        return posicion;
    }

    public void setPosicion(PointF posicion)
    {
        this.posicion = posicion;
    }

    public String getEstado()
    {
        return estado;
    }

    public void setEstado(String estado)
    {
        this.estado = estado;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getContactosComprobados()
    {
        return contactosComprobados;
    }

    public void setContactosComprobados(String contactosComprobados)
    {
        this.contactosComprobados = contactosComprobados;
    }
}

