/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
plugins {
  `maven-publish`
  id("java")
  id("idea")
}

repositories {
  mavenCentral()
}

val trinoVersion = libs.versions.trino.get()
val artifactName = "${rootProject.name}-trino-connector-$trinoVersion"

dependencies {
  implementation(project(":trino-connector:trino-common"))
  implementation(project(":clients:client-java-runtime", configuration = "shadow"))
  implementation(libs.bundles.log4j)
  implementation(libs.commons.lang3)
  implementation(libs.guava)

  compileOnly(libs.trino.spi) {
    exclude(group = "org.apache.logging.log4j")
  }

  testImplementation(libs.trino.testing) {
    exclude(group = "org.apache.logging.log4j")
  }
}

tasks.withType<Jar> {
  archiveBaseName.set(artifactName)
}

tasks {
  val copyDepends by registering(Copy::class) {
    from(configurations.runtimeClasspath)
    into("build/libs")
  }
  jar {
    finalizedBy(copyDepends)
  }

  register("copyLibs", Copy::class) {
    dependsOn(copyDepends, "build")
    from("build/libs")
    into("$rootDir/distribution/${rootProject.name}-trino-connector-$trinoVersion")
  }
}
