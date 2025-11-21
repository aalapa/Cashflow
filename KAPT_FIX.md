# Fixing KAPT Java 17 Compatibility Issue

The error occurs because KAPT needs access to internal Java compiler classes that are restricted in Java 17's module system.

## Solution Steps:

### 1. Stop the Gradle Daemon
Run this command in your terminal from the project root:
```bash
./gradlew --stop
```

Or if you're using Android Studio:
- Go to **File → Settings → Build, Execution, Deployment → Build Tools → Gradle**
- Click **Stop Gradle daemon** button

### 2. Verify Configuration
The following files should already be configured:

**gradle.properties** should have:
```
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8 --add-opens=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.jvm=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED
```

**app/build.gradle.kts** should have the kapt block with javacOptions.

### 3. Clean and Rebuild
In Android Studio:
- **Build → Clean Project**
- **Build → Rebuild Project**

Or from terminal:
```bash
./gradlew clean
./gradlew build
```

### 4. If Still Not Working - Alternative: Use KSP Instead of KAPT

If the issue persists, consider migrating from KAPT to KSP (Kotlin Symbol Processing), which is faster and doesn't have this Java 17 issue:

1. Replace `id("kotlin-kapt")` with `id("com.google.devtools.ksp")` in build.gradle.kts
2. Replace `kapt()` with `ksp()` for Room compiler
3. Add KSP plugin to project build.gradle.kts

But first, try steps 1-3 above.

