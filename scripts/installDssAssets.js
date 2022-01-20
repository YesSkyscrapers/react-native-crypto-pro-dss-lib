const fs = require('fs')

let isRootProjectDir = !__dirname.includes("node_modules")
if (process.argv.includes("-fromRootProject")) {
    isRootProjectDir = true
}

const PROJECT_DIR = isRootProjectDir ? `./` : `../../`
const DSS_DIR = `${PROJECT_DIR}dss/`
const FONTS_DIR = `${PROJECT_DIR}dss/fonts/`
const IOS_CERTS_FILE = "CpMyDssRootCerts.json"
const CERTS_FILE = "certs.json"
const STYLES_FILE = "SDKStyles.json"
const PROJECT_FILES_PATH = `${isRootProjectDir ? "" : PROJECT_DIR}android/app/src/main/assets/`
const LIB_FILES_PATH = `${isRootProjectDir ? "" : PROJECT_DIR}node_modules/react-native-crypto-pro-dss-lib/android/src/main/assets/`
const IOS_FRAMEWORK_PATH = `${isRootProjectDir ? "" : PROJECT_DIR}node_modules/react-native-crypto-pro-dss-lib/Frameworks/SDKFramework.framework/`

const convertStylesToIOS = (_stylesText = "", fonts = []) => {
    let stylesText = `${_stylesText}`
    fonts.forEach(font => {
        try {
            const newFont = font.slice(0, font.lastIndexOf("."))

            stylesText = stylesText.split(`"font": "${font}"`).join(`"font": "${newFont}"`)
        } catch (err) {
            console.log(`Произошла ошибка при конвертации шрифта ${font}: ${err.message}`)
        }
    })

    try {
        stylesText = stylesText.split(`"numberOfAttempts": "Количество попыток",`).join(`"numberOfAttempts": "Количество попыток: ",`)
    } catch (err) {
        console.log(`Произошла ошибка при конвертации надписи отличающейся на разных ОС: ${err.message}`)
    }

    try {
        stylesText = stylesText.split(`"signMTconfirmation": 3,`).join(`"signMTconfirmation": 0,`)
    } catch (err) {
        console.log(`Произошла ошибка при конвертации типа окошка подтверждения, отличающегося на разных ОС: ${err.message}`)
    }
    return stylesText
}

let fonts = []

const projectDir = fs.readdirSync(PROJECT_DIR)
if (!projectDir.includes("dss")) {
    console.log(`Папка ${DSS_DIR} не найдена`)
}

const dssDir = fs.readdirSync(DSS_DIR)

let shouldCopyFonts = true;

if (!dssDir.includes("fonts")) {
    shouldCopyFonts = false;
    console.log(`Папка ${FONTS_DIR} не найдена. Копирование шрифтов проигнорировано.`)
} else {
    const fontsDir = fs.readdirSync(FONTS_DIR)
    if (fontsDir.length == 0) {
        shouldCopyFonts = false;
        console.log(`Папка ${FONTS_DIR} пуста. Копирование шрифтов проигнорировано.`)
    } else {
        fonts = fontsDir
    }
}

if (shouldCopyFonts) {
    const fontsDir = fs.readdirSync(FONTS_DIR)
    let copiedCount = 0
    fontsDir.forEach(font => {
        try {
            fs.copyFileSync(`${FONTS_DIR}${font}`, `${LIB_FILES_PATH}/${font}`)
            copiedCount++;
        } catch (err) {
            console.log(`Ошибка копирования шрифта ${font}: ${err.message}`)
        }
    })
    console.log(`Копирование шрифтов завершено. Скопировано ${copiedCount}/${fontsDir.length}`)
}

if (!dssDir.includes(CERTS_FILE)) {
    console.log(`Файл с сертификатами не найден.`)
} else {
    try {
        fs.copyFileSync(`${DSS_DIR}${CERTS_FILE}`, `${PROJECT_FILES_PATH}/${CERTS_FILE}`)
    } catch (err) {
        console.log(`Ошибка копирования файла с сертификатами для андройд: ${err.message}`)
    }
    try {
        fs.copyFileSync(`${DSS_DIR}${CERTS_FILE}`, `${IOS_FRAMEWORK_PATH}/${IOS_CERTS_FILE}`)
    } catch (err) {
        console.log(`Ошибка копирования файла с сертификатами для айос: ${err.message}`)
    }

}

if (!dssDir.includes(STYLES_FILE)) {
    console.log(`Файл со стилями не найден.`)
} else {
    try {
        fs.copyFileSync(`${DSS_DIR}${STYLES_FILE}`, `${PROJECT_FILES_PATH}/${STYLES_FILE}`)
    } catch (err) {
        console.log(`Ошибка копирования файла со стилями для андройд: ${err.message}`)
    }
    try {
        const stylesContent = fs.readFileSync(`${DSS_DIR}${STYLES_FILE}`, "utf-8")
        const iosStylesContent = convertStylesToIOS(stylesContent, fonts)
        fs.writeFileSync(`${IOS_FRAMEWORK_PATH}${STYLES_FILE}`, iosStylesContent, "utf-8")
    } catch (err) {
        console.log(`Ошибка копирования файла со стилями для айос: ${err.message}`)
    }
}

console.log("Установка ассетов завершена")