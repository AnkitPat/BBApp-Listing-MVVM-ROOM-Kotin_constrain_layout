package com.example.bbim1041.bbstore.view


import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.support.annotation.NonNull
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.app.DialogFragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.example.bbim1041.bbstore.R
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnPausedListener
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.add_apk_dialog_fragment.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.net.URL
import java.net.URLConnection
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by BBIM1041 on 05/03/18.
 * Apk Add Dialog Fragment.
 */

class AddApkDialogFragment : DialogFragment() {

    private val REQUEST_FILE = 101
    private var chosenFile: String? = null
    private val DIALOG_LOAD_FILE = 1000
    internal var str = ArrayList<String>()

    private val TAG = "File Chooser"


    private var path = File(Environment.getExternalStorageDirectory().toString() + "")


    private var apkUploadText: TextView? = null
    var progressBar: ProgressBar?=null

    private var uploadApk: TextView?=null

    private var afterDownloadLayout: LinearLayout? = null

    private var crossImage: ImageView? = null
    
    var uploadAsCancel: Boolean=false

    private var cancelButtonDialog: Button? = null
    
    private var apkName: TextInputEditText?=null

    private var apkDate: EditText?=null

    private var apkType: AutoCompleteTextView?=null

    private var apkLanguage: TextInputEditText?=null
    private var apkVersion: TextInputEditText?=null
    
    private var apkDescription: EditText?=null



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layoutView: View = inflater.inflate(R.layout.add_apk_dialog_fragment, container, false)
        
        isCancelable=false

        mStorageRef = FirebaseStorage.getInstance().getReference();

        val myCalendar = Calendar.getInstance()
        val dateEditText: EditText = layoutView.findViewById(R.id.et_apk_date)
        val addApkButton: Button = layoutView.findViewById(R.id.buttonAddApk)
         progressBar= layoutView.findViewById(R.id.downloadProgressBar)
        apkUploadText = layoutView.findViewById(R.id.tv_apkNameUpload)
        uploadApk = layoutView.findViewById(R.id.button_uploadApk)
        afterDownloadLayout = layoutView.findViewById(R.id.after_download)
        crossImage = layoutView.findViewById(R.id.cross_image)
        cancelButtonDialog = layoutView.findViewById(R.id.cancelButton)
        
        apkName = layoutView.findViewById<TextInputEditText>(R.id.et_apk_name)
        apkDate = layoutView.findViewById(R.id.et_apk_date)
        apkType = layoutView.findViewById(R.id.et_apk_type)
        apkLanguage = layoutView.findViewById(R.id.et_apk_language)
        apkVersion = layoutView.findViewById(R.id.et_apk_version)
        apkDescription = layoutView.findViewById(R.id.apk_description_et)


