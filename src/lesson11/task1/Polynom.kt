@file:Suppress("UNUSED_PARAMETER")

package lesson11.task1

import kotlin.math.pow

/**
 * Класс "полином с вещественными коэффициентами".
 *
 * Общая сложность задания -- средняя, общая ценность в баллах -- 16.
 * Объект класса -- полином от одной переменной (x) вида 7x^4+3x^3-6x^2+x-8.
 * Количество слагаемых неограничено.
 *
 * Полиномы можно складывать -- (x^2+3x+2) + (x^3-2x^2-x+4) = x^3-x^2+2x+6,
 * вычитать -- (x^3-2x^2-x+4) - (x^2+3x+2) = x^3-3x^2-4x+2,
 * умножать -- (x^2+3x+2) * (x^3-2x^2-x+4) = x^5+x^4-5x^3-3x^2+10x+8,
 * делить с остатком -- (x^3-2x^2-x+4) / (x^2+3x+2) = x-5, остаток 12x+16
 * вычислять значение при заданном x: при x=5 (x^2+3x+2) = 42.
 *
 * В конструктор полинома передаются его коэффициенты, начиная со старшего.
 * Нули в середине и в конце пропускаться не должны, например: x^3+2x+1 --> Polynom(1.0, 2.0, 0.0, 1.0)
 * Старшие коэффициенты, равные нулю, игнорировать, например Polynom(0.0, 0.0, 5.0, 3.0) соответствует 5x+3
 */
class Polynom(vararg coeffs: Double) { // неизвестно точное количество - vararg
    val coeffList = coeffs.toList().dropWhile { it == 0.0 }.reversed() // (0.0, 0.0, 5.0, 3.0) ->  5x+3

    /**
     * Геттер: вернуть значение коэффициента при x^i
     */

    fun coeff(i: Int): Double {
        if (coeffList.isEmpty()) {
            return 0.0
        }
        if (i >= coeffList.size) {
            throw IllegalArgumentException()
        }
        return coeffList[i]
    }

    /**
     * Расчёт значения при заданном x
     */

    fun getValue(x: Double): Double {

        var result = 0.0
        var k = 0
        while (k != coeffList.size) {
            result += (coeff(k) * x.pow(k))
            k += 1
        }
        return result
    }

    /**
     * Степень (максимальная степень x при ненулевом слагаемом, например 2 для x^2+x+1).
     *
     * Степень полинома с нулевыми коэффициентами считать равной 0.
     * Слагаемые с нулевыми коэффициентами игнорировать, т.е.
     * степень 0x^2+0x+2 также равна 0.
     */
    fun degree(): Int {
        for (i in coeffList.size - 1 downTo 0) {
            if (coeffList[i] != 0.0) {
                return i
            }
        }
        return 0
    }

    /**
     * Сложение
     */
    operator fun plus(other: Polynom): Polynom {
        var result = doubleArrayOf()
        val cf = coeffList.toMutableList()
        val ocf = other.coeffList.toMutableList()
        if (other.coeffList.size > cf.size) {
            while (ocf.size != cf.size) {
                cf.add(0.0)
            }
        }
        if (ocf.size < cf.size) {
            while (ocf.size != cf.size) {
                ocf.add(0.0)
            }
        }
        var k = 0
        while (k < cf.size) {
            cf.reversed().forEach { c ->
                result += (c + ocf.reversed()[k])
                k += 1
            }
        }
        return Polynom(*result) // (1.0, -1.0, 2.0, 6.0)
    }

    /**
     * Смена знака (при всех слагаемых)
     */
    operator fun unaryMinus(): Polynom {
        var result = doubleArrayOf()
        coeffList.reversed().forEach { cf ->
            result += cf * (-1)
        }
        return Polynom(*result)
    }

