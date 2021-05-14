package com.techroof.pkpropertyzone.SellProperty;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.techroof.pkpropertyzone.Authentication.LoginActivity;
import com.techroof.pkpropertyzone.Fragments.ApartmentsFragment;
import com.techroof.pkpropertyzone.Fragments.ConstructionFragment;
import com.techroof.pkpropertyzone.Fragments.ShopsFragment;
import com.techroof.pkpropertyzone.R;

import java.util.HashMap;
import java.util.Map;

public class AddPropertyActivity extends AppCompatActivity {

    EditText propertyName, constructionPropertyDescription, propertyDescription, propertyPrice, propertyPurpose, fullAddress;

    CardView addImage, addVideo;

    ImageView uploadedImg, uploadedVideo, cancelImgFile, cancelVideoFile;

    Uri ImagefilePath, VideofilePath;

    Button addProperty, addConstructionProperty;

    EditText  propertyTypeEt;
    String[] cityandsectors;
    String propertyType ;
    String sellerPhone,context;

    StorageReference storageReference;
    FirebaseFirestore firestore;

    FirebaseAuth firebaseAuth;
    private ProgressDialog uploadingPd;

    String UserID, ImagefileUrl, VideofileUrl, PropertyTypeSpinnerString, CityandSectorSpinnerString, cityId, areaName;

    final static String constructionImagefileUrl = "https://firebasestorage.googleapis.com/v0/b/pkpropertyzone-3ea93.appspot.com/o/images%2Fnicolas-j-leclercq-WJg2bynUWOk-unsplash.jpg?alt=media&token=42253c16-c4d1-497a-a98b-a83ecbbe410f";

