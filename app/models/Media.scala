package models

import java.util.Date
import play.api.db.DB


import java.util.{Date}

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._
import scala.Long

/**
 *
 * @author knm
 */
case class Media(id: Pk[Long] = NotAssigned,publishingDate: Date, author:Option[String] = null, title: String,
  subtitle : Option[String] = null, identifier: Option[String], description : Option[String] = null,
  var consumed : Boolean = false, showId: Option[String], children : Seq[Media] = Nil
             )

case class TShow(title : String, count : Long)

/**
 * Helper for pagination.
 */
case class Page[A](items: Seq[A], page: Int, offset: Long, total: Long) {
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
  lazy val totalPages = Option(total / items.size.toLong).filter(_>= 0)
}

object TShow {
  val simple = {
    get[String]("title") ~
    get[Long]("count") map {
      case title~count => TShow(title, count)
    }
  }
}

object Media {
  val simple = {
    get[Pk[Long]]("media.id") ~
    get[Option[String]]("media.author") ~
    get[String]("media.title") ~
    get[Option[String]]("media.subtitle") ~
    get[Option[String]]("media.identifier") ~
    get[Option[String]]("media.description") ~
    get[Boolean]("media.consumed") ~
    get[Date]("media.publishingDate") ~
    get[Option[String]]("media.showid") map {
      case id~author~title~subtitle~identifier~description~consumed~publishingDate~showId => Media(id, publishingDate, author, title, subtitle, identifier, description, consumed, showId)
    }
  }

   def findById(id:Long): Option[Media] = {
     DB.withConnection{ implicit connection =>
       SQL("select * from media where id = {id}").on('id -> id).as(Media.simple.singleOpt)
     }
   }

//  def all: List[Media] = {
//    DB.withConnection{ implicit connection =>
//      SQL("select * from media").as(simple *)
//    }
//  }
//  )

  def shows(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%"): Page[TShow] = {
    val offset = pageSize * page

    DB.withConnection { implicit connection =>
      val shows = SQL(
        """
      select title, count(*) as count
      from media
      group by title
      order by {orderBy} nulls last
      limit {pageSize} offset {offset}
      """).on(
        'pageSize -> pageSize,
        'offset -> offset,
        'orderBy -> orderBy
      ).as(TShow.simple *)

      val totalRows = SQL(
        """
      select distinct(title) from media
      """
      )().map(row => row[String]("title")).toList.size



      Page(shows, page, offset, totalRows)
    }
  }

  def all(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%"): Page[(Media)] = {
    val offset = pageSize * page

    DB.withConnection { implicit connection =>
      val medias = SQL(
        """
      select * from media
      order by {orderBy} nulls last
      limit {pageSize} offset {offset}
      """).on(
        'pageSize -> pageSize,
        'offset -> offset,
        'orderBy -> orderBy
      ).as(simple *)

      val totalRows = SQL(
        """
          select count(*) from media
        """
      ).as(scalar[Long].single)

      Page(medias, page, offset, totalRows)
    }
  }

  /**
   * Get Shows for a title
   *
   * @param title
   * @param page
   * @param pageSize
   * @param orderBy
   * @return
   */
  def show(title: String, page: Int = 0, pageSize: Int = 10, orderBy: Int = 1): Page[(Media)] = {
    val offset = pageSize * page

    DB.withConnection { implicit connection =>
      val medias = SQL(
        """
      select * from media
      where title = {title}
      order by publishingDate nulls last
      limit {pageSize} offset {offset}
      """).on(
        'title -> title,
        'pageSize -> pageSize,
        'offset -> offset,
        'orderBy -> orderBy
      ).as(simple *)

      val totalRows = SQL(
              """
            select count(*) from media
            where title = {title}
            """)
        .on('title -> title)
        .as(scalar[Long].single)

      Page(medias, page, offset, totalRows)
    }
  }

  def recent(limit : Int = 4) : Seq[Media] =  {
    DB.withConnection{ implicit connection =>
    val medias = SQL(
      """
      select * from media
      where publishingDate < {today}
      order by publishingDate desc nulls last
      limit {limit}
      """).on(
      'today -> new java.util.Date(),
      'limit -> limit
    ).as(simple *)
    medias
    }
  }

  def consume(id:Long, consume:Boolean) = {
    DB.withConnection{ implicit connection  =>
      SQL(
        """
        update media
        set consumed  = {consume}
        where id = {id}
        """
      ).on(
        'consume -> consume,
        'id -> id
      ).executeUpdate()

      if(consume)
      {
        DB.withConnection{ implicit connection =>
          SQL("""
          update media
          set consumed = 1
          where title = (select title from media where id = {id})
          and publishingDate < (select publishingDate from media where id = {id})
        """).on(
            'id -> id
          ).executeUpdate()
        }
      }
    }
  }

  def consumeAll(title:String) = {
    DB.withConnection{ implicit connection  =>
      SQL(
      """
      update media
      set consumed  = 1
      where title = {title}
      """
      ).on(
      'title -> title
      ).executeUpdate()
    }
  }

  def upcoming(limit : Int = 4) : Seq[Media] =  {
    DB.withConnection{ implicit connection =>
    val medias = SQL(
      """
      select * from media
      where publishingDate >= {today}
      order by publishingDate asc nulls last
      limit {limit}
      """).on(
      'today -> new java.util.Date(),
      'limit -> limit
    ).as(simple *)
    medias
    }
  }

  def delete(title: String) = {
    DB.withConnection{ implicit connection =>
      SQL("""
      delete from media
      where title = {title}
      """)
      .on('title -> title).execute()
    }
  }
}
