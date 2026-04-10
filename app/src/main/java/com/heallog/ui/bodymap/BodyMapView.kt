package com.heallog.ui.bodymap

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.heallog.model.BodyPart
import com.heallog.model.BodyPartDef
import com.heallog.model.BodyPartPosition
import com.heallog.model.BodyParts
import com.heallog.ui.theme.HealLogTheme

// ---------------------------------------------------------------------------
// Geometry constants (all in normalised 0..1 space; scaled at draw time)
// ---------------------------------------------------------------------------
private const val HIT_RADIUS_NORM = 0.055f   // hit circle radius as fraction of canvas width
private const val INDICATOR_RADIUS_NORM = 0.022f

/**
 * A tappable body-map silhouette drawn entirely with Canvas primitives.
 *
 * @param activeInjuryParts  Set of body-part IDs that currently have active injuries.
 * @param selectedPartId     Currently highlighted body-part ID, or null.
 * @param isFrontView        If true renders front-view hit regions; back-view otherwise.
 * @param onBodyPartSelected Called with the tapped [BodyPart].
 */
@Composable
fun BodyMapView(
    activeInjuryParts: Set<String>,
    selectedPartId: String?,
    isFrontView: Boolean,
    onBodyPartSelected: (BodyPart) -> Unit,
    modifier: Modifier = Modifier
) {
    // Capture Material3 colors before entering Canvas (no CompositionLocal inside DrawScope)
    val colorSilhouette      = MaterialTheme.colorScheme.surfaceVariant
    val colorStroke          = MaterialTheme.colorScheme.outline
    val colorHitRegion       = MaterialTheme.colorScheme.primary.copy(alpha = 0.13f)
    val colorSelected        = MaterialTheme.colorScheme.primary
    val colorSelectedRing    = MaterialTheme.colorScheme.primary.copy(alpha = 0.27f)
    val colorInjuryDot       = MaterialTheme.colorScheme.error
    val colorInjuryDotBorder = MaterialTheme.colorScheme.surface

    // Pre-filter to only the parts visible on the current face.
    val visibleDefs = remember(isFrontView) {
        BodyParts.definitions.filter { def ->
            if (isFrontView) def.frontPosition != null else def.backPosition != null
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(isFrontView) {
                detectTapGestures { tapOffset ->
                    val w = size.width.toFloat()
                    val h = size.height.toFloat()
                    val hitRadiusPx = HIT_RADIUS_NORM * w

                    visibleDefs.forEach { def ->
                        val pos = if (isFrontView) def.frontPosition else def.backPosition
                        pos ?: return@forEach
                        val cx = pos.x * w
                        val cy = pos.y * h
                        val dist = tapOffset.distanceTo(Offset(cx, cy))
                        if (dist <= hitRadiusPx) {
                            onBodyPartSelected(def.part)
                            return@detectTapGestures
                        }
                    }
                }
            }
    ) {
        val w = size.width
        val h = size.height

        drawSilhouette(w, h, colorSilhouette, colorStroke)

        visibleDefs.forEach { def ->
            val pos = (if (isFrontView) def.frontPosition else def.backPosition) ?: return@forEach
            val isSelected = def.part.id == selectedPartId
            val hasInjury = def.part.id in activeInjuryParts
            drawBodyPartRegion(
                pos, w, h, isSelected, hasInjury,
                colorHitRegion, colorSelected, colorSelectedRing,
                colorInjuryDot, colorInjuryDotBorder
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Silhouette drawing — geometric primitives only, no assets needed
// ---------------------------------------------------------------------------

private fun DrawScope.drawSilhouette(
    w: Float, h: Float,
    colorSilhouette: Color, colorStroke: Color
) {
    val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)

    // --- Head ---
    val headCx = w * 0.50f
    val headCy = h * 0.055f
    val headR  = w * 0.085f
    drawCircle(colorSilhouette, headR, Offset(headCx, headCy))
    drawCircle(colorStroke, headR, Offset(headCx, headCy), style = stroke)

    // --- Neck ---
    val neckHalfW = w * 0.04f
    drawRect(
        color = colorSilhouette,
        topLeft = Offset(headCx - neckHalfW, headCy + headR - 2.dp.toPx()),
        size = Size(neckHalfW * 2, h * 0.06f)
    )

    // --- Torso ---
    val torsoTop    = headCy + headR + h * 0.04f
    val torsoBottom = h * 0.50f
    val torsoHalfW  = w * 0.175f
    val torsoPath = Path().apply {
        // shoulders wider, waist narrower, hips wider
        moveTo(headCx - torsoHalfW * 1.35f, torsoTop)           // left shoulder
        lineTo(headCx + torsoHalfW * 1.35f, torsoTop)           // right shoulder
        lineTo(headCx + torsoHalfW * 1.0f,  torsoTop + h * 0.14f) // right armpit
        lineTo(headCx + torsoHalfW * 0.85f, torsoBottom - h * 0.06f) // right waist
        lineTo(headCx + torsoHalfW * 1.1f,  torsoBottom)        // right hip
        lineTo(headCx - torsoHalfW * 1.1f,  torsoBottom)        // left hip
        lineTo(headCx - torsoHalfW * 0.85f, torsoBottom - h * 0.06f) // left waist
        lineTo(headCx - torsoHalfW * 1.0f,  torsoTop + h * 0.14f) // left armpit
        close()
    }
    drawPath(torsoPath, colorSilhouette)
    drawPath(torsoPath, colorStroke, style = stroke)

    // --- Left arm ---
    drawArm(
        shoulder = Offset(headCx - torsoHalfW * 1.35f, torsoTop),
        elbow    = Offset(w * 0.18f, h * 0.34f),
        wrist    = Offset(w * 0.14f, h * 0.44f),
        hand     = Offset(w * 0.12f, h * 0.50f),
        w = w, stroke = stroke,
        colorSilhouette = colorSilhouette, colorStroke = colorStroke
    )

    // --- Right arm ---
    drawArm(
        shoulder = Offset(headCx + torsoHalfW * 1.35f, torsoTop),
        elbow    = Offset(w * 0.82f, h * 0.34f),
        wrist    = Offset(w * 0.86f, h * 0.44f),
        hand     = Offset(w * 0.88f, h * 0.50f),
        w = w, stroke = stroke,
        colorSilhouette = colorSilhouette, colorStroke = colorStroke
    )

    // --- Left leg ---
    drawLeg(
        hip   = Offset(headCx - torsoHalfW * 0.65f, torsoBottom),
        knee  = Offset(w * 0.375f, h * 0.70f),
        ankle = Offset(w * 0.37f,  h * 0.85f),
        foot  = Offset(w * 0.34f,  h * 0.93f),
        w = w, stroke = stroke,
        colorSilhouette = colorSilhouette, colorStroke = colorStroke
    )

    // --- Right leg ---
    drawLeg(
        hip   = Offset(headCx + torsoHalfW * 0.65f, torsoBottom),
        knee  = Offset(w * 0.625f, h * 0.70f),
        ankle = Offset(w * 0.63f,  h * 0.85f),
        foot  = Offset(w * 0.66f,  h * 0.93f),
        w = w, stroke = stroke,
        colorSilhouette = colorSilhouette, colorStroke = colorStroke
    )
}

private fun DrawScope.drawArm(
    shoulder: Offset, elbow: Offset, wrist: Offset, hand: Offset,
    w: Float, stroke: Stroke,
    colorSilhouette: Color, colorStroke: Color
) {
    val armHalfW = w * 0.045f
    val path = Path().apply {
        moveTo(shoulder.x - armHalfW * 0.6f, shoulder.y)
        cubicTo(
            shoulder.x - armHalfW, elbow.y * 0.5f,
            elbow.x - armHalfW,    elbow.y,
            wrist.x - armHalfW * 0.7f, wrist.y
        )
        lineTo(wrist.x + armHalfW * 0.7f, wrist.y)
        cubicTo(
            elbow.x + armHalfW,    elbow.y,
            shoulder.x + armHalfW, elbow.y * 0.5f,
            shoulder.x + armHalfW * 0.6f, shoulder.y
        )
        close()
    }
    drawPath(path, colorSilhouette)
    drawPath(path, colorStroke, style = stroke)

    // Hand
    drawCircle(colorSilhouette, w * 0.038f, hand)
    drawCircle(colorStroke, w * 0.038f, hand, style = stroke)
}

private fun DrawScope.drawLeg(
    hip: Offset, knee: Offset, ankle: Offset, foot: Offset,
    w: Float, stroke: Stroke,
    colorSilhouette: Color, colorStroke: Color
) {
    val legHalfW = w * 0.058f
    val path = Path().apply {
        moveTo(hip.x - legHalfW, hip.y)
        cubicTo(
            hip.x - legHalfW,   knee.y * 0.7f,
            knee.x - legHalfW,  knee.y,
            ankle.x - legHalfW * 0.55f, ankle.y
        )
        lineTo(ankle.x + legHalfW * 0.55f, ankle.y)
        cubicTo(
            knee.x + legHalfW,  knee.y,
            hip.x + legHalfW,   knee.y * 0.7f,
            hip.x + legHalfW,   hip.y
        )
        close()
    }
    drawPath(path, colorSilhouette)
    drawPath(path, colorStroke, style = stroke)

    // Foot
    val footW = w * 0.075f
    val footH = w * 0.03f
    drawOval(
        color = colorSilhouette,
        topLeft = Offset(foot.x - footW * 0.4f, foot.y - footH * 0.5f),
        size = Size(footW, footH)
    )
    drawOval(
        color = colorStroke,
        topLeft = Offset(foot.x - footW * 0.4f, foot.y - footH * 0.5f),
        size = Size(footW, footH),
        style = stroke
    )
}

// ---------------------------------------------------------------------------
// Hit-region overlay
// ---------------------------------------------------------------------------

private fun DrawScope.drawBodyPartRegion(
    pos: BodyPartPosition,
    w: Float,
    h: Float,
    isSelected: Boolean,
    hasInjury: Boolean,
    colorHitRegion: Color,
    colorSelected: Color,
    colorSelectedRing: Color,
    colorInjuryDot: Color,
    colorInjuryDotBorder: Color
) {
    val cx = pos.x * w
    val cy = pos.y * h
    val hitR = HIT_RADIUS_NORM * w

    if (isSelected) {
        // Outer ring
        drawCircle(colorSelectedRing, hitR * 1.5f, Offset(cx, cy))
        // Filled circle
        drawCircle(colorSelected.copy(alpha = 0.35f), hitR, Offset(cx, cy))
        drawCircle(colorSelected, hitR, Offset(cx, cy), style = Stroke(2.dp.toPx()))
    } else {
        drawCircle(colorHitRegion, hitR, Offset(cx, cy))
    }

    // Injury indicator dot (top-right of hit circle)
    if (hasInjury) {
        val dotR = INDICATOR_RADIUS_NORM * w
        val dotCx = cx + hitR * 0.65f
        val dotCy = cy - hitR * 0.65f
        drawCircle(colorInjuryDotBorder, dotR + 1.5.dp.toPx(), Offset(dotCx, dotCy))
        drawCircle(colorInjuryDot, dotR, Offset(dotCx, dotCy))
    }
}

// ---------------------------------------------------------------------------
// Extensions
// ---------------------------------------------------------------------------

private fun Offset.distanceTo(other: Offset): Float {
    val dx = x - other.x
    val dy = y - other.y
    return kotlin.math.sqrt(dx * dx + dy * dy)
}

// ---------------------------------------------------------------------------
// Preview
// ---------------------------------------------------------------------------

@Preview(showBackground = true, widthDp = 320, heightDp = 600)
@Composable
private fun BodyMapViewPreview() {
    HealLogTheme {
        BodyMapView(
            activeInjuryParts = setOf("left_knee", "right_shoulder"),
            selectedPartId = "left_knee",
            isFrontView = true,
            onBodyPartSelected = {},
            modifier = Modifier.height(560.dp)
        )
    }
}
