plugins {
    val kotlinVersion = "1.5.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.6.7"
}

group = "net.im45.bot"
version = "1.0-SNAPSHOT"

repositories {
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
}
