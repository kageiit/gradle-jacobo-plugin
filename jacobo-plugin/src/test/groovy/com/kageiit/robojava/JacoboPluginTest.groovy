package com.kageiit.jacobo

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.After
import org.junit.Before
import org.junit.Test

class JacoboPluginTest {
    Project project = ProjectBuilder.builder().withProjectDir(new File("src/test")).withName("root").build()

    @Before
    public void setUp() {
        new JacoboPlugin().apply(project)
        project.jacobo.jacocoReport =  new File('src/test/fixtures/jacoco.xml')
        project.jacobo.coberturaReport = File.createTempFile("temp",".tmp")
        project.jacobo.srcDirs = ['src/main/groovy']
    }

    @After
    public void tearDown() {
        project.jacobo.coberturaReport.delete()
    }

    @Test
    public void convert() {
        JacoboTask task = project.getTasksByName("jacobo", false).first()
        task.convert()
        String converted = project.jacobo.coberturaReport.text.replaceAll("\\s+","")
        String expected = new File('src/test/fixtures/cobertura.xml').text.replaceAll("\\s+","")
        assert converted.equals(expected)
    }

    @Test
    public void convertWithNoTimeStamp() {
        project.jacobo.jacocoReport =  new File('src/test/fixtures/jacocoNoTs.xml')
        JacoboTask task = project.getTasksByName("jacobo", false).first()
        task.convert()
        String converted = project.jacobo.coberturaReport.text.replaceAll("\\s+","")
        String expected = new File('src/test/fixtures/coberturaNoTs.xml').text.replaceAll("\\s+","")
        assert converted.equals(expected)
    }
}
