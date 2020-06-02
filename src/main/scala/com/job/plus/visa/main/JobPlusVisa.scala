package com.job.plus.visa.main
import scala.sys.process
import scala.util.Properties

object JobPlusVisa extends App {

//  Experience level: Student, Junior, Mid-Level, Senior, Lead, Manager
//  Role: "Backend developer", "Data Scientist", "Data Administrator", "Designer", "Desktop Developer", "DevOps", "Embeded Developer", "Frontend Developer", "Full Stack Developer", "Graphics/Game Developer", "Mobile Developer", "Product Manager", "QA/Test Developer", "System Administrator"
//  Job Type: Full Time, Contract, Internship
  
  val experience_level = List(
                                "Student", 
                                "Junior", 
                                "Mid-Level", 
                                "Senior", 
                                "Lead", 
                                "Manager"
                              )
  val job_type = List("Full Time", "Contract", "Internship")
  val role =     List("Backend Developer",
                      "Data Scientist",
                      "Database Administrator",
                      "Designer",
                      "Desktop Developer",
                      "DevOps Developer",
                      "Embeded Developer",
                      "Frontend Developer",
                      "Full Stack Developer",
                      "Graphics/Game Developer",
                      "Mobile Developer",
                      "Product Manager",
                      "QA/Test Developer",
                      "System Administrator"
                    )
 // https://stackoverflow.com/jobs/feed?v=true&dr=BackendDeveloper
  
  val url = "https://stackoverflow.com/jobs/feed?v=true"
  val combinations = JpvUtils.generator(List(experience_level, job_type, role))

////Heroku compatibility
//  val server_port = Properties.envOrElse("PORT", "8080").toInt
//  val server_host = Properties.envOrElse("SERVER_URL", "0.0.0.0")
  
  
  val connection = Datasource.connectionPool.getConnection
    var inc = 0
  for (combo <- combinations) {
    inc = inc +1 
    val experienceLevel = combo(0)
    val jobType = combo(1)
    val roleType = combo(2)
    val gen_url = JpvUtils.urlGenerator(combo,url)
    println("checking combo #" + inc)

    AtomFeed(gen_url, experienceLevel, jobType, roleType,connection)
  }

  println("Job finished")
  connection.close()
}
