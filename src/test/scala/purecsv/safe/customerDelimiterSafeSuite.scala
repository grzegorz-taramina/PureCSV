/* Copyright 2015 Mario Pastorelli (pastorelli.mario@gmail.com)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package purecsv.safe

import java.io.CharArrayReader
import java.nio.file.Files

import purecsv.safe._
import purecsv.safe.tryutil._
import org.scalatest.{FunSuite, Matchers}
import purecsv.safe.converter.defaults.string.Trimming.NoAction

import scala.util.Success

class customerDelimiterSafeSuite extends FunSuite with Matchers {
  case class Event(ts: Long, msg: String, user: Option[Int])
  private val events = Seq(Event(1,"foo",None),Event(2,"bar",Some(1)))
  private val rawEvents = Seq("1|\"foo\"|","2|\"bar\"|1")

  test("Converting an iterable of events to CSV lines works") {
    events.toCSVLines("|").toSeq should contain theSameElementsInOrderAs rawEvents
  }

  test("Reading events from a String reader works") {
    val reader = new CharArrayReader(rawEvents.mkString(System.lineSeparator()).toCharArray)
    CSVReader[Event].readCSVFromReader(reader, '|', NoAction, true, Nil).toSeq should contain theSameElementsInOrderAs events.map(Success(_))
  }

  test("Reading events and get successes and failures works") {
    val reader = new CharArrayReader(rawEvents.mkString(System.lineSeparator()).toCharArray)
    val (successes,failures) = CSVReader[Event].readCSVFromReader(reader, '|', NoAction, true, Nil).getSuccessesAndFailures
    val expectedSuccesses = Seq(1 -> events(0), 2 -> events(1))
    successes should contain theSameElementsInOrderAs expectedSuccesses
    failures should be (Seq.empty[Event])
  }

  test("Can read a file written with writeCSVToFile") {
    val file = Files.createTempFile("casecsv",".csv").toFile
    events.writeCSVToFile(file, "☃")
    CSVReader[Event].readCSVFromFile(file, '☃', skipHeader = true) should contain theSameElementsInOrderAs events.map(Success(_))
  }
}
