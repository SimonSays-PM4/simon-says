package ch.zhaw.pm4.simonsays.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.resource.NoResourceFoundException

class ErrorMessageModel(
        var status: Int? = null,
        var message: String? = null,
        var errors: Map<String, String>? = null
)

@RestControllerAdvice
class ExceptionControllerAdvice {

    @ExceptionHandler(ValidationException::class)
    fun handleValidationException(ex: ValidationException): ResponseEntity<ErrorMessageModel> {
        val errorMessage = ErrorMessageModel(
                HttpStatus.BAD_REQUEST.value(),
                ex.message
        )
        return ResponseEntity(errorMessage, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<ErrorMessageModel> {
        val errors = ex.bindingResult.fieldErrors
                .associate { error -> error.field to (error.defaultMessage ?: "Unknown validation error") }

        val errorMessageModel = ErrorMessageModel(
                status = 400,
                message = "Validation failed",
                errors = errors
        )

        return ResponseEntity(errorMessageModel, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleArgumentTypeMismatchException(ex: MethodArgumentTypeMismatchException): ResponseEntity<ErrorMessageModel> {

        val errorMessageModel = ErrorMessageModel(
                status = 400,
                message = ex.message,
        )

        return ResponseEntity(errorMessageModel, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFoundException(ex: ResourceNotFoundException): ResponseEntity<ErrorMessageModel> {
        val errorMessage = ErrorMessageModel(
                HttpStatus.NOT_FOUND.value(),
                ex.message
        )
        return ResponseEntity(errorMessage, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleHttpRequestMethodNotSupportedException(ex: HttpRequestMethodNotSupportedException): ResponseEntity<ErrorMessageModel> {
        val errorMessage = ErrorMessageModel(
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                ex.message
        )
        return ResponseEntity(errorMessage, HttpStatus.METHOD_NOT_ALLOWED)
    }

    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNoResourceFoundException(ex: NoResourceFoundException): ResponseEntity<ErrorMessageModel> {
        val errorMessage = ErrorMessageModel(
                HttpStatus.NOT_FOUND.value(),
                ex.message
        )
        return ResponseEntity(errorMessage, HttpStatus.NOT_FOUND)
    }

}
