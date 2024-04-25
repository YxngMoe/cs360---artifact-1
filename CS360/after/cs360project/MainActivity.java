public class MainActivity extends AppCompatActivity {

    AppDatabase db;

    private static final int SMS_PERMISSION_REQUEST_CODE = 123;
    private static final int MIN_PASSWORD_LENGTH = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "users").build();

        // Check and request SMS permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_REQUEST_CODE);
        }
    }

    public void RegisterClick(View v) {
        EditText usernameText = findViewById(R.id.editTextUsername);
        EditText passwordText = findViewById(R.id.editTextTextPassword);
        String nameTextValue = usernameText.getText().toString();
        String passwordTextValue = passwordText.getText().toString();

        // Input validation
        if (nameTextValue.isEmpty() || passwordTextValue.isEmpty()) {
            // Show error message or toast indicating that fields are required
            return;
        }

        if (passwordTextValue.length() < MIN_PASSWORD_LENGTH) {
            // Show error message or toast indicating that password is too short
            return;
        }

        if (!checkLogin(nameTextValue, passwordTextValue)) {
            createUser(nameTextValue, passwordTextValue);
        }
    }

    public void LoginClick(View v) {
        EditText usernameText = findViewById(R.id.editTextUsername);
        EditText passwordText = findViewById(R.id.editTextTextPassword);
        String nameTextValue = usernameText.getText().toString();
        String passwordTextValue = passwordText.getText().toString();

        // Input validation
        if (nameTextValue.isEmpty() || passwordTextValue.isEmpty()) {
            // Show error message or toast indicating that fields are required
            return;
        }

        if (passwordTextValue.length() < MIN_PASSWORD_LENGTH) {
            // Show error message or toast indicating that password is too short
            return;
        }

        if (checkLogin(nameTextValue, passwordTextValue)) {
            setContentView(R.layout.main_display);
            // Check for SMS permission before sending alerts
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                sendSMSAlerts();
            }
        }
    }

    private boolean checkLogin(String username, String password) {
        UserDao userDao = db.userDao();
        User user = userDao.findByName(username, password);
        return user != null;
    }

    private void createUser(String username, String password) {
        UserDao userDao = db.userDao();
        User user = new User();
        user.userName = username;
        user.password = password;
        userDao.insertAll(user);
    }

    private void sendSMSAlerts() {
        // Implement your SMS alert logic here using SmsManager
        // This method will be called when the user logs in and SMS permission is granted
        SmsManager smsManager = SmsManager.getDefault();
        String phoneNumber = "1234567890"; // Replace with the actual phone number
        String message = "Your SMS alert message"; // Replace with the actual alert message
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
    }

    private void showPermissionDeniedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("SMS Permission Denied");
        builder.setMessage("Without SMS permission, you won't receive SMS alerts. Do you want to grant permission now?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Request SMS permission again
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User denied permission, handle accordingly
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with sending SMS alerts
                sendSMSAlerts();
            } else {
                // Permission denied, show dialog or take appropriate action
                showPermissionDeniedDialog();
            }
        }
    }
}
// Enhancement: Added input validation for username and password fields, including a minimum password length requirement.
