package com.example.data.firestore

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestoreException

enum class OperationType {
    GET,
    LIST,
    CREATE,
    UPDATE,
    DELETE,
    TRANSACTION,
    BATCH
}

object FirestoreErrorHandler {
    private const val TAG = "FirestoreError"

    fun handleFirestoreError(
        exception: Exception,
        operationType: OperationType,
        path: String
    ): String {
        val errorCode = if (exception is FirebaseFirestoreException) {
            exception.code.name
        } else {
            "UNKNOWN"
        }

        val logMessage = """
            {
                "error": "$errorCode",
                "operation": "${operationType.name}",
                "path": "$path",
                "message": "${exception.localizedMessage?.replace("\"", "'") ?: "No message"}"
            }
        """.trimIndent()

        Log.e(TAG, logMessage, exception)

        return when (errorCode) {
            "PERMISSION_DENIED" -> "Permiso denegado: Verifica que hayas iniciado sesión con tu cuenta de Google."
            "NOT_FOUND" -> "El registro solicitado no fue encontrado en la nube."
            "ALREADY_EXISTS" -> "Este registro ya existe en el catálogo."
            "RESOURCE_EXHAUSTED" -> "Límite de solicitudes alcanzado. Intenta de nuevo en unos momentos."
            "UNAVAILABLE" -> "Servicio en la nube no disponible temporalmente. Revisa tu conexión a internet."
            else -> exception.localizedMessage ?: "Ocurrió un error al conectar con la nube de GD Music."
        }
    }
}
