package views

import java.text.DecimalFormatSymbols
import java.util.Locale

/**
  * Created by seriousdron on 08.10.17.
  */
object EgaisFloat {
  private val formatter = new java.text.DecimalFormat("#########0.0000", new DecimalFormatSymbols(Locale.ROOT))
  formatter.setMaximumIntegerDigits(Int.MaxValue)
  formatter.setMinimumIntegerDigits(1)

  def format(value: Float): String = formatter.format(value)
  def format(value: Double): String = formatter.format(value)
}
