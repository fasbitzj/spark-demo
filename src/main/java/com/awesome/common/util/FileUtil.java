package com.awesome.common.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiang.zheng on 2017/11/9.
 */
public class FileUtil {

    static List<String> fileNameList = new ArrayList<>();

    public static String[] getFileArray(String path) {
        File file = new File(path);
        File[] array = file.listFiles();
        for(int i=0;i<array.length;i++){
            if(array[i].isFile()){
                fileNameList.add(array[i].getName());
            }else if(array[i].isDirectory()){
                getFileArray(array[i].getPath());
            }
        }
        return fileNameList.toArray(new String[fileNameList.size()]);
    }

    public static void main(String[] args) {
        String path = "F:\\sprark-demo-lib\\lib";
        String[] files = getFileArray(path);
        for (int i=0; i<files.length; i++) {
            System.out.println(files[i]);
        }
    }
}
