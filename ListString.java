package com.company;

public class ListString { // основной класс строки из "блоков"
    private class StringItem{ // класс "блок строки"
        private final static byte SIZE = 16; // максимальный размер блока
        private char[] symbols; // блок из символов
        private StringItem next; // ссылка на следующий блок
        private byte size; // текущий "реальный" размер блока

        private StringItem(){ // конструктор по умолчанию
            symbols = new char[SIZE]; // инициализация массива символов на максимальный размер блока
            size = 0;
            next = null;
        }
    }

    private StringItem head; // "голова", ссылка на первый блок строки

    public ListString(){ // конструктор по умолчанию
        head = new StringItem(); // инициализация пустого блока "головы"
    }
    public ListString(String string){ // конструктор для создания блоков из принимаемой строки
        head = new StringItem();
        char[] buff = string.toCharArray();
        for (char ch:buff) { // добавляем каждый символ из строки в блоки
            append(ch);
        }
    }
    public int length(){ // метод, возвращает количество символов во всех блоках строки
        StringItem buff = head;
        int length = head.size; // для подсчета количества
        while(buff.next != null){ // пока дальше есть блоки - проходим по ним
            buff = buff.next;
            length += buff.size;
        }
        return length;
    }
    public char charAt(int index) throws ListStringException { // метод для получения символа строки по индексу
        if (index < 0 || index > length()) throw new ListStringException("Index out of bounds"); // выбрасываем исключение, если индексы выходят за пределы
        int currentIndex = 0;
        StringItem buff = head;
        while (currentIndex + buff.size <= index){ // ищем блок, в котором находится нужный нам индекс
            currentIndex += buff.size;
            buff = buff.next;
        }
        return buff.symbols[index - currentIndex]; // возвращаем найденный символ
    }
    public void setCharAt(int index, char ch) throws ListStringException { // метод для перезаписи конкретного символа строки
        if (index < 0 || index > length()) throw new ListStringException("Index out of bounds"); // выбрасываем исключение, если индексы выходят за пределы
        int currentIndex = 0;
        StringItem buff = head;
        while (currentIndex + buff.size <= index){ // ищем нужный блок, как и в методе выше
            currentIndex += buff.size;
            buff = buff.next;
        }
        buff.symbols[index - currentIndex] = ch; // перезаписываем символ
    }
    public ListString substring(int start, int end) throws ListStringException { // метод для получения части строки в заданном диапазоне
        if (start < 0 || start > length() || end < 0 || end < start) throw new ListStringException("Index out of bounds"); // выбрасываем исключение, если индексы выходят за пределы
        ListString res = new ListString(); // новая строка для заполнения
        for (int i = start; i < end; i++) { // проходим по нужным символам
            try {
                res.append(charAt(i)); // добавляем в новую строку
            }
            catch (ListStringException e){ // если end вышел за пределы блока и мы поймали ошибку - значит строка закончилась и можно ее вернуть
                return res;
            }
        }
        return res; // возвращаем строку
    }
    public void append(char ch){ // метод для добавления нового символа в строку
        StringItem buff = head;
        int index = buff.size;
        if (buff.next == null && index < StringItem.SIZE){ // если нет следующего блока и есть место в текущем, то записываем символ в текущий
            buff.symbols[index] = ch;
            buff.size++;
        }
        else{ // иначе ищем ему подходящее место
            while(buff.next != null && buff.next.size == StringItem.SIZE){ // перебираем блоки в поисках свободного
                buff = buff.next;
            }
            if(buff.next == null){ // если свободных блоков не оказалось, то создаем новый
                buff.next = new StringItem();
                buff.next.symbols[buff.next.size++] = ch;
            }
            else if (buff.size < StringItem.SIZE){ // если есть свободное место в последнем блоке, то записываем символ в него
                buff.symbols[buff.size-1] = ch;
            }
        }
    }
    public void append(ListString string) { // метод для добавления нового ListString в строку
        StringItem buff = head;
        while(buff.next != null){ // ищем последний блок
            buff = buff.next;
        }
        buff.next = string.head; // записываем ссылку на следующий
    }
    public void append(String string) { // метод для добавления нового String в строку
        ListString newString = new ListString(string);
        append(newString);
    }
    public void insert(int index, ListString string) throws ListStringException { // метод для вставки строки в текущую строку
        if (index < 0 || index > length()) throw new ListStringException("Index out of bounds"); // выбрасываем исключение, если индексы выходят за пределы
        ListString beforeString = substring(0,index); // берем часть строки до заданного индекса
        ListString afterString = substring(index, length()); // берем часть строки после заданного индекса (включая сам индекс)
        head = beforeString.head; // пересобираем ссылки по порядку
        beforeString.append(string);
        string.append(afterString);
    }
    public void insert(int index, String string) throws ListStringException { // метод для вставки строки в текущую строку
        if (index < 0 || index > length()) throw new ListStringException("Index out of bounds"); // выбрасываем исключение, если индексы выходят за пределы
        insert(index, new ListString(string));
    }

    @Override
    public String toString() { // переопределение метода toString - возвращает единую строку из всех блоков
        String res = "";
        for (int i = 0; i < length(); i++){
            try {
                res += charAt(i);
            } catch (ListStringException e) {
                e.printStackTrace();
            }
        }
        return res;
    }
}
class ListStringException extends Exception{ // класс исключения для ListString
    public ListStringException(String message){
        super(message);
    }
}
