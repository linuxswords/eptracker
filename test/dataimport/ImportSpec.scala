package dataimport

import org.specs2.mutable.Specification


/**
 *
 * @author knm
 */

class ImportSpec extends Specification {

  // set to ignore since paralell insertion into the database
  // cannot be handled be FakeApplication() and
  // FakeApplication(additionalConfiguration = inMemoryDatabase())


//  "Import of" should {
//
//    running(FakeApplication()) {
//      "Chuck should succeed" in {
//
//        val title = Import.importShowIntoDb("Chuck")
//
//        val pages = Media.show(title).totalPages
//        pages must beSome
//
//      }
//
//      "asdf should succeed" in {
//
//        val title = Import.importShowIntoDb("asdf")
//
//        val pages = Media.show(title).totalPages
//        pages must beNull
//      }
//
//
//    }
//
//  }
}