    ConstraintLayout constraintLayoutConstruction, constraintLayoutOthers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_property);

        cityId = getIntent().getStringExtra("cityId");
        areaName = getIntent().getStringExtra("areaName");
        context=getIntent().getStringExtra("context");

        firebaseAuth=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();

         if (firebaseUser == null) {
            Intent intent = new Intent(AddPropertyActivity.this, LoginActivity.class);
            intent.putExtra("cityId",cityId);
            intent.putExtra("areaName",areaName);
            intent.putExtra("context",context);
            startActivity(intent);

         }else{

             final ProgressDialog progressDialog = new ProgressDialog(AddPropertyActivity.this);
             // progressDialog.setTitle("Adding Properties");
             progressDialog.setMessage("Please wait...");
             progressDialog.setCancelable(false);

             uploadingPd = new ProgressDialog(this);
             uploadingPd.setCanceledOnTouchOutside(false);

             //  getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

             constraintLayoutConstruction = findViewById(R.id.cl_construction);

             constraintLayoutOthers = findViewById(R.id.cl);

             storageReference = FirebaseStorage.getInstance().getReference();
             firestore = FirebaseFirestore.getInstance();

             firebaseAuth = FirebaseAuth.getInstance();
             UserID = firebaseAuth.getCurrentUser().getUid();

             propertyName = findViewById(R.id.property_name);
             constructionPropertyDescription = findViewById(R.id.construction_property_desc);
             propertyDescription = findViewById(R.id.property_desc);
             propertyPrice = findViewById(R.id.property_price);
             propertyPurpose = findViewById(R.id.property_purpose);
             fullAddress = findViewById(R.id.full_address);

             addImage = findViewById(R.id.add_img);
             addVideo = findViewById(R.id.add_video);

             uploadedImg = findViewById(R.id.uploaded_file_img);
             uploadedVideo = findViewById(R.id.uploaded_file_video);
             cancelImgFile = findViewById(R.id.cancel_img_file);
             cancelVideoFile = findViewById(R.id.cancel_video_file);

             addConstructionProperty = findViewById(R.id.addConstructionPropertyBtn);
             addProperty = findViewById(R.id.addPropertyBtn);

             propertyTypeEt = findViewById(R.id.property_type_et);
             propertyType = getIntent().getStringExtra("propertyType");

             if (propertyType.equals("0")){

                 propertyType="Plot";

             }else if (propertyType.equals("1")){
                 propertyType="Apartment";


             }else if (propertyType.equals("2")){
                 propertyType="Construction";
             }

             propertyTypeEt.setText(propertyType);

             if (propertyType.equals("Plot") || propertyType.equals("Apartment")){

                 constraintLayoutConstruction.setVisibility(View.GONE);
                 constraintLayoutOthers.setVisibility(View.VISIBLE);

             }else if(propertyType.equals("Construction")){

                 constraintLayoutConstruction.setVisibility(View.VISIBLE);
                 constraintLayoutOthers.setVisibility(View.GONE);

             }

             uploadedImg.setVisibility(View.INVISIBLE);
             cancelImgFile.setVisibility(View.INVISIBLE);
             uploadedVideo.setVisibility(View.INVISIBLE);
             cancelVideoFile.setVisibility(View.INVISIBLE);

             cancelImgFile.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     uploadedImg.setVisibility(View.INVISIBLE);
                     cancelImgFile.setVisibility(View.INVISIBLE);
                     addImage.setVisibility(View.VISIBLE);
                 }
             });

             firestore.collection("Users")
                     .document(firebaseAuth.getCurrentUser().getUid())
                     .get()
                     .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                         @Override
                         public void onSuccess(DocumentSnapshot documentSnapshot) {

                             sellerPhone = documentSnapshot.get("phone").toString();

                         }
                     });

             addImage.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     Dexter.withContext(getApplicationContext())
                             .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                             .withListener(new PermissionListener() {
                                 @Override
                                 public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                     Intent intent = new Intent();
                                     intent.setType("image/*");
                                     intent.setAction(Intent.ACTION_GET_CONTENT);
                                     startActivityForResult(Intent.createChooser(intent, "Select Image"), 101);
                                 }

                                 @Override
                                 public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                                 }

                                 @Override
                                 public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                     permissionToken.continuePermissionRequest();
                                 }
                             }).check();
                 }
             });

             cancelVideoFile.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     uploadedVideo.setVisibility(View.INVISIBLE);
                     cancelVideoFile.setVisibility(View.INVISIBLE);
                     addVideo.setVisibility(View.VISIBLE);
                 }
             });

             addVideo.setOnClickListener(new View.OnClickListener() {

                 @Override
                 public void onClick(View view) {
                     Dexter.withContext(getApplicationContext())
                             .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                             .withListener(new PermissionListener() {
                                 @Override
                                 public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                     Intent intent = new Intent();
                                     intent.setType("video/*");
                                     intent.setAction(Intent.ACTION_GET_CONTENT);
                                     startActivityForResult(Intent.createChooser(intent, "Select Video"), 102);
                                 }

                                 @Override
                                 public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                                 }

                                 @Override
                                 public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                                 }
                             }).check();
                 }
             });


             addProperty.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     String PropertyName = propertyName.getText().toString();
                     String PropertyDescription = propertyDescription.getText().toString();
                     String PropertyPrice = propertyPrice.getText().toString();
                     String PropertyPurpose = propertyPurpose.getText().toString();
                     String FullAddress = fullAddress.getText().toString();

                     if (TextUtils.isEmpty(PropertyName)) {
                         propertyName.setError("Please Enter Property Name");
                         return;
                     }
                     if (TextUtils.isEmpty(PropertyDescription)) {
                         propertyDescription.setError("Please Enter Property Description");
                         return;
                     }
                     if (TextUtils.isEmpty(PropertyPrice)) {
                         propertyPrice.setError("Please Enter Property Price");
                         return;
                     }
                     if (TextUtils.isEmpty(PropertyPurpose)) {
                         propertyPurpose.setError("Please Enter Property Purpose");
                         return;
                     }
                     if (TextUtils.isEmpty(FullAddress)) {
                         fullAddress.setError("Please Enter Full Address");
                         return;
                     }

                     final ProgressDialog progressDialog = new ProgressDialog(AddPropertyActivity.this);
                     //progressDialog.setTitle("Adding Properties");
                     progressDialog.setMessage("Please wait...");
                     progressDialog.setCancelable(false);
                     progressDialog.show();

                     String PropertyId = firestore.collection("Properties").document().getId();

                     Map<String, Object> properties = new HashMap<>();
                     properties.put("propertyId", PropertyId);
                     properties.put("propertyName", PropertyName);
                     properties.put("propertyDescription", PropertyDescription);
                     properties.put("propertyPrice", PropertyPrice);
                     properties.put("propertyType", propertyType);
                     properties.put("propertyPurpose", PropertyPurpose);
                     // properties.put("location", CityandSectorSpinnerString);
                     properties.put("fullAddress", FullAddress);
                     properties.put("imageUrl", ImagefileUrl);
                     properties.put("videoUrl", VideofileUrl);
                     properties.put("seller", UserID);
                     properties.put("sellerPhone", sellerPhone);
                     properties.put("cityId",cityId);
                     properties.put("timestamp", Timestamp.now());
                     properties.put("areaName",areaName);

                     firestore.collection("Properties").document(PropertyId)
                             .set(properties)
                             .addOnSuccessListener(new OnSuccessListener<Void>() {
                                 @Override
                                 public void onSuccess(Void aVoid) {
                                     progressDialog.dismiss();

                                     if (propertyType.equals("Shop")) {
                                         Intent intent = new Intent(AddPropertyActivity.this, ShopsFragment.class);
                                         startActivity(intent);
                                         finish();

                                     } else if (propertyType.equals("Apartment")) {
                                         Intent intent = new Intent(AddPropertyActivity.this, ApartmentsFragment.class);
                                         startActivity(intent);
                                         finish();
                                     }
                                     Toast.makeText(AddPropertyActivity.this, "Property Added Successfully", +Toast.LENGTH_LONG).show();
                                 }
                             })
                             .addOnFailureListener(new OnFailureListener() {
                                 @Override
                                 public void onFailure(@NonNull Exception e) {
                                     Toast.makeText(AddPropertyActivity.this, "Error" + e.getMessage(), Toast.LENGTH_SHORT).show();

                                 }
                             });

                 }
             });

             addConstructionProperty.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {

                     String PropertyDescription = constructionPropertyDescription.getText().toString();

                     if (TextUtils.isEmpty(PropertyDescription)) {
                         constructionPropertyDescription.setError("Please Enter Property Description");
                     } else {
                         progressDialog.show();

                         String PropertyId = firestore.collection("Properties")
                                 .document()
                                 .getId();

                         Map<String, Object> constructionProperties = new HashMap<>();
                         constructionProperties.put("propertyId", PropertyId);
                         constructionProperties.put("propertyType", propertyType);
                         constructionProperties.put("propertyDescription", PropertyDescription);
                         constructionProperties.put("imageUrl", constructionImagefileUrl);
                         constructionProperties.put("propertyName", "Construction Service");
                         constructionProperties.put("propertyPrice", "N/A");
                         constructionProperties.put("propertyPurpose", "Construction");
                         constructionProperties.put("location", "N/A");
                         constructionProperties.put("fullAddress", "N/A");
                         constructionProperties.put("videoUrl", "N/A");
                         constructionProperties.put("seller", UserID);
                         constructionProperties.put("sellerPhone", sellerPhone);
                         constructionProperties.put("cityId",cityId);
                         constructionProperties.put("areaName",areaName);
                         constructionProperties.put("timestamp", Timestamp.now());


                         firestore.collection("Properties")
                                 .document(PropertyId)
                                 .set(constructionProperties)
                                 .addOnSuccessListener(new OnSuccessListener<Void>() {
                                     @Override
                                     public void onSuccess(Void aVoid) {
                                         progressDialog.dismiss();

                                         Toast.makeText(AddPropertyActivity.this, "Property Added Successfully",Toast.LENGTH_LONG).show();

                                         FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                                         fragmentTransaction.replace(R.id.add_property, new ConstructionFragment()).commit();
                                     }
                                 })
                                 .addOnFailureListener(new OnFailureListener() {
                                     @Override
                                     public void onFailure(@NonNull Exception e) {
                                         Toast.makeText(AddPropertyActivity.this, "Error" + e.getMessage(), Toast.LENGTH_SHORT).show();

                                     }
                                 });
                     }
                 }
             });

         }
    }

    private void uploadImage(Uri ImagefilePath) {

        final StorageReference reference = storageReference.child("images/" + System.currentTimeMillis());
        reference.putFile(ImagefilePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                ImagefileUrl = uri.toString();
                                uploadedImg.setVisibility(View.INVISIBLE);
                                cancelImgFile.setVisibility(View.INVISIBLE);
                                addImage.setVisibility(View.VISIBLE);
                                uploadingPd.dismiss();

                            }
                        });
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        uploadingPd.setMessage((int) progress + "% Uploaded");
                        uploadingPd.show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddPropertyActivity.this, "Failed to Upload Image" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void uploadVideo(Uri VideofilePath) {

        final StorageReference reference = storageReference.child("videos/" + System.currentTimeMillis());
        reference.putFile(VideofilePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                VideofileUrl = uri.toString();

                                uploadedVideo.setVisibility(View.INVISIBLE);
                                cancelVideoFile.setVisibility(View.INVISIBLE);
                                addVideo.setVisibility(View.VISIBLE);
                                uploadingPd.dismiss();

                            }
                        });
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        uploadingPd.setMessage((int) progress + "% Uploaded");
                        uploadingPd.show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddPropertyActivity.this, "Failed to Upload Video" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK) {
            ImagefilePath = data.getData();
            uploadedImg.setVisibility(View.VISIBLE);
            cancelImgFile.setVisibility(View.VISIBLE);
            addImage.setVisibility(View.INVISIBLE);
            uploadImage(ImagefilePath);
        } else if (requestCode == 102 && resultCode == RESULT_OK) {
            VideofilePath = data.getData();
            uploadedVideo.setVisibility(View.VISIBLE);
            cancelVideoFile.setVisibility(View.VISIBLE);
            addVideo.setVisibility(View.INVISIBLE);
            uploadVideo(VideofilePath);
        }
    }

}