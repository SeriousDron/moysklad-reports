package views

class MoneyInt(money: Int) {
  def moneyFormat: String = "%.2f".format(money.toDouble / 100)
}

object MoneyInt {
  implicit def intToMoneyInt(money: Int): MoneyInt = new MoneyInt(money)
}