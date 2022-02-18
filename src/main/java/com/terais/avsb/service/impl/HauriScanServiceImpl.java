package com.terais.avsb.service.impl;

import com.terais.avsb.lib.ViRobotLog;
import com.terais.avsb.module.FilePath;
import com.terais.avsb.service.HauriScanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;

/**
 * VrSDK를 사용한 파일 검사 클래스
 */
@Service
public class HauriScanServiceImpl implements HauriScanService {
//    private static ViRobotSDK viRobotSDK = new ViRobotSDK();

    private static final Logger logger = LoggerFactory.getLogger(HauriScanServiceImpl.class);


    /**
     * 검사할 파일 혹은 폴더의 경로를 입력받아 파일에 대해 검사를 진행하는 메소드
     * @param path 검사를 진행할 파일의 경로
     */
    public void scanVrSDK(String path){
        File file = new File(path);
        if(file.isDirectory()==true){
            BufferedReader bfr=null;
            try {
                writePath(file);
                bfr = new BufferedReader(new FileReader(FilePath.directoryFileList));
                String line;
                while((line= bfr.readLine())!=null){
                    ViRobotLog.scanFile(line);
                }
            } catch (FileNotFoundException e) {
                logger.error("DirectoryFileList File Not Found Exception : "+e.getMessage());
            } catch (IOException e) {
                logger.error("DirectoryFileList IOException : "+e.getMessage());
            }finally{
                if(bfr!=null){
                    try {
                        bfr.close();
                    } catch (IOException e) {
                        logger.error("scanVrSDK BufferedReader Close IOException : "+e.getMessage());
                    }
                }
                deletePath();
            }

        }else{
            ViRobotLog.scanFile(path);
        }

    }

//    /**
//     * ViRobotSDK를 이용해서 파일을 검사하는 메소드
//     * @param path 검사할 파일의 경로
//     */
//    public void scanFile(String path){
//        String now = SimpleDateFormatCore.sdf3.format(new Date());
//        VirInfo info = new VirInfo();
//        int result = 0;
//        result = viRobotSDK.JVRProbeInto(info,path.getBytes());
//        if(result==3){
//            viRobotSDK.JVRGetVirusName(info);
//        }
//        viRobotSDK.JVRGetEngVer(info);
//        String viResult = info.getVirusName();
//        viResult = viResult==null?"Normal":viResult;
//        File file = new File(path);
//        //2022/02/04 11:31:59 [3652:40586640] target=TERA_SYSTEM_CHECK_DUMMY_FILE, targetSize=0, ClientIP=LOCAL, scanTime=2022/02/04 11:31:59, scanResult=SCAN_FAILED
//        String rs = now+ " [] target=" +path+ ", targetSize=" + file.length() + ", ClientIP=LOCAL, scanTime=" + now + ", scanResult=" + viResult+"\n";
//        FileWriter fw=null;
//        try {
//            fw = new FileWriter(FilePath.hauriLogFile,true);
//            fw.write(rs);
//            fw.flush();
//        } catch (IOException e) {
//            logger.error("Hauri Scan File IOException: "+e.getMessage());
//        }finally{
//            if(fw!=null){
//                try {
//                    fw.close();
//                } catch (IOException e) {
//                    logger.error("Hauri Scan File FileWriter Close Fail: "+e.getMessage());
//                }
//            }
//        }
//    }

    public void deletePath(){
        File file = new File(FilePath.directoryFileList);
        if(file.exists()==true){
            file.delete();
        }
    }

    /**
     * 폴더 하위에 존재하는 파일의 목록을 텍스트파일에 입력시키는 메소드
     * @param dir 폴더의 File 객체
     */
    public void writePath(File dir){
        File file = new File(FilePath.directoryFileList);
        FileWriter fw = null;
        try {
            fw = new FileWriter(file);
            dirSearch(dir,fw);
            fw.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(fw!=null){
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 폴더 하위의 파일이 폴더인지 혹은 파일인지 확인해 파일이라면 FileWriter 객체로 써내리는 메소드
     * @param dir 폴더의 경로
     * @param fw 입력에 사용할 FileWriter 객체
     */
    public void dirSearch(File dir,FileWriter fw){
        if(dir.isDirectory()==true){
            File[] files = dir.listFiles();
            for(File file : files){
                if(file.isDirectory()==true){
                    dirSearch(file,fw);
                }else{
                    try {
                        fw.write(file.getCanonicalPath()+"\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }


}
