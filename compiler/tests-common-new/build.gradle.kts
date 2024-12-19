import java.util.Properties

plugins {
    kotlin("jvm")
    id("jps-compatible")
    id("compiler-tests-convention")
}

repositories {
    maven("https://jitpack.io/")
    mavenLocal()
}

dependencies {
    testImplementation("com.github.XYZboom:CodeSmith:1.0-SNAPSHOT")
    testApi(project(":compiler:fir:entrypoint"))
    testApi(project(":compiler:fir:fir-serialization"))
    testApi(project(":compiler:fir:fir2ir:jvm-backend"))
    testApi(project(":compiler:cli"))
    testImplementation(project(":compiler:ir.tree"))
    testImplementation(project(":compiler:backend.jvm.entrypoint"))
    testImplementation(project(":compiler:backend.jvm.lower"))
    testImplementation(intellijCore())
    testImplementation(commonDependency("org.jetbrains.kotlin:kotlin-reflect")) { isTransitive = false }

    testRuntimeOnly(project(":core:descriptors.runtime"))

    testImplementation(projectTests(":generators:test-generator"))

    testApi(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testApi(libs.junit.platform.launcher)
    testApi(projectTests(":compiler:test-infrastructure"))
    testApi(projectTests(":compiler:test-infrastructure-utils"))
    testApi(projectTests(":compiler:tests-compiler-utils"))
    testApi(project(":libraries:tools:abi-comparator"))

    /*
     * Actually those dependencies are needed only at runtime, but they
     *   declared as Api dependencies to propagate them to all modules
     *   which depend on current one
     */
    testApi(commonDependency("org.jetbrains.intellij.deps.fastutil:intellij-deps-fastutil"))
    testApi(commonDependency("one.util:streamex"))
    testApi(commonDependency("org.jetbrains.intellij.deps.jna:jna"))
    testApi(jpsModel()) { isTransitive = false }
    testApi(jpsModelImpl()) { isTransitive = false }
    testApi(intellijJavaRt()) // for FileComparisonFailure
    testApi(libs.junit4) // for ComparisonFailure

    testApi(toolsJar())
}

optInToExperimentalCompilerApi()
optInToUnsafeDuringIrConstructionAPI()

sourceSets {
    "main" { none() }
    "test" {
        projectDefault()
        generatedTestDir()
    }
}

compilerTests {
    withStdlibCommon()
    withScriptRuntime()
    withTestJar()
    withAnnotations()
    withScriptingPlugin()
    withStdlibJsRuntime()
    withTestJsRuntime()
}

projectTest(
    jUnitMode = JUnitMode.JUnit5,
    defineJDKEnvVariables = listOf(
        JdkMajorVersion.JDK_11_0, // e.g. org.jetbrains.kotlin.test.runners.ForeignAnnotationsCompiledJavaTestGenerated.Java11Tests
        JdkMajorVersion.JDK_21_0, // e.g. org.jetbrains.kotlin.test.runners.codegen.FirLightTreeBlackBoxModernJdkCodegenTestGenerated.TestsWithJava21
    )
) {
    workingDir = rootDir
    useJUnitPlatform()

    inputs.dir(layout.projectDirectory.dir("../testData")).withPathSensitivity(PathSensitivity.RELATIVE)
    inputs.file(File(rootDir, "compiler/cli/cli-common/resources/META-INF/extensions/compiler.xml"))
        .withPathSensitivity(PathSensitivity.RELATIVE)
    inputs.file(File(rootDir, "compiler/testData/mockJDK/jre/lib/rt.jar")).withNormalizer(ClasspathNormalizer::class)
    inputs.dir(File(rootDir, "third-party/annotations")).withPathSensitivity(PathSensitivity.RELATIVE)
    inputs.dir(File(rootDir, "third-party/java8-annotations")).withPathSensitivity(PathSensitivity.RELATIVE)
    inputs.dir(File(rootDir, "third-party/java9-annotations")).withPathSensitivity(PathSensitivity.RELATIVE)
    inputs.dir(File(rootDir, "third-party/jsr305")).withPathSensitivity(PathSensitivity.RELATIVE)
    inputs.dir(File(rootDir, "libraries/stdlib/unsigned/src/kotlin")).withPathSensitivity(PathSensitivity.RELATIVE)
    inputs.dir(File(rootDir, "libraries/stdlib/jvm/src/kotlin")).withPathSensitivity(PathSensitivity.RELATIVE) //util/UnsignedJVM.kt
    inputs.dir(File(rootDir, "libraries/stdlib/src/kotlin")).withPathSensitivity(PathSensitivity.RELATIVE) //ranges/Progressions.kt
    inputs.dir(File(rootDir, "libraries/stdlib/jvm/runtime/kotlin")).withPathSensitivity(PathSensitivity.RELATIVE) //TypeAliases.kt
}

testsJar()

tasks.test {
    systemProperties["codesmith.logger.console"] = System.getProperty("codesmith.logger.console") ?: "info"
    systemProperties["codesmith.logger.traceFile"] = System.getProperty("codesmith.logger.traceFile") ?: "off"
    systemProperties["codesmith.logger.traceFile.ImmediateFlush"] =
        System.getProperty("codesmith.logger.traceFile.ImmediateFlush") ?: "false"
    val jacocoAgentPath = System.getProperty("jacoco.agent.path")
    if (jacocoAgentPath != null) {
        jvmArgs("-javaagent:${jacocoAgentPath}=output=none")
    }
}