        val valuesForAutoComplete = arrayOf("Beta","Online","Draft-1","Draft-2","Draft-3")
        
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,valuesForAutoComplete);
     
        apkType!!.setAdapter(adapter);
        apkType!!.setThreshold(1);
        
        
        
        cancelButtonDialog!!.setOnClickListener { 
            dismiss()
        }
        
        

        crossImage!!.setOnClickListener { 
            afterDownloadLayout!!.visibility=View.GONE
            uploadApk!!.visibility=View.VISIBLE
            crossImage!!.visibility=View.GONE
            uploadApk!!.text="Upload"
            progressBar!!.setProgress(0)
        }
        
        progressBar!!.max = 100

        dateEditText.keyListener = null

        val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val myFormat = "MM/dd/yy" //In which you need put here
            val sdf = SimpleDateFormat(myFormat, Locale.US)

            dateEditText.setText(sdf.format(myCalendar.time))

        }


        dateEditText.setOnClickListener {
            DatePickerDialog(context, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        uploadApk!!.setOnClickListener {
            if (ContextCompat.checkSelfPermission(context!!,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                if (!uploadAsCancel) {
                    loadFileList()


                    showDialog(DIALOG_LOAD_FILE)
                } else {

                    downloadId?.let { it1 -> transferUtility.cancel(it1)
                        uploadApk!!.visibility=View.VISIBLE
                        uploadApk!!.isClickable=false
                        crossImage!!.visibility=View.GONE
                        progressBar!!.setProgress(0)

                        uploadApk!!.text="Upload"
                        uploadApk!!.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                    }

                }
            else {


                ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 100);


            }
           
        }



        addApkButton.setOnClickListener {
            
            
            setDialog()
           FileDownloadAsynch(context!!,apkName!!.text.toString(),apkDate!!.text.toString(),needToUpload.length().toString(),apkType!!.text.toString(),apkLanguage!!.text.toString(),apkVersion!!.text.toString(),apkDescription!!.text.toString()).execute()

            
            

        }


        return layoutView
    }

    var dialog: AlertDialog? = null
    
    private fun setDialog() {
        val builder = AlertDialog.Builder(activity)
        //View view = getLayoutInflater().inflate(R.layout.progress);
        builder.setView(R.layout.progress)
         dialog = builder.create()
       
            dialog!!.show()
        
    }
    
    private fun setDialogFalse() {
        dialog!!.dismiss()
    }
    

    inner class  FileDownloadAsynch(context: Context, apk_name: String, apk_date: String, apk_length: String, apk_type: String, apk_language: String, apk_version:String, apk_description: String): AsyncTask<Void,Void,Boolean>() {
        
        var  app_list_data: String= String()
        val context=context
        private val apk_name = apk_name
        private val apk_date = apk_date
        private val apk_type = apk_type
        private val apk_language = apk_language
        private val apk_version = apk_version
        private val apk_length=apk_length
        private val apk_description = apk_description
        
        override fun doInBackground(vararg p0: Void?): Boolean {
            
            
            try {
                val url: URL = URL("https://s3.ap-south-1.amazonaws.com/checkbbstore/apk_list.json");
                val conection: URLConnection = url.openConnection();
                conection.connect();

                // this will be useful so that you can show a tipical 0-100%
                // progress bar
                val lenghtOfFile: Int = conection.getContentLength();

                // download the file
                val input: InputStream = BufferedInputStream(url.openStream(),
                        8192);

                // Output stream
                val output: OutputStream
                if (!File(Environment
                        .getExternalStorageDirectory().toString()+File.separator
                        + "bb_apps_folder/app_list.json").exists()) 
                    
                 {
                    File(Environment
                            .getExternalStorageDirectory().toString(),"bb_apps_folder").mkdir()
                    File(Environment
                            .getExternalStorageDirectory().toString()+File.separator
                            + "bb_apps_folder/app_list.json").createNewFile()
                    
                }
                output = FileOutputStream(Environment
                        .getExternalStorageDirectory().toString()+File.separator
                        + "bb_apps_folder/app_list.json");

                val data=ByteArray(2048)

                while (true) {

                    val length = input.read(data)

                    if (length <= 0)

                        break

                    output.write(data, 0, length)
                

                }
                app_list_data = readFile()
             

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (e: Exception) {
                Log.e("Error: ", e.message);
                activity!!.runOnUiThread {
                    setDialogFalse()   
                }
                return false
            }

            
            return true
        }
        
        fun readFile(): String {
            val file = File(Environment
                    .getExternalStorageDirectory().toString()+File.separator
                    + "bb_apps_folder/app_list.json")
            var ins:InputStream = file.inputStream()
            var content = ins.readBytes().toString(Charset.defaultCharset())
        
            return content
        }

        override fun onPostExecute(result: Boolean?) {
            super.onPostExecute(result)
            if(result!!) {
                val jsonArray: JSONArray = JSONArray(app_list_data)
                
                val jsonObject: JSONObject = JSONObject()
                jsonObject.put("apk_name",apk_name)
                jsonObject.put("apk_date",apk_date)
                jsonObject.put("apk_language",apk_language)
                jsonObject.put("apk_length",apk_length)
                jsonObject.put("apk_type",apk_type)
                jsonObject.put("apk_version",apk_version)
                jsonObject.put("apk_description",apk_description)
                
                jsonArray.put(jsonObject)

                var output: Writer? = null
                val file = File(Environment
                        .getExternalStorageDirectory().toString()+File.separator
                        + "bb_apps_folder/apk_list.json")
                output = BufferedWriter(FileWriter(file))
                output.write(jsonArray.toString())
                output.close()


                val credentialsProvider = CognitoCachingCredentialsProvider(
                        context,
                        "us-east-1:18f546b2-31d4-464f-9512-f1443fab36bf", // Identity pool ID
                        Regions.US_EAST_1 // Region
                )
                val s3 = AmazonS3Client(credentialsProvider)
                val transferUtility1 = TransferUtility(s3, context)

                val needToUpload = file

                var file_size: Int = (needToUpload.length()).toInt();

                val observer = transferUtility1.upload(
                        "checkbbstore",
                        needToUpload.name,
                        needToUpload
                )

                


                observer.setTransferListener(object : TransferListener {

                    override fun onStateChanged(id: Int, state: TransferState) {
                        // do something
                        Log.d("log", "state changed. id = $id\tstate = $state")

                        if(state.name.equals("COMPLETED")) {
                            Toast.makeText(context, "Uploaded Successfully", Toast.LENGTH_SHORT).show()
                            dismiss()
                            setDialogFalse()
                        }
                     
                    }

                    override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                        val percentage = ((bytesCurrent.toDouble() / file_size) * 100)

                        
                    }

                    override fun onError(id: Int, ex: Exception) {
                        // do something
                        setDialogFalse()
                        Toast.makeText(context,"Some Error, try uploading again..",Toast.LENGTH_SHORT).show()
                    }
                })
                
                
                
            }
        }
    }
    
    fun closeDialog() {
        dismiss()
    }

    private inner class Item(var file: String, var icon: Int?) {

        override fun toString(): String {
            return file
        }
    }

    private lateinit var fileList: Array<Item?>

    private var firstLvl: Boolean? = true
    private var adapter: ArrayAdapter<Item>? = null


    private var mStorageRef: StorageReference? = null


    private fun loadFileList() {
        try {
            path.mkdirs()
        } catch (e: SecurityException) {
            Log.e(TAG, "unable to write on the sd card ")
        }

        // Checks whether path exists
        if (path.exists()) {
            val filter = FilenameFilter { dir, filename ->
                val sel = File(dir, filename)
                // Filters based on whether the file is hidden or not
                sel.isFile && sel.name.contains(".apk") || sel.isDirectory
            }

            val fList = path.list(filter)
            fileList = arrayOfNulls(fList!!.size)
            for (i in fList.indices) {
                fileList[i] = Item(fList[i], R.drawable.android_icon)

                // Convert into file path
                val sel = File(path, fList[i])

                // Set drawables
                if (sel.isDirectory) {
                    fileList[i]!!.icon = R.drawable.folder_icon
                    Log.d("DIRECTORY", fileList[i]!!.file)
                } else {
                    Log.d("FILE", fileList[i]!!.file)
                }
            }

            if (!(firstLvl!!)) {
                val temp = arrayOfNulls<Item>(fileList.size + 1)
                for (i in fileList.indices) {
                    temp[i + 1] = fileList[i]
                }
                temp[0] = Item("Up", R.drawable.up_image)
                fileList = temp
            }
        } else {
            Log.e(TAG, "path does not exist")
        }

        adapter = object : ArrayAdapter<Item>(context,
                android.R.layout.select_dialog_item, android.R.id.text1,
                fileList) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                // creates view
                val view = super.getView(position, convertView, parent)
                val textView = view
                        .findViewById<View>(android.R.id.text1) as TextView

                textView.textSize = 15f
                // put the image on the text view
                textView.setCompoundDrawablesWithIntrinsicBounds(
                        fileList[position]!!.icon!!, 0, 0, 0)

                // add margin between image and text (support various screen
                // densities)
                val dp5 = (5 * resources.displayMetrics.density + 0.5f).toInt()
                textView.compoundDrawablePadding = dp5

                return view
            }
        }

    }


    private lateinit var transferUtility: TransferUtility
    private var downloadId: Int?=0
    private lateinit var needToUpload:File
    
    fun showDialog(id: Int) {
        var dialog: Dialog? = null
        val builder = AlertDialog.Builder(context)


        if (fileList == null) {
            Log.e(TAG, "No files loaded")
            dialog = builder.create()

            dialog!!.setCanceledOnTouchOutside(false)
            dialog.setCancelable(false)


        }

        when (id) {
            DIALOG_LOAD_FILE -> {
                builder.setTitle("Choose your file")
                builder.setAdapter(adapter) { dialog, which ->
                    val chosenFile = fileList[which]!!.file
                    val sel = File(path.toString() + "/" + chosenFile)
                    if (sel.isDirectory) {
                        firstLvl = false

                        // Adds chosen directory to list
                        str.add(chosenFile)
                        fileList = arrayOfNulls(0)
                        path = File(sel.toString() + "")

                        loadFileList()

                        dialog.cancel()
                        showDialog(DIALOG_LOAD_FILE)
                        Log.d(TAG, path.absolutePath)

                    } else if (chosenFile.equals("up", ignoreCase = true) && !sel.exists()) {

                        // present directory removed from list
                        val s = str.removeAt(str.size - 1)

                        // path modified to exclude present directory
                        path = File(path.toString().substring(0,
                                path.toString().lastIndexOf(s)))
                        fileList = arrayOfNulls(0)

                        // if there are no more directories in the list, then
                        // its the first level
                        if (str.isEmpty()) {
                            firstLvl = true
                        }
                        loadFileList()

                        dialog.cancel()
                        showDialog(DIALOG_LOAD_FILE)
                        Log.d(TAG, path.absolutePath)

                    } else {
                        // Perform action with file picked
                        apkUploadText!!.text = sel.name

                        /*val file = Uri.fromFile(sel.absoluteFile)
                        val riversRef = mStorageRef!!.child("images/"+sel.name)

                        riversRef.putFile(file)
                                .addOnSuccessListener(object : OnSuccessListener<UploadTask.TaskSnapshot> {
                                    public override fun onSuccess(taskSnapshot: UploadTask.TaskSnapshot) {
                                        // Get a URL to the uploaded content
                                        val downloadUrl = taskSnapshot.getDownloadUrl()
                                        
                                        
                                        
                                        
                                    }
                                })
                                .addOnFailureListener(object : OnFailureListener {
                                    public override fun onFailure(@NonNull exception: Exception) {
                                        // Handle unsuccessful uploads
                                        // ...
                                        Log.v("error",exception.message)
                                    }
                                })
                                .addOnProgressListener(object : OnProgressListener<UploadTask.TaskSnapshot>
                                {
                                    override fun onProgress(taskSnapshot: UploadTask.TaskSnapshot) {
                                        val progress: Double = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                        System.out.println("Upload is " + progress + "% done");
                                        progressBar!!.setProgress(progress.toInt())
                                    }
                                }
                                )}*/


                        val credentialsProvider = CognitoCachingCredentialsProvider(
                                context,
                                "us-east-1:18f546b2-31d4-464f-9512-f1443fab36bf", // Identity pool ID
                                Regions.US_EAST_1 // Region
                        )
                        val s3 = AmazonS3Client(credentialsProvider)
                        transferUtility = TransferUtility(s3, context)

                        needToUpload = sel
                        
                        
                        var file_size: Int = (needToUpload.length()).toInt();

                        val observer = transferUtility.upload(
                                "checkbbstore",
                                apkName!!.text.toString()+".apk",
                                needToUpload
                        )
                        
                        downloadId=observer.id
                        
                        
                        observer.setTransferListener(object : TransferListener {

                            override fun onStateChanged(id: Int, state: TransferState) {
                                // do something
                                Log.d("log", "state changed. id = $id\tstate = $state")
                                
                                if(state.name.equals("COMPLETED"))
                                {
                                    uploadApk!!.visibility=View.GONE
                                    afterDownloadLayout!!.visibility=View.VISIBLE
                                    progressBar!!.setProgress(0)
                                    crossImage!!.visibility=View.VISIBLE
                                    uploadAsCancel=false
                                }
                                else if(state.name.equals("IN_PROGRESS"))
                                {
                                    uploadApk!!.isClickable=true
                                    uploadApk!!.text="Cancel"
                                    uploadApk!!.setBackgroundColor(Color.RED)
                                    uploadAsCancel=true
                                }
                                else if(state.name.equals("FAILED"))
                                {
                                    afterDownloadLayout!!.visibility=View.GONE
                                    uploadApk!!.visibility=View.VISIBLE
                                    uploadApk!!.isClickable=true
                                    crossImage!!.visibility=View.GONE
                                    uploadApk!!.text="Upload"
                                    progressBar!!.setProgress(0)
                                    uploadAsCancel=false
                                    
                                    Toast.makeText(context,"Some Error, try uploading again..",Toast.LENGTH_SHORT).show()
                                }
                                
                            }

                            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                                val percentage = ((bytesCurrent.toDouble() / file_size) * 100)

                                progressBar!!.setProgress(percentage.toInt())
                                
                                if(percentage>99.0)
                                {
                                    uploadApk!!.text="Making it public for you"
                                    uploadApk!!.setBackgroundColor(Color.TRANSPARENT)
                                }
                                else if(percentage>90.0)
                                {
                                    uploadApk!!.text="Just to Finish"
                                }
                                else if(percentage>80.0)
                                {
                                    uploadApk!!.text="Synching it with S3 Bucket"
                                }
                                

                                Log.d("log", "onProgressChanged = " + percentage+ " bytes current:  "+bytesCurrent+ "bytes total :"+bytesTotal)
                            }

                            override fun onError(id: Int, ex: Exception) {
                                // do something
                                Log.d("log", "error in uploading. id = $id\nException = $ex")
                                afterDownloadLayout!!.visibility=View.GONE
                                uploadApk!!.visibility=View.VISIBLE
                                uploadApk!!.isClickable=false
                                crossImage!!.visibility=View.GONE
                                progressBar!!.setProgress(0)

                                uploadApk!!.text="Upload"
                                uploadApk!!.setBackgroundColor(Color.RED)
                                uploadAsCancel=false
                                
                                Toast.makeText(context,"Some Error, try uploading again..",Toast.LENGTH_SHORT).show()
                            }
                        })
                        
                        


                    }
                }


                dialog = builder.show()

            }
        }

    }

}