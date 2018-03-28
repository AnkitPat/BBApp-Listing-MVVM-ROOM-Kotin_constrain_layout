package com.example.bbim1041.bbstore.view

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DownloadManager
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.FloatingActionButton
import android.support.v4.BuildConfig
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.widget.LinearLayoutManager
import android.text.Html
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.*
import android.widget.*
import com.example.bbim1041.bbstore.BaseApplication
import com.example.bbim1041.bbstore.R
import com.example.bbim1041.bbstore.adapter.AdapterSectionRecycler
import com.example.bbim1041.bbstore.adapter.SimpleItemDecorater
import com.example.bbim1041.bbstore.model.data.App
import com.example.bbim1041.bbstore.model.data.SectionHeader
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.app_list_fragment.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by BBIM1041 on 23/02/18.
 * Its the main Fragment where we are publishing our list that is fetched
 */


class AppListFragment : MvvmFragment(), AdapterSectionRecycler.RecyclerListItemClickListener {

    val appListViewModel = BaseApplication.injectUserListViewModel();

    var downloadManager: DownloadManager? = null;

    var downloadButton: TextView? = null

    var fileDownloaded = false

    var folderName: String = "bb_apps_folder";

    var universalTextSizeForList: Int = 18


    var floatingActionButton: FloatingActionButton? = null
    var filterFloatingActionButton: FloatingActionButton? = null
    var sortFloatingActionButton: FloatingActionButton? = null
    var addFloatingActionButton: FloatingActionButton? = null

    var filterTextFloating: TextView? = null
    var sortTextFloating: TextView? = null
    var addTextFloating: TextView? = null

    internal var base_uri = Uri.parse("https://s3.ap-south-1.amazonaws.com/checkbbstore/")


    var isDraftOneSelected: Boolean = false
    var isDraftTwoSelected: Boolean = false
    var isDraftThreeSelected: Boolean = false
    var onlineSelected: Boolean = false
    var betaSelected: Boolean = false

    var isFromDateSelected: Boolean = false
    var isToDateSelected: Boolean = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        var layoutView: View = inflater.inflate(R.layout.app_list_fragment, container, false)
        floatingActionButton = layoutView.findViewById(R.id.floatingActionButton)
        filterFloatingActionButton = layoutView.findViewById(R.id.filterFloatingActionButton)
        sortFloatingActionButton = layoutView.findViewById(R.id.sortFloatingActionButton)
        addFloatingActionButton = layoutView.findViewById(R.id.addFloatingActionButton)

        filterTextFloating = layoutView.findViewById(R.id.filterTextView)
        sortTextFloating = layoutView.findViewById(R.id.sortTextView)
        addTextFloating = layoutView.findViewById(R.id.addTextView)

        floatingActionButton!!.setOnClickListener {
            // showApkAddDialog()

            if (!isFABOpen) {
                floatingActionButton!!.animate().rotation(-135f)
                showFABMenu();
            } else {
                floatingActionButton!!.animate().rotation(0f)
                closeFABMenu();
            }
        }


        addFloatingActionButton!!.setOnClickListener {
            showApkAddDialog()
        }

        filterFloatingActionButton!!.setOnClickListener {
            showFilterDialog()
        }


        setHasOptionsMenu(true)

