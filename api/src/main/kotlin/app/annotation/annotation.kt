package app.annotation

/**
 *
 * @author nsoushi
 */
@Target(AnnotationTarget.FIELD)
annotation class ApiDocProperty(val nullable: Boolean = false, val value: String, val example: String)
