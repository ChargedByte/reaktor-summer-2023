import com.github.gradle.node.pnpm.task.PnpmTask

plugins {
    base
    id("com.github.node-gradle.node") version "3.5.1"
}

buildDir = file("$projectDir/dist")

val pnpmInstall: Task = tasks["pnpmInstall"]

val buildTask = tasks.register<PnpmTask>("pnpmBuild") {
    dependsOn(pnpmInstall)
    args.set(listOf("build"))
    inputs.dir(project.fileTree("src").exclude("**/*.{test,spec}.{js,mjs,cjs,ts,mts,cts,jsx,tsx}"))
    inputs.dir("node_modules")
    inputs.files("tsconfig.app.json", "tsconfig.config.json", "tsconfig.json", "vite.config.*")
    outputs.dir(buildDir)
}

val unitTestTask = tasks.register<PnpmTask>("pnpmUnitTest") {
    dependsOn(pnpmInstall)
    args.set(listOf("test:unit"))
    environment.put("CI", "true")
    inputs.dir("src")
    inputs.dir("node_modules")
    inputs.files(
        "tsconfig.app.json",
        "tsconfig.config.json",
        "tsconfig.json",
        "tsconfig.vitest.json",
        "vitest.config.*"
    )
    outputs.dir("$projectDir/src/node_modules/.vitest")
}

val e2eTestTask = tasks.register<PnpmTask>("pnpmE2eTest") {
    dependsOn(pnpmInstall)
    args.set(listOf("test:e2e"))
    environment.put("CI", "true")
    inputs.dir("src")
    inputs.dir("node_modules")
    inputs.dir("e2e")
    inputs.files(
        "tsconfig.app.json",
        "tsconfig.config.json",
        "tsconfig.json",
        "tsconfig.vitest.json",
        "playwright.config.*"
    )
    outputs.dir("$projectDir/playwright-report")
}

val lintTask = tasks.register<PnpmTask>("pnpmLint") {
    dependsOn(pnpmInstall)
    args.set(listOf("lint"))
    inputs.dir("src")
    inputs.dir("node_modules")
    inputs.files(
        ".eslintrc.cjs",
        ".prettierrc.cjs",
        "tsconfig.app.json",
        "tsconfig.config.json",
        "tsconfig.json",
        "tsconfig.vitest.json"
    )
    outputs.upToDateWhen { true }
}

val cleanUnitTest = tasks.register<Delete>("cleanUnitTest") {
    delete(file("$projectDir/src/node_modules/.vitest"))
}

val cleanE2eTest = tasks.register<Delete>("cleanE2eTest") {
    delete(file("$projectDir/playwright-report"))
}

tasks.assemble {
    dependsOn(lintTask, buildTask)
}

tasks.check {
    dependsOn(lintTask, unitTestTask, e2eTestTask)
}

tasks.clean {
    dependsOn(cleanUnitTest, cleanE2eTest)
}
