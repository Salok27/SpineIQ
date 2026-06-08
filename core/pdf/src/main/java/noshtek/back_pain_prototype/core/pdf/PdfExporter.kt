package noshtek.back_pain_prototype.core.pdf

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import noshtek.back_pain_prototype.core.data.db.dao.FullAssessmentData
import noshtek.back_pain_prototype.core.data.db.entity.ScoresRecordEntity
import noshtek.back_pain_prototype.core.data.model.Gender
import noshtek.back_pain_prototype.core.scoring.model.*
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class PdfReportInput(
    val userName: String,
    val userAge: Int,
    val userGender: Gender,
    val fullData: FullAssessmentData,
    val scores: ScoresRecordEntity
)

class PdfExporter(private val context: Context) {

    suspend fun generatePdf(input: PdfReportInput): File = withContext(Dispatchers.IO) {
        val pdf = PdfDocument()
        Renderer(pdf, input).render()

        val dir = File(context.cacheDir, "pdfs").also { it.mkdirs() }
        val dateStr = try {
            LocalDate.ofEpochDay(input.fullData.record.assessmentDate)
                .format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        } catch (e: Exception) { "unknown" }
        val file = File(dir, "SpineIQ_Assessment_${dateStr}.pdf")

        FileOutputStream(file).use { pdf.writeTo(it) }
        pdf.close()
        file
    }

    private inner class Renderer(private val doc: PdfDocument, private val inp: PdfReportInput) {

        // A4 at 72 pt/inch
        private val PW = 595
        private val PH = 842
        private val M = 43f
        private val CW = PW - 2 * M
        private val FOOTER_H = 28f
        private val MAX_Y get() = PH - M - FOOTER_H

        private var page: PdfDocument.Page? = null
        private var cv: Canvas? = null
        private var y = M
        private var pageNum = 0

        private val dateLabel = try {
            LocalDate.ofEpochDay(inp.fullData.record.assessmentDate)
                .format(DateTimeFormatter.ofPattern("d MMMM yyyy"))
        } catch (e: Exception) { "—" }

        private val pBody = body(9f)
        private val pBold = bold(9f)
        private val pSmall = body(8f).also { it.color = Color.parseColor("#666666") }
        private val pSection = bold(10f).also { it.color = Color.parseColor("#1565C0") }
        private val pTitle = bold(16f).also { it.color = Color.parseColor("#0D47A1") }
        private val pDivider = Paint().also { it.color = Color.parseColor("#BDBDBD"); it.strokeWidth = 0.5f }
        private val pBlueBg = fill(Color.parseColor("#E3F2FD"))
        private val pNavyBg = fill(Color.parseColor("#0D47A1"))
        private val pWhiteTxt = bold(14f).also { it.color = Color.WHITE }
        private val pSubHeader = body(9f).also { it.color = Color.parseColor("#BBDEFB") }

        fun render() {
            newPage()
            drawPageOneHeader()
            drawSection1(); drawSection2(); drawSection3()
            drawSection4(); drawSection5(); drawSection6()
            drawSection7(); drawSection8(); drawSection9()
            drawSection10(); drawSection11(); drawSection12()
            finishCurrentPage()
        }

        private fun newPage() {
            pageNum++
            val info = PdfDocument.PageInfo.Builder(PW, PH, pageNum).create()
            page = doc.startPage(info)
            cv = page!!.canvas
            y = M
        }

        private fun finishCurrentPage() {
            val c = cv ?: return
            val fy = PH - M - FOOTER_H + 6f
            c.drawLine(M, fy, PW - M, fy, pDivider)
            small("Assessment Date: $dateLabel", M, fy + 10f)
            val pg = "Page $pageNum"
            small(pg, PW - M - pSmall.measureText(pg), fy + 10f)
            small("This report is a self-assessment screening tool and does not constitute medical advice.", M, fy + 20f)
            page?.let { doc.finishPage(it) }
            page = null; cv = null
        }

        private fun checkBreak(need: Float = 14f) {
            if (y + need > MAX_Y) {
                finishCurrentPage()
                newPage()
                runningHeader()
                y += 4f
            }
        }

        private fun drawPageOneHeader() {
            val c = cv!!
            c.drawRect(M, y, PW - M, y + 28f, pNavyBg)
            c.drawText("SpineIQ", M + 8f, y + 19f, pWhiteTxt)
            c.drawText("Spine Severity System — Personal Self-Assessment", M + 76f, y + 19f, pSubHeader)
            y += 34f

            c.drawText("Your Back Pain Risk Assessment Report", M, y, pTitle)
            y += 22f

            val info = buildString {
                append("Name: ${inp.userName}   |   Age: ${inp.userAge} yrs   |   ")
                append("Gender: ${inp.userGender.name.lowercase().replaceFirstChar { it.uppercase() }}")
            }
            text(info, M, y, pBody)
            y += 13f
            small("Assessment Date: $dateLabel", M, y)
            y += 18f
            divider()
            y += 8f
        }

        private fun runningHeader() {
            val c = cv!!
            c.drawRect(M, y, PW - M, y + 16f, pBlueBg)
            val hp = bold(8f).also { it.color = Color.parseColor("#0D47A1") }
            c.drawText("SpineIQ Report — ${inp.userName} — $dateLabel", M + 4f, y + 11f, hp)
            y += 22f
        }

        private fun drawSection1() {
            sHead("Section 1 — Your Summary")
            tRow("Full Name", inp.userName)
            tRow("Age", "${inp.userAge} years")
            tRow("Gender", inp.userGender.name.lowercase().replaceFirstChar { it.uppercase() })
            tRow("BMI", "${"%.1f".format(inp.scores.bmiScore)} (${inp.scores.bmiCategory.name.lowercase().replaceFirstChar { it.uppercase() }})")
            inp.fullData.occupation?.let { o ->
                tRow("Occupation", o.occupationType.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() })
                tRow("Daily Hours", "Sitting ${"%.0f".format(o.sittingHoursPerDay)} · Standing ${"%.0f".format(o.standingHoursPerDay)} · Driving ${"%.0f".format(o.drivingHoursPerDay)} hrs")
                tRow("Lifting Level", o.liftingLevel.name.lowercase().replaceFirstChar { it.uppercase() })
            }
            gap(6f)
        }

