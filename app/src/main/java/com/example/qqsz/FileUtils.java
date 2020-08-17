package com.example.qqsz;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    private static Context mContext;
    private static String basePath = Environment.getExternalStorageDirectory() + "";
    private static String buildVersion = "/tencent/MobileQQ/chatpic/chatimg";
    private static String savePath = Environment.getExternalStorageDirectory() + "";
    private static String szpath = savePath + "/闪照破解";

    public static String mFilePath = basePath + buildVersion;

    public static boolean getAll(Context context, String path) {
        mContext = context;
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
                            boolean imageFile = isImageFile(szpath + "/" + newFileName);
                            if (imageFile) {
                                scanFile(context, szpath + "/" + newFileName);
                            }
                            return imageFile;
                        } catch (IOException e) {
                            e.printStackTrace();
                            return false;
                        }
                    }
                } else {
                    boolean all = getAll(context, path + "//" + files[i].getName());
                    return all;
                }
            }
        }
        return false;
    }

    /**
     * 删除指定目录下所有文件
     */
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

    /**
     * 删除指定文件
     */
    public static boolean deleteFiles(String root) {
        File file = new File(root);
        return file.delete();
    }


    /**
     * 剪切文件
     */
    public static void changeDirectory(String filename, String oldpath, String newpath, boolean cover) {
        if (!oldpath.equals(newpath)) {
            File newfile = new File(newpath + "/" + filename);
//            Toast.makeText(mContext, "移动后的路径=" + newfile.getPath(), Toast.LENGTH_SHORT).show();
            moveFile(oldpath + "/" + filename, newpath + "/" + filename);
        }
    }

    /**
     * 判断文件夹是否存在，不存在创建
     */
    public static String isExistDir(String saveDir) throws IOException {
        File downloadFile = new File(savePath, saveDir);
        if (!downloadFile.mkdirs()) {
            downloadFile.createNewFile();
        }
        String savePath = downloadFile.getAbsolutePath();
        return savePath;
    }

    /**
     * 文件重命名
     */
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
//                Toast.makeText(mContext, "重命名成功！", Toast.LENGTH_SHORT).show();
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
                buildVersion = "/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/chatpic/chatimg";
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置基础路径
     */
    public static boolean setBasePath(Context context, boolean isSeparation) {
        if (isSeparation) {
            String brand = android.os.Build.BRAND;
            Toast.makeText(context, brand.toLowerCase(), Toast.LENGTH_SHORT).show();
            switch (brand.toLowerCase()) {
                case "honor":
                    basePath = basePath.replace("0", "128");
                    break;
                case "huawei":
                    basePath = basePath.replace("0", "128");
                    mFilePath = basePath + buildVersion;
                    if (!new File(mFilePath).isDirectory()) {
                        basePath = Environment.getExternalStorageDirectory().toString().replace("0", "10");
                    }
                    mFilePath = basePath + buildVersion;
                    if (!new File(mFilePath).isDirectory()) {
                        basePath = Environment.getExternalStorageDirectory().toString().replace("0", "999");
                    }
                    break;
                case "vivo":
                case "oppo":
                    basePath = basePath.replace("0", "999");
                    mFilePath = basePath + buildVersion;
                    if (!new File(mFilePath).isDirectory()) {
                        basePath = Environment.getExternalStorageDirectory() + "/AppClone";
                    }
                    break;
                case "xiaomi":
                case "oneplus":
                    basePath = basePath.replace("0", "999");
                    break;
                case "360":
                case "meizu":
                case "samsung":
                    basePath = Environment.getExternalStorageDirectory().toString();
                    break;
            }
        } else {
            basePath = Environment.getExternalStorageDirectory().toString();
        }
        mFilePath = basePath + buildVersion;
        return new File(mFilePath).isDirectory();
    }

    /**
     * 判断是不是图片文件
     */
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

    /**
     * 移动图片到相册
     */
    public static void scanFile(Context context, String filePath) {
        //保存图片后发送广播通知更新数据库
        Uri uri = Uri.fromFile(new File(filePath));
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
    }

    /**
     * 复制文件
     *
     * @param oldPath
     * @param newPath
     */
    public static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
//            Toast.makeText(mContext, "移动前的路径=" + oldfile.getPath(), Toast.LENGTH_SHORT).show();
            if (oldfile.exists()) {  //文件存在时    
                //读入原文件     
                InputStream inStream = new FileInputStream(oldPath);
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread;  //字节数 文件大小      
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }
    }

    /**
     * 移动文件到指定路径
     *
     * @param oldPath
     * @param newPath
     */
    public static void moveFile(String oldPath, String newPath) {
        copyFile(oldPath, newPath);
        deleteFiles(oldPath);
    }
}