package com.howie.grouping.plugin.utils

import com.android.SdkConstants
import javassist.CtClass
import javassist.NotFoundException

import java.util.regex.Matcher

class ClassUtils {

    public static String generateRegisterCode(List<CtClass> appLikeClazz) {
        if (appLikeClazz == null || appLikeClazz.isEmpty()) {
            return ""
        }
        StringBuilder registerCode = new StringBuilder()
        for (CtClass ctClass : appLikeClazz) {
            registerCode.append(GroupingConstant.GROUPING_NAME)
            registerCode.append(".register(")
            registerCode.append("new " + ctClass.getName() + "()")
            registerCode.append(");")
        }
        return registerCode.toString()
    }

    public static boolean isAppLike(CtClass ctClass) {
        try {
            for (CtClass ctClassInterface : ctClass.getInterfaces()) {
                if (GroupingConstant.APPLIKE_NAME == ctClassInterface.name) {
                    return true
                }
            }
        } catch (NotFoundException e) {

        }
        return false
    }

    /**
     * 从文件路径中拿全量类名
     * @param classPath
     * @param dirPath
     * @return
     */
    public static String getClassNameFromFilePath(String classPath, String dirPath) {
        String sp = dirPath == null ? "/" : "\\"
        int startIndex = dirPath == null ? 0 : dirPath.endsWith(sp) ? dirPath.length() : dirPath.length() + 1
        return classPath
                .substring(startIndex, classPath.length() - SdkConstants.DOT_CLASS.length())
                .replace(sp, ".")
    }
}
