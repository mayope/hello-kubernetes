
repositories {
    jcenter()
}

tasks {

    val registry = "registry-a81af7e6-72e9-4063-8899-4e90aba8d9b6.dyn.mayope.net/hello"

    register<Exec>("deploy") {
        dependsOn("pushDocker")
        workingDir("src/helm")
        commandLine("helm", "upgrade", "--install", "hello", ".", "--set", "image.repository=$registry")
    }

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

