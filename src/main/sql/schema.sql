DROP TABLE IF EXISTS `ecommerce_user`;
CREATE TABLE `ecommerce_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'user id',
  `username` varchar(50) NOT NULL COMMENT 'user name',
  `password` varchar(32) NOT NULL COMMENT 'password，MD5 encrypted',
  `email` varchar(50) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `question` varchar(100) DEFAULT NULL COMMENT 'retrieve password question',
  `answer` varchar(100) DEFAULT NULL COMMENT 'retrieve password answer',
  `role` int(4) NOT NULL COMMENT 'role, 0.admin, 1.user',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_name_unique` (`username`) USING BTREE
) ENGINE=InnoDB COMMENT 'user table';

DROP TABLE IF EXISTS `ecommerce_category`;
CREATE TABLE `ecommerce_category` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'category Id',
  `parent_id` int(11) DEFAULT NULL COMMENT 'parent category id, 0 indicates root category',
  `name` varchar(50) DEFAULT NULL COMMENT 'category name',
  `status` tinyint(1) DEFAULT '1' COMMENT 'status, 1.in use, 2.out of use',
  `sort_order` int(4) DEFAULT NULL COMMENT 'sort order',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT 'category table';

DROP TABLE IF EXISTS `ecommerce_product`;
CREATE TABLE `ecommerce_product` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'product id',
  `category_id` int(11) NOT NULL COMMENT 'category id',
  `name` varchar(100) NOT NULL COMMENT 'product name',
  `subtitle` varchar(200) DEFAULT NULL COMMENT 'product subtitle',
  `main_image` varchar(500) DEFAULT NULL COMMENT 'product main image, relative url',
  `sub_images` text COMMENT 'sub-images, json format',
  `detail` text COMMENT 'product detail',
  `price` decimal(20,2) NOT NULL COMMENT 'price, .xx',
  `stock` int(11) NOT NULL COMMENT 'stock number',
  `status` int(6) DEFAULT '1' COMMENT 'product status, 1.selling 2.sold out 3.deleted',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT 'product table';

DROP TABLE IF EXISTS `ecommerce_shipping`;
CREATE TABLE `ecommerce_shipping` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL COMMENT 'user id',
  `receiver_name` varchar(20) DEFAULT NULL COMMENT 'receiver name',
  `receiver_phone` varchar(20) DEFAULT NULL COMMENT 'receiver phone',
  `receiver_mobile` varchar(20) DEFAULT NULL COMMENT 'receiver mobile',
  `receiver_province` varchar(20) DEFAULT NULL COMMENT 'province',
  `receiver_city` varchar(20) DEFAULT NULL COMMENT 'city',
  `receiver_district` varchar(20) DEFAULT NULL COMMENT 'district',
  `receiver_address` varchar(200) DEFAULT NULL COMMENT 'address detail',
  `receiver_zip` varchar(6) DEFAULT NULL COMMENT 'zip',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT 'shipping table';

DROP TABLE IF EXISTS `ecommerce_cart`;
CREATE TABLE `ecommerce_cart` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT 'user id',
  `product_id` int(11) DEFAULT NULL COMMENT 'product id',
  `quantity` int(11) DEFAULT NULL COMMENT 'quantity',
  `checked` int(11) DEFAULT NULL COMMENT 'if selected?,1=selected,0=not selected',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `user_id_index` (`user_id`) USING BTREE
) ENGINE=InnoDB COMMENT 'cart table';

DROP TABLE IF EXISTS `ecommerce_order`;
CREATE TABLE `ecommerce_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'order id',
  `order_no` bigint(20) DEFAULT NULL COMMENT 'order number',
  `user_id` int(11) DEFAULT NULL COMMENT 'user id',
  `shipping_id` int(11) DEFAULT NULL,
  `payment` decimal(20,2) DEFAULT NULL COMMENT 'payment amount, .xx',
  `payment_type` int(4) DEFAULT NULL COMMENT 'payment type, 1.online payment',
  `postage` int(10) DEFAULT NULL COMMENT 'postage',
  `status` int(4) DEFAULT NULL COMMENT 'order status, 0.cancelled, 10.not paid，20.paid，40.delivered，50.transaction finished，60.transaction closed',
  `payment_time` datetime DEFAULT NULL COMMENT 'payment time',
  `send_time` datetime DEFAULT NULL COMMENT 'delivered time',
  `end_time` datetime DEFAULT NULL COMMENT 'transaction finished time',
  `close_time` datetime DEFAULT NULL COMMENT 'transaction closed time',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_no_index` (`order_no`) USING BTREE
) ENGINE=InnoDB COMMENT 'order table';

DROP TABLE IF EXISTS `ecommerce_order_item`;
CREATE TABLE `ecommerce_order_item` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'order item id',
  `user_id` int(11) DEFAULT NULL,
  `order_no` bigint(20) DEFAULT NULL COMMENT 'order number',
  `product_id` int(11) DEFAULT NULL COMMENT 'product id',
  `product_name` varchar(100) DEFAULT NULL,
  `product_image` varchar(500) DEFAULT NULL,
  `current_unit_price` decimal(20,2) DEFAULT NULL COMMENT 'unit price when creating the order',
  `quantity` int(10) DEFAULT NULL COMMENT 'quantity',
  `total_price` decimal(20,2) DEFAULT NULL COMMENT 'total price, .xx',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `order_no_index` (`order_no`) USING BTREE,
  KEY `order_no_user_id_index` (`user_id`,`order_no`) USING BTREE
) ENGINE=InnoDB COMMENT 'order item table';

DROP TABLE IF EXISTS `ecommerce_pay_info`;
CREATE TABLE `ecommerce_pay_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL COMMENT 'user id',
  `order_no` bigint(20) DEFAULT NULL COMMENT 'order number',
  `pay_platform` int(10) DEFAULT NULL COMMENT 'payment platform',
  `platform_number` varchar(200) DEFAULT NULL,
  `platform_status` varchar(20) DEFAULT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT 'payment information table';