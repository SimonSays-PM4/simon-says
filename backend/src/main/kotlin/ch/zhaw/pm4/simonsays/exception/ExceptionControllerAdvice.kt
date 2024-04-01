package ch.zhaw.pm4.simonsays.exception

import ch.zhaw.pm4.simonsays.entity.NoArgAnnotation
import jakarta.persistence.Entity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

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
}
