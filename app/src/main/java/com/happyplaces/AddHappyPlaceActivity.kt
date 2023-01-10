package com.happyplaces

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_add_happy_place.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.Manifest


class AddHappyPlaceActivity : AppCompatActivity(),View.OnClickListener {

    var cal =Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener

    /**
     * This function is auto created by Android when the Activity Class is created.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        //This call the parent constructor
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_happy_place)

        setSupportActionBar(toolbar_add_place) // Use the toolbar to set the action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // This is to use the home back button.

        toolbar_add_place.setNavigationOnClickListener {
            onBackPressed()
        }
        dateSetListener=DatePickerDialog.OnDateSetListener {
                view, year, month, day ->
            cal.set(Calendar.YEAR,year)
            cal.set(Calendar.MONTH,month)
            cal.set(Calendar.DAY_OF_MONTH,day)
            updateDateInView()
        }

        et_date!!.setOnClickListener(this)
        tv_add_image.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.et_date ->{
                DatePickerDialog(
                    this@AddHappyPlaceActivity,
                    dateSetListener,cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
            }
            R.id.tv_add_image ->{
                val pictureDiolog=AlertDialog.Builder(this)
                pictureDiolog.setTitle("Select Action")
                val pictureDialogItems= arrayOf("Select photo from gallery","Capture photo from camera")
                pictureDiolog.setItems(pictureDialogItems){
                    dialog, which ->
                    when(which){
                        0-> choosePhotoFromGallery()
                        1-> Toast.makeText(
                        this@AddHappyPlaceActivity,
                        "Camera selection coming soon...",
                        Toast.LENGTH_SHORT).show()
                    }
                }
                pictureDiolog.show()
            }
        }
    }

    private fun choosePhotoFromGallery(){
        Dexter.withActivity(this).withPermissions(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(object: MultiplePermissionsListener{
            override fun onPermissionsChecked(
                report: MultiplePermissionsReport?)

            {
                if(report!!.areAllPermissionsGranted())
                {
                Toast.makeText(this@AddHappyPlaceActivity,
                    "Storage READ/WRITE permission are granted. Now you can select an image from Gallery",Toast.LENGTH_SHORT).show()
                }
            }
            override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>, token:PermissionToken)
            {
                showRationalDialogForPermissions()
            }
        }).onSameThread().check()

    }

    private fun showRationalDialogForPermissions(){
        AlertDialog.Builder(this).setMessage("It looks like you have turned off permissions " +
                "required for this feature. It can be enabled " +
                "under the Application Settings")
            .setPositiveButton("GO TO Settings")
            {_,_ ->
                try{
                    val intent= Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri= Uri.fromParts("package",packageName,null)
                    intent.data=uri
                    startActivity(intent)
                }catch (e:ActivityNotFoundException){
                    e.printStackTrace()
                }

            }.setNegativeButton("Cancel"){dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun updateDateInView(){
        val myFormat="dd.MM.yyyy"
        val sdf=SimpleDateFormat(myFormat,Locale.getDefault())
        et_date.setText(sdf.format(cal.time).toString())

    }
}