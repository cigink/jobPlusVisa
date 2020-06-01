package com.job.plus.visa.main

import org.apache.commons.dbcp2._

object Datasource {
    val dbUrl = s"jdbc:mysql://jobplusvisa.com/u338144142_OZEJA"
    val connectionPool = new BasicDataSource()

 
      connectionPool.setUsername("u338144142_TQyFm")
      connectionPool.setPassword("E8Yk8m9Ujn")
    
    connectionPool.setDriverClassName("com.mysql.jdbc.Driver")
    connectionPool.setUrl(dbUrl)
    connectionPool.setInitialSize(3)
 }

