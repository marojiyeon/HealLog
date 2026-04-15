package com.heallog.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.heallog.data.repository.HospitalRepository
import com.heallog.data.repository.InjuryRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataExportManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val injuryRepository: InjuryRepository,
    private val hospitalRepository: HospitalRepository
) {

    private val timestampFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")

    fun clearExports() {
        File(context.cacheDir, "exports").listFiles()?.forEach { it.delete() }
    }

    private fun pruneOldExports() {
        val cutoff = System.currentTimeMillis() - 24 * 60 * 60 * 1000L
        File(context.cacheDir, "exports").listFiles()
            ?.filter { it.lastModified() < cutoff }
            ?.forEach { it.delete() }
    }

    suspend fun exportToCsv(): Uri = withContext(Dispatchers.IO) {
        pruneOldExports()
        val injuries = injuryRepository.getAllInjuriesSnapshot()
        val painLogs = injuryRepository.getAllPainLogsSnapshot()
        val visits = hospitalRepository.getAllVisitsSnapshot()
        val medications = hospitalRepository.getAllMedicationsSnapshot()

        val timestamp = LocalDateTime.now().format(timestampFormatter)
        val exportDir = File(context.cacheDir, "exports").also { it.mkdirs() }
        val zipFile = File(exportDir, "heallog_${timestamp}.csv.zip")

        java.util.zip.ZipOutputStream(zipFile.outputStream().buffered()).use { zip ->
            writeZipEntry(zip, "injuries.csv", CsvExporter.injuriesToCsv(injuries))
            writeZipEntry(zip, "pain_logs.csv", CsvExporter.painLogsToCsv(painLogs))
            writeZipEntry(zip, "hospital_visits.csv", CsvExporter.visitsToCsv(visits))
            writeZipEntry(zip, "medications.csv", CsvExporter.medicationsToCsv(medications))
        }

        fileUri(zipFile)
    }

    suspend fun exportToJson(): Uri = withContext(Dispatchers.IO) {
        pruneOldExports()
        val injuries = injuryRepository.getAllInjuriesSnapshot()
        val painLogs = injuryRepository.getAllPainLogsSnapshot()
        val visits = hospitalRepository.getAllVisitsSnapshot()
        val medications = hospitalRepository.getAllMedicationsSnapshot()

        val timestamp = LocalDateTime.now().format(timestampFormatter)
        val exportDir = File(context.cacheDir, "exports").also { it.mkdirs() }
        val jsonFile = File(exportDir, "heallog_${timestamp}.json")

        jsonFile.writeText(JsonExporter.toJson(injuries, painLogs, visits, medications), Charsets.UTF_8)

        fileUri(jsonFile)
    }

    private fun writeZipEntry(zip: java.util.zip.ZipOutputStream, name: String, content: String) {
        zip.putNextEntry(java.util.zip.ZipEntry(name))
        zip.write(content.toByteArray(Charsets.UTF_8))
        zip.closeEntry()
    }

    private fun fileUri(file: File): Uri =
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}
