-- MySQL dump 10.16  Distrib 10.1.37-MariaDB, for debian-linux-gnueabihf (armv8l)
--
-- Host: localhost    Database: smart_key
-- ------------------------------------------------------
-- Server version	10.1.37-MariaDB-0+deb9u1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `actions`
--

DROP TABLE IF EXISTS `actions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `actions` (
  `ActionID` int(11) NOT NULL AUTO_INCREMENT,
  `Action` varchar(25) NOT NULL,
  PRIMARY KEY (`ActionID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `actions`
--

LOCK TABLES `actions` WRITE;
/*!40000 ALTER TABLE `actions` DISABLE KEYS */;
/*!40000 ALTER TABLE `actions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `keyData`
--

DROP TABLE IF EXISTS `keyData`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `keyData` (
  `VehID` int(11) NOT NULL,
  `KeyID` int(11) NOT NULL AUTO_INCREMENT,
  `UserID` int(11) NOT NULL,
  `EncryptionKey` varchar(250) DEFAULT NULL,
  `KeyRoller` int(250) NOT NULL,
  `KeyChars` varchar(250) NOT NULL,
  PRIMARY KEY (`KeyID`),
  KEY `keys_ibfk_1` (`UserID`),
  KEY `VehID` (`VehID`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `keyData`
--

LOCK TABLES `keyData` WRITE;
/*!40000 ALTER TABLE `keyData` DISABLE KEYS */;
INSERT INTO `keyData` VALUES (17,13,4,NULL,87,'lysF6fs5mbEHLQv5sSv6OztKGmGpMBo9K5EyLxecVXavWFaHRGvEHfX4WNQ5dSDOwEuy7uWdSFKX9BFJ57EwTppDDzZn3WD1qMSmHbvI8ec2tdguRiFTXedkhyApZIT0OoKvxs3cPpDnzdXvdh2XLbgjwLMAECQR3iq5WWg53DwNKstKCV3tl3PNakIsAGfXfoavRJRdrxdSB6QB2Mq1LO8z88dLIILmXZQMIf6lsA80KaNFn5HSAmPcRB'),(5,15,21,NULL,4,'hdh'),(5,17,23,NULL,73,'mcfdQ5veSKPRbdvZrHLkzWj3s1XIrlrWOsTPaKOMlGWq2cOGKN80hnG7cS17KNccZ8BEtLY1xd0Md5pIxqWGPM77bn28CDvRSr7oTNadG5p7Cqg1p6qFJevlTaWaFYazGOjFDmB2Tq4plT7At11g861yScm7K50Do7dmawXgAmrXuBXUm4XRkIqbBl9LGUXTbeQtYy5PkiUvY4APJjabLPneqcoXe7Gok80hl9T3JyD5kGUf6BPttKppm7'),(3,20,29,NULL,89,'JE9r5sWgN0wDj5p3lR5U72SLdN1Ug0ZfY1QGgTApqi9tb3phyPEwtM34mJ9lvEu6ZNMcMEx3mJDzO4TVpfbjRSGI3upqcz7LmXUBR9jlT26XxKHX6fULVSbrUe1OkvDlYNeaKyl4etXAu5t9cAr7fLH6MG9i7npiSdJSFKwYF5NMFoclcTO4uLYYh9XJK9Rk4zDvWgLxgVwFQ6hwWQy4OFwjTLJcdlk0vBxFX13FmoOGMGUgjuJ4b9xJQY');
/*!40000 ALTER TABLE `keyData` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tempkeys`
--

DROP TABLE IF EXISTS `tempkeys`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tempkeys` (
  `KeyID` int(11) NOT NULL AUTO_INCREMENT,
  `UserID` int(11) NOT NULL,
  `VehID` int(11) NOT NULL,
  `DateCreated` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `EndDate` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `EncryptionKey` varchar(250) NOT NULL,
  `KeyRoller` int(250) NOT NULL,
  `KeyChars` varchar(250) NOT NULL,
  PRIMARY KEY (`KeyID`),
  KEY `UserID` (`UserID`),
  KEY `VehID` (`VehID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tempkeys`
--

LOCK TABLES `tempkeys` WRITE;
/*!40000 ALTER TABLE `tempkeys` DISABLE KEYS */;
/*!40000 ALTER TABLE `tempkeys` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `uselog`
--

DROP TABLE IF EXISTS `uselog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `uselog` (
  `VehID` int(11) NOT NULL,
  `UserID` int(11) NOT NULL,
  `TimeOf` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `Action` int(11) DEFAULT NULL,
  `ActionDesc` int(11) NOT NULL,
  PRIMARY KEY (`VehID`,`UserID`,`TimeOf`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `uselog`
--

LOCK TABLES `uselog` WRITE;
/*!40000 ALTER TABLE `uselog` DISABLE KEYS */;
/*!40000 ALTER TABLE `uselog` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `UserID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(25) NOT NULL,
  `AccessType` int(11) NOT NULL,
  `CreatedOn` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `KeyData` varchar(250) NOT NULL,
  `VehID` int(4) NOT NULL,
  PRIMARY KEY (`UserID`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'test',1,'2019-03-19 16:57:02','KeyData',0),(2,'Jake',1,'2019-03-27 20:58:55','Jake',2),(3,'Jas',1,'2019-03-27 21:29:11','9e916c1b',2),(4,'Jake',1,'2019-03-27 22:28:12','Jake',5),(5,'Jake',1,'2019-03-27 22:29:32','9e916c1b',3),(6,'Jake',1,'2019-03-27 22:30:39','9e916c1b',3),(7,'jake',1,'2019-03-27 22:35:21','9e916c1b',3),(8,'jake',1,'2019-03-27 22:37:03','9e916c1b',3),(9,'Jake',1,'2019-03-27 22:38:31','9e916c1b',3),(10,'jake',1,'2019-03-27 22:41:12','9e916c1b',3),(11,'jake',1,'2019-03-27 22:43:23','9e916c1b',3),(12,'jake',1,'2019-03-27 22:45:43','9e916c1b',3),(13,'jsdgsd',1,'2019-03-27 22:47:52','9e916c1b',3),(14,'Jake',1,'2019-03-27 22:59:37','9e916c1b',2),(15,'Jake',1,'2019-03-27 23:21:53','9e916c1b',3),(16,'jake',1,'2019-03-27 23:28:12','9e916c1b',3),(17,'Jake',1,'2019-03-27 23:41:22','9e916c1b',4),(18,'test',1,'2019-03-27 23:45:08','9e916c1b',5),(19,'jake',1,'2019-03-28 00:01:34','9e916c1b',3),(20,'Jake',1,'2019-03-28 00:02:44','Jake',5),(21,'Jake',1,'2019-03-28 00:08:48','Jake',5),(22,'fupa',1,'2019-03-28 00:09:26','9e916c1b',5),(23,'folder',1,'2019-03-28 00:11:18','9e916c1b',5),(24,'Jake',1,'2019-03-28 12:48:09','9e916c1b',3),(25,'Jake',1,'2019-03-28 13:28:12','9e916c1b',3),(26,'jake',1,'2019-03-28 14:14:52','9e916c1b',3),(27,'Jake',1,'2019-03-28 15:10:57','9e916c1b',3),(28,'jake',1,'2019-03-28 15:16:30','9e916c1b',3),(29,'420Blaze',1,'2019-03-28 15:20:26','9e916c1b',3);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `vehicles`
--

DROP TABLE IF EXISTS `vehicles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vehicles` (
  `VehID` int(11) NOT NULL AUTO_INCREMENT,
  `Brand` varchar(25) NOT NULL,
  `Model` varchar(25) NOT NULL,
  `Reg` varchar(25) DEFAULT NULL,
  `AdditionalInfo` varchar(250) DEFAULT NULL,
  `PIN` int(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`VehID`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vehicles`
--

LOCK TABLES `vehicles` WRITE;
/*!40000 ALTER TABLE `vehicles` DISABLE KEYS */;
INSERT INTO `vehicles` VALUES (2,'Jaguar','I-Pace',NULL,NULL,4),(3,'Tesla','Model S',NULL,NULL,4),(4,'Tesla','Model E',NULL,NULL,4),(5,'Tesla','Model X',NULL,NULL,4),(6,'Tesla','Model Y',NULL,NULL,4);
/*!40000 ALTER TABLE `vehicles` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-04-01  8:44:01
