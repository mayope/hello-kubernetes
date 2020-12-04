repositories {
    jcenter()
}

tasks {

    register<Exec>("deploy") {
        dependsOn("pushDocker")
        workingDir("src/helm")
        commandLine("helm", "upgrade", "--install", "hello", ".")
    }

    register<Exec>("pushDocker"){
        dependsOn("buildDocker")
        commandLine("docker", "image", "push", "registry.mayope.net/hello")
    }

    register<Exec>("buildDocker") {
        dependsOn("copyDockerFile")
        workingDir("$buildDir/buildDocker")
        commandLine("docker", "image", "build", "-t", "registry.mayope.net/hello", ".")
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