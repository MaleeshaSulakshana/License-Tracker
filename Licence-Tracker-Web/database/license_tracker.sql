-- phpMyAdmin SQL Dump
-- version 5.1.0
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 30, 2022 at 02:53 PM
-- Server version: 10.4.19-MariaDB
-- PHP Version: 7.4.19

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `license_tracker`
--

-- --------------------------------------------------------

--
-- Table structure for table `driver`
--

CREATE TABLE `driver` (
  `id` int(255) NOT NULL,
  `first_name` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  `nic` varchar(255) NOT NULL,
  `mobile` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `psw` varchar(255) NOT NULL,
  `nic_image` varchar(255) NOT NULL,
  `license_image` varchar(255) NOT NULL,
  `passport_image` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `driver`
--

INSERT INTO `driver` (`id`, `first_name`, `last_name`, `nic`, `mobile`, `email`, `psw`, `nic_image`, `license_image`, `passport_image`) VALUES
(2, 'Yasidu', 'Abesinghe', '990511497V', '0767372872', 'yasidu@gmail.com', '25d55ad283aa400af464c76d713c07ad', '2_driver.png', '', ''),
(3, 'senuri', 'navindya', '985953503v', '0715820798', 'senuri@gmail.com', '25d55ad283aa400af464c76d713c07ad', '3_driver.png', '', '');

-- --------------------------------------------------------

--
-- Table structure for table `fine`
--

CREATE TABLE `fine` (
  `id` int(255) NOT NULL,
  `driver_id` int(255) NOT NULL,
  `policeman_id` int(255) NOT NULL,
  `date_time` date NOT NULL,
  `wrong_details` varchar(255) NOT NULL,
  `amount` varchar(255) NOT NULL,
  `location` varchar(255) NOT NULL,
  `vehicle_no` varchar(255) NOT NULL,
  `status` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `fine`
--

INSERT INTO `fine` (`id`, `driver_id`, `policeman_id`, `date_time`, `wrong_details`, `amount`, `location`, `vehicle_no`, `status`) VALUES
(1, 2, 2, '2022-05-29', 'avoid color lites', '2500', 'Homagama', 'ND-6785', 'paid'),
(2, 2, 2, '2022-05-30', 'Cross double line ', '100', 'Kottawa', 'BAL-6677', 'unpaid'),
(3, 2, 2, '2022-05-30', 'vehicale issue', '1000', 'anuradhapura', 'KV 9278', 'unpaid');

-- --------------------------------------------------------

--
-- Table structure for table `policeman`
--

CREATE TABLE `policeman` (
  `id` int(255) NOT NULL,
  `police_id` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `psw` varchar(255) NOT NULL,
  `mobile` varchar(255) NOT NULL,
  `designation` varchar(255) NOT NULL,
  `status` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `policeman`
--

INSERT INTO `policeman` (`id`, `police_id`, `name`, `email`, `psw`, `mobile`, `designation`, `status`) VALUES
(2, '15648448', 'Yasindu Abesinghe', 'yasindu@gmail.com', '25d55ad283aa400af464c76d713c07ad', '0767372872', 'OIC', 'activated');

-- --------------------------------------------------------

--
-- Table structure for table `system_users`
--

CREATE TABLE `system_users` (
  `id` int(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `psw` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `system_users`
--

INSERT INTO `system_users` (`id`, `name`, `email`, `psw`) VALUES
(4, 'Yasidu', 'yasidu@gmail.com', '25d55ad283aa400af464c76d713c07ad');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `driver`
--
ALTER TABLE `driver`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `fine`
--
ALTER TABLE `fine`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `policeman`
--
ALTER TABLE `policeman`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `system_users`
--
ALTER TABLE `system_users`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `driver`
--
ALTER TABLE `driver`
  MODIFY `id` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `fine`
--
ALTER TABLE `fine`
  MODIFY `id` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `policeman`
--
ALTER TABLE `policeman`
  MODIFY `id` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `system_users`
--
ALTER TABLE `system_users`
  MODIFY `id` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
