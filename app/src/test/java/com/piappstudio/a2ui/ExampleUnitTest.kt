package com.piappstudio.a2ui

import org.junit.Test
import java.io.File
import java.util.jar.JarFile

class ExampleUnitTest {

    @Test
    fun inspectAllA2UILibraries() {
        val classPath = System.getProperty("java.class.path") ?: ""
        val entries = classPath.split(File.pathSeparator)
        
        val relevantEntries = entries.filter { it.contains("a2ui") || it.contains("compose-runtime") || it.contains("compose-ui") }
        println("Found ${relevantEntries.size} relevant jar files:")
        relevantEntries.forEach { println(" - $it") }

        val classesByJar = mutableMapOf<String, MutableList<Class<*>>>()

        for (jarPath in relevantEntries) {
            val jarFile = File(jarPath)
            if (!jarFile.exists()) continue
            val list = mutableListOf<Class<*>>()
            try {
                JarFile(jarFile).use { jar ->
                    val enumEntries = jar.entries()
                    while (enumEntries.hasMoreElements()) {
                        val entry = enumEntries.nextElement()
                        if (entry.name.endsWith(".class") && !entry.name.contains("META-INF")) {
                            val className = entry.name
                                .removeSuffix(".class")
                                .replace('/', '.')
                            try {
                                val clazz = Class.forName(className, false, Thread.currentThread().contextClassLoader)
                                if (clazz.name.startsWith("androidx.a2ui")) {
                                    list.add(clazz)
                                }
                            } catch (_: Throwable) {}
                        }
                    }
                }
            } catch (e: Throwable) {
                println("Error reading jar $jarPath: ${e.message}")
            }
            if (list.isNotEmpty()) {
                classesByJar[jarPath] = list
            }
        }

        val report = StringBuilder()
        report.append("Total jars with androidx.a2ui classes: ${classesByJar.size}\n\n")
        for ((jarPath, classes) in classesByJar) {
            report.append("### Jar: $jarPath (${classes.size} classes)\n")
            val byPackage = classes.groupBy { it.`package`?.name ?: "default" }
            for ((pkg, pkgClasses) in byPackage.entries.sortedBy { it.key }) {
                report.append("  #### Package: $pkg\n")
                for (clazz in pkgClasses.sortedBy { it.name }) {
                    val kind = when {
                        clazz.isInterface -> "interface"
                        clazz.isEnum -> "enum"
                        java.lang.reflect.Modifier.isAbstract(clazz.modifiers) -> "abstract class"
                        else -> "class"
                    }
                    report.append("    - **$kind** `${clazz.simpleName}` (`${clazz.name}`)\n")
                    val publicMethods = clazz.declaredMethods.filter { 
                        java.lang.reflect.Modifier.isPublic(it.modifiers) && !it.isSynthetic && !it.name.contains("\$")
                    }.sortedBy { it.name }
                    if (publicMethods.isNotEmpty()) {
                        report.append("      - Public Methods:\n")
                        for (m in publicMethods) {
                            val params = m.parameterTypes.joinToString { it.simpleName }
                            report.append("        - `${m.name}($params) : ${m.returnType.simpleName}`\n")
                        }
                    }
                }
                report.append("\n")
            }
            report.append("---\n\n")
        }

        val outFile = File("build/a2ui_by_jar_report.txt")
        outFile.parentFile?.mkdirs()
        outFile.writeText(report.toString())
        println("Report written to ${outFile.absolutePath}")
    }
}
