-- ============================================================
-- Smart Gym Management and Membership Tracking System
-- MySQL Initial Database Creation Script (Reference DDL)
-- ============================================================

CREATE DATABASE IF NOT EXISTS smart_gym_management
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE smart_gym_management;

-- Note: Spring Data JPA with `spring.jpa.hibernate.ddl-auto=update`
-- will automatically create all tables, indexes, and foreign keys.
-- The following DDL statements are provided for reference or manual execution.

-- 1. users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_email (email),
    INDEX idx_user_role (role)
);

-- 2. trainer_profiles table
CREATE TABLE IF NOT EXISTS trainer_profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    specialization VARCHAR(150) NOT NULL,
    qualification VARCHAR(150) NOT NULL,
    experience_years INT NOT NULL,
    bio TEXT,
    hourly_rate DECIMAL(10, 2) DEFAULT 0.00,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 3. membership_plans table
CREATE TABLE IF NOT EXISTS membership_plans (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    duration_days INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    facilities TEXT,
    includes_personal_training BOOLEAN NOT NULL DEFAULT FALSE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 4. memberships table
CREATE TABLE IF NOT EXISTS memberships (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    membership_plan_id BIGINT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES users(id) ON DELETE RESTRICT,
    FOREIGN KEY (membership_plan_id) REFERENCES membership_plans(id) ON DELETE RESTRICT,
    INDEX idx_membership_member (member_id),
    INDEX idx_membership_status (status)
);

-- 5. payments table
CREATE TABLE IF NOT EXISTS payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    membership_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_method VARCHAR(30) NOT NULL,
    transaction_reference VARCHAR(100) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    verified_by_user_id BIGINT NULL,
    verified_at TIMESTAMP NULL,
    FOREIGN KEY (membership_id) REFERENCES memberships(id) ON DELETE RESTRICT,
    FOREIGN KEY (verified_by_user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_payment_status (status)
);

-- 6. attendance table
CREATE TABLE IF NOT EXISTS attendance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    attendance_date DATE NOT NULL,
    check_in_time TIME NOT NULL,
    check_out_time TIME NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PRESENT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_member_date (member_id, attendance_date),
    FOREIGN KEY (member_id) REFERENCES users(id) ON DELETE RESTRICT,
    INDEX idx_attendance_date (attendance_date)
);

-- 7. trainer_assignments table
CREATE TABLE IF NOT EXISTS trainer_assignments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    trainer_profile_id BIGINT NOT NULL,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    notes TEXT,
    FOREIGN KEY (member_id) REFERENCES users(id) ON DELETE RESTRICT,
    FOREIGN KEY (trainer_profile_id) REFERENCES trainer_profiles(id) ON DELETE RESTRICT,
    INDEX idx_assign_member (member_id),
    INDEX idx_assign_trainer (trainer_profile_id)
);

-- 8. workout_plans table
CREATE TABLE IF NOT EXISTS workout_plans (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    trainer_profile_id BIGINT NOT NULL,
    plan_name VARCHAR(150) NOT NULL,
    description TEXT,
    start_date DATE NOT NULL,
    end_date DATE NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES users(id) ON DELETE RESTRICT,
    FOREIGN KEY (trainer_profile_id) REFERENCES trainer_profiles(id) ON DELETE RESTRICT
);

-- 9. workout_exercises table
CREATE TABLE IF NOT EXISTS workout_exercises (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    workout_plan_id BIGINT NOT NULL,
    exercise_name VARCHAR(100) NOT NULL,
    target_muscle_group VARCHAR(100) NOT NULL,
    sets INT NOT NULL,
    repetitions INT NOT NULL,
    rest_seconds INT NOT NULL,
    instructions TEXT,
    target_weight DECIMAL(6, 2) NULL,
    exercise_order INT NOT NULL DEFAULT 1,
    FOREIGN KEY (workout_plan_id) REFERENCES workout_plans(id) ON DELETE CASCADE
);

-- 10. fitness_progress table
CREATE TABLE IF NOT EXISTS fitness_progress (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    measurement_date DATE NOT NULL,
    weight_kg DECIMAL(5, 2) NOT NULL,
    body_fat_percentage DECIMAL(4, 2) NULL,
    waist_cm DECIMAL(5, 2) NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES users(id) ON DELETE RESTRICT,
    INDEX idx_progress_member_date (member_id, measurement_date)
);

-- 11. trainer_availabilities table
CREATE TABLE IF NOT EXISTS trainer_availabilities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    trainer_profile_id BIGINT NOT NULL,
    availability_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    available BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (trainer_profile_id) REFERENCES trainer_profiles(id) ON DELETE CASCADE,
    INDEX idx_avail_trainer_date (trainer_profile_id, availability_date)
);

-- 12. training_sessions table
CREATE TABLE IF NOT EXISTS training_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    trainer_profile_id BIGINT NOT NULL,
    availability_id BIGINT NULL,
    session_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    purpose VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    cancellation_reason VARCHAR(255) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES users(id) ON DELETE RESTRICT,
    FOREIGN KEY (trainer_profile_id) REFERENCES trainer_profiles(id) ON DELETE RESTRICT,
    FOREIGN KEY (availability_id) REFERENCES trainer_availabilities(id) ON DELETE SET NULL,
    INDEX idx_session_date (session_date),
    INDEX idx_session_status (status)
);
