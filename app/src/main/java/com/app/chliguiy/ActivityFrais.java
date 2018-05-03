package com.app.chliguiy;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.chliguiy.adapter.AdapterShoppingCart;
import com.app.chliguiy.connection.API;
import com.app.chliguiy.connection.RestAdapter;
import com.app.chliguiy.connection.callbacks.CallbackRapport;
import com.app.chliguiy.data.SharedPref;
import com.app.chliguiy.model.Frais;
import com.app.chliguiy.model.Fraisequipe;
import com.app.chliguiy.model.RapportSubmit;
import com.app.chliguiy.model.rapport;
import com.app.chliguiy.utils.CallbackDialog;
import com.app.chliguiy.utils.DialogUtils;
import com.app.chliguiy.utils.Tools;
import com.balysv.materialripple.MaterialRippleLayout;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityFrais extends AppCompatActivity {

    private View parent_view;
    private Spinner shipping;
    private ImageButton bt_date_shipping;
    private TextView date_shipping;
    private RecyclerView recyclerView;
    private MaterialRippleLayout lyt_add_cart;
    private TextView total_order, tax, price_tax, total_fees;
    private TextInputLayout ville_lyt,hotel_lyt,parking_lyt,depart_km_lyt,retour_km_lyt,repas_lyt,remarque_lyt;
    private EditText ville,hotel,parking,depart_km,retour_km,repas,remarque;

    private DatePickerDialog datePickerDialog;
    private AdapterShoppingCart adapter;

    private Long date_ship_millis = 0L;
    private Double _total_fees = 0D;
    private String _total_fees_str;

    private SharedPref sharedPref;
    private Call<CallbackRapport> callbackCall = null;
    // construct dialog progress
    ProgressDialog progressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frais);

        sharedPref = new SharedPref(this);
        initToolbar();
        iniComponent();
    }

    private void initToolbar() {
        ActionBar actionBar;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(R.string.title_activity_frais);
        Tools.systemBarLolipop(this);
    }

    private void iniComponent() {
        parent_view = findViewById(android.R.id.content);
        lyt_add_cart = (MaterialRippleLayout) findViewById(R.id.lyt_add_cart);

        depart_km = (EditText) findViewById(R.id.depart_km);
        depart_km_lyt = (TextInputLayout) findViewById(R.id.depart_km_lyt);

        retour_km = (EditText) findViewById(R.id.retour_km);
        retour_km_lyt = (TextInputLayout) findViewById(R.id.retour_km_lyt);
        ville = (EditText) findViewById(R.id.ville);
        ville_lyt = (TextInputLayout) findViewById(R.id.ville_lyt);

        hotel = (EditText) findViewById(R.id.hotel);
        hotel_lyt = (TextInputLayout) findViewById(R.id.hotel_lyt);
        repas = (EditText) findViewById(R.id.repas);
        repas_lyt = (TextInputLayout) findViewById(R.id.repas_lyt);
        parking = (EditText) findViewById(R.id.parking);
        parking_lyt = (TextInputLayout) findViewById(R.id.parking_layout);
        remarque = (EditText) findViewById(R.id.remarque);
        remarque_lyt = (TextInputLayout) findViewById(R.id.remarque_lyt);




        bt_date_shipping = (ImageButton) findViewById(R.id.bt_date_shipping);
        date_shipping = (TextView) findViewById(R.id.date_shipping);

        progressDialog = new ProgressDialog(ActivityFrais.this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(R.string.title_please_wait);
        progressDialog.setMessage(getString(R.string.content_submit_checkout));

        bt_date_shipping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogDatePicker();
            }
        });

        lyt_add_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_frais, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_id = item.getItemId();
        if (item_id == android.R.id.home) {
            super.onBackPressed();
        }else if (item_id == R.id.action_frais) {
            Intent   i = new Intent(this, ActivityFraisList.class);
            startActivity(i);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    private void submitForm() {

        if (!validateDateShip()) {
            Snackbar.make(parent_view, R.string.invalid_date_ship, Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (!valideville()) {
            Snackbar.make(parent_view, R.string.invalid_ville, Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (!validedepart()) {
            Snackbar.make(parent_view, R.string.invalid_departkm, Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (!valideparking()) {
            Snackbar.make(parent_view, R.string.invalid_parking, Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (!valideretour()) {
            Snackbar.make(parent_view, R.string.invalid_retourkm, Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (!validehotel()) {
            Snackbar.make(parent_view, R.string.invalid_hotel, Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (!validerepas()) {
            Snackbar.make(parent_view, R.string.invalid_repas, Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (!valideremarque()) {
            Snackbar.make(parent_view, R.string.invalid_remarque, Snackbar.LENGTH_SHORT).show();
            return;
        }





        // hide keyboard
        hideKeyboard();

        // show dialog confirmation
        dialogConfirmCheckout();
    }

    private void submitOrderData() {
        // prepare checkout data
        // submit data to server
        Fraisequipe fraisequipe = new Fraisequipe();
        Frais frais=new Frais();
        frais.date=date_shipping.getText().toString();
        frais.user_id=sharedPref.getIdUser();
        frais.depart_km=depart_km.getText().toString();
        frais.retour_km=retour_km.getText().toString();
        frais.hotel=hotel.getText().toString();
        frais.remarque=remarque.getText().toString();
        frais.parking_peage=parking.getText().toString();
        frais.ville=ville.getText().toString();
        frais.repas=repas.getText().toString();
        fraisequipe.frais=frais;

        API api = RestAdapter.createAPI();
        callbackCall = api.submitfrais(fraisequipe);
        callbackCall.enqueue(new Callback<CallbackRapport>() {
            @Override
            public void onResponse(Call<CallbackRapport> call, Response<CallbackRapport> response) {
                CallbackRapport resp = response.body();
                if (resp != null && resp.status.equals("success")) {

                    //   db.saveOrder(order);
                    dialogSuccess("");
                } else {
                    dialogFailedRetry();
                }

            }

            @Override
            public void onFailure(Call<CallbackRapport> call, Throwable t) {
                Log.e("onFailure", t.getMessage());
                if (!call.isCanceled()) dialogFailedRetry();
            }
        });
    }

    // give delay when submit data to give good UX
    private void delaySubmitOrderData() {
        progressDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                submitOrderData();
            }
        }, 2000);
    }

    public void dialogConfirmCheckout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirmation);
        builder.setMessage(getString(R.string.confirm_checkout));
        builder.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                delaySubmitOrderData();
            }
        });
        builder.setNegativeButton(R.string.NO, null);
        builder.show();
    }

    public void dialogFailedRetry() {
        progressDialog.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.failed);
        builder.setMessage(getString(R.string.failed_checkout));
        builder.setPositiveButton(R.string.TRY_AGAIN, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                delaySubmitOrderData();
            }
        });
        builder.setNegativeButton(R.string.CANCEL, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(getApplicationContext(),ActivityRapport.class));
            }
        });
        builder.show();
    }

    public void dialogSuccess(String code) {
        progressDialog.dismiss();
        Dialog dialog = new DialogUtils(this).buildDialogInfo(
                getString(R.string.success_checkout),
              "Ajoute avec succée",
                getString(R.string.OK),
                R.drawable.img_checkout_success,
                new CallbackDialog() {
                    @Override
                    public void onPositiveClick(Dialog dialog) {
                        finish();
                        dialog.dismiss();
                    }

                    @Override
                    public void onNegativeClick(Dialog dialog) {
                    }
                });
        dialog.show();
    }

    private void dialogDatePicker() {
        Calendar cur_calender = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, R.style.DatePickerTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int _year, int _month, int _day) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, _year);
                calendar.set(Calendar.MONTH, _month);
                calendar.set(Calendar.DAY_OF_MONTH, _day);
                date_ship_millis = calendar.getTimeInMillis();
                date_shipping.setText(Tools.getFormattedDateSimple(date_ship_millis));
                datePickerDialog.dismiss();
            }
        }, cur_calender.get(Calendar.YEAR), cur_calender.get(Calendar.MONTH), cur_calender.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.setCancelable(true);
        //datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }


    private boolean valideremarque() {
        String str = remarque.getText().toString().trim();
        if (str.isEmpty()) {
            remarque_lyt.setError(getString(R.string.invalid_remarque));
            requestFocus(remarque);
            return false;
        } else {
            remarque_lyt.setErrorEnabled(false);
        }
        return true;
    }
    private boolean valideville() {
        String str = ville.getText().toString().trim();
        if (str.isEmpty()) {
            ville_lyt.setError(getString(R.string.invalid_ville));
            requestFocus(ville);
            return false;
        } else {
            ville_lyt.setErrorEnabled(false);
        }
        return true;
    }
    private boolean validehotel() {
        String str = hotel.getText().toString().trim();
        if (str.isEmpty()) {
            hotel_lyt.setError(getString(R.string.invalid_hotel));
            requestFocus(hotel);
            return false;
        } else {
            hotel_lyt.setErrorEnabled(false);
        }
        return true;
    }
    private boolean valideparking() {
        String str = parking.getText().toString().trim();
        if (str.isEmpty()) {
            parking_lyt.setError(getString(R.string.invalid_parking));
            requestFocus(parking);
            return false;
        } else {
            parking_lyt.setErrorEnabled(false);
        }
        return true;
    }
    private boolean validerepas() {
        String str = repas.getText().toString().trim();
        if (str.isEmpty()) {
            repas_lyt.setError(getString(R.string.invalid_repas));
            requestFocus(repas);
            return false;
        } else {
            repas_lyt.setErrorEnabled(false);
        }
        return true;
    }
    private boolean validedepart() {
        String str = depart_km.getText().toString().trim();
        if (str.isEmpty()) {
            depart_km_lyt.setError(getString(R.string.invalid_departkm));
            requestFocus(depart_km);
            return false;
        } else {
            depart_km_lyt.setErrorEnabled(false);
        }
        return true;
    }
    private boolean valideretour() {
        String str = retour_km.getText().toString().trim();
        if (str.isEmpty()) {
            retour_km_lyt.setError(getString(R.string.invalid_retourkm));
            requestFocus(retour_km);
            return false;
        } else {
            retour_km_lyt.setErrorEnabled(false);
        }
        return true;
    }





    private boolean validateShipping() {
        int pos = shipping.getSelectedItemPosition();
        if (pos == 0) {
            return false;
        }
        return true;
    }

    private boolean validateDateShip() {
        if (date_ship_millis == 0L) {
            return false;
        }
        return true;
    }


    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private class CheckoutTextWatcher implements TextWatcher {
        private View view;

        private CheckoutTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {

                    case R.id.remarque:
                    valideremarque();
                    break;
                    case R.id.ville:
                    valideville();
                    break;
                    case R.id.repas:
                    validerepas();
                    break;
                    case R.id.retour_km:
                    valideretour();
                    break;
                    case R.id.depart_km:
                    validedepart();
                    break;
                    case R.id.hotel:
                    validehotel();
                    break;
                case R.id.parking:
                    valideparking();
                    break;



            }
        }
    }


}
