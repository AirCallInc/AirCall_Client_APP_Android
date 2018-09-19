package com.aircall.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aircall.app.Common.CameraDialogFragment;
import com.aircall.app.Common.ErrorMessages;
import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Common.RoundedTransformation;
import com.aircall.app.Common.ServiceGenerator;
import com.aircall.app.Dialog.SingleButtonAlert;
import com.aircall.app.Dialog.UpdateUsernameFragment;
import com.aircall.app.Interfaces.DialogInterfaceClick;
import com.aircall.app.Interfaces.UserProfileInterface;
import com.aircall.app.Interfaces.UsernameDialogInterfaceWithCompany;
import com.aircall.app.Interfaces.WebserviceApi;
import com.aircall.app.Model.UpdateUserProfile.ChangeUserNameResponce;
import com.aircall.app.Model.UpdateUserProfile.UpdateUserProfileResponce;
import com.aircall.app.R;
import com.aircall.app.UserProfileActivity;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class UserProfileFragment extends Fragment {

    GlobalClass globalClass;
    Activity activity;
    Context context;
    String strFragmentTag;


    @Bind(R.id.ivBack)
    ImageView ivBack;

    @Bind(R.id.ivChangeUserName)
    ImageView ivChangeUserName;

    @Bind(R.id.ivUserImage)
    ImageView ivUserImage;

    @Bind(R.id.tvUserName)
    TextView tvUserName;

    @Bind(R.id.tvUserEmail)
    TextView tvUserEmail;

    @Bind(R.id.tvCompany)
    TextView tvCompany;

    @Bind(R.id.tvUserPassword)
    TextView tvUserPassword;

    @Bind(R.id.llUpdateContactNumber)
    LinearLayout llUpdateContactNumber;

    @Bind(R.id.llUpdatePaymentMethod)
    LinearLayout llUpdatePaymentMethod;

    @Bind(R.id.llUpdateBillingHistory)
    LinearLayout llUpdateBillingHistory;

    @Bind(R.id.tvSubmitUserProfileUpdate)
    TextView tvSubmitUserProfileUpdate;

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    UserProfileInterface userProfileInterface;
    private String firstName = "", lastName = "",companyName="";
    File filepro;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        ButterKnife.bind(this, view);

        init();
        clickEvents();
        return view;
    }

    public void init() {
        globalClass = ((UserProfileActivity) activity).globalClass;
        sharedpreferences = getActivity().getSharedPreferences(globalClass.PreferenceName, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        if (globalClass.checkInternetConnection()) {
            getUserProfileDetails();
        } else {
            DialogFragment ds = new SingleButtonAlert(ErrorMessages.NoInternet,
                    new DialogInterfaceClick() {
                        @Override
                        public void dialogClick(String tag) {

                        }
                    });
            ds.setCancelable(false);
            ds.show(getFragmentManager(), "");
        }
    }

    public void clickEvents() {

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onBackPressed();
            }
        });

        tvUserPassword.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                userProfileInterface.changeFragment("ChangePassword", true);
            }
        });

        llUpdateContactNumber.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                userProfileInterface.changeFragment("ContactUpdate", true);
            }
        });

        ivChangeUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /**
                 * Use UpdateUsernameFragment Dialog for change and update user's first name and last name.
                 *
                 * Also set names font size as per length of users name
                 */
                // Change UsernameDialogInteface to UsernameDialogInterfaceWithCompany for add company name
                DialogFragment ds = new UpdateUsernameFragment(globalClass, firstName, lastName, companyName,new UsernameDialogInterfaceWithCompany() {
                    @Override
                    public void submitClick(String firstname, String lastname, String companyname) {
                        ((UserProfileActivity) activity).updateUserProfileData.FirstName = firstname;
                        ((UserProfileActivity) activity).updateUserProfileData.LastName = lastname;
                        //Add CompanyName to updateUserProfileData Model Class
                        ((UserProfileActivity) activity).updateUserProfileData.Company = companyname;
                        firstName = firstname;
                        lastName = lastname;
                        companyName = companyname;
                        String name = firstname + " " + lastname;
                        tvUserName.setText(name);
                        tvCompany.setText(companyName);
                        Log.e("Company from Dialog==",companyName);
                        if (name.length() <= 20) {
                            tvUserName.setTextSize(TypedValue.COMPLEX_UNIT_PX, dpToPx(20));
                        }
                        if (name.length() > 20) {
                            tvUserName.setTextSize(TypedValue.COMPLEX_UNIT_PX, dpToPx(16));
                        }
                        if (name.length() > 25) {
                            tvUserName.setTextSize(TypedValue.COMPLEX_UNIT_PX, dpToPx(14));
                        }
                    }
                });
                ds.setCancelable(false);
                ds.show(getFragmentManager(), "");
            }
        });

        llUpdatePaymentMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userProfileInterface.changeFragment("PaymentMethod", true);
            }
        });

        llUpdateBillingHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userProfileInterface.changeFragment("BillingHistory", true);
            }
        });

        tvSubmitUserProfileUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!globalClass.checkInternetConnection()) {
                    DialogFragment ds = new SingleButtonAlert(ErrorMessages.NoInternet,
                            new DialogInterfaceClick() {
                                @Override
                                public void dialogClick(String tag) {

                                }
                            });
                    ds.setCancelable(false);
                    ds.show(getFragmentManager(), "");
                } else {
                    updateUserName();
                }

            }
        });

        ivUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 9);
                    return;

                } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 10);
                    return;

                } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 11);
                    return;

                } else {
                    fatchImage();
                }
            }
        });
    }


    /**
     * Web API call for get user profile detail.
     */
    private void getUserProfileDetails() {

        ((UserProfileActivity) activity).showProgressDailog("Please Wait...");
        final WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.updateUserProfile(sharedpreferences.getString("Id", ""), new Callback<UpdateUserProfileResponce>() {

            @Override
            public void success(UpdateUserProfileResponce updateUserProfileResponce, Response response) {
                ((UserProfileActivity) activity).hideProgressDialog();

                if (updateUserProfileResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                    if (!updateUserProfileResponce.Token.equalsIgnoreCase("")) {
                        editor.putString("Token", updateUserProfileResponce.Token);
                        editor.apply();
                    }

                    String imagePath = sharedpreferences.getString("ProfileImage", "");

                    if (!imagePath.equalsIgnoreCase("")) {
                        Picasso.with(getActivity())
                                .load(imagePath)
                                .resize(400, 400)
                                .transform(new RoundedTransformation(1700, 0))
                                .placeholder(R.drawable.placeholder_img)
                                .into(ivUserImage);
                    }

                    String name = updateUserProfileResponce.Data.FirstName + " " + updateUserProfileResponce.Data.LastName;
                    tvUserName.setText(name);
                    if (name.length() <= 20) {
                        tvUserName.setTextSize(TypedValue.COMPLEX_UNIT_PX, dpToPx(20));
                    }

                    if (name.length() > 20) {
                        tvUserName.setTextSize(TypedValue.COMPLEX_UNIT_PX, dpToPx(16));
                    }

                    if (name.length() > 25) {
                        tvUserName.setTextSize(TypedValue.COMPLEX_UNIT_PX, dpToPx(14));
                    }

                    tvUserEmail.setText(updateUserProfileResponce.Data.Email);

                    tvUserPassword.setText("12345678");
                    firstName = updateUserProfileResponce.Data.FirstName;
                    lastName = updateUserProfileResponce.Data.LastName;
                    companyName = updateUserProfileResponce.Data.Company;
                    if (companyName != null)
                    {
                       tvCompany.setText(companyName);
                    }
                    ((UserProfileActivity) activity).strUserPassword = updateUserProfileResponce.Data.Password;
                    ((UserProfileActivity) activity).updateUserProfileData = updateUserProfileResponce.Data;

                } else if (updateUserProfileResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
                    DialogFragment ds = new SingleButtonAlert(ErrorMessages.TockenExpired,
                            new DialogInterfaceClick() {
                                @Override
                                public void dialogClick(String tag) {
                                    globalClass.Clientlogout();
                                }
                            });
                    ds.setCancelable(true);
                    ds.show(getFragmentManager(), "");
                } else {
                    Log.e("Success", "else " + updateUserProfileResponce.StatusCode);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ((UserProfileActivity) activity).hideProgressDialog();
                DialogFragment ds = new SingleButtonAlert(ErrorMessages.ServerError,
                        new DialogInterfaceClick() {
                            @Override
                            public void dialogClick(String tag) {
                                activity.finish();
                            }
                        });
                ds.setCancelable(true);
                ds.show(getFragmentManager(), "");

                Log.e("failor", "failor " + error.getMessage());
            }
        });
    }


    /**
     * Web API call when user click on submit button for update user first name, last name and profile image
     */
    private void updateUserName() {
        ((UserProfileActivity) activity).showProgressDailog("Please Wait...");

        TypedFile typedImage = null;
        if (filepro == null) {
            typedImage = null;
        } else {
            typedImage = new TypedFile("image/*", filepro);
        }

        final WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));


        Log.e("Update Company name==",((UserProfileActivity) activity).updateUserProfileData.Company);
        webserviceApi.updateUsername(sharedpreferences.getString("Id", ""), ((UserProfileActivity) activity).updateUserProfileData.FirstName,
                ((UserProfileActivity) activity).updateUserProfileData.LastName,((UserProfileActivity) activity).updateUserProfileData.Company, typedImage, new Callback<ChangeUserNameResponce>() {
                    @Override
                    public void success(ChangeUserNameResponce changeUserNameResponce, Response response) {
                        ((UserProfileActivity) activity).hideProgressDialog();

                        if (changeUserNameResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                            if (!changeUserNameResponce.Token.equalsIgnoreCase("")) {
                                editor.putString("Token", changeUserNameResponce.Token);
                            }
                            editor.putString("FirstName", changeUserNameResponce.Data.FirstName);
                            editor.putString("LastName", changeUserNameResponce.Data.LastName);
                            editor.putString("Company",changeUserNameResponce.Data.Company);
                            editor.putString("ProfileImage", changeUserNameResponce.Data.Image);
                            editor.apply();

                            try {
                                if (filepro != null) {
                                    filepro.delete();
                                }
                            } catch (Exception ex) {
                                Log.e("Exception", "Is " + ex);
                            }
                            Toast.makeText(activity, changeUserNameResponce.Message, Toast.LENGTH_LONG).show();
                            activity.onBackPressed();

                        } else if (changeUserNameResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
                            DialogFragment ds = new SingleButtonAlert(ErrorMessages.TockenExpired,
                                    new DialogInterfaceClick() {
                                        @Override
                                        public void dialogClick(String tag) {
                                            globalClass.Clientlogout();
                                        }
                                    });
                            ds.setCancelable(true);
                            ds.show(getFragmentManager(), "");
                        } else {
                            Log.e("Success", "else " + changeUserNameResponce.StatusCode);
                            DialogFragment ds = new SingleButtonAlert(changeUserNameResponce.Message,
                                    new DialogInterfaceClick() {
                                        @Override
                                        public void dialogClick(String tag) {
                                            activity.finish();
                                        }
                                    });
                            ds.setCancelable(true);
                            ds.show(getFragmentManager(), "");
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        ((UserProfileActivity) activity).hideProgressDialog();
                        DialogFragment ds = new SingleButtonAlert(ErrorMessages.ServerError,
                                new DialogInterfaceClick() {
                                    @Override
                                    public void dialogClick(String tag) {
                                        activity.finish();
                                    }
                                });
                        ds.setCancelable(true);
                        ds.show(getFragmentManager(), "");

                        Log.e("failor", "failor " + error.getMessage());
                    }
                });

    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }


    /**
     * Fetch image from camera or gallery
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {


            File img_url = new File(globalClass.ExternalStorageDirectoryPath + "pro_image.jpg");


            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(img_url.getAbsolutePath(), options);

            bitmap = getResizedBitmap(bitmap, 300);
            try {

                //convert file to bitmap

                FileOutputStream fOut = new FileOutputStream(img_url);

                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                fOut.flush();
                fOut.close();

                try {
                    //Uri tempUri = getImageUri(bitmap);
                    try {
                        copyFile(bitmap, "pro_image.jpg");
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    filepro = new File(globalClass.ExternalStorageDirectoryPath + "pro_image.jpg");

                    Picasso.with(getActivity()).load(filepro).noFade().resize(150, 150)
                            .centerCrop().transform(new RoundedTransformation(1700, 0))
                            .skipMemoryCache().into(ivUserImage);


                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {

            }


        } else if (requestCode == 1 && resultCode == Activity.RESULT_OK
                && null != data) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(
                        getActivity().getContentResolver(), selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bitmap scaled = getResizedBitmap(bitmap, 300);
            //selectedImage = getImageUri(scaled);
            try {
                copyFile(scaled, "pro_image.jpg");
            } catch (IOException e) {
                e.printStackTrace();
            }
            Picasso.with(getActivity()).load(selectedImage).resize(150, 150)
                    .centerCrop().transform(new RoundedTransformation(1700, 0))
                    .skipMemoryCache().into(ivUserImage);


            filepro = new File(globalClass.ExternalStorageDirectoryPath + "pro_image.jpg");
        }
    }


    public void copyFile(Bitmap bitmap, String string) throws IOException {
        File file = new File(globalClass.ExternalStorageDirectoryPath, string);
        if (file.exists()) {
            file.delete();
        }
        OutputStream out = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, out);
        out.close();
        Intent resultIntent = new Intent();
        getActivity().setResult(Activity.RESULT_OK, resultIntent);
    }

    public Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), inImage,
                "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {

        maxSize = 300;

        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }


    /**
     * Check permission before fetch image is given by user or not for marshmallow and above users
     * and if not given then ask for permission and move further
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 9: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    fatchImage();
                } else {
                    boolean showRationale = true;
                    for (int i = 0; i < grantResults.length; i++) {
                        if (!shouldShowRequestPermissionRationale(permissions[i])) {
                            showRationale = false;
                        }
                    }
                    if (!showRationale) {
                        DialogFragment ds = new SingleButtonAlert(ErrorMessages.BothPermissionsRequired,
                                new DialogInterfaceClick() {
                                    @Override
                                    public void dialogClick(String tag) {
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                                        intent.setData(uri);
                                        startActivityForResult(intent, 100);
                                    }
                                });
                        ds.setCancelable(true);
                        ds.show(getFragmentManager(), "");
                    } else {
                        Toast.makeText(activity, ErrorMessages.GiveBothAccess, Toast.LENGTH_LONG).show();
                    }

                }
                return;
            }

            case 10: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fatchImage();
                } else {
                    boolean showRationale = true;
                    for (int i = 0; i < grantResults.length; i++) {
                        if (!shouldShowRequestPermissionRationale(permissions[i])) {
                            showRationale = false;
                        }
                    }
                    if (!showRationale) {
                        DialogFragment ds = new SingleButtonAlert(ErrorMessages.CameraPermissionsRequired,
                                new DialogInterfaceClick() {
                                    @Override
                                    public void dialogClick(String tag) {
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                                        intent.setData(uri);
                                        startActivityForResult(intent, 100);
                                    }
                                });
                        ds.setCancelable(true);
                        ds.show(getFragmentManager(), "");
                    } else {
                        Toast.makeText(activity, ErrorMessages.GiveCameraAccess, Toast.LENGTH_LONG).show();
                    }
                }
                return;
            }

            case 11: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fatchImage();
                } else {
                    boolean showRationale = true;
                    for (int i = 0; i < grantResults.length; i++) {
                        if (!shouldShowRequestPermissionRationale(permissions[i])) {
                            showRationale = false;
                        }
                    }
                    if (!showRationale) {
                        DialogFragment ds = new SingleButtonAlert(ErrorMessages.ExternalStoragePermissionsRequired,
                                new DialogInterfaceClick() {
                                    @Override
                                    public void dialogClick(String tag) {
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                                        intent.setData(uri);
                                        startActivityForResult(intent, 100);
                                    }
                                });
                        ds.setCancelable(true);
                        ds.show(getFragmentManager(), "");
                    } else {
                        Toast.makeText(activity, ErrorMessages.GivestorageAccess, Toast.LENGTH_LONG).show();
                    }
                }
                return;
            }

            // other 'switch' lines to check for other
            // permissions this app might request
        }
    }

    private void fatchImage() {
        DialogFragment ds = new CameraDialogFragment(
                new DialogInterfaceClick() {
                    @Override
                    public void dialogClick(String tag) {
                        if (tag.equals("Camera")) {

                            Intent cameraIntent = new Intent(
                                    MediaStore.ACTION_IMAGE_CAPTURE);
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(globalClass.ExternalStorageDirectoryPath + "pro_image.jpg")));
                            startActivityForResult(cameraIntent, 0);
                        } else {

                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_PICK);
                            startActivityForResult(Intent
                                    .createChooser(intent,
                                            "Choose Profile Pic"), 1);
                        }
                    }
                });
        ds.show(getFragmentManager(), "");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        userProfileInterface = ((UserProfileActivity) activity);
        globalClass = ((UserProfileActivity) activity).globalClass;

    }
}
