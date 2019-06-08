package com.howie.grouping.plugin.task

import com.howie.grouping.plugin.utils.GroupingConstant
import org.gradle.api.tasks.Sync

import javax.inject.Inject

/**
 * 使用 Sync 而不使用 Copy
 * 因为 Sync 会清空 destination 文件夹下的所有文件
 */
class GroupingCopyApiTask extends Sync {

    @Inject
    GroupingCopyApiTask(String[] components, String serviceModule) {
        // 特别注意这里要使用绝对路径
        for (String component : components) {
            from(project.rootDir.absolutePath + "/" + component + "/src/main/java/") {
                include '**/*.java' + GroupingConstant.FILE_SUFFIX
                include '**/*.kt' + GroupingConstant.FILE_SUFFIX
            }
        }
        into(project.rootDir.absolutePath + "/" + serviceModule + "/src/main/java/")
        setIncludeEmptyDirs(false)
        int len = GroupingConstant.FILE_SUFFIX.length()
        rename { String fileName ->
            fileName.substring(0, fileName.length() - len)
        }
    }
}
