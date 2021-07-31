package com.job.plus.visa.main

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

object JpvUtils {

  def generator(x: List[List[String]]): List[List[String]] = x match {
    case Nil    => List(Nil)
    case h :: _ => h.flatMap(i => generator(x.tail).map(i :: _))
  }
  def urlGenerator(req:List[String], url:String): String = {
    val job_slug = req(1).replaceAll(" ", "")
    val role_slug = req(2).replaceAll(" ", "")
    val filtered_url = url + "&ms=" + req(0) + "&j=" + job_slug + "&dr=" + role_slug
    filtered_url
  }
  
  def linkExtractor(url:String): String ={
    val html = url
    val doc: Document = Jsoup.connect(html).get()
    val body4 = doc.body()
    var link = body4.getElementsByClass("s-btn s-btn__primary grid--cell sm:fl1 ta-center s-btn__md w100 _apply _url js-apply js-url-apply d-block")
    if (link.isEmpty) {
      link = body4.getElementsByClass("s-btn s-btn__primary flex--item sm:fl1 ta-center  _apply js-apply d-block")
    }
    link.attr("href")
  }
}
