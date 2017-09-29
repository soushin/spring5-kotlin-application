package app.apidoc

import org.springframework.http.HttpStatus


/**
 *
 * @author nsoushi
 */
data class Define(val name: String,
                  val header: List<ApiHeaderView>?,
                  val param: List<ApiParamView>?,
                  val success: List<ApiFieldView>?,
                  val successExample: ApiExampleView?,
                  val error: List<ApiFieldView>?,
                  val errorExample: ApiExampleView?) {

    override fun toString(): String {

        // @apiDefine
        val define = "@apiDefine ${name}"
        // @apiHeader
        val header = header(header)
        // @apiParam
        val param = param(param)
        // @apiSuccess
        val successFiled = field(success)
        // @apiSuccessExample
        val (successExample, successStatusCode, successJson) = example(successExample)
        // @apiError
        val errorFiled = field(error, false)
        // @apiSuccessExample
        val (errorExample, errorStatusCode, errorJson) = example(errorExample, false)

        return """
/**
${define}

${header}

${param}

${successFiled}

${successExample}
${successStatusCode}
${successJson}

${errorFiled}

${errorExample}
${errorStatusCode}
${errorJson}
 */
"""
    }

    private fun header(fields: List<ApiHeaderView>?): String {
        val filed = fields?.map {
            "@apiHeader {${it.type}} ${it.name} ${it.description}"
        }?.joinToString(separator = "\n")
        return filed ?: ""
    }

    private fun param(fields: List<ApiParamView>?): String {
        val filed = fields?.map {
            "@apiParam {${it.type}} ${it.name} ${it.description}"
        }?.joinToString(separator = "\n")
        return filed ?: ""
    }

    private fun field(fields: List<ApiFieldView>?, success: Boolean = true): String {
        val response = if (success) "Success" else "Error"
        val filed = fields?.map {
            "@api${response} {${it.type}} ${it.name} ${it.description}"
        }?.joinToString(separator = "\n")
        return filed ?: ""
    }

    private fun example(example: ApiExampleView?, success: Boolean = true): List<String> {
        val response = if (success) "Success" else "Error"
        return if (example != null) {
            listOf(
                    "@api${response}Example ${example.name}",
                    example.statusCode,
                    example.json)
        } else {
            listOf("", "", "")
        }
    }
}

data class ApiHeader(val name: String, val description: String, val example: Any, val required: Boolean = true)
data class ApiParam(val name: String, val description: String, val example: Any, val required: Boolean = true, val default: String = "")
interface ApiResponseExample {
    val name: String
    val statusCode: HttpStatus
    val model: Any
    val success: Boolean
}
data class ApiSuccessExample(override val name: String,
                             override val statusCode: HttpStatus,
                             override val model: Any,
                             override val success: Boolean = true) : ApiResponseExample
data class ApiErrorExample(override val name: String,
                           override val statusCode: HttpStatus,
                           override val model: Any,
                           override val success: Boolean = true) : ApiResponseExample
data class ApiHeaderView(val type: String, val name: String, val description: String)
data class ApiParamView(val type: String, val name: String, val description: String)
data class ApiFieldView(val type: String, val name: String, val description: String)
data class ApiExampleView(val name: String, val statusCode: String, val json: String)
