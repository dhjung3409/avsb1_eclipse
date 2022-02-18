package com.terais.avsb.service;

import java.io.File;
import java.io.FileWriter;

public interface HauriScanService {
    void scanVrSDK(String path);
//    void scanFile(String path);
    void deletePath();
    void writePath(File dir);
    void dirSearch(File dir, FileWriter fw);
}
