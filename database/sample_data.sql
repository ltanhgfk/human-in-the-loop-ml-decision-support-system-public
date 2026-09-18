-- =====================================================================
-- Rice-Farming SMS/MMS Advisory System - FICTIONAL sample data
-- =====================================================================
-- Every name, phone number, email, and message below is fabricated for
-- demonstration purposes only. None of it is derived from, or a
-- disguised version of, any real record that existed in this project's
-- original database. It exists solely so the schema in schema.sql can
-- be exercised end-to-end without needing real user data.
--
-- Do NOT treat any value here as historically accurate.
-- =====================================================================

-- Subject-matter taxonomy (topic categories the SVM classifier routes into)
INSERT INTO `tbl_major` (`id`, `code`, `name`, `note`) VALUES
(1, '1', 'Sau hai (Pests)', 'Insect and pest-related questions'),
(2, '2', 'Benh cay (Plant disease)', 'Fungal/bacterial/viral disease questions'),
(3, '3', 'Co dai (Weeds)', 'Weed control questions'),
(4, '4', 'Giong lua (Rice varieties)', 'Variety selection questions'),
(5, '5', 'Sau thu hoach (Post-harvest)', 'Post-harvest handling questions'),
(6, '6', 'Ky thuat canh tac (Farming technique)', 'General cultivation technique questions'),
(7, '7', 'Khac/Spam (Other/Spam)', 'Unclassifiable or spam messages');

-- Runtime configuration (read by model.Config at runtime; see docs/ML_PIPELINE.md)
INSERT INTO `tbl_config` (`id`, `name`, `valueint`, `valuebool`, `valuechar`, `code`) VALUES
(1, 'System classification mode', NULL, b'1', 'auto', 'systemconfig'),
(2, 'Auto keyword rebuilding', NULL, b'1', NULL, 'autokeyword'),
(3, 'Retraining check interval (seconds)', 3600, NULL, NULL, 'sleep_time_retrain'),
(4, 'Minimum new labeled messages before retraining', 20, NULL, NULL, 'number_sms_retrain'),
(5, 'Auto-answer via cosine similarity enabled', NULL, b'1', NULL, 'auto_answer_enabled'),
(6, 'Auto-answer cosine similarity threshold (percent)', 75, NULL, NULL, 'auto_answer_threshold');

-- Fictional agricultural experts (no relation to any real person)
INSERT INTO `tbl_expert` (`id`, `code`, `name`, `title`, `degree`, `workunit`, `mobile`, `gatewayid`, `email`, `address`, `birthdate`, `status`, `sex`, `loginname`, `loginpassword`, `usertype`) VALUES
(1, 'EXP01', 'Nguyen Van A (fictional)', 'Lecturer', 'PhD', 'Fictional University of Agriculture', '0900000001', 1, 'fictional.expert1@example.com', 'Fictional Address 1', '01/01/1975', b'1', 'Male', 'expert1', 'CHANGE_ME_HASH', 'expert'),
(2, 'EXP02', 'Tran Thi B (fictional)', 'Researcher', 'MSc', 'Fictional Institute of Plant Protection', '0900000002', 1, 'fictional.expert2@example.com', 'Fictional Address 2', '02/02/1980', b'1', 'Female', 'expert2', 'CHANGE_ME_HASH', 'expert');

-- Fictional incoming questions + answers (demonstrates the tbl_mms workflow
-- state machine described in docs/ARCHITECTURE.md and docs/ML_PIPELINE.md)
INSERT INTO `tbl_mms` (`id`, `sender`, `msg`, `image`, `replymsg`, `expertid`, `majoridByMachine`, `majoridByHuman`, `answered`, `classifiedByHuman`, `classifiedByMachine`, `encoded`, `receivedMms`, `used`, `receivedDate`, `answeredDate`, `gatewayid`, `sentexpert`, `sentfarmer`) VALUES
(1, '0900000101', 'Lua cua toi bi vang la, xin hoi la benh gi? (fictional example question)', NULL, 'Day co the la trieu chung thieu dam, ban nen kiem tra lai lich bon phan. (fictional example answer)', 1, 2, 2, b'1', b'1', b'1', '7', b'0', b'1', '2020-01-01 08:00:00', '2020-01-01 09:00:00', 'modem1', b'1', b'1'),
(2, '0900000102', 'Co loai sau nao an la lua khong, cach phong tri? (fictional example question)', NULL, NULL, -1, 1, -1, b'0', b'0', b'1', '7', b'0', b'0', '2020-01-02 08:00:00', NULL, 'modem1', b'0', b'0'),
(3, '0900000101', 'Lua cua toi bi vang la nhu lan truoc, phai lam sao? (fictional near-duplicate of #1, for illustrating cosine-similarity auto-answer)', NULL, 'Day co the la trieu chung thieu dam, ban nen kiem tra lai lich bon phan. (fictional example answer)', -1, 2, -1, b'1', b'0', b'1', '7', b'0', b'0', '2020-01-05 08:00:00', '2020-01-05 08:00:05', 'modem1', b'1', b'1');
