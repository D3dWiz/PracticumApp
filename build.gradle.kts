plugins {
    id("java")
}

group = "me.yan"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.formdev:flatlaf:3.4.1")
    implementation("com.formdev:flatlaf-intellij-themes:3.4.1")

    implementation("com.h2database:h2:2.2.224")

    implementation("com.github.lgooddatepicker:LGoodDatePicker:11.2.1")
    implementation("com.jgoodies:forms:1.2.1")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}