package com.ttp.lstchatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.quickblox.chat.QBChatService;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.squareup.picasso.Picasso;
import com.ttp.lstchatapp.Common.Common;
import com.ttp.lstchatapp.Holder.QBUsersHolder;
import com.ttp.lstchatapp.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class UserProfile extends AppCompatActivity {

    EditText editPassword, editOldPassword, editFullName, editEmail, editPhone;
    Button btnUpdate, btnCancle;
    ImageView user_avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final ProgressDialog mDialog = new ProgressDialog(UserProfile.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Toolbar toolbar = findViewById(R.id.user_update_toolbar);
        toolbar.setTitle("프로필");
        setSupportActionBar(toolbar);

        initViews();
        loadUserProfile();
        user_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), Common.SELECT_PICTURE);
            }
        });

        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = editPassword.getText().toString();
                String oldPassword = editOldPassword.getText().toString();
                String email = editEmail.getText().toString();
                String phone = editPhone.getText().toString();
                String fullName = editFullName.getText().toString();

                QBUser user = new QBUser();
                user.setId(QBChatService.getInstance().getUser().getId());
                if (!Common.isNullOrEmptyString(oldPassword)) user.setOldPassword(oldPassword);
                if (!Common.isNullOrEmptyString(password)) user.setPassword(password);
                if (!Common.isNullOrEmptyString(email)) user.setEmail(email);
                if (!Common.isNullOrEmptyString(fullName)) user.setFullName(fullName);
                if (!Common.isNullOrEmptyString(phone)) user.setPhone(phone);

                mDialog.setMessage("wait..");
                mDialog.show();
                QBUsers.updateUser(user).performAsync(new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser qbUser, Bundle bundle) {
                        Toast.makeText(UserProfile.this, "User: " + qbUser.getLogin() + " updated", Toast.LENGTH_SHORT).show();
                        mDialog.dismiss();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Toast.makeText(UserProfile.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == Common.SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                final ProgressDialog mDialog = new ProgressDialog(UserProfile.this);
                mDialog.setMessage("Please wait...");
                mDialog.setCancelable(false);
                mDialog.show();

                //update user avatar

                try {
                    InputStream in = getContentResolver().openInputStream(selectedImageUri);
                    final Bitmap bitmap = BitmapFactory.decodeStream(in);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG,100,bos);
                    File file = new File(Environment.getExternalStorageDirectory()+"/myimage.png");
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(bos.toByteArray());
                    fos.flush();
                    fos.close();



                    //get file size
                    final int imageSizeKb = (int) file.length()/1024;
                    if(imageSizeKb>=(1024*100)){
                        Toast.makeText(this, "Error image size", Toast.LENGTH_SHORT).show();
                    }

                    //upload file to server
                    QBContent.uploadFileTask(file, true, null).performAsync(new QBEntityCallback<QBFile>() {
                        @Override
                        public void onSuccess(QBFile qbFile, Bundle bundle) {
                            //Set avatar for user
                            final QBUser user = new QBUser();
                            user.setId(QBChatService.getInstance().getUser().getId());
                            user.setFileId(Integer.parseInt(qbFile.getId().toString()));

                            //update user
                            QBUsers.updateUser(user).performAsync(new QBEntityCallback<QBUser>() {
                                @Override
                                public void onSuccess(QBUser qbUser, Bundle bundle) {
                                    mDialog.dismiss();
                                    user_avatar.setImageBitmap(bitmap);
                                }

                                @Override
                                public void onError(QBResponseException e) {

                                }
                            });
                        }

                        @Override
                        public void onError(QBResponseException e) {

                        }
                    });
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }
    }

    private void loadUserProfile() {

        //load avatar
        QBUsers.getUser(QBChatService.getInstance().getUser().getId())
                .performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                //save to cache
                QBUsersHolder.getInstance().putUser(qbUser);

                if(qbUser.getFileId() != null){
                    int profilePictureId = qbUser.getFileId();
                    QBContent.getFile(profilePictureId).performAsync(new QBEntityCallback<QBFile>() {
                        @Override
                        public void onSuccess(QBFile qbFile, Bundle bundle) {
                            String fileUrl = qbFile.getPublicUrl();
                            Picasso.with(getBaseContext()).load(fileUrl).into(user_avatar);

                        }

                        @Override
                        public void onError(QBResponseException e) {

                        }
                    });
                }
            }



            @Override
            public void onError(QBResponseException e) {

            }
        });

        QBUser currentUser=  QBChatService.getInstance().getUser();
        String fullName = currentUser.getFullName();
        String email = currentUser.getEmail();
        String phone = currentUser.getPhone();

        editEmail.setText(email);
        editFullName.setText(fullName);
        editPhone.setText(phone);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_update_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.user_update_log_out:
                logOut();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logOut() {
        QBUsers.signOut().performAsync(new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                QBChatService.getInstance().logout(new QBEntityCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid, Bundle bundle) {
                        Toast.makeText(UserProfile.this, "Log out.......!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UserProfile.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //remove all previous activities
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(QBResponseException e) {

                    }
                });
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });
    }

    private void initViews() {
        btnCancle = findViewById(R.id.update_user_btn_cancel);
        btnUpdate = findViewById(R.id.update_user_btn_update);

        editEmail = findViewById(R.id.update_edt_email);
        editOldPassword = findViewById(R.id.update_edt_old_password);
        editPhone = findViewById(R.id.update_edt_phone);
        editFullName = findViewById(R.id.update_edt_full_name);
        editPassword = findViewById(R.id.update_edt_password);

        user_avatar = findViewById(R.id.user_avater);


    }
}
