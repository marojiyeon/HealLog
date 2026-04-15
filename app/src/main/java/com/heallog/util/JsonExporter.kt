package com.heallog.util

import com.heallog.BuildConfig
import com.heallog.data.local.entity.HospitalVisit
import com.heallog.data.local.entity.Injury
import com.heallog.data.local.entity.Medication
import com.heallog.data.local.entity.PainLog

object JsonExporter {

    fun toJson(
        injuries: List<Injury>,
        painLogs: List<PainLog>,
        visits: List<HospitalVisit>,
        medications: List<Medication>
    ): String {
        val painLogsByInjury = painLogs.groupBy { it.injuryId }
        val visitsByInjury = visits.groupBy { it.injuryId }
        val medicationsByInjury = medications.groupBy { it.injuryId }

        val injuryArray = injuries.joinToString(",\n    ", "[\n    ", "\n  ]") { injury ->
            buildInjuryJson(
                injury,
                painLogsByInjury[injury.id].orEmpty(),
                visitsByInjury[injury.id].orEmpty(),
                medicationsByInjury[injury.id].orEmpty()
            )
        }

        return "{\n  \"version\": 1,\n  \"appVersion\": \"${BuildConfig.VERSION_NAME}\",\n  \"exportedAt\": \"${java.time.LocalDateTime.now()}\",\n  \"injuries\": $injuryArray\n}"
    }

    private fun buildInjuryJson(
        injury: Injury,
        painLogs: List<PainLog>,
        visits: List<HospitalVisit>,
        medications: List<Medication>
    ): String {
        val painLogsJson = painLogs.joinToString(",\n        ", "[\n        ", "\n      ]") { p ->
            """{
          "id": ${p.id},
          "painLevel": ${p.painLevel},
          "note": ${p.note.toJsonString()},
          "loggedAt": "${p.loggedAt}",
          "updatedAt": ${p.updatedAt?.let { "\"$it\"" } ?: "null"}
        }"""
        }

        val visitsJson = visits.joinToString(",\n        ", "[\n        ", "\n      ]") { v ->
            """{
          "id": ${v.id},
          "visitDate": "${v.visitDate}",
          "hospitalName": ${v.hospitalName.toJsonString()},
          "doctorName": ${v.doctorName?.toJsonString() ?: "null"},
          "diagnosis": ${v.diagnosis?.toJsonString() ?: "null"},
          "treatmentNote": ${v.treatmentNote.toJsonString()},
          "nextAppointment": ${v.nextAppointment?.let { "\"$it\"" } ?: "null"},
          "cost": ${v.cost ?: "null"},
          "isInsuranceCovered": ${v.isInsuranceCovered ?: "null"}
        }"""
        }

        val medicationsJson = medications.joinToString(",\n        ", "[\n        ", "\n      ]") { m ->
            """{
          "id": ${m.id},
          "name": ${m.name.toJsonString()},
          "dosage": ${m.dosage.toJsonString()},
          "frequency": ${m.frequency.toJsonString()},
          "startDate": "${m.startDate}",
          "endDate": ${m.endDate?.let { "\"$it\"" } ?: "null"},
          "isActive": ${m.isActive},
          "sideEffectNote": ${m.sideEffectNote?.toJsonString() ?: "null"}
        }"""
        }

        return """{
      "id": ${injury.id},
      "bodyPart": ${injury.bodyPart.toJsonString()},
      "title": ${injury.title.toJsonString()},
      "description": ${injury.description.toJsonString()},
      "painLevel": ${injury.painLevel},
      "occurredAt": "${injury.occurredAt}",
      "status": "${injury.status}",
      "createdAt": "${injury.createdAt}",
      "updatedAt": ${injury.updatedAt?.let { "\"$it\"" } ?: "null"},
      "painLogs": $painLogsJson,
      "hospitalVisits": $visitsJson,
      "medications": $medicationsJson
    }"""
    }

    private fun String.toJsonString(): String {
        val escaped = this
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
        return "\"$escaped\""
    }
}
