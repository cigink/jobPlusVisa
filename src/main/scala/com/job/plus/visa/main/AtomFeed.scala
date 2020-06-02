package com.job.plus.visa.main

import java.net.URL
import java.sql.Connection
import java.text.SimpleDateFormat
import java.util.{Calendar, Locale, TimeZone}

import com.rometools.rome.feed.synd.SyndFeed
import com.rometools.rome.io.{SyndFeedInput, XmlReader}
import org.apache.commons.lang3.StringEscapeUtils
import org.jdom2.Element

import scala.collection.JavaConverters.asScala

case class AtomFeed (url:  String,
                     experience: String,
                     jobType:String,
                     roleType:String,
                     connection:Connection) {

  // defaults
  val post_author: BigInt = 1
  val post_status: String = "publish"
  val comment_status = "close"
  val ping_status = "close"
  val post_type = "noo_job"
  val comment_count = 0
  
  var entryCout = 0

  // Parsing and formatting time
  val sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.US)
  val sdfGMT = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.US)
                                  sdfGMT.setTimeZone(TimeZone.getTimeZone("GMT"))
  val sdfUpdate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
  val sdfUpdateGMT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                    sdfUpdateGMT.setTimeZone(TimeZone.getTimeZone("GMT"))
  val outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

  val checkerDateFormat = new SimpleDateFormat("yyyy-MM-dd")

  // Reading the atom feed
  val feedUrl = new URL(url)
  val input = new SyndFeedInput
  val feed: SyndFeed = input.build(new XmlReader(feedUrl))

  // Iterating through each entry of the feed
  val entries = asScala(feed.getEntries).toVector
  for (entry <- entries) {

    // post date
    val date = sdf.parse(entry.getPublishedDate.toString)
    val date_gmt = sdfGMT.parse(entry.getPublishedDate.toString)
    val post_date = outputFormat.format(date)
    val post_date_gmt = outputFormat.format(date_gmt)

    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, -2)
    val today = checkerDateFormat.format(calendar.getTime())
    
    
//    if (checkerDateFormat.format(date_gmt) >=(today)) {

    //id
    val id = entry.getUri.toLong
    
    //post_title
    val post_title = entry.getTitle
    
    //post_name
    val post_name = post_title.replaceAll("[^a-zA-Z\\d ]", "").toLowerCase
      .replaceAll("\\s+", " ").replaceAll(" ", "-")
    
    //guid url
    val guid = "http://jobplusvisa.com/?post_type=noo_job&p=" + id

    //post_content
    val text = entry.getDescription.getValue
    val post_content = StringEscapeUtils.unescapeHtml4(text).replaceAll("<br />", "")

    //location
    var place:String = null
    val locations = asScala(entry.getForeignMarkup).toVector
    for (location: Element <- locations if location.getName.contains("location")) {
      place = location.getValue()
    }
      //job tags
      val job_tags = asScala(entry.getCategories).toVector
      val tags =  job_tags.map(category => category.getName).toList

      //post update date
      var post_modified:String = null
      var post_modified_gmt:String = null
      val updated = asScala(entry.getForeignMarkup).toVector
      for (update: Element <- updated if update.getName.contains("updated")) {
        val modified = sdfUpdate.parse(update.getValue)
        val modified_gmt = sdfUpdateGMT.parse(update.getValue)
        post_modified = outputFormat.format(modified)
        post_modified_gmt = outputFormat.format(modified_gmt)
      }

    
    // company name
    var company_name: String = null
    val authors = asScala(entry.getForeignMarkup).toVector
    for (author: Element <- authors if author.getName.contains("author")) {
      company_name = author.getValue
    }
    
    //links
    val stackUrl = entry.getLink
    
    entryCout = entryCout + 1
    
    println("Checking in db entry #"+ entryCout)


    DatabseWriter(id, post_author, post_date, post_date_gmt, post_content, post_title,
      post_status, comment_status, ping_status, post_name, post_modified, post_modified_gmt, guid, post_type,
      comment_count, place, tags, experience, jobType, roleType, company_name, stackUrl, connection)

  }
//  }
}