        private fun drawSection2() {
            sHead("Section 2 — BMI Analysis")
            tRow("BMI Value", "${"%.1f".format(inp.scores.bmiScore)}")
            tRow("Category", inp.scores.bmiCategory.name.lowercase().replaceFirstChar { it.uppercase() })
            val interp = when (inp.scores.bmiCategory) {
                BmiCategory.UNDERWEIGHT -> "Your weight may indicate reduced muscle mass, which can contribute to spinal instability."
                BmiCategory.NORMAL -> "Your weight is not adding extra load to your spine."
                BmiCategory.OVERWEIGHT -> "Your weight is adding moderate additional mechanical load to your spinal structures and discs."
                BmiCategory.OBESE -> "Your weight is creating significant mechanical overload on your discs and lumbar joints."
            }
            wRow("What this means", interp)
            gap(6f)
        }

        private fun drawSection3() {
            sHead("Section 3 — Lifestyle Assessment")
            tRow("Your Lifestyle Risk", inp.scores.lifestyleRiskTier.name.lowercase().replaceFirstChar { it.uppercase() })
            gap(3f)
            tHead("Habit", "Your Value", "Risk")
            inp.fullData.lifestyle?.let { ls ->
                tData("Sitting", "${"%.0f".format(ls.sittingHoursPerDay)} hrs/day", inp.scores.sittingRisk.name)
                tData("Walking", "${"%.0f".format(ls.walkingMinutesPerDay)} min/day", inp.scores.walkingRisk.name)
                tData("Exercise", "${ls.exerciseDaysPerWeek} days/week", inp.scores.exerciseRisk.name)
                tData("Sleep", "${"%.0f".format(ls.sleepHoursPerNight)} hrs · ${ls.sleepQuality.name.lowercase()}", inp.scores.sleepRisk.name)
            }
            gap(4f)
            val ageBand = when (inp.scores.ageGroup) {
                AgeGroup.YOUNG_ADULT -> "20–30"; AgeGroup.MID_ADULT -> "31–45"
                AgeGroup.PRE_SENIOR -> "46–60"; AgeGroup.SENIOR -> "61+"
            }
            wRow("Age Context", "For your age group ($ageBand yrs), age-specific thresholds were applied for sitting, walking, and sleep risk.")
            gap(6f)
        }

