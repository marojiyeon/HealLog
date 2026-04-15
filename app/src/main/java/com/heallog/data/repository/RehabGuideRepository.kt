package com.heallog.data.repository

import com.heallog.model.RehabExercise
import com.heallog.model.RehabExercise.Difficulty
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RehabGuideRepository @Inject constructor() {

    fun getExercisesForBodyPart(bodyPartId: String): List<RehabExercise> {
        val group = bodyPartGroup(bodyPartId)
        return exercises.filter { group in it.targetBodyParts }
    }

    private fun bodyPartGroup(id: String): String = when {
        id.endsWith("_knee") -> "knee"
        id.endsWith("_ankle") || id.endsWith("_foot") -> "ankle"
        id.endsWith("_shoulder") -> "shoulder"
        id == "lower_back" -> "lower_back"
        id == "upper_back" -> "upper_back"
        id.endsWith("_hip") -> "hip"
        id.endsWith("_elbow") -> "elbow"
        id.endsWith("_wrist") -> "wrist"
        id == "neck" -> "neck"
        id == "head" -> "neck"
        id == "chest" -> "chest"
        else -> id
    }

    private val exercises: List<RehabExercise> = listOf(

        // ── Knee ────────────────────────────────────────────────────────────────
        RehabExercise(
            id = "knee_quad_set",
            name = "대퇴사두근 수축 운동",
            description = "무릎을 구부리지 않고 허벅지 앞 근육을 강화하는 기초 운동입니다.",
            steps = listOf(
                "바닥에 다리를 펴고 앉습니다.",
                "무릎 뒤쪽을 바닥에 밀어 대퇴사두근을 수축합니다.",
                "10초간 유지 후 천천히 이완합니다."
            ),
            durationMin = 5,
            reps = "10회 × 3세트",
            difficulty = Difficulty.EASY,
            targetBodyParts = listOf("knee")
        ),
        RehabExercise(
            id = "knee_straight_leg_raise",
            name = "다리 들기",
            description = "허벅지 근육을 강화하여 무릎 안정성을 높이는 운동입니다.",
            steps = listOf(
                "바닥에 누운 뒤 한 쪽 무릎을 구부립니다.",
                "다른 쪽 다리를 45도 높이까지 천천히 들어올립니다.",
                "3초 유지 후 내립니다."
            ),
            durationMin = 8,
            reps = "15회 × 3세트",
            difficulty = Difficulty.EASY,
            targetBodyParts = listOf("knee")
        ),
        RehabExercise(
            id = "knee_terminal_extension",
            name = "터미널 무릎 신전",
            description = "밴드를 이용해 무릎 완전 신전 능력을 회복하는 운동입니다.",
            steps = listOf(
                "저항 밴드를 무릎 뒤에 두르고 벽에 고정합니다.",
                "무릎을 살짝 구부린 자세에서 천천히 완전히 폅니다.",
                "2초 유지 후 원래 자세로 돌아옵니다."
            ),
            durationMin = 10,
            reps = "12회 × 3세트",
            difficulty = Difficulty.MEDIUM,
            targetBodyParts = listOf("knee")
        ),

        // ── Ankle / Foot ────────────────────────────────────────────────────────
        RehabExercise(
            id = "ankle_alphabet",
            name = "발목 알파벳 운동",
            description = "발목 가동 범위를 회복하는 가벼운 운동입니다.",
            steps = listOf(
                "발을 들어 공중에 뜨게 합니다.",
                "발목으로 알파벳 A부터 Z까지 천천히 그립니다.",
                "반대 방향으로 반복합니다."
            ),
            durationMin = 5,
            reps = "알파벳 1회 × 3세트",
            difficulty = Difficulty.EASY,
            targetBodyParts = listOf("ankle")
        ),
        RehabExercise(
            id = "ankle_calf_raise",
            name = "종아리 들기",
            description = "종아리 근육과 발목 안정근을 강화하는 운동입니다.",
            steps = listOf(
                "벽이나 의자를 가볍게 짚고 섭니다.",
                "발뒤꿈치를 천천히 들어올립니다.",
                "최대 높이에서 2초 유지 후 내립니다."
            ),
            durationMin = 8,
            reps = "20회 × 3세트",
            difficulty = Difficulty.EASY,
            targetBodyParts = listOf("ankle")
        ),
        RehabExercise(
            id = "ankle_balance",
            name = "한 발 균형 서기",
            description = "발목 고유감각과 균형을 회복하는 운동입니다.",
            steps = listOf(
                "맨발로 서서 부상 발에 체중을 싣습니다.",
                "균형을 유지하며 30초간 서 있습니다.",
                "눈을 감으면 난이도가 올라갑니다."
            ),
            durationMin = 5,
            reps = "30초 × 3세트",
            difficulty = Difficulty.MEDIUM,
            targetBodyParts = listOf("ankle")
        ),

        // ── Shoulder ────────────────────────────────────────────────────────────
        RehabExercise(
            id = "shoulder_pendulum",
            name = "팔 진자 운동",
            description = "어깨 관절에 가해지는 부하 없이 가동 범위를 회복하는 운동입니다.",
            steps = listOf(
                "테이블에 반대쪽 손을 짚고 앞으로 숙입니다.",
                "부상 팔을 힘을 빼고 늘어뜨립니다.",
                "몸통 움직임을 이용해 팔을 원형으로 천천히 돌립니다."
            ),
            durationMin = 5,
            reps = "각 방향 10회 × 3세트",
            difficulty = Difficulty.EASY,
            targetBodyParts = listOf("shoulder")
        ),
        RehabExercise(
            id = "shoulder_external_rotation",
            name = "어깨 외회전 운동",
            description = "회전근개를 강화해 어깨 안정성을 높이는 운동입니다.",
            steps = listOf(
                "팔꿈치를 90도 구부리고 옆구리에 붙입니다.",
                "저항 밴드를 손에 잡고 팔을 바깥쪽으로 돌립니다.",
                "팔꿈치는 몸통에 붙인 상태를 유지합니다."
            ),
            durationMin = 8,
            reps = "15회 × 3세트",
            difficulty = Difficulty.MEDIUM,
            targetBodyParts = listOf("shoulder")
        ),
        RehabExercise(
            id = "shoulder_wall_slide",
            name = "벽 슬라이드",
            description = "어깨 안정화근을 활성화하는 운동입니다.",
            steps = listOf(
                "벽에 등을 대고 섭니다.",
                "팔꿈치를 90도로 구부려 벽에 붙입니다.",
                "팔을 머리 위로 천천히 밀어올리며 벽과 접촉을 유지합니다."
            ),
            durationMin = 6,
            reps = "10회 × 3세트",
            difficulty = Difficulty.MEDIUM,
            targetBodyParts = listOf("shoulder")
        ),

        // ── Lower Back ──────────────────────────────────────────────────────────
        RehabExercise(
            id = "lower_back_cat_cow",
            name = "고양이-소 스트레칭",
            description = "허리 유연성을 높이고 척추 주변 근육을 풀어주는 운동입니다.",
            steps = listOf(
                "네발기기 자세를 취합니다.",
                "숨을 내쉬며 등을 둥글게 만듭니다 (고양이).",
                "숨을 들이쉬며 허리를 아래로 내리고 고개를 듭니다 (소)."
            ),
            durationMin = 5,
            reps = "10회 × 3세트",
            difficulty = Difficulty.EASY,
            targetBodyParts = listOf("lower_back")
        ),
        RehabExercise(
            id = "lower_back_bird_dog",
            name = "버드 독",
            description = "코어와 허리 안정화근을 함께 강화하는 운동입니다.",
            steps = listOf(
                "네발기기 자세를 취합니다.",
                "오른팔과 왼쪽 다리를 동시에 뻗어 수평을 유지합니다.",
                "3초 유지 후 내리고 반대편으로 반복합니다."
            ),
            durationMin = 10,
            reps = "양쪽 각 10회 × 3세트",
            difficulty = Difficulty.MEDIUM,
            targetBodyParts = listOf("lower_back")
        ),
        RehabExercise(
            id = "lower_back_bridge",
            name = "힙 브릿지",
            description = "엉덩이와 허리 근육을 강화하는 운동입니다.",
            steps = listOf(
                "누워서 무릎을 90도로 구부립니다.",
                "발을 바닥에 대고 엉덩이를 천천히 들어올립니다.",
                "어깨-엉덩이-무릎이 일직선이 되면 3초 유지합니다."
            ),
            durationMin = 8,
            reps = "15회 × 3세트",
            difficulty = Difficulty.EASY,
            targetBodyParts = listOf("lower_back", "hip")
        ),

        // ── Upper Back ──────────────────────────────────────────────────────────
        RehabExercise(
            id = "upper_back_chin_tuck",
            name = "턱 당기기",
            description = "상부 척추 정렬을 교정하는 운동입니다.",
            steps = listOf(
                "바로 앉거나 서서 눈을 정면에 고정합니다.",
                "턱을 천천히 당겨 이중턱 자세를 만듭니다.",
                "5초 유지 후 이완합니다."
            ),
            durationMin = 3,
            reps = "10회 × 3세트",
            difficulty = Difficulty.EASY,
            targetBodyParts = listOf("upper_back", "neck")
        ),
        RehabExercise(
            id = "upper_back_row",
            name = "밴드 로우",
            description = "등 위쪽 근육을 강화하는 운동입니다.",
            steps = listOf(
                "밴드를 앞에 고정하고 양 끝을 잡습니다.",
                "팔꿈치를 뒤로 당기며 날개뼈를 모읍니다.",
                "2초 유지 후 천천히 돌아옵니다."
            ),
            durationMin = 8,
            reps = "12회 × 3세트",
            difficulty = Difficulty.MEDIUM,
            targetBodyParts = listOf("upper_back")
        ),

        // ── Hip ─────────────────────────────────────────────────────────────────
        RehabExercise(
            id = "hip_clamshell",
            name = "클램쉘",
            description = "엉덩이 외전근을 강화하는 운동입니다.",
            steps = listOf(
                "옆으로 누워 무릎을 45도 구부립니다.",
                "발은 붙인 채 위쪽 무릎을 최대한 벌립니다.",
                "2초 유지 후 천천히 닫습니다."
            ),
            durationMin = 8,
            reps = "15회 × 3세트",
            difficulty = Difficulty.EASY,
            targetBodyParts = listOf("hip")
        ),
        RehabExercise(
            id = "hip_hip_flexor_stretch",
            name = "고관절 굴근 스트레칭",
            description = "장요근을 늘려 고관절 가동 범위를 회복합니다.",
            steps = listOf(
                "한 무릎을 바닥에 대고 런지 자세를 취합니다.",
                "몸통을 세우고 앞으로 천천히 이동합니다.",
                "고관절 앞쪽에 스트레칭 느낌이 들면 30초 유지합니다."
            ),
            durationMin = 5,
            reps = "양쪽 각 30초 × 3세트",
            difficulty = Difficulty.EASY,
            targetBodyParts = listOf("hip", "lower_back")
        ),

        // ── Elbow ────────────────────────────────────────────────────────────────
        RehabExercise(
            id = "elbow_wrist_curl",
            name = "손목 컬",
            description = "전완 굴근을 강화하는 운동입니다.",
            steps = listOf(
                "팔꿈치를 무릎에 올리고 손바닥이 위를 향하게 합니다.",
                "가벼운 덤벨 또는 물병을 쥐고 손목을 위로 구부립니다.",
                "천천히 내립니다."
            ),
            durationMin = 8,
            reps = "15회 × 3세트",
            difficulty = Difficulty.EASY,
            targetBodyParts = listOf("elbow", "wrist")
        ),
        RehabExercise(
            id = "elbow_stretch",
            name = "팔꿈치 굴신 스트레칭",
            description = "팔꿈치 가동 범위를 회복하는 스트레칭입니다.",
            steps = listOf(
                "팔을 앞으로 뻗고 손바닥이 위를 향하게 합니다.",
                "반대 손으로 손가락을 가볍게 아래로 당깁니다.",
                "30초 유지 후 반대 방향으로 반복합니다."
            ),
            durationMin = 5,
            reps = "양방향 30초 × 3세트",
            difficulty = Difficulty.EASY,
            targetBodyParts = listOf("elbow")
        ),

        // ── Wrist ────────────────────────────────────────────────────────────────
        RehabExercise(
            id = "wrist_extension_stretch",
            name = "손목 신전 스트레칭",
            description = "손목 신전근의 유연성을 회복합니다.",
            steps = listOf(
                "팔을 앞으로 뻗습니다.",
                "반대 손으로 손등을 잡고 손가락 방향으로 가볍게 당깁니다.",
                "20초 유지합니다."
            ),
            durationMin = 5,
            reps = "20초 × 3세트",
            difficulty = Difficulty.EASY,
            targetBodyParts = listOf("wrist")
        ),
        RehabExercise(
            id = "wrist_circle",
            name = "손목 돌리기",
            description = "손목 관절 가동 범위 전체를 풀어주는 운동입니다.",
            steps = listOf(
                "팔꿈치를 고정하고 손목만 움직입니다.",
                "시계 방향으로 천천히 10회 돌립니다.",
                "반시계 방향으로 반복합니다."
            ),
            durationMin = 3,
            reps = "각 방향 10회 × 3세트",
            difficulty = Difficulty.EASY,
            targetBodyParts = listOf("wrist", "elbow")
        ),

        // ── Neck ─────────────────────────────────────────────────────────────────
        RehabExercise(
            id = "neck_stretch",
            name = "목 측방 스트레칭",
            description = "목 옆 근육의 긴장을 완화하는 스트레칭입니다.",
            steps = listOf(
                "바로 앉아 한 손을 귀 위에 살짝 올립니다.",
                "천천히 귀를 어깨 방향으로 기울입니다.",
                "30초 유지 후 반대편 반복합니다."
            ),
            durationMin = 5,
            reps = "양쪽 30초 × 3세트",
            difficulty = Difficulty.EASY,
            targetBodyParts = listOf("neck")
        ),
        RehabExercise(
            id = "neck_isometric",
            name = "목 등척성 운동",
            description = "경추 안정화근을 강화하는 운동입니다.",
            steps = listOf(
                "손을 이마에 대고 앞으로 밀려는 힘에 저항합니다.",
                "목은 움직이지 않고 5초간 힘을 유지합니다.",
                "옆, 뒤 방향도 반복합니다."
            ),
            durationMin = 6,
            reps = "각 방향 5초 × 5세트",
            difficulty = Difficulty.MEDIUM,
            targetBodyParts = listOf("neck")
        ),

        // ── Chest ────────────────────────────────────────────────────────────────
        RehabExercise(
            id = "chest_doorway_stretch",
            name = "문틀 가슴 스트레칭",
            description = "대흉근을 늘려 자세와 호흡 기능을 개선합니다.",
            steps = listOf(
                "문틀에 서서 팔꿈치를 90도로 구부려 문틀에 댑니다.",
                "몸통을 문 쪽으로 천천히 기울입니다.",
                "가슴이 당기는 느낌에서 30초 유지합니다."
            ),
            durationMin = 5,
            reps = "30초 × 3세트",
            difficulty = Difficulty.EASY,
            targetBodyParts = listOf("chest")
        )
    )
}
