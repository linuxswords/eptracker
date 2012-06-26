package models

import play.api.db._
import play.api.Play.current

import anorm._
import scala.Long
import org.joda.time.DateTime
import com.googlecode.mapperdao._
import com.googlecode.mapperdao.Query._
import utils.Setup

/**
 *
 * @author knm
 */




/**
 * Helper for pagination.
 */
case class Page[A](items: Seq[A], page: Int, offset: Long, total: Long) {
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
  lazy val totalPages = Option(total / items.size.toLong).filter(_>= 0)
}



case class Media(publishingDate: DateTime, author:Option[String], title: String,
subtitle : Option[String], identifier: String, description : Option[String],
var consumed : Boolean = false, showId: String)


object MediaEntity extends Entity[LongId, Media]{
  val id = key("id") autogenerated (_.id)
  val publishingDate = column("publishingDate") to (_.publishingDate)
  val author = column("author") option (_.author)
  val title = column("title") to  (_.title)
  val subtitle = column("subtitle") option  (_.subtitle)
  val identifier = column("identifier") to (_.identifier)
  val description = column("description") option (_.description)
  val consumed = column("consumed") to (_.consumed)
  val showId = column("showId") to (_.showId)

  def constructor(implicit m: ValuesMap) =
  new Media(publishingDate, author, title, subtitle, identifier, description, consumed, showId) with LongId with Persisted  {
    val id: Long = MediaEntity.id
  }
}






object Media {

val dataSource = play.db.DB.getDataSource("default")
  val(jdbc, mapperDao, queryDao, transcationManager) = Setup.mysql(dataSource, List(MediaEntity, TVShowEntity))
  val me = MediaEntity
  val ts = TVShowEntity

   def findById(id:Long): Option[Media] = {
     queryDao.querySingleResult(select from me where me.id === id)
   }


  def shows(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%"): Page[TVShow] = {
    val offset = pageSize * page
    val shows = queryDao.query(QueryConfig(offset = Some(offset), limit = Some(pageSize)), select from ts)
    val totalRows = queryDao.count(select from ts)

    Page(shows, page, offset, totalRows)
  }

  def all(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%"): Page[(Media)] = {
    val offset = pageSize * page
    val query: OrderBy[Builder[LongId, Media]] with Builder[LongId, Media] = select from me orderBy(me.title, asc)
    val medias = queryDao.query(QueryConfig(offset = Some(offset), limit = Some(pageSize)), query)
    val totalRows = queryDao.count(query)

    Page(medias, page, offset, totalRows)
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
    val query = select from me where me.title === title orderBy(me.publishingDate)
    val medias = queryDao.query(QueryConfig(offset = Some(offset), limit = Some(pageSize)), query)
    val totalRows = queryDao.count(query)

    Page(medias, page, offset, totalRows)
  }

  def recent(limit : Int = 4) : Seq[Media] =  {
    val medias = queryDao.query(QueryConfig(limit = Some(limit)), select from me where me.publishingDate < DateTime.now  orderBy (me.publishingDate, desc))
    medias
  }

  def consume(id:String, title:String, consume:Boolean) = {
    val medias = queryDao.querySingleResult(select from me where me.identifier === id and me.title === title)
    medias foreach { media =>
      media.consumed = consume
      mapperDao.update(MediaEntity, media)
      if(consume){
        val updates = queryDao.query(select from me where me.title === title and me.publishingDate < media.publishingDate)
        updates foreach { up =>
          up.consumed = consume
          mapperDao.update(MediaEntity, up)
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
    queryDao.query(QueryConfig(limit = Some(limit)), select from me where me.publishingDate >= DateTime.now orderBy (me.publishingDate, asc))
  }

  def delete(title: String) = {
    val medias = queryDao.query(select from me where me.title === title)
    medias.map{ mapperDao.delete(me, _) }
  }
}

case class TVShow(title : String, count : Long)

object TVShowEntity extends SimpleEntity[TVShow]{
  val title = key("title") to (_.title)
  val count = column("count") to (_.count)

  def constructor(implicit m: ValuesMap) =
    new TVShow(title, count) with Persisted  {
    }
}
