-- V1: 초기 스키마 (현재 DB 상태 기준 스냅샷)
-- baseline-on-migrate=true 설정으로 인해, flyway_schema_history가 없을 때 이 버전을 기준점으로 삼아
-- V1은 실행하지 않고 V2부터 적용된다.

CREATE TABLE `members` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '회원 ID',
  `email` varchar(255) NOT NULL COMMENT '로그인 이메일',
  `password_hash` varchar(255) DEFAULT NULL COMMENT '비밀번호 해시값. OAuth 전용 회원은 NULL 가능',
  `name` varchar(100) NOT NULL COMMENT '회원 이름',
  `birth_year` int DEFAULT NULL,
  `gender` varchar(20) DEFAULT NULL COMMENT '성별. 통계 목적 선택 수집',
  `member_role` varchar(30) NOT NULL COMMENT '회원 권한: USER, SELLER, ADMIN',
  `member_status` varchar(30) NOT NULL COMMENT '회원 상태: ACTIVE, INACTIVE, WITHDRAWN, SUSPENDED',
  `last_login_at` datetime(6) DEFAULT NULL COMMENT '마지막 로그인 일시',
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성 일시',
  `updated_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정 일시',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_members_email` (`email`),
  CONSTRAINT `chk_members_birth_year` CHECK ((`birth_year` IS NULL OR `birth_year` BETWEEN 1900 AND 2100)),
  CONSTRAINT `chk_members_gender` CHECK ((`gender` IS NULL OR `gender` IN ('MALE','FEMALE','NONE'))),
  CONSTRAINT `chk_members_member_role` CHECK (`member_role` IN ('USER','SELLER','ADMIN')),
  CONSTRAINT `chk_members_member_status` CHECK (`member_status` IN ('ACTIVE','INACTIVE','WITHDRAWN','SUSPENDED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='회원 기본 정보';


CREATE TABLE `categories` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '카테고리 ID',
  `parent_id` bigint DEFAULT NULL COMMENT '상위 카테고리 ID',
  `category_code` varchar(100) NOT NULL COMMENT '카테고리 코드',
  `category_name` varchar(100) NOT NULL COMMENT '카테고리명',
  `depth` int NOT NULL COMMENT '카테고리 깊이',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '정렬 순서',
  `category_status` varchar(30) NOT NULL COMMENT '카테고리 상태: ACTIVE, INACTIVE',
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성 일시',
  `updated_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정 일시',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_categories_category_code` (`category_code`),
  KEY `idx_categories_parent` (`parent_id`),
  CONSTRAINT `fk_categories_parent` FOREIGN KEY (`parent_id`) REFERENCES `categories` (`id`),
  CONSTRAINT `chk_categories_category_status` CHECK (`category_status` IN ('ACTIVE','INACTIVE')),
  CONSTRAINT `chk_categories_depth` CHECK (`depth` >= 1)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='플랫폼 공통 카테고리';


CREATE TABLE `member_addresses` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '배송지 ID',
  `member_id` bigint NOT NULL COMMENT '회원 ID',
  `recipient_name` varchar(100) NOT NULL COMMENT '수령인 이름',
  `phone_number` varchar(30) NOT NULL COMMENT '수령인 연락처',
  `postal_code` varchar(20) NOT NULL COMMENT '우편번호',
  `address_line1` varchar(255) NOT NULL COMMENT '기본 주소',
  `address_line2` varchar(255) DEFAULT NULL COMMENT '상세 주소',
  `is_default` bit(1) NOT NULL,
  `address_status` varchar(30) NOT NULL COMMENT '배송지 상태: ACTIVE, DELETED',
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성 일시',
  `updated_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정 일시',
  PRIMARY KEY (`id`),
  KEY `idx_member_addresses_member_status` (`member_id`,`address_status`),
  CONSTRAINT `fk_member_addresses_member` FOREIGN KEY (`member_id`) REFERENCES `members` (`id`),
  CONSTRAINT `chk_member_addresses_is_default` CHECK (`is_default` IN (0,1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='회원 배송지 정보';


CREATE TABLE `member_consents` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '회원 동의 ID',
  `member_id` bigint NOT NULL COMMENT '회원 ID',
  `consent_type` varchar(50) NOT NULL COMMENT '동의 유형',
  `is_agreed` bit(1) NOT NULL,
  `agreed_at` datetime(6) DEFAULT NULL COMMENT '동의 일시',
  `withdrawn_at` datetime(6) DEFAULT NULL COMMENT '철회 일시',
  `consent_version` varchar(50) NOT NULL COMMENT '약관 또는 동의서 버전',
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성 일시',
  `updated_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정 일시',
  PRIMARY KEY (`id`),
  KEY `idx_member_consents_member_type` (`member_id`,`consent_type`),
  CONSTRAINT `fk_member_consents_member` FOREIGN KEY (`member_id`) REFERENCES `members` (`id`),
  CONSTRAINT `chk_member_consents_is_agreed` CHECK (`is_agreed` IN (0,1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='회원 약관 및 개인정보 동의 이력';


CREATE TABLE `oauth_accounts` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'OAuth 계정 ID',
  `member_id` bigint NOT NULL COMMENT '회원 ID',
  `provider` varchar(30) NOT NULL COMMENT 'OAuth 제공자: GOOGLE, KAKAO, NAVER 등',
  `provider_user_id` varchar(255) NOT NULL COMMENT 'OAuth 제공자 사용자 식별자',
  `provider_email` varchar(255) DEFAULT NULL COMMENT 'OAuth 제공자 이메일',
  `connected_at` datetime(6) DEFAULT NULL COMMENT '연결 일시',
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성 일시',
  `updated_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정 일시',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_oauth_accounts_provider_user` (`provider`,`provider_user_id`),
  UNIQUE KEY `uk_oauth_accounts_member_provider` (`member_id`,`provider`),
  CONSTRAINT `fk_oauth_accounts_member` FOREIGN KEY (`member_id`) REFERENCES `members` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='OAuth 로그인 계정 정보';


CREATE TABLE `sellers` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '판매자 ID',
  `member_id` bigint NOT NULL COMMENT '회원 ID',
  `business_name` varchar(150) NOT NULL COMMENT '상호명',
  `business_registration_no` varchar(50) NOT NULL COMMENT '사업자등록번호',
  `representative_name` varchar(100) NOT NULL COMMENT '대표자명',
  `seller_status` varchar(30) NOT NULL COMMENT '판매자 상태: PENDING, APPROVED, REJECTED, SUSPENDED',
  `approved_at` datetime(6) DEFAULT NULL COMMENT '승인 일시',
  `rejected_at` datetime(6) DEFAULT NULL COMMENT '거절 일시',
  `rejection_reason` varchar(255) DEFAULT NULL COMMENT '거절 사유',
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성 일시',
  `updated_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정 일시',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sellers_member` (`member_id`),
  UNIQUE KEY `uk_sellers_business_registration_no` (`business_registration_no`),
  CONSTRAINT `fk_sellers_member` FOREIGN KEY (`member_id`) REFERENCES `members` (`id`),
  CONSTRAINT `chk_sellers_seller_status` CHECK (`seller_status` IN ('PENDING','APPROVED','REJECTED','SUSPENDED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='판매자 정보';


CREATE TABLE `stores` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '스토어 ID',
  `seller_id` bigint NOT NULL COMMENT '판매자 ID',
  `store_name` varchar(150) NOT NULL COMMENT '스토어명',
  `store_slug` varchar(150) NOT NULL COMMENT '스토어 URL 식별자',
  `description` varchar(1000) DEFAULT NULL COMMENT '스토어 설명',
  `store_status` varchar(30) NOT NULL COMMENT '스토어 상태: ACTIVE, INACTIVE, SUSPENDED',
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성 일시',
  `updated_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정 일시',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_stores_seller` (`seller_id`),
  UNIQUE KEY `uk_stores_store_name` (`store_name`),
  UNIQUE KEY `uk_stores_store_slug` (`store_slug`),
  CONSTRAINT `fk_stores_seller` FOREIGN KEY (`seller_id`) REFERENCES `sellers` (`id`),
  CONSTRAINT `chk_stores_store_status` CHECK (`store_status` IN ('ACTIVE','INACTIVE','SUSPENDED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='스토어 정보';


CREATE TABLE `products` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '상품 ID',
  `store_id` bigint NOT NULL COMMENT '스토어 ID',
  `category_id` bigint NOT NULL COMMENT '최하위 카테고리 ID',
  `product_name` varchar(200) NOT NULL COMMENT '상품명',
  `description` varchar(2000) DEFAULT NULL COMMENT '상품 설명',
  `price` int NOT NULL COMMENT '상품 가격',
  `stock_quantity` int NOT NULL COMMENT '재고 수량',
  `product_status` varchar(30) NOT NULL COMMENT '상품 상태: ON_SALE, SOLD_OUT, HIDDEN, DELETED',
  `displayed_at` datetime(6) DEFAULT NULL COMMENT '노출 시작 일시',
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성 일시',
  `updated_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정 일시',
  PRIMARY KEY (`id`),
  KEY `idx_products_store_category_status` (`store_id`,`category_id`,`product_status`),
  KEY `idx_products_category_status` (`category_id`,`product_status`),
  CONSTRAINT `fk_products_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`),
  CONSTRAINT `fk_products_store` FOREIGN KEY (`store_id`) REFERENCES `stores` (`id`),
  CONSTRAINT `chk_products_price` CHECK (`price` >= 0),
  CONSTRAINT `chk_products_product_status` CHECK (`product_status` IN ('ON_SALE','SOLD_OUT','HIDDEN','DELETED')),
  CONSTRAINT `chk_products_stock_quantity` CHECK (`stock_quantity` >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='상품 정보';


CREATE TABLE `store_category_preferences` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '스토어 카테고리 선호 ID',
  `store_id` bigint NOT NULL COMMENT '스토어 ID',
  `category_id` bigint NOT NULL COMMENT '자주 사용하는 최하위 카테고리 ID',
  `is_favorite` tinyint NOT NULL DEFAULT '0' COMMENT '즐겨찾기 여부',
  `usage_count` int NOT NULL DEFAULT '0' COMMENT '사용 횟수',
  `last_used_at` datetime(6) DEFAULT NULL COMMENT '마지막 사용 일시',
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성 일시',
  `updated_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정 일시',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_store_category_preferences_store_category` (`store_id`,`category_id`),
  KEY `fk_store_category_preferences_category` (`category_id`),
  CONSTRAINT `fk_store_category_preferences_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`),
  CONSTRAINT `fk_store_category_preferences_store` FOREIGN KEY (`store_id`) REFERENCES `stores` (`id`),
  CONSTRAINT `chk_store_category_preferences_is_favorite` CHECK (`is_favorite` IN (0,1)),
  CONSTRAINT `chk_store_category_preferences_usage_count` CHECK (`usage_count` >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='스토어별 자주 쓰는 카테고리 설정';


CREATE TABLE `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '주문 ID',
  `member_id` bigint NOT NULL COMMENT '주문 회원 ID',
  `store_id` bigint NOT NULL COMMENT '주문 대상 스토어 ID',
  `member_address_id` bigint DEFAULT NULL COMMENT '선택한 회원 배송지 ID',
  `order_no` varchar(100) NOT NULL COMMENT '주문 번호',
  `order_status` varchar(30) NOT NULL COMMENT '주문 상태',
  `total_product_amount` int NOT NULL COMMENT '상품 총액',
  `delivery_fee` int NOT NULL DEFAULT '0' COMMENT '배송비',
  `final_payment_amount` int NOT NULL COMMENT '최종 결제 금액',
  `store_name_snapshot` varchar(150) NOT NULL COMMENT '주문 당시 스토어명',
  `ordered_at` datetime(6) NOT NULL COMMENT '주문 일시',
  `paid_at` datetime(6) DEFAULT NULL COMMENT '결제 완료 일시',
  `cancelled_at` datetime(6) DEFAULT NULL COMMENT '주문 취소 일시',
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성 일시',
  `updated_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정 일시',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_orders_order_no` (`order_no`),
  KEY `idx_orders_member_status` (`member_id`,`order_status`),
  KEY `idx_orders_store_status` (`store_id`,`order_status`),
  KEY `fk_orders_member_address` (`member_address_id`),
  CONSTRAINT `fk_orders_member` FOREIGN KEY (`member_id`) REFERENCES `members` (`id`),
  CONSTRAINT `fk_orders_member_address` FOREIGN KEY (`member_address_id`) REFERENCES `member_addresses` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_orders_store` FOREIGN KEY (`store_id`) REFERENCES `stores` (`id`),
  CONSTRAINT `chk_orders_delivery_fee` CHECK (`delivery_fee` >= 0),
  CONSTRAINT `chk_orders_final_payment_amount` CHECK (`final_payment_amount` >= 0),
  CONSTRAINT `chk_orders_order_status` CHECK (`order_status` IN ('CREATED','PAID','PREPARING','SHIPPED','DELIVERED','CANCELLED','REFUNDED')),
  CONSTRAINT `chk_orders_total_product_amount` CHECK (`total_product_amount` >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='주문 정보';


CREATE TABLE `order_items` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '주문 상품 ID',
  `order_id` bigint NOT NULL COMMENT '주문 ID',
  `product_id` bigint NOT NULL COMMENT '상품 ID',
  `product_name_snapshot` varchar(200) NOT NULL COMMENT '주문 당시 상품명',
  `product_price_snapshot` int NOT NULL COMMENT '주문 당시 상품 가격',
  `quantity` int NOT NULL COMMENT '주문 수량',
  `item_amount` int NOT NULL COMMENT '주문 상품 금액',
  `product_option_snapshot` varchar(255) DEFAULT NULL COMMENT '주문 당시 상품 옵션',
  `order_item_status` varchar(30) NOT NULL COMMENT '주문 상품 상태',
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성 일시',
  `updated_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정 일시',
  PRIMARY KEY (`id`),
  KEY `idx_order_items_order` (`order_id`),
  KEY `idx_order_items_product` (`product_id`),
  CONSTRAINT `fk_order_items_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `fk_order_items_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
  CONSTRAINT `chk_order_items_item_amount` CHECK (`item_amount` >= 0),
  CONSTRAINT `chk_order_items_price` CHECK (`product_price_snapshot` >= 0),
  CONSTRAINT `chk_order_items_quantity` CHECK (`quantity` > 0),
  CONSTRAINT `chk_order_items_status` CHECK (`order_item_status` IN ('ORDERED','CANCELLED','REFUNDED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='주문 상품 스냅샷';


CREATE TABLE `order_shipping_snapshots` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '주문 배송지 스냅샷 ID',
  `order_id` bigint NOT NULL COMMENT '주문 ID',
  `recipient_name_snapshot` varchar(100) NOT NULL COMMENT '주문 당시 수령인',
  `phone_number_snapshot` varchar(30) NOT NULL COMMENT '주문 당시 연락처',
  `postal_code_snapshot` varchar(20) NOT NULL COMMENT '주문 당시 우편번호',
  `address_line1_snapshot` varchar(255) NOT NULL COMMENT '주문 당시 기본 주소',
  `address_line2_snapshot` varchar(255) DEFAULT NULL COMMENT '주문 당시 상세 주소',
  `delivery_memo_snapshot` varchar(255) DEFAULT NULL COMMENT '배송 메모',
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성 일시',
  `updated_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정 일시',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_shipping_snapshots_order` (`order_id`),
  CONSTRAINT `fk_order_shipping_snapshots_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='주문 배송지 스냅샷';


CREATE TABLE `order_status_histories` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '주문 상태 이력 ID',
  `order_id` bigint NOT NULL COMMENT '주문 ID',
  `previous_status` varchar(30) DEFAULT NULL COMMENT '이전 상태',
  `new_status` varchar(30) NOT NULL COMMENT '변경 상태',
  `changed_reason` varchar(255) DEFAULT NULL COMMENT '상태 변경 사유',
  `changed_at` datetime(6) NOT NULL COMMENT '상태 변경 일시',
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성 일시',
  PRIMARY KEY (`id`),
  KEY `idx_order_status_histories_order` (`order_id`),
  CONSTRAINT `fk_order_status_histories_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='주문 상태 변경 이력';


CREATE TABLE `payments` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '결제 ID',
  `order_id` bigint NOT NULL COMMENT '주문 ID',
  `payment_no` varchar(100) NOT NULL COMMENT '내부 결제 번호',
  `payment_provider` varchar(50) NOT NULL COMMENT '결제 제공자',
  `payment_method` varchar(50) NOT NULL COMMENT '결제 수단',
  `payment_status` varchar(30) NOT NULL COMMENT '결제 상태',
  `payment_amount` int NOT NULL COMMENT '결제 금액',
  `provider_payment_key` varchar(255) DEFAULT NULL COMMENT 'PG사 결제 키',
  `requested_at` datetime(6) DEFAULT NULL COMMENT '결제 요청 일시',
  `approved_at` datetime(6) DEFAULT NULL COMMENT '결제 승인 일시',
  `cancelled_at` datetime(6) DEFAULT NULL COMMENT '결제 취소 일시',
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성 일시',
  `updated_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정 일시',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_payments_payment_no` (`payment_no`),
  UNIQUE KEY `uk_payments_provider_payment_key` (`provider_payment_key`),
  KEY `idx_payments_order_status` (`order_id`,`payment_status`),
  CONSTRAINT `fk_payments_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `chk_payments_payment_amount` CHECK (`payment_amount` >= 0),
  CONSTRAINT `chk_payments_payment_status` CHECK (`payment_status` IN ('READY','APPROVED','FAILED','CANCELLED','PARTIAL_CANCELLED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='결제 정보';


CREATE TABLE `payment_histories` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '결제 이력 ID',
  `payment_id` bigint NOT NULL COMMENT '결제 ID',
  `previous_status` varchar(30) DEFAULT NULL COMMENT '이전 상태',
  `new_status` varchar(30) NOT NULL COMMENT '변경 상태',
  `event_type` varchar(50) NOT NULL COMMENT '결제 이벤트 유형',
  `event_message` varchar(255) DEFAULT NULL COMMENT '결제 이벤트 메시지',
  `changed_at` datetime(6) NOT NULL COMMENT '변경 일시',
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성 일시',
  PRIMARY KEY (`id`),
  KEY `idx_payment_histories_payment` (`payment_id`),
  CONSTRAINT `fk_payment_histories_payment` FOREIGN KEY (`payment_id`) REFERENCES `payments` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='결제 상태 변경 이력';


CREATE TABLE `product_images` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '상품 이미지 ID',
  `product_id` bigint NOT NULL COMMENT '상품 ID',
  `image_url` varchar(500) NOT NULL COMMENT '이미지 URL',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '정렬 순서',
  `is_thumbnail` tinyint NOT NULL DEFAULT '0' COMMENT '대표 이미지 여부',
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성 일시',
  `updated_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정 일시',
  PRIMARY KEY (`id`),
  KEY `idx_product_images_product` (`product_id`),
  CONSTRAINT `fk_product_images_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
  CONSTRAINT `chk_product_images_is_thumbnail` CHECK (`is_thumbnail` IN (0,1)),
  CONSTRAINT `chk_product_images_sort_order` CHECK (`sort_order` >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='상품 이미지';
