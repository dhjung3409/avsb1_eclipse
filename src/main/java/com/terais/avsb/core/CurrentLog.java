package com.terais.avsb.core;

import com.terais.avsb.module.FilePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class CurrentLog {

    private static final Logger logger = LoggerFactory.getLogger(CurrentLog.class);

    public static List<String> currentLog = new ArrayList<String>();

    public static List<String> getCurrentLog(File file){
        currentLog.clear();
        RandomAccessFile raFile;
        long raFileSize=0;
        try {
            raFile = new RandomAccessFile(file,"r");
            raFileSize = raFile.length();
            raFileSize--;
            if(raFileSize>1) {
                currentLog = getLines(currentLog, 50, raFile, raFileSize);
                logger.debug("currentLog: " + currentLog.toString());
            }
            if(raFile!=null) {
                raFile.close();
            }
        } catch (FileNotFoundException e) {
            logger.error("LastLine FileNotFoundException: "+e.getMessage());
        } catch (IOException e) {
            logger.error("LastLine IOException: "+e.getMessage());
        }finally {
            raFile=null;
            raFileSize=0;
        }
        return currentLog;
    }

    public static List<String> getLines(List<String> lines,int counting,RandomAccessFile raFile,long raFileSize){
        StringBuilder builder = new StringBuilder();
        int count = 0;
        char c;
        String lastline;
        raFileSize--;
        try {
            while(true){
                if(count>=counting){
                    break;
                }
                raFile.seek(raFileSize);
                c= (char)raFile.read();
                if(c=='\n'){
                    lastline = new String(builder.reverse().toString().getBytes(),"UTF-8");
                    lastline = lastline.trim();
                    if(lastline.contains(FilePath.dummyFile)||lastline.length()==0){
                        builder.delete(0, builder.length());
                        raFileSize--;
                        continue;
                    }

                    lines.add(lastline);
                    logger.debug("LastLines: "+lines.get(count));

                    builder.delete(0,builder.length());
                    count++;

                    raFileSize--;
                    continue;

                }else if(raFileSize==0){
                    builder.append(c);
                    lastline = new String(builder.reverse().toString().getBytes(),"UTF-8");
                    logger.debug("print LastLine: "+ lastline );
                    if(lastline.contains(FilePath.dummyFile)==false) {
                        lines.add(lastline);
                    }
                    break;
                }
                builder.append(c);
                raFileSize--;
            }
            if(builder!=null) {
                builder.delete(0,builder.length());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            builder = null;
        }
        return lines;
    }
}
