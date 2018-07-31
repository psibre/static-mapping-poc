import com.xlson.groovycsv.CsvParser
import groovy.json.JsonBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

class ConvertCsvToJson extends DefaultTask {

    @InputFile
    final RegularFileProperty srcFile = newInputFile()

    @OutputFile
    final RegularFileProperty destFile = newOutputFile()

    @TaskAction
    void convert() {
        def csvReader = srcFile.get().asFile.newReader('UTF-8')
        def csv = CsvParser.parseCsv(csvReader)
        def data = [:].withDefault { [:] }
        csv.each { row ->
            row.toMap().each { column, cell ->
                def otherColumns = row.toMap().keySet() - column
                data[column] << [(cell): otherColumns.collectEntries { [(it): row[it]] }]
            }
        }
        destFile.get().asFile.withWriter('UTF-8') { writer ->
            writer.println new JsonBuilder(data).toPrettyString()
        }
    }
}
