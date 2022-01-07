package com.example.qqsz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.UriPermission;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.support.annotation.RequiresApi;
import android.support.v4.provider.DocumentFile;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FileUtils {

    public final static int SDK_THRESHOLD = Build.VERSION_CODES.R;
    private final static boolean isOver11 = Build.VERSION.SDK_INT >= SDK_THRESHOLD;
    private static Context mContext;
    private static String basePath = Environment.getExternalStorageDirectory() + "";
    private static String buildVersion = "/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/chatpic/chatimg";
    private static String savePath = Environment.getExternalStorageDirectory() + "";
    private static String szpath = savePath + "/szpj";
    private static final Lock LOCK = new ReentrantLock();
    private static String mFilePath = buildVersion;

    public static String getFilePath() {
        return mFilePath;
    }

    public static boolean getAll(Context context, String path) {
        mContext = context;
        if (isOver11) {
            DocumentFile file = FileUtils.getDocumentFilePath(mContext, path);
            return getAll(context, file);
        } else {
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
                                changeDirectory(newFileName, files[i].getParent(), isExistDir("/szpj"), true);
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
                        boolean all = getAll(context, path + "/" + files[i].getName());
                        return all;
                    }
                }
            }
        }
        return false;
    }

    public static boolean getAll(Context context, DocumentFile file) {
        if (!file.isDirectory()) {
            Log.e("ttm", "getAll: fileName=" + file.getName());
        } else {
            //是文件夹，便遍历出里面所有的文件（文件，文件夹）
            DocumentFile[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                //继续判断是文件夹还是文件
                if (!files[i].isDirectory()) {
                    Log.e("ttm", "getAll: fileNameI=" + files[i].getName());
                    if (files[i].getName().contains("_fp")) {
                        String newFileName = getMD5(files[i].getName()) + ".png";
                        try {
                            files[i].renameTo(newFileName);
                            DocumentMv(context, isExistDir("/szpj"), files[i]);
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
                    boolean all = getAll(context, files[i]);
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
     * 剪切文件
     */
    public static void DocumentMv(Context context, String oldpath, DocumentFile documentFile) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(documentFile.getUri());
            try {
                int bytesum = 0;
                int byteread = 0;
                //读入原文件
                FileOutputStream fs = new FileOutputStream(oldpath + File.separator + documentFile.getName());
                byte[] buffer = new byte[1444];
                while ((byteread = inputStream.read(buffer)) != -1) {
                    bytesum += byteread;  //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            documentFile.delete();
        } catch (Exception e) {
            e.printStackTrace();
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
            isExistDir("/szpj");
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

    public static String getBuildVersion() {
        return buildVersion;
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


    public static String root = Environment.getExternalStorageDirectory().getPath() + "/";


    //判断是否已经获取了Data权限，改改逻辑就能判断其他目录，懂得都懂
    public static boolean isGrant(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            for (UriPermission persistedUriPermission : context.getContentResolver().getPersistedUriPermissions()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (persistedUriPermission.isReadPermission() && persistedUriPermission.getUri().toString().equals("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata%2Fcom.tencent.mobileqq%2FTencent%2FMobileQQ%2Fchatpic%2Fchatimg")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    //直接返回DocumentFile
    public static DocumentFile getDocumentFilePath(Context context, String path) {
        DocumentFile document = DocumentFile.fromTreeUri(context, Uri.parse(changeToUri(path)));
//        String[] parts = path.split("/");
//        for (int i = 3; i < parts.length; i++) {
//            document = document.findFile(parts[i]);
//        }
        return document;
    }


    public static boolean deleteAllFiles(DocumentFile documentFile, final MainActivity mainActivity) {
        final DocumentFile files[] = documentFile.listFiles();
        if (files != null && files.length > 0) {
            if (files.length > 30) {
                Toast.makeText(mainActivity.getApplicationContext(), "长时间未初始化时间会比较长,耐心等待", Toast.LENGTH_LONG).show();
            }
            AtomicInteger scount = new AtomicInteger();
            List<DocumentFile> list = Arrays.asList(files);
//            for (int i = 0; i < 3000; i++) {
//                list.add(documentFile);
//            }
            //处理数据数量
            int listSize = list.size();
            //跑批分页大小
            int EXPIRED_PAGE_SIZE = 5;
            //线程数
            int runSize;
            if (listSize % EXPIRED_PAGE_SIZE == 0) {
                runSize = (listSize / EXPIRED_PAGE_SIZE);
            } else {
                runSize = (listSize / EXPIRED_PAGE_SIZE) + 1;
            }
            List<List<DocumentFile>> handleLists = new ArrayList<>();
            //建立线程
            for (int i = 0; i < runSize; i++) {
                List<DocumentFile> handleList;
                //计算每一个线程对应的数据
                if ((i + 1) == runSize) {
                    int startIndex = i * EXPIRED_PAGE_SIZE;
                    int endIndex = list.size();
                    handleList = list.subList(startIndex, endIndex);
                } else {
                    int startIndex = i * EXPIRED_PAGE_SIZE;
                    int endIndex = (i + 1) * EXPIRED_PAGE_SIZE;
                    handleList = list.subList(startIndex, endIndex);
                }
                handleLists.add(handleList);
            }
            for (List<DocumentFile> fs : handleLists) {
                ThreadPoolManager.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        final int size = fs.size();
                        for (int i = 0; i < size; ++i) {
                            scount.getAndIncrement();
                            int progress = (int) ((float) scount.get() / (listSize) * 100);
                            System.out.println(progress);
                            mainActivity.progressbarShow(progress, scount.get(), listSize);
                            DocumentFile f = fs.get(i);
                            Log.d("ttm", "线程中：" + f.getName());
                            f.delete();
//                            try {
//                                Thread.sleep(300);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
                        }
//                        mainActivity.progressbarDisShow();
                    }
                });
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        if (ThreadPoolManager.getInstance().isThreadPoolExeComplete()) {
                            Log.d("ttm", "最终计算 count ----->" + scount.get());
                            mainActivity.progressbarDisShow();
                            return;
                        }
                    }
                }
            }).start();
        } else {
            mainActivity.ToastShow("初始化完毕");
        }
        return true;
    }

    //转换至uriTree的路径
    public static String changeToUri(String path) {
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        String path2 = path.replace("/storage/emulated/0/", "").replace("/", "%2F");
        return "content://com.android.externalstorage.documents/tree/primary%3A" + path2;
    }

    //转换至uriTree的路径
    public static DocumentFile getDoucmentFile(Context context, String path) {
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        String path2 = path.replace("/storage/emulated/0/", "").replace("/", "%2F");
        return DocumentFile.fromSingleUri(context, Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3A" + path2));
    }


    //转换至uriTree的路径
    public static String changeToUri2(String path) {
        String[] paths = path.replaceAll("/storage/emulated/0/Android/data", "").split("/");
        StringBuilder stringBuilder = new StringBuilder("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3AAndroid%2Fdata");
        for (String p : paths) {
            if (p.length() == 0) continue;
            stringBuilder.append("%2F").append(p);
        }
        return stringBuilder.toString();

    }


    //转换至uriTree的路径
    public static String changeToUri3(String path) {
        path = path.replace("Android/data/", "").replace("/", "%2F");
        return ("content://com.android.externalstorage.documents/tree/primary%3A" + path);

    }

    //获取指定目录的权限
    public static void startFor(String path, Activity context, int REQUEST_CODE_FOR_DIR) {
        Uri uri = Uri.parse("content://com.android.externalstorage.documents/document/primary:" + URLEncoder.encode(path));
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);
        }
        context.startActivityForResult(intent, REQUEST_CODE_FOR_DIR);
    }

    //直接获取data权限，推荐使用这种方案
    public static void startForRoot(Activity context, int REQUEST_CODE_FOR_DIR) {
        Uri uri1 = Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata");
//        DocumentFile documentFile = DocumentFile.fromTreeUri(context, uri1);
        String uri = changeToUri(Environment.getExternalStorageDirectory().getPath());
        uri = uri + "/document/primary%3A" + Environment.getExternalStorageDirectory().getPath().replace("/storage/emulated/0/", "").replace("/", "%2F");
        Uri parse = Uri.parse(uri);
        DocumentFile documentFile = DocumentFile.fromTreeUri(context, uri1);
        Intent intent1 = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent1.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
        intent1.putExtra(DocumentsContract.EXTRA_INITIAL_URI, documentFile.getUri());
        context.startActivityForResult(intent1, REQUEST_CODE_FOR_DIR);
    }
}