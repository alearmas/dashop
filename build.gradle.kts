plugins {
	java
	id("org.springframework.boot") version "3.5.3" apply false
	id("io.spring.dependency-management") version "1.1.7" apply false
}

group = "com.aarmas.dashop"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(21))
	}
}

repositories {
	mavenCentral()
}

subprojects {
	apply(plugin = "java")
	apply(plugin = "io.spring.dependency-management")

	repositories { mavenCentral() }

	tasks.test {
		useJUnitPlatform()
	}
}