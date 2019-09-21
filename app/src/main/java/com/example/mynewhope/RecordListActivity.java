  package com.example.mynewhope;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.Button;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

  public class RecordListActivity extends AppCompatActivity {

    ListView mListView;
    ArrayList<Model> mList;
    RecordListAdapter mAdapter = null;

    ImageView imageViewIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Record List");
        //to enable back button in actionbar set parent activity main activity in manifest

        mListView = findViewById(R.id.listView);
        mList = new ArrayList<>();
        mAdapter = new RecordListAdapter(this,R.layout.row, mList);
        mListView.setAdapter(mAdapter);

        //get all data from sqlite
        Cursor cursor = MainActivity.mSQLiteHelper.getData("SELECT*FROM RECORD");
        mList.clear();
        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String phone = cursor.getString(2);
            String address = cursor.getString(3);
            String email = cursor.getString(4);
            String category = cursor.getString(5);
            byte[] image = cursor.getBlob(6);
            //add to list
            mList.add(new Model(id, name, phone, address, email, category, image));

        }

        mAdapter.notifyDataSetChanged();
        if (mList.size()==0){
            Toast.makeText(this, "No record found...", Toast.LENGTH_SHORT).show();
        }
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                CharSequence[] items = {"Update","Delete"};

                AlertDialog.Builder dialog = new AlertDialog.Builder(RecordListActivity.this);

                dialog.setTitle("Choose an action");
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i == 0){
                            Cursor c = MainActivity.mSQLiteHelper.getData("SELECT id FROM RECORD");
                            ArrayList<Integer> arrID = new ArrayList<Integer>();
                            while (c.moveToNext()){
                                arrID.add(c.getInt(0));
                            }
                            //show update dialog
                            showDialogUpdate(RecordListActivity.this, arrID.get(position));

                        }
                        if(i == 1){
                            //delete
                            Cursor c = MainActivity.mSQLiteHelper.getData("SELECT id FROM RECORD");
                            ArrayList<Integer> arrID = new ArrayList<Integer>();
                            while (c.moveToNext()){
                                arrID.add(c.getInt(0));
                            }
                            showDialogDelete(arrID.get(position));

                        }
                    }
                });
                dialog.show();
                return true;
            }
        });
    }

      private void showDialogDelete(final int idRecord) {
        AlertDialog.Builder dialogDelete = new AlertDialog.Builder(RecordListActivity.this);
        dialogDelete.setTitle("Warning!!");
        dialogDelete.setMessage("Are you sure to delete");
        dialogDelete.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    MainActivity.mSQLiteHelper.deleteData(idRecord);
                    Toast.makeText(RecordListActivity.this, "Delete Successfully", Toast.LENGTH_SHORT).show();

                }
                catch (Exception e){
                    Log.e("error",e.getMessage());

                }
                updateRecordList();

            }
        });
        dialogDelete.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialogDelete.show();


      }

      private void  showDialogUpdate(Activity activity, final int position){
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.update_dialog);
        dialog.setTitle("Update");
//                                                       //imageViewRecord
        imageViewIcon = dialog.findViewById(R.id.imageViewRecord);
        final EditText edtName = dialog.findViewById(R.id.imageViewRecord);
        final EditText edtPhone = dialog.findViewById(R.id.imageViewRecord);
        final EditText edtAddress = dialog.findViewById(R.id.imageViewRecord);
        final EditText edtEmail = dialog.findViewById(R.id.imageViewRecord);
        final EditText edtCategory = dialog.findViewById(R.id.imageViewRecord);
        Button btnUpdate = dialog.findViewById(R.id.btnUpdate);

        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels*0.95);
        int height = (int) (activity.getResources().getDisplayMetrics().heightPixels*0.7);
        dialog.getWindow().setLayout(width,height);
        dialog.show();

        imageViewIcon.setOnClickListener(new View.OnClickListener() {
            @Override
             public void onClick(View view) {
                ActivityCompat.requestPermissions(
                        RecordListActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        888
                );

            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                try {
                    MainActivity.mSQLiteHelper.updateData(
                          edtName.getText().toString().trim(),
                          edtPhone.getText().toString().trim(),
                          edtAddress.getText().toString().trim(),
                          edtEmail.getText().toString().trim(),
                          edtCategory.getText().toString().trim(),
                            MainActivity.imageViewToByte(imageViewIcon),
                            position
                    );
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Update Successfull", Toast.LENGTH_SHORT).show();

                }
                catch (Exception error){
                    Log.e("Update error",error.getMessage());
                }
                  updateRecordList();

            }

        });
    }

      private void updateRecordList() {
        Cursor cursor = MainActivity.mSQLiteHelper.getData("SELECT * FROM RECORD");
        mList.clear();
        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String phone = cursor.getString(2);
            String address = cursor.getString(3);
            String email = cursor.getString(4);
            String category = cursor.getString(5);
            byte[] image = cursor.getBlob(6);

            mList.add(new Model(id,name,phone,address,email,category,image));

        }
        mAdapter.notifyDataSetChanged();
      }

      private static byte[] mImageViewToByte(ImageView mImageView) {
          Bitmap bitmap= ((BitmapDrawable)mImageView.getDrawable()).getBitmap();
          ByteArrayOutputStream stream = new ByteArrayOutputStream();
          bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
          byte[] byteArray = stream.toByteArray();
          return byteArray;
      }

      public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
          if(requestCode==888){
              if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                  Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                  galleryIntent.setType("image/*");
                  startActivityForResult(galleryIntent, 888);
              }
              else{
                  Toast.makeText(this, "Don't have permission to access file location", Toast.LENGTH_SHORT).show();
              }
              return;
          }
          super.onRequestPermissionsResult(requestCode, permissions,grantResults);
      }

      @Override
      protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
          if(requestCode == 888 && resultCode == RESULT_OK){
              Uri imageUri = data.getData();
              CropImage.activity(imageUri)
                      .setGuidelines(CropImageView.Guidelines.ON)//enable image guidlines
                      .setAspectRatio(1,1)//image will be square
                      .start(this);

          }
          if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
              CropImage.ActivityResult result = CropImage.getActivityResult(data);
              if(requestCode==RESULT_OK){
                  Uri resultUri = result.getUri();
                  //set image choosed from gallery to image view
                  imageViewIcon.setImageURI(resultUri);
              }
              else if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                  Exception error = result.getError();
              }
          }
          super.onActivityResult(requestCode, resultCode, data);
      }

  }
