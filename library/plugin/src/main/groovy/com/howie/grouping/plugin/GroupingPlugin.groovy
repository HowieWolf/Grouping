package com.howie.grouping.plugin

import com.android.build.gradle.AppPlugin
import com.howie.grouping.plugin.task.GroupingCopyApiTask
import com.howie.grouping.plugin.utils.GroupingConstant
import com.howie.grouping.plugin.utils.PropertyUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

class GroupingPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        println("GroupingPlugin--start")
        if (!project.plugins.hasPlugin(AppPlugin)) {
            throw new IllegalArgumentException('grouping gradle plugin only works in with Android Application module.')
        }
        String[] components = PropertyUtils.getGroupingComponents(project)
        String moduleName = PropertyUtils.getGroupingServiceModuleName(project)
        // 添加依赖
        addDependency(project, components)
        // 复制业务模块下的 AppLike 到组件化服务库中
        doCopy(project, components, moduleName)
        // 自动扫描 APPLike 并注入到 Application
        project.android.registerTransform(new GroupingTransform(project))
        println("GroupingPlugin--end")
    }

    private static void addDependency(Project project, String[] components) {
        for (String componentName : components) {
            componentName = componentName.trim()
            if (componentName.isEmpty()) {
                throw new IllegalArgumentException("error component name")
            }
            if (componentName.charAt(0) != ':') {
                componentName = ":" + componentName
            }
            /**
             * 示例语法:module
             * compileComponent=readercomponent,sharecomponent
             */
            project.dependencies.add("implementation", project.project(componentName))
            System.out.println("add dependencies project " + componentName)
        }
    }

    private static void doCopy(Project project, String[] components, String moduleName) {
        Task copyTask = project.tasks.create(GroupingConstant.COPY_API_TASK_NAME, GroupingCopyApiTask, components, moduleName)
        Task prebuild = project.tasks.getByName("preBuild")
        // copy 文件要在 build 之前
        prebuild.dependsOn copyTask
    }
}

