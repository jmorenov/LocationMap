package com.androidgranada.botemap.GUI;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidgranada.botemap.R;
import com.androidgranada.botemap.utils.Global;
import com.androidgranada.botemap.utils.Session;
import com.androidgranada.botemap.utils.Contacts;


public class MapFragment extends Fragment {
    public MapTouchImageView map;
    private SavePositionFragment savePositionFragment;

    public MapFragment() {
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        assert (rootView != null);

        savePositionFragment = new SavePositionFragment();
        getChildFragmentManager().beginTransaction().
                add(R.id.save_position_container, savePositionFragment).
                hide(savePositionFragment).commit();

        map = (MapTouchImageView) rootView.findViewById(R.id.Map);
        map.setImageResource(R.drawable.map_total_recortado);
        map.setMaxZoom(10);
        map.setFragmentListener(this);


        dialogLogin();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setActionBarTitleByPosition(0);
        ((MainActivity) getActivity()).restoreActionBar();
        map.setImageResource(R.drawable.map_total_recortado);
        map.setMaxZoom(10);
        map.setFragmentListener(this);
        if (Session.user != null && Session.user.getPosicion().x != 0 && Session.user.getPosicion().y != 0) {
            //Log.i("POS: ", Session.user.getPosicion().x + " " + Session.user.getPosicion().y);
            map.setPositionMapFromRealPoint(Session.user.getPosicion());
            map.enablePosition();
        }
    }

    @Override
    public void onPause() {
        map.setImageBitmap(null);
        super.onPause();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public void dialogLogin() {
        if (Session.user == null || Session.user.getCorreo().equals("")) {
            DialogLogin dialogo = new DialogLogin();
            dialogo.setParentFragment(this);
            dialogo.setCancelable(false);
            dialogo.show(getChildFragmentManager(), "");
        }
        new Contacts(getActivity().getApplicationContext()).execute();
    }

    public void dialogRegister() {
        if (Session.user == null || Session.user.getCorreo().equals("")) {
            DialogRegister dialogo = new DialogRegister();
            dialogo.setParentFragment(this);
            dialogo.setCancelable(false);
            dialogo.show(getChildFragmentManager(), "");
        }
    }

    public void locationChange() {
        if (savePositionFragment.isVisible())
            savePositionFragment.hide();
        savePositionFragment.show();
    }

    public void selectPositionMap() {
        DialogSelectPosition dialogo = new DialogSelectPosition();
        dialogo.setParentFragment(this);
        dialogo.show(getChildFragmentManager(), "");
    }

    public void getPositionMap() {
        DialogGetPosition dialogo = new DialogGetPosition();
        dialogo.setPositionNumber(map.getPalabraPosition());
        dialogo.show(getChildFragmentManager(), "");
    }

    public void sharePositionMap() {
        DialogSharePosition dialogo = new DialogSharePosition();
        dialogo.setParentFragment(this);
        dialogo.show(getChildFragmentManager(), "");
    }

    /**
     * Cuadro emergente solicitando guardar posición.
     */
    public static class SavePositionFragment extends Fragment implements View.OnClickListener {
        private Button accept;
        private Button cancel;
        private MapFragment mapFragment;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_save_position, container, false);
            assert (rootView != null);

            accept = (Button) rootView.findViewById(R.id.accept_new_position);
            cancel = (Button) rootView.findViewById(R.id.cancel_new_position);
            accept.setOnClickListener(this);
            cancel.setOnClickListener(this);

            return rootView;
        }

        public void show() {
            getChildFragmentManager().beginTransaction().
                    //setCustomAnimations(R.anim.slide_in, R.anim.slide_out).
                            setCustomAnimations(R.anim.slide_enter, R.anim.slide_exit, R.anim.pop_enter, R.anim.pop_exit).
                    show(this).commit();
        }

