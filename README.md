gradle-jacobo-plugin
======================
[![Build Status](https://travis-ci.org/kageiit/gradle-jacobo-plugin.svg?branch=master)](https://travis-ci.org/kageiit/gradle-jacobo-plugin) [![Coverage Status](https://coveralls.io/repos/kageiit/gradle-jacobo-plugin/badge.svg?branch=master)](https://coveralls.io/r/kageiit/gradle-jacobo-plugin?branch=master)

Jacobo is a [Gradle](https://www.gradle.org) plugin that converts [JaCoCo](http://www.eclemma.org/jacoco/) coverage reports to [Cobertura](http://cobertura.github.io/cobertura/) coverage reports.

Usage
-----
Add the following to buildscript:

```groovy
buildscript {
    dependencies {
        classpath 'com.kageiit:jacobo-plugin:1.+'
    }
}
```

Apply and configure jacobo like so:

```groovy
apply plugin: 'com.kageiit.jacobo'

jacobo {
    jacocoReport file("${project.buildDir}/reports/jacoco/test/jacocoTestReport.xml")
    coberturaReport file("${project.buildDir}/reports/cobertura/cobertura.xml")
    srcDirs = sourceSets.main.java.srcDirs
}
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
