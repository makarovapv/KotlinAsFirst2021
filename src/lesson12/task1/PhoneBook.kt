@file:Suppress("UNUSED_PARAMETER")

package lesson12.task1

/**
 * Класс "Телефонная книга".
 *
 * Общая сложность задания -- средняя, общая ценность в баллах -- 14.
 * Объект класса хранит список людей и номеров их телефонов,
 * при чём у каждого человека может быть более одного номера телефона.
 * Человек задаётся строкой вида "Фамилия Имя".
 * Телефон задаётся строкой из цифр, +, *, #, -.
 * Поддерживаемые методы: добавление / удаление человека,
 * добавление / удаление телефона для заданного человека,
 * поиск номера(ов) телефона по заданному имени человека,
 * поиск человека по заданному номеру телефона.
 *
 * Класс должен иметь конструктор по умолчанию (без параметров).
 */
class PhoneBook {
    val list = mutableListOf<String>()
    private var map = mutableMapOf<String, String>()
    private var listOfPhones = mutableListOf<String>()

    /**
     * Добавить человека.
     * Возвращает true, если человек был успешно добавлен,
     * и false, если человек с таким именем уже был в телефонной книге
     * (во втором случае телефонная книга не должна меняться).
     */
    fun addHuman(name: String): Boolean {
        return if (name !in list) {
            list.add(name)
            true
        } else false
    }

    /**
     * Убрать человека.
     * Возвращает true, если человек был успешно удалён,
     * и false, если человек с таким именем отсутствовал в телефонной книге
     * (во втором случае телефонная книга не должна меняться).
     */
    fun removeHuman(name: String): Boolean {
        return if (name in list) {
            list.remove(name)
            true
        } else false
    }

    /**
     * Добавить номер телефона.
     * Возвращает true, если номер был успешно добавлен,
     * и false, если человек с таким именем отсутствовал в телефонной книге,
     * либо у него уже был такой номер телефона,
     * либо такой номер телефона зарегистрирован за другим человеком.
     */
    fun addPhone(name: String, phone: String): Boolean {
        map[phone] = name
        if (name in list) {
            return if (phone !in listOfPhones) {
                listOfPhones.add(phone)
                true
            } else false
        }

        return false
    }

    /**
     * Убрать номер телефона.
     * Возвращает true, если номер был успешно удалён,
     * и false, если человек с таким именем отсутствовал в телефонной книге
     * либо у него не было такого номера телефона.
     */

    fun removePhone(name: String, phone: String): Boolean {
        if (phone in map.keys && map[phone] == name) {
            map.remove(phone)
            return true
        }
        return false
    }

    /**
     * Вернуть все номера телефона заданного человека.
     * Если этого человека нет в книге, вернуть пустой список
     */

    fun phones(name: String): Set<String> {
        val result = mutableListOf<String>()
        map.forEach { (n) ->
            if (map[n] == name) {
                result.add(n)
            }
        }
        return result.toSet()
    }

    /**
     * Вернуть имя человека по заданному номеру телефона.
     * Если такого номера нет в книге, вернуть null.
     */
    fun humanByPhone(phone: String): String? {
        if (phone in map.keys) {
            return map[phone]
        }
        return null
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PhoneBook

        if (list.sorted() != other.list.sorted()) return false
        if (map.toSortedMap(compareBy { it }) != other.map.toSortedMap(compareBy { it })) return false
        if (listOfPhones.sorted() != other.listOfPhones.sorted()) return false

        return true
    }

    override fun hashCode(): Int {
        var result = list.sorted().hashCode()
        result = 31 * result + map.toSortedMap(compareBy { it }).hashCode()
        result = 31 * result + listOfPhones.sorted().hashCode()
        return result
    }
}