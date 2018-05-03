package com.app.chliguiy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.app.chliguiy.R;
import com.app.chliguiy.connection.API;
import com.app.chliguiy.connection.RestAdapter;
import com.app.chliguiy.connection.callbacks.CallbackClient;
import com.app.chliguiy.connection.callbacks.CallbackUser;
import com.app.chliguiy.data.SharedPref;
import com.app.chliguiy.utils.NetworkCheck;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login_Fragment extends Fragment implements OnClickListener {
	private static View view;

	private static EditText emailid, password;
	private static Button loginButton;
	private static TextView forgotPassword, signUp;
	private static CheckBox show_hide_password;
	private static LinearLayout loginLayout;
	private static Animation shakeAnimation;
	private static FragmentManager fragmentManager;

	private Call<CallbackUser> callbackCall = null;
	public Login_Fragment() {

	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.login_layout, container, false);
		initViews();
		setListeners();
		return view;
	}

	// Initiate Views
	private void initViews() {
		fragmentManager = getActivity().getSupportFragmentManager();

		emailid = (EditText) view.findViewById(R.id.login_emailid);
		password = (EditText) view.findViewById(R.id.login_password);
		loginButton = (Button) view.findViewById(R.id.loginBtn);
		forgotPassword = (TextView) view.findViewById(R.id.forgot_password);
		show_hide_password = (CheckBox) view
				.findViewById(R.id.show_hide_password);
		loginLayout = (LinearLayout) view.findViewById(R.id.login_layout);

		// Load ShakeAnimation
		shakeAnimation = AnimationUtils.loadAnimation(getActivity(),
				R.anim.shake);

		// Setting text selector over textviews
		@SuppressLint("ResourceType") XmlResourceParser xrp = getResources().getXml(R.drawable.text_selector);
		try {
			ColorStateList csl = ColorStateList.createFromXml(getResources(),
					xrp);

			forgotPassword.setTextColor(csl);
			show_hide_password.setTextColor(csl);
			signUp.setTextColor(csl);
		} catch (Exception e) {
		}
	}

	// Set Listeners
	private void setListeners() {
		loginButton.setOnClickListener(this);
		forgotPassword.setOnClickListener(this);


		// Set check listener over checkbox for showing and hiding password
		show_hide_password
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton button,
							boolean isChecked) {

						// If it is checkec then show password else hide
						// password
						if (isChecked) {

							show_hide_password.setText(R.string.hide_pwd);// change
																			// checkbox
																			// text

							password.setInputType(InputType.TYPE_CLASS_TEXT);
							password.setTransformationMethod(HideReturnsTransformationMethod
									.getInstance());// show password
						} else {
							show_hide_password.setText(R.string.show_pwd);// change
																			// checkbox
																			// text

							password.setInputType(InputType.TYPE_CLASS_TEXT
									| InputType.TYPE_TEXT_VARIATION_PASSWORD);
							password.setTransformationMethod(PasswordTransformationMethod
									.getInstance());// hide password

						}

					}
				});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.loginBtn:
			checkValidation();
			break;

		case R.id.forgot_password:

			// Replace forgot password fragment with animation
			fragmentManager
					.beginTransaction()
					.setCustomAnimations(R.anim.right_enter, R.anim.left_out)
					.replace(R.id.frameContainer,
							new ForgotPassword_Fragment(),
							Utils.ForgotPassword_Fragment).commit();
			break;

		}

	}
	private void requestLogin(final String Username,final String Password) {
		API api = RestAdapter.createAPI();
		callbackCall = api.Login(Username,Password );
		callbackCall.enqueue(new Callback<CallbackUser>() {


			@Override
			public void onResponse(Call<CallbackUser> call, Response<CallbackUser> response) {
				CallbackUser resp = response.body();
				if (resp != null && resp.status.equals("success")) {

					Toast.makeText(getActivity(), "Do Login.", Toast.LENGTH_SHORT)
							.show();
					Intent intent = new Intent(getActivity(), ActivityMain.class);
					intent.putExtra("id",resp.user.id);
					SharedPref pref=new SharedPref(getContext());
					pref.SetIdUser(String.valueOf(resp.user.id));
					startActivity(intent);
				} else {

					Toast.makeText(getActivity(), "Login or mot de passe inccorect.", Toast.LENGTH_SHORT)
							.show();
				}
			}

			@Override
			public void onFailure(Call<CallbackUser> call, Throwable t) {
				Log.e("onFailure", t.getMessage());
				Toast.makeText(getActivity(), "Connection failded.", Toast.LENGTH_SHORT)
						.show();
			}

		});
	}





	// Check Validation before login
	private void checkValidation() {
		// Get email id and password
		String getEmailId = emailid.getText().toString();
		String getPassword = password.getText().toString();

		// Check patter for email id
		//Pattern p = Pattern.compile(Utils.regEx);

		//Matcher m = p.matcher(getEmailId);

		// Check for both field is empty or not
		if (getEmailId.equals("") || getEmailId.length() == 0
				|| getPassword.equals("") || getPassword.length() == 0) {
			loginLayout.startAnimation(shakeAnimation);
			new CustomToast().Show_Toast(getActivity(), view,
					"Enter both credentials.");

		}
		// Check if email id is valid or not
		//	else if (!m.find())
		//		new CustomToast().Show_Toast(getActivity(), view,
		//				"Your Email Id is Invalid.");
		// Else do login and do your stuff}

		else
		{

			requestLogin(getEmailId,getPassword);
		}
	}
}
