plugins {
	java
	id("org.springframework.boot") version "3.5.3"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.aarmas"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(21))
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-validation")

	implementation("com.amazonaws.serverless:aws-serverless-java-container-springboot3:2.0.0")
	implementation("com.amazonaws:aws-lambda-java-core:1.2.3")
	implementation("com.amazonaws:aws-lambda-java-events:3.11.3")

	implementation(platform("software.amazon.awssdk:bom:2.25.17"))
	implementation("software.amazon.awssdk:dynamodb")
	implementation("software.amazon.awssdk:dynamodb-enhanced")

	implementation("net.logstash.logback:logstash-logback-encoder:7.4")

	developmentOnly("org.springframework.boot:spring-boot-devtools")
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.junit.jupiter:junit-jupiter:5.10.3")

	// Testcontainers
	testImplementation("org.testcontainers:junit-jupiter:1.20.1")
	testImplementation("org.testcontainers:testcontainers:1.20.1")

	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
	useJUnitPlatform {
		excludeTags("integration")
	}
}

val integrationTest = tasks.register<Test>("integrationTest") {
	useJUnitPlatform {
		includeTags("integration") // solo integración
	}
	shouldRunAfter(tasks.test)
	// si tus IT necesitan el perfil local:
	systemProperty("spring.profiles.active", "local")
}

tasks.check { dependsOn(integrationTest) }

tasks.register<Jar>("awsJar") {
	archiveBaseName.set("sales-service")
	archiveClassifier.set("aws")
	archiveVersion.set("0.0.1-SNAPSHOT")
	duplicatesStrategy = DuplicatesStrategy.EXCLUDE

	from(sourceSets.main.get().output)

	dependsOn(configurations.runtimeClasspath)
	from({
		configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
	})

	manifest {
		attributes["Main-Class"] = "org.springframework.boot.loader.JarLauncher"
	}
}

tasks.bootJar {
	archiveClassifier.set("aws")
	mainClass.set("org.springframework.boot.loader.JarLauncher")
}