package com.heallog.model

/**
 * Canonical list of body parts with normalized hit-region positions.
 *
 * [frontPosition] / [backPosition]: (x, y) in the range 0f..1f relative to the
 * bounding box of the silhouette canvas. Used by BodyMapView to place hit circles
 * and injury indicators without hard-coding pixel values.
 *
 * x=0 → left edge of canvas, x=1 → right edge (body's anatomical RIGHT is on the
 * viewer's left, matching standard anatomical front/back illustrations).
 * y=0 → top, y=1 → bottom.
 */
data class BodyPartPosition(
    val x: Float,
    val y: Float
)

data class BodyPartDef(
    val part: BodyPart,
    val frontPosition: BodyPartPosition?,
    val backPosition: BodyPartPosition?
)

object BodyParts {

    val HEAD = BodyPart("head", "머리", "Head")
    val NECK = BodyPart("neck", "목", "Neck")
    val LEFT_SHOULDER = BodyPart("left_shoulder", "왼쪽 어깨", "Left Shoulder")
    val RIGHT_SHOULDER = BodyPart("right_shoulder", "오른쪽 어깨", "Right Shoulder")
    val LEFT_UPPER_ARM = BodyPart("left_upper_arm", "왼쪽 위팔", "Left Upper Arm")
    val RIGHT_UPPER_ARM = BodyPart("right_upper_arm", "오른쪽 위팔", "Right Upper Arm")
    val LEFT_ELBOW = BodyPart("left_elbow", "왼쪽 팔꿈치", "Left Elbow")
    val RIGHT_ELBOW = BodyPart("right_elbow", "오른쪽 팔꿈치", "Right Elbow")
    val LEFT_WRIST = BodyPart("left_wrist", "왼쪽 손목", "Left Wrist")
    val RIGHT_WRIST = BodyPart("right_wrist", "오른쪽 손목", "Right Wrist")
    val CHEST = BodyPart("chest", "가슴", "Chest")
    val UPPER_BACK = BodyPart("upper_back", "등 위", "Upper Back")
    val LOWER_BACK = BodyPart("lower_back", "허리", "Lower Back")
    val LEFT_HIP = BodyPart("left_hip", "왼쪽 엉덩이", "Left Hip")
    val RIGHT_HIP = BodyPart("right_hip", "오른쪽 엉덩이", "Right Hip")
    val LEFT_KNEE = BodyPart("left_knee", "왼쪽 무릎", "Left Knee")
    val RIGHT_KNEE = BodyPart("right_knee", "오른쪽 무릎", "Right Knee")
    val LEFT_ANKLE = BodyPart("left_ankle", "왼쪽 발목", "Left Ankle")
    val RIGHT_ANKLE = BodyPart("right_ankle", "오른쪽 발목", "Right Ankle")
    val LEFT_FOOT = BodyPart("left_foot", "왼쪽 발", "Left Foot")
    val RIGHT_FOOT = BodyPart("right_foot", "오른쪽 발", "Right Foot")

    // -------------------------------------------------------------------------
    // Normalized positions (0f..1f) within the silhouette bounding box.
    // Front view: viewer's left = body's right (anatomical convention).
    // Back positions mirror x for symmetric parts; non-symmetric parts
    // (chest → upper_back) swap to their dorsal equivalent.
    // -------------------------------------------------------------------------
    val definitions: List<BodyPartDef> = listOf(
        BodyPartDef(HEAD,          front(0.50f, 0.04f), back(0.50f, 0.04f)),
        BodyPartDef(NECK,          front(0.50f, 0.11f), back(0.50f, 0.11f)),
        BodyPartDef(LEFT_SHOULDER, front(0.30f, 0.17f), back(0.30f, 0.17f)),
        BodyPartDef(RIGHT_SHOULDER,front(0.70f, 0.17f), back(0.70f, 0.17f)),
        BodyPartDef(LEFT_UPPER_ARM,front(0.24f, 0.25f), back(0.24f, 0.25f)),
        BodyPartDef(RIGHT_UPPER_ARM,front(0.76f,0.25f), back(0.76f, 0.25f)),
        BodyPartDef(LEFT_ELBOW,    front(0.20f, 0.33f), back(0.20f, 0.33f)),
        BodyPartDef(RIGHT_ELBOW,   front(0.80f, 0.33f), back(0.80f, 0.33f)),
        BodyPartDef(LEFT_WRIST,    front(0.17f, 0.41f), back(0.17f, 0.41f)),
        BodyPartDef(RIGHT_WRIST,   front(0.83f, 0.41f), back(0.83f, 0.41f)),
        BodyPartDef(CHEST,         front(0.50f, 0.24f), backNull()),
        BodyPartDef(UPPER_BACK,    frontNull(),          back(0.50f, 0.24f)),
        BodyPartDef(LOWER_BACK,    frontNull(),          back(0.50f, 0.35f)),
        BodyPartDef(LEFT_HIP,      front(0.38f, 0.52f), back(0.38f, 0.52f)),
        BodyPartDef(RIGHT_HIP,     front(0.62f, 0.52f), back(0.62f, 0.52f)),
        BodyPartDef(LEFT_KNEE,     front(0.38f, 0.69f), back(0.38f, 0.69f)),
        BodyPartDef(RIGHT_KNEE,    front(0.62f, 0.69f), back(0.62f, 0.69f)),
        BodyPartDef(LEFT_ANKLE,    front(0.38f, 0.84f), back(0.38f, 0.84f)),
        BodyPartDef(RIGHT_ANKLE,   front(0.62f, 0.84f), back(0.62f, 0.84f)),
        BodyPartDef(LEFT_FOOT,     front(0.37f, 0.92f), back(0.37f, 0.92f)),
        BodyPartDef(RIGHT_FOOT,    front(0.63f, 0.92f), back(0.63f, 0.92f)),
    )

    /** All BodyPart instances for convenience (e.g. search/listing). */
    val all: List<BodyPart> = definitions.map { it.part }

    fun findById(id: String): BodyPart? = all.firstOrNull { it.id == id }

    // -------------------------------------------------------------------------
    // Helpers to keep the definition table readable
    // -------------------------------------------------------------------------
    private fun front(x: Float, y: Float) = BodyPartPosition(x, y)
    private fun back(x: Float, y: Float) = BodyPartPosition(x, y)
    private fun frontNull(): BodyPartPosition? = null
    private fun backNull(): BodyPartPosition? = null
}
