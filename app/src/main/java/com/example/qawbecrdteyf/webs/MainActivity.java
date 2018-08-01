package com.example.qawbecrdteyf.webs;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.shockwave.pdfium.PdfDocument;

import java.io.File;
import java.io.IOException;
import java.util.List;

import es.voghdev.pdfviewpager.library.PDFViewPager;
import es.voghdev.pdfviewpager.library.RemotePDFViewPager;
import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter;
import es.voghdev.pdfviewpager.library.remote.DownloadFile;

public class MainActivity extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener {

    String url = "http://www.iitbbs.ac.in/transportation.php";
    String url1 = "http://www.iitbbs.ac.in/transportation-fle/";
    ProgressDialog mProgressDialog;
    PDFView pdfView;
    Integer pageNumber = 0;
    String pdfFileName;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button getButton = (Button) findViewById(R.id.Link_button);
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.e("Permission error","You have permission");
                //downloadManager.enqueue(request);
                //return true;
            } else {

                Log.e("Permission error","You have asked for permission");
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                //downloadManager.enqueue(request);
                //return false;
            }
        }
        else { //you dont need to worry about these stuff below api level 23
            Log.e("Permission error", "You already have the permission");
            //downloadManager.enqueue(request);
            //return true;
        }

        getButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new GetSchedule().execute();
            }
        });


        pdfView = (PDFView)findViewById(R.id.pdfView);

    }

    @Override
    public void onDestroy(){

        super.onDestroy();
        File f = new File("/storage/emulated/0/Download/transportschedule.pdf");
        if(f.exists()){
            boolean deleted = f.delete();
        }

    }

    @Override
    public void loadComplete(int nbPages) {

    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
        setTitle(String.format("%s %s / %s", pdfFileName, page + 1, pageCount));
    }

    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            Log.e("TAG", String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }

    private class GetSchedule extends AsyncTask<Void, Void, Void>{

        String linkStr = "";

        @Override
        protected void onPreExecute() {

            Log.d("Error in PE", "Why?");

            super.onPreExecute();
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setTitle("Android Basic JSoup Tutorial");
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
            Log.d("Error in PE", "Why?");
        }


        @Override
        protected Void doInBackground(Void... voids) {
            try{

                Document document = Jsoup.connect(url).get();
                Elements link = document.select("a:contains(Click here):eq(1)");
                //Element link =
                String linkHref = link.attr("href");
                linkStr = linkHref.toString();
                String linkStr1 = processString(linkStr);
                linkStr = url1+linkStr1;
                Log.d("umm", linkStr);
                Log.d("msg is here", linkStr);
                DownloadManager downloadManager;
                downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uri = Uri.parse(linkStr);
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                File file = new File("/storage/emulated/0/Download/transportschedule.pdf");
                if(!file.exists()){
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS.toString(), "transportschedule"+".pdf");
                    request.setMimeType("application/pdf");
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                == PackageManager.PERMISSION_GRANTED) {
                            Log.e("Permission error","You have permission");
                            downloadManager.enqueue(request);
                            //return true;
                        } else {

                            Log.e("Permission error","You have asked for permission");
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                            downloadManager.enqueue(request);
                            //return false;
                        }
                    }
                    else { //you dont need to worry about these stuff below api level 23
                        Log.e("Permission error", "You already have the permission");
                        downloadManager.enqueue(request);
                        //return true;
                    }
                }else{
                    //public void boolean deleted = file.delete();
                    Log.d("deleted", "deleted");
                }

                Log.d("down", "eded");


            }catch(IOException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Set description into TextView
            String s = "loss";
            File f = new File("/storage/emulated/0/Download/transportschedule.pdf");
            if(f.exists()){
               s = "win";
            }
            Log.d("bool", s );
            Uri puri = Uri.fromFile(new File("/storage/emulated/0/Download/transportschedule.pdf"));
            Log.d("please", puri.toString());
            pdfView.fromUri(puri) // all pages are displayed by default
                    .enableSwipe(true) // allows to block changing pages using swipe
                    .swipeHorizontal(false)
                    .enableDoubletap(true)
                    .defaultPage(0)
                    // allows to draw something on the current page, usually visible in the middle of the screen
                    .enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
                    .password(null)
                    .scrollHandle(null)
                    .enableAntialiasing(true) // improve rendering a little bit on low-res screens
                    // spacing between pages in dp. To define spacing color, set view background
                    .spacing(0)
                    .load();
            mProgressDialog.dismiss();
        }

    }

    private String processString(String linkStr) {
        int n = linkStr.length();
        StringBuilder sb = new StringBuilder();
        for(int  i = n-1; i>-1; i--){
            if(linkStr.charAt(i) == '/'){
                break;
            }else{
                sb.append(linkStr.charAt(i));
            }
        }

        sb.reverse();
        return sb.toString();
    }
}
