import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

class GenerateSource extends DefaultTask {

    @InputFile
    final RegularFileProperty srcFile = newInputFile()

    @OutputDirectory
    final DirectoryProperty destDir = newOutputDirectory()

    @TaskAction
    void generate() {
        def mapping = new JsonSlurper().parse(srcFile.get().asFile)
        def className = (srcFile.get().asFile.name - '.json').capitalize()
        destDir.file(className + '.groovy').get().asFile.withWriter { writer ->
            writer.println "class $className {"
            mapping.each { key, value ->
                writer.println "    static Map get${key.capitalize()}() {"
                writer.println "        ${unwrapValue(value)}"
                writer.println "    }"
            }
            writer.println "}"
        }
    }

    String unwrapValue(Object value) {
        if (value instanceof Map) {
            return value.collect { k, v ->
                "'$k': ${unwrapValue(v)}"
            }
        } else {
            return "'$value'"
        }
    }
}
