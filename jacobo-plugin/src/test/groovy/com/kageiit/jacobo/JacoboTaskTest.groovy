package com.kageiit.jacobo

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.After
import org.junit.Before
import org.junit.Test

class JacoboTaskTest {

    Project project = ProjectBuilder.builder().withProjectDir(new File("src/test")).withName("root").build()
    JacoboTask jacobo

    @Before
    public void setUp() {
        jacobo = project.tasks.create("jacobo", JacoboTask, {
            it.jacocoReport = new File('src/test/fixtures/jacoco.xml')
            it.coberturaReport = File.createTempFile("temp", ".tmp")
            it.srcDirs = ['src/main/groovy']
        })
    }

    @After
    public void tearDown() {
        jacobo.coberturaReport.delete()
    }

    @Test
    public void convert() {
        jacobo.convert()
        String converted = jacobo.coberturaReport.text.replaceAll("\\s+", "")
        String expected = new File('src/test/fixtures/cobertura.xml').text.replaceAll("\\s+", "")
        assert converted.equals(expected)
    }

    @Test
    public void convertWithNoTimeStamp() {
        jacobo = project.tasks.create("jacobo2", JacoboTask, {
            it.jacocoReport = new File('src/test/fixtures/jacocoNoTs.xml')
            it.coberturaReport = File.createTempFile("temp", ".tmp")
            it.srcDirs = ['src/main/groovy']
        })
        jacobo.convert()
        String converted = jacobo.coberturaReport.text.replaceAll("\\s+", "")
        String expected = new File('src/test/fixtures/coberturaNoTs.xml').text.replaceAll("\\s+", "")
        assert converted.equals(expected)
    }

    @Test
    public void convertWithIncludes() {
        jacobo = project.tasks.create("jacobo3", JacoboTask, {
            it.jacocoReport = new File('src/test/fixtures/jacocoFileNames.xml')
            it.coberturaReport = File.createTempFile("temp", ".tmp")
            it.srcDirs = ['src/main/groovy']
            it.includeFileNames = ['Jacobo.java']
        })
        jacobo.convert()
        String converted = jacobo.coberturaReport.text.replaceAll("\\s+", "")
        String expected = new File('src/test/fixtures/coberturaFileNames.xml').text.replaceAll("\\s+", "")
        assert converted.equals(expected)
    }

    @Test
    public void convertWithMissingLineNumber() {
        jacobo = project.tasks.create("jacobo4", JacoboTask, {
            it.jacocoReport = new File('src/test/fixtures/jacocoMissingLineNumber.xml')
            it.coberturaReport = File.createTempFile("temp", ".tmp")
            it.srcDirs = ['src/main/groovy']
        })
        jacobo.convert()
        String converted = jacobo.coberturaReport.text.replaceAll("\\s+", "")
        String expected = new File('src/test/fixtures/coberturaMissingLineNumber.xml').text.replaceAll("\\s+", "")
        assert converted.equals(expected)
    }

    @Test
    public void convertWithNonJavaFileNames() {
        jacobo = project.tasks.create("jacobo5", JacoboTask, {
            it.jacocoReport = new File('src/test/fixtures/jacocoNonJavaFileNames.xml')
            it.coberturaReport = File.createTempFile("temp", ".tmp")
            it.srcDirs = ['src/main/groovy']
        })
        jacobo.convert()
        String converted = jacobo.coberturaReport.text.replaceAll("\\s+", "")
        String expected = new File('src/test/fixtures/coberturaNonJavaFileNames.xml').text.replaceAll("\\s+", "")
        assert converted.equals(expected)
    }
}