        private fun drawSection4() {
            sHead("Section 4 — Activity Assessment")
            inp.fullData.lifestyle?.let { ls ->
                tRow("Exercise Days", "${ls.exerciseDaysPerWeek} days/week")
                tRow("Exercise Types", ls.exerciseTypes.joinToString { it.name.replace('_', ' ').lowercase() })
                tRow("High-Impact Modifier", if (inp.scores.exerciseTypeModifier) "Yes — high-impact exercise noted" else "No")
                tRow("Walking", "${"%.0f".format(ls.walkingMinutesPerDay)} min/day")
                ls.dailySteps?.let { tRow("Daily Steps", "$it") }
                ls.activeMinutesPerDay?.let { tRow("Active Minutes", "$it min/day") }
                ls.restingHeartRate?.let { tRow("Resting Heart Rate", "$it bpm") }
                ls.averageHeartRate?.let { tRow("Average Heart Rate", "$it bpm") }
            }
            tRow("Exercise Risk", inp.scores.exerciseRisk.name.lowercase().replaceFirstChar { it.uppercase() })
            gap(6f)
        }

        private fun drawSection5() {
            sHead("Section 5 — Occupational Risk")
            inp.fullData.occupation?.let { o ->
                tRow("Occupation", o.occupationType.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() })
                tRow("Sitting", "${"%.0f".format(o.sittingHoursPerDay)} hrs/day")
                tRow("Standing", "${"%.0f".format(o.standingHoursPerDay)} hrs/day")
                tRow("Driving", "${"%.0f".format(o.drivingHoursPerDay)} hrs/day")
                tRow("Lifting Level", o.liftingLevel.name.lowercase().replaceFirstChar { it.uppercase() })
                o.workPatternNotes?.let { n -> if (n.isNotBlank()) wRow("Notes", n) }
            }
            gap(6f)
        }

        private fun drawSection6() {
            sHead("Section 6 — Pain Pattern Analysis")
            inp.fullData.pain?.let { p ->
                tRow("Pain Locations", p.painLocations.joinToString { it.name.replace('_', ' ').lowercase() }.ifBlank { "—" })
                tRow("VAS Score", "${p.vasScore} / 10")
                tRow("Pain Duration", p.painDuration.name.lowercase().replaceFirstChar { it.uppercase() })
                tRow("Pain Pattern", p.painPattern.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() })
                tRow("Triggers", p.painTriggers.joinToString { it.name.replace('_', ' ').lowercase() }.ifBlank { "—" })
                tRow("Radiculopathy Severity", p.radiculopathySeverity.name.lowercase().replaceFirstChar { it.uppercase() })
                p.radiationLocation?.let { tRow("Radiation Side", it.name.replace('_', ' ').lowercase().replaceFirstChar { c -> c.uppercase() }) }
                tRow("Functional Limitation Severity", p.functionalLimitationSeverity?.name?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "—")
                p.functionalLimitationsText?.let { n -> if (n.isNotBlank()) wRow("Your notes", n) }
            }
            tRow("Chronicity Points", "${inp.scores.chronicityPoints}")
            gap(6f)
        }

        private fun drawSection7() {
            sHead("Section 7 — Functional Assessment (Modified ODI)")
            inp.fullData.functional?.let { f ->
                tHead("Activity", "Level", "Points")
                tData("Walking", f.walking.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() }, "${f.walking.points}")
                tData("Sitting", f.sitting.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() }, "${f.sitting.points}")
                tData("Standing", f.standing.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() }, "${f.standing.points}")
                tData("Sleep", f.sleep.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() }, "${f.sleep.points}")
                tData("Daily Activities", f.dailyActivities.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() }, "${f.dailyActivities.points}")
            }
            gap(3f)
            tRow("ODI Total", "${inp.scores.odiScore} / 10  (SSS Points: ${inp.scores.odiPoints})")
            gap(6f)
        }

        private fun drawSection8() {
            sHead("Section 8 — Red Flag Status")
            val rf = inp.fullData.redFlags
            if (rf == null || !rf.hasAnyRedFlag) {
                tRow("Status", "No red flags confirmed")
            } else {
                tRow("Status", "RED FLAG CONFIRMED — Urgent medical attention required")
                listOf(
                    "History of cancer" to rf.historyCancer,
                    "Unexplained weight loss" to rf.unexplainedWeightLoss,
                    "Fever / infection" to rf.feverOrInfection,
                    "Recent major trauma" to rf.recentMajorTrauma,
                    "Bowel / bladder dysfunction" to rf.bowelBladderDysfunction,
                    "Saddle anaesthesia" to rf.saddleAnaesthesia,
                    "Progressive neurological deficit" to rf.progressiveNeurologicalDeficit,
                    "Other serious symptoms" to rf.otherSeriousPathologySuspicion
                ).filter { (_, v) -> v }.forEach { (lbl, _) -> tRow("  •", lbl) }
                gap(3f)
                wRow("Action Required", "Please seek immediate medical attention from a doctor or emergency department. Do not delay.")
            }
            gap(6f)
        }

        private fun drawSection9() {
            sHead("Section 9 — Key Risk Factors")
            val factors = buildRiskFactors()
            if (factors.isEmpty()) {
                wrapped("No significant risk factors identified at this time.")
            } else {
                factors.forEach { (lbl, detail) -> wRow("• $lbl", detail) }
            }
            gap(6f)
        }

        private fun buildRiskFactors(): List<Pair<String, String>> {
            val s = inp.scores
            return buildList {
                if (s.redFlagScore > 0) add("Red Flag" to "A confirmed red flag symptom requiring urgent medical attention.")
                if (s.bmiPoints >= 2) add("Obesity" to "Your BMI (${"%.1f".format(s.bmiScore)}) is creating significant mechanical overload on your spinal structures.")
                else if (s.bmiPoints == 1) add("Overweight" to "Your BMI (${"%.1f".format(s.bmiScore)}) is adding moderate additional load to your spine.")
                if (s.radiculopathyScore >= 2) add("Radiculopathy" to "Significant nerve root irritation or compression identified in your assessment.")
                else if (s.radiculopathyScore == 1) add("Mild Radiculopathy" to "Mild leg pain or referred symptoms noted in your assessment.")
                if (s.sittingRisk == RiskTier.HIGH) add("Prolonged Sitting" to "Your daily sitting exceeds the age-appropriate threshold, increasing disc pressure and weakening core muscles.")
                if (s.walkingRisk == RiskTier.HIGH) add("Low Walking Activity" to "Insufficient walking reduces your spinal mobility and postural endurance.")
                if (s.exerciseRisk == RiskTier.HIGH) add("Exercise Risk" to "Your exercise pattern is contributing to spinal deconditioning.")
                if (s.sleepRisk == RiskTier.HIGH) add("Poor Sleep" to "Your sleep is impairing musculoskeletal recovery and may be amplifying your pain perception.")
                if (s.chronicityPoints >= 1) add("Chronic Pain" to "Pain duration >6 weeks suggests established tissue changes or central sensitisation.")
                if (s.odiPoints >= 2) add("Severe Functional Limitation" to "Significant difficulty with multiple daily activities was identified.")
            }
        }

        private fun drawSection10() {
            sHead("Section 10 — Your Back Pain Risk Score")
            val s = inp.scores
            tRow("Your SSS Score", "${s.totalSSSScore} / 11")
            tRow("Severity Tier", s.sssSeverityTier.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() })
            tRow("Lifestyle Risk", s.lifestyleRiskTier.name.lowercase().replaceFirstChar { it.uppercase() })
            tRow("Composite Classification", s.backPainRiskClassification.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() })
            gap(4f)
            tHead("SSS Component", "Your Value", "Points")
            tData("VAS — Pain Intensity", "${s.vasScore}/10", "${s.vasPoints}")
            tData("Radiculopathy / Leg Pain", "${s.radiculopathyScore}", "${s.radiculopathyScore}")
            tData("ODI — Functional", "${s.odiScore}/10", "${s.odiPoints}")
            tData("BMI", "${"%.1f".format(s.bmiScore)} (${s.bmiCategory.name})", "${s.bmiPoints}")
            tData("Chronicity", if (s.chronicityPoints > 0) ">6 weeks" else "Acute/Subacute", "${s.chronicityPoints}")
            tData("Red Flag Override", if (s.redFlagScore > 0) "Confirmed" else "None", if (s.redFlagScore > 0) "→ Score=11" else "0")
            gap(4f)
            tHead("Lifestyle Component", "Risk Tier", "")
            tData("Sitting", s.sittingRisk.name, "")
            tData("Walking", s.walkingRisk.name, "")
            tData("Exercise", s.exerciseRisk.name, "")
            tData("Sleep", s.sleepRisk.name, "")
            gap(6f)
        }

        private fun drawSection11() {
            sHead("Section 11 — Probable Contributors to Your Back Pain")
            wrapped(buildNarrative())
            gap(6f)
        }

        private fun buildNarrative(): String {
            val s = inp.scores
            val parts = mutableListOf<String>()
            if (s.redFlagScore > 0)
                parts += "A confirmed red flag symptom has been identified. Please seek immediate medical attention from a doctor or emergency department — do not delay."
            if (s.bmiPoints >= 2)
                parts += "Your BMI (${"%.1f".format(s.bmiScore)}) is creating significant mechanical overload on your intervertebral discs and lumbar joints, increasing tissue stress and the risk of degenerative change."
            else if (s.bmiPoints == 1)
                parts += "Your BMI (${"%.1f".format(s.bmiScore)}) is contributing moderate additional mechanical load to your spinal structures."
            if (s.radiculopathyScore >= 2)
                parts += "Significant radiculopathy or leg pain symptoms suggest nerve root irritation or compression, most commonly associated with disc herniation or foraminal stenosis."
            else if (s.radiculopathyScore == 1)
                parts += "Mild radiculopathy or referred leg symptoms indicate some degree of nerve root involvement warranting monitoring."
            if (s.sittingRisk == RiskTier.HIGH)
                parts += "Your prolonged daily sitting exceeds age-appropriate thresholds, significantly increasing intradiscal pressure while promoting core muscle weakening."
            if (s.sleepRisk == RiskTier.HIGH)
                parts += "Poor or insufficient sleep is impairing your musculoskeletal recovery and elevating inflammatory markers, amplifying your pain perception."
            if (s.chronicityPoints >= 1)
                parts += "The chronic duration of your symptoms (>6 weeks) suggests established tissue changes or central sensitisation, which typically benefits from structured rehabilitation."
            if (s.odiPoints >= 2)
                parts += "Severe functional limitation across multiple daily activities was identified, indicating substantial disability and impact on your quality of life."
            return if (parts.isEmpty())
                "No dominant risk contributors were identified. Your overall risk profile appears low. Continuing healthy lifestyle habits and periodic reassessment is recommended."
            else parts.take(3).joinToString(" ")
        }

        private fun drawSection12() {
            sHead("Section 12 — Recommended Next Steps")
            val steps = when (inp.scores.backPainRiskClassification) {
                BackPainRiskClassification.LOW, BackPainRiskClassification.LOW_MODERATE ->
                    "Maintain healthy habits. Self-monitor your symptoms. Consider a postural assessment to optimise your ergonomics. Reassess if symptoms worsen."
                BackPainRiskClassification.MILD_MODERATE, BackPainRiskClassification.MODERATE ->
                    "Consider talking to a physiotherapist. Focus on the lifestyle improvements identified in this report. Reassess in 4–6 weeks."
                BackPainRiskClassification.MODERATE_HIGH, BackPainRiskClassification.HIGH ->
                    "We recommend speaking with a doctor or spine specialist about your results. Review all lifestyle risk factors identified above."
                BackPainRiskClassification.SEVERE_URGENT ->
                    "Please seek urgent medical attention. Share this report with a doctor immediately. Do not delay."
            }
            wrapped(steps)
            gap(8f)
            divider()
            gap(4f)
            wrapped(
                "DISCLAIMER: This report is generated by a self-assessment screening tool and does not constitute medical advice. It is not a substitute for consultation with a qualified healthcare professional. If you are concerned about your symptoms, please consult a doctor. SSS v1.0 — SpineIQ.",
                pSmall
            )
        }

        private fun sHead(title: String) {
            checkBreak(22f)
            val c = cv!!
            c.drawRect(M, y, PW - M, y + 16f, pBlueBg)
            c.drawText(title, M + 4f, y + 11f, pSection)
            y += 20f
        }

        private fun tRow(label: String, value: String) {
            checkBreak(13f)
            val c = cv!!
            val col = M + 135f
            val vw = CW - 135f
            c.drawText(label, M, y, pBold)
            if (pBody.measureText(value) <= vw) {
                c.drawText(value, col, y, pBody)
                y += pBody.textSize * 1.4f + 1f
            } else {
                y += pBody.textSize * 1.4f
                wrapped(value, pBody, col, vw)
                y += 1f
            }
        }

        private fun wRow(label: String, value: String) {
            checkBreak(13f)
            val c = cv!!
            c.drawText(label, M, y, pBold)
            y += pBold.textSize * 1.4f
            wrapped(value, pBody, M + 8f, CW - 8f)
            y += 2f
        }

        private fun tHead(c1: String, c2: String, c3: String) {
            checkBreak(14f)
            val c = cv!!
            val col2 = M + CW * 0.5f
            val col3 = M + CW * 0.8f
            c.drawRect(M, y - 9f, PW - M, y + 4f, pBlueBg)
            c.drawText(c1, M + 2f, y, pSection)
            c.drawText(c2, col2, y, pSection)
            if (c3.isNotEmpty()) c.drawText(c3, col3, y, pSection)
            y += 14f
        }

        private fun tData(c1: String, c2: String, c3: String) {
            checkBreak(12f)
            val c = cv!!
            val col2 = M + CW * 0.5f
            val col3 = M + CW * 0.8f
            c.drawText(c1, M + 2f, y, pBody)
            c.drawText(c2, col2, y, pBody)
            if (c3.isNotEmpty()) c.drawText(c3, col3, y, pBody)
            y += 13f
        }

        private fun wrapped(text: String, paint: Paint = pBody, x: Float = M, maxW: Float = CW) {
            val lineH = paint.textSize * 1.4f
            val words = text.split(" ")
            val sb = StringBuilder()
            for (word in words) {
                val candidate = if (sb.isEmpty()) word else "$sb $word"
                if (paint.measureText(candidate) <= maxW) {
                    sb.clear(); sb.append(candidate)
                } else {
                    if (sb.isNotEmpty()) { checkBreak(lineH); cv!!.drawText(sb.toString(), x, y, paint); y += lineH }
                    sb.clear(); sb.append(word)
                }
            }
            if (sb.isNotEmpty()) { checkBreak(lineH); cv!!.drawText(sb.toString(), x, y, paint); y += lineH }
        }

        private fun divider() { cv!!.drawLine(M, y, PW - M, y, pDivider); y += 4f }
        private fun gap(pts: Float) { y += pts }
        private fun text(t: String, x: Float, ty: Float, p: Paint) { cv!!.drawText(t, x, ty, p) }
        private fun small(t: String, x: Float, ty: Float) { cv!!.drawText(t, x, ty, pSmall) }

        private fun body(size: Float) = Paint().apply { isAntiAlias = true; color = Color.BLACK; textSize = size }
        private fun bold(size: Float) = Paint().apply { isAntiAlias = true; color = Color.BLACK; textSize = size; typeface = Typeface.DEFAULT_BOLD }
        private fun fill(c: Int) = Paint().apply { color = c; style = Paint.Style.FILL }
    }
}
