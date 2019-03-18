# Как использовать нативные библиотеки в Android

Предположим, что для вашего приложения необходима какая-то библиотека, которая написана на C или C++ из которой вы хотите использовать определенные методы для решения своих задач. 

На примере аудиокодека [Opus](http://opus-codec.org/), который является по сути библиотекой на C, я покажу как добавить к себе в приложение нативную библиотеку, использовать методы из неё и как всё это собрать вместе.

Для работы с нативным кодом в Android вам нужен **NDK**, поэтому [установите](https://developer.android.com/ndk/guides/) его, если еще не сделали этого.

## Скачиваем исходники библиотеки 
Скачиваем с официального [сайта](http://opus-codec.org/downloads/) Opus исходники. 
Теперь у нас есть куча C файлов, но что с ними делать?
На этот вопрос можно ответить зная, какую задачу вы хотите решить с помощью данной библиотеки.

В нашем случае мы хотим кодировать аудио сигнал, полученный с нашего микрофона и отправлять закодированные байты информации по сети, в то же время мы будем получать такие же, закодированные с помощью Opus байты аудио информации, которые нам нужно декодировать в обычный звук и воспроизвести его.

Для простоты, в примере, закодированное аудио мы будем сразу декодировать и воспроизводить, чтобы можно было убедиться, что кодек дейсвительно работает и мы правильно всё собрали.

Для этой задачи в Opus есть структуры [OpusEncoder](http://opus-codec.org/docs/opus_api-1.2/group__opus__encoder.html#gaf461a3ef2f10c2fe8b994a176f06c9bd) и [OpusDecoder](http://opus-codec.org/docs/opus_api-1.2/group__opus__decoder.html#ga401d8579958d36094715a6b90cd159a6), а также функции `opus_encode` и `opus_decode`, которые собственно и выполняют полезную работу.
Добавим исходники к себе в проект: для этого создадим в *app/src/main* папку *cpp* и копируем в неё  папку с исходниками, которую скачали до этого.

Отлично, мы нашли нужные функции, почитали документацию к ним и поняли, что мы должны им передать, чтобы получить нужный результат, но возникает новый вопрос: а как их вызвать из Java?

## Пишем JNI обертку для вызова C функций

Вся работа с кодом написанным на C/C++ в Java реализована с помощью [Java Native Interface](https://developer.android.com/training/articles/perf-jni)(JNI).

Рассмотрим его работу на примере вызова метода [opus_encoder_init](https://mf4.xiph.org/jenkins/view/opus/job/opus/ws/doc/html/group__opus__encoder.html#ga515db1c267a7421dacaad3610f79eb79), который инициализирует объект энкодера.
Чтобы вызвать метод `opus_encoder_init` из Java нужно создать обычный Java метод с ключевым словом *native*. 
Для начала создадим класс, где будут располагаться все такие методы(JNI declarations).Назовем его **Opus** и в нём уже создадим метод `initEncoder`.

```java
public native boolean initEncoder(int samplingRate, int numberOfChannels, int frameSize, int maxFrameSize);
```

Как видно, мы передаём туда некоторое количество параметров, а в ответ нам вернётся `boolean`.

Теперь нам нужен С файл, в котором мы реализуем данный метод. Создадим его в той же папке, где лежат исходники Opus (*app/src/main/cpp*) и назовём **jniopus.c**.
Содержимое этого файла:

```c
#include <jni.h>
#include <include/opus.h>
#include <malloc.h>
#include <android/log.h>

OpusEncoder *enc;
opus_int32 SAMPLING_RATE;
int CHANNELS;
int APPLICATION_TYPE = OPUS_APPLICATION_VOIP;
int FRAME_SIZE;
int MAX_FRAME_SIZE;

JNIEXPORT jboolean JNICALL
Java_com_forasoft_androidopus_Opus_initEncoder(JNIEnv *env, jobject instance, jint samplingRate,
                                               jint numberOfChannels,
                                               jint frameSize, jint maxFrameSize) {

    FRAME_SIZE = frameSize;
    SAMPLING_RATE = samplingRate;
    CHANNELS = numberOfChannels;
    MAX_FRAME_SIZE = maxFrameSize;

    int error;
    int size;

    size = opus_encoder_get_size(1);
    enc = malloc(size);
    error = opus_encoder_init(enc, SAMPLING_RATE, CHANNELS, APPLICATION_TYPE);

    if (error < 0) {
        sprintf(logMsg, "Initialized Encoder with ErrorCode: %d", error);
        __android_log_write(ANDROID_LOG_DEBUG, "Native Code:", logMsg);
        return JNI_FALSE;
    }

    return JNI_TRUE;

}
```

Чтобы JNI смог с ним работать обязательно нужно добавить **jni.h**. Обратите внимание на именование метода: сначала идёт указание, что вызов идёт из Java, а затем через нижнее подчёркивание полное имя класса(т.е. вместе с package name) и имя самого метода.

Входных параметров больше, чем мы передавали: добавился `JNIEnv *env` и `jobject instance`.
Эти праметры передаются в каждом JNI вызове.
`*env` это указатель на структуру, которая содержит все функции необходимые для взаимодействия с виртуальной машиной и для работы с Java объектами.
`instance` это ссылка на Java объект где объявлен *native* метод, который мы вызвали.

Далее мы уже вызываем метод из самой библиотеки opus `opus_encoder_init` и делаем инициализацию. В конце возвращаем `JNI_TRUE`: это константа равная 1, которая заранее определена в JNI для удобства. 


Теперь объявим другие нужные нам *native* методы в классе **Opus**. Этот класс можно сравнить с интерфейсом, т.к. у методов с ключевым словом *native* нет реализации. Мы просто объявляем какие методы мы будем использовать для работы с нативным кодом, какие параметры мы будем туда передавать и что ожидаем получить назад. Вот, что получилось:
```java

public class Opus {

    static {
        System.loadLibrary("jniopus");
    }


    public native boolean initEncoder(int samplingRate, int numberOfChannels, int frameSize, int maxFrameSize);

    public native int encodeBytes(short[] in, byte[] out);

    public native boolean releaseEncoder();

    public native boolean initDecoder(int samplingRate, int numberOfChannels, int frameSize);

    public native int decodeBytes(byte[] in, short[] out);

    public native boolean releaseDecoder();

    public int encode(short[] in, byte[] out) {
        return encodeBytes(in, out);
    }

    public int decode(byte[] encodedBuffer, short[] buffer) {
        return decodeBytes(encodedBuffer, buffer);
    }
}
```

>Важно отметить, что в самом начале мы добавили *static* блок с `System.loadLibrary("jniopus");`, в котором загружаем нативную библиотеку, которую будем использовать.
>
Нативный код можно собрать в статическую(**.а** / **.lib**) или динамическую(**.so**) библиотеку.
В случае статической происходит линковка с основным проектом на этапе компиляции и код копируется из библиотеки в наш проект: увеличивается размер итогового установочного файла, но у нас есть все что нужно и в одном месте.
Но статическая библиотека нам не подходит, т.к. с JNI в Android [можно использовать](https://developer.android.com/training/articles/perf-jni#native-libraries) только динамические библиотеки(**.so**).

Динамические библиотеки ещё называют *shared objects* отсюда и расширение **.so**. Они собираются в отдельные модули и при сборке используется динамическая линковка с кодом основного проекта, так что код из такой библиотеки вызывается динамически в рантайме. 

Теперь в папке *cpp* у нас есть **jniopus.c**, где находится имплементации всех методов из Java с ключевым словом *native* и папка *opus* с исходниками этой библиотеки. Настало время собрать все это и упаковать в **.apk**.

## Сборка нативного кода

Для сборки нативного кода в Android есть два инструмента: **CMake** и **ndk-build**.

**CMake** является инструментом сборки по умолчанию и при написании нового кода, в [документации](https://developer.android.com/studio/projects/add-native-code#create-cmake-script) рекомендуют использовать его.
**ndk-build** поддерживается, т.к. есть много legacy проектов где он используется.

Мы будем собирать с помощью **CMake**. Для этого нужно создать билд скрипт, в котором мы опишем как нужно собрать наш нативный код. Поэтому создаём `CMakeLists.txt` в корне проекта (*app/*).

В начале любого `CMakeLists.txt` должна идти строчка:
```cmake
cmake_minimum_required(VERSION 3.6.0)
```
В ней мы указываем какая минимальная версия **CMake** должна быть установлена, чтобы использовать этот билд скрипт.

Сначала нам нужно собрать библиотку с **Opus**:
```cmake
add_library( # Sets the name of the library.
        opus

        # Sets the library as a static library.
        STATIC

        # Provides a relative path to your source file(s).
        # Associated headers in the same location as their source
        # file are automatically included.
        # We include relative path to header files, because some of them
        # not in the same directory as sources.
        ${OPUS_SOURCES} ${CELT_SOURCES} ${SILK_SOURCES} ${OPUS_HEADEARS})
```
Команда `add_library` используется для компиляции библиотеки. Сначала указывается имя, создаваемой библиотеки(`opus`), затем тип (`STATIC`) , далее идут все исходники из которых нужно собрать библиотеку(почему они так странно написаны расскажу чуть позже).

В данном случае мы собираем библиотеку как статическую и это никак не сходится с тем, что я писал выше о невозможности их использования с JNI. Дело в том, что с JNI мы будём использовать другую библиотеку, которорую создадим как динамическую из нашего файла **jniopus.c**, а библиотеку с opus мы статически прилинкуем  к ней, чтобы у нас в итоге был только один **.so** файл, а не два.

Пропишем правило для компиляции библиотеки, которую будет использовать JNI:

```cmake
add_library( # Sets the name of the library.
        jniopus

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        ${NATIVE_SOURCES_PATH}/jniopus.c)
```
Здесь название `jniopus`, тип `SHARED` и исходный файл из которого мы собираем.

Также нужно добавить команду `include_directories(${OPUS_HEADERS_DIRS})` с указанием путей к файлам хэдеров(**.h**), чтобы компилятор знал где их искать.        
        
Теперь стоит сказать о конструкциях вида `${NATIVE_SOURCES_PATH}`: таким образом в **CMake** скрипте мы можем обращаться к значению переменной, которую до этого объявили с помощью команды `set`.

```cmake
set(NATIVE_SOURCES_PATH "src/main/cpp")
```
Пишем имя переменной, дальше через пробел/табуляцию/новую строку пишем значение переменной(причём можно писать как с кавычками, так и без них).

Значением переменной в этой команде могут являться сразу несколько элементов, как например `${OPUS_SOURCES}`:

```cmake
set(OPUS_SOURCES
        "${OPUS_DIR_PATH}/src/opus.c"
        "${OPUS_DIR_PATH}/src/opus_decoder.c"
        "${OPUS_DIR_PATH}/src/opus_encoder.c"
        "${OPUS_DIR_PATH}/src/opus_multistream.c"
        "${OPUS_DIR_PATH}/src/opus_multistream_encoder.c"
        "${OPUS_DIR_PATH}/src/opus_multistream_decoder.c"
        "${OPUS_DIR_PATH}/src/repacketizer.c"
        "${OPUS_DIR_PATH}/src/opus_projection_encoder.c"
        "${OPUS_DIR_PATH}/src/opus_projection_decoder.c"
        "${OPUS_DIR_PATH}/src/mapping_matrix.c")
```
Это очень удобно, когда нужно указать много файлов исходников. Так как **Opus** достаточно большая и сложная библиотека, то и исходников там много, поэтому в моём **CMake** билд скрипте много команд `set`. Все эти пути к исходникам и хэдерам прописаны в make файлах самой библиотеки, так что не волнуйтесь если думаете, что придётся искать всё исходники и вручную всё прописывать. Создатели библиотек обычно делают это сами.

Но нам нужна еще одна библиотека для логов, чтобы можно было выводить в Logcat сообщения из нативного кода. Эта библиотека, как и множество других есть в NDK и чтобы использовать её нам не надо её компилировать, а нужно просто найти и слинковать с нашей **.so**.
Чтобы найти уже собранную библиотеку нужно воспользоваться командой `find_library`:
```cmake
find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log )
```

Здесь мы указываем имя(`log-lib`) по которому потом будем обращаться к этой библиотеке и имя(`log`), по которому **CMake** будет искать в NDK библиотеку для логирования.

И финальный шаг это линкование нашей **.so** с библиотеками, код которых мы используем в **jniopus.c**:
```cmake
target_link_libraries(
        # Specifies the target library.
        jniopus

        # Specifies the libraries that should be linked to our target
        opus ${log-lib})
```

Сначала указываем имя нашей target библиотеки, к которой мы будем линковать остальные, затем через пробел/табуляцию/новую строку указываем имена библиотек, которые будут слинкованы с target библиотекой.
>Важно отметить, что после сборки к названию библиотеки автоматически будет лобавлен префикс *lib* и получится **libjniopus.so**(при этом в Java, когда мы пишем `System.loadLibrary("jniopus");`, мы должны указать имя библиотеки без префикса). Это такое правило именования библиотек существует в мире нативного кода.
>
## Сборка всего проекта

Осталось в нашем `build.gradle` скрипте указать, что нам нужно собрать нативные библиотеки и указать какой билд скрипт для этого использовать.

```groovy
android {
    compileSdkVersion 28
    defaultConfig {
        applicationId "com.forasoft.androidopus"
        minSdkVersion 19
        targetSdkVersion 28
        versionCode 1
        versionName "1.0"
        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"

        externalNativeBuild {
            cmake {
                /* This command line argument for C compiler indicates that CPUs our target devices
                   don't have NEON instructions set: it's made for simplicity.
                   It locates here, and don't in CMakeLists.txt, because I decide
                   to divide specific command-line arguments for Opus and this*/
                arguments "-DANDROID_ARM_NEON=FALSE"
                cppFlags ""
            }
        }
         // Uncomment these lines if you want to build only for these ABIs
//        ndk {
//            abiFilters "arm64-v8a", "armeabi-v7a"
//        }
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }

    externalNativeBuild {
        cmake {
            path "CMakeLists.txt"
        }
    }
}
```

Здесь блок `externalNativeBuild` c блоком `cmake` внутри встречается два раза. 

В первом случае мы указываем внутри агрумент командной строки, который нужно применить `arguments "-DANDROID_ARM_NEON=FALSE"` , который указывает, что поддержка набора инструкций NEON для архитектуры Arm будет выключена(сделано это для упрощения, т.к. для Opus при включении NEON нужно было бы прописывать еще дополнительные исходники из которых нужно собирать библиотеку). Здесь также есть параметр `cppFlags`, где можно указать C++ флаги.

Во втором случае мы указываем где находится наш билд скрипт `CMakeLists.txt`. Путь указывается относительный от того места, где находтся данный `build.gradle` файл, а т.к. `CMakeLists.txt` находится в той же папке, что и `build.gradle`, то путь это просто название файла.

Теперь синхронизируем проект с помощью Gradle, нажимаем Run и готово. Приложение открывается, жмём на кнопку **Start call** и говорим, тут же слышим, что сказали: значит всё работает как надо.

Если посмотреть на содержимое собраного **.apk** , то там будет папка *lib*, в ней несколько папок с названиями разных архитектур процессоров или по-другому [Application Binary Interface](https://developer.android.com/ndk/guides/abis)(ABI) и внутри каждой папки наша библиотека **libjniopus.so**. 

**NDK** автоматически собирает под все non-deprecated **ABIs** на данный момент. Можно указать явно конкретные архитектуры, которые вам нужны. Это делается с помощью добавления пары строчек в `build.gradle`:
```groovy
ndk {
    abiFilters "arm64-v8a", "armeabi-v7a"
}
```
Здесь мы указываем, что хотим собрать только под две архитектуры `arm64-v8a` и `armeabi-v7a`. 

>Кстати именно по причине наличия различных ABI нам нужно было собирать Opus из исходников, хотя казалось бы мы могли собрать саму библиотеку opus(с помощью, подготовленного разработчиками Opus, билд скрипта) у себя на машине в виде **libopus.so** или **libopus.a** и просто слинковать её с нашей библиотекой **libjniopus.so**(таким образом мы бы собрали **libopus.so** только под одну архитектуру).
>

## Покажите мне код
Весь код, который использовался в статье, можно посмотреть в [репозитории](https://github.com/vitaliyFora/AndroidOpus). 

## Итоги

В данной статье мы рассмотрели процесс добавления к себе в Android проект нативной библиотеки на примере аудио-кодека **Opus**, вызов нативного кода из Java через JNI и сборку проекта вместе со сборкой нативного кода.

Самый ошеломляющим и вгоняющим в ступор для меня была именно сборка нативных библиотек(написание билд скрипта `CMakeLiatas.txt`), т.к там оказалось много мелких деталей и неочевидных вещей, которые не описаны ни в документации Android, ни в документации CMake.

Но изучив вопрос глубже, всё получилось. Надеюсь, что данная статья поможет вам быстрее разобраться, что к чему в использовании нативного кода в Android.  

## PS

В `CMakeLists.txt` есть такой участок:

```cmake
set(FIXED_POINT "1")

# Set required C flags for opus
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -DUSE_ALLOCA -DOPUS_BUILD")

# Add certain sources and C flags if we want to use fixed point or float
if (${FIXED_POINT})
    set(SILK_SOURCES ${SILK_SOURCES} ${SILK_SOURCES_FIXED})
    set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -DFIXED_POINT=1 -DDISABLE_FLOAT_API")
else ()
    set(SILK_SOURCES ${SILK_SOURCES} ${SILK_SOURCES_FLOAT})
    set(OPUS_SOURCES ${OPUS_SOURCES} ${OPUS_SOURCES_FLOAT})
endif ()
```

Это небольшое усложнение связанное с обязательными для сборки **opus** агрументами командной строки и выбора режима **FIXED_POINT** или **FLOAT_POINT**.

`CMAKE_C_FLAGS` это переменная куда мы добавляем агрументы командной строки. Повторение в той же строке в виде `${CMAKE_C_FLAGS}` связано с тем, что мы не хотим, чтобы те значения, которые были записаны в переменную раньше были затёрты нашей новой записью.


### Все ссылки

Opus codec [http://opus-codec.org/](http://opus-codec.org/)

Установка NDK [https://developer.android.com/ndk/guides/](https://developer.android.com/ndk/guides/)

JNI для Android [https://developer.android.com/training/articles/perf-jni](https://developer.android.com/training/articles/perf-jni)

Android документация по CMake [https://developer.android.com/studio/projects/add-native-code#create-cmake-script](https://developer.android.com/studio/projects/add-native-code#create-cmake-script)

Разные ABI на Android [https://developer.android.com/ndk/guides/abis](https://developer.android.com/ndk/guides/abis)

Репозиторий с проектом из статьи https://github.com/vitaliyFora/AndroidOpus
