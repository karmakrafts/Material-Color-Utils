import java.nio.file.StandardOpenOption
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.inputStream
import java.nio.file.Path
import java.time.Instant

plugins {
    eclipse
    idea
    java
    `maven-publish`
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(11)
    }
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

val projectPath: Path = project.projectDir.toPath()
val buildConfig: Properties = Properties().apply {
    Path("build.properties").inputStream(StandardOpenOption.READ).use(::load)
}
val license: String = buildConfig["license"] as String
val buildNumber: Int = System.getenv("CI_PIPELINE_IID")?.toIntOrNull() ?: 0
val buildTime: Instant = Instant.now()

version = "${libs.versions.materialColorUtils.get()}.$buildNumber"
group = buildConfig["group"] as String
base.archivesName = "material-color-utils"

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation(libs.annotations)
}

tasks {
    System.getenv("CI_API_V4_URL")?.let { apiUrl ->
        publishing {
            repositories {
                maven {
                    url = uri("${apiUrl.replace("http://", "https://")}/projects/267/packages/maven")
                    name = "GitLab"
                    credentials(HttpHeaderCredentials::class) {
                        name = "Job-Token"
                        value = System.getenv("CI_JOB_TOKEN")
                    }
                    authentication {
                        create("header", HttpHeaderAuthentication::class)
                    }
                }
            }

            publications {
                create<MavenPublication>("materialColorUtils") {
                    groupId = project.group as String
                    artifactId = project.base.archivesName.get()
                    version = project.version as String

                    artifact(jar)

                    pom {
                        name = artifactId
                        url = "https://git.karmakrafts.dev/kk/material-color-utils"
                        scm {
                            url = this@pom.url
                        }
                        issueManagement {
                            system = "gitlab"
                            url = "https://git.karmakrafts.dev/kk/material-color-utils/issues"
                        }
                        licenses {
                            license {
                                name = license
                                distribution = "repo"
                            }
                        }
                        developers {
                            developer {
                                id = "kitsunealex"
                                name = "KitsuneAlex"
                                url = "https://git.karmakrafts.dev/KitsuneAlex"
                            }
                        }
                    }
                }
            }
        }
    }
}