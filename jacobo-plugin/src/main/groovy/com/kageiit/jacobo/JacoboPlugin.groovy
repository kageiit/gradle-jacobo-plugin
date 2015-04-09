package com.kageiit.jacobo

import org.gradle.api.Plugin
import org.gradle.api.Project

class JacoboPlugin implements Plugin<Project> {

    public static final String JACOBO = "jacobo"

    @Override
    void apply(Project project) {
        JacoboExtension config = project.extensions.create(JACOBO, JacoboExtension)
        JacoboTask jacoboTask = project.tasks.create(name: JacoboTask.NAME, type: JacoboTask)
        jacoboTask.config = project.extensions.jacobo
        jacoboTask.group = JACOBO
        jacoboTask.description = "Converts JaCoCo coverage report to Cobertura coverage report."
    }
}
