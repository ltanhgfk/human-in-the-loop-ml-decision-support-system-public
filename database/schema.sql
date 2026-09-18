-- =====================================================================
-- Rice-Farming SMS/MMS Advisory System - Database Schema (structure only)
-- =====================================================================
-- Reconstructed from the original 2014 MySQL dump. Every INSERT (real
-- data) statement has been removed. See ../docs/DATA_PRIVACY.md for the
-- full accounting of what was excluded and why.
--
-- Real data dumps remain ONLY in the private, local pre-phase2 branch /
-- ORIGINAL_PRESERVED copy. They are not part of this public schema file
-- and are not published.
-- =====================================================================

CREATE DATABASE  IF NOT EXISTS `assmms` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `assmms`;
-- MySQL dump 10.13  Distrib 5.6.13, for Win32 (x86)
--
-- Host: 127.0.0.1    Database: assmms
-- ------------------------------------------------------
-- Server version	5.6.14

--
-- Table structure for table `admin`
--

DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
  `ID_ADMIN` int(11) NOT NULL AUTO_INCREMENT,
  `USERNAME` varchar(45) NOT NULL,
  `PASSWORD` varchar(255) NOT NULL,
  PRIMARY KEY (`ID_ADMIN`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
--
--


--
-- Table structure for table `tbl_config`
--

DROP TABLE IF EXISTS `tbl_config`;
CREATE TABLE `tbl_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(200) NOT NULL,
  `valueint` int(11) DEFAULT NULL,
  `valuebool` bit(1) DEFAULT NULL,
  `valuechar` varchar(45) DEFAULT NULL,
  `code` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code_UNIQUE` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;
--
--


--
-- Table structure for table `tbl_content`
--

DROP TABLE IF EXISTS `tbl_content`;
CREATE TABLE `tbl_content` (
  `id` int(11) NOT NULL,
  `name` varchar(200) NOT NULL,
  `code` int(11) NOT NULL AUTO_INCREMENT,
  `content` varchar(2000) DEFAULT NULL,
  `createddate` varchar(25) DEFAULT NULL,
  `modifieddate` varchar(25) DEFAULT NULL,
  `expertid` int(11) NOT NULL,
  `keywords` text,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code_UNIQUE` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
--
--


--
-- Table structure for table `tbl_content_mms`
--

DROP TABLE IF EXISTS `tbl_content_mms`;
CREATE TABLE `tbl_content_mms` (
  `content_id` int(11) NOT NULL,
  `mms_id` int(11) NOT NULL,
  PRIMARY KEY (`content_id`,`mms_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
--


--
-- Table structure for table `tbl_expert`
--

DROP TABLE IF EXISTS `tbl_expert`;
CREATE TABLE `tbl_expert` (
  `id` int(4) NOT NULL AUTO_INCREMENT,
  `code` varchar(15) CHARACTER SET utf8 NOT NULL,
  `name` varchar(100) CHARACTER SET utf8 NOT NULL,
  `title` varchar(40) CHARACTER SET utf8 NOT NULL,
  `degree` varchar(40) CHARACTER SET utf8 NOT NULL,
  `workunit` varchar(100) CHARACTER SET utf8 DEFAULT NULL,
  `mobile` varchar(15) CHARACTER SET utf8 NOT NULL,
  `gatewayid` int(4) DEFAULT NULL,
  `email` varchar(100) CHARACTER SET utf8 NOT NULL,
  `address` varchar(100) CHARACTER SET utf8 DEFAULT NULL,
  `birthdate` varchar(10) CHARACTER SET utf8 DEFAULT NULL,
  `image` mediumblob,
  `status` bit(1) NOT NULL,
  `sex` varchar(30) CHARACTER SET utf8 NOT NULL,
  `loginname` varchar(50) CHARACTER SET utf8 NOT NULL,
  `loginpassword` varchar(50) CHARACTER SET utf8 NOT NULL,
  `usertype` varchar(11) CHARACTER SET utf8 NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`),
  UNIQUE KEY `loginname` (`loginname`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
--
--


--
-- Table structure for table `tbl_expertmajor`
--

DROP TABLE IF EXISTS `tbl_expertmajor`;
CREATE TABLE `tbl_expertmajor` (
  `expertid` int(4) NOT NULL,
  `majorid` int(4) NOT NULL,
  PRIMARY KEY (`expertid`,`majorid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
--
--


--
-- Table structure for table `tbl_gateway`
--

DROP TABLE IF EXISTS `tbl_gateway`;
CREATE TABLE `tbl_gateway` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `operator` varchar(45) NOT NULL,
  `gatewayip` varchar(45) NOT NULL,
  `port` int(11) NOT NULL,
  `mmsc` varchar(200) NOT NULL,
  `mmscnumber` varchar(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `operator_UNIQUE` (`operator`),
  UNIQUE KEY `gateway_UNIQUE` (`gatewayip`),
  UNIQUE KEY `mmsc_UNIQUE` (`mmsc`),
  UNIQUE KEY `mmscnumber_UNIQUE` (`mmscnumber`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;
--
--


--
-- Table structure for table `tbl_log_calls`
--

DROP TABLE IF EXISTS `tbl_log_calls`;
CREATE TABLE `tbl_log_calls` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `call_date` datetime NOT NULL,
  `gateway_id` varchar(64) NOT NULL,
  `caller_id` varchar(16) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
--


--
-- Table structure for table `tbl_major`
--

DROP TABLE IF EXISTS `tbl_major`;
CREATE TABLE `tbl_major` (
  `id` int(4) NOT NULL AUTO_INCREMENT,
  `code` varchar(2) CHARACTER SET utf8 NOT NULL,
  `name` varchar(100) CHARACTER SET utf8 NOT NULL,
  `note` text CHARACTER SET utf8,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`),
  UNIQUE KEY `code_2` (`code`,`name`)
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=latin1;
--
--


--
-- Table structure for table `tbl_mms`
--

DROP TABLE IF EXISTS `tbl_mms`;
CREATE TABLE `tbl_mms` (
  `id` int(4) NOT NULL AUTO_INCREMENT,
  `sender` varchar(15) CHARACTER SET utf8 NOT NULL,
  `msg` varchar(1000) CHARACTER SET utf8 NOT NULL,
  `image` varchar(200) CHARACTER SET utf8 DEFAULT NULL,
  `replymsg` varchar(1000) CHARACTER SET utf8 DEFAULT NULL,
  `expertid` int(4) DEFAULT '-1',
  `majoridByMachine` int(4) DEFAULT '-1',
  `majoridByHuman` int(4) DEFAULT '-1',
  `answered` bit(1) NOT NULL DEFAULT b'0',
  `classifiedByHuman` bit(1) NOT NULL DEFAULT b'0',
  `classifiedByMachine` bit(1) NOT NULL DEFAULT b'0',
  `encoded` char(1) NOT NULL DEFAULT '7',
  `receivedMms` bit(1) NOT NULL DEFAULT b'0',
  `used` bit(1) DEFAULT b'0',
  `receivedDate` varchar(20) DEFAULT 'CURRENT_TIMESTAMP',
  `answeredDate` varchar(20) DEFAULT NULL,
  `gatewayid` varchar(45) NOT NULL DEFAULT 'modem1',
  `sentexpert` bit(1) NOT NULL DEFAULT b'0',
  `sentfarmer` bit(1) DEFAULT b'0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=966 DEFAULT CHARSET=latin1;
--
--


--
-- Table structure for table `tbl_sms_in`
--

DROP TABLE IF EXISTS `tbl_sms_in`;
CREATE TABLE `tbl_sms_in` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `process` int(11) NOT NULL,
  `originator` varchar(16) NOT NULL,
  `type` varchar(1) NOT NULL,
  `encoding` char(1) NOT NULL,
  `message_date` datetime NOT NULL,
  `receive_date` datetime NOT NULL,
  `text` varchar(1000) NOT NULL,
  `original_ref_no` varchar(64) DEFAULT NULL,
  `original_receive_date` datetime DEFAULT NULL,
  `gateway_id` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8;
--
--

/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50003 TRIGGER insert_tbl_mms AFTER INSERT ON tbl_sms_in
FOR EACH ROW 
BEGIN
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `tbl_sms_out`
--

DROP TABLE IF EXISTS `tbl_sms_out`;
CREATE TABLE `tbl_sms_out` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `type` varchar(1) NOT NULL DEFAULT 'O',
  `recipient` varchar(16) NOT NULL,
  `text` varchar(1000) NOT NULL,
  `wap_url` varchar(100) DEFAULT NULL,
  `wap_expiry_date` datetime DEFAULT NULL,
  `wap_signal` varchar(1) DEFAULT NULL,
  `create_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `originator` varchar(16) NOT NULL DEFAULT ' ',
  `encoding` varchar(1) NOT NULL DEFAULT '7',
  `status_report` int(1) NOT NULL DEFAULT '0',
  `flash_sms` int(1) NOT NULL DEFAULT '0',
  `src_port` int(6) NOT NULL DEFAULT '-1',
  `dst_port` int(6) NOT NULL DEFAULT '-1',
  `sent_date` datetime DEFAULT NULL,
  `ref_no` varchar(64) DEFAULT NULL,
  `priority` int(5) NOT NULL DEFAULT '0',
  `status` varchar(1) NOT NULL DEFAULT 'U',
  `errors` int(2) NOT NULL DEFAULT '0',
  `gateway_id` varchar(64) NOT NULL DEFAULT '*',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=295 DEFAULT CHARSET=utf8;
--
--

-- Dump completed on 2014-03-01 14:51:23
