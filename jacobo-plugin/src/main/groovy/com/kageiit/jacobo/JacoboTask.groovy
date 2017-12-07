package com.kageiit.jacobo

import groovy.util.slurpersupport.GPathResult
import groovy.xml.MarkupBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

class JacoboTask extends DefaultTask {

    static final String LINE = 'LINE'
    static final String BRANCH = 'BRANCH'
    static final String COMPLEXITY = 'COMPLEXITY'

    @InputFile
    File jacocoReport

    @InputFiles
    String[] srcDirs = {}

    @Input
    Set<String> includeFileNames = [] as Set

    @OutputFile
    File coberturaReport

    @TaskAction
    void convert() {
        XmlSlurper slurper = new XmlSlurper(false, false, true)
        slurper.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
        GPathResult jacoco = slurper.parse(jacocoReport)
        def sw = new StringWriter()
        def cobertura = new MarkupBuilder(sw)

        def timestamp = 0L
        if (jacoco.sessioninfo != null && !jacoco.sessioninfo.isEmpty()) {
            timestamp = ((jacoco.sessioninfo.first().@start).toLong() / 1000).toLong()
        }
        cobertura.coverage(timestamp: timestamp, 'line-rate': counter(jacoco, LINE), 'branch-rate': counter(jacoco, BRANCH), complexity: counter(jacoco, COMPLEXITY, this.&sum)) {
            sources {
                srcDirs.each { src ->
                    source(src)
                }
            }
            packages {
                def exts = sourceFileExts(jacoco.package)
                jacoco.package.each { pkg ->
                    'package'(name: (pkg.@name).toString().replace("/", '.'), 'line-rate': counter(pkg, LINE), 'branch-rate': counter(pkg, BRANCH), complexity: counter(pkg, COMPLEXITY, this.&sum)) {
                        classes {
                            pkg.class.each { clazz ->
                                def classname = clazz.@name.toString()
                                def pathToFile = pathToFile(classname)
                                def topLevelClassName = topLevelClassName(pathToFile) as String
                                def ext = exts.getOrDefault(topLevelClassName, "java")
                                def basename = classname.substring(classname.lastIndexOf("/") + 1).replaceAll("\\u0024.*", "")
                                basename = "${basename}.${ext}" as String
                                if (!includeFileNames.empty && !includeFileNames.contains(basename)) {
                                    return
                                }
                                'class'(name: (classname).toString().replace("/", '.'), filename: "${pathToFile}.${ext}", 'line-rate': counter(clazz, LINE), 'branch-rate': counter(clazz, BRANCH), complexity: counter(clazz, COMPLEXITY, this.&sum)) {
                                    def lynes = pkg.sourcefile.find {
                                        it.@name.equals(basename)
                                    }.line
                                    methods {
                                        def mthds = clazz.method
                                        mthds.each { mthd ->
                                            'method'(name: (mthd.@name).toString().replace("/", '.'), signature: mthd.@desc, 'line-rate': counter(mthd, LINE), 'branch-rate': counter(mthd, BRANCH), complexity: counter(mthd, COMPLEXITY, this.&sum)) {
                                                lines {
                                                    method_lines(mthd, mthds, lynes).each { lyne ->
                                                        def mb = lyne.@mb.toInteger()
                                                        def cb = lyne.@cb.toInteger()
                                                        def ci = lyne.@ci.toInteger()

                                                        def number = lyne.@nr
                                                        def hits = ci > 0 ? 1 : 0
                                                        def mcb = cb + mb

                                                        if (mcb > 0) {
                                                            def percentage = ((100 * cb / (mcb)).toInteger()).toString() + '%'
                                                            line(branch: 'true', 'condition-coverage': "${percentage} ($cb/$mcb)", number: number, hits: hits) {
                                                                conditions {
                                                                    'condition'(number: 0, type: 'jump', coverage: percentage)
                                                                }
                                                            }
                                                        } else {
                                                            line(branch: 'false', number: number, hits: hits)
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    lines {
                                        lynes.each { lyne ->
                                            def mb = lyne.@mb.toInteger()
                                            def cb = lyne.@cb.toInteger()
                                            def ci = lyne.@ci.toInteger()

                                            def number = lyne.@nr
                                            def hits = ci > 0 ? 1 : 0
                                            def mcb = cb + mb

                                            if (mcb > 0) {
                                                def percentage = ((100 * cb / (mcb)).toInteger()).toString() + '%'
                                                line(branch: 'true', 'condition-coverage': "${percentage} ($cb/$mcb)", number: number, hits: hits) {
                                                    conditions {
                                                        'condition'(number: 0, type: 'jump', coverage: percentage)
                                                    }
                                                }
                                            } else {
                                                line(branch: 'false', number: number, hits: hits)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        coberturaReport.parentFile.mkdirs()
        coberturaReport.createNewFile()
        coberturaReport.write('<?xml version="1.0" encoding="UTF-8"?>\n' + sw)
    }

    def static method_lines(method, methods, lines) {
        if (method.@line.isEmpty()) {
            return []
        }

        def start_line = method.@line.toInteger()
        def larger = methods.findAll { !it.@line.isEmpty() && it.@line.toInteger() > start_line }.collect {
            it.@line.toInteger()
        }
        def end_line = larger.empty ? 99999999 : larger.min()

        def method_lines = lines.findAll { start_line <= it.@nr.toInteger() && it.@nr.toInteger() < end_line }

        // Don't go beyond the number of lines known to be associated with this method
        def line_counter = method.counter.find { it.@type == LINE }
        if (!line_counter) {
            return method_lines
        }
        def limit = line_counter.@missed.toInteger() + line_counter.@covered.toInteger()
        return method_lines.take(limit)
    }

    def static fraction(covered, missed) {
        return covered / (covered + missed)
    }

    def static sum(covered, missed) {
        return covered + missed
    }

    def static pathToFile(path_to_class) {
        def match
        if ((match = path_to_class =~ /([^\u0024]*)/)) {
            return "${match.group(1)}"
        } else {
            return "${path_to_class}"
        }
    }

    def static topLevelClassName(path_to_file) {
        def index = path_to_file.lastIndexOf('/')
        if (index != -1) {
            return path_to_file.substring(index + 1)
        }
        return path_to_file
    }

    def static counter(source, type, operation = this.&fraction) {
        def node = source.counter.find { it.@type.equals(type) }
        def covered = node.@covered
        def missed = node.@missed
        if ("".equals(covered.toString()) || "".equals(missed.toString())) {
            return '0.0'
        } else {
            return operation(covered.toFloat(), missed.toFloat())
        }
    }

    def static sourceFileExts(source) {
        def exts = [:]
        source.sourcefile.each {
            def topLevelClassName = it.@name as String
            def index = topLevelClassName.lastIndexOf('.')
            if (index != -1) {
                def ext = topLevelClassName.substring(index + 1)
                topLevelClassName = topLevelClassName.substring(0, index)
                exts.put(topLevelClassName, ext)
            }
        }
        return exts
    }
}
