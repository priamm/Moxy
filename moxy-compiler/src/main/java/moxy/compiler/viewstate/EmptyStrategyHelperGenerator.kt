package moxy.compiler.viewstate

import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Modifier

object EmptyStrategyHelperGenerator {

    /**
     * @param migrationMethods non empty list of methods
     *
     * @return File with references to files witch need in refactoring
     */
    @JvmStatic
    fun generate(migrationMethods: List<MigrationMethod>): JavaFile {

        val exampleView = migrationMethods[0].clazz.simpleName

        val classBuilder = TypeSpec
            .classBuilder("EmptyStrategyHelper")
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)

        val javaDoc = """
            This class was generated, because 'enableEmptyStrategyHelper' compiler option is set to true.
            
            
            It is required for all view methods to have strategy.
            Add @StateStrategyType annotation to methods listed below.
            You can also set annotation directly to the View interface.
            
            Do not pay attention to compilation errors like these:
            'error: $exampleView is abstract; cannot be instantiated'
            
            Just use your IDE to navigate to methods and set necessary strategy to it.
            When you fix all methods, you can remove 'enableEmptyStrategyHelper' option for current module.
            
        """.trimIndent() // leave blank line above for nice generated javadoc


        classBuilder.addJavadoc(javaDoc)

        val methodSpecBuilder = MethodSpec.methodBuilder("getViewStateProviders")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)

        methodSpecBuilder.addComment("If you are using Intellij IDEA or Android Studio, use Go to declaration (Ctrl/⌘+B or Ctrl/⌘+Click)")
        methodSpecBuilder.addComment("to navigate to '${migrationMethods.first().method.simpleName}()'")

        for ((clazz, method) in migrationMethods) {
            val statement = "new %s().%s()".format(clazz.qualifiedName, method.simpleName)
            methodSpecBuilder.addStatement(statement)
        }

        classBuilder.addMethod(methodSpecBuilder.build())

        return JavaFile.builder("moxy", classBuilder.build())
            .indent("\t")
            .build()
    }
}
