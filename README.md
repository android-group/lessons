Step By Step - Шаг за шагом

<h1>Первое занятие - Введение</h1>
Как устанавливать Android Studio.
Структура проекта в Android.
Разбираем Hello World.

Установить Android Studio
http://developer.android.com/sdk/index.html#top

-- Ответы на вопросы:
1) Как с Linux установить Android Studiо?
c Linux зашел на страницу установки Android Studio (http://developer.android.com/sdk/index.html#top).
И скачал zip архив в котором есть каталог android-studio/bin со скриптом для запуска studio.sh
Скриншот 1.jpg

2) Под какой версией Android можно запускать приложения?
При создании проекта указывается минимальная версия SDK
Скриншот 2.jpg

<h1>Второе занятие</h1>
Диплоим приложение на телефон. (много особенностей)
Сегодня хотел рассказать как диплоить приложения на с компьютера на телефон и как вешать события на кнопки 
:)
Диплоить приложения не так то просто если до этого не пробывал, как кажется на первый взгляд
Для начала нужно установить драйвера для телефона, чтобы компьютер его определил.

http://developer.samsung.com/technical-doc/view.do?v=T000000117
Отправлено:
Вт
 Потом прописать в PATH 
;%ANDROID_SDK_HOME%\tools;%ANDROID_SDK_HOME%\platform-tools;

где ANDROID_SDK_HOME до sdk 
У меня это
D:\Users\user\AppData\Local\Android\sdk
Отправлено:
Вт
Это всё?
Конечно нет) нужно перевести телефон в режим разработчика.
раздел: Настройки -> Об устройстве
там есть кнопка Номер сборки
по ней кликать до посинения пока не включится режим разработчика
Отлично)
Теперь нам нужно понять, что компьютер определил наш телефон и "авторизовал его" (процесс авторизации - подтверждения со стороны телефона, что он не против работать с моим компьютером)
Настройки - Парамтры разработчика.
Тут есть две важные кнопки.
Отладка USB и Отозвать авторизацию отладки USB
Зачем они? Для того чтобы связать наш телефон с компьютером (авторизовать).
Отладка USB 

Галочка включая которую нам приходи запрос на авторизацию.
Отозвать авторизацию отладки USB.

- ОЧЕНЬ ВАЖНО.
Если ранее вы авторизовались на одном компьютере, подключив другой компьютер процесс авторизации может не пройти.
Для этого нужно нажать эту кнопку Отозвать авторизацию отладки USB.
Итак, наконец то, как же диплоить приложения?
Это просто и быстрее чем на виртуальном эмуляторе.

Скриншот 3.jpg

Обратите внимание на Development Target Options
Отправлено:
Вт
Target: USB Device
после настройки проекта просто нажимаем на Run или shift f10
ну вот и всё. рассказывать можно еще долго, что такое ADB и какие прелисти диплоя приложения на Linux но пока что не нужно

Для тех кто устанавливает Android Studio*‎ на *Ubuntu

Скорее всего у вас возникнет ошибка:
ADB Connection Error: Unable to create Debug Bridge: Unable to start adb server: Unable to detect adb version

Для её решения нужно скачать platform-tools и заменить её в своей SDK
(/home/*your-login*/Android/Sdk)
https://dl-ssl.google.com/android/repository/platform-tools_r23.0.1-linux.zip
Отправлено:
Вт
для того чтобы проверить ваш телефон авторизован в компьютере
удобно пользоваться командой adb devices -l