    /**
     * Вычитание
     */
    operator fun minus(other: Polynom): Polynom {
        var result = doubleArrayOf()
        val cf = coeffList.toMutableList()
        val ocf = other.coeffList.toMutableList()
        if (other.coeffList.size > cf.size) {
            while (ocf.size != cf.size) {
                cf.add(0.0)
            }
        }
        if (ocf.size < cf.size) {
            while (ocf.size != cf.size) {
                ocf.add(0.0)
            }
        }
        var k = 0
        while (k < cf.size) {
            cf.reversed().forEach { c ->
                result += (c - ocf.reversed()[k])
                k += 1
            }
        }
        return Polynom(*result) // (1.0, -1.0, 2.0, 6.0)
    }

    /**
     * Умножение
     */
    operator fun times(other: Polynom): Polynom {
        val cf = coeffList.toMutableList()
        val ocf = other.coeffList.toMutableList()
        val maxD = (cf.size - 1) + (ocf.size - 1)
        var archive = doubleArrayOf()
        while (archive.size != maxD + 1) {
            archive += 0.0
        }
        cf.forEachIndexed { ind1, c1 ->
            ocf.forEachIndexed { ind2, c2 ->
                archive[ind1 + ind2] += c1 * c2
            }
        }
        var result = doubleArrayOf()
        archive.reversed().forEach { now ->
            result += now
        }
        return Polynom(*result)
    }

    /**
     * Деление
     *
     * Про операции деления и взятия остатка см. статью Википедии
     * "Деление многочленов столбиком". Основные свойства:
     *
     * Если A / B = C и A % B = D, то A = B * C + D и степень D меньше степени B
     */

    operator fun div(other: Polynom): Polynom {
        var cf = coeffList.reversed().dropWhile { it == 0.0 }.toMutableList()// (1.0, -2.0, -1.0, 4.0)
        val ocf = other.coeffList.reversed().dropWhile { it == 0.0 }.toMutableList() // (1.0, 3.0, 2.0)
        var result = doubleArrayOf()
        val cfSize = cf.size
        val archivePolynom = DoubleArray(ocf.size) // для изменений p1
//        val set = mutableSetOf<Double>()
//        cf.forEach { num ->
//            if (num != 1.0 || num != 0.0) {
//                set.add(num)
//            }
//      7  }
//        val newSet = set.sorted()
//        var flag = true
//        for (i in 1 until set.size) {
//            if (newSet[i] % newSet[0] != 0.0) {
//                flag = false
//                break
//            }
//        }
//        if (flag) {
//            cf.forEachIndexed { ind, num ->
//                cf[ind] = num / newSet[0]
//            }
//        }
        if (cfSize >= ocf.size - 1) {
            var k = 0
            while (cfSize - ocf.size + 1 > k) {

                ocf.forEachIndexed { ind, num ->
                    if (cf[0] != num) {
                        archivePolynom[ind] = cf[0] * num
                    } else {
                        archivePolynom[ind] = num
                    }
                }
                result += cf[0] / ocf[0]
                archivePolynom.forEachIndexed { ind, num ->
                    if (ind != 0) {
                        if (cf[0] + archivePolynom[0] == 0.0) {
                            cf[ind] = cf[ind] + num
                        } else if (cf[0] - archivePolynom[0] == 0.0) {
                            cf[ind] = cf[ind] - num
                        }
                    }
                }
                if (cf[0] + archivePolynom[0] == 0.0) {
                    cf[0] = cf[0] + archivePolynom[0]
                } else if (cf[0] - archivePolynom[0] == 0.0) {
                    cf[0] = cf[0] - archivePolynom[0]
                }
                cf = cf.dropWhile { it == 0.0 }.toMutableList()
                k += 1
//                val flag = true
//                cf.forEachIndexed { ind, num ->
//                    if (cf[ind] != archivePolynom[ind]) {
//                        !flag
//                    }
//                }
//                if (flag && rem == true) {
//                    return Polynom(0.0)
//                }
//                if (flag && rem == false) {
//                    return Polynom(1.0)
//                }
            }
        }
        return Polynom(*result) // целого - нет
    }

    /**
     * Взятие остатка
     */
    operator fun rem(other: Polynom): Polynom = this - this / other * other

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Polynom

        if (coeffList != other.coeffList) return false

        return true
    }

    override fun hashCode(): Int = coeffList.hashCode()

}
