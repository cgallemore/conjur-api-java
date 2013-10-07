package net.conjur.api.support

import org.apache.commons.logging.LogFactory
import org.scalatest.{ShouldMatchers, FunSpec}
import scala.io.Source

trait Log {
  protected def logName = getClass.getName
  protected lazy val log = LogFactory.getLog(logName)
}

trait FileFixtures {
  val fixturesDir = "fixtures"

  def fixtureFile(name:String) = {
    Source.fromURL(getClass.getResource(s"/$fixturesDir/$name")).mkString
  }
}

trait AllTestSupport extends Log with HasConjurEnv with FileFixtures

trait ConjurSpec extends FunSpec with ShouldMatchers with AllTestSupport