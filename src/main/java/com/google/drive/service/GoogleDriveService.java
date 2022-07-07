package com.google.drive.service;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.*;
import com.google.api.services.drive.model.File;
import com.google.drive.config.*;
import org.springframework.stereotype.*;

import java.io.*;
import java.util.*;

@Service
public class GoogleDriveService {

    private static final String FIELDS = "id,name,mimeType,parents";

    private final GoogleDriveConfig driveService;

    public GoogleDriveService(GoogleDriveConfig driveService) {
        this.driveService = driveService;
    }

    public List<Object> listFiles(String username) throws IOException {
        Drive drive = driveService.getDriveInstance(username);
        List<Object> responseList = new ArrayList<>();
        FileList fileList = drive.files().list().setFields("files(" + FIELDS + ")").execute();
        for (File file : fileList.getFiles()) {
            List list = new ArrayList();
            list.add(file.getId());
            list.add(file.getName());
            list.add(file.getThumbnailLink());
            responseList.add(list);
        }
        return responseList;
    }
}
