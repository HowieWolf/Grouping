package com.howie.grouping.plugin

import com.android.SdkConstants
import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformInvocation
import com.android.build.api.transform.Format
import com.android.build.gradle.internal.pipeline.TransformManager
import com.howie.grouping.plugin.utils.ClassUtils
import com.howie.grouping.plugin.utils.GroupingConstant
import com.howie.grouping.plugin.utils.PropertyUtils
import javassist.CannotCompileException
import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod
import javassist.NotFoundException
import org.apache.commons.io.FileUtils
import org.apache.commons.codec.digest.DigestUtils
import org.gradle.api.Project

import java.util.jar.JarEntry
import java.util.jar.JarFile

class GroupingTransform extends Transform {

    private Project project

    GroupingTransform(Project project) {
        this.project = project
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        // 一定要先加载所有类到 ClassPool
        List<CtClass> jarClassList = getAllJarClass(transformInvocation)
        // AppLike
        List<CtClass> applikeList = getAllAppLikeClass(jarClassList)
        // 输出
        outputToFile(transformInvocation, applikeList)
    }

    private List<CtClass> getAllJarClass(TransformInvocation transformInvocation) {
        List<CtClass> classList = new ArrayList<>()
        ClassPool classPool = ClassPool.getDefault()
        transformInvocation.inputs.forEach { input ->
            input.jarInputs.forEach { jarInput ->
                classPool.insertClassPath(jarInput.file.absolutePath)
                JarFile jarFile = new JarFile(jarInput.file)
                Enumeration<JarEntry> classes = jarFile.entries()
                while (classes.hasMoreElements()) {
                    JarEntry libClass = classes.nextElement()
                    String filePath = libClass.getName()
                    if (filePath.endsWith(SdkConstants.DOT_CLASS)) {
                        String className = ClassUtils.getClassNameFromFilePath(filePath, null)
                        CtClass clazz = classPool.get(className)
                        classList.add(clazz)
                    }
                }
                jarFile.close()
                //jar文件一般是第三方依赖库jar文件
                // 重命名输出文件（同目录copyFile会冲突）
                def jarName = jarInput.name
                def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                if (jarName.endsWith(SdkConstants.DOT_JAR)) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                }
                //生成输出路径
                def dest = transformInvocation.outputProvider.getContentLocation(jarName + md5Name,
                        jarInput.contentTypes, jarInput.scopes, Format.JAR)
                //将输入内容复制到输出
                FileUtils.copyFile(jarInput.file, dest)
            }
        }
        return classList
    }

    private List<CtClass> getAllAppLikeClass(List<CtClass> classList) {
        List<CtClass> result = new ArrayList<>()
        for (CtClass c : classList) {
            if (ClassUtils.isAppLike(c)) {
                result.add(c)
            }
        }
        return result
    }

    private void injectCode(CtClass application, List<CtClass> applikeList, String dirWithoutPackage) {
        application.defrost()
        try {
            CtMethod onCreateMethod = application.getDeclaredMethod("onCreate", null)
            String registerCode = ClassUtils.generateRegisterCode(applikeList)
            println(">>>>register code begin")
            println(registerCode)
            println(">>>>register code end")
            onCreateMethod.insertAfter(registerCode)
            application.writeFile(dirWithoutPackage)
        } catch (CannotCompileException | NotFoundException e) {
            throw new IllegalStateException("you must override onCreate method")
        }
    }

    private void outputToFile(TransformInvocation transformInvocation, List<CtClass> applikeList) {
        ClassPool classPool = ClassPool.getDefault()
        String applicationName = PropertyUtils.getApplicationClassName(project)
        CtClass application = null
        transformInvocation.inputs.each { TransformInput input ->
            //对类型为“文件夹”的input进行遍历
            input.directoryInputs.each { DirectoryInput directoryInput ->
                if (application == null) {
                    String dirPath = directoryInput.file.absolutePath
                    classPool.insertClassPath(dirPath)
                    File dir = new File(dirPath)
                    dir.eachFileRecurse { File file ->
                        String filePath = file.absolutePath
                        if (filePath.endsWith(SdkConstants.DOT_CLASS)
                                && ClassUtils.getClassNameFromFilePath(filePath, dirPath) == applicationName) {
                            application = classPool.get(applicationName)
                            injectCode(application, applikeList, dirPath)
                        }
                    }
                }

                def dest = transformInvocation.outputProvider.getContentLocation(directoryInput.name,
                        directoryInput.contentTypes,
                        directoryInput.scopes, Format.DIRECTORY)
                // 将input的目录复制到output指定目录
                FileUtils.copyDirectory(directoryInput.file, dest)
            }
        }
        if (application == null) {
            throw new IllegalStateException("you must extends android.app.Application and override onCreate method")
        }
    }

    @Override
    String getName() {
        return GroupingConstant.TRANSFORM_NAME
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return true
    }
}
