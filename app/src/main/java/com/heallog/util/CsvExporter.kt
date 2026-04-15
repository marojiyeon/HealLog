package com.heallog.util

import com.heallog.data.local.entity.HospitalVisit
import com.heallog.data.local.entity.Injury
import com.heallog.data.local.entity.Medication
import com.heallog.data.local.entity.PainLog

object CsvExporter {

    private const val BOM = "\uFEFF"

    fun injuriesToCsv(injuries: List<Injury>): String {
        val header = "id,bodyPart,title,description,painLevel,occurredAt,status,createdAt,updatedAt"
        val rows = injuries.joinToString("\n") { i ->
            listOf(
                i.id, i.bodyPart.escape(), i.title.escape(), i.description.escape(),
                i.painLevel, i.occurredAt, i.status, i.createdAt, i.updatedAt ?: ""
            ).joinToString(",")
        }
        return "$BOM$header\n$rows"
    }

    fun painLogsToCsv(painLogs: List<PainLog>): String {
        val header = "id,injuryId,painLevel,note,loggedAt,updatedAt"
        val rows = painLogs.joinToString("\n") { p ->
            listOf(
                p.id, p.injuryId, p.painLevel, p.note.escape(),
                p.loggedAt, p.updatedAt ?: ""
            ).joinToString(",")
        }
        return "$BOM$header\n$rows"
    }

    fun visitsToCsv(visits: List<HospitalVisit>): String {
        val header = "id,injuryId,visitDate,hospitalName,doctorName,diagnosis,treatmentNote,nextAppointment,cost,isInsuranceCovered,createdAt"
        val rows = visits.joinToString("\n") { v ->
            listOf(
                v.id, v.injuryId, v.visitDate, v.hospitalName.escape(),
                v.doctorName?.escape() ?: "", v.diagnosis?.escape() ?: "",
                v.treatmentNote.escape(), v.nextAppointment ?: "", v.cost ?: "",
                v.isInsuranceCovered ?: "", v.createdAt
            ).joinToString(",")
        }
        return "$BOM$header\n$rows"
    }

    fun medicationsToCsv(medications: List<Medication>): String {
        val header = "id,injuryId,hospitalVisitId,name,dosage,frequency,startDate,endDate,isActive,sideEffectNote"
        val rows = medications.joinToString("\n") { m ->
            listOf(
                m.id, m.injuryId, m.hospitalVisitId ?: "", m.name.escape(),
                m.dosage.escape(), m.frequency.escape(), m.startDate,
                m.endDate ?: "", m.isActive, m.sideEffectNote?.escape() ?: ""
            ).joinToString(",")
        }
        return "$BOM$header\n$rows"
    }

    private fun String.escape(): String {
        val needsQuoting = contains(',') || contains('"') || contains('\n') || contains('\r')
        if (!needsQuoting) return this
        val normalized = replace("\"", "\"\"")
            .replace("\r\n", "\n")
            .replace("\r", "\n")
            .replace("\n", "\r\n")
        return "\"$normalized\""
    }
}
