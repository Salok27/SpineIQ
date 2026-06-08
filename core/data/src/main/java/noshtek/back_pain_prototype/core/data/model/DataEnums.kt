package noshtek.back_pain_prototype.core.data.model

/** User gender — stored in UserProfile. */
enum class Gender { MALE, FEMALE, OTHER, PREFER_NOT_TO_SAY }

/** Assessment lifecycle state (Section 15.5). */
enum class AssessmentStatus { IN_PROGRESS, COMPLETED }

/** Occupation type selector (Screen 4). */
enum class OccupationType {
    OFFICE_WORKER, FIELD_WORKER, DRIVER, HOMEMAKER, STUDENT, MANUAL_LABOR, OTHER
}

/** Lifting activity level (Screen 4). */
enum class LiftingLevel { NONE, LIGHT, MODERATE, HEAVY }

/** Pain pattern (Screen 6). */
enum class PainPattern { CONSTANT, INTERMITTENT, ACTIVITY_RELATED, POSITION_RELATED }

/** Pain location text-checklist options (Screen 6, FR-05). */
enum class PainLocation {
    UPPER_BACK,
    MID_BACK,
    LOWER_BACK_LUMBAR,
    SACRAL_COCCYX,
    LEFT_HIP_BUTTOCK,
    RIGHT_HIP_BUTTOCK
}

/** Pain trigger multi-select (Screen 6). */
enum class PainTrigger {
    SITTING, STANDING, WALKING, LIFTING, BENDING,
    MORNING_STIFFNESS, REST_SLEEP, NO_CLEAR_TRIGGER
}

/** Radiation/radiculopathy leg location — conditional field (Screen 6). */
enum class RadiationLocation { LEFT_LEG, RIGHT_LEG, BOTH_LEGS }

/** Structured functional limitation severity (Screen 6). */
enum class FunctionalLimitationSeverity { NONE, MILD, MODERATE, SEVERE }

/** Whether a lifestyle field was populated from Health Connect or entered manually (Section 14.7). */
enum class DataSource { MANUAL, HEALTH_CONNECT }
