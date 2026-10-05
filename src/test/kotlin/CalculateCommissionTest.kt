import org.junit.Assert.*
import org.junit.Test
import ru.netology.calculateCommission

class CalculateCommissionTest {

    // Тестирование карт Mastercard / Maestro

    @Test
    //
    fun `mastercard amount is within the allowed range, month limit has not been exceeded`() {
        assertEquals(0.0, calculateCommission("Mastercard", 0, 1000)!!, 0.001)
    }

    @Test
    fun `mastercard amount less than 300 rubles`() {
        // 200 * 0.006 + 20 = 21.2
        // Проверка работы CI. Выставим заранее некорректное значение в тесте, для того чтобы сломать сборку и увидеть это на GitHub
        assertEquals(100.0, calculateCommission("Mastercard", 0, 200)!!, 0.001)
    }

    @Test
    fun `mastercard limit of 75,000 exceeded, month limit has not been exceeded`() {
        // (120000 - 75000) * 0.006 + 20 = 290.0
        assertEquals(290.0, calculateCommission("Mastercard", 0, 120_000)!!, 0.001)
    }

    @Test
    fun `maestro with previous transfers in month, month limit has not been exceeded`() {
        // остаток 10000, перевод 20000, превышение 10000 -> 10000*0.006+20 = 80.0
        assertEquals(80.0, calculateCommission("Maestro", 65_000, 20_000)!!, 0.001)
    }

    // Тестирование карт Visa и Мир

    @Test
    fun `misa small transfer uses minimum 35 rub`() {
        assertEquals(35.0, calculateCommission("Visa", 0, 1000)!!, 0.001)
    }

    @Test
    fun `visa large transfer uses percent`() {
        // 50000 * 0.0075 = 375.0
        assertEquals(375.0, calculateCommission("Visa", 0, 50_000)!!, 0.001)
    }

    @Test
    fun `mir uses same rules as visa`() {
        assertEquals(35.0, calculateCommission("Мир", 0, 1000)!!, 0.001)
        assertEquals(750.0, calculateCommission("Мир", 0, 100_000)!!, 0.001)
    }

    // ---------- VK Pay ----------

    @Test
    fun `vkpay always free within limits`() {
        assertEquals(0.0, calculateCommission("VKPay", 0, 10_000)!!, 0.001)
    }

    @Test
    fun `vkpay blocked above daily limit`() {
        assertNull(calculateCommission("VKPay", 0, 20_000))
    }

    @Test
    fun `vkpay blocked above monthly limit`() {
        assertNull(calculateCommission("VKPay", 30_000, 15_000))
    }

    // ---------- Общие лимиты ----------

    @Test
    fun `card blocked above daily limit`() {
        assertNull(calculateCommission("Visa", 0, 200_000))
    }

    @Test
    fun `card blocked above monthly limit`() {
        assertNull(calculateCommission("Visa", 550_000, 100_000))
    }

    // ---------- Неизвестная карта ----------

    @Test
    fun `unknown card throws exception`() {
        assertThrows(IllegalArgumentException::class.java) {
            calculateCommission("Unknown", 0, 1000)
        }
    }
}