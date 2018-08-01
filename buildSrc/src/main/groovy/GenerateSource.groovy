import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

class GenerateSource extends DefaultTask {

    @InputFile
    final RegularFileProperty srcFile = newInputFile()

    @OutputFile
    final RegularFileProperty destSrcFile = newOutputFile()

    @OutputFile
    final RegularFileProperty destTestSrcFile = newOutputFile()

    @TaskAction
    void generate() {
        def mapping = new JsonSlurper().parse(srcFile.get().asFile)
        def className = (srcFile.get().asFile.name - '.json').capitalize()
        def srcWriter = destSrcFile.get().asFile.newWriter('UTF-8')
        def testSrcWriter = destTestSrcFile.get().asFile.newWriter('UTF-8')
        srcWriter.println "class $className {"
        testSrcWriter.println "class ${className}Test {"
        mapping.each { key, value ->
            srcWriter.println "    static Map get${key.capitalize()}() {"
            srcWriter.println "        ${unwrapValue(value)}"
            srcWriter.println "    }"
        }
        srcWriter.println "}"
        testSrcWriter.println "}"
        srcWriter.close()
        testSrcWriter.close()
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
