
repositories {
    jcenter()
}

tasks {

    register<Exec>("deploy") {
        dependsOn("pushDocker")
        workingDir("src/helm")
        commandLine("helm", "upgrade", "--install", "hello", ".")
    }

    val registry = "registry-797ecf26-f6b5-4fad-9e7a-d87a2b96853e.dyn.mayope.net/hello:latest"

    register<Exec>("pushDocker"){
        dependsOn("buildDocker")
        commandLine("docker", "image", "push", registry)
    }

    register<Exec>("buildDocker") {
        dependsOn("copyDockerFile")
        workingDir("$buildDir/buildDocker")
        commandLine("docker", "image", "build", "-t", registry, ".")
    }

    register<Copy>("copyBuild") {
        from("src/app") {
            include("**/*.*")
        }
        into("$buildDir/buildDocker/app")
    }

    register<Copy>("copyDockerFile") {
        dependsOn("copyBuild")
        from("src/docker") {
            include("Dockerfile")
        }
        into("$buildDir/buildDocker")
    }
}

