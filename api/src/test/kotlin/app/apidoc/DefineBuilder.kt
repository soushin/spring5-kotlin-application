package app.apidoc

import app.annotation.ApiDocProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.io.File
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.javaField

/**
 *
 * @author nsoushi
 */
class DefineBuilder() {
    constructor(init: DefineBuilder.() -> Unit) : this() {
        init()
    }

    private val objectMapper = ObjectMapper().registerModule(KotlinModule())

    private var versionHolder: String = ""
    private var nameHolder: String = "name"
    private var headerHolder: MutableList<ApiHeader>? = mutableListOf()
    private var paramHolder: MutableList<ApiParam>? = mutableListOf()
    private var successFiledHolder: MutableList<Any>? = mutableListOf()
    private var successExampleHolder: ApiResponseExample? = null
    private var errorFiledHolder: MutableList<Any>? = mutableListOf()
    private var errorExampleHolder: ApiResponseExample? = null

    fun version(init: () -> String) {
        versionHolder = init()
    }

    fun name(init: () -> String) {
        nameHolder = init()
    }

    fun header(init: () -> ApiHeader) {
        headerHolder?.add(init())
    }

    fun param(init: () -> ApiParam) {
        paramHolder?.add(init())
    }

    fun success(init: () -> Any) {
        successFiledHolder?.add(init())
    }

    fun successExample(init: () -> ApiResponseExample) {
        successExampleHolder = init()
    }

    fun errorExample(init: () -> ApiResponseExample) {
        errorExampleHolder = init()
    }

    fun error(init: () -> Any) {
        errorFiledHolder?.add(init())
    }

    private fun headerView(holder: List<ApiHeader>?): List<ApiHeaderView>? {
        val responseView = if (holder != null) {
            val fields = holder
            fields.map { field ->
                ApiHeaderView(field.example.javaClass.simpleName, field.name, "${field.description}, Required:${field.required.toString().toUpperCase()}")
            }
        } else {
            null
        }
        return responseView
    }

    private fun paramView(holder: List<ApiParam>?): List<ApiParamView>? {
        val responseView = if (holder != null) {
            val fields = holder
            fields.map { field ->
                val description = if (!field.required) {
                    "${field.description}, Required:${field.required.toString().toUpperCase()}, Default:${field.default}"
                } else {
                    "${field.description}, Required:${field.required.toString().toUpperCase()}"
                }
                ApiParamView(field.example.javaClass.simpleName, field.name, description)
            }
        } else {
            null
        }
        return responseView
    }

    private fun exampleView(holder: ApiResponseExample?): ApiExampleView? {
        val responseExampleView = if (holder != null) {
            val example = holder
            ApiExampleView(example.name,
                    "${example.statusCode.value()} ${example.statusCode.reasonPhrase}",
                    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(example.model))
        } else {
            null
        }
        return responseExampleView
    }

    private fun fieldView(holder: List<Any>?): List<ApiFieldView>? {
        val responseView = if (holder != null) {
            val fields = holder
            fields.map { field ->
                val clazz = field.javaClass.kotlin
                clazz.declaredMemberProperties.map {
                    val javaField = it.javaField!!
                    val anno = javaField.getAnnotation(ApiDocProperty::class.java)
                    if (anno != null)
                        ApiFieldView(javaField.type.simpleName, javaField.name, "${anno.value}, Nullable:${anno.nullable.toString().toUpperCase()}, Example:${anno.example}")
                    else
                        ApiFieldView(javaField.type.simpleName, javaField.name, "")
                }
            }.flatten()

        } else {
            null
        }
        return responseView
    }

    fun build(): Define {
        // header
        val headerView = headerView(headerHolder)
        // param
        val paramView = paramView(paramHolder)
        // success
        val successExampleView = exampleView(successExampleHolder)
        val successFieldView = fieldView(successFiledHolder)
        // errors
        val errorExampleView = exampleView(errorExampleHolder)
        val errorFieldView = fieldView(errorFiledHolder)

        return Define(name = nameHolder,
                header = headerView,
                param = paramView,
                success = successFieldView,
                successExample = successExampleView,
                error = errorFieldView,
                errorExample = errorExampleView)
    }

    fun genDoc() {
        val defineName = nameHolder
        val docs = this.build().toString()
        val path = File("").absolutePath
        File("${path}/docs/define").mkdir()
        File("${path}/docs/define/${defineName}-${versionHolder}.kt").writeBytes(docs.toByteArray())
    }
}
