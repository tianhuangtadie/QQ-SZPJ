package com.example.qqsz;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    public static String mFilePath = Environment.getExternalStorageDirectory() + "/tencent/MobileQQ/chatpic/chatimg";
    public static String path = Environment.getExternalStorageDirectory() + "";
    private static String szpath = path + "/闪照破解";

    public static boolean getAll(String path) {
        File file = new File(path);
        //判断是不是文件夹
        if (!file.isDirectory()) {
            Log.e("ttm", "getAll: fileName=" + file.getName());
        } else {
            //是文件夹，便遍历出里面所有的文件（文件，文件夹）
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                //继续判断是文件夹还是文件
                if (!files[i].isDirectory()) {
                    Log.e("ttm", "getAll: fileNameI=" + files[i].getName());
                    if (files[i].getName().contains("_fp")) {
                        String newFileName = getMD5(files[i].getName()) + ".png";
                        renameFile(files[i].getParent(), files[i].getName(), newFileName);
                        try {
                            changeDirectory(newFileName, files[i].getParent(), isExistDir("/闪照破解"), true);
                            return isImageFile(szpath + "/" + newFileName);
                        } catch (IOException e) {
                            e.printStackTrace();
                            return false;
                        }
                    }
                } else {
                    getAll(path + "//" + files[i].getName());
                }
            }
        }
        return true;
    }

    public static boolean deleteAllFiles(File root) {
        File files[] = root.listFiles();
        if (files != null)
            for (File f : files) {
                if (f.isDirectory()) { // 判断是否为文件夹  
                    deleteAllFiles(f);
                    try {
                        f.delete();
                    } catch (Exception e) {
                    }
                } else {
                    if (f.exists()) { // 判断是否存在  
                        deleteAllFiles(f);
                        try {
                            f.delete();
                        } catch (Exception e) {
                        }
                    }
                }
            }
        return true;
    }


    public static boolean deleteFiles(String root) {
        File file = new File(root);
        return file.delete();
    }


    //剪切文件
    public static void changeDirectory(String filename, String oldpath, String newpath, boolean cover) {
        if (!oldpath.equals(newpath)) {
            File oldfile = new File(oldpath + "/" + filename);
            File newfile = new File(newpath + "/" + filename);
            if (newfile.exists()) {//若在待转移目录下，已经存在待转移文件
                if (cover)//覆盖
                    oldfile.renameTo(newfile);
                else
                    System.out.println("在新目录下已经存在：" + filename);
            } else {
                oldfile.renameTo(newfile);
            }
        }
    }


    public static String isExistDir(String saveDir) throws IOException {
        File downloadFile = new File(path, saveDir);
        if (!downloadFile.mkdirs()) {
            downloadFile.createNewFile();
        }
        String savePath = downloadFile.getAbsolutePath();
        return savePath;
    }

    //文件重命名 
    public static void renameFile(String path, String oldname, String newname) {
        if (!oldname.equals(newname)) {//新的文件名和以前文件名不同时,才有必要进行重命名 
            File oldfile = new File(path + "/" + oldname);
            File newfile = new File(path + "/" + newname);
            if (!oldfile.exists()) {
                return;//重命名文件不存在
            }
            if (newfile.exists())//若在该目录下已经有一个文件和新文件名相同，则不允许重命名 
                System.out.println(newname + "已经存在！");
            else {
                oldfile.renameTo(newfile);
            }
        } else {
            System.out.println("新文件名和旧文件名相同...");
        }
    }


    public static List<String> getSZ() {
        try {
            isExistDir("/闪照破解");
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<String> list = new ArrayList<>();
        File file = new File(szpath);
        //是文件夹，便遍历出里面所有的文件（文件，文件夹）
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (!files[i].isDirectory()) {
                Log.e("ttm", "getAll: fileNameI=" + files[i].getName());
                list.add(files[i].getParent() + "/" + files[i].getName());
            }
        }
        return list;
    }


    public static String getMD5(String string) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] result = md.digest(string.getBytes());
            StringBuffer sb = new StringBuffer();
            for (byte b : result) {
                int sign = b & 0xff;
                String str = Integer.toHexString(sign);
                if (str.length() == 1) {
                    sb.append("0");
                }
                sb.append(str);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    //获取apk的版本号 currentVersionCode
    public static void getAPPLocalVersion(Context ctx) {
        PackageManager manager = ctx.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo("com.tencent.mobileqq", 0);
            int localVersionCode = info.versionCode; // 版本号
            if (localVersionCode > 1345) {
                mFilePath = Environment.getExternalStorageDirectory() + "/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/chatpic/chatimg";
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static boolean isImageFile(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        if (options.outWidth == -1) {
            deleteFiles(filePath);
            return false;
        }
        return true;
    }
}