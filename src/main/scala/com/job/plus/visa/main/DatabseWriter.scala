package com.job.plus.visa.main

import java.sql.{Connection, PreparedStatement}

import org.apache.commons.lang3.StringUtils

case class DatabseWriter (
                      post_id:Long,
                      post_author: BigInt = 0,
                      post_date:String,
                      post_date_gmt:String,
                      post_content:String,
                      post_title:String,
                      post_status: String = "publish",
                      comment_status:String= "closed",
                      ping_status:String = "closed",
                      post_name:String,
                      post_modified:String,
                      post_modified_gmt:String,
                      guid:String,
                      post_type :String= "noo_job",
                      comment_count :Int= 0,
                      location:String,
                      tags:List[String],
                      experience: String,
                      jobType:String,
                      roleType:String,
                      companyName:String,
                      stackUrl: String,
                      connection: Connection
                    ) {
  
  try {
    
    var post_id = findPostId()
    if(post_id == 0) {
      insertPost()
      val link = JpvUtils.linkExtractor(stackUrl)
      post_id = findPostId()
      insertPostMeta(post_id, "_noo_job_field_job_level", experience)
      insertPostMeta(post_id, "_noo_job_field_company_name", companyName)
      insertPostMeta(post_id, "_custom_application_url", link)
      insertPostMeta(post_id, "_noo_job_count", "0")
    }
    
    
    //inserting into term job_location
    var location_term_id = findTermId(location)
    if(location_term_id == 0) {
      insertWpTerms(location)
      location_term_id = findTermId(location)
    }

    //inserting job_location into taxonomy
    var locationTaxonomyId = findTaxonomyId(location_term_id, "job_location")
    if (locationTaxonomyId == 0) {
      println("inserting location taxonomy" + location)
      insertTaxonomyId(location_term_id, "job_location")
      locationTaxonomyId = findTaxonomyId(location_term_id, "job_location")
    }
    
    // inserting location into relationship table
    val location_taxonomy_relation = findTaxonomyRelationId(post_id,locationTaxonomyId)
    if(location_taxonomy_relation == 0) {
      insertTermRelationship(locationTaxonomyId, post_id)
      updateTaxonomyTable(location_term_id, "job_location")
    }

    //inserting job_type into term
    var jobType_term_id = findTermId(jobType)
    if(jobType_term_id == 0) {
      insertWpTerms(jobType)
      jobType_term_id = findTermId(jobType)
    }

    //inserting job_type into taxonomy
    var jobTypeTaxonomyId = findTaxonomyId(jobType_term_id, "job_type")
    if (jobTypeTaxonomyId == 0) {
      insertTaxonomyId(jobType_term_id, "job_type")
      jobTypeTaxonomyId = findTaxonomyId(jobType_term_id, "job_type")
    }

    // inserting job_type into relationship table
    val jobType_taxonomy_relation = findTaxonomyRelationId(post_id,jobTypeTaxonomyId)
    if(jobType_taxonomy_relation == 0) {
      insertTermRelationship(jobTypeTaxonomyId, post_id)
      updateTaxonomyTable(jobType_term_id, "job_type")
    }

    
    //inserting role_type into term
    var roleType_term_id = findTermId(roleType)
    if(roleType_term_id == 0) {
      insertWpTerms(roleType)
      roleType_term_id = findTermId(roleType)
    }

    //inserting job_type into taxonomy
    var roleTypeTaxonomyId = findTaxonomyId(roleType_term_id, "job_category")
    if (roleTypeTaxonomyId == 0) {
      insertTaxonomyId(roleType_term_id, "job_category")
      roleTypeTaxonomyId = findTaxonomyId(roleType_term_id, "job_category")
    }

    // inserting job_type into relationship table
    val roleType_taxonomy_relation = findTaxonomyRelationId(post_id,roleTypeTaxonomyId)
    if(roleType_taxonomy_relation == 0) {
      insertTermRelationship(roleTypeTaxonomyId, post_id)
      updateTaxonomyTable(roleType_term_id, "job_category")
    }

    
    //inserting job_tags into term
    tags.foreach(jobTag => {
      var jobTag_term_id = findTermId(jobTag)
      if(jobTag_term_id == 0) {
        insertWpTerms(jobTag)
        jobTag_term_id = findTermId(jobTag)
      }

      //inserting job_type into taxonomy
      var jobTagTaxonomyId = findTaxonomyId(jobTag_term_id, "job_tag")
      if (jobTagTaxonomyId == 0) {
        insertTaxonomyId(jobTag_term_id, "job_tag")
        jobTagTaxonomyId = findTaxonomyId(jobTag_term_id, "job_tag")
      }

      // inserting job_type into relationship table
      val jobTag_taxonomy_relation = findTaxonomyRelationId (post_id, jobTagTaxonomyId)
      if(jobTag_taxonomy_relation == 0) {
        insertTermRelationship (jobTagTaxonomyId, post_id)
        updateTaxonomyTable(jobTag_term_id, "job_tag")
      }
    })
  }

  def findPostId(): Long ={
    val selectSql = "SELECT ID FROM wp_posts where ID = ?"
    val preparedStmt2: PreparedStatement = connection.prepareStatement(selectSql)
    preparedStmt2.setLong (1, post_id)
    val rs = preparedStmt2.executeQuery()
    val id =
      if(rs.next())
      {
        rs.getLong("ID")
      }
      else {
        0
      }
    id
  }
  
  def insertPost(): Unit = {
    val insertSql = """
                      |INSERT into wp_posts (ID,post_author,post_date,post_date_gmt,post_content,post_title,post_excerpt,
                      |  post_status,comment_status,ping_status,post_password,post_name,to_ping,pinged,
                      |  post_modified,post_modified_gmt,post_content_filtered,post_parent,guid,menu_order,
                      |  post_type,post_mime_type,comment_count)
                      |VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
                """.stripMargin
    val preparedStmt: PreparedStatement = connection.prepareStatement(insertSql)

    preparedStmt.setLong (1, post_id)
    preparedStmt.setInt (2, post_author.toInt)
    preparedStmt.setString    (3, post_date.toString)
    preparedStmt.setString    (4, post_date_gmt.toString)
    preparedStmt.setString (5, post_content)
    preparedStmt.setString (6, post_title)
    preparedStmt.setString (7,"")
    preparedStmt.setString (8, post_status)
    preparedStmt.setString (9, comment_status)
    preparedStmt.setString (10, ping_status)
    preparedStmt.setString (11,"")

    preparedStmt.setString (12, post_name)
    preparedStmt.setString (13,"")
    preparedStmt.setString (14,"")
    preparedStmt.setString    (15, post_modified.toString)
    preparedStmt.setString    (16, post_modified_gmt.toString)
    preparedStmt.setString (17,"")
    preparedStmt.setInt (18,0)
    preparedStmt.setString    (19, guid)
    preparedStmt.setInt (20,0)
    preparedStmt.setString    (21, post_type)
    preparedStmt.setString (22,"")
    preparedStmt.setInt    (23, comment_count)
    
    println("post inserted " + post_id)
    preparedStmt.execute
  }
  
  def insertPostMeta(post_id: Long, metaKey:String, metaValue:String): Unit = {
    val insertMetaSql = """
                           |INSERT into wp_postmeta (post_id,meta_key,meta_value)
                           |VALUES (?,?,?)
                """.stripMargin

    val preparedStmt: PreparedStatement = connection.prepareStatement(insertMetaSql)

    preparedStmt.setLong (1, post_id)
    preparedStmt.setString (2, metaKey)
    preparedStmt.setString    (3, metaValue)
    
    preparedStmt.execute
  }

  def findTermId(term:String): Long = {
    val selectSql = "SELECT term_id FROM wp_terms where name = ?"
    val preparedStmt2: PreparedStatement = connection.prepareStatement(selectSql)
    preparedStmt2.setString (1, term)
    val rs = preparedStmt2.executeQuery()
    var id =
    if(rs.next())
    {
      rs.getInt("term_id")
    }
    else {
      0
    }
    id
  }

  def insertWpTerms(term:String): Unit = {
    val insertTermsSql = """
                           |INSERT into wp_terms (name,slug,term_group)
                           |VALUES (?,?,?)
                """.stripMargin

    val preparedStmt: PreparedStatement = connection.prepareStatement(insertTermsSql)
    val slug = term.replaceAll(",", "").replaceAll(" ", "-").toLowerCase()
      .replaceAll("[Øø]","o")
    println(slug)
    preparedStmt.setString (1, term)
    preparedStmt.setString    (2, StringUtils.stripAccents(slug))
    preparedStmt.setInt(3, 0)

    preparedStmt.execute
  }

  def findTaxonomyId(termId:Long, taxonomy:String): Long = {
    val selectSql = "SELECT term_taxonomy_id FROM wp_term_taxonomy where term_id = ? and taxonomy = ?"
    val preparedStmt2: PreparedStatement = connection.prepareStatement(selectSql)
    preparedStmt2.setLong (1, termId)
    preparedStmt2.setString (2, taxonomy)
    val rs = preparedStmt2.executeQuery()
    val id = 
    if(rs.next())
    {
      rs.getInt("term_taxonomy_id")
    }
    else {
     0
    }
    id
  }

  def insertTaxonomyId(id:Long, taxonomy:String): Unit = {
    val insertTaxonomySql =
      """
        |INSERT into wp_term_taxonomy (term_id,taxonomy,description,parent,count)
        |VALUES (?,?,?,?,?)
                """.stripMargin

    val preparedStmt: PreparedStatement = connection.prepareStatement(insertTaxonomySql)

    preparedStmt.setLong(1, id)
    preparedStmt.setString(2, taxonomy)
    preparedStmt.setString(3, "")
    preparedStmt.setInt(4, 0)
    preparedStmt.setInt(5, 0)

    preparedStmt.execute
  }

  def updateTaxonomyTable(termID: Long,taxonomy:String)= {

    val count = termCount(termID, taxonomy)
    val updateTaxonomySql = "UPDATE wp_term_taxonomy SET count =? where term_id = ? and taxonomy = ?"
    val preparedStmt: PreparedStatement = connection.prepareStatement(updateTaxonomySql)
    preparedStmt.setInt(1, count + 1)
    preparedStmt.setLong(2, termID)
    preparedStmt.setString(3, taxonomy)
    preparedStmt.execute
  }

  def termCount(termID:Long, taxonomy:String): Int = {
    val selectSql = "SELECT count FROM wp_term_taxonomy where term_id = ? and taxonomy = ?"
    val preparedStmt2: PreparedStatement = connection.prepareStatement(selectSql)
    preparedStmt2.setLong (1, termID)
    preparedStmt2.setString (2, taxonomy)
    val rs = preparedStmt2.executeQuery()
    rs.next()
    val count = rs.getInt("count")
    count
  }

  def findTaxonomyRelationId(postId:Long, termTaxonomyId: Long): Long = {
    val selectSql = "SELECT count(*) as count FROM wp_term_relationships where object_id = ? and term_taxonomy_id = ?"
    val preparedStmt2: PreparedStatement = connection.prepareStatement(selectSql)
    preparedStmt2.setLong (1, postId)
    preparedStmt2.setLong (2, termTaxonomyId)
    val rs = preparedStmt2.executeQuery()
    rs.next()
    val id = rs.getInt("count")
    id
  }

  def insertTermRelationship(taxonomyID: Long, postId: Long): Unit = {
    val taxonomyRelationCount = findTaxonomyRelationId(postId, taxonomyID)
    if(taxonomyRelationCount == 0) {
      val insertTaxonomySql = """
                                |INSERT into wp_term_relationships (object_id,term_taxonomy_id,term_order)
                                |VALUES (?,?,?)
                """.stripMargin

      val preparedStmt: PreparedStatement = connection.prepareStatement(insertTaxonomySql)

      preparedStmt.setString (1, postId.toString)
      preparedStmt.setLong(2, taxonomyID)
      preparedStmt.setInt(3, 0)

      preparedStmt.execute
    } }
  
}
