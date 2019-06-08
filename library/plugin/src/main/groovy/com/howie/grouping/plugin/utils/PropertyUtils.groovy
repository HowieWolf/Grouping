package com.howie.grouping.plugin.utils

import org.gradle.api.Project

class PropertyUtils {

    static String[] getGroupingComponents(Project project) {
        String components = (String) project.properties.get(GroupingConstant.GROUPING_COMPONENTS)
        if (components == null || components.length() == 0) {
            throw new IllegalArgumentException("miss property " + GroupingConstant.GROUPING_COMPONENTS)
        }
        return components.split(",")
    }

    static String getGroupingServiceModuleName(Project project) {
        def moduleName = (String) project.property(GroupingConstant.SERVICE_MODULE)
        if (moduleName == null || moduleName.length() == 0) {
            throw new IllegalArgumentException("miss property " + GroupingConstant.SERVICE_MODULE)
        }
        return moduleName
    }

    static String getApplicationClassName(Project project) {
        def applicationName = (String) project.property(GroupingConstant.APPLICATION_NAME)
        if (applicationName == null || applicationName.length() == 0) {
            throw new IllegalArgumentException("miss property " + GroupingConstant.APPLICATION_NAME)
        }
        return applicationName
    }
}