        return layoutView;
    }

    var isFABOpen = false
    fun showFABMenu() {
        addFloatingActionButton!!.visibility = View.VISIBLE
        filterFloatingActionButton!!.visibility = View.VISIBLE
        sortFloatingActionButton!!.visibility = View.VISIBLE
        addTextFloating!!.visibility = View.VISIBLE
        filterTextFloating!!.visibility = View.VISIBLE
        sortTextFloating!!.visibility = View.VISIBLE

        /* isFABOpen = true;
         addFloatingActionButton!!.animate().translationY(-28F);
         filterFloatingActionButton!!.animate().translationY(-76F);
         sortFloatingActionButton!!.animate().translationY(-124F);
         
         addTextFloating!!.animate().translationY(-16F)
         filterTextFloating!!.animate().translationY(-52F)
         sortTextFloating!!.animate().translationY(-88F)*/

        isFABOpen = true;
        addFloatingActionButton!!.animate().translationY(-124F);
        filterFloatingActionButton!!.animate().translationY(-28F);
        sortFloatingActionButton!!.animate().translationY(-76F);

        addTextFloating!!.animate().translationY(-115F)
        filterTextFloating!!.animate().translationY(-30f)
        sortTextFloating!!.animate().translationY(-65f)
    }

    fun closeFABMenu() {


        isFABOpen = false;
        addFloatingActionButton!!.animate().translationY(0f);
        filterFloatingActionButton!!.animate().translationY(0f);
        sortFloatingActionButton!!.animate().translationY(0f);

        addTextFloating!!.animate().translationY(0f)
        filterTextFloating!!.animate().translationY(0f)
        sortTextFloating!!.animate().translationY(0f)
        
        
        addFloatingActionButton!!.visibility = View.INVISIBLE
        filterFloatingActionButton!!.visibility = View.INVISIBLE
        sortFloatingActionButton!!.visibility = View.INVISIBLE
        addTextFloating!!.visibility = View.INVISIBLE
        filterTextFloating!!.visibility = View.INVISIBLE
        sortTextFloating!!.visibility = View.INVISIBLE
    }


    fun showFilterDialog() {
        val factory = LayoutInflater.from(context)
        val filterDialogView = factory.inflate(R.layout.filter_list_layout, null)
        val filterDialog = AlertDialog.Builder(context).create()
        filterDialog.setView(filterDialogView)


        val draftOneButton: Button = filterDialogView.findViewById(R.id.draftOneButton)
        val draftTwoButton: Button = filterDialogView.findViewById(R.id.draftTwoButton)
        val draftThreeButton: Button = filterDialogView.findViewById(R.id.draftThreeButton)
        val onlineButton: Button = filterDialogView.findViewById(R.id.onlineButton)
        val betaButton: Button = filterDialogView.findViewById(R.id.betaButton)

        val fromDateButton: Button = filterDialogView.findViewById(R.id.fromDateButton)
        val toDateButton: Button = filterDialogView.findViewById(R.id.toDateButton)

        draftOneButton.setOnClickListener {
            isDraftOneSelected = !isDraftOneSelected
            
            if(isDraftOneSelected){
                draftOneButton.elevation = 2f;
                draftOneButton.setBackgroundColor(Color.GRAY)
            }
            else {
                
                draftOneButton.elevation = 9f;
                draftOneButton.setBackgroundColor(Color.WHITE)
                
            }
            
        }
        draftTwoButton.setOnClickListener {
            isDraftTwoSelected = !isDraftTwoSelected
            if(isDraftTwoSelected){
                draftTwoButton.elevation = 2f;
                draftTwoButton.setBackgroundColor(Color.GRAY)
            }
            else {

                draftTwoButton.elevation = 9f;
                draftTwoButton.setBackgroundColor(Color.WHITE)

            }
        }
        draftThreeButton.setOnClickListener {
            isDraftThreeSelected = !isDraftThreeSelected
            if(isDraftThreeSelected){
                draftThreeButton.elevation = 2f;
                draftThreeButton.setBackgroundColor(Color.GRAY)
            }
            else {

                draftThreeButton.elevation = 9f;
                draftThreeButton.setBackgroundColor(Color.WHITE)

            }
        }

        betaButton.setOnClickListener {
            betaSelected = !betaSelected
            if(betaSelected){
                betaButton.elevation = 2f;
                betaButton.setBackgroundColor(Color.GRAY)
            }
            else {

                betaButton.elevation = 9f;
                betaButton.setBackgroundColor(Color.WHITE)

            }
        }
        onlineButton.setOnClickListener {
            onlineSelected = !onlineSelected
            if(onlineSelected){
                onlineButton.elevation = 2f;
                onlineButton.setBackgroundColor(Color.GRAY)
            }
            else {

                onlineButton.elevation = 9f;
                onlineButton.setBackgroundColor(Color.WHITE)

            }
        }
        
        fromDateButton.setOnLongClickListener { 
            isFromDateSelected = !isFromDateSelected
            
            if(isFromDateSelected) {
                fromDateButton.setBackgroundColor(Color.WHITE)
                fromDateButton.setTextColor(Color.BLACK)
                fromDateButton.setText("From Date")
            }
            
            true
        }

        fromDateButton.setOnClickListener {
            isFromDateSelected = !isFromDateSelected
            val myCalendar = Calendar.getInstance()
            val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val myFormat = "MM/dd/yy" //In which you need put here
                val sdf = SimpleDateFormat(myFormat, Locale.US)
            
                fromDateButton.setText(sdf.format(myCalendar.time))

                fromDateButton.setTextColor(Color.GREEN)
                fromDateButton.setBackgroundColor(Color.GRAY)
            }

            DatePickerDialog(context, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show()
            
            
            
           
        }
        
        toDateButton.setOnLongClickListener {
            isToDateSelected = !isToDateSelected

            if(isToDateSelected) {
                toDateButton.setBackgroundColor(Color.WHITE)
                toDateButton.setTextColor(Color.BLACK)
                toDateButton.setText("To Date")
            }

            true
        }
        
        toDateButton.setOnClickListener {
            isToDateSelected = !isToDateSelected
            val myCalendar = Calendar.getInstance()
            val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val myFormat = "MM/dd/yy" //In which you need put here
                val sdf = SimpleDateFormat(myFormat, Locale.US)

                toDateButton.setText(sdf.format(myCalendar.time))

                toDateButton.setTextColor(Color.GREEN)
                toDateButton.setBackgroundColor(Color.GRAY)
            }

            DatePickerDialog(context, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show()

        }


        filterDialog.show()
    }

    fun showDownloadDialog(appInstance: App) {
        val factory = LayoutInflater.from(context)
        val downloadDialogView = factory.inflate(R.layout.fragment_download_dialog, null)
        val downloadDialog = AlertDialog.Builder(context).create()
        downloadDialog.setView(downloadDialogView)
        downloadDialog.setCanceledOnTouchOutside(false)

        val apkNameTV = downloadDialogView.findViewById<TextView>(R.id.apk_name_tv)
        val percentageTV = downloadDialogView.findViewById<TextView>(R.id.percentage_tv)

        val progressBar = downloadDialogView.findViewById<ProgressBar>(R.id.progressBar)

        downloadButton = downloadDialogView.findViewById<TextView>(R.id.button_download)
        val cancelButton = downloadDialogView.findViewById<TextView>(R.id.button_close)

        val downloadStatusTextView = downloadDialogView.findViewById<TextView>(R.id.downloadStatusTextView)

        apkNameTV.text = appInstance.apk_name
        val destination: String
        if (appInstance.apk_name.contains(".apk")) {
            destination = Environment.getExternalStorageDirectory().toString() + File.separator + folderName + File.separator + appInstance.apk_name;
        } else {
            destination = Environment.getExternalStorageDirectory().toString() + File.separator + folderName + File.separator + appInstance.apk_name + ".apk";

        }

        if (File(destination).exists())
            downloadButton!!.text = "Install"
        else
            downloadButton!!.text = "Download"


        downloadButton!!.setOnClickListener {

            
            

            if (!File(destination).exists())
                downloadData(base_uri, appInstance.apk_name, progressBar, percentageTV, downloadStatusTextView)
            else {
                Log.v("PackageName",getAppLabel(context!!.packageManager,destination))
                /*  val apk_uri: Uri = Uri.parse("file://" + destination);
                  val install = Intent(Intent.ACTION_VIEW)
                  install.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                  install.setDataAndType(apk_uri,
                          downloadManager!!.getMimeTypeForDownloadedFile(downloadReference))
                  if (install.resolveActivity(context!!.packageManager) != null)
                      startActivity(install)*/


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    val apkUri: Uri = FileProvider.getUriForFile(context!!, BuildConfig.APPLICATION_ID + ".provider", File(destination));
                    val intent: Intent = Intent(Intent.ACTION_INSTALL_PACKAGE);
                    intent.setData(apkUri);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    if (intent.resolveActivity(context!!.packageManager) != null)
                        activity!!.startActivity(intent)
                } else {
                    val apkUri: Uri = Uri.fromFile(File(destination))
                    val intent: Intent = Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (intent.resolveActivity(context!!.packageManager) != null)
                        activity!!.startActivity(intent);
                }

            }

        }

        cancelButton!!.setOnClickListener {
            if (!downloadReference.equals(0) && downloadManager != null) {
                downloading = false
                downloadManager!!.remove(downloadReference)
                progressBar.setProgress(0)
                percentageTV.text = "0"


            }

            downloadDialog.cancel()

        }


        downloadDialog.show()
    }


    fun getAppLabel(pm: PackageManager, pathToApk: String): String {
        val packageInfo: PackageInfo = pm.getPackageArchiveInfo(pathToApk, 0);

        if (Build.VERSION.SDK_INT >= 8) {
            // those two lines do the magic:
            packageInfo.applicationInfo.sourceDir = pathToApk;
            packageInfo.applicationInfo.publicSourceDir = pathToApk;
        }

        val label: CharSequence = pm . getApplicationLabel (packageInfo.applicationInfo);
        if(label!=null)
            return label.toString()
        else
            return ""
      
    }

    fun showDetailDialog(appInstance: App) {
        val factory = LayoutInflater.from(context)
        val detailDialogView = factory.inflate(R.layout.detail_dialog_view, null)

        detailDialogView.setBackgroundColor(Color.TRANSPARENT)
        val detailDialog = AlertDialog.Builder(context).create()
        detailDialog.setView(detailDialogView)

        detailDialog.getWindow().setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));




        detailDialog.show()

        val apkNameTV: TextView = detailDialogView!!.findViewById(R.id.apk_name_tv)

        val apkDateTV: TextView = detailDialogView.findViewById(R.id.apk_date_tv)

        val apkDescriptionTV: TextView = detailDialogView.findViewById(R.id.apk_description_tv)

        val thumbnailImage: ImageView = detailDialogView.findViewById(R.id.thumbnailImage)

        apkNameTV.text = appInstance.apk_name
        apkDateTV.text = appInstance.apk_date
        apkDescriptionTV.text = appInstance.apk_description

        apkDescriptionTV.movementMethod = ScrollingMovementMethod()


    }


    fun showApkAddDialog() {
        var addApkDialogFragment: AddApkDialogFragment = AddApkDialogFragment()


        addApkDialogFragment.show(fragmentManager, "Add Apk")
    }

    override fun onStart() {
        super.onStart()

        subscribe(appListViewModel.getAppList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    // showApp(it)
                    // Log.v("Data",it.toString())


                }
        )
        val sectionHeader: ArrayList<SectionHeader> = ArrayList()
        val alreadyDoneMonths: ArrayList<String> = ArrayList()
        subscribe(appListViewModel.getUniqueDates()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {

                    it.forEach { temp ->
                        Log.v("date", temp)
                        var tempMonthString: String? = ""
                        var tempYearString: String? = ""

                        if (!temp.equals("")) {
                            tempMonthString = temp.substring(0, 2)
                            tempYearString = temp.substring(6, 8)
                        }

                        var datePatternToSearch: String = ""
                        if (!tempMonthString.equals(""))
                            datePatternToSearch = tempMonthString + "/%/" + tempYearString


                        val childList: ArrayList<App> = ArrayList<App>()
                        subscribe(appListViewModel.getAppByDates(datePatternToSearch!!)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe {

                                    childList.addAll(it)

                                    if (childList.size != 0 && !alreadyDoneMonths.contains(datePatternToSearch)) {
                                        alreadyDoneMonths.add(datePatternToSearch)
                                        sectionHeader.add(SectionHeader(childList, temp))
                                    }
                                    showSectionedApp(sectionHeader)
                                }


                        )


                    }
                }
        )

        Log.v("Data completed", sectionHeader.toString())
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.app_list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val itemId = item!!.itemId;
        when (itemId) {
            R.id.refresh -> {
                /* subscribe(appListViewModel.getAppList()
                         .subscribeOn(Schedulers.io())
                         .observeOn(AndroidSchedulers.mainThread())
                         .subscribe {
                             showApp(it)
                         }
                 )*/

                subscribe(appListViewModel.getAppList()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            // showApp(it)
                            // Log.v("Data",it.toString())


                        }
                )
                val sectionHeader: ArrayList<SectionHeader> = ArrayList()
                val alreadyDoneMonths: ArrayList<String> = ArrayList()
                subscribe(appListViewModel.getUniqueDates()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {

                            it.forEach { temp ->
                                Log.v("date", temp)
                                var tempMonthString: String? = ""
                                var tempYearString: String? = ""

                                if (!temp.equals("")) {
                                    tempMonthString = temp.substring(0, 2)
                                    tempYearString = temp.substring(6, 8)
                                }

                                var datePatternToSearch: String = ""
                                if (!tempMonthString.equals(""))
                                    datePatternToSearch = tempMonthString + "/%/" + tempYearString


                                val childList: ArrayList<App> = ArrayList<App>()
                                subscribe(appListViewModel.getAppByDates(datePatternToSearch!!)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe {

                                            childList.addAll(it)

                                            if (childList.size != 0 && !alreadyDoneMonths.contains(datePatternToSearch)) {
                                                alreadyDoneMonths.add(datePatternToSearch)
                                                sectionHeader.add(SectionHeader(childList, temp))
                                            }
                                            showSectionedApp(sectionHeader)
                                        }


                                )


                            }
                        }
                )
            }
            R.id.size_change -> {
                val alertDialog: AlertDialog.Builder = AlertDialog.Builder(context)
                val layoutAlertView: View = LayoutInflater.from(context).inflate(R.layout.change_text_size, null)
                alertDialog.setView(layoutAlertView)

                val builder = alertDialog.create()

                val textSize: EditText = layoutAlertView.findViewById(R.id.textSizeValue)
                val changeButton: Button = layoutAlertView.findViewById(R.id.changeButton)

                changeButton.setOnClickListener {
                    universalTextSizeForList = textSize.text.toString().toInt()

                    /* subscribe(appListViewModel.getAppList()
                             .subscribeOn(Schedulers.io())
                             .observeOn(AndroidSchedulers.mainThread())
                             .subscribe {
                                 showApp(it)
                             }
                     )*/

                    subscribe(appListViewModel.getAppList()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe {
                                // showApp(it)
                                // Log.v("Data",it.toString())


                            }
                    )
                    val sectionHeader: ArrayList<SectionHeader> = ArrayList()
                    val alreadyDoneMonths: ArrayList<String> = ArrayList()
                    subscribe(appListViewModel.getUniqueDates()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe {

                                it.forEach { temp ->
                                    Log.v("date", temp)
                                    var tempMonthString: String? = ""
                                    var tempYearString: String? = ""

                                    if (!temp.equals("")) {
                                        tempMonthString = temp.substring(0, 2)
                                        tempYearString = temp.substring(6, 8)
                                    }

                                    var datePatternToSearch: String = ""
                                    if (!tempMonthString.equals(""))
                                        datePatternToSearch = tempMonthString + "/%/" + tempYearString


                                    val childList: ArrayList<App> = ArrayList<App>()
                                    subscribe(appListViewModel.getAppByDates(datePatternToSearch!!)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe {

                                                childList.addAll(it)

                                                if (childList.size != 0 && !alreadyDoneMonths.contains(datePatternToSearch)) {
                                                    alreadyDoneMonths.add(datePatternToSearch)
                                                    sectionHeader.add(SectionHeader(childList, temp))
                                                }
                                                showSectionedApp(sectionHeader)
                                            }


                                    )


                                }
                            }
                    )


                    builder.dismiss()
                }


                builder.show()


            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }

        return true

    }

    var downloadReference: Long = 0
    var downloading = true


    private fun downloadData(uri: Uri, apk_name: String, progressBar: ProgressBar, percentageTv: TextView, downloadStatusText: TextView): Long {


        // Create request for android download manager
        downloadManager = activity!!.getSystemService(DOWNLOAD_SERVICE) as DownloadManager?
        val request: DownloadManager.Request
        if (!apk_name.contains(".apk")) {
            request = DownloadManager.Request(uri.buildUpon().appendPath(apk_name + ".apk").build())
        } else {
            request = DownloadManager.Request(uri.buildUpon().appendPath(apk_name).build())

        }

        //Setting title of request
        request.setTitle("BB-Store")

        //Setting description of request
        request.setDescription(apk_name)




        if (apk_name.contains(".apk")) {
            request.setDestinationInExternalPublicDir(
                    folderName + File.separator, apk_name)
        } else {
            request.setDestinationInExternalPublicDir(
                    folderName + File.separator, apk_name + ".apk")
        }


        //Enqueue download and save into referenceId
        downloadReference = downloadManager!!.enqueue(request)




        Thread(Runnable {

            while (downloading) {

                val q = DownloadManager.Query()
                q.setFilterById(downloadReference)

                val cursor = downloadManager!!.query(q)



                cursor.moveToFirst()
                val bytes_downloaded = cursor.getInt(cursor
                        .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                val bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))

                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                    downloading = false
                }

                if (cursor != null)
                    downloadStatus(cursor, 100, downloadStatusText)

                val dl_progress = ((bytes_downloaded * 100L) / bytes_total).toInt()



                activity!!.runOnUiThread(Runnable {
                    progressBar.setProgress(dl_progress.toInt())

                    if (dl_progress != null && !dl_progress.toString().isEmpty()) {
                        percentageTv.text = dl_progress.toString() + "%"
                    }

                })


                cursor.close()
            }
        }).start()


        return downloadReference
    }


    private fun downloadStatus(cursor: Cursor, DownloadId: Long, downloadStatusText: TextView) {

        var reason = 0
        var filename = ""
        //column for download  status
        val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
        var status: Int = 0
        try {
            if (columnIndex != -1) {
                status = cursor.getInt(columnIndex)
            }
            val columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON)
            reason = cursor.getInt(columnReason)
            //get the download filename
            val filenameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME)
            filename = cursor.getString(filenameIndex)
        } catch (e: Exception) {
        }
        //column for reason code if the download failed or paused


        var statusText = ""
        var reasonText = ""

        when (status) {
            DownloadManager.STATUS_FAILED -> {
                statusText = "STATUS_FAILED"
                when (reason) {
                    DownloadManager.ERROR_CANNOT_RESUME -> reasonText = "ERROR_CANNOT_RESUME"
                    DownloadManager.ERROR_DEVICE_NOT_FOUND -> reasonText = "ERROR_DEVICE_NOT_FOUND"
                    DownloadManager.ERROR_FILE_ALREADY_EXISTS -> reasonText = "ERROR_FILE_ALREADY_EXISTS"
                    DownloadManager.ERROR_FILE_ERROR -> reasonText = "ERROR_FILE_ERROR"
                    DownloadManager.ERROR_HTTP_DATA_ERROR -> reasonText = "ERROR_HTTP_DATA_ERROR"
                    DownloadManager.ERROR_INSUFFICIENT_SPACE -> reasonText = "ERROR_INSUFFICIENT_SPACE"
                    DownloadManager.ERROR_TOO_MANY_REDIRECTS -> reasonText = "ERROR_TOO_MANY_REDIRECTS"
                    DownloadManager.ERROR_UNHANDLED_HTTP_CODE -> reasonText = "ERROR_UNHANDLED_HTTP_CODE"
                    DownloadManager.ERROR_UNKNOWN -> reasonText = "ERROR_UNKNOWN"
                }
            }
            DownloadManager.STATUS_PAUSED -> {
                statusText = "STATUS_PAUSED"
                when (reason) {
                    DownloadManager.PAUSED_QUEUED_FOR_WIFI -> reasonText = "PAUSED_QUEUED_FOR_WIFI"
                    DownloadManager.PAUSED_UNKNOWN -> reasonText = "PAUSED_UNKNOWN"
                    DownloadManager.PAUSED_WAITING_FOR_NETWORK -> reasonText = "PAUSED_WAITING_FOR_NETWORK"
                    DownloadManager.PAUSED_WAITING_TO_RETRY -> reasonText = "PAUSED_WAITING_TO_RETRY"
                }
            }
            DownloadManager.STATUS_PENDING -> statusText = "STATUS_PENDING"
            DownloadManager.STATUS_RUNNING -> statusText = "STATUS_RUNNING"
            DownloadManager.STATUS_SUCCESSFUL -> {
                statusText = "STATUS_SUCCESSFUL"
                reasonText = "Filename:\n" + filename

                fileDownloaded = true
            }
        }

        if (DownloadId == 100L) {


            activity!!.runOnUiThread(Runnable {
                downloadStatusText.text = statusText
                if (statusText.equals("STATUS_SUCCESSFUL"))
                    downloadButton!!.text = "Install"

            })


        }

    }


    /*fun showApp(appData: List<App>) {
        if (appData.size != 0) {

            var hashMap: HashMap<String, Array<String>> = HashMap()


            var appRecyclerAdapter: AppRecyclerAdapter = AppRecyclerAdapter(appData as ArrayList<App>, context, this@AppListFragment, universalTextSizeForList)
            appList.layoutManager = LinearLayoutManager(context)

            appList.addItemDecoration(SimpleItemDecorater(context))
            appList.adapter = appRecyclerAdapter;
        }
    }*/

    fun showSectionedApp(appData: List<SectionHeader>) {
        if (appData.size != 0) {


            Collections.sort(appData, kotlin.Comparator { l, r ->
                r.sectionText.compareTo(l.sectionText)
            })


            var appRecyclerAdapter: AdapterSectionRecycler = AdapterSectionRecycler(activity, appData, this@AppListFragment, universalTextSizeForList);
            appList.layoutManager = LinearLayoutManager(context)

            appList.addItemDecoration(SimpleItemDecorater(context))
            appList.adapter = appRecyclerAdapter;
        }
    }

    override fun onCLickListener(appObject: App) {
        fileDownloaded = false
        if (ContextCompat.checkSelfPermission(context!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            showDownloadDialog(appObject)
        else {


            ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 100);


        }
    }

    override fun onListItemClickListener(appObject: App?) {

        showDetailDialog(appObject!!)

    }
}