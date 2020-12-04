import java.util.Base64


repositories {
    jcenter()
}

tasks {

    register<Exec>("deploy") {
        dependsOn("pushDocker")
        workingDir("src/helm")
        commandLine("helm", "upgrade", "--install", "hello", ".")
    }

    val registry = "registry-0fc25963-933a-42b0-93e3-5036329931cb.dyn.mayope.net/hello:latest"

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

private val decoder = Base64.getDecoder()
fun base64Decode(string: String) = String(decoder.decode(string), Charsets.UTF_8)


fun getSecret(name: String, key: String): String {
    return base64Decode(
        command(
            listOf(
                "kubectl", "get", "secret", name,
                "-o", "template", "--template={{.data.$key}}"
            )
        )
    )
}

fun command(cmd: List<String>, workingDirectory: String = ".") =
    java.io.ByteArrayOutputStream().also { stream ->
        println("Running command $cmd")
        exec {
            commandLine = cmd
            standardOutput = stream
            workingDir = File(workingDirectory)
        }
    }.run {
        toString().trim()
    }

