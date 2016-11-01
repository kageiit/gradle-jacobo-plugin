gradle-jacobo-plugin
======================
[![Build Status](https://travis-ci.org/kageiit/gradle-jacobo-plugin.svg?branch=master)](https://travis-ci.org/kageiit/gradle-jacobo-plugin) [![Coverage Status](https://coveralls.io/repos/github/kageiit/gradle-jacobo-plugin/badge.svg?branch=master)](https://coveralls.io/github/kageiit/gradle-jacobo-plugin?branch=master)

Jacobo is a [Gradle](https://www.gradle.org) plugin that converts [JaCoCo](http://www.eclemma.org/jacoco/) coverage reports to [Cobertura](http://cobertura.github.io/cobertura/) coverage reports.

Usage
-----
Add the following to buildscript:
```groovy
buildscript {
  repositories {
    maven {
      url "https://plugins.gradle.org/m2/"
    }
  }
  dependencies {
    classpath "gradle.plugin.com.kageiit:jacobo-plugin:2.0.5"
  }
}

apply plugin: "com.kageiit.jacobo"
```

Create a jacobo task like so:
```groovy
project.tasks.create("jacobo", JacoboTask, {
  it.jacocoReport = file("${project.buildDir}/reports/jacoco/test/jacocoTestReport.xml")
  it.coberturaReport = file("${project.buildDir}/reports/cobertura/cobertura.xml")
  it.srcDirs = sourceSets.main.java.srcDirs

  // Only output coverage for selected file names. Set to [] to output for all files
  it.includeFileNames = ['File1.java', 'File2.java' ]
})
```

Run the `jacobo` task to convert jacoco report to cobertura report.
```bash
./gradlew jacobo
```

See example project for detailed configuration. This plugin was mostly ported over from [cover2cover](https://github.com/rix0rrr/cover2cover).

License
-------

    Copyright 2015 Gautam Korlam

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
