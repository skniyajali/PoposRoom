package com.niyaj.poposroom.utils;

import androidx.core.content.FileProvider;

import com.niyaj.poposroom.R;

public class MyFileProvider extends FileProvider {
    public MyFileProvider() {
        super(R.xml.file_paths);
    }
}