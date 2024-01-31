plugins {
    kotlin("jvm")
}

description = "Build Swift IR from Analysis API"

kotlin {
    explicitApi()
}

dependencies {
    compileOnly(kotlinStdlib())

    api(project(":native:swift:sir"))
    api(project(":analysis:analysis-api"))
    implementation(project(":analysis:analysis-api-standalone"))
}

sourceSets {
    "main" { projectDefault() }
}
