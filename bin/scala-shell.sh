#!/bin/bash
mvn exec:exec -Dexec.classpathScope=test -Dexec.args="-cp %classpath" -Dexec.executable=scala
