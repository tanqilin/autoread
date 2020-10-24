package com.example.appexmple;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnPageChangeListener;

/*
 * 使用教程查看页面
 */
public class HelpFragment extends Fragment {
    private PDFView pdfView;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_help, container, false);
    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pdfView = view.findViewById(R.id.pdf_view);
        pdfView.fromAsset("usehelp.pdf")
                .defaultPage(1)
                .enableSwipe(true)
                .swipeVertical(true)
                .load();
    }
}