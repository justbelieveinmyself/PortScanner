package com.justbelieveinmyself.portscanner.feeders;

import com.justbelieveinmyself.portscanner.core.ScanningSubject;

import java.net.InetAddress;

/**
 * Интерфейс для Фидеров, который используется для отправки сканеру IP адресов
 *
 * Классы, реализующие Feeder, должны предоставить алгоритм последовательной генерации
 * списка отсканированных IP адресов
 *
 * Для каждого сканирования будет создаваться новый объект Feeder, передающий необходимые
 * параметры в конструктор. Конструктор также должен быть предоставлен для того,
 * чтобы запросить и идентификатор фидера.
 *
 * Реализации должны быть неизменяемыми, т.е. после создания они не должны изменять свои внутренние
 * параметры (метод getInfo() должен возвращать всегда одно и то же значение)
 */
public interface Feeder {

    /**
     * @return true, если остались ещё IP адреса в обработке
     */
    boolean hasNext();

    /**
     * @return следующий IP для обработки
     */
    ScanningSubject next();

    /**
     * @return значение от 0 до 100, описывающее объем уже проделанной работы
     */
    int percentageComplete();

    /**
     * @return информацию об настройках текущего фидера
     */
    String getInfo();

    /**
     * @return true, при сканировании адресов локальной сети (чтобы можно было использовать ARP и т.д.)
     */
    default boolean isLocalNetwork() {
        return false;
    }

    default ScanningSubject subject(InetAddress ip) {
        return new ScanningSubject(ip);
    }

    String getId();
}
