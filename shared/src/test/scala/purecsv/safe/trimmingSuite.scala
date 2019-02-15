package purecsv.safe

import org.scalatest.{FlatSpec, MustMatchers}
import purecsv.safe._
import purecsv.safe.converter.defaults.string.Trimming
import purecsv.safe.converter.defaults.string.Trimming.{NoAction, TrimAll, TrimEmpty}

import scala.util.Success

class trimmingSuite extends FlatSpec with MustMatchers {
  private final case class Account(id: Int, name: String, surname: Option[String])

  it should "convert string containing whitespace only to none when trimming empty" in {
    val csv = "1,some name,   "

    readCsv(csv, TrimEmpty) must contain only Success(Account(1, "some name", None))
  }

  it should "should convert given string to trimmed string when trimming all" in {
    val csv = "1,some name,    some surname    "

    readCsv(csv, TrimAll) must contain only Success(Account(1, "some name", Some("some surname")))
  }

  it should "convert return given string as it is when rimming is set to no action" in {
    val csv = "1,some name,   some surname    "

    readCsv(csv, NoAction) must contain only Success(Account(1, "some name", Some("   some surname    ")))
  }

  private def readCsv(csv: String, trimming: Trimming) =
    CSVReader[Account].readCSVFromString(csv, ',', skipHeader = false, trimming = trimming)
}