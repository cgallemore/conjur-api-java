package net.conjur.api.support

import scala.util.{Success, Failure, Try}
import java.io.FileInputStream
import scala.collection.mutable
import net.conjur.api._


case class BadTestEnv(msg:String) extends Exception(msg)
object NoCredentials extends BadTestEnv("No credentials set in system properties")

trait ConjurEnvLike {
  def credentialsOption:Option[Credentials]
  def credentials = credentialsOption.getOrElse(throw NoCredentials)
  def stack:String
  def account:String
  def endpoints   = Endpoints getHostedEndpoints (stack, account)
  override def toString = s"Env{stack=$stack,account=$account,credentials=$credentialsOption}"
}

trait HasConjurEnv {
  def env:ConjurEnvLike = ConjurEnv
}

/**
 * Test environment, loaded from system properties + a conjur.properties resource
 * + conjur.properties in your home directory, as well as any CONJUR_XYZ environment
 * variables
 *
 * More of a "playing around with scala" thing than anything :-)
 */
object ConjurEnv extends Log with ConjurEnvLike {
  private val conjurProp = "net\\.conjur\\.api\\.(.+)".r
  private val conjurVar = "CONJUR_(.+)".r
  private var loaded = false

  private def load = {
    Seq(
      Option(getClass.getResource("/conjur.properties")).map(_.getPath),
      sys.env.get("HOME").map(_ + "/conjur.properties"),
      sys.props.get("net.conjur.api.config")
    ).foreach(_.foreach(loadPath))
    loaded = true
    // super important for this to come *after* loaded=true O.o
    log info s"loaded test environment $this"
  }

  private def loadPath(path:String) = {
    Try{ System.getProperties.load(new FileInputStream(path))  } match {
      case Failure(ex) => log.warn(s"Unable to load conjur properties from $path")
      case Success(_) => log.info(s"Loaded conjur test properties from $path")
    }
  }


  private def envProps = {
    sys.env collect {
      case (conjurVar(rest), value) => rest.toLowerCase -> value
    }
  }

  private lazy val loadedProps = {
    if(!loaded) load
    sys.props
  }

  protected override def logName = "ConjurEnv"

  private lazy val sysProps = loadedProps collect {
    case (conjurProp(rest), value) if value != "null" => rest -> value
  }

  lazy val props = sysProps ++ envProps

  def stackFor(account:String) = account match {
    case "ci" => "ci"
    case _ => "v3"
  }

  def credentialsOption = props.get("credentials").map(Credentials.fromString(_))
  def account = props getOrElse("account", "sandbox")
  def stack = props getOrElse ("stack", stackFor(account))
}
