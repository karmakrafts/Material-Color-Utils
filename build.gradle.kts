import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.inputStream

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

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}

eclipse {
    classpath {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
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
    jar {
        manifest.attributes.apply {
            this["Specification-Title"] = "Material Color Utils"
            this["Specification-Vendor"] = "Google LLC"
            this["Specification-Version"] = "1"
            this["Implementation-Title"] = "material-color-utils"
            this["Implementation-Vendor"] = "Karma Krafts"
            this["Implementation-Version"] = project.version
            this["Implementation-Timestamp"] = SimpleDateFormat.getDateTimeInstance().format(Date.from(buildTime))
        }
    }

    val classes by getting

    val sourcesJar = create<Jar>("sourcesJar") {
        from(sourceSets.main.get().allSource)
        dependsOn(classes)
        archiveClassifier = "sources"
    }

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
                    artifact(sourcesJar)

                    pom {
                        name = artifactId
                        url = "https://git.karmakrafts.dev/kk/${project.name}"
                        scm {
                            url = this@pom.url
                        }
                        issueManagement {
                            system = "gitlab"
                            url = "https://git.karmakrafts.dev/kk/${project.name}/issues"
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