package ru.netology

import kotlin.math.roundToInt

/**
 * Задача №1. Максимальное покрытие
 * Вам нужно взять функцию расчёта комиссии при переводе и написать для неё автотесты:
 *  *
 *  * За переводы с карты Mastercard и Maestro комиссия не взимается, при сумме перевода от 300 руб., до 75_000 руб.
 *  * в календарный месяц, в рамках акции(*). В иных случаях 0,6% + 20 руб.
 *  * За переводы с карты Visa и Мир комиссия составит 0,75%, минимальная сумма комиссии 35 руб.
 *  * За переводы по счету VK Pay комиссия не взимается.
 *  *
 *  * Кроме того, введём лимиты на суммы перевода за сутки и за месяц. Максимальная сумма перевода с одной карты:
 *  *
 *  * Максимальная сумма перевода по одной карте 150_000 руб., в сутки и 600_000 руб., в месяц раздельно на отправку и получение.
 *  * Комиссия в лимитах не учитывается.
 *  * Максимальная сумма перевода со счета VK Pay 15_000 руб., за один раз и 40_000 руб., в месяц.
 *  *
 *  * В случае превышения какого-либо из лимитов операция должна блокироваться.
 *  * Подключите JUnit4 и JaCoCo. Добейтесь того, чтобы покрытие кода по branch было не менее 80 %:
 *  * Авто-тесты также должны храниться в репозитории.
 */

fun main() {
    println(calculateCommission("Mastercard", 600_000, 100_000))
    println(calculateCommission("Maestro", 0, 100))
    println(calculateCommission("Visa", 0, 50_000))
    println(calculateCommission("Мир", 0, 300_000))
    println(calculateCommission("VKPay", 10_000, 10_000))
    println(calculateCommission("VKPay", 50_000, 10_000))
}


//Функция расчета комиссии в зависимости от типа карты и суммы перевода
fun calculateCommission(
    cardLevel: String = "VKPay", //тип карты (по умолчанию счет Вконтакте);
    sumTransferAmountToMonth: Int = 0, //сумма предыдущих переводов в этом месяце (по умолчанию 0 рублей);
    transferAmount: Int, // сумма совершаемого перевода.
): Double?
{
    //Проверяем лимит по сумме за день и в месяц
    when (cardLevel) {
        "VKPay" -> {
            if (transferAmount > VK_PAY_DAILY_LIMIT) {
                return null //Прерываем операцию, при превышении лимита за раз для VK Pay
            }
            if (sumTransferAmountToMonth + transferAmount > VK_PAY_MONTHLY_LIMIT) {
                return null //Прерываем операцию, при превышении лимита за месяц для VK Pay
            }
        }
        "Mastercard", "Maestro", "Visa", "Мир" -> {
            if (transferAmount > DAILY_LIMIT) {
                return null //Прерываем операцию, при превышении лимита за раз
            }
            if (sumTransferAmountToMonth + transferAmount > MONTHLY_LIMIT) {
                return null //Прерываем операцию, при превышении лимита за месяц
            }
        }
        //Отрабатываем исключение, если выбран неизвестный тип карты
        else -> throw IllegalArgumentException("[ $cardLevel] : Неизвестный тип карты!")
    }

    //Рассчитаем комиссию для выбранной карточки
    val commission: Double = when (cardLevel) {
        "Mastercard", "Maestro" -> {
            //Определяем условие перевода без комиссии: Если перевод от 300 руб., до 75_000 руб. "И" если сумма переводов не превысила лимит в месяц
            val isNotCommission: Boolean = transferAmount in MIN_MONTHLY_LIMIT..MAX_MONTHLY_LIMIT &&
                    transferAmount + sumTransferAmountToMonth <= MAX_MONTHLY_LIMIT
            if (isNotCommission){
                return 0.0 //Возвращаем 0 комиссии
            } else { // В иных случаях 0,6% + 20 руб.
                //Определим что сумма перевода меньше 300 рублей
                if (transferAmount < MIN_MONTHLY_LIMIT){
                    return transferAmount * COMMISSION_PERCENT_MASTER + COMMISSION_RUB_MASTER //Комиссия составит (сумма < 300 руб.) * 0,6% + 20 руб.
                } else {
                    //Определяем сумму превышения лимита
                    val freeRemaining = maxOf(0, MAX_MONTHLY_LIMIT - sumTransferAmountToMonth) //Остаток до назначения комиссии. Пример 75_000 - 0 = 75_000
                    val overLimit = maxOf(0, transferAmount - freeRemaining) //Определение превышения лимита за месяц. Пример 80_000 - 75_000 = 5_000 (превышение есть)
                    return overLimit * COMMISSION_PERCENT_MASTER + COMMISSION_RUB_MASTER //Комиссия составит 5000 * 0,6% + 20 руб., от суммы превышения лимита
                }
            }
        }
        "Visa", "Мир" -> maxOf(transferAmount * COMMISSION_PERCENT_VISA, COMMISSION_RUB_VISA) //комиссия составит 0,75% если она больше 35 руб.
        "VKPay" -> 0.0
        else -> throw IllegalArgumentException("[ $cardLevel] : Неизвестный тип карты!")
    }

    // Округление до копеек
    return (commission * 100).roundToInt() / 100.0
}

const val MAX_MONTHLY_LIMIT = 75_000          // бесплатный лимит Mastercard/Maestro в месяц
const val MIN_MONTHLY_LIMIT = 300             // минимальная сумма для акции
const val COMMISSION_PERCENT_MASTER = 0.006   // 0,6 %
const val COMMISSION_RUB_MASTER = 20.0
const val COMMISSION_PERCENT_VISA = 0.0075    // 0,75 %
const val COMMISSION_RUB_VISA = 35.0
const val DAILY_LIMIT = 150_000               // суточный лимит по карте
const val MONTHLY_LIMIT = 600_000             // месячный лимит по карте
const val VK_PAY_DAILY_LIMIT = 15_000         // лимит VK Pay за раз
const val VK_PAY_MONTHLY_LIMIT = 40_000       // лимит VK Pay в месяц