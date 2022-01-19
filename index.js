import { NativeModules } from 'react-native';

const { CryptoProDssLib } = NativeModules;

let CryptoProDss = {
    sdkInitialization: () => CryptoProDssLib.sdkInitialization(),
    getOperations: (kid) => CryptoProDssLib.getOperations(kid),
    signMT: (transactionId, kid) => CryptoProDssLib.signMT(transactionId, kid),
    updateStyles: () => CryptoProDssLib.updateStyles(),
    getUsers: () => CryptoProDssLib.getUsers(),
    continueInitViaQr: (kid) => CryptoProDssLib.continueInitViaQr(kid),
    initViaQr: (qr, useBiometric) => CryptoProDssLib.initViaQr(qr, useBiometric),
}


export default CryptoProDss;
