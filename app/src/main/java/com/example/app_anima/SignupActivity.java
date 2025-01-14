package com.example.app_anima;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    private EditText editTextEmail, editTextPassword, editTextPasswordCheck, editTextName, editTextPetName, editTextSpecie, editTextSex, editTextAge;
    private Button btnApply, btnValidate;
    private ImageButton btnBack;

    private String email, password, passwordCheck, name, petName, specie, sex, age;
    private boolean isValidated;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        editTextEmail = findViewById(R.id.et_email);
        editTextPassword = findViewById(R.id.et_pswd);
        editTextPasswordCheck = findViewById(R.id.et_chkpswd);
        editTextName = findViewById(R.id.et_name);
        editTextPetName = findViewById(R.id.et_petname);
        editTextSpecie = findViewById(R.id.et_petspecie);
        editTextSex = findViewById(R.id.et_petsex);
        editTextAge = findViewById(R.id.et_petage);
        btnApply = findViewById(R.id.btn_apply);
        btnBack = findViewById(R.id.btn_back);
        btnValidate = findViewById(R.id.btn_validate);
        isValidated = false;

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = editTextEmail.getText().toString();
                if (email.equals("")) {
                    AlertDialog builder = new AlertDialog.Builder(SignupActivity.this)
                            .setMessage("이메일을 입력하세요!")
                            .setPositiveButton("확인", null)
                            .show();
                    isValidated = false;
                } else {
                    ValidateRequest validateRequest = new ValidateRequest(email, validateResponseListener);
                    RequestQueue queue = Volley.newRequestQueue(SignupActivity.this);
                    queue.add(validateRequest);
                }
            }
        });

        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = editTextEmail.getText().toString();
                password = editTextPassword.getText().toString();
                passwordCheck = editTextPasswordCheck.getText().toString();
                name = editTextName.getText().toString();
                petName = editTextPetName.getText().toString();
                specie = editTextSpecie.getText().toString();
                sex = editTextSex.getText().toString();
                age = editTextAge.getText().toString();

                if (checkNull(email, password, passwordCheck, name)) {
                    if (passwordValidate(password, passwordCheck)) {
                        if (isValidated) {
                            if (checkPetNull(petName, specie, sex, age)) {
                                SignupRequest signupRequest = new SignupRequest(email, password, name, petName, specie, sex, age, responseListener);
                                RequestQueue queue = Volley.newRequestQueue(SignupActivity.this);
                                queue.add(signupRequest);
                            }
                        } else {
                            AlertDialog builder = new AlertDialog.Builder(SignupActivity.this)
                                    .setMessage("아이디 중복확인을 진행하세요")
                                    .setPositiveButton("확인", null)
                                    .show();
                        }
                    } else {
                        AlertDialog builder = new AlertDialog.Builder(SignupActivity.this)
                                .setMessage("비밀번호 확인이 일치하지 않습니다!")
                                .setPositiveButton("확인", null)
                                .show();
                    }
                }
            }
        });
    }

    private boolean passwordValidate(String password, String passwordCheck) {
        return passwordCheck.equals(password);
    }

    private boolean checkNull(String email, String password, String passwordCheck, String name) {
        if (email.equals("")) {
            AlertDialog builder = new AlertDialog.Builder(SignupActivity.this)
                    .setMessage("이메일을 입력하세요!")
                    .setPositiveButton("확인", null)
                    .show();
            return false;
        } else if (password.equals("")) {
            AlertDialog builder = new AlertDialog.Builder(SignupActivity.this)
                    .setMessage("비밀번호를 입력하세요!")
                    .setPositiveButton("확인", null)
                    .show();
            return false;
        } else if (passwordCheck.equals("")) {
            AlertDialog builder = new AlertDialog.Builder(SignupActivity.this)
                    .setMessage("비밀번호 확인을 입력하세요!")
                    .setPositiveButton("확인", null)
                    .show();
            return false;
        } else if (name.equals("")) {
            AlertDialog builder = new AlertDialog.Builder(SignupActivity.this)
                    .setMessage("이름을 입력하세요!")
                    .setPositiveButton("확인", null)
                    .show();
            return false;
        }
        return true;
    }

    private boolean checkPetNull(String name, String specie, String sex, String age) {
        if (name.equals("")) {
            AlertDialog builder = new AlertDialog.Builder(SignupActivity.this)
                    .setMessage("반려견 이름을 입력하세요!")
                    .setPositiveButton("확인", null)
                    .show();
            return false;
        } else if (specie.equals("")) {
            AlertDialog builder = new AlertDialog.Builder(SignupActivity.this)
                    .setMessage("종을 입력하세요!")
                    .setPositiveButton("확인", null)
                    .show();
            return false;
        } else if (sex.equals("")) {
            AlertDialog builder = new AlertDialog.Builder(SignupActivity.this)
                    .setMessage("성별을 입력하세요!")
                    .setPositiveButton("확인", null)
                    .show();
            return false;
        } else if (age.equals("")) {
            AlertDialog builder = new AlertDialog.Builder(SignupActivity.this)
                    .setMessage("나이를 입력하세요!")
                    .setPositiveButton("확인", null)
                    .show();
            return false;
        }
        return true;
    }

    Response.Listener<String> responseListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            try {
                Log.d("SignUp Result", response);
                boolean success = Boolean.parseBoolean(response);

                if (success) {
                    Toast.makeText(SignupActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    AlertDialog builder = new AlertDialog.Builder(SignupActivity.this)
                            .setMessage("회원가입 실패")
                            .setPositiveButton("확인", null)
                            .show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                AlertDialog builder = new AlertDialog.Builder(SignupActivity.this)
                        .setMessage("ERROR!")
                        .setPositiveButton("확인", null)
                        .show();
            }
        }
    };

    Response.Listener<String> validateResponseListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            try {
                Log.d("Validate Result", response);
                boolean success = Boolean.parseBoolean(response);

                if (success) {
                    AlertDialog builder = new AlertDialog.Builder(SignupActivity.this)
                            .setMessage("사용가능한 이메일 입니다.")
                            .setPositiveButton("확인", null)
                            .show();
                    isValidated = true;
                    btnValidate.setText("완료");
                    editTextEmail.setEnabled(false);
                    btnValidate.setEnabled(false);
                } else {
                    AlertDialog builder = new AlertDialog.Builder(SignupActivity.this)
                            .setMessage("이미 가입된 이메일 입니다.")
                            .setPositiveButton("확인", null)
                            .show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                AlertDialog builder = new AlertDialog.Builder(SignupActivity.this)
                        .setMessage("ERROR!")
                        .setPositiveButton("확인", null)
                        .show();
            }
        }
    };
}

class SignupRequest extends StringRequest {
    private final static String URL = "http://167.179.103.235/signup.php";
    private Map<String, String> map;

    public SignupRequest(String email, String password, String name, String petName, String specie, String sex, String age, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("email", email);
        map.put("pswd", password);
        map.put("name", name);
        map.put("petname", petName);
        map.put("specie", specie);
        map.put("sex", sex);
        map.put("age", age);
    }

    @Override
    protected Map<String, String>getParams() throws AuthFailureError {
        return map;
    }
}

class ValidateRequest extends StringRequest {
    private final static String URL = "http://167.179.103.235/uservalidate.php";
    private Map<String, String> map;

    public ValidateRequest(String email, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("email", email);
    }

    @Override
    protected Map<String, String>getParams() throws AuthFailureError {
        return map;
    }
}