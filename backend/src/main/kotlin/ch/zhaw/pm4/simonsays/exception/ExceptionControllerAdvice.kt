package ch.zhaw.pm4.simonsays.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

class ErrorMessageModel(
        var status: Int? = null,
        var message: String? = null
)

@ControllerAdvice
class ExceptionControllerAdvice {

    @ExceptionHandler
    fun handleNotFoundException(ex: IllegalStateException): ResponseEntity<ErrorMessageModel> {

        val errorMessage = ErrorMessageModel(
                HttpStatus.NOT_FOUND.value(),
                ex.message
        )
        return ResponseEntity(errorMessage, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler
    fun handleValidationException(ex: ValidationException): ResponseEntity<ErrorMessageModel> {
        val errorMessage = ErrorMessageModel(
                HttpStatus.BAD_REQUEST.value(),
                ex.message
        )
        return ResponseEntity(errorMessage, HttpStatus.BAD_REQUEST)
    }
}
