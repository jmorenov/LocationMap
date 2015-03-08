package com.androidgranada.botemap.GUI;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.androidgranada.botemap.R;
import com.androidgranada.botemap.objects.User;
import com.androidgranada.botemap.utils.Session;

/**
 * Created by jmorenov on 19/03/14.
 */
public class PerfilFragment extends Fragment
{

    static TextView tvNombreuser, tvNombre, tvCorreo, tvEstado;
    static EditText etNombreuser, etNombre, etCorreo, etEstado;
    static Button btGuardar;
    ProgressDialog progressDialog;

    public PerfilFragment()
    {
    }

    public static void cambiarModo(int modo)
    {

        if (modo == 1)
        {
            tvNombre.setVisibility(View.GONE);
            tvCorreo.setVisibility(View.VISIBLE);
            tvEstado.setVisibility(View.GONE);
            tvNombreuser.setVisibility(View.GONE);

            etNombre.setVisibility(View.VISIBLE);
            //etCorreo.setVisibility(View.VISIBLE);
            etEstado.setVisibility(View.VISIBLE);
            etNombreuser.setVisibility(View.VISIBLE);

            btGuardar.setVisibility(View.VISIBLE);
        }
        else if (modo == 2)
        {
            etNombre.setVisibility(View.GONE);
            //etCorreo.setVisibility(View.GONE);
            etEstado.setVisibility(View.GONE);
            etNombreuser.setVisibility(View.GONE);
            tvNombre.setVisibility(View.VISIBLE);
            tvCorreo.setVisibility(View.VISIBLE);
            tvEstado.setVisibility(View.VISIBLE);
            tvNombreuser.setVisibility(View.VISIBLE);
            etNombre.setText(Session.user.getNombre());
            //etCorreo.setText((Session.user.getCorreo()));
            etEstado.setText((Session.user.getEstado()));
            etNombreuser.setText(Session.user.getNombreUser());

            if (!Session.user.getNombre().equals(""))
            {
                tvNombre.setText(Session.user.getNombre());
                etNombre.setText(Session.user.getNombre());
            }
            else
                tvNombre.setText("Escribe tu nombre y será mas facil buscarte");

            if (!Session.user.getEstado().equals("null"))
            {
                tvEstado.setText((Session.user.getEstado()));
                etEstado.setText((Session.user.getEstado()));
            }
            else
            {
                tvEstado.setText("Escribe un estado");
                etEstado.setText("Sin estado");
            }

            tvCorreo.setText((Session.user.getCorreo()));
            tvNombreuser.setText(Session.user.getNombreUser());
            btGuardar.setVisibility(View.GONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.activity_perfil, container, false);
        tvNombre = (TextView) rootView.findViewById(R.id.tvPerfil_nombre);
        tvCorreo = (TextView) rootView.findViewById(R.id.tvPerfil_correo);
        tvEstado = (TextView) rootView.findViewById(R.id.tvPerfil_estado);
        tvNombreuser = (TextView) rootView.findViewById(R.id.tvPerfil_nombreUsuario);

        etNombre = (EditText) rootView.findViewById(R.id.etPerfil_nombre);
        //etCorreo = (EditText) rootView.findViewById(R.id.etPerfil_correo);
        etEstado = (EditText) rootView.findViewById(R.id.etPerfil_estado);
        etNombreuser = (EditText) rootView.findViewById(R.id.etPerfil_nombreUsuario);

        btGuardar = (Button) rootView.findViewById(R.id.btPerfil_guardarCambios);
        //btGuardar.setVisibility(View.GONE);

        cambiarModo(2);

        btGuardar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                ActualizarPerfil actualizarPerfil = new ActualizarPerfil();
                actualizarPerfil.execute();
            }
        });
        return rootView;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        ((MainActivity) getActivity()).setActionBarTitleByPosition(2);
        ((MainActivity) getActivity()).restoreActionBar();
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
    }

    private class ActualizarPerfil extends AsyncTask<Void, Void, Void>
    {

        User user;
        boolean success;

        @Override
        protected void onPreExecute()
        {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle(PerfilFragment.this.getResources()
                    .getString(R.string.app_name));
            progressDialog.setMessage("Actualizando");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params)
        {

            user = Session.user;
            if (!etNombre.getText().toString().equals(user.getNombre())
                    && !etNombre.getText().toString().equals("")
                    && !etNombre.getText().toString().equals(" ")
                    )
                user.setNombre(etNombre.getText().toString());

            if (!etEstado.getText().toString().equals(user.getEstado())
                    && !etEstado.getText().toString().equals("")
                    && !etEstado.getText().toString().equals(" ")
                    )
                user.setEstado(etEstado.getText().toString());

            if (!etNombreuser.getText().toString().equals(user.getNombreUser())
                    && !etNombreuser.getText().toString().equals("")
                    && !etNombreuser.getText().toString().equals(" ")
                    )
                user.setNombreUser(etNombreuser.getText().toString());

            //success = new Globales().actualizaruser(user);

            return null;
        }

        @Override
        protected void onPostExecute(Void bytes)
        {

            //Si el parser ha ido bien
            /*if (success) {
                Globales.db.insertaruser(user);
                Globales.inicializar(getActivity().getApplicationContext());
            }
            //Si no ha ido bien mostramos un toast con el error
            else {
                Globales.mostrarError(getActivity());
            }*/
            cambiarModo(2);
            progressDialog.dismiss();
        }
    }

}

