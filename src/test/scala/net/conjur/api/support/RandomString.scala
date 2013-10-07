package net.conjur.api.support

/**
 *
 */

object RandomString {
  private lazy val ranges = Seq(
    Range('0', '9'),
    Range('A','Z'),
    Range('a','z')
  )
  private lazy val maxChar = ranges.map(_.length).fold(0)(_+_)
  private def nextInt = util.Random.nextInt(maxChar)
  def randomChar = {

  }
  def randomString(n:Int):String = List.fill(n)(randomChar).mkString
  def randomString:String = randomString(12)
  def randomString(prefix:String):String = prefix + randomString
  def randomString(prefix:String, count:Int):String = prefix + randomString(count)
}


