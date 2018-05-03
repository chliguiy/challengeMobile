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
import com.app.chliguiy.model.Plan;
import com.app.chliguiy.model.PlanAction;
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

public class ActivityPlan extends AppCompatActivity {
    private View parent_view;
    private Spinner shipping;
    private ImageButton bt_date_shipping;
    private TextView date_shipping;
    private RecyclerView recyclerView;
    private MaterialRippleLayout lyt_add_cart;
    private TextView total_order, tax, price_tax, total_fees;
    private TextInputLayout phone_lyt, contact_lyt, remarque_lyt, client_lyt,commande_lyt;
    private EditText phone, contact, remarque, client,commande;

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
        setContentView(R.layout.activity_plan);
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
        actionBar.setTitle(R.string.title_activity_plan);
        Tools.systemBarLolipop(this);
    }

    private void iniComponent() {
        parent_view = findViewById(android.R.id.content);
        lyt_add_cart = (MaterialRippleLayout) findViewById(R.id.lyt_add_cart);
        remarque = (EditText) findViewById(R.id.ramarque);
        remarque_lyt = (TextInputLayout) findViewById(R.id.ramarque_lyt);
        client = (EditText) findViewById(R.id.client);
        client_lyt = (TextInputLayout) findViewById(R.id.client_lyt);
        commande = (EditText) findViewById(R.id.commande);
        commande_lyt = (TextInputLayout) findViewById(R.id.commande_lyt);
        contact = (EditText) findViewById(R.id.contact);
        contact_lyt = (TextInputLayout) findViewById(R.id.contact_lyt);
        phone = (EditText) findViewById(R.id.phone);
        phone_lyt = (TextInputLayout) findViewById(R.id.phone_lyt);

        bt_date_shipping = (ImageButton) findViewById(R.id.bt_date_shipping);
        date_shipping = (TextView) findViewById(R.id.date_shipping);
        progressDialog = new ProgressDialog(ActivityPlan.this);
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
        getMenuInflater().inflate(R.menu.menu_activity_plan, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_id = item.getItemId();
        if (item_id == android.R.id.home) {
            super.onBackPressed();
        }else if (item_id == R.id.action_plan) {
            Intent   i = new Intent(this, ActivityPlanList.class);
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
        if (!valideclient()) {
            Snackbar.make(parent_view, R.string.invalid_client, Snackbar.LENGTH_SHORT).show();
            return;
        }  if (!validecommande()) {
            Snackbar.make(parent_view, R.string.invalid_commande, Snackbar.LENGTH_SHORT).show();
            return;
        }  if (!validecontact()) {
            Snackbar.make(parent_view, R.string.invalid_contact, Snackbar.LENGTH_SHORT).show();
            return;
        }  if (!valideremarque()) {
            Snackbar.make(parent_view, R.string.invalid_remarque, Snackbar.LENGTH_SHORT).show();
            return;
        }  if (!validerephone()) {
            Snackbar.make(parent_view, R.string.invalid_phone, Snackbar.LENGTH_SHORT).show();
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
        PlanAction planAction = new PlanAction();
        Plan plan=new Plan();
        plan.date=date_shipping.getText().toString();
        plan.user_id=sharedPref.getIdUser();
        plan.remarque=remarque.getText().toString();
        plan.client=client.getText().toString();
        plan.contact=contact.getText().toString();
        plan.commande=commande.getText().toString();
        plan.contact=contact.getText().toString();
        plan.phone=phone.getText().toString();

        planAction.plan=plan;
        API api = RestAdapter.createAPI();
        callbackCall = api.submitplan(planAction);
        callbackCall.enqueue(new Callback<CallbackRapport>() {
            @Override
            public void onResponse(Call<CallbackRapport> call, Response<CallbackRapport> response) {
                CallbackRapport resp = response.body();
                if (resp != null && resp.status.equals("success")) {
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
                "Ajoute Avec succées.",
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
    private boolean validerephone() {
        String str = phone.getText().toString().trim();
        if (str.isEmpty()) {
            phone_lyt.setError(getString(R.string.invalid_phone));
            requestFocus(phone);
            return false;
        } else {
            phone_lyt.setErrorEnabled(false);
        }
        return true;
    }
    private boolean valideclient() {
        String str = client.getText().toString().trim();
        if (str.isEmpty()) {
            client_lyt.setError(getString(R.string.invalid_client));
            requestFocus(client);
            return false;
        } else {
            client_lyt.setErrorEnabled(false);
        }
        return true;
    }
    private boolean validecontact() {
        String str = contact.getText().toString().trim();
        if (str.isEmpty()) {
            contact_lyt.setError(getString(R.string.invalid_contact));
            requestFocus(contact);
            return false;
        } else {
            contact_lyt.setErrorEnabled(false);
        }
        return true;
    }
    private boolean validecommande() {
        String str = commande.getText().toString().trim();
        if (str.isEmpty()) {
            commande_lyt.setError(getString(R.string.invalid_commande));
            requestFocus(commande);
            return false;
        } else {
            commande_lyt.setErrorEnabled(false);
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

                case R.id.ramarque:
                    valideremarque();
                    break;
                case R.id.client:
                    valideclient();
                    break;
                    case R.id.commande:
                    validecommande();
                    break;
                    case R.id.contact:
                    validecontact();
                    break;
                    case R.id.phone:
                    validerephone();
                    break;



            }
        }
    }


}
