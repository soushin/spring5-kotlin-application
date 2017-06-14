package app.service

/**
 *
 * @author nsoushi
 */
interface ApplicationService<IN, OUT> {
    operator fun invoke(command: IN): OUT
}