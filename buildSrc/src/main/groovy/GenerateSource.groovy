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
        testSrcWriter.println "import org.testng.annotations.Test\n"
        srcWriter.println "class $className {\n"
        testSrcWriter.println "class ${className}Test {\n"
        mapping.each { key, value ->
            srcWriter.println "    static Map get${key.capitalize()}() {"
            srcWriter.println "        ${unwrapToMap(value)}"
            srcWriter.println "    }\n"
            testSrcWriter.println "    @Test"
            testSrcWriter.println "    void test${key.capitalize()}() {"
            unwrapToAssertions("$className", key, value).each { assertion ->
                testSrcWriter.println "        $assertion"
            }
            testSrcWriter.println "    }\n"
        }
        srcWriter.println "}"
        testSrcWriter.println "}"
        srcWriter.close()
        testSrcWriter.close()
    }

    String unwrapToMap(Object value) {
        if (value instanceof Map) {
            return value.collect { k, v ->
                "'$k': ${unwrapToMap(v)}"
            }
        } else {
            return "'$value'"
        }
    }

    List<String> unwrapToAssertions(String prefix, String key, Object value) {
        def assertions = []
        if (value instanceof Map) {
            value.each { k, v ->
                assertions += unwrapToAssertions("${prefix}['$key']", k, v)
            }
        } else {
            return ["assert ${prefix}['$key'] == '$value'"]
        }
        return assertions
    }
}
