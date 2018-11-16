package com.example.yamashita.packagemanager;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SubActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);


        // 端末にインストール済のアプリケーション一覧情報を取得
        final PackageManager pm = getPackageManager();
        final int flags = PackageManager.GET_UNINSTALLED_PACKAGES | PackageManager.GET_DISABLED_COMPONENTS;
        final List<ApplicationInfo> installedAppList = pm.getInstalledApplications(flags);


        Intent intent = getIntent();
        int position = intent.getIntExtra("position", 0);
        ApplicationInfo item = installedAppList.get(position);
        PackageManager pManager = getPackageManager();
        PackageInfo packageInfo = null;

        try {
            packageInfo = pManager.getPackageInfo(item.packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String label = item.loadLabel(pm).toString();
        setTitle(label);

        // packageInfoから情報を取り、TextViewにセットする。
        TextView tv_packageName = findViewById(R.id.textView_packageName);
        String packageName = packageInfo.packageName;
        tv_packageName.setText("packageName : " + packageName);

        TextView tv_versionCode = findViewById(R.id.textView_versionCode);
        int versionCode = packageInfo.versionCode;
        tv_versionCode.setText("versionCode : " + versionCode);

        TextView tv_versionName = findViewById(R.id.textView_versionName);
        String versionName = packageInfo.versionName;
        tv_versionName.setText("versionName : " + versionName);

        TextView tv_targetSdkVersion = findViewById(R.id.textView_targetSdkVersion);
        int targetSdkVersion = item.targetSdkVersion;
        tv_targetSdkVersion.setText("targetSdkVersion : " + targetSdkVersion);

        TextView tv_firstInstallTime = findViewById(R.id.textView_firstInstallTime);
        long firstInstallTimeLong = packageInfo.firstInstallTime;
        // longをカレンダー型へ変換
        String firstInstallTime = convertLongToYyyymmddhhmmss(firstInstallTimeLong);
        tv_firstInstallTime.setText("firstInstallTime : " + firstInstallTime);

        TextView tv_lastUpdateTime = findViewById(R.id.textView_lastUpdateTime);
        long lastUpdateTimeLong = packageInfo.lastUpdateTime;
        // longをカレンダー型へ変換
        String lastUpdateTime = convertLongToYyyymmddhhmmss(lastUpdateTimeLong);
        tv_lastUpdateTime.setText("lastUpdateTime : " + lastUpdateTime);


        TextView tv_usesPermissions = findViewById(R.id.textView_usesPermissions);
        try {
            packageInfo = pm.getPackageInfo(packageName, pm.GET_PERMISSIONS);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String[] usesPermissionsArray = packageInfo.requestedPermissions;
        int[] usesPermissionsFlagsArray = packageInfo.requestedPermissionsFlags;
        tv_usesPermissions.setText("uses-Permissions : ◯granted  ×not granted\n");
        if(usesPermissionsArray != null) {
            for (int i = 0; i < usesPermissionsArray.length; i++) {
                String usesPermissions = usesPermissionsArray[i];
                int usesPermissionsFlags = usesPermissionsFlagsArray[i];
                if ((usesPermissionsFlags & packageInfo.REQUESTED_PERMISSION_GRANTED) != 0) {
                    tv_usesPermissions.append("◯");
                } else {
                    tv_usesPermissions.append("×");
                }
                tv_usesPermissions.append(usesPermissions + "\n");
            }
        } else {
            tv_usesPermissions.setText("uses-Permissions : null");
        }


        TextView tv_permission = findViewById(R.id.textView_permission);
        try {
            packageInfo = pm.getPackageInfo(packageName, pm.GET_PERMISSIONS);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        PermissionInfo[] permissionInfoArray = packageInfo.permissions;
        tv_permission.setText("permission : \n");
        if(permissionInfoArray != null) {
            for (int i = 0; i < permissionInfoArray.length; i++) {
                String permission = permissionInfoArray[i].name;
                int permissionProtection;
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    permissionProtection = permissionInfoArray[i].getProtection();
                } else {
                    permissionProtection = permissionInfoArray[i].protectionLevel & PermissionInfo.PROTECTION_MASK_BASE;
                }
                switch (permissionProtection) {
                    case PermissionInfo.PROTECTION_NORMAL:
                        tv_permission.append("[Base Protection Level:Normal]");
                        break;
                    case PermissionInfo.PROTECTION_DANGEROUS:
                        tv_permission.append("[Base Protection Level:Dangerous]");
                        break;
                    case PermissionInfo.PROTECTION_SIGNATURE:
                        tv_permission.append("[Base Protection Level:Signature]");
                        break;
                    case PermissionInfo.PROTECTION_SIGNATURE_OR_SYSTEM:
                        tv_permission.append("[Base Protection Level:Signature or System]");
                        break;
                }
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    permissionProtection = permissionInfoArray[i].getProtectionFlags();
                } else {
                    permissionProtection = permissionInfoArray[i].protectionLevel;
                }
                StringBuilder protectionLevel = new StringBuilder();
                if ((permissionProtection & PermissionInfo.PROTECTION_FLAG_APPOP) != 0) {
                    protectionLevel.append("|appop");
                }
                if ((permissionProtection & PermissionInfo.PROTECTION_FLAG_DEVELOPMENT) != 0) {
                    protectionLevel.append("|development");
                }
                if ((permissionProtection & PermissionInfo.PROTECTION_FLAG_INSTALLER) != 0) {
                    protectionLevel.append("|installer");
                }
                if ((permissionProtection & PermissionInfo.PROTECTION_FLAG_INSTANT) != 0) {
                    protectionLevel.append("|instant");
                }
                if ((permissionProtection & PermissionInfo.PROTECTION_FLAG_PRE23) != 0) {
                    protectionLevel.append("|pre23");
                }
                if ((permissionProtection & PermissionInfo.PROTECTION_FLAG_PREINSTALLED) != 0) {
                    protectionLevel.append("|preinstalled");
                }
                if ((permissionProtection & PermissionInfo.PROTECTION_FLAG_PRIVILEGED) != 0) {
                    protectionLevel.append("|privileged");
                }
                if ((permissionProtection & PermissionInfo.PROTECTION_FLAG_PRIVILEGED) != 0) {
                    protectionLevel.append("|runtime");
                }
                if ((permissionProtection & PermissionInfo.PROTECTION_FLAG_SETUP) != 0) {
                    protectionLevel.append("|setup");
                }
                if ((permissionProtection & PermissionInfo.PROTECTION_FLAG_VERIFIER) != 0) {
                    protectionLevel.append("|verifier");
                }
                if (!protectionLevel.toString().equals("")) {
                    protectionLevel.setCharAt(0, '[');
                    protectionLevel.append(']');
                    tv_permission.append(protectionLevel);
                }
                tv_permission.append("\n" + permission + "\n");
            }
        } else {
            tv_permission.setText("permission : null");
        }



        TextView tv_sourceDir = findViewById(R.id.textView_sourceDir);
        String sourceDir = item.sourceDir;
        tv_sourceDir.setText("sourceDir : " + sourceDir);




        TextView tv_enabled = findViewById(R.id.textView_enabled);
        boolean enabled = item.enabled;
        if (enabled) {
            tv_enabled.setText("enabled : enabled");
        } else {
            tv_enabled.setText("enabled : disabled");
        }




        TextView tv_updated = findViewById(R.id.textView_updated);
        if ((item.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
            tv_updated.setText("updated(System App) : ◯");
            tv_updated.append("\n(firstInstallTime : " + convertLongToYyyymmddhhmmss(packageInfo.firstInstallTime) + "\nlastUpdateTime" + convertLongToYyyymmddhhmmss(packageInfo.lastUpdateTime)+ ")");
        } else {
            tv_updated.setText("updated(System App) : ×");
        }
        if ((item.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
            if (packageInfo.firstInstallTime != packageInfo.lastUpdateTime) {
                tv_updated.setText("updated : ◯");
                tv_updated.append("\n(firstInstallTime : " + convertLongToYyyymmddhhmmss(packageInfo.firstInstallTime) + "\nlastUpdateTime" + convertLongToYyyymmddhhmmss(packageInfo.lastUpdateTime)+ ")");
            } else {
                tv_updated.setText("updated : ×");
            }

        }

    }

    static DateFormat yyyymmddhhmmss = new SimpleDateFormat("yyyy/MM/dd  HH:mm:ss");
    public static String convertLongToYyyymmddhhmmss(Long date) {
        return yyyymmddhhmmss.format(new Date(date));
    }


}




