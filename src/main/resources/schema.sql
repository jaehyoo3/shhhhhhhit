-- =============================================
-- H2 Database Schema for Foorend
-- 서버 시작 시 자동 실행됩니다.
-- MariaDB DDL → H2 호환 문법으로 변환
-- =============================================

-- 1. restaurants (식당 정보)
CREATE TABLE IF NOT EXISTS restaurants (
    restaurant_id       INT AUTO_INCREMENT PRIMARY KEY,
    restaurant_name     VARCHAR(100) NOT NULL,
    restaurant_addr     VARCHAR(300) NOT NULL,
    location_area       VARCHAR(50) NOT NULL,
    avg_price_tier      VARCHAR(10) NOT NULL,                 -- LOW, MID, HIGH
    category            VARCHAR(50)
);

-- 2. users (회원 정보)
CREATE TABLE IF NOT EXISTS users (
    user_id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    google_id           VARCHAR(191) NOT NULL UNIQUE,
    email               VARCHAR(255) NOT NULL UNIQUE,
    name                VARCHAR(50) NOT NULL,
    phone_number        VARCHAR(20),
    gender              VARCHAR(10),                          -- MALE, FEMALE
    birthday            DATE,
    profile_image_url   VARCHAR(100),
    relationship_status VARCHAR(20),                          -- SINGLE, COUPLE, MARRIED
    nationality         CHAR(2) DEFAULT 'KR',
    job_title           VARCHAR(100),
    user_status         VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, SUSPENDED, WITHDRAWAL
    refresh_token       VARCHAR(1024),
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. meeting_slots (모임 일정)
CREATE TABLE IF NOT EXISTS meeting_slots (
    slot_id                     BIGINT AUTO_INCREMENT PRIMARY KEY,
    confirmed_restaurant_id     INT,
    location_area               VARCHAR(50) NOT NULL,
    meet_date                   DATE NOT NULL,
    meet_time                   TIME NOT NULL,
    max_capacity                INT,
    current_count               INT DEFAULT 0,
    status                      VARCHAR(20) DEFAULT 'OPEN',   -- OPEN, CONFIRMED, CANCELED, FINISHED
    created_at                  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_slots_rest FOREIGN KEY (confirmed_restaurant_id) REFERENCES restaurants(restaurant_id),
    CONSTRAINT uq_schedule UNIQUE (location_area, meet_date, meet_time)
);

-- 4. slot_entries (모임 소속 회원)
CREATE TABLE IF NOT EXISTS slot_entries (
    entry_id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    slot_id         BIGINT NOT NULL,
    user_id         BIGINT NOT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'JOINED',    -- JOINED, CANCELED, LATE, NOSHOW
    cancel_reason   VARCHAR(255),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT uq_slot_user UNIQUE (slot_id, user_id),
    CONSTRAINT fk_entries_slot FOREIGN KEY (slot_id) REFERENCES meeting_slots(slot_id) ON DELETE CASCADE,
    CONSTRAINT fk_entries_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 5. user_reviews (유저 피드백 리뷰)
CREATE TABLE IF NOT EXISTS user_reviews (
    user_review_id  BIGINT AUTO_INCREMENT PRIMARY KEY,
    slot_id         BIGINT NOT NULL,
    user_id         BIGINT NOT NULL,
    target_user_id  BIGINT NOT NULL,
    manner_score    TINYINT NOT NULL DEFAULT 5,
    situation_share VARCHAR(50),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT uq_ureview UNIQUE (slot_id, user_id, target_user_id),
    CONSTRAINT fk_urev_slot FOREIGN KEY (slot_id) REFERENCES meeting_slots(slot_id) ON DELETE CASCADE,
    CONSTRAINT fk_urev_from FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_urev_to FOREIGN KEY (target_user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 6. user_trait_languages (회원 선호 언어)
CREATE TABLE IF NOT EXISTS user_trait_languages (
    user_id     BIGINT NOT NULL,
    language    VARCHAR(10) NOT NULL,                         -- KO, EN, JP
    
    PRIMARY KEY (user_id, language),
    CONSTRAINT fk_lang_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 7. user_trait_prices (회원 선호 가격대)
CREATE TABLE IF NOT EXISTS user_trait_prices (
    user_id     BIGINT NOT NULL,
    price_tier  VARCHAR(10) NOT NULL,                         -- LOW, MID, HIGH
    
    PRIMARY KEY (user_id, price_tier),
    CONSTRAINT fk_price_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 8. user_traits (회원 성향)
CREATE TABLE IF NOT EXISTS user_traits (
    traits_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    answers     CLOB,                                         -- JSON 형태 답변 데이터
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_traits_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 9. withdrawal_logs (회원 탈퇴 사유 로그)
CREATE TABLE IF NOT EXISTS withdrawal_logs (
    log_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT,
    reason      VARCHAR(100) NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_withdrawal_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL
);

-- 10. meeting_reviews (모임 피드백 리뷰)
CREATE TABLE IF NOT EXISTS meeting_reviews (
    review_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    slot_id         BIGINT NOT NULL,
    user_id         BIGINT NOT NULL,
    score_food      TINYINT NOT NULL,
    score_service   TINYINT NOT NULL,
    score_ambiance  TINYINT NOT NULL,
    score_slot      TINYINT NOT NULL DEFAULT 5,
    feedback        CLOB,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT uq_review UNIQUE (slot_id, user_id),
    CONSTRAINT fk_mrev_slot FOREIGN KEY (slot_id) REFERENCES meeting_slots(slot_id) ON DELETE CASCADE,
    CONSTRAINT fk_mrev_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- =============================================
-- 인덱스
-- =============================================
CREATE INDEX IF NOT EXISTS idx_user_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_user_google_id ON users(google_id);
CREATE INDEX IF NOT EXISTS idx_my_entry ON slot_entries(user_id, status);
CREATE INDEX IF NOT EXISTS idx_slot ON slot_entries(slot_id);
CREATE INDEX IF NOT EXISTS idx_traits_user ON user_traits(user_id);
