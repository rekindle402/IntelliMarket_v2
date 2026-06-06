-- V2: Boolean 컬럼 타입 통일 (tinyint → bit(1))
-- Hibernate @Column 매핑과 DB 타입 불일치 해소
-- 대상: store_category_preferences.is_favorite, product_images.is_thumbnail

ALTER TABLE `store_category_preferences`
  MODIFY COLUMN `is_favorite` bit(1) NOT NULL DEFAULT b'0' COMMENT '즐겨찾기 여부';

ALTER TABLE `product_images`
  MODIFY COLUMN `is_thumbnail` bit(1) NOT NULL DEFAULT b'0' COMMENT '대표 이미지 여부';
