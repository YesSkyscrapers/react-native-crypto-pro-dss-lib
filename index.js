import { NativeModules } from 'react-native';

const { CryptoProDssLib } = NativeModules;

let CryptoProDss = {
    sdkInitialization: (...params) => CryptoProDssLib.sdkInitialization(...params),
    getOperations: (...params) => CryptoProDssLib.getOperations(...params),
    signMT: (...params) => CryptoProDssLib.signMT(...params),
    updateStyles: (...params) => CryptoProDssLib.updateStyles(...params),
    getUsers: (...params) => CryptoProDssLib.getUsers(...params),
    continueInitViaQr: (...params) => CryptoProDssLib.continueInitViaQr(...params),
    initViaQr: (...params) => CryptoProDssLib.initViaQr(...params),
}


export default CryptoProDss;
