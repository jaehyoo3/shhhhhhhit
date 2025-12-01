-- Foorend Database Schema
-- 생성일: 2025-01-20

-- 데이터베이스 생성 (필요시)
-- CREATE DATABASE IF NOT EXISTS foorend CHARACTER SET utf8mb4 COLLATE utf8mb4_uca1400_ai_ci;
-- USE foorend;

-- ============================================
-- 1. 식당 테이블
-- ============================================
CREATE TABLE IF NOT EXISTS `restaurants` (
  `restaurant_id` int(11) NOT NULL AUTO_INCREMENT,
  `restaurant_name` varchar(100) NOT NULL,
  `restaurant_addr` varchar(300) NOT NULL,
  `location_area` varchar(50) NOT NULL,
  `avg_price_tier` varchar(10) NOT NULL,
  `category` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`restaurant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- ============================================
-- 2. 사용자 테이블
-- ============================================
CREATE TABLE IF NOT EXISTS `users` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `google_id` varchar(191) NOT NULL,
  `email` varchar(255) NOT NULL,
  `name` varchar(50) NOT NULL,
  `phone_number` varchar(20) DEFAULT NULL,
  `gender` varchar(10) DEFAULT NULL,
  `birthday` date DEFAULT NULL,
  `profile_image_url` varchar(100) DEFAULT NULL,
  `relationship_status` varchar(20) DEFAULT NULL,
  `nationality` char(2) DEFAULT 'KR',
  `job_category` varchar(100) DEFAULT NULL,
  `user_status` varchar(20) NOT NULL DEFAULT 'ACTIVE',
  `refresh_token` varchar(1024) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `google_id` (`google_id`),
  UNIQUE KEY `email` (`email`),
  KEY `idx_user_email` (`email`),
  KEY `idx_user_google_id` (`google_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- ============================================
-- 3. 모임 일정 테이블
-- ============================================
CREATE TABLE IF NOT EXISTS `meeting_slots` (
  `slot_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `confirmed_restaurant_id` int(11) DEFAULT NULL,
  `location_area` varchar(50) NOT NULL,
  `meet_date` date NOT NULL,
  `meet_time` time NOT NULL,
  `max_capacity` int(11) DEFAULT NULL,
  `current_count` int(11) DEFAULT 0,
  `status` varchar(20) DEFAULT 'OPEN',
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`slot_id`),
  UNIQUE KEY `uq_schedule` (`location_area`,`meet_date`,`meet_time`),
  KEY `fk_slots_rest` (`confirmed_restaurant_id`),
  CONSTRAINT `fk_slots_rest` FOREIGN KEY (`confirmed_restaurant_id`) REFERENCES `restaurants` (`restaurant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- ============================================
-- 4. 모임 참여 테이블
-- ============================================
CREATE TABLE IF NOT EXISTS `slot_entries` (
  `entry_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `slot_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'JOINED',
  `cancel_reason` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`entry_id`),
  UNIQUE KEY `uq_slot_user` (`slot_id`,`user_id`),
  KEY `idx_my_entry` (`user_id`,`status`),
  KEY `idx_slot` (`slot_id`),
  CONSTRAINT `fk_entries_slot` FOREIGN KEY (`slot_id`) REFERENCES `meeting_slots` (`slot_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_entries_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- ============================================
-- 5. 사용자 성향 테이블
-- ============================================
CREATE TABLE IF NOT EXISTS `user_traits` (
  `traits_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `answers` text DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`traits_id`),
  KEY `idx_traits_user` (`user_id`),
  CONSTRAINT `fk_traits_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- ============================================
-- 6. 사용자 선호 가격대 테이블
-- ============================================
CREATE TABLE IF NOT EXISTS `user_trait_prices` (
  `user_id` bigint(20) NOT NULL,
  `price_tier` varchar(10) NOT NULL,
  PRIMARY KEY (`user_id`,`price_tier`),
  CONSTRAINT `fk_price_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- ============================================
-- 7. 사용자 선호 언어 테이블
-- ============================================
CREATE TABLE IF NOT EXISTS `user_trait_languages` (
  `user_id` bigint(20) NOT NULL,
  `language` varchar(10) NOT NULL,
  PRIMARY KEY (`user_id`,`language`),
  CONSTRAINT `fk_lang_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- ============================================
-- 8. 사용자 리뷰 테이블 (사용자 간 매너 평가)
-- ============================================
CREATE TABLE IF NOT EXISTS `user_reviews` (
  `user_review_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `slot_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `target_user_id` bigint(20) NOT NULL,
  `manner_score` tinyint(4) NOT NULL DEFAULT 5,
  `situation_share` varchar(50) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`user_review_id`),
  UNIQUE KEY `uq_ureview` (`slot_id`,`user_id`,`target_user_id`),
  KEY `fk_urev_from` (`user_id`),
  KEY `fk_urev_to` (`target_user_id`),
  CONSTRAINT `fk_urev_from` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_urev_slot` FOREIGN KEY (`slot_id`) REFERENCES `meeting_slots` (`slot_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_urev_to` FOREIGN KEY (`target_user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- ============================================
-- 9. 모임 리뷰 테이블 (식당/모임 평가)
-- ============================================
CREATE TABLE IF NOT EXISTS `meeting_reviews` (
  `review_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `slot_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `score_food` tinyint(4) NOT NULL,
  `score_service` tinyint(4) NOT NULL,
  `score_ambiance` tinyint(4) NOT NULL,
  `score_slot` tinyint(4) NOT NULL DEFAULT 5,
  `feedback` text DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`review_id`),
  UNIQUE KEY `uq_review` (`slot_id`,`user_id`),
  KEY `fk_mrev_user` (`user_id`),
  CONSTRAINT `fk_mrev_slot` FOREIGN KEY (`slot_id`) REFERENCES `meeting_slots` (`slot_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_mrev_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- ============================================
-- 10. 회원 탈퇴 로그 테이블
-- ============================================
CREATE TABLE IF NOT EXISTS `withdraw_logs` (
  `log_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL,
  `original_email` varchar(100) DEFAULT NULL,
  `reason` varchar(100) NOT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`log_id`),
  KEY `fk_withdrawal_user` (`user_id`),
  CONSTRAINT `fk_withdrawal_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