        public void hide() {
            getChildFragmentManager().beginTransaction().
                    //setCustomAnimations(R.anim.slide_in, R.anim.slide_out).
                            setCustomAnimations(R.anim.slide_enter, R.anim.slide_exit, R.anim.pop_enter, R.anim.pop_exit).
                    hide(this).commit();
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.accept_new_position:
                    mapFragment.map.enablePosition();
                    Session.user.setPosicion(mapFragment.map.getPositionMap());
                    new Global().actualizaruser(Session.user);
                    break;
                case R.id.cancel_new_position:
                    if (mapFragment.map.isPositionEnabled())
                        mapFragment.map.backOldPosition();
                    else
                        mapFragment.map.disablePosition();
                    break;
            }
            hide();
        }

        @Override
        public void onResume() {
            super.onResume();
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            mapFragment = (MapFragment) ((MainActivity) activity).fragment;
        }
    }

    public static class DialogSelectPosition extends DialogFragment {

        Fragment fragment;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_insert_position, null);
            final EditText codePosition = (EditText) view.findViewById(R.id.code_position);

            builder.setTitle(R.string.action_select_map_position)
                    .setView(view)
                    .setNegativeButton(getResources().getString(R.string.cancel_action), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    })
                    .setPositiveButton(getResources().getString(R.string.accept_action), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            String code = codePosition.getText().toString();
                            if (((MapFragment) fragment).map.checkPositionCode(code)) {
                                ((MapFragment) fragment).map.setPositionCode(code);
                                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.sucess_code_position),
                                        Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.error_code_position),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            return builder.create();
        }

        public void setParentFragment(Fragment f) {
            fragment = f;
        }
    }

    public static class DialogGetPosition extends DialogFragment {

        String position_number;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_get_position, null);

            EditText numberPosition = (EditText) view.findViewById(R.id.number_position);
            numberPosition.setText(position_number);

            builder.setTitle(R.string.action_get_map_position)
                    .setView(view)
                    .setPositiveButton(getResources().getString(R.string.accept_action), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            return builder.create();
        }

        public void setPositionNumber(String positionNumber) {
            position_number = positionNumber;
        }
    }

    public static class DialogSharePosition extends DialogFragment {
        Fragment fragment;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            String[] items = {getResources().getString(R.string.action_print_map),
                    getResources().getString(R.string.action_show_number_position)};

            builder.setTitle(R.string.action_get_map_position)
                    .setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    ((MapFragment) fragment).map.printMap();
                                    break;
                                case 1:
                                    ((MapFragment) fragment).getPositionMap();
                                    break;
                            }
                        }
                    });
            return builder.create();
        }

        public void setParentFragment(Fragment f) {
            fragment = f;
        }
    }

    public static class DialogLogin extends DialogFragment {
        Fragment fragment;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_login, null);
            final EditText mail = (EditText) view.findViewById(R.id.mail);
            final EditText pass = (EditText) view.findViewById(R.id.password);

            builder.setView(view)
                    .setTitle(R.string.login)
                    .setOnKeyListener(new DialogInterface.OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode,
                                             KeyEvent event) {
                            if (keyCode == KeyEvent.KEYCODE_BACK) {
                                getActivity().finish();
                                return true;
                            }
                            return false;
                        }
                    })
                            // Add action buttons
                    .setPositiveButton(R.string.login, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            if (mail.getText().length() != 0 && pass.getText().length() != 0
                                    && isValidEmail(mail.getText())) {
                                String mail_s = mail.getText().toString();
                                String pass_s = pass.getText().toString();
                                if (Global.login(mail_s, pass_s))
                                    Toast.makeText(getActivity().getApplicationContext(),
                                            R.string.login_success, Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(getActivity().getApplicationContext(),
                                            R.string.login_fail, Toast.LENGTH_SHORT).show();
                            } else {
                                if (mail.getText().length() == 0)
                                    Toast.makeText(getActivity().getApplicationContext(),
                                            R.string.falta_correo, Toast.LENGTH_SHORT).show();
                                else if (pass.getText().length() == 0)
                                    Toast.makeText(getActivity().getApplicationContext(),
                                            R.string.falta_pass, Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(getActivity().getApplicationContext(),
                                            R.string.login_fail, Toast.LENGTH_SHORT).show();
                            }

                            ((MapFragment) fragment).dialogLogin();
                            ((MapFragment) fragment).map.enablePosition();
                            ((MapFragment) fragment).map.setPositionMapFromRealPoint(Session.user.getPosicion());
                        }
                    })
                    .setNeutralButton(R.string.register, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            ((MapFragment) fragment).dialogRegister();
                        }
                    });
            return builder.create();
        }

        public void setParentFragment(Fragment f) {
            fragment = f;
        }
    }

    public static class DialogRegister extends DialogFragment {
        Fragment fragment;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_register, null);
            final EditText mail = (EditText) view.findViewById(R.id.mail);
            final EditText username = (EditText) view.findViewById(R.id.username);
            final EditText pass = (EditText) view.findViewById(R.id.password);
            final EditText cpass = (EditText) view.findViewById(R.id.cpassword);
            final EditText name = (EditText) view.findViewById(R.id.name);
            builder.setView(view)
                    .setTitle(R.string.register)
                    .setOnKeyListener(new DialogInterface.OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode,
                                             KeyEvent event) {
                            if (keyCode == KeyEvent.KEYCODE_BACK) {
                                getActivity().finish();
                                return true;
                            }
                            return false;
                        }
                    })
                            // Add action buttons
                    .setPositiveButton(R.string.register, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            if (mail.getText().length() != 0 && pass.getText().length() != 0
                                    && cpass.getText().length() != 0 && username.getText().length() != 0
                                    && isValidEmail(mail.getText())) {
                                String mail_s = mail.getText().toString();
                                String username_s = username.getText().toString();
                                String pass_s = pass.getText().toString();
                                String cpass_s = cpass.getText().toString();
                                String name_s = name.getText().toString();
                                if (Global.registrar(username_s, pass_s, cpass_s, mail_s, name_s))
                                    Toast.makeText(getActivity().getApplicationContext(),
                                            R.string.register_success, Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(getActivity().getApplicationContext(),
                                            R.string.register_fail, Toast.LENGTH_SHORT).show();
                            } else {
                                if (mail.getText().length() == 0)
                                    Toast.makeText(getActivity().getApplicationContext(),
                                            R.string.falta_correo, Toast.LENGTH_SHORT).show();
                                else if (pass.getText().length() == 0)
                                    Toast.makeText(getActivity().getApplicationContext(),
                                            R.string.falta_pass, Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(getActivity().getApplicationContext(),
                                            R.string.register_fail, Toast.LENGTH_SHORT).show();
                            }
                            ((MapFragment) fragment).dialogRegister();
                        }
                    })
                    .setNeutralButton(R.string.login, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            ((MapFragment) fragment).dialogLogin();
                        }
                    });
            return builder.create();
        }

        public void setParentFragment(Fragment f) {
            fragment = f;
        }
    }
}

