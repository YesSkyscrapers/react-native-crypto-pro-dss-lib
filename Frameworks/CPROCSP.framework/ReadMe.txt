[UTF-8]
[Use `iconv -f utf-8', if needed]

Если вы планируете использовать MonoDevelop, ознакомьтесь с инструкцией MonoDevelopBuild.txt

Для сборки примера в Xcode:

0. Проекты CreateFile и iStunnelSimple уже настроены, чтобы работать с ними, их надо извлечь в одну папку с CPROCSP.framework и выполнить пункт 1

1. Запустить 

	SetApplicationLicense 40400-W0037-EKVQK-9YDNG-D3F67 license.enc

   В текущей директории будет создан файл license.enc. 
2. Открыть проект в XCode
3. В левой панели выбрать проект, затем в основном окне в списке targets выбрать 
   цель сборки, перейти на вкладку "Build Phases", выбрать там "Link Binary With Libraries",
   нажать на плюс, в появившемся окне выбрать "Add other", добавить CPROCSP.framework.
4. Нажать на CPROCSP.framework правой кнопкой мыши, выбрать "Show In Finder".
   Перетащить все ресурсы кроме ru.lproj и en.lproj из директории Resources 
   в проект (тоже в Resource).
!!!ВАЖНО!!!---------------------------------------------------------------------
   При переносе файлов следует установить флажок "Create Folder referencies for 
    any added folders".                                                         
--------------------------------------------------------------------------------
5. Созданный в п.1 файл license.enc перетащить в Resources проекта(аналогично п.4,
   тоже выбрав "Create Folder referencies for any added folders").
6. Файлы из Resources\en.lproj и Resources\ru.lproj перетащить в ресурсы приложения.
   В меню выбрать "Create Folder Groups".
   При этом они будут автоматически сгруппированы в двуязычные файлы локализации.
7. В свойствах проекта настроить 
   "Valid Architectures: armv7"
8. Если используется XCode, в левой панели выбрать проект, затем 
   в основном окне в списке targets выбрать цель сборки, перейти на вкладку
   "Build Phases", выбрать там "Add Build Phase - Add Run Script". Появившуюся
   фазу сборки "Run Script" отредактировать, поместив в поле для скрипта
          <Путь к директории с фреймворком>/ConfigureApplication
   Если путь содержит пробелы, перед ними надо поставить backslash: "\".
9. В свойствах проекта укажите "C++ Standard Library" - "libc++ (LLVM C++ Stanard Library)".
10. Добавить линковку с модулем поддержки считывателя, в соответствии с инструкцией Readers.txt
11. В одном из файлов проекта определите переменную extern bool USE_CACHE_DIR; с помощью неё можно
    задавать директорию, где будут храниться ключи. 
    При задании (рекомендуется) bool USE_CACHE_DIR = false; эта директория будет Documents/cprocsp/keys/
    При задании bool USE_CACHE_DIR = true; это будет Library/Caches/cprocsp/keys/

Установка корневого сертификата удостоверяющего центра:
1. Создать копию корневого хранилища
   mv /var/opt/cprocsp/users/stores/root.sto{,.old}
2. Скопировать корневое хранилище, находящееся в ресурсах фреймворка
   cp /path_to_framework/CPROCSP.framework/Resources/root.sto /var/opt/cprocsp/users/stores/
3. Каким-то образом получить корневой сертификат УЦ
   Например, скачать с помощью curl/Safari/CPFox.
   Пример: /opt/cprocsp/bin/curl https://www.cryptopro.ru:5555/ui/certnew.asp?Type=chain -o /path_to_cert/certName --insecure
4. Установить его в корневое хранилище
   /opt/cprocsp/bin/certmgr -inst -store mRoot -file /path_to_cert/certName
5. Убедиться, что сертификат был добавлен
   /opt/cprocsp/bin/certmgr -list -store mRoot
6. Переместить корневое хранилище в ресурсы фреймворка
   mv /var/opt/cprocsp/users/stores/root.sto /path_to_framework/CPROCSP.framework/Resources/root.sto
7. Вернуть исходное корневое хранилище
   mv /var/opt/cprocsp/users/stores/root.sto.old /var/opt/cprocsp/users/stores/root.sto

Сохранение/восстановление контейнеров
1. С помощью iTunes File Sharing найдите приложение, слинкованное с фреймворком CPROCSP
2.
   а) Сохранение: cкопируйте папку keys, лежащую внутри cprocsp, в директорию на компьютере
   б) Восстановление: замените папку keys, лежащую внутри cprocsp, на аналогичную заранее сохраненную папку.

!!!ВАЖНО!!!---------------------------------------------------------------------
При сборке собственного проекта с КриптоПро CSP и в свойствах проекта и в свойствах target 
должны быть отключены опции "Dead Code Striping", "Strip during copy",  "Strip linked products".

Во время отладки проектов необходимо отключать контроль целостности CSP. Для этого нужно
вызвать функцию DisableIntegrityCheck() из /Headers/DisableIntegrity.h .
--------------------------------------------------------------------------------

Проверить корректность работы приложения на iOS можно воспользовавшись инструкцией RunTest.txt
